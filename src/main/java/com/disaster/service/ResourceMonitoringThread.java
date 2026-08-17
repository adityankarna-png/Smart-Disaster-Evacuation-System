package com.disaster.service;

import com.disaster.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class ResourceMonitoringThread implements Runnable {

    private final AtomicBoolean running =
            new AtomicBoolean(false);

    private Thread monitoringThread;

    private int previousTotalQuantity = -1;
    private int previousOutOfStockCount = -1;


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
                        "Resource-Monitoring-Thread"
                );

        monitoringThread.setDaemon(true);

        monitoringThread.start();

        System.out.println(
                "[Resource Monitoring] Thread started."
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
                "[Resource Monitoring] Thread stopped."
        );
    }


    // ==========================================
    // RUN
    // ==========================================

    @Override
    public void run() {

        while (running.get()) {

            try {

                checkResources();

                Thread.sleep(10000);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                break;

            } catch (Exception e) {

                System.err.println(
                        "[Resource Monitoring] Error: "
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
                "[Resource Monitoring] Thread exited."
        );
    }


    // ==========================================
    // CHECK RESOURCES
    // ==========================================

    private void checkResources() {

        String sql =
                "SELECT " +
                        "COALESCE(SUM(quantity), 0), " +
                        "SUM(CASE " +
                        "WHEN status = 'OUT_OF_STOCK' " +
                        "THEN 1 ELSE 0 END) " +
                        "FROM resources";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                int totalQuantity =
                        resultSet.getInt(1);

                int outOfStockCount =
                        resultSet.getInt(2);


                // ==========================================
                // FIRST CHECK
                // ==========================================

                if (previousTotalQuantity == -1) {

                    previousTotalQuantity =
                            totalQuantity;

                    previousOutOfStockCount =
                            outOfStockCount;

                    System.out.println(
                            "[Resource Monitoring] " +
                                    "Total resource quantity: "
                                    + totalQuantity
                    );

                    System.out.println(
                            "[Resource Monitoring] " +
                                    "Out-of-stock resources: "
                                    + outOfStockCount
                    );

                    return;
                }


                // ==========================================
                // QUANTITY CHANGE
                // ==========================================

                if (totalQuantity !=
                        previousTotalQuantity) {

                    System.out.println(
                            "[Resource Monitoring] " +
                                    "Resource quantity changed."
                    );

                    System.out.println(
                            "[Resource Monitoring] " +
                                    "Previous quantity: "
                                    + previousTotalQuantity
                    );

                    System.out.println(
                            "[Resource Monitoring] " +
                                    "Current quantity: "
                                    + totalQuantity
                    );

                    previousTotalQuantity =
                            totalQuantity;
                }


                // ==========================================
                // OUT-OF-STOCK CHANGE
                // ==========================================

                if (outOfStockCount !=
                        previousOutOfStockCount) {

                    System.out.println(
                            "[Resource Monitoring] " +
                                    "Resource availability changed."
                    );

                    System.out.println(
                            "[Resource Monitoring] " +
                                    "Previous out-of-stock: "
                                    + previousOutOfStockCount
                    );

                    System.out.println(
                            "[Resource Monitoring] " +
                                    "Current out-of-stock: "
                                    + outOfStockCount
                    );


                    if (outOfStockCount >
                            previousOutOfStockCount) {

                        System.out.println(
                                "[Resource Monitoring] " +
                                        "ALERT: Resource became out of stock."
                        );
                    }


                    if (outOfStockCount <
                            previousOutOfStockCount) {

                        System.out.println(
                                "[Resource Monitoring] " +
                                        "Resource availability improved."
                        );
                    }


                    previousOutOfStockCount =
                            outOfStockCount;
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "[Resource Monitoring] " +
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