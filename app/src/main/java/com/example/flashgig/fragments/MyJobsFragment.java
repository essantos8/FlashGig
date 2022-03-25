package com.example.flashgig.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.flashgig.R;
import com.example.flashgig.activities.JobRecyclerViewAdapter;
import com.example.flashgig.activities.VPAdapter;
import com.example.flashgig.databinding.FragmentMyJobsBinding;
import com.example.flashgig.models.Job;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class MyJobsFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentMyJobsBinding binding = FragmentMyJobsBinding.inflate(inflater, container, false);

        tabLayout = binding.jobtab;
        viewPager = binding.viewPager;

        tabLayout.setupWithViewPager(viewPager);

        VPAdapter vpAdapter = new VPAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new PendingJobsFragment(),"Pending");
        vpAdapter.addFragment(new InProgressJobsFragment(),"In Progress");
        vpAdapter.addFragment(new CompletedJobsFragment(),"Completed");
        viewPager.setAdapter(vpAdapter);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}