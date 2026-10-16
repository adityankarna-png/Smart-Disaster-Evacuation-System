package com.disaster.weather;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherService {

    private final HttpClient httpClient;

    public WeatherService() {

        httpClient = HttpClient.newHttpClient();
    }


    // ==========================================
    // GET CURRENT WEATHER
    // ==========================================

    public WeatherData getCurrentWeather(
            String location,
            double latitude,
            double longitude
    ) throws IOException, InterruptedException {

        String url =
                "https://api.open-meteo.com/v1/forecast"
                        + "?latitude=" + latitude
                        + "&longitude=" + longitude
                        + "&current=temperature_2m"
                        + ",relative_humidity_2m"
                        + ",precipitation"
                        + ",wind_speed_10m"
                        + ",weather_code"
                        + "&timezone=auto";


        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();


        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );


        if (response.statusCode() != 200) {

            throw new IOException(
                    "Weather API returned HTTP "
                            + response.statusCode()
            );
        }


        return parseWeatherResponse(
                location,
                response.body()
        );
    }


    // ==========================================
    // PARSE RESPONSE
    // ==========================================

    private WeatherData parseWeatherResponse(
            String location,
            String json
    ) {

        /*
         * Open-Meteo contains both:
         *
         * current_units
         *
         * and
         *
         * current
         *
         * Both contain fields such as
         * temperature_2m.
         *
         * Therefore we first isolate the
         * current object.
         */

        int currentPosition =
                json.indexOf("\"current\"");

        if (currentPosition == -1) {

            throw new IllegalArgumentException(
                    "Current weather data not found."
            );
        }


        int objectStart =
                json.indexOf(
                        "{",
                        currentPosition
                );

        if (objectStart == -1) {

            throw new IllegalArgumentException(
                    "Invalid current weather object."
            );
        }


        String currentJson =
                json.substring(objectStart);


        // ======================================
        // CURRENT WEATHER VALUES
        // ======================================

        double temperature =
                extractDouble(
                        currentJson,
                        "\"temperature_2m\":"
                );


        double humidity =
                extractDouble(
                        currentJson,
                        "\"relative_humidity_2m\":"
                );


        double precipitation =
                extractDouble(
                        currentJson,
                        "\"precipitation\":"
                );


        double windSpeed =
                extractDouble(
                        currentJson,
                        "\"wind_speed_10m\":"
                );


        int weatherCode =
                (int) extractDouble(
                        currentJson,
                        "\"weather_code\":"
                );


        String time =
                extractString(
                        currentJson,
                        "\"time\":\""
                );


        return new WeatherData(
                location,
                temperature,
                humidity,
                precipitation,
                windSpeed,
                weatherCode,
                time
        );
    }


    // ==========================================
    // EXTRACT DOUBLE
    // ==========================================

    private double extractDouble(
            String json,
            String key
    ) {

        int keyPosition =
                json.indexOf(key);


        if (keyPosition == -1) {

            throw new IllegalArgumentException(
                    "Weather field not found: "
                            + key
            );
        }


        int start =
                keyPosition + key.length();


        // Skip whitespace
        while (
                start < json.length()
                        &&
                        Character.isWhitespace(
                                json.charAt(start)
                        )
        ) {

            start++;
        }


        // Handle null
        if (
                json.startsWith(
                        "null",
                        start
                )
        ) {

            return 0.0;
        }


        int end = start;


        while (
                end < json.length()
                        &&
                        (
                                Character.isDigit(
                                        json.charAt(end)
                                )
                                        ||
                                        json.charAt(end) == '.'
                                        ||
                                        json.charAt(end) == '-'
                                        ||
                                        json.charAt(end) == '+'
                                        ||
                                        json.charAt(end) == 'E'
                                        ||
                                        json.charAt(end) == 'e'
                        )
        ) {

            end++;
        }


        String value =
                json.substring(
                        start,
                        end
                ).trim();


        if (value.isEmpty()) {

            throw new IllegalArgumentException(
                    "Empty value received for: "
                            + key
            );
        }


        return Double.parseDouble(value);
    }


    // ==========================================
    // EXTRACT STRING
    // ==========================================

    private String extractString(
            String json,
            String key
    ) {

        int keyPosition =
                json.indexOf(key);


        if (keyPosition == -1) {

            throw new IllegalArgumentException(
                    "Weather field not found: "
                            + key
            );
        }


        int start =
                keyPosition + key.length();


        int end =
                json.indexOf(
                        "\"",
                        start
                );


        if (end == -1) {

            throw new IllegalArgumentException(
                    "Invalid weather response."
            );
        }


        return json.substring(
                start,
                end
        );
    }
}