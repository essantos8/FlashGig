package com.example.flashgig.fragments;

import android.annotation.SuppressLint;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.R;
import com.example.flashgig.adapters.HorizontalImageRecyclerViewAdapter;
import com.example.flashgig.databinding.FragmentAppliedPendingBinding;
import com.example.flashgig.models.Job;
import com.example.flashgig.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class AppliedPendingFragment extends Fragment implements HorizontalImageRecyclerViewAdapter.ItemClickListener {
    private StorageReference storageRef;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentSnapshot document;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String jobId;
    private String jobStatus;
    private String curUser;

    private Task<QuerySnapshot> curJob;
    private User clientUser;
    private User bidUser;
    private User workUser;
    private Job job;

    private FragmentAppliedPendingBinding binding;
    private ImageView profilePicDetail;

    private TextView textJobTitle, textJobDate, textJobBudget, textJobLocation, textJobClientEmail, textJobClientName, textJobDescription, textJobWorkers;

    private RecyclerView imageRecyclerView;

    public AppliedPendingFragment() {
        // Required empty public constructor
    }

    public static AppliedPendingFragment newInstance(String param1, String param2) {
        AppliedPendingFragment fragment = new AppliedPendingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        curUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (getArguments() != null) {
            jobId = getArguments().getString(ARG_PARAM1);
            jobStatus = getArguments().getString(ARG_PARAM2);
        }
        curJob = db.collection("jobs").whereEqualTo("jobId",jobId).get();
        storageRef = FirebaseStorage.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        binding = FragmentAppliedPendingBinding.inflate(inflater, container, false);

        textJobTitle = binding.textJobTitle;
        textJobLocation = binding.textJobLocation;
        textJobDate = binding.textJobDate;
        textJobClientEmail = binding.textJobClientEmail;
        textJobClientName = binding.textJobClientName;
        textJobDescription = binding.textJobDescription;
        textJobBudget = binding.textJobBudget;
        textJobWorkers = binding.textJobWorkers;
        profilePicDetail = binding.profilePicDetail;
        imageRecyclerView = binding.imageRecyclerView;

        curJob.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    document = task.getResult().getDocuments().get(0);
                    job = document.toObject(Job.class);
                    // get client user id
                    db.collection("users").whereEqualTo("email", job.getClient()).get().addOnCompleteListener(task1 -> {
                        if(task1.getResult().getDocuments().isEmpty()){
                            Log.d("Pending Fragment Worker", "onComplete: User not found");
                            Toast.makeText(getContext(), "Client user not found!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        clientUser = task1.getResult().getDocuments().get(0).toObject(User.class);
                        setViews();
                        loadImages();
                    });
                }
            }
        });

        FragmentManager fm = getActivity().getSupportFragmentManager();

        binding.backButton.setOnClickListener(view ->{
            fm.popBackStackImmediate();
        });

        return binding.getRoot();
    }


    private void setViews(){
        textJobTitle.setText(job.getTitle());
        textJobLocation.setText(job.getLocation());
        textJobDate.setText(job.getDate());
        textJobClientEmail.setText(job.getClient());
        textJobClientName.setText(clientUser.getFullName());
        textJobDescription.setText(job.getDescription());
        textJobWorkers.setText(String.valueOf(job.getNumWorkers()));
        textJobBudget.setText(job.getBudget());

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

        // Client Card


    }
    private void loadImages() {
        String clientId = clientUser.getUserId();
        // load client profile pic
        StorageReference userRef = storageRef.child("media/images/profile_pictures/" + clientId);
        userRef.getMetadata().addOnSuccessListener(storageMetadata -> {
            try {
                GlideApp.with(this)
                        .load(userRef)
                        .error(R.drawable.default_profile)
                        .signature(new ObjectKey(String.valueOf(storageMetadata.getCreationTimeMillis())))
                        .fitCenter()
                        .into(profilePicDetail);
                binding.progressBarDetail.setVisibility(View.GONE);
            } catch (Exception e) {
                Log.d("Pending Fragment Client", "loadImage: "+e.toString());
            }
        }).addOnFailureListener(e -> {
            binding.progressBarDetail.setVisibility(View.GONE);
            profilePicDetail.setImageResource(R.drawable.default_profile);
            Log.d("Pending Fragment Worker", "retrieveInfo: "+e.toString());
        });
        // load job images
        ArrayList<String> jobImageUris = new ArrayList<>(job.getJobImages());
        ArrayList<Uri> jobImageArrayList = new ArrayList<>();
        StorageReference jobImagesRef = storageRef.child("/media/images/addjob_pictures/");
        final Integer[] imageCounter = {0};
        for(String imageUriString: jobImageUris){
            Log.d("Pending Fragment Worker", "loadImages: "+String.valueOf(imageCounter[0]));
            StorageReference jobImageRef = jobImagesRef.child(imageUriString);
            jobImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                imageCounter[0]++;
                jobImageArrayList.add(uri);
                // if last image uri is fetched, set adapter
                if(imageCounter[0] == jobImageUris.size()){
//                    Toast.makeText(getContext(), "last image is"+String.valueOf(imageCounter[0]), Toast.LENGTH_SHORT).show();
                    HorizontalImageRecyclerViewAdapter adapter = new HorizontalImageRecyclerViewAdapter(getContext(), jobImageArrayList, this);
                    LinearLayoutManager layoutManager
                            = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

                    imageRecyclerView.setLayoutManager(layoutManager);
                    imageRecyclerView.setAdapter(adapter);
                }
            });
        }
    }

    @Override
    public void onItemClick(Uri imageUri) {
        ImagePopupFragment imagePopupFragment = new ImagePopupFragment(imageUri);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.add(R.id.frameLayout, imagePopupFragment,"imagePopup").addToBackStack(null).commit();
        return;
    }
    /*
    @Override
    public void onItemClick(String userId, String jobId) {
        Fragment fragment = DisplayBidder.newInstance(userId, jobId);    //CHANGE TO DISPLAYCLIENT!!!
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, "displayBidder");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }*/
}