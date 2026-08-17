package com.disaster.gui;

import com.disaster.dao.ReportDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ReportsPanel extends JPanel {

    // ==========================================
    // COLORS
    // ==========================================

    private static final Color BACKGROUND =
            new Color(245, 247, 250);

    private static final Color CARD =
            Color.WHITE;

    private static final Color TEXT =
            new Color(35, 45, 55);

    private static final Color SUBTEXT =
            new Color(100, 115, 130);

    private static final Color BORDER =
            new Color(215, 222, 230);

    private static final Color BLUE =
            new Color(45, 100, 180);

    private static final Color RED =
            new Color(210, 55, 60);

    private static final Color ORANGE =
            new Color(220, 125, 25);

    private static final Color GREEN =
            new Color(35, 155, 90);

    private static final Color PURPLE =
            new Color(120, 80, 170);

    private static final Color TEAL =
            new Color(30, 140, 145);


    // ==========================================
    // DAO
    // ==========================================

    private final ReportDAO reportDAO;


    // ==========================================
    // LABELS
    // ==========================================

    private JLabel totalDisastersLabel;
    private JLabel activeDisastersLabel;
    private JLabel resolvedDisastersLabel;

    private JLabel totalVictimsLabel;
    private JLabel highPriorityVictimsLabel;
    private JLabel waitingVictimsLabel;

    private JLabel totalSheltersLabel;
    private JLabel availableSheltersLabel;
    private JLabel fullSheltersLabel;

    private JLabel totalResourcesLabel;
    private JLabel availableResourcesLabel;
    private JLabel outOfStockResourcesLabel;

    private JLabel totalEvacuationsLabel;
    private JLabel pendingEvacuationsLabel;
    private JLabel completedEvacuationsLabel;

    private JLabel shelterCapacityLabel;
    private JLabel shelterOccupancyLabel;
    private JLabel resourceQuantityLabel;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public ReportsPanel() {

        reportDAO = new ReportDAO();

        setLayout(
                new BorderLayout()
        );

        setBackground(
                BACKGROUND
        );

        buildUI();

        loadReports();
    }


    // ==========================================
    // BUILD UI
    // ==========================================

    private void buildUI() {

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout()
                );

        mainPanel.setBackground(
                BACKGROUND
        );

        mainPanel.setBorder(
                new EmptyBorder(
                        20,
                        28,
                        20,
                        28
                )
        );


        // ==========================================
        // HEADER
        // ==========================================

        JPanel headerPanel =
                new JPanel(
                        new BorderLayout()
                );

        headerPanel.setOpaque(false);


        JPanel titlePanel =
                new JPanel();

        titlePanel.setOpaque(false);

        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS
                )
        );


        JLabel titleLabel =
                new JLabel(
                        "Reports & Analytics"
                );

        titleLabel.setForeground(
                TEXT
        );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );


        JLabel subtitleLabel =
                new JLabel(
                        "Real-time emergency response statistics"
                );

        subtitleLabel.setForeground(
                SUBTEXT
        );

        subtitleLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );


        titlePanel.add(
                titleLabel
        );

        titlePanel.add(
                Box.createVerticalStrut(5)
        );

        titlePanel.add(
                subtitleLabel
        );


        JButton refreshButton =
                new JButton(
                        "Refresh Reports"
                );

        refreshButton.setForeground(
                Color.WHITE
        );

        refreshButton.setBackground(
                BLUE
        );

        refreshButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12
                )
        );

        refreshButton.setFocusPainted(false);

        refreshButton.setBorderPainted(false);

        refreshButton.setPreferredSize(
                new Dimension(
                        150,
                        40
                )
        );

        refreshButton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        refreshButton.addActionListener(
                e -> loadReports()
        );


        headerPanel.add(
                titlePanel,
                BorderLayout.WEST
        );

        headerPanel.add(
                refreshButton,
                BorderLayout.EAST
        );


        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );


        // ==========================================
        // REPORT CONTENT
        // ==========================================

        JPanel reportContainer =
                new JPanel(
                        new GridLayout(
                                5,
                                1,
                                0,
                                14
                        )
                );

        reportContainer.setOpaque(false);

        reportContainer.setBorder(
                new EmptyBorder(
                        20,
                        0,
                        0,
                        0
                )
        );


        // ==========================================
        // DISASTER SECTION
        // ==========================================

        JPanel disasterSection =
                createReportSection(
                        "DISASTER OVERVIEW",
                        RED
                );

        totalDisastersLabel =
                addMetric(
                        disasterSection,
                        "Total Disasters",
                        "0"
                );

        activeDisastersLabel =
                addMetric(
                        disasterSection,
                        "Active",
                        "0"
                );

        resolvedDisastersLabel =
                addMetric(
                        disasterSection,
                        "Resolved",
                        "0"
                );

        reportContainer.add(
                disasterSection
        );


        // ==========================================
        // VICTIM SECTION
        // ==========================================

        JPanel victimSection =
                createReportSection(
                        "VICTIM OVERVIEW",
                        ORANGE
                );

        totalVictimsLabel =
                addMetric(
                        victimSection,
                        "Total Victims",
                        "0"
                );

        highPriorityVictimsLabel =
                addMetric(
                        victimSection,
                        "High Priority",
                        "0"
                );

        waitingVictimsLabel =
                addMetric(
                        victimSection,
                        "Waiting",
                        "0"
                );

        reportContainer.add(
                victimSection
        );


        // ==========================================
        // SHELTER SECTION
        // ==========================================

        JPanel shelterSection =
                createReportSection(
                        "SHELTER OVERVIEW",
                        GREEN
                );

        totalSheltersLabel =
                addMetric(
                        shelterSection,
                        "Total Shelters",
                        "0"
                );

        availableSheltersLabel =
                addMetric(
                        shelterSection,
                        "Available",
                        "0"
                );

        fullSheltersLabel =
                addMetric(
                        shelterSection,
                        "Full",
                        "0"
                );

        reportContainer.add(
                shelterSection
        );


        // ==========================================
        // RESOURCE SECTION
        // ==========================================

        JPanel resourceSection =
                createReportSection(
                        "RESOURCE OVERVIEW",
                        BLUE
                );

        totalResourcesLabel =
                addMetric(
                        resourceSection,
                        "Resource Types",
                        "0"
                );

        availableResourcesLabel =
                addMetric(
                        resourceSection,
                        "Available",
                        "0"
                );

        outOfStockResourcesLabel =
                addMetric(
                        resourceSection,
                        "Out of Stock",
                        "0"
                );

        reportContainer.add(
                resourceSection
        );


        // ==========================================
        // EVACUATION SECTION
        // ==========================================

        JPanel evacuationSection =
                createReportSection(
                        "EVACUATION OVERVIEW",
                        PURPLE
                );

        totalEvacuationsLabel =
                addMetric(
                        evacuationSection,
                        "Total",
                        "0"
                );

        pendingEvacuationsLabel =
                addMetric(
                        evacuationSection,
                        "Pending",
                        "0"
                );

        completedEvacuationsLabel =
                addMetric(
                        evacuationSection,
                        "Completed",
                        "0"
                );

        reportContainer.add(
                evacuationSection
        );


        mainPanel.add(
                reportContainer,
                BorderLayout.CENTER
        );


        // ==========================================
        // BOTTOM SUMMARY
        // ==========================================

        JPanel bottomPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                14,
                                0
                        )
                );

        bottomPanel.setOpaque(false);

        bottomPanel.setBorder(
                new EmptyBorder(
                        15,
                        0,
                        0,
                        0
                )
        );


        JPanel capacityCard =
                createSummaryCard(
                        "TOTAL SHELTER CAPACITY",
                        "0",
                        GREEN
                );

        shelterCapacityLabel =
                (JLabel) capacityCard.getClientProperty(
                        "valueLabel"
                );


        JPanel occupancyCard =
                createSummaryCard(
                        "CURRENT OCCUPANCY",
                        "0",
                        TEAL
                );

        shelterOccupancyLabel =
                (JLabel) occupancyCard.getClientProperty(
                        "valueLabel"
                );


        JPanel resourceQuantityCard =
                createSummaryCard(
                        "TOTAL RESOURCE QUANTITY",
                        "0",
                        BLUE
                );

        resourceQuantityLabel =
                (JLabel) resourceQuantityCard.getClientProperty(
                        "valueLabel"
                );


        bottomPanel.add(
                capacityCard
        );

        bottomPanel.add(
                occupancyCard
        );

        bottomPanel.add(
                resourceQuantityCard
        );


        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );


        add(
                mainPanel,
                BorderLayout.CENTER
        );
    }


    // ==========================================
    // REPORT SECTION
    // ==========================================

    private JPanel createReportSection(
            String title,
            Color accent
    ) {

        JPanel section =
                new JPanel(
                        new BorderLayout()
                );

        section.setBackground(
                CARD
        );

        section.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                12,
                                15,
                                12,
                                15
                        )
                )
        );


        JPanel titlePanel =
                new JPanel(
                        new BorderLayout()
                );

        titlePanel.setOpaque(false);


        JPanel accentBar =
                new JPanel();

        accentBar.setBackground(
                accent
        );

        accentBar.setPreferredSize(
                new Dimension(
                        5,
                        28
                )
        );


        JLabel titleLabel =
                new JLabel(
                        title
                );

        titleLabel.setForeground(
                TEXT
        );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        titleLabel.setBorder(
                new EmptyBorder(
                        0,
                        10,
                        0,
                        0
                )
        );


        titlePanel.add(
                accentBar,
                BorderLayout.WEST
        );

        titlePanel.add(
                titleLabel,
                BorderLayout.CENTER
        );


        section.add(
                titlePanel,
                BorderLayout.NORTH
        );


        JPanel metricsPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                12,
                                0
                        )
                );

        metricsPanel.setOpaque(false);

        metricsPanel.setBorder(
                new EmptyBorder(
                        8,
                        0,
                        0,
                        0
                )
        );


        section.add(
                metricsPanel,
                BorderLayout.CENTER
        );


        section.putClientProperty(
                "metricsPanel",
                metricsPanel
        );

        section.putClientProperty(
                "accent",
                accent
        );


        return section;
    }


    // ==========================================
    // ADD METRIC
    // ==========================================

    private JLabel addMetric(
            JPanel section,
            String title,
            String value
    ) {

        JPanel metricsPanel =
                (JPanel) section.getClientProperty(
                        "metricsPanel"
                );

        Color accent =
                (Color) section.getClientProperty(
                        "accent"
                );


        JPanel metric =
                new JPanel(
                        new BorderLayout()
                );

        metric.setBackground(
                new Color(
                        248,
                        249,
                        251
                )
        );

        metric.setBorder(
                BorderFactory.createLineBorder(
                        new Color(
                                230,
                                234,
                                238
                        )
                )
        );


        JLabel titleLabel =
                new JLabel(
                        title
                );

        titleLabel.setForeground(
                SUBTEXT
        );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        10
                )
        );

        titleLabel.setBorder(
                new EmptyBorder(
                        8,
                        10,
                        0,
                        10
                )
        );


        JLabel valueLabel =
                new JLabel(
                        value
                );

        valueLabel.setForeground(
                accent
        );

        valueLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        valueLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        valueLabel.setBorder(
                new EmptyBorder(
                        0,
                        10,
                        8,
                        10
                )
        );


        metric.add(
                titleLabel,
                BorderLayout.NORTH
        );

        metric.add(
                valueLabel,
                BorderLayout.CENTER
        );


        metricsPanel.add(
                metric
        );


        return valueLabel;
    }


    // ==========================================
    // SUMMARY CARD
    // ==========================================

    private JPanel createSummaryCard(
            String title,
            String value,
            Color accent
    ) {

        JPanel card =
                new JPanel(
                        new BorderLayout()
                );

        card.setBackground(
                CARD
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                12,
                                15,
                                12,
                                15
                        )
                )
        );


        JLabel titleLabel =
                new JLabel(
                        title
                );

        titleLabel.setForeground(
                SUBTEXT
        );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        10
                )
        );


        JLabel valueLabel =
                new JLabel(
                        value
                );

        valueLabel.setForeground(
                accent
        );

        valueLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        valueLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );


        card.add(
                titleLabel,
                BorderLayout.NORTH
        );

        card.add(
                valueLabel,
                BorderLayout.CENTER
        );


        card.putClientProperty(
                "valueLabel",
                valueLabel
        );


        return card;
    }


    // ==========================================
    // LOAD REPORTS
    // ==========================================

    private void loadReports() {

        SwingWorker<Void, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected Void doInBackground() {

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


                        SwingUtilities.invokeLater(
                                () -> {

                                    totalDisastersLabel
                                            .setText(
                                                    String.valueOf(
                                                            totalDisasters
                                                    )
                                            );

                                    activeDisastersLabel
                                            .setText(
                                                    String.valueOf(
                                                            activeDisasters
                                                    )
                                            );

                                    resolvedDisastersLabel
                                            .setText(
                                                    String.valueOf(
                                                            resolvedDisasters
                                                    )
                                            );


                                    totalVictimsLabel
                                            .setText(
                                                    String.valueOf(
                                                            totalVictims
                                                    )
                                            );

                                    highPriorityVictimsLabel
                                            .setText(
                                                    String.valueOf(
                                                            highPriorityVictims
                                                    )
                                            );

                                    waitingVictimsLabel
                                            .setText(
                                                    String.valueOf(
                                                            waitingVictims
                                                    )
                                            );


                                    totalSheltersLabel
                                            .setText(
                                                    String.valueOf(
                                                            totalShelters
                                                    )
                                            );

                                    availableSheltersLabel
                                            .setText(
                                                    String.valueOf(
                                                            availableShelters
                                                    )
                                            );

                                    fullSheltersLabel
                                            .setText(
                                                    String.valueOf(
                                                            fullShelters
                                                    )
                                            );


                                    totalResourcesLabel
                                            .setText(
                                                    String.valueOf(
                                                            totalResources
                                                    )
                                            );

                                    availableResourcesLabel
                                            .setText(
                                                    String.valueOf(
                                                            availableResources
                                                    )
                                            );

                                    outOfStockResourcesLabel
                                            .setText(
                                                    String.valueOf(
                                                            outOfStockResources
                                                    )
                                            );


                                    totalEvacuationsLabel
                                            .setText(
                                                    String.valueOf(
                                                            totalEvacuations
                                                    )
                                            );

                                    pendingEvacuationsLabel
                                            .setText(
                                                    String.valueOf(
                                                            pendingEvacuations
                                                    )
                                            );

                                    completedEvacuationsLabel
                                            .setText(
                                                    String.valueOf(
                                                            completedEvacuations
                                                    )
                                            );


                                    shelterCapacityLabel
                                            .setText(
                                                    String.valueOf(
                                                            shelterCapacity
                                                    )
                                            );

                                    shelterOccupancyLabel
                                            .setText(
                                                    String.valueOf(
                                                            shelterOccupancy
                                                    )
                                            );

                                    resourceQuantityLabel
                                            .setText(
                                                    String.valueOf(
                                                            resourceQuantity
                                                    )
                                            );
                                }
                        );

                        return null;
                    }
                };


        worker.execute();
    }
}