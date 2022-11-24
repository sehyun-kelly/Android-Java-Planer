package com.example.planer.favourite;

public interface FavouriteCallbackListener {
    void updateFavouritePair(final String passport, final String destination);

    void onFavouritePairClick(final String countryPair);
}
