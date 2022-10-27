package com.example.planer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.planer.data.CountryDriver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {
    public static String CURRENT_USER_UUID_KEY = "current_user_uuid";
    public static final String TAG = "ActivityMain";

    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    public String homeCountry;
    private String country;

    Button searchBtn;
    Button favoriteBtn;
    Button profileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Get home country from Firestore
        DocumentReference userDoc = db.collection("users").document(currentUser.getUid());
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Update country field using firebase user doc
                    TextView home_val = findViewById(R.id.home_value);
                    homeCountry = (String) document.get("country");
                    home_val.setText(homeCountry);
                    onBindViewToData();
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
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

                Bundle countryBundle = new Bundle();
                countryBundle.putString("home", homeCountry);
                countryBundle.putString("country", country);

                Fragment searchFragment = new SearchFragment();
                searchFragment.setArguments(countryBundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.card_fragment, searchFragment);
                fragmentTransaction.commit();
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
            saveFavouritePair();
            Fragment favoriteFragment = new FavouriteFragment();
            favoriteFragment.setArguments(getUserInfo());
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

    /**
     * Save favourite to Firestore.
     */
    //TODO: Implement a favourite button. Call this when the button is clicked.
    private void saveFavouritePair() {
        TextView homeVisa = findViewById(R.id.home_value);
        String homeVisaValue = homeVisa.getText().toString();

        String countryPair = homeVisaValue + " - " + country;

        db = FirebaseFirestore.getInstance();
        assert currentUser != null;
        db.collection("favourite")
                .document(currentUser.getUid())
                .update("list", FieldValue.arrayUnion(countryPair))
                .addOnSuccessListener(o ->
                        Toast.makeText(this, "Added favourite", Toast.LENGTH_SHORT))
                .addOnFailureListener(e ->
                        initializeFavouriteList(countryPair));
    }

    /**
     * Add an array field named 'list' to user's favourite collection.
     * Initialize this 'list' with a value.
     *
     * @param value a given String
     */
    private void initializeFavouriteList(String value) {
        HashMap<String, Object> docData = new HashMap<>();
        ArrayList<String> listOfCountries = new ArrayList<>();
        listOfCountries.add(value);

        docData.put("list", listOfCountries);

        db.collection("favourite")
                .document(currentUser.getUid())
                .set(docData);
    }

    /**
     * Create a bundle with user information.
     *
     * @return a bundle
     */
    private Bundle getUserInfo() {
        Bundle bundle = new Bundle();
        bundle.putString(CURRENT_USER_UUID_KEY, currentUser.getUid());
        return bundle;
    }
}