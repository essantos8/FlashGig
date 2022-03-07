package com.example.flashgig.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.flashgig.databinding.ActivityLoginBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mifmif.common.regex.Main;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText editTextEmail, editTextPassword;
    private TextInputLayout tilemail, tilpassword;
    private TextInputEditText tietemail, tietpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        tilemail = binding.tilemail;
        tietemail = binding.tietemail;
        tilpassword = binding.tilpassword;
        tietpassword = binding.tietpassword;


        binding.btnLoginLogin.setOnClickListener(view -> {
            loginUser();
        });

    }

    private void loginUser(){
        String email = tietemail.getText().toString(),
                password = tietpassword.getText().toString();

        if(email.isEmpty()){
            tilemail.setError("Email is required!");
            //editTextEmail.requestFocus();
            return;
        }
        else{
            tilemail.setError(null);
        }
        if(password.isEmpty()){
            tilpassword.setError("Password is required!");
            //editTextPassword.requestFocus();
            return;
        }
        else{
            tilemail.setError(null);
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(this, MainActivity.class));
            }
            else{
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(this, "User already signed in!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}