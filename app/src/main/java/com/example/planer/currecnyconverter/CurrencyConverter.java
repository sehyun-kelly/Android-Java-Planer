package com.example.planer.currecnyconverter;

public class CurrencyConverter {

    private final String DEFAULT_HOME = "Canada";
    private final String DEFAULT_DESTINATION = "Vietnam";
    private final double DEFAULT_RATE_HD = 1.25;
    private final double DEFAUL_RATE_DH = 0.75;

    private String home;
    private String destination;
    private double homeRate;
    private double destinationRate;


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

    // Home to Destination
    public double convertHD(double num) {
        return num * this.homeRate;
    }

    // Destination to Home
    public double convertDH(double num) {
        return num * this.destinationRate;
    }

    public void updateRate() {
        this.homeRate = getRateAPI();
        this.destinationRate = 1 / this.homeRate;
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
    private double getRateAPI() {
        return DEFAULT_RATE_HD;
    }
}
