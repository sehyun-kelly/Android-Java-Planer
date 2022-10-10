package com.example.planer.data;

import android.content.Context;

import com.example.planer.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CountryDriver {
    public static ArrayList<String> readCountries(Context context) {
        ArrayList<String> countries = new ArrayList<>();

        InputStream inputStream = context.getResources().openRawResource(R.raw.info);
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
}
