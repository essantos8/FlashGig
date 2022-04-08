package com.example.flashgig.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashgig.R;
import com.example.flashgig.activities.JobRecyclerViewAdapter;
import com.example.flashgig.databinding.FragmentHomeBinding;
import com.example.flashgig.models.Job;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;


public class HomeFragment extends Fragment implements JobRecyclerViewAdapter.ItemClickListener {

    private FirebaseFirestore db;
    private ArrayList<Job> jobList = new ArrayList<>();

    FragmentHomeBinding binding;

    private JobRecyclerViewAdapter adapter;
    private SearchView searchView;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        jobList.clear();
    }

    private void eventChangeListener() {
//        final ProgressDialog pd = new ProgressDialog(getContext());
//        pd.setTitle("Loading...");
//        pd.show();
        ProgressBar pb = binding.progressBarHome;
        AtomicBoolean firstRun = new AtomicBoolean(true);
        db.collection("jobs").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            if (error != null || value.isEmpty()) {
                Log.d("error", "Firebase error");
                Toast.makeText(getContext(), "Database Error!", Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);
                return;
            }
            for (DocumentChange dc : value.getDocumentChanges()) {
                Job newJob = dc.getDocument().toObject(Job.class);
                if(dc.getType() == DocumentChange.Type.ADDED){
                    if(firstRun.get()) jobList.add(newJob);
                    else jobList.add(0, newJob);
                }
                else if(dc.getType() == DocumentChange.Type.REMOVED){
                    jobList.remove(newJob);
                }
/*
                else{
                    int oldIndex = jobList.indexOf(newJob);
                    jobList.remove(newJob);
                    jobList.add(oldIndex,newJob);
                }*/
            }
            firstRun.set(false);
            adapter = new JobRecyclerViewAdapter(this.getContext(), jobList, this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            pb.setVisibility(View.GONE);
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        FloatingActionButton fltBtnAddJob = binding.floatingBtnAddJob;

        fltBtnAddJob.setOnClickListener(view -> {
            FragmentTransaction fragment = getActivity().getSupportFragmentManager().beginTransaction();
            fragment.replace(R.id.frameLayout, new JobAdderFragment());
            fragment.commit();
        });


        recyclerView = binding.recyclerViewJobs;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(false);

        eventChangeListener();


        searchView = binding.searchviewHome;

        searchView.setOnSearchClickListener(view -> binding.cardView.setVisibility(View.VISIBLE));
        searchView.setOnClickListener(view -> binding.searchviewHome.setIconified(false));
        searchView.setOnCloseListener(() -> {
            binding.cardView.setVisibility(View.GONE);
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        setChipListeners();

        ArrayAdapter<CharSequence> adapterLocationCity = ArrayAdapter.createFromResource(getContext(), R.array.locationCity, android.R.layout.simple_spinner_item);
        adapterLocationCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCity.setAdapter(adapterLocationCity);
//        initLocations(binding);
        DatePickerDialog datePickerDialog = getDatePickerDialog(binding.btnDatePicker);

        binding.btnDatePicker.setOnClickListener(view -> {
            datePickerDialog.show();
        });

//        binding.floatingBtnAddJob.setOnClickListener(view -> {
            // add job fragment here
//            startActivity(new Intent(this.getContext(), JobAdderActivity.class));
//        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void setChipListeners() {
        binding.chipCarpentry.setOnCheckedChangeListener((compoundButton, b) ->
                adapter.toggleCategoryFilter("Carpentry", b));
        binding.chipPlumbing.setOnCheckedChangeListener((compoundButton, b) ->
                adapter.toggleCategoryFilter("Plumbing", b));
        binding.chipElectrical.setOnCheckedChangeListener((compoundButton, b) ->
                adapter.toggleCategoryFilter("Electrical", b));
        binding.chipElectronics.setOnCheckedChangeListener((compoundButton, b) ->
                adapter.toggleCategoryFilter("Electronics", b));
        binding.chipPersonalShopping.setOnCheckedChangeListener((compoundButton, b) ->
                adapter.toggleCategoryFilter("Shopping", b));
        binding.chipVirtualAssistant.setOnCheckedChangeListener((compoundButton, b) ->
                adapter.toggleCategoryFilter("Assistant", b));
        binding.chipOther.setOnCheckedChangeListener((compoundButton, b) ->
                adapter.toggleCategoryFilter("Other", b));
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
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

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        switch (month) {
            case 1:
                return "JAN";
            case 2:
                return "FEB";
            case 3:
                return "MAR";
            case 4:
                return "APR";
            case 5:
                return "MAY";
            case 6:
                return "JUN";
            case 7:
                return "JUL";
            case 8:
                return "AUG";
            case 9:
                return "SEP";
            case 10:
                return "OCT";
            case 11:
                return "NOV";
            default:
                return "DEC";
        }
    }

    @Override
    public void onItemClick(String JID) {
        Fragment fragment = DetailFragment.newInstance(JID);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, "jobDetail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}