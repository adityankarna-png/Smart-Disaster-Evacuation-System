package com.disaster.service;

import com.disaster.dao.ReportDAO;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReportingServiceThread implements Runnable {

    private final AtomicBoolean running =
            new AtomicBoolean(false);

    private Thread reportingThread;

    private final ReportDAO reportDAO;

    public ReportingServiceThread() {

        reportDAO = new ReportDAO();
    }


    // ==========================================
    // START REPORTING SERVICE
    // ==========================================

    public synchronized void startReportingService() {

        if (running.get()) {
            return;
        }

        running.set(true);

        reportingThread =
                new Thread(
                        this,
                        "Reporting-Service-Thread"
                );

        reportingThread.setDaemon(true);

        reportingThread.start();

        System.out.println(
                "[Reporting Service] Thread started."
        );
    }


    // ==========================================
    // STOP REPORTING SERVICE
    // ==========================================

    public synchronized void stopReportingService() {

        running.set(false);

        if (reportingThread != null) {
            reportingThread.interrupt();
        }

        System.out.println(
                "[Reporting Service] Thread stopped."
        );
    }


    // ==========================================
    // RUN
    // ==========================================

    @Override
    public void run() {

        while (running.get()) {

            try {

                generateReport();

                Thread.sleep(30000);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                break;

            } catch (Exception e) {

                System.err.println(
                        "[Reporting Service] Error: "
                                + e.getMessage()
                );

                try {

                    Thread.sleep(30000);

                } catch (InterruptedException interruptedException) {

                    Thread.currentThread().interrupt();

                    break;
                }
            }
        }

        running.set(false);

        System.out.println(
                "[Reporting Service] Thread exited."
        );
    }


    // ==========================================
    // GENERATE SYSTEM REPORT
    // ==========================================

    private void generateReport() {

        int totalDisasters =
                reportDAO.getTotalDisasters();

        int activeDisasters =
                reportDAO.getActiveDisasters();

        int resolvedDisasters =
                reportDAO.getResolvedDisasters();


        int totalVictims =
                reportDAO.getTotalVictims();

        int highPriorityVictims =
                reportDAO.getHighPriorityVictims();

        int waitingVictims =
                reportDAO.getWaitingVictims();


        int totalShelters =
                reportDAO.getTotalShelters();

        int availableShelters =
                reportDAO.getAvailableShelters();

        int fullShelters =
                reportDAO.getFullShelters();


        int totalResources =
                reportDAO.getTotalResources();

        int availableResources =
                reportDAO.getAvailableResources();

        int outOfStockResources =
                reportDAO.getOutOfStockResources();


        int totalEvacuations =
                reportDAO.getTotalEvacuations();

        int pendingEvacuations =
                reportDAO.getPendingEvacuations();

        int completedEvacuations =
                reportDAO.getCompletedEvacuations();


        int shelterCapacity =
                reportDAO.getTotalShelterCapacity();

        int shelterOccupancy =
                reportDAO.getCurrentShelterOccupancy();

        int resourceQuantity =
                reportDAO.getTotalResourceQuantity();


        // ==========================================
        // DISPLAY REPORT
        // ==========================================

        System.out.println();

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "       AUTOMATED SYSTEM REPORT"
        );

        System.out.println(
                "=========================================="
        );


        System.out.println(
                "DISASTERS"
        );

        System.out.println(
                "Total       : " +
                        totalDisasters
        );

        System.out.println(
                "Active      : " +
                        activeDisasters
        );

        System.out.println(
                "Resolved    : " +
                        resolvedDisasters
        );


        System.out.println();

        System.out.println(
                "VICTIMS"
        );

        System.out.println(
                "Total       : " +
                        totalVictims
        );

        System.out.println(
                "High Priority: " +
                        highPriorityVictims
        );

        System.out.println(
                "Waiting     : " +
                        waitingVictims
        );


        System.out.println();

        System.out.println(
                "SHELTERS"
        );

        System.out.println(
                "Total       : " +
                        totalShelters
        );

        System.out.println(
                "Available   : " +
                        availableShelters
        );

        System.out.println(
                "Full        : " +
                        fullShelters
        );

        System.out.println(
                "Capacity    : " +
                        shelterCapacity
        );

        System.out.println(
                "Occupancy   : " +
                        shelterOccupancy
        );


        System.out.println();

        System.out.println(
                "RESOURCES"
        );

        System.out.println(
                "Types       : " +
                        totalResources
        );

        System.out.println(
                "Available   : " +
                        availableResources
        );

        System.out.println(
                "Out of Stock: " +
                        outOfStockResources
        );

        System.out.println(
                "Quantity    : " +
                        resourceQuantity
        );


        System.out.println();

        System.out.println(
                "EVACUATIONS"
        );

        System.out.println(
                "Total       : " +
                        totalEvacuations
        );

        System.out.println(
                "Pending     : " +
                        pendingEvacuations
        );

        System.out.println(
                "Completed   : " +
                        completedEvacuations
        );


        System.out.println();

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "       END OF AUTOMATED REPORT"
        );

        System.out.println(
                "=========================================="
        );

        System.out.println();
    }


    // ==========================================
    // STATUS
    // ==========================================

    public boolean isRunning() {

        return running.get();
    }
}