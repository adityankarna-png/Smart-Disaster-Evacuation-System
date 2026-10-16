package com.disaster.gui;

import com.disaster.dao.ShelterDAO;
import com.disaster.model.Shelter;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ShelterPanel extends JPanel {
    private static final Color BG = new Color(0x081821);
    private static final Color PANEL = new Color(0x132B3B);
    private static final Color PANEL2 = new Color(0x18354A);
    private static final Color LINE = new Color(0x294B61);
    private static final Color TEXT = new Color(0xE8F0F6);
    private static final Color MUTED = new Color(0x8EA4B5);
    private static final Color BLUE = new Color(0x55B4FF);
    private static final Color BLUE_DARK = new Color(0x214D70);
    private static final Color GREEN = new Color(0x3DD4A0);
    private static final Color RED = new Color(0xFF5B68);
    private static final Color AMBER = new Color(0xF4B44B);
    private static final Color HOVER = new Color(0x17364A);
    private static final Color SELECTED = new Color(0x1C4662);

    private JTable shelterTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private final ShelterDAO shelterDAO;

    private JTextField searchField;
    private JLabel countChip;
    private JLabel allCount;
    private JLabel availableCount;
    private JLabel fullCount;
    private JLabel shelterValue;
    private JLabel totalCapacity;
    private JLabel occupied;
    private JLabel freeBeds;
    private JLabel footer;

    private int activeFilter = 0;

    public ShelterPanel() {
        shelterDAO = new ShelterDAO();
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(26, 32, 20, 32));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        loadShelters();
    }

    private JPanel buildHeader() {
        JPanel root = transparent(new BorderLayout());

        JPanel titleBox = transparent(new GridLayout(2, 1, 0, 1));
        JLabel title = label("Shelter Management", 29, TEXT, true);
        JLabel subtitle = label("Manage emergency shelters, capacity and occupancy", 13, MUTED, false);
        titleBox.add(title);
        titleBox.add(subtitle);

        countChip = label("0 shelters", 12, BLUE, true);
        countChip.setHorizontalAlignment(SwingConstants.CENTER);
        RoundedPanel chip = new RoundedPanel(18, PANEL2, LINE);
        chip.setLayout(new GridBagLayout());
        chip.setBorder(new EmptyBorder(0, 14, 0, 14));
        chip.add(countChip);
        chip.setPreferredSize(new Dimension(105, 42));

        root.add(titleBox, BorderLayout.WEST);
        root.add(chip, BorderLayout.EAST);

        JPanel wrapper = transparent(new BorderLayout(0, 17));
        wrapper.add(root, BorderLayout.NORTH);
        wrapper.add(buildStats(), BorderLayout.CENTER);
        wrapper.setBorder(new EmptyBorder(0, 0, 16, 0));
        return wrapper;
    }

    private JPanel buildStats() {
        JPanel p = transparent(new GridLayout(1, 4, 12, 0));
        p.setPreferredSize(new Dimension(10, 91));

        shelterValue = new JLabel("0");
        totalCapacity = new JLabel("0");
        occupied = new JLabel("0");
        freeBeds = new JLabel("0");

        p.add(statCard("Shelters", shelterValue, "Registered", BLUE, "■"));
        p.add(statCard("Total capacity", totalCapacity, "Beds in total", AMBER, "■"));
        p.add(statCard("Sheltered now", occupied, "People sheltered", GREEN, "■"));
        p.add(statCard("Beds free", freeBeds, "Available space", BLUE, "■"));
        return p;
    }

    private JPanel statCard(String title, JLabel value, String sub, Color accent, String marker) {
        RoundedPanel card = new RoundedPanel(10, PANEL, LINE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(12, 15, 10, 15));

        JPanel top = transparent(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel dot = label(marker, 10, accent, true);
        JLabel heading = label("  " + title, 12, MUTED, true);
        top.add(dot);
        top.add(heading);

        value.setFont(new Font("Segoe UI", Font.BOLD, 30));
        value.setForeground(accent);

        JLabel bottom = label(sub, 11, MUTED, false);
        bottom.setHorizontalAlignment(SwingConstants.RIGHT);

        card.add(top, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildBody() {
        JPanel p = transparent(new BorderLayout(0, 13));
        p.add(buildToolbar(), BorderLayout.NORTH);
        p.add(buildTableCard(), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildToolbar() {
        JPanel p = transparent(new BorderLayout());
        p.setPreferredSize(new Dimension(10, 41));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setForeground(TEXT);
        searchField.setBackground(PANEL2);
        searchField.setCaretColor(BLUE);
        searchField.setBorder(new CompoundBorder(
                new LineBorder(LINE, 1, true),
                new EmptyBorder(9, 13, 9, 13)
        ));
        searchField.setPreferredSize(new Dimension(340, 40));
        searchField.setToolTipText("Search shelter name or location");
        installPlaceholder(searchField, "Search name or location");

        JPanel filters = transparent(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filters.setBorder(new CompoundBorder(
                new LineBorder(LINE, 1, true),
                new EmptyBorder(2, 2, 2, 2)
        ));
        filters.setBackground(PANEL2);
        filters.setOpaque(true);

        allCount = filterButton(filters, "All", 0);
        availableCount = filterButton(filters, "Available", 1);
        fullCount = filterButton(filters, "Full", 2);

        p.add(searchField, BorderLayout.WEST);
        p.add(filters, BorderLayout.EAST);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });
        return p;
    }

    private JLabel filterButton(JPanel parent, String text, int index) {
        JLabel b = label(text + " 0", 12, MUTED, true);
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setBorder(new EmptyBorder(8, 13, 8, 13));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                activeFilter = index;
                updateFilterVisuals();
                filterTable();
            }
            public void mouseEntered(MouseEvent e) {
                if (activeFilter != index) b.setForeground(TEXT);
            }
            public void mouseExited(MouseEvent e) {
                if (activeFilter != index) b.setForeground(MUTED);
            }
        });
        parent.add(b);
        return b;
    }

    private void updateFilterVisuals() {
        styleFilter(allCount, activeFilter == 0);
        styleFilter(availableCount, activeFilter == 1);
        styleFilter(fullCount, activeFilter == 2);
    }

    private void styleFilter(JLabel b, boolean selected) {
        b.setForeground(selected ? TEXT : MUTED);
        b.setOpaque(selected);
        b.setBackground(selected ? BLUE_DARK : PANEL2);
        b.setBorder(new EmptyBorder(8, 13, 8, 13));
    }

    private JPanel buildTableCard() {
        RoundedPanel card = new RoundedPanel(10, PANEL, LINE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(5, 5, 0, 5));
        card.add(buildTableScroll(), BorderLayout.CENTER);
        card.add(buildActions(), BorderLayout.SOUTH);
        return card;
    }

    private JScrollPane buildTableScroll() {
        String[] columns = {"ID", "NAME", "LOCATION", "CAPACITY", "OCCUPANCY", "STATUS"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
            public Class<?> getColumnClass(int column) {
                return column == 0 || column == 3 || column == 4 ? Integer.class : String.class;
            }
        };

        shelterTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        shelterTable.setRowSorter(sorter);
        shelterTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        shelterTable.setForeground(TEXT);
        shelterTable.setBackground(PANEL);
        shelterTable.setSelectionBackground(SELECTED);
        shelterTable.setSelectionForeground(TEXT);
        shelterTable.setRowHeight(58);
        shelterTable.setShowGrid(false);
        shelterTable.setIntercellSpacing(new Dimension(0, 0));
        shelterTable.setFillsViewportHeight(true);
        shelterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        shelterTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = shelterTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setForeground(MUTED);
        header.setBackground(PANEL);
        header.setPreferredSize(new Dimension(0, 43));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new HeaderRenderer());

        int[] widths = {70, 290, 190, 125, 250, 145};
        for (int i = 0; i < widths.length; i++) {
            TableColumn c = shelterTable.getColumnModel().getColumn(i);
            c.setPreferredWidth(widths[i]);
            c.setCellRenderer(new CellRenderer(i));
        }

        shelterTable.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                int row = shelterTable.rowAtPoint(e.getPoint());
                shelterTable.putClientProperty("hoverRow", row);
                shelterTable.repaint();
            }
        });
        shelterTable.addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
                shelterTable.putClientProperty("hoverRow", -1);
                shelterTable.repaint();
            }
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    showUpdateShelterDialog();
                }
            }
        });

        shelterTable.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "deleteShelter");
        shelterTable.getActionMap().put("deleteShelter", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { deleteShelter(); }
        });
        shelterTable.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "editShelter");
        shelterTable.getActionMap().put("editShelter", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { showUpdateShelterDialog(); }
        });

        JScrollPane scroll = new JScrollPane(shelterTable);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(PANEL);
        scroll.setBackground(PANEL);
        scroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new DarkScrollBarUI());
        return scroll;
    }

    private JPanel buildActions() {
        JPanel p = transparent(new BorderLayout());
        p.setBorder(new EmptyBorder(13, 0, 10, 0));

        footer = label("Showing 0 of 0 shelters", 12, MUTED, false);

        JPanel buttons = transparent(new FlowLayout(FlowLayout.RIGHT, 9, 0));
        JButton refresh = button("Refresh", PANEL2, TEXT, false);
        JButton add = button("Add Shelter", BLUE, new Color(0x06223A), true);
        JButton update = button("Update Shelter", PANEL2, TEXT, false);
        JButton delete = button("Delete Shelter", BG, RED, false);

        buttons.add(refresh);
        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        p.add(footer, BorderLayout.WEST);
        p.add(buttons, BorderLayout.EAST);

        refresh.addActionListener(e -> loadShelters());
        add.addActionListener(e -> showAddShelterDialog());
        update.addActionListener(e -> showUpdateShelterDialog());
        delete.addActionListener(e -> deleteShelter());

        shelterTable.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = shelterTable.getSelectedRow() >= 0;
            update.setEnabled(selected);
            delete.setEnabled(selected);
            updateButtonState(update, selected);
            updateButtonState(delete, selected);
        });
        update.setEnabled(false);
        delete.setEnabled(false);
        updateButtonState(update, false);
        updateButtonState(delete, false);
        return p;
    }

    private void updateButtonState(JButton b, boolean enabled) {
        b.setForeground(enabled ? TEXT : new Color(0x526675));
        b.setBackground(enabled ? PANEL2 : new Color(0x0D202C));
    }

    private JButton button(String text, Color bg, Color fg, boolean primary) {
        RoundedButton b = new RoundedButton(text, bg, fg, primary);
        return b;
    }

    private static class RoundedButton extends JButton {
        private final Color normalBg;
        private final Color normalFg;
        private final boolean primary;
        private boolean hover;

        RoundedButton(String text, Color bg, Color fg, boolean primary) {
            super(text);
            this.normalBg = bg;
            this.normalFg = fg;
            this.primary = primary;

            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(fg);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(9, 17, 9, 17));
            setPreferredSize(new Dimension(text.equals("Add Shelter") ? 122 :
                    text.equals("Delete Shelter") ? 122 :
                            text.equals("Update Shelter") ? 118 : 94, 38));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }
                @Override public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            boolean enabled = isEnabled();
            Color bg;
            Color fg;

            if (!enabled) {
                bg = new Color(0x102532);
                fg = new Color(0x526675);
            } else if (primary) {
                bg = hover ? new Color(0x4AA8F5) : new Color(0x55B4FF);
                fg = new Color(0x06223A);
            } else {
                bg = hover ? new Color(0x1D4057) : normalBg;
                fg = normalFg;
            }

            int arc = 7;
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

            if (getText().equals("Delete Shelter") && enabled) {
                g2.setColor(hover ? new Color(0xFF5B68) : new Color(0xA33C4B));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            } else if (!primary && enabled) {
                g2.setColor(new Color(0x2A4D63));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            }

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            // Border is painted manually above so Swing's default LAF cannot add a white border.
        }

        @Override
        public Color getBackground() {
            return normalBg;
        }

        @Override
        public Color getForeground() {
            return isEnabled() ? normalFg : new Color(0x526675);
        }
    }

    private void loadShelters() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        try {
            List<Shelter> list = shelterDAO.getAllShelters();
            int capacity = 0;
            int currentOccupancy = 0;
            int availableBeds = 0;
            int available = 0;
            int full = 0;

            for (Shelter s : list) {
                tableModel.addRow(new Object[]{
                        s.getShelterId(),
                        s.getName(),
                        s.getLocation(),
                        s.getCapacity(),
                        s.getCurrentOccupancy(),
                        s.getStatus()
                });
                capacity += s.getCapacity();
                currentOccupancy += s.getCurrentOccupancy();
                availableBeds += Math.max(0, s.getCapacity() - s.getCurrentOccupancy());
                if ("FULL".equalsIgnoreCase(s.getStatus())) full++;
                else available++;
            }

            countChip.setText(list.size() + " " + (list.size() == 1 ? "shelter" : "shelters"));
            shelterValue.setText(String.valueOf(list.size()));
            totalCapacity.setText(formatNumber(capacity));
            occupied.setText(formatNumber(currentOccupancy));
            freeBeds.setText(formatNumber(availableBeds));

            allCount.setText("All " + list.size());
            availableCount.setText("Available " + available);
            fullCount.setText("Full " + full);
            updateFilterVisuals();
            filterTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading shelters:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTable() {
        if (sorter == null || searchField == null) return;
        String q = searchField.getText().trim().toLowerCase();
        if (q.equals("search name or location")) q = "";
        final String query = q;
        final int filter = activeFilter;

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String name = String.valueOf(entry.getValue(1)).toLowerCase();
                String location = String.valueOf(entry.getValue(2)).toLowerCase();
                String status = String.valueOf(entry.getValue(5));
                boolean matchesText = query.isEmpty() || name.contains(query) || location.contains(query);
                boolean matchesStatus = filter == 0
                        || (filter == 1 && status.equalsIgnoreCase("AVAILABLE"))
                        || (filter == 2 && status.equalsIgnoreCase("FULL"));
                return matchesText && matchesStatus;
            }
        });

        if (footer != null) {
            footer.setText("Showing " + shelterTable.getRowCount() + " of " + tableModel.getRowCount() + " shelters");
        }
    }

    private void showAddShelterDialog() {
        ShelterDialog dialog = new ShelterDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (!dialog.saved) return;
        try {
            shelterDAO.addShelter(dialog.shelter);
            JOptionPane.showMessageDialog(this, "Shelter added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadShelters();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding shelter:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showUpdateShelterDialog() {
        int viewRow = shelterTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a shelter first.", "No Shelter Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = shelterTable.convertRowIndexToModel(viewRow);
        Shelter s = new Shelter();
        s.setShelterId((Integer) tableModel.getValueAt(row, 0));
        s.setName(String.valueOf(tableModel.getValueAt(row, 1)));
        s.setLocation(String.valueOf(tableModel.getValueAt(row, 2)));
        s.setCapacity((Integer) tableModel.getValueAt(row, 3));
        s.setCurrentOccupancy((Integer) tableModel.getValueAt(row, 4));
        s.setStatus(String.valueOf(tableModel.getValueAt(row, 5)));

        ShelterDialog dialog = new ShelterDialog(SwingUtilities.getWindowAncestor(this), s);
        dialog.setVisible(true);
        if (!dialog.saved) return;
        try {
            shelterDAO.updateShelter(dialog.shelter);
            JOptionPane.showMessageDialog(this, "Shelter updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadShelters();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating shelter:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteShelter() {
        int viewRow = shelterTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a shelter first.", "No Shelter Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = shelterTable.convertRowIndexToModel(viewRow);
        int id = (Integer) tableModel.getValueAt(row, 0);
        String name = String.valueOf(tableModel.getValueAt(row, 1));

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete shelter #" + id + " - " + name + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            shelterDAO.deleteShelter(id);
            JOptionPane.showMessageDialog(this, "Shelter deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadShelters();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting shelter:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                       boolean focused, int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(MUTED);
            setBackground(PANEL);
            setBorder(new EmptyBorder(0, 14, 0, 8));
            return this;
        }
    }

    private class CellRenderer extends JPanel implements TableCellRenderer {
        private final int column;

        CellRenderer(int column) {
            this.column = column;
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                       boolean focused, int row, int columnIndex) {
            removeAll();

            Object hoverObj = table.getClientProperty("hoverRow");
            int hoverRow = hoverObj instanceof Integer ? (Integer) hoverObj : -1;
            setBackground(selected ? SELECTED : row == hoverRow ? HOVER : (row % 2 == 0 ? PANEL : new Color(0x102735)));
            setBorder(new MatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 10)));
            setLayout(new BorderLayout());

            String text = value == null ? "" : String.valueOf(value);

            if (this.column == 0) {
                add(textCell("#" + text, BLUE, true, 12, 13), BorderLayout.CENTER);
            } else if (this.column == 1) {
                JPanel namePanel = transparent(new FlowLayout(FlowLayout.LEFT, 0, 0));
                namePanel.setBorder(new EmptyBorder(0, 8, 0, 0));
                namePanel.add(new ShelterIcon());
                JLabel name = textCell(text, TEXT, true, 13, 12);
                namePanel.add(name);
                add(namePanel, BorderLayout.CENTER);
            } else if (this.column == 4) {
                add(occupancyCell(row), BorderLayout.CENTER);
            } else if (this.column == 5) {
                add(statusCell(text), BorderLayout.CENTER);
            } else {
                add(textCell(text, TEXT, false, 13, 13), BorderLayout.CENTER);
            }
            return this;
        }

        private JLabel textCell(String text, Color color, boolean bold, int size, int left) {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, size));
            label.setForeground(color);
            label.setBorder(new EmptyBorder(0, left, 0, 8));
            return label;
        }

        private JPanel occupancyCell(int viewRow) {
            int modelRow = shelterTable.convertRowIndexToModel(viewRow);
            int occupancy = (Integer) tableModel.getValueAt(modelRow, 4);
            int capacity = (Integer) tableModel.getValueAt(modelRow, 3);
            double ratio = capacity > 0 ? Math.min(1.0, occupancy / (double) capacity) : 0;
            Color color = ratio >= .90 ? RED : ratio >= .70 ? AMBER : GREEN;

            JPanel root = transparent(new BorderLayout(0, 4));
            root.setBorder(new EmptyBorder(7, 12, 7, 14));

            JPanel top = transparent(new BorderLayout());
            JLabel amount = textCell(occupancy + " of " + capacity, TEXT, true, 12, 0);
            JLabel percent = textCell(Math.round(ratio * 100) + "%", color, true, 11, 0);
            percent.setHorizontalAlignment(SwingConstants.RIGHT);
            top.add(amount, BorderLayout.WEST);
            top.add(percent, BorderLayout.EAST);

            JComponent bar = new JComponent() {
                protected void paintComponent(Graphics graphics) {
                    Graphics2D g = (Graphics2D) graphics.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setColor(new Color(255, 255, 255, 22));
                    g.fillRoundRect(0, 0, getWidth(), 6, 6, 6);
                    g.setColor(color);
                    g.fillRoundRect(0, 0, (int) (getWidth() * ratio), 6, 6, 6);
                    g.dispose();
                }
            };
            bar.setPreferredSize(new Dimension(120, 6));
            bar.setOpaque(false);

            root.add(top, BorderLayout.NORTH);
            root.add(bar, BorderLayout.SOUTH);
            return root;
        }

        private JPanel statusCell(String status) {
            JPanel p = transparent(new FlowLayout(FlowLayout.LEFT, 12, 0));
            Color color = status.equalsIgnoreCase("AVAILABLE") ? GREEN : RED;
            Color bg = status.equalsIgnoreCase("AVAILABLE") ? new Color(0x194238) : new Color(0x4B2631);
            JLabel pill = new JLabel(status.toUpperCase());
            pill.setFont(new Font("Segoe UI", Font.BOLD, 11));
            pill.setForeground(color);
            pill.setOpaque(true);
            pill.setBackground(bg);
            pill.setBorder(new EmptyBorder(6, 12, 6, 12));
            p.add(pill);
            return p;
        }
    }

    private static class ShelterIcon extends JComponent {
        ShelterIcon() {
            setPreferredSize(new Dimension(34, 34));
            setMinimumSize(new Dimension(34, 34));
            setOpaque(false);
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(0x174A50));
            g.fillRoundRect(0, 0, 32, 32, 7, 7);
            g.setColor(GREEN);
            g.setStroke(new BasicStroke(1.7f));
            g.drawLine(9, 15, 16, 9);
            g.drawLine(16, 9, 23, 15);
            g.drawRect(11, 14, 10, 9);
            g.drawLine(16, 18, 16, 23);
            g.dispose();
        }
    }

    private static class ShelterDialog extends JDialog {
        private final JTextField name = new JTextField();
        private final JTextField location = new JTextField();
        private final JTextField capacity = new JTextField();
        private final JTextField occupancy = new JTextField("0");

        private final JLabel usageLabel = new JLabel();
        private final JComponent usageBar = new JComponent() {
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g.setColor(new Color(255, 255, 255, 28));
                g.fillRoundRect(0, 0, w, h, h, h);

                int cap = parse(capacity.getText());
                int occ = parse(occupancy.getText());
                double ratio = cap > 0 ? Math.min(1.0, Math.max(0.0, occ / (double) cap)) : 0;
                Color c = ratio >= 1.0 ? RED : ratio >= .70 ? AMBER : GREEN;
                g.setColor(c);
                g.fillRoundRect(0, 0, (int) (w * ratio), h, h, h);
                g.dispose();
            }
        };

        private final JLabel availableSegment = new JLabel("Available", SwingConstants.CENTER);
        private final JLabel fullSegment = new JLabel("Full", SwingConstants.CENTER);
        private final JLabel closedSegment = new JLabel("Closed", SwingConstants.CENTER);

        private Shelter shelter;
        private boolean saved;

        ShelterDialog(Window owner, Shelter existing) {
            super(owner, existing == null ? "Add Shelter" : "Update Shelter", ModalityType.APPLICATION_MODAL);
            shelter = existing;
            build(existing);
            setSize(500, 530);
            setResizable(false);
            setLocationRelativeTo(owner);
        }

        private void build(Shelter existing) {
            JPanel root = new JPanel(new BorderLayout(0, 0));
            root.setBackground(PANEL);
            root.setBorder(new EmptyBorder(24, 28, 22, 28));

            JPanel content = new JPanel();
            content.setOpaque(false);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

            String titleText = existing == null ? "Add shelter" : "Update shelter";
            String subtitleText = existing == null
                    ? "Create a new emergency shelter"
                    : "Edit the details of shelter #" + existing.getShelterId();

            JLabel title = label(titleText, 23, TEXT, true);
            JLabel sub = label(subtitleText, 12, MUTED, false);
            content.add(title);
            content.add(Box.createVerticalStrut(2));
            content.add(sub);
            content.add(Box.createVerticalStrut(18));

            addReferenceField(content, "Name", name);
            addReferenceField(content, "Location", location);

            JPanel numbers = new JPanel(new GridLayout(1, 2, 12, 0));
            numbers.setOpaque(false);
            numbers.setAlignmentX(Component.LEFT_ALIGNMENT);
            addReferenceField(numbers, "Capacity (beds)", capacity);
            addReferenceField(numbers, "Occupancy (people now)", occupancy);
            content.add(numbers);
            content.add(Box.createVerticalStrut(5));

            JPanel usage = new JPanel(new BorderLayout(0, 6));
            usage.setOpaque(false);
            usage.setAlignmentX(Component.LEFT_ALIGNMENT);

            JPanel usageTop = new JPanel(new BorderLayout());
            usageTop.setOpaque(false);
            usageLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            usageTop.add(usageLabel, BorderLayout.WEST);
            usage.add(usageTop, BorderLayout.NORTH);

            usageBar.setPreferredSize(new Dimension(10, 8));
            usage.add(usageBar, BorderLayout.SOUTH);
            content.add(usage);
            content.add(Box.createVerticalStrut(19));

            JLabel statusTitle = label("Status", 12, MUTED, true);
            statusTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(statusTitle);
            content.add(Box.createVerticalStrut(7));
            content.add(buildStatusSelector());
            content.add(Box.createVerticalGlue());

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttons.setOpaque(false);
            buttons.setAlignmentX(Component.RIGHT_ALIGNMENT);

            JButton cancel = dialogButton("Cancel", new Color(0x102A3C), TEXT, false);
            JButton save = dialogButton(existing == null ? "Add Shelter" : "Save changes",
                    BLUE, new Color(0x06223A), true);

            buttons.add(cancel);
            buttons.add(save);

            root.add(content, BorderLayout.CENTER);
            root.add(buttons, BorderLayout.SOUTH);

            cancel.addActionListener(e -> dispose());
            save.addActionListener(e -> save());

            name.addActionListener(e -> save());
            location.addActionListener(e -> save());
            capacity.getDocument().addDocumentListener(new SimpleDocumentListener(this::updateUsage));
            occupancy.getDocument().addDocumentListener(new SimpleDocumentListener(this::updateUsage));

            if (existing != null) {
                name.setText(existing.getName());
                location.setText(existing.getLocation());
                capacity.setText(String.valueOf(existing.getCapacity()));
                occupancy.setText(String.valueOf(existing.getCurrentOccupancy()));
            }

            updateUsage();
            setContentPane(root);
        }

        private JPanel buildStatusSelector() {
            JPanel wrapper = new JPanel(new GridLayout(1, 3, 0, 0));
            wrapper.setOpaque(false);
            wrapper.setBorder(new LineBorder(new Color(0x274B61), 1, true));
            wrapper.setMaximumSize(new Dimension(242, 39));
            wrapper.setPreferredSize(new Dimension(242, 39));
            wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

            styleSegment(availableSegment, true);
            styleSegment(fullSegment, false);
            styleSegment(closedSegment, false);
            closedSegment.setForeground(new Color(0x708797));
            closedSegment.setToolTipText("Closed status is not supported by the current Shelter model");

            availableSegment.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { selectStatus("AVAILABLE"); }
            });
            fullSegment.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { selectStatus("FULL"); }
            });
            closedSegment.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { Toolkit.getDefaultToolkit().beep(); }
            });

            wrapper.add(availableSegment);
            wrapper.add(fullSegment);
            wrapper.add(closedSegment);
            return wrapper;
        }

        private void styleSegment(JLabel segment, boolean selected) {
            segment.setFont(new Font("Segoe UI", Font.BOLD, 12));
            segment.setForeground(selected ? TEXT : MUTED);
            segment.setOpaque(selected);
            segment.setBackground(selected ? new Color(0x25516D) : new Color(0x102A3C));
            segment.setBorder(new EmptyBorder(8, 8, 8, 8));
            segment.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        private void selectStatus(String status) {
            styleSegment(availableSegment, "AVAILABLE".equals(status));
            styleSegment(fullSegment, "FULL".equals(status));
            closedSegment.setOpaque(false);
            closedSegment.setBackground(new Color(0x102A3C));
            closedSegment.setForeground(new Color(0x708797));
            updateUsage();
        }

        private void addReferenceField(JPanel parent, String text, JComponent component) {
            JPanel holder = new JPanel(new BorderLayout(0, 5));
            holder.setOpaque(false);
            holder.setBorder(new EmptyBorder(0, 0, 10, 0));

            JLabel l = label(text, 12, MUTED, true);
            component.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            component.setForeground(TEXT);
            component.setBackground(PANEL2);
            component.setBorder(new CompoundBorder(
                    new LineBorder(new Color(0x294D64), 1, true),
                    new EmptyBorder(8, 12, 8, 12)
            ));
            component.setPreferredSize(new Dimension(10, 40));
            component.setMinimumSize(new Dimension(10, 40));
            if (component instanceof JTextField) {
                ((JTextField) component).setCaretColor(BLUE);
            }

            holder.add(l, BorderLayout.NORTH);
            holder.add(component, BorderLayout.CENTER);
            parent.add(holder);
        }

        private JButton dialogButton(String text, Color bg, Color fg, boolean primary) {
            JButton b = new JButton(text) {
                private boolean hover;
                {
                    addMouseListener(new MouseAdapter() {
                        public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                        public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                    });
                }

                protected void paintComponent(Graphics graphics) {
                    Graphics2D g = (Graphics2D) graphics.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color fill = primary
                            ? (hover ? new Color(0x4AA8F5) : BLUE)
                            : (hover ? new Color(0x183A50) : bg);
                    g.setColor(fill);
                    g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    if (!primary) {
                        g.setColor(new Color(0x16394E));
                        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    }
                    g.dispose();
                    super.paintComponent(graphics);
                }
            };
            b.setFont(new Font("Segoe UI", Font.BOLD, 12));
            b.setForeground(fg);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            b.setOpaque(false);
            b.setBorder(new EmptyBorder(10, 17, 10, 17));
            b.setPreferredSize(new Dimension(text.equalsIgnoreCase("Cancel") ? 86 : 138, 38));
            return b;
        }

        private void updateUsage() {
            int c = parse(capacity.getText());
            int o = parse(occupancy.getText());
            int safe = c > 0 ? Math.min(Math.max(o, 0), c) : 0;
            boolean full = c > 0 && o >= c;
            usageLabel.setText(safe + " of " + Math.max(c, 0) + " beds in use");
            usageLabel.setForeground(full ? RED : TEXT);
            if (full) {
                JPanel parent = (JPanel) usageLabel.getParent();
                // Add the state text at the right without changing the data model.
                parent.removeAll();
                parent.setLayout(new BorderLayout());
                parent.add(usageLabel, BorderLayout.WEST);
                JLabel state = label("Full", 12, RED, true);
                parent.add(state, BorderLayout.EAST);
                parent.revalidate();
                parent.repaint();
            } else {
                JPanel parent = (JPanel) usageLabel.getParent();
                parent.removeAll();
                parent.setLayout(new BorderLayout());
                parent.add(usageLabel, BorderLayout.WEST);
                parent.revalidate();
                parent.repaint();
            }
            usageBar.repaint();
        }

        private void save() {
            try {
                String n = name.getText().trim();
                String l = location.getText().trim();
                int c = Integer.parseInt(capacity.getText().trim());
                int o = Integer.parseInt(occupancy.getText().trim());

                if (n.isEmpty() || l.isEmpty() || c <= 0 || o < 0 || o > c) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter valid shelter details.",
                            "Invalid Input",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String st = o >= c ? "FULL" : "AVAILABLE";
                Shelter x = new Shelter();
                if (shelter != null) x.setShelterId(shelter.getShelterId());
                x.setName(n);
                x.setLocation(l);
                x.setCapacity(c);
                x.setCurrentOccupancy(o);
                x.setStatus(st);
                shelter = x;
                saved = true;
                dispose();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Capacity and occupancy must be numbers.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        private static int parse(String value) {
            try { return Integer.parseInt(value.trim()); }
            catch (Exception e) { return 0; }
        }

        private interface ChangeAction { void run(); }

        private static class SimpleDocumentListener implements DocumentListener {
            private final ChangeAction action;
            SimpleDocumentListener(ChangeAction action) { this.action = action; }
            public void insertUpdate(DocumentEvent e) { action.run(); }
            public void removeUpdate(DocumentEvent e) { action.run(); }
            public void changedUpdate(DocumentEvent e) { action.run(); }
        }
    }

    private static void installPlaceholder(JTextField field, String text) {
        field.putClientProperty("placeholder", text);
        field.setText(text);
        field.setForeground(MUTED);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(text)) {
                    field.setText("");
                    field.setForeground(TEXT);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(text);
                    field.setForeground(MUTED);
                }
            }
        });
    }

    private static String formatNumber(int n) {
        return String.format("%,d", n);
    }

    private static JPanel transparent(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setOpaque(false);
        return p;
    }

    private static JLabel label(String text, int size, Color color, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(color);
        return l;
    }

    private static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color fill;
        private final Color borderColor;

        RoundedPanel(int arc, Color fill, Color borderColor) {
            this.arc = arc;
            this.fill = fill;
            this.borderColor = borderColor;
            setOpaque(false);
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g.setColor(borderColor);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class DarkScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        protected void configureScrollBarColors() {
            thumbColor = new Color(0x31556B);
            trackColor = PANEL;
        }

        protected JButton createDecreaseButton(int orientation) { return zeroButton(); }
        protected JButton createIncreaseButton(int orientation) { return zeroButton(); }

        private JButton zeroButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }
    }
}
