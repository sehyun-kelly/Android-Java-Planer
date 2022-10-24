package com.example.planer;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.planer.favourite.FavouriteCountriesAdapter;
import com.example.planer.favourite.FavouriteCountry;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavouriteFragment extends Fragment {
    public static final String TAG = FavouriteFragment.class.getName();

    private FirebaseFirestore db;
    private String userUuid;
    private final ArrayList<FavouriteCountry> favouriteCountries = new ArrayList<>();

    private FavouriteCountriesAdapter favouriteCountriesAdapter;

    public FavouriteFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            userUuid = getArguments().getString(SearchActivity.CURRENT_USER_UUID_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        favouriteCountriesAdapter = new FavouriteCountriesAdapter(favouriteCountries);
        recyclerView.setAdapter(favouriteCountriesAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        db.collection("favourite")
                .document(userUuid)
                .get()
                // Retrieve all country pairs
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("list");
                    if (group == null) return;
                    for (String pair : group) {
                        favouriteCountries.add(new FavouriteCountry(pair));
                    }
                })
                // When finish reading all data, notify adapter
                .addOnSuccessListener(o -> favouriteCountriesAdapter.notifyDataSetChanged())
                .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));

    }
}