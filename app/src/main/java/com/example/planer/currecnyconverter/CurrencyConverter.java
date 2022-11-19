package com.example.planer.currecnyconverter;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

public class CurrencyConverter {

    private final String DEFAULT_HOME = "Canada";
    private final String DEFAULT_DESTINATION = "Vietnam";
    private final double DEFAULT_RATE_HD = 1.25;
    private final double DEFAULT_RATE_DH = 1 / DEFAULT_RATE_HD;
    static final String API_KEY = "NhsgcPMeSuheM59uUjhJzNvl54oDlIs3";
    static final int SIGFIGS = 3;

    private String home;
    private String destination;
    private String homeAbrev;
    private String destinationAbrev;
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
        this.destinationRate = DEFAULT_RATE_DH;
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
        this.homeRate = rate;
        this.destinationRate = rate;
    }

    // Home to Destination
    public double convertHD(double num) {
        return num * this.homeRate;
    }

    // Destination to Home
    public double convertDH(double num) {
        return num * this.destinationRate;
    }


    public static String sigFigs(double num) {
        MathContext mathContext = new MathContext(3, RoundingMode.DOWN);
        BigDecimal bigDecimal = new BigDecimal(num,mathContext);
        return bigDecimal.toPlainString();
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
     * This function became extra nasty. I don't like how much time this took.
     *
     * @return
     */
    private void getRateAPI(Context context, Runnable callBack) {

        // RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(context);

        // We use Locale to convert country name to 3 letter abbreviation
        // Create a collection of all available countries
        Map<String, String> countries = new HashMap<>();

        // Map ISO countries to country name
        // TODO must make currency code filter faster.
        String[] countryCodes = Locale.getISOCountries();
        for (String countryCode : countryCodes) {
            Locale locale = new Locale("", countryCode);
            Currency c = Currency.getInstance(locale);
            if (c != null) {
                countries.put(locale.getDisplayCountry(), Currency.getInstance(locale).getCurrencyCode());
            }
        }

        this.homeAbrev = countries.get(this.home);
        this.destinationAbrev = countries.get(this.destination);

        String url = "https://api.apilayer.com/exchangerates_data/latest?symbols=" + this.destinationAbrev + "&base=" + this.homeAbrev;

        mStringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // response
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONObject rates = json.getJSONObject("rates");
                        // We are only getting one rate.
                        double rate = rates.getDouble(destinationAbrev);
                        setRate(rate);
                        callBack.run();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    if (error == null || error.networkResponse == null) {
                        return;
                    }

                    String body = "";
                    final String statusCode = String.valueOf(error.networkResponse.statusCode);
                    try {
                        body = new String(error.networkResponse.data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        // exception
                    }
                    Log.d("ERROR", "error => Status code: " + statusCode + ", " + body);
                }
        )

                // I don't know how this works :( I am ashamed.
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("apikey", API_KEY);
                return params;
            }
        };

        mRequestQueue.add(mStringRequest);
    }

    public String toString() {
        return this.homeAbrev + " - " + this.destinationAbrev + ": " + sigFigs(this.getRate());
    }
}


