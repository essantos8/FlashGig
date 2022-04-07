package com.example.flashgig.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashgig.R;
import com.example.flashgig.databinding.ActivityLoginBinding;
import com.example.flashgig.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText editTextEmail, editTextPassword;
    private TextInputLayout tilemail, tilpassword;
    private TextInputEditText tietemail, tietpassword;
    private GoogleSignInClient mGoogleSignInClient;

    private final Integer googleRC = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        tilemail = binding.tilemail;
        tietemail = binding.tietemail;
        tilpassword = binding.tilpassword;
        tietpassword = binding.tietpassword;


        binding.btnLoginLogin.setOnClickListener(view -> {
            loginUser();
        });

        binding.btnLoginGoogle.setOnClickListener(view -> {
            loginGoogle();
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "User already signed in!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == googleRC) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Exception e = task.getException();
            if (task.isSuccessful()) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    mAuth = FirebaseAuth.getInstance();


                    firebaseAuthWithGoogle(account.getIdToken(), account);
                } catch (ApiException ex) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("Google Sign In", "Google sign in failed", ex);
                }
            } else {
                Log.w("Google Sign In", e.toString());
            }

        }
    }

    private void loginGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, googleRC);
    }

    private void loginUser() {
        String email = tietemail.getText().toString(),
                password = tietpassword.getText().toString();

        if (email.isEmpty()) {
            tilemail.setError("Email is required!");
            //editTextEmail.requestFocus();
            return;
        } else {
            tilemail.setError(null);
        }
        if (password.isEmpty()) {
            tilpassword.setError("Password is required!");
            //editTextPassword.requestFocus();
            return;
        } else {
            tilemail.setError(null);
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(this, MainActivity.class));
            } else {
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void firebaseAuthWithGoogle(String idToken, GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Google Sign In", "signInWithCredential:success");
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        // Check if account is in database
                        db.collection("users").whereEqualTo("email", account.getEmail()).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                if (task1.getResult().size() > 0) {
                                    Log.d("Google Sign In", "Existing account");
                                } else {
                                    Log.d("Google Sign In", "New account, adding to Database");
                                    User userData = new User(account.getDisplayName(), account.getEmail(), "", mAuth.getCurrentUser().getUid());

                                    db.collection("users").document(mAuth.getCurrentUser().getUid()).set(userData);
                                }
                            } else {
                                Toast.makeText(this, "Error accessing database!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        startActivity(new Intent(this, MainActivity.class));
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Google Sign In", "signInWithCredential:failure", task.getException());
                    }
                });
    }


}