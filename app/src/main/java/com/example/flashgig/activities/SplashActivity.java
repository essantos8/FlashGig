package com.example.flashgig.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Toast;

import com.example.flashgig.R;
import com.example.flashgig.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());

        // Grabbing objects/views from layout
        setContentView(R.layout.splash_intro);
//        setContentView(binding.getRoot());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentView(binding.getRoot());
            }
        }, 2000);
        mAuth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(view ->
                startActivity(new Intent(this, LoginActivity.class)));
        binding.btnSignup.setOnClickListener(view ->
                startActivity(new Intent(this, SignUpActivity.class)));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(this, "User already signed in!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}