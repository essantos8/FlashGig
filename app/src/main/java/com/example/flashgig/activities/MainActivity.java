package com.example.flashgig.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.flashgig.databinding.ActivityMainBinding;
import com.example.flashgig.R;
import com.example.flashgig.fragments.HomeFragment;
import com.example.flashgig.fragments.MessagesFragment;
import com.example.flashgig.fragments.MyJobsFragment;
import com.example.flashgig.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    static HashMap<String, Integer> menuOrder = new HashMap<String, Integer>(){{
        put("home", 1);
        put("messages", 2);
        put("myJobs", 3);
        put("profile", 4);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        replaceFragment(new HomeFragment(), "home", "LtoR");

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            Fragment curFragment = fragments.get(fragments.size()-1);
            String curFragmentTag = curFragment.getTag();
            assert curFragmentTag != null;

            switch (item.getItemId()) {
                case R.id.home:
                    if(!curFragmentTag.equals("home")) {
                        replaceFragment(new HomeFragment(), "home", "LtoR");
                    }
                    break;
                case R.id.messages:
                    if(!curFragmentTag.equals("messages")) {
                        if(!curFragmentTag.equals("home")){
                            replaceFragment(new MessagesFragment(), "messages", "LtoR");
                        }
                        else{
                            replaceFragment(new MessagesFragment(), "messages", "RtoL");
                        }
                    }
                    break;
                case R.id.myJobs:
                    if(!curFragmentTag.equals("myJobs")) {
                        if(!curFragmentTag.equals("profile")){
                            replaceFragment(new MyJobsFragment(), "myJobs", "RtoL");
                        }
                        else{
                            replaceFragment(new MyJobsFragment(), "myJobs", "LtoR");
                        }
                    }
                    break;
                case R.id.profile:
                    if(!curFragmentTag.equals("profile")) {
                        replaceFragment(new ProfileFragment(), "profile", "RtoL");
                    }
                    break;
            }
            return true;});
    }

    @Override
    public void onBackPressed () {
        SearchView searchBar = findViewById(R.id.searchviewHome);
        if(!searchBar.isIconified()){
            searchBar.setIconified(true);
            searchBar.onActionViewCollapsed();
            return;
        }
        this.moveTaskToBack(true);
    }

    private void replaceFragment(Fragment fragment, String tag, String animDirection){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Default anim is Left to Right
        Integer[] customAnimations = {
                R.anim.fade_in, //enter
                R.anim.slide_out_right, //exit
                R.anim.fade_in, //pop enter
                R.anim.slide_out_right //pop exit]
        };
        if(animDirection.equals("LtoR")){
            fragmentTransaction
                    .setCustomAnimations(R.anim.fade_in, //enter
                            R.anim.slide_out_right, //exit
                            R.anim.fade_in, //pop enter
                            R.anim.slide_out_right //pop exit
                    )
                    .replace(R.id.frameLayout, fragment, tag)
                    .commit();
        }
        else{
            fragmentTransaction
                    .setCustomAnimations(R.anim.fade_in, //enter
                            R.anim.slide_out_left, //exit
                            R.anim.fade_in, //pop enter
                            R.anim.slide_out_left //pop exit
                    )
                    .replace(R.id.frameLayout, fragment, tag)
                    .commit();
        }
    }

}