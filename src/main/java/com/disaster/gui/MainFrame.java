package com.disaster.gui;

import com.disaster.dao.DisasterDAO;
import com.disaster.dao.ResourceDAO;
import com.disaster.dao.ShelterDAO;
import com.disaster.dao.VictimDAO;
import com.disaster.earthquake.EarthquakeData;
import com.disaster.model.User;
import com.disaster.service.SystemMonitor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    // ==========================================
    // COMPONENTS
    // ==========================================

    private JPanel contentPanel;

    private NavButton dashboardButton;
    private NavButton disasterButton;
    private NavButton victimButton;
    private NavButton shelterButton;
    private NavButton resourceButton;
    private NavButton evacuationButton;
    private NavButton reportsButton;

    // ==========================================
    // STATE & SERVICES
    // ==========================================

    private final User currentUser;
    private final SystemMonitor systemMonitor;

    private final DisasterDAO disasterDAO;
    private final VictimDAO victimDAO;
    private final ShelterDAO shelterDAO;
    private final ResourceDAO resourceDAO;

    private DashboardPanel dashboardPanel;

    private UserDashboardPanel userDashboardPanel;

    private final List<EarthquakeData> recentEarthquakeAlerts =
            new ArrayList<>();

    private Timer sidebarCountTimer;

    // ==========================================
    // PALETTE
    // ==========================================

    private static final Color BG_SIDEBAR =
            new Color(0x0A1118);

    private static final Color BG_CONTENT =
            new Color(0x0C1822);

    private static final Color NAV_ACTIVE =
            new Color(0x183046);

    private static final Color NAV_HOVER =
            new Color(0x122435);

    private static final Color TEXT_MAIN =
            new Color(0xE8F0F5);

    private static final Color TEXT_MUTED =
            new Color(0x8BA5B8);

    private static final Color BRAND_ORANGE =
            new Color(0xF26D4B);

    private static final Color BADGE_RED =
            new Color(0xFF5A5F);

    private static final Color BADGE_DARK =
            new Color(0x1A2835);

    private static Font font(
            int style,
            float size
    ) {

        return new Font(
                "Segoe UI",
                Font.PLAIN,
                12
        ).deriveFont(
                style,
                size
        );
    }

    private static void aa(Graphics2D g) {

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        g.setRenderingHint(
                RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE
        );
    }

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public MainFrame(User user) {

        this.currentUser = user;

        this.systemMonitor =
                new SystemMonitor();

        this.disasterDAO =
                new DisasterDAO();

        this.victimDAO =
                new VictimDAO();

        this.shelterDAO =
                new ShelterDAO();

        this.resourceDAO =
                new ResourceDAO();

        systemMonitor.setEarthquakeAlertListener(
                this::showEarthquakeAlert
        );

        setTitle(
                "Smart Disaster Evacuation System"
        );

        setSize(
                1440,
                900
        );

        setMinimumSize(
                new Dimension(
                        1024,
                        720
                )
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        setLayout(
                new BorderLayout()
        );

        buildSidebar();

        buildContent();

        applyRolePermissions();

        showDashboard();

        systemMonitor.startAll();

        updateSidebarCounts();
        startSidebarCountRefresh();

        SwingUtilities.invokeLater(() -> {

            systemMonitor.setEvacuationListener(
                    evacuation -> {

                        SwingUtilities.invokeLater(() -> {

                            if (dashboardPanel != null) {

                                dashboardPanel
                                        .updateEvacuationStatus(
                                                evacuation
                                        );
                            }
                        });
                    }
            );

            if (dashboardPanel != null) {

                EarthquakeData latestEarthquake =
                        systemMonitor.getLatestEarthquake();

                if (latestEarthquake != null) {

                    dashboardPanel
                            .updateLatestEarthquake(
                                    latestEarthquake
                            );
                }
            }
        });

        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(
                            WindowEvent e
                    ) {

                        stopSidebarCountRefresh();

                        if (userDashboardPanel != null) {
                            userDashboardPanel.stopRefresh();
                        }

                        systemMonitor.stopAll();

                        dispose();
                    }
                }
        );
    }

    // ==========================================
    // SIDEBAR BUILDER
    // ==========================================

    private void buildSidebar() {

        JPanel sidebar =
                new JPanel(
                        new BorderLayout()
                );

        sidebar.setBackground(
                BG_SIDEBAR
        );

        sidebar.setPreferredSize(
                new Dimension(
                        240,
                        0
                )
        );

        sidebar.setBorder(
                new EmptyBorder(
                        24,
                        16,
                        24,
                        16
                )
        );

        // ======================================
        // BRANDING
        // ======================================

        JPanel brand =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                12,
                                0
                        )
                );

        brand.setOpaque(false);

        JComponent logo =
                new JComponent() {

                    @Override
                    protected void paintComponent(
                            Graphics g0
                    ) {

                        Graphics2D g =
                                (Graphics2D) g0.create();

                        aa(g);

                        g.setColor(
                                BRAND_ORANGE
                        );

                        g.fillRoundRect(
                                0,
                                0,
                                32,
                                32,
                                10,
                                10
                        );

                        g.setColor(
                                Color.WHITE
                        );

                        g.setStroke(
                                new BasicStroke(
                                        1.8f,
                                        BasicStroke.CAP_ROUND,
                                        BasicStroke.JOIN_ROUND
                                )
                        );

                        Path2D.Float p =
                                new Path2D.Float();

                        p.moveTo(
                                6,
                                16
                        );

                        p.lineTo(
                                10,
                                16
                        );

                        p.lineTo(
                                14,
                                8
                        );

                        p.lineTo(
                                18,
                                24
                        );

                        p.lineTo(
                                22,
                                16
                        );

                        p.lineTo(
                                26,
                                16
                        );

                        g.draw(p);

                        g.dispose();
                    }
                };

        logo.setPreferredSize(
                new Dimension(
                        32,
                        32
                )
        );

        JPanel brandText =
                new JPanel();

        brandText.setLayout(
                new BoxLayout(
                        brandText,
                        BoxLayout.Y_AXIS
                )
        );

        brandText.setOpaque(false);

        JLabel l1 =
                new JLabel(
                        "Smart Disaster"
                );

        l1.setFont(
                font(
                        Font.BOLD,
                        13
                )
        );

        l1.setForeground(
                TEXT_MAIN
        );

        JLabel l2 =
                new JLabel(
                        "Evacuation System"
                );

        l2.setFont(
                font(
                        Font.BOLD,
                        13
                )
        );

        l2.setForeground(
                TEXT_MAIN
        );

        JLabel l3 =
                new JLabel(
                        "Control centre"
                );

        l3.setFont(
                font(
                        Font.PLAIN,
                        11
                )
        );

        l3.setForeground(
                TEXT_MUTED
        );

        brandText.add(l1);
        brandText.add(l2);
        brandText.add(l3);

        brand.add(logo);
        brand.add(brandText);

        sidebar.add(
                brand,
                BorderLayout.NORTH
        );

        // ======================================
        // NAVIGATION
        // ======================================

        JPanel nav =
                new JPanel();

        nav.setLayout(
                new BoxLayout(
                        nav,
                        BoxLayout.Y_AXIS
                )
        );

        nav.setOpaque(false);

        nav.setBorder(
                new EmptyBorder(
                        40,
                        0,
                        0,
                        0
                )
        );

        dashboardButton =
                new NavButton(
                        "Dashboard",
                        0,
                        null,
                        null
                );

        disasterButton =
                new NavButton(
                        "Disasters",
                        1,
                        "0",
                        BADGE_RED
                );

        /*
         * No hardcoded victim count anymore.
         */
        victimButton =
                new NavButton(
                        "Victims",
                        2,
                        "0",
                        BADGE_DARK
                );

        shelterButton =
                new NavButton(
                        "Shelters",
                        3,
                        "0",
                        BADGE_DARK
                );

        resourceButton =
                new NavButton(
                        "Resources",
                        4,
                        "0",
                        BADGE_DARK
                );

        evacuationButton =
                new NavButton(
                        "Evacuation",
                        5,
                        null,
                        null
                );

        reportsButton =
                new NavButton(
                        "Reports",
                        6,
                        null,
                        null
                );

        nav.add(
                dashboardButton
        );

        nav.add(
                Box.createVerticalStrut(4)
        );

        nav.add(
                disasterButton
        );

        nav.add(
                Box.createVerticalStrut(4)
        );

        nav.add(
                victimButton
        );

        nav.add(
                Box.createVerticalStrut(4)
        );

        nav.add(
                shelterButton
        );

        nav.add(
                Box.createVerticalStrut(4)
        );

        nav.add(
                resourceButton
        );

        nav.add(
                Box.createVerticalStrut(4)
        );

        nav.add(
                evacuationButton
        );

        nav.add(
                Box.createVerticalStrut(4)
        );

        nav.add(
                reportsButton
        );

        sidebar.add(
                nav,
                BorderLayout.CENTER
        );

        // ======================================
        // USER PROFILE FOOTER
        // ======================================

        JPanel footer =
                new JPanel(
                        new BorderLayout()
                );

        footer.setOpaque(false);

        JPanel userInfo =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );

        userInfo.setOpaque(false);

        String initials;

        if (
                currentUser != null
                        &&
                        currentUser.getName() != null
                        &&
                        currentUser.getName().length() > 0
        ) {

            initials =
                    currentUser
                            .getName()
                            .substring(
                                    0,
                                    1
                            )
                            .toUpperCase();

        } else {

            initials = "SA";
        }

        if (
                currentUser != null
                        &&
                        currentUser.getName() != null
                        &&
                        currentUser.getName().contains(" ")
        ) {

            String[] parts =
                    currentUser
                            .getName()
                            .split(" ");

            if (
                    parts.length >= 2
                            &&
                            !parts[0].isEmpty()
                            &&
                            !parts[1].isEmpty()
            ) {

                initials =
                        parts[0]
                                .substring(0, 1)
                                .toUpperCase()
                                +
                                parts[1]
                                        .substring(0, 1)
                                        .toUpperCase();
            }
        }

        final String avatarText =
                initials;

        JComponent avatar =
                new JComponent() {

                    @Override
                    protected void paintComponent(
                            Graphics g0
                    ) {

                        Graphics2D g =
                                (Graphics2D) g0.create();

                        aa(g);

                        g.setColor(
                                NAV_ACTIVE
                        );

                        g.fill(
                                new Ellipse2D.Float(
                                        0,
                                        0,
                                        36,
                                        36
                                )
                        );

                        g.setColor(
                                TEXT_MAIN
                        );

                        g.setFont(
                                font(
                                        Font.BOLD,
                                        13
                                )
                        );

                        FontMetrics fm =
                                g.getFontMetrics();

                        g.drawString(
                                avatarText,
                                (
                                        36
                                                -
                                                fm.stringWidth(
                                                        avatarText
                                                )
                                ) / 2,
                                (
                                        36
                                                +
                                                fm.getAscent()
                                                -
                                                fm.getDescent()
                                ) / 2
                        );

                        g.dispose();
                    }
                };

        avatar.setPreferredSize(
                new Dimension(
                        36,
                        36
                )
        );

        JPanel userText =
                new JPanel();

        userText.setLayout(
                new BoxLayout(
                        userText,
                        BoxLayout.Y_AXIS
                )
        );

        userText.setOpaque(false);

        JLabel uName =
                new JLabel(
                        currentUser != null
                                ? currentUser.getName()
                                : "System Administrator"
                );

        uName.setFont(
                font(
                        Font.BOLD,
                        12
                )
        );

        uName.setForeground(
                TEXT_MAIN
        );

        JLabel uRole =
                new JLabel(
                        "Role: "
                                +
                                (
                                        currentUser != null
                                                ? currentUser.getRole()
                                                : "Admin"
                                )
                );

        uRole.setFont(
                font(
                        Font.PLAIN,
                        11
                )
        );

        uRole.setForeground(
                TEXT_MUTED
        );

        userText.add(uName);
        userText.add(uRole);

        userInfo.add(avatar);
        userInfo.add(userText);

        footer.add(
                userInfo,
                BorderLayout.CENTER
        );

        JButton logoutBtn =
                new JButton(
                        "Log out"
                ) {

                    @Override
                    protected void paintComponent(
                            Graphics g0
                    ) {

                        Graphics2D g =
                                (Graphics2D) g0.create();

                        aa(g);

                        g.setColor(
                                new Color(
                                        0x111A24
                                )
                        );

                        g.fillRoundRect(
                                0,
                                0,
                                getWidth(),
                                getHeight(),
                                10,
                                10
                        );

                        g.dispose();

                        super.paintComponent(
                                g0
                        );
                    }
                };

        logoutBtn.setFont(
                font(
                        Font.PLAIN,
                        11
                )
        );

        logoutBtn.setForeground(
                TEXT_MUTED
        );

        logoutBtn.setContentAreaFilled(
                false
        );

        logoutBtn.setBorderPainted(
                false
        );

        logoutBtn.setFocusPainted(
                false
        );

        logoutBtn.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        logoutBtn.setPreferredSize(
                new Dimension(
                        70,
                        26
                )
        );

        logoutBtn.addActionListener(
                e -> confirmLogout()
        );

        JPanel logoutWrap =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                46,
                                12
                        )
                );

        logoutWrap.setOpaque(false);

        logoutWrap.add(
                logoutBtn
        );

        footer.add(
                logoutWrap,
                BorderLayout.SOUTH
        );

        sidebar.add(
                footer,
                BorderLayout.SOUTH
        );

        add(
                sidebar,
                BorderLayout.WEST
        );

        // ======================================
        // NAVIGATION ACTIONS
        // ======================================

        dashboardButton.addActionListener(
                e -> showDashboard()
        );

        disasterButton.addActionListener(
                e -> showDisasters()
        );

        victimButton.addActionListener(
                e -> showVictims()
        );

        shelterButton.addActionListener(
                e -> showShelters()
        );

        resourceButton.addActionListener(
                e -> showResources()
        );

        evacuationButton.addActionListener(
                e -> showEvacuations()
        );

        reportsButton.addActionListener(
                e -> showReports()
        );
    }

    // ==========================================
    // SIDEBAR DATABASE COUNTS
    // ==========================================

    private void updateSidebarCounts() {

        if (
                disasterButton == null
                        ||
                        victimButton == null
                        ||
                        shelterButton == null
                        ||
                        resourceButton == null
        ) {
            return;
        }

        try {

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

            disasterButton.setBadgeText(
                    String.valueOf(disasterCount)
            );

            victimButton.setBadgeText(
                    String.valueOf(victimCount)
            );

            shelterButton.setBadgeText(
                    String.valueOf(shelterCount)
            );

            resourceButton.setBadgeText(
                    String.valueOf(resourceCount)
            );

            disasterButton.repaint();
            victimButton.repaint();
            shelterButton.repaint();
            resourceButton.repaint();

        } catch (Exception e) {

            System.err.println(
                    "[MainFrame] Unable to update sidebar counts: "
                            + e.getMessage()
            );
        }
    }

    private void startSidebarCountRefresh() {

        if (sidebarCountTimer != null) {
            sidebarCountTimer.stop();
        }

        sidebarCountTimer =
                new Timer(
                        2000,
                        e -> updateSidebarCounts()
                );

        sidebarCountTimer.setRepeats(true);
        sidebarCountTimer.start();
    }

    private void stopSidebarCountRefresh() {

        if (sidebarCountTimer != null) {

            sidebarCountTimer.stop();

            sidebarCountTimer = null;
        }
    }

    // ==========================================
    // CONTENT WRAPPER
    // ==========================================

    private void buildContent() {

        contentPanel =
                new JPanel(
                        new BorderLayout()
                );

        contentPanel.setBackground(
                BG_CONTENT
        );

        add(
                contentPanel,
                BorderLayout.CENTER
        );
    }

    // ==========================================
    // CUSTOM NAVIGATION BUTTON
    // ==========================================

    private class NavButton
            extends JButton {

        private final int iconType;

        private String badgeText;

        private final Color badgeColor;

        private boolean hover;

        private boolean active;

        NavButton(
                String text,
                int iconType,
                String badgeText,
                Color badgeColor
        ) {

            super(text);

            this.iconType =
                    iconType;

            this.badgeText =
                    badgeText;

            this.badgeColor =
                    badgeColor;

            setFont(
                    font(
                            Font.PLAIN,
                            13
                    )
            );

            setForeground(
                    TEXT_MUTED
            );

            setHorizontalAlignment(
                    SwingConstants.LEFT
            );

            setBorder(
                    new EmptyBorder(
                            0,
                            42,
                            0,
                            12
                    )
            );

            setContentAreaFilled(
                    false
            );

            setBorderPainted(
                    false
            );

            setFocusPainted(
                    false
            );

            setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.HAND_CURSOR
                    )
            );

            setAlignmentX(
                    LEFT_ALIGNMENT
            );

            setMaximumSize(
                    new Dimension(
                            Integer.MAX_VALUE,
                            40
                    )
            );

            setPreferredSize(
                    new Dimension(
                            200,
                            40
                    )
            );

            addMouseListener(
                    new MouseAdapter() {

                        @Override
                        public void mouseEntered(
                                MouseEvent e
                        ) {

                            hover = true;

                            repaint();
                        }

                        @Override
                        public void mouseExited(
                                MouseEvent e
                        ) {

                            hover = false;

                            repaint();
                        }
                    }
            );
        }

        void setBadgeText(
                String badgeText
        ) {

            this.badgeText =
                    badgeText;

            repaint();
        }

        String getBadgeText() {

            return badgeText;
        }

        void setActive(
                boolean active
        ) {

            this.active =
                    active;

            setFont(
                    font(
                            active
                                    ? Font.BOLD
                                    : Font.PLAIN,
                            13
                    )
            );

            setForeground(
                    active
                            ? Color.WHITE
                            : TEXT_MUTED
            );

            repaint();
        }

        @Override
        protected void paintComponent(
                Graphics g0
        ) {

            Graphics2D g =
                    (Graphics2D) g0.create();

            aa(g);

            // ==================================
            // BACKGROUND
            // ==================================

            if (active) {

                g.setColor(
                        NAV_ACTIVE
                );

                g.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        12,
                        12
                );

            } else if (hover) {

                g.setColor(
                        NAV_HOVER
                );

                g.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        12,
                        12
                );
            }

            // ==================================
            // ICON
            // ==================================

            g.setColor(
                    active
                            ? Color.WHITE
                            : TEXT_MUTED
            );

            g.setStroke(
                    new BasicStroke(
                            1.5f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            int ix = 14;

            int iy =
                    getHeight() / 2;

            Path2D.Float p =
                    new Path2D.Float();

            switch (iconType) {

                case 0:

                    g.drawRoundRect(
                            ix,
                            iy - 6,
                            5,
                            5,
                            2,
                            2
                    );

                    g.drawRoundRect(
                            ix + 7,
                            iy - 6,
                            5,
                            5,
                            2,
                            2
                    );

                    g.drawRoundRect(
                            ix,
                            iy + 1,
                            5,
                            5,
                            2,
                            2
                    );

                    g.drawRoundRect(
                            ix + 7,
                            iy + 1,
                            5,
                            5,
                            2,
                            2
                    );

                    break;

                case 1:

                    p.moveTo(
                            ix + 6,
                            iy - 6
                    );

                    p.lineTo(
                            ix + 12,
                            iy + 5
                    );

                    p.lineTo(
                            ix,
                            iy + 5
                    );

                    p.closePath();

                    g.draw(p);

                    g.drawLine(
                            ix + 6,
                            iy - 2,
                            ix + 6,
                            iy + 1
                    );

                    g.drawLine(
                            ix + 6,
                            iy + 3,
                            ix + 6,
                            iy + 3
                    );

                    break;

                case 2:

                    g.drawOval(
                            ix + 3,
                            iy - 6,
                            5,
                            5
                    );

                    p.moveTo(
                            ix,
                            iy + 6
                    );

                    p.quadTo(
                            ix + 5,
                            iy + 1,
                            ix + 11,
                            iy + 6
                    );

                    g.draw(p);

                    break;

                case 3:

                    p.moveTo(
                            ix,
                            iy
                    );

                    p.lineTo(
                            ix + 6,
                            iy - 6
                    );

                    p.lineTo(
                            ix + 12,
                            iy
                    );

                    p.moveTo(
                            ix + 2,
                            iy - 2
                    );

                    p.lineTo(
                            ix + 2,
                            iy + 6
                    );

                    p.lineTo(
                            ix + 10,
                            iy + 6
                    );

                    p.lineTo(
                            ix + 10,
                            iy - 2
                    );

                    g.draw(p);

                    break;

                case 4:

                    p.moveTo(
                            ix + 6,
                            iy - 6
                    );

                    p.lineTo(
                            ix + 12,
                            iy - 3
                    );

                    p.lineTo(
                            ix + 12,
                            iy + 4
                    );

                    p.lineTo(
                            ix + 6,
                            iy + 7
                    );

                    p.lineTo(
                            ix,
                            iy + 4
                    );

                    p.lineTo(
                            ix,
                            iy - 3
                    );

                    p.closePath();

                    p.moveTo(
                            ix,
                            iy - 3
                    );

                    p.lineTo(
                            ix + 6,
                            iy
                    );

                    p.lineTo(
                            ix + 12,
                            iy - 3
                    );

                    p.moveTo(
                            ix + 6,
                            iy
                    );

                    p.lineTo(
                            ix + 6,
                            iy + 7
                    );

                    g.draw(p);

                    break;

                case 5:

                    g.drawLine(
                            ix,
                            iy,
                            ix + 12,
                            iy
                    );

                    p.moveTo(
                            ix + 8,
                            iy - 4
                    );

                    p.lineTo(
                            ix + 12,
                            iy
                    );

                    p.lineTo(
                            ix + 8,
                            iy + 4
                    );

                    g.draw(p);

                    break;

                case 6:

                    p.moveTo(
                            ix + 2,
                            iy - 6
                    );

                    p.lineTo(
                            ix + 7,
                            iy - 6
                    );

                    p.lineTo(
                            ix + 10,
                            iy - 3
                    );

                    p.lineTo(
                            ix + 10,
                            iy + 6
                    );

                    p.lineTo(
                            ix + 2,
                            iy + 6
                    );

                    p.closePath();

                    p.moveTo(
                            ix + 7,
                            iy - 6
                    );

                    p.lineTo(
                            ix + 7,
                            iy - 3
                    );

                    p.lineTo(
                            ix + 10,
                            iy - 3
                    );

                    g.draw(p);

                    g.drawLine(
                            ix + 4,
                            iy + 1,
                            ix + 8,
                            iy + 1
                    );

                    g.drawLine(
                            ix + 4,
                            iy + 3,
                            ix + 8,
                            iy + 3
                    );

                    break;
            }

            // ==================================
            // BADGE
            // ==================================

            if (
                    badgeText != null
                            &&
                            badgeColor != null
            ) {

                g.setFont(
                        font(
                                Font.BOLD,
                                10
                        )
                );

                FontMetrics fm =
                        g.getFontMetrics();

                int bw =
                        Math.max(
                                16,
                                fm.stringWidth(
                                        badgeText
                                ) + 8
                        );

                int bx =
                        getWidth()
                                - bw
                                - 12;

                int by =
                        (
                                getHeight()
                                        - 16
                        ) / 2;

                g.setColor(
                        badgeColor
                );

                g.fillRoundRect(
                        bx,
                        by,
                        bw,
                        16,
                        16,
                        16
                );

                g.setColor(
                        Color.WHITE
                );

                g.drawString(
                        badgeText,
                        bx
                                +
                                (
                                        bw
                                                -
                                                fm.stringWidth(
                                                        badgeText
                                                )
                                ) / 2,
                        by + 12
                );
            }

            g.dispose();

            super.paintComponent(
                    g0
            );
        }
    }

    // ==========================================
    // ACTIVE MENU
    // ==========================================

    private void setActiveMenu(
            NavButton activeBtn
    ) {

        NavButton[] btns = {

                dashboardButton,
                disasterButton,
                victimButton,
                shelterButton,
                resourceButton,
                evacuationButton,
                reportsButton
        };

        for (NavButton b : btns) {

            b.setActive(
                    b == activeBtn
            );
        }
    }

    // ==========================================
    // ROLE PERMISSIONS
    // ==========================================

    private void applyRolePermissions() {

        if (
                currentUser == null
                        ||
                        currentUser.getRole() == null
        ) {

            return;
        }

        String role =
                currentUser
                        .getRole()
                        .trim()
                        .toUpperCase();

        if (role.equals("USER")) {

            disasterButton.setVisible(false);
            victimButton.setVisible(false);
            shelterButton.setVisible(false);
            resourceButton.setVisible(false);
            evacuationButton.setVisible(false);
            reportsButton.setVisible(false);

            dashboardButton.setVisible(true);

            return;
        }

        if (
                role.equals(
                        "MEDICAL_TEAM"
                )
        ) {

            disasterButton.setVisible(
                    false
            );

            shelterButton.setVisible(
                    false
            );

            resourceButton.setVisible(
                    false
            );

            reportsButton.setVisible(
                    false
            );
        }
    }

    // ==========================================
    // VIEW SWITCHERS
    // ==========================================

    private void showDashboard() {
        updateSidebarCounts();

        setActiveMenu(
                dashboardButton
        );

        contentPanel.removeAll();

        String role =
                currentUser != null
                        ? currentUser.getRole()
                        : null;

        if (role != null && role.trim().equalsIgnoreCase("USER")) {

            userDashboardPanel =
                    new UserDashboardPanel(
                            currentUser
                    );

            contentPanel.add(
                    userDashboardPanel,
                    BorderLayout.CENTER
            );

        } else {

            dashboardPanel =
                    new DashboardPanel(
                            systemMonitor
                    );

            contentPanel.add(
                    dashboardPanel,
                    BorderLayout.CENTER
            );

            EarthquakeData latestEarthquake =
                    systemMonitor.getLatestEarthquake();

            if (
                    latestEarthquake != null
            ) {

                dashboardPanel
                        .updateLatestEarthquake(
                                latestEarthquake
                        );
            }
        }

        refreshContent();
    }

    private void showDisasters() {
        updateSidebarCounts();


        setActiveMenu(
                disasterButton
        );

        contentPanel.removeAll();

        contentPanel.add(
                new DisasterPanel(),
                BorderLayout.CENTER
        );

        refreshContent();
    }

    private void showVictims() {
        updateSidebarCounts();


        setActiveMenu(
                victimButton
        );

        contentPanel.removeAll();

        contentPanel.add(
                new VictimPanel(),
                BorderLayout.CENTER
        );

        refreshContent();
    }

    private void showShelters() {
        updateSidebarCounts();


        setActiveMenu(
                shelterButton
        );

        contentPanel.removeAll();

        contentPanel.add(
                new ShelterPanel(),
                BorderLayout.CENTER
        );

        refreshContent();
    }

    private void showResources() {
        updateSidebarCounts();


        setActiveMenu(
                resourceButton
        );

        contentPanel.removeAll();

        contentPanel.add(
                new ResourcePanel(),
                BorderLayout.CENTER
        );

        refreshContent();
    }

    private void showEvacuations() {
        updateSidebarCounts();


        setActiveMenu(
                evacuationButton
        );

        contentPanel.removeAll();

        contentPanel.add(
                new EvacuationPanel(),
                BorderLayout.CENTER
        );

        refreshContent();
    }

    private void showReports() {
        updateSidebarCounts();


        setActiveMenu(
                reportsButton
        );

        contentPanel.removeAll();

        contentPanel.add(
                new ReportsPanel(),
                BorderLayout.CENTER
        );

        refreshContent();
    }

    // ==========================================
    // REFRESH CONTENT
    // ==========================================

    private void refreshContent() {

        contentPanel.revalidate();

        contentPanel.repaint();
    }

    // ==========================================
    // LOGOUT
    // ==========================================

    private void confirmLogout() {

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to logout?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (
                result
                        ==
                        JOptionPane.YES_OPTION
        ) {

            stopSidebarCountRefresh();

            if (userDashboardPanel != null) {
                userDashboardPanel.stopRefresh();
            }

            systemMonitor.stopAll();

            dispose();

            SwingUtilities.invokeLater(
                    () -> {

                        LoginFrame loginFrame =
                                new LoginFrame();

                        loginFrame.setVisible(
                                true
                        );
                    }
            );
        }
    }

    // ==========================================
    // EARTHQUAKE GUI ALERT
    // ==========================================

    private void showEarthquakeAlert(
            EarthquakeData earthquake
    ) {

        if (
                earthquake == null
        ) {

            return;
        }

        SwingUtilities.invokeLater(
                () -> {

                    if (
                            dashboardPanel != null
                    ) {

                        dashboardPanel
                                .updateLatestEarthquake(
                                        earthquake
                                );
                    }

                    if (userDashboardPanel != null) {

                        userDashboardPanel
                                .updateEarthquake(
                                        earthquake
                                );
                    }

                    recentEarthquakeAlerts.add(
                            earthquake
                    );

                    if (
                            recentEarthquakeAlerts.size()
                                    > 20
                    ) {

                        recentEarthquakeAlerts.remove(
                                0
                        );
                    }

                    /*
                     * Victim records may have been
                     * created by the earthquake
                     * test workflow.
                     */
                    System.out.println();

                    System.out.println(
                            "[GUI] Earthquake alert received: "
                                    + earthquake.getPlace()
                    );

                    System.out.println(
                            "[GUI] Magnitude: "
                                    + earthquake.getMagnitude()
                    );

                    System.out.println();
                }
        );
    }

    // ==========================================
    // MAIN
    // ==========================================

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                () -> {

                    try {

                        UIManager.setLookAndFeel(
                                UIManager
                                        .getCrossPlatformLookAndFeelClassName()
                        );

                    } catch (Exception ignored) {
                    }

                    User testUser =
                            new User();

                    testUser.setName(
                            "System Administrator"
                    );

                    testUser.setUsername(
                            "admin"
                    );

                    testUser.setRole(
                            "Admin"
                    );

                    MainFrame frame =
                            new MainFrame(
                                    testUser
                            );

                    frame.setVisible(
                            true
                    );
                }
        );
    }
}