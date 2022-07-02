package com.example.flashgig.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashgig.adapters.UserRecyclerViewAdapter;
import com.example.flashgig.databinding.ActivityUsersListBinding;
import com.example.flashgig.listeners.UserListener;
import com.example.flashgig.models.User;
import com.example.flashgig.utilities.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity implements UserListener{
    private ActivityUsersListBinding binding;
    private FirebaseUser curUser;
    private UserRecyclerViewAdapter userRecyclerViewAdapter, filteredAdapter;
    private ArrayList<String> categoryFilters = new ArrayList<>();
    private RecyclerView recyclerView;
    private ArrayList<User> users = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.usersRecyclerView;
        recyclerView.setItemViewCacheSize(50);
        curUser = FirebaseAuth.getInstance().getCurrentUser();
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        getUsers();
        setChipListeners();

        binding.userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                userRecyclerViewAdapter.getFilter().filter(s);
                if(filteredAdapter != null){
                    filteredAdapter.getFilter().filter(s);
                }
                return false;
            }
        });

        binding.btnFilterUsers.setOnClickListener(view -> {
            switch(binding.cardViewUserFilters.getVisibility()){
                case View.GONE:
                    binding.cardViewUserFilters.setVisibility(View.VISIBLE);
                    break;
                case View.VISIBLE:
                    binding.cardViewUserFilters.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        });

        binding.btnShowResults.setOnClickListener(view -> {
            setCategoryFilters();
        });
    }


    private void toggleCategoryFilter(String category, boolean unused){
        if(categoryFilters.contains(category)){
            categoryFilters.remove(category);
        }
        else{
            categoryFilters.add(category);
        }
        setCategoryFilters();
    }

    private void setCategoryFilters() {
        if(categoryFilters.isEmpty()){
            recyclerView.setAdapter(userRecyclerViewAdapter);
            return;
        }
        ArrayList<User> filteredUserList = new ArrayList<>();
        filteredAdapter = new UserRecyclerViewAdapter(this, filteredUserList, this);
        for(User user: users){
            for(String category: categoryFilters){
                Log.d("yep", user.getSkills().toString());
                if(user.getSkills().contains(category)){
                    filteredUserList.add(user);
                    break;
                }
            }
        }
        filteredAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(filteredAdapter);
    }

    private void setChipListeners() {
        binding.chipCarpentry.setOnCheckedChangeListener((compoundButton, b) ->
                toggleCategoryFilter("Carpentry", b));
        binding.chipPlumbing.setOnCheckedChangeListener((compoundButton, b) ->
                toggleCategoryFilter("Plumbing", b));
        binding.chipElectrical.setOnCheckedChangeListener((compoundButton, b) ->
                toggleCategoryFilter("Electrical", b));
        binding.chipElectronics.setOnCheckedChangeListener((compoundButton, b) ->
                toggleCategoryFilter("Electronics", b));
        binding.chipPersonalShopping.setOnCheckedChangeListener((compoundButton, b) ->
                toggleCategoryFilter("Shopping", b));
        binding.chipVirtualAssistant.setOnCheckedChangeListener((compoundButton, b) ->
                toggleCategoryFilter("Assistant", b));
        binding.chipOther.setOnCheckedChangeListener((compoundButton, b) ->
                toggleCategoryFilter("Others", b));
    }

    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task ->{
                    loading(false);
                    String currentUserId = curUser.getUid();//preferenceManager.getString();
                    if(task.isSuccessful() && task.getResult() != null){
                        users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            User user = queryDocumentSnapshot.toObject(User.class);
                            users.add(user);
                        }
                        if(users.size() > 0) {
                            userRecyclerViewAdapter = new UserRecyclerViewAdapter(this, users, this);
                            binding.usersRecyclerView.setAdapter(userRecyclerViewAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }

                });

    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s"), TextView.BufferType.valueOf("No User available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        } else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}