/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.menu;

import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.gui.menu.LogisimMenuItem;
import com.cburch.logisim.gui.menu.Menu;
import com.cburch.logisim.gui.menu.MenuItem;
import com.cburch.logisim.gui.menu.Strings;
import com.cburch.logisim.gui.opts.OptionsFrame;
import com.cburch.logisim.gui.prefs.PreferencesFrame;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.ProjectActions;
import com.cburch.logisim.util.MacCompatibility;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

class MenuFile
extends Menu
implements ActionListener {
    private LogisimMenuBar menubar;
    private JMenuItem newi = new JMenuItem();
    private JMenuItem open = new JMenuItem();
    private JMenuItem close = new JMenuItem();
    private JMenuItem save = new JMenuItem();
    private JMenuItem saveAs = new JMenuItem();
    private MenuItem print;
    private MenuItem exportGif;
    private JMenuItem prefs;
    private JMenuItem quit;

    public MenuFile(LogisimMenuBar menubar) {
        this.print = new MenuItem(this, LogisimMenuBar.PRINT);
        this.exportGif = new MenuItem(this, LogisimMenuBar.EXPORT_GIF);
        this.prefs = new JMenuItem();
        this.quit = new JMenuItem();
        this.menubar = menubar;
        int menuMask = this.getToolkit().getMenuShortcutKeyMask();
        this.newi.setAccelerator(KeyStroke.getKeyStroke(78, menuMask));
        this.open.setAccelerator(KeyStroke.getKeyStroke(79, menuMask));
        this.close.setAccelerator(KeyStroke.getKeyStroke(87, menuMask));
        this.save.setAccelerator(KeyStroke.getKeyStroke(83, menuMask));
        this.saveAs.setAccelerator(KeyStroke.getKeyStroke(83, menuMask | 1));
        this.print.setAccelerator(KeyStroke.getKeyStroke(80, menuMask));
        this.quit.setAccelerator(KeyStroke.getKeyStroke(81, menuMask));
        this.add(this.newi);
        this.add(this.open);
        this.addSeparator();
        this.add(this.close);
        this.add(this.save);
        this.add(this.saveAs);
        this.addSeparator();
        this.add(this.exportGif);
        this.add(this.print);
        if (!MacCompatibility.isPreferencesAutomaticallyPresent()) {
            this.addSeparator();
            this.add(this.prefs);
        }
        if (!MacCompatibility.isQuitAutomaticallyPresent()) {
            this.addSeparator();
            this.add(this.quit);
        }
        Project proj = menubar.getProject();
        this.newi.addActionListener(this);
        this.open.addActionListener(this);
        if (proj == null) {
            this.close.setEnabled(false);
            this.save.setEnabled(false);
            this.saveAs.setEnabled(false);
        } else {
            this.close.addActionListener(this);
            this.save.addActionListener(this);
            this.saveAs.addActionListener(this);
        }
        menubar.registerItem(LogisimMenuBar.EXPORT_GIF, this.exportGif);
        menubar.registerItem(LogisimMenuBar.PRINT, this.print);
        this.prefs.addActionListener(this);
        this.quit.addActionListener(this);
    }

    public void localeChanged() {
        this.setText(Strings.get("fileMenu"));
        this.newi.setText(Strings.get("fileNewItem"));
        this.open.setText(Strings.get("fileOpenItem"));
        this.close.setText(Strings.get("fileCloseItem"));
        this.save.setText(Strings.get("fileSaveItem"));
        this.saveAs.setText(Strings.get("fileSaveAsItem"));
        this.exportGif.setText(Strings.get("fileExportGifItem"));
        this.print.setText(Strings.get("filePrintItem"));
        this.prefs.setText(Strings.get("filePreferencesItem"));
        this.quit.setText(Strings.get("fileQuitItem"));
    }

    @Override
    void computeEnabled() {
        this.setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        Project proj = this.menubar.getProject();
        if (src == this.newi) {
            ProjectActions.doNew(proj);
        } else if (src == this.open) {
            ProjectActions.doOpen(proj == null ? null : proj.getFrame().getCanvas(), proj);
        } else if (src == this.close) {
            Frame frame = proj.getFrame();
            if (frame.confirmClose()) {
                frame.dispose();
                OptionsFrame f = proj.getOptionsFrame(false);
                if (f != null) {
                    f.dispose();
                }
            }
        } else if (src == this.save) {
            ProjectActions.doSave(proj);
        } else if (src == this.saveAs) {
            ProjectActions.doSaveAs(proj);
        } else if (src == this.prefs) {
            PreferencesFrame.showPreferences();
        } else if (src == this.quit) {
            ProjectActions.doQuit();
        }
    }
}

