package com.example.planer.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class CountryDriver extends android.app.Application {
    public static ArrayList<String> readCountries() {
        ArrayList<String> countries = new ArrayList<>();

        String file = "res/raw/info.txt";
        InputStream inputStream = Objects.requireNonNull(CountryDriver.class.getClassLoader()).getResourceAsStream(file);
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
