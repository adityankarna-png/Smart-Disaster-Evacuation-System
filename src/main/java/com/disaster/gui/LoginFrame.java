package com.disaster.gui;

import com.disaster.model.User;
import com.disaster.service.AuthenticationService;
import com.disaster.service.AuthenticationServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    // ==========================================
    // COLORS - Premium Modern Palette
    // ==========================================
    private static final Color HEADER_COLOR = new Color(24, 33, 47);    // Deep Navy
    private static final Color BACKGROUND = new Color(240, 242, 245);   // Soft Gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color PRIMARY = new Color(41, 128, 185);       // Action Blue
    private static final Color PRIMARY_HOVER = new Color(31, 97, 141);  // Darker Action Blue
    private static final Color SECONDARY = new Color(236, 240, 241);    // Light Gray Btn
    private static final Color SECONDARY_HOVER = new Color(208, 211, 212);
    private static final Color TEXT = new Color(44, 62, 80);            // Slate Dark
    private static final Color SUBTEXT = new Color(127, 140, 141);      // Slate Light
    private static final Color BORDER = new Color(222, 221, 225);       // Soft Border
    private static final Color ERROR = new Color(231, 76, 60);          // Red
    private static final Color SUCCESS = new Color(46, 204, 113);       // Green

    // ==========================================
    // COMPONENTS
    // ==========================================
    private RoundedTextField usernameField;
    private RoundedPasswordField passwordField;
    private JLabel messageLabel;
    private RoundedButton loginButton;
    private RoundedButton clearButton;
    private final AuthenticationService authenticationService;

    public LoginFrame() {
        authenticationService = new AuthenticationServiceImpl();

        setTitle("Smart Disaster Evacuation System - Login");
        setSize(500, 720);
        setMinimumSize(new Dimension(480, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        buildUI();
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);

        // ==========================================
        // HEADER
        // ==========================================
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(35, 25, 35, 25));

        JLabel titleLabel = new JLabel("SMART DISASTER");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel titleLabel2 = new JLabel("EVACUATION SYSTEM");
        titleLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel2.setForeground(Color.WHITE);
        titleLabel2.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel subtitleLabel = new JLabel("Emergency Response Management");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setForeground(new Color(149, 165, 166));
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(titleLabel2);
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(subtitleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // CENTER AREA (Wrapper for the Card)
        // ==========================================
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BACKGROUND);

        // ==========================================
        // ROUNDED LOGIN CARD
        // ==========================================
        RoundedPanel loginCard = new RoundedPanel(20); // 20px border radius
        loginCard.setBackground(CARD_COLOR);
        loginCard.setLayout(new GridBagLayout()); // GridBag Layout for precise alignment
        loginCard.setPreferredSize(new Dimension(380, 420));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Title
        gbc.gridy = 0;
        gbc.insets = new Insets(30, 30, 5, 30);
        JLabel loginTitle = new JLabel("Welcome Back", SwingConstants.CENTER);
        loginTitle.setForeground(TEXT);
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        loginCard.add(loginTitle, gbc);

        // Subtitle
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 30, 25, 30);
        JLabel loginSubtitle = new JLabel("Sign in to access the control center", SwingConstants.CENTER);
        loginSubtitle.setForeground(SUBTEXT);
        loginSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginCard.add(loginSubtitle, gbc);

        // Username Label
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 30, 5, 30);
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setForeground(TEXT);
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginCard.add(usernameLabel, gbc);

        // Username Field
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 30, 15, 30);
        usernameField = new RoundedTextField();
        loginCard.add(usernameField, gbc);

        // Password Label
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 30, 5, 30);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(TEXT);
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginCard.add(passwordLabel, gbc);

        // Password Field
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 30, 15, 30);
        passwordField = new RoundedPasswordField();
        loginCard.add(passwordField, gbc);

        // Message Label
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 30, 15, 30);
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginCard.add(messageLabel, gbc);

        // Buttons Panel
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 30, 20, 30);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(new Dimension(320, 45));

        loginButton = new RoundedButton("LOGIN", PRIMARY, PRIMARY_HOVER, Color.WHITE);
        clearButton = new RoundedButton("CLEAR", SECONDARY, SECONDARY_HOVER, TEXT);

        buttonPanel.add(loginButton);
        buttonPanel.add(clearButton);
        loginCard.add(buttonPanel, gbc);

        // Demo Accounts
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 30, 5, 30);
        JLabel demoTitle = new JLabel("Available Demo Accounts", SwingConstants.CENTER);
        demoTitle.setForeground(SUBTEXT);
        demoTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginCard.add(demoTitle, gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(0, 30, 30, 30);
        JLabel demoText = new JLabel("<html><center>Admin: admin / admin123<br>Rescue: rescue / rescue123</center></html>", SwingConstants.CENTER);
        demoText.setForeground(SUBTEXT);
        demoText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        loginCard.add(demoText, gbc);

        centerPanel.add(loginCard);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ==========================================
        // FOOTER
        // ==========================================
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(HEADER_COLOR);
        footerPanel.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel footerLabel = new JLabel("SMART DISASTER EVACUATION SYSTEM v2.0");
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        footerLabel.setForeground(new Color(149, 165, 166));
        footerLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));

        footerPanel.add(footerLabel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // ==========================================
        // ACTIONS
        // ==========================================
        loginButton.addActionListener(e -> performLogin());
        clearButton.addActionListener(e -> clearFields());
        getRootPane().setDefaultButton(loginButton);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            showError("Please enter your username.");
            usernameField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            showError("Please enter your password.");
            passwordField.requestFocus();
            return;
        }

        loginButton.setEnabled(false);
        messageLabel.setForeground(SUBTEXT);
        messageLabel.setText("Authenticating...");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                return authenticationService.login(username, password);
            }
            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        showSuccess("Login successful.");
                        openMainFrame(user);
                    } else {
                        showError("Invalid username or password.");
                        passwordField.setText("");
                        passwordField.requestFocus();
                        loginButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    showError("Unable to connect to database.");
                    e.printStackTrace();
                    loginButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void openMainFrame(User user) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(user);
            mainFrame.setVisible(true);
        });
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        messageLabel.setText(" ");
        usernameField.requestFocus();
    }

    private void showError(String message) {
        messageLabel.setForeground(ERROR);
        messageLabel.setText(message);
    }

    private void showSuccess(String message) {
        messageLabel.setForeground(SUCCESS);
        messageLabel.setText(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }

    // ==========================================
    // CUSTOM UI COMPONENTS FOR ROUNDED LOOK
    // ==========================================

    private static class RoundedPanel extends JPanel {
        private final int radius;
        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedTextField extends JTextField {
        private Color currentBorder = new Color(209, 213, 219);
        public RoundedTextField() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            setPreferredSize(new Dimension(300, 42));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { currentBorder = PRIMARY; repaint(); }
                @Override public void focusLost(FocusEvent e) { currentBorder = new Color(209, 213, 219); repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            super.paintComponent(g);
            g2.setColor(currentBorder);
            g2.setStroke(new BasicStroke(currentBorder == PRIMARY ? 2f : 1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
        }
    }

    private static class RoundedPasswordField extends JPasswordField {
        private Color currentBorder = new Color(209, 213, 219);
        public RoundedPasswordField() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            setPreferredSize(new Dimension(300, 42));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { currentBorder = PRIMARY; repaint(); }
                @Override public void focusLost(FocusEvent e) { currentBorder = new Color(209, 213, 219); repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            super.paintComponent(g);
            g2.setColor(currentBorder);
            g2.setStroke(new BasicStroke(currentBorder == PRIMARY ? 2f : 1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
        }
    }

    private static class RoundedButton extends JButton {
        private final Color defaultBg;
        private final Color hoverBg;

        public RoundedButton(String text, Color bg, Color hoverBg, Color fg) {
            super(text);
            this.defaultBg = bg;
            this.hoverBg = hoverBg;
            setForeground(fg);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { if (isEnabled()) setBackground(hoverBg); repaint(); }
                @Override public void mouseExited(MouseEvent e) { setBackground(defaultBg); repaint(); }
            });
            setBackground(defaultBg);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g);
            g2.dispose();
        }
    }
}