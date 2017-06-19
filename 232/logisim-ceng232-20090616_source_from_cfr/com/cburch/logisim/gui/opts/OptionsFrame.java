/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.opts;

import com.cburch.logisim.file.LibraryEvent;
import com.cburch.logisim.file.LibraryListener;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.LogisimFileActions;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.gui.opts.CanvasOptions;
import com.cburch.logisim.gui.opts.MouseOptions;
import com.cburch.logisim.gui.opts.OptionsPanel;
import com.cburch.logisim.gui.opts.SimulateOptions;
import com.cburch.logisim.gui.opts.Strings;
import com.cburch.logisim.gui.opts.ToolbarOptions;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.StringUtil;
import com.cburch.logisim.util.WindowMenuItemManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class OptionsFrame
extends JFrame {
    private Project project;
    private LogisimFile file;
    private MyListener myListener;
    private WindowMenuManager windowManager;
    private OptionsPanel[] panels;
    private JTabbedPane tabbedPane;
    private JButton revert;
    private JButton close;

    public OptionsFrame(Project project) {
        this.myListener = new MyListener();
        this.windowManager = new WindowMenuManager();
        this.revert = new JButton();
        this.close = new JButton();
        this.project = project;
        this.file = project.getLogisimFile();
        this.file.addLibraryListener(this.myListener);
        this.setDefaultCloseOperation(1);
        this.setJMenuBar(new LogisimMenuBar(this, project));
        this.panels = new OptionsPanel[]{new CanvasOptions(this), new SimulateOptions(this), new ToolbarOptions(this), new MouseOptions(this)};
        this.tabbedPane = new JTabbedPane();
        for (int index = 0; index < this.panels.length; ++index) {
            OptionsPanel panel = this.panels[index];
            this.tabbedPane.addTab(panel.getTitle(), null, panel, panel.getToolTipText());
        }
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.revert);
        buttonPanel.add(this.close);
        this.revert.addActionListener(this.myListener);
        this.close.addActionListener(this.myListener);
        Container contents = this.getContentPane();
        this.tabbedPane.setPreferredSize(new Dimension(450, 300));
        contents.add((Component)this.tabbedPane, "Center");
        contents.add((Component)buttonPanel, "South");
        LocaleManager.addLocaleListener(this.myListener);
        this.myListener.localeChanged();
        this.pack();
    }

    public Project getProject() {
        return this.project;
    }

    public LogisimFile getLogisimFile() {
        return this.file;
    }

    public Options getOptions() {
        return this.file.getOptions();
    }

    @Override
    public void setVisible(boolean value) {
        if (value) {
            this.windowManager.frameOpened(this);
        }
        super.setVisible(value);
    }

    OptionsPanel[] getPrefPanels() {
        return this.panels;
    }

    private static String computeTitle(LogisimFile file) {
        String name = file == null ? "???" : file.getName();
        return StringUtil.format(Strings.get("optionsFrameTitle"), name);
    }

    private class MyListener
    implements ActionListener,
    LibraryListener,
    LocaleListener {
        private MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src == OptionsFrame.this.revert) {
                OptionsFrame.this.getProject().doAction(LogisimFileActions.revertDefaults());
            } else if (src == OptionsFrame.this.close) {
                WindowEvent e = new WindowEvent(OptionsFrame.this, 201);
                OptionsFrame.this.processWindowEvent(e);
            }
        }

        @Override
        public void libraryChanged(LibraryEvent event) {
            if (event.getAction() == 5) {
                OptionsFrame.this.setTitle(OptionsFrame.computeTitle(OptionsFrame.this.file));
                OptionsFrame.this.windowManager.localeChanged();
            }
        }

        @Override
        public void localeChanged() {
            OptionsFrame.this.setTitle(OptionsFrame.computeTitle(OptionsFrame.this.file));
            for (int i = 0; i < OptionsFrame.this.panels.length; ++i) {
                OptionsFrame.this.tabbedPane.setTitleAt(i, OptionsFrame.this.panels[i].getTitle());
                OptionsFrame.this.tabbedPane.setToolTipTextAt(i, OptionsFrame.this.panels[i].getToolTipText());
                OptionsFrame.this.panels[i].localeChanged();
            }
            OptionsFrame.this.revert.setText(Strings.get("revertButton"));
            OptionsFrame.this.close.setText(Strings.get("closeButton"));
            OptionsFrame.this.windowManager.localeChanged();
        }
    }

    private class WindowMenuManager
    extends WindowMenuItemManager
    implements LocaleListener {
        WindowMenuManager() {
            super(Strings.get("optionsFrameMenuItem"), false);
        }

        @Override
        public JFrame getJFrame(boolean create) {
            return OptionsFrame.this;
        }

        @Override
        public void localeChanged() {
            String title = OptionsFrame.this.project.getLogisimFile().getDisplayName();
            this.setText(StringUtil.format(Strings.get("optionsFrameMenuItem"), title));
        }
    }

}

