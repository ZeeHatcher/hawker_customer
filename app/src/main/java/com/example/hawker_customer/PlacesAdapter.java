package com.example.hawker_customer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder> {

    public interface OnPlaceSelectListener {
        void onPlaceSelect(Place place);
    }

    private static final String TAG = "PlacesAdapter";
    private ArrayList<Place> places;
    private Context context;

    public PlacesAdapter(ArrayList<Place> places) {
        this.places = places;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_place, parent, false);

        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        final Place place = places.get(position);

        holder.tvName.setText(place.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, place.toString());
                ((OnPlaceSelectListener) context).onPlaceSelect(place);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.place);
        }
    }
}
