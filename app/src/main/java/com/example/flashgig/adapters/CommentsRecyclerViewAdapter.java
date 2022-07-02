package com.example.flashgig.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.R;
import com.example.flashgig.activities.ChatActivity;
import com.example.flashgig.databinding.ClientFeedbackRecyclerViewRowBinding;
import com.example.flashgig.fragments.DisplayWorker;
import com.example.flashgig.models.Comment;
import com.example.flashgig.models.User;
import com.example.flashgig.utilities.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<Comment> comments;
    private Context ctx;
    private StorageReference storageRef;

    public CommentsRecyclerViewAdapter(Context ctx, ArrayList<Comment> commentList){
        this.ctx = ctx;
        this.comments = commentList;
    }

    @NonNull
    @Override
    public CommentsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("RAN", "onCreateViewHolder: Succefully inflated");
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.client_feedback_recycler_view_row, parent, false);
        return new CommentsRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsRecyclerViewAdapter.MyViewHolder holder, int position) {
        Comment curComment = comments.get(position);
        storageRef = FirebaseStorage.getInstance().getReference();
        holder.name.setText(curComment.getRator());
        holder.comment.setText(curComment.getText());
        holder.rating.setRating(curComment.getRating());
        holder.userCardView.setOnClickListener(view -> {
            getUserProfile(curComment.ratorId);
        });
        StorageReference profilePicRef = storageRef.child("media/images/profile_pictures/" + curComment.getRatorId());
        profilePicRef.getMetadata().addOnSuccessListener(storageMetadata -> {
            GlideApp.with(ctx)
                    .load(profilePicRef)
                    .fitCenter()
                    .into(holder.profilePic);
            holder.progressBar.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            holder.progressBar.setVisibility(View.GONE);
            holder.profilePic.setImageResource(R.drawable.ic_baseline_account_circle_24);
            Log.d("CommentsRecyclerView", "onBindViewHolder: "+ e);
        });

    }

    private void getUserProfile(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                User user =task.getResult().toObject(User.class);
                Fragment fragment = DisplayWorker.newInstance(user.getUserId(), "");
                FragmentTransaction fragmentTransaction = ((AppCompatActivity) ctx).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment, "displayWorker");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            else{
                Log.d("RCVA", "getUserProfile: ");
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder {
        ClientFeedbackRecyclerViewRowBinding binding = ClientFeedbackRecyclerViewRowBinding.inflate(LayoutInflater.from(ctx));
        TextView name, email, comment;
        RatingBar rating;
        ImageView profilePic;
        CardView userCardView;
        ProgressBar progressBar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profilePicClientFeedback);
            name = itemView.findViewById(R.id.textJobClientName);
            comment = itemView.findViewById(R.id.textClientFeedback);
            email = itemView.findViewById(R.id.textJobClientEmail);
            rating = itemView.findViewById(R.id.ratingBarClient);
            progressBar = itemView.findViewById(R.id.progressBarDetail);
            userCardView = itemView.findViewById(R.id.userCardFeedbackRow);
        }
    }
}
