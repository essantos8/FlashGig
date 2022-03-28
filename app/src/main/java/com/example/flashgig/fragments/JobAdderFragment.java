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

import com.example.flashgig.R;
import com.example.flashgig.databinding.FragmentJobAdderBinding;
import com.example.flashgig.models.Job;
import com.google.android.material.chip.Chip;
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




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentJobAdderBinding.inflate(inflater, container, false);
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
//        dateButton = findViewById(R.id.btnDatePicker);

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

        ArrayList<String> jobImages = new ArrayList<>();

        DocumentReference doc = db.collection("jobs").document();
        Job job = new Job(title, description, mAuth.getCurrentUser().getEmail(), dateButton.getText().toString(), categories, spinnerWorkers.getSelectedItemPosition() + 1, spinnerLocation.getSelectedItem().toString(), etMin.getText().toString() + "-" + etMax.getText().toString(), doc.getId(), jobImages);
        doc.set(job);

        HashMap<String, Object> timestamp = new HashMap<String, Object>();
        timestamp.put("timestamp", FieldValue.serverTimestamp());
        doc.update(timestamp);
        Toast.makeText(getContext(), "Job Added to Database", Toast.LENGTH_SHORT).show();
        getActivity().getSupportFragmentManager().popBackStackImmediate();
//        finish();
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