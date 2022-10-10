package com.example.planer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.example.planer.data.CountryDriver;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    String country = "";
    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

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
    }


    /**
     * Redirect user to login activity.
     *
     * @param view sign in view
     * @author Ravinder Shokar
     */
    public void signIn(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void signUp(View view) {
        EditText username = findViewById(R.id.username_input);
        EditText email = findViewById(R.id.email_input);
        EditText password = findViewById(R.id.password_input);
        EditText confirmPassword = findViewById(R.id.confirm_password_input);

        String userNameInput = username.getText().toString();
        String emailInput = email.getText().toString();
        String passwordInput = password.getText().toString();
        String confirmPasswordInput = confirmPassword.getText().toString();

        if (!validateForm(userNameInput, emailInput, passwordInput, confirmPasswordInput)) {
            Toast.makeText(RegisterActivity.this,
                    "Please enter user name, email, password, and confirm password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign in, adding user to db
        mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                .addOnFailureListener(e -> {
                    Log.e("QA", "Failed to add data", e); // log error to logcat
                })
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        mAuth.signInWithEmailAndPassword(emailInput, passwordInput)
                                .addOnCompleteListener(this, task2 -> {
                                    if (task2.isSuccessful()) {
                                        // log in, and store user inputs in new doc under 'users' collection
                                        user = FirebaseAuth.getInstance().getCurrentUser();
                                        Map<String, Object> registrant = new HashMap<>();
                                        registrant.put("uid", user.getUid());
                                        registrant.put("username", userNameInput);
                                        registrant.put("email", emailInput);
                                        registrant.put("country", country);
                                        db.collection("users").document(user.getUid()).set(registrant);
                                        Intent intent = new Intent(RegisterActivity.this, SearchActivity.class);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(RegisterActivity.this,
                                                "Incorrect email / password.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // If an error display error as toast
                        Toast.makeText(RegisterActivity.this,
                                "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private boolean validateForm(String userName, String email, String password, String confirmPassword) {
        return !userName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty();
    }
}
