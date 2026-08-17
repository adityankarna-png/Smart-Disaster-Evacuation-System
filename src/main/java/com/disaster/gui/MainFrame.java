package com.disaster.gui;

import com.disaster.model.User;
import com.disaster.service.SystemMonitor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    // ==========================================
    // COMPONENTS
    // ==========================================

    private JPanel contentPanel;
    private JPanel navigationPanel;

    private JLabel pageTitleLabel;
    private JLabel pageSubtitleLabel;

    private JLabel userNameLabel;
    private JLabel userRoleLabel;

    private JButton dashboardButton;
    private JButton disasterButton;
    private JButton victimButton;
    private JButton shelterButton;
    private JButton resourceButton;
    private JButton evacuationButton;
    private JButton reportsButton;
    private JButton logoutButton;

    // ==========================================
    // CURRENT USER
    // ==========================================

    private final User currentUser;
    private final SystemMonitor systemMonitor;

    // ==========================================
    // COLORS
    // ==========================================

    private static final Color HEADER_COLOR =
            new Color(25, 55, 90);

    private static final Color HEADER_DARK =
            new Color(20, 45, 75);

    private static final Color SIDEBAR_COLOR =
            new Color(35, 50, 65);

    private static final Color BUTTON_COLOR =
            new Color(42, 62, 82);

    private static final Color BUTTON_HOVER =
            new Color(55, 85, 110);

    private static final Color BUTTON_ACTIVE =
            new Color(55, 105, 155);

    private static final Color BACKGROUND =
            new Color(245, 247, 250);

    private static final Color TEXT =
            new Color(35, 45, 55);

    private static final Color SUBTEXT =
            new Color(105, 115, 125);

    private static final Color BORDER =
            new Color(220, 225, 230);

    private static final Color SUCCESS =
            new Color(40, 150, 90);

    private static final Color DANGER =
            new Color(205, 65, 65);


    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    public MainFrame(User user) {

        this.currentUser = user;

        this.systemMonitor =
                new SystemMonitor();

        setTitle(
                "Smart Disaster Evacuation System"
        );

        setSize(1250, 750);

        setMinimumSize(
                new Dimension(1100, 650)
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        setLayout(
                new BorderLayout()
        );

        getContentPane().setBackground(
                BACKGROUND
        );

        buildHeader();

        buildSidebar();

        buildContent();

        applyRolePermissions();

        showDashboard();

        systemMonitor.startAll();

        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(
                            WindowEvent e
                    ) {

                        systemMonitor.stopAll();

                        dispose();
                    }
                }
        );
    }


    // ==========================================
    // HEADER
    // ==========================================

    private void buildHeader() {

        JPanel headerPanel =
                new JPanel(
                        new BorderLayout()
                );

        headerPanel.setBackground(
                HEADER_COLOR
        );

        headerPanel.setPreferredSize(
                new Dimension(1250, 92)
        );

        headerPanel.setBorder(
                new EmptyBorder(
                        12,
                        28,
                        12,
                        28
                )
        );


        // ==========================================
        // BRAND
        // ==========================================

        JPanel brandPanel =
                new JPanel();

        brandPanel.setOpaque(false);

        brandPanel.setLayout(
                new BoxLayout(
                        brandPanel,
                        BoxLayout.Y_AXIS
                )
        );


        JLabel titleLabel =
                new JLabel(
                        "SMART DISASTER EVACUATION SYSTEM"
                );

        titleLabel.setForeground(
                Color.WHITE
        );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        25
                )
        );


        JLabel subtitleLabel =
                new JLabel(
                        "Emergency response and evacuation management"
                );

        subtitleLabel.setForeground(
                new Color(
                        205,
                        220,
                        235
                )
        );

        subtitleLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );


        brandPanel.add(titleLabel);

        brandPanel.add(
                Box.createVerticalStrut(5)
        );

        brandPanel.add(subtitleLabel);


        headerPanel.add(
                brandPanel,
                BorderLayout.WEST
        );


        // ==========================================
        // USER INFORMATION
        // ==========================================

        JPanel userPanel =
                new JPanel();

        userPanel.setOpaque(false);

        userPanel.setLayout(
                new BoxLayout(
                        userPanel,
                        BoxLayout.Y_AXIS
                )
        );


        userNameLabel =
                new JLabel(
                        "Welcome"
                );

        userNameLabel.setForeground(
                Color.WHITE
        );

        userNameLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        userNameLabel.setAlignmentX(
                Component.RIGHT_ALIGNMENT
        );


        userRoleLabel =
                new JLabel(
                        "Role"
                );

        userRoleLabel.setForeground(
                new Color(
                        180,
                        205,
                        225
                )
        );

        userRoleLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        11
                )
        );

        userRoleLabel.setAlignmentX(
                Component.RIGHT_ALIGNMENT
        );


        userPanel.add(
                userNameLabel
        );

        userPanel.add(
                Box.createVerticalStrut(4)
        );

        userPanel.add(
                userRoleLabel
        );


        if (currentUser != null) {

            userNameLabel.setText(
                    "Welcome, " +
                            currentUser.getName()
            );

            userRoleLabel.setText(
                    "ROLE: " +
                            currentUser.getRole()
            );
        }


        headerPanel.add(
                userPanel,
                BorderLayout.EAST
        );


        add(
                headerPanel,
                BorderLayout.NORTH
        );
    }


    // ==========================================
    // SIDEBAR
    // ==========================================

    private void buildSidebar() {

        JPanel sidebarPanel =
                new JPanel(
                        new BorderLayout()
                );

        sidebarPanel.setBackground(
                SIDEBAR_COLOR
        );

        sidebarPanel.setPreferredSize(
                new Dimension(225, 650)
        );


        // ==========================================
        // SIDEBAR TOP
        // ==========================================

        JPanel sideTop =
                new JPanel();

        sideTop.setBackground(
                HEADER_DARK
        );

        sideTop.setLayout(
                new BoxLayout(
                        sideTop,
                        BoxLayout.Y_AXIS
                )
        );

        sideTop.setBorder(
                new EmptyBorder(
                        20,
                        22,
                        18,
                        22
                )
        );


        JLabel menuLabel =
                new JLabel(
                        "CONTROL CENTER"
                );

        menuLabel.setForeground(
                new Color(
                        180,
                        200,
                        220
                )
        );

        menuLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        11
                )
        );


        JLabel menuDescription =
                new JLabel(
                        "Manage response operations"
                );

        menuDescription.setForeground(
                new Color(
                        230,
                        235,
                        240
                )
        );

        menuDescription.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );


        sideTop.add(menuLabel);

        sideTop.add(
                Box.createVerticalStrut(6)
        );

        sideTop.add(menuDescription);


        sidebarPanel.add(
                sideTop,
                BorderLayout.NORTH
        );


        // ==========================================
        // NAVIGATION
        // ==========================================

        navigationPanel =
                new JPanel(
                        new GridLayout(
                                0,
                                1,
                                0,
                                5
                        )
                );

        navigationPanel.setBackground(
                SIDEBAR_COLOR
        );

        navigationPanel.setBorder(
                new EmptyBorder(
                        10,
                        8,
                        10,
                        8
                )
        );



        dashboardButton =
                createMenuButton(
                        "Dashboard"
                );

        disasterButton =
                createMenuButton(
                        "Disasters"
                );

        victimButton =
                createMenuButton(
                        "Victims"
                );

        shelterButton =
                createMenuButton(
                        "Shelters"
                );

        resourceButton =
                createMenuButton(
                        "Resources"
                );

        evacuationButton =
                createMenuButton(
                        "Evacuation"
                );

        reportsButton =
                createMenuButton(
                        "Reports"
                );

        logoutButton =
                createMenuButton(
                        "Logout"
                );


        navigationPanel.add(
                dashboardButton
        );

        navigationPanel.add(
                disasterButton
        );

        navigationPanel.add(
                victimButton
        );

        navigationPanel.add(
                shelterButton
        );

        navigationPanel.add(
                resourceButton
        );

        navigationPanel.add(
                evacuationButton
        );

        navigationPanel.add(
                reportsButton
        );

        navigationPanel.add(
                logoutButton
        );


        sidebarPanel.add(
                navigationPanel,
                BorderLayout.CENTER
        );


        // ==========================================
        // SIDEBAR FOOTER
        // ==========================================

        JPanel footer =
                new JPanel(
                        new BorderLayout()
                );

        footer.setBackground(
                new Color(
                        30,
                        43,
                        55
                )
        );

        footer.setBorder(
                new EmptyBorder(
                        10,
                        16,
                        10,
                        16
                )
        );


        JLabel footerText =
                new JLabel(
                        "DISASTER RESPONSE SYSTEM"
                );

        footerText.setForeground(
                new Color(
                        150,
                        170,
                        190
                )
        );

        footerText.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        9
                )
        );


        footer.add(
                footerText,
                BorderLayout.CENTER
        );


        sidebarPanel.add(
                footer,
                BorderLayout.SOUTH
        );


        add(
                sidebarPanel,
                BorderLayout.WEST
        );


        // ==========================================
        // ACTIONS
        // ==========================================

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

        logoutButton.addActionListener(
                e -> confirmLogout()
        );
    }


    // ==========================================
    // ROLE PERMISSIONS
    // ==========================================

    private void applyRolePermissions() {

        if (currentUser == null) {
            return;
        }

        String role = currentUser.getRole();

        if (role == null) {
            return;
        }

        role = role.trim().toUpperCase();

        // ==========================================
        // ADMIN
        // ==========================================

        if (role.equals("ADMIN")) {

            enableAllModules();

            return;
        }

        // ==========================================
        // RESCUE TEAM
        // ==========================================

        if (role.equals("RESCUE_TEAM")) {

            enableAllModules();

            return;
        }

        // ==========================================
        // MEDICAL TEAM
        // ==========================================

        if (role.equals("MEDICAL_TEAM")) {

            navigationPanel.remove(
                    disasterButton
            );

            navigationPanel.remove(
                    shelterButton
            );

            navigationPanel.remove(
                    resourceButton
            );

            navigationPanel.remove(
                    reportsButton
            );

            dashboardButton.setEnabled(true);

            victimButton.setEnabled(true);

            evacuationButton.setEnabled(true);

            logoutButton.setEnabled(true);

            navigationPanel.revalidate();
            navigationPanel.repaint();

            return;
        }

        // ==========================================
        // UNKNOWN ROLE
        // ==========================================

        navigationPanel.remove(
                disasterButton
        );

        navigationPanel.remove(
                victimButton
        );

        navigationPanel.remove(
                shelterButton
        );

        navigationPanel.remove(
                resourceButton
        );

        navigationPanel.remove(
                evacuationButton
        );

        dashboardButton.setEnabled(true);
        logoutButton.setEnabled(true);

        navigationPanel.revalidate();
        navigationPanel.repaint();
    }


    // ==========================================
    // ENABLE ALL
    // ==========================================

    private void enableAllModules() {

        dashboardButton.setEnabled(true);

        disasterButton.setEnabled(true);

        victimButton.setEnabled(true);

        shelterButton.setEnabled(true);

        resourceButton.setEnabled(true);

        evacuationButton.setEnabled(true);

        reportsButton.setEnabled(true);

        logoutButton.setEnabled(true);
    }


    // ==========================================
    // CONTENT
    // ==========================================

    private void buildContent() {

        JPanel wrapper =
                new JPanel(
                        new BorderLayout()
                );

        wrapper.setBackground(
                BACKGROUND
        );


        // ==========================================
        // PAGE HEADER
        // ==========================================

        JPanel pageHeader =
                new JPanel(
                        new BorderLayout()
                );

        pageHeader.setBackground(
                Color.WHITE
        );

        pageHeader.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0,
                                0,
                                1,
                                0,
                                BORDER
                        ),
                        new EmptyBorder(
                                12,
                                24,
                                12,
                                24
                        )
                )
        );


        pageTitleLabel =
                new JLabel(
                        "Dashboard"
                );

        pageTitleLabel.setForeground(
                TEXT
        );

        pageTitleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );


        pageSubtitleLabel =
                new JLabel(
                        "Overview of emergency response operations"
                );

        pageSubtitleLabel.setForeground(
                SUBTEXT
        );

        pageSubtitleLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        11
                )
        );


        JPanel pageInfo =
                new JPanel();

        pageInfo.setOpaque(false);

        pageInfo.setLayout(
                new BoxLayout(
                        pageInfo,
                        BoxLayout.Y_AXIS
                )
        );


        pageInfo.add(
                pageTitleLabel
        );

        pageInfo.add(
                Box.createVerticalStrut(2)
        );

        pageInfo.add(
                pageSubtitleLabel
        );


        pageHeader.add(
                pageInfo,
                BorderLayout.WEST
        );


        wrapper.add(
                pageHeader,
                BorderLayout.NORTH
        );


        contentPanel =
                new JPanel(
                        new BorderLayout()
                );

        contentPanel.setBackground(
                BACKGROUND
        );


        wrapper.add(
                contentPanel,
                BorderLayout.CENTER
        );


        add(
                wrapper,
                BorderLayout.CENTER
        );
    }


    // ==========================================
    // CREATE MENU BUTTON
    // ==========================================

    private JButton createMenuButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setForeground(
                new Color(
                        235,
                        240,
                        245
                )
        );

        button.setBackground(
                BUTTON_COLOR
        );

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setOpaque(true);

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setBorder(
                new EmptyBorder(
                        0,
                        22,
                        0,
                        10
                )
        );


        button.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e
                    ) {

                        if (button.isEnabled()
                                &&
                                !button.getBackground()
                                        .equals(
                                                BUTTON_ACTIVE
                                        )) {

                            button.setBackground(
                                    BUTTON_HOVER
                            );
                        }
                    }


                    @Override
                    public void mouseExited(
                            MouseEvent e
                    ) {

                        if (button.isEnabled()
                                &&
                                !button.getBackground()
                                        .equals(
                                                BUTTON_ACTIVE
                                        )) {

                            button.setBackground(
                                    BUTTON_COLOR
                            );
                        }
                    }
                }
        );


        return button;
    }


    // ==========================================
    // ACTIVE MENU
    // ==========================================

    private void setActiveButton(
            JButton activeButton
    ) {

        JButton[] buttons = {
                dashboardButton,
                disasterButton,
                victimButton,
                shelterButton,
                resourceButton,
                evacuationButton,
                reportsButton
        };


        for (JButton button : buttons) {

            button.setBackground(
                    BUTTON_COLOR
            );

            button.setForeground(
                    new Color(
                            235,
                            240,
                            245
                    )
            );
        }


        activeButton.setBackground(
                BUTTON_ACTIVE
        );

        activeButton.setForeground(
                Color.WHITE
        );
    }


    // ==========================================
    // PAGE HEADER
    // ==========================================

    private void updatePageHeader(
            String title,
            String subtitle
    ) {

        pageTitleLabel.setText(
                title
        );

        pageSubtitleLabel.setText(
                subtitle
        );
    }


    // ==========================================
    // DASHBOARD
    // ==========================================

    private void showDashboard() {

        setActiveButton(
                dashboardButton
        );

        updatePageHeader(
                "Dashboard",
                "Overview of emergency response operations"
        );

        contentPanel.removeAll();

        contentPanel.add(
                new DashboardPanel(),
                BorderLayout.CENTER
        );

        refreshContent();
    }


    // ==========================================
    // DISASTERS
    // ==========================================

    private void showDisasters() {

        setActiveButton(
                disasterButton
        );

        updatePageHeader(
                "Disaster Management",
                "Monitor and manage active disaster incidents"
        );

        contentPanel.removeAll();

        contentPanel.add(
                new DisasterPanel(),
                BorderLayout.CENTER
        );

        refreshContent();
    }


    // ==========================================
    // VICTIMS
    // ==========================================

    private void showVictims() {

        setActiveButton(
                victimButton
        );

        updatePageHeader(
                "Victim Management",
                "Manage registered victims and evacuation priorities"
        );

        contentPanel.removeAll();

        contentPanel.add(
                new VictimPanel(),
                BorderLayout.CENTER
        );

        refreshContent();
    }


    // ==========================================
    // SHELTERS
    // ==========================================

    private void showShelters() {

        setActiveButton(
                shelterButton
        );

        updatePageHeader(
                "Shelter Management",
                "Monitor shelter capacity and occupancy"
        );

        contentPanel.removeAll();

        contentPanel.add(
                new ShelterPanel(),
                BorderLayout.CENTER
        );

        refreshContent();
    }


    // ==========================================
    // RESOURCES
    // ==========================================

    private void showResources() {

        setActiveButton(
                resourceButton
        );

        updatePageHeader(
                "Resource Management",
                "Track emergency supplies and availability"
        );

        contentPanel.removeAll();

        contentPanel.add(
                new ResourcePanel(),
                BorderLayout.CENTER
        );

        refreshContent();
    }


    // ==========================================
    // EVACUATIONS
    // ==========================================

    private void showEvacuations() {

        setActiveButton(
                evacuationButton
        );

        updatePageHeader(
                "Evacuation Management",
                "Create and monitor intelligent evacuation assignments"
        );

        contentPanel.removeAll();

        contentPanel.add(
                new EvacuationPanel(),
                BorderLayout.CENTER
        );

        refreshContent();
    }

    // ==========================================
    // REPORTS
    // ==========================================

    private void showReports() {

        setActiveButton(
                reportsButton
        );

        updatePageHeader(
                "Reports & Analytics",
                "Real-time emergency response statistics"
        );

        contentPanel.removeAll();

        contentPanel.add(
                new ReportsPanel(),
                BorderLayout.CENTER
        );

        refreshContent();
    }


    // ==========================================
    // REFRESH
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


        if (result ==
                JOptionPane.YES_OPTION) {

            dispose();

            SwingUtilities.invokeLater(
                    () -> {

                        LoginFrame loginFrame =
                                new LoginFrame();

                        loginFrame.setVisible(true);
                    }
            );
        }
    }


    // ==========================================
    // MAIN - TESTING ONLY
    // ==========================================

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                () -> {

                    try {

                        UIManager.setLookAndFeel(
                                UIManager
                                        .getSystemLookAndFeelClassName()
                        );

                    } catch (Exception ignored) {
                    }


                    // Test user
                    User testUser =
                            new User();

                    testUser.setName(
                            "System Administrator"
                    );

                    testUser.setUsername(
                            "admin"
                    );

                    testUser.setRole(
                            "ADMIN"
                    );


                    MainFrame frame =
                            new MainFrame(
                                    testUser
                            );

                    frame.setVisible(true);
                }
        );
    }
}