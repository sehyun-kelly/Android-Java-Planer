package com.example.planer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

//import java.text.DecimalFormat;

public class WeatherActivity extends AppCompatActivity {

    EditText etCity, etCountry;
    TextView tvResult;
//    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        etCountry = findViewById(R.id.country_name);
        etCity = findViewById(R.id.city_name);
    }

    @SuppressLint("SetTextI18n")
    public void getWeather(View view) {
        String tempUrl;
        String city = etCity.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        if(city.equals("")) {
            tvResult.setText("City cannot be empty");
        } else {
            String APPID = "6ce8f403d3cbee49efdb370cafcc65c3";
            String URL = "http://api.openweathermap.org/data/2.5/weather";
            if (!country.equals("")) {
                tempUrl = URL + "?q=" + city + "," + country + "&appid=" + APPID;
            } else {
                tempUrl = URL + "?q=" + city + "&appid=" + APPID;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    tempUrl, response -> Log.d("weather:", response), error ->
                    Toast.makeText(getApplicationContext(), error.toString().trim(),
                            Toast.LENGTH_SHORT).show());
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }

}