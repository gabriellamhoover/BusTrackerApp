package com.example.appdev2assign2;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StopAdapter extends RecyclerView.Adapter<StopViewHolder>{
    private final List<Stop> StopList;
    private final StopActivity stopAct;
    private long start;
    StopAdapter(List<Stop> empList, StopActivity s) {
        this.StopList = empList;
        stopAct = s;
    }

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stop_entry, parent, false);

        itemView.setOnClickListener(stopAct);

        return new StopViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StopViewHolder holder, int position) {
        Stop stop = StopList.get(position);
        String dis = String.format("%.0f m of your location", stop.dist);
        holder.dist.setText(dis);
        holder.name.setText(stop.name);
        int color = Color.parseColor(stop.color);
        if (Color.luminance(color) < 0.25) {
            holder.dist.setTextColor(Color.WHITE);
            holder.name.setTextColor(Color.WHITE);
        } else {
            holder.dist.setTextColor(Color.BLACK);
            holder.name.setTextColor(Color.BLACK);
        }
        holder.constraint.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return StopList.size();
    }
}
