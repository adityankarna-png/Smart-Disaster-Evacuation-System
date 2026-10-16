package com.disaster.service;

import com.disaster.database.DBConnection;
import com.disaster.earthquake.EarthquakeData;
import com.disaster.exceptions.NoResourceAvailableException;
import com.disaster.exceptions.NoShelterAvailableException;
import com.disaster.exceptions.VictimNotFoundException;
import com.disaster.model.Evacuation;
import com.disaster.model.Victim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class NotificationServiceThread implements Runnable {

    private final AtomicBoolean running =
            new AtomicBoolean(false);

    private Thread notificationThread;

    private final EvacuationService evacuationService;

    private int previousActiveDisasters = -1;

    private int previousOutOfStockResources = -1;

    private int previousPendingEvacuations = -1;

    private Consumer<EarthquakeData>
            earthquakeAlertListener;

    public NotificationServiceThread() {
        evacuationService =
                new EvacuationServiceImpl();
    }

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

    public synchronized void stopNotificationService() {

        running.set(false);

        if (notificationThread != null) {
            notificationThread.interrupt();
        }

        System.out.println(
                "[Notification Service] Thread stopped."
        );
    }

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

    private void checkSystemNotifications() {

        checkActiveDisasters();

        checkOutOfStockResources();

        checkPendingEvacuations();
    }

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

                if (
                        activeDisasters >
                                previousActiveDisasters
                ) {

                    showNotification(
                            "NEW DISASTER",
                            "A new active disaster has been detected."
                    );
                }

                if (
                        activeDisasters <
                                previousActiveDisasters
                ) {

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
                    "[Notification Service] "
                            + "Disaster check failed: "
                            + e.getMessage()
            );
        }
    }

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

                if (
                        previousOutOfStockResources == -1
                ) {

                    previousOutOfStockResources =
                            outOfStockResources;

                    return;
                }

                if (
                        outOfStockResources >
                                previousOutOfStockResources
                ) {

                    showNotification(
                            "RESOURCE ALERT",
                            "One or more resources are now out of stock."
                    );
                }

                if (
                        outOfStockResources <
                                previousOutOfStockResources
                ) {

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
                    "[Notification Service] "
                            + "Resource check failed: "
                            + e.getMessage()
            );
        }
    }

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

                if (
                        previousPendingEvacuations == -1
                ) {

                    previousPendingEvacuations =
                            pendingEvacuations;

                    return;
                }

                if (
                        pendingEvacuations >
                                previousPendingEvacuations
                ) {

                    showNotification(
                            "EVACUATION ALERT",
                            "A new evacuation assignment is pending."
                    );
                }

                if (
                        pendingEvacuations <
                                previousPendingEvacuations
                ) {

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
                    "[Notification Service] "
                            + "Evacuation check failed: "
                            + e.getMessage()
            );
        }
    }

    public void setEarthquakeAlertListener(
            Consumer<EarthquakeData> listener
    ) {

        this.earthquakeAlertListener =
                listener;
    }

    public void notifyEarthquake(
            EarthquakeData earthquake
    ) {

        notifyEarthquake(
                earthquake,
                true
        );
    }

    private void notifyEarthquake(
            EarthquakeData earthquake,
            boolean triggerEvacuation
    ) {

        if (earthquake == null) {
            return;
        }

        double magnitude =
                earthquake.getMagnitude();

        String severity;

        if (magnitude >= 6.0) {

            severity = "CRITICAL";

        } else if (magnitude >= 5.0) {

            severity = "HIGH";

        } else if (magnitude >= 4.0) {

            severity = "MODERATE";

        } else {

            severity = "LOW";
        }

        String message =
                "Magnitude : "
                        + magnitude
                        + "\n"
                        + "Location  : "
                        + earthquake.getPlace()
                        + "\n"
                        + "Depth     : "
                        + earthquake.getDepth()
                        + " km\n"
                        + "Tsunami   : "
                        + earthquake.isTsunami()
                        + "\n"
                        + "Alert     : "
                        + earthquake.getAlert()
                        + "\n"
                        + "Severity  : "
                        + severity;

        showNotification(
                "EARTHQUAKE ALERT - " + severity,
                message
        );

        /*
         * Normal/live earthquake:
         * automatic emergency evacuation is enabled.
         *
         * Test earthquake:
         * evacuation is already created by the test
         * workflow, so this is disabled.
         */
        if (triggerEvacuation) {

            triggerEmergencyEvacuation(
                    earthquake
            );
        }

        if (earthquakeAlertListener != null) {

            earthquakeAlertListener.accept(
                    earthquake
            );
        }
    }

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

        System.out.println(message);

        System.out.println(
                "=========================================="
        );

        System.out.println();

        SwingUtilities.invokeLater(() -> {

            JOptionPane.showMessageDialog(
                    null,
                    message,
                    title,
                    JOptionPane.WARNING_MESSAGE
            );
        });
    }

    public boolean isRunning() {

        return running.get();
    }


    private void createMonitoringUserEvacuation() {

        String userSql =
                "SELECT victim_id " +
                        "FROM users " +
                        "WHERE role = 'USER' " +
                        "AND victim_id IS NOT NULL " +
                        "ORDER BY user_id " +
                        "LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(userSql);
             ResultSet resultSet = statement.executeQuery()) {

            if (!resultSet.next()) {
                System.out.println(
                        "[TEST] No USER account with a linked victim was found."
                );
                return;
            }

            int userVictimId = resultSet.getInt("victim_id");

            String existingSql =
                    "SELECT evacuation_id " +
                            "FROM evacuations " +
                            "WHERE victim_id = ? " +
                            "AND status = 'PENDING' " +
                            "ORDER BY evacuation_id DESC " +
                            "LIMIT 1";

            try (PreparedStatement existingStatement =
                         connection.prepareStatement(existingSql)) {

                existingStatement.setInt(1, userVictimId);

                try (ResultSet existingResult =
                             existingStatement.executeQuery()) {

                    if (existingResult.next()) {
                        System.out.println(
                                "[TEST] USER already has a pending evacuation. " +
                                        "Evacuation ID: " +
                                        existingResult.getInt("evacuation_id")
                        );
                        return;
                    }
                }
            }

            Evacuation userEvacuation =
                    evacuationService.createEvacuation(userVictimId);

            System.out.println();
            System.out.println(
                    "[TEST] USER monitoring evacuation created successfully."
            );
            System.out.println(
                    "[TEST] USER Victim ID: "
                            + userEvacuation.getVictimId()
            );
            System.out.println(
                    "[TEST] USER Evacuation ID: "
                            + userEvacuation.getEvacuationId()
            );
            System.out.println(
                    "[TEST] USER Shelter ID: "
                            + userEvacuation.getShelterId()
            );
            System.out.println(
                    "[TEST] USER Resource ID: "
                            + userEvacuation.getResourceId()
            );
            System.out.println(
                    "[TEST] USER Priority: "
                            + userEvacuation.getPriorityScore()
            );
            System.out.println(
                    "[TEST] USER Status: "
                            + userEvacuation.getStatus()
            );

        } catch (VictimNotFoundException e) {

            System.err.println(
                    "[TEST] USER victim was not found: "
                            + e.getMessage()
            );

        } catch (NoShelterAvailableException e) {

            showNotification(
                    "USER SHELTER ALERT",
                    "The Emergency User could not be assigned a shelter."
            );

        } catch (NoResourceAvailableException e) {

            showNotification(
                    "USER RESOURCE ALERT",
                    "The Emergency User could not be assigned an emergency resource."
            );

        } catch (Exception e) {

            System.err.println(
                    "[TEST] USER evacuation creation failed: "
                            + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    public void triggerTestEarthquakeAlert() {

        EarthquakeData testEarthquake =
                new EarthquakeData(
                        "TEST-EQ-001",
                        6.5,
                        "33 km SSW of Honcho, Japan",
                        System.currentTimeMillis(),
                        140.123,
                        35.456,
                        42.9,
                        true,
                        "red",
                        "automatic"
                );

        System.out.println();

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "[TEST] Triggering complete earthquake response..."
        );

        System.out.println(
                "=========================================="
        );

        /*
         * ==========================================
         * AUTOMATIC VICTIM GENERATION
         * ==========================================
         */

        try {

            VictimGenerationService
                    victimGenerationService =
                    new VictimGenerationService();

            List<Victim> generatedVictims =
                    victimGenerationService.generateVictims(
                            5
                    );

            System.out.println(
                    "[TEST] Automatically generated and saved "
                            + generatedVictims.size()
                            + " victims."
            );

            /*
             * ==========================================
             * SELECT HIGHEST PRIORITY VICTIM
             * ==========================================
             */

            Victim highestPriorityVictim = null;

            for (Victim victim :
                    generatedVictims) {

                if (
                        highestPriorityVictim == null
                                ||
                                victim.getPriorityScore()
                                        >
                                        highestPriorityVictim
                                                .getPriorityScore()
                ) {

                    highestPriorityVictim =
                            victim;
                }
            }

            /*
             * ==========================================
             * CREATE ONE EVACUATION
             * ==========================================
             */

            if (highestPriorityVictim != null) {

                System.out.println();

                System.out.println(
                        "=========================================="
                );

                System.out.println(
                        "     HIGHEST PRIORITY VICTIM"
                );

                System.out.println(
                        "=========================================="
                );

                System.out.println(
                        "Victim   : "
                                + highestPriorityVictim
                                .getName()
                );

                System.out.println(
                        "Victim ID: "
                                + highestPriorityVictim
                                .getVictimId()
                );

                System.out.println(
                        "Priority : "
                                + highestPriorityVictim
                                .getPriorityScore()
                );

                System.out.println(
                        "Medical  : "
                                + highestPriorityVictim
                                .getMedicalCondition()
                );

                System.out.println(
                        "=========================================="
                );

                try {

                    Evacuation evacuation =
                            evacuationService
                                    .createEvacuation(
                                            highestPriorityVictim
                                                    .getVictimId()
                                    );

                    System.out.println();

                    System.out.println(
                            "[TEST] Evacuation created successfully."
                    );

                    System.out.println(
                            "[TEST] Evacuation ID: "
                                    + evacuation
                                    .getEvacuationId()
                    );

                    System.out.println(
                            "[TEST] Victim ID: "
                                    + evacuation
                                    .getVictimId()
                    );

                    System.out.println(
                            "[TEST] Shelter ID: "
                                    + evacuation
                                    .getShelterId()
                    );

                    System.out.println(
                            "[TEST] Resource ID: "
                                    + evacuation
                                    .getResourceId()
                    );

                    System.out.println(
                            "[TEST] Priority: "
                                    + evacuation
                                    .getPriorityScore()
                    );

                    System.out.println(
                            "[TEST] Status: "
                                    + evacuation
                                    .getStatus()
                    );

                } catch (Exception e) {

                    System.err.println(
                            "[TEST] Evacuation creation failed: "
                                    + e.getMessage()
                    );

                    e.printStackTrace();
                }

                /*
                 * ==========================================
                 * USER MONITORING EVACUATION
                 * ==========================================
                 *
                 * The normal test flow above keeps the original
                 * highest-priority victim demonstration.
                 *
                 * In addition, the USER account's linked victim
                 * receives an evacuation assignment so that the
                 * USER monitoring dashboard can display the actual
                 * assigned shelter and evacuation status.
                 */
                createMonitoringUserEvacuation();

            } else {

                System.out.println(
                        "[TEST] No victims were generated."
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "[TEST] Automatic victim generation failed: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }

        /*
         * ==========================================
         * SEND TEST EARTHQUAKE NOTIFICATION
         * ==========================================
         *
         * FALSE means:
         * Do NOT create another evacuation.
         *
         * The evacuation was already created above.
         */

        notifyEarthquake(
                testEarthquake,
                false
        );
    }

    private void triggerEmergencyEvacuation(
            EarthquakeData earthquake
    ) {

        if (earthquake == null) {
            return;
        }

        if (earthquake.getMagnitude() < 6.0) {
            return;
        }

        try {

            System.out.println();

            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "       EMERGENCY EVACUATION"
            );

            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "Earthquake Magnitude : "
                            + earthquake.getMagnitude()
            );

            System.out.println(
                    "Location             : "
                            + earthquake.getPlace()
            );

            System.out.println(
                    "Action               : EVACUATION WARNING"
            );

            Evacuation evacuation =
                    evacuationService
                            .createEmergencyEvacuation();

            System.out.println();

            System.out.println(
                    "Evacuation created successfully."
            );

            System.out.println(
                    "Evacuation ID : "
                            + evacuation.getEvacuationId()
            );

            System.out.println(
                    "Victim ID     : "
                            + evacuation.getVictimId()
            );

            System.out.println(
                    "Shelter ID    : "
                            + evacuation.getShelterId()
            );

            System.out.println(
                    "Resource ID   : "
                            + evacuation.getResourceId()
            );

            System.out.println(
                    "Priority      : "
                            + evacuation.getPriorityScore()
            );

            System.out.println(
                    "Status        : "
                            + evacuation.getStatus()
            );

            System.out.println(
                    "=========================================="
            );

        } catch (VictimNotFoundException e) {

            showNotification(
                    "EVACUATION ERROR",
                    "No suitable victim found for emergency evacuation."
            );

        } catch (NoShelterAvailableException e) {

            showNotification(
                    "SHELTER ALERT",
                    "Emergency evacuation triggered, but no shelter is available."
            );

        } catch (NoResourceAvailableException e) {

            showNotification(
                    "RESOURCE ALERT",
                    "Emergency evacuation triggered, but no resource is available."
            );

        } catch (Exception e) {

            System.err.println(
                    "[Emergency Evacuation] Error: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}