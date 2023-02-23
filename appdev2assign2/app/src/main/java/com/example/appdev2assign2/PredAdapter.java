package com.example.appdev2assign2;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PredAdapter extends RecyclerView.Adapter<PredViewHolder>{
    private final List<Prediction> PredList;
    private final PredictionsActivity predAct;
    private long start;
    PredAdapter(List<Prediction> empList, PredictionsActivity p) {
        this.PredList = empList;
        predAct = p;
    }

    @NonNull
    @Override
    public PredViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pred_entry, parent, false);

        itemView.setOnClickListener(predAct);

        return new PredViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PredViewHolder holder, int position) {
        Prediction pred = PredList.get(position);
        String[] s = pred.predtime.split(" ");
        if(s == null || s.length == 1) {
            holder.busTime.setText(pred.predtime);
        }
        else{
            String time = s[1];
            holder.busTime.setText(time);
        }
        holder.busMin.setText("Due in " + pred.pred + " mins at");
        holder.busName.setText("Bus #" + pred.vid);
        holder.busDes.setText(pred.des);
        int color = Color.parseColor(pred.color);
        if (Color.luminance(color) < 0.25) {
            holder.busTime.setTextColor(Color.WHITE);
            holder.busMin.setTextColor(Color.WHITE);
            holder.busName.setTextColor(Color.WHITE);
            holder.busDes.setTextColor(Color.WHITE);
        } else {
            holder.busTime.setTextColor(Color.BLACK);
            holder.busMin.setTextColor(Color.BLACK);
            holder.busName.setTextColor(Color.BLACK);
            holder.busDes.setTextColor(Color.BLACK);
        }
        holder.constraint.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return PredList.size();
    }

}
