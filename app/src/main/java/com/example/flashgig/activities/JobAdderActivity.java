package com.example.flashgig.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.flashgig.models.Job;
import com.example.flashgig.databinding.ActivityJobAdderBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JobAdderActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    //private EditText editTextTitle, editTextDescription;
    private TextInputLayout tiltitlejobadder, tildescriptionjobadder;
    private TextInputEditText tiettitlejobadder, tietdescriptionjobadder;

    ActivityJobAdderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityJobAdderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        tiltitlejobadder = binding.tiltitlejobadder;
        tildescriptionjobadder = binding.tildescriptionjobadder;
        tiettitlejobadder = binding.tiettitlejobadder;
        tietdescriptionjobadder = binding.tietdescriptionjobadder;

        binding.btnAddJob.setOnClickListener(view -> {
            addJob();
        });

    }

    private void addJob() {
        String title = tiettitlejobadder.getText().toString(),
                description = tietdescriptionjobadder.getText().toString();
        ArrayList<String> categories = new ArrayList<>();
        for (int i = 0; i < binding.chipGroupCategories.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroupCategories.getChildAt(i);
            if(chip.isChecked()){
                switch (i){
                    case 0:
                        categories.add("Carpentry");
                        break;
                    case 1:
                        categories.add("Plumbing");
                        break;
                    case 2:
                        categories.add("Electrical");
                        break;
                    case 3:
                        categories.add("Electronics");
                        break;
                    case 4:
                        categories.add("Shopping");
                        break;
                    case 5:
                        categories.add("Assistant");
                        break;
                    case 6:
                        categories.add("Others");
                        break;
                }
            }
        }



        if(title.isEmpty()){
            tiltitlejobadder.setError("Job Title is required!");
            //editTextTitle.requestFocus();
            return;
        }
        if(description.isEmpty()){
            tildescriptionjobadder.setError("Description is required!");
            //editTextDescription.requestFocus();
            return;
        }


        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyy");
        mAuth = FirebaseAuth.getInstance();
        DocumentReference doc = db.collection("jobs").document();
        Job job = new Job(title, description, mAuth.getCurrentUser().getEmail(), dateFormat.format(Calendar.getInstance().getTime()), categories, doc.getId());
        doc.set(job);

        HashMap timestamp = new HashMap();
        timestamp.put("timestamp", FieldValue.serverTimestamp());
        doc.update(timestamp);
        Toast.makeText(this, "Job Added to Database", Toast.LENGTH_SHORT).show();
        finish();
    }
}