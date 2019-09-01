package org.gg.glibrary.adapter;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.gg.glibrary.R;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubAdapter1 extends RecyclerView.Adapter<SubAdapter1.ViewHolder> {

    private int color;

    public SubAdapter1() {
        color = new Random().nextInt();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv.setBackground(new ColorDrawable(color));
        holder.tv.setText("POSITION = " + (position + 1));
    }

    @Override
    public int getItemCount() {
        return 40;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = (TextView) itemView;
        }
    }
}
