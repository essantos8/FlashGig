package com.example.flashgig.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashgig.R;
import com.example.flashgig.databinding.ActivitySignupBinding;
import com.example.flashgig.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mifmif.common.regex.Main;


public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    //private EditText editTextFullName, editTextEmail, editTextPassword, editTextPhone;
    private TextInputLayout tilnamesignup, tilemailsignup, tilpasswordsignup, tilnumbersignup, tilpassword2signup;
    private TextInputEditText tietnamesignup, tietemailsignup, tietpasswordsignup, tietnumbersignup, tietpassword2signup;

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
        tilpassword2signup = binding.tilpasswordagainsignup;

        tietnamesignup = binding.tietnamesignup;
        tietemailsignup = binding.tietemailsignup;
        tietpasswordsignup = binding.tietpasswordsignup;
        tietnumbersignup = binding.tietnumbersignup;
        tietpassword2signup = binding.tietpasswordagainsignup;

        binding.btnSignUp.setOnClickListener(view -> {
            registerUser();
        });
    }

    private void registerUser() {
        String fullName = tietnamesignup.getText().toString().trim(),
                email = tietemailsignup.getText().toString().trim(),
                password = tietpasswordsignup.getText().toString(),
                password2 = tietpassword2signup.getText().toString(),
                phone = tietnumbersignup.getText().toString();

        Boolean reset = false;

        if (fullName.isEmpty()) {
            tilnamesignup.setError("Name is required!");
            reset = true;
        }
        else if(fullName.split(" ").length < 2){
            tilnamesignup.setError("Please enter your first and last name!");
            reset = true;
        }
        else{
            tilnamesignup.setErrorEnabled(false);
        }

        if (email.isEmpty()) {
            tilemailsignup.setError("Email is required!");
            reset = true;
        }
        else if(!email.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$")){
            tilemailsignup.setError("Invalid email!");
            reset = true;
        }
        else{
            tilemailsignup.setErrorEnabled(false);
        }

        if (password.isEmpty()) {
            tilpasswordsignup.setError("Password is required!");
            reset = true;
        }
        else if(password.length() < 8){
            tilpasswordsignup.setError("Password must be at least 8 characters!");
            reset = true;
        }
        else{
            tilpasswordsignup.setErrorEnabled(false);
            if(!password2.equals(password)){
                tilpassword2signup.setError("Passwords do not match!");
                reset = true;
            }
            else{
                tilpassword2signup.setErrorEnabled(false);
            }
        }

        if (phone.isEmpty()) {
            tilnumbersignup.setError("Phone number is required!");
            reset = true;
        }
        else if(phone.length() != 10 || phone.charAt(0) != '9'){
            tilnumbersignup.setError("Invalid phone number!");
            reset = true;
        }
        else{
            tilnumbersignup.setErrorEnabled(false);
        }

        if(reset){
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUpActivity.this, "User registered successfully!", Toast.LENGTH_LONG).show();
                FirebaseUser user = mAuth.getCurrentUser();
                // Update Database with new user info
                User data = new User(fullName, email, "+63"+phone, user.getUid());
                db.collection("users").document(user.getUid()).set(data);
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build();
                user.updateProfile(profileUpdates);
//                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, MainActivity.class));
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