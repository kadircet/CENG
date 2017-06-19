/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.start;

import com.cburch.logisim.data.Value;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class About {
    private static final int IMAGE_BORDER = 10;
    private static final int IMAGE_WIDTH = 380;
    private static final int IMAGE_HEIGHT = 284;

    private About() {
    }

    public static JPanel getImagePanel() {
        return new MyPanel();
    }

    public static void showAboutDialog(JFrame owner) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(About.getImagePanel());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JOptionPane.showMessageDialog(owner, panel, "Logisim 2.1.8", -1);
    }

    private static class MyPanel
    extends JPanel
    implements AncestorListener {
        private final String LOGO_LOC = "com/cburch/logisim/resources/hendrix.png";
        private final Color fadeColor = new Color(255, 255, 255, 128);
        private final Color headerColor = new Color(143, 0, 0);
        private final Color authorColor = new Color(0, 0, 176);
        private final Color gateColor = Color.DARK_GRAY;
        private final Font headerFont = new Font("Monospaced", 1, 72);
        private final Font versionFont = new Font("Serif", 2, 32);
        private final Font copyrightFont = new Font("Serif", 2, 18);
        private final Font authorFont = new Font("Serif", 0, 24);
        private final Font urlFont = new Font("Serif", 2, 24);
        private Image logo = null;
        private Value upper = Value.FALSE;
        private Value lower = Value.TRUE;
        private PanelThread thread = null;

        public MyPanel() {
            this.setPreferredSize(new Dimension(400, 304));
            this.setBackground(Color.WHITE);
            this.addAncestorListener(this);
            URL url = About.class.getClassLoader().getResource("com/cburch/logisim/resources/hendrix.png");
            if (url != null) {
                this.logo = this.getToolkit().createImage(url);
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            try {
                int x = 10;
                int y = 10;
                this.drawCircuit(g, x + 10, y + 55);
                g.setColor(this.fadeColor);
                g.fillRect(x, y, 380, 284);
                this.drawText(g, x, y);
                if (this.logo != null) {
                    g.drawImage(this.logo, x + 330, y + 185, this);
                }
            }
            catch (Throwable t) {
                // empty catch block
            }
        }

        private void drawCircuit(Graphics g, int x0, int y0) {
            if (g instanceof Graphics2D) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setStroke(new BasicStroke(5.0f));
            }
            this.drawWires(g, x0, y0);
            g.setColor(this.gateColor);
            this.drawNot(g, x0, y0, 70, 10);
            this.drawNot(g, x0, y0, 70, 110);
            this.drawAnd(g, x0, y0, 130, 30);
            this.drawAnd(g, x0, y0, 130, 90);
            this.drawOr(g, x0, y0, 220, 60);
        }

        private void drawWires(Graphics g, int x0, int y0) {
            Value upperNot = this.upper.not();
            Value lowerNot = this.lower.not();
            Value upperAnd = upperNot.and(this.lower);
            Value lowerAnd = lowerNot.and(this.upper);
            Value out = upperAnd.or(lowerAnd);
            g.setColor(this.upper.getColor());
            int x = MyPanel.toX(x0, 20);
            int y = MyPanel.toY(y0, 10);
            g.fillOval(x - 7, y - 7, 14, 14);
            g.drawLine(MyPanel.toX(x0, 0), y, MyPanel.toX(x0, 40), y);
            g.drawLine(x, y, x, MyPanel.toY(y0, 70));
            y = MyPanel.toY(y0, 70);
            g.drawLine(x, y, MyPanel.toX(x0, 80), y);
            g.setColor(upperNot.getColor());
            y = MyPanel.toY(y0, 10);
            g.drawLine(MyPanel.toX(x0, 70), y, MyPanel.toX(x0, 80), y);
            g.setColor(this.lower.getColor());
            x = MyPanel.toX(x0, 30);
            y = MyPanel.toY(y0, 110);
            g.fillOval(x - 7, y - 7, 14, 14);
            g.drawLine(MyPanel.toX(x0, 0), y, MyPanel.toX(x0, 40), y);
            g.drawLine(x, y, x, MyPanel.toY(y0, 50));
            y = MyPanel.toY(y0, 50);
            g.drawLine(x, y, MyPanel.toX(x0, 80), y);
            g.setColor(lowerNot.getColor());
            y = MyPanel.toY(y0, 110);
            g.drawLine(MyPanel.toX(x0, 70), y, MyPanel.toX(x0, 80), y);
            g.setColor(upperAnd.getColor());
            x = MyPanel.toX(x0, 150);
            y = MyPanel.toY(y0, 30);
            g.drawLine(MyPanel.toX(x0, 130), y, x, y);
            g.drawLine(x, y, x, MyPanel.toY(y0, 45));
            y = MyPanel.toY(y0, 45);
            g.drawLine(x, y, MyPanel.toX(x0, 174), y);
            g.setColor(lowerAnd.getColor());
            y = MyPanel.toY(y0, 90);
            g.drawLine(MyPanel.toX(x0, 130), y, x, y);
            g.drawLine(x, y, x, MyPanel.toY(y0, 75));
            y = MyPanel.toY(y0, 75);
            g.drawLine(x, y, MyPanel.toX(x0, 174), y);
            g.setColor(out.getColor());
            y = MyPanel.toY(y0, 60);
            g.drawLine(MyPanel.toX(x0, 220), y, MyPanel.toX(x0, 240), y);
        }

        private void drawNot(Graphics g, int x0, int y0, int x, int y) {
            int[] xp = new int[4];
            int[] yp = new int[4];
            xp[0] = MyPanel.toX(x0, x - 10);
            yp[0] = MyPanel.toY(y0, y);
            xp[1] = MyPanel.toX(x0, x - 29);
            yp[1] = MyPanel.toY(y0, y - 7);
            xp[2] = xp[1];
            yp[2] = MyPanel.toY(y0, y + 7);
            xp[3] = xp[0];
            yp[3] = yp[0];
            g.drawPolyline(xp, yp, 4);
            int diam = MyPanel.toDim(10);
            g.drawOval(xp[0], yp[0] - diam / 2, diam, diam);
        }

        private void drawAnd(Graphics g, int x0, int y0, int x, int y) {
            int[] xp = new int[4];
            int[] yp = new int[4];
            xp[0] = MyPanel.toX(x0, x - 25);
            yp[0] = MyPanel.toY(y0, y - 25);
            xp[1] = MyPanel.toX(x0, x - 50);
            yp[1] = yp[0];
            xp[2] = xp[1];
            yp[2] = MyPanel.toY(y0, y + 25);
            xp[3] = xp[0];
            yp[3] = yp[2];
            int diam = MyPanel.toDim(50);
            g.drawArc(xp[1], yp[1], diam, diam, -90, 180);
            g.drawPolyline(xp, yp, 4);
        }

        private void drawOr(Graphics g, int x0, int y0, int x, int y) {
            int cx = MyPanel.toX(x0, x - 50);
            int cd = MyPanel.toDim(62);
            GraphicsUtil.drawCenteredArc(g, cx, MyPanel.toY(y0, y - 37), cd, -90, 53);
            GraphicsUtil.drawCenteredArc(g, cx, MyPanel.toY(y0, y + 37), cd, 90, -53);
            GraphicsUtil.drawCenteredArc(g, MyPanel.toX(x0, x - 93), MyPanel.toY(y0, y), MyPanel.toDim(50), -30, 60);
        }

        private static int toX(int x0, int offs) {
            return x0 + offs * 3 / 2;
        }

        private static int toY(int y0, int offs) {
            return y0 + offs * 3 / 2;
        }

        private static int toDim(int offs) {
            return offs * 3 / 2;
        }

        private void drawText(Graphics g, int x, int y) {
            g.setColor(this.headerColor);
            g.setFont(this.headerFont);
            g.drawString("Logisim", x, y + 45);
            g.setFont(this.copyrightFont);
            FontMetrics fm = g.getFontMetrics();
            String str = "\u00a9 2009";
            g.drawString(str, x + 380 - fm.stringWidth(str), y + 16);
            g.setFont(this.versionFont);
            fm = g.getFontMetrics();
            str = "Version 2.1.8";
            g.drawString(str, x + 380 - fm.stringWidth(str), y + 75);
            g.setColor(this.authorColor);
            g.setFont(this.authorFont);
            fm = g.getFontMetrics();
            str = "Carl Burch";
            g.drawString(str, x + (380 - fm.stringWidth(str)) / 2, y + 224);
            str = "Hendrix College";
            g.drawString(str, x + (380 - fm.stringWidth(str)) / 2, y + 251);
            g.setFont(this.urlFont);
            fm = g.getFontMetrics();
            str = "www.cburch.com/logisim/";
            g.drawString(str, x + (380 - fm.stringWidth(str)) / 2, y + 277);
        }

        @Override
        public void ancestorAdded(AncestorEvent arg0) {
            if (this.thread == null) {
                this.thread = new PanelThread(this);
                this.thread.start();
            }
        }

        @Override
        public void ancestorRemoved(AncestorEvent arg0) {
            if (this.thread != null) {
                this.thread.running = false;
            }
        }

        @Override
        public void ancestorMoved(AncestorEvent arg0) {
        }
    }

    private static class PanelThread
    extends Thread {
        private MyPanel panel;
        private int count = 1;
        private boolean running = true;

        PanelThread(MyPanel panel) {
            this.panel = panel;
        }

        @Override
        public void run() {
            while (this.running) {
                this.panel.upper = this.count == 2 || this.count == 3 ? Value.TRUE : Value.FALSE;
                this.panel.lower = this.count == 1 || this.count == 2 ? Value.TRUE : Value.FALSE;
                this.count = (this.count + 1) % 4;
                this.panel.repaint();
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException ex) {}
            }
        }
    }

}

