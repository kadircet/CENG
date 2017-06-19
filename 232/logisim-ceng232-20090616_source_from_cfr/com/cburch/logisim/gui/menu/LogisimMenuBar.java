/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.menu;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Simulator;
import com.cburch.logisim.gui.menu.LogisimMenuItem;
import com.cburch.logisim.gui.menu.MenuEdit;
import com.cburch.logisim.gui.menu.MenuFile;
import com.cburch.logisim.gui.menu.MenuHelp;
import com.cburch.logisim.gui.menu.MenuItem;
import com.cburch.logisim.gui.menu.MenuProject;
import com.cburch.logisim.gui.menu.MenuSimulate;
import com.cburch.logisim.gui.menu.SimulateListener;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class LogisimMenuBar
extends JMenuBar {
    public static final LogisimMenuItem PRINT = new LogisimMenuItem("Print");
    public static final LogisimMenuItem EXPORT_GIF = new LogisimMenuItem("ExportGIF");
    public static final LogisimMenuItem CUT = new LogisimMenuItem("Cut");
    public static final LogisimMenuItem COPY = new LogisimMenuItem("Copy");
    public static final LogisimMenuItem PASTE = new LogisimMenuItem("Paste");
    public static final LogisimMenuItem DELETE = new LogisimMenuItem("Delete");
    public static final LogisimMenuItem SELECT_ALL = new LogisimMenuItem("SelectAll");
    public static final LogisimMenuItem ADD_CIRCUIT = new LogisimMenuItem("AddCircuit");
    public static final LogisimMenuItem RENAME_CIRCUIT = new LogisimMenuItem("RenameCircuit");
    public static final LogisimMenuItem SET_MAIN_CIRCUIT = new LogisimMenuItem("SetMainCircuit");
    public static final LogisimMenuItem REMOVE_CIRCUIT = new LogisimMenuItem("RemoveCircuit");
    public static final LogisimMenuItem ANALYZE_CIRCUIT = new LogisimMenuItem("AnalyzeCircuit");
    private JFrame parent;
    private MyListener listener;
    private Project proj;
    private SimulateListener simulateListener = null;
    private HashMap menuItems = new HashMap();
    private MenuFile file;
    private MenuEdit edit;
    private MenuProject project;
    private MenuSimulate simulate;
    private MenuHelp help;

    public LogisimMenuBar(JFrame parent, Project proj) {
        this.parent = parent;
        this.listener = new MyListener();
        this.proj = proj;
        this.file = new MenuFile(this);
        this.add(this.file);
        this.edit = new MenuEdit(this);
        this.add(this.edit);
        this.project = new MenuProject(this);
        this.add(this.project);
        this.simulate = new MenuSimulate(this);
        this.add(this.simulate);
        this.help = new MenuHelp(this);
        this.add(this.help);
        LocaleManager.addLocaleListener(this.listener);
        this.listener.localeChanged();
    }

    public void setEnabled(LogisimMenuItem which, boolean value) {
        JMenuItem item = (JMenuItem)this.menuItems.get(which);
        if (item != null) {
            item.setEnabled(value);
        }
    }

    public void addActionListener(LogisimMenuItem which, ActionListener l) {
        MenuItem item = (MenuItem)this.menuItems.get(which);
        if (item != null) {
            item.addActionListener(l);
        }
    }

    public void removeActionListener(LogisimMenuItem which, ActionListener l) {
        MenuItem item = (MenuItem)this.menuItems.get(which);
        if (item != null) {
            item.removeActionListener(l);
        }
    }

    public void setSimulateListener(SimulateListener l) {
        this.simulateListener = l;
    }

    public void setCircuitState(Simulator sim, CircuitState state) {
        this.simulate.setCurrentState(sim, state);
    }

    Project getProject() {
        return this.proj;
    }

    JFrame getParentWindow() {
        return this.parent;
    }

    void registerItem(LogisimMenuItem which, MenuItem item) {
        this.menuItems.put(which, item);
    }

    void fireStateChanged(Simulator sim, CircuitState state) {
        if (this.simulateListener != null) {
            this.simulateListener.stateChangeRequested(sim, state);
        }
    }

    private class MyListener
    implements LocaleListener {
        private MyListener() {
        }

        @Override
        public void localeChanged() {
            LogisimMenuBar.this.file.localeChanged();
            LogisimMenuBar.this.edit.localeChanged();
            LogisimMenuBar.this.project.localeChanged();
            LogisimMenuBar.this.simulate.localeChanged();
            LogisimMenuBar.this.help.localeChanged();
        }
    }

}

