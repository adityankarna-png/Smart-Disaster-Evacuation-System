package com.disaster.weather;

public class WeatherTest {

    public static void main(String[] args) {

        WeatherService weatherService =
                new WeatherService();

        try {

            WeatherData weather =
                    weatherService.getCurrentWeather(
                            "Chennai",
                            13.0827,
                            80.2707
                    );

            System.out.println();
            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "        LIVE WEATHER DATA"
            );

            System.out.println(
                    "=========================================="
            );

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

            System.out.println(
                    "=========================================="
            );
            System.out.println();

        } catch (Exception e) {

            System.err.println(
                    "Unable to fetch live weather data."
            );

            e.printStackTrace();
        }
    }
}