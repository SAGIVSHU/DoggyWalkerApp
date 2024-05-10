package com.example.doggywalkerapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TripClassAdapter extends RecyclerView.Adapter<TripClassAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private ArrayList<TripClass> list;

    public TripClassAdapter(Context context, ArrayList<TripClass> list, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.list = list;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.trip_item, parent, false);
        return new MyViewHolder(v, recyclerViewInterface);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TripClass tripClass = list.get(position);
        holder.dogWalkerName.setText(tripClass.getDogWalkerName());
        holder.dogWalkerPhone.setText(tripClass.getDogWalkerPhoneNumber());
        holder.dayOfOccurrence.setText(tripClass.getTripOccurDay());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dogWalkerName, dogWalkerPhone, dayOfOccurrence;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            dogWalkerName = itemView.findViewById(R.id.dogWalkerName);
            dogWalkerPhone = itemView.findViewById(R.id.dogWalkerPhone);
            dayOfOccurrence = itemView.findViewById(R.id.dayOfOccurrence);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }

                    }

                }
            });

        }
    }

}
