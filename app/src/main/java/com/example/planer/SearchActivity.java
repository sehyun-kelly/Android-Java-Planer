package com.example.planer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

        searchBtn = findViewById(R.id.nav_search);
        favoriteBtn = findViewById(R.id.nav_favourite);
        profileBtn = findViewById(R.id.nav_profile);

        Fragment searchCard = new SearchFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.cardFragment, searchCard);
        fragmentTransaction.commit();


        Spinner countrySpinner = findViewById(R.id.spinner_country);

        ArrayList<String> countries = new ArrayList<>();

        InputStream inputStream = getBaseContext().getResources().openRawResource(R.raw.info);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] split = line.split("/");
                countries.add(split[0]);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            inputStream.close();
        } catch (Exception e) {
            System.out.println("FileRead Error");
            e.printStackTrace();
        }

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
    }

    public void buttonUnfocusedEffect(View button){
        button.setBackgroundColor(getResources().getColor(R.color.greyish_turquoise));
        button.setOnClickListener(l -> buttonFocusedEffect(button));
    }

    public void buttonFocusedEffect(View button){
        if (button.equals(searchBtn)) {
            buttonUnfocusedEffect(favoriteBtn);
            buttonUnfocusedEffect(profileBtn);
        } else if (button.equals(favoriteBtn)) {
            buttonUnfocusedEffect(searchBtn);
            buttonUnfocusedEffect(profileBtn);
        } else {
            buttonUnfocusedEffect(profileBtn);
            buttonUnfocusedEffect(favoriteBtn);
        }

        button.setBackgroundColor(getResources().getColor(R.color.turquoise));
    }
}