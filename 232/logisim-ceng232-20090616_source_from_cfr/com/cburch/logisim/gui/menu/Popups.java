/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.menu;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.file.LoadedLibrary;
import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.menu.ProjectCircuitActions;
import com.cburch.logisim.gui.menu.ProjectLibraryActions;
import com.cburch.logisim.gui.menu.Strings;
import com.cburch.logisim.proj.Dependencies;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class Popups {
    public static JPopupMenu forCircuit(Project proj, AddTool tool, Circuit circ) {
        return new CircuitPopup(proj, tool, circ);
    }

    public static JPopupMenu forTool(Project proj, Tool tool) {
        return null;
    }

    public static JPopupMenu forProject(Project proj) {
        return new ProjectPopup(proj);
    }

    public static JPopupMenu forLibrary(Project proj, Library lib, boolean isTop) {
        return new LibraryPopup(proj, lib, isTop);
    }

    private static class CircuitPopup
    extends JPopupMenu
    implements ActionListener {
        Project proj;
        Tool tool;
        Circuit circuit;
        JMenuItem view = new JMenuItem(Strings.get("projectViewCircuitItem"));
        JMenuItem analyze = new JMenuItem(Strings.get("projectAnalyzeCircuitItem"));
        JMenuItem rename = new JMenuItem(Strings.get("projectRenameCircuitItem"));
        JMenuItem main = new JMenuItem(Strings.get("projectSetAsMainItem"));
        JMenuItem remove = new JMenuItem(Strings.get("projectRemoveCircuitItem"));

        CircuitPopup(Project proj, Tool tool, Circuit circuit) {
            super(Strings.get("circuitMenu"));
            this.proj = proj;
            this.tool = tool;
            this.circuit = circuit;
            this.add(this.view);
            this.view.addActionListener(this);
            this.addSeparator();
            this.add(this.rename);
            this.rename.addActionListener(this);
            this.add(this.main);
            this.main.addActionListener(this);
            this.add(this.remove);
            this.remove.addActionListener(this);
            boolean canChange = proj.getLogisimFile().contains(circuit);
            LogisimFile file = proj.getLogisimFile();
            this.view.setEnabled(proj.getCurrentCircuit() != circuit);
            this.rename.setEnabled(canChange);
            this.main.setEnabled(canChange && file.getMainCircuit() != circuit);
            this.remove.setEnabled(canChange && file.getCircuitCount() > 1 && proj.getDependencies().canRemove(circuit));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == this.view) {
                this.proj.setCurrentCircuit(this.circuit);
            } else if (source == this.analyze) {
                ProjectCircuitActions.doAnalyze(this.proj, this.circuit);
            } else if (source == this.rename) {
                ProjectCircuitActions.doRenameCircuit(this.proj, this.circuit);
            } else if (source == this.main) {
                ProjectCircuitActions.doSetAsMainCircuit(this.proj, this.circuit);
            } else if (source == this.remove) {
                ProjectCircuitActions.doRemoveCircuit(this.proj, this.circuit);
            }
        }
    }

    private static class LibraryPopup
    extends JPopupMenu
    implements ActionListener {
        Project proj;
        Library lib;
        JMenuItem unload = new JMenuItem(Strings.get("projectUnloadLibraryItem"));
        JMenuItem reload = new JMenuItem(Strings.get("projectReloadLibraryItem"));

        LibraryPopup(Project proj, Library lib, boolean is_top) {
            super(Strings.get("libMenu"));
            this.proj = proj;
            this.lib = lib;
            this.add(this.unload);
            this.unload.addActionListener(this);
            this.add(this.reload);
            this.reload.addActionListener(this);
            this.unload.setEnabled(is_top);
            this.reload.setEnabled(is_top && lib instanceof LoadedLibrary);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == this.unload) {
                ProjectLibraryActions.doUnloadLibrary(this.proj, this.lib);
            } else if (src == this.reload) {
                Loader loader = this.proj.getLogisimFile().getLoader();
                loader.reload((LoadedLibrary)this.lib);
            }
        }
    }

    private static class ProjectPopup
    extends JPopupMenu
    implements ActionListener {
        Project proj;
        JMenuItem add = new JMenuItem(Strings.get("projectAddCircuitItem"));
        JMenu load = new JMenu(Strings.get("projectLoadLibraryItem"));
        JMenuItem loadBuiltin = new JMenuItem(Strings.get("projectLoadBuiltinItem"));
        JMenuItem loadLogisim = new JMenuItem(Strings.get("projectLoadLogisimItem"));
        JMenuItem loadJar = new JMenuItem(Strings.get("projectLoadJarItem"));

        ProjectPopup(Project proj) {
            super(Strings.get("projMenu"));
            this.proj = proj;
            this.load.add(this.loadBuiltin);
            this.loadBuiltin.addActionListener(this);
            this.load.add(this.loadLogisim);
            this.loadLogisim.addActionListener(this);
            this.load.add(this.loadJar);
            this.loadJar.addActionListener(this);
            this.add(this.add);
            this.add.addActionListener(this);
            this.add(this.load);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == this.add) {
                ProjectCircuitActions.doAddCircuit(this.proj);
            } else if (src == this.loadBuiltin) {
                ProjectLibraryActions.doLoadBuiltinLibrary(this.proj);
            } else if (src == this.loadLogisim) {
                ProjectLibraryActions.doLoadLogisimLibrary(this.proj);
            } else if (src == this.loadJar) {
                ProjectLibraryActions.doLoadJarLibrary(this.proj);
            }
        }
    }

}

