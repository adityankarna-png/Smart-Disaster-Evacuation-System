package com.disaster.gui;

import com.disaster.dao.ShelterDAO;
import com.disaster.model.Shelter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ShelterPanel extends JPanel {

    // =========================================================
    // COLORS
    // =========================================================

    private static final Color BACKGROUND =
            new Color(245, 247, 250);

    private static final Color TEXT =
            new Color(35, 45, 55);

    private static final Color BLUE =
            new Color(52, 105, 170);

    private static final Color GREEN =
            new Color(45, 150, 90);

    private static final Color ORANGE =
            new Color(235, 145, 35);

    private static final Color RED =
            new Color(210, 65, 65);

    private static final Color TABLE_HEADER =
            new Color(235, 240, 245);

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

        // =====================================================
        // TOP SECTION
        // =====================================================

        JPanel topPanel =
                new JPanel(
                        new BorderLayout()
                );

        topPanel.setBackground(
                BACKGROUND
        );

        topPanel.setBorder(
                new EmptyBorder(
                        18,
                        25,
                        10,
                        25
                )
        );

        // -----------------------------------------------------
        // TITLE
        // -----------------------------------------------------

        JPanel titlePanel =
                new JPanel();

        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS
                )
        );

        titlePanel.setBackground(
                BACKGROUND
        );

        JLabel titleLabel =
                new JLabel(
                        "Shelter Management"
                );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        titleLabel.setForeground(
                TEXT
        );

        JLabel subtitleLabel =
                new JLabel(
                        "Manage emergency shelters, capacity and occupancy"
                );

        subtitleLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        subtitleLabel.setForeground(
                new Color(
                        100,
                        110,
                        125
                )
        );

        titlePanel.add(titleLabel);

        titlePanel.add(
                Box.createVerticalStrut(4)
        );

        titlePanel.add(
                subtitleLabel
        );

        topPanel.add(
                titlePanel,
                BorderLayout.WEST
        );

        // -----------------------------------------------------
        // SHELTER COUNT
        // -----------------------------------------------------

        shelterCountLabel =
                new JLabel(
                        "Shelters: 0"
                );

        shelterCountLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        shelterCountLabel.setForeground(
                BLUE
        );

        shelterCountLabel.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        topPanel.add(
                shelterCountLabel,
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

                "Shelter ID",
                "Name",
                "Location",
                "Capacity",
                "Occupancy",
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

        shelterTable =
                new JTable(
                        tableModel
                );

        // =====================================================
        // TABLE STYLE
        // =====================================================

        shelterTable.setRowHeight(
                34
        );

        shelterTable.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        shelterTable.setForeground(
                TEXT
        );

        shelterTable.setBackground(
                Color.WHITE
        );

        shelterTable.setGridColor(
                new Color(
                        215,
                        220,
                        225
                )
        );

        shelterTable.setSelectionBackground(
                new Color(
                        220,
                        232,
                        245
                )
        );

        shelterTable.setSelectionForeground(
                TEXT
        );

        shelterTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        // =====================================================
        // TABLE HEADER
        // =====================================================

        shelterTable.getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                13
                        )
                );

        shelterTable.getTableHeader()
                .setBackground(
                        TABLE_HEADER
                );

        shelterTable.getTableHeader()
                .setForeground(
                        TEXT
                );

        shelterTable.getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                36
                        )
                );

        // =====================================================
        // COLUMN WIDTHS
        // =====================================================

        shelterTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(90);

        shelterTable.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(190);

        shelterTable.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(150);

        shelterTable.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(100);

        shelterTable.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(110);

        shelterTable.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(120);

        // =====================================================
        // CUSTOM RENDERERS
        // =====================================================

        shelterTable.getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        new OccupancyRenderer()
                );

        shelterTable.getColumnModel()
                .getColumn(5)
                .setCellRenderer(
                        new StatusRenderer()
                );

        // =====================================================
        // SCROLL PANE
        // =====================================================

        JScrollPane scrollPane =
                new JScrollPane(
                        shelterTable
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
                .setBackground(
                        Color.WHITE
                );

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
                        "Add Shelter",
                        BLUE
                );

        JButton updateButton =
                createButton(
                        "Update Shelter",
                        new Color(
                                70,
                                120,
                                175
                        )
                );

        JButton refreshButton =
                createButton(
                        "Refresh",
                        new Color(
                                100,
                                110,
                                120
                        )
                );

        JButton deleteButton =
                createButton(
                        "Delete Shelter",
                        RED
                );

        buttonPanel.add(
                addButton
        );

        buttonPanel.add(
                updateButton
        );

        buttonPanel.add(
                refreshButton
        );

        buttonPanel.add(
                deleteButton
        );

        add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        // =====================================================
        // BUTTON ACTIONS
        // =====================================================

        addButton.addActionListener(
                e -> showAddShelterDialog()
        );

        updateButton.addActionListener(
                e -> showUpdateShelterDialog()
        );

        refreshButton.addActionListener(
                e -> loadShelters()
        );

        deleteButton.addActionListener(
                e -> deleteShelter()
        );

        // =====================================================
        // LOAD DATA
        // =====================================================

        loadShelters();
    }

    // =========================================================
    // CREATE BUTTON
    // =========================================================

    private JButton createButton(
            String text,
            Color color
    ) {

        JButton button =
                new JButton(
                        text
                );

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(
                Color.WHITE
        );

        button.setBackground(
                color
        );

        button.setFocusPainted(
                false
        );

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
    // LOAD SHELTERS
    // =========================================================

    private void loadShelters() {

        tableModel.setRowCount(0);

        List<Shelter> shelters =
                shelterDAO.getAllShelters();

        for (Shelter shelter : shelters) {

            tableModel.addRow(
                    new Object[]{

                            shelter.getShelterId(),

                            shelter.getName(),

                            shelter.getLocation(),

                            shelter.getCapacity(),

                            shelter.getCurrentOccupancy(),

                            shelter.getStatus()
                    }
            );
        }

        shelterCountLabel.setText(
                "Shelters: "
                        + shelters.size()
        );
    }

    // =========================================================
    // ADD SHELTER
    // =========================================================

    private void showAddShelterDialog() {

        JTextField nameField =
                new JTextField();

        JTextField locationField =
                new JTextField();

        JTextField capacityField =
                new JTextField();

        JTextField occupancyField =
                new JTextField("0");

        JPanel panel =
                createFormPanel();

        panel.add(
                new JLabel(
                        "Name:"
                )
        );

        panel.add(
                nameField
        );

        panel.add(
                new JLabel(
                        "Location:"
                )
        );

        panel.add(
                locationField
        );

        panel.add(
                new JLabel(
                        "Capacity:"
                )
        );

        panel.add(
                capacityField
        );

        panel.add(
                new JLabel(
                        "Current Occupancy:"
                )
        );

        panel.add(
                occupancyField
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Add Shelter",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            String name =
                    nameField
                            .getText()
                            .trim();

            String location =
                    locationField
                            .getText()
                            .trim();

            int capacity =
                    Integer.parseInt(
                            capacityField
                                    .getText()
                                    .trim()
                    );

            int occupancy =
                    Integer.parseInt(
                            occupancyField
                                    .getText()
                                    .trim()
                    );

            // -------------------------------------------------
            // VALIDATION
            // -------------------------------------------------

            if (name.isEmpty()
                    || location.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please fill all fields.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (capacity <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Capacity must be greater than 0.",
                        "Invalid Capacity",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (occupancy < 0
                    || occupancy > capacity) {

                JOptionPane.showMessageDialog(
                        this,
                        "Occupancy must be between 0 and capacity.",
                        "Invalid Occupancy",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // -------------------------------------------------
            // AUTOMATIC STATUS
            // -------------------------------------------------

            String status;

            if (occupancy >= capacity) {

                status = "FULL";

            } else {

                status = "AVAILABLE";
            }

            Shelter shelter =
                    new Shelter();

            shelter.setName(
                    name
            );

            shelter.setLocation(
                    location
            );

            shelter.setCapacity(
                    capacity
            );

            shelter.setCurrentOccupancy(
                    occupancy
            );

            shelter.setStatus(
                    status
            );

            shelterDAO.addShelter(
                    shelter
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Shelter added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadShelters();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Capacity and occupancy must be numbers.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding shelter:\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // UPDATE SHELTER
    // =========================================================

    private void showUpdateShelterDialog() {

        int selectedRow =
                shelterTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a shelter first.",
                    "No Shelter Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int shelterId =
                (int) tableModel.getValueAt(
                        selectedRow,
                        0
                );

        String currentName =
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString();

        String currentLocation =
                tableModel.getValueAt(
                        selectedRow,
                        2
                ).toString();

        int currentCapacity =
                (int) tableModel.getValueAt(
                        selectedRow,
                        3
                );

        int currentOccupancy =
                (int) tableModel.getValueAt(
                        selectedRow,
                        4
                );

        JTextField nameField =
                new JTextField(
                        currentName
                );

        JTextField locationField =
                new JTextField(
                        currentLocation
                );

        JTextField capacityField =
                new JTextField(
                        String.valueOf(
                                currentCapacity
                        )
                );

        JTextField occupancyField =
                new JTextField(
                        String.valueOf(
                                currentOccupancy
                        )
                );

        JPanel panel =
                createFormPanel();

        panel.add(
                new JLabel(
                        "Name:"
                )
        );

        panel.add(
                nameField
        );

        panel.add(
                new JLabel(
                        "Location:"
                )
        );

        panel.add(
                locationField
        );

        panel.add(
                new JLabel(
                        "Capacity:"
                )
        );

        panel.add(
                capacityField
        );

        panel.add(
                new JLabel(
                        "Current Occupancy:"
                )
        );

        panel.add(
                occupancyField
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Update Shelter ID "
                                + shelterId,
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            String name =
                    nameField
                            .getText()
                            .trim();

            String location =
                    locationField
                            .getText()
                            .trim();

            int capacity =
                    Integer.parseInt(
                            capacityField
                                    .getText()
                                    .trim()
                    );

            int occupancy =
                    Integer.parseInt(
                            occupancyField
                                    .getText()
                                    .trim()
                    );

            // -------------------------------------------------
            // VALIDATION
            // -------------------------------------------------

            if (name.isEmpty()
                    || location.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please fill all fields.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (capacity <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Capacity must be greater than 0.",
                        "Invalid Capacity",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (occupancy < 0
                    || occupancy > capacity) {

                JOptionPane.showMessageDialog(
                        this,
                        "Occupancy must be between 0 and capacity.",
                        "Invalid Occupancy",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // -------------------------------------------------
            // AUTOMATIC STATUS
            // -------------------------------------------------

            String status;

            if (occupancy >= capacity) {

                status = "FULL";

            } else {

                status = "AVAILABLE";
            }

            Shelter shelter =
                    new Shelter();

            shelter.setShelterId(
                    shelterId
            );

            shelter.setName(
                    name
            );

            shelter.setLocation(
                    location
            );

            shelter.setCapacity(
                    capacity
            );

            shelter.setCurrentOccupancy(
                    occupancy
            );

            shelter.setStatus(
                    status
            );

            shelterDAO.updateShelter(
                    shelter
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Shelter updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadShelters();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Capacity and occupancy must be numbers.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating shelter:\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // DELETE SHELTER
    // =========================================================

    private void deleteShelter() {

        int selectedRow =
                shelterTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a shelter first.",
                    "No Shelter Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int shelterId =
                (int) tableModel.getValueAt(
                        selectedRow,
                        0
                );

        String shelterName =
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString();

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete\n"
                                + "Shelter ID: "
                                + shelterId
                                + "\nName: "
                                + shelterName
                                + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            shelterDAO.deleteShelter(
                    shelterId
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Shelter deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadShelters();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting shelter:\n"
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
                                4,
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
    // OCCUPANCY RENDERER
    // =========================================================

    private static class OccupancyRenderer
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

                int occupancy =
                        Integer.parseInt(
                                value.toString()
                        );

                int capacity =
                        Integer.parseInt(
                                table.getValueAt(
                                        row,
                                        3
                                ).toString()
                        );

                double percentage =
                        (double) occupancy
                                / capacity
                                * 100;

                if (percentage >= 100) {

                    component.setForeground(
                            RED
                    );

                } else if (percentage >= 80) {

                    component.setForeground(
                            ORANGE
                    );

                } else {

                    component.setForeground(
                            GREEN
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

                if (status.equals("AVAILABLE")) {

                    component.setForeground(
                            GREEN
                    );

                } else if (status.equals("FULL")) {

                    component.setForeground(
                            RED
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