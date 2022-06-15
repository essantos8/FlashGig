package com.example.flashgig.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.R;
import com.example.flashgig.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BidderRecyclerViewAdapter extends RecyclerView.Adapter<BidderRecyclerViewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<User> bidderList;
    private ItemClickListener clickListener;
    private String jobId, type;
    private StorageReference storageRef;

    public BidderRecyclerViewAdapter(Context context, ArrayList<User> bidderList, ItemClickListener clickListener, String jobId, String type) {
        this.context = context;
        this.bidderList = bidderList;
        this.clickListener = clickListener;
        this.jobId = jobId;
        this.type = type;
    }

    @NonNull
    @Override
    public BidderRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.bidder_recycler_view_row, parent, false);
        return new BidderRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BidderRecyclerViewAdapter.MyViewHolder holder, int position) {
        User curBidder = bidderList.get(position);
        storageRef = FirebaseStorage.getInstance().getReference();

        //holder.imageBidder.setImageResource(curBidder.get);
        holder.textViewBName.setText(curBidder.getFullName());
        holder.textViewBNumber.setText(curBidder.getPhone());
        holder.textViewBEmail.setText(curBidder.getEmail());
        if(type.equals("bidder")){
            holder.bidderCard.setOnClickListener(view -> clickListener.onItemClickBidder(curBidder.userId, jobId));
        }
        else{
            holder.bidderCard.setOnClickListener(view -> clickListener.onItemClickWorker(curBidder.userId, jobId));
        }
        StorageReference profilePicRef = storageRef.child("media/images/profile_pictures/" + curBidder.getUserId());
        profilePicRef.getMetadata().addOnSuccessListener(storageMetadata -> {
//            Snackbar.make(binding.getRoot(), "File exists!", Snackbar.LENGTH_SHORT).show();
                GlideApp.with(context)
                        .load(profilePicRef)
                        .signature(new ObjectKey(String.valueOf(storageMetadata.getCreationTimeMillis())))
                        .fitCenter()
                        .into(holder.imageBidder);
        }).addOnFailureListener(e -> {
            Log.d("Profile", "retrieveInfo: "+e.toString());
//            Snackbar.make(binding.getRoot(), "File does not exist!", Snackbar.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return bidderList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewBName, textViewBNumber, textViewBEmail;
        CardView bidderCard;
        ImageView imageBidder;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageBidder = itemView.findViewById(R.id.imageBidder);
            textViewBName = itemView.findViewById(R.id.textBidderName);
            textViewBNumber = itemView.findViewById(R.id.textBidderNumber);
            textViewBEmail = itemView.findViewById(R.id.textBidderEmail);
            bidderCard = itemView.findViewById(R.id.bidderCardPopup);
        }
    }

    public interface ItemClickListener {
        public void onItemClickBidder(String userId, String jobId);
        public void onItemClickWorker(String userId, String jobId);
    }
}
