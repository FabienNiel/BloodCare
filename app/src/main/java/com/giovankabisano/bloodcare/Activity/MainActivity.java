package com.giovankabisano.bloodcare.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.giovankabisano.bloodcare.Adapter.ViewPagerAdapter;
import com.giovankabisano.bloodcare.Fragment.InsightFragment;
import com.giovankabisano.bloodcare.Fragment.MeasureFragment;
import com.giovankabisano.bloodcare.Manifest;
import com.giovankabisano.bloodcare.Math.StaticConfig;
import com.giovankabisano.bloodcare.Model.Member;
import com.giovankabisano.bloodcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static String TAG = "MainActivity";
    private ViewPager viewPager;
    private TabLayout tabLayout = null;
    public static String STR_INSIGHT_FRAGMENT = "INSIGHT";
    public static String STR_MEASURE_FRAGMENT = "MEASURE";

    private ViewPagerAdapter adapter;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    DrawerLayout drawer;
    NavigationView navigationView;
    View headerView;
    protected TextView tv_nama, tv_age, tv_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Blood Care");
        }

        viewPager = findViewById(R.id.viewpager);

        drawer = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        tv_nama = headerView.findViewById(R.id.bio_name);
        tv_age = headerView.findViewById(R.id.bio_age);
        tv_email = headerView.findViewById(R.id.bio_email);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CAMERA,
            }, 7001);
        }
        initTab();
        initFirebase();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    StaticConfig.UID = user.getUid();
                    FirebaseDatabase.getInstance().getReference().child("member/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap hashUser = (HashMap) dataSnapshot.getValue();
                            Member userInfo = new Member();
                            userInfo.nama = (String) hashUser.get("nama");
                            userInfo.email = (String) hashUser.get("email");
                            String umur = (String) hashUser.get("umur");
                            userInfo.umur = Double.valueOf(umur);
                            DecimalFormat format = new DecimalFormat("0.#");
                            tv_nama.setText(userInfo.nama);
                            tv_email.setText(userInfo.email);
                            tv_age.setText(String.valueOf(format.format(userInfo.umur))+ " Tahun");
                            StaticConfig.STR_EXTRA_USERNAME = userInfo.nama;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    MainActivity.this.finish();
                    // User is signed in
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }

    private void initTab() {
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorIndivateTab));
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }


    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.ic_tab_measure,
                R.drawable.ic_tab_insight
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MeasureFragment(), STR_MEASURE_FRAGMENT);
        adapter.addFrag(new InsightFragment(), STR_INSIGHT_FRAGMENT);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
//            case R.id.nav_about:
//                startActivity(new Intent(MainActivity.this, AboutActivity.class));
//                break;
            case R.id.nav_map:
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                break;
        }
        return true;
    }
}
