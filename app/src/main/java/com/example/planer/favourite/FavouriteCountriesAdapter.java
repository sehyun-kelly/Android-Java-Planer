package com.example.planer.favourite;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.example.planer.databinding.FragmentFavouriteItemBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class FavouriteCountriesAdapter extends RecyclerView.Adapter<FavouriteCountriesAdapter.ViewHolder> {

    private final ArrayList<FavouriteCountry> countryList;
    private final ArrayList<FavouriteCountry> initialCountryList = new ArrayList<>();
    private final FavouriteCallbackListener favouriteCallbackListener;
    private int sortOrder = 1;

    public FavouriteCountriesAdapter(ArrayList<FavouriteCountry> items, FavouriteCallbackListener favouriteCallbackListener) {
        this.countryList = items;
        this.favouriteCallbackListener = favouriteCallbackListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentFavouriteItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.contentView.setOnClickListener(v -> {
            favouriteCallbackListener.onFavouritePairClick(countryList.get(position).name);
        });
        holder.countryPair.setText(countryList.get(position).name);
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    public void notifyDataLoaded() {
        notifyDataSetChanged();
        this.initialCountryList.addAll(countryList);
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<FavouriteCountry> filteredList = new ArrayList<>();
                if (constraint == null || constraint.toString().isEmpty()) {
                    filteredList.addAll(initialCountryList);
                } else {
                    String query = constraint.toString().trim().toLowerCase();
                    for (FavouriteCountry country : initialCountryList) {
                        if (country.name.toLowerCase(Locale.ROOT).contains(query)) {
                            filteredList.add(country);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                countryList.clear();
                countryList.addAll((ArrayList<FavouriteCountry>) results.values);
                sortFavouritePair(sortOrder);
            }
        };
    }

    public void sortFavouritePair(int order) {
        sortOrder = order;

        switch (order) {
            case 1:
                countryList.sort((o1, o2) -> o1.name.compareTo(o2.name));
                break;
            case 2:
                countryList.sort((o1, o2) -> o2.name.compareTo(o1.name));
                break;
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final CardView contentView;
        public final TextView countryPair;

        public ViewHolder(FragmentFavouriteItemBinding binding) {
            super(binding.getRoot());
            contentView = binding.favCard;
            countryPair = binding.countryName;
        }
    }
}
