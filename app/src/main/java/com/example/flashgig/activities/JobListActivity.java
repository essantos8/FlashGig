package com.example.flashgig.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.flashgig.models.Job;
import com.example.flashgig.databinding.ActivityJobListBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class JobListActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ArrayList<Job> jobList = new ArrayList<>();
    private JobRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityJobListBinding binding = ActivityJobListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        RecyclerView recyclerView = binding.recyclerViewJobs;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new JobRecyclerViewAdapter(this, jobList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
//        db.collection("jobs").get().addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                for(QueryDocumentSnapshot doc : task.getResult()){
//                    jobList.add(new Job(doc.getString("title"),
//                                        doc.getString("description"),
//                                        (ArrayList<String>) doc.get("categories"),
//                                        doc.getString("jobId")));
//                }
//            }
//            adapter.notifyDataSetChanged();
//
//        });

        eventChangeListener();
/*

*/

//        Toast.makeText(this, "hehe", Toast.LENGTH_SHORT).show();
    }

    private void eventChangeListener(){
        db.collection("jobs").addSnapshotListener((value, error) -> {
            if(error != null){
                Log.d("erorrr", "Firebase error");
            }
            for(DocumentChange dc : value.getDocumentChanges()){
                if(dc.getType() == DocumentChange.Type.ADDED){
                    jobList.add(dc.getDocument().toObject(Job.class));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}