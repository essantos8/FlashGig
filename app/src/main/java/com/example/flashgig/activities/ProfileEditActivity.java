package com.example.flashgig.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.signature.ObjectKey;
import com.example.flashgig.GlideApp;
import com.example.flashgig.databinding.ActivityProfileEditBinding;
import com.example.flashgig.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class ProfileEditActivity extends AppCompatActivity {
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private final Integer getPicRC = 100;

    private ActivityProfileEditBinding binding;
    private ImageView profilePicture;
    private Uri imageUri;
    private FirebaseFirestore db;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        profilePicture = binding.profilePic2;

        fetchUserInfo();

        binding.btnEditPhoto.setOnClickListener(view -> {
            getPicture();
        });

        binding.btnCancel.setOnClickListener(view -> onBackPressed());

        binding.btnConfirm.setOnClickListener(view -> {
            // Only upload to storage if new image is selected
            if (imageUri != null) uploadPicture();
            pushUserInfo();
        });
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
            Toast.makeText(this, "No changes were made", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return;
        }
        db.collection("users").document(currentUser.getUid()).update(hashMap).addOnSuccessListener(unused ->
                Toast.makeText(this, "User Information Updated", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to update User Information!", Toast.LENGTH_SHORT).show()
        ).addOnCompleteListener(task -> onBackPressed());
    }

    private void fetchUserInfo() {
        StorageReference userRef = storageRef.child("media/images/profile_pictures/" + currentUser.getUid());
        userRef.getMetadata().addOnSuccessListener(storageMetadata -> {
            GlideApp.with(this)
                    .load(userRef)
                    .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
//                    .transform(new CircleCrop())
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getPicRC && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profilePicture.setImageURI(imageUri);
        }
    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading...");
        pd.show();

        StorageReference imageRef = storageRef.child("media/images/profile_pictures/" + currentUser.getUid());
        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
//            Toast.makeText(this, "Image uploaded!", Toast.LENGTH_SHORT).show();
            Snackbar.make(binding.getRoot(), "Image uploaded!", Snackbar.LENGTH_SHORT).show();
            pd.dismiss();
        }).addOnFailureListener(e -> {
            Snackbar.make(binding.getRoot(), "Error uploading image!", Snackbar.LENGTH_SHORT).show();
            pd.dismiss();
//            Toast.makeText(this, "Error ", Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> {
            double progress = 100.0 * ((double) snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            pd.setMessage("Progress: " + progress + "%");
        });
    }
}
