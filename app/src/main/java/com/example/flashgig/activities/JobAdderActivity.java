package com.example.flashgig.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.flashgig.models.Job;
import com.example.flashgig.databinding.ActivityJobAdderBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class JobAdderActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    private EditText editTextTitle, editTextDescription;
    private ToggleButton tglCarp, tglPlum, tglElec, tglOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityJobAdderBinding binding = ActivityJobAdderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        editTextTitle = binding.editTextTextAddJobTitle;
        editTextDescription = binding.editTextTextAddJobDesc;
        tglCarp = binding.tglCarp;
        tglPlum = binding.tglPlum;
        tglElec = binding.tglElec;
        tglOther = binding.tglOther;

        binding.btnAddJob.setOnClickListener(view -> {
            addJob();
        });
    }

    private void addJob() {
        String title = editTextTitle.getText().toString(),
                description = editTextDescription.getText().toString();
        ArrayList<String> categories = new ArrayList<>();

        if(title.isEmpty()){
            editTextTitle.setError("Job Title is required!");
            editTextTitle.requestFocus();
            return;
        }
        if(description.isEmpty()){
            editTextDescription.setError("Description is required!");
            editTextDescription.requestFocus();
            return;
        }

        if(tglCarp.isChecked()){
            categories.add("Carpentry");
        }
        if(tglPlum.isChecked()){
            categories.add("Plumbing");
        }
        if(tglElec.isChecked()){
            categories.add("Electrical");
        }
        if(tglOther.isChecked()){
            categories.add("Other");
        }
        if(categories.isEmpty()){
            categories.add("Other");
        }

        DocumentReference doc = db.collection("jobs").document();
        Job job = new Job(title, description, categories, doc.getId());
        doc.set(job);

        Toast.makeText(this, "Job Added to Database", Toast.LENGTH_SHORT).show();
        finish();
    }
}