package com.example.flashgig.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashgig.models.Job;
import com.example.flashgig.R;

import java.util.ArrayList;

public class JobRecyclerViewAdapter extends RecyclerView.Adapter<JobRecyclerViewAdapter.MyViewHolder> {
    Context ctx;
    ArrayList<Job> jobArrayList;

    public JobRecyclerViewAdapter(Context ctx, ArrayList<Job> jobArrayList){
        this.ctx = ctx;
        this.jobArrayList = jobArrayList;
    }


    @NonNull
    @Override
    public JobRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull JobRecyclerViewAdapter.MyViewHolder holder, int position) {
        // assign values for each view holder as they come on screen
        // based on position of recycler view
        holder.textViewTitle.setText(jobArrayList.get(position).getTitle());
        holder.textViewDesc.setText(jobArrayList.get(position).getDescription());
        String categories = "";
        for (int i = 0; i < jobArrayList.get(position).getCategories().size(); i++) {
            categories += jobArrayList.get(position).getCategories().get(i);
            if(i != jobArrayList.get(position).getCategories().size() -1){
                categories += ", ";
            }
        }
        holder.textViewCategories.setText(categories);
    }

    @Override
    public int getItemCount() {
        // number of items in total
        return jobArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // grabbing the views from the row layout file
        // similar with oncreate

        TextView textViewTitle, textViewDesc, textViewCategories;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textJobTitle);
            textViewDesc = itemView.findViewById(R.id.textJobDescription);
            textViewCategories = itemView.findViewById(R.id.textJobCategories);

        }
    }
}
