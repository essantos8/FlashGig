package com.example.flashgig.activities;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.flashgig.models.User;
import com.example.flashgig.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    private EditText editTextFullName, editTextEmail, editTextPassword, editTextPhone;

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



        editTextFullName = binding.editTextTextPersonName;
        editTextEmail = binding.editTextTextEmailAddress;
        editTextPassword = binding.editTextTextPassword;
        editTextPhone = binding.editTextPhone;

        binding.btnSignUp.setOnClickListener(view -> {
            registerUser();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(this, "User already signed in!", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser() {
        String fullName = editTextFullName.getText().toString(),
                email = editTextEmail.getText().toString(),
                password = editTextPassword.getText().toString(),
                phone = editTextPhone.getText().toString();

        if(fullName.isEmpty()){
            editTextFullName.setError("Name is required!");
            editTextFullName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if(phone.isEmpty()){
            editTextPhone.setError("Phone number is required!");
            editTextPhone.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, task -> {
            if(task.isSuccessful()){
                Toast.makeText(SignUpActivity.this, "User registered successfully!", Toast.LENGTH_LONG).show();
                FirebaseUser user = mAuth.getCurrentUser();
                // Update Database with new user info
                User data = new User(fullName, email, phone, user.getUid());
                db.collection("users").document(user.getUid()).set(data);
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
            }
            else{
                Toast.makeText(SignUpActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}