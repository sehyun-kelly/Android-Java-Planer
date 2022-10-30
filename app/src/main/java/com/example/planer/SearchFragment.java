package com.example.planer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.planer.favourite.FavouriteCountriesAdapter;
import com.example.planer.favourite.FavouriteCountry;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Map;
import java.util.List;

public class SearchFragment extends Fragment {
    private FirebaseFirestore db;
    private String homeCountry;
    private String countrySelected;
    private TextView airport;
    private TextView visaInfoText;
    private TextView advisory;
    private ImageView riskLevelIcon;

    public SearchFragment() {
        super(R.layout.fragment_search);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            db = FirebaseFirestore.getInstance();
            homeCountry = getArguments().getString("home");
            countrySelected = getArguments().getString("country");
            System.out.println(countrySelected);
            updateVisaCard();
            updateDataFromCountries();
        }
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

        airport = view.findViewById(R.id.airportInfo);
        visaInfoText = view.findViewById(R.id.visaInfo);
        advisory = view.findViewById(R.id.restrictionCovidDetail);
        riskLevelIcon = view.findViewById(R.id.imageView);
    }

    private void updateDataFromCountries(){
        db.collection("countries")
                .document(countrySelected)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> group = document.getData();
                        assert group != null;
                        group.forEach((key, value) -> {
                            if (key.equalsIgnoreCase("airport")) {
                                airport.setText(value.toString());
                            } else if (key.equalsIgnoreCase("advisory")) {
                                advisory.setText(value.toString());
                                getRiskLevelImage(value.toString());
                            }
                        });
                    }
                });
    }
    private void updateVisaCard(){
        db.collection("visa")
                .document(homeCountry)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> group = document.getData();
                        assert group != null;
                        group.forEach((key, value) -> {
                            if (key.equalsIgnoreCase(countrySelected)) {
                                visaInfoText.setText(value.toString());
                            }
                        });
                    }
                });
    }


    public void gotoWeather(View view) {
        Intent intent = new Intent(getActivity(), WeatherActivity.class);
        startActivity(intent);
    }

    public void gotoCurrency(View view) {
        Intent intent = new Intent(getActivity(), CurrencyConverterActivity.class);
        intent.putExtra("home", homeCountry);
        intent.putExtra("destination", countrySelected);
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

    private void getRiskLevelImage(String advisory){
        if(advisory.contains("normal")) riskLevelIcon.setImageResource(R.drawable.normal);
        if(advisory.contains("caution")) riskLevelIcon.setImageResource(R.drawable.high_caution);
        if(advisory.contains("non-essential")) riskLevelIcon.setImageResource(R.drawable.avoid_non_essential);
        if(advisory.contains("all travel")) riskLevelIcon.setImageResource(R.drawable.no_travel);
    }
}