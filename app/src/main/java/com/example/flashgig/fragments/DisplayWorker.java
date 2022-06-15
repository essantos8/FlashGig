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
import com.example.flashgig.databinding.FragmentDisplayWorkerBinding;
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

public class DisplayWorker extends Fragment {
    private static final String ARG_PARAM1 = "param1";    //for userId (of worker)
    private static final String ARG_PARAM2 = "param2";    //for jobId
    private String mParam1;    //for userId (of worker)
    private String mParam2;    //for jobId

    private FirebaseFirestore db;
    DocumentSnapshot document;
    //Task<QuerySnapshot> workUser1;
    DocumentReference workerDocRef;
    DocumentReference jobDocRef;
    FragmentManager fm;

    Job job;    //container of the job (to check if bidder is already worker)
    User workUserAcc;    //container of bidder/worker

    private TextView textWorkerName, textWorkerNumber, textWorkerEmail;

    public DisplayWorker() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DisplayWorker.
     */
    // TODO: Rename and change types and number of parameters
    public static DisplayWorker newInstance(String mParam1, String mParam2) {
        DisplayWorker fragment = new DisplayWorker();
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
        workerDocRef = db.collection("users").document(mParam1);
        jobDocRef = db.collection("jobs").document(mParam2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentDisplayWorkerBinding binding = FragmentDisplayWorkerBinding.inflate(inflater, container, false);

        textWorkerName = binding.textWorkerName;
        textWorkerNumber = binding.textWorkerNumber;
        textWorkerEmail = binding.textWorkerEmail;

        //SETTING/SHOWING THE PROFILE OF BIDDER
        workerDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                workUserAcc = documentSnapshot.toObject(User.class);
                //Log.d("DisplayWorker", "onComplete: User's name is: " + workUserAcc.getFullName());
                textWorkerName.setText(workUserAcc.getFullName());
                textWorkerNumber.setText(workUserAcc.getPhone());
                textWorkerEmail.setText(workUserAcc.getEmail());
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
        binding.backButtonDW.setOnClickListener(view ->{
            fm = getActivity().getSupportFragmentManager();
            fm.popBackStackImmediate();
        });

        return binding.getRoot();
    }
}