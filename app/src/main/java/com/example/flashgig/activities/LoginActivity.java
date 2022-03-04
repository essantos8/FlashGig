package com.example.flashgig.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.flashgig.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText editTextEmail, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = binding.editTextTextLoginEmail;
        editTextPassword = binding.editTextTextLoginPassword;

        binding.btnLoginLogin.setOnClickListener(view -> {
            loginUser();
        });

    }

    private void loginUser(){
        String email = editTextEmail.getText().toString(),
                password = editTextPassword.getText().toString();

        if(email.isEmpty()){
            editTextEmail.setError("Name is required!");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Name is required!");
            editTextPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ProfileActivity.class));
            }
            else{
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}