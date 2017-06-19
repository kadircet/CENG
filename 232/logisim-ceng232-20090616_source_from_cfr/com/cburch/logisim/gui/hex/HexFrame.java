/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.hex;

import com.cburch.hex.Caret;
import com.cburch.hex.HexEditor;
import com.cburch.hex.HexModel;
import com.cburch.logisim.gui.hex.Clip;
import com.cburch.logisim.gui.hex.HexFile;
import com.cburch.logisim.gui.hex.Strings;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.gui.menu.LogisimMenuItem;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.WindowMenuItemManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class HexFrame
extends JFrame {
    private WindowMenuManager windowManager;
    private EditListener editListener;
    private MyListener myListener;
    private HexModel model;
    private HexEditor editor;
    private JButton open;
    private JButton save;
    private JButton close;

    public HexFrame(Project proj, HexModel model) {
        this.windowManager = new WindowMenuManager();
        this.editListener = new EditListener();
        this.myListener = new MyListener();
        this.open = new JButton();
        this.save = new JButton();
        this.close = new JButton();
        this.setDefaultCloseOperation(1);
        LogisimMenuBar menubar = new LogisimMenuBar(this, proj);
        this.setJMenuBar(menubar);
        this.model = model;
        this.editor = new HexEditor(model);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.open);
        buttonPanel.add(this.save);
        buttonPanel.add(this.close);
        this.open.addActionListener(this.myListener);
        this.save.addActionListener(this.myListener);
        this.close.addActionListener(this.myListener);
        Dimension pref = this.editor.getPreferredSize();
        JScrollPane scroll = new JScrollPane(this.editor, 22, 31);
        pref.height = Math.min(pref.height, pref.width * 3 / 2);
        scroll.setPreferredSize(pref);
        scroll.getViewport().setBackground(this.editor.getBackground());
        Container contents = this.getContentPane();
        contents.add((Component)scroll, "Center");
        contents.add((Component)buttonPanel, "South");
        LocaleManager.addLocaleListener(this.myListener);
        this.myListener.localeChanged();
        this.pack();
        Dimension size = this.getSize();
        Dimension screen = this.getToolkit().getScreenSize();
        if (size.width > screen.width || size.height > screen.height) {
            size.width = Math.min(size.width, screen.width);
            size.height = Math.min(size.height, screen.height);
            this.setSize(size);
        }
        this.editor.getCaret().addChangeListener(this.editListener);
        this.editor.getCaret().setDot(0, false);
        this.editListener.register(menubar);
    }

    @Override
    public void setVisible(boolean value) {
        if (value && !this.isVisible()) {
            this.windowManager.frameOpened(this);
        }
        super.setVisible(value);
    }

    private class EditListener
    implements ActionListener,
    ChangeListener {
        private Clip clip;

        private EditListener() {
            this.clip = null;
        }

        private Clip getClip() {
            if (this.clip == null) {
                this.clip = new Clip(HexFrame.this.editor);
            }
            return this.clip;
        }

        private void register(LogisimMenuBar menubar) {
            menubar.addActionListener(LogisimMenuBar.CUT, this);
            menubar.addActionListener(LogisimMenuBar.COPY, this);
            menubar.addActionListener(LogisimMenuBar.PASTE, this);
            menubar.addActionListener(LogisimMenuBar.DELETE, this);
            menubar.addActionListener(LogisimMenuBar.SELECT_ALL, this);
            this.enableItems(menubar);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == LogisimMenuBar.CUT) {
                this.getClip().copy();
                HexFrame.this.editor.delete();
            } else if (src == LogisimMenuBar.COPY) {
                this.getClip().copy();
            } else if (src == LogisimMenuBar.PASTE) {
                this.getClip().paste();
            } else if (src == LogisimMenuBar.DELETE) {
                HexFrame.this.editor.delete();
            } else if (src == LogisimMenuBar.SELECT_ALL) {
                HexFrame.this.editor.selectAll();
            }
        }

        private void enableItems(LogisimMenuBar menubar) {
            boolean sel = HexFrame.this.editor.selectionExists();
            boolean clip = true;
            menubar.setEnabled(LogisimMenuBar.CUT, sel);
            menubar.setEnabled(LogisimMenuBar.COPY, sel);
            menubar.setEnabled(LogisimMenuBar.PASTE, clip);
            menubar.setEnabled(LogisimMenuBar.DELETE, sel);
            menubar.setEnabled(LogisimMenuBar.SELECT_ALL, true);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            this.enableItems((LogisimMenuBar)HexFrame.this.getJMenuBar());
        }
    }

    private class MyListener
    implements ActionListener,
    LocaleListener {
        private File lastFile;

        private MyListener() {
            this.lastFile = null;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src == HexFrame.this.open) {
                JFileChooser chooser = new JFileChooser();
                if (this.lastFile != null) {
                    chooser.setSelectedFile(this.lastFile);
                }
                chooser.setDialogTitle(Strings.get("openButton"));
                int choice = chooser.showOpenDialog(HexFrame.this);
                if (choice == 0) {
                    File f = chooser.getSelectedFile();
                    try {
                        HexFile.open(HexFrame.this.model, f);
                        this.lastFile = f;
                    }
                    catch (IOException e) {
                        JOptionPane.showMessageDialog(HexFrame.this, e.getMessage(), Strings.get("hexOpenErrorTitle"), 0);
                    }
                }
            } else if (src == HexFrame.this.save) {
                JFileChooser chooser = new JFileChooser();
                if (this.lastFile != null) {
                    chooser.setSelectedFile(this.lastFile);
                }
                chooser.setDialogTitle(Strings.get("saveButton"));
                int choice = chooser.showSaveDialog(HexFrame.this);
                if (choice == 0) {
                    File f = chooser.getSelectedFile();
                    try {
                        HexFile.save(f, HexFrame.this.model);
                        this.lastFile = f;
                    }
                    catch (IOException e) {
                        JOptionPane.showMessageDialog(HexFrame.this, e.getMessage(), Strings.get("hexSaveErrorTitle"), 0);
                    }
                }
            } else if (src == HexFrame.this.close) {
                WindowEvent e = new WindowEvent(HexFrame.this, 201);
                HexFrame.this.processWindowEvent(e);
            }
        }

        @Override
        public void localeChanged() {
            HexFrame.this.setTitle(Strings.get("hexFrameTitle"));
            HexFrame.this.open.setText(Strings.get("openButton"));
            HexFrame.this.save.setText(Strings.get("saveButton"));
            HexFrame.this.close.setText(Strings.get("closeButton"));
        }
    }

    private class WindowMenuManager
    extends WindowMenuItemManager
    implements LocaleListener {
        WindowMenuManager() {
            super(Strings.get("hexFrameMenuItem"), false);
            LocaleManager.addLocaleListener(this);
        }

        @Override
        public JFrame getJFrame(boolean create) {
            return HexFrame.this;
        }

        @Override
        public void localeChanged() {
            this.setText(Strings.get("hexFrameMenuItem"));
        }
    }

}

