package com.example.flashgig.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flashgig.databinding.ActivityUserVerificationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;

import java.util.concurrent.TimeUnit;

public class UserVerificationActivity extends AppCompatActivity {

    private ActivityUserVerificationBinding binding;

    private FirebaseAuth mAuth;

    private PhoneAuthCredential credential;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+639288974143")       // Phone number to verify
                        .setTimeout(5L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(UserVerificationActivity.this)                 // Activity (for callback binding)
                        .setCallbacks(setCallbacks())          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        binding.btnSubmit.setOnClickListener(view -> {
            String code = binding.tietCode.getText().toString();
            Toast.makeText(this, code, Toast.LENGTH_SHORT).show();
            if (credential == null) {
                credential = PhoneAuthProvider.getCredential(verificationId, code);
            }
            signInWithPhoneAuthCredential(credential);
        });


    }

    public OnVerificationStateChangedCallbacks setCallbacks() {
        return new OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId1,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token1) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                Toast.makeText(UserVerificationActivity.this, "Code sent!", Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                verificationId = verificationId1;
                token = token1;
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Toast.makeText(UserVerificationActivity.this, "User verification success!", Toast.LENGTH_SHORT).show();

                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(UserVerificationActivity.this, "The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                           // The verification code entered was invalid

                        }
                    }
                });
    }

}