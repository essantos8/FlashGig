package com.example.flashgig.fragments;

import android.os.Bundle;

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
import com.example.flashgig.models.Job;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class DetailFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private FirebaseFirestore db;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView JobTitle = view.findViewById(R.id.JobTitle);
        db.collection("jobs").whereEqualTo("jobId", mParam1).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QueryDocumentSnapshot job;
                if(!task.getResult().iterator().hasNext()){
                    Toast.makeText(getContext(), "Job not found in database!", Toast.LENGTH_SHORT).show();
                    Log.d("TAG","job not in database");
                    return;
                }
                job = task.getResult().iterator().next();

                JobTitle.setText(job.getString("client"));
            }
            else{
                Log.d("TAG","mission failed");
            }
        });


        //Designated back button for DetailFragment items
        ImageButton back = (ImageButton) view.findViewById(R.id.backButton);

        back.setOnClickListener(v -> {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStackImmediate();
        });

        return view;
    }
}