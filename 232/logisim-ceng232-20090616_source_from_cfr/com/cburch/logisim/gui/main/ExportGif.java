/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.gui.main.CircuitJList;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.main.Strings;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.GifEncoder;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListModel;
import javax.swing.ProgressMonitor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

class ExportGif {
    private static final int SLIDER_DIVISIONS = 6;
    private static final String EXTENSION = ".gif";
    private static final int BORDER_SIZE = 5;

    private ExportGif() {
    }

    static void doExport(Project proj) {
        Frame frame = proj.getFrame();
        CircuitJList list = new CircuitJList(proj, true);
        if (list.getModel().getSize() == 0) {
            JOptionPane.showMessageDialog(proj.getFrame(), Strings.get("exportEmptyCircuitsMessage"), Strings.get("exportEmptyCircuitsTitle"), 0);
            return;
        }
        GifPanel gifPanel = new GifPanel(list);
        int action = JOptionPane.showConfirmDialog(frame, gifPanel, Strings.get("exportGifSelect"), 2, 3);
        if (action != 0) {
            return;
        }
        List circuits = list.getSelectedCircuits();
        double scale = gifPanel.getScale();
        boolean printerView = gifPanel.getPrinterView();
        if (circuits.isEmpty()) {
            return;
        }
        Loader loader = proj.getLogisimFile().getLoader();
        JFileChooser chooser = loader.createChooser();
        if (circuits.size() > 1) {
            chooser.setFileSelectionMode(1);
            chooser.setDialogTitle(Strings.get("exportGifDirectorySelect"));
        } else {
            chooser.setFileFilter(new GifFilter());
            chooser.setDialogTitle(Strings.get("exportGifFileSelect"));
        }
        int returnVal = chooser.showDialog(frame, Strings.get("exportGifButton"));
        if (returnVal != 0) {
            return;
        }
        File dest = chooser.getSelectedFile();
        chooser.setCurrentDirectory(dest.isDirectory() ? dest : dest.getParentFile());
        if (dest.exists()) {
            int confirm;
            if (!dest.isDirectory() && (confirm = JOptionPane.showConfirmDialog(proj.getFrame(), Strings.get("confirmOverwriteMessage"), Strings.get("confirmOverwriteTitle"), 0)) != 0) {
                return;
            }
        } else if (circuits.size() > 1) {
            boolean created = dest.mkdir();
            if (!created) {
                JOptionPane.showMessageDialog(proj.getFrame(), Strings.get("exportNewDirectoryErrorMessage"), Strings.get("exportNewDirectoryErrorTitle"), 0);
                return;
            }
        } else {
            String name = dest.getName();
            if (name.indexOf(46) < 0) {
                dest = new File(dest.getParentFile(), name + ".gif");
            }
        }
        ProgressMonitor monitor = new ProgressMonitor(frame, Strings.get("exportGifProgress"), null, 0, 10000);
        monitor.setMillisToDecideToPopup(100);
        monitor.setMillisToPopup(200);
        monitor.setProgress(0);
        new ExportThread(frame, frame.getCanvas(), dest, circuits, scale, printerView, monitor).start();
    }

    private static class ExportThread
    extends Thread {
        Frame frame;
        Canvas canvas;
        File dest;
        List circuits;
        double scale;
        boolean printerView;
        ProgressMonitor monitor;

        ExportThread(Frame frame, Canvas canvas, File dest, List circuits, double scale, boolean printerView, ProgressMonitor monitor) {
            this.frame = frame;
            this.canvas = canvas;
            this.dest = dest;
            this.circuits = circuits;
            this.scale = scale;
            this.printerView = printerView;
            this.monitor = monitor;
        }

        @Override
        public void run() {
            Iterator it = this.circuits.iterator();
            while (it.hasNext()) {
                this.export((Circuit)it.next());
            }
        }

        private void export(Circuit circuit) {
            int height;
            Bounds bds = circuit.getBounds(this.canvas.getGraphics()).expand(5);
            int width = (int)Math.round((double)bds.getWidth() * this.scale);
            Image img = this.canvas.createImage(width, height = (int)Math.round((double)bds.getHeight() * this.scale));
            if (img == null) {
                JOptionPane.showMessageDialog(this.frame, Strings.get("couldNotCreateGifImage"));
                this.monitor.close();
                return;
            }
            Graphics base = img.getGraphics();
            Graphics g = base.create();
            if (g instanceof Graphics2D) {
                ((Graphics2D)g).scale(this.scale, this.scale);
                ((Graphics2D)g).translate(- bds.getX(), - bds.getY());
            } else {
                img = this.canvas.createImage((bds = bds.expand(-5)).getX() + bds.getWidth() + 5, bds.getY() + bds.getHeight() + 5);
                if (img == null) {
                    JOptionPane.showMessageDialog(this.frame, Strings.get("couldNotCreateGifImage"));
                    this.monitor.close();
                    return;
                }
            }
            CircuitState circuitState = this.canvas.getProject().getCircuitState(circuit);
            ComponentDrawContext context = new ComponentDrawContext(this.canvas, circuit, circuitState, base, g, this.printerView);
            circuit.draw(context, null);
            File where = this.dest.isDirectory() ? new File(this.dest, circuit.getName() + ".gif") : this.dest;
            try {
                GifEncoder.toFile(img, where, this.monitor);
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this.frame, Strings.get("couldNotCreateGifFile"));
                this.monitor.close();
                return;
            }
            g.dispose();
            this.monitor.close();
        }
    }

    private static class GifFilter
    extends FileFilter {
        private GifFilter() {
        }

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(".gif");
        }

        @Override
        public String getDescription() {
            return Strings.get("exportGifFilter");
        }
    }

    private static class GifPanel
    extends JPanel
    implements ChangeListener {
        JSlider slider = new JSlider(0, -18, 18, 0);
        JLabel curScale;
        JCheckBox printerView;
        GridBagLayout gridbag;
        GridBagConstraints gbc;
        Dimension curScaleDim;

        GifPanel(JList list) {
            this.slider.setMajorTickSpacing(10);
            this.slider.addChangeListener(this);
            this.curScale = new JLabel("222%");
            this.curScale.setHorizontalAlignment(4);
            this.curScale.setVerticalAlignment(0);
            this.curScaleDim = new Dimension(this.curScale.getPreferredSize());
            this.curScaleDim.height = Math.max(this.curScaleDim.height, this.slider.getPreferredSize().height);
            this.stateChanged(null);
            this.printerView = new JCheckBox();
            this.printerView.setSelected(true);
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
            this.addGb(new JLabel(Strings.get("labelScale") + " "));
            this.addGb(this.slider);
            this.addGb(this.curScale);
            ++this.gbc.gridy;
            this.addGb(new JLabel(Strings.get("labelPrinterView") + " "));
            this.addGb(this.printerView);
        }

        private void addGb(JComponent comp) {
            this.gridbag.setConstraints(comp, this.gbc);
            this.add(comp);
        }

        double getScale() {
            return Math.pow(2.0, (double)this.slider.getValue() / 6.0);
        }

        boolean getPrinterView() {
            return this.printerView.isSelected();
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            double scale = this.getScale();
            this.curScale.setText("" + (int)Math.round(100.0 * scale) + "%");
            if (this.curScaleDim != null) {
                this.curScale.setPreferredSize(this.curScaleDim);
            }
        }
    }

}

