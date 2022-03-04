package com.example.flashgig.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashgig.activities.JobAdderActivity;
import com.example.flashgig.activities.SplashActivity;
import com.example.flashgig.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private TextView textName, textEmail, textPhone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private void retrieveInfo(){

        db.collection("users").whereEqualTo("email", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QueryDocumentSnapshot user;
                if(!task.getResult().iterator().hasNext()){
                    Toast.makeText(getContext(), "User not found in database!", Toast.LENGTH_SHORT).show();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentProfileBinding binding = FragmentProfileBinding.inflate(inflater, container, false);

        textName = binding.textName;
        textEmail = binding.textEmail;
        textPhone = binding.textPhone;

        retrieveInfo();


        binding.btnProfileUpdate.setOnClickListener(view -> {
            retrieveInfo();
        });

        binding.btnProfileAddJob.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), JobAdderActivity.class));
        });

        binding.btnLogout.setOnClickListener(view ->{
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this.getContext(), "User logged out!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            startActivity(new Intent(getContext(), SplashActivity.class));
        });
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}