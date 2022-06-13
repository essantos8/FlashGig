package com.example.flashgig.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;


import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.R;
import com.example.flashgig.databinding.ActivityInboxChatBinding;
import com.example.flashgig.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class InboxChat extends AppCompatActivity {
    private ActivityInboxChatBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseUser currentUser;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        currentUser = mAuth.getCurrentUser();
        
        
        binding = ActivityInboxChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadUserDetails();
    }

    private void loadUserDetails() {
        StorageReference userRef = storageRef.child("media/images/profile_pictures/" + currentUser.getUid());
        GlideApp.with(this)
                .load(userRef)
                .fitCenter()
                .into(binding.imageProfile);

    }

    private void setListeners(){
        binding.fabNewChat.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
//        super.onBackPressed();
    }
}