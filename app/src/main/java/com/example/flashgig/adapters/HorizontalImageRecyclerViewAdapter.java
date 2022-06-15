package com.example.flashgig.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.flashgig.GlideApp;
import com.example.flashgig.R;
import com.example.flashgig.databinding.ImagePopupBinding;
import com.example.flashgig.fragments.ImagePopupFragment;
import com.example.flashgig.models.Image;

import java.util.ArrayList;

public class HorizontalImageRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalImageRecyclerViewAdapter.MyViewHolder> {
    private Context ctx;
    private ArrayList<Uri> imageArrayList;
    private LayoutInflater inflater;
    private HorizontalImageRecyclerViewAdapter.ItemClickListener clickListener;


    public HorizontalImageRecyclerViewAdapter(Context ctx, ArrayList<Uri> imageArrayList, ItemClickListener clickListener) {
        this.clickListener = clickListener;
        this.ctx = ctx;
        this.imageArrayList = imageArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.recycler_view_horizontal_image, parent, false);
        return new HorizontalImageRecyclerViewAdapter.MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull HorizontalImageRecyclerViewAdapter.MyViewHolder holder, int position) {
        // assign values for each view holder as they come on screen
        // based on position of recycler view
        Uri curImage = imageArrayList.get(position);
        ProgressBar progressBar = holder.progressBar;
        if(curImage == null){
            Toast.makeText(ctx, "bat null lol", Toast.LENGTH_SHORT).show();
        }
        GlideApp.with(holder.itemView)
                .load(curImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imageView);

        holder.imageView.setOnClickListener(view -> {
            clickListener.onItemClick(curImage);
        });
    }

    @Override
    public int getItemCount() {
        // number of items in total
        return imageArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // grabbing the views from the row layout file
        // similar with oncreate

        ImageView imageView;
        ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewItemHorizontal);
            progressBar = itemView.findViewById(R.id.progressBarImage);
        }
    }

    public interface ItemClickListener {
        void onItemClick(Uri imageUri);
    }

}
