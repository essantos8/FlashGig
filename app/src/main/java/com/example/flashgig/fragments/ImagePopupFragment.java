package com.example.flashgig.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.flashgig.GlideApp;
import com.example.flashgig.databinding.FragmentImagePopupBinding;
import com.github.chrisbanes.photoview.PhotoView;


public class ImagePopupFragment extends Fragment {
    private FragmentImagePopupBinding binding;
    private PhotoView imageView;
    private RelativeLayout relativeLayout;
    private Uri imageUri;


    public ImagePopupFragment(Uri imageUri){
        this.imageUri = imageUri;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentImagePopupBinding.inflate(inflater, container, false);
        imageView = binding.imageViewPopup;
        relativeLayout = binding.RelativeLayout;

        binding.imageViewPopup.setOnOutsidePhotoTapListener(view -> {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        });

        GlideApp.with(getContext())
                .load(imageUri)
                .into(imageView);
        return binding.getRoot();
    }

}