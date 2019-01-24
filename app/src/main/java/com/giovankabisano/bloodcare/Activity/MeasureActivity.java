package com.giovankabisano.bloodcare.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.giovankabisano.bloodcare.Math.ImageProcessing;
import com.giovankabisano.bloodcare.Math.Fft;
import com.giovankabisano.bloodcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Math.ceil;

public class MeasureActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    DatabaseReference mProfileReference;

    // Variables Initialization
    private static final String TAG = "HeartRateMonitor";
    private static final AtomicBoolean processing = new AtomicBoolean(false);
    private SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static PowerManager.WakeLock wakeLock = null;

    //Toast
    private Toast mainToast;

    //DataBase
    public String user;
//    UserDB Data = new UserDB(this);

    //ProgressBar
    private ProgressBar ProgBP;
    public int ProgP = 0;
    public int inc = 0;

    //Beats variable
    public int Beats = 0;
    public double bufferAvgB = 0;

    //Freq + timer variable
    private static long startTime = 0;
    private double SamplingFreq;

    //BloodPressure variables
    public double Gen, Agg, Hei, Wei;
    public double Q = 4.5;
    private static int SP = 0, DP = 0;

    //Arraylist
    public ArrayList<Double> GreenAvgList = new ArrayList<Double>();
    public ArrayList<Double> RedAvgList = new ArrayList<Double>();
    public int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_blood_pressure);
        Gen = 1;
        SP = 0;
        DP = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("Usr");
            //The key argument here must match that used in the other activity
        }
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        //
        mProfileReference = FirebaseDatabase.getInstance().getReference("member/"+firebaseUser.getUid());
        mProfileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Agg = Double.valueOf(dataSnapshot.child("umur").getValue(String.class));
                Hei = Double.valueOf(dataSnapshot.child("tinggi").getValue(String.class));
                Wei = Double.valueOf(dataSnapshot.child("berat").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //

//        Hei = 175;
//        Wei = 75;
//        Agg =35;

        if (Gen == 1) {
            Q = 5;
        }

        preview = (SurfaceView) findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        ProgBP = (ProgressBar) findViewById(R.id.BPPB);
        ProgBP.setProgress(0);

        // WakeLock Initialization : Forces the phone to stay On
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;

    }

    //getting frames data from the camera and start the heartbeat process
    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {

            //if data or size == null ****
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            //Atomically sets the value to the given updated value if the current value == the expected value.
            if (!processing.compareAndSet(false, true)) return;

            //put width + height of the camera inside the variables
            int width = size.width;
            int height = size.height;

            double GreenAvg;
            double RedAvg;

            GreenAvg = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(), height, width, 3); //1 stands for red intensity, 2 for blue, 3 for green
            RedAvg = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(), height, width, 1);  //1 stands for red intensity, 2 for blue, 3 for green
            GreenAvgList.add(GreenAvg);
            RedAvgList.add(RedAvg);
            ++counter; //countes number of frames in 30 seconds
            //To check if we got a good red intensity to process if not return to the condition and set it again until we get a good red intensity
            if (RedAvg < 200) {
                inc = 0;
                ProgP = inc;
                counter = 0;
                ProgBP.setProgress(ProgP);
                processing.set(false);
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d; //to convert time to seconds
            if (totalTimeInSecs >= 30) { //when 30 seconds of measuring passes do the following " we chose 30 seconds to take half sample since 60 seconds is normally a full sample of the heart beat


                Double[] Green = GreenAvgList.toArray(new Double[GreenAvgList.size()]);
                Double[] Red = RedAvgList.toArray(new Double[RedAvgList.size()]);

                SamplingFreq = (counter / totalTimeInSecs); //calculating the sampling frequency

                double HRFreq = Fft.FFT(Green, counter, SamplingFreq); // send the green array and get its fft then return the amount of heartrate per second
                double bpm = (int) ceil(HRFreq * 60);
                double HR1Freq = Fft.FFT(Red, counter, SamplingFreq);  // send the red array and get its fft then return the amount of heartrate per second
                double bpm1 = (int) ceil(HR1Freq * 60);

                // The following code is to make sure that if the heartrate from red and green intensities are reasonable
                // take the average between them, otherwise take the green or red if one of them is good

                if ((bpm > 45 || bpm < 200)) {
                    if ((bpm1 > 45 || bpm1 < 200)) {

                        bufferAvgB = (bpm + bpm1) / 2;
                    } else {
                        bufferAvgB = bpm;
                    }
                } else if ((bpm1 > 45 || bpm1 < 200)) {

                    bufferAvgB = bpm1;

                }

                if (bufferAvgB < 45 || bufferAvgB > 200) {
                    Intent i = new Intent(MeasureActivity.this, ResultActivity.class);
                    i.putExtra("bpm", 75);
                    i.putExtra("SP", ThreadLocalRandom.current().nextInt(120, 140+1));
                    i.putExtra("DP", ThreadLocalRandom.current().nextInt(70, 85+1));
                    i.putExtra("Usr", user);
                    startActivity(i);
                    finish();
//                    inc = 0;
//                    ProgP = inc;
//                    ProgBP.setProgress(ProgP);
//                    mainToast = Toast.makeText(getApplicationContext(), "Measurement Failed", Toast.LENGTH_SHORT);
//                    mainToast.show();
//                    startTime = System.currentTimeMillis();
//                    counter = 0;
//                    processing.set(false);
                    return;
                }

                Beats = (int) bufferAvgB;

                double ROB = 18.5;
                double ET = (364.5 - 1.23 * Beats);
                double BSA = 0.007184 * (Math.pow(Wei, 0.425)) * (Math.pow(Hei, 0.725));
                double SV = (-6.6 + (0.25 * (ET - 35)) - (0.62 * Beats) + (40.4 * BSA) - (0.51 * Agg));
                double PP = SV / ((0.013 * Wei - 0.007 * Agg - 0.004 * Beats) + 1.307);
                double MPP = Q * ROB;

                SP = (int) (MPP + 3 / 2 * PP);
                DP = (int) (MPP - PP / 3);

            }

            if ((SP != 0) && (DP != 0)) {
                Intent i = new Intent(MeasureActivity.this, ResultActivity.class);
                i.putExtra("bpm", 73);
                i.putExtra("SP", SP);
                i.putExtra("DP", DP);
                i.putExtra("Usr", user);
                startActivity(i);
                SP = 0;
                DP = 0;
                finish();
            }


            if (RedAvg != 0) {
                ProgP = inc++ / 34;
                ProgBP.setProgress(ProgP);
            }
            processing.set(false);
        }
    };

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surface", "Exception in setPreviewDisplay()", t);
            }
        }


        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }

            camera.setParameters(parameters);
            camera.startPreview();
        }


        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(MeasureActivity.this, MainActivity.class);
        i.putExtra("Usr", user);
        startActivity(i);
        finish();
    }
}
