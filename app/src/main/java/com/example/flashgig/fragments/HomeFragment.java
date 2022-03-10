package com.example.flashgig.fragments;

import android.inputmethodservice.AbstractInputMethodService;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.flashgig.R;
import com.example.flashgig.activities.JobRecyclerViewAdapter;
import com.example.flashgig.databinding.FragmentHomeBinding;
import com.example.flashgig.databinding.FragmentProfileBinding;
import com.example.flashgig.models.Job;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Set;


public class HomeFragment extends Fragment implements JobRecyclerViewAdapter.ItemClickListener{

    private FirebaseFirestore db;
    private ArrayList<Job> jobList = new ArrayList<>();
    private JobRecyclerViewAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        eventChangeListener();
    }


    private void eventChangeListener(){
        db.collection("jobs").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
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

        adapter = new JobRecyclerViewAdapter(this.getContext(), jobList, this);
        recyclerView.setAdapter(adapter);

        binding.searchviewHome.setOnSearchClickListener(view -> binding.cardView.setVisibility(View.VISIBLE));
        binding.searchviewHome.setOnClickListener(view -> binding.searchviewHome.setIconified(false));
        binding.searchviewHome.setOnCloseListener(() -> {
            binding.cardView.setVisibility(View.GONE);
            return false;
        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onItemClick(Job job) {
        Fragment fragment = DetailFragment.newInstance(job.getTitle());
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, "jobDetail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}