/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.menu;

import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.gui.menu.LogisimMenuItem;
import com.cburch.logisim.gui.menu.Menu;
import com.cburch.logisim.gui.menu.MenuItem;
import com.cburch.logisim.gui.menu.ProjectLibraryActions;
import com.cburch.logisim.gui.menu.Strings;
import com.cburch.logisim.gui.opts.OptionsFrame;
import com.cburch.logisim.proj.Project;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

class MenuProject
extends Menu {
    private LogisimMenuBar menubar;
    private MyListener myListener;
    private MenuItem addCircuit;
    private JMenu loadLibrary;
    private JMenuItem loadBuiltin;
    private JMenuItem loadLogisim;
    private JMenuItem loadJar;
    private JMenuItem unload;
    private MenuItem analyze;
    private MenuItem rename;
    private MenuItem setAsMain;
    private MenuItem remove;
    private JMenuItem options;

    MenuProject(LogisimMenuBar menubar) {
        this.myListener = new MyListener();
        this.addCircuit = new MenuItem(this, LogisimMenuBar.ADD_CIRCUIT);
        this.loadLibrary = new JMenu();
        this.loadBuiltin = new JMenuItem();
        this.loadLogisim = new JMenuItem();
        this.loadJar = new JMenuItem();
        this.unload = new JMenuItem();
        this.analyze = new MenuItem(this, LogisimMenuBar.ANALYZE_CIRCUIT);
        this.rename = new MenuItem(this, LogisimMenuBar.RENAME_CIRCUIT);
        this.setAsMain = new MenuItem(this, LogisimMenuBar.SET_MAIN_CIRCUIT);
        this.remove = new MenuItem(this, LogisimMenuBar.REMOVE_CIRCUIT);
        this.options = new JMenuItem();
        this.menubar = menubar;
        menubar.registerItem(LogisimMenuBar.ADD_CIRCUIT, this.addCircuit);
        this.loadBuiltin.addActionListener(this.myListener);
        this.loadLogisim.addActionListener(this.myListener);
        this.loadJar.addActionListener(this.myListener);
        this.unload.addActionListener(this.myListener);
        menubar.registerItem(LogisimMenuBar.ANALYZE_CIRCUIT, this.analyze);
        menubar.registerItem(LogisimMenuBar.RENAME_CIRCUIT, this.rename);
        menubar.registerItem(LogisimMenuBar.SET_MAIN_CIRCUIT, this.setAsMain);
        menubar.registerItem(LogisimMenuBar.REMOVE_CIRCUIT, this.remove);
        this.options.addActionListener(this.myListener);
        this.loadLibrary.add(this.loadBuiltin);
        this.loadLibrary.add(this.loadLogisim);
        this.loadLibrary.add(this.loadJar);
        this.add(this.addCircuit);
        this.add(this.loadLibrary);
        this.add(this.unload);
        this.addSeparator();
        this.add(this.rename);
        this.add(this.setAsMain);
        this.add(this.remove);
        this.addSeparator();
        this.add(this.options);
        boolean known = menubar.getProject() != null;
        this.loadLibrary.setEnabled(known);
        this.loadBuiltin.setEnabled(known);
        this.loadLogisim.setEnabled(known);
        this.loadJar.setEnabled(known);
        this.unload.setEnabled(known);
        this.options.setEnabled(known);
        this.computeEnabled();
    }

    public void localeChanged() {
        this.setText(Strings.get("projectMenu"));
        this.addCircuit.setText(Strings.get("projectAddCircuitItem"));
        this.loadLibrary.setText(Strings.get("projectLoadLibraryItem"));
        this.loadBuiltin.setText(Strings.get("projectLoadBuiltinItem"));
        this.loadLogisim.setText(Strings.get("projectLoadLogisimItem"));
        this.loadJar.setText(Strings.get("projectLoadJarItem"));
        this.unload.setText(Strings.get("projectUnloadLibrariesItem"));
        this.analyze.setText(Strings.get("projectAnalyzeCircuitItem"));
        this.rename.setText(Strings.get("projectRenameCircuitItem"));
        this.setAsMain.setText(Strings.get("projectSetAsMainItem"));
        this.remove.setText(Strings.get("projectRemoveCircuitItem"));
        this.options.setText(Strings.get("projectOptionsItem"));
    }

    @Override
    void computeEnabled() {
        this.setEnabled(this.menubar.getProject() != null || this.addCircuit.hasListeners() || this.analyze.hasListeners() || this.rename.hasListeners() || this.setAsMain.hasListeners() || this.remove.hasListeners());
    }

    private class MyListener
    implements ActionListener {
        private MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            Project proj = MenuProject.this.menubar.getProject();
            if (src == MenuProject.this.loadBuiltin) {
                ProjectLibraryActions.doLoadBuiltinLibrary(proj);
            } else if (src == MenuProject.this.loadLogisim) {
                ProjectLibraryActions.doLoadLogisimLibrary(proj);
            } else if (src == MenuProject.this.loadJar) {
                ProjectLibraryActions.doLoadJarLibrary(proj);
            } else if (src == MenuProject.this.unload) {
                ProjectLibraryActions.doUnloadLibraries(proj);
            } else if (src == MenuProject.this.options) {
                OptionsFrame frame = proj.getOptionsFrame(true);
                frame.setVisible(true);
            }
        }
    }

}

