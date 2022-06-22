package com.example.flashgig.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashgig.GlideApp;
import com.example.flashgig.R;
import com.example.flashgig.activities.ChatActivity;
import com.example.flashgig.fragments.DisplayWorker;
import com.example.flashgig.models.Job;
import com.example.flashgig.models.User;
import com.example.flashgig.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ClientRecyclerViewAdapter extends RecyclerView.Adapter<ClientRecyclerViewAdapter.MyViewHolder> {
    private FirebaseFirestore db;
    private Context context;
    private ArrayList<User> workerList;
    private ItemClickListener clickListener;
    private Job job;
    private String jobId;
    private Button rateButton;
    public User curWorker;
    private StorageReference storageRef;

    public ClientRecyclerViewAdapter(Context context, ArrayList<User> workerList, ItemClickListener clickListener, String jobId) {
        this.context = context;
        this.workerList = workerList;
        this.clickListener = clickListener;
        this.jobId = jobId;
    }



    @NonNull
    @Override
    public ClientRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.worker_recycler_view_row, parent, false);



        return new ClientRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientRecyclerViewAdapter.MyViewHolder holder, int position) {
        User curWorker = workerList.get(position);
        storageRef = FirebaseStorage.getInstance().getReference();
        //holder.imageWorker.setImageResource(curWorker.get);
        Log.d("Rating", "list of rated jobs: "+curWorker.ratings.keySet());
        Log.d("Rating", "job ID"+jobId);

        if(curWorker.ratings.keySet().contains(jobId)){
            holder.editComment.setVisibility(View.INVISIBLE);
            holder.rateButton.setVisibility(View.GONE);
            holder.textComment.setVisibility(View.VISIBLE);
            holder.ratingBar.setIsIndicator(true);
            holder.textComment.setText(curWorker.getComment(jobId).getText());
            float rating = curWorker.getComment(jobId).getRating();
//            Log.d("Rating", "Existing Rating!: "+rating);
            holder.ratingBar.setRating(rating);
        }
        else{
            Log.d("rating", "No rating yet! Setting to zero: ");
        }

        holder.textViewWName.setText(curWorker.getFullName());
        holder.textViewWNumber.setText(curWorker.getPhone());
        holder.textViewWEmail.setText(curWorker.getEmail());
        holder.rateButton.setOnClickListener(view ->{
            Toast.makeText(context, "Feedback Submitted!", Toast.LENGTH_SHORT).show();
            holder.editComment.setVisibility(View.INVISIBLE);
            holder.rateButton.setVisibility(View.GONE);
            holder.ratingBar.setIsIndicator(true);
            holder.textComment.setVisibility(View.VISIBLE);
            clickListener.RateBtnOnClick(curWorker.userId, jobId, holder.ratingBar.getRating(),holder.editComment.getText().toString());
        });
        holder.chatButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(Constants.KEY_USER, curWorker);
            context.startActivity(intent);
        });
        holder.workerCard.setOnClickListener(view -> {
            Fragment fragment = DisplayWorker.newInstance(curWorker.getUserId(), jobId);
            FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fragment, "displayWorker");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        StorageReference profilePicRef = storageRef.child("media/images/profile_pictures/" + curWorker.getUserId());
        profilePicRef.getMetadata().addOnSuccessListener(storageMetadata -> {
            GlideApp.with(context)
                    .load(profilePicRef)
                    .fitCenter()
                    .into(holder.imageClient);
        }).addOnFailureListener(e -> {
            Log.d("Profile", "retrieveInfo: "+e.toString());
//            Snackbar.make(binding.getRoot(), "File does not exist!", Snackbar.LENGTH_SHORT).show();
        });
//        holder.rateButton.setOnClickListener(view -> clickListener.onIt);
    }

    @Override
    public int getItemCount() {
        return workerList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //ImageView imageWorker;
        TextView textViewWName, textViewWNumber, textViewWEmail, textComment;
        EditText editComment;
        CardView workerCard;
        RatingBar ratingBar;
        Button rateButton, chatButton;
        ImageView imageClient;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //imageWorker = itemView.findViewById(R.id.imageWorker);
            textViewWName = itemView.findViewById(R.id.textWorkerName);
            textViewWNumber = itemView.findViewById(R.id.textWorkerNumber);
            textViewWEmail = itemView.findViewById(R.id.textWorkerEmail);
            textComment = itemView.findViewById(R.id.comText);
            editComment = itemView.findViewById(R.id.editComText);
            workerCard = itemView.findViewById(R.id.workerCardPopup);
            ratingBar = itemView.findViewById(R.id.rbWorker);
            rateButton = itemView.findViewById(R.id.rateBtn);
            chatButton = itemView.findViewById(R.id.btnChatWorkerRow);
            imageClient = itemView.findViewById(R.id.imageWorker);
        }
    }

    public interface ItemClickListener {
        public void RateBtnOnClick(String jobId, String userId, float rating, String s);
    }

}
