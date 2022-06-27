package com.example.flashgig.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.flashgig.R;

import com.example.flashgig.adapters.PARecyclerViewAdapter;
import com.example.flashgig.databinding.FragmentAppliedBinding;
import com.example.flashgig.models.Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;


public class AppliedFragment extends Fragment implements PARecyclerViewAdapter.ItemClickListener {
    private FirebaseFirestore db;
    private ArrayList<Job> jobList = new ArrayList<>();
    private PARecyclerViewAdapter adapter;
    String curUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        curUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        eventChangeListener();
    }

    private void eventChangeListener() {
        db.collection("jobs").whereArrayContains("bidders",curUser).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((value,error) -> {
            if (error != null) {
                Log.d("error", "Firebase error");
            }else{
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        jobList.add(dc.getDocument().toObject(Job.class));
                    }
                    else if(dc.getType() == DocumentChange.Type.REMOVED){
                        jobList.remove(dc.getDocument().toObject(Job.class));
                    }
                    else{
                        jobList.add(dc.getDocument().toObject(Job.class));
                        jobList.remove(dc.getDocument().toObject(Job.class));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        db.collection("jobs").whereArrayContains("workers",curUser).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((value,error) -> {
            if (error != null) {
                Log.d("error", "Firebase error");
            }else{
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        jobList.add(dc.getDocument().toObject(Job.class));
                    }
                    else if(dc.getType() == DocumentChange.Type.REMOVED){
                        jobList.remove(dc.getDocument().toObject(Job.class));
                    }
                    else{
                        jobList.add(dc.getDocument().toObject(Job.class));
                        jobList.remove(dc.getDocument().toObject(Job.class));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentAppliedBinding binding = FragmentAppliedBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.recyclerViewApplied;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        adapter = new PARecyclerViewAdapter(this.getContext(), jobList, this);
        recyclerView.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void onItemClick(String JID, String status) {
        Fragment fragment = null;
        if (status.equals("pending")){
            fragment = AppliedPendingFragment.newInstance(JID, status);
        }
        else if(status.equals("in progress")){
            fragment = AppliedInProgressFragment.newInstance(JID, status);
        }
        else if(status.equals("completed")){
            fragment = AppliedCompletedFragment.newInstance(JID, status);
        }
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, "jobDetail");
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }
}