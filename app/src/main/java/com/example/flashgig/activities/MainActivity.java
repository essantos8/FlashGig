package com.example.flashgig.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.flashgig.databinding.ActivityMainBinding;
import com.example.flashgig.R;
import com.example.flashgig.databinding.FragmentProfileBinding;
import com.example.flashgig.fragments.HomeFragment;
import com.example.flashgig.fragments.MessagesFragment;
import com.example.flashgig.fragments.MyJobsFragment;
import com.example.flashgig.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    ImageButton back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        replaceFragment(new HomeFragment());

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.messages:
                    replaceFragment(new MessagesFragment());
                    break;
                case R.id.myJobs:
                    replaceFragment(new MyJobsFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }
            return true;});
    }


    @Override
    public void onBackPressed () {

        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
        SearchView searchBar = findViewById(R.id.searchviewHome);
        if(!searchBar.isIconified()){
            searchBar.setIconified(true);
            searchBar.onActionViewCollapsed();
            return;
        }
//        this.moveTaskToBack(true);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

}