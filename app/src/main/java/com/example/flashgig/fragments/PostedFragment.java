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
import com.google.firebase.firestore.ListenerRegistration;
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
                Log.d("error", error.toString());
            }
            else {
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        jobList.add(dc.getDocument().toObject(Job.class));
//                    Log.d("jobss", "eventChangeListener: added");
                    } else if (dc.getType() == DocumentChange.Type.REMOVED) {
                        jobList.remove(dc.getDocument().toObject(Job.class));
//                    Log.d("jobss", "eventChangeListener: removed");
                    } else {
                        Integer index = -1;
                        index = jobList.indexOf(dc.getDocument().toObject(Job.class));
                        assert !index.equals(-1);
                        Log.d("INDEX", "eventChangeListener: " + String.valueOf(index));
                        jobList.remove(dc.getDocument().toObject(Job.class));
                        jobList.add(index, dc.getDocument().toObject(Job.class));
//                    Log.d("jobss", "eventChangeListener: modified");
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentPostedBinding binding = FragmentPostedBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.recyclerViewPosted;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        adapter = new PARecyclerViewAdapter(this.getContext(), jobList, this);
        recyclerView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onItemClick(String JID, String status) {
        Log.d("JOBID", "onItemClick: "+JID);
        Fragment fragment = null;
        if (status.equals("pending")){
            fragment = PendingFragmentClient.newInstance(JID, status);
            //fragment = DetailFragment.newInstance(JID);
        }
        // disabled for now
        else if(status.equals("in progress")){
            fragment = DetailFragment.newInstance(JID);
//            fragment = new JobInProgressFragment(JID);
        }
        else if(status.equals("completed")){
            fragment = new ClientCompletedFragment(JID);
//            fragment = DetailFragment.newInstance(JID);
        }
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, "jobDetail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}