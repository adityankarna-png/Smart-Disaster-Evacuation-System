package com.disaster.weather;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherMonitor {

    // ==========================================
    // CONFIGURATION
    // ==========================================

    private static final long UPDATE_INTERVAL_MINUTES = 5;

    // Chennai coordinates
    private static final String LOCATION = "Chennai";
    private static final double LATITUDE = 13.0827;
    private static final double LONGITUDE = 80.2707;


    // ==========================================
    // SERVICES
    // ==========================================

    private final WeatherService weatherService;

    private ScheduledExecutorService scheduler;

    private volatile WeatherData latestWeather;

    private volatile boolean running;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public WeatherMonitor() {

        weatherService =
                new WeatherService();

        running = false;
    }


    // ==========================================
    // START MONITORING
    // ==========================================

    public synchronized void startMonitoring() {

        if (running) {

            System.out.println(
                    "[Weather Monitoring] " +
                            "Already running."
            );

            return;
        }


        System.out.println(
                "[Weather Monitoring] " +
                        "Starting weather monitor..."
        );


        scheduler =
                Executors.newSingleThreadScheduledExecutor(
                        runnable -> {

                            Thread thread =
                                    new Thread(
                                            runnable,
                                            "Weather-Monitor"
                                    );

                            thread.setDaemon(true);

                            return thread;
                        }
                );


        running = true;


        // Fetch immediately
        updateWeather();


        // Then update every 5 minutes
        scheduler.scheduleAtFixedRate(
                this::updateWeather,
                UPDATE_INTERVAL_MINUTES,
                UPDATE_INTERVAL_MINUTES,
                TimeUnit.MINUTES
        );


        System.out.println(
                "[Weather Monitoring] " +
                        "Weather monitor started."
        );
    }


    // ==========================================
    // FETCH WEATHER
    // ==========================================

    private void updateWeather() {

        if (!running) {

            return;
        }


        try {

            WeatherData weather =
                    weatherService.getCurrentWeather(
                            LOCATION,
                            LATITUDE,
                            LONGITUDE
                    );


            latestWeather = weather;


            System.out.println();
            System.out.println(
                    "[Weather Monitoring] " +
                            "Live weather updated."
            );

            System.out.println(
                    "[Weather Monitoring] "
                            + "Temperature: "
                            + weather.getTemperature()
                            + " °C"
            );

            System.out.println(
                    "[Weather Monitoring] "
                            + "Humidity: "
                            + weather.getHumidity()
                            + " %"
            );

            System.out.println(
                    "[Weather Monitoring] "
                            + "Wind: "
                            + weather.getWindSpeed()
                            + " km/h"
            );

            System.out.println(
                    "[Weather Monitoring] "
                            + "Weather Code: "
                            + weather.getWeatherCode()
            );

            System.out.println(
                    "[Weather Monitoring] "
                            + "Updated: "
                            + weather.getTime()
            );

        } catch (Exception e) {

            System.err.println(
                    "[Weather Monitoring] "
                            + "Unable to update weather."
            );

            System.err.println(
                    "[Weather Monitoring] "
                            + e.getMessage()
            );
        }
    }


    // ==========================================
    // GET LATEST WEATHER
    // ==========================================

    public WeatherData getLatestWeather() {

        return latestWeather;
    }


    // ==========================================
    // STOP MONITORING
    // ==========================================

    public synchronized void stopMonitoring() {

        if (!running) {

            return;
        }


        System.out.println(
                "[Weather Monitoring] " +
                        "Stopping weather monitor..."
        );


        running = false;


        if (scheduler != null) {

            scheduler.shutdownNow();

            scheduler = null;
        }


        System.out.println(
                "[Weather Monitoring] " +
                        "Weather monitor stopped."
        );
    }


    // ==========================================
    // STATUS
    // ==========================================

    public boolean isRunning() {

        return running;
    }
}