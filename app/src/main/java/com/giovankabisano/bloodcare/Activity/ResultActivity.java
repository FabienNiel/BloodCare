package com.giovankabisano.bloodcare.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.giovankabisano.bloodcare.Model.Measurement;
import com.giovankabisano.bloodcare.R;
import com.giovankabisano.bloodcare.Math.StaticConfig;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ResultActivity extends AppCompatActivity {
    private String user, Date;
    int SP, DP, HeartRate;
    long timestamp;
    ImageView imageResult;
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    java.util.Date today = Calendar.getInstance().getTime();
    Double umur = 45.0;
    Button btnSelengkapnya;
    TextView percentage;
    public String[] sample_jk = {"p", "p", "p", "p", "l", "l", "p", "l", "p", "l", "l", "p", "p", "p", "l", "p", "p", "p", "l", "p"};
    public String[] sample_merokok = {"tidak", "tidak", "tidak", "tidak", "ya", "ya", "tidak", "tidak", "tidak", "ya", "tidak", "tidak", "tidak", "tidak", "ya", "tidak", "tidak", "tidak", "tidak", "tidak"};
    public double[] sample_umur = {48, 39, 49, 44, 50, 42, 32, 30, 47, 52, 79, 80, 65, 67, 75, 32, 85, 70, 56, 48};
    public double[] sample_sistolik = {160, 140, 160, 165, 190, 120, 110, 120, 130, 120, 110, 180, 155, 170, 180, 160, 180, 120, 130, 120};
    public double[] sample_diastolik = {100, 80, 100, 100, 140, 80, 80, 80, 90, 80, 70, 120, 110, 100, 100, 100, 100, 90, 90, 80};
    public String[] sample_kelas = {"hipertensi", "hipertensi", "hipertensi", "hipertensi", "hipertensi", "normal", "normal", "normal", "normal", "normal", "normal", "hipertensi", "hipertensi", "hipertensi", "hipertensi", "hipertensi", "hipertensi", "normal", "normal", "normal"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Date = df.format(today);
        TextView TBP = findViewById(R.id.tv_bp);
        percentage = findViewById(R.id.tv_percentage);
        imageResult = findViewById(R.id.result_image);
        btnSelengkapnya = findViewById(R.id.button_selengkapnya);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            SP = bundle.getInt("SP");
            DP = bundle.getInt("DP");
            HeartRate = bundle.getInt("bpm");
            user = bundle.getString("Usr");
            timestamp = System.currentTimeMillis();

            Measurement measurement = new Measurement(HeartRate, SP, DP, timestamp);
            FirebaseDatabase.getInstance().getReference().child("measurement/" + StaticConfig.UID).push().setValue(measurement);
            TBP.setText("Tekanan Darah : " + String.valueOf(SP) +"/" + String.valueOf(DP));
            if (SP > 135){
                imageResult.setImageResource(R.drawable.high);
            }else if (SP < 120){
                imageResult.setImageResource(R.drawable.low);
            }else if(SP > 120 && SP < 135){
                imageResult.setImageResource(R.drawable.normal);
                btnSelengkapnya.setVisibility(View.GONE);
            }
        }
        btnSelengkapnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ResultActivity.this, SuggestionActivity.class);
                i.putExtra("SP", SP);
                startActivity(i);
            }
        });
        calculateHypertension();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    private void calculateHypertension() {
        FirebaseDatabase.getInstance().getReference().child("member/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                HashMap hashUser = (HashMap) dataSnapshot.getValue();
//                Long a = Long.valueOf(hashUser.get("umur"));
//                umur = Double.valueOf(a);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        String jk = "p";
        String merokok = "ya";
        double sistolik = SP;
        double diastolik = DP;

        double Ll_umur_normal = Likelihood_continue(umur, "normal", "umur");
        double Ll_umur_hipertensi = Likelihood_continue(umur, "hipertensi", "umur");
        double Ll_sistolik_normal = Likelihood_continue(sistolik, "normal", "sistolik");
        double Ll_sistolik_hipertensi = Likelihood_continue(sistolik, "hipertensi", "sistolik");
        double Ll_diastolik_normal = Likelihood_continue(diastolik, "normal", "diastolik");
        double Ll_diastolik_hipertensi = Likelihood_continue(diastolik, "hipertensi", "diastolik");
        double Ll_merokok_normal = Likelihood_diskritRokok(merokok, "normal");
        double Ll_merokok_hipertensi = Likelihood_diskritRokok(merokok, "hipertensi");
        double Ll_jk_normal = Likelihood_diskritJenisKelamin(jk, "normal");
        double Ll_jk_hipertensi = Likelihood_diskritJenisKelamin(jk, "hipertensi");

        double jumlah_normal = Hitung_normal();
        double jumlah_hipertensi = Hitung_hipertensi();

        double hitung_normal = Ll_umur_normal * Ll_merokok_normal * Ll_sistolik_normal * Ll_diastolik_normal * Ll_jk_normal * (jumlah_normal / 35);
        double hitung_hipertensi = Ll_umur_hipertensi * Ll_merokok_hipertensi * Ll_sistolik_hipertensi * Ll_diastolik_hipertensi * Ll_jk_hipertensi * (jumlah_hipertensi / 35);
        Double b = (hitung_hipertensi / (hitung_hipertensi + hitung_normal) * 100);
        percentage.setText("Risiko anda terkena hipertensi : " + new DecimalFormat("##.##").format(b) + "%");
        Log.d("umurc", umur.toString());
        Log.d("hipertensi", b.toString() + "%");
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ResultActivity.this, MainActivity.class);
        i.putExtra("Usr", user);
        startActivity(i);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public double varians(String y, String fitur) {
        double hasil = 0, rata = 0, c = 0, x;
        for (int i = 0; i < sample_kelas.length; i++) {
            if (fitur.equals("umur")) {
                if (sample_kelas[i].equals(y)) {
                    hasil = hasil + sample_umur[i];
                    rata = Math.pow((sample_umur[i] - hasil), 2);
                    c = c + 1;
                } else {
                    continue;
                }
            } else if (fitur.equals("sistolik")) {
                if (sample_kelas[i].equals(y)) {
                    hasil = hasil + sample_sistolik[i];
                    rata = Math.pow((sample_sistolik[i] - hasil), 2);
                    c = c + 1;
                } else {
                    continue;
                }
            } else if (fitur.equals("diastolik")) {
                if (sample_kelas[i].equals(y)) {
                    hasil = hasil + sample_diastolik[i];
                    rata = Math.pow((sample_diastolik[i] - hasil), 2);
                    c = c + 1;
                } else {
                    continue;
                }
            }
        }
        x = rata / c;
        return x;
    }

    public double Likelihood_continue(double x, String y, String fitur) {
        double bawah = Math.sqrt(2 * Math.PI * varians(y, fitur));
        double atas = 1 / bawah;
        double pangkat = Math.pow(x - rata_rata(y), 2);
        double xx = atas * Math.exp((pangkat / (2 * varians(y, fitur))) * (-1));
        return xx;
    }

    public double rata_rata(String y) {
        double c = 0;
        for (int i = 0; i < sample_kelas.length; i++) {
            if (sample_kelas[i].equals(y)) {
                c = c + 1;
            } else {
                continue;
            }
        }
        return c;
    }

    public double Likelihood_diskritRokok(String c, String y) {
        int x = 0;
        for (int i = 0; i < sample_merokok.length; i++) {
            if ((sample_merokok[i].equals(c)) && sample_kelas[i].equals(y)) {
                x = x + 1;
            } else {
                continue;
            }
        }
        return x;
    }

    public double Likelihood_diskritJenisKelamin(String c, String y) {
        int x = 0;
        for (int i = 0; i < sample_jk.length; i++) {
            if ((sample_jk[i].equals(c)) && sample_kelas[i].equals(y)) {
                x = x + 1;
            } else {
                continue;
            }
        }
        return x;
    }

    public double Hitung_normal() {
        int x = 0;
        String a = "normal";
        for (int i = 0; i < sample_kelas.length; i++) {
            if (sample_kelas[i].equals(a)) {
                x = x + 1;
            } else {
                continue;
            }
        }
        return x;
    }

    public double Hitung_hipertensi() {
        int x = 0;
        String a = "hipertensi";
        for (int i = 0; i < sample_kelas.length; i++) {
            if (sample_kelas[i].equals(a)) {
                x = x + 1;
            } else {
                continue;
            }
        }
        return x;
    }
}