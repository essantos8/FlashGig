package com.example.flashgig.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashgig.databinding.ActivityReviewsBinding;

public class ReviewsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityReviewsBinding binding = ActivityReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}
