package com.disaster.gui;

import com.disaster.database.DBConnection;
import com.disaster.earthquake.EarthquakeData;
import com.disaster.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserDashboardPanel extends JPanel {

    private final User currentUser;

    private JLabel disasterStatusLabel;
    private JLabel disasterInfoLabel;

    private JLabel evacuationStatusLabel;
    private JLabel priorityLabel;

    private JLabel shelterNameLabel;
    private JLabel shelterLocationLabel;
    private JLabel shelterCapacityLabel;
    private JLabel shelterStatusLabel;

    private JTable shelterTable;
    private DefaultTableModel shelterTableModel;

    private Timer refreshTimer;
    private int lastEvacuationId = -1;
    private String lastEarthquakeId;

    private static final Color BG = new Color(12, 24, 34);
    private static final Color PANEL = new Color(20, 40, 58);
    private static final Color PANEL2 = new Color(26, 50, 71);
    private static final Color TEXT = new Color(232, 240, 245);
    private static final Color MUTED = new Color(139, 165, 184);
    private static final Color RED = new Color(255, 90, 95);
    private static final Color GREEN = new Color(63, 199, 154);
    private static final Color BLUE = new Color(79, 168, 240);
    private static final Color AMBER = new Color(245, 168, 60);
    private static final Color LINE = new Color(38, 67, 91);

    public UserDashboardPanel(User currentUser) {

        this.currentUser = currentUser;

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        buildUI();
        loadUserData();

        refreshTimer = new Timer(3000, e -> loadUserData());
        refreshTimer.start();
    }


    private void buildUI() {

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(26, 30, 30, 30));

        // =========================
        // HEADER
        // =========================
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Emergency Monitoring Dashboard");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 29));

        JLabel subtitle = new JLabel(
                "Real-time disaster information and your emergency shelter assignment"
        );
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        titles.add(title);
        titles.add(Box.createVerticalStrut(4));
        titles.add(subtitle);

        header.add(titles, BorderLayout.WEST);

        content.add(header);
        content.add(Box.createVerticalStrut(20));

        // =========================
        // DISASTER ALERT CARD
        // =========================
        JPanel disasterPanel = createCard();

        disasterStatusLabel = new JLabel("● NO ACTIVE ALERT");
        disasterStatusLabel.setForeground(GREEN);
        disasterStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));

        disasterInfoLabel = new JLabel(
                "No active disaster information available."
        );
        disasterInfoLabel.setForeground(TEXT);
        disasterInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        disasterPanel.add(disasterStatusLabel);
        disasterPanel.add(Box.createVerticalStrut(10));
        disasterPanel.add(disasterInfoLabel);

        content.add(disasterPanel);
        content.add(Box.createVerticalStrut(18));

        // =========================
        // EVACUATION CARD
        // =========================
        JPanel evacuationPanel = createCard();

        JLabel evacuationTitle = new JLabel("My Emergency Evacuation");
        evacuationTitle.setForeground(TEXT);
        evacuationTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));

        evacuationStatusLabel = createValueLabel("Not assigned");
        priorityLabel = createValueLabel("-");

        JPanel evacuationInfo = new JPanel();
        evacuationInfo.setOpaque(false);
        evacuationInfo.setLayout(new BoxLayout(evacuationInfo, BoxLayout.Y_AXIS));

        evacuationInfo.add(createInfoRow("Evacuation Status", evacuationStatusLabel));
        evacuationInfo.add(Box.createVerticalStrut(8));
        evacuationInfo.add(createInfoRow("Priority Score", priorityLabel));

        evacuationPanel.add(evacuationTitle);
        evacuationPanel.add(Box.createVerticalStrut(13));
        evacuationPanel.add(evacuationInfo);

        content.add(evacuationPanel);
        content.add(Box.createVerticalStrut(18));

        // =========================
        // ASSIGNED SHELTER CARD
        // =========================
        JPanel shelterPanel = createCard();

        JLabel shelterTitle = new JLabel("Your Assigned Shelter");
        shelterTitle.setForeground(TEXT);
        shelterTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));

        shelterNameLabel = createValueLabel("Not assigned");
        shelterLocationLabel = createValueLabel("-");
        shelterCapacityLabel = createValueLabel("-");
        shelterStatusLabel = createValueLabel("-");

        JPanel shelterInfo = new JPanel();
        shelterInfo.setOpaque(false);
        shelterInfo.setLayout(new BoxLayout(shelterInfo, BoxLayout.Y_AXIS));

        shelterInfo.add(createInfoRow("Shelter", shelterNameLabel));
        shelterInfo.add(Box.createVerticalStrut(8));
        shelterInfo.add(createInfoRow("Location", shelterLocationLabel));
        shelterInfo.add(Box.createVerticalStrut(8));
        shelterInfo.add(createInfoRow("Capacity", shelterCapacityLabel));
        shelterInfo.add(Box.createVerticalStrut(8));
        shelterInfo.add(createInfoRow("Shelter Status", shelterStatusLabel));

        shelterPanel.add(shelterTitle);
        shelterPanel.add(Box.createVerticalStrut(13));
        shelterPanel.add(shelterInfo);

        content.add(shelterPanel);
        content.add(Box.createVerticalStrut(18));

        // =========================
        // AVAILABLE SHELTERS
        // =========================
        JPanel availableHeader = new JPanel(new BorderLayout());
        availableHeader.setOpaque(false);
        availableHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel availableTitle = new JLabel("Available Shelters");
        availableTitle.setForeground(TEXT);
        availableTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));

        availableHeader.add(availableTitle, BorderLayout.WEST);

        content.add(availableHeader);
        content.add(Box.createVerticalStrut(10));

        shelterTableModel = new DefaultTableModel(
                new Object[]{
                        "Shelter",
                        "Location",
                        "Capacity",
                        "Occupancy",
                        "Status"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        shelterTable = new JTable(shelterTableModel);
        shelterTable.setBackground(PANEL);
        shelterTable.setForeground(TEXT);
        shelterTable.setSelectionBackground(new Color(38, 75, 102));
        shelterTable.setSelectionForeground(Color.WHITE);
        shelterTable.setGridColor(new Color(38, 67, 91));
        shelterTable.setRowHeight(34);
        shelterTable.setIntercellSpacing(new Dimension(0, 1));
        shelterTable.setShowVerticalLines(false);
        shelterTable.setShowHorizontalLines(true);
        shelterTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        shelterTable.getTableHeader().setBackground(PANEL2);
        shelterTable.getTableHeader().setForeground(MUTED);
        shelterTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );
        shelterTable.getTableHeader().setPreferredSize(new Dimension(0, 38));
        shelterTable.getTableHeader().setReorderingAllowed(false);

        shelterTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        shelterTable.getColumnModel().getColumn(1).setPreferredWidth(230);
        shelterTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        shelterTable.getColumnModel().getColumn(3).setPreferredWidth(140);
        shelterTable.getColumnModel().getColumn(4).setPreferredWidth(170);

        JScrollPane scrollPane = new JScrollPane(
                shelterTable,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(BorderFactory.createLineBorder(LINE));
        scrollPane.getViewport().setBackground(PANEL);
        scrollPane.setBackground(PANEL);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(800, 220));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(scrollPane);

        root.add(content, BorderLayout.CENTER);

        JScrollPane outerScroll = new JScrollPane(
                root,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        outerScroll.setBorder(null);
        outerScroll.setBackground(BG);
        outerScroll.getViewport().setBackground(BG);
        outerScroll.getVerticalScrollBar().setUnitIncrement(18);

        add(outerScroll, BorderLayout.CENTER);
    }

    private JPanel createCard() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                new EmptyBorder(20, 22, 20, 22)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

        return panel;
    }

    private JLabel createValueLabel(String text) {

        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));

        return label;
    }

    private JPanel createInfoRow(String name, JLabel value) {

        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBackground(PANEL);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(MUTED);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        value.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(nameLabel, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);

        return row;
    }

    private void loadUserData() {

        if (currentUser == null ||
                currentUser.getVictimId() == null) {

            return;
        }

        loadEvacuation();
        loadShelters();
    }

    private void loadEvacuation() {

        String sql =
                "SELECT " +
                        "e.evacuation_id, " +
                        "e.status, " +
                        "e.priority_score, " +
                        "s.name AS shelter_name, " +
                        "s.location AS shelter_location, " +
                        "s.capacity, " +
                        "s.current_occupancy, " +
                        "s.status AS shelter_status " +
                        "FROM evacuations e " +
                        "JOIN shelters s ON e.shelter_id = s.shelter_id " +
                        "WHERE e.victim_id = ? " +
                        "ORDER BY e.evacuation_id DESC " +
                        "LIMIT 1";

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    currentUser.getVictimId()
            );

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {

                    int evacuationId =
                            rs.getInt("evacuation_id");

                    String evacuationStatus =
                            rs.getString("status");

                    int priority =
                            rs.getInt("priority_score");

                    String shelterName =
                            rs.getString("shelter_name");

                    String shelterLocation =
                            rs.getString("shelter_location");

                    int capacity =
                            rs.getInt("capacity");

                    int occupancy =
                            rs.getInt("current_occupancy");

                    String shelterStatus =
                            rs.getString("shelter_status");

                    evacuationStatusLabel.setText(
                            evacuationStatus
                    );

                    priorityLabel.setText(
                            String.valueOf(priority)
                    );

                    shelterNameLabel.setText(
                            shelterName
                    );

                    shelterLocationLabel.setText(
                            shelterLocation
                    );

                    shelterCapacityLabel.setText(
                            occupancy + " / " + capacity
                    );

                    shelterStatusLabel.setText(
                            shelterStatus
                    );

                    if (evacuationId != lastEvacuationId) {

                        if (lastEvacuationId != -1) {

                            showShelterNotification(
                                    shelterName,
                                    shelterLocation,
                                    evacuationStatus
                            );
                        }

                        lastEvacuationId = evacuationId;
                    }

                    evacuationStatusLabel.setForeground(
                            evacuationStatus.equalsIgnoreCase("COMPLETED")
                                    ? GREEN
                                    : AMBER
                    );

                    shelterStatusLabel.setForeground(
                            shelterStatus.equalsIgnoreCase("AVAILABLE")
                                    ? GREEN
                                    : RED
                    );

                } else {

                    evacuationStatusLabel.setText(
                            "Not assigned"
                    );

                    priorityLabel.setText("-");

                    shelterNameLabel.setText(
                            "No shelter assigned"
                    );

                    shelterLocationLabel.setText("-");

                    shelterCapacityLabel.setText("-");

                    shelterStatusLabel.setText("-");
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "Error loading user evacuation: "
                            + e.getMessage()
            );
        }
    }

    private void loadShelters() {

        shelterTableModel.setRowCount(0);

        String sql =
                "SELECT name, location, capacity, " +
                        "current_occupancy, status " +
                        "FROM shelters " +
                        "WHERE current_occupancy < capacity " +
                        "AND status = 'AVAILABLE' " +
                        "ORDER BY name";

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs =
                     statement.executeQuery()) {

            while (rs.next()) {

                shelterTableModel.addRow(
                        new Object[]{
                                rs.getString("name"),
                                rs.getString("location"),
                                rs.getInt("capacity"),
                                rs.getInt("current_occupancy"),
                                rs.getString("status")
                        }
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "Error loading shelters: "
                            + e.getMessage()
            );
        }
    }

    private void showShelterNotification(
            String shelterName,
            String shelterLocation,
            String status
    ) {

        JOptionPane.showMessageDialog(
                this,
                "EMERGENCY EVACUATION ASSIGNED\n\n"
                        + "Your evacuation request has been processed.\n\n"
                        + "Assigned Shelter:\n"
                        + shelterName
                        + "\n\nLocation:\n"
                        + shelterLocation
                        + "\n\nEvacuation Status:\n"
                        + status,
                "Emergency Shelter Notification",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public void updateEarthquake(EarthquakeData earthquake) {

        if (earthquake == null) {
            return;
        }

        String earthquakeId = earthquake.getId();

        if (earthquakeId != null
                && earthquakeId.equals(lastEarthquakeId)) {
            return;
        }

        lastEarthquakeId = earthquakeId;

        double magnitude = earthquake.getMagnitude();

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

        String tsunami =
                earthquake.isTsunami() ? "YES" : "NO";

        String alert =
                earthquake.getAlert() == null
                        ? "NONE"
                        : earthquake.getAlert().toUpperCase();

        disasterStatusLabel.setText(
                "● EARTHQUAKE ALERT - " + severity
        );

        disasterStatusLabel.setForeground(
                severity.equals("CRITICAL")
                        ? RED
                        : severity.equals("HIGH")
                        ? AMBER
                        : severity.equals("MODERATE")
                        ? AMBER
                        : GREEN
        );

        disasterInfoLabel.setText(
                "<html>"
                        + "<b>Magnitude:</b> "
                        + String.format("%.1f", magnitude)
                        + "&nbsp;&nbsp;&nbsp;"
                        + "<b>Location:</b> "
                        + escapeHtml(earthquake.getPlace())
                        + "<br>"
                        + "<b>Depth:</b> "
                        + String.format("%.1f km", earthquake.getDepth())
                        + "&nbsp;&nbsp;&nbsp;"
                        + "<b>Tsunami:</b> "
                        + tsunami
                        + "&nbsp;&nbsp;&nbsp;"
                        + "<b>Alert Level:</b> "
                        + escapeHtml(alert)
                        + "</html>"
        );

        SwingUtilities.invokeLater(() -> {

            String shelterName =
                    shelterNameLabel == null
                            ? "Not assigned"
                            : shelterNameLabel.getText();

            String shelterLocation =
                    shelterLocationLabel == null
                            ? "-"
                            : shelterLocationLabel.getText();

            String evacuationStatus =
                    evacuationStatusLabel == null
                            ? "Not assigned"
                            : evacuationStatusLabel.getText();

            JOptionPane.showMessageDialog(
                    this,
                    "EARTHQUAKE ALERT - " + severity
                            + "\n\n"
                            + "Magnitude: "
                            + String.format("%.1f", magnitude)
                            + "\n"
                            + "Location: "
                            + earthquake.getPlace()
                            + "\n"
                            + "Depth: "
                            + String.format("%.1f km", earthquake.getDepth())
                            + "\n"
                            + "Tsunami: "
                            + tsunami
                            + "\n"
                            + "Alert Level: "
                            + alert
                            + "\n\n"
                            + "YOUR EVACUATION STATUS: "
                            + evacuationStatus
                            + "\n"
                            + "ASSIGNED SHELTER: "
                            + shelterName
                            + "\n"
                            + "SHELTER LOCATION: "
                            + shelterLocation,
                    "Emergency Disaster Notification",
                    severity.equals("CRITICAL")
                            ? JOptionPane.ERROR_MESSAGE
                            : JOptionPane.WARNING_MESSAGE
            );
        });
    }

    private String escapeHtml(String value) {

        if (value == null) {
            return "Unknown";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    public void stopRefresh() {

        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }
}
