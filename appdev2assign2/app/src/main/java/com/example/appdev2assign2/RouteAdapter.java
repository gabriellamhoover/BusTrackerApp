package com.example.appdev2assign2;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class RouteAdapter extends RecyclerView.Adapter<RouteViewHolder>{
    private final List<Route> RouteList;
    private final MainActivity mainAct;
    private long start;
    RouteAdapter(List<Route> empList, MainActivity ma) {
        this.RouteList = empList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_entry, parent, false);

        itemView.setOnClickListener(mainAct);

        return new RouteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Route route = RouteList.get(position);
        holder.name.setText(route.name);
        holder.number.setText(route.number);
        int color = Color.parseColor(route.color);
        if (Color.luminance(color) < 0.25) {
            holder.number.setTextColor(Color.WHITE);
            holder.name.setTextColor(Color.WHITE);
        } else {
            holder.number.setTextColor(Color.BLACK);
            holder.name.setTextColor(Color.BLACK);
        }
        holder.constraint.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return RouteList.size();
    }
}
