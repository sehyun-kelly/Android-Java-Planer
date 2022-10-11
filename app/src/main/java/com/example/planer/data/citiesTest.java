package com.example.planer.data;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class citiesTest {

    // Add countries info to "countries" collection
    public static void uploadCitiesToCountries(FirebaseFirestore db) {
        String file = "res/raw/cities.txt";
        InputStream inputStream = Objects.requireNonNull(FirebaseDriver.class.getClassLoader()).getResourceAsStream(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] info = line.split("\t");
                String city = info[0];
                String country = info[1];
                db.collection("countries").document(country).update("cities", FieldValue.arrayUnion(city));
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            inputStream.close();
        } catch (Exception e) {
            System.out.println("FileRead Error");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        uploadCitiesToCountries(db);
    }
}
