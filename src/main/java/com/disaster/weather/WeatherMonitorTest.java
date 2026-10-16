package com.disaster.weather;

public class WeatherMonitorTest {

    public static void main(String[] args) {

        System.out.println();
        System.out.println(
                "=========================================="
        );
        System.out.println(
                "       STARTING WEATHER MONITOR TEST"
        );
        System.out.println(
                "=========================================="
        );


        WeatherMonitor monitor =
                new WeatherMonitor();


        // Start monitoring
        monitor.startMonitoring();


        System.out.println();
        System.out.println(
                "Weather monitor is running: "
                        + monitor.isRunning()
        );


        // Give the monitor time to perform
        // its first weather update.
        try {

            Thread.sleep(10000);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }


        // Display latest weather
        WeatherData weather =
                monitor.getLatestWeather();


        System.out.println();
        System.out.println(
                "=========================================="
        );
        System.out.println(
                "          LATEST WEATHER"
        );
        System.out.println(
                "=========================================="
        );


        if (weather != null) {

            System.out.println(
                    "Location      : "
                            + weather.getLocation()
            );

            System.out.println(
                    "Temperature   : "
                            + weather.getTemperature()
                            + " °C"
            );

            System.out.println(
                    "Humidity      : "
                            + weather.getHumidity()
                            + " %"
            );

            System.out.println(
                    "Precipitation : "
                            + weather.getPrecipitation()
                            + " mm"
            );

            System.out.println(
                    "Wind Speed    : "
                            + weather.getWindSpeed()
                            + " km/h"
            );

            System.out.println(
                    "Weather Code  : "
                            + weather.getWeatherCode()
            );

            System.out.println(
                    "Updated       : "
                            + weather.getTime()
            );

        } else {

            System.out.println(
                    "No weather data received yet."
            );
        }


        System.out.println(
                "=========================================="
        );


        // Stop monitor
        monitor.stopMonitoring();


        System.out.println();
        System.out.println(
                "Weather monitor is running: "
                        + monitor.isRunning()
        );


        System.out.println();
        System.out.println(
                "=========================================="
        );
        System.out.println(
                "       WEATHER MONITOR TEST COMPLETE"
        );
        System.out.println(
                "=========================================="
        );
    }
}