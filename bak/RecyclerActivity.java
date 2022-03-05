package com.example.flashgig;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.flashgig.databinding.ActivityRecyclerBinding;

import java.util.ArrayList;
import java.util.List;

public class RecyclerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<User> userArrayList;
    UserAdapter userAdapter;
    String names[];
    String emails[];
    String phones[];
    String ratings[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityRecyclerBinding binding = ActivityRecyclerBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setHasFixedSize(true);

        userArrayList = new ArrayList<User>();

        userAdapter = new UserAdapter(this, userArrayList);
        recyclerView.setAdapter(userAdapter);

        names = new String[]{"a", "b", "c"};
        emails = new String[]{"a", "b", "c"};
        phones = new String[]{"1", "2", "3"};
//        rating = new String[]{"1", "2", "3"};
        getData();

    }

    private void getData() {

        for(int i=0; i<names.length; i++){
            User user = new User(names[i], emails[i], phones[i]);
            userArrayList.add(user);
        }
        userAdapter.notifyDataSetChanged();
    }
}