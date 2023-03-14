package com.example.androidthings.attendapp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidthings.attendapp.Constants;
import com.example.androidthings.attendapp.R;
import com.example.androidthings.attendapp.models.RollNumber;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private static ArrayList<RollNumber> rollNumbers = new ArrayList<>();
    public MyAdapter(ArrayList<RollNumber> rollNumbers) {
        MyAdapter.rollNumbers = rollNumbers;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_items, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RollNumber rollNumber = rollNumbers.get(position);
        holder.textView.setText(rollNumber.getRollNumber());
        String attendance = rollNumber.getAttendance();
        if (attendance.equalsIgnoreCase(Constants.PRESENT)){
            holder.rootView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
            holder.rootView.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.purple_700));
            holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black_light));
        }else {
            holder.rootView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.red));
          //  holder.textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
        }
        holder.rootView.setOnClickListener(view -> {

            if (attendance.equalsIgnoreCase(Constants.PRESENT)){
                holder.rootView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.btn_shake));
                rollNumbers.get(position).setAttendance(Constants.ABSENT);
            }else {
                rollNumbers.get(position).setAttendance(Constants.PRESENT);
            }
            notifyItemChanged(position);
        });
    }
    @Override
    public int getItemCount() {
        return rollNumbers.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        private MaterialCardView rootView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.rootView);
            textView = itemView.findViewById(R.id.textView);
        }
    }


    public static ArrayList<RollNumber> getRollNumbers() {
        return rollNumbers;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

}
