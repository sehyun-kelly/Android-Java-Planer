package com.example.planer;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.planer.ranking.CovidRestriction;
import com.example.planer.ranking.RecommendationLevel;
import com.example.planer.ranking.Visa;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.Map;

public class SearchFragment extends Fragment {
    private FirebaseFirestore db;
    private String homeCountry;
    private String countrySelected;

    private CardView scoreCard;
    private TextView score;
    private TextView airport;
    private TextView visaInfoText;
    private TextView advisory;
    private ImageView riskLevelIcon;

    private String visaContent;
    private String advisoryContent;

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

        score = view.findViewById(R.id.score);
        scoreCard = view.findViewById(R.id.score_card);
        airport = view.findViewById(R.id.airportInfo);
        visaInfoText = view.findViewById(R.id.visaInfo);
        advisory = view.findViewById(R.id.restrictionCovidDetail);
        riskLevelIcon = view.findViewById(R.id.imageView);
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
                                advisory.setText(advisoryContent);
                                getRiskLevelImage(advisoryContent);
                            }
                        });
                        calculateScore();
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
            totalScore += Visa.findScoreByDescription(visaContent);
            factors++;
        }
        if (advisoryContent != null) {
            totalScore += CovidRestriction.findScoreByDescription(advisoryContent);
            factors++;
        }

        double scaledScore = (factors != 0) ? (double) totalScore / factors : 0;

        changeScoreCardBackground(scaledScore);

        DecimalFormat df = new DecimalFormat("###.##");
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
}
