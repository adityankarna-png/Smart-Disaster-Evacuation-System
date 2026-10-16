package com.disaster.gui;

import com.disaster.dao.EvacuationDAO;
import com.disaster.dao.VictimDAO;
import com.disaster.exceptions.NoResourceAvailableException;
import com.disaster.exceptions.NoShelterAvailableException;
import com.disaster.exceptions.VictimNotFoundException;
import com.disaster.model.Evacuation;
import com.disaster.model.Victim;
import com.disaster.service.EvacuationService;
import com.disaster.service.EvacuationServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class EvacuationPanel extends JPanel {

    // =========================================================
    // DARK GUI DESIGN
    // =========================================================

    private static final Color BG = new Color(0x0C1822);
    private static final Color PANEL = new Color(0x14283A);
    private static final Color PANEL2 = new Color(0x1A3247);
    private static final Color LINE = new Color(0x26435B);
    private static final Color TEXT = new Color(0xE8F0F5);
    private static final Color MUTED = new Color(0x8BA5B8);

    private static final Color RED = new Color(0xFF5A5F);
    private static final Color AMBER = new Color(0xF5A83C);
    private static final Color GREEN = new Color(0x3FC79A);
    private static final Color BLUE = new Color(0x4FA8F0);

    private static final Color HOVER = new Color(0x182F44);
    private static final Color SELECTED = new Color(0x1E3D57);

    private static final String FONT_FAMILY = "Segoe UI";

    private JComboBox<Victim> victimComboBox;
    private JLabel priorityLabel;
    private JLabel evacuationCountLabel;

    private JLabel totalValue;
    private JLabel pendingValue;
    private JLabel criticalValue;
    private JLabel victimsValue;

    private JTextField searchField;
    private JPanel statusFilterPanel;
    private String activeStatus = "All";

    private JTable evacuationTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private final Map<Integer, Victim> victimMap = new HashMap<>();
    private JLabel activityLabel;
    private int lastCreatedEvacuationId = -1;

    private final VictimDAO victimDAO;
    private final EvacuationDAO evacuationDAO;
    private final EvacuationService evacuationService;

    public EvacuationPanel() {

        victimDAO = new VictimDAO();
        evacuationDAO = new EvacuationDAO();
        evacuationService = new EvacuationServiceImpl();

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(26, 32, 22, 32));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);

        loadVictims();
        loadEvacuations();
    }

    // =========================================================
    // HEADER
    // =========================================================

    private JPanel buildHeader() {

        JPanel header = transparent(new BorderLayout());
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel titles = transparent();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel title = label("Evacuation Management", Font.BOLD, 28, TEXT);
        JLabel subtitle = label(
                "Prioritize victims and coordinate emergency evacuation assignments",
                Font.PLAIN, 14, MUTED
        );

        titles.add(title);
        titles.add(Box.createVerticalStrut(4));
        titles.add(subtitle);

        header.add(titles, BorderLayout.WEST);

        JPanel chip = roundedPanel(18, new Color(0x173750), null);
        chip.setLayout(new BorderLayout());

        evacuationCountLabel = label("0 evacuations", Font.BOLD, 13, BLUE);
        evacuationCountLabel.setBorder(new EmptyBorder(8, 14, 8, 14));
        chip.add(evacuationCountLabel, BorderLayout.CENTER);

        JPanel chipWrap = transparent(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        chipWrap.add(chip);

        header.add(chipWrap, BorderLayout.EAST);

        return header;
    }

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    private JComponent buildMainContent() {

        JPanel root = transparent();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        root.add(buildCommandBar());
        root.add(Box.createVerticalStrut(14));
        root.add(buildSummaryCards());
        root.add(Box.createVerticalStrut(14));
        root.add(buildToolbar());
        root.add(Box.createVerticalStrut(12));
        root.add(buildTableCard());

        return root;
    }

    // =========================================================
    // COMMAND BAR
    // =========================================================

    private JPanel buildCommandBar() {

        JPanel card = roundedPanel(16, PANEL, LINE);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(12, 18, 12, 18));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setPreferredSize(new Dimension(0, 70));
        card.setMinimumSize(new Dimension(0, 70));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.anchor = GridBagConstraints.CENTER;
        g.fill = GridBagConstraints.NONE;
        g.insets = new Insets(0, 0, 0, 12);

        JLabel title = label("New evacuation", Font.BOLD, 14, TEXT);
        title.setPreferredSize(new Dimension(128, 42));
        title.setVerticalAlignment(SwingConstants.CENTER);
        card.add(title, g);

        victimComboBox = new JComboBox<>();
        victimComboBox.setPreferredSize(new Dimension(340, 42));
        victimComboBox.setMinimumSize(new Dimension(340, 42));
        victimComboBox.setMaximumSize(new Dimension(340, 42));
        victimComboBox.setFont(font(Font.PLAIN, 13));
        victimComboBox.setBackground(PANEL2);
        victimComboBox.setForeground(TEXT);
        victimComboBox.setOpaque(true);
        victimComboBox.setFocusable(false);
        victimComboBox.setBorder(BorderFactory.createLineBorder(LINE, 1, true));
        victimComboBox.setUI(new DarkComboBoxUI());
        card.add(victimComboBox, g);

        JButton priorityButton = createButton("Calculate priority", AMBER, true);
        priorityButton.setPreferredSize(new Dimension(161, 38));
        priorityButton.setMinimumSize(new Dimension(161, 38));
        priorityButton.setMaximumSize(new Dimension(161, 38));
        card.add(priorityButton, g);

        priorityLabel = label("Priority: —", Font.BOLD, 13, MUTED);
        priorityLabel.setPreferredSize(new Dimension(252, 38));
        priorityLabel.setHorizontalAlignment(SwingConstants.LEFT);
        card.add(priorityLabel, g);

        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 0, 0);
        card.add(Box.createHorizontalGlue(), g);

        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        g.insets = new Insets(0, 12, 0, 0);

        JButton createButton = createButton("Create evacuation", BLUE, true);
        createButton.setPreferredSize(new Dimension(170, 38));
        createButton.setMinimumSize(new Dimension(170, 38));
        createButton.setMaximumSize(new Dimension(170, 38));
        card.add(createButton, g);

        priorityButton.addActionListener(e -> calculateSelectedPriority());
        createButton.addActionListener(e -> createEvacuation());

        return card;
    }

    // =========================================================
    // SUMMARY CARDS
    // =========================================================

    private JPanel buildSummaryCards() {

        JPanel cards = transparent(new GridLayout(1, 4, 14, 0));
        cards.setAlignmentX(LEFT_ALIGNMENT);
        cards.setPreferredSize(new Dimension(0, 80));
        cards.setMinimumSize(new Dimension(0, 80));
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        totalValue = new JLabel("0");
        pendingValue = new JLabel("0");
        criticalValue = new JLabel("0");
        victimsValue = new JLabel("0");

        cards.add(summaryCard("Evacuations", totalValue, BLUE, "Assignments"));
        cards.add(summaryCard("Pending", pendingValue, AMBER, "Waiting to start"));
        cards.add(summaryCard("Critical priority", criticalValue, RED, "Score 20 or more"));
        cards.add(summaryCard("Victims covered", victimsValue, GREEN, "Distinct people"));

        return cards;
    }

    private JPanel summaryCard(
            String title,
            JLabel value,
            Color accent,
            String sub
    ) {

        JPanel card = roundedPanel(16, PANEL, LINE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(12, 16, 10, 16));

        JPanel top = transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));

        JLabel dot = new JLabel("■");
        dot.setFont(font(Font.BOLD, 10));
        dot.setForeground(accent);

        JLabel t = label(title, Font.BOLD, 12, MUTED);

        top.add(dot);
        top.add(t);

        card.add(top, BorderLayout.NORTH);

        JPanel bottom = transparent(new BorderLayout());

        value.setFont(font(Font.BOLD, 28));
        value.setForeground(accent);

        JLabel s = label(sub, Font.PLAIN, 11, MUTED);

        bottom.add(value, BorderLayout.WEST);
        bottom.add(s, BorderLayout.EAST);

        card.add(bottom, BorderLayout.CENTER);

        return card;
    }

    // =========================================================
    // TOOLBAR
    // =========================================================

    private JPanel buildToolbar() {

        JPanel toolbar = transparent(new BorderLayout(16, 0));
        toolbar.setAlignmentX(LEFT_ALIGNMENT);
        toolbar.setPreferredSize(new Dimension(0, 40));
        toolbar.setMinimumSize(new Dimension(0, 40));
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        searchField = createSearchField("Search victim, shelter or ID");
        searchField.setPreferredSize(new Dimension(320, 40));

        JPanel left = transparent(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.add(searchField);
        toolbar.add(left, BorderLayout.WEST);

        statusFilterPanel = buildStatusFilter();

        JPanel right = transparent(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.add(statusFilterPanel);
        JButton refreshButton = createButton("Refresh", PANEL2, false);
        right.add(refreshButton);
        toolbar.add(right, BorderLayout.EAST);

        DocumentListener listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        };
        searchField.getDocument().addDocumentListener(listener);

        refreshButton.addActionListener(e -> {
            loadVictims();
            loadEvacuations();
            priorityLabel.setText("Priority: —");
            priorityLabel.setForeground(MUTED);
        });

        return toolbar;
    }

    private JPanel buildStatusFilter() {

        JPanel group = roundedPanel(22, PANEL2, new Color(0x31516A));
        group.setLayout(new GridLayout(1, 4, 0, 0));
        group.setPreferredSize(new Dimension(444, 40));
        group.setMinimumSize(new Dimension(444, 40));
        group.setMaximumSize(new Dimension(444, 40));

        String[] labels = {"All", "Pending", "In progress", "Completed"};

        for (String text : labels) {

            FilterButton button = new FilterButton(text);
            button.setSelectedState(text.equals("All"));

            button.addActionListener(e -> {

                activeStatus =
                        text.equals("All")
                                ? "All"
                                : text.toUpperCase();

                for (Component c : group.getComponents()) {

                    if (c instanceof FilterButton) {

                        FilterButton b =
                                (FilterButton) c;

                        b.setSelectedState(
                                b == button
                        );
                    }
                }

                applyFilter();
            });

            group.add(button);
        }

        return group;
    }

    // =========================================================
    // TABLE
    // =========================================================

    private JComponent buildTableCard() {

        String[] columns = {
                "ID",
                "Victim",
                "Shelter",
                "Resource",
                "Priority",
                "Status",
                "Evacuation Time"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };

        evacuationTable = new JTable(tableModel) {

            @Override
            public Component prepareRenderer(
                    TableCellRenderer renderer,
                    int row,
                    int column
            ) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (!isRowSelected(row)) {
                    c.setBackground(
                            row % 2 == 0 ? PANEL : new Color(0x112536)
                    );
                }

                return c;
            }
        };

        sorter = new TableRowSorter<>(tableModel);
        evacuationTable.setRowSorter(sorter);

        sorter.setSortKeys(
                java.util.Collections.singletonList(
                        new RowSorter.SortKey(4, SortOrder.DESCENDING)
                )
        );

        evacuationTable.setRowHeight(48);
        evacuationTable.setFont(font(Font.PLAIN, 13));
        evacuationTable.setForeground(TEXT);
        evacuationTable.setBackground(PANEL);
        evacuationTable.setShowGrid(false);
        evacuationTable.setIntercellSpacing(new Dimension(0, 0));
        evacuationTable.setFillsViewportHeight(true);
        evacuationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        evacuationTable.setSelectionBackground(SELECTED);
        evacuationTable.setSelectionForeground(TEXT);
        evacuationTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = evacuationTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 44));
        header.setBackground(PANEL);
        header.setForeground(MUTED);
        header.setFont(font(Font.BOLD, 11));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setDefaultRenderer(new DarkHeaderRenderer());

        DefaultTableCellRenderer normal = new DarkCellRenderer();

        for (int i = 0; i < evacuationTable.getColumnCount(); i++) {
            evacuationTable.getColumnModel().getColumn(i).setCellRenderer(normal);
        }

        evacuationTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        evacuationTable.getColumnModel().getColumn(1).setPreferredWidth(286);
        evacuationTable.getColumnModel().getColumn(2).setPreferredWidth(105);
        evacuationTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        evacuationTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        evacuationTable.getColumnModel().getColumn(5).setPreferredWidth(145);
        evacuationTable.getColumnModel().getColumn(6).setPreferredWidth(205);

        evacuationTable.getColumnModel().getColumn(1).setCellRenderer(new VictimRenderer());
        evacuationTable.getColumnModel().getColumn(2).setCellRenderer(new IdPillRenderer());
        evacuationTable.getColumnModel().getColumn(3).setCellRenderer(new IdPillRenderer());
        evacuationTable.getColumnModel().getColumn(4).setCellRenderer(new PriorityRenderer());
        evacuationTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        JScrollPane scrollPane = new JScrollPane(evacuationTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(PANEL);
        scrollPane.setBackground(PANEL);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel card = roundedPanel(16, PANEL, LINE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(6, 6, 6, 6));
        card.setAlignmentX(LEFT_ALIGNMENT);

        card.add(scrollPane, BorderLayout.CENTER);

        activityLabel = label("", Font.PLAIN, 12, GREEN);
        activityLabel.setBorder(new EmptyBorder(10, 0, 2, 0));
        card.add(activityLabel, BorderLayout.SOUTH);

        return card;
    }

    // =========================================================
    // LOAD VICTIMS
    // =========================================================

    private void loadVictims() {

        Victim current = (Victim) victimComboBox.getSelectedItem();
        victimComboBox.removeAllItems();
        victimMap.clear();

        List<Victim> victims = victimDAO.getAllVictims();
        for (Victim victim : victims) {
            victimMap.put(victim.getVictimId(), victim);
            victimComboBox.addItem(victim);
        }

        victimComboBox.setRenderer(new VictimComboRenderer());

        if (current != null) {
            for (int i = 0; i < victimComboBox.getItemCount(); i++) {
                Victim v = victimComboBox.getItemAt(i);
                if (v.getVictimId() == current.getVictimId()) {
                    victimComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    // =========================================================
    // CALCULATE PRIORITY
    // =========================================================

    private void calculateSelectedPriority() {

        Victim selectedVictim =
                (Victim) victimComboBox.getSelectedItem();

        if (selectedVictim == null) {

            showMessage(
                    "Please select a victim first.",
                    "No Victim Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int priority =
                evacuationService.calculatePriority(selectedVictim);

        priorityLabel.setText("Priority: " + priority);

        if (priority >= 20) {
            priorityLabel.setForeground(RED);
        } else if (priority >= 10) {
            priorityLabel.setForeground(AMBER);
        } else {
            priorityLabel.setForeground(GREEN);
        }
    }

    // =========================================================
    // CREATE EVACUATION
    // =========================================================

    private void createEvacuation() {

        Victim selectedVictim =
                (Victim) victimComboBox.getSelectedItem();

        if (selectedVictim == null) {

            showMessage(
                    "Please select a victim first.",
                    "No Victim Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int victimId = selectedVictim.getVictimId();

        try {

            Evacuation evacuation =
                    evacuationService.createEvacuation(victimId);

            lastCreatedEvacuationId =
                    evacuation.getEvacuationId();

            showMessage(
                    "Evacuation created successfully!\n\n"
                            + "Victim: " + selectedVictim.getName() + "\n"
                            + "Priority: " + evacuation.getPriorityScore() + "\n"
                            + "Status: " + evacuation.getStatus(),
                    "Evacuation Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadEvacuations();
            loadVictims();

            if (activityLabel != null) {
                activityLabel.setText("Evacuation #" + evacuation.getEvacuationId()
                        + " created for " + selectedVictim.getName() + ".");
            }

            priorityLabel.setText("Priority: —");
            priorityLabel.setForeground(MUTED);

        } catch (VictimNotFoundException e) {

            showMessage(
                    "VICTIM ERROR:\n" + e.getMessage(),
                    "Victim Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (NoShelterAvailableException e) {

            showMessage(
                    "SHELTER ERROR:\n" + e.getMessage(),
                    "Shelter Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (NoResourceAvailableException e) {

            showMessage(
                    "RESOURCE ERROR:\n" + e.getMessage(),
                    "Resource Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            showMessage(
                    "Unexpected error:\n"
                            + (e.getMessage() == null ? e.toString() : e.getMessage()),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

    // =========================================================
    // LOAD EVACUATIONS
    // =========================================================

    private void loadEvacuations() {

        if (tableModel == null) return;
        tableModel.setRowCount(0);

        List<Evacuation> evacuations = evacuationDAO.getAllEvacuations();
        int pending = 0;
        int critical = 0;
        Set<Integer> victims = new HashSet<>();

        for (Evacuation evacuation : evacuations) {
            Victim victim = victimMap.get(evacuation.getVictimId());
            String victimName = victim == null
                    ? "Victim #" + evacuation.getVictimId()
                    : victim.getName();

            tableModel.addRow(new Object[]{
                    "#" + evacuation.getEvacuationId(),
                    new VictimDisplay(victimName, evacuation.getVictimId()),
                    "#" + evacuation.getShelterId(),
                    "#" + evacuation.getResourceId(),
                    String.valueOf(evacuation.getPriorityScore()),
                    evacuation.getStatus(),
                    formatTime(evacuation.getEvacuationTime())
            });

            victims.add(evacuation.getVictimId());
            if ("PENDING".equalsIgnoreCase(evacuation.getStatus())) pending++;
            if (evacuation.getPriorityScore() >= 20) critical++;
        }

        evacuationCountLabel.setText(
                evacuations.size() + (evacuations.size() == 1 ? " evacuation" : " evacuations")
        );
        totalValue.setText(String.valueOf(evacuations.size()));
        pendingValue.setText(String.valueOf(pending));
        criticalValue.setText(String.valueOf(critical));
        victimsValue.setText(String.valueOf(victims.size()));
        applyFilter();
        selectLastCreatedEvacuation();
    }

    private void selectLastCreatedEvacuation() {

        if (lastCreatedEvacuationId < 0 ||
                evacuationTable == null) {
            return;
        }

        for (int modelRow = 0;
             modelRow < tableModel.getRowCount();
             modelRow++) {

            String id =
                    String.valueOf(
                            tableModel.getValueAt(modelRow, 0)
                    );

            if (id.equals("#" + lastCreatedEvacuationId)) {

                int viewRow =
                        evacuationTable.convertRowIndexToView(
                                modelRow
                        );

                if (viewRow >= 0) {

                    evacuationTable.setRowSelectionInterval(
                            viewRow,
                            viewRow
                    );

                    evacuationTable.scrollRectToVisible(
                            evacuationTable.getCellRect(
                                    viewRow,
                                    0,
                                    true
                            )
                    );
                }

                break;
            }
        }
    }

    // =========================================================
    // FILTER
    // =========================================================

    private void applyFilter() {

        if (sorter == null) return;
        String query = searchField == null ? "" : searchField.getText().trim().toLowerCase();

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                int row = entry.getIdentifier();
                Object id = tableModel.getValueAt(row, 0);
                Object victim = tableModel.getValueAt(row, 1);
                Object shelter = tableModel.getValueAt(row, 2);
                Object resource = tableModel.getValueAt(row, 3);
                Object priority = tableModel.getValueAt(row, 4);
                Object rowStatus = tableModel.getValueAt(row, 5);

                String normalizedStatus = String.valueOf(rowStatus).toUpperCase();
                if (!"All".equals(activeStatus) && !normalizedStatus.equals(activeStatus)) return false;

                String searchable = String.valueOf(id) + " " + String.valueOf(victim) + " "
                        + String.valueOf(shelter) + " " + String.valueOf(resource) + " "
                        + String.valueOf(priority) + " " + String.valueOf(rowStatus);

                return query.isEmpty() || searchable.toLowerCase().contains(query);
            }
        });
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private String formatTime(LocalDateTime time) {

        if (time == null) {
            return "-";
        }

        return time.toString()
                .replace("T", "  ");
    }

    private JTextField createSearchField(String placeholder) {

        JTextField field = new HintTextField(placeholder);

        field.setFont(font(Font.PLAIN, 13));
        field.setForeground(TEXT);
        field.setBackground(PANEL2);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));

        field.setToolTipText(placeholder);

        return field;
    }

    private JButton createButton(
            String text,
            Color color,
            boolean filled
    ) {

        JButton button = new JButton(text) {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 =
                        (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                boolean hover = getModel().isRollover();
                boolean pressed = getModel().isPressed();

                if (filled) {

                    Color base = color;

                    if (pressed) {
                        base = color.darker();
                    } else if (hover) {
                        base = lighten(color, 0.08f);
                    }

                    g2.setColor(base);

                    g2.fillRoundRect(
                            0,
                            0,
                            getWidth(),
                            getHeight(),
                            12,
                            12
                    );

                } else {

                    g2.setColor(
                            hover || pressed
                                    ? new Color(0x203D54)
                                    : PANEL2
                    );

                    g2.fillRoundRect(
                            0,
                            0,
                            getWidth(),
                            getHeight(),
                            12,
                            12
                    );

                    g2.setColor(LINE);

                    g2.drawRoundRect(
                            0,
                            0,
                            getWidth() - 1,
                            getHeight() - 1,
                            12,
                            12
                    );
                }

                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(font(Font.BOLD, 12));
        button.setForeground(
                filled
                        ? new Color(0x06223A)
                        : TEXT
        );

        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setBorder(
                new EmptyBorder(10, 18, 10, 18)
        );
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return button;
    }

    private static Color lighten(Color color, float amount) {

        int r = Math.min(
                255,
                (int) (color.getRed() + 255 * amount)
        );

        int g = Math.min(
                255,
                (int) (color.getGreen() + 255 * amount)
        );

        int b = Math.min(
                255,
                (int) (color.getBlue() + 255 * amount)
        );

        return new Color(r, g, b);
    }

    private void showMessage(
            String message,
            String title,
            int type
    ) {

        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                type
        );
    }

    private JLabel label(
            String text,
            int style,
            int size,
            Color color
    ) {

        JLabel label =
                new JLabel(text);

        label.setFont(font(style, size));
        label.setForeground(color);

        return label;
    }

    private JPanel transparent() {
        return transparent(new BorderLayout());
    }

    private JPanel transparent(LayoutManager manager) {

        JPanel panel =
                new JPanel(manager);

        panel.setOpaque(false);

        return panel;
    }

    private JPanel roundedPanel(
            int radius,
            Color fill,
            Color border
    ) {

        return new RoundedPanel(
                radius,
                fill,
                border
        );
    }

    private static Font font(
            int style,
            int size
    ) {

        return new Font(
                FONT_FAMILY,
                style,
                size
        );
    }

    // =========================================================
    // DARK COMBO BOX
    // =========================================================

    private static class DarkComboBoxUI extends BasicComboBoxUI {

        @Override
        protected JButton createArrowButton() {

            JButton button = new BasicArrowButton(
                    BasicArrowButton.SOUTH,
                    PANEL2,
                    PANEL2,
                    MUTED,
                    PANEL2
            );

            button.setPreferredSize(new Dimension(40, 40));
            button.setMinimumSize(new Dimension(40, 40));
            button.setMaximumSize(new Dimension(40, 40));
            button.setBorder(
                    BorderFactory.createMatteBorder(
                            0, 1, 0, 0, LINE
                    )
            );
            button.setFocusPainted(false);
            button.setContentAreaFilled(true);
            button.setOpaque(true);

            return button;
        }

        @Override
        public void paintCurrentValueBackground(
                Graphics g,
                Rectangle bounds,
                boolean hasFocus
        ) {
            g.setColor(PANEL2);
            g.fillRect(
                    bounds.x,
                    bounds.y,
                    bounds.width,
                    bounds.height
            );
        }

        @Override
        public void paintCurrentValue(
                Graphics g,
                Rectangle bounds,
                boolean hasFocus
        ) {
            ListCellRenderer<? super Victim> renderer =
                    (ListCellRenderer<? super Victim>) comboBox.getRenderer();

            Victim value = (Victim) comboBox.getSelectedItem();

            @SuppressWarnings("unchecked")
            JList<Victim> victimList =
                    (JList<Victim>) (JList<?>) listBox;

            Component component = renderer.getListCellRendererComponent(
                    victimList,
                    value,
                    -1,
                    false,
                    false
            );

            component.setBackground(PANEL2);

            component.setForeground(TEXT);

            SwingUtilities.paintComponent(
                    g,
                    component,
                    comboBox,
                    bounds.x + 2,
                    bounds.y + 2,
                    Math.max(0, bounds.width - 4),
                    Math.max(0, bounds.height - 4)
            );
        }
    }

    private static class VictimComboRenderer
            extends JPanel
            implements ListCellRenderer<Victim> {

        private final CircleAvatar avatar = new CircleAvatar();
        private final JLabel nameLabel = new JLabel();
        private final JLabel idLabel = new JLabel();

        VictimComboRenderer() {

            setOpaque(true);
            setLayout(new BorderLayout(10, 0));
            setBorder(new EmptyBorder(5, 10, 5, 10));

            avatar.setPreferredSize(new Dimension(30, 30));
            avatar.setMinimumSize(new Dimension(30, 30));
            avatar.setMaximumSize(new Dimension(30, 30));

            JPanel textPanel = new JPanel();
            textPanel.setOpaque(false);
            textPanel.setLayout(new FlowLayout(
                    FlowLayout.LEFT, 0, 0
            ));

            nameLabel.setFont(font(Font.BOLD, 13));
            nameLabel.setForeground(TEXT);

            idLabel.setFont(font(Font.PLAIN, 12));
            idLabel.setForeground(MUTED);
            idLabel.setBorder(new EmptyBorder(0, 8, 0, 0));

            textPanel.add(nameLabel);
            textPanel.add(idLabel);

            add(avatar, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends Victim> list,
                Victim victim,
                int index,
                boolean selected,
                boolean focus
        ) {

            setBackground(selected ? SELECTED : PANEL2);
            setForeground(TEXT);

            if (victim == null) {
                nameLabel.setText("Select victim");
                idLabel.setText("");
                avatar.setText("?");
                avatar.setFill(BLUE);
                return this;
            }

            String name = victim.getName();
            int id = victim.getVictimId();

            nameLabel.setText(name);
            idLabel.setText("ID: " + id);

            String initials = getInitials(name);
            avatar.setText(initials);
            avatar.setFill(getAvatarColor(id));

            return this;
        }

        private static String getInitials(String name) {

            if (name == null || name.trim().isEmpty()) {
                return "V";
            }

            String[] parts = name.trim().split("\\s+");

            if (parts.length == 1) {
                return parts[0].substring(
                        0,
                        Math.min(2, parts[0].length())
                ).toUpperCase();
            }

            return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0))
                    .toUpperCase();
        }

        private static Color getAvatarColor(int id) {

            Color[] colors = {
                    new Color(0x5B3F8C),
                    new Color(0x0E7A4F),
                    new Color(0xA66A2B),
                    new Color(0x156B8A),
                    new Color(0x7A4A9B)
            };

            return colors[Math.abs(id) % colors.length];
        }
    }

    // =========================================================
    // DARK TABLE CELL
    // =========================================================

    private static class DarkCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focus, int row, int column) {

            JLabel label = (JLabel)super.getTableCellRendererComponent(
                    table, value, selected, focus, row, column);
            label.setOpaque(true);
            label.setFont(font(Font.PLAIN, 13));
            label.setForeground(column == 0 || column == 2 || column == 3 ? MUTED : TEXT);
            label.setBackground(selected ? SELECTED : (row % 2 == 0 ? PANEL : new Color(0x112536)));
            label.setBorder(new EmptyBorder(0, 14, 0, 14));
            return label;
        }
    }

    private static class VictimDisplay {
        final String name;
        final int id;
        VictimDisplay(String name, int id) { this.name = name; this.id = id; }
        @Override public String toString() { return name + " #" + id; }
    }

    private static class VictimRenderer extends JPanel implements TableCellRenderer {

        private final CircleAvatar avatar = new CircleAvatar();
        private final JLabel name = new JLabel();
        private final JLabel id = new JLabel();

        VictimRenderer() {

            setOpaque(true);
            setLayout(new BorderLayout(9, 0));

            avatar.setPreferredSize(
                    new Dimension(30, 30)
            );

            name.setFont(
                    font(Font.BOLD, 13)
            );

            id.setFont(
                    font(Font.PLAIN, 11)
            );

            id.setForeground(MUTED);

            JPanel text =
                    new JPanel(
                            new BorderLayout(4, 0)
                    );

            text.setOpaque(false);

            text.add(
                    name,
                    BorderLayout.WEST
            );

            text.add(
                    id,
                    BorderLayout.CENTER
            );

            add(
                    avatar,
                    BorderLayout.WEST
            );

            add(
                    text,
                    BorderLayout.CENTER
            );

            setBorder(
                    new EmptyBorder(
                            0,
                            12,
                            0,
                            8
                    )
            );
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focus,
                int row,
                int column
        ) {

            VictimDisplay v =
                    value instanceof VictimDisplay
                            ? (VictimDisplay) value
                            : new VictimDisplay(
                            String.valueOf(value),
                            0
                    );

            Color bg =
                    selected
                            ? SELECTED
                            : row % 2 == 0
                            ? PANEL
                            : new Color(0x112536);

            setBackground(bg);

            name.setForeground(TEXT);

            id.setText(
                    "#" + v.id
            );

            name.setText(
                    v.name
            );

            int seed =
                    Math.abs(v.id) % 4;

            Color[] colors = {
                    new Color(0x155E75),
                    new Color(0x166534),
                    new Color(0x6B4FA3),
                    new Color(0x8A5A2B)
            };

            avatar.setFill(
                    colors[seed]
            );

            avatar.setText(
                    initials(v.name)
            );

            return this;
        }

        private String initials(String value) {

            if (value == null ||
                    value.trim().isEmpty()) {
                return "#";
            }

            String[] p =
                    value.trim().split("\\s+");

            if (p.length == 1) {

                return p[0]
                        .substring(0, 1)
                        .toUpperCase();
            }

            return (
                    p[0].substring(0, 1)
                            + p[p.length - 1]
                            .substring(0, 1)
            ).toUpperCase();
        }
    }

    private static class CircleAvatar extends JLabel {

        private Color fill =
                new Color(0x155E75);

        CircleAvatar() {

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            setVerticalAlignment(
                    SwingConstants.CENTER
            );

            setForeground(TEXT);

            setFont(
                    font(Font.BOLD, 10)
            );

            setOpaque(false);
        }

        void setFill(Color fill) {

            this.fill = fill;
        }

        @Override
        protected void paintComponent(
                Graphics graphics
        ) {

            Graphics2D g =
                    (Graphics2D) graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int d =
                    Math.min(
                            getWidth(),
                            getHeight()
                    ) - 2;

            int x =
                    (getWidth() - d) / 2;

            int y =
                    (getHeight() - d) / 2;

            g.setColor(fill);

            g.fillOval(
                    x,
                    y,
                    d,
                    d
            );

            g.setColor(
                    fill.brighter()
            );

            g.drawOval(
                    x,
                    y,
                    d - 1,
                    d - 1
            );

            g.dispose();

            super.paintComponent(
                    graphics
            );
        }
    }


    // =========================================================
    // ROUNDED PILL CELL
    // =========================================================

    private static class PillCell extends DefaultTableCellRenderer {
        private Color pillBg = PANEL2;
        private Color pillFg = TEXT;
        private String pillText = "";

        void setPill(Color bg, Color fg, String text) {
            pillBg = bg;
            pillFg = fg;
            pillText = text;
            setText(text);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D)graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());

            FontMetrics fm = g.getFontMetrics(getFont());
            int w = fm.stringWidth(pillText) + 22;
            int h = Math.min(28, getHeight() - 12);
            int x = Math.max(8, (getWidth() - w) / 2);
            int y = (getHeight() - h) / 2;

            g.setColor(pillBg);
            g.fillRoundRect(x, y, w, h, h, h);
            g.setColor(pillFg);
            g.setFont(getFont());
            int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(pillText, x + 11, ty);
            g.dispose();
        }
    }

    // =========================================================
    // ROUNDED PANEL
    // =========================================================

    private static class RoundedPanel extends JPanel {

        private final int radius;
        private final Color fill;
        private final Color border;

        RoundedPanel(
                int radius,
                Color fill,
                Color border
        ) {

            this.radius = radius;
            this.fill = fill;
            this.border = border;

            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            Graphics2D g =
                    (Graphics2D) graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w = getWidth();
            int h = getHeight();

            g.setColor(fill);

            g.fillRoundRect(
                    0,
                    0,
                    w - 1,
                    h - 1,
                    radius,
                    radius
            );

            if (border != null) {

                g.setColor(border);

                g.drawRoundRect(
                        0,
                        0,
                        w - 1,
                        h - 1,
                        radius,
                        radius
                );
            }

            g.dispose();

            super.paintComponent(graphics);
        }
    }

    // =========================================================
    // TABLE HEADER
    // =========================================================

    private static class FilterButton extends JButton {

        private boolean selectedState;

        FilterButton(String text) {

            super(text);

            setFont(
                    font(Font.BOLD, 12)
            );

            setForeground(MUTED);
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(
                    new EmptyBorder(
                            0,
                            12,
                            0,
                            12
                    )
            );

            setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.HAND_CURSOR
                    )
            );
        }

        void setSelectedState(
                boolean selected
        ) {

            selectedState = selected;

            setForeground(
                    selected
                            ? TEXT
                            : MUTED
            );

            repaint();
        }

        @Override
        protected void paintComponent(
                Graphics graphics
        ) {

            Graphics2D g =
                    (Graphics2D) graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (selectedState) {

                g.setColor(
                        new Color(0x234D6C)
                );

                g.fillRoundRect(
                        2,
                        3,
                        getWidth() - 4,
                        getHeight() - 6,
                        18,
                        18
                );
            }

            g.dispose();

            super.paintComponent(
                    graphics
            );
        }
    }

    private static class HintTextField extends JTextField {

        private final String hint;

        HintTextField(String hint) {

            this.hint = hint;

            setFont(
                    font(Font.PLAIN, 13)
            );

            setForeground(TEXT);
            setBackground(PANEL2);
            setCaretColor(TEXT);

            setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    LINE,
                                    1,
                                    true
                            ),
                            new EmptyBorder(
                                    8,
                                    14,
                                    8,
                                    14
                            )
                    )
            );
        }

        @Override
        protected void paintComponent(
                Graphics graphics
        ) {

            super.paintComponent(
                    graphics
            );

            if (getText().isEmpty() &&
                    !isFocusOwner()) {

                Graphics2D g =
                        (Graphics2D) graphics.create();

                g.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                );

                g.setFont(
                        getFont()
                );

                g.setColor(
                        new Color(0x6F899C)
                );

                Insets insets =
                        getInsets();

                FontMetrics fm =
                        g.getFontMetrics();

                int y =
                        (getHeight()
                                - fm.getHeight()) / 2
                                + fm.getAscent();

                g.drawString(
                        hint,
                        insets.left,
                        y
                );

                g.dispose();
            }
        }
    }

    private static class DarkHeaderRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focus,
                int row,
                int column
        ) {

            JLabel label =
                    (JLabel) super.getTableCellRendererComponent(
                            table,
                            value,
                            selected,
                            focus,
                            row,
                            column
                    );

            label.setFont(
                    font(Font.BOLD, 11)
            );

            label.setForeground(MUTED);
            label.setBackground(PANEL);
            label.setBorder(
                    new EmptyBorder(0, 14, 0, 14)
            );

            return label;
        }
    }

    // =========================================================
    // PRIORITY RENDERER
    // =========================================================

    private static class IdPillRenderer extends PillCell {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focus,
                int row,
                int column
        ) {

            super.getTableCellRendererComponent(
                    table,
                    value,
                    selected,
                    focus,
                    row,
                    column
            );

            setFont(
                    font(Font.BOLD, 11)
            );

            setOpaque(false);

            setPill(
                    new Color(0x20364A),
                    TEXT,
                    String.valueOf(value)
            );

            setBackground(
                    selected
                            ? SELECTED
                            : row % 2 == 0
                            ? PANEL
                            : new Color(0x112536)
            );

            return this;
        }
    }

    private static class PriorityRenderer extends PillCell {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focus, int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            setFont(font(Font.BOLD, 12));
            setOpaque(false);
            int priority = 0;
            try { priority = Integer.parseInt(String.valueOf(value)); } catch (Exception ignored) { }
            if (priority >= 20) setPill(new Color(0x4A252B), RED, priority + "  CRITICAL");
            else if (priority >= 10) setPill(new Color(0x4A3820), AMBER, priority + "  HIGH");
            else setPill(new Color(0x183B35), GREEN, priority + "  LOW");
            setBackground(selected ? SELECTED : (row % 2 == 0 ? PANEL : new Color(0x112536)));
            return this;
        }
    }

    // =========================================================
    // STATUS RENDERER
    // =========================================================

    private static class StatusRenderer extends PillCell {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focus, int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            setFont(font(Font.BOLD, 11));
            setOpaque(false);
            String status = value == null ? "" : value.toString().toUpperCase();
            if ("PENDING".equals(status)) setPill(new Color(0x4A3820), AMBER, status);
            else if ("IN PROGRESS".equals(status)) setPill(new Color(0x193A55), BLUE, status);
            else if ("COMPLETED".equals(status) || "EVACUATED".equals(status)) setPill(new Color(0x183B35), GREEN, status);
            else if ("CANCELLED".equals(status) || "FAILED".equals(status)) setPill(new Color(0x4A252B), RED, status);
            else setPill(PANEL2, MUTED, status);
            setBackground(selected ? SELECTED : (row % 2 == 0 ? PANEL : new Color(0x112536)));
            return this;
        }
    }

}
