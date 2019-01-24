package com.giovankabisano.bloodcare.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.giovankabisano.bloodcare.Activity.MeasureActivity;
import com.giovankabisano.bloodcare.Activity.ResultActivity;
import com.giovankabisano.bloodcare.R;

public class MeasureFragment extends Fragment {

    Button measureBloodPressure, submitBloodPressure;
    TextView sistolik, diastolik, tutorial;

    Dialog tutorialDialog;

    public MeasureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_measure, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tutorial = getView().findViewById(R.id.measure_tutorial);
        sistolik = getView().findViewById(R.id.sp);
        diastolik = getView().findViewById(R.id.dp);
        tutorialDialog = new Dialog(getContext());
        measureBloodPressure = getView().findViewById(R.id.measure_blood_pressure);
        measureBloodPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MeasureActivity.class);
                startActivity(intent);
            }
        });
        submitBloodPressure = getView().findViewById(R.id.button_submit_pressure);
        submitBloodPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int SP = Integer.parseInt(sistolik.getText().toString());
                int DP = Integer.parseInt(diastolik.getText().toString());
                Intent intent = new Intent(getActivity(), ResultActivity.class);
                intent.putExtra("SP", SP);
                intent.putExtra("DP", DP);
                startActivity(intent);
                getActivity().finish();
            }
        });
        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp();
            }
        });
    }

    private void showPopUp() {
        Button close;

        tutorialDialog.setContentView(R.layout.tutorial_popup);
        close = tutorialDialog.findViewById(R.id.tutorial_buttonOk);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutorialDialog.dismiss();
            }
        });

        tutorialDialog.show();
    }
}
