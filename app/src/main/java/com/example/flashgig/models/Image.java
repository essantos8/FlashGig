package com.example.flashgig.models;

import android.net.Uri;

public class Image {
    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageId(Uri imageUri) {
        this.imageUri = imageUri;
    }

    private Uri imageUri;

    public Image(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
