package com.example.flashgig.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashgig.R;
import com.example.flashgig.models.Job;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class PARecyclerViewAdapter extends RecyclerView.Adapter<PARecyclerViewAdapter.MyViewHolder> implements Filterable {
    private Context ctx;
    private ArrayList<Job> fullJobArrayList, jobArrayList;
    private ItemClickListener clickListener;
    private ArrayList<String> categoryFilters = new ArrayList<>();

    public PARecyclerViewAdapter(Context ctx, ArrayList<Job> jobArrayList, ItemClickListener clickListener) {
        this.ctx = ctx;
        this.fullJobArrayList = new ArrayList<>(jobArrayList);
        this.jobArrayList = jobArrayList;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public PARecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.posted_recycler_view_row, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PARecyclerViewAdapter.MyViewHolder holder, int position) {
        // assign values for each view holder as they come on screen
        // based on position of recycler view
        Job curJob = jobArrayList.get(position);

        holder.textViewTitle.setText(curJob.getTitle());
        holder.textViewStatus.setText(curJob.getStatus().toUpperCase(Locale.ROOT));
        holder.textViewDescription.setText(curJob.getDescription());

        holder.chipCarpentry.setVisibility(View.GONE);
        holder.chipPlumbing.setVisibility(View.GONE);
        holder.chipElectrical.setVisibility(View.GONE);
        holder.chipElectronics.setVisibility(View.GONE);
        holder.chipPersonalShopping.setVisibility(View.GONE);
        holder.chipVirtualAssistant.setVisibility(View.GONE);
        holder.chipOther.setVisibility(View.GONE);

        for (String category : curJob.getCategories()) {
            switch (category) {
                case "Carpentry":
                    holder.chipCarpentry.setVisibility(View.VISIBLE);
                    break;
                case "Plumbing":
                    holder.chipPlumbing.setVisibility(View.VISIBLE);
                    break;
                case "Electrical":
                    holder.chipElectrical.setVisibility(View.VISIBLE);
                    break;
                case "Electronics":
                    holder.chipElectronics.setVisibility(View.VISIBLE);
                    break;
                case "Personal Shopping":
                case "Shopping":
                    holder.chipPersonalShopping.setVisibility(View.VISIBLE);
                    break;
                case "Virtual Assistant":
                case "Assistant":
                    holder.chipVirtualAssistant.setVisibility(View.VISIBLE);
                    break;
                case "Other":
                    holder.chipOther.setVisibility(View.VISIBLE);
                    break;
            }
        }

        holder.textViewDate.setText(curJob.getDate());
        holder.textViewWorkers.setText(String.valueOf(curJob.getWorkers().size()));
        holder.textViewBudget.setText(curJob.getBudget());

        holder.jobCard.setOnClickListener(view -> clickListener.onItemClick(curJob.jobId, curJob.getStatus()));
    }

    @Override
    public int getItemCount() {
        // number of items in total
        return jobArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return jobTextFilter;
    }


    private final Filter jobTextFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Job> filteredJobArrayList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0) {
//                Toast.makeText(ctx, "", Toast.LENGTH_SHORT).show();
                filteredJobArrayList.addAll(fullJobArrayList);
            }
            else{
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(Job job : fullJobArrayList){
                    if(job.getTitle().toLowerCase().contains(filterPattern) || job.getDescription().toLowerCase().contains(filterPattern)){
                        filteredJobArrayList.add(job);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredJobArrayList;
            results.count = filteredJobArrayList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            setCategoryFilters((ArrayList<Job>) filterResults.values);
//            jobArrayList.clear();
//            jobArrayList.addAll((ArrayList) filterResults.values);
//            notifyDataSetChanged();
        }
    };

    public void toggleCategoryFilter(String category, boolean addCategory) {
        if(addCategory){
            categoryFilters.add(category);
        }
        else{
            categoryFilters.remove(category);
        }
        setCategoryFilters(fullJobArrayList);
    }

    private void setCategoryFilters(@Nullable ArrayList<Job> textFilteredJobArrayList){
        // possibly redundant
        if(textFilteredJobArrayList == null){
            textFilteredJobArrayList = fullJobArrayList;
        }

        ArrayList<Job> filteredJobArrayList = new ArrayList<>();

        if(categoryFilters.isEmpty()){
            filteredJobArrayList.addAll(textFilteredJobArrayList);
        }
        else{
            Log.d("categories", "notall");
            for (String cat: categoryFilters){
                Log.d("categories", "toggleCategoryFilter: "+cat);
            }
            for (Job job : textFilteredJobArrayList) {
                // "and" filter
//                if (job.getCategories().containsAll(categoryFilters))
//                    filteredJobArrayList.add(job);

                // "or" filter
                if(!Collections.disjoint(job.getCategories(), categoryFilters))
                    filteredJobArrayList.add(job);
            }
        }
        jobArrayList.clear();
        jobArrayList.addAll(filteredJobArrayList);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // grabbing the views from the row layout file
        // similar with oncreate
        TextView textViewTitle, textViewStatus, textViewDescription, textViewDate, textViewBudget, textViewWorkers;
        Chip chipCarpentry, chipPlumbing, chipElectronics, chipElectrical, chipPersonalShopping, chipVirtualAssistant, chipOther;
        CardView jobCard;
        //Button btnBidderAccess;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textJobTitle);
            textViewDate = itemView.findViewById(R.id.textJobDate);
            textViewWorkers = itemView.findViewById(R.id.textJobWorkers);
            textViewBudget = itemView.findViewById(R.id.textJobBudget);
            chipCarpentry = itemView.findViewById(R.id.chipCarpentry);
            chipPlumbing = itemView.findViewById(R.id.chipPlumbing);
            chipElectronics = itemView.findViewById(R.id.chipElectronics);
            chipElectrical = itemView.findViewById(R.id.chipElectrical);
            chipPersonalShopping = itemView.findViewById(R.id.chipPersonalShopping);
            chipVirtualAssistant = itemView.findViewById(R.id.chipVirtualAssistant);
            chipOther = itemView.findViewById(R.id.chipOther);
            jobCard = itemView.findViewById(R.id.jobCardPopup);
            textViewStatus = itemView.findViewById(R.id.textViewStatus2);
            textViewDescription = itemView.findViewById(R.id.textJobDescription);

            /*itemView.findViewById(R.id.btnBidderAccess).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout2, fragment, "displayBidder");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });*/
        }
    }

    public interface ItemClickListener {
        public void onItemClick(String jobId, String status);
    }

}
