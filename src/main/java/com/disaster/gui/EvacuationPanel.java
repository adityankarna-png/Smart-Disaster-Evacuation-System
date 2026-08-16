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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.List;

public class EvacuationPanel extends JPanel {

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

    private JComboBox<Victim> victimComboBox;

    private JLabel priorityLabel;

    private JLabel evacuationCountLabel;

    private JTable evacuationTable;

    private DefaultTableModel tableModel;

    private final VictimDAO victimDAO;

    private final EvacuationDAO evacuationDAO;

    private final EvacuationService evacuationService;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public EvacuationPanel() {

        victimDAO = new VictimDAO();

        evacuationDAO = new EvacuationDAO();

        evacuationService =
                new EvacuationServiceImpl();

        setLayout(
                new BorderLayout()
        );

        setBackground(
                BACKGROUND
        );

        // =====================================================
        // HEADER
        // =====================================================

        JPanel headerPanel =
                new JPanel(
                        new BorderLayout()
                );

        headerPanel.setBackground(
                BACKGROUND
        );

        headerPanel.setBorder(
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
                        "Evacuation Management"
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
                        "Prioritize victims and coordinate emergency evacuation assignments"
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
                Box.createVerticalStrut(
                        4
                )
        );

        titlePanel.add(
                subtitleLabel
        );

        headerPanel.add(
                titlePanel,
                BorderLayout.WEST
        );

        // -----------------------------------------------------
        // EVACUATION COUNT
        // -----------------------------------------------------

        evacuationCountLabel =
                new JLabel(
                        "Evacuations: 0"
                );

        evacuationCountLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        evacuationCountLabel.setForeground(
                BLUE
        );

        evacuationCountLabel.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        headerPanel.add(
                evacuationCountLabel,
                BorderLayout.EAST
        );

        add(
                headerPanel,
                BorderLayout.NORTH
        );

        // =====================================================
        // MAIN CONTENT
        // =====================================================

        JPanel centerPanel =
                new JPanel(
                        new BorderLayout()
                );

        centerPanel.setBackground(
                BACKGROUND
        );

        // =====================================================
        // CONTROL PANEL
        // =====================================================

        JPanel controlPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                10
                        )
                );

        controlPanel.setBackground(
                Color.WHITE
        );

        controlPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1,
                                0,
                                1,
                                0,
                                new Color(
                                        220,
                                        225,
                                        230
                                )
                        ),
                        new EmptyBorder(
                                8,
                                15,
                                8,
                                15
                        )
                )
        );

        // -----------------------------------------------------
        // VICTIM LABEL
        // -----------------------------------------------------

        JLabel victimLabel =
                new JLabel(
                        "Select Victim:"
                );

        victimLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        victimLabel.setForeground(
                TEXT
        );

        controlPanel.add(
                victimLabel
        );

        // -----------------------------------------------------
        // VICTIM COMBO BOX
        // -----------------------------------------------------

        victimComboBox =
                new JComboBox<>();

        victimComboBox.setPreferredSize(
                new Dimension(
                        260,
                        34
                )
        );

        victimComboBox.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        controlPanel.add(
                victimComboBox
        );

        // -----------------------------------------------------
        // PRIORITY BUTTON
        // -----------------------------------------------------

        JButton priorityButton =
                createButton(
                        "Calculate Priority",
                        ORANGE
                );

        controlPanel.add(
                priorityButton
        );

        // -----------------------------------------------------
        // PRIORITY LABEL
        // -----------------------------------------------------

        priorityLabel =
                new JLabel(
                        "Priority: -"
                );

        priorityLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        priorityLabel.setForeground(
                TEXT
        );

        priorityLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        10,
                        0,
                        10
                )
        );

        controlPanel.add(
                priorityLabel
        );

        // -----------------------------------------------------
        // CREATE BUTTON
        // -----------------------------------------------------

        JButton createButton =
                createButton(
                        "Create Evacuation",
                        BLUE
                );

        controlPanel.add(
                createButton
        );

        // -----------------------------------------------------
        // REFRESH BUTTON
        // -----------------------------------------------------

        JButton refreshButton =
                createButton(
                        "Refresh",
                        new Color(
                                100,
                                110,
                                120
                        )
                );

        controlPanel.add(
                refreshButton
        );

        centerPanel.add(
                controlPanel,
                BorderLayout.NORTH
        );

        // =====================================================
        // TABLE
        // =====================================================

        String[] columns = {

                "Evacuation ID",

                "Victim ID",

                "Shelter ID",

                "Resource ID",

                "Priority",

                "Status",

                "Evacuation Time"
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

        evacuationTable =
                new JTable(
                        tableModel
                );

        // =====================================================
        // TABLE STYLE
        // =====================================================

        evacuationTable.setRowHeight(
                34
        );

        evacuationTable.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        evacuationTable.setForeground(
                TEXT
        );

        evacuationTable.setBackground(
                Color.WHITE
        );

        evacuationTable.setGridColor(
                new Color(
                        215,
                        220,
                        225
                )
        );

        evacuationTable.setSelectionBackground(
                new Color(
                        220,
                        232,
                        245
                )
        );

        evacuationTable.setSelectionForeground(
                TEXT
        );

        evacuationTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        // =====================================================
        // TABLE HEADER
        // =====================================================

        evacuationTable.getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                13
                        )
                );

        evacuationTable.getTableHeader()
                .setBackground(
                        TABLE_HEADER
                );

        evacuationTable.getTableHeader()
                .setForeground(
                        TEXT
                );

        evacuationTable.getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                36
                        )
                );

        // =====================================================
        // COLUMN WIDTHS
        // =====================================================

        evacuationTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(105);

        evacuationTable.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(90);

        evacuationTable.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(90);

        evacuationTable.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(95);

        evacuationTable.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(90);

        evacuationTable.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(110);

        evacuationTable.getColumnModel()
                .getColumn(6)
                .setPreferredWidth(190);

        // =====================================================
        // CUSTOM RENDERERS
        // =====================================================

        evacuationTable.getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        new PriorityRenderer()
                );

        evacuationTable.getColumnModel()
                .getColumn(5)
                .setCellRenderer(
                        new StatusRenderer()
                );

        // =====================================================
        // SCROLL PANE
        // =====================================================

        JScrollPane scrollPane =
                new JScrollPane(
                        evacuationTable
                );

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        25,
                        10,
                        25
                )
        );

        scrollPane.getViewport()
                .setBackground(
                        Color.WHITE
                );

        centerPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        add(
                centerPanel,
                BorderLayout.CENTER
        );

        // =====================================================
        // BUTTON ACTIONS
        // =====================================================

        priorityButton.addActionListener(
                e -> calculateSelectedPriority()
        );

        createButton.addActionListener(
                e -> createEvacuation()
        );

        refreshButton.addActionListener(
                e -> {

                    loadVictims();

                    loadEvacuations();

                    priorityLabel.setText(
                            "Priority: -"
                    );

                    priorityLabel.setForeground(
                            TEXT
                    );
                }
        );

        // =====================================================
        // INITIAL DATA
        // =====================================================

        loadVictims();

        loadEvacuations();
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
                        16,
                        10,
                        16
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
    // LOAD VICTIMS
    // =========================================================

    private void loadVictims() {

        victimComboBox.removeAllItems();

        List<Victim> victims =
                victimDAO.getAllVictims();

        for (Victim victim : victims) {

            victimComboBox.addItem(
                    victim
            );
        }

        // =====================================================
        // CUSTOM COMBO BOX DISPLAY
        // =====================================================

        victimComboBox.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component
                    getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus
                    ) {

                        super.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                isSelected,
                                cellHasFocus
                        );

                        if (value instanceof Victim) {

                            Victim victim =
                                    (Victim) value;

                            setText(
                                    victim.getName()
                                            + " (ID: "
                                            + victim.getVictimId()
                                            + ")"
                            );
                        }

                        return this;
                    }
                }
        );
    }

    // =========================================================
    // CALCULATE PRIORITY
    // =========================================================

    private void calculateSelectedPriority() {

        Victim selectedVictim =
                (Victim)
                        victimComboBox
                                .getSelectedItem();

        if (selectedVictim == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a victim first.",
                    "No Victim Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int priority =
                evacuationService.calculatePriority(
                        selectedVictim
                );

        priorityLabel.setText(
                "Priority: "
                        + priority
        );

        // =====================================================
        // PRIORITY COLOR
        // =====================================================

        if (priority >= 20) {

            priorityLabel.setForeground(
                    RED
            );

        } else if (priority >= 10) {

            priorityLabel.setForeground(
                    ORANGE
            );

        } else {

            priorityLabel.setForeground(
                    GREEN
            );
        }
    }

    // =========================================================
    // CREATE EVACUATION
    // =========================================================

    private void createEvacuation() {

        Victim selectedVictim =
                (Victim)
                        victimComboBox
                                .getSelectedItem();

        if (selectedVictim == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a victim first.",
                    "No Victim Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int victimId =
                selectedVictim.getVictimId();

        try {

            // =================================================
            // CREATE EVACUATION THROUGH SERVICE
            // =================================================

            Evacuation evacuation =
                    evacuationService.createEvacuation(
                            victimId
                    );

            // =================================================
            // SUCCESS MESSAGE
            // =================================================

            JOptionPane.showMessageDialog(
                    this,

                    "Evacuation created successfully!\n\n"
                            + "Victim: "
                            + selectedVictim.getName()
                            + "\n"
                            + "Priority: "
                            + evacuation.getPriorityScore()
                            + "\n"
                            + "Status: "
                            + evacuation.getStatus(),

                    "Evacuation Successful",

                    JOptionPane.INFORMATION_MESSAGE
            );

            // =================================================
            // REFRESH DATA
            // =================================================

            loadEvacuations();

            loadVictims();

            priorityLabel.setText(
                    "Priority: -"
            );

            priorityLabel.setForeground(
                    TEXT
            );

        } catch (
                VictimNotFoundException e
        ) {

            JOptionPane.showMessageDialog(
                    this,

                    "VICTIM ERROR:\n"
                            + e.getMessage(),

                    "Victim Error",

                    JOptionPane.ERROR_MESSAGE
            );

        } catch (
                NoShelterAvailableException e
        ) {

            JOptionPane.showMessageDialog(
                    this,

                    "SHELTER ERROR:\n"
                            + e.getMessage(),

                    "Shelter Error",

                    JOptionPane.ERROR_MESSAGE
            );

        } catch (
                NoResourceAvailableException e
        ) {

            JOptionPane.showMessageDialog(
                    this,

                    "RESOURCE ERROR:\n"
                            + e.getMessage(),

                    "Resource Error",

                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,

                    "Unexpected error:\n"
                            + e.getMessage(),

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

        tableModel.setRowCount(
                0
        );

        List<Evacuation> evacuations =
                evacuationDAO.getAllEvacuations();

        for (
                Evacuation evacuation :
                evacuations
        ) {

            tableModel.addRow(
                    new Object[]{

                            evacuation.getEvacuationId(),

                            evacuation.getVictimId(),

                            evacuation.getShelterId(),

                            evacuation.getResourceId(),

                            evacuation.getPriorityScore(),

                            evacuation.getStatus(),

                            evacuation.getEvacuationTime()
                    }
            );
        }

        evacuationCountLabel.setText(
                "Evacuations: "
                        + evacuations.size()
        );
    }

    // =========================================================
    // PRIORITY RENDERER
    // =========================================================

    private static class PriorityRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component
        getTableCellRendererComponent(
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

                int priority =
                        Integer.parseInt(
                                value.toString()
                        );

                if (priority >= 20) {

                    component.setForeground(
                            RED
                    );

                } else if (priority >= 10) {

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
        public Component
        getTableCellRendererComponent(
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
                        "PENDING"
                )) {

                    component.setForeground(
                            ORANGE
                    );

                } else if (
                        status.equals(
                                "COMPLETED"
                        )
                                || status.equals(
                                "EVACUATED"
                        )
                ) {

                    component.setForeground(
                            GREEN
                    );

                } else if (
                        status.equals(
                                "CANCELLED"
                        )
                                || status.equals(
                                "FAILED"
                        )
                ) {

                    component.setForeground(
                            RED
                    );

                } else {

                    component.setForeground(
                            TEXT
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