package com.disaster.gui;

import com.disaster.dao.VictimDAO;
import com.disaster.model.Victim;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Polished Victim Management panel.
 *
 * Visual language intentionally matches ShelterPanel:
 * - light slate background
 * - white rounded card
 * - blue count badge
 * - clean table with alternating rows
 * - rounded action buttons
 * - soft status/priority chips
 *
 * Database behaviour is kept intact: load, add, update and delete victims.
 */
public class VictimPanel extends JPanel {

    // =========================================================
    // DESIGN TOKENS — same visual system as ShelterPanel
    // =========================================================

    private static final Color BACKGROUND   = new Color(0xF1, 0xF5, 0xF9);
    private static final Color SURFACE      = Color.WHITE;
    private static final Color ROW_ALT      = new Color(0xF8, 0xFA, 0xFC);
    private static final Color BORDER_SOFT  = new Color(0xE2, 0xE8, 0xF0);
    private static final Color HEADER_BG    = new Color(0xF8, 0xFA, 0xFC);
    private static final Color TEXT         = new Color(0x0F, 0x17, 0x2A);
    private static final Color TEXT_SUBTLE  = new Color(0x47, 0x55, 0x69);
    private static final Color TEXT_MUTED   = new Color(0x64, 0x74, 0x8B);
    private static final Color SELECTION_BG = new Color(0xDB, 0xEA, 0xFE);

    private static final Color BLUE         = new Color(0x25, 0x63, 0xEB);
    private static final Color BLUE_HOVER   = new Color(0x1D, 0x4E, 0xD8);
    private static final Color GREEN        = new Color(0x16, 0xA3, 0x4A);
    private static final Color ORANGE       = new Color(0xEA, 0x8A, 0x04);
    private static final Color RED          = new Color(0xDC, 0x26, 0x26);
    private static final Color RED_HOVER    = new Color(0xB9, 0x1C, 0x1C);
    private static final Color NEUTRAL      = new Color(0x47, 0x55, 0x69);
    private static final Color NEUTRAL_HOVER= new Color(0x33, 0x41, 0x55);

    private static final Color GREEN_SOFT   = new Color(0xDC, 0xFC, 0xE7);
    private static final Color GREEN_TEXT   = new Color(0x15, 0x80, 0x3D);
    private static final Color ORANGE_SOFT  = new Color(0xFE, 0xF3, 0xC7);
    private static final Color ORANGE_TEXT  = new Color(0xB4, 0x53, 0x09);
    private static final Color RED_SOFT     = new Color(0xFE, 0xE2, 0xE2);
    private static final Color RED_TEXT     = new Color(0xB9, 0x1C, 0x1C);

    // =========================================================
    // FONTS
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
    // COMPONENTS / STATE
    // =========================================================

    private JTable victimTable;
    private DefaultTableModel tableModel;
    private JLabel victimCountLabel;
    private final VictimDAO victimDAO;
    private JTextField searchField;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VictimPanel() {
        victimDAO = new VictimDAO();

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCard(), BorderLayout.CENTER);

        loadVictims();
    }

    // =========================================================
    // HEADER
    // =========================================================

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 4, 20, 4));

        JPanel titleGroup = new JPanel();
        titleGroup.setOpaque(false);
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Victim Management");
        titleLabel.setFont(font(Font.BOLD, 26));
        titleLabel.setForeground(TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(
                "Manage registered victims and evacuation priority information");
        subtitleLabel.setFont(font(Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleGroup.add(titleLabel);
        titleGroup.add(Box.createVerticalStrut(6));
        titleGroup.add(subtitleLabel);

        header.add(titleGroup, BorderLayout.WEST);

        victimCountLabel = new JLabel("0 Victims");
        victimCountLabel.setFont(font(Font.BOLD, 13));
        victimCountLabel.setForeground(Color.WHITE);

        BadgePanel badge = new BadgePanel(BLUE);
        badge.setLayout(new BorderLayout());
        badge.setBorder(new EmptyBorder(8, 16, 8, 16));
        badge.add(victimCountLabel, BorderLayout.CENTER);

        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge);

        header.add(badgeWrap, BorderLayout.EAST);

        return header;
    }

    // =========================================================
    // CARD
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
                "Victim ID",
                "Name",
                "Age",
                "Medical Condition",
                "Vulnerability",
                "Zone Risk",
                "Waiting Time",
                "Priority",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        victimTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(
                    javax.swing.table.TableCellRenderer renderer,
                    int row,
                    int column) {

                Component c = super.prepareRenderer(renderer, row, column);

                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? SURFACE : ROW_ALT);
                }

                return c;
            }
        };

        victimTable.setRowHeight(40);
        victimTable.setFont(font(Font.PLAIN, 13));
        victimTable.setForeground(TEXT_SUBTLE);
        victimTable.setBackground(SURFACE);
        victimTable.setShowGrid(false);
        victimTable.setIntercellSpacing(new Dimension(0, 0));
        victimTable.setFillsViewportHeight(true);
        victimTable.setSelectionBackground(SELECTION_BG);
        victimTable.setSelectionForeground(TEXT);
        victimTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        victimTable.setAutoCreateRowSorter(true);

        // Header
        JTableHeader th = victimTable.getTableHeader();
        th.setFont(font(Font.BOLD, 12));
        th.setBackground(HEADER_BG);
        th.setForeground(TEXT_MUTED);
        th.setPreferredSize(new Dimension(0, 44));
        th.setReorderingAllowed(false);
        th.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, BORDER_SOFT));

        if (th.getDefaultRenderer() instanceof DefaultTableCellRenderer) {
            ((DefaultTableCellRenderer) th.getDefaultRenderer())
                    .setHorizontalAlignment(SwingConstants.LEFT);
        }

        // Column widths
        int[] widths = {
                85, 155, 55, 180, 105, 90, 105, 125, 125
        };

        for (int i = 0; i < widths.length; i++) {
            victimTable.getColumnModel().getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        // Text renderers
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setBorder(new EmptyBorder(0, 16, 0, 16));

        for (int i = 0; i < columns.length; i++) {
            victimTable.getColumnModel().getColumn(i)
                    .setCellRenderer(left);
        }

        // Specific renderers
        victimTable.getColumnModel().getColumn(0)
                .setCellRenderer(new IdRenderer());

        victimTable.getColumnModel().getColumn(2)
                .setCellRenderer(new CenterRenderer());

        victimTable.getColumnModel().getColumn(4)
                .setCellRenderer(new ScoreRenderer(7, 4));

        victimTable.getColumnModel().getColumn(5)
                .setCellRenderer(new ScoreRenderer(3, 2));

        victimTable.getColumnModel().getColumn(6)
                .setCellRenderer(new CenterRenderer());

        victimTable.getColumnModel().getColumn(7)
                .setCellRenderer(new PriorityRenderer());

        victimTable.getColumnModel().getColumn(8)
                .setCellRenderer(new StatusRenderer());

        JScrollPane scrollPane = new JScrollPane(victimTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.setBackground(SURFACE);

        return scrollPane;
    }

    // =========================================================
    // ACTION BAR
    // =========================================================

    private JComponent buildActionBar() {

        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(14, 12, 10, 12));

        // Search is kept from the original VictimPanel functionality.
        searchField = new JTextField();
        searchField.setFont(font(Font.PLAIN, 13));
        searchField.setForeground(TEXT_SUBTLE);
        searchField.setBackground(SURFACE);
        searchField.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER_SOFT, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        searchField.setPreferredSize(new Dimension(220, 38));

        searchField.setText("Search victims...");
        searchField.setForeground(TEXT_MUTED);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if ("Search victims...".equals(searchField.getText())) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_SUBTLE);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Search victims...");
                    searchField.setForeground(TEXT_MUTED);
                }
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });

        JPanel searchWrap = new JPanel(new FlowLayout(
                FlowLayout.LEFT, 0, 0));
        searchWrap.setOpaque(false);
        searchWrap.add(searchField);

        JPanel buttonRow = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);

        JButton refreshButton =
                ghostButton("Refresh");

        JButton updateButton =
                pillButton("Update Victim",
                        NEUTRAL, NEUTRAL_HOVER, true);

        JButton deleteButton =
                pillButton("Delete Victim",
                        RED, RED_HOVER, true);

        JButton addButton =
                pillButton("Add Victim",
                        BLUE, BLUE_HOVER, true);

        buttonRow.add(refreshButton);
        buttonRow.add(updateButton);
        buttonRow.add(deleteButton);
        buttonRow.add(addButton);

        bar.add(searchWrap, BorderLayout.WEST);
        bar.add(buttonRow, BorderLayout.EAST);

        refreshButton.addActionListener(e -> loadVictims());
        updateButton.addActionListener(e -> showUpdateVictimDialog());
        deleteButton.addActionListener(e -> deleteVictim());
        addButton.addActionListener(e -> showAddVictimDialog());

        victimTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2
                        && SwingUtilities.isLeftMouseButton(e)) {
                    showUpdateVictimDialog();
                }
            }
        });

        return bar;
    }

    // =========================================================
    // BUTTON FACTORIES
    // =========================================================

    private JButton pillButton(
            String text,
            Color base,
            Color hover,
            boolean lightText) {

        PillButton button = new PillButton(base, hover, 10);
        button.setText(text);
        button.setFont(font(Font.BOLD, 13));
        button.setForeground(lightText ? Color.WHITE : TEXT);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JButton ghostButton(String text) {

        PillButton button = new PillButton(
                SURFACE,
                new Color(0xF1, 0xF5, 0xF9),
                10
        );

        button.setText(text);
        button.setFont(font(Font.BOLD, 13));
        button.setForeground(TEXT_SUBTLE);
        button.setOutline(BORDER_SOFT);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // =========================================================
    // LOAD VICTIMS
    // =========================================================

    private void loadVictims() {

        tableModel.setRowCount(0);

        try {
            List<Victim> victims = victimDAO.getAllVictims();

            for (Victim victim : victims) {
                tableModel.addRow(new Object[]{
                        victim.getVictimId(),
                        victim.getName(),
                        victim.getAge(),
                        victim.getMedicalCondition(),
                        victim.getVulnerabilityScore(),
                        victim.getZoneRisk(),
                        victim.getWaitingTime(),
                        victim.getPriorityScore(),
                        victim.getStatus()
                });
            }

            victimCountLabel.setText(victims.size() + " Victims");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading victims:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // SEARCH FILTER
    // =========================================================

    private void filterTable() {

        if (tableModel == null || victimTable == null
                || searchField == null) {
            return;
        }

        String query = searchField.getText().trim();

        if (query.equals("Search victims...")) {
            query = "";
        }

        final String finalQuery = query;

        if (!(victimTable.getRowSorter()
                instanceof javax.swing.table.TableRowSorter)) {
            return;
        }

        @SuppressWarnings("unchecked")
        javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                (javax.swing.table.TableRowSorter<DefaultTableModel>)
                        victimTable.getRowSorter();

        if (finalQuery.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }

        try {
            sorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + java.util.regex.Pattern
                                    .quote(finalQuery)
                    )
            );
        } catch (Exception ignored) {
            sorter.setRowFilter(null);
        }
    }

    // =========================================================
    // DELETE VICTIM
    // =========================================================

    private void deleteVictim() {

        int selectedRow = victimTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a victim first.",
                    "No Victim Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow =
                victimTable.convertRowIndexToModel(selectedRow);

        int victimId =
                Integer.parseInt(tableModel
                        .getValueAt(modelRow, 0).toString());

        String name =
                String.valueOf(tableModel.getValueAt(modelRow, 1));

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete\n"
                        + "Victim ID: " + victimId
                        + "\nName: " + name + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            victimDAO.deleteVictim(victimId);

            JOptionPane.showMessageDialog(
                    this,
                    "Victim deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadVictims();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting victim:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // ADD VICTIM
    // =========================================================

    private void showAddVictimDialog() {

        JTextField disasterIdField = styledField("");
        JTextField nameField = styledField("");
        JTextField ageField = styledField("");
        JTextField medicalField = styledField("");

        JPanel panel = createFormPanel(4);

        panel.add(new JLabel("Disaster ID:"));
        panel.add(disasterIdField);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Age:"));
        panel.add(ageField);

        panel.add(new JLabel("Medical Condition:"));
        panel.add(medicalField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add New Victim",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int disasterId =
                    Integer.parseInt(disasterIdField.getText().trim());

            String name = nameField.getText().trim();

            int age =
                    Integer.parseInt(ageField.getText().trim());

            String medical =
                    medicalField.getText().trim();

            if (name.isEmpty()) {
                throw new IllegalArgumentException(
                        "Name cannot be empty.");
            }

            if (age <= 0) {
                throw new IllegalArgumentException(
                        "Age must be greater than 0.");
            }

            Victim victim = new Victim();

            victim.setDisasterId(disasterId);
            victim.setName(name);
            victim.setAge(age);
            victim.setMedicalCondition(medical);

            victim.setVulnerabilityScore(0);
            victim.setZoneRisk(0);
            victim.setWaitingTime(0);
            victim.setPriorityScore(0);
            victim.setStatus("WAITING");

            victimDAO.addVictim(victim);

            JOptionPane.showMessageDialog(
                    this,
                    "Victim added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadVictims();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Disaster ID and Age must be valid numbers.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding victim:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // UPDATE VICTIM
    // =========================================================

    private void showUpdateVictimDialog() {

        int selectedRow = victimTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a victim first.",
                    "No Victim Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow =
                victimTable.convertRowIndexToModel(selectedRow);

        int victimId =
                Integer.parseInt(tableModel
                        .getValueAt(modelRow, 0).toString());

        int currentDisasterId = 0;

        try {
            for (Victim victim : victimDAO.getAllVictims()) {
                if (victim.getVictimId() == victimId) {
                    currentDisasterId = victim.getDisasterId();
                    break;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading victim details:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        JTextField disasterIdField =
                styledField(String.valueOf(currentDisasterId));

        JTextField nameField =
                styledField(String.valueOf(
                        tableModel.getValueAt(modelRow, 1)));

        JTextField ageField =
                styledField(String.valueOf(
                        tableModel.getValueAt(modelRow, 2)));

        JTextField medicalField =
                styledField(String.valueOf(
                        tableModel.getValueAt(modelRow, 3)));

        JTextField vulnerabilityField =
                styledField(String.valueOf(
                        tableModel.getValueAt(modelRow, 4)));

        JTextField zoneRiskField =
                styledField(String.valueOf(
                        tableModel.getValueAt(modelRow, 5)));

        JTextField waitingTimeField =
                styledField(String.valueOf(
                        tableModel.getValueAt(modelRow, 6)));

        JTextField priorityField =
                styledField(String.valueOf(
                        tableModel.getValueAt(modelRow, 7)));

        String currentStatus =
                String.valueOf(tableModel.getValueAt(modelRow, 8));

        String[] statusOptions = {
                "WAITING",
                "EVACUATED",
                "CRITICAL"
        };

        JComboBox<String> statusBox =
                new JComboBox<>(statusOptions);

        statusBox.setSelectedItem(currentStatus);
        statusBox.setFont(font(Font.PLAIN, 13));

        JPanel panel = createFormPanel(9);

        panel.add(new JLabel("Disaster ID:"));
        panel.add(disasterIdField);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Age:"));
        panel.add(ageField);

        panel.add(new JLabel("Medical Condition:"));
        panel.add(medicalField);

        panel.add(new JLabel("Vulnerability:"));
        panel.add(vulnerabilityField);

        panel.add(new JLabel("Zone Risk:"));
        panel.add(zoneRiskField);

        panel.add(new JLabel("Waiting Time:"));
        panel.add(waitingTimeField);

        panel.add(new JLabel("Priority Score:"));
        panel.add(priorityField);

        panel.add(new JLabel("Status:"));
        panel.add(statusBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Update Victim ID " + victimId,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            String name = nameField.getText().trim();

            int age =
                    Integer.parseInt(ageField.getText().trim());

            if (name.isEmpty()) {
                throw new IllegalArgumentException(
                        "Name cannot be empty.");
            }

            if (age <= 0) {
                throw new IllegalArgumentException(
                        "Age must be greater than 0.");
            }

            Victim victim = new Victim();

            victim.setVictimId(victimId);

            victim.setDisasterId(
                    Integer.parseInt(
                            disasterIdField.getText().trim()));

            victim.setName(name);
            victim.setAge(age);

            victim.setMedicalCondition(
                    medicalField.getText().trim());

            victim.setVulnerabilityScore(
                    Integer.parseInt(
                            vulnerabilityField.getText().trim()));

            victim.setZoneRisk(
                    Integer.parseInt(
                            zoneRiskField.getText().trim()));

            victim.setWaitingTime(
                    Integer.parseInt(
                            waitingTimeField.getText().trim()));

            victim.setPriorityScore(
                    Integer.parseInt(
                            priorityField.getText().trim()));

            victim.setStatus(
                    String.valueOf(
                            statusBox.getSelectedItem()));

            victimDAO.updateVictim(victim);

            JOptionPane.showMessageDialog(
                    this,
                    "Victim updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadVictims();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "All numeric fields must contain valid numbers.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating victim:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // FORM HELPERS
    // =========================================================

    private JTextField styledField(String value) {

        JTextField field = new JTextField(value);

        field.setFont(font(Font.PLAIN, 13));
        field.setForeground(TEXT_SUBTLE);
        field.setBackground(SURFACE);
        field.setCaretColor(BLUE);

        field.setBorder(new CompoundBorder(
                new RoundedLineBorder(BORDER_SOFT, 8),
                new EmptyBorder(7, 10, 7, 10)
        ));

        return field;
    }

    private JPanel createFormPanel(int rows) {

        JPanel panel = new JPanel(
                new GridLayout(rows, 2, 12, 12));

        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        return panel;
    }

    // =========================================================
    // TABLE RENDERERS
    // =========================================================

    private static class BaseRenderer extends DefaultTableCellRenderer {

        BaseRenderer() {
            setBorder(new EmptyBorder(0, 16, 0, 16));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column);

            c.setFont(font(Font.PLAIN, 13));
            c.setForeground(selected ? TEXT : TEXT_SUBTLE);

            if (!selected) {
                c.setBackground(
                        row % 2 == 0 ? SURFACE : ROW_ALT);
            } else {
                c.setBackground(SELECTION_BG);
            }

            return c;
        }
    }

    private static class CenterRenderer extends BaseRenderer {

        CenterRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }
    }

    private static class IdRenderer extends BaseRenderer {

        IdRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column);

            setFont(font(Font.BOLD, 12));
            setForeground(selected ? TEXT : BLUE);

            return c;
        }
    }

    private static class ScoreRenderer extends BaseRenderer {

        private final int highThreshold;
        private final int mediumThreshold;

        ScoreRenderer(int highThreshold, int mediumThreshold) {
            this.highThreshold = highThreshold;
            this.mediumThreshold = mediumThreshold;
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column);

            if (!selected && value != null) {
                try {
                    int score = Integer.parseInt(value.toString());

                    if (score >= highThreshold) {
                        setForeground(RED);
                    } else if (score >= mediumThreshold) {
                        setForeground(ORANGE);
                    } else {
                        setForeground(GREEN);
                    }

                    setFont(font(Font.BOLD, 13));

                } catch (NumberFormatException e) {
                    setForeground(TEXT_SUBTLE);
                }
            }

            return c;
        }
    }

    private static class PriorityRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column) {

            String text = value == null ? "" : value.toString();

            Color bg;
            Color fg;
            String label;

            try {
                int score = Integer.parseInt(text);

                if (score >= 20) {
                    bg = RED_SOFT;
                    fg = RED_TEXT;
                    label = score + "  CRITICAL";
                } else if (score >= 10) {
                    bg = ORANGE_SOFT;
                    fg = ORANGE_TEXT;
                    label = score + "  HIGH";
                } else {
                    bg = GREEN_SOFT;
                    fg = GREEN_TEXT;
                    label = score + "  LOW";
                }

            } catch (NumberFormatException e) {
                bg = ROW_ALT;
                fg = TEXT_MUTED;
                label = text;
            }

            PillCell cell = new PillCell(label, bg, fg);

            cell.setBackground(
                    selected
                            ? SELECTION_BG
                            : (row % 2 == 0 ? SURFACE : ROW_ALT));

            return cell;
        }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column) {

            String status =
                    value == null
                            ? ""
                            : value.toString().toUpperCase();

            Color bg;
            Color fg;

            if ("CRITICAL".equals(status)) {
                bg = RED_SOFT;
                fg = RED_TEXT;
            } else if ("EVACUATED".equals(status)) {
                bg = GREEN_SOFT;
                fg = GREEN_TEXT;
            } else {
                bg = ORANGE_SOFT;
                fg = ORANGE_TEXT;
            }

            PillCell cell = new PillCell(
                    value == null ? "" : value.toString(),
                    bg,
                    fg
            );

            cell.setBackground(
                    selected
                            ? SELECTION_BG
                            : (row % 2 == 0 ? SURFACE : ROW_ALT));

            return cell;
        }
    }

    // =========================================================
    // PILL CELL
    // =========================================================

    private static class PillCell extends JPanel {

        private final String text;
        private final Color pillBg;
        private final Color pillFg;

        PillCell(
                String text,
                Color pillBg,
                Color pillFg) {

            this.text = text;
            this.pillBg = pillBg;
            this.pillFg = pillFg;

            setOpaque(true);
            setBorder(new EmptyBorder(0, 12, 0, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            if (text == null || text.isEmpty()) {
                return;
            }

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setFont(font(Font.BOLD, 11));

            FontMetrics fm = g2.getFontMetrics();

            int textWidth = fm.stringWidth(text);
            int paddingX = 12;
            int pillWidth = textWidth + paddingX * 2;
            int pillHeight = 22;

            int x = Math.max(
                    8,
                    (getWidth() - pillWidth) / 2);

            int y =
                    (getHeight() - pillHeight) / 2;

            g2.setColor(pillBg);

            g2.fill(new RoundRectangle2D.Float(
                    x,
                    y,
                    pillWidth,
                    pillHeight,
                    pillHeight,
                    pillHeight
            ));

            g2.setColor(pillFg);

            int textY =
                    y
                            + (pillHeight - fm.getHeight()) / 2
                            + fm.getAscent();

            g2.drawString(
                    text,
                    x + paddingX,
                    textY
            );

            g2.dispose();
        }
    }

    // =========================================================
    // ROUNDED CARD
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

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int shadowLayers = 8;
            int width = getWidth();
            int height = getHeight();

            for (int i = shadowLayers; i > 0; i--) {

                float alpha =
                        0.03f
                                * (shadowLayers - i + 1)
                                / shadowLayers;

                g2.setColor(
                        new Color(0f, 0f, 0f, alpha));

                g2.fill(new RoundRectangle2D.Float(
                        i,
                        i + 2,
                        width - i * 2,
                        height - i * 2 - 2,
                        arc,
                        arc
                ));
            }

            g2.setColor(fill);

            g2.fill(new RoundRectangle2D.Float(
                    4,
                    4,
                    width - 8,
                    height - 10,
                    arc,
                    arc
            ));

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // =========================================================
    // BADGE
    // =========================================================

    private static class BadgePanel extends JPanel {

        private final Color fill;

        BadgePanel(Color fill) {
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(fill);

            g2.fill(new RoundRectangle2D.Float(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    getHeight(),
                    getHeight()
            ));

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // =========================================================
    // PILL BUTTON
    // =========================================================

    private static class PillButton extends JButton {

        private final Color base;
        private final Color hover;
        private final int arc;

        private Color outline;
        private boolean hovering;

        PillButton(
                Color base,
                Color hover,
                int arc) {

            this.base = base;
            this.hover = hover;
            this.arc = arc;

            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);

            addMouseListener(
                    new java.awt.event.MouseAdapter() {

                        @Override
                        public void mouseEntered(
                                java.awt.event.MouseEvent e) {
                            hovering = true;
                            repaint();
                        }

                        @Override
                        public void mouseExited(
                                java.awt.event.MouseEvent e) {
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

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(
                    hovering ? hover : base);

            g2.fill(new RoundRectangle2D.Float(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    arc,
                    arc
            ));

            if (outline != null) {

                g2.setColor(outline);
                g2.setStroke(new BasicStroke(1f));

                g2.draw(new RoundRectangle2D.Float(
                        0.5f,
                        0.5f,
                        getWidth() - 1,
                        getHeight() - 1,
                        arc,
                        arc
                ));
            }

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // =========================================================
    // ROUNDED BORDER
    // =========================================================

    private static class RoundedLineBorder
            extends javax.swing.border.AbstractBorder {

        private final Color color;
        private final int arc;

        RoundedLineBorder(Color color, int arc) {
            this.color = color;
            this.arc = arc;
        }

        @Override
        public void paintBorder(
                Component component,
                Graphics graphics,
                int x,
                int y,
                int width,
                int height) {

            Graphics2D g2 =
                    (Graphics2D) graphics.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(color);

            g2.draw(new RoundRectangle2D.Float(
                    x,
                    y,
                    width - 1,
                    height - 1,
                    arc,
                    arc
            ));

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component component) {
            return new Insets(arc / 2, arc / 2,
                    arc / 2, arc / 2);
        }
    }
}