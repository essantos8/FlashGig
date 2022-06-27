package com.example.flashgig.fragments;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.R;
import com.example.flashgig.activities.ChatActivity;
import com.example.flashgig.adapters.BidderRecyclerViewAdapter;
import com.example.flashgig.adapters.CommentsRecyclerViewAdapter;
import com.example.flashgig.databinding.FragmentDetailBinding;
import com.example.flashgig.databinding.FragmentDisplayBidderBinding;
import com.example.flashgig.models.Comment;
import com.example.flashgig.models.Job;
import com.example.flashgig.models.User;
import com.example.flashgig.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DisplayBidder extends Fragment {
    private static final String ARG_PARAM1 = "param1";    //for userId (of bidder)
    private static final String ARG_PARAM2 = "param2";    //for jobId
    private String mParam1;    //for userId (of bidder)
    private String mParam2;    //for jobId

    private ArrayList<String> bidderListTemp = new ArrayList<>();    //putting all of the accepted bidders in 1 list temporarily
    private ArrayList<String> workerListTemp = new ArrayList<>();    //putting all of the workers in 1 list temporarily
    private FirebaseFirestore db;
    private Map<String, Integer> jobRating = new HashMap<>();
    private FragmentDisplayBidderBinding binding;
    private ArrayList<Comment> comments = new ArrayList<>();
    private CommentsRecyclerViewAdapter adapter;

    DocumentSnapshot document;
    DocumentReference bidderDocRef;
    DocumentReference jobDocRef;
    FragmentManager fm;

    Job job;
    User bidUserAcc;
    private TextView textBidderName, textBidderNumber, textBidderEmail;
    public DisplayBidder() {
    }

    public static DisplayBidder newInstance(String mParam1, String mParam2) {
        DisplayBidder fragment = new DisplayBidder();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, mParam1);
        args.putString(ARG_PARAM2, mParam2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        bidderDocRef = db.collection("users").document(mParam1);
        jobDocRef = db.collection("jobs").document(mParam2);
    }

    private void statusCheck(DocumentReference jobDocRef) {
        jobDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            Job job1;
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                job1 = documentSnapshot.toObject(Job.class);
                if (job1.getStatus().equals("pending")) {
                    if (job1.getWorkers().size() >= job1.getNumWorkers()) {
                        jobDocRef.update("status", "in progress").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("DisplayBidderAccept", "DocumentSnapshot successfully updated! (bidders)");
                                Toast.makeText(getActivity(), "Status changed!", Toast.LENGTH_SHORT).show();
                                job1.setBidders(new ArrayList<>());
                                jobDocRef.update("bidders", new ArrayList<>());
                                Log.d("InProgressTransition", "Cleared Bidder list");
                            }

                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("DisplayBidderAccept", "Error updating document. (status)", e);
                                    }
                                });
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDisplayBidderBinding.inflate(inflater, container, false);
        textBidderName = binding.textBidderName;
        textBidderNumber = binding.textBidderNumber;
        textBidderEmail = binding.textBidderEmail;

        //SETTING/SHOWING THE PROFILE OF BIDDER
        bidderDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                bidUserAcc = documentSnapshot.toObject(User.class);
                retrieveInfo(bidUserAcc);
            }
        });

        //For checking if bidder is already a worker or declined
        jobDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                job = documentSnapshot.toObject(Job.class);
            }
        });

        //BACK BUTTON
        binding.backButton.setOnClickListener(view ->{
            fm = getActivity().getSupportFragmentManager();
            fm.popBackStackImmediate();
        });

        binding.btnChatBidderProfile.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra(Constants.KEY_USER, bidUserAcc);
            getContext().startActivity(intent);
        });

        binding.btnAcceptBidder.setOnClickListener(view -> {
            if (job.getWorkers().contains(bidUserAcc.getEmail())) {    //if the job has bidder in its "workers" array
                binding.btnAcceptBidder.setBackgroundColor(808080);    //set the button color to grey
                Toast.makeText(getActivity(), "Bidder already accepted as worker!", Toast.LENGTH_SHORT).show();    //show that the bidder is already a worker
            } else {
                bidderListTemp = job.getBidders();
                workerListTemp = job.getWorkers();
                bidderListTemp.remove(bidUserAcc.getEmail());    //remove the bidder from the tempbidder list
                workerListTemp.add(bidUserAcc.getEmail());    //add the bidder to the tempworker list
                //UPDATING BIDDERS AND WORKERS
                jobDocRef.update("bidders", bidderListTemp, "workers", workerListTemp).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("DisplayBidderAccept", "DocumentSnapshot successfully updated! (bidders)");
                                Toast.makeText(getActivity(), "Bidder accepted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("DisplayBidderAccept", "Error updating document. (bidders)", e);
                            }
                        });
            }
            statusCheck(jobDocRef);
            Fragment fragment = new PostedFragment();
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fragment, "jobDetail");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        binding.btnDeclineBidder.setOnClickListener(view -> {
            if (!job.getBidders().contains(bidUserAcc.getEmail())) {    //if the job has bidder in its "workers" array
                binding.btnDeclineBidder.setBackgroundColor(808080);    //set the button color to grey
                Toast.makeText(getActivity(), "Bidder already declined.", Toast.LENGTH_SHORT).show();    //show that the bidder is already a worker
            } else {
                bidderListTemp = job.getBidders();    //get the bidder list

                bidderListTemp.remove(bidUserAcc.getEmail());    //remove the bidder from the tempbidder list

                //UPDATING BIDDERS AND WORKERS
                jobDocRef.update("bidders", bidderListTemp).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("DisplayBidderAccept", "DocumentSnapshot successfully updated! (bidders)");
                                Toast.makeText(getActivity(), "Bidder declined.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("DisplayBidderAccept", "Error updating document. (bidders)", e);
                            }
                        });
                Fragment fragment = new PostedFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment, "jobDetail");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return binding.getRoot();
    }

    private void retrieveInfo(User user){
        textBidderName.setText(user.getFullName());
        textBidderNumber.setText(user.getPhone());
        textBidderEmail.setText(user.getEmail());
        binding.rbBidder.setRating(user.getAverageRating());
        getComments(user);
        setSkills();
        StorageReference userRef = FirebaseStorage.getInstance().getReference().child("media/images/profile_pictures/" + user.getUserId());
        userRef.getMetadata().addOnSuccessListener(storageMetadata -> {
            GlideApp.with(this)
                    .load(userRef)
                    .signature(new ObjectKey(String.valueOf(storageMetadata.getCreationTimeMillis())))
                    .fitCenter()
                    .into(binding.imageBidder);
        }).addOnFailureListener(e -> {
            Log.d("Profile", "retrieveInfo: "+e.toString());
        });
    }
    private void getComments(User user) {
        db.collection("users").document(user.getUserId()).get().addOnCompleteListener(task -> {
            User curUser = task.getResult().toObject(User.class);
            Log.d("fDB", "getComments: "+ curUser.getRatings().keySet());
            for(String key : curUser.getRatings().keySet()){
                comments.add(curUser.getComment(key));
            }
            adapter.notifyDataSetChanged();
        });
        RecyclerView recyclerView = binding.bidderFeedbackRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new CommentsRecyclerViewAdapter(this.getContext(), comments);
        recyclerView.setAdapter(adapter);
    }

    public void setSkills(){
        if (bidUserAcc.getSkills().size() > 0) {
            for(String skill: bidUserAcc.getSkills()){
                switch(skill){
                    case "Carpentry":
                        binding.chipCarpentry.setVisibility(View.VISIBLE);
                        break;
                    case "Plumbing":
                        binding.chipPlumbing.setVisibility(View.VISIBLE);
                        break;
                    case "Electrical":
                        binding.chipElectrical.setVisibility(View.VISIBLE);
                        break;
                    case "Electronics":
                        binding.chipElectronics.setVisibility(View.VISIBLE);
                        break;
                    case "Shopping":
                        binding.chipPersonalShopping.setVisibility(View.VISIBLE);
                        break;
                    case "Assistant":
                        binding.chipVirtualAssistant.setVisibility(View.VISIBLE);
                        break;
                    case "Other":
                        binding.chipOther.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
        else {
            binding.chipOther.setVisibility(View.VISIBLE);
        }
    }
}