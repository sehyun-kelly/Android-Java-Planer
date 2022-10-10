package com.example.planer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.planer.data.CountryDriver;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    String country;
    Button searchBtn;
    Button favoriteBtn;
    Button profileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        onBindViewToData();
    }

    private void onBindViewToData() {
        ArrayList<String> countries = CountryDriver.readCountries();
        Spinner countrySpinner = findViewById(R.id.spinner_country);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                country = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        searchBtn = findViewById(R.id.nav_search);
        favoriteBtn = findViewById(R.id.nav_favourite);
        profileBtn = findViewById(R.id.nav_profile);

        Fragment searchCard = new SearchFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.card_fragment, searchCard);
        fragmentTransaction.commit();

        searchBtn.setOnClickListener(v -> {
            Fragment fragment = fragmentManager.findFragmentByTag("currentFragment");
            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .remove(fragment)
                        .addToBackStack(null)
                        .commit();
            }
            buttonFocusedEffect(searchBtn);
        });

        favoriteBtn.setOnClickListener(v -> {
            Fragment favoriteFragment = new FavouriteFragment(countries);
            fragmentManager.beginTransaction()
                    .replace(R.id.page_fragment, favoriteFragment, "currentFragment")
                    .addToBackStack(null)
                    .commit();
            buttonFocusedEffect(favoriteBtn);
        });

        profileBtn.setOnClickListener(v -> {
            fragmentManager.beginTransaction()
                    .replace(R.id.page_fragment, new ProfileFragment(), "currentFragment")
                    .addToBackStack(null)
                    .commit();
            buttonFocusedEffect(profileBtn);
        });
    }

    public void buttonFocusedEffect(View button) {
        Resources.Theme theme = getTheme();
        if (button.equals(searchBtn)) {
            favoriteBtn.setBackgroundColor(getResources().getColor(R.color.greyish_turquoise, theme));
            profileBtn.setBackgroundColor(getResources().getColor(R.color.greyish_turquoise, theme));
            findViewById(R.id.search_activity_group).setVisibility(View.VISIBLE);
        } else if (button.equals(favoriteBtn)) {
            searchBtn.setBackgroundColor(getResources().getColor(R.color.greyish_turquoise, theme));
            profileBtn.setBackgroundColor(getResources().getColor(R.color.greyish_turquoise, theme));
            findViewById(R.id.search_activity_group).setVisibility(View.INVISIBLE);
        } else {
            searchBtn.setBackgroundColor(getResources().getColor(R.color.greyish_turquoise, theme));
            favoriteBtn.setBackgroundColor(getResources().getColor(R.color.greyish_turquoise, theme));
            findViewById(R.id.search_activity_group).setVisibility(View.INVISIBLE);
        }

        button.setBackgroundColor(getResources().getColor(R.color.turquoise, theme));
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}