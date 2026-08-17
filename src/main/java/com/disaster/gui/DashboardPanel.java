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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class DashboardPanel extends JPanel {

    // ==========================================
    // COLORS  (refined, cohesive palette)
    // ==========================================

    private static final Color BACKGROUND     = new Color(238, 241, 246); // cool light gray
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255);
    private static final Color TEXT           = new Color(30, 41, 59);   // slate-800
    private static final Color SECONDARY_TEXT  = new Color(100, 116, 139); // slate-500
    private static final Color BORDER          = new Color(226, 232, 240); // slate-200
    private static final Color PILL_BACKGROUND = new Color(243, 246, 250);

    private static final Color BLUE   = new Color(37, 99, 235);
    private static final Color BLUE_H = new Color(29, 78, 216);
    private static final Color RED    = new Color(220, 38, 38);
    private static final Color GREEN  = new Color(22, 163, 74);
    private static final Color ORANGE = new Color(217, 119, 6);


    // ==========================================
    // FONTS  (modern stack with graceful fallback)
    // ==========================================

    private static final String FONT_FAMILY = resolveFontFamily(
            "Segoe UI", "SF Pro Text", "Helvetica Neue", "Roboto", "Arial"
    );

    private static Font font(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    private static String resolveFontFamily(String... preferences) {
        Map<String, Boolean> available = new HashMap<>();
        for (String f : GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames()) {
            available.put(f, Boolean.TRUE);
        }
        for (String pref : preferences) {
            if (available.containsKey(pref)) {
                return pref;
            }
        }
        return Font.SANS_SERIF;
    }


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

        disasterDAO = new DisasterDAO();
        victimDAO = new VictimDAO();
        shelterDAO = new ShelterDAO();
        resourceDAO = new ResourceDAO();

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);


        // ---- main content ----
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(28, 32, 28, 32));


        // ---- title section ----
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND);
        titlePanel.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel titleLabel = new JLabel("Disaster Management Dashboard");
        titleLabel.setFont(font(Font.BOLD, 26));
        titleLabel.setForeground(TEXT);

        JLabel subtitleLabel =
                new JLabel("Real-time overview of disaster response operations");
        subtitleLabel.setFont(font(Font.PLAIN, 14));
        subtitleLabel.setForeground(SECONDARY_TEXT);

        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setOpaque(false);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        titleTextPanel.add(titleLabel);
        titleTextPanel.add(Box.createVerticalStrut(6));
        titleTextPanel.add(subtitleLabel);


        // ---- refresh button ----
        RoundedButton refreshButton =
                new RoundedButton("Refresh Dashboard", BLUE, BLUE_H);
        refreshButton.setFont(font(Font.BOLD, 13));
        refreshButton.setBorder(new EmptyBorder(11, 22, 11, 22));
        refreshButton.addActionListener(e -> refreshDashboard());

        JPanel buttonWrap = new JPanel(new GridBagLayout());
        buttonWrap.setOpaque(false);
        buttonWrap.add(refreshButton);

        titlePanel.add(titleTextPanel, BorderLayout.WEST);
        titlePanel.add(buttonWrap, BorderLayout.EAST);

        mainPanel.add(titlePanel, BorderLayout.NORTH);


        // ---- center content ----
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(BACKGROUND);


        // ---- statistics cards ----
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBackground(BACKGROUND);

        disasterCountLabel = createCountLabel(RED);
        victimCountLabel = createCountLabel(ORANGE);
        shelterCountLabel = createCountLabel(GREEN);
        resourceCountLabel = createCountLabel(BLUE);

        cardsPanel.add(createStatCard(
                "DISASTERS", "Active and registered incidents",
                disasterCountLabel, RED));
        cardsPanel.add(createStatCard(
                "VICTIMS", "People registered for assistance",
                victimCountLabel, ORANGE));
        cardsPanel.add(createStatCard(
                "SHELTERS", "Emergency shelter centers",
                shelterCountLabel, GREEN));
        cardsPanel.add(createStatCard(
                "RESOURCES", "Available emergency resources",
                resourceCountLabel, BLUE));

        centerPanel.add(cardsPanel, BorderLayout.CENTER);


        // ---- system status ----
        centerPanel.add(createStatusPanel(), BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);


        // ---- load data ----
        refreshDashboard();
    }


    // ==========================================
    // CREATE COUNT LABEL
    // ==========================================

    private JLabel createCountLabel(Color accent) {
        JLabel label = new JLabel("0", SwingConstants.LEFT);
        label.setFont(font(Font.BOLD, 46));
        label.setForeground(accent);
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
        RoundedPanel card = new RoundedPanel(18, CARD_BACKGROUND, BORDER);
        card.setLayout(new BorderLayout());
        card.setContentPadding(22, 24, 20, 24);

        // top: accent bar + title
        JPanel topPanel = new JPanel(new BorderLayout(12, 0));
        topPanel.setOpaque(false);

        RoundedPanel accent = new RoundedPanel(3, accentColor, null);
        accent.setPreferredSize(new Dimension(5, 22));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(font(Font.BOLD, 14));
        titleLabel.setForeground(SECONDARY_TEXT);

        topPanel.add(accent, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        card.add(topPanel, BorderLayout.NORTH);

        // center: big count
        JPanel countPanel = new JPanel(new BorderLayout());
        countPanel.setOpaque(false);
        countPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        countLabel.setHorizontalAlignment(SwingConstants.LEFT);
        countPanel.add(countLabel, BorderLayout.CENTER);
        card.add(countPanel, BorderLayout.CENTER);

        // bottom: description
        JLabel descriptionLabel = new JLabel(description, SwingConstants.LEFT);
        descriptionLabel.setFont(font(Font.PLAIN, 13));
        descriptionLabel.setForeground(SECONDARY_TEXT);
        card.add(descriptionLabel, BorderLayout.SOUTH);

        return card;
    }


    // ==========================================
    // CREATE SYSTEM STATUS PANEL
    // ==========================================

    private JPanel createStatusPanel() {
        RoundedPanel panel = new RoundedPanel(18, CARD_BACKGROUND, BORDER);
        panel.setLayout(new BorderLayout());
        panel.setContentPadding(20, 24, 20, 24);

        JLabel titleLabel = new JLabel("SYSTEM STATUS");
        titleLabel.setFont(font(Font.BOLD, 14));
        titleLabel.setForeground(SECONDARY_TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 16, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel statusContainer = new JPanel(new GridLayout(1, 3, 16, 0));
        statusContainer.setOpaque(false);

        databaseStatusLabel = createStatusLabel("Database", false);
        evacuationStatusLabel = createStatusLabel("Evacuation Service", false);
        resourceStatusLabel = createStatusLabel("Resource Monitoring", false);

        statusContainer.add(wrapInPill(databaseStatusLabel));
        statusContainer.add(wrapInPill(evacuationStatusLabel));
        statusContainer.add(wrapInPill(resourceStatusLabel));

        panel.add(statusContainer, BorderLayout.CENTER);
        return panel;
    }

    private JPanel wrapInPill(JLabel label) {
        RoundedPanel pill = new RoundedPanel(12, PILL_BACKGROUND, null);
        pill.setLayout(new BorderLayout());
        pill.setContentPadding(12, 16, 12, 16);
        pill.add(label, BorderLayout.CENTER);
        return pill;
    }


    // ==========================================
    // CREATE STATUS LABEL
    // ==========================================

    private JLabel createStatusLabel(String name, boolean active) {
        JLabel label = new JLabel();
        label.setFont(font(Font.BOLD, 13));

        if (active) {
            label.setText("\u25CF  " + name + ": ACTIVE");
            label.setForeground(GREEN);
        } else {
            label.setText("\u25CF  " + name + ": CHECKING...");
            label.setForeground(SECONDARY_TEXT);
        }
        return label;
    }


    // ==========================================
    // REFRESH DASHBOARD
    // ==========================================

    private void refreshDashboard() {
        try {
            int disasterCount = disasterDAO.getAllDisasters().size();
            int victimCount = victimDAO.getAllVictims().size();
            int shelterCount = shelterDAO.getAllShelters().size();
            int resourceCount = resourceDAO.getAllResources().size();

            disasterCountLabel.setText(String.valueOf(disasterCount));
            victimCountLabel.setText(String.valueOf(victimCount));
            shelterCountLabel.setText(String.valueOf(shelterCount));
            resourceCountLabel.setText(String.valueOf(resourceCount));

            checkDatabase();
            checkEvacuationService();
            checkResourceMonitoring();

        } catch (Exception e) {
            e.printStackTrace();
            databaseStatusLabel.setText("\u25CF  Database: ERROR");
            databaseStatusLabel.setForeground(RED);
        }

        revalidate();
        repaint();
    }


    // ==========================================
    // CHECK DATABASE
    // ==========================================

    private void checkDatabase() {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                databaseStatusLabel.setText("\u25CF  Database: CONNECTED");
                databaseStatusLabel.setForeground(GREEN);
            } else {
                databaseStatusLabel.setText("\u25CF  Database: OFFLINE");
                databaseStatusLabel.setForeground(RED);
            }
        } catch (Exception e) {
            databaseStatusLabel.setText("\u25CF  Database: OFFLINE");
            databaseStatusLabel.setForeground(RED);
        }
    }


    // ==========================================
    // CHECK EVACUATION SERVICE
    // ==========================================

    private void checkEvacuationService() {
        try {
            new EvacuationServiceImpl();
            evacuationStatusLabel.setText("\u25CF  Evacuation Service: ACTIVE");
            evacuationStatusLabel.setForeground(GREEN);
        } catch (Exception e) {
            evacuationStatusLabel.setText("\u25CF  Evacuation Service: ERROR");
            evacuationStatusLabel.setForeground(RED);
        }
    }


    // ==========================================
    // CHECK RESOURCE MONITORING
    // ==========================================

    private void checkResourceMonitoring() {
        try {
            resourceDAO.getAllResources();
            resourceStatusLabel.setText("\u25CF  Resource Monitoring: ACTIVE");
            resourceStatusLabel.setForeground(GREEN);
        } catch (Exception e) {
            resourceStatusLabel.setText("\u25CF  Resource Monitoring: ERROR");
            resourceStatusLabel.setForeground(RED);
        }
    }


    // ==========================================
    // CUSTOM COMPONENT: ROUNDED PANEL + SOFT SHADOW
    // ==========================================

    private static class RoundedPanel extends JPanel {

        private static final int SHADOW_INSET = 10;

        private final int radius;
        private final Color fill;
        private final Color borderColor;

        RoundedPanel(int radius, Color fill, Color borderColor) {
            this.radius = radius;
            this.fill = fill;
            this.borderColor = borderColor;
            setOpaque(false);
        }

        /** Sets inner padding while reserving room for the drop shadow. */
        void setContentPadding(int top, int left, int bottom, int right) {
            setBorder(new EmptyBorder(
                    top + SHADOW_INSET,
                    left + SHADOW_INSET,
                    bottom + SHADOW_INSET,
                    right + SHADOW_INSET
            ));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int cardX = SHADOW_INSET;
            int cardY = SHADOW_INSET;
            int cardW = w - SHADOW_INSET * 2;
            int cardH = h - SHADOW_INSET * 2;

            // layered soft shadow (translucent layers stack into a smooth blur)
            for (int i = 6; i >= 1; i--) {
                g2.setColor(new Color(15, 23, 42, 7));
                g2.fill(new RoundRectangle2D.Float(
                        cardX - i, cardY - i + 3,
                        cardW + i * 2f, cardH + i * 2f,
                        radius + i, radius + i));
            }

            // card fill
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(
                    cardX, cardY, cardW, cardH, radius, radius));

            // hairline border
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(
                        cardX, cardY, cardW - 1f, cardH - 1f, radius, radius));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }


    // ==========================================
    // CUSTOM COMPONENT: ROUNDED BUTTON WITH HOVER
    // ==========================================

    private static class RoundedButton extends JButton {

        private final Color base;
        private final Color hover;
        private boolean hovered = false;

        RoundedButton(String text, Color base, Color hover) {
            super(text);
            this.base = base;
            this.hover = hover;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hovered ? hover : base);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}