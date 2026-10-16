package com.disaster.earthquake;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EarthquakeTest {

    public static void main(String[] args) {

        System.out.println();
        System.out.println(
                "=========================================="
        );
        System.out.println(
                "       LIVE EARTHQUAKE DATA TEST"
        );
        System.out.println(
                "=========================================="
        );

        EarthquakeService earthquakeService =
                new EarthquakeService();

        try {

            List<EarthquakeData> earthquakes =
                    earthquakeService.getRecentEarthquakes();

            System.out.println();
            System.out.println(
                    "Total earthquakes found: "
                            + earthquakes.size()
            );

            System.out.println();

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss"
                    );


            // ==================================
            // DISPLAY EARTHQUAKES
            // ==================================

            for (
                    int i = 0;
                    i < earthquakes.size();
                    i++
            ) {

                EarthquakeData earthquake =
                        earthquakes.get(i);

                System.out.println(
                        "------------------------------------------"
                );

                System.out.println(
                        "Earthquake #" + (i + 1)
                );

                System.out.println(
                        "ID          : "
                                + earthquake.getId()
                );

                System.out.println(
                        "Magnitude   : "
                                + earthquake.getMagnitude()
                );

                System.out.println(
                        "Location    : "
                                + earthquake.getPlace()
                );

                System.out.println(
                        "Depth       : "
                                + earthquake.getDepth()
                                + " km"
                );

                System.out.println(
                        "Latitude    : "
                                + earthquake.getLatitude()
                );

                System.out.println(
                        "Longitude   : "
                                + earthquake.getLongitude()
                );

                System.out.println(
                        "Tsunami     : "
                                + earthquake.isTsunami()
                );

                System.out.println(
                        "Alert       : "
                                + earthquake.getAlert()
                );

                System.out.println(
                        "Status      : "
                                + earthquake.getStatus()
                );

                System.out.println(
                        "Time        : "
                                + dateFormat.format(
                                new Date(
                                        earthquake.getTime()
                                )
                        )
                );
            }


            System.out.println();
            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "       EARTHQUAKE TEST COMPLETE"
            );

            System.out.println(
                    "=========================================="
            );


        } catch (Exception e) {

            System.err.println();
            System.err.println(
                    "Unable to fetch earthquake data."
            );

            e.printStackTrace();
        }
    }
}