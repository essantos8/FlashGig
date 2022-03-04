package com.example.flashgig.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashgig.databinding.ActivityProfileBinding;
import com.example.flashgig.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private TextView textName, textEmail, textPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityProfileBinding binding= ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        textName = binding.textName;
        textEmail = binding.textEmail;
        textPhone = binding.textPhone;

        retrieveInfo();

        binding.btnProfileUpdate.setOnClickListener(view -> {
            retrieveInfo();
        });

        binding.btnProfileAddJob.setOnClickListener(view -> {
            startActivity(new Intent(this, JobAdderActivity.class));
        });

        binding.btnProfileViewJobs.setOnClickListener(view -> {
            startActivity(new Intent(this, JobListActivity.class));
        });

        binding.btnLogout.setOnClickListener(view ->{
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "User logged out!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(this, MainActivity.class));
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void retrieveInfo(){
        db.collection("users").whereEqualTo("email", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QueryDocumentSnapshot user;
                if(!task.getResult().iterator().hasNext()){
                    Toast.makeText(this, "User not found in database!", Toast.LENGTH_SHORT).show();
                    Log.d("TAG","user not in database");
                    return;
                }
                user = task.getResult().iterator().next();
                textName.setText(user.getString("fullName"));
                textEmail.setText(user.getString("email"));
                textPhone.setText(user.getString("phone"));
//                Log.d("TAG",user.getId() + " => " + user.getData());
                // for multiple results
//                for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
//                    Log.d("TAG",documentSnapshot.getId() + " => " + documentSnapshot.getData());
//                }
            }
            else{
                Log.d("TAG","mission failed");
            }
        });
    }
}