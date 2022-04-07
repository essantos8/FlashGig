package com.example.flashgig.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashgig.R;
import com.example.flashgig.databinding.ActivitySignupBinding;
import com.example.flashgig.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    //private EditText editTextFullName, editTextEmail, editTextPassword, editTextPhone;
    private TextInputLayout tilnamesignup, tilemailsignup, tilpasswordsignup, tilnumbersignup;
    private TextInputEditText tietnamesignup, tietemailsignup, tietpasswordsignup, tietnumbersignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySignupBinding binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configure Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions
//                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(clientId)
//                .requestEmail()
//                .build();

        // Initialize Firebase Auth and database
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tilnamesignup = binding.tilnamesignup;
        tilemailsignup = binding.tilemailsignup;
        tilpasswordsignup = binding.tilpasswordsignup;
        tilnumbersignup = binding.tilnumbersignup;

        tietnamesignup = binding.tietnamesignup;
        tietemailsignup = binding.tietemailsignup;
        tietpasswordsignup = binding.tietpasswordsignup;
        tietnumbersignup = binding.tietnumbersignup;

        binding.btnSignUp.setOnClickListener(view -> {
            registerUser();
        });
    }

    private void registerUser() {
        String fullName = tietnamesignup.getText().toString(),
                email = tietemailsignup.getText().toString(),
                password = tietpasswordsignup.getText().toString(),
                phone = tietnumbersignup.getText().toString();

        if (fullName.isEmpty()) {
            tilnamesignup.setError("Name is required!");
            return;
        }
        if (email.isEmpty()) {
            tilemailsignup.setError("Email is required!");
            return;
        }
        if (password.isEmpty()) {
            tilpasswordsignup.setError("Password is required!");
            return;
        }
        if (phone.isEmpty()) {
            tilnumbersignup.setError("Phone number is required!");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUpActivity.this, "User registered successfully!", Toast.LENGTH_LONG).show();
                FirebaseUser user = mAuth.getCurrentUser();
                // Update Database with new user info
                User data = new User(fullName, email, phone, user.getUid());
                db.collection("users").document(user.getUid()).set(data);
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Toast.makeText(SignUpActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "User already signed in!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}