package com.example.planer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.planer.data.CountryDriver;
import com.example.planer.favourite.FavouriteCallbackListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements FavouriteCallbackListener {
    public static String CURRENT_USER_UUID_KEY = "current_user_uuid";
    public static final String COLLECTION_NAME = "favourite";
    public static final String TAG = "ActivityMain";

    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private TextView passportHeld;
    private Spinner destinationCountrySpinner;
    private Button searchBtn;
    private Button favoriteBtn;
    private Button profileBtn;
    private Button addToFavBtn;

    private String homeCountry;
    private String destinationCountry = "Afghanistan";
    private ArrayList<String> countries;
    private boolean isFavourite = false;
    private int hiddenCountryIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setTitle("Search");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        passportHeld = findViewById(R.id.home_value);
        destinationCountrySpinner = findViewById(R.id.spinner_country);
        addToFavBtn = findViewById(R.id.button_fav);
        addToFavBtn.setOnClickListener(v -> toggleFavourite());

        countries = CountryDriver.readCountries();
        // Load home country from Firestore and set 'Destination Country' spinner
        getUserHomeCountry(db);
    }

    public void getUserHomeCountry(FirebaseFirestore db) {
        DocumentReference userDoc = db.collection("users").document(currentUser.getUid());
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Update country field using firebase user doc
                    homeCountry = (String) document.get("country");
                    passportHeld.setText(homeCountry);
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
        hiddenCountryIndex = countries.indexOf(homeCountry);
        destinationCountrySpinner = findViewById(R.id.spinner_country);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countries) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View country = null;
                if (position == hiddenCountryIndex) {
                    TextView hiddenCountry = new TextView(getContext());
                    hiddenCountry.setVisibility(View.GONE);
                    hiddenCountry.setHeight(0);
                    country = hiddenCountry;
                    country.setVisibility(View.GONE);
                }
                else {
                    country = super.getDropDownView(position, null, parent);
                }
                return country;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destinationCountrySpinner.setAdapter(adapter);

        destinationCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == hiddenCountryIndex) return;

                destinationCountry = adapterView.getItemAtPosition(position).toString();
                TextView homeVisa = findViewById(R.id.home_value);

                String countryPair;
                Bundle countryBundle = new Bundle();
                countryBundle.putString("country", destinationCountry);

                if (!isFavourite) {
                    homeVisa.setText(homeCountry);
                    countryBundle.putString("home", homeCountry);
                    countryPair = homeCountry + " - " + destinationCountry;
                } else {
                    countryPair = homeVisa.getText().toString() + " - " + destinationCountry;
                    countryBundle.putString("home", homeVisa.getText().toString());
                    isFavourite = false;
                }
                checkFavouritePair(countryPair);

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
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.card_fragment, searchCard);
        fragmentTransaction.commit();

        searchBtn.setOnClickListener(v -> {
            getSupportActionBar().setTitle("Search");
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
            getSupportActionBar().setTitle("Favourite");
            Fragment favoriteFragment = new FavouriteFragment();
            favoriteFragment.setArguments(getUserInfo());
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.page_fragment, favoriteFragment, "currentFragment")
                    .addToBackStack(null)
                    .commit();
            buttonFocusedEffect(favoriteBtn);
        });

        profileBtn.setOnClickListener(v -> {
            getSupportActionBar().setTitle("Profile");
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
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
     * Toggle favourite button
     */
    private void toggleFavourite() {
        TextView homeVisa = findViewById(R.id.home_value);
        String homeVisaValue = homeVisa.getText().toString();

        String countryPair = homeVisaValue + " - " + destinationCountry;

        if (addToFavBtn.getTag().toString().equalsIgnoreCase("false")) {
            addToFavBtn.setTag("true");
            addToFavBtn.setBackgroundResource(R.drawable.ic_favorite_filled);
            saveFavouritePair();
        } else {
            addToFavBtn.setTag("false");
            addToFavBtn.setBackgroundResource(R.drawable.ic_favorite_bordered);
            deleteFavouritePair(countryPair);
        }
    }

    /**
     * Save favourite to Firestore when the favourite button is clicked
     */
    private void saveFavouritePair() {
        TextView homeVisa = findViewById(R.id.home_value);
        String homeVisaValue = homeVisa.getText().toString();

        String countryPair = homeVisaValue + " - " + destinationCountry;
        Map<String, String> favPair = new HashMap<>();
        favPair.put("home", homeVisaValue);
        favPair.put("destination", destinationCountry);

        // Create new line;
        Map<String, Object> favMap = new HashMap<>();
        favMap.put(countryPair, favPair);

        db = FirebaseFirestore.getInstance();
        assert currentUser != null;
        db.collection(COLLECTION_NAME)
                .document(currentUser.getUid())
                .update(favMap) // Update or insert.
                .addOnSuccessListener(o ->
                        Toast.makeText(this, "Added favourite", Toast.LENGTH_SHORT))
                .addOnFailureListener(e -> {
                    db.collection(COLLECTION_NAME)
                            .document(currentUser.getUid())
                            .set(favMap);
                    Toast.makeText(this, "Error adding favourite", Toast.LENGTH_SHORT);
                });

    }

    /**
     * Delete the country pair from the Firestore on favorite button toggle
     *
     * @param countryPair a string that contains home - destination countries
     */
    private void deleteFavouritePair(String countryPair) {
        db = FirebaseFirestore.getInstance();
        assert currentUser != null;
        db.collection("favourite")
                .document(currentUser.getUid())
                .update(countryPair, FieldValue.delete())
                .addOnSuccessListener(o ->
                        Toast.makeText(this, "Deleted from favourite", Toast.LENGTH_SHORT))
                .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
    }

    /**
     * Check if the country pair exists in the favourite collection on Firestore
     *
     * @param countryPair a string that contains home - destination countries
     */
    private void checkFavouritePair(String countryPair) {
        db = FirebaseFirestore.getInstance();
        String temp = currentUser.getUid();

        db.collection("favourite")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    Map<String, Object> favPair = task.getResult().getData();
                    if (favPair != null && favPair.containsKey(countryPair)) {
                        addToFavBtn.setTag(R.string.fav_true);
                        addToFavBtn.setBackgroundResource(R.drawable.ic_favorite_filled);
                    } else {
                        addToFavBtn.setTag(R.string.fav_false);
                        addToFavBtn.setBackgroundResource(R.drawable.ic_favorite_bordered);
                    }
                })
                .addOnSuccessListener(o -> System.out.println("pair checked success"))
                .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
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

    @Override
    public void updateFavouritePair(String passport, String destination) {
        passportHeld.setText(passport);
        int id = countries.indexOf(destination);
        isFavourite = true;

        // Reset destination country to an invalid value.
        // Have to do this before setting the real value or else
        // it's not updating (when the destination is the same).
        destinationCountrySpinner.setSelection(countries.indexOf(homeCountry), true);
        destinationCountrySpinner.setSelection(id);
    }

    @Override
    public void onFavouritePairClick(String countryPair) {
        db.collection("favourite")
                .document(currentUser.getUid())
                .get()
                // Retrieve all country pairs
                .addOnCompleteListener(task -> {
                    searchBtn.callOnClick();
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> favPair = document.getData();
                    if (favPair == null) return;
                    favPair.forEach((k, v) -> {
                        if (k.equals(countryPair)) {
                            Map<String, String> value = (Map<String, String>) v;
                            updateFavouritePair(value.get("home"), value.get("destination"));
                        }
                    });
                })
                .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
    }
}
