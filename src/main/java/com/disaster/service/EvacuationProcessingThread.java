package com.disaster.service;

import com.disaster.database.DBConnection;
import com.disaster.model.Evacuation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class EvacuationProcessingThread implements Runnable {

    private final AtomicBoolean running =
            new AtomicBoolean(false);

    private Thread processingThread;

    private int previousPendingCount = -1;

    // ==========================================
    // GUI EVACUATION LISTENER
    // ==========================================

    private Consumer<Evacuation> evacuationListener;


    // ==========================================
    // SET LISTENER
    // ==========================================

    public void setEvacuationListener(
            Consumer<Evacuation> listener
    ) {

        this.evacuationListener = listener;
    }


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
                    // NEW PENDING EVACUATION
                    // ==========================================

                    if (pendingCount >
                            previousPendingCount) {

                        System.out.println(
                                "[Evacuation Processing] " +
                                        "New evacuation assignment detected."
                        );

                        Evacuation evacuation =
                                getLatestPendingEvacuation();

                        if (evacuation != null) {

                            System.out.println(
                                    "[Evacuation Processing] " +
                                            "Evacuation ID: "
                                            + evacuation.getEvacuationId()
                            );

                            // ==================================
                            // SEND TO GUI
                            // ==================================

                            if (evacuationListener != null) {

                                evacuationListener.accept(
                                        evacuation
                                );
                            }
                        }
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
    // GET LATEST PENDING EVACUATION
    // ==========================================

    private Evacuation getLatestPendingEvacuation() {

        String sql =
                "SELECT * " +
                        "FROM evacuations " +
                        "WHERE status = 'PENDING' " +
                        "ORDER BY evacuation_id DESC " +
                        "LIMIT 1";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                Evacuation evacuation =
                        new Evacuation();

                evacuation.setEvacuationId(
                        resultSet.getInt(
                                "evacuation_id"
                        )
                );

                evacuation.setVictimId(
                        resultSet.getInt(
                                "victim_id"
                        )
                );

                evacuation.setShelterId(
                        resultSet.getInt(
                                "shelter_id"
                        )
                );

                evacuation.setResourceId(
                        resultSet.getInt(
                                "resource_id"
                        )
                );

                evacuation.setPriorityScore(
                        resultSet.getInt(
                                "priority_score"
                        )
                );

                evacuation.setStatus(
                        resultSet.getString(
                                "status"
                        )
                );

                /*
                 * evacuation_time is a LocalDateTime
                 * in your Evacuation model.
                 */
                if (resultSet.getTimestamp(
                        "evacuation_time"
                ) != null) {

                    evacuation.setEvacuationTime(
                            resultSet.getTimestamp(
                                    "evacuation_time"
                            ).toLocalDateTime()
                    );
                }

                return evacuation;
            }

        } catch (Exception e) {

            System.err.println(
                    "[Evacuation Processing] " +
                            "Unable to load latest evacuation: "
                            + e.getMessage()
            );
        }

        return null;
    }


    // ==========================================
    // STATUS
    // ==========================================

    public boolean isRunning() {

        return running.get();
    }
}