package com.example.flashgig.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.R;
import com.example.flashgig.databinding.ClientFeedbackRecyclerViewRowBinding;
import com.example.flashgig.models.Comment;
import com.google.firebase.database.FirebaseDatabase;
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
        // get rator id
        StorageReference profilePicRef = storageRef.child("media/images/profile_pictures/" + curComment.getRatorId());
        profilePicRef.getMetadata().addOnSuccessListener(storageMetadata -> {
            GlideApp.with(ctx)
                    .load(profilePicRef)
                    .signature(new ObjectKey(String.valueOf(storageMetadata.getCreationTimeMillis())))
                    .fitCenter()
                    .into(holder.profilePic);
            holder.progressBar.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            Log.d("CommentsRecyclerView", "onBindViewHolder: "+e.toString());
//            Snackbar.make(binding.getRoot(), "File does not exist!", Snackbar.LENGTH_SHORT).show();
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
        ProgressBar progressBar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profilePicClientFeedback);
            name = itemView.findViewById(R.id.textJobClientName);
            comment = itemView.findViewById(R.id.textClientFeedback);
            email = itemView.findViewById(R.id.textJobClientEmail);
            rating = itemView.findViewById(R.id.ratingBarClient);
            progressBar = itemView.findViewById(R.id.progressBarDetail);
        }
    }
}
