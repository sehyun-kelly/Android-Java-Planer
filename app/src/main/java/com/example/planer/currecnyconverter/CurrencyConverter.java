package com.example.planer.currecnyconverter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class CurrencyConverter {

    private final String DEFAULT_HOME = "Canada";
    private final String DEFAULT_DESTINATION = "Vietnam";
    private final double DEFAULT_RATE_HD = 1.25;
    private final double DEFAUL_RATE_DH = 1 / DEFAULT_RATE_HD;
    static final String API_KEY = "vd7qdS6VOHYSw4P8LybvPR2HPsbxzecc";

    private String home;
    private String destination;
    private double homeRate;
    private double destinationRate;

    // For API Call
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    // Default constructor for quick testing.
    public CurrencyConverter() {
        this.home = DEFAULT_HOME;
        this.destination = DEFAULT_DESTINATION;
        this.homeRate = DEFAULT_RATE_HD;
        this.destinationRate = DEFAUL_RATE_DH;
    }

    public CurrencyConverter(String home, String destination) {
        this.home = home;
        this.destination = destination;
        this.homeRate = getRate();
        this.destinationRate = 1 / this.homeRate;
    }

    public CurrencyConverter(String home, String destination, double homeRate) {
        this.home = home;
        this.destination = destination;
        this.homeRate = homeRate;
        this.destinationRate = 1 / homeRate;
    }

    public String getDestination() {
        return destination;
    }

    public String getHome() {
        return this.home;
    }

    public double getRate() {
        return this.homeRate;
    }

    private void setRate(double rate) {
        Log.d("Android", "Success rate: " + rate);
        this.homeRate = rate;
        this.destinationRate = 1 / rate;
    }

    // Home to Destination
    public double convertHD(double num) {
        return num * this.homeRate;
    }

    // Destination to Home
    public double convertDH(double num) {
        return num * this.destinationRate;
    }

    public void updateRate(Context context, Runnable callBack) {
        getRateAPI(context, callBack);
    }



    public void setHome(String home) {
        this.home = home;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * TODO
     *
     * I am going to black box this for now.
     * This function will be accessing the API
     * @return
     */
    private void getRateAPI(Context context, Runnable callBack) {

        // RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(context);

        // We use Locale to convert country name to 3 letter abbreviation
        // Create a collection of all available countries
        Map<String, String> countries = new HashMap<>();

        // Map ISO countries to country name
        String[] countryCodes = Locale.getISOCountries();
        for (String countryCode : countryCodes){
            Locale locale = new Locale("", countryCode);
            String iso = locale.getISO3Country();
            String code = locale.getCountry();
            String name = locale.getDisplayCountry();

            Currency c = Currency.getInstance(locale);
            if (c != null) {
                countries.put(locale.getDisplayCountry(), Currency.getInstance(locale).getCurrencyCode());
            }
        }

        String homeAbbreviation = countries.get(this.home);
        String destinationAbbreviation = countries.get(this.destination);

        String url ="https://api.apilayer.com/exchangerates_data/latest?symbols=" + destinationAbbreviation + "&base=" + homeAbbreviation;

        mStringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try {
                    JSONObject json = new JSONObject(response);
                    JSONObject rates = json.getJSONObject("rates");
                    // We are only getting one rate.
                    double rate = rates.getDouble(destinationAbbreviation);
                    setRate(rate);
                    callBack.run();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }},
            new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+ error.toString());
                    }
                }
        )

        // I don't know how this works :( I am ashamed.
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("apikey", API_KEY);
                return params;
            }
        };

        mRequestQueue.add(mStringRequest);

    }
}


