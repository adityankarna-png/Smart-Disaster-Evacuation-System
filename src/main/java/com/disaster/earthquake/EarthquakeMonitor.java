package com.disaster.earthquake;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EarthquakeMonitor {

    // ==========================================
    // CONFIGURATION
    // ==========================================

    private static final long UPDATE_INTERVAL_MINUTES = 1;


    // ==========================================
    // SERVICE
    // ==========================================

    private final EarthquakeService earthquakeService;


    // ==========================================
    // SCHEDULER
    // ==========================================

    private ScheduledExecutorService scheduler;


    // ==========================================
    // KNOWN EARTHQUAKES
    // ==========================================

    private final Set<String> knownEarthquakes;
    private volatile EarthquakeData latestEarthquake;
    private Consumer<EarthquakeData> earthquakeListener;


    // ==========================================
    // RUNNING STATUS
    // ==========================================

    private volatile boolean running;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public EarthquakeMonitor() {

        earthquakeService =
                new EarthquakeService();

        knownEarthquakes =
                new HashSet<>();

        latestEarthquake = null;

        running = false;
    }

    // ==========================================
    // EARTHQUAKE LISTENER
    // ==========================================

    public void setEarthquakeListener(
            Consumer<EarthquakeData> listener
    ) {

        this.earthquakeListener = listener;
    }


    // ==========================================
    // START MONITORING
    // ==========================================

    public synchronized void startMonitoring() {

        if (running) {

            System.out.println(
                    "[Earthquake Monitoring] "
                            + "Already running."
            );

            return;
        }


        System.out.println(
                "[Earthquake Monitoring] "
                        + "Starting earthquake monitor..."
        );


        scheduler =
                Executors.newSingleThreadScheduledExecutor(
                        runnable -> {

                            Thread thread =
                                    new Thread(
                                            runnable,
                                            "Earthquake-Monitor"
                                    );

                            thread.setDaemon(true);

                            return thread;
                        }
                );


        running = true;


        // ======================================
        // INITIAL UPDATE
        // ======================================

        updateEarthquakes();


        // ======================================
        // PERIODIC UPDATE
        // ======================================

        scheduler.scheduleAtFixedRate(
                this::updateEarthquakes,
                UPDATE_INTERVAL_MINUTES,
                UPDATE_INTERVAL_MINUTES,
                TimeUnit.MINUTES
        );


        System.out.println(
                "[Earthquake Monitoring] "
                        + "Earthquake monitor started."
        );
    }


    // ==========================================
    // UPDATE EARTHQUAKES
    // ==========================================

    private void updateEarthquakes() {

        if (!running) {

            return;
        }


        try {

            List<EarthquakeData> earthquakes =
                    earthquakeService
                            .getRecentEarthquakes();


            System.out.println(
                    "[Earthquake Monitoring] "
                            + "Checking "
                            + earthquakes.size()
                            + " recent earthquakes."
            );


            // ==================================
            // FIRST RUN
            // ==================================

            if (knownEarthquakes.isEmpty()) {

                for (
                        EarthquakeData earthquake
                        : earthquakes
                ) {

                    knownEarthquakes.add(
                            earthquake.getId()
                    );
                }

                // Find the latest earthquake for the dashboard.
                if (!earthquakes.isEmpty()) {

                    latestEarthquake =
                            earthquakes.get(0);

                    for (
                            EarthquakeData earthquake
                            : earthquakes
                    ) {

                        if (
                                latestEarthquake == null
                                        || earthquake.getTime()
                                        > latestEarthquake.getTime()
                        ) {

                            latestEarthquake =
                                    earthquake;
                        }
                    }
                }

                System.out.println(
                        "[Earthquake Monitoring] "
                                + "Initial earthquake list loaded: "
                                + knownEarthquakes.size()
                );

                return;
            }


            // ==================================
            // FIND NEW EARTHQUAKES
            // ==================================

            int newEarthquakes = 0;


            for (
                    EarthquakeData earthquake
                    : earthquakes
            ) {

                String earthquakeId =
                        earthquake.getId();


                if (
                        !knownEarthquakes.contains(
                                earthquakeId
                        )
                ) {

                    knownEarthquakes.add(
                            earthquakeId
                    );

                    latestEarthquake =
                            earthquake;

                    newEarthquakes++;

                    printEarthquakeAlert(
                            earthquake
                    );

                    if (earthquakeListener != null) {

                        earthquakeListener.accept(
                                earthquake
                        );
                    }
                }
            }


            // ==================================
            // SUMMARY
            // ==================================

            if (newEarthquakes == 0) {

                System.out.println(
                        "[Earthquake Monitoring] "
                                + "No new earthquakes detected."
                );

            } else {

                System.out.println(
                        "[Earthquake Monitoring] "
                                + newEarthquakes
                                + " new earthquake(s) detected."
                );
            }


        } catch (Exception e) {

            System.err.println(
                    "[Earthquake Monitoring] "
                            + "Unable to update earthquake data."
            );

            System.err.println(
                    "[Earthquake Monitoring] "
                            + e.getMessage()
            );
        }
    }


    // ==========================================
    // EARTHQUAKE ALERT
    // ==========================================

    private void printEarthquakeAlert(
            EarthquakeData earthquake
    ) {

        System.out.println();

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "        🚨 NEW EARTHQUAKE DETECTED"
        );

        System.out.println(
                "=========================================="
        );


        System.out.println(
                "Magnitude : "
                        + earthquake.getMagnitude()
        );


        System.out.println(
                "Location  : "
                        + earthquake.getPlace()
        );


        System.out.println(
                "Depth     : "
                        + earthquake.getDepth()
                        + " km"
        );


        System.out.println(
                "Latitude  : "
                        + earthquake.getLatitude()
        );


        System.out.println(
                "Longitude : "
                        + earthquake.getLongitude()
        );


        System.out.println(
                "Tsunami   : "
                        + earthquake.isTsunami()
        );


        System.out.println(
                "Alert     : "
                        + earthquake.getAlert()
        );


        System.out.println(
                "Status    : "
                        + earthquake.getStatus()
        );


        System.out.println(
                "=========================================="
        );

        System.out.println();
    }


    // ==========================================
    // STOP MONITORING
    // ==========================================

    public synchronized void stopMonitoring() {

        if (!running) {

            return;
        }


        System.out.println(
                "[Earthquake Monitoring] "
                        + "Stopping earthquake monitor..."
        );


        running = false;


        if (scheduler != null) {

            scheduler.shutdownNow();

            scheduler = null;
        }


        System.out.println(
                "[Earthquake Monitoring] "
                        + "Earthquake monitor stopped."
        );
    }


    // ==========================================
    // STATUS
    // ==========================================

    public boolean isRunning() {

        return running;
    }


    // ==========================================
    // KNOWN EARTHQUAKE COUNT
    // ==========================================

    public int getKnownEarthquakeCount() {

        return knownEarthquakes.size();
    }

    public EarthquakeData getLatestEarthquake() {

        return latestEarthquake;
    }
}