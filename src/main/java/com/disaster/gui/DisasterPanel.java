package com.disaster.gui;

import com.disaster.dao.DisasterDAO;
import com.disaster.model.Disaster;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DisasterPanel extends JPanel {

    // =========================================================
    // COLOR PALETTE (shared design language)
    // =========================================================

    private static final Color BACKGROUND   = new Color(0xF1, 0xF5, 0xF9); // slate-100
    private static final Color SURFACE       = Color.WHITE;
    private static final Color BORDER        = new Color(0xE2, 0xE8, 0xF0); // slate-200
    private static final Color TEXT          = new Color(0x1E, 0x29, 0x3B); // slate-800
    private static final Color TEXT_MUTED    = new Color(0x64, 0x74, 0x8B); // slate-500

    // Accent colors
    private static final Color ACCENT        = new Color(0x25, 0x63, 0xEB); // blue-600
    private static final Color ACCENT_HOVER  = new Color(0x1D, 0x4E, 0xD8); // blue-700
    private static final Color NEUTRAL_BTN   = new Color(0x47, 0x55, 0x69); // slate-600
    private static final Color NEUTRAL_HOVER = new Color(0x33, 0x41, 0x55); // slate-700
    private static final Color RED           = new Color(0xDC, 0x26, 0x26); // red-600
    private static final Color RED_HOVER     = new Color(0xB9, 0x1C, 0x1C); // red-700

    private static final Color RED_SOFT      = new Color(0xFE, 0xE2, 0xE2);
    private static final Color RED_TEXT      = new Color(0x99, 0x1B, 0x1B);
    private static final Color ORANGE_SOFT   = new Color(0xFF, 0xED, 0xD5);
    private static final Color ORANGE_TEXT   = new Color(0x9A, 0x34, 0x12);
    private static final Color GREEN_SOFT    = new Color(0xDC, 0xFC, 0xE7);
    private static final Color GREEN_TEXT    = new Color(0x16, 0x65, 0x34);

    private static final Color TABLE_HEADER  = new Color(0xF8, 0xFA, 0xFC); // slate-50
    private static final Color ROW_ALT       = new Color(0xF8, 0xFA, 0xFC);
    private static final Color ROW_SELECT    = new Color(0xDB, 0xEA, 0xFE); // blue-100

    // =========================================================
    // FONT STACK (modern with graceful fallback)
    // =========================================================

    private static final String FONT_FAMILY = resolveFontFamily();

    private static String resolveFontFamily() {
        String[] preferred = {
                "Segoe UI", "SF Pro Text", "Helvetica Neue",
                "Roboto", "Inter", "Arial"
        };
        java.util.Set<String> available = new java.util.HashSet<>(java.util.Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames()));
        for (String f : preferred) {
            if (available.contains(f)) {
                return f;
            }
        }
        return Font.SANS_SERIF;
    }

    private static Font font(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    // =========================================================
    // COMPONENTS
    // =========================================================

    private JTable disasterTable;
    private DefaultTableModel tableModel;
    private JLabel disasterCountLabel;

    private final DisasterDAO disasterDAO;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DisasterPanel() {

        disasterDAO = new DisasterDAO();

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);

        loadDisasters();
    }

    // =========================================================
    // HEADER (title + subtitle + count badge)
    // =========================================================

    private JComponent buildHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 2, 20, 2));

        // Title + subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Disaster Management");
        titleLabel.setFont(font(Font.BOLD, 26));
        titleLabel.setForeground(TEXT);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(
                "Manage active disasters and emergency incident information");
        subtitleLabel.setFont(font(Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_MUTED);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subtitleLabel);

        header.add(titlePanel, BorderLayout.WEST);

        // Count badge
        disasterCountLabel = new JLabel("0 Disasters");
        disasterCountLabel.setFont(font(Font.BOLD, 13));
        disasterCountLabel.setForeground(ACCENT);
        disasterCountLabel.setBorder(new EmptyBorder(8, 16, 8, 16));

        JPanel badge = new RoundedPanel(999, new Color(0xE0, 0xEA, 0xFF));
        badge.setLayout(new BorderLayout());
        badge.add(disasterCountLabel, BorderLayout.CENTER);

        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge);
        header.add(badgeWrap, BorderLayout.EAST);

        return header;
    }

    // =========================================================
    // TABLE CARD (rounded surface wrapping table + actions)
    // =========================================================

    private JComponent buildTableCard() {

        RoundedPanel card = new RoundedPanel(16, SURFACE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(6, 6, 6, 6));

        buildTable();

        JScrollPane scrollPane = new JScrollPane(disasterTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.setBackground(SURFACE);

        card.add(scrollPane, BorderLayout.CENTER);
        card.add(buildButtonBar(), BorderLayout.SOUTH);

        return card;
    }

    private void buildTable() {

        String[] columns = {
                "Disaster ID", "Type", "Location",
                "Severity", "Description", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        disasterTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(
                    javax.swing.table.TableCellRenderer renderer,
                    int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? SURFACE : ROW_ALT);
                }
                return c;
            }
        };

        disasterTable.setRowHeight(44);
        disasterTable.setFont(font(Font.PLAIN, 13));
        disasterTable.setForeground(TEXT);
        disasterTable.setBackground(SURFACE);
        disasterTable.setShowGrid(false);
        disasterTable.setIntercellSpacing(new Dimension(0, 0));
        disasterTable.setFillsViewportHeight(true);
        disasterTable.setSelectionBackground(ROW_SELECT);
        disasterTable.setSelectionForeground(TEXT);
        disasterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        disasterTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Default left-padded renderer for text columns
        DefaultTableCellRenderer padded = new DefaultTableCellRenderer();
        padded.setBorder(new EmptyBorder(0, 14, 0, 14));

        // Header styling
        JTableHeader tableHeader = disasterTable.getTableHeader();
        tableHeader.setFont(font(Font.BOLD, 12));
        tableHeader.setBackground(TABLE_HEADER);
        tableHeader.setForeground(TEXT_MUTED);
        tableHeader.setPreferredSize(new Dimension(0, 42));
        tableHeader.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) tableHeader.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.LEFT);
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        // Column widths
        int[] widths = {90, 110, 130, 90, 280, 120};
        for (int i = 0; i < widths.length; i++) {
            disasterTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Text column left padding
        for (int i : new int[]{0, 1, 2, 4}) {
            disasterTable.getColumnModel().getColumn(i).setCellRenderer(
                    new PaddedRenderer());
        }

        // Custom renderers
        disasterTable.getColumnModel().getColumn(3)
                .setCellRenderer(new SeverityRenderer());
        disasterTable.getColumnModel().getColumn(5)
                .setCellRenderer(new StatusRenderer());
    }

    // =========================================================
    // BUTTON BAR
    // =========================================================

    private JComponent buildButtonBar() {

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        JButton addButton     = createButton("Add Disaster", ACCENT, ACCENT_HOVER);
        JButton updateButton  = createButton("Update Disaster", NEUTRAL_BTN, NEUTRAL_HOVER);
        JButton refreshButton = createGhostButton("Refresh");
        JButton deleteButton  = createButton("Delete Disaster", RED, RED_HOVER);

        bar.add(refreshButton);
        bar.add(addButton);
        bar.add(updateButton);
        bar.add(deleteButton);

        addButton.addActionListener(e -> showAddDisasterDialog());
        updateButton.addActionListener(e -> showUpdateDisasterDialog());
        refreshButton.addActionListener(e -> loadDisasters());
        deleteButton.addActionListener(e -> deleteDisaster());

        return bar;
    }

    // =========================================================
    // BUTTON FACTORIES
    // =========================================================

    private JButton createButton(String text, Color base, Color hover) {

        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(font(Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorder(new EmptyBorder(11, 20, 11, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createGhostButton(String text) {

        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(0xF1, 0xF5, 0xF9));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(font(Font.BOLD, 13));
        button.setForeground(TEXT_MUTED);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorder(new EmptyBorder(11, 20, 11, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // =========================================================
    // LOAD DISASTERS
    // =========================================================

    private void loadDisasters() {

        tableModel.setRowCount(0);

        List<Disaster> disasters = disasterDAO.getAllDisasters();

        for (Disaster disaster : disasters) {
            tableModel.addRow(new Object[]{
                    disaster.getDisasterId(),
                    disaster.getType(),
                    disaster.getLocation(),
                    disaster.getSeverity(),
                    disaster.getDescription(),
                    disaster.getStatus()
            });
        }

        disasterCountLabel.setText(disasters.size() + " Disasters");
    }

    // =========================================================
    // ADD DISASTER
    // =========================================================

    private void showAddDisasterDialog() {

        JTextField typeField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField severityField = new JTextField();
        JTextField descriptionField = new JTextField();
        JComboBox<String> statusField = new JComboBox<>(
                new String[]{"ACTIVE", "RESOLVED", "CLOSED"});

        JPanel panel = createFormPanel();
        panel.add(new JLabel("Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Severity (1-5):"));
        panel.add(severityField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Add Disaster",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String type = typeField.getText().trim();
            String location = locationField.getText().trim();
            int severity = Integer.parseInt(severityField.getText().trim());
            String description = descriptionField.getText().trim();
            String status = statusField.getSelectedItem().toString();

            if (type.isEmpty() || location.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all fields.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (severity < 1 || severity > 5) {
                JOptionPane.showMessageDialog(this,
                        "Severity must be between 1 and 5.",
                        "Invalid Severity", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Disaster disaster = new Disaster();
            disaster.setType(type);
            disaster.setLocation(location);
            disaster.setSeverity(severity);
            disaster.setDescription(description);
            disaster.setStatus(status);

            disasterDAO.addDisaster(disaster);

            JOptionPane.showMessageDialog(this,
                    "Disaster added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            loadDisasters();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Severity must be a number between 1 and 5.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error adding disaster:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // UPDATE DISASTER
    // =========================================================

    private void showUpdateDisasterDialog() {

        int selectedRow = disasterTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a disaster first.",
                    "No Disaster Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int disasterId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentType = tableModel.getValueAt(selectedRow, 1).toString();
        String currentLocation = tableModel.getValueAt(selectedRow, 2).toString();
        int currentSeverity = (int) tableModel.getValueAt(selectedRow, 3);
        String currentDescription = tableModel.getValueAt(selectedRow, 4).toString();
        String currentStatus = tableModel.getValueAt(selectedRow, 5).toString();

        JTextField typeField = new JTextField(currentType);
        JTextField locationField = new JTextField(currentLocation);
        JTextField severityField = new JTextField(String.valueOf(currentSeverity));
        JTextField descriptionField = new JTextField(currentDescription);
        JComboBox<String> statusField = new JComboBox<>(
                new String[]{"ACTIVE", "RESOLVED", "CLOSED"});
        statusField.setSelectedItem(currentStatus);

        JPanel panel = createFormPanel();
        panel.add(new JLabel("Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Severity (1-5):"));
        panel.add(severityField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Update Disaster ID " + disasterId,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String type = typeField.getText().trim();
            String location = locationField.getText().trim();
            int severity = Integer.parseInt(severityField.getText().trim());
            String description = descriptionField.getText().trim();
            String status = statusField.getSelectedItem().toString();

            if (type.isEmpty() || location.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all fields.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (severity < 1 || severity > 5) {
                JOptionPane.showMessageDialog(this,
                        "Severity must be between 1 and 5.",
                        "Invalid Severity", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Disaster disaster = new Disaster();
            disaster.setDisasterId(disasterId);
            disaster.setType(type);
            disaster.setLocation(location);
            disaster.setSeverity(severity);
            disaster.setDescription(description);
            disaster.setStatus(status);

            disasterDAO.updateDisaster(disaster);

            JOptionPane.showMessageDialog(this,
                    "Disaster updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            loadDisasters();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Severity must be a number between 1 and 5.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error updating disaster:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // DELETE DISASTER
    // =========================================================

    private void deleteDisaster() {

        int selectedRow = disasterTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a disaster first.",
                    "No Disaster Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int disasterId = (int) tableModel.getValueAt(selectedRow, 0);
        String disasterType = tableModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete\n"
                        + "Disaster ID: " + disasterId
                        + "\nType: " + disasterType + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            disasterDAO.deleteDisaster(disasterId);

            JOptionPane.showMessageDialog(this,
                    "Disaster deleted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            loadDisasters();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting disaster:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // FORM PANEL
    // =========================================================

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 12, 12));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        return panel;
    }

    // =========================================================
    // ROUNDED PANEL (soft drop shadow + rounded corners)
    // =========================================================

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill;

        RoundedPanel(int radius, Color fill) {
            this.radius = radius;
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int shadow = 6;
            // soft layered shadow
            for (int i = shadow; i > 0; i--) {
                float alpha = 0.04f * (shadow - i + 1);
                g2.setColor(new Color(0.06f, 0.09f, 0.16f, Math.min(alpha, 0.20f)));
                g2.fillRoundRect(i, i + 2, getWidth() - i * 2,
                        getHeight() - i * 2, radius, radius);
            }

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - shadow,
                    getHeight() - shadow, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================
    // PADDED TEXT RENDERER
    // =========================================================

    private static class PaddedRenderer extends DefaultTableCellRenderer {
        PaddedRenderer() {
            setBorder(new EmptyBorder(0, 14, 0, 14));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            setBorder(new EmptyBorder(0, 14, 0, 14));
            c.setForeground(isSelected ? TEXT : TEXT);
            return c;
        }
    }

    // =========================================================
    // SEVERITY RENDERER (colored pill)
    // =========================================================

    private static class SeverityRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(false);

            if (value != null) {
                int severity;
                try {
                    severity = Integer.parseInt(value.toString());
                } catch (NumberFormatException ex) {
                    severity = 0;
                }

                Color bg, fg;
                if (severity >= 5) {
                    bg = RED_SOFT; fg = RED_TEXT;
                } else if (severity >= 3) {
                    bg = ORANGE_SOFT; fg = ORANGE_TEXT;
                } else {
                    bg = GREEN_SOFT; fg = GREEN_TEXT;
                }

                return new PillCell("Level " + value, bg, fg,
                        isSelected ? ROW_SELECT
                                : (row % 2 == 0 ? SURFACE : ROW_ALT));
            }

            return label;
        }
    }

    // =========================================================
    // STATUS RENDERER (colored pill)
    // =========================================================

    private static class StatusRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(false);

            if (value != null) {
                String status = value.toString().toUpperCase();
                Color bg, fg;
                if (status.equals("ACTIVE")) {
                    bg = RED_SOFT; fg = RED_TEXT;
                } else if (status.equals("RESOLVED") || status.equals("CLOSED")) {
                    bg = GREEN_SOFT; fg = GREEN_TEXT;
                } else {
                    bg = ORANGE_SOFT; fg = ORANGE_TEXT;
                }

                return new PillCell(value.toString(), bg, fg,
                        isSelected ? ROW_SELECT
                                : (row % 2 == 0 ? SURFACE : ROW_ALT));
            }

            return label;
        }
    }

    // =========================================================
    // PILL CELL (rounded badge painted inside a table cell)
    // =========================================================

    private static class PillCell extends JPanel {
        private final String text;
        private final Color pillBg;
        private final Color pillFg;

        PillCell(String text, Color pillBg, Color pillFg, Color cellBg) {
            this.text = text;
            this.pillBg = pillBg;
            this.pillFg = pillFg;
            setBackground(cellBg);
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Font f = font(Font.BOLD, 12);
            g2.setFont(f);
            FontMetrics fm = g2.getFontMetrics();

            int textW = fm.stringWidth(text);
            int padX = 12;
            int pillW = textW + padX * 2;
            int pillH = 24;
            int x = (getWidth() - pillW) / 2;
            int y = (getHeight() - pillH) / 2;

            g2.setColor(pillBg);
            g2.fillRoundRect(x, y, pillW, pillH, pillH, pillH);

            g2.setColor(pillFg);
            int textX = x + padX;
            int textY = y + (pillH - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, textX, textY);

            g2.dispose();
        }
    }
}