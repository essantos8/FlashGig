package com.example.flashgig.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashgig.R;
import com.example.flashgig.databinding.FragmentDetailBinding;
import com.example.flashgig.databinding.FragmentHomeBinding;
import com.example.flashgig.models.Job;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class DetailFragment extends Fragment{

    private String jobId;
    private Job job;

    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            jobId = getArguments().getString("jobId");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        db.collection("jobs").document(jobId).get().addOnCompleteListener(task -> {
            job = task.getResult().toObject(Job.class);
            Toast.makeText(getContext(), "onFire", Toast.LENGTH_SHORT).show();
            setView(view);
        });

        //Designated back button for DetailFragment items
        ImageButton back = (ImageButton) view.findViewById(R.id.backButton);

        back.setOnClickListener(v -> {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStackImmediate();
        });

        return view;
    }

    /*
        setView

        Since database access is async, we need to wait for response.
        This method is called when the response arrives and this is where
        we set the new view values.
     */
    private void setView(View view){
        TextView JobTitle = view.findViewById(R.id.JobTitle);
        JobTitle.setText(job.getTitle());
        view.findViewById(R.id.jobDetailParent).setVisibility(View.VISIBLE);
    }

}