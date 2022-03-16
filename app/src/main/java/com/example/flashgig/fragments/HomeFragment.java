package com.example.flashgig.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.flashgig.R;
import com.example.flashgig.activities.JobAdderActivity;
import com.example.flashgig.activities.JobRecyclerViewAdapter;
import com.example.flashgig.databinding.FragmentHomeBinding;
import com.example.flashgig.models.Job;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;


public class HomeFragment extends Fragment implements JobRecyclerViewAdapter.ItemClickListener{

    private FirebaseFirestore db;
    private ArrayList<Job> jobList = new ArrayList<>();
    private JobRecyclerViewAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        eventChangeListener();
    }


    private void eventChangeListener(){
        db.collection("jobs").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            if(error != null){
                Log.d("error", "Firebase error");
            }
            for(DocumentChange dc : value.getDocumentChanges()){
                if(dc.getType() == DocumentChange.Type.ADDED){
                    jobList.add(dc.getDocument().toObject(Job.class));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.recyclerViewJobs;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        adapter = new JobRecyclerViewAdapter(this.getContext(), jobList, this);
        recyclerView.setAdapter(adapter);

        binding.searchviewHome.setOnSearchClickListener(view -> binding.cardView.setVisibility(View.VISIBLE));
        binding.searchviewHome.setOnClickListener(view -> binding.searchviewHome.setIconified(false));
        binding.searchviewHome.setOnCloseListener(() -> {
            binding.cardView.setVisibility(View.GONE);
            return false;
        });

        ArrayAdapter<CharSequence> adapterLocationCity = ArrayAdapter.createFromResource(getContext(), R.array.locationCity, android.R.layout.simple_spinner_item);
        adapterLocationCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCity.setAdapter(adapterLocationCity);
//        initLocations(binding);
        DatePickerDialog datePickerDialog = getDatePickerDialog(binding.btnDatePicker);

        binding.btnDatePicker.setOnClickListener(view -> {
            datePickerDialog.show();
        });

        binding.floatingBtnAddJob.setOnClickListener(view -> {
            startActivity(new Intent(this.getContext(), JobAdderActivity.class));
        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void initLocations(FragmentHomeBinding binding) {

//        ArrayList<String> baranggayList = new ArrayList<>();
//        db.collection("locations").orderBy("baranggay", Query.Direction.ASCENDING).addSnapshotListener((value, error) ->{
//            for(DocumentChange dc: value.getDocumentChanges()){
//                if(dc.getType() == DocumentChange.Type.ADDED){
//                    baranggayList.add(dc.getDocument().getString("baranggay"));
////                    Toast.makeText(getContext(), dc.getDocument().getString("baranggay"), Toast.LENGTH_SHORT).show();
//                }
//            }
//            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, baranggayList);
//            binding.spinnerBaranggay.setAdapter(spinnerArrayAdapter);
//        });
    }

    private String getTodaysDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month =  month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private DatePickerDialog getDatePickerDialog(Button dateButton) {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            dateButton.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        return new DatePickerDialog(getContext(), style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year){
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month){
        switch (month){
            case 1: return "JAN";
            case 2: return "FEB";
            case 3: return "MAR";
            case 4: return "APR";
            case 5: return "MAY";
            case 6: return "JUN";
            case 7: return "JUL";
            case 8: return "AUG";
            case 9: return "SEP";
            case 10: return "OCT";
            case 11: return "NOV";
            default: return "DEC";
        }
    }

    @Override
    public void onItemClick(String jobId) {
//        Fragment fragment = DetailFragment.newInstance(job.getTitle());
        Fragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString("jobId", jobId);
        fragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, "jobDetail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}