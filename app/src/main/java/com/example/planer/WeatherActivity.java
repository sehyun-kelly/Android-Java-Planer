package com.example.planer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
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
    Button refreshBtn;
    ImageView ivIcon;
    TextView temperature;
    TextView tvResult;
    DecimalFormat df = new DecimalFormat("#.#");

    // To hold bundle values from SearchActivity.java
    String city;
    String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // Set to today's date
        TextView date = findViewById(R.id.currentDate);
        DateFormat dateInstance = SimpleDateFormat.getDateInstance();
        String dateStr = dateInstance.format(Calendar.getInstance().getTime());
        date.setText(dateStr);

        // Unpack bundle and set view IDs
        Bundle bundle = getIntent().getExtras();
        city = bundle.getString("city");
        country = bundle.getString("country");
        etCity = findViewById(R.id.city_name);
        etCity.setText(city);
        etCountry = findViewById(R.id.country_name);
        etCountry.setText(country);

        // Set remaining view IDs
        ivIcon = findViewById(R.id.icon);
        temperature = findViewById(R.id.temperature);
        tvResult = findViewById(R.id.weatherResults);

        // Show weather of country capital from SearchActivity
        String initWeatherCall = getApiUrl();
        GetWeatherAsync initialCall = new GetWeatherAsync();
        initialCall.execute(initWeatherCall);

        // Set onClickListener so user can change cities and get updated weather info
        refreshBtn = findViewById(R.id.getWeatherBtn);
        refreshBtn.setOnClickListener(view -> {
            String updateURL = getApiUrl();
            GetWeatherAsync updateWeather = new GetWeatherAsync();
            updateWeather.execute(updateURL);
        });
    }

    /**
     * Reads EditText views and creates a URL for openWeatherAPI call
     * @return String
     */
    @SuppressLint("SetTextI18n")
    private String getApiUrl() {
        String tempUrl = "";
        String cityName = etCity.getText().toString().trim();
        if (cityName.equals("")) {
            tvResult.setText("City field can not be empty!");
        } else {
            // API info
            String URL = "http://api.openweathermap.org/data/2.5/weather";
            String WEATHERKEY = BuildConfig.WEATHER_KEY;
            tempUrl = URL + "?q=" + cityName + "&appid=" + WEATHERKEY;
        }
        return tempUrl;
    }

    /**
     * AsyncTask inner class for getting weather info (used in onCreate and refreshBtn)
     */
    private class GetWeatherAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(WeatherActivity.this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0],
                    null, response -> {
                        try {
                            /* Create new JSONObjects and JSONArrays as necessary
                            (see online JSON object to verify array vs object)
                             */
                            String output = "";
                            JSONObject jsonObjectMain = response.getJSONObject("main");
                            JSONArray jsonArray = response.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                            JSONObject jsonObjectWind = response.getJSONObject("wind");
                            JSONObject jsonObjectClouds = response.getJSONObject("clouds");

                            /* Get the icon code jsonObjectWeather, plug it into the necessary URL,
                            use Glide library to easily replace the img in ImageView with
                            the image at the URL
                             */
                            String iconCode = jsonObjectWeather.getString("icon");
                            String iconUrl = "http://openweathermap.org/img/wn/";
                            iconUrl += iconCode + "@4x.png";
                            Glide.with(WeatherActivity.this).load(iconUrl).into(ivIcon);

                            // Get and store relevant data from your JSONObjects
                            String description = jsonObjectWeather.getString("description");
                            double temp = jsonObjectMain.getDouble("temp") - 273.15;
                            double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                            float pressure = jsonObjectMain.getInt("pressure");
                            int humidity = jsonObjectMain.getInt("humidity");
                            String wind = jsonObjectWind.getString("speed");
                            String clouds = jsonObjectClouds.getString("all");
                            String city1 = response.getString("name");

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
                    }, error -> Toast.makeText(WeatherActivity.this, error.toString(),
                    Toast.LENGTH_SHORT).show());
            queue.add(request);
            return null;
        }
    }
}