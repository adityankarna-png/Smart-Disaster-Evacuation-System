package com.disaster.gui;

import com.disaster.dao.ReportDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ReportsPanel
 *
 * GUI-only redesign matching the supplied Reports & Analytics reference.
 * ReportDAO/database calls are kept intact.
 */
public class ReportsPanel extends JPanel {

    private static final Color BG = new Color(0x0C1822);
    private static final Color CARD = new Color(0x14283A);
    private static final Color CARD2 = new Color(0x172D41);
    private static final Color LINE = new Color(0x29465E);
    private static final Color TEXT = new Color(0xE8F0F5);
    private static final Color MUTED = new Color(0x8FA7B9);
    private static final Color RED = new Color(0xFF5A64);
    private static final Color ORANGE = new Color(0xF5A83C);
    private static final Color GREEN = new Color(0x3FC79A);
    private static final Color BLUE = new Color(0x4FA8F0);
    private static final Color PURPLE = new Color(0xA58BFF);

    private final ReportDAO reportDAO;

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
    private JLabel freeBedsLabel;
    private JLabel resourceQuantityLabel;

    private JLabel responseIdLabel;
    private JLabel responseVictimLabel;
    private JLabel responsePriorityLabel;
    private JLabel responseStatusLabel;
    private JLabel responseShelterLabel;
    private JLabel responseResourceLabel;
    private JLabel responseTitleLabel;

    private JLabel updatedLabel;
    private JLabel infoLabel;

    private ProgressPanel victimProgress;
    private ProgressPanel shelterProgress;
    private ProgressPanel resourceProgress;
    private ProgressPanel evacuationProgress;
    private CapacityGauge capacityGauge;

    private JButton refreshButton;
    private JButton exportButton;

    private Map<String, String> latestExportData =
            new LinkedHashMap<>();

    public ReportsPanel() {
        reportDAO = new ReportDAO();

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(24, 32, 18, 32));

        buildUI();
        loadReports();
    }

    private static Font f(int style, int size) {
        return new Font("Arial", style, size);
    }

    private static JLabel text(
            String value,
            int style,
            int size,
            Color color
    ) {
        JLabel label = new JLabel(value);
        label.setFont(f(style, size));
        label.setForeground(color);
        return label;
    }

    private static String number(int value) {
        return NumberFormat.getIntegerInstance().format(value);
    }

    private static JLabel responseField(
            JPanel grid,
            String title,
            String value
    ) {
        JPanel field = new JPanel();
        field.setOpaque(false);
        field.setLayout(
                new BoxLayout(field, BoxLayout.Y_AXIS)
        );

        JLabel titleLabel = text(
                title,
                Font.PLAIN,
                11,
                MUTED
        );

        JLabel valueLabel = text(
                value,
                Font.BOLD,
                14,
                TEXT
        );

        field.add(titleLabel);
        field.add(Box.createVerticalStrut(3));
        field.add(valueLabel);

        grid.add(field);

        return valueLabel;
    }

    private void buildUI() {

        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(
                new BoxLayout(titles, BoxLayout.Y_AXIS)
        );

        titles.add(
                text(
                        "Reports & Analytics",
                        Font.BOLD,
                        28,
                        TEXT
                )
        );

        titles.add(Box.createVerticalStrut(3));

        titles.add(
                text(
                        "Real-time emergency response statistics",
                        Font.PLAIN,
                        14,
                        MUTED
                )
        );

        JPanel right = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        12,
                        0
                )
        );
        right.setOpaque(false);

        updatedLabel = text(
                "Updated --:--:--",
                Font.PLAIN,
                12,
                MUTED
        );

        exportButton = button(
                "Export report",
                new Color(0x1D3850),
                TEXT
        );

        refreshButton = button(
                "Refresh reports",
                BLUE,
                new Color(0x06223A)
        );

        exportButton.addActionListener(
                e -> exportReport()
        );

        refreshButton.addActionListener(
                e -> refreshReports()
        );

        right.add(updatedLabel);
        right.add(exportButton);
        right.add(refreshButton);

        header.add(titles, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS
                )
        );

        // =====================================================
        // ROW 1 - VICTIMS / SHELTERS
        // =====================================================

        JPanel row1 = row();

        victimProgress = new ProgressPanel();
        shelterProgress = new ProgressPanel();

        row1.add(
                overviewCard(
                        "VICTIM OVERVIEW",
                        RED,
                        new String[]{
                                "Total victims",
                                "High priority",
                                "Waiting"
                        },
                        new Color[]{
                                RED,
                                RED,
                                RED
                        },
                        new JLabel[]{
                                totalVictimsLabel =
                                        valueLabel(RED),
                                highPriorityVictimsLabel =
                                        valueLabel(RED),
                                waitingVictimsLabel =
                                        valueLabel(RED)
                        },
                        victimProgress
                )
        );

        row1.add(
                overviewCard(
                        "SHELTER OVERVIEW",
                        GREEN,
                        new String[]{
                                "Total shelters",
                                "Available",
                                "Full"
                        },
                        new Color[]{
                                GREEN,
                                GREEN,
                                GREEN
                        },
                        new JLabel[]{
                                totalSheltersLabel =
                                        valueLabel(GREEN),
                                availableSheltersLabel =
                                        valueLabel(GREEN),
                                fullSheltersLabel =
                                        valueLabel(GREEN)
                        },
                        shelterProgress
                )
        );

        content.add(row1);
        content.add(Box.createVerticalStrut(18));

        // =====================================================
        // ROW 2 - RESOURCE / EVACUATION
        // =====================================================

        JPanel row2 = row();

        resourceProgress = new ProgressPanel();
        evacuationProgress = new ProgressPanel();

        row2.add(
                overviewCard(
                        "RESOURCE OVERVIEW",
                        BLUE,
                        new String[]{
                                "Resource types",
                                "Available",
                                "Out of stock"
                        },
                        new Color[]{
                                BLUE,
                                BLUE,
                                BLUE
                        },
                        new JLabel[]{
                                totalResourcesLabel =
                                        valueLabel(BLUE),
                                availableResourcesLabel =
                                        valueLabel(BLUE),
                                outOfStockResourcesLabel =
                                        valueLabel(BLUE)
                        },
                        resourceProgress
                )
        );

        row2.add(
                overviewCard(
                        "EVACUATION OVERVIEW",
                        PURPLE,
                        new String[]{
                                "Total",
                                "Pending",
                                "Completed"
                        },
                        new Color[]{
                                PURPLE,
                                PURPLE,
                                PURPLE
                        },
                        new JLabel[]{
                                totalEvacuationsLabel =
                                        valueLabel(PURPLE),
                                pendingEvacuationsLabel =
                                        valueLabel(PURPLE),
                                completedEvacuationsLabel =
                                        valueLabel(PURPLE)
                        },
                        evacuationProgress
                )
        );

        content.add(row2);
        content.add(Box.createVerticalStrut(18));

        // =====================================================
        // ROW 3 - RESPONSE / CAPACITY
        // =====================================================

        JPanel row3 = new JPanel(
                new GridLayout(1, 2, 18, 0)
        );
        row3.setOpaque(false);
        row3.setPreferredSize(
                new Dimension(0, 270)
        );
        row3.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        270
                )
        );
        row3.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        row3.add(createResponseCard());
        row3.add(createCapacityCard());

        content.add(row3);

        JScrollPane scroll = new JScrollPane(
                content
        );
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        root.add(scroll, BorderLayout.CENTER);

        infoLabel = text(
                " ",
                Font.PLAIN,
                12,
                MUTED
        );

        infoLabel.setBorder(
                new EmptyBorder(7, 0, 0, 0)
        );

        root.add(
                infoLabel,
                BorderLayout.SOUTH
        );

        add(root, BorderLayout.CENTER);
    }

    private JPanel row() {
        JPanel row = new JPanel(
                new GridLayout(1, 2, 18, 0)
        );
        row.setOpaque(false);
        row.setPreferredSize(
                new Dimension(0, 196)
        );
        row.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        196
                )
        );
        row.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );
        return row;
    }

    private JPanel overviewCard(
            String title,
            Color accent,
            String[] names,
            Color[] colors,
            JLabel[] values,
            ProgressPanel progress
    ) {

        RoundedPanel card = new RoundedPanel(12);
        card.setLayout(
                new BorderLayout()
        );
        card.setBorder(
                new EmptyBorder(
                        17,
                        21,
                        13,
                        21
                )
        );

        JPanel heading = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        0,
                        0
                )
        );
        heading.setOpaque(false);

        JPanel square = new JPanel();
        square.setBackground(accent);
        square.setPreferredSize(
                new Dimension(10, 10)
        );

        heading.add(square);
        heading.add(
                Box.createHorizontalStrut(9)
        );
        heading.add(
                text(
                        title,
                        Font.BOLD,
                        13,
                        TEXT
                )
        );

        card.add(
                heading,
                BorderLayout.NORTH
        );

        JPanel metrics = new JPanel(
                new GridLayout(1, 3, 22, 0)
        );
        metrics.setOpaque(false);
        metrics.setBorder(
                new EmptyBorder(
                        10,
                        0,
                        0,
                        0
                )
        );

        for (int i = 0; i < 3; i++) {

            JPanel metric = new JPanel();
            metric.setOpaque(false);
            metric.setLayout(
                    new BoxLayout(
                            metric,
                            BoxLayout.Y_AXIS
                    )
            );

            metric.add(
                    text(
                            names[i],
                            Font.PLAIN,
                            11,
                            MUTED
                    )
            );

            metric.add(
                    Box.createVerticalStrut(3)
            );

            metric.add(values[i]);

            metrics.add(metric);
        }

        JPanel center = new JPanel(
                new BorderLayout()
        );
        center.setOpaque(false);

        center.add(
                metrics,
                BorderLayout.NORTH
        );

        center.add(
                progress,
                BorderLayout.CENTER
        );

        card.add(
                center,
                BorderLayout.CENTER
        );

        return card;
    }

    private JLabel valueLabel(Color color) {
        JLabel label = new JLabel("0");
        label.setFont(
                f(Font.BOLD, 30)
        );
        label.setForeground(color);
        return label;
    }

    private JPanel createResponseCard() {

        RoundedPanel card = new RoundedPanel(12);

        card.setLayout(new BorderLayout());

        card.setBorder(
                new EmptyBorder(
                        18,
                        26,
                        18,
                        22
                )
        );

        // Thin red accent bar at the far left.
        JPanel accent = new JPanel();
        accent.setOpaque(true);
        accent.setBackground(RED);
        accent.setPreferredSize(
                new Dimension(4, 224)
        );

        card.add(
                accent,
                BorderLayout.WEST
        );

        // All response content starts directly beside the accent.
        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setBorder(
                new EmptyBorder(
                        7,
                        22,
                        0,
                        0
                )
        );

        details.setLayout(
                new BoxLayout(
                        details,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel sectionTitle = text(
                "LATEST EMERGENCY RESPONSE",
                Font.BOLD,
                13,
                RED
        );
        sectionTitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        details.add(sectionTitle);
        details.add(
                Box.createVerticalStrut(7)
        );

        responseTitleLabel = text(
                "Latest evacuation response detected",
                Font.BOLD,
                17,
                TEXT
        );
        responseTitleLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        details.add(responseTitleLabel);
        details.add(
                Box.createVerticalStrut(12)
        );

        JPanel grid = new JPanel(
                new GridLayout(
                        3,
                        2,
                        42,
                        9
                )
        );
        grid.setOpaque(false);
        grid.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        responseIdLabel =
                responseField(
                        grid,
                        "Evacuation ID",
                        "—"
                );

        responseVictimLabel =
                responseField(
                        grid,
                        "Victim",
                        "—"
                );

        responsePriorityLabel =
                responseField(
                        grid,
                        "Priority",
                        "—"
                );

        responseStatusLabel =
                responseField(
                        grid,
                        "Status",
                        "—"
                );

        responseShelterLabel =
                responseField(
                        grid,
                        "Shelter",
                        "—"
                );

        responseResourceLabel =
                responseField(
                        grid,
                        "Resource",
                        "—"
                );

        details.add(grid);
        details.add(
                Box.createVerticalStrut(9)
        );

        JButton viewButton = button(
                "View all evacuations",
                new Color(0x1D3850),
                TEXT
        );

        viewButton.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        viewButton.addActionListener(
                e -> JOptionPane.showMessageDialog(
                        this,
                        "Evacuation records are available in the Evacuation Management panel.",
                        "Evacuation Records",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        details.add(viewButton);

        card.add(
                details,
                BorderLayout.CENTER
        );

        return card;
    }

    private JPanel createCapacityCard() {

        RoundedPanel card = new RoundedPanel(12);
        card.setLayout(
                new BorderLayout()
        );
        card.setBorder(
                new EmptyBorder(
                        19,
                        21,
                        17,
                        21
                )
        );

        JPanel heading = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        0,
                        0
                )
        );
        heading.setOpaque(false);

        JPanel square = new JPanel();
        square.setBackground(ORANGE);
        square.setPreferredSize(
                new Dimension(10, 10)
        );

        heading.add(square);
        heading.add(
                Box.createHorizontalStrut(9)
        );

        heading.add(
                text(
                        "CAPACITY & STOCK",
                        Font.BOLD,
                        13,
                        TEXT
                )
        );

        card.add(
                heading,
                BorderLayout.NORTH
        );

        JPanel body = new JPanel(
                new BorderLayout(18, 0)
        );
        body.setOpaque(false);

        capacityGauge = new CapacityGauge();
        capacityGauge.setPreferredSize(
                new Dimension(145, 170)
        );

        body.add(
                capacityGauge,
                BorderLayout.WEST
        );

        JPanel stats = new JPanel();
        stats.setOpaque(false);
        stats.setLayout(
                new BoxLayout(
                        stats,
                        BoxLayout.Y_AXIS
                )
        );

        stats.add(
                text(
                        "Total shelter capacity",
                        Font.PLAIN,
                        11,
                        MUTED
                )
        );

        shelterCapacityLabel =
                text(
                        "0",
                        Font.BOLD,
                        29,
                        ORANGE
                );

        stats.add(shelterCapacityLabel);
        stats.add(
                Box.createVerticalStrut(7)
        );

        stats.add(
                text(
                        "Current occupancy",
                        Font.PLAIN,
                        11,
                        MUTED
                )
        );

        shelterOccupancyLabel =
                text(
                        "0",
                        Font.BOLD,
                        29,
                        GREEN
                );

        stats.add(shelterOccupancyLabel);
        stats.add(
                Box.createVerticalStrut(7)
        );

        stats.add(
                text(
                        "Free beds",
                        Font.PLAIN,
                        11,
                        MUTED
                )
        );

        freeBedsLabel =
                text(
                        "0",
                        Font.BOLD,
                        29,
                        BLUE
                );

        stats.add(freeBedsLabel);

        body.add(
                stats,
                BorderLayout.CENTER
        );

        card.add(
                body,
                BorderLayout.CENTER
        );

        JPanel bottom = new JPanel(
                new BorderLayout()
        );
        bottom.setOpaque(false);
        bottom.setBorder(
                new EmptyBorder(
                        7,
                        0,
                        0,
                        0
                )
        );

        JPanel line = new JPanel();
        line.setBackground(LINE);
        line.setPreferredSize(
                new Dimension(1, 1)
        );

        bottom.add(
                line,
                BorderLayout.NORTH
        );

        JPanel resourceRow = new JPanel(
                new BorderLayout()
        );
        resourceRow.setOpaque(false);
        resourceRow.setBorder(
                new EmptyBorder(
                        8,
                        0,
                        0,
                        0
                )
        );

        resourceRow.add(
                text(
                        "Total resource quantity",
                        Font.BOLD,
                        12,
                        MUTED
                ),
                BorderLayout.WEST
        );

        resourceQuantityLabel =
                text(
                        "0",
                        Font.BOLD,
                        26,
                        GREEN
                );

        resourceRow.add(
                resourceQuantityLabel,
                BorderLayout.EAST
        );

        bottom.add(
                resourceRow,
                BorderLayout.CENTER
        );

        card.add(
                bottom,
                BorderLayout.SOUTH
        );

        return card;
    }

    private JButton button(
            String title,
            Color background,
            Color foreground
    ) {

        JButton b = new JButton(title);

        b.setFont(
                f(Font.BOLD, 12)
        );

        b.setForeground(foreground);
        b.setBackground(background);

        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);

        b.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        b.setBorder(
                new EmptyBorder(
                        10,
                        17,
                        10,
                        17
                )
        );

        return b;
    }

    // =========================================================
    // PROGRESS PANEL
    // =========================================================

    private static class ProgressPanel
            extends JPanel {

        private double first;
        private double second;

        private Color firstColor = RED;
        private Color secondColor = new Color(0x4C6A85);

        private String firstText = "";
        private String secondText = "";

        ProgressPanel() {
            setOpaque(false);
            setPreferredSize(
                    new Dimension(0, 55)
            );
        }

        void setData(
                double first,
                double second,
                Color firstColor,
                Color secondColor,
                String firstText,
                String secondText
        ) {

            this.first = Math.max(
                    0,
                    Math.min(1, first)
            );

            this.second = Math.max(
                    0,
                    Math.min(1, second)
            );

            this.firstColor = firstColor;
            this.secondColor = secondColor;
            this.firstText = firstText;
            this.secondText = secondText;

            repaint();
        }

        @Override
        protected void paintComponent(
                Graphics g0
        ) {

            Graphics2D g =
                    (Graphics2D) g0.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w = getWidth();
            int barY = 7;
            int barH = 9;

            g.setColor(
                    new Color(0x496982)
            );

            g.fillRoundRect(
                    0,
                    barY,
                    w,
                    barH,
                    10,
                    10
            );

            int firstW =
                    (int) Math.round(
                            w * first
                    );

            if (firstW > 0) {

                g.setColor(firstColor);

                g.fillRoundRect(
                        0,
                        barY,
                        firstW,
                        barH,
                        10,
                        10
                );
            }

            int secondW =
                    (int) Math.round(
                            w * second
                    );

            if (secondW > 0) {

                g.setColor(secondColor);

                int secondX = firstW;

                if (firstW == 0) {
                    secondX = 0;
                }

                g.fillRoundRect(
                        secondX,
                        barY,
                        Math.min(
                                secondW,
                                w - secondX
                        ),
                        barH,
                        10,
                        10
                );
            }

            int y = 35;

            if (!firstText.isEmpty()) {

                g.setColor(firstColor);

                g.fillOval(
                        1,
                        y + 1,
                        7,
                        7
                );

                g.setFont(
                        f(Font.PLAIN, 11)
                );

                g.setColor(MUTED);

                g.drawString(
                        firstText,
                        14,
                        y + 9
                );
            }

            if (!secondText.isEmpty()) {

                int offset =
                        Math.min(
                                180,
                                18 + firstText.length() * 6
                        );

                g.setColor(secondColor);

                g.fillOval(
                        offset,
                        y + 1,
                        7,
                        7
                );

                g.setColor(MUTED);

                g.drawString(
                        secondText,
                        offset + 13,
                        y + 9
                );
            }

            g.dispose();
        }
    }

    // =========================================================
    // CAPACITY GAUGE
    // =========================================================

    private static class CapacityGauge
            extends JComponent {

        private int capacity;
        private int occupancy;

        void setValues(
                int capacity,
                int occupancy
        ) {

            this.capacity = capacity;
            this.occupancy = occupancy;

            repaint();
        }

        @Override
        protected void paintComponent(
                Graphics g0
        ) {

            Graphics2D g =
                    (Graphics2D) g0.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            double ratio =
                    capacity <= 0
                            ? 0
                            : Math.min(
                            1,
                            occupancy /
                                    (double) capacity
                    );

            int size = 116;
            int x = 10;
            int y = 13;

            g.setStroke(
                    new BasicStroke(
                            12f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            g.setColor(
                    new Color(
                            255,
                            255,
                            255,
                            25
                    )
            );

            g.drawArc(
                    x,
                    y,
                    size,
                    size,
                    0,
                    360
            );

            g.setColor(GREEN);

            g.draw(
                    new Arc2D.Double(
                            x,
                            y,
                            size,
                            size,
                            90,
                            -360 * ratio,
                            Arc2D.OPEN
                    )
            );

            String percent =
                    Math.round(
                            ratio * 100
                    ) + "%";

            g.setFont(
                    f(Font.BOLD, 25)
            );

            FontMetrics fm =
                    g.getFontMetrics();

            g.setColor(TEXT);

            g.drawString(
                    percent,
                    x + (
                            size -
                                    fm.stringWidth(
                                            percent
                                    )
                    ) / 2,
                    y + 67
            );

            g.setFont(
                    f(Font.PLAIN, 11)
            );

            fm = g.getFontMetrics();

            g.setColor(MUTED);

            String occupiedText =
                    "occupied";

            g.drawString(
                    occupiedText,
                    x + (
                            size -
                                    fm.stringWidth(
                                            occupiedText
                                    )
                    ) / 2,
                    y + 86
            );

            g.dispose();
        }
    }

    // =========================================================
    // REFRESH / LOAD
    // =========================================================

    private void refreshReports() {

        refreshButton.setEnabled(false);
        refreshButton.setText(
                "Refreshing..."
        );

        loadReports(() -> {

            refreshButton.setEnabled(true);
            refreshButton.setText(
                    "Refresh reports"
            );

            infoLabel.setForeground(GREEN);
            infoLabel.setText(
                    "Reports refreshed successfully."
            );

            Timer timer = new Timer(
                    2500,
                    e -> infoLabel.setText(" ")
            );

            timer.setRepeats(false);
            timer.start();
        });
    }

    private void loadReports() {
        loadReports(null);
    }

    private void loadReports(
            Runnable afterLoad
    ) {

        SwingWorker<ReportValues, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected ReportValues doInBackground() {

                        ReportValues r =
                                new ReportValues();

                        // Keep all existing DAO calls.
                        r.totalDisasters =
                                reportDAO.getTotalDisasters();

                        r.activeDisasters =
                                reportDAO.getActiveDisasters();

                        r.resolvedDisasters =
                                reportDAO.getResolvedDisasters();

                        r.totalVictims =
                                reportDAO.getTotalVictims();

                        r.highPriorityVictims =
                                reportDAO.getHighPriorityVictims();

                        r.waitingVictims =
                                reportDAO.getWaitingVictims();

                        r.totalShelters =
                                reportDAO.getTotalShelters();

                        r.availableShelters =
                                reportDAO.getAvailableShelters();

                        r.fullShelters =
                                reportDAO.getFullShelters();

                        r.totalResources =
                                reportDAO.getTotalResources();

                        r.availableResources =
                                reportDAO.getAvailableResources();

                        r.outOfStockResources =
                                reportDAO.getOutOfStockResources();

                        r.totalEvacuations =
                                reportDAO.getTotalEvacuations();

                        r.pendingEvacuations =
                                reportDAO.getPendingEvacuations();

                        r.completedEvacuations =
                                reportDAO.getCompletedEvacuations();

                        r.shelterCapacity =
                                reportDAO.getTotalShelterCapacity();

                        r.shelterOccupancy =
                                reportDAO.getCurrentShelterOccupancy();

                        r.resourceQuantity =
                                reportDAO.getTotalResourceQuantity();

                        r.latestResponse =
                                reportDAO.getLatestEvacuationResponse();

                        return r;
                    }

                    @Override
                    protected void done() {

                        try {

                            applyReportValues(
                                    get()
                            );

                            if (afterLoad != null) {
                                afterLoad.run();
                            }

                        } catch (Exception ex) {

                            ex.printStackTrace();

                            infoLabel.setForeground(
                                    RED
                            );

                            infoLabel.setText(
                                    "Unable to load report data."
                            );

                            if (afterLoad != null) {
                                afterLoad.run();
                            }
                        }
                    }
                };

        worker.execute();
    }

    private void applyReportValues(
            ReportValues r
    ) {

        totalVictimsLabel.setText(
                number(r.totalVictims)
        );

        highPriorityVictimsLabel.setText(
                number(r.highPriorityVictims)
        );

        waitingVictimsLabel.setText(
                number(r.waitingVictims)
        );

        totalSheltersLabel.setText(
                number(r.totalShelters)
        );

        availableSheltersLabel.setText(
                number(r.availableShelters)
        );

        fullSheltersLabel.setText(
                number(r.fullShelters)
        );

        totalResourcesLabel.setText(
                number(r.totalResources)
        );

        availableResourcesLabel.setText(
                number(r.availableResources)
        );

        outOfStockResourcesLabel.setText(
                number(r.outOfStockResources)
        );

        totalEvacuationsLabel.setText(
                number(r.totalEvacuations)
        );

        pendingEvacuationsLabel.setText(
                number(r.pendingEvacuations)
        );

        completedEvacuationsLabel.setText(
                number(r.completedEvacuations)
        );

        shelterCapacityLabel.setText(
                number(r.shelterCapacity)
        );

        shelterOccupancyLabel.setText(
                number(r.shelterOccupancy)
        );

        int freeBeds =
                Math.max(
                        0,
                        r.shelterCapacity -
                                r.shelterOccupancy
                );

        freeBedsLabel.setText(
                number(freeBeds)
        );

        resourceQuantityLabel.setText(
                number(r.resourceQuantity)
        );

        // -----------------------------------------------------
        // VICTIM PROGRESS
        // -----------------------------------------------------

        double victimPriority =
                r.totalVictims <= 0
                        ? 0
                        : r.highPriorityVictims /
                        (double) r.totalVictims;

        double victimLower =
                Math.max(
                        0,
                        1 - victimPriority
                );

        victimProgress.setData(
                victimPriority,
                victimLower,
                RED,
                new Color(0x496982),
                "High priority "
                        + number(
                        r.highPriorityVictims
                ),
                "Lower priority "
                        + number(
                        Math.max(
                                0,
                                r.totalVictims -
                                        r.highPriorityVictims
                        )
                )
        );

        // Waiting is retained in the DAO-driven value but the reference
        // progress is based on the victim total/priority distribution.

        // -----------------------------------------------------
        // SHELTER PROGRESS
        // -----------------------------------------------------

        double shelterAvailable =
                r.totalShelters <= 0
                        ? 0
                        : r.availableShelters /
                        (double) r.totalShelters;

        double shelterFull =
                r.totalShelters <= 0
                        ? 0
                        : r.fullShelters /
                        (double) r.totalShelters;

        shelterProgress.setData(
                shelterAvailable,
                shelterFull,
                GREEN,
                RED,
                "Available "
                        + number(
                        r.availableShelters
                ),
                "Full "
                        + number(
                        r.fullShelters
                )
        );

        // -----------------------------------------------------
        // RESOURCE PROGRESS
        // -----------------------------------------------------

        double resourceAvailable =
                r.totalResources <= 0
                        ? 0
                        : r.availableResources /
                        (double) r.totalResources;

        double resourceOut =
                r.totalResources <= 0
                        ? 0
                        : r.outOfStockResources /
                        (double) r.totalResources;

        resourceProgress.setData(
                resourceAvailable,
                resourceOut,
                GREEN,
                RED,
                "Available "
                        + number(
                        r.availableResources
                ),
                "Out of stock "
                        + number(
                        r.outOfStockResources
                )
        );

        // -----------------------------------------------------
        // EVACUATION PROGRESS
        // -----------------------------------------------------

        double evacuationCompleted =
                r.totalEvacuations <= 0
                        ? 0
                        : r.completedEvacuations /
                        (double) r.totalEvacuations;

        double evacuationPending =
                r.totalEvacuations <= 0
                        ? 0
                        : r.pendingEvacuations /
                        (double) r.totalEvacuations;

        evacuationProgress.setData(
                evacuationCompleted,
                evacuationPending,
                GREEN,
                ORANGE,
                "Completed "
                        + number(
                        r.completedEvacuations
                ),
                "Pending "
                        + number(
                        r.pendingEvacuations
                )
        );

        capacityGauge.setValues(
                r.shelterCapacity,
                r.shelterOccupancy
        );

        updateLatestResponse(
                r.latestResponse
        );

        latestExportData =
                buildExportData(r);

        updatedLabel.setText(
                "Updated "
                        + LocalTime.now().format(
                        DateTimeFormatter.ofPattern(
                                "HH:mm:ss"
                        )
                )
        );

        revalidate();
        repaint();
    }

    // =========================================================
    // LATEST RESPONSE
    // =========================================================

    private void updateLatestResponse(
            String latestResponse
    ) {

        if (latestResponse == null
                || latestResponse.trim().isEmpty()
                || latestResponse.equals(
                "No evacuation response available."
        )) {

            responseTitleLabel.setText(
                    "No emergency response recorded."
            );

            responseIdLabel.setText("—");
            responseVictimLabel.setText("—");
            responsePriorityLabel.setText("—");
            responseStatusLabel.setText("—");
            responseShelterLabel.setText("—");
            responseResourceLabel.setText("—");

            return;
        }

        responseTitleLabel.setText(
                "Latest evacuation response detected"
        );

        Map<String, String> values =
                parseResponse(latestResponse);

        responseIdLabel.setText(
                values.getOrDefault(
                        "Evacuation ID",
                        "—"
                )
        );

        responseVictimLabel.setText(
                values.getOrDefault(
                        "Victim",
                        "—"
                )
        );

        String priority =
                values.getOrDefault(
                        "Priority",
                        "—"
                );

        responsePriorityLabel.setText(
                priority
        );

        String status =
                values.getOrDefault(
                        "Status",
                        "—"
                );

        responseStatusLabel.setText(
                status
        );

        responseShelterLabel.setText(
                values.getOrDefault(
                        "Shelter",
                        "—"
                )
        );

        responseResourceLabel.setText(
                values.getOrDefault(
                        "Resource",
                        "—"
                )
        );
    }

    private Map<String, String> parseResponse(
            String response
    ) {

        Map<String, String> map =
                new LinkedHashMap<>();

        String[] lines =
                response.replace(
                        "<br>",
                        "\n"
                ).split(
                        "\\r?\\n"
                );

        for (String line : lines) {

            int colon =
                    line.indexOf(':');

            if (colon <= 0) {
                continue;
            }

            String key =
                    line.substring(
                            0,
                            colon
                    ).trim();

            String value =
                    line.substring(
                            colon + 1
                    ).trim();

            map.put(key, value);
        }

        return map;
    }

    // =========================================================
    // EXPORT
    // =========================================================

    private void exportReport() {

        JFileChooser chooser =
                new JFileChooser();

        chooser.setDialogTitle(
                "Export Report"
        );

        chooser.setSelectedFile(
                new File(
                        "disaster_report.csv"
                )
        );

        if (chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {

            return;
        }

        File file =
                chooser.getSelectedFile();

        try (FileWriter writer =
                     new FileWriter(file)) {

            writer.write(
                    "REPORT,METRIC,VALUE\n"
            );

            for (Map.Entry<String, String> entry :
                    latestExportData.entrySet()) {

                writer.write(
                        csv("Emergency Report")
                                + ","
                                + csv(entry.getKey())
                                + ","
                                + csv(entry.getValue())
                                + "\n"
                );
            }

            infoLabel.setForeground(
                    GREEN
            );

            infoLabel.setText(
                    "Report exported successfully."
            );

        } catch (Exception ex) {

            ex.printStackTrace();

            infoLabel.setForeground(
                    RED
            );

            infoLabel.setText(
                    "Unable to export report."
            );
        }
    }

    private static String csv(
            String value
    ) {

        if (value == null) {
            return "\"\"";
        }

        return "\""
                + value.replace(
                "\"",
                "\"\""
        )
                + "\"";
    }

    private Map<String, String> buildExportData(
            ReportValues r
    ) {

        Map<String, String> data =
                new LinkedHashMap<>();

        data.put(
                "Total Victims",
                number(r.totalVictims)
        );

        data.put(
                "High Priority Victims",
                number(r.highPriorityVictims)
        );

        data.put(
                "Waiting Victims",
                number(r.waitingVictims)
        );

        data.put(
                "Total Shelters",
                number(r.totalShelters)
        );

        data.put(
                "Available Shelters",
                number(r.availableShelters)
        );

        data.put(
                "Full Shelters",
                number(r.fullShelters)
        );

        data.put(
                "Resource Types",
                number(r.totalResources)
        );

        data.put(
                "Available Resources",
                number(r.availableResources)
        );

        data.put(
                "Out of Stock Resources",
                number(r.outOfStockResources)
        );

        data.put(
                "Total Evacuations",
                number(r.totalEvacuations)
        );

        data.put(
                "Pending Evacuations",
                number(r.pendingEvacuations)
        );

        data.put(
                "Completed Evacuations",
                number(r.completedEvacuations)
        );

        data.put(
                "Total Shelter Capacity",
                number(r.shelterCapacity)
        );

        data.put(
                "Current Occupancy",
                number(r.shelterOccupancy)
        );

        data.put(
                "Free Beds",
                number(
                        Math.max(
                                0,
                                r.shelterCapacity -
                                        r.shelterOccupancy
                        )
                )
        );

        data.put(
                "Total Resource Quantity",
                number(r.resourceQuantity)
        );

        return data;
    }

    // =========================================================
    // DATA HOLDER
    // =========================================================

    private static class ReportValues {

        int totalDisasters;
        int activeDisasters;
        int resolvedDisasters;

        int totalVictims;
        int highPriorityVictims;
        int waitingVictims;

        int totalShelters;
        int availableShelters;
        int fullShelters;

        int totalResources;
        int availableResources;
        int outOfStockResources;

        int totalEvacuations;
        int pendingEvacuations;
        int completedEvacuations;

        int shelterCapacity;
        int shelterOccupancy;
        int resourceQuantity;

        String latestResponse;
    }

    // =========================================================
    // ROUNDED CARD
    // =========================================================

    private static class RoundedPanel
            extends JPanel {

        private final int radius;

        RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(
                Graphics g0
        ) {

            Graphics2D g =
                    (Graphics2D) g0.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g.setColor(CARD);

            g.fillRoundRect(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 1,
                    radius,
                    radius
            );

            g.setColor(LINE);

            g.drawRoundRect(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 1,
                    radius,
                    radius
            );

            g.dispose();

            super.paintComponent(g0);
        }
    }
}
