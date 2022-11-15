package com.example.planer.data;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirebaseDriver {
    public static int count = 0;
    public static int pos = 0;

    // Add countries info to "countries" collection
    public static void uploadCountries(FirebaseFirestore db) {
        Map<String, String> countries = new HashMap<>();

        String file = "res/raw/info.txt";
        InputStream inputStream = Objects.requireNonNull(FirebaseDriver.class.getClassLoader()).getResourceAsStream(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                line = bufferedReader.readLine();
                String[] info = line.split("/");
                countries.put("airport", info[1]);
                countries.put("advisory", info[2]);

                db.collection("countries").document(info[0])
                        .set(countries)
                        .addOnSuccessListener(o -> Log.d(TAG, "Count: " + count + ", DocumentSnapshot added")).addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                count++;
            }
            bufferedReader.close();
            inputStream.close();
        } catch (Exception e) {
            System.out.println("FileRead Error");
            e.printStackTrace();
        }
    }

    // Add visa info to "visa" collection
    public static void uploadVisa(FirebaseFirestore db) {
        Map<String, String> visa = new HashMap<>();

        String file = "res/raw/visa.txt";
        InputStream inputStream = Objects.requireNonNull(FirebaseDriver.class.getClassLoader()).getResourceAsStream(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ArrayList<String> visaList = new ArrayList<>();

        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] info = line.split(",");

                if (pos == 0) {
                    visaList.addAll(Arrays.asList(info));
                } else {
                    for (int i = 1; i < info.length; i++) {
                        visa.put(visaList.get(i), info[i]);
                    }
                    db.collection("visa").document(info[0])
                            .set(visa)
                            .addOnSuccessListener(o -> Log.d(TAG, "Count: " + pos + ", DocumentSnapshot added")).addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                }
                pos++;
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            inputStream.close();
        } catch (Exception e) {
            System.out.println("FileRead Error");
            e.printStackTrace();
        }
    }

    public static void uploadCovidLinks(FirebaseFirestore db) {
        String file = "res/raw/covid_link.txt";
        InputStream inputStream = Objects.requireNonNull(FirebaseDriver.class.getClassLoader()).getResourceAsStream(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String country = "";
        ArrayList<String> links = new ArrayList<>();
        try {
            String line = bufferedReader.readLine();
            String[] initial_info = line.split(",");
            country = initial_info[0];
            while (line != null) {
                String[] info = line.split(",");
                if (info[0] != country) {
                    String[] links_array = links.toArray(new String[0]);
                    db.collection("countries").document(country).update("links", FieldValue.arrayUnion(links_array));
                    links.clear();
                }
                country = info[0];
                links.add(info[1]);
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
        uploadCountries(db);
        uploadVisa(db);
        uploadCountries(db);
    }
}
