package com.disaster.gui;

import com.disaster.model.User;
import com.disaster.service.AuthenticationService;
import com.disaster.service.AuthenticationServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    // ==========================================
    // COLORS
    // ==========================================

    private static final Color HEADER_COLOR =
            new Color(25, 55, 90);

    private static final Color BACKGROUND =
            new Color(245, 247, 250);

    private static final Color CARD_COLOR =
            Color.WHITE;

    private static final Color PRIMARY =
            new Color(55, 105, 155);

    private static final Color PRIMARY_HOVER =
            new Color(45, 90, 140);

    private static final Color TEXT =
            new Color(35, 45, 55);

    private static final Color SUBTEXT =
            new Color(105, 115, 125);

    private static final Color BORDER =
            new Color(215, 220, 225);

    private static final Color ERROR =
            new Color(205, 65, 65);

    private static final Color SUCCESS =
            new Color(40, 150, 90);


    // ==========================================
    // COMPONENTS
    // ==========================================

    private JTextField usernameField;

    private JPasswordField passwordField;

    private JLabel messageLabel;

    private JButton loginButton;

    private JButton clearButton;


    // ==========================================
    // SERVICE
    // ==========================================

    private final AuthenticationService authenticationService;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public LoginFrame() {

        authenticationService =
                new AuthenticationServiceImpl();

        setTitle(
                "Smart Disaster Evacuation System - Login"
        );

        setSize(
                520,
                650
        );

        setMinimumSize(
                new Dimension(480, 600)
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        setResizable(false);

        buildUI();
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


        // ==========================================
        // HEADER
        // ==========================================

        JPanel headerPanel =
                new JPanel();

        headerPanel.setBackground(
                HEADER_COLOR
        );

        headerPanel.setLayout(
                new BoxLayout(
                        headerPanel,
                        BoxLayout.Y_AXIS
                )
        );

        headerPanel.setBorder(
                new EmptyBorder(
                        32,
                        25,
                        30,
                        25
                )
        );


        JLabel titleLabel =
                new JLabel(
                        "SMART DISASTER"
                );

        titleLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        titleLabel.setForeground(
                Color.WHITE
        );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        27
                )
        );


        JLabel titleLabel2 =
                new JLabel(
                        "EVACUATION SYSTEM"
                );

        titleLabel2.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        titleLabel2.setForeground(
                Color.WHITE
        );

        titleLabel2.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        27
                )
        );


        JLabel subtitleLabel =
                new JLabel(
                        "Emergency Response Management"
                );

        subtitleLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        subtitleLabel.setForeground(
                new Color(205, 220, 235)
        );

        subtitleLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );


        headerPanel.add(titleLabel);

        headerPanel.add(
                Box.createVerticalStrut(2)
        );

        headerPanel.add(titleLabel2);

        headerPanel.add(
                Box.createVerticalStrut(10)
        );

        headerPanel.add(subtitleLabel);


        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );


        // ==========================================
        // LOGIN CARD
        // ==========================================

        JPanel centerPanel =
                new JPanel(
                        new GridBagLayout()
                );

        centerPanel.setBackground(
                BACKGROUND
        );


        JPanel loginCard =
                new JPanel();

        loginCard.setBackground(
                CARD_COLOR
        );

        loginCard.setLayout(
                new BoxLayout(
                        loginCard,
                        BoxLayout.Y_AXIS
                )
        );

        loginCard.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                32,
                                38,
                                32,
                                38
                        )
                )
        );


        // ==========================================
        // LOGIN TITLE
        // ==========================================

        JLabel loginTitle =
                new JLabel(
                        "Welcome Back"
                );

        loginTitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        loginTitle.setForeground(
                TEXT
        );

        loginTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );


        JLabel loginSubtitle =
                new JLabel(
                        "Sign in to access the control center"
                );

        loginSubtitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        loginSubtitle.setForeground(
                SUBTEXT
        );

        loginSubtitle.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );


        loginCard.add(loginTitle);

        loginCard.add(
                Box.createVerticalStrut(5)
        );

        loginCard.add(loginSubtitle);

        loginCard.add(
                Box.createVerticalStrut(28)
        );


        // ==========================================
        // USERNAME
        // ==========================================

        JLabel usernameLabel =
                createFieldLabel(
                        "Username"
                );

        loginCard.add(usernameLabel);

        loginCard.add(
                Box.createVerticalStrut(7)
        );


        usernameField =
                new JTextField();

        styleTextField(
                usernameField
        );

        loginCard.add(
                usernameField
        );

        loginCard.add(
                Box.createVerticalStrut(18)
        );


        // ==========================================
        // PASSWORD
        // ==========================================

        JLabel passwordLabel =
                createFieldLabel(
                        "Password"
                );

        loginCard.add(passwordLabel);

        loginCard.add(
                Box.createVerticalStrut(7)
        );


        passwordField =
                new JPasswordField();

        styleTextField(
                passwordField
        );

        loginCard.add(
                passwordField
        );

        loginCard.add(
                Box.createVerticalStrut(12)
        );


        // ==========================================
        // MESSAGE
        // ==========================================

        messageLabel =
                new JLabel(
                        " "
                );

        messageLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        messageLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        messageLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );

        loginCard.add(messageLabel);

        loginCard.add(
                Box.createVerticalStrut(12)
        );


        // ==========================================
        // BUTTONS
        // ==========================================

        JPanel buttonPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                10,
                                0
                        )
                );

        buttonPanel.setOpaque(false);

        buttonPanel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );


        loginButton =
                createPrimaryButton(
                        "LOGIN"
                );

        clearButton =
                createSecondaryButton(
                        "CLEAR"
                );


        buttonPanel.add(loginButton);
        buttonPanel.add(clearButton);


        loginCard.add(buttonPanel);


        // ==========================================
        // DEMO ACCOUNTS
        // ==========================================

        loginCard.add(
                Box.createVerticalStrut(25)
        );


        JLabel demoTitle =
                new JLabel(
                        "Available Demo Accounts"
                );

        demoTitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        demoTitle.setForeground(
                SUBTEXT
        );

        demoTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        11
                )
        );


        JLabel demoText =
                new JLabel(
                        "<html><center>" +
                                "Admin: admin / admin123<br>" +
                                "Rescue: rescue / rescue123<br>" +
                                "Medical: medical / medical123" +
                                "</center></html>"
                );

        demoText.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        demoText.setForeground(
                SUBTEXT
        );

        demoText.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        10
                )
        );


        loginCard.add(demoTitle);

        loginCard.add(
                Box.createVerticalStrut(6)
        );

        loginCard.add(demoText);


        centerPanel.add(
                loginCard
        );


        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );


        // ==========================================
        // FOOTER
        // ==========================================

        JPanel footerPanel =
                new JPanel(
                        new BorderLayout()
                );

        footerPanel.setBackground(
                HEADER_COLOR
        );

        footerPanel.setBorder(
                new EmptyBorder(
                        9,
                        15,
                        9,
                        15
                )
        );


        JLabel footerLabel =
                new JLabel(
                        "SMART DISASTER EVACUATION SYSTEM"
                );

        footerLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        footerLabel.setForeground(
                new Color(190, 205, 220)
        );

        footerLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        9
                )
        );


        footerPanel.add(
                footerLabel,
                BorderLayout.CENTER
        );


        mainPanel.add(
                footerPanel,
                BorderLayout.SOUTH
        );


        add(mainPanel);


        // ==========================================
        // ACTIONS
        // ==========================================

        loginButton.addActionListener(
                e -> performLogin()
        );

        clearButton.addActionListener(
                e -> clearFields()
        );


        // ENTER KEY
        getRootPane().setDefaultButton(
                loginButton
        );
    }


    // ==========================================
    // FIELD LABEL
    // ==========================================

    private JLabel createFieldLabel(
            String text
    ) {

        JLabel label =
                new JLabel(text);

        label.setForeground(
                TEXT
        );

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12
                )
        );

        label.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return label;
    }


    // ==========================================
    // TEXT FIELD STYLE
    // ==========================================

    private void styleTextField(
            JTextField field
    ) {

        field.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        field.setForeground(
                TEXT
        );

        field.setBackground(
                Color.WHITE
        );

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                9,
                                11,
                                9,
                                11
                        )
                )
        );

        field.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        40
                )
        );

        field.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );
    }


    // ==========================================
    // PRIMARY BUTTON
    // ==========================================

    private JButton createPrimaryButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setForeground(
                Color.WHITE
        );

        button.setBackground(
                PRIMARY
        );

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12
                )
        );

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }


    // ==========================================
    // SECONDARY BUTTON
    // ==========================================

    private JButton createSecondaryButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setForeground(
                TEXT
        );

        button.setBackground(
                new Color(
                        235,
                        238,
                        242
                )
        );

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12
                )
        );

        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createLineBorder(
                        BORDER
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }


    // ==========================================
    // LOGIN
    // ==========================================

    private void performLogin() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );


        // ==========================================
        // VALIDATION
        // ==========================================

        if (username.isEmpty()) {

            showError(
                    "Please enter your username."
            );

            usernameField.requestFocus();

            return;
        }


        if (password.isEmpty()) {

            showError(
                    "Please enter your password."
            );

            passwordField.requestFocus();

            return;
        }


        loginButton.setEnabled(false);

        messageLabel.setForeground(
                SUBTEXT
        );

        messageLabel.setText(
                "Authenticating..."
        );


        // ==========================================
        // AUTHENTICATE
        // ==========================================

        SwingWorker<User, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected User doInBackground() {

                        return authenticationService.login(
                                username,
                                password
                        );
                    }


                    @Override
                    protected void done() {

                        try {

                            User user = get();

                            if (user != null) {

                                showSuccess(
                                        "Login successful."
                                );

                                openMainFrame(
                                        user
                                );

                            } else {

                                showError(
                                        "Invalid username or password."
                                );

                                passwordField.setText("");

                                passwordField.requestFocus();

                                loginButton.setEnabled(
                                        true
                                );
                            }

                        } catch (Exception e) {

                            showError(
                                    "Unable to connect to database."
                            );

                            e.printStackTrace();

                            loginButton.setEnabled(
                                    true
                            );
                        }
                    }
                };


        worker.execute();
    }


    // ==========================================
    // OPEN MAIN FRAME
    // ==========================================

    private void openMainFrame(User user) {

        dispose();

        SwingUtilities.invokeLater(
                () -> {

                    MainFrame mainFrame =
                            new MainFrame(user);

                    mainFrame.setVisible(true);
                }
        );
    }


    // ==========================================
    // CLEAR
    // ==========================================

    private void clearFields() {

        usernameField.setText("");

        passwordField.setText("");

        messageLabel.setText(" ");

        usernameField.requestFocus();
    }


    // ==========================================
    // ERROR MESSAGE
    // ==========================================

    private void showError(
            String message
    ) {

        messageLabel.setForeground(
                ERROR
        );

        messageLabel.setText(
                message
        );
    }


    // ==========================================
    // SUCCESS MESSAGE
    // ==========================================

    private void showSuccess(
            String message
    ) {

        messageLabel.setForeground(
                SUCCESS
        );

        messageLabel.setText(
                message
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
                                        .getSystemLookAndFeelClassName()
                        );

                    } catch (Exception ignored) {
                    }

                    LoginFrame frame =
                            new LoginFrame();

                    frame.setVisible(true);
                }
        );
    }
}