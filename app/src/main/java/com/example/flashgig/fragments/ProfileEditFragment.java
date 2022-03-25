package com.example.flashgig.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.R;
import com.example.flashgig.databinding.FragmentProfileEditBinding;
import com.example.flashgig.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class ProfileEditFragment extends Fragment {
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private final Integer getPicRC = 100;

    private FragmentProfileEditBinding binding;
    private ImageView profilePicture;
    private Uri imageUri = null;
    private FirebaseFirestore db;

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
        binding = FragmentProfileEditBinding.inflate(inflater, container, false);
        profilePicture = binding.profilePic2;

        fetchUserInfo();

        binding.btnEditPhoto.setOnClickListener(view -> {
            getPicture();
        });

        binding.btnCancel.setOnClickListener(view -> getActivity().onBackPressed());

        binding.btnConfirm.setOnClickListener(view -> {
            pushUserInfo();
        });
        return binding.getRoot();
    }

    private void pushUserInfo() {
        HashMap<String, Object> hashMap = new HashMap<>();
        String newName = binding.etName.getText().toString(), newAbout = binding.etAboutMe.getText().toString();
        if (!newName.equals(user.getFullName()) && !newName.equals("")) {
            hashMap.put("fullName", binding.etName.getText().toString());
        }
        if (!newAbout.equals(user.getAbout()) && !newAbout.equals("")) {
            hashMap.put("about", binding.etAboutMe.getText().toString());
        }
//        hashMap.put("skills", binding.etAboutMe.getText().toString()); // arraylist
        if (hashMap.size() == 0) {
            Log.d("User info update", "No changes were made");
            uploadPicture();
        }
        else{
            db.collection("users").document(currentUser.getUid()).update(hashMap).addOnSuccessListener(unused ->
                    Snackbar.make(getActivity().findViewById(R.id.frameLayout), "User information updated!", Snackbar.LENGTH_SHORT).show()
            ).addOnFailureListener(e ->
                    Toast.makeText(getContext(), "Failed to update User Information!", Toast.LENGTH_SHORT).show()
            ).addOnCompleteListener(task -> uploadPicture());
        }
    }

    private void fetchUserInfo() {
        StorageReference userRef = storageRef.child("media/images/profile_pictures/" + currentUser.getUid());
        userRef.getMetadata().addOnSuccessListener(storageMetadata -> {
            GlideApp.with(this)
                    .load(userRef)
                    .signature(new ObjectKey(String.valueOf(storageMetadata.getCreationTimeMillis())))
                    .fitCenter()
                    .into(profilePicture);
        }).addOnFailureListener(e -> {
            Log.d("Cloud Storage Access", "File not found!");
        });
        db.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
            user = task.getResult().toObject(User.class);
            binding.etName.setHint(user.getFullName());
            if (user.getAbout() != "") {
                binding.etAboutMe.setHint(user.getAbout());
            }
            if (user.getSkills().size() > 0) {
//                binding.
            }
        });
    }

    private void getPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, getPicRC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getPicRC && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profilePicture.setImageURI(imageUri);
        }
    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Uploading...");
        // Only upload to storage if new image is selected
        if (imageUri == null) {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
            return;
        }
        pd.show();
        StorageReference imageRef = storageRef.child("media/images/profile_pictures/" + currentUser.getUid());
        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Log.d("Cloud Storage", "Image uploaded!");
            pd.dismiss();
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }).addOnFailureListener(e -> {
            Log.d("Cloud Storage", "Error uploading image!");
            pd.dismiss();
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }).addOnProgressListener(snapshot -> {
            double progress = 100.0 * ((double) snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            pd.setMessage("Progress: " + progress + "%");
        });
    }
}