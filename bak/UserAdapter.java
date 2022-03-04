package com.example.flashgig;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context ctx;
    private List<User> userList;


    public UserAdapter(Context ctx, List<User> userList) {
        this.ctx = ctx;
        this.userList = userList;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.list_item, null);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.textName.setText(user.getName());
        holder.textEmail.setText(user.getEmail());
        holder.textPhone.setText(user.getPhone());
//        holder.textViewRating.setText("4.7");
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textName;
        TextView textEmail;
//        TextView textViewRating;
        TextView textPhone;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgUser);
            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
//            textViewRating = itemView.findViewById(R.id.textRating);
            textPhone = itemView.findViewById(R.id.textPhone);
        }
    }
}
