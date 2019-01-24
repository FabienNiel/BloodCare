package com.giovankabisano.bloodcare.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.giovankabisano.bloodcare.ListMeasureViewHolder;
import com.giovankabisano.bloodcare.Model.Measurement;
import com.giovankabisano.bloodcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class InsightFragment extends Fragment {

    FirebaseRecyclerAdapter mFirebaseAdapter;
    DatabaseReference mRestaurantReference;
    RecyclerView mRecyclerView;
    ImageView mImageView;
    TextView mTextView;

    public InsightFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_insight, container, false);
        mRecyclerView = layout.findViewById(R.id.recycleListHistory);
        mImageView = layout.findViewById(R.id.insight_empty_image);
        mImageView.setVisibility(View.GONE);
        mTextView = layout.findViewById(R.id.insight_empty_text);
        mTextView.setVisibility(View.GONE);
        mRestaurantReference = FirebaseDatabase.getInstance().getReference("measurement")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        updateList();
        setUpFirebaseAdapter();
        return layout;
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Measurement, ListMeasureViewHolder>
                (Measurement.class, R.layout.insight_layout, ListMeasureViewHolder.class,
                        mRestaurantReference) {

            @Override
            protected void populateViewHolder(ListMeasureViewHolder viewHolder,
                                              Measurement model, int position) {
                if (model.getHeartRate() == 0) {
                    viewHolder.txtHeartRate.setText("Heart Rate : No Data");
                } else if (model.getHeartRate() != 0) {
                    viewHolder.txtHeartRate.setText("Heart Rate : " + String.valueOf(model.getHeartRate()));
                }
                viewHolder.txtDP.setText(String.valueOf(model.getDiastolic()));
                viewHolder.txtSP.setText(String.valueOf(model.getSistolic()));
                if (model.getSistolic() > 135){
                    viewHolder.imgArrow.setImageResource(R.drawable.up);
                }else if (model.getSistolic() < 120){
                    viewHolder.imgArrow.setImageResource(R.drawable.down);
                }else{
                    viewHolder.imgArrow.setImageResource(R.drawable.ok);
                }
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(model.getTimestamp());
                String date = DateFormat.format("dd-MM-yyyy", cal).toString();
                viewHolder.dateTime.setText(String.valueOf(date));
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    private void updateList() {
        final ArrayList<Measurement> data = new ArrayList<>();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("measurement");
        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userDetails : dataSnapshot.getChildren()) {
                        data.add(userDetails.getValue(Measurement.class));
                    }
                    Collections.reverse(data);
                } else if (!dataSnapshot.exists()) {
                    mRecyclerView.setVisibility(View.GONE);
                    mImageView.setVisibility(View.VISIBLE);
                    mTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}