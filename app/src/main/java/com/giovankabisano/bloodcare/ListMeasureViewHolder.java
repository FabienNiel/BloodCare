package com.giovankabisano.bloodcare;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ListMeasureViewHolder extends RecyclerView.ViewHolder{
    public TextView txtHeartRate, txtDP, txtSP, dateTime;
    public ImageView imgArrow;

    public ListMeasureViewHolder(View itemView) {
        super(itemView);
        imgArrow = itemView.findViewById(R.id.insight_arrow_image);
        txtHeartRate = itemView.findViewById(R.id.txt_HeartRate);
        txtDP = itemView.findViewById(R.id.txt_DP);
        txtSP = itemView.findViewById(R.id.txt_SP);
        dateTime = itemView.findViewById(R.id.txt_dateTime);
    }
}
