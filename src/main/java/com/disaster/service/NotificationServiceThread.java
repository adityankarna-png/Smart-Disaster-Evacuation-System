package com.disaster.service;

import com.disaster.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class NotificationServiceThread implements Runnable {

    private final AtomicBoolean running =
            new AtomicBoolean(false);

    private Thread notificationThread;

    private int previousActiveDisasters = -1;
    private int previousOutOfStockResources = -1;
    private int previousPendingEvacuations = -1;


    // ==========================================
    // START NOTIFICATION SERVICE
    // ==========================================

    public synchronized void startNotificationService() {

        if (running.get()) {
            return;
        }

        running.set(true);

        notificationThread =
                new Thread(
                        this,
                        "Notification-Service-Thread"
                );

        notificationThread.setDaemon(true);

        notificationThread.start();

        System.out.println(
                "[Notification Service] Thread started."
        );
    }


    // ==========================================
    // STOP NOTIFICATION SERVICE
    // ==========================================

    public synchronized void stopNotificationService() {

        running.set(false);

        if (notificationThread != null) {
            notificationThread.interrupt();
        }

        System.out.println(
                "[Notification Service] Thread stopped."
        );
    }


    // ==========================================
    // RUN
    // ==========================================

    @Override
    public void run() {

        while (running.get()) {

            try {

                checkSystemNotifications();

                Thread.sleep(10000);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                break;

            } catch (Exception e) {

                System.err.println(
                        "[Notification Service] Error: "
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
                "[Notification Service] Thread exited."
        );
    }


    // ==========================================
    // CHECK SYSTEM CONDITIONS
    // ==========================================

    private void checkSystemNotifications() {

        checkActiveDisasters();

        checkOutOfStockResources();

        checkPendingEvacuations();
    }


    // ==========================================
    // ACTIVE DISASTERS
    // ==========================================

    private void checkActiveDisasters() {

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

                int activeDisasters =
                        resultSet.getInt(1);


                if (previousActiveDisasters == -1) {

                    previousActiveDisasters =
                            activeDisasters;

                    return;
                }


                if (activeDisasters >
                        previousActiveDisasters) {

                    showNotification(
                            "NEW DISASTER",
                            "A new active disaster has been detected."
                    );
                }


                if (activeDisasters <
                        previousActiveDisasters) {

                    showNotification(
                            "DISASTER UPDATE",
                            "An active disaster has been resolved."
                    );
                }


                previousActiveDisasters =
                        activeDisasters;
            }

        } catch (Exception e) {

            System.err.println(
                    "[Notification Service] " +
                            "Disaster check failed: "
                            + e.getMessage()
            );
        }
    }


    // ==========================================
    // OUT OF STOCK RESOURCES
    // ==========================================

    private void checkOutOfStockResources() {

        String sql =
                "SELECT COUNT(*) " +
                        "FROM resources " +
                        "WHERE status = 'OUT_OF_STOCK'";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                int outOfStockResources =
                        resultSet.getInt(1);


                if (previousOutOfStockResources == -1) {

                    previousOutOfStockResources =
                            outOfStockResources;

                    return;
                }


                if (outOfStockResources >
                        previousOutOfStockResources) {

                    showNotification(
                            "RESOURCE ALERT",
                            "One or more resources are now out of stock."
                    );
                }


                if (outOfStockResources <
                        previousOutOfStockResources) {

                    showNotification(
                            "RESOURCE UPDATE",
                            "Resource availability has improved."
                    );
                }


                previousOutOfStockResources =
                        outOfStockResources;
            }

        } catch (Exception e) {

            System.err.println(
                    "[Notification Service] " +
                            "Resource check failed: "
                            + e.getMessage()
            );
        }
    }


    // ==========================================
    // PENDING EVACUATIONS
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

                int pendingEvacuations =
                        resultSet.getInt(1);


                if (previousPendingEvacuations == -1) {

                    previousPendingEvacuations =
                            pendingEvacuations;

                    return;
                }


                if (pendingEvacuations >
                        previousPendingEvacuations) {

                    showNotification(
                            "EVACUATION ALERT",
                            "A new evacuation assignment is pending."
                    );
                }


                if (pendingEvacuations <
                        previousPendingEvacuations) {

                    showNotification(
                            "EVACUATION UPDATE",
                            "A pending evacuation has been processed."
                    );
                }


                previousPendingEvacuations =
                        pendingEvacuations;
            }

        } catch (Exception e) {

            System.err.println(
                    "[Notification Service] " +
                            "Evacuation check failed: "
                            + e.getMessage()
            );
        }
    }


    // ==========================================
    // DISPLAY NOTIFICATION
    // ==========================================

    private void showNotification(
            String title,
            String message
    ) {

        System.out.println();

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "[NOTIFICATION] " + title
        );

        System.out.println(
                message
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