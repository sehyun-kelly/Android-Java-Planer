package com.example.planer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.View;

public class SearchFragment extends Fragment {

    public SearchFragment() {
        super(R.layout.fragment_search);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // OnClickListeners to switch to new activities when clicking cards (Visa, Covid, Weather)
        CardView visa = requireView().findViewById(R.id.visaCard);
        visa.setOnClickListener(this::gotoVisa);

        CardView travelRestrictions = requireView().findViewById(R.id.travelRestrictionsCard);
        travelRestrictions.setOnClickListener(this::gotoRestrictions);

        CardView weather = requireView().findViewById(R.id.weatherCard);
        weather.setOnClickListener(this::gotoWeather);

        CardView currency = requireView().findViewById(R.id.currencyCard);
        currency.setOnClickListener(this::gotoCurrency);

    }

    public void gotoWeather(View view) {
        Intent intent = new Intent(getActivity(), WeatherActivity.class);
        startActivity(intent);
    }

    public void gotoCurrency(View view) {
        Intent intent = new Intent(getActivity(), CurrencyConverterActivity.class);
        startActivity(intent);
    }

    public void gotoVisa(View view) {
        Intent intent = new Intent(getActivity(), VisaActivity.class);
        startActivity(intent);
    }

    public void gotoRestrictions(View view) {
        Intent intent = new Intent(getActivity(), TravelRestrictionsActivity.class);
        startActivity(intent);
    }
}