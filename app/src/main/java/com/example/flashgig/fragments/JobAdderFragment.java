package com.example.flashgig.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.flashgig.GlideApp;
import com.example.flashgig.R;
import com.example.flashgig.databinding.FragmentJobAdderBinding;
import com.example.flashgig.databinding.FragmentProfileEditBinding;
import com.example.flashgig.models.Job;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;


public class JobAdderFragment extends Fragment{
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private FragmentJobAdderBinding binding;

    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private TextInputEditText tietTitle, tietDescription;
    private Spinner spinnerWorkers, spinnerLocation;
    private EditText etMin, etMax;

    private ImageView jobPicture1;
    public Uri imageUri1;
    private ImageView jobPicture2;
    public Uri imageUri2;
    private ImageView jobPicture3;
    public Uri imageUri3;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private final Integer getPicRC1 = 100;
    private final Integer getPicRC2 = 101;
    private final Integer getPicRC3 = 102;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

    }

    private void choosePicture1(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, getPicRC1);
    }

    private void choosePicture2(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, getPicRC2);
    }

    private void choosePicture3(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, getPicRC3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==getPicRC1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri1 = data.getData();
//            GlideApp.with(this)
//                    .load(imageUri1)
//                    .into(binding.jobImage1);
            binding.jobImage1.setImageURI(imageUri1);
        }
        if(requestCode==getPicRC2 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri2 = data.getData();
//            GlideApp.with(this)
//                    .load(imageUri1)
//                    .into(binding.jobImage2);
            binding.jobImage2.setImageURI(imageUri2);
        }
        if(requestCode==getPicRC3 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri3 = data.getData();
//            GlideApp.with(this)
//                    .load(imageUri1)
//                    .into(binding.jobImage3);
            binding.jobImage3.setImageURI(imageUri3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentJobAdderBinding.inflate(inflater, container, false);

        jobPicture1 = binding.jobImage1;
        jobPicture1.setOnClickListener(view -> choosePicture1());

        jobPicture2 = binding.jobImage2;
        jobPicture2.setOnClickListener(view -> choosePicture2());

        jobPicture3 = binding.jobImage3;
        jobPicture3.setOnClickListener(view -> choosePicture3());

        Spinner spinner_noOfWorkers = binding.spinnerNoOfWorkers;
        ArrayAdapter<CharSequence> adapter_noOfWorkers = ArrayAdapter.createFromResource(getActivity(), R.array.numberOfWorkers, android.R.layout.simple_spinner_item);
        adapter_noOfWorkers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_noOfWorkers.setAdapter(adapter_noOfWorkers);
        spinner_noOfWorkers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Spinner spinner_locationCity = binding.spinnerLocation;
        ArrayAdapter<CharSequence> adapter_locationCity = ArrayAdapter.createFromResource(getActivity(), R.array.locationCity, android.R.layout.simple_spinner_item);
        adapter_locationCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_locationCity.setAdapter(adapter_locationCity);
        spinner_locationCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        initDatePicker();

        tietTitle = binding.tietJobTitle;
        tietDescription = binding.tietJobDesc;
        spinnerWorkers = binding.spinnerNoOfWorkers;
        spinnerLocation = binding.spinnerLocation;
        dateButton = binding.btnDatePicker;
        etMin = binding.editTextMinAmt;
        etMax = binding.editTextMaxAmt;
        dateButton.setText(getTodaysDate());
        dateButton.setOnClickListener(view -> {
            datePickerDialog.show();
        });
        binding.buttonPostJob.setOnClickListener(view -> {
            addJob();
        });
        return binding.getRoot();
    }

    private void addJob() {
        String title = tietTitle.getText().toString(), description = tietDescription.getText().toString();

        ArrayList<String> categories = new ArrayList<>();
        for (int i = 0; i < binding.chipGroupJobCateg.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroupJobCateg.getChildAt(i);
            if (chip.isChecked()) {
                switch (i) {
                    case 0:
                        categories.add("Carpentry");
                        break;
                    case 1:
                        categories.add("Plumbing");
                        break;
                    case 2:
                        categories.add("Electrical");
                        break;
                    case 3:
                        categories.add("Electronics");
                        break;
                    case 4:
                        categories.add("Shopping");
                        break;
                    case 5:
                        categories.add("Assistant");
                        break;
                    case 6:
                        categories.add("Others");
                        break;
                }
            }
        }

        if (title.isEmpty()) {
            tietTitle.setError("Job Title is required!");
            return;
        }
        if (description.isEmpty()) {
            tietDescription.setError("Description is required!");
            return;
        }
        //uploadPicture();
        ArrayList<String> jobImages = new ArrayList<>();

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Uploading...");

        if (imageUri1 != null) {
            pd.show();
            final String randomKey1 = UUID.randomUUID().toString();
            jobImages.add(randomKey1);
            StorageReference imageRef1 = storageReference.child("media/images/addjob_pictures/" + randomKey1);

            imageRef1.putFile(imageUri1).addOnSuccessListener(taskSnapshot -> {
                Log.d("Cloud Storage", "Image uploaded!");
                pd.dismiss();
            }).addOnFailureListener(e -> {
                Log.d("Cloud Storage", "Error uploading image!");
                pd.dismiss();
            }).addOnProgressListener(snapshot -> {
                double progress = 100.0 * ((double) snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Uploading Image 1: " + Math.round(progress) + "%");
            });
        }

        if (imageUri2 != null) {
            pd.show();
            final String randomKey2 = UUID.randomUUID().toString();
            jobImages.add(randomKey2);
            StorageReference imageRef2 = storageReference.child("media/images/addjob_pictures/" + randomKey2);

            imageRef2.putFile(imageUri2).addOnSuccessListener(taskSnapshot -> {
                Log.d("Cloud Storage", "Image uploaded!");
                pd.dismiss();
            }).addOnFailureListener(e -> {
                Log.d("Cloud Storage", "Error uploading image!");
                pd.dismiss();
            }).addOnProgressListener(snapshot -> {
                double progress = 100.0 * ((double) snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Uploading Image 2: " + Math.round(progress) + "%");
            });
        }

        if (imageUri3 != null) {
            pd.show();
            final String randomKey3 = UUID.randomUUID().toString();
            jobImages.add(randomKey3);
            StorageReference imageRef3 = storageReference.child("media/images/addjob_pictures/" + randomKey3);

            imageRef3.putFile(imageUri3).addOnSuccessListener(taskSnapshot -> {
                Log.d("Cloud Storage", "Image uploaded!");
                pd.dismiss();
            }).addOnFailureListener(e -> {
                Log.d("Cloud Storage", "Error uploading image!");
                pd.dismiss();
            }).addOnProgressListener(snapshot -> {
                double progress = 100.0 * ((double) snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Uploading Image 3: " + Math.round(progress) + "%");
            });
        }


        DocumentReference doc = db.collection("jobs").document();
        Job job = new Job(title, description, mAuth.getCurrentUser().getEmail(), dateButton.getText().toString(), categories, spinnerWorkers.getSelectedItemPosition() + 1, spinnerLocation.getSelectedItem().toString(), etMin.getText().toString() + "-" + etMax.getText().toString(), doc.getId(), jobImages);
        doc.set(job);

        HashMap<String, Object> timestamp = new HashMap<String, Object>();
        timestamp.put("timestamp", FieldValue.serverTimestamp());
        doc.update(timestamp);
//        Toast.makeText(getContext(), "Job Added to Database", Toast.LENGTH_SHORT).show();
        Snackbar.make(getActivity().findViewById(R.id.frameLayout), "Job Added!", Snackbar.LENGTH_SHORT).show();

        FragmentTransaction fragment = getActivity().getSupportFragmentManager().beginTransaction();
        fragment.replace(R.id.frameLayout, new HomeFragment(), "home");
        fragment.commit();

    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(getContext(), style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        return "JAN";
    }
}