package com.giovankabisano.bloodcare.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.giovankabisano.bloodcare.Model.Measurement;
import com.giovankabisano.bloodcare.Model.Member;
import com.giovankabisano.bloodcare.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    android.support.v7.widget.Toolbar toolbar;
    DatabaseReference mProfileReference;
    TextView tv_umur, tv_email;
    EditText et_nama, et_tinggi, et_berat;
    Button save;
    String nama, tinggi, berat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tv_email = findViewById(R.id.profile_email);
        tv_umur = findViewById(R.id.profile_umur);
        et_tinggi = findViewById(R.id.profile_tinggi);
        et_berat = findViewById(R.id.profile_berat);
        et_nama = findViewById(R.id.profile_name);
        save = findViewById(R.id.profile_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfil();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        displayProfile();
    }

    private void displayProfile() {
        mProfileReference = FirebaseDatabase.getInstance().getReference("member/" + user.getUid());
        mProfileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tv_email.setText(dataSnapshot.child("email").getValue(String.class));
                tv_umur.setText(dataSnapshot.child("umur").getValue(String.class) + " Tahun");
                et_nama.setText(dataSnapshot.child("nama").getValue(String.class));
                et_tinggi.setText(dataSnapshot.child("tinggi").getValue(String.class));
                et_berat.setText(dataSnapshot.child("berat").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateProfil() {
        nama = et_nama.getText().toString();
        tinggi = et_tinggi.getText().toString();
        berat = et_berat.getText().toString();
        FirebaseDatabase.getInstance().getReference("member/" + user.getUid() + "/nama").setValue(nama);
        FirebaseDatabase.getInstance().getReference("member/" + user.getUid() + "/tinggi").setValue(tinggi);
        FirebaseDatabase.getInstance().getReference("member/" + user.getUid() + "/berat").setValue(berat);
        Toast.makeText(ProfileActivity.this, "Update Berhasil", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
