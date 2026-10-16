package com.disaster.gui;

import com.disaster.dao.VictimDAO;
import com.disaster.model.Victim;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class VictimPanel extends JPanel {

    private static final Color BG       = new Color(0x08, 0x18, 0x21);
    private static final Color PANEL    = new Color(0x13, 0x2B, 0x3B);
    private static final Color PANEL2   = new Color(0x18, 0x35, 0x4A);
    private static final Color PANEL3   = new Color(0x1B, 0x3D, 0x55);
    private static final Color LINE     = new Color(0x2A, 0x4D, 0x63);
    private static final Color TEXT     = new Color(0xE8, 0xF0, 0xF6);
    private static final Color MUTED    = new Color(0x8D, 0xA2, 0xB3);

    private static final Color BLUE     = new Color(0x55, 0xB4, 0xFF);
    private static final Color RED      = new Color(0xFF, 0x5B, 0x68);
    private static final Color AMBER    = new Color(0xF4, 0xB4, 0x4B);
    private static final Color GREEN    = new Color(0x3D, 0xD4, 0xA0);
    private static final Color ORANGE   = new Color(0xFF, 0x8A, 0x3D);
    private static final Color TEAL    = GREEN;

    private static final Color BLUE_SOFT  = new Color(0x18, 0x3D, 0x59);
    private static final Color RED_SOFT   = new Color(0x4B, 0x26, 0x31);
    private static final Color AMBER_SOFT = new Color(0x4A, 0x3B, 0x20);
    private static final Color GREEN_SOFT = new Color(0x19, 0x42, 0x37);
    private static final Color SEL        = new Color(0x21, 0x4B, 0x6A);

    private static final Font SANS_BOLD14  = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font SANS_BOLD12  = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font SANS_PLAIN13 = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font SANS_PLAIN12 = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font MONO12       = new Font("Consolas", Font.PLAIN, 12);
    private static final Font SANS_BOLD22  = new Font("Segoe UI", Font.BOLD, 22);

    private JTable victimTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private VictimDAO victimDAO;

    private JLabel victimCountLabel;
    private JLabel allVictimsValue;
    private JLabel criticalVictimsValue;
    private JLabel highVictimsValue;
    private JLabel lowVictimsValue;
    private JLabel footerLabel;

    private JTextField searchField;

    public VictimPanel() {
        victimDAO = new VictimDAO();

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(10, 28, 10, 28));

        add(buildTop(), BorderLayout.NORTH);
        add(buildMain(), BorderLayout.CENTER);

        loadVictims();
    }

    private JPanel buildTop() {
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Victim Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Manage registered victims and evacuation priority information");
        subtitle.setFont(SANS_PLAIN13);
        subtitle.setForeground(MUTED);

        titles.add(title);
        titles.add(Box.createVerticalStrut(2));
        titles.add(subtitle);

        victimCountLabel = new JLabel("0 victims");
        victimCountLabel.setFont(SANS_BOLD12);
        victimCountLabel.setForeground(BLUE);

        RoundedPanel count = new RoundedPanel(22, BLUE_SOFT);
        count.setLayout(new BorderLayout());
        count.setBorder(new EmptyBorder(7, 13, 7, 13));
        count.add(victimCountLabel);

        JPanel countWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        countWrap.setOpaque(false);
        countWrap.add(count);

        titleRow.add(titles, BorderLayout.WEST);
        titleRow.add(countWrap, BorderLayout.EAST);

        top.add(titleRow);
        top.add(Box.createVerticalStrut(13));
        top.add(buildStats());

        return top;
    }

    private JPanel buildStats() {
        JPanel stats = new JPanel(new GridLayout(1, 4, 10, 0));
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(10, 78));

        allVictimsValue = statValue("0", BLUE);
        criticalVictimsValue = statValue("0", RED);
        highVictimsValue = statValue("0", AMBER);
        lowVictimsValue = statValue("0", GREEN);

        stats.add(createStatCard("All victims", allVictimsValue, "Registered", BLUE, true));
        stats.add(createStatCard("Critical", criticalVictimsValue, "Score 20+", RED, false));
        stats.add(createStatCard("High", highVictimsValue, "Score 10 to 19", AMBER, false));
        stats.add(createStatCard("Low", lowVictimsValue, "Score under 10", GREEN, false));

        return stats;
    }

    private JLabel statValue(String value, Color color) {
        JLabel label = new JLabel(value);
        label.setFont(new Font("Segoe UI", Font.BOLD, 27));
        label.setForeground(color);
        return label;
    }

    private JPanel createStatCard(String title, JLabel value, String suffix,
                                  Color accent, boolean selected) {
        RoundedPanel card = new RoundedPanel(9, PANEL);
        card.setLayout(new BorderLayout());
        card.setBorder(new CompoundBorder(
                new LineBorder(selected ? BLUE : LINE, selected ? 2 : 1, true),
                new EmptyBorder(9, 13, 9, 13)
        ));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JPanel labelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelRow.setOpaque(false);

        JLabel dot = new JLabel("■");
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        dot.setForeground(accent);

        JLabel label = new JLabel(title);
        label.setFont(SANS_BOLD12);
        label.setForeground(MUTED);

        labelRow.add(dot);
        labelRow.add(Box.createHorizontalStrut(6));
        labelRow.add(label);

        left.add(labelRow);
        left.add(Box.createVerticalStrut(1));
        left.add(value);

        JLabel suffixLabel = new JLabel(suffix);
        suffixLabel.setFont(SANS_PLAIN12);
        suffixLabel.setForeground(MUTED);

        card.add(left, BorderLayout.CENTER);
        card.add(suffixLabel, BorderLayout.EAST);

        return card;
    }

    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(12, 0, 0, 0));

        main.add(buildToolbar(), BorderLayout.NORTH);
        main.add(buildTableCard(), BorderLayout.CENTER);

        return main;
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(10, 38));

        searchField = new JTextField();
        searchField.setFont(SANS_PLAIN12);
        searchField.setForeground(TEXT);
        searchField.setBackground(PANEL2);
        searchField.setCaretColor(BLUE);
        searchField.setBorder(new CompoundBorder(
                new LineBorder(LINE, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        searchField.setPreferredSize(new Dimension(312, 37));
        searchField.setToolTipText("Search victims");

        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        searchWrap.setPreferredSize(new Dimension(315, 37));
        searchWrap.add(searchField);

        JLabel hint = new JLabel("Most urgent first. Click a column to sort.");
        hint.setFont(SANS_PLAIN12);
        hint.setForeground(MUTED);

        bar.add(searchWrap, BorderLayout.WEST);
        bar.add(hint, BorderLayout.EAST);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        return bar;
    }

    private JPanel buildTableCard() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        RoundedPanel card = new RoundedPanel(11, PANEL);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 5, 0, 5));

        card.add(buildTable(), BorderLayout.CENTER);
        card.add(buildActionBar(), BorderLayout.SOUTH);

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private JScrollPane buildTable() {
        String[] cols = {
                "ID", "NAME", "AGE", "MEDICAL CONDITION",
                "VULNERABILITY", "ZONE RISK", "WAITING", "PRIORITY", "STATUS"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0 || column == 2 || column == 4 ||
                        column == 5 || column == 6 || column == 7) {
                    return Integer.class;
                }
                return String.class;
            }
        };

        victimTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        victimTable.setRowSorter(sorter);

        styleTable();

        int[] widths = {58, 190, 58, 190, 145, 120, 95, 135, 125};
        for (int i = 0; i < widths.length; i++) {
            victimTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        victimTable.getColumnModel().getColumn(0).setCellRenderer(new IdRenderer());
        victimTable.getColumnModel().getColumn(1).setCellRenderer(new NameRenderer());
        victimTable.getColumnModel().getColumn(2).setCellRenderer(new CenterRenderer());
        victimTable.getColumnModel().getColumn(3).setCellRenderer(new MedicalRenderer());
        victimTable.getColumnModel().getColumn(4).setCellRenderer(new RiskRenderer());
        victimTable.getColumnModel().getColumn(5).setCellRenderer(new RiskRenderer());
        victimTable.getColumnModel().getColumn(6).setCellRenderer(new CenterRenderer());
        victimTable.getColumnModel().getColumn(7).setCellRenderer(new PriorityPillRenderer());
        victimTable.getColumnModel().getColumn(8).setCellRenderer(new StatusPillRenderer());

        victimTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    showUpdateVictimDialog();
                }
            }
        });

        victimTable.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke("DELETE"), "deleteVictim");
        victimTable.getActionMap().put("deleteVictim", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteVictim();
            }
        });

        victimTable.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke("ENTER"), "updateVictim");
        victimTable.getActionMap().put("updateVictim", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUpdateVictimDialog();
            }
        });

        JScrollPane scroll = new JScrollPane(victimTable);
        scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        scroll.getViewport().setBackground(PANEL);
        scroll.setBackground(PANEL);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JScrollBar vs = scroll.getVerticalScrollBar();
        vs.setPreferredSize(new Dimension(9, 0));
        vs.setBackground(PANEL);
        vs.setUI(new ThinScrollBarUI());

        return scroll;
    }

    private void styleTable() {
        victimTable.setFont(SANS_PLAIN13);
        victimTable.setForeground(TEXT);
        victimTable.setBackground(PANEL);
        victimTable.setRowHeight(46);
        victimTable.setShowGrid(false);
        victimTable.setIntercellSpacing(new Dimension(0, 0));
        victimTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        victimTable.setSelectionBackground(SEL);
        victimTable.setSelectionForeground(TEXT);
        victimTable.setFillsViewportHeight(true);
        victimTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        JTableHeader th = victimTable.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 11));
        th.setBackground(PANEL2);
        th.setForeground(MUTED);
        th.setPreferredSize(new Dimension(0, 42));
        th.setReorderingAllowed(false);
        th.setResizingAllowed(false);
        th.setBorder(new MatteBorder(0, 0, 1, 0, LINE));
        th.setDefaultRenderer(new HeaderRenderer());

        sorter.setSortKeys(List.of(
                new RowSorter.SortKey(7, SortOrder.DESCENDING)
        ));
    }

    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(12, 2, 10, 2));

        footerLabel = new JLabel("Showing 0 of 0 victims");
        footerLabel.setFont(SANS_PLAIN12);
        footerLabel.setForeground(MUTED);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        JButton refresh = makeButton("Refresh", PANEL2, PANEL3, TEXT, false);
        JButton add = makeButton("Add victim", BLUE, new Color(0x73, 0xC4, 0xFF),
                new Color(0x06, 0x22, 0x3A), false);
        JButton update = makeButton("Update victim", PANEL2, PANEL3, TEXT, false);
        JButton delete = makeButton("Delete victim", new Color(0x5A, 0x20, 0x29),
                new Color(0x74, 0x30, 0x3A), RED, true);

        buttons.add(refresh);
        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);

        refresh.addActionListener(e -> {
            loadVictims();
            footerLabel.setText("Victims refreshed.");
        });
        add.addActionListener(e -> showAddVictimDialog());
        update.addActionListener(e -> showUpdateVictimDialog());
        delete.addActionListener(e -> deleteVictim());

        bar.add(footerLabel, BorderLayout.WEST);
        bar.add(buttons, BorderLayout.EAST);

        return bar;
    }

    private JButton makeButton(String text, Color bg, Color hover,
                               Color fg, boolean outline) {
        JButton button = new JButton(text) {
            private boolean hovering;

            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) {
                        hovering = true;
                        repaint();
                    }
                    @Override public void mouseExited(MouseEvent e) {
                        hovering = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                aa(g);

                g.setColor(hovering ? hover : bg);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                if (outline) {
                    g.setColor(RED);
                    g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }

                g.dispose();
                super.paintComponent(g0);
            }
        };

        button.setFont(SANS_BOLD12);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(new EmptyBorder(9, 16, 9, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    private static void aa(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        HeaderRenderer() {
            setHorizontalAlignment(SwingConstants.LEFT);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {
            super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(MUTED);
            setBackground(PANEL2);
            setBorder(new EmptyBorder(0, 10, 0, 10));
            return this;
        }
    }

    private static class BaseRenderer extends DefaultTableCellRenderer {
        BaseRenderer(int align) {
            setHorizontalAlignment(align);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {
            super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column);

            setFont(SANS_PLAIN13);
            setForeground(TEXT);
            setBackground(selected ? SEL : (row % 2 == 0 ? PANEL : BG));
            setBorder(new EmptyBorder(0, 10, 0, 10));
            return this;
        }
    }

    private static class IdRenderer extends BaseRenderer {
        IdRenderer() {
            super(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {
            super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column);
            setFont(MONO12);
            setForeground(selected ? TEXT : BLUE);
            return this;
        }
    }

    private static class CenterRenderer extends BaseRenderer {
        CenterRenderer() {
            super(SwingConstants.CENTER);
        }
    }

    private static class MedicalRenderer extends BaseRenderer {
        MedicalRenderer() {
            super(SwingConstants.LEFT);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {
            super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column);

            String text = value == null ? "" : value.toString();

            if (!"None".equalsIgnoreCase(text) && !text.isEmpty()) {
                setText("●  " + text);
                setForeground(selected ? TEXT : RED);
            } else {
                setText("None");
                setForeground(selected ? TEXT : MUTED);
            }

            return this;
        }
    }

    private static class NameRenderer extends JPanel
            implements TableCellRenderer {

        private final AvatarLabel avatar = new AvatarLabel();
        private final JLabel name = new JLabel();

        NameRenderer() {
            setOpaque(true);
            setLayout(new GridBagLayout());

            GridBagConstraints a = new GridBagConstraints();
            a.gridx = 0;
            a.gridy = 0;
            a.weightx = 0;
            a.weighty = 1;
            a.anchor = GridBagConstraints.CENTER;
            a.insets = new Insets(0, 8, 0, 8);

            GridBagConstraints n = new GridBagConstraints();
            n.gridx = 1;
            n.gridy = 0;
            n.weightx = 1;
            n.weighty = 1;
            n.fill = GridBagConstraints.HORIZONTAL;
            n.anchor = GridBagConstraints.WEST;
            n.insets = new Insets(0, 0, 0, 8);

            avatar.setPreferredSize(new Dimension(28, 28));
            name.setFont(SANS_PLAIN13);
            name.setForeground(TEXT);

            add(avatar, a);
            add(name, n);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {

            String n = value == null ? "" : value.toString().trim();
            name.setText(n);

            String initials = "";
            if (!n.isEmpty()) {
                String[] parts = n.split("\\s+");

                if (parts.length >= 2) {
                    initials = String.valueOf(parts[0].charAt(0))
                            + parts[parts.length - 1].charAt(0);
                } else {
                    initials = parts[0].length() >= 2
                            ? parts[0].substring(0, 2)
                            : parts[0];
                }
            }

            avatar.setText(initials.toUpperCase());

            Color rowColor = selected
                    ? SEL
                    : (row % 2 == 0 ? PANEL : BG);

            setBackground(rowColor);
            avatar.setSelected(selected);

            return this;
        }
    }

    private static class AvatarLabel extends JLabel {
        private boolean selected;

        AvatarLabel() {
            super("", SwingConstants.CENTER);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 9));
            setForeground(TEXT);
        }

        void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);

            int size = Math.min(getWidth(), getHeight()) - 1;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g.setColor(selected
                    ? new Color(BLUE.getRed(), BLUE.getGreen(), BLUE.getBlue(), 85)
                    : new Color(BLUE.getRed(), BLUE.getGreen(), BLUE.getBlue(), 45));
            g.fillOval(x, y, size, size);

            g.setColor(new Color(
                    BLUE.getRed(), BLUE.getGreen(), BLUE.getBlue(), 150));
            g.drawOval(x, y, size - 1, size - 1);

            g.setFont(new Font("Segoe UI", Font.BOLD, 9));
            g.setColor(TEXT);

            FontMetrics fm = g.getFontMetrics();
            String text = getText();
            int tx = (getWidth() - fm.stringWidth(text)) / 2;
            int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            g.drawString(text, tx, ty);

            g.dispose();
        }
    }

    private static class RiskRenderer extends JPanel
            implements TableCellRenderer {

        private int value;
        private boolean selected;
        private Color accent;

        RiskRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {

            try {
                this.value = Integer.parseInt(String.valueOf(value));
            } catch (Exception e) {
                this.value = 0;
            }

            this.selected = selected;
            this.accent = riskColor(this.value);
            setBackground(selected ? SEL : (row % 2 == 0 ? PANEL : BG));

            return this;
        }

        private static Color riskColor(int value) {
            if (value >= 9) return RED;
            if (value >= 6) return ORANGE;
            if (value >= 4) return AMBER;
            return GREEN;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);

            Graphics2D g = (Graphics2D) g0.create();
            aa(g);

            String valueText = String.valueOf(value);
            g.setFont(new Font("Consolas", Font.BOLD, 12));

            int valueWidth = 22;
            int startX = getWidth() / 2 - 42;
            int centerY = getHeight() / 2;

            g.setColor(selected ? TEXT : accent);
            g.drawString(valueText, startX, centerY + 5);

            int barX = startX + valueWidth;
            int barY = centerY - 3;
            int barW = 65;
            int barH = 6;
            int gap = 2;

            for (int i = 0; i < 10; i++) {
                int x = barX + i * (barW / 10);
                boolean active = i < value;

                g.setColor(active
                        ? accent
                        : new Color(255, 255, 255, 25));

                g.fillRoundRect(x, barY,
                        Math.max(3, (barW / 10) - gap),
                        barH, 3, 3);
            }

            g.dispose();
        }
    }

    private static class PriorityPillRenderer extends JPanel
            implements TableCellRenderer {

        private String text = "";
        private Color pillBg = PANEL2;
        private Color pillFg = TEXT;
        private boolean selected;

        PriorityPillRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {

            this.selected = selected;
            setBackground(selected ? SEL : (row % 2 == 0 ? PANEL : BG));

            int score = 0;
            try {
                score = Integer.parseInt(String.valueOf(value));
            } catch (Exception ignored) {
            }

            if (score >= 20) {
                text = score + "  CRITICAL";
                pillBg = RED_SOFT;
                pillFg = RED;
            } else if (score >= 10) {
                text = score + "  HIGH";
                pillBg = AMBER_SOFT;
                pillFg = AMBER;
            } else {
                text = score + "  LOW";
                pillBg = GREEN_SOFT;
                pillFg = GREEN;
            }

            return this;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);

            Graphics2D g = (Graphics2D) g0.create();
            aa(g);

            g.setFont(new Font("Segoe UI", Font.BOLD, 10));
            FontMetrics fm = g.getFontMetrics();

            int w = fm.stringWidth(text) + 20;
            int h = 22;
            int x = (getWidth() - w) / 2;
            int y = (getHeight() - h) / 2;

            g.setColor(pillBg);
            g.fillRoundRect(x, y, w, h, h, h);

            g.setColor(pillFg);
            g.drawString(text, x + 10,
                    y + (h + fm.getAscent() - fm.getDescent()) / 2);

            g.dispose();
        }
    }

    private static class StatusPillRenderer extends JPanel
            implements TableCellRenderer {

        private String text = "";
        private Color bg = AMBER_SOFT;
        private Color fg = AMBER;

        StatusPillRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {

            setBackground(selected ? SEL : (row % 2 == 0 ? PANEL : BG));
            text = value == null ? "" : value.toString().toUpperCase();

            if ("EVACUATED".equals(text) || "SAFE".equals(text)) {
                bg = GREEN_SOFT;
                fg = GREEN;
            } else if ("CRITICAL".equals(text)) {
                bg = RED_SOFT;
                fg = RED;
            } else {
                bg = AMBER_SOFT;
                fg = AMBER;
            }

            return this;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);

            Graphics2D g = (Graphics2D) g0.create();
            aa(g);

            g.setFont(new Font("Segoe UI", Font.BOLD, 10));
            FontMetrics fm = g.getFontMetrics();

            int w = fm.stringWidth(text) + 22;
            int h = 22;
            int x = (getWidth() - w) / 2;
            int y = (getHeight() - h) / 2;

            g.setColor(bg);
            g.fillRoundRect(x, y, w, h, h, h);

            g.setColor(fg);
            g.drawString(text, x + 11,
                    y + (h + fm.getAscent() - fm.getDescent()) / 2);

            g.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color fill;

        RoundedPanel(int arc, Color fill) {
            this.arc = arc;
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);

            g.setColor(fill);
            g.fillRoundRect(0, 0,
                    getWidth() - 1, getHeight() - 1,
                    arc, arc);

            g.setColor(LINE);
            g.drawRoundRect(0, 0,
                    getWidth() - 1, getHeight() - 1,
                    arc, arc);

            g.dispose();
            super.paintComponent(g0);
        }
    }

    private static class ThinScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(0x3A, 0x5A, 0x70);
            trackColor = PANEL;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return zeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return zeroButton();
        }

        private JButton zeroButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }
    }


    // Compatibility aliases used by the existing CRUD/dialog implementation.
    private static final Color SURFACE = PANEL;
    private static final Color SURFACE2 = PANEL2;
    private static final Color BORDER_CLR = new Color(255, 255, 255, 25);
    private static final Color TEAL_DIM = new Color(0, 201, 167, 30);
    private static final Color RED_DIM = new Color(255, 91, 104, 35);
    private static final Color AMBER_DIM = new Color(244, 180, 75, 35);
    private static final Color GREEN_DIM = new Color(61, 212, 160, 35);
    private static final Color SLATE = new Color(51, 65, 85);

    // ═══════════════════════════════════════════════════════════════════════
    // DATA LOADING
    // ═══════════════════════════════════════════════════════════════════════

    private void loadVictims() {
        tableModel.setRowCount(0);

        try {
            List<Victim> victims = victimDAO.getAllVictims();

            int critical = 0;
            int high = 0;
            int low = 0;

            for (Victim v : victims) {
                tableModel.addRow(new Object[]{
                        v.getVictimId(),
                        v.getName(),
                        v.getAge(),
                        v.getMedicalCondition(),
                        v.getVulnerabilityScore(),
                        v.getZoneRisk(),
                        v.getWaitingTime(),
                        v.getPriorityScore(),
                        v.getStatus()
                });

                int score = v.getPriorityScore();

                if (score >= 20) critical++;
                else if (score >= 10) high++;
                else low++;
            }

            victimCountLabel.setText(
                    victims.size() + (victims.size() == 1 ? " victim" : " victims"));

            allVictimsValue.setText(String.valueOf(victims.size()));
            criticalVictimsValue.setText(String.valueOf(critical));
            highVictimsValue.setText(String.valueOf(high));
            lowVictimsValue.setText(String.valueOf(low));

            if (sorter != null) {
                sorter.setSortKeys(List.of(
                        new RowSorter.SortKey(7, SortOrder.DESCENDING)
                ));
            }

            footerLabel.setText(
                    "Showing " + victims.size() + " of " + victims.size() + " victims");

            filterTable();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading victims:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTable() {
        if (sorter == null || searchField == null) return;

        String q = searchField.getText().trim();

        if (q.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(
                    "(?i)" + java.util.regex.Pattern.quote(q)));
        }

        int visible = victimTable.getRowCount();
        footerLabel.setText(
                "Showing " + visible + " of " + tableModel.getRowCount() + " victims");
    }


    private static class RoundBorder extends AbstractBorder {
        private final int arc;
        private final Color color;

        RoundBorder(int arc, Color color) {
            this.arc = arc;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            aa(g2);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, arc, arc);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(arc / 2, arc / 2, arc / 2, arc / 2);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DELETE
    // ═══════════════════════════════════════════════════════════════════════

    private void deleteVictim() {
        int row = victimTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a victim first.",
                    "No Victim Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow  = victimTable.convertRowIndexToModel(row);
        int victimId  = (int) tableModel.getValueAt(modelRow, 0);
        String name   = tableModel.getValueAt(modelRow, 1).toString();

        int choice = JOptionPane.showConfirmDialog(this,
                "Remove \"" + name + "\" (ID #" + victimId + ") from the system?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) return;

        try {
            victimDAO.deleteVictim(victimId);
            JOptionPane.showMessageDialog(this,
                    "Victim removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadVictims();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting victim:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ADD DIALOG
    // ═══════════════════════════════════════════════════════════════════════

    private void showAddVictimDialog() {
        JTextField disasterIdField = dialogField("");
        JTextField nameField = dialogField("");
        JTextField ageField = dialogField("");
        JTextField medicalField = dialogField("None");

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(24, 28, 8, 28));

        JLabel title = dialogTitle("Add victim");
        JLabel subtitle = dialogSubtitle("Register a person who needs evacuation assistance.");
        form.add(title);
        form.add(Box.createVerticalStrut(3));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(20));

        JPanel nameAge = new JPanel(new GridLayout(1, 2, 12, 0));
        nameAge.setOpaque(false);
        nameAge.add(labeledField("Name", nameField));
        nameAge.add(labeledField("Age", ageField));
        form.add(nameAge);
        form.add(Box.createVerticalStrut(13));

        form.add(labeledField("Disaster ID", disasterIdField));
        form.add(Box.createVerticalStrut(13));

        form.add(labeledField("Medical condition", medicalField));
        form.add(Box.createVerticalStrut(8));
        form.add(quickMedicalPills(medicalField));
        form.add(Box.createVerticalStrut(14));

        JPanel note = new JPanel(new BorderLayout());
        note.setOpaque(false);
        JLabel noteText = new JLabel("New victims start with vulnerability, zone risk and waiting time at 0.");
        noteText.setFont(SANS_PLAIN12);
        noteText.setForeground(MUTED);
        note.add(noteText, BorderLayout.WEST);
        form.add(note);

        showVictimDialog("Add victim", form, "Cancel", "Add victim", () -> {
            try {
                int disasterId = Integer.parseInt(disasterIdField.getText().trim());
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String medical = medicalField.getText().trim();

                if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
                if (age <= 0) throw new IllegalArgumentException("Age must be greater than 0.");

                Victim v = new Victim();
                v.setDisasterId(disasterId);
                v.setName(name);
                v.setAge(age);
                v.setMedicalCondition(medical.isEmpty() ? "None" : medical);
                v.setVulnerabilityScore(0);
                v.setZoneRisk(0);
                v.setWaitingTime(0);
                v.setPriorityScore(0);
                v.setStatus("WAITING");

                victimDAO.addVictim(v);
                loadVictims();

                JOptionPane.showMessageDialog(this,
                        "Victim added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Disaster ID and Age must be valid numbers.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        e.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error adding victim:\n" + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // UPDATE DIALOG
    // ═══════════════════════════════════════════════════════════════════════

    private void showUpdateVictimDialog() {
        int row = victimTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a victim first.",
                    "No Victim Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = victimTable.convertRowIndexToModel(row);
        int victimId = (int) tableModel.getValueAt(modelRow, 0);

        int currentDisasterId = 0;
        try {
            List<Victim> victims = victimDAO.getAllVictims();
            for (Victim v : victims) {
                if (v.getVictimId() == victimId) {
                    currentDisasterId = v.getDisasterId();
                    break;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading victim details:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nameField = dialogField(String.valueOf(tableModel.getValueAt(modelRow, 1)));
        JTextField ageField = dialogField(String.valueOf(tableModel.getValueAt(modelRow, 2)));
        JTextField medicalField = dialogField(String.valueOf(tableModel.getValueAt(modelRow, 3)));
        JTextField waitingField = dialogField(String.valueOf(tableModel.getValueAt(modelRow, 6)));

        int currentVulnerability = (int) tableModel.getValueAt(modelRow, 4);
        int currentZoneRisk = (int) tableModel.getValueAt(modelRow, 5);
        int currentPriority = (int) tableModel.getValueAt(modelRow, 7);
        String currentStatus = String.valueOf(tableModel.getValueAt(modelRow, 8));

        CirclePicker vulnerabilityPicker = new CirclePicker(currentVulnerability, false);
        CirclePicker zonePicker = new CirclePicker(currentZoneRisk, true);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(24, 28, 8, 28));

        JLabel title = dialogTitle("Update victim");
        JLabel subtitle = dialogSubtitle("Edit the details of victim #" + victimId);
        form.add(title);
        form.add(Box.createVerticalStrut(3));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(20));

        JPanel nameAge = new JPanel(new GridLayout(1, 2, 12, 0));
        nameAge.setOpaque(false);
        nameAge.add(labeledField("Name", nameField));
        nameAge.add(labeledField("Age", ageField));
        form.add(nameAge);
        form.add(Box.createVerticalStrut(13));

        form.add(labeledField("Medical condition", medicalField));
        form.add(Box.createVerticalStrut(8));
        form.add(quickMedicalPills(medicalField));
        form.add(Box.createVerticalStrut(13));

        form.add(labeledComponent("Vulnerability (1 to 10)", vulnerabilityPicker));
        form.add(Box.createVerticalStrut(10));

        form.add(labeledComponent("Zone risk (1 to 10)", zonePicker));
        form.add(Box.createVerticalStrut(10));

        JPanel waitingStatus = new JPanel(new GridLayout(1, 2, 12, 0));
        waitingStatus.setOpaque(false);
        waitingStatus.add(labeledField("Waiting time", waitingField));

        StatusPicker statusPicker = new StatusPicker(currentStatus);
        waitingStatus.add(labeledComponent("Status", statusPicker));
        form.add(waitingStatus);
        form.add(Box.createVerticalStrut(13));

        JPanel priorityRow = new JPanel(new BorderLayout());
        priorityRow.setOpaque(false);
        JLabel priorityLabel = new JLabel("Calculated priority");
        priorityLabel.setFont(SANS_BOLD12);
        priorityLabel.setForeground(MUTED);

        JLabel priorityPill = new JLabel(priorityText(currentPriority), SwingConstants.CENTER);
        priorityPill.setFont(new Font("Segoe UI", Font.BOLD, 12));
        priorityPill.setForeground(currentPriority >= 20 ? RED : currentPriority >= 10 ? AMBER : GREEN);
        priorityPill.setOpaque(true);
        priorityPill.setBorder(new EmptyBorder(7, 14, 7, 14));
        priorityPill.setBackground(currentPriority >= 20 ? RED_SOFT : currentPriority >= 10 ? AMBER_SOFT : GREEN_SOFT);

        priorityRow.add(priorityLabel, BorderLayout.WEST);
        priorityRow.add(priorityPill, BorderLayout.CENTER);
        form.add(priorityRow);

        final int savedDisasterId = currentDisasterId;

        // Keep the existing stored priority exactly as it is; the reference treats it as calculated/read-only.
        showVictimDialog("Update victim", form, "Cancel", "Save changes", () -> {
            try {
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                int waiting = Integer.parseInt(waitingField.getText().trim());

                if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
                if (age <= 0) throw new IllegalArgumentException("Age must be greater than 0.");
                if (waiting < 0) throw new IllegalArgumentException("Waiting time cannot be negative.");

                Victim v = new Victim();
                v.setVictimId(victimId);
                v.setDisasterId(savedDisasterId);
                v.setName(name);
                v.setAge(age);
                v.setMedicalCondition(medicalField.getText().trim().isEmpty()
                        ? "None" : medicalField.getText().trim());
                v.setVulnerabilityScore(vulnerabilityPicker.getValue());
                v.setZoneRisk(zonePicker.getValue());
                v.setWaitingTime(waiting);
                v.setPriorityScore(currentPriority);
                v.setStatus(statusPicker.getDatabaseValue());

                victimDAO.updateVictim(v);
                loadVictims();

                JOptionPane.showMessageDialog(this,
                        "Victim updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Age and Waiting time must be valid numbers.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        e.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error updating victim:\n" + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private String priorityText(int score) {
        if (score >= 20) return score + "   CRITICAL";
        if (score >= 10) return score + "   HIGH";
        return score + "   LOW";
    }

    private JLabel dialogTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setForeground(TEXT);
        return label;
    }

    private JLabel dialogSubtitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SANS_PLAIN13);
        label.setForeground(MUTED);
        return label;
    }

    private JPanel labeledField(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(SANS_BOLD12);
        l.setForeground(MUTED);
        panel.add(l, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel labeledComponent(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(SANS_BOLD12);
        l.setForeground(MUTED);
        panel.add(l, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JTextField dialogField(String value) {
        JTextField field = new JTextField(value);
        field.setFont(SANS_PLAIN13);
        field.setForeground(TEXT);
        field.setBackground(PANEL2);
        field.setCaretColor(BLUE);
        field.setBorder(new CompoundBorder(
                new RoundBorder(7, LINE),
                new EmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(100, 40));
        return field;
    }

    private JPanel quickMedicalPills(JTextField field) {
        JPanel pills = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pills.setOpaque(false);
        String[] values = {"None", "Injury", "Heart Condition", "Fracture", "Cancer"};
        for (String value : values) {
            JButton button = new JButton(value);
            button.setFont(SANS_BOLD12);
            button.setForeground(MUTED);
            button.setBackground(PANEL2);
            button.setFocusPainted(false);
            button.setBorder(new EmptyBorder(6, 12, 6, 12));
            button.setContentAreaFilled(true);
            button.addActionListener(e -> field.setText(value));
            pills.add(button);
        }
        return pills;
    }

    private void showVictimDialog(String title, JPanel form,
                                  String cancelText, String saveText,
                                  Runnable saveAction) {
        final JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                title,
                Dialog.ModalityType.APPLICATION_MODAL);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PANEL);
        root.setBorder(new LineBorder(LINE, 1, true));
        root.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.setBorder(new EmptyBorder(0, 28, 20, 28));

        JButton cancel = dialogButton(cancelText, PANEL2, PANEL3, TEXT, false);
        JButton save = dialogButton(saveText, BLUE, new Color(0x73, 0xC4, 0xFF),
                new Color(0x06, 0x22, 0x3A), false);

        buttons.add(cancel);
        buttons.add(save);
        root.add(buttons, BorderLayout.SOUTH);

        cancel.addActionListener(e -> dialog.dispose());
        save.addActionListener(e -> {
            saveAction.run();
            dialog.dispose();
        });

        dialog.setContentPane(root);
        dialog.setUndecorated(true);
        dialog.setResizable(false);
        dialog.setSize(title.startsWith("Update") ? 500 : 500,
                title.startsWith("Update") ? 610 : 470);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JButton dialogButton(String text, Color base, Color hover,
                                 Color fg, boolean outline) {
        JButton button = new JButton(text) {
            private boolean hovering;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                aa(g);
                g.setColor(hovering ? hover : base);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                if (outline) {
                    g.setColor(RED);
                    g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                g.dispose();
                super.paintComponent(g0);
            }
        };
        button.setFont(SANS_BOLD12);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static class CirclePicker extends JPanel {
        private int value;
        private final boolean warm;
        private int hover = -1;

        CirclePicker(int value, boolean warm) {
            this.value = Math.max(0, Math.min(10, value));
            this.warm = warm;
            setOpaque(false);
            setPreferredSize(new Dimension(430, 36));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    int index = indexAt(e.getX());
                    if (index >= 1) {
                        CirclePicker.this.value = index;
                        repaint();
                    }
                }
                @Override public void mouseExited(MouseEvent e) {
                    hover = -1;
                    repaint();
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    hover = indexAt(e.getX());
                    repaint();
                }
            });
        }

        int getValue() { return value; }

        private int indexAt(int x) {
            int start = 16;
            for (int i = 1; i <= 10; i++) {
                int cx = start + (i - 1) * 34;
                if (x >= cx && x <= cx + 30) return i;
            }
            return -1;
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);

            for (int i = 1; i <= 10; i++) {
                int x = 16 + (i - 1) * 34;
                int y = 2;
                boolean active = i <= value;
                boolean hot = i == hover;

                Color fill;
                Color border;
                Color textColor;
                if (active) {
                    if (warm) {
                        fill = i >= 9 ? ORANGE : AMBER;
                        border = fill;
                    } else {
                        fill = i >= 8 ? RED : i >= 5 ? AMBER : GREEN;
                        border = fill;
                    }
                    textColor = new Color(0x0B, 0x1A, 0x24);
                } else {
                    fill = new Color(255, 255, 255, 0);
                    border = new Color(0x4D, 0x68, 0x7B);
                    textColor = MUTED;
                }

                if (hot && !active) border = BLUE;

                g.setColor(fill);
                g.fillOval(x, y, 30, 30);
                g.setColor(border);
                g.drawOval(x, y, 30, 30);

                g.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g.setColor(textColor);
                String s = String.valueOf(i);
                FontMetrics fm = g.getFontMetrics();
                g.drawString(s, x + (30 - fm.stringWidth(s)) / 2,
                        y + (30 - fm.getHeight()) / 2 + fm.getAscent());
            }
            g.dispose();
        }
    }

    private static class StatusPicker extends JPanel {
        private final String[] labels = {"Waiting", "Evacuated", "Critical"};
        private final String[] values = {"WAITING", "EVACUATED", "CRITICAL"};
        private int selected;
        private int hover = -1;

        StatusPicker(String current) {
            setOpaque(false);
            setPreferredSize(new Dimension(250, 40));
            selected = 0;
            for (int i = 0; i < values.length; i++) {
                if (values[i].equalsIgnoreCase(current)) {
                    selected = i;
                    break;
                }
            }

            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    int i = indexAt(e.getX());
                    if (i >= 0) {
                        selected = i;
                        repaint();
                    }
                }
                @Override public void mouseExited(MouseEvent e) {
                    hover = -1;
                    repaint();
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    hover = indexAt(e.getX());
                    repaint();
                }
            });
        }

        String getDatabaseValue() { return values[selected]; }

        private int indexAt(int x) {
            int width = Math.max(1, getWidth() / 3);
            int i = x / width;
            return i >= 0 && i < 3 ? i : -1;
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);

            int w = getWidth();
            int h = 38;
            g.setColor(PANEL2);
            g.fillRoundRect(0, 0, w - 1, h, h, h);
            g.setColor(LINE);
            g.drawRoundRect(0, 0, w - 1, h, h, h);

            int segment = w / 3;
            int x = selected * segment + 2;
            g.setColor(hover >= 0 ? new Color(0x1D, 0x4B, 0x6A) : PANEL3);
            g.fillRoundRect(x, 2, segment - 4, h - 4, h - 4, h - 4);
            g.setColor(BLUE);
            g.drawRoundRect(x, 2, segment - 4, h - 4, h - 4, h - 4);

            g.setFont(SANS_BOLD12);
            for (int i = 0; i < 3; i++) {
                FontMetrics fm = g.getFontMetrics();
                int tx = i * segment + (segment - fm.stringWidth(labels[i])) / 2;
                g.setColor(i == selected ? TEXT : MUTED);
                g.drawString(labels[i], tx,
                        (h - fm.getHeight()) / 2 + fm.getAscent());
            }
            g.dispose();
        }
    }


}
