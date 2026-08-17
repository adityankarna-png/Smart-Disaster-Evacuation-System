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
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class EvacuationPanel extends JPanel {

    // =========================================================
    // DESIGN TOKENS
    // =========================================================

    // Neutrals (slate)
    private static final Color BACKGROUND    = new Color(0xF1, 0xF5, 0xF9); // slate-100
    private static final Color SURFACE       = new Color(0xFF, 0xFF, 0xFF); // white
    private static final Color BORDER_SOFT   = new Color(0xE2, 0xE8, 0xF0); // slate-200
    private static final Color TEXT          = new Color(0x0F, 0x17, 0x2A); // slate-900
    private static final Color TEXT_MUTED    = new Color(0x64, 0x74, 0x8B); // slate-500

    // Accent colors
    private static final Color BLUE          = new Color(0x25, 0x63, 0xEB); // blue-600
    private static final Color GREEN         = new Color(0x15, 0x9E, 0x5E); // emerald
    private static final Color ORANGE        = new Color(0xEA, 0x8A, 0x0C); // amber-600
    private static final Color RED           = new Color(0xDC, 0x26, 0x26); // red-600
    private static final Color NEUTRAL_BTN   = new Color(0x47, 0x55, 0x69); // slate-600

    // Soft pill backgrounds
    private static final Color GREEN_SOFT    = new Color(0xDC, 0xFC, 0xE7);
    private static final Color ORANGE_SOFT   = new Color(0xFE, 0xF3, 0xC7);
    private static final Color RED_SOFT      = new Color(0xFE, 0xE2, 0xE2);
    private static final Color NEUTRAL_SOFT  = new Color(0xE2, 0xE8, 0xF0);

    private static final Color ROW_ALT       = new Color(0xF8, 0xFA, 0xFC); // slate-50
    private static final Color SELECTION     = new Color(0xDB, 0xEA, 0xFE); // blue-100
    private static final Color HEADER_BG      = new Color(0xF8, 0xFA, 0xFC); // slate-50

    // =========================================================
    // FONTS
    // =========================================================

    private static final String FONT_FAMILY = resolveFontFamily();

    private static String resolveFontFamily() {
        String[] preferred = {
                "Segoe UI", "SF Pro Text", "Helvetica Neue", "Roboto", "Inter", "Arial"
        };
        java.util.Set<String> available = new java.util.HashSet<>(
                java.util.Arrays.asList(
                        GraphicsEnvironment.getLocalGraphicsEnvironment()
                                .getAvailableFontFamilyNames()));
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
        evacuationService = new EvacuationServiceImpl();

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(24, 28, 28, 28));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCard(), BorderLayout.CENTER);

        loadVictims();
        loadEvacuations();
    }

    // =========================================================
    // HEADER
    // =========================================================

    private JPanel buildHeader() {

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 4, 20, 4));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Evacuation Management");
        titleLabel.setFont(font(Font.BOLD, 26));
        titleLabel.setForeground(TEXT);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(
                "Prioritize victims and coordinate emergency evacuation assignments");
        subtitleLabel.setFont(font(Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_MUTED);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        evacuationCountLabel = new JLabel("0 evacuations");
        evacuationCountLabel.setFont(font(Font.BOLD, 13));
        evacuationCountLabel.setForeground(BLUE);
        evacuationCountLabel.setBorder(new EmptyBorder(8, 14, 8, 14));
        evacuationCountLabel.setOpaque(false);

        JPanel badgeWrap = new RoundedPanel(999, SELECTION, null);
        badgeWrap.setLayout(new BorderLayout());
        badgeWrap.add(evacuationCountLabel, BorderLayout.CENTER);

        JPanel eastWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        eastWrap.setOpaque(false);
        eastWrap.add(badgeWrap);

        headerPanel.add(eastWrap, BorderLayout.EAST);
        return headerPanel;
    }

    // =========================================================
    // CARD (control bar + table)
    // =========================================================

    private JComponent buildCard() {

        RoundedPanel card = new RoundedPanel(18, SURFACE, BORDER_SOFT);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(10, 10, 10, 10));

        card.add(buildControlBar(), BorderLayout.NORTH);
        card.add(buildTable(), BorderLayout.CENTER);
        return card;
    }

    private JComponent buildControlBar() {

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_SOFT),
                new EmptyBorder(6, 8, 12, 8)));

        JLabel victimLabel = new JLabel("Select Victim");
        victimLabel.setFont(font(Font.BOLD, 13));
        victimLabel.setForeground(TEXT_MUTED);
        controlPanel.add(victimLabel);

        victimComboBox = new JComboBox<>();
        victimComboBox.setPreferredSize(new Dimension(260, 38));
        victimComboBox.setFont(font(Font.PLAIN, 13));
        victimComboBox.setBackground(SURFACE);
        victimComboBox.setForeground(TEXT);
        victimComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_SOFT, 1, true),
                new EmptyBorder(2, 8, 2, 8)));
        controlPanel.add(victimComboBox);

        JButton priorityButton = createButton("Calculate Priority", ORANGE, true);
        controlPanel.add(priorityButton);

        priorityLabel = new JLabel("Priority: -");
        priorityLabel.setFont(font(Font.BOLD, 14));
        priorityLabel.setForeground(TEXT);
        priorityLabel.setBorder(new EmptyBorder(0, 8, 0, 8));
        controlPanel.add(priorityLabel);

        JButton createButton = createButton("Create Evacuation", BLUE, true);
        controlPanel.add(createButton);

        JButton refreshButton = createButton("Refresh", NEUTRAL_BTN, false);
        controlPanel.add(refreshButton);

        // Actions
        priorityButton.addActionListener(e -> calculateSelectedPriority());
        createButton.addActionListener(e -> createEvacuation());
        refreshButton.addActionListener(e -> {
            loadVictims();
            loadEvacuations();
            priorityLabel.setText("Priority: -");
            priorityLabel.setForeground(TEXT);
        });

        return controlPanel;
    }

    private JComponent buildTable() {

        String[] columns = {
                "Evacuation ID", "Victim ID", "Shelter ID", "Resource ID",
                "Priority", "Status", "Evacuation Time"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        evacuationTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer,
                                             int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? SURFACE : ROW_ALT);
                }
                return c;
            }
        };

        evacuationTable.setRowHeight(40);
        evacuationTable.setFont(font(Font.PLAIN, 13));
        evacuationTable.setForeground(TEXT);
        evacuationTable.setBackground(SURFACE);
        evacuationTable.setShowGrid(false);
        evacuationTable.setIntercellSpacing(new Dimension(0, 0));
        evacuationTable.setFillsViewportHeight(true);
        evacuationTable.setSelectionBackground(SELECTION);
        evacuationTable.setSelectionForeground(TEXT);
        evacuationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Header styling
        JTableHeader header = evacuationTable.getTableHeader();
        header.setFont(font(Font.BOLD, 12));
        header.setBackground(HEADER_BG);
        header.setForeground(TEXT_MUTED);
        header.setPreferredSize(new Dimension(0, 42));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_SOFT));
        DefaultTableCellRenderer headerRenderer =
                (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                lbl.setFont(font(Font.BOLD, 12));
                lbl.setForeground(TEXT_MUTED);
                lbl.setBackground(HEADER_BG);
                lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
                lbl.setHorizontalAlignment(
                        (column == 4 || column == 5) ? SwingConstants.CENTER : SwingConstants.LEFT);
                return lbl;
            }
        });

        // Default cell padding + alignment
        DefaultTableCellRenderer padded = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
                lbl.setForeground(isSelected ? TEXT : TEXT);
                return lbl;
            }
        };
        for (int i = 0; i < evacuationTable.getColumnCount(); i++) {
            evacuationTable.getColumnModel().getColumn(i).setCellRenderer(padded);
        }

        // Column widths
        int[] widths = {105, 90, 90, 95, 90, 120, 190};
        for (int i = 0; i < widths.length; i++) {
            evacuationTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Pill renderers
        evacuationTable.getColumnModel().getColumn(4).setCellRenderer(new PriorityRenderer());
        evacuationTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        JScrollPane scrollPane = new JScrollPane(evacuationTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.setBackground(SURFACE);
        return scrollPane;
    }

    // =========================================================
    // BUTTON FACTORY (rounded + hover)
    // =========================================================

    private JButton createButton(String text, Color color, boolean filled) {

        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                boolean hover = getModel().isRollover();
                boolean pressed = getModel().isPressed();

                if (filled) {
                    Color base = color;
                    if (pressed) {
                        base = base.darker();
                    } else if (hover) {
                        base = lighten(color, 0.10f);
                    }
                    g2.setColor(base);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                } else {
                    if (hover || pressed) {
                        g2.setColor(NEUTRAL_SOFT);
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                    }
                    g2.setColor(BORDER_SOFT);
                    g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f,
                            getWidth() - 1, getHeight() - 1, 12, 12));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(font(Font.BOLD, 13));
        button.setForeground(filled ? Color.WHITE : color);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setBorder(new EmptyBorder(9, 18, 9, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static Color lighten(Color c, float amt) {
        int r = Math.min(255, (int) (c.getRed() + 255 * amt));
        int g = Math.min(255, (int) (c.getGreen() + 255 * amt));
        int b = Math.min(255, (int) (c.getBlue() + 255 * amt));
        return new Color(r, g, b);
    }

    // =========================================================
    // LOAD VICTIMS
    // =========================================================

    private void loadVictims() {

        victimComboBox.removeAllItems();

        List<Victim> victims = victimDAO.getAllVictims();
        for (Victim victim : victims) {
            victimComboBox.addItem(victim);
        }

        victimComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                setFont(font(Font.PLAIN, 13));
                if (value instanceof Victim) {
                    Victim victim = (Victim) value;
                    setText(victim.getName() + "  (ID: " + victim.getVictimId() + ")");
                }
                return this;
            }
        });
    }

    // =========================================================
    // CALCULATE PRIORITY
    // =========================================================

    private void calculateSelectedPriority() {

        Victim selectedVictim = (Victim) victimComboBox.getSelectedItem();

        if (selectedVictim == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a victim first.",
                    "No Victim Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int priority = evacuationService.calculatePriority(selectedVictim);
        priorityLabel.setText("Priority: " + priority);

        if (priority >= 20) {
            priorityLabel.setForeground(RED);
        } else if (priority >= 10) {
            priorityLabel.setForeground(ORANGE);
        } else {
            priorityLabel.setForeground(GREEN);
        }
    }

    // =========================================================
    // CREATE EVACUATION
    // =========================================================

    private void createEvacuation() {

        Victim selectedVictim = (Victim) victimComboBox.getSelectedItem();

        if (selectedVictim == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a victim first.",
                    "No Victim Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int victimId = selectedVictim.getVictimId();

        try {
            Evacuation evacuation = evacuationService.createEvacuation(victimId);

            JOptionPane.showMessageDialog(this,
                    "Evacuation created successfully!\n\n"
                            + "Victim: " + selectedVictim.getName() + "\n"
                            + "Priority: " + evacuation.getPriorityScore() + "\n"
                            + "Status: " + evacuation.getStatus(),
                    "Evacuation Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            loadEvacuations();
            loadVictims();

            priorityLabel.setText("Priority: -");
            priorityLabel.setForeground(TEXT);

        } catch (VictimNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "VICTIM ERROR:\n" + e.getMessage(),
                    "Victim Error",
                    JOptionPane.ERROR_MESSAGE);

        } catch (NoShelterAvailableException e) {
            JOptionPane.showMessageDialog(this,
                    "SHELTER ERROR:\n" + e.getMessage(),
                    "Shelter Error",
                    JOptionPane.ERROR_MESSAGE);

        } catch (NoResourceAvailableException e) {
            JOptionPane.showMessageDialog(this,
                    "RESOURCE ERROR:\n" + e.getMessage(),
                    "Resource Error",
                    JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // =========================================================
    // LOAD EVACUATIONS
    // =========================================================

    private void loadEvacuations() {

        tableModel.setRowCount(0);

        List<Evacuation> evacuations = evacuationDAO.getAllEvacuations();

        for (Evacuation evacuation : evacuations) {
            tableModel.addRow(new Object[]{
                    evacuation.getEvacuationId(),
                    evacuation.getVictimId(),
                    evacuation.getShelterId(),
                    evacuation.getResourceId(),
                    evacuation.getPriorityScore(),
                    evacuation.getStatus(),
                    evacuation.getEvacuationTime()
            });
        }

        evacuationCountLabel.setText(evacuations.size() + " evacuations");
    }

    // =========================================================
    // ROUNDED PANEL (card / badge surface with soft shadow)
    // =========================================================

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill;
        private final Color border;

        RoundedPanel(int radius, Color fill, Color border) {
            this.radius = radius;
            this.fill = fill;
            this.border = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int shadowGap = 6;
            int w = getWidth();
            int h = getHeight();

            // Soft layered drop shadow
            for (int i = shadowGap; i > 0; i--) {
                float alpha = 0.05f * (shadowGap - i + 1) / shadowGap;
                g2.setColor(new Color(0.06f, 0.09f, 0.16f, alpha));
                g2.fill(new RoundRectangle2D.Float(
                        i, i + 1, w - i * 2, h - i * 2, radius, radius));
            }

            int inset = shadowGap;
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(
                    inset, inset, w - inset * 2, h - inset * 2, radius, radius));

            if (border != null) {
                g2.setColor(border);
                g2.draw(new RoundRectangle2D.Float(
                        inset, inset, w - inset * 2 - 1, h - inset * 2 - 1, radius, radius));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================
    // PILL BORDER (rounded background for cell text)
    // =========================================================

    private static class PillCell extends DefaultTableCellRenderer {
        private Color pillBg = NEUTRAL_SOFT;
        private Color pillFg = TEXT;

        void setPill(Color bg, Color fg) {
            this.pillBg = bg;
            this.pillFg = fg;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            FontMetrics fm = g2.getFontMetrics(getFont());
            String text = getText() == null ? "" : getText();
            int textW = fm.stringWidth(text);
            int textH = fm.getHeight();

            int padX = 12;
            int padY = 5;
            int pillW = textW + padX * 2;
            int pillH = textH + padY * 2;

            int x = (getWidth() - pillW) / 2;
            int y = (getHeight() - pillH) / 2;

            g2.setColor(pillBg);
            g2.fill(new RoundRectangle2D.Float(x, y, pillW, pillH, pillH, pillH));

            g2.setColor(pillFg);
            g2.setFont(getFont());
            int tx = x + padX;
            int ty = y + (pillH - textH) / 2 + fm.getAscent();
            g2.drawString(text, tx, ty);
            g2.dispose();
        }
    }

    // =========================================================
    // PRIORITY RENDERER (colored pill)
    // =========================================================

    private static class PriorityRenderer extends PillCell {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(font(Font.BOLD, 12));
            setOpaque(false);
            setBackground(isSelected ? SELECTION : (row % 2 == 0 ? SURFACE : ROW_ALT));

            int priority = 0;
            if (value != null) {
                try {
                    priority = Integer.parseInt(value.toString());
                } catch (NumberFormatException ignored) {
                }
            }

            if (priority >= 20) {
                setPill(RED_SOFT, RED);
            } else if (priority >= 10) {
                setPill(ORANGE_SOFT, ORANGE);
            } else {
                setPill(GREEN_SOFT, GREEN);
            }
            return this;
        }
    }

    // =========================================================
    // STATUS RENDERER (colored pill)
    // =========================================================

    private static class StatusRenderer extends PillCell {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(font(Font.BOLD, 12));
            setOpaque(false);
            setBackground(isSelected ? SELECTION : (row % 2 == 0 ? SURFACE : ROW_ALT));

            String status = value == null ? "" : value.toString().toUpperCase();

            if (status.equals("PENDING")) {
                setPill(ORANGE_SOFT, ORANGE);
            } else if (status.equals("COMPLETED") || status.equals("EVACUATED")) {
                setPill(GREEN_SOFT, GREEN);
            } else if (status.equals("CANCELLED") || status.equals("FAILED")) {
                setPill(RED_SOFT, RED);
            } else {
                setPill(NEUTRAL_SOFT, TEXT_MUTED);
            }
            return this;
        }
    }
}