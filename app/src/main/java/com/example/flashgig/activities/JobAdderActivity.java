package com.example.flashgig.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.flashgig.R;
import com.example.flashgig.databinding.ActivityJobAdderBinding;
import com.example.flashgig.models.Job;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class JobAdderActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ActivityJobAdderBinding binding;

    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    private TextInputEditText tietTitle, tietDescription;
    private Spinner spinnerWorkers, spinnerLocation;
    private EditText etMin, etMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJobAdderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        Spinner spinner_noOfWorkers = findViewById(R.id.spinner_noOfWorkers);
        ArrayAdapter<CharSequence> adapter_noOfWorkers = ArrayAdapter.createFromResource(this, R.array.numberOfWorkers, android.R.layout.simple_spinner_item);
        adapter_noOfWorkers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_noOfWorkers.setAdapter(adapter_noOfWorkers);
        spinner_noOfWorkers.setOnItemSelectedListener(this);

        Spinner spinner_locationCity = findViewById(R.id.spinner_location);
        ArrayAdapter<CharSequence> adapter_locationCity = ArrayAdapter.createFromResource(this, R.array.locationCity, android.R.layout.simple_spinner_item);
        adapter_locationCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_locationCity.setAdapter(adapter_locationCity);
        spinner_locationCity.setOnItemSelectedListener(this);


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

        binding.buttonPostJob.setOnClickListener(view -> {
            addJob();
        });

    }

    private void addJob() {
        String title = tietTitle.getText().toString(),
                description = tietDescription.getText().toString();
        ArrayList<String> categories = new ArrayList<>();
        for (int i = 0; i < binding.chipGroupJobCateg.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroupJobCateg.getChildAt(i);
            if(chip.isChecked()){
                switch (i){
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


        if(title.isEmpty()){
            tietTitle.setError("Job Title is required!");
            return;
        }
        if(description.isEmpty()){
            tietDescription.setError("Description is required!");
            return;
        }

        DocumentReference doc = db.collection("jobs").document();
        Job job = new Job(title, description, mAuth.getCurrentUser().getEmail(), dateButton.getText().toString(), categories, spinnerWorkers.getSelectedItemPosition()+1, spinnerLocation.getSelectedItem().toString(), etMin.getText().toString()+etMax.getText().toString(), doc.getId());
        doc.set(job);

        HashMap<String, Object> timestamp = new HashMap<String, Object>();
        timestamp.put("timestamp", FieldValue.serverTimestamp());
        doc.update(timestamp);
        Toast.makeText(this, "Job Added to Database", Toast.LENGTH_SHORT).show();
        finish();
    }

    private String getTodaysDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month =  month + 1;
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

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year){
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month){
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        return "JAN";
    }

    public void openDatePicker(View view){
        datePickerDialog.show();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}