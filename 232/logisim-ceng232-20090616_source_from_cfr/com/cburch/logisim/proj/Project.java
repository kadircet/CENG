/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.proj;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitEvent;
import com.cburch.logisim.circuit.CircuitListener;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Simulator;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.file.LibraryEvent;
import com.cburch.logisim.file.LibraryListener;
import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.MouseMappings;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.gui.log.LogFrame;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.main.Selection;
import com.cburch.logisim.gui.opts.OptionsFrame;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Dependencies;
import com.cburch.logisim.proj.ProjectEvent;
import com.cburch.logisim.proj.ProjectListener;
import com.cburch.logisim.proj.Projects;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.EventSourceWeakSupport;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JFileChooser;
import tr.edu.metu.ceng.ceng232.grader.Grader;

public class Project {
    private static final int MAX_UNDO_SIZE = 64;
    private Simulator simulator = new Simulator();
    private LogisimFile file;
    private CircuitState circuitState;
    private HashMap stateMap = new HashMap();
    private Frame frame = null;
    private OptionsFrame optionsFrame = null;
    private LogFrame logFrame = null;
    private Tool tool = null;
    private LinkedList undoLog = new LinkedList();
    private int undoMods = 0;
    private EventSourceWeakSupport projectListeners = new EventSourceWeakSupport();
    private EventSourceWeakSupport fileListeners = new EventSourceWeakSupport();
    private EventSourceWeakSupport circuitListeners = new EventSourceWeakSupport();
    private ProjectEvent repaint;
    private Dependencies depends;
    private Selection selection;
    private MyListener myListener;
    private boolean startupScreen;
    private Grader grader;

    public Project(LogisimFile file) {
        this.selection = new Selection(this);
        this.myListener = new MyListener();
        this.startupScreen = false;
        this.grader = null;
        this.selection.addListener(this.myListener);
        this.addLibraryListener(this.myListener);
        this.addCircuitListener(this.myListener);
        this.repaint = new ProjectEvent(3, this, null);
        this.setLogisimFile(file);
        this.grader = new Grader(this);
    }

    public void setFrame(Frame value) {
        if (this.frame == value) {
            return;
        }
        Frame oldValue = this.frame;
        this.frame = value;
        Projects.windowCreated(this, oldValue, value);
    }

    public LogisimFile getLogisimFile() {
        return this.file;
    }

    public Simulator getSimulator() {
        return this.simulator;
    }

    public Options getOptions() {
        return this.file.getOptions();
    }

    public Dependencies getDependencies() {
        return this.depends;
    }

    public Frame getFrame() {
        return this.frame;
    }

    public OptionsFrame getOptionsFrame(boolean create) {
        if (this.optionsFrame == null || this.optionsFrame.getLogisimFile() != this.file) {
            this.optionsFrame = create ? new OptionsFrame(this) : null;
        }
        return this.optionsFrame;
    }

    public LogFrame getLogFrame(boolean create) {
        if (this.logFrame == null && create) {
            this.logFrame = new LogFrame(this);
        }
        return this.logFrame;
    }

    public Circuit getCurrentCircuit() {
        return this.circuitState == null ? null : this.circuitState.getCircuit();
    }

    public CircuitState getCircuitState() {
        return this.circuitState;
    }

    public CircuitState getCircuitState(Circuit circuit) {
        if (this.circuitState != null && this.circuitState.getCircuit() == circuit) {
            return this.circuitState;
        }
        CircuitState ret = (CircuitState)this.stateMap.get(circuit);
        if (ret == null) {
            ret = new CircuitState(this, circuit);
            this.stateMap.put(circuit, ret);
        }
        return ret;
    }

    public Action getLastAction() {
        if (this.undoLog.size() == 0) {
            return null;
        }
        return ((ActionData)this.undoLog.getLast()).action;
    }

    public Tool getTool() {
        return this.tool;
    }

    public Selection getSelection() {
        return this.selection;
    }

    public boolean isFileDirty() {
        return this.undoMods != 0;
    }

    public JFileChooser createChooser() {
        if (this.file == null) {
            return new JFileChooser();
        }
        Loader loader = this.file.getLoader();
        return loader == null ? new JFileChooser() : loader.createChooser();
    }

    public void addProjectListener(ProjectListener what) {
        this.projectListeners.add(what);
    }

    public void removeProjectListener(ProjectListener what) {
        this.projectListeners.remove(what);
    }

    public void addLibraryListener(LibraryListener value) {
        this.fileListeners.add(value);
        if (this.file != null) {
            this.file.addLibraryListener(value);
        }
    }

    public void removeLibraryListener(LibraryListener value) {
        this.fileListeners.remove(value);
        if (this.file != null) {
            this.file.removeLibraryListener(value);
        }
    }

    public void addCircuitListener(CircuitListener value) {
        this.circuitListeners.add(value);
        Circuit current = this.getCurrentCircuit();
        if (current != null) {
            current.addCircuitListener(value);
        }
    }

    public void removeCircuitListener(CircuitListener value) {
        this.circuitListeners.remove(value);
        Circuit current = this.getCurrentCircuit();
        if (current != null) {
            current.removeCircuitListener(value);
        }
    }

    private void fireEvent(int action, Object old, Object data) {
        this.fireEvent(new ProjectEvent(action, this, old, data));
    }

    private void fireEvent(int action, Object data) {
        this.fireEvent(new ProjectEvent(action, this, data));
    }

    private void fireEvent(ProjectEvent event) {
        Iterator it = this.projectListeners.iterator();
        while (it.hasNext()) {
            ProjectListener what = (ProjectListener)it.next();
            what.projectChanged(event);
        }
    }

    public boolean isStartupScreen() {
        return this.startupScreen;
    }

    public boolean confirmClose(String title) {
        return this.frame.confirmClose(title);
    }

    public void setStartupScreen(boolean value) {
        this.startupScreen = value;
    }

    public void setLogisimFile(LogisimFile value) {
        Iterator it;
        LogisimFile old = this.file;
        if (old != null) {
            it = this.fileListeners.iterator();
            while (it.hasNext()) {
                old.removeLibraryListener((LibraryListener)it.next());
            }
        }
        this.file = value;
        this.stateMap.clear();
        this.depends = new Dependencies(this.file);
        this.undoLog.clear();
        this.undoMods = 0;
        this.fireEvent(0, old, this.file);
        this.setCurrentCircuit(this.file.getMainCircuit());
        if (this.file != null) {
            it = this.fileListeners.iterator();
            while (it.hasNext()) {
                LibraryListener l = (LibraryListener)it.next();
                this.file.addLibraryListener(l);
            }
        }
        this.file.setDirty(true);
        this.file.setDirty(false);
    }

    public void setCircuitState(CircuitState value) {
        Iterator it;
        boolean circuitChanged;
        if (value == null || this.circuitState == value) {
            return;
        }
        CircuitState old = this.circuitState;
        Circuit oldCircuit = old == null ? null : old.getCircuit();
        Circuit newCircuit = value.getCircuit();
        boolean bl = circuitChanged = old == null || oldCircuit != newCircuit;
        if (circuitChanged) {
            if (this.tool != null) {
                this.tool.deselect(this.frame.getCanvas());
            }
            this.selection.clear();
            if (this.tool != null) {
                this.tool.select(this.frame.getCanvas());
            }
            if (oldCircuit != null) {
                it = this.circuitListeners.iterator();
                while (it.hasNext()) {
                    oldCircuit.removeCircuitListener((CircuitListener)it.next());
                }
            }
        }
        this.circuitState = value;
        this.stateMap.put(this.circuitState.getCircuit(), this.circuitState);
        this.simulator.setCircuitState(this.circuitState);
        if (circuitChanged) {
            this.fireEvent(1, oldCircuit, newCircuit);
            if (newCircuit != null) {
                it = this.circuitListeners.iterator();
                while (it.hasNext()) {
                    newCircuit.addCircuitListener((CircuitListener)it.next());
                }
            }
        }
        this.fireEvent(5, old, this.circuitState);
    }

    public void setCurrentCircuit(Circuit circuit) {
        CircuitState circState = (CircuitState)this.stateMap.get(circuit);
        if (circState == null) {
            circState = new CircuitState(this, circuit);
        }
        this.setCircuitState(circState);
    }

    public void setTool(Tool value) {
        if (this.tool == value) {
            return;
        }
        Tool old = this.tool;
        if (old != null) {
            old.deselect(this.frame.getCanvas());
        }
        if (!this.selection.isEmpty()) {
            if (value == null) {
                this.selection.dropAll();
            } else if (!this.getOptions().getMouseMappings().containsSelectTool()) {
                this.selection.clear();
            }
        }
        this.startupScreen = false;
        this.tool = value;
        if (this.tool != null) {
            this.tool.select(this.frame.getCanvas());
        }
        this.fireEvent(2, old, this.tool);
    }

    public void doAction(Action act) {
        if (act == null) {
            return;
        }
        this.startupScreen = false;
        act.doIt(this);
        if (!this.undoLog.isEmpty() && act.shouldAppendTo(this.getLastAction())) {
            ActionData firstData = (ActionData)this.undoLog.removeLast();
            Action first = firstData.action;
            if (first.isModification()) {
                --this.undoMods;
            }
            act = first.append(act);
        }
        while (this.undoLog.size() > 64) {
            this.undoLog.removeFirst();
        }
        this.undoLog.add(new ActionData(this.circuitState, act));
        if (act.isModification()) {
            ++this.undoMods;
        }
        this.file.setDirty(this.isFileDirty());
        this.fireEvent(this.repaint);
    }

    public void undoAction() {
        if (this.undoLog != null && this.undoLog.size() > 0) {
            ActionData data = (ActionData)this.undoLog.removeLast();
            this.setCircuitState(data.circuitState);
            Action action = data.action;
            if (action.isModification()) {
                --this.undoMods;
            }
            action.undo(this);
            this.file.setDirty(this.isFileDirty());
            this.fireEvent(this.repaint);
        }
    }

    public void setFileAsClean() {
        this.undoMods = 0;
        this.file.setDirty(this.isFileDirty());
    }

    public void repaintCanvas() {
        this.fireEvent(this.repaint);
    }

    private class MyListener
    implements Selection.Listener,
    LibraryListener,
    CircuitListener {
        private MyListener() {
        }

        @Override
        public void selectionChanged(Selection.Event e) {
            Project.this.fireEvent(4, Project.this.selection);
        }

        @Override
        public void libraryChanged(LibraryEvent event) {
            int action = event.getAction();
            if (action == 3) {
                Library unloaded = (Library)event.getData();
                if (Project.this.tool != null && unloaded.containsFromSource(Project.this.tool)) {
                    Project.this.setTool(null);
                }
            } else if (action == 1 && event.getData() == Project.this.getCurrentCircuit()) {
                Project.this.setCurrentCircuit(Project.this.file.getMainCircuit());
            }
        }

        @Override
        public void circuitChanged(CircuitEvent event) {
            if (event.getAction() == 2) {
                Component comp = (Component)event.getData();
                if (Project.this.selection != null) {
                    Project.this.selection.remove(comp);
                }
            } else if (event.getAction() == 5) {
                Project.this.selection.clear(false);
            }
        }
    }

    private static class ActionData {
        CircuitState circuitState;
        Action action;

        public ActionData(CircuitState circuitState, Action action) {
            this.circuitState = circuitState;
            this.action = action;
        }
    }

}

