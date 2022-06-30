package com.example.flashgig.adapters;

import static com.google.api.ResourceProto.resource;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.R;
import com.example.flashgig.databinding.ItemContainerSentMessageBinding;
import com.example.flashgig.databinding.ItemContainterReceivedMessageBinding;
import com.example.flashgig.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.annotation.Target;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<ChatMessage> chatMessages;
    private final String senderId;

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private Context ctx;
    private StorageReference receiverPicRef;

    public ChatAdapter(Context ctx, List<ChatMessage> chatMessages, String senderId) {
        this.ctx = ctx;
        this.chatMessages = chatMessages;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else {
            return new ReceivedMessageViewHolder(
                    ItemContainterReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position){
        if (getItemViewType(position) == VIEW_TYPE_SENT){
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position));
            if(receiverPicRef != null){
                GlideApp.with(ctx)
                        .load(receiverPicRef)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                        .into(((ReceivedMessageViewHolder) holder).binding.imageProfile);
            }
            else {
                ((ReceivedMessageViewHolder) holder).binding.imageProfile.setImageResource(R.drawable.ic_baseline_account_circle_24);
            }
        }

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position){
        if (chatMessages.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        } else{
            return VIEW_TYPE_RECEIVED;
        }
    }

    public void setReceiverPicRef(StorageReference receiverPicRef) {
        this.receiverPicRef = receiverPicRef;
    }

    public StorageReference getReceiverPicRef() {
        return receiverPicRef;
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{

        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            binding.textMessage.setOnClickListener(view -> {
                Log.d("HERE", "setData: gets here");
                TextView dateTime = binding.textDateTime;
                dateTime.setVisibility(dateTime.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
//                if(dateTime.getVisibility() == View.GONE){
//                    dateTime.setVisibility(View.VISIBLE);
//                }
//                else{
//                    dateTime.setVisibility(View.GONE);
//                }
            });
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        private final ItemContainterReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainterReceivedMessageBinding itemContainterReceivedMessageBinding){
            super(itemContainterReceivedMessageBinding.getRoot());
            binding = itemContainterReceivedMessageBinding;
        }
        //receiver profile image not included
        void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            String otherUserId = chatMessage.getSenderId();
            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(otherUserId)){
                otherUserId = chatMessage.getReceiverId();
            }
            binding.textMessage.setOnClickListener(view -> {
                Log.d("HERE", "setData: gets here");
                TextView dateTime = binding.textDateTime;
                dateTime.setVisibility(dateTime.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            });
        }
    }
}
