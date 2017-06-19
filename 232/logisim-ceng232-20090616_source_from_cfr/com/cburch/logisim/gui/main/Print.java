/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.gui.main.CircuitJList;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.main.Strings;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.StringUtil;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;

public class Print {
    private Print() {
    }

    public static void doPrint(Project proj) {
        CircuitJList list = new CircuitJList(proj, true);
        Frame frame = proj.getFrame();
        if (list.getModel().getSize() == 0) {
            JOptionPane.showMessageDialog(proj.getFrame(), Strings.get("printEmptyCircuitsMessage"), Strings.get("printEmptyCircuitsTitle"), 0);
            return;
        }
        ParmsPanel parmsPanel = new ParmsPanel(list);
        int action = JOptionPane.showConfirmDialog(frame, parmsPanel, Strings.get("printParmsTitle"), 2, 3);
        if (action != 0) {
            return;
        }
        List circuits = list.getSelectedCircuits();
        if (circuits.isEmpty()) {
            return;
        }
        PageFormat format = new PageFormat();
        MyPrintable print = new MyPrintable(proj, circuits, parmsPanel.getHeader(), parmsPanel.getRotateToFit(), parmsPanel.getPrinterView());
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(print, format);
        if (!job.printDialog()) {
            return;
        }
        try {
            job.print();
        }
        catch (PrinterException e) {
            JOptionPane.showMessageDialog(proj.getFrame(), StringUtil.format(Strings.get("printError"), e.toString()), Strings.get("printErrorTitle"), 0);
        }
    }

    private static String format(String header, int index, int max, String circName) {
        int mark = header.indexOf(37);
        if (mark < 0) {
            return header;
        }
        StringBuffer ret = new StringBuffer();
        int start = 0;
        while (mark >= 0 && mark + 1 < header.length()) {
            ret.append(header.substring(start, mark));
            switch (header.charAt(mark + 1)) {
                case 'n': {
                    ret.append(circName);
                    break;
                }
                case 'p': {
                    ret.append("" + index);
                    break;
                }
                case 'P': {
                    ret.append("" + max);
                    break;
                }
                case '%': {
                    ret.append("%");
                    break;
                }
                default: {
                    ret.append("%" + header.charAt(mark + 1));
                }
            }
            start = mark + 2;
            mark = header.indexOf(37, start);
        }
        if (start < header.length()) {
            ret.append(header.substring(start));
        }
        return ret.toString();
    }

    private static class MyPrintable
    implements Printable {
        Project proj;
        List circuits;
        String header;
        boolean rotateToFit;
        boolean printerView;

        MyPrintable(Project proj, List circuits, String header, boolean rotateToFit, boolean printerView) {
            this.proj = proj;
            this.circuits = circuits;
            this.header = header;
            this.rotateToFit = rotateToFit;
            this.printerView = printerView;
        }

        @Override
        public int print(Graphics base, PageFormat format, int pageIndex) {
            if (pageIndex >= this.circuits.size()) {
                return 1;
            }
            Circuit circ = (Circuit)this.circuits.get(pageIndex);
            CircuitState circState = this.proj.getCircuitState(circ);
            Graphics g = base.create();
            Graphics2D g2 = g instanceof Graphics2D ? (Graphics2D)g : null;
            FontMetrics fm = g.getFontMetrics();
            String head = this.header != null && !this.header.equals("") ? Print.format(this.header, pageIndex + 1, this.circuits.size(), circ.getName()) : null;
            int headHeight = head == null ? 0 : fm.getHeight();
            double imWidth = format.getImageableWidth();
            double imHeight = format.getImageableHeight();
            Bounds bds = circ.getBounds(g).expand(4);
            double scale = Math.min(imWidth / (double)bds.getWidth(), (imHeight - (double)headHeight) / (double)bds.getHeight());
            if (g2 != null) {
                double scale2;
                g2.translate(format.getImageableX(), format.getImageableY());
                if (this.rotateToFit && scale < 0.9090909090909091 && (scale2 = Math.min(imHeight / (double)bds.getWidth(), (imWidth - (double)headHeight) / (double)bds.getHeight())) >= scale * 1.1) {
                    scale = scale2;
                    if (imHeight > imWidth) {
                        g2.translate(0.0, imHeight);
                        g2.rotate(-1.5707963267948966);
                    } else {
                        g2.translate(imWidth, 0.0);
                        g2.rotate(1.5707963267948966);
                    }
                    double t = imHeight;
                    imHeight = imWidth;
                    imWidth = t;
                }
            }
            if (head != null) {
                g.drawString(head, (int)Math.round((imWidth - (double)fm.stringWidth(head)) / 2.0), fm.getAscent());
                if (g2 != null) {
                    imHeight -= (double)headHeight;
                    g2.translate(0, headHeight);
                }
            }
            if (g2 != null) {
                if (scale < 1.0) {
                    g2.scale(scale, scale);
                    imWidth /= scale;
                    imHeight /= scale;
                }
                double dx = Math.max(0.0, (imWidth - (double)bds.getWidth()) / 2.0);
                g2.translate((double)(- bds.getX()) + dx, (double)(- bds.getY()));
            }
            Rectangle clip = g.getClipBounds();
            clip.add(bds.getX(), bds.getY());
            clip.add(bds.getX() + bds.getWidth(), bds.getY() + bds.getHeight());
            g.setClip(clip);
            ComponentDrawContext context = new ComponentDrawContext(this.proj.getFrame().getCanvas(), circ, circState, base, g, this.printerView);
            circ.draw(context, Collections.EMPTY_SET);
            g.dispose();
            return 0;
        }
    }

    private static class ParmsPanel
    extends JPanel {
        JCheckBox rotateToFit = new JCheckBox();
        JCheckBox printerView;
        JTextField header;
        GridBagLayout gridbag;
        GridBagConstraints gbc;

        ParmsPanel(JList list) {
            this.rotateToFit.setSelected(true);
            this.printerView = new JCheckBox();
            this.printerView.setSelected(true);
            this.header = new JTextField(20);
            this.header.setText("%n (%p of %P)");
            this.gridbag = new GridBagLayout();
            this.gbc = new GridBagConstraints();
            this.setLayout(this.gridbag);
            this.gbc.gridy = 0;
            this.gbc.gridx = -1;
            this.gbc.anchor = 18;
            this.gbc.insets = new Insets(5, 0, 5, 0);
            this.gbc.fill = 0;
            this.addGb(new JLabel(Strings.get("labelCircuits") + " "));
            this.gbc.fill = 2;
            this.addGb(new JScrollPane(list));
            this.gbc.fill = 0;
            ++this.gbc.gridy;
            this.addGb(new JLabel(Strings.get("labelHeader") + " "));
            this.addGb(this.header);
            ++this.gbc.gridy;
            this.addGb(new JLabel(Strings.get("labelRotateToFit") + " "));
            this.addGb(this.rotateToFit);
            ++this.gbc.gridy;
            this.addGb(new JLabel(Strings.get("labelPrinterView") + " "));
            this.addGb(this.printerView);
        }

        private void addGb(JComponent comp) {
            this.gridbag.setConstraints(comp, this.gbc);
            this.add(comp);
        }

        boolean getRotateToFit() {
            return this.rotateToFit.isSelected();
        }

        boolean getPrinterView() {
            return this.printerView.isSelected();
        }

        String getHeader() {
            return this.header.getText();
        }
    }

}

