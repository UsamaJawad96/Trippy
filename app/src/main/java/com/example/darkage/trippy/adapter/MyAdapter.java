package com.example.darkage.trippy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.darkage.trippy.R;
import com.example.darkage.trippy.TripClass;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<TripClass> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tName,tAdmin,tDate,tBuddies;
        public ViewHolder(View v) {
            super(v);
            tName = v.findViewById(R.id.trip_name);
            tDate = v.findViewById(R.id.trip_date);
            tAdmin = v.findViewById(R.id.trip_admin);
            tBuddies = v.findViewById(R.id.trip_buddies);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<TripClass> tc) {
        mDataset = tc;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        ///...
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.tAdmin.setText(mDataset.get(position).getTrip_admin_name());
        holder.tDate.setText(mDataset.get(position).getDate());
        holder.tBuddies.setText(Integer.toString(mDataset.get(position).getTotal_buddies()));
        holder.tName.setText(mDataset.get(position).getTrip_name());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
