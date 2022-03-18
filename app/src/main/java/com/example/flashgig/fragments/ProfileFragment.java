package com.example.flashgig.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.activities.JobAdderActivity;
import com.example.flashgig.activities.ProfileEditActivity;
import com.example.flashgig.activities.ReviewsActivity;
import com.example.flashgig.activities.SplashActivity;
import com.example.flashgig.databinding.FragmentProfileBinding;
import com.example.flashgig.models.User;
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
    private TextView textName, textEmail, textPhone;
    private ImageView profilePicture;

    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        currentUser = mAuth.getCurrentUser();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        textName = binding.textName;
        textEmail = binding.textEmail;
        textPhone = binding.textPhone;
        profilePicture = binding.ProfilePic;

        retrieveInfo(false);

        binding.btnProfileUpdate.setOnClickListener(view -> {
            retrieveInfo(false);
        });

        binding.btnEditProfile.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), ProfileEditActivity.class));

        });

        binding.btnProfileAddJob.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), JobAdderActivity.class));

        });

        binding.btnReviews.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), ReviewsActivity.class));
        });

        binding.btnProfileUpdate.setOnClickListener(view -> {
            retrieveInfo(true);
        });

        binding.btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            db.clearPersistence();
            Toast.makeText(this.getContext(), "User logged out!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            startActivity(new Intent(getContext(), SplashActivity.class));
        });
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void retrieveInfo(Boolean refresh) {
        StorageReference userRef = storageRef.child("media/images/profile_pictures/" + currentUser.getUid());
        userRef.getMetadata().addOnSuccessListener(storageMetadata -> {
//            Snackbar.make(binding.getRoot(), "File exists!", Snackbar.LENGTH_SHORT).show();
            if (refresh) {
                GlideApp.with(this)
                        .load(userRef)
                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
                        .fitCenter()
                        .into(profilePicture);
            } else {
                GlideApp.with(this)
                        .load(userRef)
//                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                        .fitCenter()
                        .into(profilePicture);
            }
        }).addOnFailureListener(e -> {
//            Snackbar.make(binding.getRoot(), "File does not exist!", Snackbar.LENGTH_SHORT).show();
        });
        db.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
            user = task.getResult().toObject(User.class);
            binding.textName.setText(user.getFullName());
            if (user.getAbout() != "") {
                binding.tvdesc.setText(user.getAbout());
            }
            if (user.getSkills().size() > 0) {
//                binding.
            }
        });
        db.collection("users").whereEqualTo("email", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QueryDocumentSnapshot user;
                if (!task.getResult().iterator().hasNext()) {
                    Toast.makeText(getContext(), "User not found in database!", Toast.LENGTH_SHORT).show();
                    Log.d("Profile", "user not in database");
                    return;
                }
                user = task.getResult().iterator().next();

                textName.setText(user.getString("fullName"));
                textEmail.setText(user.getString("email"));
                textPhone.setText(user.getString("phone"));

            } else {
                Log.d("Profile", "Database error");
            }
        });
    }
}