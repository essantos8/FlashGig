package com.example.flashgig.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashgig.R;
import com.example.flashgig.models.Comment;

import java.util.ArrayList;

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<Comment> comments;
    private Context ctx;

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
        holder.name.setText(curComment.getRator());
        holder.comment.setText(curComment.getText());
        holder.rating.setRating(curComment.getRating());

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder {
        TextView name, email, comment;
        RatingBar rating;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textJobClientName);
            comment = itemView.findViewById(R.id.textClientFeedback);
            rating = itemView.findViewById(R.id.ratingBarClient);
        }
    }
}
