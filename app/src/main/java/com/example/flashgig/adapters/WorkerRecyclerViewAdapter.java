package com.example.flashgig.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashgig.R;
import com.example.flashgig.models.Job;
import com.example.flashgig.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class WorkerRecyclerViewAdapter extends RecyclerView.Adapter<WorkerRecyclerViewAdapter.MyViewHolder> {
    private FirebaseFirestore db;
    private Context context;
    private ArrayList<User> workerList;
    private ItemClickListener clickListener;
    private Job job;
    private String jobId;
    private Button rateButton;
    public User curWorker;
    public WorkerRecyclerViewAdapter(Context context, ArrayList<User> workerList, ItemClickListener clickListener, String jobId) {
        this.context = context;
        this.workerList = workerList;
        this.clickListener = clickListener;
        this.jobId = jobId;
    }



    @NonNull
    @Override
    public WorkerRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.worker_recycler_view_row, parent, false);



        return new WorkerRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerRecyclerViewAdapter.MyViewHolder holder, int position) {
        User curWorker = workerList.get(position);
        //holder.imageWorker.setImageResource(curWorker.get);
        Log.d("Rating", "list of rated jobs: "+curWorker.ratings.keySet());
        Log.d("Rating", "job ID"+jobId);

        if(curWorker.getRating(jobId) != null){
            int rating = curWorker.getRating(jobId);
            Log.d("Rating", "Existing Rating!: "+rating);
            holder.ratingBar.setRating(rating);
        }
        else{
            Log.d("rating", "No rating yet! Setting to zero: ");
        }

        holder.textViewWName.setText(curWorker.getFullName());
        holder.textViewWNumber.setText(curWorker.getPhone());
        holder.textViewWEmail.setText(curWorker.getEmail());
        holder.workerCard.setOnClickListener(view -> clickListener.onItemClickWorker(curWorker.userId, jobId));
        holder.rateButton.setOnClickListener(view -> clickListener.RateBtnOnClick(curWorker.userId, jobId, holder.ratingBar.getRating()));
//        holder.rateButton.setOnClickListener(view -> clickListener.onIt);
    }

    @Override
    public int getItemCount() {
        return workerList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //ImageView imageWorker;
        TextView textViewWName, textViewWNumber, textViewWEmail;
        CardView workerCard;
        RatingBar ratingBar;
        Button rateButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //imageWorker = itemView.findViewById(R.id.imageWorker);
            textViewWName = itemView.findViewById(R.id.textWorkerName);
            textViewWNumber = itemView.findViewById(R.id.textWorkerNumber);
            textViewWEmail = itemView.findViewById(R.id.textWorkerEmail);
            workerCard = itemView.findViewById(R.id.workerCardPopup);
            ratingBar = itemView.findViewById(R.id.rbWorker);
            rateButton = itemView.findViewById(R.id.rateBtn);
        }
    }

    public interface ItemClickListener {
        public void onItemClickWorker(String userId1, String jobId1);
        public void RateBtnOnClick(String jobId, String userId, float rating);
    }

}
