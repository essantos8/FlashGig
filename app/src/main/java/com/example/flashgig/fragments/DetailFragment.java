package com.example.flashgig.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashgig.R;
import com.example.flashgig.databinding.FragmentDetailBinding;
import com.example.flashgig.databinding.FragmentProfileBinding;
import com.example.flashgig.models.Job;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class DetailFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private FirebaseFirestore db;
    String curUser;
    DocumentSnapshot document;
    Task<QuerySnapshot> curJob;
    FragmentManager fm;
    Job job;

    private TextView textJobTitle, textJobDate, textJobBudget, textJobLocation, textJobClient, textJobDescription;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        curUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        curJob = db.collection("jobs").whereEqualTo("jobId",mParam1).get();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        FragmentDetailBinding binding = FragmentDetailBinding.inflate(inflater, container, false);

        textJobTitle = binding.textJobTitle;
        textJobLocation = binding.textJobLocation;
        textJobDate = binding.textJobDate;
        textJobClient = binding.textJobClient;
        textJobDescription = binding.textJobDescription;
        textJobBudget = binding.textJobBudget;

        curJob.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    document = task.getResult().getDocuments().get(0);
                    job = document.toObject(Job.class);
                    textJobTitle.setText(job.getTitle());
                    textJobLocation.setText(job.getLocation());
                    textJobDate.setText(job.getDate());
                    textJobClient.setText(job.getClient());
                    textJobDescription.setText(job.getDescription());
                    textJobBudget.setText(job.getBudget());

                    if (job.getClient().equals(curUser)){
                        binding.btnAcceptJob.setVisibility(View.GONE);
                    }

                    for (String category : job.getCategories()){
                        switch (category) {
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
                            case "Personal Shopping":
                            case "Shopping":
                                binding.chipPersonalShopping.setVisibility(View.VISIBLE);
                                break;
                            case "Virtual Assistant":
                            case "Assistant":
                                binding.chipVirtualAssistant.setVisibility(View.VISIBLE);
                                break;
                            case "Other":
                                binding.chipOther.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                    if(job.getBidders().contains(curUser)){
                        binding.btnAcceptJob.setBackgroundColor(808080);
                    }
                }
            }
        });

        binding.backButton.setOnClickListener(view ->{
            fm = getActivity().getSupportFragmentManager();
            fm.popBackStackImmediate();
        });

        binding.btnAcceptJob.setOnClickListener(view ->{
             if(job.getBidders().contains(curUser)){
                 binding.btnAcceptJob.setBackgroundColor(808080);
                 Toast.makeText(getActivity(),"Applied for job already!",Toast.LENGTH_SHORT).show();
             } else {
                 final Map<String, Object> addUsertoArrayMap = new HashMap<>();
                 addUsertoArrayMap.put("bidders", FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                 curJob.addOnCompleteListener(task -> {
                     if (task.isSuccessful()) {
                         db.collection("jobs").document(document.getId()).update(addUsertoArrayMap);
                         Toast.makeText(getActivity(),"Applied for job already!",Toast.LENGTH_SHORT).show();
                         binding.btnAcceptJob.setBackgroundColor(808080);
                     }
                 });
             }


        });

        return binding.getRoot();
    }
}