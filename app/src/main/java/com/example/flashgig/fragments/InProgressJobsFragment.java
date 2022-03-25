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
import com.example.flashgig.activities.JobRecyclerViewAdapter;

import com.example.flashgig.databinding.FragmentInProgressJobsBinding;
import com.example.flashgig.models.Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class InProgressJobsFragment extends Fragment implements JobRecyclerViewAdapter.ItemClickListener {
    private FirebaseFirestore db;
    private ArrayList<Job> jobList = new ArrayList<>();
    private JobRecyclerViewAdapter adapter;
    String curUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        curUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        eventChangeListener();
    }


    private void eventChangeListener() {
        db.collection("jobs").whereEqualTo("client",curUser).whereEqualTo("status","inprogress").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((value,error) -> {
            if (error != null) {
                Log.d("error", "Firebase error");
            }else{
                for (DocumentSnapshot dc : value.getDocuments()) {
                    jobList.add(dc.toObject(Job.class));
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentInProgressJobsBinding binding = FragmentInProgressJobsBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.recyclerViewInProgressJobs;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        adapter = new JobRecyclerViewAdapter(this.getContext(), jobList, this);
        recyclerView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onItemClick(String JID) {
        Fragment fragment = DetailFragment.newInstance(JID);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, "jobDetail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}