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

public class BidderRecyclerViewAdapter extends RecyclerView.Adapter<BidderRecyclerViewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<User> bidderList;
    private ItemClickListener clickListener;
    private String jobId;

    public BidderRecyclerViewAdapter(Context context, ArrayList<User> bidderList, ItemClickListener clickListener, String jobId) {
        this.context = context;
        this.bidderList = bidderList;
        this.clickListener = clickListener;
        this.jobId = jobId;
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

        //holder.imageBidder.setImageResource(curBidder.get);
        holder.textViewBName.setText(curBidder.getFullName());
        holder.textViewBNumber.setText(curBidder.getPhone());
        holder.textViewBEmail.setText(curBidder.getEmail());

        holder.bidderCard.setOnClickListener(view -> clickListener.onItemClick(curBidder.userId, jobId));
    }

    @Override
    public int getItemCount() {
        return bidderList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //ImageView imageBidder;
        TextView textViewBName, textViewBNumber, textViewBEmail;
        CardView bidderCard;
        //Button buttonAcceptBidder;
        //Button buttonDeclineBidder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //imageBidder = itemView.findViewById(R.id.imageBidder);
            textViewBName = itemView.findViewById(R.id.textBidderName);
            textViewBNumber = itemView.findViewById(R.id.textBidderNumber);
            textViewBEmail = itemView.findViewById(R.id.textBidderEmail);
            bidderCard = itemView.findViewById(R.id.bidderCardPopup);
            /*
            itemView.findViewById(R.id.btnAcceptBidder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            itemView.findViewById(R.id.btnDeclineBidder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
        }
    }

    public interface ItemClickListener {
        public void onItemClick(String userId, String jobId);
    }
}
