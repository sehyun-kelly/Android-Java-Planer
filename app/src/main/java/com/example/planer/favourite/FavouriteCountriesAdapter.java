package com.example.planer.favourite;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.planer.databinding.FragmentFavouriteItemBinding;

import java.util.ArrayList;

public class FavouriteCountriesAdapter extends RecyclerView.Adapter<FavouriteCountriesAdapter.ViewHolder> {

    private final ArrayList<FavouriteCountry> countryList;
    private FavouriteCallbackListener favouriteCallbackListener;

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
