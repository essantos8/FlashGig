package com.example.flashgig.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.databinding.ItemContainerRecentConversationBinding;
import com.example.flashgig.listeners.ConversationListener;
import com.example.flashgig.models.ChatMessage;
import com.example.flashgig.models.User;
import com.google.common.net.InternetDomainName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversationViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final ConversationListener conversationListener;
    private Context ctx;
    private StorageReference storageRef;
    private FirebaseAuth firebaseAuth;

    public RecentConversationsAdapter(Context ctx, List<ChatMessage> chatMessages, ConversationListener conversationListener) {
        this.ctx = ctx;
        this.chatMessages = chatMessages;
        this.conversationListener = conversationListener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(
                ItemContainerRecentConversationBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        storageRef = FirebaseStorage.getInstance().getReference();
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder{

        ItemContainerRecentConversationBinding binding;

        ConversationViewHolder(ItemContainerRecentConversationBinding itemContainerRecentConversationBinding){
            super(itemContainerRecentConversationBinding.getRoot());
            binding = itemContainerRecentConversationBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.textName.setText(chatMessage.conversationName);
            binding.textRecentMessage.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(v -> {
                User user = new User();
                user.userId = chatMessage.conversationId;
                user.fullName = chatMessage.conversationName;
                conversationListener.onConversationClicked(user);
            });
            String otherUserId = chatMessage.getSenderId();
            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(otherUserId)){
                otherUserId = chatMessage.getReceiverId();
            }
            StorageReference profilePicRef = storageRef.child("media/images/profile_pictures/" + otherUserId);
            profilePicRef.getMetadata().addOnSuccessListener(storageMetadata -> {
                GlideApp.with(ctx.getApplicationContext())
                        .load(profilePicRef)
                        .signature(new ObjectKey(String.valueOf(storageMetadata.getCreationTimeMillis())))
                        .fitCenter()
                        .into(binding.imageProfile);
            }).addOnFailureListener(e -> {
                Log.d("RecentConversationsAd", "setData: "+e.toString());
            });
        }
    }
}
