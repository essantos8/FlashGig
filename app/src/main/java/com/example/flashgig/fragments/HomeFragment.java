package com.example.flashgig.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.flashgig.R;
import com.example.flashgig.activities.JobRecyclerViewAdapter;
import com.example.flashgig.databinding.FragmentHomeBinding;
import com.example.flashgig.databinding.FragmentProfileBinding;
import com.example.flashgig.models.Job;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private FirebaseFirestore db;
    private ArrayList<Job> jobList = new ArrayList<>();
    private JobRecyclerViewAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
    }

    private void eventChangeListener(){
        db.collection("jobs").addSnapshotListener((value, error) -> {
            if(error != null){
                Log.d("error", "Firebase error");
            }
            for(DocumentChange dc : value.getDocumentChanges()){
                if(dc.getType() == DocumentChange.Type.ADDED){
                    jobList.add(dc.getDocument().toObject(Job.class));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.recyclerViewJobs;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        adapter = new JobRecyclerViewAdapter(this.getContext(), jobList);
        recyclerView.setAdapter(adapter);


        eventChangeListener();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}