package com.disaster.earthquake;

public class EarthquakeMonitorTest {

    public static void main(String[] args) {

        System.out.println();
        System.out.println(
                "=========================================="
        );
        System.out.println(
                "      STARTING EARTHQUAKE MONITOR TEST"
        );
        System.out.println(
                "=========================================="
        );


        EarthquakeMonitor monitor =
                new EarthquakeMonitor();


        // ==========================================
        // START MONITOR
        // ==========================================

        monitor.startMonitoring();


        System.out.println();

        System.out.println(
                "Earthquake monitor running: "
                        + monitor.isRunning()
        );


        System.out.println(
                "Known earthquakes: "
                        + monitor.getKnownEarthquakeCount()
        );


        // ==========================================
        // WAIT
        // ==========================================

        try {

            Thread.sleep(10000);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }


        // ==========================================
        // STATUS
        // ==========================================

        System.out.println();

        System.out.println(
                "Earthquake monitor running: "
                        + monitor.isRunning()
        );

        System.out.println(
                "Known earthquakes: "
                        + monitor.getKnownEarthquakeCount()
        );


        // ==========================================
        // STOP MONITOR
        // ==========================================

        monitor.stopMonitoring();


        System.out.println();

        System.out.println(
                "Earthquake monitor running: "
                        + monitor.isRunning()
        );


        System.out.println();

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "      EARTHQUAKE MONITOR TEST COMPLETE"
        );

        System.out.println(
                "=========================================="
        );
    }
}