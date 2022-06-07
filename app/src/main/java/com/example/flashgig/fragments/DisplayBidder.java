package com.example.flashgig.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashgig.R;
import com.example.flashgig.databinding.FragmentDetailBinding;
import com.example.flashgig.databinding.FragmentDisplayBidderBinding;
import com.example.flashgig.models.Job;
import com.example.flashgig.models.User;
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
    DocumentSnapshot document;
    //Task<QuerySnapshot> bidUser1;
    DocumentReference bidderDocRef;
    DocumentReference jobDocRef;
    FragmentManager fm;

    Job job;    //container of the job (to check if bidder is already worker)
    User bidUserAcc;    //container of bidder/worker

    private TextView textBidderName, textBidderNumber, textBidderEmail;

    public DisplayBidder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DisplayBidder.
     */
    // TODO: Rename and change types and number of parameters
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentDisplayBidderBinding binding = FragmentDisplayBidderBinding.inflate(inflater, container, false);

        textBidderName = binding.textBidderName;
        textBidderNumber = binding.textBidderNumber;
        textBidderEmail = binding.textBidderEmail;

        //SETTING/SHOWING THE PROFILE OF BIDDER
        bidderDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                bidUserAcc = documentSnapshot.toObject(User.class);
                //Log.d("BiddersFragment", "onComplete: User's name is: " + bidUserAcc.getFullName());
                textBidderName.setText(bidUserAcc.getFullName());
                textBidderNumber.setText(bidUserAcc.getPhone());
                textBidderEmail.setText(bidUserAcc.getEmail());
            }
        });

        //For checking if bidder is already a worker or declined
        jobDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                job = documentSnapshot.toObject(Job.class);
            }
        });

        /*
        //For getting the job.getWorkers()
        db.collection("jobs").whereEqualTo("jobId", mParam2).get().addOnCompleteListener(task1 -> {
            if (task1.getResult().getDocuments().isEmpty()) {
                Log.d("DisplayBidder", "onComplete: Job is NOT FOUND");
                Toast.makeText(getContext(), "Job NOT FOUND.", Toast.LENGTH_SHORT).show();
                return;
            }
            job = task1.getResult().getDocuments().get(0).toObject(Job.class);
        });*/

        //BACK BUTTON
        binding.backButton.setOnClickListener(view ->{
            fm = getActivity().getSupportFragmentManager();
            fm.popBackStackImmediate();
        });

        binding.btnAcceptBidder.setOnClickListener(view -> {
            if (job.getWorkers().contains(bidUserAcc.getEmail())) {    //if the job has bidder in its "workers" array
                binding.btnAcceptBidder.setBackgroundColor(808080);    //set the button color to grey
                Toast.makeText(getActivity(), "Bidder already accepted as worker!", Toast.LENGTH_SHORT).show();    //show that the bidder is already a worker
            } else {
                bidderListTemp = job.getBidders();    //get the bidder list
                workerListTemp = job.getWorkers();    //get the worker list
//                jobRating.put(job.jobId,0); //put an initial rating of 0 for a particular job
//                bidUserAcc.ratings.put(job.jobId, 0); // inputs a temporary rating of 0;
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
//                bidderDocRef.update("ratings",jobRating);

            }
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
            }
        });


        return binding.getRoot();
    }
}