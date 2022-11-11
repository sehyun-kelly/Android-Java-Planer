package com.example.planer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.example.planer.favourite.FavouriteCountriesAdapter;
import com.example.planer.favourite.FavouriteCountry;
import com.example.planer.favourite.FavouriteCallbackListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class FavouriteFragment extends Fragment {
    public static final String TAG = FavouriteFragment.class.getName();

    private FirebaseFirestore db;
    private String userUuid;
    private final ArrayList<FavouriteCountry> favouriteCountries = new ArrayList<>();

    private FavouriteCountriesAdapter favouriteCountriesAdapter;

    public FavouriteFragment() {
    }

    private FavouriteCallbackListener favouriteCallbackListener;

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
        favouriteCountriesAdapter = new FavouriteCountriesAdapter(favouriteCountries, favouriteCallbackListener);
        recyclerView.setAdapter(favouriteCountriesAdapter);

        SearchView searchBar = view.findViewById(R.id.search_bar);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                favouriteCountriesAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                favouriteCountriesAdapter.getFilter().filter(newText);
                return true;
            }
        });

        ImageButton imageButton = view.findViewById(R.id.favourite_sort_menu);
        PopupMenu popupMenu = new PopupMenu(getContext(), imageButton);
        popupMenu.inflate(R.menu.favourite_sort_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case (R.id.from_a_to_z):
                    favouriteCountriesAdapter.sortFavouritePair(1);
                    break;
                case (R.id.from_z_to_a):
                    favouriteCountriesAdapter.sortFavouritePair(2);
                    break;
            }
            return true;
        });
        imageButton.setOnClickListener(v -> popupMenu.show());
        return view;
    }

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        favouriteCallbackListener = (FavouriteCallbackListener) context;
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
                    Map<String, Object> favPair = document.getData();
                    if (favPair == null) return;
                    favPair.forEach((k, v) -> favouriteCountries.add(new FavouriteCountry(k)));
                })
                // When finish reading all data, notify adapter
                .addOnSuccessListener(o -> favouriteCountriesAdapter.notifyDataLoaded())
                .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
    }
}