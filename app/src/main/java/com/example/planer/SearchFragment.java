package com.example.planer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.planer.currencyconverter.CurrencyConverter;
import com.example.planer.ranking.CovidRestriction;
import com.example.planer.ranking.RecommendationLevel;
import com.example.planer.ranking.Visa;
import com.example.planer.ranking.WeatherCondition;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Map;

public class SearchFragment extends Fragment implements Runnable {
    private FirebaseFirestore db;
    private String homeCountry;
    private String countrySelected;
    private String capital;

    private CardView scoreCard;
    private TextView score;
    private TextView airport;
    private TextView visaInfoText;
    private TextView advisory;
    private ImageView riskLevelIcon;
    private TextView rate;

    private String visaContent;
    private String advisoryContent;
    private int weatherCode = 0;

    private final int NUMBER_OF_CONDITIONS = 3;
    private int lock = 0;

    DecimalFormat df = new DecimalFormat("#.#");
    private TextView weatherCity;
    private TextView conditions;
    private ImageView conditionsIcon;
    private TextView temperature;

    // Currecny Converter
    private CurrencyConverter cC = new CurrencyConverter();

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
            System.out.println("search : " + countrySelected);
            run();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        score = view.findViewById(R.id.score);
        scoreCard = view.findViewById(R.id.score_card);
        airport = view.findViewById(R.id.airportInfo);
        visaInfoText = view.findViewById(R.id.visaInfo);
        advisory = view.findViewById(R.id.restrictionCovidDetail);
        riskLevelIcon = view.findViewById(R.id.imageView);
        rate = view.findViewById(R.id.currencyBlank);
        weatherCity = view.findViewById(R.id.currentWeather);
        conditions = view.findViewById(R.id.conditions);
        conditionsIcon = view.findViewById(R.id.conditionsIcon);
        temperature = view.findViewById(R.id.temp);

        scoreCard.findViewById(R.id.score_title).setOnClickListener(v -> goToScore());

        CardView visa = requireView().findViewById(R.id.visaCard);
        visa.findViewById(R.id.visa).setOnClickListener(v -> goToVisa());

        CardView travelRestrictions = requireView().findViewById(R.id.travelRestrictionsCard);
        travelRestrictions.setOnClickListener(v -> goToRestrictions());

        CardView airport = requireView().findViewById(R.id.airportCard);
        airport.findViewById(R.id.airport).setOnClickListener(v -> goToAirport());

        CardView weather = requireView().findViewById(R.id.weatherCard);
        weather.setOnClickListener(v -> goToWeather());

        CardView currency = requireView().findViewById(R.id.currencyCard);
        currency.setOnClickListener(v -> goToCurrency());

    }

    private void updateDataFromCountries() {
        db.collection("countries")
                .document(countrySelected)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> group = document.getData();
                        assert group != null;
                        group.forEach((key, value) -> {
                            if (key.equalsIgnoreCase("airport")) {
                                airport.setText(value.toString());
                            } else if (key.equalsIgnoreCase("advisory")) {
                                advisoryContent = value.toString();
                                String restriction;
                                if (advisoryContent.contains("normal"))
                                    restriction = "Take normal security";
                                else if (advisoryContent.contains("non-essential"))
                                    restriction = "Avoid non-essential travel";
                                else if (advisoryContent.contains("high"))
                                    restriction = "High degree of caution";
                                else restriction = advisoryContent;
                                advisory.setText(restriction);
                                getRiskLevelImage(advisoryContent);
                                lock++;
                            }
                        });
                    }
                });
    }

    private void updateVisaCard() {
        db.collection("visa")
                .document(homeCountry)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> group = document.getData();
                        assert group != null;
                        group.forEach((key, value) -> {
                            if (key.equalsIgnoreCase(countrySelected)) {
                                visaContent = value.toString();
                                visaInfoText.setText(visaContent);
                                lock++;
                            }
                        });
                    }
                });
    }

    private void updateWeather() {
        // get countrySelected, query db for capital, send capital into api call, pick apart json and update views
        db.collection("countries")
                .document(countrySelected)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        Map<String, Object> group = doc.getData();
                        assert group != null;
                        group.forEach((key, value) -> {
                            if (key.equalsIgnoreCase("capital")) {
                                String currWeather = "Current weather in " + value.toString();
                                capital = value.toString();
                                weatherCity.setText(currWeather);

                                // Call api
                                String APPID = BuildConfig.WEATHER_KEY;
                                String URL = "http://api.openweathermap.org/data/2.5/weather";
                                String tempUrl = URL + "?q=" + capital + "&appid=" + APPID;
                                StringRequest stringRequest = new StringRequest(Request.Method.GET,
                                        tempUrl, response -> {
                                    Log.d("response", response);
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");

                                        int idCode = jsonObjectWeather.getInt("id");
                                        weatherCode = idCode;
                                        lock++;

                                        String iconCode = jsonObjectWeather.getString("icon");
                                        String iconUrl = "http://openweathermap.org/img/wn/";
                                        iconUrl += iconCode + "@4x.png";
                                        Glide.with(this).load(iconUrl).into(conditionsIcon);

                                        // Get and store relevant data from your JSONObjects
                                        String description = jsonObjectWeather.getString("description");
                                        double temp = jsonObjectMain.getDouble("temp") - 273.15;
                                        String tempStr = "" + df.format(temp) + "°C";

                                        // Set views' values using data gathered above
                                        conditions.setText(description);
                                        temperature.setText(tempStr);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }, error -> Toast.makeText(getContext(),
                                        error.toString().trim(), Toast.LENGTH_SHORT).show());
                                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                                requestQueue.add(stringRequest);
                            }
                        });
                    }
                });
    }

    private void updateCurrency() {
        if (homeCountry != null || countrySelected != null) {
            cC.setHome(homeCountry);
            cC.setDestination(countrySelected);

            Log.d("Android", "HOME AND DEST: " + cC.getHome() + " " + cC.getDestination());

            cC.updateRate(getContext(), () -> {
                rate.setText(cC.toString());
            });
        }

    }

    public void goToScore() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.SheetDialog);
        dialog.setContentView(R.layout.layout_bottom_sheet);
        TextView dialogHeader = dialog.findViewById(R.id.title);
        TextView dialogContent = dialog.findViewById(R.id.content);
        dialogHeader.setText(R.string.score_description_title);
        dialogContent.setText(R.string.score_description_content);
        dialog.show();
    }

    public void goToVisa() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.SheetDialog);
        dialog.setContentView(R.layout.layout_bottom_sheet);
        TextView dialogHeader = dialog.findViewById(R.id.title);
        TextView dialogContent = dialog.findViewById(R.id.content);
        dialogHeader.setText(R.string.visa_description_title);
        dialogContent.setText(R.string.visa_description_content);
        dialog.show();
    }

    public void goToRestrictions() {
        Intent intent = new Intent(getActivity(), TravelRestrictionsActivity.class);
        intent.putExtra("home", homeCountry);
        intent.putExtra("destination", countrySelected);
        startActivity(intent);
    }

    public void goToAirport() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.SheetDialog);
        dialog.setContentView(R.layout.layout_bottom_sheet);
        TextView dialogHeader = dialog.findViewById(R.id.title);
        TextView dialogContent = dialog.findViewById(R.id.content);
        dialogHeader.setText(R.string.airport_description_title);
        dialogContent.setText(R.string.airport_description_content);
        dialog.show();
    }

    public void goToWeather() {
        Bundle bundle = new Bundle();
        bundle.putString("city", capital);
        bundle.putString("country", countrySelected);
        Intent intent = new Intent(getActivity(), WeatherActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void goToCurrency() {
        Intent intent = new Intent(getActivity(), CurrencyConverterActivity.class);
        intent.putExtra("home", homeCountry);
        intent.putExtra("destination", countrySelected);
        startActivity(intent);
    }

    private void getRiskLevelImage(String advisory) {
        if (advisory.contains("normal")) {
            riskLevelIcon.setImageResource(R.drawable.normal);
            return;
        }
        if (advisory.contains("caution")) {
            riskLevelIcon.setImageResource(R.drawable.high_caution);
            return;
        }
        if (advisory.contains("non-essential")) {
            riskLevelIcon.setImageResource(R.drawable.avoid_non_essential);
            return;
        }
        if (advisory.contains("all travel")) {
            riskLevelIcon.setImageResource(R.drawable.no_travel);
        }
    }

    private void calculateScore() {
        int totalScore = 0;
        int factors = 0;

        if (visaContent != null) {
            int weight = 2;
            totalScore += Visa.findScoreByDescription(visaContent) * weight;
            factors += weight;
        }
        if (advisoryContent != null) {
            int weight = 4;
            totalScore += CovidRestriction.findScoreByDescription(advisoryContent) * weight;
            factors += weight;
        }
        if (weatherCode != 0) {
            int weight = 1;
            totalScore += WeatherCondition.findScoreByWeatherCode(weatherCode) * weight;
            factors += weight;
        }

        double scaledScore = (factors != 0) ? (double) totalScore / factors : 0;

        changeScoreCardBackground(scaledScore);

        DecimalFormat df = new DecimalFormat("###");
        score.setText(df.format(scaledScore));
    }

    private void changeScoreCardBackground(double score) {
        RecommendationLevel recommendation = RecommendationLevel.findRecommendationLevel(score);
        switch (recommendation) {
            case STRONGLY_RECOMMENDED:
                scoreCard.setCardBackgroundColor(Color.GREEN);
                return;
            case RECOMMENDED:
                scoreCard.setCardBackgroundColor(Color.CYAN);
                return;
            case URGENCY_ONLY:
                scoreCard.setCardBackgroundColor(Color.parseColor("#FFA500"));
                return;
            case NOT_RECOMMENDED:
                scoreCard.setCardBackgroundColor(Color.GRAY);
        }
    }

    @Override
    public void run() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                synchronized (this) {
                    updateVisaCard();
                    updateDataFromCountries();
                    updateWeather();
                    updateCurrency();

                    while (lock != NUMBER_OF_CONDITIONS) {
                        // do nothing
                    }

                    requireActivity().runOnUiThread(() -> calculateScore());
                }
            }
        };
        thread.start();
    }
}
