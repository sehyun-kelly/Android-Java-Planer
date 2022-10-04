package com.example.planer;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    public static int count = 0;
    public static int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);

        }
    }

    public void signUp(View view) {
        EditText username = findViewById(R.id.username_input);
        String user = username.getText().toString();
        EditText password = findViewById(R.id.password_input);
        String pass = password.getText().toString();

        if (!validateForm(user, pass)) {
            Toast.makeText(MainActivity.this,
                    "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(user, pass)
                .addOnFailureListener(e -> {
                    Log.e("QA", "Failed to add data", e); // log error to logcat
                })
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this,
                                "Account created. Please log in.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void signIn(View view) {
        EditText username = findViewById(R.id.username_input);
        String user = username.getText().toString();
        EditText password = findViewById(R.id.password_input);
        String pass = password.getText().toString();

        if (!validateForm(user, pass)) {
            Toast.makeText(MainActivity.this,
                    "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(MainActivity.this,
                        "Incorrect email / password.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validateForm(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }


    // Add countries info to "countries" collection
    public void uploadCountries(FirebaseFirestore db){
        Map<String, String> countries = new HashMap<>();

        InputStream inputStream = getBaseContext().getResources().openRawResource(R.raw.info);
        BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));
        try{
            String line = bufferedReader.readLine();
            while (line != null) {
                line = bufferedReader.readLine();
                String info[] = line.split("/");
                countries.put("airport", info[1]);
                countries.put("advisory", info[2]);

                db.collection("countries").document(info[0])
                        .set(countries)
                        .addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d(TAG, "Count: " + count + ", DocumentSnapshot added");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

                count++;
            }
            bufferedReader.close();
            inputStream.close();
        }catch (Exception e){
            System.out.println("FileRead Error");
            e.printStackTrace();
        }
    }

    // Add visa info to "visa" collection
    public void uploadVisa(FirebaseFirestore db){
        Map<String, String> visa = new HashMap<>();
        InputStream inputStream = getBaseContext().getResources().openRawResource(R.raw.visa);
        BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));
        ArrayList<String> visaList = new ArrayList<>();

        try{
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                String info[] = line.split(",");

                if(pos == 0){
                    for(int i = 0; i < info.length; i++){
                        visaList.add(info[i]);
                    }
                }else{
                    for(int i = 1; i < info.length; i++){
                        visa.put(visaList.get(i), info[i]);
                    }
                    db.collection("visa").document(info[0])
                            .set(visa)
                            .addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    Log.d(TAG, "Count: " + pos + ", DocumentSnapshot added");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                }
                pos++;

            }
            bufferedReader.close();
            inputStream.close();
        }catch (Exception e){
            System.out.println("FileRead Error");
            e.printStackTrace();
        }
    }
}