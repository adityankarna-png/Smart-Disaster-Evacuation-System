package com.disaster.gui;

import com.disaster.dao.DisasterDAO;
import com.disaster.dao.ResourceDAO;
import com.disaster.dao.ShelterDAO;
import com.disaster.dao.VictimDAO;
import com.disaster.database.DBConnection;
import com.disaster.service.EvacuationServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;

public class DashboardPanel extends JPanel {

    // ==========================================
    // COLORS
    // ==========================================

    private static final Color BACKGROUND =
            new Color(245, 247, 250);

    private static final Color TEXT =
            new Color(35, 45, 55);

    private static final Color SECONDARY_TEXT =
            new Color(100, 110, 120);

    private static final Color CARD_BACKGROUND =
            Color.WHITE;

    private static final Color BORDER =
            new Color(220, 225, 230);

    private static final Color BLUE =
            new Color(45, 100, 170);

    private static final Color RED =
            new Color(190, 65, 65);

    private static final Color GREEN =
            new Color(55, 145, 90);

    private static final Color ORANGE =
            new Color(220, 135, 45);


    // ==========================================
    // DAO OBJECTS
    // ==========================================

    private final DisasterDAO disasterDAO;
    private final VictimDAO victimDAO;
    private final ShelterDAO shelterDAO;
    private final ResourceDAO resourceDAO;


    // ==========================================
    // LABELS
    // ==========================================

    private JLabel disasterCountLabel;
    private JLabel victimCountLabel;
    private JLabel shelterCountLabel;
    private JLabel resourceCountLabel;

    private JLabel databaseStatusLabel;
    private JLabel evacuationStatusLabel;
    private JLabel resourceStatusLabel;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public DashboardPanel() {

        disasterDAO =
                new DisasterDAO();

        victimDAO =
                new VictimDAO();

        shelterDAO =
                new ShelterDAO();

        resourceDAO =
                new ResourceDAO();


        setLayout(
                new BorderLayout()
        );

        setBackground(
                BACKGROUND
        );


        // ==========================================
        // MAIN CONTENT
        // ==========================================

        JPanel mainPanel =
                new JPanel();

        mainPanel.setLayout(
                new BorderLayout()
        );

        mainPanel.setBackground(
                BACKGROUND
        );

        mainPanel.setBorder(
                new EmptyBorder(
                        25,
                        30,
                        25,
                        30
                )
        );


        // ==========================================
        // TITLE SECTION
        // ==========================================

        JPanel titlePanel =
                new JPanel(
                        new BorderLayout()
                );

        titlePanel.setBackground(
                BACKGROUND
        );


        JLabel titleLabel =
                new JLabel(
                        "Disaster Management Dashboard"
                );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        titleLabel.setForeground(
                TEXT
        );


        JLabel subtitleLabel =
                new JLabel(
                        "Real-time overview of disaster response operations"
                );

        subtitleLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        subtitleLabel.setForeground(
                SECONDARY_TEXT
        );


        JPanel titleTextPanel =
                new JPanel();

        titleTextPanel.setLayout(
                new BoxLayout(
                        titleTextPanel,
                        BoxLayout.Y_AXIS
                )
        );

        titleTextPanel.setBackground(
                BACKGROUND
        );


        titleTextPanel.add(
                titleLabel
        );

        titleTextPanel.add(
                Box.createVerticalStrut(5)
        );

        titleTextPanel.add(
                subtitleLabel
        );


        // ==========================================
        // REFRESH BUTTON
        // ==========================================

        JButton refreshButton =
                new JButton(
                        "Refresh Dashboard"
                );

        refreshButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        refreshButton.setForeground(
                Color.WHITE
        );

        refreshButton.setBackground(
                BLUE
        );

        refreshButton.setFocusPainted(
                false
        );

        refreshButton.setBorderPainted(
                false
        );

        refreshButton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        refreshButton.setBorder(
                new EmptyBorder(
                        10,
                        18,
                        10,
                        18
                )
        );


        refreshButton.addActionListener(
                e -> refreshDashboard()
        );


        titlePanel.add(
                titleTextPanel,
                BorderLayout.WEST
        );

        titlePanel.add(
                refreshButton,
                BorderLayout.EAST
        );


        mainPanel.add(
                titlePanel,
                BorderLayout.NORTH
        );


        // ==========================================
        // CENTER CONTENT
        // ==========================================

        JPanel centerPanel =
                new JPanel();

        centerPanel.setLayout(
                new BorderLayout(
                        0,
                        20
                )
        );

        centerPanel.setBackground(
                BACKGROUND
        );


        // ==========================================
        // STATISTICS CARDS
        // ==========================================

        JPanel cardsPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                2,
                                20,
                                20
                        )
                );

        cardsPanel.setBackground(
                BACKGROUND
        );


        // ==========================================
        // CREATE COUNT LABELS
        // ==========================================

        disasterCountLabel =
                createCountLabel();

        victimCountLabel =
                createCountLabel();

        shelterCountLabel =
                createCountLabel();

        resourceCountLabel =
                createCountLabel();


        // ==========================================
        // CREATE CARDS
        // ==========================================

        JPanel disasterCard =
                createStatCard(
                        "DISASTERS",
                        "Active and registered incidents",
                        disasterCountLabel,
                        RED
                );


        JPanel victimCard =
                createStatCard(
                        "VICTIMS",
                        "People registered for assistance",
                        victimCountLabel,
                        ORANGE
                );


        JPanel shelterCard =
                createStatCard(
                        "SHELTERS",
                        "Emergency shelter centers",
                        shelterCountLabel,
                        GREEN
                );


        JPanel resourceCard =
                createStatCard(
                        "RESOURCES",
                        "Available emergency resources",
                        resourceCountLabel,
                        BLUE
                );


        cardsPanel.add(
                disasterCard
        );

        cardsPanel.add(
                victimCard
        );

        cardsPanel.add(
                shelterCard
        );

        cardsPanel.add(
                resourceCard
        );


        centerPanel.add(
                cardsPanel,
                BorderLayout.CENTER
        );


        // ==========================================
        // SYSTEM STATUS
        // ==========================================

        JPanel statusPanel =
                createStatusPanel();


        centerPanel.add(
                statusPanel,
                BorderLayout.SOUTH
        );


        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );


        add(
                mainPanel,
                BorderLayout.CENTER
        );


        // ==========================================
        // LOAD DATA
        // ==========================================

        refreshDashboard();
    }


    // ==========================================
    // CREATE COUNT LABEL
    // ==========================================

    private JLabel createCountLabel() {

        JLabel label =
                new JLabel(
                        "0",
                        SwingConstants.CENTER
                );

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        42
                )
        );

        label.setForeground(
                TEXT
        );

        return label;
    }


    // ==========================================
    // CREATE STAT CARD
    // ==========================================

    private JPanel createStatCard(
            String title,
            String description,
            JLabel countLabel,
            Color accentColor
    ) {

        JPanel card =
                new JPanel(
                        new BorderLayout()
                );

        card.setBackground(
                CARD_BACKGROUND
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER,
                                1
                        ),
                        new EmptyBorder(
                                18,
                                20,
                                18,
                                20
                        )
                )
        );


        // ==========================================
        // TOP SECTION
        // ==========================================

        JPanel topPanel =
                new JPanel(
                        new BorderLayout()
                );

        topPanel.setBackground(
                CARD_BACKGROUND
        );


        JLabel titleLabel =
                new JLabel(
                        title
                );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );

        titleLabel.setForeground(
                TEXT
        );


        JPanel accent =
                new JPanel();

        accent.setBackground(
                accentColor
        );

        accent.setPreferredSize(
                new Dimension(
                        5,
                        25
                )
        );


        JPanel titleContainer =
                new JPanel(
                        new BorderLayout(
                                10,
                                0
                        )
                );

        titleContainer.setBackground(
                CARD_BACKGROUND
        );

        titleContainer.add(
                accent,
                BorderLayout.WEST
        );

        titleContainer.add(
                titleLabel,
                BorderLayout.CENTER
        );


        topPanel.add(
                titleContainer,
                BorderLayout.WEST
        );


        card.add(
                topPanel,
                BorderLayout.NORTH
        );


        // ==========================================
        // CENTER COUNT
        // ==========================================

        JPanel countPanel =
                new JPanel(
                        new BorderLayout()
                );

        countPanel.setBackground(
                CARD_BACKGROUND
        );

        countPanel.add(
                countLabel,
                BorderLayout.CENTER
        );


        card.add(
                countPanel,
                BorderLayout.CENTER
        );


        // ==========================================
        // DESCRIPTION
        // ==========================================

        JLabel descriptionLabel =
                new JLabel(
                        description,
                        SwingConstants.CENTER
                );

        descriptionLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        descriptionLabel.setForeground(
                SECONDARY_TEXT
        );


        card.add(
                descriptionLabel,
                BorderLayout.SOUTH
        );


        return card;
    }


    // ==========================================
    // CREATE SYSTEM STATUS PANEL
    // ==========================================

    private JPanel createStatusPanel() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(
                Color.WHITE
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER,
                                1
                        ),
                        new EmptyBorder(
                                15,
                                20,
                                15,
                                20
                        )
                )
        );


        // ==========================================
        // STATUS TITLE
        // ==========================================

        JLabel titleLabel =
                new JLabel(
                        "SYSTEM STATUS"
                );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        titleLabel.setForeground(
                TEXT
        );


        panel.add(
                titleLabel,
                BorderLayout.NORTH
        );


        // ==========================================
        // STATUS ROW
        // ==========================================

        JPanel statusContainer =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                30,
                                0
                        )
                );

        statusContainer.setBackground(
                Color.WHITE
        );


        databaseStatusLabel =
                createStatusLabel(
                        "Database",
                        false
                );


        evacuationStatusLabel =
                createStatusLabel(
                        "Evacuation Service",
                        false
                );


        resourceStatusLabel =
                createStatusLabel(
                        "Resource Monitoring",
                        false
                );


        statusContainer.add(
                databaseStatusLabel
        );

        statusContainer.add(
                evacuationStatusLabel
        );

        statusContainer.add(
                resourceStatusLabel
        );


        panel.add(
                statusContainer,
                BorderLayout.CENTER
        );


        return panel;
    }


    // ==========================================
    // CREATE STATUS LABEL
    // ==========================================

    private JLabel createStatusLabel(
            String name,
            boolean active
    ) {

        JLabel label =
                new JLabel();

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );


        if (active) {

            label.setText(
                    "● " + name + ": ACTIVE"
            );

            label.setForeground(
                    GREEN
            );

        } else {

            label.setText(
                    "● " + name + ": CHECKING..."
            );

            label.setForeground(
                    SECONDARY_TEXT
            );
        }


        return label;
    }


    // ==========================================
    // REFRESH DASHBOARD
    // ==========================================

    private void refreshDashboard() {

        try {

            // ==================================
            // GET COUNTS
            // ==================================

            int disasterCount =
                    disasterDAO
                            .getAllDisasters()
                            .size();


            int victimCount =
                    victimDAO
                            .getAllVictims()
                            .size();


            int shelterCount =
                    shelterDAO
                            .getAllShelters()
                            .size();


            int resourceCount =
                    resourceDAO
                            .getAllResources()
                            .size();


            // ==================================
            // UPDATE COUNTS
            // ==================================

            disasterCountLabel.setText(
                    String.valueOf(
                            disasterCount
                    )
            );


            victimCountLabel.setText(
                    String.valueOf(
                            victimCount
                    )
            );


            shelterCountLabel.setText(
                    String.valueOf(
                            shelterCount
                    )
            );


            resourceCountLabel.setText(
                    String.valueOf(
                            resourceCount
                    )
            );


            // ==================================
            // CHECK DATABASE
            // ==================================

            checkDatabase();


            // ==================================
            // CHECK EVACUATION SERVICE
            // ==================================

            checkEvacuationService();


            // ==================================
            // CHECK RESOURCE SERVICE
            // ==================================

            checkResourceMonitoring();


        } catch (Exception e) {

            e.printStackTrace();

            databaseStatusLabel.setText(
                    "● Database: ERROR"
            );

            databaseStatusLabel.setForeground(
                    RED
            );
        }


        revalidate();

        repaint();
    }


    // ==========================================
    // CHECK DATABASE
    // ==========================================

    private void checkDatabase() {

        try (
                Connection connection =
                        DBConnection.getConnection()
        ) {

            if (
                    connection != null
                            && !connection.isClosed()
            ) {

                databaseStatusLabel.setText(
                        "● Database: CONNECTED"
                );

                databaseStatusLabel.setForeground(
                        GREEN
                );

            } else {

                databaseStatusLabel.setText(
                        "● Database: OFFLINE"
                );

                databaseStatusLabel.setForeground(
                        RED
                );
            }

        } catch (Exception e) {

            databaseStatusLabel.setText(
                    "● Database: OFFLINE"
            );

            databaseStatusLabel.setForeground(
                    RED
            );
        }
    }


    // ==========================================
    // CHECK EVACUATION SERVICE
    // ==========================================

    private void checkEvacuationService() {

        try {

            new EvacuationServiceImpl();

            evacuationStatusLabel.setText(
                    "● Evacuation Service: ACTIVE"
            );

            evacuationStatusLabel.setForeground(
                    GREEN
            );

        } catch (Exception e) {

            evacuationStatusLabel.setText(
                    "● Evacuation Service: ERROR"
            );

            evacuationStatusLabel.setForeground(
                    RED
            );
        }
    }


    // ==========================================
    // CHECK RESOURCE MONITORING
    // ==========================================

    private void checkResourceMonitoring() {

        try {

            resourceDAO.getAllResources();

            resourceStatusLabel.setText(
                    "● Resource Monitoring: ACTIVE"
            );

            resourceStatusLabel.setForeground(
                    GREEN
            );

        } catch (Exception e) {

            resourceStatusLabel.setText(
                    "● Resource Monitoring: ERROR"
            );

            resourceStatusLabel.setForeground(
                    RED
            );
        }
    }
}