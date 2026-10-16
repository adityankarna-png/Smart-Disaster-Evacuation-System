package com.disaster.service;

import com.disaster.earthquake.EarthquakeData;
import com.disaster.earthquake.EarthquakeMonitor;
import com.disaster.weather.WeatherMonitor;
import com.disaster.model.Evacuation;

import java.util.function.Consumer;

public class SystemMonitor {

    // ==========================================
    // MONITORING SERVICES
    // ==========================================

    private final DisasterMonitoringThread
            disasterMonitoring;

    private final ResourceMonitoringThread
            resourceMonitoring;

    private final EvacuationProcessingThread
            evacuationProcessing;

    private final NotificationServiceThread
            notificationService;

    private final ReportingServiceThread
            reportingService;

    private final WeatherMonitor
            weatherMonitor;

    private final EarthquakeMonitor
            earthquakeMonitor;


    // ==========================================
    // RUNNING STATUS
    // ==========================================

    private boolean running;


    // ==========================================
    // LATEST EARTHQUAKE
    // ==========================================

    /*
     * Static so the latest earthquake remains available
     * even when a new SystemMonitor is created after
     * logout/login.
     */
    private static volatile EarthquakeData latestEarthquake;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public SystemMonitor() {

        disasterMonitoring =
                new DisasterMonitoringThread();

        resourceMonitoring =
                new ResourceMonitoringThread();

        evacuationProcessing =
                new EvacuationProcessingThread();

        notificationService =
                new NotificationServiceThread();

        reportingService =
                new ReportingServiceThread();

        weatherMonitor =
                new WeatherMonitor();

        earthquakeMonitor =
                new EarthquakeMonitor();


        // ======================================
        // EARTHQUAKE → NOTIFICATION
        // ======================================

        earthquakeMonitor.setEarthquakeListener(
                notificationService::notifyEarthquake
        );

        running = false;
    }


    // ==========================================
    // START ALL SERVICES
    // ==========================================

    public synchronized void startAll() {

        if (running) {

            System.out.println(
                    "[System Monitor] "
                            + "Services are already running."
            );

            return;
        }

        System.out.println();

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "       STARTING SYSTEM MONITOR"
        );

        System.out.println(
                "=========================================="
        );


        disasterMonitoring.startMonitoring();

        resourceMonitoring.startMonitoring();

        evacuationProcessing.startProcessing();

        notificationService.startNotificationService();

        reportingService.startReportingService();

        weatherMonitor.startMonitoring();

        earthquakeMonitor.startMonitoring();


        running = true;


        System.out.println(
                "[System Monitor] "
                        + "All background services started."
        );

        System.out.println(
                "=========================================="
        );

        System.out.println();
    }


    // ==========================================
    // STOP ALL SERVICES
    // ==========================================

    public synchronized void stopAll() {

        if (!running) {
            return;
        }

        System.out.println();

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "       STOPPING SYSTEM MONITOR"
        );

        System.out.println(
                "=========================================="
        );


        disasterMonitoring.stopMonitoring();

        resourceMonitoring.stopMonitoring();

        evacuationProcessing.stopProcessing();

        notificationService.stopNotificationService();

        reportingService.stopReportingService();

        weatherMonitor.stopMonitoring();

        earthquakeMonitor.stopMonitoring();


        running = false;


        System.out.println(
                "[System Monitor] "
                        + "All background services stopped."
        );

        System.out.println(
                "=========================================="
        );

        System.out.println();
    }


    // ==========================================
    // STATUS
    // ==========================================

    public synchronized boolean isRunning() {

        return running;
    }


    // ==========================================
    // INDIVIDUAL STATUS
    // ==========================================

    public boolean isDisasterMonitoringRunning() {

        return disasterMonitoring.isRunning();
    }


    public boolean isResourceMonitoringRunning() {

        return resourceMonitoring.isRunning();
    }


    public boolean isEvacuationProcessingRunning() {

        return evacuationProcessing.isRunning();
    }


    public boolean isNotificationServiceRunning() {

        return notificationService.isRunning();
    }


    public boolean isReportingServiceRunning() {

        return reportingService.isRunning();
    }


    public boolean isWeatherMonitoringRunning() {

        return weatherMonitor.isRunning();
    }


    public boolean isEarthquakeMonitoringRunning() {

        return earthquakeMonitor.isRunning();
    }


    // ==========================================
    // GUI EVACUATION LISTENER
    // ==========================================

    public void setEvacuationListener(
            Consumer<Evacuation> listener
    ) {

        evacuationProcessing.setEvacuationListener(
                listener
        );
    }


    // ==========================================
    // GUI EARTHQUAKE ALERT LISTENER
    // ==========================================

    public void setEarthquakeAlertListener(
            Consumer<EarthquakeData> listener
    ) {

        /*
         * NotificationService sends the earthquake
         * event to this listener.
         *
         * We store the latest earthquake here so that
         * a newly created MainFrame/UserDashboard can
         * still see the previous alert.
         */
        notificationService.setEarthquakeAlertListener(
                earthquake -> {

                    if (earthquake != null) {

                        latestEarthquake = earthquake;
                    }

                    if (listener != null) {

                        listener.accept(earthquake);
                    }
                }
        );


        /*
         * If an earthquake already occurred before this
         * MainFrame was created, immediately send it
         * to the new GUI.
         */
        if (latestEarthquake != null &&
                listener != null) {

            listener.accept(
                    latestEarthquake
            );
        }
    }


    // ==========================================
    // GET LATEST EARTHQUAKE
    // ==========================================

    public EarthquakeData getLatestEarthquake() {

        /*
         * Return the globally retained latest earthquake
         * instead of depending only on the current
         * EarthquakeMonitor instance.
         */
        if (latestEarthquake != null) {

            return latestEarthquake;
        }

        return earthquakeMonitor.getLatestEarthquake();
    }


    // ==========================================
    // TEST EARTHQUAKE ALERT
    // ==========================================

    public void triggerTestEarthquakeAlert() {

        notificationService.triggerTestEarthquakeAlert();
    }
}