package com.disaster.service;

import com.disaster.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class DisasterMonitoringThread implements Runnable {

    private final AtomicBoolean running = new AtomicBoolean(false);

    private Thread monitoringThread;

    private int previousActiveCount = -1;


    // ==========================================
    // START MONITORING
    // ==========================================

    public synchronized void startMonitoring() {

        if (running.get()) {
            return;
        }

        running.set(true);

        monitoringThread =
                new Thread(
                        this,
                        "Disaster-Monitoring-Thread"
                );

        monitoringThread.setDaemon(true);

        monitoringThread.start();

        System.out.println(
                "[Disaster Monitoring] Thread started."
        );
    }


    // ==========================================
    // STOP MONITORING
    // ==========================================

    public synchronized void stopMonitoring() {

        running.set(false);

        if (monitoringThread != null) {
            monitoringThread.interrupt();
        }

        System.out.println(
                "[Disaster Monitoring] Thread stopped."
        );
    }


    // ==========================================
    // RUN
    // ==========================================

    @Override
    public void run() {

        while (running.get()) {

            try {

                checkDisasters();

                Thread.sleep(10000);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                break;

            } catch (Exception e) {

                System.err.println(
                        "[Disaster Monitoring] Error: "
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
                "[Disaster Monitoring] Thread exited."
        );
    }


    // ==========================================
    // CHECK DISASTERS
    // ==========================================

    private void checkDisasters() {

        String sql =
                "SELECT COUNT(*) " +
                        "FROM disasters " +
                        "WHERE status = 'ACTIVE'";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                int activeCount =
                        resultSet.getInt(1);


                // ==========================================
                // FIRST CHECK
                // ==========================================

                if (previousActiveCount == -1) {

                    previousActiveCount =
                            activeCount;

                    System.out.println(
                            "[Disaster Monitoring] " +
                                    "Active disasters: "
                                    + activeCount
                    );

                    return;
                }


                // ==========================================
                // DETECT CHANGE
                // ==========================================

                if (activeCount != previousActiveCount) {

                    System.out.println(
                            "[Disaster Monitoring] " +
                                    "Disaster status changed."
                    );

                    System.out.println(
                            "[Disaster Monitoring] " +
                                    "Previous active disasters: "
                                    + previousActiveCount
                    );

                    System.out.println(
                            "[Disaster Monitoring] " +
                                    "Current active disasters: "
                                    + activeCount
                    );


                    if (activeCount >
                            previousActiveCount) {

                        System.out.println(
                                "[Disaster Monitoring] " +
                                        "ALERT: New active disaster detected."
                        );
                    }


                    if (activeCount <
                            previousActiveCount) {

                        System.out.println(
                                "[Disaster Monitoring] " +
                                        "An active disaster has been resolved."
                        );
                    }


                    previousActiveCount =
                            activeCount;
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "[Disaster Monitoring] " +
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