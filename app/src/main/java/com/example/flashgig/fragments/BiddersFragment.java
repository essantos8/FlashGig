package com.example.flashgig.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.flashgig.R;
import com.example.flashgig.adapters.BidderRecyclerViewAdapter;
import com.example.flashgig.adapters.JobRecyclerViewAdapter;
import com.example.flashgig.adapters.PARecyclerViewAdapter;
import com.example.flashgig.databinding.FragmentBiddersBinding;
import com.example.flashgig.models.Job;
import com.example.flashgig.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class BiddersFragment extends Fragment implements BidderRecyclerViewAdapter.ItemClickListener {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private FirebaseFirestore db;
    private ArrayList<String> bidderListString = new ArrayList<>();    //put names of bidders first here
    private ArrayList<User> bidderList = new ArrayList<>();    //after converting into User, put in this list

    DocumentSnapshot document;
    Task<QuerySnapshot> curJob;
    FragmentBiddersBinding binding;

    private BidderRecyclerViewAdapter adapter;
    Job job;    //container of the job being referred to in curJob
    User bidUser;    //container of the bidder as user
    public BiddersFragment() {
        // Required empty public constructor
    }

    public static BiddersFragment newInstance(String param1) {
        BiddersFragment fragment = new BiddersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        //bidUser = db.collection("users").whereEqualTo("userId", mParam1).get();
        curJob = db.collection("jobs").whereEqualTo("jobId", mParam1).get();

    }

    private void eventChangeListener() {
        for (int i = 0; i < bidderListString.size(); i++) {
            int finalI = i;
            db.collection("users").whereEqualTo("email", bidderListString.get(i)).get().addOnCompleteListener(task1 -> {
                if (task1.getResult().getDocuments().isEmpty()) {
                    Log.d("BiddersFragment", "onComplete: User is NOT FOUND");
                    Toast.makeText(getContext(), "Bidder user NOT FOUND.",Toast.LENGTH_SHORT).show();
                    return;
                }
                bidUser = task1.getResult().getDocuments().get(0).toObject(User.class);
                bidderList.add(bidUser);
                Log.d("bidderListStringSIZE", "bidderListString size: " + bidderListString.get(finalI));
                Log.d("bidderListSIZE", "bidderList size: " + bidderList.size());
                //Toast.makeText(getActivity(), "bidderListString size: " + bidderListString.get(i),Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "bidderList size: " + bidderList.size(),Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            });
        }
        RecyclerView recyclerView = binding.bidderRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        adapter = new BidderRecyclerViewAdapter(this.getContext(), bidderList, this, mParam1);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bidderList.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBiddersBinding.inflate(inflater, container, false);
        curJob.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    document = task.getResult().getDocuments().get(0);
                    job = document.toObject(Job.class);
                    bidderListString = job.getBidders();    //get the list of bidders' emails and put them here
                    eventChangeListener();
                }
            }
        });
        // Inflate the layout for this fragment
        //if (bidderList.size() == 0) {
        //    Toast.makeText(getActivity(),"No bidders yet!",Toast.LENGTH_SHORT).show();
        //} else
        FragmentManager fm = getActivity().getSupportFragmentManager();

        binding.backButtonBiddersList.setOnClickListener(view ->{
            fm.popBackStackImmediate();
        });

        return binding.getRoot();
    }

    @Override
    public void onItemClick(String BID, String JID) {
        Fragment fragment = DisplayBidder.newInstance(BID, JID);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, "displayBidder");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}