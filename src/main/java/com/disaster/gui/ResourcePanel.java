package com.disaster.gui;

import com.disaster.dao.ResourceDAO;
import com.disaster.model.Resource;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class ResourcePanel extends JPanel {

    // =========================================================
    // SHARED DESIGN SYSTEM
    // =========================================================

    // Neutrals (slate)
    private static final Color BACKGROUND    = new Color(0xF1, 0xF5, 0xF9); // slate-100
    private static final Color SURFACE       = new Color(0xFF, 0xFF, 0xFF); // white
    private static final Color BORDER_SOFT   = new Color(0xE2, 0xE8, 0xF0); // slate-200
    private static final Color ROW_ALT       = new Color(0xF8, 0xFA, 0xFC); // slate-50
    private static final Color TEXT_STRONG   = new Color(0x0F, 0x17, 0x2A); // slate-900
    private static final Color TEXT_BODY     = new Color(0x33, 0x41, 0x55); // slate-700
    private static final Color TEXT_MUTED    = new Color(0x64, 0x74, 0x8B); // slate-500

    // Accent colors
    private static final Color BLUE          = new Color(0x25, 0x63, 0xEB); // blue-600
    private static final Color BLUE_HOVER    = new Color(0x1D, 0x4E, 0xD8); // blue-700
    private static final Color GREEN         = new Color(0x16, 0xA3, 0x4A); // green-600
    private static final Color ORANGE        = new Color(0xD9, 0x77, 0x06); // amber-600
    private static final Color RED           = new Color(0xDC, 0x26, 0x26); // red-600
    private static final Color RED_HOVER     = new Color(0xB9, 0x1C, 0x1C); // red-700
    private static final Color NEUTRAL_BTN   = new Color(0x47, 0x55, 0x69); // slate-600
    private static final Color NEUTRAL_HOVER = new Color(0x33, 0x41, 0x55); // slate-700

    // Soft pill backgrounds
    private static final Color GREEN_SOFT    = new Color(0xDC, 0xFC, 0xE7); // green-100
    private static final Color GREEN_TEXT    = new Color(0x15, 0x80, 0x3D); // green-700
    private static final Color ORANGE_SOFT   = new Color(0xFE, 0xF3, 0xC7); // amber-100
    private static final Color ORANGE_TEXT   = new Color(0xB4, 0x53, 0x09); // amber-700
    private static final Color RED_SOFT      = new Color(0xFE, 0xE2, 0xE2); // red-100
    private static final Color RED_TEXT      = new Color(0xB9, 0x1C, 0x1C); // red-700
    private static final Color SELECTION     = new Color(0xDB, 0xEA, 0xFE); // blue-100

    // Modern font stack with runtime fallback
    private static final String FONT_FAMILY = resolveFontFamily();

    private static String resolveFontFamily() {
        String[] preferred = {
                "Segoe UI", "SF Pro Text", "Helvetica Neue", "Roboto", "Inter", "Arial"
        };
        java.util.Set<String> available = new java.util.HashSet<>(java.util.Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames()));
        for (String f : preferred) {
            if (available.contains(f)) return f;
        }
        return Font.SANS_SERIF;
    }

    private static Font font(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    // =========================================================
    // COMPONENTS
    // =========================================================

    private JTable resourceTable;
    private DefaultTableModel tableModel;
    private JLabel resourceCountLabel;

    private final ResourceDAO resourceDAO;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ResourcePanel() {

        resourceDAO = new ResourceDAO();

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCard(), BorderLayout.CENTER);

        loadResources();
    }

    // =========================================================
    // HEADER (title + subtitle + count badge)
    // =========================================================

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 2, 20, 2));

        JPanel titleCol = new JPanel();
        titleCol.setOpaque(false);
        titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Resource Management");
        title.setFont(font(Font.BOLD, 26));
        title.setForeground(TEXT_STRONG);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Manage emergency supplies and resource availability");
        subtitle.setFont(font(Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleCol.add(title);
        titleCol.add(Box.createVerticalStrut(6));
        titleCol.add(subtitle);

        resourceCountLabel = new JLabel("0 Resources");
        resourceCountLabel.setFont(font(Font.BOLD, 13));
        resourceCountLabel.setForeground(BLUE);

        Badge badge = new Badge();
        badge.setLayout(new BorderLayout());
        badge.add(resourceCountLabel, BorderLayout.CENTER);

        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge);

        header.add(titleCol, BorderLayout.WEST);
        header.add(badgeWrap, BorderLayout.EAST);
        return header;
    }

    // =========================================================
    // CARD (rounded shadowed surface: table + action bar)
    // =========================================================

    private JComponent buildCard() {
        RoundedCard card = new RoundedCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 16, 20));

        card.add(buildTable(), BorderLayout.CENTER);
        card.add(buildActionBar(), BorderLayout.SOUTH);
        return card;
    }

    private JComponent buildTable() {
        String[] columns = { "Resource ID", "Type", "Name", "Quantity", "Location", "Status" };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resourceTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? SURFACE : ROW_ALT);
                }
                return c;
            }
        };

        resourceTable.setRowHeight(40);
        resourceTable.setFont(font(Font.PLAIN, 13));
        resourceTable.setForeground(TEXT_BODY);
        resourceTable.setBackground(SURFACE);
        resourceTable.setShowGrid(false);
        resourceTable.setIntercellSpacing(new Dimension(0, 0));
        resourceTable.setFillsViewportHeight(true);
        resourceTable.setSelectionBackground(SELECTION);
        resourceTable.setSelectionForeground(TEXT_STRONG);
        resourceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Default cell padding renderer for text columns
        DefaultTableCellRenderer pad = new DefaultTableCellRenderer();
        pad.setBorder(new EmptyBorder(0, 14, 0, 14));

        // Header styling
        JTableHeader th = resourceTable.getTableHeader();
        th.setFont(font(Font.BOLD, 12));
        th.setBackground(SURFACE);
        th.setForeground(TEXT_MUTED);
        th.setPreferredSize(new Dimension(0, 42));
        th.setReorderingAllowed(false);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_SOFT));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
        th.setDefaultRenderer(new HeaderRenderer());

        // Column widths
        int[] widths = { 95, 110, 180, 100, 180, 130 };
        for (int i = 0; i < widths.length; i++) {
            resourceTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Text renderer with left padding for columns 0,1,2,4
        DefaultTableCellRenderer textRenderer = new DefaultTableCellRenderer();
        textRenderer.setBorder(new EmptyBorder(0, 14, 0, 14));
        for (int i : new int[]{0, 1, 2, 4}) {
            resourceTable.getColumnModel().getColumn(i).setCellRenderer(textRenderer);
        }

        // Custom renderers
        resourceTable.getColumnModel().getColumn(3).setCellRenderer(new QuantityRenderer());
        resourceTable.getColumnModel().getColumn(5).setCellRenderer(new PillCell());

        JScrollPane scroll = new JScrollPane(resourceTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(SURFACE);
        scroll.setOpaque(false);
        return scroll;
    }

    private JComponent buildActionBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(16, 0, 4, 0));

        JButton add = pillButton("Add Resource", BLUE, BLUE_HOVER, Color.WHITE);
        JButton update = pillButton("Update Resource", SURFACE, BORDER_SOFT, TEXT_BODY);
        JButton refresh = pillButton("Refresh", SURFACE, BORDER_SOFT, TEXT_BODY);
        JButton delete = pillButton("Delete Resource", RED, RED_HOVER, Color.WHITE);

        add.addActionListener(e -> showAddResourceDialog());
        update.addActionListener(e -> showUpdateResourceDialog());
        refresh.addActionListener(e -> loadResources());
        delete.addActionListener(e -> deleteResource());

        bar.add(update);
        bar.add(refresh);
        bar.add(add);
        bar.add(delete);
        return bar;
    }

    // =========================================================
    // ROUNDED, HOVER-AWARE BUTTON
    // =========================================================

    private JButton pillButton(String text, Color base, Color hover, Color fg) {
        final boolean ghost = base.equals(SURFACE);
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hovered = getModel().isRollover();
                int arc = 10;
                if (ghost) {
                    g2.setColor(hovered ? ROW_ALT : SURFACE);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
                    g2.setColor(hovered ? NEUTRAL_BTN : hover); // hover param carries border color for ghost
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
                } else {
                    g2.setColor(hovered ? hover : base);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(font(Font.BOLD, 13));
        button.setForeground(fg);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // =========================================================
    // LOAD RESOURCES
    // =========================================================

    private void loadResources() {

        tableModel.setRowCount(0);

        List<Resource> resources = resourceDAO.getAllResources();

        for (Resource resource : resources) {
            tableModel.addRow(new Object[]{
                    resource.getResourceId(),
                    resource.getResourceType(),
                    resource.getName(),
                    resource.getQuantity(),
                    resource.getLocation(),
                    resource.getStatus()
            });
        }

        resourceCountLabel.setText(resources.size() + " Resources");
    }

    // =========================================================
    // ADD RESOURCE
    // =========================================================

    private void showAddResourceDialog() {

        JTextField typeField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField locationField = new JTextField();

        JPanel panel = createFormPanel();
        panel.add(formLabel("Resource Type:"));
        panel.add(typeField);
        panel.add(formLabel("Name:"));
        panel.add(nameField);
        panel.add(formLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(formLabel("Location:"));
        panel.add(locationField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Add Resource",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String type = typeField.getText().trim();
            String name = nameField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            String location = locationField.getText().trim();

            // VALIDATION
            if (type.isEmpty() || name.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all fields.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (quantity < 0) {
                JOptionPane.showMessageDialog(this,
                        "Quantity cannot be negative.",
                        "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // AUTOMATIC STATUS
            String status = (quantity == 0) ? "OUT_OF_STOCK" : "AVAILABLE";

            // CREATE RESOURCE
            Resource resource = new Resource();
            resource.setResourceType(type);
            resource.setName(name);
            resource.setQuantity(quantity);
            resource.setLocation(location);
            resource.setStatus(status);

            resourceDAO.addResource(resource);

            JOptionPane.showMessageDialog(this,
                    "Resource added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            loadResources();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Quantity must be a number.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error adding resource:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // UPDATE RESOURCE
    // =========================================================

    private void showUpdateResourceDialog() {

        int selectedRow = resourceTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a resource first.",
                    "No Resource Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resourceId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentType = tableModel.getValueAt(selectedRow, 1).toString();
        String currentName = tableModel.getValueAt(selectedRow, 2).toString();
        int currentQuantity = (int) tableModel.getValueAt(selectedRow, 3);
        String currentLocation = tableModel.getValueAt(selectedRow, 4).toString();

        JTextField typeField = new JTextField(currentType);
        JTextField nameField = new JTextField(currentName);
        JTextField quantityField = new JTextField(String.valueOf(currentQuantity));
        JTextField locationField = new JTextField(currentLocation);

        JPanel panel = createFormPanel();
        panel.add(formLabel("Resource Type:"));
        panel.add(typeField);
        panel.add(formLabel("Name:"));
        panel.add(nameField);
        panel.add(formLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(formLabel("Location:"));
        panel.add(locationField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Update Resource ID " + resourceId,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String type = typeField.getText().trim();
            String name = nameField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            String location = locationField.getText().trim();

            // VALIDATION
            if (type.isEmpty() || name.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all fields.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (quantity < 0) {
                JOptionPane.showMessageDialog(this,
                        "Quantity cannot be negative.",
                        "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // AUTOMATIC STATUS
            String status = (quantity == 0) ? "OUT_OF_STOCK" : "AVAILABLE";

            // CREATE UPDATED RESOURCE
            Resource resource = new Resource();
            resource.setResourceId(resourceId);
            resource.setResourceType(type);
            resource.setName(name);
            resource.setQuantity(quantity);
            resource.setLocation(location);
            resource.setStatus(status);

            resourceDAO.updateResource(resource);

            JOptionPane.showMessageDialog(this,
                    "Resource updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            loadResources();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Quantity must be a number.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error updating resource:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // DELETE RESOURCE
    // =========================================================

    private void deleteResource() {

        int selectedRow = resourceTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a resource first.",
                    "No Resource Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resourceId = (int) tableModel.getValueAt(selectedRow, 0);
        String resourceName = tableModel.getValueAt(selectedRow, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete\n"
                        + "Resource ID: " + resourceId
                        + "\nName: " + resourceName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            resourceDAO.deleteResource(resourceId);

            JOptionPane.showMessageDialog(this,
                    "Resource deleted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            loadResources();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting resource:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // FORM HELPERS
    // =========================================================

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 12, 12));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        return panel;
    }

    private JLabel formLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font(Font.PLAIN, 13));
        label.setForeground(TEXT_BODY);
        return label;
    }

    // =========================================================
    // ROUNDED CARD WITH SOFT SHADOW
    // =========================================================

    private static class RoundedCard extends JPanel {
        RoundedCard() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 18;
            int shadowSize = 10;

            // Soft layered shadow
            for (int i = shadowSize; i > 0; i--) {
                float alpha = 0.04f * (shadowSize - i + 1) / shadowSize;
                g2.setColor(new Color(0f, 0f, 0f, alpha));
                g2.fill(new RoundRectangle2D.Float(
                        i, i + 2,
                        getWidth() - (i * 2), getHeight() - (i * 2),
                        arc, arc));
            }

            // Surface
            g2.setColor(SURFACE);
            g2.fill(new RoundRectangle2D.Float(
                    shadowSize, shadowSize,
                    getWidth() - shadowSize * 2, getHeight() - shadowSize * 2 - 2,
                    arc, arc));

            // Hairline border
            g2.setColor(BORDER_SOFT);
            g2.draw(new RoundRectangle2D.Float(
                    shadowSize, shadowSize,
                    getWidth() - shadowSize * 2, getHeight() - shadowSize * 2 - 2,
                    arc, arc));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================
    // ROUNDED COUNT BADGE
    // =========================================================

    private static class Badge extends JPanel {
        Badge() {
            setOpaque(false);
            setBorder(new EmptyBorder(8, 16, 8, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0xDB, 0xEA, 0xFE)); // blue-100
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================
    // HEADER RENDERER (left-aligned, padded)
    // =========================================================

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        HeaderRenderer() {
            setBorder(new EmptyBorder(0, 14, 0, 14));
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(font(Font.BOLD, 12));
            setForeground(TEXT_MUTED);
            setBackground(SURFACE);
            if (column == 3) setHorizontalAlignment(SwingConstants.CENTER);
            else setHorizontalAlignment(SwingConstants.LEFT);
            return this;
        }
    }

    // =========================================================
    // QUANTITY RENDERER (colored, bold, low-stock semantics)
    // =========================================================

    private static class QuantityRenderer extends DefaultTableCellRenderer {

        QuantityRenderer() {
            setBorder(new EmptyBorder(0, 14, 0, 14));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component component = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(font(Font.BOLD, 13));

            if (!isSelected && value != null) {
                int quantity = Integer.parseInt(value.toString());
                if (quantity == 0) {
                    component.setForeground(RED);
                } else if (quantity <= 10) {
                    component.setForeground(ORANGE);
                } else {
                    component.setForeground(GREEN);
                }
            } else {
                component.setForeground(TEXT_STRONG);
            }

            return component;
        }
    }

    // =========================================================
    // STATUS PILL RENDERER
    // =========================================================

    private static class PillCell extends DefaultTableCellRenderer {

        private String text = "";
        private Color pillBg = GREEN_SOFT;
        private Color pillFg = GREEN_TEXT;
        private boolean selected = false;

        PillCell() {
            setOpaque(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            this.selected = isSelected;
            this.text = value == null ? "" : value.toString();

            String status = text.toUpperCase();
            if (status.equals("AVAILABLE")) {
                pillBg = GREEN_SOFT; pillFg = GREEN_TEXT;
            } else if (status.equals("OUT_OF_STOCK")) {
                pillBg = RED_SOFT; pillFg = RED_TEXT;
            } else {
                pillBg = ORANGE_SOFT; pillFg = ORANGE_TEXT;
            }

            setBackground(isSelected ? SELECTION : (row % 2 == 0 ? SURFACE : ROW_ALT));
            setOpaque(true);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            String display = text.replace('_', ' ');
            Font f = font(Font.BOLD, 11);
            g2.setFont(f);
            FontMetrics fm = g2.getFontMetrics();

            int textW = fm.stringWidth(display);
            int padX = 12;
            int pillW = textW + padX * 2;
            int pillH = 22;
            int x = 14; // left aligned within padded cell
            int y = (getHeight() - pillH) / 2;

            g2.setColor(pillBg);
            g2.fillRoundRect(x, y, pillW, pillH, pillH, pillH);

            g2.setColor(pillFg);
            int textY = y + (pillH - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(display, x + padX, textY);

            g2.dispose();
        }
    }
}