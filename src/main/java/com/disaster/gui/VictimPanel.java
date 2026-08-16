package com.disaster.gui;

import com.disaster.dao.VictimDAO;
import com.disaster.model.Victim;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VictimPanel extends JPanel {

    // ==========================================
    // COLORS
    // ==========================================

    private static final Color BACKGROUND =
            new Color(245, 247, 250);

    private static final Color CARD_BACKGROUND =
            Color.WHITE;

    private static final Color HEADER_COLOR =
            new Color(25, 55, 90);

    private static final Color BLUE =
            new Color(50, 105, 175);

    private static final Color GREEN =
            new Color(40, 150, 90);

    private static final Color RED =
            new Color(200, 65, 65);

    private static final Color ORANGE =
            new Color(220, 135, 35);

    private static final Color TEXT =
            new Color(35, 45, 55);

    private static final Color SECONDARY_TEXT =
            new Color(100, 110, 120);

    private static final Color BORDER =
            new Color(215, 220, 225);

    // ==========================================
    // COMPONENTS
    // ==========================================

    private JTable victimTable;

    private DefaultTableModel tableModel;

    private VictimDAO victimDAO;

    private JLabel victimCountLabel;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public VictimPanel() {

        victimDAO = new VictimDAO();

        setLayout(new BorderLayout());

        setBackground(BACKGROUND);

        // ==========================================
        // TOP SECTION
        // ==========================================

        JPanel topPanel =
                new JPanel(new BorderLayout());

        topPanel.setBackground(BACKGROUND);

        topPanel.setBorder(
                new EmptyBorder(
                        22,
                        28,
                        15,
                        28
                )
        );


        JLabel titleLabel =
                new JLabel("Victim Management");

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
                        "Manage registered victims and evacuation priority information"
                );

        subtitleLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        subtitleLabel.setForeground(
                SECONDARY_TEXT
        );


        JPanel titlePanel =
                new JPanel();

        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS
                )
        );

        titlePanel.setBackground(BACKGROUND);

        titlePanel.add(titleLabel);

        titlePanel.add(
                Box.createVerticalStrut(5)
        );

        titlePanel.add(subtitleLabel);


        // ==========================================
        // VICTIM COUNT
        // ==========================================

        victimCountLabel =
                new JLabel("Victims: 0");

        victimCountLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        victimCountLabel.setForeground(
                BLUE
        );


        topPanel.add(
                titlePanel,
                BorderLayout.WEST
        );

        topPanel.add(
                victimCountLabel,
                BorderLayout.EAST
        );


        add(
                topPanel,
                BorderLayout.NORTH
        );


        // ==========================================
        // TABLE
        // ==========================================

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


        victimTable =
                new JTable(tableModel);


        victimTable.setRowHeight(32);

        victimTable.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        victimTable.setForeground(TEXT);

        victimTable.setBackground(
                Color.WHITE
        );

        victimTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        victimTable.setGridColor(BORDER);

        victimTable.setShowGrid(true);

        victimTable.setIntercellSpacing(
                new Dimension(1, 1)
        );


        // ==========================================
        // TABLE HEADER
        // ==========================================

        victimTable.getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                13
                        )
                );

        victimTable.getTableHeader()
                .setForeground(TEXT);

        victimTable.getTableHeader()
                .setBackground(
                        new Color(235, 240, 245)
                );

        victimTable.getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                36
                        )
                );


        // ==========================================
        // COLUMN WIDTHS
        // ==========================================

        int[] widths = {

                75,     // ID
                130,    // Name
                55,     // Age
                145,    // Medical
                100,    // Vulnerability
                85,     // Zone
                100,    // Waiting
                80,     // Priority
                100     // Status
        };


        for (
                int i = 0;
                i < widths.length;
                i++
        ) {

            victimTable
                    .getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(
                            widths[i]
                    );
        }


        // ==========================================
        // CENTER ALIGN NUMERIC COLUMNS
        // ==========================================

        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );


        for (
                int i = 0;
                i < victimTable
                        .getColumnCount();
                i++
        ) {

            if (
                    i == 0 ||
                            i == 2 ||
                            i == 4 ||
                            i == 5 ||
                            i == 6 ||
                            i == 7
            ) {

                victimTable
                        .getColumnModel()
                        .getColumn(i)
                        .setCellRenderer(
                                centerRenderer
                        );
            }
        }


        // ==========================================
        // ALTERNATING ROW COLORS
        // ==========================================

        victimTable.setDefaultRenderer(
                Object.class,
                new DefaultTableCellRenderer() {

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


                        if (isSelected) {

                            component.setBackground(
                                    new Color(
                                            210,
                                            225,
                                            245
                                    )
                            );

                            component.setForeground(
                                    TEXT
                            );

                        } else {

                            if (row % 2 == 0) {

                                component.setBackground(
                                        Color.WHITE
                                );

                            } else {

                                component.setBackground(
                                        new Color(
                                                248,
                                                250,
                                                252
                                        )
                                );

                            }

                            component.setForeground(
                                    TEXT
                            );
                        }


                        setBorder(
                                BorderFactory
                                        .createEmptyBorder(
                                                0,
                                                5,
                                                0,
                                                5
                                        )
                        );


                        return component;
                    }
                }
        );


        // ==========================================
        // PRIORITY RENDERER
        // ==========================================

        victimTable
                .getColumnModel()
                .getColumn(7)
                .setCellRenderer(
                        new DefaultTableCellRenderer() {

                            @Override
                            public Component
                            getTableCellRendererComponent(
                                    JTable table,
                                    Object value,
                                    boolean selected,
                                    boolean focus,
                                    int row,
                                    int column
                            ) {

                                JLabel label =
                                        (JLabel)
                                                super
                                                        .getTableCellRendererComponent(
                                                                table,
                                                                value,
                                                                selected,
                                                                focus,
                                                                row,
                                                                column
                                                        );

                                label.setHorizontalAlignment(
                                        SwingConstants.CENTER
                                );

                                if (!selected) {

                                    try {

                                        int priority =
                                                Integer.parseInt(
                                                        value.toString()
                                                );

                                        if (priority >= 20) {

                                            label.setForeground(
                                                    RED
                                            );

                                            label.setFont(
                                                    label.getFont()
                                                            .deriveFont(
                                                                    Font.BOLD
                                                            )
                                            );

                                        } else if (
                                                priority >= 10
                                        ) {

                                            label.setForeground(
                                                    ORANGE
                                            );

                                        } else {

                                            label.setForeground(
                                                    GREEN
                                            );
                                        }

                                    } catch (Exception ignored) {
                                    }
                                }

                                return label;
                            }
                        }
                );


        // ==========================================
        // STATUS RENDERER
        // ==========================================

        victimTable
                .getColumnModel()
                .getColumn(8)
                .setCellRenderer(
                        new DefaultTableCellRenderer() {

                            @Override
                            public Component
                            getTableCellRendererComponent(
                                    JTable table,
                                    Object value,
                                    boolean selected,
                                    boolean focus,
                                    int row,
                                    int column
                            ) {

                                JLabel label =
                                        (JLabel)
                                                super
                                                        .getTableCellRendererComponent(
                                                                table,
                                                                value,
                                                                selected,
                                                                focus,
                                                                row,
                                                                column
                                                        );

                                label.setHorizontalAlignment(
                                        SwingConstants.CENTER
                                );

                                if (!selected) {

                                    String status =
                                            value == null
                                                    ? ""
                                                    : value.toString()
                                                    .toUpperCase();

                                    if (
                                            status.equals(
                                                    "WAITING"
                                            )
                                    ) {

                                        label.setForeground(
                                                ORANGE
                                        );

                                    } else if (
                                            status.equals(
                                                    "EVACUATED"
                                            )
                                    ) {

                                        label.setForeground(
                                                GREEN
                                        );

                                    } else if (
                                            status.equals(
                                                    "CRITICAL"
                                            )
                                    ) {

                                        label.setForeground(
                                                RED
                                        );

                                    } else {

                                        label.setForeground(
                                                SECONDARY_TEXT
                                        );
                                    }
                                }

                                return label;
                            }
                        }
                );


        JScrollPane scrollPane =
                new JScrollPane(
                        victimTable
                );

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        BORDER
                )
        );

        scrollPane.setBackground(
                CARD_BACKGROUND
        );


        JPanel tableWrapper =
                new JPanel(
                        new BorderLayout()
                );

        tableWrapper.setBackground(
                BACKGROUND
        );

        tableWrapper.setBorder(
                new EmptyBorder(
                        0,
                        28,
                        0,
                        28
                )
        );

        tableWrapper.add(
                scrollPane,
                BorderLayout.CENTER
        );


        add(
                tableWrapper,
                BorderLayout.CENTER
        );


        // ==========================================
        // BUTTON PANEL
        // ==========================================

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                15
                        )
                );

        buttonPanel.setBackground(
                BACKGROUND
        );

        buttonPanel.setBorder(
                new EmptyBorder(
                        5,
                        28,
                        15,
                        28
                )
        );


        JButton addButton =
                createButton(
                        "Add Victim",
                        BLUE
                );

        JButton updateButton =
                createButton(
                        "Update Victim",
                        new Color(
                                70,
                                120,
                                170
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
                        "Delete Victim",
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


        // ==========================================
        // BUTTON ACTIONS
        // ==========================================

        refreshButton.addActionListener(
                e -> loadVictims()
        );


        deleteButton.addActionListener(
                e -> deleteVictim()
        );


        addButton.addActionListener(
                e -> showAddVictimDialog()
        );


        updateButton.addActionListener(
                e -> showUpdateVictimDialog()
        );


        // ==========================================
        // DOUBLE CLICK UPDATE
        // ==========================================

        victimTable.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e
                    ) {

                        if (
                                e.getClickCount() == 2
                                        &&
                                        SwingUtilities.isLeftMouseButton(e)
                        ) {

                            showUpdateVictimDialog();
                        }
                    }
                }
        );


        // ==========================================
        // LOAD DATA
        // ==========================================

        loadVictims();
    }


    // ==========================================
    // CREATE BUTTON
    // ==========================================

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

        button.setForeground(
                Color.WHITE
        );

        button.setBackground(
                color
        );

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


    // ==========================================
    // LOAD VICTIMS
    // ==========================================

    private void loadVictims() {

        tableModel.setRowCount(0);

        try {

            List<Victim> victims =
                    victimDAO.getAllVictims();


            for (Victim victim : victims) {

                Object[] row = {

                        victim.getVictimId(),

                        victim.getName(),

                        victim.getAge(),

                        victim.getMedicalCondition(),

                        victim.getVulnerabilityScore(),

                        victim.getZoneRisk(),

                        victim.getWaitingTime(),

                        victim.getPriorityScore(),

                        victim.getStatus()
                };


                tableModel.addRow(row);
            }


            victimCountLabel.setText(
                    "Victims: " + victims.size()
            );


        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading victims:\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }


    // ==========================================
    // DELETE VICTIM
    // ==========================================

    private void deleteVictim() {

        int selectedRow =
                victimTable.getSelectedRow();


        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a victim first.",
                    "No Victim Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        int victimId =
                (int) tableModel.getValueAt(
                        selectedRow,
                        0
                );


        String victimName =
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString();


        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete victim \"" +
                                victimName +
                                "\" (ID: " +
                                victimId +
                                ")?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );


        if (
                choice ==
                        JOptionPane.YES_OPTION
        ) {

            try {

                victimDAO.deleteVictim(
                        victimId
                );


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
                        "Error deleting victim:\n"
                                + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }


    // ==========================================
    // ADD VICTIM
    // ==========================================

    private void showAddVictimDialog() {

        JTextField disasterIdField =
                new JTextField();

        JTextField nameField =
                new JTextField();

        JTextField ageField =
                new JTextField();

        JTextField medicalField =
                new JTextField();


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


        panel.add(
                new JLabel("Disaster ID:")
        );

        panel.add(
                disasterIdField
        );


        panel.add(
                new JLabel("Name:")
        );

        panel.add(
                nameField
        );


        panel.add(
                new JLabel("Age:")
        );

        panel.add(
                ageField
        );


        panel.add(
                new JLabel("Medical Condition:")
        );

        panel.add(
                medicalField
        );


        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Add Victim",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );


        if (
                result !=
                        JOptionPane.OK_OPTION
        ) {

            return;
        }


        try {

            int disasterId =
                    Integer.parseInt(
                            disasterIdField
                                    .getText()
                                    .trim()
                    );


            String name =
                    nameField
                            .getText()
                            .trim();


            int age =
                    Integer.parseInt(
                            ageField
                                    .getText()
                                    .trim()
                    );


            String medicalCondition =
                    medicalField
                            .getText()
                            .trim();


            if (name.isEmpty()) {

                throw new IllegalArgumentException(
                        "Name cannot be empty."
                );
            }


            if (age <= 0) {

                throw new IllegalArgumentException(
                        "Age must be greater than 0."
                );
            }


            Victim victim =
                    new Victim();


            victim.setDisasterId(
                    disasterId
            );

            victim.setName(
                    name
            );

            victim.setAge(
                    age
            );

            victim.setMedicalCondition(
                    medicalCondition
            );


            // Default values

            victim.setVulnerabilityScore(
                    0
            );

            victim.setZoneRisk(
                    0
            );

            victim.setWaitingTime(
                    0
            );

            victim.setPriorityScore(
                    0
            );

            victim.setStatus(
                    "WAITING"
            );


            victimDAO.addVictim(
                    victim
            );


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
                    "Error adding victim:\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // ==========================================
    // UPDATE VICTIM
    // ==========================================

    private void showUpdateVictimDialog() {

        int selectedRow =
                victimTable.getSelectedRow();


        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a victim first.",
                    "No Victim Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        int victimId =
                (int) tableModel.getValueAt(
                        selectedRow,
                        0
                );


        String currentName =
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString();


        int currentAge =
                (int) tableModel.getValueAt(
                        selectedRow,
                        2
                );


        String currentMedical =
                tableModel.getValueAt(
                        selectedRow,
                        3
                ).toString();


        int currentVulnerability =
                (int) tableModel.getValueAt(
                        selectedRow,
                        4
                );


        int currentZoneRisk =
                (int) tableModel.getValueAt(
                        selectedRow,
                        5
                );


        int currentWaitingTime =
                (int) tableModel.getValueAt(
                        selectedRow,
                        6
                );


        int currentPriority =
                (int) tableModel.getValueAt(
                        selectedRow,
                        7
                );


        String currentStatus =
                tableModel.getValueAt(
                        selectedRow,
                        8
                ).toString();


        // ==========================================
        // GET DISASTER ID
        // ==========================================

        int currentDisasterId = 0;


        List<Victim> victims =
                victimDAO.getAllVictims();


        for (Victim victim : victims) {

            if (
                    victim.getVictimId()
                            == victimId
            ) {

                currentDisasterId =
                        victim.getDisasterId();

                break;
            }
        }


        // ==========================================
        // INPUT FIELDS
        // ==========================================

        JTextField disasterIdField =
                new JTextField(
                        String.valueOf(
                                currentDisasterId
                        )
                );


        JTextField nameField =
                new JTextField(
                        currentName
                );


        JTextField ageField =
                new JTextField(
                        String.valueOf(
                                currentAge
                        )
                );


        JTextField medicalField =
                new JTextField(
                        currentMedical
                );


        JTextField vulnerabilityField =
                new JTextField(
                        String.valueOf(
                                currentVulnerability
                        )
                );


        JTextField zoneRiskField =
                new JTextField(
                        String.valueOf(
                                currentZoneRisk
                        )
                );


        JTextField waitingTimeField =
                new JTextField(
                        String.valueOf(
                                currentWaitingTime
                        )
                );


        JTextField priorityField =
                new JTextField(
                        String.valueOf(
                                currentPriority
                        )
                );


        JTextField statusField =
                new JTextField(
                        currentStatus
                );


        // ==========================================
        // FORM
        // ==========================================

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                9,
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


        panel.add(
                new JLabel("Disaster ID:")
        );

        panel.add(
                disasterIdField
        );


        panel.add(
                new JLabel("Name:")
        );

        panel.add(
                nameField
        );


        panel.add(
                new JLabel("Age:")
        );

        panel.add(
                ageField
        );


        panel.add(
                new JLabel("Medical Condition:")
        );

        panel.add(
                medicalField
        );


        panel.add(
                new JLabel("Vulnerability Score:")
        );

        panel.add(
                vulnerabilityField
        );


        panel.add(
                new JLabel("Zone Risk:")
        );

        panel.add(
                zoneRiskField
        );


        panel.add(
                new JLabel("Waiting Time:")
        );

        panel.add(
                waitingTimeField
        );


        panel.add(
                new JLabel("Priority Score:")
        );

        panel.add(
                priorityField
        );


        panel.add(
                new JLabel("Status:")
        );

        panel.add(
                statusField
        );


        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Update Victim ID "
                                + victimId,
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );


        if (
                result !=
                        JOptionPane.OK_OPTION
        ) {

            return;
        }


        try {

            int disasterId =
                    Integer.parseInt(
                            disasterIdField
                                    .getText()
                                    .trim()
                    );


            String name =
                    nameField
                            .getText()
                            .trim();


            int age =
                    Integer.parseInt(
                            ageField
                                    .getText()
                                    .trim()
                    );


            String medicalCondition =
                    medicalField
                            .getText()
                            .trim();


            int vulnerability =
                    Integer.parseInt(
                            vulnerabilityField
                                    .getText()
                                    .trim()
                    );


            int zoneRisk =
                    Integer.parseInt(
                            zoneRiskField
                                    .getText()
                                    .trim()
                    );


            int waitingTime =
                    Integer.parseInt(
                            waitingTimeField
                                    .getText()
                                    .trim()
                    );


            int priority =
                    Integer.parseInt(
                            priorityField
                                    .getText()
                                    .trim()
                    );


            String status =
                    statusField
                            .getText()
                            .trim();


            if (name.isEmpty()) {

                throw new IllegalArgumentException(
                        "Name cannot be empty."
                );
            }


            Victim victim =
                    new Victim();


            victim.setVictimId(
                    victimId
            );

            victim.setDisasterId(
                    disasterId
            );

            victim.setName(
                    name
            );

            victim.setAge(
                    age
            );

            victim.setMedicalCondition(
                    medicalCondition
            );

            victim.setVulnerabilityScore(
                    vulnerability
            );

            victim.setZoneRisk(
                    zoneRisk
            );

            victim.setWaitingTime(
                    waitingTime
            );

            victim.setPriorityScore(
                    priority
            );

            victim.setStatus(
                    status
            );


            victimDAO.updateVictim(
                    victim
            );


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
                    "ID, age, scores and waiting time must be valid numbers.",
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
                    "Error updating victim:\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}