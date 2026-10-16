package com.disaster.gui;

import com.disaster.dao.ResourceDAO;
import com.disaster.model.Resource;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ResourcePanel extends JPanel {

    // =========================================================
    // DARK DISASTER DASHBOARD DESIGN SYSTEM
    // =========================================================

    private static final Color BG       = new Color(0x08, 0x18, 0x21);
    private static final Color PANEL    = new Color(0x13, 0x2B, 0x3B);
    private static final Color PANEL2   = new Color(0x18, 0x35, 0x4A);
    private static final Color LINE     = new Color(0x2A, 0x4D, 0x63);
    private static final Color TEXT     = new Color(0xE8, 0xF0, 0xF6);
    private static final Color MUTED    = new Color(0x8D, 0xA2, 0xB3);
    private static final Color BLUE     = new Color(0x55, 0xB4, 0xFF);
    private static final Color GREEN    = new Color(0x3D, 0xD4, 0xA0);
    private static final Color AMBER    = new Color(0xF4, 0xB4, 0x4B);
    private static final Color RED      = new Color(0xFF, 0x5B, 0x68);
    private static final Color VIOLET   = new Color(0xA5, 0x95, 0xFF);
    private static final Color BROWN    = new Color(0xD0, 0x8B, 0x5B);
    private static final Color TEAL     = new Color(0x3F, 0xC7, 0xC7);
    private static final Color SEL      = new Color(0x1E, 0x3D, 0x57);
    private static final Color HOVER    = new Color(0x18, 0x2F, 0x44);

    private static final String[] FILTERS = {"All", "Available", "Low stock", "Out of stock"};

    private JTable resourceTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private JLabel resourceCountLabel;
    private JLabel totalUnitsLabel;
    private JLabel availableLabel;
    private JLabel restockLabel;
    private JLabel infoLabel;
    private StatCard itemsCard;

    private JTextField searchField;
    private SegmentedFilter filterBox;

    private JButton refreshButton;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;

    private final ResourceDAO resourceDAO;

    public ResourcePanel() {
        resourceDAO = new ResourceDAO();

        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        setBorder(new EmptyBorder(26, 32, 22, 32));

        add(buildTop(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        loadResources();
    }

    // =========================================================
    // TOP SECTION
    // =========================================================

    private JPanel buildTop() {
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel title = label("Resource Management", Font.BOLD, 28, TEXT);
        JLabel subtitle = label(
                "Manage emergency supplies and resource availability",
                Font.PLAIN, 14, MUTED
        );

        titles.add(title);
        titles.add(Box.createVerticalStrut(4));
        titles.add(subtitle);

        JPanel chip = new RoundedChip();
        resourceCountLabel = label("0 resources", Font.BOLD, 12, BLUE);
        chip.add(resourceCountLabel);

        header.add(titles, BorderLayout.WEST);

        JPanel chipWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        chipWrap.setOpaque(false);
        chipWrap.add(chip);
        header.add(chipWrap, BorderLayout.EAST);

        top.add(header);
        top.add(Box.createVerticalStrut(18));

        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        StatCard items = new StatCard("Resource items", "Registered", BLUE);
        itemsCard = items;
        StatCard units = new StatCard("Total units", "Units in stock", AMBER);
        StatCard available = new StatCard("Available", "Ready to deploy", GREEN);
        StatCard restock = new StatCard("Needs restock", "Low or out of stock", RED);

        totalUnitsLabel = units.value;
        availableLabel = available.value;
        restockLabel = restock.value;

        stats.add(items);
        stats.add(units);
        stats.add(available);
        stats.add(restock);

        top.add(stats);
        top.add(Box.createVerticalStrut(16));

        JPanel toolbar = new JPanel(new BorderLayout(16, 0));
        toolbar.setOpaque(false);
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        searchField = new JTextField();
        searchField.setToolTipText("Search type, name or location");
        styleTextField(searchField);
        searchField.setPreferredSize(new Dimension(340, 40));

        JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchWrap.setOpaque(false);
        searchWrap.add(searchField);
        toolbar.add(searchWrap, BorderLayout.WEST);

        filterBox = new SegmentedFilter();
        filterBox.setPreferredSize(new Dimension(434, 40));

        JPanel filterWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filterWrap.setOpaque(false);
        filterWrap.add(filterBox);
        toolbar.add(filterWrap, BorderLayout.EAST);

        top.add(toolbar);
        top.add(Box.createVerticalStrut(14));

        // Keep the first card accessible for live updates.
        top.putClientProperty("itemsCard", items);

        return top;
    }

    // =========================================================
    // TABLE CARD
    // =========================================================

    private JPanel buildTableCard() {
        RoundedPanel card = new RoundedPanel(PANEL);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(6, 6, 6, 6));

        String[] columns = {
                "ID", "TYPE", "NAME", "QUANTITY", "LOCATION", "STATUS"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 || column == 3 ? Integer.class : String.class;
            }
        };

        resourceTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);

        resourceTable.setRowSorter(sorter);
        resourceTable.setRowHeight(56);
        resourceTable.setShowGrid(false);
        resourceTable.setIntercellSpacing(new Dimension(0, 0));
        resourceTable.setFillsViewportHeight(true);
        resourceTable.setBackground(PANEL);
        resourceTable.setForeground(TEXT);
        resourceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resourceTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        ResourceCell renderer = new ResourceCell();

        int[] widths = {88, 153, 271, 132, 261, 171};
        for (int i = 0; i < widths.length; i++) {
            TableColumn column = resourceTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths[i]);
            column.setCellRenderer(renderer);
        }

        JTableHeader header = resourceTable.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setPreferredSize(new Dimension(0, 42));
        header.setBackground(PANEL);
        header.setForeground(MUTED);
        header.setDefaultRenderer(new HeaderRenderer());

        JScrollPane scroll = new JScrollPane(
                resourceTable,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(PANEL);

        card.add(scroll, BorderLayout.CENTER);

        resourceTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = resourceTable.rowAtPoint(e.getPoint());
                setHover(row);
            }
        });

        resourceTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                setHover(-1);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && resourceTable.rowAtPoint(e.getPoint()) >= 0) {
                    showUpdateResourceDialog();
                }
            }
        });

        resourceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtons();
        });

        resourceTable.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "editResource");
        resourceTable.getActionMap().put("editResource", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUpdateResourceDialog();
            }
        });

        resourceTable.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteResource");
        resourceTable.getActionMap().put("deleteResource", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteResource();
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });

        filterBox.onChange = this::applyFilter;

        return card;
    }

    // =========================================================
    // FOOTER
    // =========================================================

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 0, 0, 0));

        infoLabel = label("Showing 0 resources", Font.PLAIN, 13, MUTED);
        footer.add(infoLabel, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        refreshButton = darkButton("Refresh", PANEL2, TEXT);
        addButton = darkButton("Add Resource", BLUE, new Color(0x06, 0x22, 0x3A));
        updateButton = darkButton("Update Resource", PANEL2, TEXT);
        deleteButton = outlineButton("Delete Resource", RED);

        refreshButton.addActionListener(e -> loadResources());
        addButton.addActionListener(e -> showAddResourceDialog());
        updateButton.addActionListener(e -> showUpdateResourceDialog());
        deleteButton.addActionListener(e -> deleteResource());

        actions.add(refreshButton);
        actions.add(addButton);
        actions.add(updateButton);
        actions.add(deleteButton);

        footer.add(actions, BorderLayout.EAST);

        return footer;
    }

    // =========================================================
    // LOAD / FILTER
    // =========================================================

    private void loadResources() {
        tableModel.setRowCount(0);

        try {
            List<Resource> resources = resourceDAO.getAllResources();

            int totalUnits = 0;
            int available = 0;
            int restock = 0;

            for (Resource resource : resources) {
                tableModel.addRow(new Object[]{
                        resource.getResourceId(),
                        resource.getResourceType(),
                        resource.getName(),
                        resource.getQuantity(),
                        resource.getLocation(),
                        resource.getStatus()
                });

                totalUnits += resource.getQuantity();

                String status = resource.getStatus();
                if ("AVAILABLE".equalsIgnoreCase(status)) {
                    available++;
                }

                if ("OUT_OF_STOCK".equalsIgnoreCase(status)
                        || resource.getQuantity() <= 10) {
                    restock++;
                }
            }

            resourceCountLabel.setText(resources.size() + " "
                    + (resources.size() == 1 ? "resource" : "resources"));

            int lowStock = 0;
            int outOfStock = 0;
            for (Resource resource : resources) {
                String status = resource.getStatus() == null ? "" : resource.getStatus().toUpperCase();
                if ("OUT_OF_STOCK".equals(status) || resource.getQuantity() == 0) {
                    outOfStock++;
                } else if (resource.getQuantity() > 0 && resource.getQuantity() <= 10) {
                    lowStock++;
                }
            }
            filterBox.setLabels(new String[]{
                    "All " + resources.size(),
                    "Available " + available,
                    "Low stock " + lowStock,
                    "Out of stock " + outOfStock
            });

            if (itemsCard != null) {
                itemsCard.value.setText(String.valueOf(resources.size()));
            }

            totalUnitsLabel.setText(formatNumber(totalUnits));
            availableLabel.setText(String.valueOf(available));
            restockLabel.setText(String.valueOf(restock));

            // Needs-restock card turns green when nothing needs attention.
            restockLabel.setForeground(restock == 0 ? GREEN : RED);

            applyFilter();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading resources:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void applyFilter() {
        if (sorter == null) return;

        final String query = searchField.getText().trim().toLowerCase();
        final int selected = filterBox.getSelectedIndex();

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                int modelRow = entry.getIdentifier();

                String type = String.valueOf(tableModel.getValueAt(modelRow, 1));
                String name = String.valueOf(tableModel.getValueAt(modelRow, 2));
                String location = String.valueOf(tableModel.getValueAt(modelRow, 4));
                String status = String.valueOf(tableModel.getValueAt(modelRow, 5));
                int quantity = ((Number) tableModel.getValueAt(modelRow, 3)).intValue();

                if (!query.isEmpty()) {
                    String combined = type + " " + name + " " + location;
                    if (!combined.toLowerCase().contains(query)) return false;
                }

                if (selected == 1) {
                    return "AVAILABLE".equalsIgnoreCase(status);
                }

                if (selected == 2) {
                    return quantity > 0 && quantity <= 10;
                }

                if (selected == 3) {
                    return "OUT_OF_STOCK".equalsIgnoreCase(status) || quantity == 0;
                }

                return true;
            }
        });

        updateInfo();
        updateButtons();
    }

    private void updateInfo() {
        int shown = resourceTable.getRowCount();
        int total = tableModel.getRowCount();

        infoLabel.setText(
                "Showing " + shown + " of " + total
                        + (total == 1 ? " resource" : " resources")
        );
    }

    private void setHover(int row) {
        Integer current = (Integer) resourceTable.getClientProperty("hoverRow");
        if (current == null || current != row) {
            resourceTable.putClientProperty("hoverRow", row);
            resourceTable.repaint();
        }
    }

    // =========================================================
    // ADD RESOURCE
    // =========================================================

    private void showAddResourceDialog() {
        ResourceDialog dialog = new ResourceDialog(
                SwingUtilities.getWindowAncestor(this),
                "Add resource",
                "Register supplies or equipment for the response.",
                null
        );

        dialog.setVisible(true);

        if (!dialog.saved) return;

        try {
            Resource resource = new Resource();
            resource.setResourceType(dialog.typeField.getText().trim());
            resource.setName(dialog.nameField.getText().trim());
            resource.setQuantity(dialog.quantity());
            resource.setLocation(dialog.locationField.getText().trim());

            // Preserve existing automatic status logic.
            resource.setStatus(dialog.quantity() == 0
                    ? "OUT_OF_STOCK"
                    : "AVAILABLE");

            resourceDAO.addResource(resource);

            JOptionPane.showMessageDialog(
                    this,
                    "Resource added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadResources();

        } catch (NumberFormatException e) {
            showError("Quantity must be a number.");
        } catch (Exception e) {
            showError("Error adding resource:\n" + e.getMessage());
        }
    }

    // =========================================================
    // UPDATE RESOURCE
    // =========================================================

    private void showUpdateResourceDialog() {
        int selectedRow = resourceTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a resource first.",
                    "No Resource Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = resourceTable.convertRowIndexToModel(selectedRow);

        int resourceId = ((Number) tableModel.getValueAt(modelRow, 0)).intValue();
        String currentType = String.valueOf(tableModel.getValueAt(modelRow, 1));
        String currentName = String.valueOf(tableModel.getValueAt(modelRow, 2));
        int currentQuantity = ((Number) tableModel.getValueAt(modelRow, 3)).intValue();
        String currentLocation = String.valueOf(tableModel.getValueAt(modelRow, 4));

        Resource existing = new Resource();
        existing.setResourceId(resourceId);
        existing.setResourceType(currentType);
        existing.setName(currentName);
        existing.setQuantity(currentQuantity);
        existing.setLocation(currentLocation);

        ResourceDialog dialog = new ResourceDialog(
                SwingUtilities.getWindowAncestor(this),
                "Update resource",
                "Edit the details of resource #" + resourceId + ".",
                existing
        );

        dialog.setVisible(true);

        if (!dialog.saved) return;

        try {
            Resource resource = new Resource();
            resource.setResourceId(resourceId);
            resource.setResourceType(dialog.typeField.getText().trim());
            resource.setName(dialog.nameField.getText().trim());
            resource.setQuantity(dialog.quantity());
            resource.setLocation(dialog.locationField.getText().trim());

            // Preserve existing automatic status logic.
            resource.setStatus(dialog.quantity() == 0
                    ? "OUT_OF_STOCK"
                    : "AVAILABLE");

            resourceDAO.updateResource(resource);

            JOptionPane.showMessageDialog(
                    this,
                    "Resource updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadResources();

        } catch (NumberFormatException e) {
            showError("Quantity must be a number.");
        } catch (Exception e) {
            showError("Error updating resource:\n" + e.getMessage());
        }
    }

    // =========================================================
    // DELETE RESOURCE
    // =========================================================

    private void deleteResource() {
        int selectedRow = resourceTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a resource first.",
                    "No Resource Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = resourceTable.convertRowIndexToModel(selectedRow);

        int resourceId = ((Number) tableModel.getValueAt(modelRow, 0)).intValue();
        String resourceName = String.valueOf(tableModel.getValueAt(modelRow, 2));

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete\nResource ID: "
                        + resourceId + "\nName: " + resourceName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            resourceDAO.deleteResource(resourceId);

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
                    "Error deleting resource:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateButtons() {
        boolean hasSelection = resourceTable != null && resourceTable.getSelectedRow() >= 0;
        if (updateButton != null) updateButton.setEnabled(hasSelection);
        if (deleteButton != null) deleteButton.setEnabled(hasSelection);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================
    // CUSTOM RESOURCE DIALOG
    // =========================================================

    private static class ResourceDialog extends JDialog {

        final JTextField typeField = field("");
        final JTextField nameField = field("");
        final JTextField quantityField = field("");
        final JTextField locationField = field("");

        boolean saved = false;

        private final JLabel quantityHint = label("", Font.PLAIN, 12, AMBER);
        private final JLabel errorLabel = label("", Font.PLAIN, 12, RED);
        private final StatusSegment statusSegment;

        ResourceDialog(
                Window owner,
                String title,
                String subtitle,
                Resource existing
        ) {
            super(owner, title, ModalityType.APPLICATION_MODAL);

            setUndecorated(true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(false);

            JPanel root = new JPanel(null);
            root.setBackground(BG);
            root.setPreferredSize(new Dimension(480, 528));

            // -------------------------------------------------
            // Heading
            // -------------------------------------------------

            JLabel heading = label(title, Font.BOLD, 22, TEXT);
            heading.setBounds(28, 24, 424, 30);
            root.add(heading);

            JLabel sub = label(subtitle, Font.PLAIN, 13, MUTED);
            sub.setBounds(28, 55, 424, 20);
            root.add(sub);

            // -------------------------------------------------
            // Type
            // -------------------------------------------------

            JLabel typeLabel = label("Type", Font.BOLD, 12, MUTED);
            typeLabel.setBounds(28, 89, 424, 18);
            root.add(typeLabel);

            typeField.setBounds(28, 109, 424, 40);
            root.add(typeField);

            // -------------------------------------------------
            // Quick type buttons
            // -------------------------------------------------

            int x = 28;
            String[] types = {"Food", "Water", "Medical", "Shelter", "Clothing"};

            for (String type : types) {
                JButton pill = smallPill(type);
                Dimension d = pill.getPreferredSize();
                pill.setBounds(x, 157, d.width, 28);
                pill.addActionListener(e -> typeField.setText(type.toUpperCase()));
                root.add(pill);
                x += d.width + 7;
            }

            // -------------------------------------------------
            // Name
            // -------------------------------------------------

            JLabel nameLabel = label("Name", Font.BOLD, 12, MUTED);
            nameLabel.setBounds(28, 198, 424, 18);
            root.add(nameLabel);

            nameField.setBounds(28, 217, 424, 40);
            root.add(nameField);

            // -------------------------------------------------
            // Quantity / Location
            // -------------------------------------------------

            JLabel quantityLabel = label("Quantity", Font.BOLD, 12, MUTED);
            quantityLabel.setBounds(28, 272, 205, 18);
            root.add(quantityLabel);

            JLabel locationLabel = label("Location", Font.BOLD, 12, MUTED);
            locationLabel.setBounds(246, 272, 206, 18);
            root.add(locationLabel);

            quantityField.setBounds(28, 291, 205, 40);
            root.add(quantityField);

            locationField.setBounds(246, 291, 206, 40);
            root.add(locationField);

            // -------------------------------------------------
            // Quantity hint
            // -------------------------------------------------

            quantityHint.setBounds(28, 334, 424, 20);
            root.add(quantityHint);

            errorLabel.setBounds(28, 334, 424, 20);
            errorLabel.setVisible(false);
            root.add(errorLabel);

            // -------------------------------------------------
            // Status
            // -------------------------------------------------

            JLabel statusLabel = label("Status", Font.BOLD, 12, MUTED);
            statusLabel.setBounds(28, 366, 424, 18);
            root.add(statusLabel);

            int selectedStatus = 0;

            if (existing != null) {
                int q = existing.getQuantity();
                selectedStatus = q == 0 ? 2 : q <= 10 ? 1 : 0;
            }

            statusSegment = new StatusSegment(selectedStatus);
            statusSegment.setBounds(28, 386, 330, 38);
            root.add(statusSegment);

            // -------------------------------------------------
            // Buttons
            // -------------------------------------------------

            JButton cancel = dialogButton("Cancel", PANEL2, TEXT);
            cancel.setBounds(209, 459, 84, 38);
            root.add(cancel);

            JButton save = dialogButton(
                    existing == null ? "Add resource" : "Save changes",
                    BLUE,
                    new Color(0x06, 0x22, 0x3A)
            );
            save.setBounds(304, 459, 137, 38);
            root.add(save);

            // -------------------------------------------------
            // Existing values
            // -------------------------------------------------

            if (existing != null) {
                typeField.setText(existing.getResourceType());
                nameField.setText(existing.getName());
                quantityField.setText(String.valueOf(existing.getQuantity()));
                locationField.setText(existing.getLocation());
            }

            // -------------------------------------------------
            // Actions
            // -------------------------------------------------

            cancel.addActionListener(e -> dispose());

            save.addActionListener(e -> {
                if (!validateFields()) return;
                saved = true;
                dispose();
            });

            quantityField.getDocument().addDocumentListener(new DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) { refreshStatus(); }
                @Override public void removeUpdate(DocumentEvent e) { refreshStatus(); }
                @Override public void changedUpdate(DocumentEvent e) { refreshStatus(); }
            });

            // ESC closes the dialog.
            root.registerKeyboardAction(
                    e -> dispose(),
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW
            );

            setContentPane(root);
            pack();
            setLocationRelativeTo(owner);

            refreshStatus();
        }

        private boolean validateFields() {
            String type = typeField.getText().trim();
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();

            if (type.isEmpty()) {
                fail("Resource type is required.", typeField);
                return false;
            }

            if (name.isEmpty()) {
                fail("Resource name is required.", nameField);
                return false;
            }

            if (location.isEmpty()) {
                fail("Location is required.", locationField);
                return false;
            }

            int q = quantity();

            if (q < 0) {
                fail("Quantity must be a whole number (0 or more).", quantityField);
                return false;
            }

            errorLabel.setVisible(false);
            quantityHint.setVisible(true);
            return true;
        }

        private int quantity() {
            try {
                return Integer.parseInt(quantityField.getText().trim());
            } catch (Exception e) {
                return -1;
            }
        }

        private void refreshStatus() {
            int q = quantity();

            errorLabel.setVisible(false);
            quantityHint.setVisible(true);

            if (q == 0) {
                statusSegment.setSelected(2);
                quantityHint.setText(
                        "Quantity is 0. The resource will be marked Out of stock."
                );
                quantityHint.setForeground(RED);
            } else if (q > 0 && q <= 10) {
                statusSegment.setSelected(1);
                quantityHint.setText(
                        "Low quantity. Review this resource for restocking."
                );
                quantityHint.setForeground(AMBER);
            } else if (q > 10) {
                statusSegment.setSelected(0);
                quantityHint.setText("Resource is ready to deploy.");
                quantityHint.setForeground(GREEN);
            } else {
                statusSegment.setSelected(0);
                quantityHint.setText("Enter a whole number.");
                quantityHint.setForeground(AMBER);
            }
        }

        private void fail(String message, JComponent focus) {
            quantityHint.setVisible(false);
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            focus.requestFocusInWindow();
        }
    }

    private static class StatusSegment extends JComponent {

        private final String[] labels = {"Available", "Low stock", "Out of stock"};
        private int selected;

        StatusSegment(int selected) {
            this.selected = Math.max(0, Math.min(2, selected));
            setPreferredSize(new Dimension(330, 38));
            setMaximumSize(new Dimension(330, 38));
            setOpaque(false);
        }

        void setSelected(int index) {
            selected = Math.max(0, Math.min(2, index));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);

            int w = getWidth();
            int h = getHeight();

            g.setColor(PANEL2);
            g.fillRoundRect(0, 0, w - 1, h - 1, h, h);

            g.setColor(LINE);
            g.drawRoundRect(0, 0, w - 1, h - 1, h, h);

            int[] widths = {100, 105, 125};
            int x = 3;

            for (int i = 0; i < labels.length; i++) {
                int sw = widths[i];

                if (i == selected) {
                    g.setColor(BLUE);
                    g.fillRoundRect(
                            x, 3,
                            sw, h - 7,
                            h - 7, h - 7
                    );

                    g.setColor(new Color(255, 255, 255, 32));
                    g.drawRoundRect(
                            x, 3,
                            sw - 1, h - 8,
                            h - 8, h - 8
                    );
                }

                g.setFont(font(Font.BOLD, 12));
                FontMetrics fm = g.getFontMetrics();

                g.setColor(i == selected ? TEXT : MUTED);

                int tx = x + (sw - fm.stringWidth(labels[i])) / 2;
                int ty = (h + fm.getAscent() - fm.getDescent()) / 2;

                g.drawString(labels[i], tx, ty);
                x += sw;
            }

            g.dispose();
        }
    }

    // =========================================================
    // TABLE RENDERER
    // =========================================================

    private class ResourceCell extends JComponent implements TableCellRenderer {

        private String text = "";
        private int kind;
        private int quantity;
        private Color accent = MUTED;
        private boolean selected;
        private boolean hover;
        private int column;

        private static final int ID = 0;
        private static final int TYPE = 1;
        private static final int NAME = 2;
        private static final int QTY = 3;
        private static final int TEXT_KIND = 4;
        private static final int STATUS = 5;

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            Object hoverObject = table.getClientProperty("hoverRow");
            int hoverRow = hoverObject instanceof Integer
                    ? (Integer) hoverObject
                    : -1;

            hover = hoverRow == row;
            selected = isSelected;
            this.column = column;
            text = value == null ? "" : String.valueOf(value);
            accent = MUTED;

            switch (column) {
                case 0:
                    kind = ID;
                    text = "#" + text;
                    break;

                case 1:
                    kind = TYPE;
                    accent = typeColor(text);
                    break;

                case 2:
                    kind = NAME;
                    break;

                case 3:
                    kind = QTY;
                    quantity = value instanceof Number
                            ? ((Number) value).intValue()
                            : 0;

                    int modelRow = table.convertRowIndexToModel(row);
                    String status = String.valueOf(
                            tableModel.getValueAt(modelRow, 5)
                    );
                    accent = statusColor(status, quantity);
                    break;

                case 5:
                    kind = STATUS;
                    accent = statusColor(text, -1);
                    break;

                default:
                    kind = TEXT_KIND;
                    break;
            }

            return this;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);

            int w = getWidth();
            int h = getHeight();

            g.setColor(selected ? SEL : hover ? HOVER : PANEL);
            g.fillRect(0, 0, w, h);

            g.setColor(new Color(255, 255, 255, 14));
            g.drawLine(0, h - 1, w, h - 1);

            if (selected && column == 0) {
                g.setColor(BLUE);
                g.fillRect(0, 0, 3, h);
            }

            int pad = 16;

            switch (kind) {
                case ID:
                    g.setFont(font(Font.BOLD, 13));
                    g.setColor(MUTED);
                    drawText(g, text, pad, w - pad, h);
                    break;

                case TYPE:
                    drawPill(g, text, accent, pad, h, false);
                    break;

                case NAME:
                    drawName(g, text, pad, h);
                    break;

                case QTY:
                    g.setFont(font(Font.BOLD, 15));
                    g.setColor(quantity == 0
                            ? RED
                            : quantity <= 10
                            ? AMBER
                            : TEXT);
                    drawText(g, formatNumber(quantity), pad, w - pad, h);
                    break;

                case STATUS:
                    drawPill(g, text.replace('_', ' '), accent, pad, h, true);
                    break;

                default:
                    g.setFont(font(Font.PLAIN, 14));
                    g.setColor(TEXT);
                    drawText(g, text, pad, w - pad, h);
                    break;
            }

            g.dispose();
        }

        private void drawName(Graphics2D g, String value, int pad, int h) {
            int d = 32;

            g.setColor(new Color(BLUE.getRed(), BLUE.getGreen(), BLUE.getBlue(), 48));
            g.fillRoundRect(pad, (h - d) / 2, d, d, 10, 10);

            g.setColor(BLUE);
            g.setStroke(new BasicStroke(
                    1.8f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            int cx = pad + d / 2;
            int cy = h / 2;

            g.drawRect(cx - 7, cy - 5, 14, 11);
            g.drawLine(cx - 7, cy - 1, cx + 7, cy - 1);
            g.drawLine(cx, cy - 5, cx, cy - 1);

            g.setFont(font(Font.BOLD, 14));
            g.setColor(TEXT);
            drawText(g, value, pad + d + 12, getWidth() - pad, h);
        }

        private void drawPill(
                Graphics2D g,
                String value,
                Color color,
                int pad,
                int h,
                boolean status
        ) {
            g.setFont(font(Font.BOLD, status ? 11 : 12));
            FontMetrics fm = g.getFontMetrics();

            int pillWidth = fm.stringWidth(value) + 24;
            int pillHeight = status ? 26 : 26;
            int y = (h - pillHeight) / 2;

            g.setColor(new Color(
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    44
            ));
            g.fillRoundRect(pad, y, pillWidth, pillHeight,
                    pillHeight, pillHeight);

            g.setColor(color);
            g.drawString(
                    value,
                    pad + 12,
                    y + (pillHeight + fm.getAscent() - fm.getDescent()) / 2
            );
        }

        private void drawText(
                Graphics2D g,
                String value,
                int x,
                int right,
                int h
        ) {
            FontMetrics fm = g.getFontMetrics();
            String draw = clip(value, fm, right - x);
            g.drawString(
                    draw,
                    x,
                    (h + fm.getAscent() - fm.getDescent()) / 2
            );
        }
    }

    // =========================================================
    // HEADER RENDERER
    // =========================================================

    private static class HeaderRenderer extends JComponent
            implements TableCellRenderer {

        private String text = "";

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            text = value == null ? "" : value.toString().toUpperCase();
            return this;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);

            g.setColor(PANEL);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(LINE);
            g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

            g.setFont(font(Font.BOLD, 11));
            g.setColor(MUTED);

            FontMetrics fm = g.getFontMetrics();

            g.drawString(
                    text,
                    16,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2
            );

            g.dispose();
        }
    }

    // =========================================================
    // STAT CARD
    // =========================================================

    private static class StatCard extends JPanel {

        final JLabel value = new JLabel("0");
        private final String title;
        private final String subtitle;
        private final Color accent;

        StatCard(String title, String subtitle, Color accent) {
            this.title = title;
            this.subtitle = subtitle;
            this.accent = accent;

            setOpaque(false);
            setLayout(null);
            setPreferredSize(new Dimension(200, 90));

            value.setFont(font(Font.BOLD, 30));
            value.setForeground(accent);
            value.setOpaque(false);
            add(value);
        }

        @Override
        public void doLayout() {
            value.setBounds(16, 34, getWidth() - 32, 40);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);

            RoundRectangle2D r = new RoundRectangle2D.Float(
                    0.5f, 0.5f,
                    getWidth() - 1,
                    getHeight() - 1,
                    10, 10
            );

            g.setColor(PANEL);
            g.fill(r);

            g.setColor(LINE);
            g.draw(r);

            g.setColor(accent);
            g.fillRect(16, 18, 7, 7);

            g.setFont(font(Font.BOLD, 12));
            g.setColor(MUTED);
            g.drawString(title, 32, 26);

            g.setFont(font(Font.PLAIN, 12));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(MUTED);
            g.drawString(
                    subtitle,
                    getWidth() - 16 - fm.stringWidth(subtitle),
                    66
            );

            super.paintComponent(g);
            g.dispose();
        }
    }

    // =========================================================
    // DIALOG / UI HELPERS
    // =========================================================

    private static JTextField field(String placeholder) {
        JTextField field = new JTextField();
        field.putClientProperty("placeholder", placeholder);
        styleTextField(field);
        return field;
    }

    private static JPanel fieldBlock(String title, JComponent field) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel label = label(title, Font.BOLD, 12, MUTED);
        label.setAlignmentX(LEFT_ALIGNMENT);

        field.setAlignmentX(LEFT_ALIGNMENT);

        p.add(label);
        p.add(Box.createVerticalStrut(6));
        p.add(field);

        return p;
    }

    private static void styleTextField(JTextField field) {
        field.setFont(font(Font.PLAIN, 13));
        field.setForeground(TEXT);
        field.setBackground(PANEL2);
        field.setCaretColor(BLUE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                new EmptyBorder(8, 12, 8, 12)
        ));
        field.setOpaque(true);
    }

    private static JButton dialogButton(String text, Color background, Color foreground) {
        return darkButton(text, background, foreground);
    }

    private static JButton smallPill(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                aa(g);
                g.setColor(getModel().isRollover() ? SEL : PANEL2);
                g.fillRoundRect(0, 0, getWidth(), getHeight(),
                        getHeight(), getHeight());
                g.dispose();
                super.paintComponent(g0);
            }
        };

        b.setFont(font(Font.BOLD, 12));
        b.setForeground(MUTED);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setBorder(new EmptyBorder(5, 12, 5, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return b;
    }

    private static JButton darkButton(
            String text,
            Color background,
            Color foreground
    ) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                aa(g);

                Color fill = getModel().isEnabled()
                        ? getModel().isRollover()
                        ? lighten(background)
                        : background
                        : new Color(20, 39, 52);

                g.setColor(fill);
                g.fillRoundRect(
                        0, 0,
                        getWidth() - 1,
                        getHeight() - 1,
                        8, 8
                );

                g.dispose();
                super.paintComponent(g0);
            }
        };

        b.setFont(font(Font.BOLD, 12));
        b.setForeground(foreground);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setBorder(new EmptyBorder(10, 24, 10, 24));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return b;
    }

    private static JButton outlineButton(String text, Color color) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                aa(g);

                g.setColor(new Color(
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        getModel().isRollover() ? 28 : 12
                ));
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                g.setColor(color);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                g.dispose();
                super.paintComponent(g0);
            }
        };

        b.setFont(font(Font.BOLD, 12));
        b.setForeground(TEXT);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setBorder(new EmptyBorder(10, 24, 10, 24));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return b;
    }

    private static JLabel label(String text, int style, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font(style, size));
        l.setForeground(color);
        return l;
    }

    private static String formatNumber(int value) {
        return NumberFormat.getIntegerInstance(Locale.US).format(value);
    }

    private static Color typeColor(String type) {
        String s = type == null ? "" : type.toLowerCase();

        if (s.contains("food")) return AMBER;
        if (s.contains("water")) return BLUE;
        if (s.contains("medical")) return RED;
        if (s.contains("shelter") || s.contains("tent") || s.contains("blanket"))
            return VIOLET;
        if (s.contains("cloth")) return TEAL;
        if (s.contains("fuel") || s.contains("vehicle") || s.contains("boat"))
            return BROWN;

        return MUTED;
    }

    private static Color statusColor(String status, int quantity) {
        String s = status == null ? "" : status.toUpperCase();

        if (s.contains("OUT_OF_STOCK") || quantity == 0) return RED;
        if (quantity > 0 && quantity <= 10) return AMBER;
        if (s.contains("AVAILABLE")) return GREEN;

        return MUTED;
    }

    private static String clip(
            String text,
            FontMetrics fm,
            int maxWidth
    ) {
        if (fm.stringWidth(text) <= maxWidth) return text;

        String ellipsis = "...";
        int width = fm.stringWidth(ellipsis);

        StringBuilder out = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            String candidate = out.toString() + text.charAt(i);

            if (fm.stringWidth(candidate) + width > maxWidth) break;

            out.append(text.charAt(i));
        }

        return out + ellipsis;
    }

    private static Color lighten(Color c) {
        return new Color(
                Math.min(255, c.getRed() + 12),
                Math.min(255, c.getGreen() + 12),
                Math.min(255, c.getBlue() + 12)
        );
    }

    private static void aa(Graphics2D g) {
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );
    }

    private static Font font(int style, int size) {
        return new Font("Segoe UI", style, size);
    }

    // =========================================================
    // REFERENCE-STYLE SEGMENTED FILTER
    // =========================================================

    private static class SegmentedFilter extends JComponent {

        private String[] labels = {"All 0", "Available 0", "Low stock 0", "Out of stock 0"};
        private int selected = 0;
        private Runnable onChange;

        SegmentedFilter() {
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int index = indexAt(e.getX());
                    if (index >= 0 && index != selected) {
                        selected = index;
                        repaint();
                        if (onChange != null) onChange.run();
                    }
                }
            });
        }

        void setLabels(String[] labels) {
            if (labels != null && labels.length == 4) {
                this.labels = labels.clone();
                repaint();
            }
        }

        int getSelectedIndex() {
            return selected;
        }

        private int indexAt(int x) {
            int segmentWidth = getWidth() / labels.length;
            int index = x / Math.max(1, segmentWidth);
            return Math.max(0, Math.min(labels.length - 1, index));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);

            int w = getWidth();
            int h = getHeight();

            g.setColor(PANEL2);
            g.fillRoundRect(0, 0, w - 1, h - 1, h, h);

            g.setColor(LINE);
            g.drawRoundRect(0, 0, w - 1, h - 1, h, h);

            int segmentWidth = w / labels.length;

            for (int i = 0; i < labels.length; i++) {
                int x = i * segmentWidth;

                if (i == selected) {
                    g.setColor(BLUE);
                    g.fillRoundRect(
                            x + 3, 3,
                            segmentWidth - 6, h - 7,
                            h - 7, h - 7
                    );

                    g.setColor(new Color(255, 255, 255, 24));
                    g.drawRoundRect(
                            x + 3, 3,
                            segmentWidth - 7, h - 8,
                            h - 8, h - 8
                    );
                }

                g.setFont(font(Font.BOLD, 12));
                FontMetrics fm = g.getFontMetrics();

                String text = labels[i];
                int tx = x + (segmentWidth - fm.stringWidth(text)) / 2;
                int ty = (h + fm.getAscent() - fm.getDescent()) / 2;

                g.setColor(i == selected ? TEXT : MUTED);
                g.drawString(text, tx, ty);
            }

            g.dispose();
        }
    }

    // =========================================================
    // CUSTOM PANELS
    // =========================================================

    private static class RoundedPanel extends JPanel {
        private final Color color;

        RoundedPanel(Color color) {
            this.color = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);

            g.setColor(color);
            g.fillRoundRect(
                    0, 0,
                    getWidth() - 1,
                    getHeight() - 1,
                    12, 12
            );

            g.setColor(LINE);
            g.drawRoundRect(
                    0, 0,
                    getWidth() - 1,
                    getHeight() - 1,
                    12, 12
            );

            g.dispose();
            super.paintComponent(g0);
        }
    }

    private static class RoundedChip extends JPanel {
        RoundedChip() {
            setOpaque(false);
            setBorder(new EmptyBorder(9, 14, 9, 14));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);

            g.setColor(new Color(0x16, 0x3A, 0x55));
            g.fillRoundRect(
                    0, 0,
                    getWidth(),
                    getHeight(),
                    12, 12
            );

            g.setColor(LINE);
            g.drawRoundRect(
                    0, 0,
                    getWidth() - 1,
                    getHeight() - 1,
                    12, 12
            );

            g.dispose();
            super.paintComponent(g0);
        }
    }
}
