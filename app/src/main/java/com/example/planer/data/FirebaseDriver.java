package com.example.planer.data;

import static android.content.ContentValues.TAG;

import android.content.res.Resources;
import android.util.Log;

import com.example.planer.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDriver {
    public static int count = 0;
    public static int pos = 0;

    // Add countries info to "countries" collection
    public static void uploadCountries(FirebaseFirestore db) {
        Map<String, String> countries = new HashMap<>();

        InputStream inputStream = Resources.getSystem().openRawResource(R.raw.info);
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
                        .addOnSuccessListener((OnSuccessListener) o -> Log.d(TAG, "Count: " + count + ", DocumentSnapshot added")).addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
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
        InputStream inputStream = Resources.getSystem().openRawResource(R.raw.visa);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ArrayList<String> visaList = new ArrayList<>();

        try {
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                String[] info = line.split(",");

                if (pos == 0) {
                    visaList.addAll(Arrays.asList(info));
                } else {
                    for (int i = 1; i < info.length; i++) {
                        visa.put(visaList.get(i), info[i]);
                    }
                    db.collection("visa").document(info[0])
                            .set(visa)
                            .addOnSuccessListener((OnSuccessListener) o -> Log.d(TAG, "Count: " + pos + ", DocumentSnapshot added")).addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                }
                pos++;

            }
            bufferedReader.close();
            inputStream.close();
        } catch (Exception e) {
            System.out.println("FileRead Error");
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        uploadCountries(db);
//        uploadVisa(db);
//    }
}