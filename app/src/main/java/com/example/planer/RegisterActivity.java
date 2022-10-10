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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    static final String TAG = "Android";
    private FirebaseAuth mAuth;

    String country = "";
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.db = FirebaseFirestore.getInstance();

        ArrayList<String> countries = readCountries();
        Spinner countrySpinner = findViewById(R.id.spinner_country);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, readCountries());
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

    private ArrayList<String> readCountries() {
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
        return countries;
    }


    /**
     * Redirect user to login activity.
     *
     * @param view
     * @author Ravinder Shokar
     */
    public void signIn(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void signUp(View view) {
        EditText username = findViewById(R.id.username_input);
        EditText password = findViewById(R.id.password_input);
        EditText email = findViewById(R.id.email_input);
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


        // This is where we would add the user to the db and then redirect to login activity
        // Add user to db
        mAuth.createUserWithEmailAndPassword(userNameInput, passwordInput)
                .addOnFailureListener(e -> {
                    Log.e("QA", "Failed to add data", e); // log error to logcat
                })
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,
                                "Account created. Please log in.", Toast.LENGTH_SHORT).show();

                        // Redirect to sign up
                        signIn(view);
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
