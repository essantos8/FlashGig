package com.example.flashgig.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.example.flashgig.adapters.CommentsRecyclerViewAdapter;
import com.example.flashgig.databinding.FragmentDetailBinding;
import com.example.flashgig.databinding.FragmentDisplayBidderBinding;
import com.example.flashgig.databinding.FragmentDisplayWorkerBinding;
import com.example.flashgig.models.Comment;
import com.example.flashgig.models.Job;
import com.example.flashgig.models.User;
import com.example.flashgig.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class DisplayWorker extends Fragment {
    private static final String ARG_PARAM1 = "param1";    //for userId (of worker)
    private static final String ARG_PARAM2 = "param2";    //for jobId
    private String mParam1;    //for userId (of worker)
    private String mParam2;    //for jobId

    private FirebaseFirestore db;
    private FragmentDisplayWorkerBinding binding;
    private ArrayList<Comment> comments = new ArrayList<>();
    private CommentsRecyclerViewAdapter adapter;
    private FirebaseUser currentUser;
    DocumentSnapshot document;
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
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        jobDocRef = db.collection("jobs").document(mParam2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDisplayWorkerBinding.inflate(inflater, container, false);

        textWorkerName = binding.textWorkerName;
        textWorkerNumber = binding.textWorkerNumber;
        textWorkerEmail = binding.textWorkerEmail;

        //SETTING/SHOWING THE PROFILE OF BIDDER
        workerDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                workUserAcc = documentSnapshot.toObject(User.class);
                //Log.d("DisplayWorker", "onComplete: User's name is: " + workUserAcc.getFullName());
                retrieveInfo(workUserAcc);
            }
        });



        //BACK BUTTON
        binding.backButtonDW.setOnClickListener(view ->{
            fm = getActivity().getSupportFragmentManager();
            fm.popBackStackImmediate();
        });

        return binding.getRoot();
    }

    private void retrieveInfo(User user){
        textWorkerName.setText(user.getFullName());
        textWorkerNumber.setText(user.getPhone());
        textWorkerEmail.setText(user.getEmail());
        if(!workUserAcc.getEmail().equals(currentUser.getEmail())){
            binding.btnChatWorkerProfile.setVisibility(View.VISIBLE);
            binding.btnChatWorkerProfile.setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra(Constants.KEY_USER, workUserAcc);
                getContext().startActivity(intent);
            });
        }
        binding.rbWorker.setRating(user.getAverageRating());
        getComments(user);
        setSkills();
        StorageReference userRef = FirebaseStorage.getInstance().getReference().child("media/images/profile_pictures/" + user.getUserId());
        userRef.getMetadata().addOnSuccessListener(storageMetadata -> {
//            Snackbar.make(binding.getRoot(), "File exists!", Snackbar.LENGTH_SHORT).show();
            if(getContext() != null) {
                GlideApp.with(getContext())
                        .load(userRef)
                        .signature(new ObjectKey(String.valueOf(storageMetadata.getCreationTimeMillis())))
                        .fitCenter()
                        .into(binding.imageWorker);
            }
        }).addOnFailureListener(e -> {
            Log.d("Profile", "retrieveInfo: "+e.toString());
//            Snackbar.make(binding.getRoot(), "File does not exist!", Snackbar.LENGTH_SHORT).show();
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
        RecyclerView recyclerView = binding.workerFeedbackRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new CommentsRecyclerViewAdapter(this.getContext(), comments);
        recyclerView.setAdapter(adapter);
    }
    public void setSkills(){
        if (workUserAcc.getSkills().size() > 0) {
            for(String skill: workUserAcc.getSkills()){
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