package com.disaster.gui;

import com.disaster.dao.DisasterDAO;
import com.disaster.model.Disaster;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class DisasterPanel extends JPanel {

    private static final Color BG = new Color(0x0B, 0x18, 0x26);
    private static final Color PANEL = new Color(0x12, 0x29, 0x3D);
    private static final Color PANEL2 = new Color(0x16, 0x30, 0x47);
    private static final Color LINE = new Color(0x2A, 0x4B, 0x65);
    private static final Color TEXT = new Color(0xF3, 0xF7, 0xFB);
    private static final Color MUTED = new Color(0x9B, 0xAF, 0xC0);
    private static final Color BLUE = new Color(0x4D, 0xB8, 0xFF);
    private static final Color BLUE_DARK = new Color(0x1D, 0x5F, 0x8E);
    private static final Color RED = new Color(0xFF, 0x5B, 0x66);
    private static final Color AMBER = new Color(0xFF, 0xB4, 0x3D);
    private static final Color GREEN = new Color(0x39, 0xD6, 0x9A);
    private static final Color VIOLET = new Color(0xA5, 0x8B, 0xF5);
    private static final Color BROWN = new Color(0xD0, 0x8B, 0x5B);
    private static final Color SEL = new Color(0x1E, 0x46, 0x63);
    private static final Color HOVER = new Color(0x17, 0x35, 0x4D);

    private static final Color RED_SOFT = new Color(0x52, 0x24, 0x2A);
    private static final Color AMBER_SOFT = new Color(0x54, 0x3B, 0x1C);
    private static final Color GREEN_SOFT = new Color(0x18, 0x4A, 0x3A);

    private static final String FONT_FAMILY = resolveFontFamily();

    private static String resolveFontFamily() {
        String[] preferred = {"Segoe UI", "SF Pro Text", "Helvetica Neue", "Roboto", "Inter", "Arial"};
        java.util.Set<String> available = new java.util.HashSet<>(
                java.util.Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String f : preferred) if (available.contains(f)) return f;
        return Font.SANS_SERIF;
    }

    private static Font font(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    private static void aa(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private static Color typeColor(String type) {
        if (type == null) return MUTED;
        String s = type.toLowerCase();
        if (s.contains("flood") || s.contains("tsunami")) return BLUE;
        if (s.contains("earthquake") || s.contains("quake")) return AMBER;
        if (s.contains("fire")) return RED;
        if (s.contains("cyclone") || s.contains("storm") || s.contains("hurricane") || s.contains("typhoon")) return VIOLET;
        if (s.contains("landslide")) return BROWN;
        return MUTED;
    }

    private static Color statusColor(String status) {
        if (status == null) return MUTED;
        switch (status.toUpperCase()) {
            case "ACTIVE": return RED;
            case "MONITORING": return AMBER;
            case "RESOLVED":
            case "CLOSED": return GREEN;
            default: return MUTED;
        }
    }

    private static Color levelColor(int level) {
        if (level >= 5) return RED;
        if (level == 4) return new Color(0xFF, 0x8A, 0x3D);
        if (level == 3) return AMBER;
        if (level == 2) return new Color(0x9A, 0xCD, 0x4B);
        return GREEN;
    }

    private JTable disasterTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private SegmentedFilter statusFilter;
    private JLabel disasterCountLabel;
    private JLabel activeCountLabel;
    private JLabel monitoringCountLabel;
    private JLabel resolvedCountLabel;
    private JLabel infoLabel;
    private JButton updateButton;
    private JButton deleteButton;
    private final DisasterDAO disasterDAO;

    public DisasterPanel() {
        disasterDAO = new DisasterDAO();
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(26, 32, 22, 32));

        add(buildTop(), BorderLayout.NORTH);
        add(buildMainCard(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        loadDisasters();
    }

    private JPanel buildTop() {
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Disaster Management");
        title.setFont(font(Font.BOLD, 28));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Manage active disasters and emergency incident information");
        subtitle.setFont(font(Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        titles.add(title);
        titles.add(Box.createVerticalStrut(4));
        titles.add(subtitle);

        disasterCountLabel = new JLabel("0 disasters");
        disasterCountLabel.setFont(font(Font.BOLD, 12));
        disasterCountLabel.setForeground(BLUE);
        disasterCountLabel.setBorder(new EmptyBorder(7, 13, 7, 13));

        JPanel countChip = new RoundedPanel(20, new Color(0x13, 0x2F, 0x49));
        countChip.setLayout(new BorderLayout());
        countChip.add(disasterCountLabel, BorderLayout.CENTER);

        JPanel chipWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        chipWrap.setOpaque(false);
        chipWrap.add(countChip);

        header.add(titles, BorderLayout.CENTER);
        header.add(chipWrap, BorderLayout.EAST);

        top.add(header);
        top.add(Box.createVerticalStrut(20));

        JPanel toolbar = new JPanel(new BorderLayout(16, 0));
        toolbar.setOpaque(false);

        searchField = new Field("Search type, location or description", PANEL2);
        searchField.setPreferredSize(new Dimension(340, 40));
        toolbar.add(searchField, BorderLayout.WEST);

        statusFilter = new SegmentedFilter();
        toolbar.add(statusFilter, BorderLayout.EAST);

        top.add(toolbar);
        top.add(Box.createVerticalStrut(16));

        return top;
    }

    private JPanel buildMainCard() {
        RoundedPanel card = new RoundedPanel(12, PANEL);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(6, 6, 6, 6));

        buildTable();

        JScrollPane scroll = new JScrollPane(
                disasterTable,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(PANEL);
        scroll.setBackground(PANEL);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private void buildTable() {
        String[] columns = {"ID", "Type", "Location", "Severity", "Description", "Status"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int column) {
                return (column == 0 || column == 3) ? Integer.class : String.class;
            }
        };

        disasterTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        disasterTable.setRowSorter(sorter);

        disasterTable.setRowHeight(54);
        disasterTable.setShowGrid(false);
        disasterTable.setIntercellSpacing(new Dimension(0, 0));
        disasterTable.setFillsViewportHeight(true);
        disasterTable.setBackground(PANEL);
        disasterTable.setForeground(TEXT);
        disasterTable.setSelectionBackground(SEL);
        disasterTable.setSelectionForeground(TEXT);
        disasterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        disasterTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        disasterTable.setFont(font(Font.PLAIN, 14));
        disasterTable.setFocusable(true);

        int[] widths = {70, 170, 180, 120, 350, 160};
        for (int i = 0; i < widths.length; i++) {
            TableColumn col = disasterTable.getColumnModel().getColumn(i);
            col.setPreferredWidth(widths[i]);
            col.setCellRenderer(new DisasterCellRenderer());
        }

        JTableHeader header = disasterTable.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setPreferredSize(new Dimension(0, 42));
        header.setBackground(PANEL);
        header.setForeground(MUTED);
        header.setDefaultRenderer(new HeaderRenderer());
        header.setBorder(BorderFactory.createEmptyBorder());

        disasterTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int row = disasterTable.rowAtPoint(e.getPoint());
                Integer old = (Integer) disasterTable.getClientProperty("hoverRow");
                if (old == null || old != row) {
                    disasterTable.putClientProperty("hoverRow", row);
                    disasterTable.repaint();
                }
            }
        });

        disasterTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) {
                disasterTable.putClientProperty("hoverRow", -1);
                disasterTable.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && disasterTable.rowAtPoint(e.getPoint()) >= 0) {
                    showUpdateDisasterDialog();
                }
            }
        });

        disasterTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtons();
        });

        disasterTable.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteDisaster");
        disasterTable.getActionMap().put("deleteDisaster", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { deleteDisaster(); }
        });

        disasterTable.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "updateDisaster");
        disasterTable.getActionMap().put("updateDisaster", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { showUpdateDisasterDialog(); }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });

        searchField.registerKeyboardAction(
                e -> searchField.setText(""),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED);
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 0, 0, 0));

        infoLabel = new JLabel("Showing 0 of 0 disasters");
        infoLabel.setFont(font(Font.PLAIN, 13));
        infoLabel.setForeground(MUTED);
        footer.add(infoLabel, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton refresh = createButton("Refresh", PANEL2, TEXT, false);
        JButton add = createButton("Add disaster", BLUE, new Color(0x06, 0x22, 0x3A), false);
        updateButton = createButton("Update disaster", PANEL2, TEXT, false);
        deleteButton = createButton("Delete disaster", BG, RED, true);

        actions.add(refresh);
        actions.add(add);
        actions.add(updateButton);
        actions.add(deleteButton);

        refresh.addActionListener(e -> loadDisasters());
        add.addActionListener(e -> showAddDisasterDialog());
        updateButton.addActionListener(e -> showUpdateDisasterDialog());
        deleteButton.addActionListener(e -> deleteDisaster());

        footer.add(actions, BorderLayout.EAST);
        return footer;
    }

    private JButton createButton(String text, Color fill, Color fg, boolean outline) {
        JButton button = new JButton(text) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                aa(g);
                boolean hover = getModel().isRollover();
                Color base = hover ? (outline ? new Color(0x35,0x1F,0x25) : new Color(
                        Math.min(255, fill.getRed() + 18),
                        Math.min(255, fill.getGreen() + 18),
                        Math.min(255, fill.getBlue() + 18))) : fill;
                g.setColor(base);
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 9, 9);
                if (outline) {
                    g.setColor(RED);
                    g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 9, 9);
                }
                g.dispose();
                super.paintComponent(g0);
            }
        };
        button.setFont(font(Font.BOLD, 12));
        button.setForeground(fg);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setEnabled(true);
        return button;
    }

    private void loadDisasters() {
        tableModel.setRowCount(0);
        List<Disaster> disasters = disasterDAO.getAllDisasters();

        int active = 0, monitoring = 0, resolved = 0;
        int critical = 0;

        for (Disaster d : disasters) {
            tableModel.addRow(new Object[]{
                    d.getDisasterId(), d.getType(), d.getLocation(),
                    d.getSeverity(), d.getDescription(), d.getStatus()
            });

            String status = d.getStatus() == null ? "" : d.getStatus().toUpperCase();
            if ("ACTIVE".equals(status)) active++;
            else if ("MONITORING".equals(status)) monitoring++;
            else if ("RESOLVED".equals(status)) resolved++;
            if (d.getSeverity() >= 5) critical++;
        }

        disasterCountLabel.setText(disasters.size() + (disasters.size() == 1 ? " disaster" : " disasters"));
        activeCountLabel = null;
        monitoringCountLabel = null;
        resolvedCountLabel = null;

        statusFilter.setLabels(new String[]{
                "All " + disasters.size(),
                "Active " + active,
                "Monitoring " + monitoring,
                "Resolved " + resolved
        });

        applyFilter();
    }

    private void applyFilter() {
        if (sorter == null) return;
        final String query = searchField.getText().trim().toLowerCase();
        final int selected = statusFilter.getSelected();

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                int row = entry.getIdentifier();
                String type = String.valueOf(tableModel.getValueAt(row, 1));
                String location = String.valueOf(tableModel.getValueAt(row, 2));
                String description = String.valueOf(tableModel.getValueAt(row, 4));
                String status = String.valueOf(tableModel.getValueAt(row, 5)).toUpperCase();

                boolean searchMatch = query.isEmpty()
                        || type.toLowerCase().contains(query)
                        || location.toLowerCase().contains(query)
                        || description.toLowerCase().contains(query);

                String wanted = selected == 1 ? "ACTIVE" : selected == 2 ? "MONITORING" : selected == 3 ? "RESOLVED" : "";
                boolean statusMatch = wanted.isEmpty() || status.equals(wanted);
                return searchMatch && statusMatch;
            }
        });

        infoLabel.setText("Showing " + disasterTable.getRowCount() + " of " + tableModel.getRowCount()
                + (tableModel.getRowCount() == 1 ? " disaster" : " disasters"));
        updateButtons();
    }

    private void updateButtons() {
        boolean selected = disasterTable != null && disasterTable.getSelectedRow() >= 0;
        if (updateButton != null) updateButton.setEnabled(selected);
        if (deleteButton != null) deleteButton.setEnabled(selected);
    }

    private void showAddDisasterDialog() {
        final JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Add Disaster",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setUndecorated(true);

        RoundedPanel root = new RoundedPanel(18, PANEL);
        root.setLayout(new BorderLayout());
        root.setBorder(new EmptyBorder(24, 28, 22, 28));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Add disaster");
        title.setFont(font(Font.BOLD, 23));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter the details of the new disaster incident");
        subtitle.setFont(font(Font.PLAIN, 12));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(3));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(17));

        content.add(formLabel("Type"));
        content.add(Box.createVerticalStrut(6));

        Field typeField = new Field("", PANEL2);
        typeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        typeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 41));
        content.add(typeField);

        content.add(Box.createVerticalStrut(8));
        JPanel typePills = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
        typePills.setOpaque(false);
        typePills.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] types = {"Flood", "Earthquake", "Cyclone", "Fire", "Landslide"};
        for (String type : types) {
            JButton pill = createDialogPill(type);
            pill.addActionListener(e -> typeField.setText(type));
            typePills.add(pill);
        }
        content.add(typePills);

        content.add(Box.createVerticalStrut(14));
        content.add(formLabel("Location"));
        content.add(Box.createVerticalStrut(6));

        Field locationField = new Field("", PANEL2);
        locationField.setAlignmentX(Component.LEFT_ALIGNMENT);
        locationField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 41));
        content.add(locationField);

        content.add(Box.createVerticalStrut(14));
        content.add(formLabel("Severity"));
        content.add(Box.createVerticalStrut(6));

        SeverityPicker severityPicker = new SeverityPicker(1);
        severityPicker.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(severityPicker);

        content.add(Box.createVerticalStrut(14));
        content.add(formLabel("Status"));
        content.add(Box.createVerticalStrut(6));

        StatusPicker statusPicker = new StatusPicker("ACTIVE");
        statusPicker.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(statusPicker);

        content.add(Box.createVerticalStrut(14));
        content.add(formLabel("Description"));
        content.add(Box.createVerticalStrut(6));

        JTextArea descriptionField = new JTextArea();
        descriptionField.setFont(font(Font.PLAIN, 14));
        descriptionField.setForeground(TEXT);
        descriptionField.setCaretColor(TEXT);
        descriptionField.setBackground(PANEL2);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        descriptionField.setBorder(new EmptyBorder(10, 12, 10, 12));

        RoundedPanel descriptionBox = new RoundedPanel(10, PANEL2);
        descriptionBox.setLayout(new BorderLayout());
        descriptionBox.setBorder(new EmptyBorder(1, 1, 1, 1));
        descriptionBox.add(descriptionField, BorderLayout.CENTER);
        descriptionBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionBox.setPreferredSize(new Dimension(420, 88));
        descriptionBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));
        content.add(descriptionBox);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.setBorder(new EmptyBorder(18, 0, 0, 0));

        JButton cancel = createDialogGhostButton("Cancel");
        JButton addButton = createDialogPrimaryButton("Add disaster");

        buttons.add(cancel);
        buttons.add(addButton);

        cancel.addActionListener(e -> dialog.dispose());

        addButton.addActionListener(e -> {
            String type = typeField.getText().trim();
            String location = locationField.getText().trim();
            String description = descriptionField.getText().trim();
            int severity = severityPicker.getValue();
            String status = statusPicker.getValue();

            if (type.isEmpty() || location.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Please fill all fields.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                Disaster d = new Disaster();
                d.setType(type);
                d.setLocation(location);
                d.setSeverity(severity);
                d.setDescription(description);
                d.setStatus(status);

                disasterDAO.addDisaster(d);
                dialog.dispose();

                JOptionPane.showMessageDialog(
                        this,
                        "Disaster added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadDisasters();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Error adding disaster:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        root.add(content, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.setSize(480, 620);
        dialog.setMinimumSize(new Dimension(480, 620));
        dialog.setLocationRelativeTo(this);
        dialog.getRootPane().setDefaultButton(addButton);
        dialog.setVisible(true);
    }

    private void showUpdateDisasterDialog() {
        int selectedRow = disasterTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a disaster first.",
                    "No Disaster Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = disasterTable.convertRowIndexToModel(selectedRow);
        int disasterId = (int) tableModel.getValueAt(modelRow, 0);
        String currentType = String.valueOf(tableModel.getValueAt(modelRow, 1));
        String currentLocation = String.valueOf(tableModel.getValueAt(modelRow, 2));
        int currentSeverity = (int) tableModel.getValueAt(modelRow, 3);
        String currentDescription = String.valueOf(tableModel.getValueAt(modelRow, 4));
        String currentStatus = String.valueOf(tableModel.getValueAt(modelRow, 5)).toUpperCase();

        final JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Update Disaster",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setUndecorated(true);

        RoundedPanel root = new RoundedPanel(18, PANEL);
        root.setLayout(new BorderLayout());
        root.setBorder(new EmptyBorder(24, 28, 22, 28));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Update disaster");
        title.setFont(font(Font.BOLD, 23));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Edit the details of incident #" + disasterId);
        subtitle.setFont(font(Font.PLAIN, 12));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(3));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(17));

        JLabel typeLabel = formLabel("Type");
        content.add(typeLabel);
        content.add(Box.createVerticalStrut(6));

        Field typeField = new Field("", PANEL2);
        typeField.setText(currentType);
        typeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        typeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 41));
        content.add(typeField);

        content.add(Box.createVerticalStrut(8));
        JPanel typePills = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
        typePills.setOpaque(false);
        typePills.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] types = {"Flood", "Earthquake", "Cyclone", "Fire", "Landslide"};
        for (String type : types) {
            JButton pill = createDialogPill(type);
            pill.addActionListener(e -> typeField.setText(type));
            typePills.add(pill);
        }
        content.add(typePills);

        content.add(Box.createVerticalStrut(14));
        content.add(formLabel("Location"));
        content.add(Box.createVerticalStrut(6));

        Field locationField = new Field("", PANEL2);
        locationField.setText(currentLocation);
        locationField.setAlignmentX(Component.LEFT_ALIGNMENT);
        locationField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 41));
        content.add(locationField);

        content.add(Box.createVerticalStrut(14));
        content.add(formLabel("Severity"));
        content.add(Box.createVerticalStrut(6));

        SeverityPicker severityPicker = new SeverityPicker(currentSeverity);
        severityPicker.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(severityPicker);

        content.add(Box.createVerticalStrut(14));
        content.add(formLabel("Status"));
        content.add(Box.createVerticalStrut(6));

        StatusPicker statusPicker = new StatusPicker(currentStatus);
        statusPicker.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(statusPicker);

        content.add(Box.createVerticalStrut(14));
        content.add(formLabel("Description"));
        content.add(Box.createVerticalStrut(6));

        JTextArea descriptionField = new JTextArea(currentDescription);
        descriptionField.setFont(font(Font.PLAIN, 14));
        descriptionField.setForeground(TEXT);
        descriptionField.setCaretColor(TEXT);
        descriptionField.setBackground(PANEL2);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        descriptionField.setBorder(new EmptyBorder(10, 12, 10, 12));

        RoundedPanel descriptionBox = new RoundedPanel(10, PANEL2);
        descriptionBox.setLayout(new BorderLayout());
        descriptionBox.setBorder(new EmptyBorder(1, 1, 1, 1));
        descriptionBox.add(descriptionField, BorderLayout.CENTER);
        descriptionBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionBox.setPreferredSize(new Dimension(420, 88));
        descriptionBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));
        content.add(descriptionBox);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.setBorder(new EmptyBorder(18, 0, 0, 0));

        JButton cancel = createDialogGhostButton("Cancel");
        JButton save = createDialogPrimaryButton("Save changes");

        buttons.add(cancel);
        buttons.add(save);

        cancel.addActionListener(e -> dialog.dispose());

        save.addActionListener(e -> {
            String type = typeField.getText().trim();
            String location = locationField.getText().trim();
            String description = descriptionField.getText().trim();
            int severity = severityPicker.getValue();
            String status = statusPicker.getValue();

            if (type.isEmpty() || location.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please fill all fields.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Disaster d = new Disaster();
                d.setDisasterId(disasterId);
                d.setType(type);
                d.setLocation(location);
                d.setSeverity(severity);
                d.setDescription(description);
                d.setStatus(status);

                disasterDAO.updateDisaster(d);
                dialog.dispose();
                JOptionPane.showMessageDialog(this,
                        "Disaster updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadDisasters();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error updating disaster:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        root.add(content, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.setSize(480, 620);
        dialog.setMinimumSize(new Dimension(480, 620));
        dialog.setLocationRelativeTo(this);
        dialog.getRootPane().setDefaultButton(save);
        dialog.setVisible(true);
    }

    private JLabel formLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font(Font.BOLD, 12));
        label.setForeground(MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton createDialogPill(String text) {
        JButton button = new JButton(text);
        button.setFont(font(Font.BOLD, 12));
        button.setForeground(MUTED);
        button.setBackground(PANEL2);
        button.setBorder(new EmptyBorder(6, 12, 6, 12));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createDialogGhostButton(String text) {
        JButton button = new JButton(text) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                aa(g);
                g.setColor(getModel().isRollover() ? HOVER : PANEL2);
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 9, 9);
                g.dispose();
                super.paintComponent(g0);
            }
        };
        button.setFont(font(Font.BOLD, 13));
        button.setForeground(TEXT);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createDialogPrimaryButton(String text) {
        JButton button = new JButton(text) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                aa(g);
                g.setColor(getModel().isRollover() ? new Color(0x65, 0xC5, 0xFF) : BLUE);
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 9, 9);
                g.dispose();
                super.paintComponent(g0);
            }
        };
        button.setFont(font(Font.BOLD, 13));
        button.setForeground(new Color(0x07, 0x20, 0x31));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static class SeverityPicker extends JComponent {
        private int value;
        private int hover = -1;
        private final Color[] colors = {GREEN, new Color(0xA6, 0xC8, 0x3A), AMBER,
                new Color(0xE2, 0x82, 0x4D), RED};

        SeverityPicker(int value) {
            this.value = Math.max(1, Math.min(5, value));
            setPreferredSize(new Dimension(390, 42));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    int index = indexAt(e.getX());
                    if (index >= 0) { SeverityPicker.this.value = index + 1; repaint(); }
                }
                @Override public void mouseExited(MouseEvent e) { hover = -1; repaint(); }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) { hover = indexAt(e.getX()); repaint(); }
            });
        }

        int getValue() { return value; }

        private int indexAt(int x) {
            int start = 1;
            for (int i = 0; i < 5; i++) {
                int cx = start + i * 43;
                if (x >= cx && x <= cx + 34) return i;
            }
            return -1;
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            for (int i = 0; i < 5; i++) {
                int x = 1 + i * 43;
                int y = 3;
                Color c = colors[i];
                boolean active = i + 1 == value;
                boolean hovered = i == hover;
                if (active) {
                    g.setColor(c);
                    g.fillOval(x, y, 35, 35);
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 0));
                    g.setColor(PANEL2);
                    g.fillOval(x, y, 35, 35);
                    g.setColor(c);
                    g.setStroke(new BasicStroke(1.5f));
                    g.drawOval(x, y, 35, 35);
                    if (hovered) {
                        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 35));
                        g.fillOval(x + 3, y + 3, 29, 29);
                    }
                }
                g.setFont(font(Font.PLAIN, 13));
                FontMetrics fm = g.getFontMetrics();
                String n = String.valueOf(i + 1);
                g.drawString(n, x + (35 - fm.stringWidth(n)) / 2,
                        y + (35 + fm.getAscent() - fm.getDescent()) / 2);
            }

            String level = value == 5 ? "Critical" : value == 4 ? "High" :
                    value == 3 ? "Moderate" : value == 2 ? "Low" : "Minor";
            g.setColor(colors[value - 1]);
            g.setFont(font(Font.BOLD, 13));
            g.drawString(level, 218, 26);
            g.dispose();
        }
    }

    private static class StatusPicker extends JComponent {
        private String value;
        private final String[] options = {"ACTIVE", "MONITORING", "RESOLVED"};
        private int hover = -1;

        StatusPicker(String current) {
            value = "CLOSED".equals(current) ? "RESOLVED" : current;
            if (!value.equals("ACTIVE") && !value.equals("MONITORING") && !value.equals("RESOLVED")) {
                value = "ACTIVE";
            }
            setPreferredSize(new Dimension(310, 42));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    int i = indexAt(e.getX());
                    if (i >= 0) { value = options[i]; repaint(); }
                }
                @Override public void mouseExited(MouseEvent e) { hover = -1; repaint(); }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) { hover = indexAt(e.getX()); repaint(); }
            });
        }

        String getValue() { return value; }

        private int indexAt(int x) {
            int[] widths = {82, 106, 92};
            int cx = 1;
            for (int i = 0; i < widths.length; i++) {
                if (x >= cx && x < cx + widths[i]) return i;
                cx += widths[i] + 2;
            }
            return -1;
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            int[] widths = {82, 106, 92};
            int cx = 1;
            g.setColor(PANEL2);
            g.fillRoundRect(0, 0, 286, 40, 20, 20);
            g.setColor(LINE);
            g.drawRoundRect(0, 0, 286, 40, 20, 20);

            for (int i = 0; i < options.length; i++) {
                int w = widths[i];
                boolean selected = value.equals(options[i]);
                if (selected) {
                    g.setColor(new Color(0x1C, 0x50, 0x78));
                    g.fillRoundRect(cx, 3, w, 34, 17, 17);
                    g.setColor(BLUE);
                    g.drawRoundRect(cx, 3, w - 1, 33, 17, 17);
                } else if (i == hover) {
                    g.setColor(new Color(255, 255, 255, 12));
                    g.fillRoundRect(cx, 3, w, 34, 17, 17);
                }
                g.setFont(font(Font.BOLD, 12));
                g.setColor(selected ? TEXT : MUTED);
                FontMetrics fm = g.getFontMetrics();
                g.drawString(options[i], cx + (w - fm.stringWidth(options[i])) / 2,
                        (40 + fm.getAscent() - fm.getDescent()) / 2);
                cx += w + 2;
            }
            g.dispose();
        }
    }

    private void deleteDisaster() {
        int selectedRow = disasterTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a disaster first.", "No Disaster Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = disasterTable.convertRowIndexToModel(selectedRow);
        int disasterId = (int) tableModel.getValueAt(modelRow, 0);
        String type = String.valueOf(tableModel.getValueAt(modelRow, 1));

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete\nDisaster ID: " + disasterId + "\nType: " + type + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            disasterDAO.deleteDisaster(disasterId);
            JOptionPane.showMessageDialog(this, "Disaster deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadDisasters();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting disaster:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField createInputField(String value) {
        JTextField field = new JTextField(value);
        field.setFont(font(Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBackground(PANEL2);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                new EmptyBorder(9, 12, 9, 12)));
        return field;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(font(Font.PLAIN, 14));
        box.setForeground(TEXT);
        box.setBackground(PANEL2);
        return box;
    }

    private JPanel createFormPanel(String title, JTextField type, JTextField location,
                                   JTextField severity, JTextField description,
                                   JComboBox<String> status) {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(PANEL);
        root.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel heading = new JLabel(title);
        heading.setFont(font(Font.BOLD, 20));
        heading.setForeground(TEXT);
        root.add(heading, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(5, 2, 12, 12));
        fields.setOpaque(false);
        addField(fields, "Type", type);
        addField(fields, "Location", location);
        addField(fields, "Severity (1-5)", severity);
        addField(fields, "Description", description);
        addField(fields, "Status", status);
        root.add(fields, BorderLayout.CENTER);
        return root;
    }

    private void addField(JPanel panel, String label, Component field) {
        JLabel l = new JLabel(label);
        l.setFont(font(Font.BOLD, 12));
        l.setForeground(MUTED);
        panel.add(l);
        panel.add(field);
    }

    private static class Field extends JTextField {
        private final String placeholder;
        private final Color fill;

        Field(String placeholder, Color fill) {
            this.placeholder = placeholder;
            this.fill = fill;
            setOpaque(false);
            setForeground(TEXT);
            setCaretColor(TEXT);
            setFont(font(Font.PLAIN, 14));
            setBorder(new EmptyBorder(9, 14, 9, 14));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { repaint(); }
                @Override public void focusLost(FocusEvent e) { repaint(); }
            });
        }

        @Override public Dimension getPreferredSize() {
            return new Dimension(super.getPreferredSize().width, 40);
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g.setColor(hasFocus() ? BLUE : LINE);
            g.setStroke(new BasicStroke(hasFocus() ? 1.6f : 1f));
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g.dispose();
            super.paintComponent(g0);
            if (getText().isEmpty()) {
                Graphics2D p = (Graphics2D) g0.create();
                aa(p);
                p.setColor(new Color(0x58, 0x72, 0x86));
                p.setFont(getFont());
                FontMetrics fm = p.getFontMetrics();
                p.drawString(placeholder, getInsets().left,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                p.dispose();
            }
        }
    }

    private class SegmentedFilter extends JComponent {
        private String[] labels = {"All 0", "Active 0", "Monitoring 0", "Resolved 0"};
        private int selected = 0;
        private int hover = -1;

        SegmentedFilter() {
            setFont(font(Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    int i = indexAt(e.getX());
                    if (i >= 0) {
                        selected = i;
                        repaint();
                        applyFilter();
                    }
                }
                @Override public void mouseExited(MouseEvent e) { hover = -1; repaint(); }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) { hover = indexAt(e.getX()); repaint(); }
            });
        }

        int getSelected() { return selected; }

        void setSelected(int value) {
            selected = Math.max(0, Math.min(labels.length - 1, value));
            repaint();
        }

        void setLabels(String[] values) {
            labels = values;
            revalidate();
            repaint();
        }

        private int segmentWidth(int i) {
            return getFontMetrics(getFont()).stringWidth(labels[i]) + 30;
        }

        private int indexAt(int x) {
            int cx = 4;
            for (int i = 0; i < labels.length; i++) {
                int w = segmentWidth(i);
                if (x >= cx && x < cx + w) return i;
                cx += w;
            }
            return -1;
        }

        @Override public Dimension getPreferredSize() {
            int width = 8;
            for (int i = 0; i < labels.length; i++) width += segmentWidth(i);
            return new Dimension(width, 40);
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            g.setColor(PANEL);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g.setColor(LINE);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());

            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();
            int cx = 4;
            for (int i = 0; i < labels.length; i++) {
                int w = segmentWidth(i);
                if (i == selected) {
                    g.setColor(new Color(0x1C, 0x50, 0x78));
                    g.fillRoundRect(cx, 4, w, getHeight() - 8, getHeight() - 8, getHeight() - 8);
                    g.setColor(BLUE);
                    g.drawRoundRect(cx, 4, w - 1, getHeight() - 9, getHeight() - 8, getHeight() - 8);
                } else if (i == hover) {
                    g.setColor(new Color(255, 255, 255, 14));
                    g.fillRoundRect(cx, 4, w, getHeight() - 8, getHeight() - 8, getHeight() - 8);
                }
                g.setColor(i == selected ? Color.WHITE : MUTED);
                g.drawString(labels[i], cx + 15,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                cx += w;
            }
            g.dispose();
        }
    }

    private static class DisasterCellRenderer extends JComponent implements TableCellRenderer {
        private String text = "";
        private int kind;
        private Color accent = MUTED;
        private boolean selected;
        private boolean hover;

        @Override public Component getTableCellRendererComponent(JTable table, Object value,
                                                                 boolean isSelected, boolean hasFocus,
                                                                 int row, int column) {
            Integer hoverRow = (Integer) table.getClientProperty("hoverRow");
            hover = hoverRow != null && hoverRow == row;
            selected = isSelected;

            if (column == 0) { kind = 0; text = "#" + value; accent = MUTED; }
            else if (column == 1) { kind = 1; text = String.valueOf(value); accent = typeColor(text); }
            else if (column == 3) { kind = 2; int level = Integer.parseInt(String.valueOf(value)); text = "Level " + level; accent = levelColor(level); }
            else if (column == 4) { kind = 3; text = String.valueOf(value); accent = MUTED; }
            else if (column == 5) { kind = 2; text = String.valueOf(value).toUpperCase(); accent = statusColor(text); }
            else { kind = 4; text = String.valueOf(value); accent = TEXT; }
            return this;
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            int w = getWidth(), h = getHeight();
            g.setColor(selected ? SEL : hover ? HOVER : PANEL);
            g.fillRect(0, 0, w, h);
            g.setColor(new Color(255, 255, 255, 12));
            g.drawLine(0, h - 1, w, h - 1);

            int pad = 16;
            if (selected && getParent() != null && getParent().getParent() != null) {
                // Blue selection indicator is drawn on the first column only below.
            }

            switch (kind) {
                case 0:
                    g.setFont(font(Font.BOLD, 13));
                    g.setColor(MUTED);
                    drawText(g, text, pad, w - pad, h);
                    break;
                case 1:
                    g.setColor(accent);
                    g.fillRoundRect(pad, h / 2 - 5, 10, 10, 4, 4);
                    g.setFont(font(Font.BOLD, 14));
                    g.setColor(TEXT);
                    drawText(g, text, pad + 20, w - pad, h);
                    break;
                case 2:
                    g.setFont(font(Font.BOLD, 12));
                    FontMetrics fm = g.getFontMetrics();
                    int pw = fm.stringWidth(text) + 24;
                    int ph = 26;
                    int py = (h - ph) / 2;
                    g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 44));
                    g.fillRoundRect(pad, py, pw, ph, ph, ph);
                    g.setColor(accent);
                    g.drawString(text, pad + 12, py + (ph + fm.getAscent() - fm.getDescent()) / 2);
                    break;
                case 3:
                    g.setFont(font(Font.PLAIN, 14));
                    g.setColor(new Color(0xBF, 0xD0, 0xDC));
                    drawText(g, text, pad, w - pad, h);
                    break;
                default:
                    g.setFont(font(Font.PLAIN, 14));
                    g.setColor(TEXT);
                    drawText(g, text, pad, w - pad, h);
            }
            g.dispose();
        }

        private void drawText(Graphics2D g, String value, int x, int right, int h) {
            FontMetrics fm = g.getFontMetrics();
            String s = value == null ? "" : value;
            if (fm.stringWidth(s) > right - x) {
                String ellipsis = "…";
                int n = s.length();
                while (n > 0 && fm.stringWidth(s.substring(0, n) + ellipsis) > right - x) n--;
                s = s.substring(0, n) + ellipsis;
            }
            g.drawString(s, x, (h + fm.getAscent() - fm.getDescent()) / 2);
        }
    }

    private static class HeaderRenderer extends JComponent implements TableCellRenderer {
        private String text = "";
        @Override public Component getTableCellRendererComponent(JTable table, Object value,
                                                                 boolean isSelected, boolean hasFocus,
                                                                 int row, int column) {
            text = String.valueOf(value).toUpperCase();
            return this;
        }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            g.setColor(PANEL);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(LINE);
            g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            g.setFont(font(Font.BOLD, 11));
            g.setColor(MUTED);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, 16, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill;
        RoundedPanel(int radius, Color fill) {
            this.radius = radius;
            this.fill = fill;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g.setColor(LINE);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g.dispose();
            super.paintComponent(g0);
        }
    }
}
