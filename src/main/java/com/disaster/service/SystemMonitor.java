package com.disaster.service;

public class SystemMonitor {

    // ==========================================
    // MONITORING SERVICES
    // ==========================================

    private final DisasterMonitoringThread disasterMonitoring;

    private final ResourceMonitoringThread resourceMonitoring;

    private final EvacuationProcessingThread evacuationProcessing;

    private final NotificationServiceThread notificationService;

    private final ReportingServiceThread reportingService;


    // ==========================================
    // RUNNING STATUS
    // ==========================================

    private boolean running;


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

        running = false;
    }


    // ==========================================
    // START ALL SERVICES
    // ==========================================

    public synchronized void startAll() {

        if (running) {

            System.out.println(
                    "[System Monitor] " +
                            "Services are already running."
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


        running = true;


        System.out.println(
                "[System Monitor] " +
                        "All background services started."
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


        running = false;


        System.out.println(
                "[System Monitor] " +
                        "All background services stopped."
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
}