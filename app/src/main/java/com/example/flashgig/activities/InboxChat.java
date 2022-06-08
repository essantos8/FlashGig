package com.example.flashgig.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;


import com.example.flashgig.R;
import com.example.flashgig.databinding.ActivityInboxChatBinding;
import com.example.flashgig.databinding.ActivityMainBinding;

public class InboxChat extends AppCompatActivity {
    private ActivityInboxChatBinding binding;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInboxChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.fabNewChat.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
    }
}