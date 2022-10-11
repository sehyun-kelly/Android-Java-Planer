package com.example.planer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;

public class WeatherActivity extends AppCompatActivity {

    EditText etCity, etCountry;
    TextView tvResult;
    ImageView ivIcon;
    DecimalFormat df = new DecimalFormat("#.#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        TextView date = findViewById(R.id.currentDate);
        DateFormat dateInstance = SimpleDateFormat.getDateInstance();
        String dateStr = dateInstance.format(Calendar.getInstance().getTime());
        date.setText(dateStr);

        etCountry = findViewById(R.id.country_name);
        etCity = findViewById(R.id.city_name);
        tvResult = findViewById(R.id.weatherResults);
        ivIcon = findViewById(R.id.icon);
    }

    public void getWeather(View view) {
        String tempUrl;
        String city = etCity.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        if(city.isEmpty()) {
            Toast.makeText(WeatherActivity.this,
                    R.string.noCity, Toast.LENGTH_SHORT).show();
        } else {
            String APPID = "6ce8f403d3cbee49efdb370cafcc65c3";
            String URL = "http://api.openweathermap.org/data/2.5/weather";
            if (!country.equals("")) {
                tempUrl = URL + "?q=" + city + "," + country + "&appid=" + APPID;
            } else {
                tempUrl = URL + "?q=" + city + "&appid=" + APPID;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    tempUrl, response -> {
                        Log.d("response", response);
                        String output = "";
                        try {
                            String iconUrl = "http://openweathermap.org/img/wn/";  // + @2x.png
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                            String iconCode = jsonObjectWeather.getString("icon");
                            iconUrl += iconCode + "@4x.png";
                            Glide.with(this).load(iconUrl).into(ivIcon);
                            String description = jsonObjectWeather.getString("description");
                            JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                            double temp = jsonObjectMain.getDouble("temp") - 273.15;
                            double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                            float pressure = jsonObjectMain.getInt("pressure");
                            int humidity = jsonObjectMain.getInt("humidity");
                            JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                            String wind = jsonObjectWind.getString("speed");
                            JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                            String clouds = jsonObjectClouds.getString("all");
                            String city1 = jsonResponse.getString("name");
                            TextView temperature = findViewById(R.id.temperature);
                            String tempStr = "" + df.format(temp) + "°C";
                            temperature.setText(tempStr);
                            output += "Current weather in " + city1 + ":\n" +
                                    "\nConditions:   " + description +
                                    "\nFeels like:     " + df.format(feelsLike) + "°C" +
                                    "\nPressure:     " + pressure + " hPa" +
                                    "\nHumidity:     " + humidity + "%" +
                                    "\n\nWind:           " + wind + " m/s" +
                                    "\nClouds:        " + clouds + "%";
                          tvResult.setText(output);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Toast.makeText(getApplicationContext(),
                    error.toString().trim(), Toast.LENGTH_SHORT).show());
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}