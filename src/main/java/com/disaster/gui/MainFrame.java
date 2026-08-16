package com.disaster.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel contentPanel;

    // ==========================================
    // COLORS
    // ==========================================

    private static final Color HEADER_COLOR =
            new Color(25, 55, 90);

    private static final Color SIDEBAR_COLOR =
            new Color(35, 50, 65);

    private static final Color BUTTON_COLOR =
            new Color(45, 65, 85);

    private static final Color BUTTON_HOVER_COLOR =
            new Color(55, 85, 110);

    private static final Color BACKGROUND_COLOR =
            new Color(245, 247, 250);

    private static final Color TEXT_COLOR =
            new Color(35, 45, 55);


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public MainFrame() {

        setTitle(
                "Smart Disaster Evacuation System"
        );

        setSize(
                1250,
                750
        );

        setMinimumSize(
                new Dimension(
                        1100,
                        650
                )
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        setLayout(
                new BorderLayout()
        );


        // ==========================================
        // HEADER
        // ==========================================

        JPanel headerPanel =
                new JPanel(
                        new BorderLayout()
                );

        headerPanel.setBackground(
                HEADER_COLOR
        );

        headerPanel.setPreferredSize(
                new Dimension(
                        1250,
                        85
                )
        );


        JLabel titleLabel =
                new JLabel(
                        "SMART DISASTER EVACUATION SYSTEM",
                        SwingConstants.CENTER
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


        headerPanel.add(
                titleLabel,
                BorderLayout.CENTER
        );


        add(
                headerPanel,
                BorderLayout.NORTH
        );


        // ==========================================
        // SIDEBAR
        // ==========================================

        JPanel sidebarPanel =
                new JPanel();

        sidebarPanel.setPreferredSize(
                new Dimension(
                        220,
                        650
                )
        );

        sidebarPanel.setBackground(
                SIDEBAR_COLOR
        );

        sidebarPanel.setLayout(
                new GridLayout(
                        7,
                        1,
                        0,
                        8
                )
        );

        sidebarPanel.setBorder(
                new EmptyBorder(
                        8,
                        0,
                        8,
                        0
                )
        );


        // ==========================================
        // BUTTONS
        // ==========================================

        JButton dashboardButton =
                createMenuButton(
                        "Dashboard"
                );

        JButton disasterButton =
                createMenuButton(
                        "Disasters"
                );

        JButton victimButton =
                createMenuButton(
                        "Victims"
                );

        JButton shelterButton =
                createMenuButton(
                        "Shelters"
                );

        JButton resourceButton =
                createMenuButton(
                        "Resources"
                );

        JButton evacuationButton =
                createMenuButton(
                        "Evacuation"
                );

        JButton exitButton =
                createMenuButton(
                        "Exit"
                );


        sidebarPanel.add(
                dashboardButton
        );

        sidebarPanel.add(
                disasterButton
        );

        sidebarPanel.add(
                victimButton
        );

        sidebarPanel.add(
                shelterButton
        );

        sidebarPanel.add(
                resourceButton
        );

        sidebarPanel.add(
                evacuationButton
        );

        sidebarPanel.add(
                exitButton
        );


        add(
                sidebarPanel,
                BorderLayout.WEST
        );


        // ==========================================
        // CONTENT PANEL
        // ==========================================

        contentPanel =
                new JPanel(
                        new BorderLayout()
                );

        contentPanel.setBackground(
                BACKGROUND_COLOR
        );


        add(
                contentPanel,
                BorderLayout.CENTER
        );


        // ==========================================
        // BUTTON ACTIONS
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

        exitButton.addActionListener(
                e -> {

                    int result =
                            JOptionPane.showConfirmDialog(
                                    this,
                                    "Are you sure you want to exit?",
                                    "Exit",
                                    JOptionPane.YES_NO_OPTION
                            );

                    if (result ==
                            JOptionPane.YES_OPTION) {

                        System.exit(0);
                    }
                }
        );


        // ==========================================
        // DEFAULT SCREEN
        // ==========================================

        showDashboard();
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
                Color.WHITE
        );

        button.setBackground(
                BUTTON_COLOR
        );

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        button.setFocusPainted(
                false
        );

        button.setBorderPainted(
                false
        );

        button.setOpaque(
                true
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setBorder(
                new EmptyBorder(
                        10,
                        15,
                        10,
                        15
                )
        );


        // ==========================================
        // HOVER EFFECT
        // ==========================================

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {

                        button.setBackground(
                                BUTTON_HOVER_COLOR
                        );
                    }


                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        button.setBackground(
                                BUTTON_COLOR
                        );
                    }
                }
        );


        return button;
    }


    // ==========================================
    // DASHBOARD
    // ==========================================

    private void showDashboard() {

        contentPanel.removeAll();

        contentPanel.add(
                new DashboardPanel(),
                BorderLayout.CENTER
        );

        contentPanel.revalidate();

        contentPanel.repaint();
    }


    // ==========================================
    // DISASTERS
    // ==========================================

    private void showDisasters() {

        contentPanel.removeAll();

        contentPanel.add(
                new DisasterPanel(),
                BorderLayout.CENTER
        );

        contentPanel.revalidate();

        contentPanel.repaint();
    }


    // ==========================================
    // VICTIMS
    // ==========================================

    private void showVictims() {

        contentPanel.removeAll();

        contentPanel.add(
                new VictimPanel(),
                BorderLayout.CENTER
        );

        contentPanel.revalidate();

        contentPanel.repaint();
    }


    // ==========================================
    // SHELTERS
    // ==========================================

    private void showShelters() {

        contentPanel.removeAll();

        contentPanel.add(
                new ShelterPanel(),
                BorderLayout.CENTER
        );

        contentPanel.revalidate();

        contentPanel.repaint();
    }


    // ==========================================
    // RESOURCES
    // ==========================================

    private void showResources() {

        contentPanel.removeAll();

        contentPanel.add(
                new ResourcePanel(),
                BorderLayout.CENTER
        );

        contentPanel.revalidate();

        contentPanel.repaint();
    }


    // ==========================================
    // EVACUATIONS
    // ==========================================

    private void showEvacuations() {

        contentPanel.removeAll();

        contentPanel.add(
                new EvacuationPanel(),
                BorderLayout.CENTER
        );

        contentPanel.revalidate();

        contentPanel.repaint();
    }


    // ==========================================
    // MAIN
    // ==========================================

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                () -> {

                    MainFrame frame =
                            new MainFrame();

                    frame.setVisible(true);
                }
        );
    }
}