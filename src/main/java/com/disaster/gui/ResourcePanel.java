package com.disaster.gui;

import com.disaster.dao.ResourceDAO;
import com.disaster.model.Resource;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ResourcePanel extends JPanel {

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

        // =====================================================
        // TITLE + SUBTITLE
        // =====================================================

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
                        "Resource Management"
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
                        "Manage emergency supplies and resource availability"
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

        titlePanel.add(
                titleLabel
        );

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

        // =====================================================
        // RESOURCE COUNT
        // =====================================================

        resourceCountLabel =
                new JLabel(
                        "Resources: 0"
                );

        resourceCountLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        resourceCountLabel.setForeground(
                BLUE
        );

        resourceCountLabel.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        topPanel.add(
                resourceCountLabel,
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

                "Resource ID",
                "Type",
                "Name",
                "Quantity",
                "Location",
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

        resourceTable =
                new JTable(
                        tableModel
                );

        // =====================================================
        // TABLE STYLE
        // =====================================================

        resourceTable.setRowHeight(
                34
        );

        resourceTable.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        resourceTable.setForeground(
                TEXT
        );

        resourceTable.setBackground(
                Color.WHITE
        );

        resourceTable.setGridColor(
                new Color(
                        215,
                        220,
                        225
                )
        );

        resourceTable.setSelectionBackground(
                new Color(
                        220,
                        232,
                        245
                )
        );

        resourceTable.setSelectionForeground(
                TEXT
        );

        resourceTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        // =====================================================
        // TABLE HEADER
        // =====================================================

        resourceTable.getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                13
                        )
                );

        resourceTable.getTableHeader()
                .setBackground(
                        TABLE_HEADER
                );

        resourceTable.getTableHeader()
                .setForeground(
                        TEXT
                );

        resourceTable.getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                36
                        )
                );

        // =====================================================
        // COLUMN WIDTHS
        // =====================================================

        resourceTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(95);

        resourceTable.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(110);

        resourceTable.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(180);

        resourceTable.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(100);

        resourceTable.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(180);

        resourceTable.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(130);

        // =====================================================
        // CUSTOM RENDERERS
        // =====================================================

        resourceTable.getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        new QuantityRenderer()
                );

        resourceTable.getColumnModel()
                .getColumn(5)
                .setCellRenderer(
                        new StatusRenderer()
                );

        // =====================================================
        // SCROLL PANE
        // =====================================================

        JScrollPane scrollPane =
                new JScrollPane(
                        resourceTable
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
                        "Add Resource",
                        BLUE
                );

        JButton updateButton =
                createButton(
                        "Update Resource",
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
                        "Delete Resource",
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
                e -> showAddResourceDialog()
        );

        updateButton.addActionListener(
                e -> showUpdateResourceDialog()
        );

        refreshButton.addActionListener(
                e -> loadResources()
        );

        deleteButton.addActionListener(
                e -> deleteResource()
        );

        // =====================================================
        // LOAD DATA
        // =====================================================

        loadResources();
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
    // LOAD RESOURCES
    // =========================================================

    private void loadResources() {

        tableModel.setRowCount(0);

        List<Resource> resources =
                resourceDAO.getAllResources();

        for (Resource resource : resources) {

            tableModel.addRow(
                    new Object[]{

                            resource.getResourceId(),

                            resource.getResourceType(),

                            resource.getName(),

                            resource.getQuantity(),

                            resource.getLocation(),

                            resource.getStatus()
                    }
            );
        }

        resourceCountLabel.setText(
                "Resources: "
                        + resources.size()
        );
    }

    // =========================================================
    // ADD RESOURCE
    // =========================================================

    private void showAddResourceDialog() {

        JTextField typeField =
                new JTextField();

        JTextField nameField =
                new JTextField();

        JTextField quantityField =
                new JTextField();

        JTextField locationField =
                new JTextField();

        JPanel panel =
                createFormPanel();

        panel.add(
                new JLabel(
                        "Resource Type:"
                )
        );

        panel.add(
                typeField
        );

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
                        "Quantity:"
                )
        );

        panel.add(
                quantityField
        );

        panel.add(
                new JLabel(
                        "Location:"
                )
        );

        panel.add(
                locationField
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Add Resource",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            String type =
                    typeField
                            .getText()
                            .trim();

            String name =
                    nameField
                            .getText()
                            .trim();

            int quantity =
                    Integer.parseInt(
                            quantityField
                                    .getText()
                                    .trim()
                    );

            String location =
                    locationField
                            .getText()
                            .trim();

            // =================================================
            // VALIDATION
            // =================================================

            if (type.isEmpty()
                    || name.isEmpty()
                    || location.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please fill all fields.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (quantity < 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Quantity cannot be negative.",
                        "Invalid Quantity",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // =================================================
            // AUTOMATIC STATUS
            // =================================================

            String status;

            if (quantity == 0) {

                status = "OUT_OF_STOCK";

            } else {

                status = "AVAILABLE";
            }

            // =================================================
            // CREATE RESOURCE
            // =================================================

            Resource resource =
                    new Resource();

            resource.setResourceType(
                    type
            );

            resource.setName(
                    name
            );

            resource.setQuantity(
                    quantity
            );

            resource.setLocation(
                    location
            );

            resource.setStatus(
                    status
            );

            resourceDAO.addResource(
                    resource
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Resource added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadResources();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Quantity must be a number.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding resource:\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // UPDATE RESOURCE
    // =========================================================

    private void showUpdateResourceDialog() {

        int selectedRow =
                resourceTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a resource first.",
                    "No Resource Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int resourceId =
                (int) tableModel.getValueAt(
                        selectedRow,
                        0
                );

        String currentType =
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString();

        String currentName =
                tableModel.getValueAt(
                        selectedRow,
                        2
                ).toString();

        int currentQuantity =
                (int) tableModel.getValueAt(
                        selectedRow,
                        3
                );

        String currentLocation =
                tableModel.getValueAt(
                        selectedRow,
                        4
                ).toString();

        JTextField typeField =
                new JTextField(
                        currentType
                );

        JTextField nameField =
                new JTextField(
                        currentName
                );

        JTextField quantityField =
                new JTextField(
                        String.valueOf(
                                currentQuantity
                        )
                );

        JTextField locationField =
                new JTextField(
                        currentLocation
                );

        JPanel panel =
                createFormPanel();

        panel.add(
                new JLabel(
                        "Resource Type:"
                )
        );

        panel.add(
                typeField
        );

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
                        "Quantity:"
                )
        );

        panel.add(
                quantityField
        );

        panel.add(
                new JLabel(
                        "Location:"
                )
        );

        panel.add(
                locationField
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Update Resource ID "
                                + resourceId,
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            String type =
                    typeField
                            .getText()
                            .trim();

            String name =
                    nameField
                            .getText()
                            .trim();

            int quantity =
                    Integer.parseInt(
                            quantityField
                                    .getText()
                                    .trim()
                    );

            String location =
                    locationField
                            .getText()
                            .trim();

            // =================================================
            // VALIDATION
            // =================================================

            if (type.isEmpty()
                    || name.isEmpty()
                    || location.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please fill all fields.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (quantity < 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Quantity cannot be negative.",
                        "Invalid Quantity",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // =================================================
            // AUTOMATIC STATUS
            // =================================================

            String status;

            if (quantity == 0) {

                status = "OUT_OF_STOCK";

            } else {

                status = "AVAILABLE";
            }

            // =================================================
            // CREATE UPDATED RESOURCE
            // =================================================

            Resource resource =
                    new Resource();

            resource.setResourceId(
                    resourceId
            );

            resource.setResourceType(
                    type
            );

            resource.setName(
                    name
            );

            resource.setQuantity(
                    quantity
            );

            resource.setLocation(
                    location
            );

            resource.setStatus(
                    status
            );

            resourceDAO.updateResource(
                    resource
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Resource updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadResources();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Quantity must be a number.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating resource:\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // DELETE RESOURCE
    // =========================================================

    private void deleteResource() {

        int selectedRow =
                resourceTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a resource first.",
                    "No Resource Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int resourceId =
                (int) tableModel.getValueAt(
                        selectedRow,
                        0
                );

        String resourceName =
                tableModel.getValueAt(
                        selectedRow,
                        2
                ).toString();

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete\n"
                                + "Resource ID: "
                                + resourceId
                                + "\nName: "
                                + resourceName
                                + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            resourceDAO.deleteResource(
                    resourceId
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Resource deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadResources();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting resource:\n"
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
    // QUANTITY RENDERER
    // =========================================================

    private static class QuantityRenderer
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

                int quantity =
                        Integer.parseInt(
                                value.toString()
                        );

                if (quantity == 0) {

                    component.setForeground(
                            RED
                    );

                } else if (quantity <= 10) {

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

                if (status.equals(
                        "AVAILABLE"
                )) {

                    component.setForeground(
                            GREEN
                    );

                } else if (status.equals(
                        "OUT_OF_STOCK"
                )) {

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