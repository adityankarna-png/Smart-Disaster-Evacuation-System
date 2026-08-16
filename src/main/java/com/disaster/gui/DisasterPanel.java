package com.disaster.gui;

import com.disaster.dao.DisasterDAO;
import com.disaster.model.Disaster;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DisasterPanel extends JPanel {

    // =========================================================
    // COLORS
    // =========================================================

    private static final Color BACKGROUND =
            new Color(245, 247, 250);

    private static final Color TEXT =
            new Color(35, 45, 55);

    private static final Color BLUE =
            new Color(52, 105, 170);

    private static final Color BLUE_DARK =
            new Color(25, 55, 90);

    private static final Color RED =
            new Color(210, 65, 65);

    private static final Color ORANGE =
            new Color(235, 145, 35);

    private static final Color GREEN =
            new Color(45, 150, 90);

    private static final Color TABLE_HEADER =
            new Color(235, 240, 245);

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

        // =====================================================
        // TOP SECTION
        // =====================================================

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND);
        topPanel.setBorder(
                new EmptyBorder(
                        18,
                        25,
                        10,
                        25
                )
        );

        // Title + subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS
                )
        );

        titlePanel.setBackground(BACKGROUND);

        JLabel titleLabel =
                new JLabel("Disaster Management");

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        titleLabel.setForeground(TEXT);

        JLabel subtitleLabel =
                new JLabel(
                        "Manage active disasters and emergency incident information"
                );

        subtitleLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        subtitleLabel.setForeground(
                new Color(100, 110, 125)
        );

        titlePanel.add(titleLabel);
        titlePanel.add(
                Box.createVerticalStrut(4)
        );
        titlePanel.add(subtitleLabel);

        topPanel.add(
                titlePanel,
                BorderLayout.WEST
        );

        // Disaster count
        disasterCountLabel =
                new JLabel("Disasters: 0");

        disasterCountLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        disasterCountLabel.setForeground(BLUE);

        disasterCountLabel.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        topPanel.add(
                disasterCountLabel,
                BorderLayout.EAST
        );

        add(
                topPanel,
                BorderLayout.NORTH
        );

        // =====================================================
        // TABLE
        // =====================================================

        String[] columns = {
                "Disaster ID",
                "Type",
                "Location",
                "Severity",
                "Description",
                "Status"
        };

        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        disasterTable =
                new JTable(tableModel);

        // Basic table settings
        disasterTable.setRowHeight(34);

        disasterTable.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        disasterTable.setForeground(TEXT);

        disasterTable.setBackground(Color.WHITE);

        disasterTable.setGridColor(
                new Color(215, 220, 225)
        );

        disasterTable.setSelectionBackground(
                new Color(220, 232, 245)
        );

        disasterTable.setSelectionForeground(
                TEXT
        );

        disasterTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        disasterTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS
        );

        // =====================================================
        // TABLE HEADER
        // =====================================================

        disasterTable.getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                13
                        )
                );

        disasterTable.getTableHeader()
                .setBackground(TABLE_HEADER);

        disasterTable.getTableHeader()
                .setForeground(TEXT);

        disasterTable.getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                36
                        )
                );

        // =====================================================
        // COLUMN WIDTHS
        // =====================================================

        disasterTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(90);

        disasterTable.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(110);

        disasterTable.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(130);

        disasterTable.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(80);

        disasterTable.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(280);

        disasterTable.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(110);

        // =====================================================
        // CUSTOM CELL RENDERERS
        // =====================================================

        disasterTable.getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        new SeverityRenderer()
                );

        disasterTable.getColumnModel()
                .getColumn(5)
                .setCellRenderer(
                        new StatusRenderer()
                );

        // =====================================================
        // SCROLL PANE
        // =====================================================

        JScrollPane scrollPane =
                new JScrollPane(
                        disasterTable
                );

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        25,
                        10,
                        25
                )
        );

        scrollPane.getViewport()
                .setBackground(Color.WHITE);

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        // =====================================================
        // BUTTON PANEL
        // =====================================================

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                12
                        )
                );

        buttonPanel.setBackground(
                Color.WHITE
        );

        buttonPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        25,
                        10,
                        25
                )
        );

        JButton addButton =
                createButton(
                        "Add Disaster",
                        BLUE
                );

        JButton updateButton =
                createButton(
                        "Update Disaster",
                        new Color(70, 120, 175)
                );

        JButton refreshButton =
                createButton(
                        "Refresh",
                        new Color(100, 110, 120)
                );

        JButton deleteButton =
                createButton(
                        "Delete Disaster",
                        RED
                );

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);

        add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        // =====================================================
        // BUTTON ACTIONS
        // =====================================================

        addButton.addActionListener(
                e -> showAddDisasterDialog()
        );

        updateButton.addActionListener(
                e -> showUpdateDisasterDialog()
        );

        refreshButton.addActionListener(
                e -> loadDisasters()
        );

        deleteButton.addActionListener(
                e -> deleteDisaster()
        );

        // =====================================================
        // LOAD DATA
        // =====================================================

        loadDisasters();
    }

    // =========================================================
    // CREATE BUTTON
    // =========================================================

    private JButton createButton(
            String text,
            Color color
    ) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(Color.WHITE);

        button.setBackground(color);

        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        18,
                        10,
                        18
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }

    // =========================================================
    // LOAD DISASTERS
    // =========================================================

    private void loadDisasters() {

        tableModel.setRowCount(0);

        List<Disaster> disasters =
                disasterDAO.getAllDisasters();

        for (Disaster disaster : disasters) {

            tableModel.addRow(
                    new Object[]{

                            disaster.getDisasterId(),

                            disaster.getType(),

                            disaster.getLocation(),

                            disaster.getSeverity(),

                            disaster.getDescription(),

                            disaster.getStatus()
                    }
            );
        }

        disasterCountLabel.setText(
                "Disasters: " + disasters.size()
        );
    }

    // =========================================================
    // ADD DISASTER
    // =========================================================

    private void showAddDisasterDialog() {

        JTextField typeField =
                new JTextField();

        JTextField locationField =
                new JTextField();

        JTextField severityField =
                new JTextField();

        JTextField descriptionField =
                new JTextField();

        JComboBox<String> statusField =
                new JComboBox<>(
                        new String[]{
                                "ACTIVE",
                                "RESOLVED",
                                "CLOSED"
                        }
                );

        JPanel panel =
                createFormPanel();

        panel.add(
                new JLabel("Type:")
        );

        panel.add(typeField);

        panel.add(
                new JLabel("Location:")
        );

        panel.add(locationField);

        panel.add(
                new JLabel("Severity (1-5):")
        );

        panel.add(severityField);

        panel.add(
                new JLabel("Description:")
        );

        panel.add(descriptionField);

        panel.add(
                new JLabel("Status:")
        );

        panel.add(statusField);

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Add Disaster",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            String type =
                    typeField.getText().trim();

            String location =
                    locationField.getText().trim();

            int severity =
                    Integer.parseInt(
                            severityField
                                    .getText()
                                    .trim()
                    );

            String description =
                    descriptionField
                            .getText()
                            .trim();

            String status =
                    statusField
                            .getSelectedItem()
                            .toString();

            if (type.isEmpty()
                    || location.isEmpty()
                    || description.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please fill all fields.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (severity < 1
                    || severity > 5) {

                JOptionPane.showMessageDialog(
                        this,
                        "Severity must be between 1 and 5.",
                        "Invalid Severity",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            Disaster disaster =
                    new Disaster();

            disaster.setType(type);

            disaster.setLocation(location);

            disaster.setSeverity(severity);

            disaster.setDescription(
                    description
            );

            disaster.setStatus(status);

            disasterDAO.addDisaster(
                    disaster
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Disaster added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadDisasters();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Severity must be a number between 1 and 5.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding disaster:\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // UPDATE DISASTER
    // =========================================================

    private void showUpdateDisasterDialog() {

        int selectedRow =
                disasterTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a disaster first.",
                    "No Disaster Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int disasterId =
                (int) tableModel.getValueAt(
                        selectedRow,
                        0
                );

        String currentType =
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString();

        String currentLocation =
                tableModel.getValueAt(
                        selectedRow,
                        2
                ).toString();

        int currentSeverity =
                (int) tableModel.getValueAt(
                        selectedRow,
                        3
                );

        String currentDescription =
                tableModel.getValueAt(
                        selectedRow,
                        4
                ).toString();

        String currentStatus =
                tableModel.getValueAt(
                        selectedRow,
                        5
                ).toString();

        JTextField typeField =
                new JTextField(
                        currentType
                );

        JTextField locationField =
                new JTextField(
                        currentLocation
                );

        JTextField severityField =
                new JTextField(
                        String.valueOf(
                                currentSeverity
                        )
                );

        JTextField descriptionField =
                new JTextField(
                        currentDescription
                );

        JComboBox<String> statusField =
                new JComboBox<>(
                        new String[]{
                                "ACTIVE",
                                "RESOLVED",
                                "CLOSED"
                        }
                );

        statusField.setSelectedItem(
                currentStatus
        );

        JPanel panel =
                createFormPanel();

        panel.add(
                new JLabel("Type:")
        );

        panel.add(typeField);

        panel.add(
                new JLabel("Location:")
        );

        panel.add(locationField);

        panel.add(
                new JLabel("Severity (1-5):")
        );

        panel.add(severityField);

        panel.add(
                new JLabel("Description:")
        );

        panel.add(descriptionField);

        panel.add(
                new JLabel("Status:")
        );

        panel.add(statusField);

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Update Disaster ID "
                                + disasterId,
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            String type =
                    typeField.getText().trim();

            String location =
                    locationField.getText().trim();

            int severity =
                    Integer.parseInt(
                            severityField
                                    .getText()
                                    .trim()
                    );

            String description =
                    descriptionField
                            .getText()
                            .trim();

            String status =
                    statusField
                            .getSelectedItem()
                            .toString();

            if (type.isEmpty()
                    || location.isEmpty()
                    || description.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please fill all fields.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (severity < 1
                    || severity > 5) {

                JOptionPane.showMessageDialog(
                        this,
                        "Severity must be between 1 and 5.",
                        "Invalid Severity",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            Disaster disaster =
                    new Disaster();

            disaster.setDisasterId(
                    disasterId
            );

            disaster.setType(type);

            disaster.setLocation(location);

            disaster.setSeverity(severity);

            disaster.setDescription(
                    description
            );

            disaster.setStatus(status);

            disasterDAO.updateDisaster(
                    disaster
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Disaster updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadDisasters();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Severity must be a number between 1 and 5.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating disaster:\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // DELETE DISASTER
    // =========================================================

    private void deleteDisaster() {

        int selectedRow =
                disasterTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a disaster first.",
                    "No Disaster Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int disasterId =
                (int) tableModel.getValueAt(
                        selectedRow,
                        0
                );

        String disasterType =
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString();

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete\n"
                                + "Disaster ID: "
                                + disasterId
                                + "\nType: "
                                + disasterType
                                + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            disasterDAO.deleteDisaster(
                    disasterId
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Disaster deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadDisasters();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting disaster:\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // FORM PANEL
    // =========================================================

    private JPanel createFormPanel() {

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                5,
                                2,
                                12,
                                12
                        )
                );

        panel.setBorder(
                new EmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        return panel;
    }

    // =========================================================
    // SEVERITY RENDERER
    // =========================================================

    private static class SeverityRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            Component component =
                    super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            if (!isSelected && value != null) {

                int severity =
                        Integer.parseInt(
                                value.toString()
                        );

                if (severity >= 5) {

                    component.setForeground(
                            RED
                    );

                    setFont(
                            getFont().deriveFont(
                                    Font.BOLD
                            )
                    );

                } else if (severity >= 3) {

                    component.setForeground(
                            ORANGE
                    );

                    setFont(
                            getFont().deriveFont(
                                    Font.BOLD
                            )
                    );

                } else {

                    component.setForeground(
                            GREEN
                    );

                    setFont(
                            getFont().deriveFont(
                                    Font.BOLD
                            )
                    );
                }
            }

            return component;
        }
    }

    // =========================================================
    // STATUS RENDERER
    // =========================================================

    private static class StatusRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            Component component =
                    super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            if (!isSelected && value != null) {

                String status =
                        value.toString()
                                .toUpperCase();

                if (status.equals("ACTIVE")) {

                    component.setForeground(
                            RED
                    );

                } else if (
                        status.equals("RESOLVED")
                                || status.equals("CLOSED")
                ) {

                    component.setForeground(
                            GREEN
                    );

                } else {

                    component.setForeground(
                            ORANGE
                    );
                }

                setFont(
                        getFont().deriveFont(
                                Font.BOLD
                        )
                );
            }

            return component;
        }
    }
}