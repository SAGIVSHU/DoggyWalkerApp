package com.example.doggywalkerapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DogWalkerAdapter extends RecyclerView.Adapter<DogWalkerAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private ArrayList<DogWalkerClass> list;

    public DogWalkerAdapter(Context context, ArrayList<DogWalkerClass> list, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.list = list;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.dogwalker_item, parent, false);
        return new MyViewHolder(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DogWalkerClass dogWalker = list.get(position);
        holder.name.setText(dogWalker.getName());
        holder.phoneNumber.setText(dogWalker.getPhoneNumber());
        holder.location.setText(dogWalker.getLocation());
        holder.rating.setText(dogWalker.getRating());


        //set image of the dog walker

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child("Uploads/" + dogWalker.getWalkerId());
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.dogWalkerImage.getContext())
                        .load(uri)
                        .error(R.drawable.loading_image)
                        .into(holder.dogWalkerImage);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, phoneNumber, location, rating;
        ImageView dogWalkerImage;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            name = itemView.findViewById(R.id.dogWalkerName);
            phoneNumber = itemView.findViewById(R.id.dogWalkerPhone);
            location = itemView.findViewById(R.id.dogWalkerLocation);
            rating = itemView.findViewById(R.id.dogWalkerRating);
            dogWalkerImage = itemView.findViewById(R.id.dogWalkerImage);

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
