package com.example.flashgig.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.databinding.ItemContainerUserBinding;
import com.example.flashgig.listeners.UserListener;
import com.example.flashgig.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.UserViewHolder> implements Filterable {

    private final ArrayList<User> users;
    private ArrayList<User> fullUserArrayList;
    private final UserListener userListener;
    private Context ctx;
    private StorageReference storageRef;

    public UserRecyclerViewAdapter(Context ctx, ArrayList<User>users, UserListener userListener){
        this.ctx = ctx;
        this.users = users;
        this.fullUserArrayList = users;
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

    private final Filter usersTextFilter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            fullUserArrayList = new ArrayList<>(fullUserArrayList);
            ArrayList<User> filteredUserArrayList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0) {
                filteredUserArrayList.addAll(fullUserArrayList);
            }
            else{
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(User user : fullUserArrayList){
                    if(user.getFullName().toLowerCase().contains(filterPattern) || TextUtils.join(" ",user.getSkills()).toLowerCase().contains(filterPattern)){
                        filteredUserArrayList.add(user);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredUserArrayList;
            results.count = filteredUserArrayList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            users.clear();
            users.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return usersTextFilter;
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
