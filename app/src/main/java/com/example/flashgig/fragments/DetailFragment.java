package com.example.flashgig.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.R;
import com.example.flashgig.activities.ChatActivity;
import com.example.flashgig.adapters.HorizontalImageRecyclerViewAdapter;
import com.example.flashgig.databinding.FragmentDetailBinding;
import com.example.flashgig.models.Job;
import com.example.flashgig.models.User;
import com.example.flashgig.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DetailFragment extends Fragment implements HorizontalImageRecyclerViewAdapter.ItemClickListener{
    private StorageReference storageRef;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentSnapshot document;

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private String curUser;
    private String jobId;

    private Task<QuerySnapshot> curJob;
    private User clientUser;
    private Job job;

    private FragmentDetailBinding binding;
    private ImageView profilePicDetail;

    private TextView textJobTitle, textJobDate, textJobBudget, textJobLocation, textJobClientEmail, textJobClientName, textJobDescription, textJobWorkers;

    private RecyclerView imageRecyclerView;

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
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        curUser = currentUser.getEmail();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            jobId = mParam1;
        }
        curJob = db.collection("jobs").whereEqualTo("jobId",mParam1).get();
        storageRef = FirebaseStorage.getInstance().getReference();

    }


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentDetailBinding.inflate(inflater, container, false);

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
                    if(!curUser.equals(job.getClient()) && job.getStatus().equalsIgnoreCase("pending")){
                        binding.btnApplyForJob.setVisibility(View.VISIBLE);
                    }
                    else if (curUser.equals(job.getClient())){
                        binding.btnDeleteJob.setVisibility(View.VISIBLE);
                    }
                    // get client user id
                    db.collection("users").whereEqualTo("email", job.getClient()).get().addOnCompleteListener(task1 -> {
                        if(task1.getResult().getDocuments().isEmpty()){
                            Log.d("Detail Fragment", "onComplete: User not found");
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

        binding.btnDeleteJob.setOnClickListener(view -> {
            AlertDialog jobDeletionDialog = new AlertDialog.Builder(getContext())
                    .setTitle("Deleting \""+job.getTitle()+"\"")
                    .setMessage("Are you sure you want to delete this job post?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        deleteJob();
                    })
                    .setNegativeButton("No", null)
                    .show();
            jobDeletionDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.light_red));
            jobDeletionDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
        });


        binding.btnApplyForJob.setOnClickListener(view ->{
            if(job.getBidders().contains(curUser)){
                 binding.btnApplyForJob.setBackgroundColor(65536);
                 Toast.makeText(getActivity(),"Applied for job already!",Toast.LENGTH_SHORT).show();
            }
            else {
                final Map<String, Object> addUsertoArrayMap = new HashMap<>();
                addUsertoArrayMap.put("bidders", FieldValue.arrayUnion(curUser));
                db.collection("jobs").document(document.getId()).update(addUsertoArrayMap);
                Toast.makeText(getActivity(),"Applied for job!",Toast.LENGTH_SHORT).show();
                binding.btnApplyForJob.setBackgroundColor(808080);
                fm.popBackStackImmediate();
            }
        });

        return binding.getRoot();
    }

    private void deleteJob() {
        ProgressDialog progressDialogDeletion = new ProgressDialog(getContext());
        progressDialogDeletion.setMessage("Deleting Job");
        progressDialogDeletion.show();
        // delete from db
        ArrayList<String> jobImageUrls = new ArrayList<>(job.getJobImages());
        db.collection("jobs").document(jobId).delete().addOnSuccessListener(unused -> {
            Log.d("Job Deletion", "deleteJob: deleted from db");
            // delete from storage
            if(jobImageUrls.isEmpty()){
                Log.d("Job Deletion", "deleteJob: no media in storage");
                progressDialogDeletion.dismiss();
                Snackbar.make(getActivity().findViewById(R.id.frameLayout), "Job Deleted!", Snackbar.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
            for (int i = 0; i < jobImageUrls.size(); i++) {
                String imageUrl = jobImageUrls.get(i);
                int finalI = i;
                storageRef.child("media/images/addjob_pictures/" + imageUrl).delete().addOnSuccessListener(unused1 -> {
                    Log.d("Job Deletion", "deleteJob: deleted from storage");
                }).addOnFailureListener(e -> {
                    Log.d("Job Deletion", "deleteJob: failed to delete from storage");
                }).addOnCompleteListener(task -> {
                    Log.d("Job Deletion", "deleteJob: url "+imageUrl);

                    if(finalI == jobImageUrls.size()-1){
                        progressDialogDeletion.dismiss();
//                        Toast.makeText(getContext(), "Job Deleted!", Toast.LENGTH_SHORT).show();
                        Snackbar.make(getActivity().findViewById(R.id.frameLayout), "Job Deleted!", Snackbar.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    }
                });
            }
        }).addOnFailureListener(e -> {
            Log.d("Job Deletion", "deleteJob: failed to delete from db");
        }).addOnCompleteListener(task -> {
            Log.d("Job Deletion", "deleteJob: docId "+jobId);
        });
    }

    private void setViews(){
        String temp = String.valueOf(job.getWorkers().size()) + '/' + job.getNumWorkers();
        textJobTitle.setText(job.getTitle());
        textJobLocation.setText(job.getLocation());
        textJobDate.setText(job.getDate());
        textJobClientEmail.setText(job.getClient());
        textJobClientName.setText(clientUser.getFullName());
        textJobDescription.setText(job.getDescription());
        textJobWorkers.setText(temp);
        textJobBudget.setText(job.getBudget());
        binding.cardView3.setOnClickListener(view -> {
                Fragment fragment = DisplayWorker.newInstance(clientUser.getUserId(), jobId);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment, "displayWorker");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
        });
        if(!clientUser.getEmail().equals(curUser)){
            binding.btnChat.setVisibility(View.VISIBLE);
            binding.btnChat.setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra(Constants.KEY_USER, clientUser);
                startActivity(intent);
            });
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
            binding.btnApplyForJob.setBackgroundColor(808080);
        }

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
                        .centerInside()
                        .into(profilePicDetail);
                binding.progressBarDetail.setVisibility(View.GONE);
            } catch (Exception e) {
                Log.d("Detail Fragment", "loadImage: "+e.toString());
            }
        }).addOnFailureListener(e -> {
            binding.progressBarDetail.setVisibility(View.GONE);
            profilePicDetail.setImageResource(R.drawable.default_profile);
            Log.d("Detail Fragment", "retrieveInfo: "+e.toString());
        });
        // load job images
        ArrayList<String> jobImageUris = new ArrayList<>(job.getJobImages());
        ArrayList<Uri> jobImageArrayList = new ArrayList<>();
        StorageReference jobImagesRef = storageRef.child("/media/images/addjob_pictures/");
        if(jobImageUris.isEmpty()){
            return;
        }
        final Integer[] imageCounter = {0};
        for(String imageUriString: jobImageUris){
            Log.d("detail fragg", "loadImages: "+String.valueOf(imageCounter[0]));
            StorageReference jobImageRef = jobImagesRef.child(imageUriString);
            jobImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                imageCounter[0]++;
                jobImageArrayList.add(uri);
                // if last image uri is fetched, set adapter
                if(imageCounter[0] == jobImageUris.size()){
//                    Toast.makeText(getContext(), "last image is"+String.valueOf(imageCounter[0]), Toast.LENGTH_SHORT).show();
                    HorizontalImageRecyclerViewAdapter adapter = new HorizontalImageRecyclerViewAdapter(getContext(), jobImageArrayList, this);
                    LinearLayoutManager layoutManager
                            = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

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
}