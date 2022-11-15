package com.example.planer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class TravelRestrictionsActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    TextView cardLinkTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_restrictions);
        Intent intent = getIntent();
        String country = intent.getStringExtra("destination");
        cardLinkTitle = findViewById(R.id.card_links);
        String cardLinkText = "Covid-19 related restrictions and information for " + country;
        cardLinkTitle.setText(cardLinkText);
        linearLayout = findViewById(R.id.restriction_linear);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("countries")
                .document(country)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> group = document.getData();
                        assert group != null;
                        group.forEach((key, value) -> {
                            if (key.equalsIgnoreCase("links")) {
                                String linksString = value.toString();
                                String[] links = linksString.substring(1, linksString.length() - 2).split(", ");
                                for (String link : links) {
                                    TextView textView = new TextView(this);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(0, 20, 0, 20);
                                    textView.setTextSize(12);
                                    textView.setLayoutParams(params);

                                    textView.setText(link);
                                    Linkify.addLinks(textView, Linkify.WEB_URLS);
                                    textView.setLinksClickable(true);
                                    textView.setLinkTextColor(getColor(R.color.turquoise));

                                    linearLayout.addView(textView);
                                }
                            }
                        });
                    }
                });
    }
}