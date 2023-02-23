package com.example.appdev2assign2;

import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RouteViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView number;
    ConstraintLayout constraint;


    RouteViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.routeName);
        number = view.findViewById(R.id.routeNum);
        constraint = view.findViewById(R.id.constraint);

    }
}
