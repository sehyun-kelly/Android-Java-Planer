package com.example.planer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    static final String TAG = "Android";

    // Firebase
    FirebaseUser user;
    FirebaseFirestore db;
    DocumentReference userDoc;
    StorageReference storageRef;
    Uri imageUri;

    // Profile page Views
    CircleImageView profilePicBtn;
    EditText usernameField;
    TextView emailField;
    EditText countryField;
    Button updateBtn;
    Button logoutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set buttons
        updateBtn = requireView().findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(this::update);
        logoutBtn = requireView().findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(this::logout);
        profilePicBtn = requireView().findViewById(R.id.profilePic);
        profilePicBtn.setOnClickListener(this::changeProfilePic);

        // Get user info and write to text fields
        updatePic();
        userDoc = db.collection("users").document(user.getUid());
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Update fields using firebase user doc
                    TextView userHeader = requireView().findViewById(R.id.username);
                    usernameField = requireView().findViewById(R.id.usernameField);
                    emailField = requireView().findViewById(R.id.email);
                    countryField = requireView().findViewById(R.id.countryField);
                    String username = (String) document.get("username");
                    String email = (String) document.get("email");
                    String country = (String) document.get("country");
                    userHeader.setText(username);
                    usernameField.setText(username);
                    emailField.setText(email);
                    countryField.setText(country);

                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePic();
    }

    /**
     * Starts new ChangeProfilePic activity
     * @param view this profile fragment
     */
    public void changeProfilePic(View view) {
        Intent intent = new Intent(getActivity(), ChangeProfilePic.class);
        startActivity(intent);
    }

    /**
     * Updates the various user info views in ProfileFragment
     * @param view this profile fragment
     */
    public void update(View view) {
        String username = usernameField.getText().toString();
        String country = countryField.getText().toString();

        // Update relevant fields in Firestore
        db.collection("users").document(user.getUid()).update(
                "username", username,
                "country", country
        );
        Toast.makeText(getActivity(), "Information updated", Toast.LENGTH_SHORT).show();

        //TODO: There must be a better solution...
        // Refreshing and refocusing SearchActivity.java to show change to 'Passport Held'
        new Handler().post(() -> {
            Intent intent = getActivity().getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            getActivity().overridePendingTransition(0, 0);
            getActivity().finish();
            getActivity().overridePendingTransition(0, 0);
            startActivity(intent);
        });
    }

    /**
     * Log the user out of the app
     * @param view this profile fragment
     */
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    /**
     * Helper method to replace the profile pic with the current image in Storage
     */
    public void updatePic() {
        profilePicBtn = requireView().findViewById(R.id.profilePic);
        StorageReference picRef = storageRef.child("ProfilePics/" + user.getUid());
        picRef.getDownloadUrl().addOnSuccessListener(uri -> {
            imageUri = uri;
            Glide.with(ProfileFragment.this).load(imageUri).placeholder(R.drawable.profileplaceholder).centerCrop().into(profilePicBtn);
        });
    }
}