package com.disaster.service;

import com.disaster.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class EvacuationProcessingThread implements Runnable {

    private final AtomicBoolean running =
            new AtomicBoolean(false);

    private Thread processingThread;

    private int previousPendingCount = -1;


    // ==========================================
    // START PROCESSING
    // ==========================================

    public synchronized void startProcessing() {

        if (running.get()) {
            return;
        }

        running.set(true);

        processingThread =
                new Thread(
                        this,
                        "Evacuation-Processing-Thread"
                );

        processingThread.setDaemon(true);

        processingThread.start();

        System.out.println(
                "[Evacuation Processing] Thread started."
        );
    }


    // ==========================================
    // STOP PROCESSING
    // ==========================================

    public synchronized void stopProcessing() {

        running.set(false);

        if (processingThread != null) {
            processingThread.interrupt();
        }

        System.out.println(
                "[Evacuation Processing] Thread stopped."
        );
    }


    // ==========================================
    // RUN
    // ==========================================

    @Override
    public void run() {

        while (running.get()) {

            try {

                checkPendingEvacuations();

                Thread.sleep(10000);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                break;

            } catch (Exception e) {

                System.err.println(
                        "[Evacuation Processing] Error: "
                                + e.getMessage()
                );

                try {

                    Thread.sleep(10000);

                } catch (InterruptedException interruptedException) {

                    Thread.currentThread().interrupt();

                    break;
                }
            }
        }

        running.set(false);

        System.out.println(
                "[Evacuation Processing] Thread exited."
        );
    }


    // ==========================================
    // CHECK PENDING EVACUATIONS
    // ==========================================

    private void checkPendingEvacuations() {

        String sql =
                "SELECT COUNT(*) " +
                        "FROM evacuations " +
                        "WHERE status = 'PENDING'";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                int pendingCount =
                        resultSet.getInt(1);


                // ==========================================
                // FIRST CHECK
                // ==========================================

                if (previousPendingCount == -1) {

                    previousPendingCount =
                            pendingCount;

                    System.out.println(
                            "[Evacuation Processing] " +
                                    "Pending evacuations: "
                                    + pendingCount
                    );

                    return;
                }


                // ==========================================
                // DETECT CHANGE
                // ==========================================

                if (pendingCount !=
                        previousPendingCount) {

                    System.out.println(
                            "[Evacuation Processing] " +
                                    "Pending evacuation count changed."
                    );

                    System.out.println(
                            "[Evacuation Processing] " +
                                    "Previous pending: "
                                    + previousPendingCount
                    );

                    System.out.println(
                            "[Evacuation Processing] " +
                                    "Current pending: "
                                    + pendingCount
                    );


                    // ==========================================
                    // NEW PENDING EVACUATIONS
                    // ==========================================

                    if (pendingCount >
                            previousPendingCount) {

                        System.out.println(
                                "[Evacuation Processing] " +
                                        "New evacuation assignment detected."
                        );
                    }


                    // ==========================================
                    // PENDING EVACUATIONS REDUCED
                    // ==========================================

                    if (pendingCount <
                            previousPendingCount) {

                        System.out.println(
                                "[Evacuation Processing] " +
                                        "Pending evacuation processed."
                        );
                    }


                    previousPendingCount =
                            pendingCount;
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "[Evacuation Processing] " +
                            "Database check failed: "
                            + e.getMessage()
            );
        }
    }


    // ==========================================
    // STATUS
    // ==========================================

    public boolean isRunning() {

        return running.get();
    }
}