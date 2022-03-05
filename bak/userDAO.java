package com.example.flashgig;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class userDAO {
    private DatabaseReference dbRef;
    public userDAO(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("users");
    }

    public Task<Void> add(User user){
        return dbRef.push().setValue(user);
    }
}
