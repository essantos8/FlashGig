package com.example.flashgig.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.databinding.ItemContainerSentMessageBinding;
import com.example.flashgig.databinding.ItemContainterReceivedMessageBinding;
import com.example.flashgig.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<ChatMessage> chatMessages;
    private final String senderId;

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private Context ctx;

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
                if(dateTime.getVisibility() == View.GONE){
                    dateTime.setVisibility(View.VISIBLE);
                }
                else{
                    dateTime.setVisibility(View.GONE);
                }
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
            //binding.imageProfile.setImageBitmap(receiverProfileImage);
            String otherUserId = chatMessage.getSenderId();
            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(otherUserId)){
                otherUserId = chatMessage.getReceiverId();
            }
            binding.textMessage.setOnClickListener(view -> {
                Log.d("HERE", "setData: gets here");
                TextView dateTime = binding.textDateTime;
                if(dateTime.getVisibility() == View.GONE){
                    dateTime.setVisibility(View.VISIBLE);
                }
                else{
                    dateTime.setVisibility(View.GONE);
                }
            });
            StorageReference profilePicRef = storageRef.child("media/images/profile_pictures/" + otherUserId);
            profilePicRef.getMetadata().addOnSuccessListener(storageMetadata -> {
                GlideApp.with(binding.getRoot().getContext())
                        .load(profilePicRef)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                        .into(binding.imageProfile);
            }).addOnFailureListener(e -> {
                Log.d("CommentsRecyclerView", "onBindViewHolder: "+e.toString());
//            Snackbar.make(binding.getRoot(), "File does not exist!", Snackbar.LENGTH_SHORT).show();
            });
        }
    }
}
