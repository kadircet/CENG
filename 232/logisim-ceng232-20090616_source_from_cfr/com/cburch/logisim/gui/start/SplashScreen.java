/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.start;

import com.cburch.logisim.gui.start.About;
import com.cburch.logisim.gui.start.Strings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.border.Border;

public class SplashScreen
extends JWindow
implements ActionListener {
    public static final int LIBRARIES = 0;
    public static final int TEMPLATE_CREATE = 1;
    public static final int TEMPLATE_OPEN = 2;
    public static final int TEMPLATE_LOAD = 3;
    public static final int TEMPLATE_CLOSE = 4;
    public static final int GUI_INIT = 5;
    public static final int FILE_CREATE = 6;
    public static final int FILE_LOAD = 7;
    public static final int PROJECT_CREATE = 8;
    public static final int FRAME_CREATE = 9;
    private static final int PROGRESS_MAX = 3568;
    private static final boolean PRINT_TIMES = false;
    Marker[] markers = new Marker[]{new Marker(377, Strings.get("progressLibraries")), new Marker(990, Strings.get("progressTemplateCreate")), new Marker(1002, Strings.get("progressTemplateOpen")), new Marker(1002, Strings.get("progressTemplateLoad")), new Marker(1470, Strings.get("progressTemplateClose")), new Marker(1478, Strings.get("progressGuiInitialize")), new Marker(2114, Strings.get("progressFileCreate")), new Marker(2114, Strings.get("progressFileLoad")), new Marker(2383, Strings.get("progressProjectCreate")), new Marker(2519, Strings.get("progressFrameCreate"))};
    boolean inClose = false;
    JProgressBar progress = new JProgressBar(0, 3568);
    JButton close = new JButton(Strings.get("startupCloseButton"));
    JButton cancel = new JButton(Strings.get("startupQuitButton"));
    long startTime = System.currentTimeMillis();

    public SplashScreen() {
        JPanel imagePanel = About.getImagePanel();
        imagePanel.setBorder(null);
        this.progress.setStringPainted(true);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.close);
        this.close.addActionListener(this);
        buttonPanel.add(this.cancel);
        this.cancel.addActionListener(this);
        JPanel contents = new JPanel(new BorderLayout());
        contents.add((Component)imagePanel, "North");
        contents.add((Component)this.progress, "Center");
        contents.add((Component)buttonPanel, "South");
        contents.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        Color bg = imagePanel.getBackground();
        contents.setBackground(bg);
        buttonPanel.setBackground(bg);
        this.setBackground(bg);
        this.setContentPane(contents);
    }

    public void setProgress(int markerId) {
        Marker marker;
        Marker marker2 = marker = this.markers == null ? null : this.markers[markerId];
        if (marker instanceof Marker) {
            Marker m = marker;
            this.progress.setString(m.message);
            this.progress.setValue(m.count);
        }
    }

    @Override
    public void setVisible(boolean value) {
        if (value) {
            this.pack();
            Dimension dim = this.getToolkit().getScreenSize();
            int x = (int)(dim.getWidth() - (double)this.getWidth()) / 2;
            int y = (int)(dim.getHeight() - (double)this.getHeight()) / 2;
            this.setLocation(x, y);
        }
        super.setVisible(value);
    }

    public void close() {
        if (this.inClose) {
            return;
        }
        this.inClose = true;
        this.setVisible(false);
        this.inClose = false;
        this.markers = null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == this.cancel) {
            System.exit(0);
        } else if (src == this.close) {
            this.close();
        }
    }

    private static class Marker {
        int count;
        String message;

        Marker(int count, String message) {
            this.count = count;
            this.message = message;
        }
    }

}

