package com.example.flashgig.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.R;
import com.example.flashgig.activities.ChatActivity;
import com.example.flashgig.adapters.ClientRecyclerViewAdapter;
import com.example.flashgig.adapters.HorizontalImageRecyclerViewAdapter;
import com.example.flashgig.databinding.FragmentAppliedCompletedBinding;
import com.example.flashgig.models.Comment;
import com.example.flashgig.models.Job;
import com.example.flashgig.models.User;
import com.example.flashgig.utilities.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class AppliedCompletedFragment extends Fragment implements HorizontalImageRecyclerViewAdapter.ItemClickListener, ClientRecyclerViewAdapter.ItemClickListener {
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
    private Job job;
    private ArrayList<User> clientList = new ArrayList<>();
    private ClientRecyclerViewAdapter adapter;

    private FragmentAppliedCompletedBinding binding;

    private RecyclerView imageRecyclerView, feedbackRecyclerView;

    public AppliedCompletedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PendingFragmentWorker.
     */
    // TODO: Rename and change types and number of parameters
    public static AppliedCompletedFragment newInstance(String param1, String param2) {
        AppliedCompletedFragment fragment = new AppliedCompletedFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAppliedCompletedBinding.inflate(inflater, container, false);
        db.collection("jobs").whereEqualTo("jobId",jobId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                job = document.toObject(Job.class);
                // get client user id
                db.collection("users").whereEqualTo("email", job.getClient()).get().addOnCompleteListener(task1 -> {
                    if(task1.getResult().getDocuments().isEmpty()){
                        Log.d("Detail Fragment", "onComplete: User not found");
                        Toast.makeText(getContext(), "Client user not found!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    clientUser = task1.getResult().getDocuments().get(0).toObject(User.class);

                    clientList.add(clientUser);
                    feedbackRecyclerView = binding.workerRecyclerView;
                    feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
                    adapter = new ClientRecyclerViewAdapter(this.getContext(), clientList, this, jobId);
                    feedbackRecyclerView.setAdapter(adapter);

                    setViews();
                    loadImages();
                });
            }
        });

        FragmentManager fm = getActivity().getSupportFragmentManager();

        binding.backButton.setOnClickListener(view ->{
            fm.popBackStackImmediate();
        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }


    private void setViews(){
        String temp = String.valueOf(job.getWorkers().size()) + '/' + job.getNumWorkers();
        binding.textJobClientName.setText(clientUser.getFullName());
        binding.textJobClientEmail.setText(clientUser.getEmail());
        binding.textJobTitle.setText(job.getTitle());
        binding.textJobLocation.setText(job.getLocation());
        binding.textJobDate.setText(job.getDate());
        binding.textJobBudget.setText(job.getBudget());
        binding.textJobDescription.setText(job.description);
        binding.textJobWorkers.setText(temp);
        imageRecyclerView = binding.imageRecyclerView;
        feedbackRecyclerView = binding.workerRecyclerView;
        binding.cardView3.setOnClickListener(view -> {
            Fragment fragment = DisplayWorker.newInstance(clientUser.getUserId(), jobId);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fragment, "displayWorker");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        binding.btnChat.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra(Constants.KEY_USER, clientUser);
            startActivity(intent);
        });

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
            if(getContext() != null){
                GlideApp.with(getContext())
                        .load(userRef)
                        .error(R.drawable.default_profile)
                        .signature(new ObjectKey(String.valueOf(storageMetadata.getCreationTimeMillis())))
                        .fitCenter()
                        .into(binding.profilePicDetail);
                binding.progressBarDetail.setVisibility(View.GONE);
            }
        }).addOnFailureListener(e -> {
            binding.progressBarDetail.setVisibility(View.GONE);
            binding.profilePicDetail.setImageResource(R.drawable.default_profile);
            Log.d("AppliedCompletedFrag", "loadImages: "+e.toString());
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

    @Override
    public void RateBtnOnClick(String userId,String jobId, float rating, String comment) {
        Comment comme = new Comment(currentUser.getEmail(),rating,comment,currentUser.getUid());
        db.collection("users").document(userId).update("ratings"+"."+jobId,comme);
    }
}