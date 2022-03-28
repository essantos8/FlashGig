package com.example.flashgig.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.R;
import com.example.flashgig.activities.ReviewsActivity;
import com.example.flashgig.activities.SplashActivity;
import com.example.flashgig.databinding.FragmentProfileBinding;
import com.example.flashgig.models.User;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ProfileFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseUser currentUser;

    private FragmentProfileBinding binding;
    private TextView textName, textEmail, textPhone, textDescription;
    private ImageView profilePicture;

    private ProgressBar progressBar;
    private User user;
    private GoogleSignInClient googleSignInClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .build();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        currentUser = mAuth.getCurrentUser();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        progressBar = binding.progressBar;

        textName = binding.textName;
        textEmail = binding.textEmail;
        textPhone = binding.textPhone;
        profilePicture = binding.ProfilePic;

        retrieveInfo(false);

        binding.btnEditProfile.setOnClickListener(view -> {
//            startActivity(new Intent(getContext(), ProfileEditActivity.class));
            replaceFragment(new ProfileEditFragment(), "profileEdit");
        });

        binding.btnProfileAddJob.setOnClickListener(view -> {
//            startActivity(new Intent(getContext(), JobAdderActivity.class));
            replaceFragment(new JobAdderFragment(), "jobAdder");
        });

        binding.btnReviews.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), ReviewsActivity.class));
        });

        binding.btnProfileUpdate.setOnClickListener(view -> {
            retrieveInfo(true);
        });

        binding.btnLogout.setOnClickListener(view -> {
            googleSignInClient.signOut();
            FirebaseAuth.getInstance().signOut();
            db.clearPersistence();
            Toast.makeText(this.getContext(), "User logged out!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            startActivity(new Intent(getContext(), SplashActivity.class));
        });
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
    private void replaceFragment(Fragment newFragment, String tag){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, newFragment, tag)
                .addToBackStack(null)
                .commit();
    }

    private void retrieveInfo(Boolean refresh) {
        StorageReference userRef = storageRef.child("media/images/profile_pictures/" + currentUser.getUid());
        userRef.getMetadata().addOnSuccessListener(storageMetadata -> {
//            Snackbar.make(binding.getRoot(), "File exists!", Snackbar.LENGTH_SHORT).show();
            if (refresh) {
                //
            } else {
                GlideApp.with(this)
                        .load(userRef)
                        .signature(new ObjectKey(String.valueOf(storageMetadata.getCreationTimeMillis())))
                        .fitCenter()
                        .into(profilePicture);
            }
            progressBar.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            Log.d("Profile", "retrieveInfo: "+e.toString());
//            Snackbar.make(binding.getRoot(), "File does not exist!", Snackbar.LENGTH_SHORT).show();
        });
        db.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
            user = task.getResult().toObject(User.class);

            if (user != null && task.isSuccessful()) {

                textName.setText(user.getFullName());
                textEmail.setText(user.getEmail());
                textPhone.setText(user.getPhone());

                binding.tvdesc.setText(user.getAbout());
                if (user.getSkills().size() > 0) {
                    for(String skill: user.getSkills()){
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
                    //                binding.
                }
            }
            else{
                Log.d("Profile", "retrieveInfo: "+String.valueOf(currentUser.getUid()));


                Log.d("Profile", "user not in database");
//                Toast.makeText(getContext(), "User not found in database!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}