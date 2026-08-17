package com.disaster.gui;

import com.disaster.dao.ShelterDAO;
import com.disaster.model.Shelter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class ShelterPanel extends JPanel {

    // =========================================================
    // DESIGN TOKENS (shared across all panels)
    // =========================================================

    // Neutrals (slate)
    private static final Color BACKGROUND    = new Color(0xF1, 0xF5, 0xF9); // slate-100
    private static final Color SURFACE       = new Color(0xFF, 0xFF, 0xFF); // white
    private static final Color ROW_ALT       = new Color(0xF8, 0xFA, 0xFC); // slate-50
    private static final Color BORDER_SOFT   = new Color(0xE2, 0xE8, 0xF0); // slate-200
    private static final Color HEADER_BG     = new Color(0xF8, 0xFA, 0xFC); // slate-50
    private static final Color TEXT          = new Color(0x0F, 0x17, 0x2A); // slate-900
    private static final Color TEXT_SUBTLE   = new Color(0x47, 0x55, 0x69); // slate-600
    private static final Color TEXT_MUTED    = new Color(0x64, 0x74, 0x8B); // slate-500
    private static final Color SELECTION_BG  = new Color(0xDB, 0xEA, 0xFE); // blue-100

    // Accent colors
    private static final Color BLUE          = new Color(0x25, 0x63, 0xEB); // blue-600
    private static final Color BLUE_HOVER    = new Color(0x1D, 0x4E, 0xD8); // blue-700
    private static final Color GREEN         = new Color(0x16, 0xA3, 0x4A); // green-600
    private static final Color ORANGE        = new Color(0xEA, 0x8A, 0x04); // amber-600
    private static final Color RED           = new Color(0xDC, 0x26, 0x26); // red-600
    private static final Color RED_HOVER     = new Color(0xB9, 0x1C, 0x1C); // red-700
    private static final Color NEUTRAL       = new Color(0x47, 0x55, 0x69); // slate-600
    private static final Color NEUTRAL_HOVER = new Color(0x33, 0x41, 0x55); // slate-700

    // Soft pill backgrounds
    private static final Color GREEN_SOFT    = new Color(0xDC, 0xFC, 0xE7); // green-100
    private static final Color GREEN_TEXT    = new Color(0x15, 0x80, 0x3D); // green-700
    private static final Color ORANGE_SOFT   = new Color(0xFE, 0xF3, 0xC7); // amber-100
    private static final Color ORANGE_TEXT   = new Color(0xB4, 0x53, 0x09); // amber-700
    private static final Color RED_SOFT      = new Color(0xFE, 0xE2, 0xE2); // red-100
    private static final Color RED_TEXT      = new Color(0xB9, 0x1C, 0x1C); // red-700

    // =========================================================
    // FONTS (modern stack with runtime fallback)
    // =========================================================

    private static final String FONT_FAMILY = resolveFontFamily();

    private static String resolveFontFamily() {
        String[] preferred = {
                "Segoe UI", "SF Pro Text", "SF Pro Display",
                "Helvetica Neue", "Roboto", "Inter", "Arial"
        };
        java.util.Set<String> available = new java.util.HashSet<>();
        for (String f : GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames()) {
            available.add(f);
        }
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

    private JTable shelterTable;
    private DefaultTableModel tableModel;
    private JLabel shelterCountLabel;

    private final ShelterDAO shelterDAO;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ShelterPanel() {

        shelterDAO = new ShelterDAO();

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCard(), BorderLayout.CENTER);

        loadShelters();
    }

    // =========================================================
    // HEADER (title + count badge)
    // =========================================================

    private JPanel buildHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 4, 20, 4));

        JPanel titleGroup = new JPanel();
        titleGroup.setOpaque(false);
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Shelter Management");
        titleLabel.setFont(font(Font.BOLD, 26));
        titleLabel.setForeground(TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel =
                new JLabel("Manage emergency shelters, capacity and occupancy");
        subtitleLabel.setFont(font(Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleGroup.add(titleLabel);
        titleGroup.add(Box.createVerticalStrut(6));
        titleGroup.add(subtitleLabel);

        header.add(titleGroup, BorderLayout.WEST);

        shelterCountLabel = new JLabel("0 Shelters");
        shelterCountLabel.setFont(font(Font.BOLD, 13));
        shelterCountLabel.setForeground(Color.WHITE);
        shelterCountLabel.setOpaque(false);

        BadgePanel badge = new BadgePanel(BLUE);
        badge.setLayout(new BorderLayout());
        badge.setBorder(new EmptyBorder(8, 16, 8, 16));
        badge.add(shelterCountLabel, BorderLayout.CENTER);

        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge);
        header.add(badgeWrap, BorderLayout.EAST);

        return header;
    }

    // =========================================================
    // CARD (rounded shadowed surface wrapping table + actions)
    // =========================================================

    private JComponent buildCard() {

        RoundedCard card = new RoundedCard(18, SURFACE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(8, 8, 8, 8));

        card.add(buildTable(), BorderLayout.CENTER);
        card.add(buildActionBar(), BorderLayout.SOUTH);

        return card;
    }

    // =========================================================
    // TABLE
    // =========================================================

    private JComponent buildTable() {

        String[] columns = {
                "Shelter ID", "Name", "Location",
                "Capacity", "Occupancy", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        shelterTable = new JTable(tableModel) {
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

        shelterTable.setRowHeight(40);
        shelterTable.setFont(font(Font.PLAIN, 13));
        shelterTable.setForeground(TEXT_SUBTLE);
        shelterTable.setBackground(SURFACE);
        shelterTable.setShowGrid(false);
        shelterTable.setIntercellSpacing(new Dimension(0, 0));
        shelterTable.setFillsViewportHeight(true);
        shelterTable.setSelectionBackground(SELECTION_BG);
        shelterTable.setSelectionForeground(TEXT);
        shelterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Default cell padding via a base renderer
        DefaultTableCellRenderer base = new DefaultTableCellRenderer();
        base.setBorder(new EmptyBorder(0, 16, 0, 16));

        // Header styling
        JTableHeader th = shelterTable.getTableHeader();
        th.setFont(font(Font.BOLD, 12));
        th.setBackground(HEADER_BG);
        th.setForeground(TEXT_MUTED);
        th.setPreferredSize(new Dimension(0, 44));
        th.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) th.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.LEFT);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_SOFT));

        // Column widths
        int[] widths = {90, 190, 150, 100, 110, 130};
        for (int i = 0; i < widths.length; i++) {
            shelterTable.getColumnModel().getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        // Left padding renderer for text columns 0-3
        DefaultTableCellRenderer padded = new DefaultTableCellRenderer();
        padded.setBorder(new EmptyBorder(0, 16, 0, 16));
        for (int i = 0; i < 4; i++) {
            shelterTable.getColumnModel().getColumn(i).setCellRenderer(padded);
        }

        // Custom renderers
        shelterTable.getColumnModel().getColumn(4)
                .setCellRenderer(new OccupancyRenderer());
        shelterTable.getColumnModel().getColumn(5)
                .setCellRenderer(new StatusPillRenderer());

        JScrollPane scrollPane = new JScrollPane(shelterTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.setBackground(SURFACE);

        return scrollPane;
    }

    // =========================================================
    // ACTION BAR
    // =========================================================

    private JComponent buildActionBar() {

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(14, 12, 10, 12));

        JButton addButton     = pillButton("Add Shelter", BLUE, BLUE_HOVER, true);
        JButton updateButton  = pillButton("Update Shelter", NEUTRAL, NEUTRAL_HOVER, true);
        JButton refreshButton = ghostButton("Refresh");
        JButton deleteButton  = pillButton("Delete Shelter", RED, RED_HOVER, true);

        bar.add(refreshButton);
        bar.add(updateButton);
        bar.add(deleteButton);
        bar.add(addButton);

        addButton.addActionListener(e -> showAddShelterDialog());
        updateButton.addActionListener(e -> showUpdateShelterDialog());
        refreshButton.addActionListener(e -> loadShelters());
        deleteButton.addActionListener(e -> deleteShelter());

        return bar;
    }

    // =========================================================
    // BUTTON FACTORIES
    // =========================================================

    private JButton pillButton(String text, Color base, Color hover, boolean lightText) {
        PillButton button = new PillButton(base, hover, 10);
        button.setText(text);
        button.setFont(font(Font.BOLD, 13));
        button.setForeground(lightText ? Color.WHITE : TEXT);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton ghostButton(String text) {
        PillButton button = new PillButton(SURFACE, new Color(0xF1, 0xF5, 0xF9), 10);
        button.setText(text);
        button.setFont(font(Font.BOLD, 13));
        button.setForeground(TEXT_SUBTLE);
        button.setOutline(BORDER_SOFT);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // =========================================================
    // LOAD SHELTERS
    // =========================================================

    private void loadShelters() {

        tableModel.setRowCount(0);

        List<Shelter> shelters = shelterDAO.getAllShelters();

        for (Shelter shelter : shelters) {
            tableModel.addRow(new Object[]{
                    shelter.getShelterId(),
                    shelter.getName(),
                    shelter.getLocation(),
                    shelter.getCapacity(),
                    shelter.getCurrentOccupancy(),
                    shelter.getStatus()
            });
        }

        shelterCountLabel.setText(shelters.size() + " Shelters");
    }

    // =========================================================
    // ADD SHELTER
    // =========================================================

    private void showAddShelterDialog() {

        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField capacityField = new JTextField();
        JTextField occupancyField = new JTextField("0");

        JPanel panel = createFormPanel();
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Capacity:"));
        panel.add(capacityField);
        panel.add(new JLabel("Current Occupancy:"));
        panel.add(occupancyField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Add Shelter",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            int capacity = Integer.parseInt(capacityField.getText().trim());
            int occupancy = Integer.parseInt(occupancyField.getText().trim());

            if (name.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all fields.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (capacity <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Capacity must be greater than 0.",
                        "Invalid Capacity", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (occupancy < 0 || occupancy > capacity) {
                JOptionPane.showMessageDialog(this,
                        "Occupancy must be between 0 and capacity.",
                        "Invalid Occupancy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String status = (occupancy >= capacity) ? "FULL" : "AVAILABLE";

            Shelter shelter = new Shelter();
            shelter.setName(name);
            shelter.setLocation(location);
            shelter.setCapacity(capacity);
            shelter.setCurrentOccupancy(occupancy);
            shelter.setStatus(status);

            shelterDAO.addShelter(shelter);

            JOptionPane.showMessageDialog(this,
                    "Shelter added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            loadShelters();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Capacity and occupancy must be numbers.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error adding shelter:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // UPDATE SHELTER
    // =========================================================

    private void showUpdateShelterDialog() {

        int selectedRow = shelterTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a shelter first.",
                    "No Shelter Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int shelterId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = tableModel.getValueAt(selectedRow, 1).toString();
        String currentLocation = tableModel.getValueAt(selectedRow, 2).toString();
        int currentCapacity = (int) tableModel.getValueAt(selectedRow, 3);
        int currentOccupancy = (int) tableModel.getValueAt(selectedRow, 4);

        JTextField nameField = new JTextField(currentName);
        JTextField locationField = new JTextField(currentLocation);
        JTextField capacityField = new JTextField(String.valueOf(currentCapacity));
        JTextField occupancyField = new JTextField(String.valueOf(currentOccupancy));

        JPanel panel = createFormPanel();
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Capacity:"));
        panel.add(capacityField);
        panel.add(new JLabel("Current Occupancy:"));
        panel.add(occupancyField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Update Shelter ID " + shelterId,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            int capacity = Integer.parseInt(capacityField.getText().trim());
            int occupancy = Integer.parseInt(occupancyField.getText().trim());

            if (name.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all fields.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (capacity <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Capacity must be greater than 0.",
                        "Invalid Capacity", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (occupancy < 0 || occupancy > capacity) {
                JOptionPane.showMessageDialog(this,
                        "Occupancy must be between 0 and capacity.",
                        "Invalid Occupancy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String status = (occupancy >= capacity) ? "FULL" : "AVAILABLE";

            Shelter shelter = new Shelter();
            shelter.setShelterId(shelterId);
            shelter.setName(name);
            shelter.setLocation(location);
            shelter.setCapacity(capacity);
            shelter.setCurrentOccupancy(occupancy);
            shelter.setStatus(status);

            shelterDAO.updateShelter(shelter);

            JOptionPane.showMessageDialog(this,
                    "Shelter updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            loadShelters();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Capacity and occupancy must be numbers.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error updating shelter:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // DELETE SHELTER
    // =========================================================

    private void deleteShelter() {

        int selectedRow = shelterTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a shelter first.",
                    "No Shelter Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int shelterId = (int) tableModel.getValueAt(selectedRow, 0);
        String shelterName = tableModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete\n"
                        + "Shelter ID: " + shelterId
                        + "\nName: " + shelterName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            shelterDAO.deleteShelter(shelterId);

            JOptionPane.showMessageDialog(this,
                    "Shelter deleted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            loadShelters();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting shelter:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // FORM PANEL
    // =========================================================

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 12, 12));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        return panel;
    }

    // =========================================================
    // OCCUPANCY RENDERER (percentage-based color, preserved logic)
    // =========================================================

    private static class OccupancyRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component component = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(new EmptyBorder(0, 16, 0, 16));
            setFont(font(Font.BOLD, 13));

            if (!isSelected && value != null) {
                int occupancy = Integer.parseInt(value.toString());
                int capacity = Integer.parseInt(
                        table.getValueAt(row, 3).toString());

                double percentage = (double) occupancy / capacity * 100;

                if (percentage >= 100) {
                    component.setForeground(RED);
                } else if (percentage >= 80) {
                    component.setForeground(ORANGE);
                } else {
                    component.setForeground(GREEN);
                }
            }

            return component;
        }
    }

    // =========================================================
    // STATUS PILL RENDERER
    // =========================================================

    private static class StatusPillRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            String status = value == null ? "" : value.toString().toUpperCase();

            Color bg;
            Color fg;
            if (status.equals("AVAILABLE")) {
                bg = GREEN_SOFT; fg = GREEN_TEXT;
            } else if (status.equals("FULL")) {
                bg = RED_SOFT; fg = RED_TEXT;
            } else {
                bg = ORANGE_SOFT; fg = ORANGE_TEXT;
            }

            PillCell pill = new PillCell(
                    value == null ? "" : value.toString(), bg, fg);
            pill.setOpaque(true);
            pill.setBackground(isSelected ? SELECTION_BG
                    : (row % 2 == 0 ? SURFACE : ROW_ALT));
            return pill;
        }
    }

    // =========================================================
    // PILL CELL (rounded colored chip inside a table cell)
    // =========================================================

    private static class PillCell extends JPanel {
        private final String text;
        private final Color pillBg;
        private final Color pillFg;

        PillCell(String text, Color pillBg, Color pillFg) {
            this.text = text;
            this.pillBg = pillBg;
            this.pillFg = pillFg;
            setBorder(new EmptyBorder(0, 12, 0, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (text == null || text.isEmpty()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(font(Font.BOLD, 11));

            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(text);
            int padX = 12;
            int pillW = textW + padX * 2;
            int pillH = 22;
            int x = 12;
            int y = (getHeight() - pillH) / 2;

            g2.setColor(pillBg);
            g2.fill(new RoundRectangle2D.Float(x, y, pillW, pillH, pillH, pillH));

            g2.setColor(pillFg);
            int ty = y + (pillH - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, x + padX, ty);

            g2.dispose();
        }
    }

    // =========================================================
    // ROUNDED CARD (soft layered drop shadow + rounded surface)
    // =========================================================

    private static class RoundedCard extends JPanel {
        private final int arc;
        private final Color fill;

        RoundedCard(int arc, Color fill) {
            this.arc = arc;
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int shadowLayers = 8;
            int w = getWidth();
            int h = getHeight();

            for (int i = shadowLayers; i > 0; i--) {
                float alpha = 0.03f * (shadowLayers - i + 1) / shadowLayers;
                g2.setColor(new Color(0f, 0f, 0f, alpha));
                g2.fill(new RoundRectangle2D.Float(
                        i, i + 2, w - i * 2, h - i * 2 - 2, arc, arc));
            }

            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(
                    4, 4, w - 8, h - 10, arc, arc));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================
    // BADGE PANEL (rounded solid chip)
    // =========================================================

    private static class BadgePanel extends JPanel {
        private final Color fill;

        BadgePanel(Color fill) {
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(),
                    getHeight(), getHeight()));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================
    // PILL BUTTON (rounded, hover-aware)
    // =========================================================

    private static class PillButton extends JButton {
        private final Color base;
        private final Color hover;
        private final int arc;
        private Color outline;
        private boolean hovering;

        PillButton(Color base, Color hover, int arc) {
            this.base = base;
            this.hover = hover;
            this.arc = arc;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    hovering = true;
                    repaint();
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    hovering = false;
                    repaint();
                }
            });
        }

        void setOutline(Color outline) {
            this.outline = outline;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(hovering ? hover : base);
            g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), arc, arc));

            if (outline != null) {
                g2.setColor(outline);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(
                        0.5f, 0.5f, getWidth() - 1, getHeight() - 1, arc, arc));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}