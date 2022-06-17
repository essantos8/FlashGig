package com.example.flashgig.fragments;

import android.content.Intent;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.R;
import com.example.flashgig.activities.ChatActivity;
import com.example.flashgig.adapters.HorizontalImageRecyclerViewAdapter;
import com.example.flashgig.adapters.WorkerRecyclerViewAdapter;
import com.example.flashgig.databinding.FragmentPostedCompletedBinding;
import com.example.flashgig.models.Comment;
import com.example.flashgig.models.Job;
import com.example.flashgig.models.User;
import com.example.flashgig.utilities.Constants;
import com.google.common.net.InternetDomainName;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class PostedCompletedFragment extends Fragment implements HorizontalImageRecyclerViewAdapter.ItemClickListener, WorkerRecyclerViewAdapter.ItemClickListener{
    private FragmentPostedCompletedBinding binding;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String jobId;
    private RatingBar ratingBar;
    private Job job;
    private User clientUser;
    private StorageReference storageRef;
    private RecyclerView imageRecyclerView, feedbackRecyclerView;
    private ArrayList<String> workerListString = new ArrayList<>();
    private ArrayList<User> workerList = new ArrayList<>();

    private WorkerRecyclerViewAdapter adapter;


    public PostedCompletedFragment(String JID){
        this.jobId = JID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        workerList.clear();
        binding = FragmentPostedCompletedBinding.inflate(inflater, container, false);
        db.collection("jobs").whereEqualTo("jobId",jobId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                job = document.toObject(Job.class);
                db.collection("users").whereEqualTo("email", job.getClient()).get().addOnCompleteListener(task1 -> {
                    if(task1.getResult().getDocuments().isEmpty()){
                        Log.d("Detail Fragment", "onComplete: User not found");
                        Toast.makeText(getContext(), "Client user not found!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    clientUser = task1.getResult().getDocuments().get(0).toObject(User.class);
                    getWorkers();
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


    private void getWorkers() {
        workerListString = (ArrayList<String>) job.getWorkers();
        for (int i = 0; i < workerListString.size(); i++) {
            int finalI = i;
            db.collection("users").whereEqualTo("email", workerListString.get(i)).get().addOnCompleteListener(task -> {
                if (task.getResult().getDocuments().isEmpty()) {
                    Log.d("CompletedClientFragment", workerListString.get(finalI) + ": User is NOT FOUND");
                    return;
                }
                workerList.add(task.getResult().getDocuments().get(0).toObject(User.class));
                adapter.notifyDataSetChanged();
            });
        }

        feedbackRecyclerView = binding.workerRecyclerView;
        feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        adapter = new WorkerRecyclerViewAdapter(this.getContext(), workerList, this, jobId);
        feedbackRecyclerView.setAdapter(adapter);
    }

    private void setViews() {
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
                        .into(binding.profilePicDetail);
                binding.progressBarDetail.setVisibility(View.GONE);
            } catch (Exception e) {
                Log.d("Detail Fragment", "loadImage: "+e.toString());
            }
        }).addOnFailureListener(e -> {
            binding.progressBarDetail.setVisibility(View.GONE);
            binding.profilePicDetail.setImageResource(R.drawable.default_profile);
            Log.d("Detail Fragment", "retrieveInfo: "+e.toString());
        });
        // load job images
        ArrayList<String> jobImageUris = new ArrayList<>(job.getJobImages());
        ArrayList<Uri> jobImageArrayList = new ArrayList<>();
        StorageReference jobImagesRef = storageRef.child("/media/images/addjob_pictures/");
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

//    @Override
//    public void onItemClickWorker(String userId1, String jobId1) {
//    }

    public void RateBtnOnClick(String userId, String jobId, float rating, String comment){
        Comment comme = new Comment(job.getClient(),rating,comment,clientUser.getUserId());
        Log.d("BTN!", "RateBtnOnClick: "+userId+jobId+rating+comment);
        db.collection("users").document(userId).update("ratings"+"."+jobId,comme);
        Log.d("Hello!", "RateBtnOnClick: Updated!");
    }
}