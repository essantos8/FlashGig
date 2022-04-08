package com.example.flashgig.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.flashgig.R;
import com.example.flashgig.databinding.ActivityMain2Binding;
import com.example.flashgig.databinding.ActivityMainBinding;
import com.example.flashgig.fragments.BiddersFragment;

public class MainActivity2 extends AppCompatActivity {

    BiddersFragment biddersFragment = new BiddersFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMain2Binding binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout2, fragment, tag).addToBackStack(null).commit();
    }
}
