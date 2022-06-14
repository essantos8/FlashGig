package com.example.flashgig.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.databinding.ItemContainerUserBinding;
import com.example.flashgig.listeners.UserListener;
import com.example.flashgig.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.common.net.InternetDomainName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private final List<User> users;
    private final UserListener userListener;
    private Context ctx;
    private StorageReference storageRef;

    public UsersAdapter(Context ctx, List<User>users, UserListener userListener){
        this.ctx = ctx;
        this.users = users;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        storageRef = FirebaseStorage.getInstance().getReference();
        holder.setUserInfo(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        ItemContainerUserBinding binding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserInfo(User user){
            binding.textName.setText(user.fullName);
            binding.textEmail.setText(user.email);
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
            StorageReference userRef = storageRef.child("media/images/profile_pictures/" + user.getUserId());
            userRef.getMetadata().addOnSuccessListener(storageMetadata -> {
                    GlideApp.with(ctx)
                            .load(userRef)
                            .signature(new ObjectKey(String.valueOf(storageMetadata.getCreationTimeMillis())))
                            .fitCenter()
                            .into(binding.imageProfile);
            }).addOnFailureListener(e -> {
                Log.d("Profile", "retrieveInfo: "+e.toString());
            });
        }

    }




}
