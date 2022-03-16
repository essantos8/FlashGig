package com.example.flashgig.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.flashgig.activities.JobAdderActivity;
import com.example.flashgig.activities.SplashActivity;
import com.example.flashgig.databinding.FragmentProfileBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import io.grpc.Context;


public class ProfileFragment extends Fragment {

    private final Integer imageUpdateRC = 100;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference storageRef;

    private TextView textName, textEmail, textPhone;
    private ImageView profilePic;
    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentProfileBinding binding = FragmentProfileBinding.inflate(inflater, container, false);

        textName = binding.textName;
        textEmail = binding.textEmail;
        textPhone = binding.textPhone;
        profilePic = binding.imageViewProfilePic;

        retrieveInfo();
        binding.imageViewProfilePic.setOnClickListener(view -> {
            updateProfilePicture();
        });

        binding.btnProfileUpdate.setOnClickListener(view -> {
            retrieveInfo();
        });

        binding.btnProfileAddJob.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), JobAdderActivity.class));
        });

        binding.btnLogout.setOnClickListener(view ->{
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this.getContext(), "User logged out!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            startActivity(new Intent(getContext(), SplashActivity.class));
        });
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void updateProfilePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, imageUpdateRC);
    }

    private void retrieveInfo(){
        db.collection("users").whereEqualTo("email", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QueryDocumentSnapshot user;
                if(!task.getResult().iterator().hasNext()){
                    Toast.makeText(getContext(), "User not found in database!", Toast.LENGTH_SHORT).show();
                    Log.d("TAG","user not in database");
                    return;
                }
                user = task.getResult().iterator().next();

                textName.setText(user.getString("fullName"));
                textEmail.setText(user.getString("email"));
                textPhone.setText(user.getString("phone"));
            }
            else{
                Log.d("TAG",task.getException().toString());
            }
        });
        StorageReference imageRef = storageRef.child("media/images/" + user.getUid());
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            imageUri = uri;
            Glide.with(getContext())
                    .load(uri)
                    .into(profilePic);
            profilePic.requestLayout();
        }).addOnFailureListener(e -> {
            Log.d("Storage", "File not found!");
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == imageUpdateRC && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Uploading Image...");
        pd.show();
        StorageReference imageRef = storageRef.child("media/images/" + user.getUid());
        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            pd.dismiss();
            Toast.makeText(getContext(), "Image uploaded!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(getContext(), "Failed to upload image!", Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> {
            double progressPercent = (100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
            pd.setMessage("Percentage: " + (int) progressPercent + '%');
        });
    }
}