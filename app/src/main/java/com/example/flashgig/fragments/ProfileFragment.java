package com.example.flashgig.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.JobAdderFragment;
import com.example.flashgig.R;
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

    private ProgressBar progressBar;
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