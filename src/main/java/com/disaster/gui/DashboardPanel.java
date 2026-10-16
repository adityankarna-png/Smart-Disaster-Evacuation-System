package com.disaster.gui;

import com.disaster.dao.DisasterDAO;
import com.disaster.dao.ResourceDAO;
import com.disaster.dao.ShelterDAO;
import com.disaster.dao.VictimDAO;
import com.disaster.database.DBConnection;
import com.disaster.earthquake.EarthquakeData;
import com.disaster.service.EvacuationServiceImpl;
import com.disaster.service.SystemMonitor;
import com.disaster.weather.WeatherData;
import com.disaster.weather.WeatherService;
import com.disaster.model.Evacuation;
import com.disaster.model.Victim;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class DashboardPanel extends JPanel {

    // =========================================================
    // COLORS & PALETTE
    // =========================================================

    private static final Color BG         = new Color(0x0C1822);
    private static final Color PANEL      = new Color(0x14283A);
    private static final Color PANEL2     = new Color(0x1A3247);
    private static final Color LINE       = new Color(0x26435B);
    private static final Color TEXT       = new Color(0xE8F0F5);
    private static final Color MUTED      = new Color(0x8BA5B8);
    private static final Color RED        = new Color(0xFF5A5F);
    private static final Color AMBER      = new Color(0xF5A83C);
    private static final Color GREEN      = new Color(0x3FC79A);
    private static final Color BLUE       = new Color(0x4FA8F0);

    private static Font font(int style, float size) {
        return new Font("Segoe UI", Font.PLAIN, 12).deriveFont(style, size);
    }

    private static void aa(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    private static JLabel lbl(String text, int style, float size, Color c) {
        JLabel l = new JLabel(text);
        l.setFont(font(style, size));
        l.setForeground(c);
        return l;
    }

    private static JPanel transparent(LayoutManager lm) {
        JPanel p = new JPanel(lm);
        p.setOpaque(false);
        return p;
    }

    private static JPanel vbox() {
        JPanel p = transparent(null);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    private static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // =========================================================
    // REUSABLE COMPONENTS
    // =========================================================

    static class Card extends JPanel {
        Color fill = PANEL;
        int arc = 18;

        Card(LayoutManager lm) {
            super(lm);
            setOpaque(false);
            setBorder(new EmptyBorder(18, 20, 18, 20));
            setAlignmentX(LEFT_ALIGNMENT);
        }

        @Override public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE); // Allowed to stretch vertically
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            RoundRectangle2D r = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, arc, arc);
            g.setColor(fill);
            g.fill(r);
            g.setColor(LINE);
            g.draw(r);
            g.dispose();
        }
    }

    static class PillButton extends JButton {
        private final Color base;
        private boolean outline;
        private boolean hover;

        PillButton(String text, Color base, Color fg, boolean outline) {
            super(text);
            this.base = base;
            this.outline = outline;
            setForeground(fg);
            setFont(font(Font.BOLD, 13));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 18, 10, 18));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        void setOutline(boolean outline, Color fg) {
            this.outline = outline;
            setForeground(fg);
            repaint();
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            RoundRectangle2D r = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12);
            Color c = isEnabled() ? base : base.darker();
            if (outline) {
                g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), hover ? 40 : 0));
                g.fill(r);
                g.setColor(c);
                g.setStroke(new BasicStroke(1.5f));
                g.draw(r);
            } else {
                g.setColor(hover && isEnabled() ? c.brighter() : c);
                g.fill(r);
            }
            g.dispose();
            super.paintComponent(g0);
        }
    }

    static class Chip extends JComponent {
        private String text;
        private Color color;

        Chip(String text, Color color) {
            this.text = text;
            this.color = color;
            setFont(font(Font.BOLD, 12));
        }

        void set(String text, Color color) {
            this.text = text;
            this.color = color;
            revalidate();
            repaint();
        }

        @Override public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(fm.stringWidth(text) + 34, 26);
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 46));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g.setColor(color);
            g.fillOval(11, getHeight() / 2 - 3, 7, 7);
            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, 24, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g.dispose();
        }
    }

    static class Readout extends JPanel {
        final JLabel value;

        Readout(String label, String initial) {
            super(new BorderLayout(0, 2));
            setOpaque(false);
            setBorder(new EmptyBorder(10, 14, 10, 14));
            value = lbl(initial, Font.BOLD, 15, TEXT);
            add(lbl(label, Font.BOLD, 12, MUTED), BorderLayout.NORTH);
            add(value, BorderLayout.CENTER);
        }

        void set(String text, Color c) {
            value.setText(text);
            value.setForeground(c);
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            g.setColor(PANEL2);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g.dispose();
        }
    }

    static class StatTile extends JComponent {
        private final String label, sub;
        private final Color color;
        private final boolean divider;
        private int shown, target;
        private boolean hover;
        private Timer anim;

        StatTile(String label, String sub, Color color, boolean divider, Runnable onClick) {
            this.label = label;
            this.sub = sub;
            this.color = color;
            this.divider = divider;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(220, 150));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) { onClick.run(); }
            });
        }

        void setValue(int v, boolean animate) {
            if (anim != null) anim.stop();
            int from = animate ? 0 : shown;
            target = v;
            if (!animate) { shown = v; repaint(); return; }
            long t0 = System.currentTimeMillis();
            anim = new Timer(16, null);
            anim.addActionListener(e -> {
                double p = Math.min(1, (System.currentTimeMillis() - t0) / 800.0);
                double ease = 1 - Math.pow(1 - p, 3);
                shown = (int) Math.round(from + (target - from) * ease);
                repaint();
                if (p >= 1) anim.stop();
            });
            anim.start();
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            g.setColor(hover ? PANEL2 : PANEL);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(color);
            g.fillRect(0, 0, getWidth(), 4);
            if (divider) {
                g.setColor(LINE);
                g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
            }
            g.fillRoundRect(22, 26, 10, 10, 3, 3);
            g.setFont(font(Font.BOLD, 13));
            g.setColor(MUTED);
            g.drawString(label, 40, 36);
            g.setFont(font(Font.BOLD, 54));
            g.setColor(color);
            g.drawString(String.valueOf(shown), 20, 98);
            g.setFont(font(Font.PLAIN, 12.5f));
            g.setColor(MUTED);
            g.drawString(sub, 22, getHeight() - 20);
            g.dispose();
        }
    }

    static class Strip extends JPanel {
        Strip() {
            super(new GridLayout(1, 4));
            setOpaque(false);
            setAlignmentX(LEFT_ALIGNMENT);
        }

        @Override public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
        }

        @Override protected void paintChildren(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            g.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
            super.paintChildren(g);
            g.setClip(null);
            g.setColor(LINE);
            g.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 18, 18));
            g.dispose();
        }
    }

    static class Seismograph extends JComponent {
        private static final int N = 360;
        private final double[] buf = new double[N];
        private final Random rnd = new Random(7);
        private double amp = 0.12, target = 0.12, burst = 0;
        private int phase;
        private boolean alarm;
        private String magText = "3.0";
        private final Timer timer = new Timer(33, e -> { sample(); repaint(); });

        Seismograph() {
            setPreferredSize(new Dimension(400, 220));
            setMinimumSize(new Dimension(100, 220));
            for (int i = 0; i < N; i++) sample();
            timer.start();
        }

        void setMagnitude(String m) { magText = m; repaint(); }

        void setAlarm(boolean alarm) {
            this.alarm = alarm;
            target = alarm ? 0.85 : 0.12;
            if (alarm) burst = 1;
        }

        private void sample() {
            amp += (target - amp) * 0.03;
            phase++;
            double v = (Math.sin(phase * 0.21) * 0.35 + Math.sin(phase * 0.67) * 0.25 + (rnd.nextDouble() - 0.5) * 0.9) * amp;
            if (burst > 0) {
                v += (rnd.nextDouble() - 0.5) * 2.2 * burst;
                burst *= 0.985;
                if (burst < 0.02) burst = 0;
            }
            if (!alarm && rnd.nextDouble() < 0.004) v += (rnd.nextDouble() - 0.5) * 0.9;
            System.arraycopy(buf, 1, buf, 0, N - 1);
            buf[N - 1] = Math.max(-1, Math.min(1, v));
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            int w = getWidth(), h = getHeight();
            RoundRectangle2D shape = new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, 14, 14);
            g.setColor(new Color(0x08141D));
            g.fill(shape);
            g.setClip(shape);

            g.setColor(new Color(139, 165, 184, 30));
            for (int i = 1; i < 12; i++) g.drawLine(i * w / 12, 0, i * w / 12, h);
            for (int i = 1; i < 6; i++) g.drawLine(0, i * h / 6, w, i * h / 6);
            g.setColor(new Color(139, 165, 184, 70));
            g.drawLine(0, h / 2, w, h / 2);

            Color c = alarm ? RED : BLUE;
            Path2D.Float path = new Path2D.Float();
            for (int i = 0; i < N; i++) {
                float x = i * (w - 4f) / (N - 1);
                float y = (float) (h / 2.0 - buf[i] * h * 0.44);
                if (i == 0) path.moveTo(x, y); else path.lineTo(x, y);
            }
            g.setPaint(new GradientPaint(0, 0, new Color(c.getRed(), c.getGreen(), c.getBlue(), 0), w * 0.3f, 0, c));
            g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
            g.draw(path);
            g.setComposite(AlphaComposite.SrcOver);
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(path);
            g.setColor(c);
            g.fillOval(w - 8, (int) (h / 2.0 - buf[N - 1] * h * 0.44) - 4, 8, 8);

            g.setFont(font(Font.BOLD, 12));
            g.setColor(MUTED);
            g.drawString("Magnitude", 18, 28);
            g.setFont(font(Font.BOLD, 44));
            g.setColor(TEXT);
            g.drawString(magText, 17, 70);
            g.setFont(font(Font.PLAIN, 11));
            g.setColor(new Color(0x587286));
            g.drawString("Live trace", w - 66, h - 10);
            g.setClip(null);
            g.setColor(LINE);
            g.draw(shape);
            g.dispose();
        }
    }

    static class AlertRow extends JPanel {
        final int weight;
        private final JLabel title;
        private final PillButton ack;
        private boolean done;

        AlertRow(String titleText, String desc, int weight, Color accent, Runnable onAck) {
            super(new BorderLayout(12, 0));
            this.weight = weight;
            setOpaque(false);
            setBorder(new EmptyBorder(12, 18, 12, 12));
            setAlignmentX(LEFT_ALIGNMENT);
            title = lbl(titleText, Font.BOLD, 14, TEXT);
            JPanel text = vbox();
            text.add(title);
            text.add(Box.createVerticalStrut(3));
            text.add(lbl(desc, Font.PLAIN, 12.5f, MUTED));
            ack = new PillButton("Acknowledge", GREEN, TEXT, true);
            ack.setFont(font(Font.BOLD, 12));
            ack.setBorder(new EmptyBorder(6, 12, 6, 12));
            ack.addActionListener(e -> { markDone(); onAck.run(); });
            add(text, BorderLayout.CENTER);
            JPanel right = transparent(new GridBagLayout());
            right.add(ack);
            add(right, BorderLayout.EAST);
        }

        boolean isDone() { return done; }

        void markDone() {
            done = true;
            title.setText(title.getText() + " (acknowledged)");
            title.setForeground(MUTED);
            ack.setVisible(false);
            repaint();
        }

        @Override public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            g.setColor(PANEL2);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g.setColor(done ? GREEN : RED);
            g.fillRoundRect(0, 0, 5, getHeight(), 5, 5);
            g.dispose();
        }
    }

    static class Meter extends JComponent {
        private final double value;
        private double shown;
        private final Color color;

        Meter(double value, Color color) {
            this.value = value;
            this.color = color;
            setPreferredSize(new Dimension(100, 8));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
            setAlignmentX(LEFT_ALIGNMENT);
        }

        void play(double targetVal) {
            long t0 = System.currentTimeMillis();
            Timer t = new Timer(16, null);
            t.addActionListener(e -> {
                double p = Math.min(1, (System.currentTimeMillis() - t0) / 900.0);
                shown = targetVal * (1 - Math.pow(1 - p, 3));
                repaint();
                if (p >= 1) t.stop();
            });
            t.start();
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            g.setColor(PANEL2);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g.setColor(color);
            g.fillRoundRect(0, 0, (int) (getWidth() * shown), getHeight(), 8, 8);
            g.dispose();
        }
    }

    static class ThinScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor = LINE;
            trackColor = BG;
        }
        @Override protected JButton createDecreaseButton(int o) { return zero(); }
        @Override protected JButton createIncreaseButton(int o) { return zero(); }
        private JButton zero() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }
        @Override protected void paintThumb(Graphics g0, JComponent c, Rectangle r) {
            Graphics2D g = (Graphics2D) g0.create();
            aa(g);
            g.setColor(thumbColor);
            g.fillRoundRect(r.x + 3, r.y + 2, r.width - 6, r.height - 4, 8, 8);
            g.dispose();
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(trackColor);
            g.fillRect(r.x, r.y, r.width, r.height);
        }
        @Override public Dimension getPreferredSize(JComponent c) { return new Dimension(12, 12); }
    }


    // =========================================================
    // DAOS, SERVICES & STATE FIELDS
    // =========================================================

    private final DisasterDAO disasterDAO;
    private final VictimDAO victimDAO;
    private final ShelterDAO shelterDAO;
    private final ResourceDAO resourceDAO;
    private final WeatherService weatherService;
    private final SystemMonitor systemMonitor;

    private StatTile tDis, tVic, tShe, tRes;
    private Seismograph scope;
    private Chip sevChip, alertChip;
    private Readout rLoc, rDepth, rStatus, rTsu, rAlert, rMag;
    private JLabel lastResp, stamp;
    private JPanel alertsBox, banner;
    private PillButton testBtn, refreshBtn;
    private Meter humidityMeter;
    private AlertRow baseAlert, testAlert;

    private JLabel dbStatusVal;
    private JLabel evacStatusVal;
    private JLabel resStatusVal;

    private JLabel weatherTempVal;
    private JLabel weatherCondVal;
    private JLabel weatherHumVal;
    private Readout weatherPrecipVal;
    private Readout weatherWindVal;

    private boolean testMode;
    private int activeDisasters = 0;
    private int victims = 0;
    private int shelters = 0;
    private int resources = 0;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DashboardPanel(SystemMonitor systemMonitor) {
        this.systemMonitor = systemMonitor;

        disasterDAO = new DisasterDAO();
        victimDAO = new VictimDAO();
        shelterDAO = new ShelterDAO();
        resourceDAO = new ResourceDAO();
        weatherService = new WeatherService();

        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildDashboard(), BorderLayout.CENTER);

        refreshDashboard();
    }


    // =========================================================
    // UI BUILDER METHODS
    // =========================================================

    private JComponent buildDashboard() {
        // OVERRIDE getPreferredSize so the layout forces GridBag to consume full vertical viewport height
        JPanel page = new JPanel(new GridBagLayout()) {
            @Override
            public Dimension getPreferredSize() {
                Dimension pref = super.getPreferredSize();
                Container parent = getParent();
                if (parent instanceof JViewport) {
                    pref.height = Math.max(pref.height, parent.getHeight());
                    pref.width = Math.max(pref.width, parent.getWidth());
                }
                return pref;
            }
        };
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(26, 32, 30, 32));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.weightx = 1;

        JPanel header = transparent(new BorderLayout(20, 0));
        JPanel titles = vbox();
        titles.add(lbl("Disaster Management Dashboard", Font.BOLD, 26, TEXT));
        titles.add(Box.createVerticalStrut(4));
        titles.add(lbl("Real-time overview of disaster response operations & system telemetry", Font.PLAIN, 14, MUTED));
        header.add(titles, BorderLayout.CENTER);

        JPanel buttons = transparent(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        testBtn = new PillButton("Test earthquake alert", RED, Color.WHITE, false);
        testBtn.addActionListener(e -> testEarthquakeAlert());

        refreshBtn = new PillButton("Refresh dashboard", BLUE, new Color(0x06223A), false);
        refreshBtn.addActionListener(e -> refreshDashboard());

        buttons.add(testBtn);
        buttons.add(refreshBtn);
        header.add(buttons, BorderLayout.EAST);
        page.add(header, c);

        banner = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                aa(g);
                g.setColor(new Color(255, 90, 95, 36));
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g.setColor(RED);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(12, 16, 12, 16));
        banner.add(lbl("<html><b style='color:#FF5A5F'>Simulation Active.</b> Critical earthquake testing protocol is running.</html>", Font.PLAIN, 14, TEXT));
        banner.setVisible(false);
        c.gridy = 1; c.insets = new Insets(18, 0, 0, 0);
        page.add(banner, c);

        Strip strip = new Strip();
        tDis = new StatTile("Active disasters", "Registered critical incidents", RED, true, () -> {});
        tVic = new StatTile("Registered victims", "People requesting emergency aid", AMBER, true, () -> {});
        tShe = new StatTile("Shelter centres", "Available refuge facilities", GREEN, true, () -> {});
        tRes = new StatTile("Emergency resources", "Stocked supplies and units", BLUE, false, () -> {});
        strip.add(tDis); strip.add(tVic); strip.add(tShe); strip.add(tRes);
        c.gridy = 2; c.insets = new Insets(22, 0, 0, 0);
        page.add(strip, c);

        // STRETCHING SECTION (weighty = 1.0)
        c.gridy = 3; c.gridwidth = 1; c.weightx = 0.62; c.weighty = 1.0; c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(22, 0, 22, 11);

        JPanel left = transparent(new BorderLayout(0, 22));
        left.add(buildTelemetry(), BorderLayout.CENTER);
        left.add(buildStatus(), BorderLayout.SOUTH);
        page.add(left, c);

        JPanel rightCol = transparent(new BorderLayout(0, 22));
        rightCol.add(buildAlerts(), BorderLayout.NORTH);
        rightCol.add(buildWeather(), BorderLayout.CENTER);
        c.gridx = 1; c.weightx = 0.38; c.insets = new Insets(22, 11, 22, 0);
        page.add(rightCol, c);

        JScrollPane sp = new JScrollPane(page, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.getViewport().setBackground(BG);
        sp.getVerticalScrollBar().setUI(new ThinScrollBarUI());
        sp.getVerticalScrollBar().setUnitIncrement(24);
        return sp;
    }

    private Card buildTelemetry() {
        Card card = new Card(new BorderLayout(0, 14));
        JPanel head = transparent(new BorderLayout());
        head.add(lbl("Latest earthquake telemetry", Font.BOLD, 17, TEXT), BorderLayout.WEST);
        sevChip = new Chip("Low", GREEN);
        JPanel chipWrap = transparent(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        chipWrap.add(sevChip);
        head.add(chipWrap, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        // Body uses BorderLayout so the Seismograph stretches vertically
        JPanel body = transparent(new BorderLayout(0, 14));
        scope = new Seismograph();
        scope.setAlignmentX(LEFT_ALIGNMENT);
        body.add(scope, BorderLayout.CENTER);

        rLoc = new Readout("Location", "Waiting for live data");
        rDepth = new Readout("Depth", "--");
        rStatus = new Readout("Status", "Waiting");
        rTsu = new Readout("Tsunami", "--");
        rAlert = new Readout("Alert level", "None");
        rMag = new Readout("Magnitude", "--");

        JPanel grid = transparent(new GridBagLayout());
        grid.setAlignmentX(LEFT_ALIGNMENT);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH;
        g.insets = new Insets(0, 0, 10, 10);
        g.gridy = 0; g.gridx = 0; g.weightx = 2; grid.add(rLoc, g);
        g.gridx = 1; g.weightx = 1; grid.add(rDepth, g);
        g.gridx = 2; g.weightx = 1; g.insets = new Insets(0, 0, 10, 0); grid.add(rStatus, g);
        g.gridy = 1; g.gridx = 0; g.weightx = 2; g.insets = new Insets(0, 0, 0, 10); grid.add(rTsu, g);
        g.gridx = 1; g.weightx = 1; grid.add(rAlert, g);
        g.gridx = 2; g.weightx = 1; g.insets = new Insets(0, 0, 0, 0); grid.add(rMag, g);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, grid.getPreferredSize().height));

        body.add(grid, BorderLayout.SOUTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private Card buildStatus() {
        Card card = new Card(new BorderLayout(0, 14));
        card.add(lbl("System status", Font.BOLD, 17, TEXT), BorderLayout.NORTH);
        JPanel row = transparent(new GridLayout(1, 4, 10, 0));

        dbStatusVal = lbl("Checking...", Font.BOLD, 14, TEXT);
        row.add(createDirectStatusCell("Database", dbStatusVal, MUTED));

        evacStatusVal = lbl("Checking...", Font.BOLD, 14, TEXT);
        row.add(createDirectStatusCell("Evacuation", evacStatusVal, MUTED));

        resStatusVal = lbl("Checking...", Font.BOLD, 14, TEXT);
        row.add(createDirectStatusCell("Resources", resStatusVal, MUTED));

        lastResp = lbl("None triggered", Font.BOLD, 14, TEXT);
        row.add(createDirectStatusCell("Last response", lastResp, MUTED));

        card.add(row, BorderLayout.CENTER);

        // Prevent status card from growing vertically
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        return card;
    }

    private JPanel createDirectStatusCell(String labelText, JLabel valueLabel, Color dotColor) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                aa(g);
                g.setColor(PANEL2);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g.setColor(new Color(dotColor.getRed(), dotColor.getGreen(), dotColor.getBlue(), dotColor == MUTED ? 0 : 55));
                g.fillOval(14, getHeight() / 2 - 8, 16, 16);
                g.setColor(dotColor);
                g.fillOval(18, getHeight() / 2 - 4, 8, 8);
                g.dispose();
            }
        };
        p.setOpaque(false);
        JPanel text = vbox();
        text.setBorder(new EmptyBorder(11, 42, 11, 8));
        text.add(lbl(labelText, Font.BOLD, 12, MUTED));
        text.add(valueLabel);
        p.add(text);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(p, BorderLayout.CENTER);
        return wrapper;
    }

    private Card buildAlerts() {
        Card card = new Card(new BorderLayout(0, 14));
        JPanel head = transparent(new BorderLayout());
        head.add(lbl("Live emergency alerts", Font.BOLD, 17, TEXT), BorderLayout.WEST);
        alertChip = new Chip("0 alerts", GREEN);
        JPanel chipWrap = transparent(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        chipWrap.add(alertChip);
        head.add(chipWrap, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        alertsBox = vbox();
        card.add(alertsBox, BorderLayout.CENTER);
        return card;
    }

    private Card buildWeather() {
        Card card = new Card(new BorderLayout(0, 12));
        JPanel head = transparent(new BorderLayout());
        head.add(lbl("Live weather", Font.BOLD, 17, TEXT), BorderLayout.WEST);
        JPanel chipWrap = transparent(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        chipWrap.add(new Chip("Chennai", BLUE));
        head.add(chipWrap, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        JPanel body = vbox();
        JPanel top = transparent(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.setAlignmentX(LEFT_ALIGNMENT);

        weatherTempVal = lbl("-- \u00B0C", Font.BOLD, 40, TEXT);
        top.add(weatherTempVal);

        JPanel desc = vbox();
        desc.setBorder(new EmptyBorder(0, 16, 0, 0));
        weatherCondVal = lbl("Loading weather...", Font.BOLD, 15, TEXT);
        desc.add(weatherCondVal);
        desc.add(lbl("Live feed", Font.PLAIN, 13, MUTED));
        top.add(desc);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, top.getPreferredSize().height));
        body.add(top);
        body.add(Box.createVerticalStrut(16));

        JPanel humRow = transparent(new BorderLayout());
        humRow.setAlignmentX(LEFT_ALIGNMENT);
        humRow.add(lbl("Humidity", Font.BOLD, 13, MUTED), BorderLayout.WEST);

        weatherHumVal = lbl("--%", Font.BOLD, 13, TEXT);
        humRow.add(weatherHumVal, BorderLayout.EAST);

        humRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, humRow.getPreferredSize().height));
        body.add(humRow);
        body.add(Box.createVerticalStrut(6));

        humidityMeter = new Meter(0.0, BLUE);
        body.add(humidityMeter);
        body.add(Box.createVerticalStrut(16));

        JPanel two = transparent(new GridLayout(1, 2, 10, 0));
        two.setAlignmentX(LEFT_ALIGNMENT);

        weatherPrecipVal = new Readout("Precipitation", "-- mm");
        weatherWindVal = new Readout("Wind", "-- km/h");
        two.add(weatherPrecipVal);
        two.add(weatherWindVal);

        two.setMaximumSize(new Dimension(Integer.MAX_VALUE, two.getPreferredSize().height));
        body.add(two);
        body.add(Box.createVerticalStrut(14));

        stamp = lbl("Updated --", Font.PLAIN, 12, MUTED);
        stamp.setAlignmentX(LEFT_ALIGNMENT);
        body.add(stamp);

        card.add(body, BorderLayout.NORTH); // Pins weather top inside its stretched card
        return card;
    }


    // =========================================================
    // DATABASE & SERVICE REFRESH LOGIC (DAO HOOKS)
    // =========================================================

    private void refreshDashboard() {
        refreshBtn.setText("Refreshing\u2026");
        refreshBtn.setEnabled(false);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    activeDisasters = disasterDAO.getAllDisasters().size();
                    victims = victimDAO.getAllVictims().size();
                    shelters = shelterDAO.getAllShelters().size();
                    resources = resourceDAO.getAllResources().size();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                refreshBtn.setText("Refresh dashboard");
                refreshBtn.setEnabled(true);

                tDis.setValue(testMode ? activeDisasters + 1 : activeDisasters, true);
                tVic.setValue(victims, true);
                tShe.setValue(shelters, true);
                tRes.setValue(resources, true);

                checkDatabaseConnection();
                checkEvacuationServiceStatus();
                checkResourceMonitoringStatus();
                refreshEmergencyAlertsList();
                refreshWeatherAsync();

                stamp.setText("Updated " + now());
                revalidate();
                repaint();
            }
        }.execute();
    }

    private void checkDatabaseConnection() {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                dbStatusVal.setText("Online");
                dbStatusVal.setForeground(TEXT);
            } else {
                dbStatusVal.setText("Offline");
                dbStatusVal.setForeground(RED);
            }
        } catch (Exception e) {
            dbStatusVal.setText("Offline");
            dbStatusVal.setForeground(RED);
        }
    }

    private void checkEvacuationServiceStatus() {
        try {
            new EvacuationServiceImpl();
            evacStatusVal.setText("Active");
            evacStatusVal.setForeground(TEXT);
        } catch (Exception e) {
            evacStatusVal.setText("Error");
            evacStatusVal.setForeground(RED);
        }
    }

    private void checkResourceMonitoringStatus() {
        try {
            resourceDAO.getAllResources();
            resStatusVal.setText("Active");
            resStatusVal.setForeground(TEXT);
        } catch (Exception e) {
            resStatusVal.setText("Error");
            resStatusVal.setForeground(RED);
        }
    }

    private void refreshEmergencyAlertsList() {
        alertsBox.removeAll();
        try {
            int total = disasterDAO.getAllDisasters().size();
            if (total > 0) {
                baseAlert = new AlertRow("Disaster activity registered", total + " record(s) currently active in database.", total, RED, this::updateAlertChipCount);
                alertsBox.add(baseAlert);
                alertChip.set(total + (total == 1 ? " alert" : " alerts"), RED);
            } else {
                JLabel noAlert = lbl("✓ No active emergency alerts", Font.PLAIN, 13, GREEN);
                noAlert.setBorder(new EmptyBorder(8, 4, 8, 4));
                alertsBox.add(noAlert);
                alertChip.set("0 alerts", GREEN);
            }
        } catch (Exception e) {
            JLabel errAlert = lbl("Unable to load alerts", Font.PLAIN, 13, RED);
            alertsBox.add(errAlert);
            alertChip.set("Error", RED);
        }
        alertsBox.revalidate();
        alertsBox.repaint();
    }

    private void updateAlertChipCount() {
        int open = (baseAlert != null && !baseAlert.isDone()) ? baseAlert.weight : 0;
        if (testAlert != null && !testAlert.isDone()) open += testAlert.weight;
        alertChip.set(open + (open == 1 ? " alert" : " alerts"), open == 0 ? GREEN : RED);
    }

    private void refreshWeatherAsync() {
        new SwingWorker<WeatherData, Void>() {
            @Override
            protected WeatherData doInBackground() throws Exception {
                return weatherService.getCurrentWeather("Chennai", 13.0827, 80.2707);
            }

            @Override
            protected void done() {
                try {
                    WeatherData weather = get();
                    if (weather != null) {
                        weatherTempVal.setText(String.format("%.1f \u00B0C", weather.getTemperature()));
                        weatherHumVal.setText(String.format("%.0f%%", weather.getHumidity()));
                        weatherPrecipVal.set(String.format("%.1f mm", weather.getPrecipitation()), TEXT);
                        weatherWindVal.set(String.format("%.1f km/h", weather.getWindSpeed()), TEXT);
                        weatherCondVal.setText(getWeatherDescription(weather.getWeatherCode()));
                        humidityMeter.play(weather.getHumidity() / 100.0);
                        stamp.setText("Updated " + weather.getTime());
                    }
                } catch (Exception e) {
                    weatherCondVal.setText("Weather unavailable");
                    weatherCondVal.setForeground(RED);
                }
                revalidate();
                repaint();
            }
        }.execute();
    }

    private String getWeatherDescription(int code) {
        if (code == 0) return "Clear sky";
        if (code <= 3) return "Partly cloudy";
        if (code <= 48) return "Foggy";
        if (code <= 57) return "Drizzle";
        if (code <= 67) return "Rain";
        if (code == 95) return "Thunderstorm";
        return "Normal conditions";
    }

    // =========================================================
    // EARTHQUAKE TEST WORKFLOW & PUBLIC HOOKS
    // =========================================================

    private void testEarthquakeAlert() {

        testMode = !testMode;

        if (testMode) {

            testBtn.setText("End test");
            testBtn.setOutline(true, RED);

            banner.setVisible(true);
            scope.setAlarm(true);
            scope.setMagnitude("6.5");

            rMag.set("6.5", TEXT);
            rLoc.set("33 km SSW of Honcho, Japan", TEXT);
            rDepth.set("42.9 km", TEXT);
            rStatus.set("Test", AMBER);
            rTsu.set("Yes", AMBER);
            rAlert.set("Red", RED);
            sevChip.set("Critical", RED);

            testAlert = new AlertRow(
                    "Test: Critical earthquake detected",
                    "Magnitude 6.5 simulation in progress.",
                    1,
                    AMBER,
                    this::updateAlertChipCount
            );

            alertsBox.add(testAlert, 0);
            alertsBox.add(
                    Box.createVerticalStrut(10),
                    1
            );

            lastResp.setText(
                    "Test triggered at "
                            + LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("HH:mm")
                    )
            );

            lastResp.setForeground(RED);

            /*
             * IMPORTANT:
             * Victims are NOT generated here.
             *
             * NotificationServiceThread handles:
             *
             * 1. Generate 5 victims
             * 2. Calculate priority
             * 3. Select highest priority victim
             * 4. Create evacuation
             * 5. Send earthquake notification
             */

            if (systemMonitor != null) {

                systemMonitor.triggerTestEarthquakeAlert();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "System Monitor is not available.",
                        "System Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Earthquake simulation triggered.\n\n"
                            + "5 simulated victims will be generated.\n"
                            + "Highest-priority victim will be selected.\n"
                            + "Evacuation will be created automatically.",
                    "Earthquake Simulation",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } else {

            testBtn.setText("Test earthquake alert");
            testBtn.setOutline(false, Color.WHITE);

            banner.setVisible(false);

            scope.setAlarm(false);
            scope.setMagnitude("3.0");

            rMag.set("3.0", TEXT);
            rLoc.set("Waiting for live data", MUTED);
            rDepth.set("--", MUTED);
            rStatus.set("Waiting", MUTED);
            rTsu.set("--", MUTED);
            rAlert.set("None", TEXT);
            sevChip.set("Low", GREEN);

            if (testAlert != null) {

                refreshEmergencyAlertsList();

                testAlert = null;
            }

            lastResp.setText("None triggered");
            lastResp.setForeground(TEXT);

            JOptionPane.showMessageDialog(
                    this,
                    "Simulation ended.\n"
                            + "Telemetry returning to live stream.",
                    "Simulation Stopped",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

        tDis.setValue(
                testMode
                        ? activeDisasters + 1
                        : activeDisasters,
                true
        );

        updateAlertChipCount();

        alertsBox.revalidate();
        alertsBox.repaint();

        revalidate();
        repaint();
    }

    public void updateLatestEarthquake(EarthquakeData earthquake) {
        if (earthquake == null) return;
        SwingUtilities.invokeLater(() -> {
            String magStr = String.format("%.1f", earthquake.getMagnitude());
            scope.setMagnitude(magStr);
            rMag.set(magStr, TEXT);
            rLoc.set(earthquake.getPlace() != null ? earthquake.getPlace() : "Unknown location", TEXT);
            rDepth.set(String.format("%.1f km", earthquake.getDepth()), TEXT);
            rTsu.set(earthquake.isTsunami() ? "Yes" : "No", earthquake.isTsunami() ? AMBER : GREEN);
            rAlert.set(earthquake.getAlert() != null ? earthquake.getAlert().toUpperCase() : "NONE", TEXT);
            rStatus.set("Live", GREEN);
            sevChip.set(earthquake.getMagnitude() >= 6.0 ? "Critical" : "Moderate", RED);
            revalidate();
            repaint();
        });
    }

    public void updateEvacuationStatus(Evacuation evacuation) {
        if (evacuation == null) return;
        String message = "Victim ID: " + evacuation.getVictimId() + " | Shelter: " + evacuation.getShelterId() + " | Status: " + evacuation.getStatus();
        lastResp.setText(message);
        lastResp.setForeground(GREEN);
        revalidate();
        repaint();
    }
}