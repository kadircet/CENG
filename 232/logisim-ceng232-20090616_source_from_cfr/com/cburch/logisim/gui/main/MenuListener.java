/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Simulator;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.file.LibraryEvent;
import com.cburch.logisim.file.LibraryListener;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.main.Clipboard;
import com.cburch.logisim.gui.main.ExportGif;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.main.Print;
import com.cburch.logisim.gui.main.Selection;
import com.cburch.logisim.gui.main.SelectionActions;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.gui.menu.LogisimMenuItem;
import com.cburch.logisim.gui.menu.ProjectCircuitActions;
import com.cburch.logisim.gui.menu.SimulateListener;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.ProjectEvent;
import com.cburch.logisim.proj.ProjectListener;
import com.cburch.logisim.std.Base;
import com.cburch.logisim.tools.Tool;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.Set;

class MenuListener {
    private Frame frame;
    private LogisimMenuBar menubar;
    private FileListener fileListener;
    private EditListener editListener;
    private ProjectMenuListener projectListener;
    private SimulateMenuListener simulateListener;

    public MenuListener(Frame frame, LogisimMenuBar menubar) {
        this.fileListener = new FileListener();
        this.editListener = new EditListener();
        this.projectListener = new ProjectMenuListener();
        this.simulateListener = new SimulateMenuListener();
        this.frame = frame;
        this.menubar = menubar;
    }

    public void register() {
        this.fileListener.register();
        this.editListener.register();
        this.projectListener.register();
        this.simulateListener.register();
    }

    class SimulateMenuListener
    implements ProjectListener,
    SimulateListener {
        SimulateMenuListener() {
        }

        void register() {
            Project proj = MenuListener.this.frame.getProject();
            proj.addProjectListener(this);
            MenuListener.this.menubar.setSimulateListener(this);
            MenuListener.this.menubar.setCircuitState(proj.getSimulator(), proj.getCircuitState());
        }

        @Override
        public void projectChanged(ProjectEvent event) {
            if (event.getAction() == 5) {
                MenuListener.this.menubar.setCircuitState(MenuListener.this.frame.getProject().getSimulator(), MenuListener.this.frame.getProject().getCircuitState());
            }
        }

        @Override
        public void stateChangeRequested(Simulator sim, CircuitState state) {
            if (state != null) {
                MenuListener.this.frame.getProject().setCircuitState(state);
            }
        }
    }

    class ProjectMenuListener
    implements ProjectListener,
    LibraryListener,
    ActionListener {
        ProjectMenuListener() {
        }

        void register() {
            Project proj = MenuListener.this.frame.getProject();
            if (proj == null) {
                return;
            }
            proj.addProjectListener(this);
            proj.addLibraryListener(this);
            MenuListener.this.menubar.addActionListener(LogisimMenuBar.ADD_CIRCUIT, this);
            MenuListener.this.menubar.addActionListener(LogisimMenuBar.RENAME_CIRCUIT, this);
            MenuListener.this.menubar.addActionListener(LogisimMenuBar.SET_MAIN_CIRCUIT, this);
            MenuListener.this.menubar.addActionListener(LogisimMenuBar.REMOVE_CIRCUIT, this);
            MenuListener.this.menubar.addActionListener(LogisimMenuBar.ANALYZE_CIRCUIT, this);
            this.computeEnabled();
        }

        @Override
        public void projectChanged(ProjectEvent event) {
            int action = event.getAction();
            if (action == 1) {
                this.computeEnabled();
            } else if (action == 0) {
                this.computeEnabled();
            }
        }

        @Override
        public void libraryChanged(LibraryEvent event) {
            this.computeEnabled();
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            Project proj = MenuListener.this.frame.getProject();
            if (src == LogisimMenuBar.ADD_CIRCUIT) {
                ProjectCircuitActions.doAddCircuit(proj);
            } else if (src == LogisimMenuBar.ANALYZE_CIRCUIT) {
                ProjectCircuitActions.doAnalyze(proj, proj.getCurrentCircuit());
            } else if (src == LogisimMenuBar.RENAME_CIRCUIT) {
                ProjectCircuitActions.doRenameCircuit(proj, proj.getCurrentCircuit());
            } else if (src == LogisimMenuBar.SET_MAIN_CIRCUIT) {
                ProjectCircuitActions.doSetAsMainCircuit(proj, proj.getCurrentCircuit());
            } else if (src == LogisimMenuBar.REMOVE_CIRCUIT) {
                ProjectCircuitActions.doRemoveCircuit(proj, proj.getCurrentCircuit());
            }
        }

        private void computeEnabled() {
            Project proj = MenuListener.this.frame.getProject();
            boolean isWritableCircuit = proj != null && proj.getLogisimFile().contains(proj.getCurrentCircuit());
            boolean isMainCircuit = proj != null && proj.getLogisimFile().getMainCircuit() == proj.getCurrentCircuit();
            MenuListener.this.menubar.setEnabled(LogisimMenuBar.ADD_CIRCUIT, proj != null);
            MenuListener.this.menubar.setEnabled(LogisimMenuBar.ANALYZE_CIRCUIT, true);
            MenuListener.this.menubar.setEnabled(LogisimMenuBar.RENAME_CIRCUIT, isWritableCircuit);
            MenuListener.this.menubar.setEnabled(LogisimMenuBar.SET_MAIN_CIRCUIT, isWritableCircuit && !isMainCircuit);
            MenuListener.this.menubar.setEnabled(LogisimMenuBar.REMOVE_CIRCUIT, isWritableCircuit && proj.getLogisimFile().getTools().size() > 1);
        }
    }

    private class EditListener
    implements ProjectListener,
    LibraryListener,
    PropertyChangeListener,
    ActionListener {
        private EditListener() {
        }

        private void register() {
            Project proj = MenuListener.this.frame.getProject();
            Clipboard.addPropertyChangeListener("contents", this);
            proj.addProjectListener(this);
            proj.addLibraryListener(this);
            MenuListener.this.menubar.addActionListener(LogisimMenuBar.CUT, this);
            MenuListener.this.menubar.addActionListener(LogisimMenuBar.COPY, this);
            MenuListener.this.menubar.addActionListener(LogisimMenuBar.PASTE, this);
            MenuListener.this.menubar.addActionListener(LogisimMenuBar.DELETE, this);
            MenuListener.this.menubar.addActionListener(LogisimMenuBar.SELECT_ALL, this);
            this.enableItems();
        }

        @Override
        public void projectChanged(ProjectEvent e) {
            int action = e.getAction();
            if (action == 0) {
                this.enableItems();
            } else if (action == 1) {
                this.enableItems();
            } else if (action == 4) {
                this.enableItems();
            }
        }

        @Override
        public void libraryChanged(LibraryEvent e) {
            int action = e.getAction();
            if (action == 2) {
                this.enableItems();
            } else if (action == 3) {
                this.enableItems();
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getPropertyName().equals("contents")) {
                this.enableItems();
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            Project proj = MenuListener.this.frame.getProject();
            if (src == LogisimMenuBar.CUT) {
                proj.doAction(SelectionActions.cut());
            } else if (src == LogisimMenuBar.COPY) {
                proj.doAction(SelectionActions.copy());
            } else if (src == LogisimMenuBar.PASTE) {
                this.selectSelectTool(proj);
                proj.doAction(SelectionActions.paste());
            } else if (src == LogisimMenuBar.DELETE) {
                proj.doAction(SelectionActions.clear());
            } else if (src == LogisimMenuBar.SELECT_ALL) {
                this.selectSelectTool(proj);
                Selection sel = proj.getSelection();
                Circuit circ = proj.getCurrentCircuit();
                sel.addAll(circ.getWires());
                sel.addAll(circ.getNonWires());
                proj.repaintCanvas();
            }
        }

        private void selectSelectTool(Project proj) {
            for (Object lib : proj.getLogisimFile().getLibraries()) {
                Tool tool;
                Base base;
                if (!(lib instanceof Base) || (tool = (base = (Base)lib).getTool("Select Tool")) == null) continue;
                proj.setTool(tool);
            }
        }

        public void enableItems() {
            Project proj = MenuListener.this.frame.getProject();
            Selection sel = proj == null ? null : proj.getSelection();
            boolean selEmpty = sel == null ? true : sel.isEmpty();
            boolean canChange = proj != null && proj.getLogisimFile().contains(proj.getCurrentCircuit());
            boolean selectAvailable = false;
            for (Object lib : proj.getLogisimFile().getLibraries()) {
                if (!(lib instanceof Base)) continue;
                selectAvailable = true;
            }
            MenuListener.this.menubar.setEnabled(LogisimMenuBar.CUT, !selEmpty && selectAvailable && canChange);
            MenuListener.this.menubar.setEnabled(LogisimMenuBar.COPY, !selEmpty && selectAvailable);
            MenuListener.this.menubar.setEnabled(LogisimMenuBar.PASTE, selectAvailable && canChange && !Clipboard.isEmpty());
            MenuListener.this.menubar.setEnabled(LogisimMenuBar.DELETE, !selEmpty && selectAvailable && canChange);
            MenuListener.this.menubar.setEnabled(LogisimMenuBar.SELECT_ALL, selectAvailable);
        }
    }

    private class FileListener
    implements ActionListener {
        private FileListener() {
        }

        private void register() {
            MenuListener.this.menubar.addActionListener(LogisimMenuBar.EXPORT_GIF, this);
            MenuListener.this.menubar.addActionListener(LogisimMenuBar.PRINT, this);
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            Project proj = MenuListener.this.frame.getProject();
            if (src == LogisimMenuBar.EXPORT_GIF) {
                ExportGif.doExport(proj);
            } else if (src == LogisimMenuBar.PRINT) {
                Print.doPrint(proj);
            }
        }
    }

}

