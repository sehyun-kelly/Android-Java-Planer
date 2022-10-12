package com.example.planer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WeatherActivity extends AppCompatActivity {

    EditText etCity, etCountry;
    TextView tvResult;
    ImageView ivIcon;
    TextView temperature;
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
        temperature = findViewById(R.id.temperature);
    }

    public void getWeather(View view) {
        String tempUrl;
        String city = etCity.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        if(city.isEmpty()) {
            Toast.makeText(WeatherActivity.this,
                    R.string.noCity, Toast.LENGTH_SHORT).show();
        } else {
            // City has been entered, init key + URL base
            String APPID = "6ce8f403d3cbee49efdb370cafcc65c3";
            String URL = "http://api.openweathermap.org/data/2.5/weather";
            // complete URL
            if (!country.equals("")) {
                tempUrl = URL + "?q=" + city + "," + country + "&appid=" + APPID;
            } else {
                tempUrl = URL + "?q=" + city + "&appid=" + APPID;
            }
            /* StringRequest obj via Volley to get JSON as string
            (read as: 'GET' from 'tempUrl' the 'response'; a string)
             */
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    tempUrl, response -> {
                        Log.d("response", response); /* Copy this giant 1 ln string from Logcat
                        and paste into an online JSON object viewer (e.g., http://jsonviewer.stack.hu/)
                        to see obj structure
                        */
                        String output = "";
                        try {
                            /* Create new JSONObjects and JSONArrays as necessary
                            (see online JSON object to verify array vs object)
                             */
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                            JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                            JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                            JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");

                            /* Get the icon code jsonObjectWeather, plug it into the necessary URL,
                            use Glide library to easily replace the img in ImageView with
                            the image at the URL
                             */
                            String iconCode = jsonObjectWeather.getString("icon");
                            String iconUrl = "http://openweathermap.org/img/wn/";
                            iconUrl += iconCode + "@4x.png";
                            Glide.with(this).load(iconUrl).into(ivIcon);

                            // Get and store relevant data from your JSONObjects
                            String description = jsonObjectWeather.getString("description");
                            double temp = jsonObjectMain.getDouble("temp") - 273.15;
                            double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                            float pressure = jsonObjectMain.getInt("pressure");
                            int humidity = jsonObjectMain.getInt("humidity");
                            String wind = jsonObjectWind.getString("speed");
                            String clouds = jsonObjectClouds.getString("all");
                            String city1 = jsonResponse.getString("name");

                            // Set views' values using data gathered above
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

            // https://google.github.io/volley/simple.html
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}