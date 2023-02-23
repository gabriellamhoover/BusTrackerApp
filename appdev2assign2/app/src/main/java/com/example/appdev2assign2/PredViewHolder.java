package com.example.appdev2assign2;

import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class PredViewHolder extends RecyclerView.ViewHolder {
    TextView busName;
    TextView busDes;
    TextView busTime;
    TextView busMin;
    ConstraintLayout constraint;


    PredViewHolder(View view) {
        super(view);
        busName = view.findViewById(R.id.busTextView);
        busDes = view.findViewById(R.id.desTextView);
        busTime = view.findViewById(R.id.timeTextView);
        busMin =  view.findViewById(R.id.minTextView);
        constraint = view.findViewById(R.id.predConstraint);

    }
}
