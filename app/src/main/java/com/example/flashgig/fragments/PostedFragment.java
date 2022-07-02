package com.example.flashgig.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.flashgig.R;
import com.example.flashgig.adapters.PARecyclerViewAdapter;
import com.example.flashgig.databinding.FragmentPostedBinding;
import com.example.flashgig.models.Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class PostedFragment extends Fragment implements PARecyclerViewAdapter.ItemClickListener {
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
        db.collection("jobs").whereEqualTo("client",curUser).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d("PostedFragment", error.toString());
            }
            else {
                for (DocumentChange dc : value.getDocumentChanges()) {
                    Job job = dc.getDocument().toObject(Job.class);
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        jobList.add(job);
                    } else if (dc.getType() == DocumentChange.Type.REMOVED) {
                        jobList.remove(job);
                    } else {
                        jobList.remove(job);
                        jobList.add(job);
                    }
                    adapter.notifyDataSetChanged();
                }
                AppliedFragment.sortJobsByTimestamp(jobList);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPostedBinding binding = FragmentPostedBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.recyclerViewPosted;
        recyclerView.setItemViewCacheSize(50);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        adapter = new PARecyclerViewAdapter(this.getContext(), jobList, this);
        recyclerView.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void onItemClick(String JID, String status) {
        Log.d("JOBID", "onItemClick: "+JID);
        Fragment fragment = null;
        if (status.equals("pending")){
            fragment = new PostedPendingFragment(JID, status);
        }
        else if(status.equals("in progress")){
            fragment = new PostedInProgressFragment(JID);
        }
        else if(status.equals("completed")){
            fragment = new PostedCompletedFragment(JID);
        }
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, "jobDetail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}