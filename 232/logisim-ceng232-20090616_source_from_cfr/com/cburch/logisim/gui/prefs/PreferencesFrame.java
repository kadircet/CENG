/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.prefs;

import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.gui.prefs.ExperimentalOptions;
import com.cburch.logisim.gui.prefs.IntlOptions;
import com.cburch.logisim.gui.prefs.OptionsPanel;
import com.cburch.logisim.gui.prefs.Strings;
import com.cburch.logisim.gui.prefs.TemplateOptions;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
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

public class PreferencesFrame
extends JFrame {
    private static final WindowMenuManager windowMenuManager = new WindowMenuManager();
    private MyListener myListener;
    private OptionsPanel[] panels;
    private JTabbedPane tabbedPane;
    private JButton close;

    private PreferencesFrame() {
        this.myListener = new MyListener();
        this.close = new JButton();
        this.setDefaultCloseOperation(1);
        this.setJMenuBar(new LogisimMenuBar(this, null));
        this.panels = new OptionsPanel[]{new TemplateOptions(this), new IntlOptions(this), new ExperimentalOptions(this)};
        this.tabbedPane = new JTabbedPane();
        for (int index = 0; index < this.panels.length; ++index) {
            OptionsPanel panel = this.panels[index];
            this.tabbedPane.addTab(panel.getTitle(), null, panel, panel.getToolTipText());
        }
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.close);
        this.close.addActionListener(this.myListener);
        Container contents = this.getContentPane();
        this.tabbedPane.setPreferredSize(new Dimension(450, 300));
        contents.add((Component)this.tabbedPane, "Center");
        contents.add((Component)buttonPanel, "South");
        LocaleManager.addLocaleListener(this.myListener);
        this.myListener.localeChanged();
        this.pack();
    }

    public static void showPreferences() {
        JFrame frame = windowMenuManager.getJFrame(true);
        frame.setVisible(true);
    }

    private class MyListener
    implements ActionListener,
    LocaleListener {
        private MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src == PreferencesFrame.this.close) {
                WindowEvent e = new WindowEvent(PreferencesFrame.this, 201);
                PreferencesFrame.this.processWindowEvent(e);
            }
        }

        @Override
        public void localeChanged() {
            PreferencesFrame.this.setTitle(Strings.get("preferencesFrameTitle"));
            for (int i = 0; i < PreferencesFrame.this.panels.length; ++i) {
                PreferencesFrame.this.tabbedPane.setTitleAt(i, PreferencesFrame.this.panels[i].getTitle());
                PreferencesFrame.this.tabbedPane.setToolTipTextAt(i, PreferencesFrame.this.panels[i].getToolTipText());
                PreferencesFrame.this.panels[i].localeChanged();
            }
            PreferencesFrame.this.close.setText(Strings.get("closeButton"));
        }
    }

    private static class WindowMenuManager
    extends WindowMenuItemManager
    implements LocaleListener {
        private PreferencesFrame window = null;

        WindowMenuManager() {
            super(Strings.get("preferencesFrameMenuItem"), false);
            LocaleManager.addLocaleListener(this);
        }

        @Override
        public JFrame getJFrame(boolean create) {
            if (create && this.window == null) {
                this.window = new PreferencesFrame();
                this.frameOpened(this.window);
            }
            return this.window;
        }

        @Override
        public void localeChanged() {
            this.setText(Strings.get("preferencesFrameMenuItem"));
        }
    }

}

