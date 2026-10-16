package com.disaster.earthquake;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeService {

    // ==========================================
    // USGS FEED
    // ==========================================

    private static final String USGS_URL =
            "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_day.geojson";


    // ==========================================
    // HTTP CLIENT
    // ==========================================

    private final HttpClient httpClient;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public EarthquakeService() {

        httpClient =
                HttpClient.newBuilder()
                        .connectTimeout(
                                Duration.ofSeconds(10)
                        )
                        .build();
    }


    // ==========================================
    // GET EARTHQUAKES
    // ==========================================

    public List<EarthquakeData> getRecentEarthquakes()
            throws IOException, InterruptedException {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(USGS_URL))
                        .timeout(
                                Duration.ofSeconds(15)
                        )
                        .GET()
                        .build();


        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );


        if (response.statusCode() != 200) {

            throw new IOException(
                    "USGS API returned HTTP "
                            + response.statusCode()
            );
        }


        return parseEarthquakeResponse(
                response.body()
        );
    }


    // ==========================================
    // PARSE GEOJSON
    // ==========================================

    private List<EarthquakeData> parseEarthquakeResponse(
            String json
    ) {

        List<EarthquakeData> earthquakes =
                new ArrayList<>();


        int featuresPosition =
                json.indexOf("\"features\"");


        if (featuresPosition == -1) {

            throw new IllegalArgumentException(
                    "USGS earthquake features not found."
            );
        }


        int searchPosition =
                featuresPosition;


        while (true) {

            int featurePosition =
                    json.indexOf(
                            "\"type\":\"Feature\"",
                            searchPosition
                    );


            if (featurePosition == -1) {

                break;
            }


            int nextFeaturePosition =
                    json.indexOf(
                            "\"type\":\"Feature\"",
                            featurePosition + 1
                    );


            String feature;


            if (nextFeaturePosition == -1) {

                feature =
                        json.substring(
                                featurePosition
                        );

            } else {

                feature =
                        json.substring(
                                featurePosition,
                                nextFeaturePosition
                        );
            }


            try {

                EarthquakeData earthquake =
                        parseFeature(feature);

                if (earthquake != null) {

                    earthquakes.add(
                            earthquake
                    );
                }

            } catch (Exception e) {

                System.err.println(
                        "[Earthquake Service] "
                                + "Unable to parse earthquake."
                );

                System.err.println(
                        e.getMessage()
                );
            }


            if (nextFeaturePosition == -1) {

                break;
            }


            searchPosition =
                    nextFeaturePosition;
        }


        return earthquakes;
    }


    // ==========================================
    // PARSE SINGLE FEATURE
    // ==========================================

    private EarthquakeData parseFeature(
            String feature
    ) {

        String id =
                extractString(
                        feature,
                        "\"id\":\""
                );


        double magnitude =
                extractDouble(
                        feature,
                        "\"mag\":"
                );


        String place =
                extractNullableString(
                        feature,
                        "\"place\":"
                );


        long time =
                (long) extractDouble(
                        feature,
                        "\"time\":"
                );


        double longitude =
                extractCoordinate(
                        feature,
                        0
                );


        double latitude =
                extractCoordinate(
                        feature,
                        1
                );


        double depth =
                extractCoordinate(
                        feature,
                        2
                );


        boolean tsunami =
                extractDouble(
                        feature,
                        "\"tsunami\":"
                ) == 1;


        String alert =
                extractNullableString(
                        feature,
                        "\"alert\":"
                );


        String status =
                extractNullableString(
                        feature,
                        "\"status\":"
                );


        return new EarthquakeData(
                id,
                magnitude,
                place,
                time,
                longitude,
                latitude,
                depth,
                tsunami,
                alert,
                status
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
                    "Field not found: "
                            + key
            );
        }


        int start =
                keyPosition + key.length();


        while (
                start < json.length()
                        &&
                        Character.isWhitespace(
                                json.charAt(start)
                        )
        ) {

            start++;
        }


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
                    "Field not found: "
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
                    "Invalid string field: "
                            + key
            );
        }


        return json.substring(
                start,
                end
        );
    }


    // ==========================================
    // EXTRACT NULLABLE STRING
    // ==========================================

    private String extractNullableString(
            String json,
            String key
    ) {

        int keyPosition =
                json.indexOf(key);


        if (keyPosition == -1) {

            return "UNKNOWN";
        }


        int start =
                keyPosition + key.length();


        while (
                start < json.length()
                        &&
                        Character.isWhitespace(
                                json.charAt(start)
                        )
        ) {

            start++;
        }


        if (
                json.startsWith(
                        "null",
                        start
                )
        ) {

            return "NONE";
        }


        if (
                start >= json.length()
                        ||
                        json.charAt(start) != '"'
        ) {

            return "UNKNOWN";
        }


        start++;


        int end =
                json.indexOf(
                        "\"",
                        start
                );


        if (end == -1) {

            return "UNKNOWN";
        }


        return json.substring(
                start,
                end
        );
    }


    // ==========================================
    // EXTRACT COORDINATE
    // ==========================================

    private double extractCoordinate(
            String feature,
            int index
    ) {

        int coordinatePosition =
                feature.indexOf(
                        "\"coordinates\""
                );


        if (coordinatePosition == -1) {

            throw new IllegalArgumentException(
                    "Coordinates not found."
            );
        }


        int arrayStart =
                feature.indexOf(
                        "[",
                        coordinatePosition
                );


        int arrayEnd =
                feature.indexOf(
                        "]",
                        arrayStart
                );


        if (
                arrayStart == -1
                        ||
                        arrayEnd == -1
        ) {

            throw new IllegalArgumentException(
                    "Invalid coordinates."
            );
        }


        String coordinates =
                feature.substring(
                        arrayStart + 1,
                        arrayEnd
                );


        String[] values =
                coordinates.split(",");


        if (values.length <= index) {

            throw new IllegalArgumentException(
                    "Invalid coordinate index."
            );
        }


        return Double.parseDouble(
                values[index].trim()
        );
    }
}