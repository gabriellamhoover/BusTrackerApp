package com.example.appdev2assign2;

import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class StopViewHolder  extends RecyclerView.ViewHolder {
    TextView dist;
    TextView name;
    ConstraintLayout constraint;


    StopViewHolder(View view) {
        super(view);
        dist = view.findViewById(R.id.stopDistanceTextView);
        name = view.findViewById(R.id.stopNameTextView);
        constraint = view.findViewById(R.id.stopConstraint);

    }

}
