package com.example.planer.favourite;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.planer.databinding.FragmentFavouriteItemBinding;

import java.util.ArrayList;

public class FavouriteCountriesAdapter extends RecyclerView.Adapter<FavouriteCountriesAdapter.ViewHolder> {

    private final ArrayList<FavouriteCountry> countryList;

    public FavouriteCountriesAdapter(ArrayList<FavouriteCountry> items) {
        countryList = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentFavouriteItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = countryList.get(position);
        holder.mContentView.setText(countryList.get(position).name);
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mContentView;
        public FavouriteCountry mItem;

        public ViewHolder(FragmentFavouriteItemBinding binding) {
            super(binding.getRoot());
            mContentView = binding.countryName;
        }
    }
}
