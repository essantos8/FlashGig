package com.example.flashgig.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.flashgig.activities.VPAdapter;
import com.example.flashgig.databinding.FragmentMyJobsBinding;
import com.google.android.material.tabs.TabLayout;


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
        vpAdapter.addFragment(new PostedFragment(),"Posted");
        vpAdapter.addFragment(new AppliedFragment(),"Applied");
        viewPager.setAdapter(vpAdapter);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}