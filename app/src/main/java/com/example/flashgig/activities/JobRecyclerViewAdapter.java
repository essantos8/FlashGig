package com.example.flashgig.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
        Job curJob = jobArrayList.get(position);
        holder.textViewTitle.setText(curJob.getTitle());
        holder.textViewDesc.setText(curJob.getDescription());
        String categories = "";
        for (int i = 0; i < curJob.getCategories().size(); i++) {
            categories += curJob.getCategories().get(i);
            if(i != curJob.getCategories().size() -1){
                categories += ", ";
            }
        }
        holder.textViewCategories.setText(categories);
        holder.textViewDate.setText(curJob.getDate());
        holder.textViewClient.setText(curJob.getClient());
        holder.textViewWorkers.setText(String.valueOf(curJob.getWorkers().size()));
        holder.textViewLocationRegion.setText(jobArrayList.get(position).getLocation().getRegion());
        holder.textViewLocationCity.setText(jobArrayList.get(position).getLocation().getCity());
        holder.textViewLocationBaranggay.setText(jobArrayList.get(position).getLocation().getBaranggay());
    }

    @Override
    public int getItemCount() {
        // number of items in total
        return jobArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // grabbing the views from the row layout file
        // similar with oncreate

        TextView textViewTitle, textViewDesc, textViewCategories, textViewDate, textViewClient, textViewLocationRegion, textViewLocationCity, textViewLocationBaranggay, textViewWorkers;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textJobTitle);
            textViewDesc = itemView.findViewById(R.id.textJobDescription);
            textViewCategories = itemView.findViewById(R.id.textJobCategories);
            textViewDate = itemView.findViewById(R.id.textJobDate);
            textViewClient = itemView.findViewById(R.id.textJobClient);
            textViewLocationRegion = itemView.findViewById(R.id.textJobLocationRegion);
            textViewLocationCity = itemView.findViewById(R.id.textJobLocationCity);
            textViewLocationBaranggay = itemView.findViewById(R.id.textJobLocationBaranggay);
            textViewWorkers = itemView.findViewById(R.id.textJobWorkers);

        }
    }
}
