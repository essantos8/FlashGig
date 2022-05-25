package com.example.flashgig.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashgig.R;
import com.example.flashgig.models.User;

import java.util.ArrayList;

public class WorkerRecyclerViewAdapter extends RecyclerView.Adapter<WorkerRecyclerViewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<User> workerList;
    private ItemClickListener clickListener;
    private String jobId1;

    public WorkerRecyclerViewAdapter(Context context, ArrayList<User> workerList, ItemClickListener clickListener, String jobId1) {
        this.context = context;
        this.workerList = workerList;
        this.clickListener = clickListener;
        this.jobId1 = jobId1;
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
        holder.textViewWName.setText(curWorker.getFullName());
        holder.textViewWNumber.setText(curWorker.getPhone());
        holder.textViewWEmail.setText(curWorker.getEmail());

        holder.workerCard.setOnClickListener(view -> clickListener.onItemClickWorker(curWorker.userId, jobId1));
    }

    @Override
    public int getItemCount() {
        return workerList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //ImageView imageWorker;
        TextView textViewWName, textViewWNumber, textViewWEmail;
        CardView workerCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //imageWorker = itemView.findViewById(R.id.imageWorker);
            textViewWName = itemView.findViewById(R.id.textWorkerName);
            textViewWNumber = itemView.findViewById(R.id.textWorkerNumber);
            textViewWEmail = itemView.findViewById(R.id.textWorkerEmail);
            workerCard = itemView.findViewById(R.id.workerCardPopup);
        }
    }

    public interface ItemClickListener {
        public void onItemClickWorker(String userId1, String jobId1);
    }
}
