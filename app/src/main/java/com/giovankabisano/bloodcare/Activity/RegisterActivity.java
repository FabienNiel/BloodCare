package com.giovankabisano.bloodcare.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.giovankabisano.bloodcare.Model.Member;
import com.giovankabisano.bloodcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    Calendar myCalendar;
    Button register;
    EditText et_nama, et_email, et_password, et_berat, et_tinggi, et_umur;
    private String nama, email, password;
    private int berat, tinggi;
    Double umur;
    private boolean b_rokok;
    RadioGroup radioGroup;
    RadioButton radioButton;
    FirebaseUser user;
    FirebaseAuth mAuth;
    DatePickerDialog.OnDateSetListener date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        et_nama = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_berat = findViewById(R.id.et_berat);
        et_tinggi = findViewById(R.id.et_tinggi);
        et_umur = findViewById(R.id.et_umur);
        radioGroup = findViewById(R.id.radio_group);
        register = findViewById(R.id.btnRegister);
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        et_umur.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegisterActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_umur.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
    }

    private void doRegister() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(selectedId);
        if (radioButton.getText().equals("Merokok")) {
            b_rokok = true;
        }else if (radioButton.getText().equals("Tidak Merokok")){
            b_rokok = false;
        }
        nama = et_nama.getText().toString();
        email = et_email.getText().toString();
        password = et_password.getText().toString();
        berat = Integer.valueOf(et_berat.getText().toString());
        tinggi = Integer.valueOf(et_tinggi.getText().toString());
        umur = getAges(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        Log.d("cek", email + password);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("asu", "akwjklq");
                        if (task.isSuccessful()) {
                            Log.d("asususu", "akwjklq");

                            user = mAuth.getCurrentUser();
                            user = mAuth.getCurrentUser();
                            saveUser();
                            Toast.makeText(RegisterActivity.this, "Register and Login success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            RegisterActivity.this.finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private Double getAges(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Double ageS = Double.valueOf(age);
        return ageS;
    }

    private void saveUser() {
        Member newUser = new Member(nama,email,password,tinggi,berat,umur,b_rokok);
        FirebaseDatabase.getInstance().getReference().child("member/" + user.getUid()).setValue(newUser);
    }
}
