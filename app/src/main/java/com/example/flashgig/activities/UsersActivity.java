package com.example.flashgig.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.flashgig.adapters.UsersAdapter;
import com.example.flashgig.databinding.ActivityUsersListBinding;
import com.example.flashgig.listeners.UserListener;
import com.example.flashgig.models.User;
import com.example.flashgig.utilities.Constants;
import com.example.flashgig.utilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener{
    private ActivityUsersListBinding binding;
    private FirebaseUser curUser;
    private UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersListBinding.inflate(getLayoutInflater());
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        setContentView(binding.getRoot());
        curUser = FirebaseAuth.getInstance().getCurrentUser();
        getUsers();
        binding.userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                usersAdapter.getFilter().filter(s);
                return false;
            }
        });
    }

    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task ->{
                    loading(false);
                    String currentUserId = curUser.getUid();//preferenceManager.getString();
                    if(task.isSuccessful() && task.getResult() != null){
                        ArrayList<User> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            User user = new User();
                            user.fullName = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.userId = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if(users.size() > 0) {
                            usersAdapter = new UsersAdapter(this, users, this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }

                });

    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s"), TextView.BufferType.valueOf("No User available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        } else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}