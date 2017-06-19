/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitActions;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.ComponentAction;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.circuit.WireUtil;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.EndData;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.gui.main.AttributeTable;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.gui.main.Clipboard;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.main.Selection;
import com.cburch.logisim.gui.main.Strings;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.CollectionUtil;
import java.awt.Graphics;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class SelectionBase {
    Project proj;
    private ArrayList listeners = new ArrayList();
    final HashSet selected = new HashSet();
    final HashSet lifted = new HashSet();
    final Set unionSet = CollectionUtil.createUnmodifiableSetUnion(this.selected, this.lifted);
    private Action dropped = null;
    private Bounds bounds = Bounds.EMPTY_BOUNDS;
    private boolean shouldSnap = false;

    public SelectionBase(Project proj) {
        this.proj = proj;
    }

    public void addListener(Selection.Listener l) {
        this.listeners.add(l);
    }

    public void removeListener(Selection.Listener l) {
        this.listeners.remove(l);
    }

    public void fireSelectionChanged() {
        Selection.Event e = new Selection.Event(this);
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Selection.Listener)it.next()).selectionChanged(e);
        }
    }

    public Bounds getBounds() {
        if (this.bounds == null) {
            this.bounds = SelectionBase.computeBounds(this.unionSet);
        }
        return this.bounds;
    }

    public Bounds getBounds(Graphics g) {
        if (this.unionSet.isEmpty()) {
            this.bounds = Bounds.EMPTY_BOUNDS;
        } else {
            Iterator it = this.unionSet.iterator();
            this.bounds = ((Component)it.next()).getBounds(g);
            while (it.hasNext()) {
                Component comp = (Component)it.next();
                Bounds bds = comp.getBounds(g);
                this.bounds = this.bounds.add(bds);
            }
        }
        return this.bounds;
    }

    public boolean shouldSnap() {
        return this.shouldSnap;
    }

    public boolean hasConflictWhenMoved(int dx, int dy) {
        return this.hasConflictTranslated(this.unionSet, dx, dy, false);
    }

    public void add(Component comp) {
        if (this.selected.add(comp)) {
            if (SelectionBase.shouldSnapComponent(comp)) {
                this.shouldSnap = true;
            }
            this.bounds = null;
            this.fireSelectionChanged();
        }
    }

    public void addAll(Collection comps) {
        if (this.selected.addAll(comps)) {
            this.computeShouldSnap();
            this.bounds = null;
            this.fireSelectionChanged();
        }
    }

    public void remove(Component comp) {
        boolean removed = this.selected.remove(comp);
        if (this.lifted.remove(comp)) {
            removed = true;
            ComponentAction addAction = CircuitActions.addComponent(this.proj.getCurrentCircuit(), comp, false);
            addAction.doIt(this.proj);
            if (this.dropped != null) {
                this.dropped = this.dropped.append(addAction);
            }
        }
        if (removed) {
            if (SelectionBase.shouldSnapComponent(comp)) {
                this.computeShouldSnap();
            }
            this.bounds = null;
            this.fireSelectionChanged();
        }
    }

    public void dropAll() {
        if (!this.lifted.isEmpty()) {
            ComponentAction action = CircuitActions.addComponents(this.proj.getCurrentCircuit(), this.lifted);
            action.doIt(this.proj);
            if (this.dropped != null) {
                this.dropped = this.dropped.append(action);
            }
            this.selected.addAll(action.getAdditions());
            this.lifted.clear();
        }
    }

    public void clear() {
        this.clear(true);
    }

    public void clear(boolean dropLifted) {
        if (this.selected.isEmpty() && this.lifted.isEmpty()) {
            return;
        }
        if (dropLifted && !this.lifted.isEmpty()) {
            ComponentAction action = CircuitActions.addComponents(this.proj.getCurrentCircuit(), this.lifted);
            action.doIt(this.proj);
            if (this.dropped != null) {
                this.dropped = this.dropped.append(action);
            }
        }
        this.selected.clear();
        this.lifted.clear();
        this.shouldSnap = false;
        this.bounds = Bounds.EMPTY_BOUNDS;
        this.fireSelectionChanged();
    }

    public Action paste(final Clipboard clipboard) {
        return new Action(){
            private HashSet oldSelected;
            private Action oldDropped;
            private HashMap componentCopies;
            private AttributeSet oldOldAttributeSet;
            private AttributeSet oldAttributeSet;
            private Circuit oldAttributeCircuit;
            private Component oldAttributeComponent;

            @Override
            public String getName() {
                return Strings.get("pasteClipboardAction");
            }

            @Override
            public void doIt(Project proj) {
                AttributeSet attrs;
                AttributeTable attrTable = proj.getFrame().getAttributeTable();
                AttributeSet attrsCur = attrTable.getAttributeSet();
                this.oldOldAttributeSet = null;
                this.oldAttributeSet = attrTable.getAttributeSet();
                this.oldAttributeCircuit = proj.getCurrentCircuit();
                this.oldAttributeComponent = proj.getFrame().getCanvas().getHaloedComponent();
                AttributeSet attrsInter = null;
                Component compInter = null;
                if (attrsCur == clipboard.getOldAttributeSet() || attrsCur == null) {
                    Collection clipSel = clipboard.getComponents();
                    attrsInter = clipboard.getNewAttributeSet();
                    for (Component c : clipSel) {
                        if (c.getAttributeSet() != attrsInter) continue;
                        compInter = c;
                    }
                }
                this.oldSelected = new HashSet(SelectionBase.this.selected);
                this.oldSelected.addAll(SelectionBase.this.lifted);
                SelectionBase.this.clear();
                this.oldDropped = SelectionBase.this.dropped;
                this.componentCopies = SelectionBase.this.copyComponents(clipboard.getComponents());
                SelectionBase.this.restore(Collections.EMPTY_SET, this.componentCopies.values(), new ComponentAction(proj.getCurrentCircuit()));
                SelectionBase.this.fireSelectionChanged();
                Component attrComp = null;
                if (compInter != null) {
                    attrComp = (Component)this.componentCopies.get(compInter);
                }
                if (attrComp == null && proj != null && (attrs = attrTable.getAttributeSet()) == null) {
                    if (!SelectionBase.this.selected.isEmpty()) {
                        attrComp = (Component)SelectionBase.this.selected.iterator().next();
                    } else if (!SelectionBase.this.lifted.isEmpty()) {
                        attrComp = (Component)SelectionBase.this.lifted.iterator().next();
                    }
                }
                if (attrComp != null) {
                    attrs = attrComp.getAttributeSet();
                    this.oldOldAttributeSet = clipboard.getOldAttributeSet();
                    clipboard.setOldAttributeSet(attrs);
                    attrTable.setAttributeSet(attrs);
                    proj.getFrame().getCanvas().setHaloedComponent(proj.getCurrentCircuit(), attrComp);
                }
            }

            @Override
            public void undo(Project proj) {
                if (proj != null) {
                    proj.getFrame().getAttributeTable().setAttributeSet(this.oldAttributeSet);
                    proj.getFrame().getCanvas().setHaloedComponent(this.oldAttributeCircuit, this.oldAttributeComponent);
                }
                if (this.oldOldAttributeSet != null) {
                    clipboard.setOldAttributeSet(this.oldOldAttributeSet);
                }
                SelectionBase.this.dropped.undo(proj);
                SelectionBase.this.restore(this.oldSelected, Collections.EMPTY_SET, this.oldDropped);
                SelectionBase.this.fireSelectionChanged();
            }
        };
    }

    public Action deleteAll() {
        return new Action(){
            private HashSet oldSelected;
            private HashSet oldLifted;
            private Action oldDropped;
            private Action deleteAction;

            @Override
            public String getName() {
                return Strings.get("clearSelectionAction");
            }

            @Override
            public void doIt(Project proj) {
                SelectionBase.this.restore(Collections.EMPTY_SET, Collections.EMPTY_SET, null);
                this.deleteAction = CircuitActions.removeComponents(proj.getCurrentCircuit(), this.oldSelected);
                this.deleteAction.doIt(proj);
                SelectionBase.this.fireSelectionChanged();
            }

            @Override
            public void undo(Project proj) {
                this.deleteAction.undo(proj);
                SelectionBase.this.restore(this.oldSelected, this.oldLifted, this.oldDropped);
                SelectionBase.this.fireSelectionChanged();
                SelectionBase.this.fireSelectionChanged();
            }
        };
    }

    public Action translateAll(final int dx, final int dy) {
        return new Action(){
            private HashSet oldSelected;
            private HashSet oldLifted;
            private Action oldDropped;
            private HashMap oldState;
            private Component oldAttrsComp;
            private Component newAttrsComp;
            private Action deleteAction;
            private ComponentAction addSelectedAction;
            private ComponentAction addLiftedAction;

            @Override
            public String getName() {
                return Strings.get("moveSelectionAction");
            }

            @Override
            public void doIt(Project proj) {
                HashMap selectedAfter = SelectionBase.this.copyComponents(SelectionBase.this.selected, dx, dy);
                HashMap liftedAfter = SelectionBase.this.copyComponents(SelectionBase.this.lifted, dx, dy);
                Circuit circuit = proj.getCurrentCircuit();
                CircuitState circState = proj.getCircuitState();
                AttributeTable attrTable = proj.getFrame().getAttributeTable();
                AttributeSet oldAttrs = attrTable.getAttributeSet();
                Component oldAttrsComp = null;
                Component newAttrsComp = null;
                if (oldAttrs != null) {
                    for (Component comp2 : selectedAfter.keySet()) {
                        if (comp2.getAttributeSet() != oldAttrs) continue;
                        oldAttrsComp = comp2;
                        newAttrsComp = (Component)selectedAfter.get(comp2);
                        break;
                    }
                    for (Component comp2 : liftedAfter.keySet()) {
                        if (comp2.getAttributeSet() != oldAttrs) continue;
                        oldAttrsComp = comp2;
                        newAttrsComp = (Component)liftedAfter.get(comp2);
                        break;
                    }
                }
                if (circState != null) {
                    for (Component comp2 : SelectionBase.this.selected) {
                        Object compState = circState.getData(comp2);
                        if (compState == null) continue;
                        this.oldState.put(comp2, compState);
                    }
                }
                SelectionBase.this.restore(Collections.EMPTY_SET, Collections.EMPTY_SET, null);
                this.deleteAction = CircuitActions.removeComponents(circuit, this.oldSelected);
                this.deleteAction.doIt(proj);
                this.addSelectedAction = CircuitActions.addComponents(circuit, WireUtil.mergeExclusive(selectedAfter.values()));
                this.addSelectedAction.doIt(proj);
                for (Component oldComp : this.oldState.keySet()) {
                    Component newComp = (Component)selectedAfter.get(oldComp);
                    Object state = this.oldState.get(oldComp);
                    circState.setData(newComp, state);
                }
                SelectionBase.this.restore(this.addSelectedAction.getAdditions(), Collections.EMPTY_SET, null);
                this.addLiftedAction = CircuitActions.addComponents(circuit, WireUtil.mergeExclusive(liftedAfter.values()));
                this.addLiftedAction.doIt(proj);
                SelectionBase.this.selected.addAll(this.addLiftedAction.getAdditions());
                SelectionBase.this.fireSelectionChanged();
                if (newAttrsComp != null) {
                    this.oldAttrsComp = oldAttrsComp;
                    this.newAttrsComp = newAttrsComp;
                    proj.getFrame().getCanvas().setHaloedComponent(proj.getCurrentCircuit(), newAttrsComp);
                    attrTable.setAttributeSet(newAttrsComp.getAttributeSet());
                    Clipboard clip = Clipboard.get();
                    if (clip != null && clip.getOldAttributeSet() == oldAttrsComp.getAttributeSet()) {
                        clip.setOldAttributeSet(newAttrsComp.getAttributeSet());
                    }
                }
                SelectionBase.this.computeShouldSnap();
            }

            @Override
            public void undo(Project proj) {
                this.addLiftedAction.undo(proj);
                this.addSelectedAction.undo(proj);
                this.deleteAction.undo(proj);
                for (Component oldComp : this.oldState.keySet()) {
                    Object state = this.oldState.get(oldComp);
                    proj.getCircuitState().setData(oldComp, state);
                }
                SelectionBase.this.restore(this.oldSelected, this.oldLifted, this.oldDropped);
                SelectionBase.this.fireSelectionChanged();
                if (this.oldAttrsComp != null) {
                    proj.getFrame().getCanvas().setHaloedComponent(proj.getCurrentCircuit(), this.oldAttrsComp);
                    proj.getFrame().getAttributeTable().setAttributeSet(this.oldAttrsComp.getAttributeSet());
                    Clipboard clip = Clipboard.get();
                    if (clip != null && clip.getOldAttributeSet() == this.newAttrsComp.getAttributeSet()) {
                        clip.setOldAttributeSet(this.oldAttrsComp.getAttributeSet());
                    }
                }
                SelectionBase.this.computeShouldSnap();
            }
        };
    }

    private void restore(Collection oldSelected, Collection oldLifted, Action oldDropped) {
        this.selected.clear();
        this.selected.addAll(oldSelected);
        this.lifted.clear();
        this.lifted.addAll(oldLifted);
        this.dropped = oldDropped;
        this.bounds = null;
        this.computeShouldSnap();
    }

    private void computeShouldSnap() {
        this.shouldSnap = false;
        for (Component comp : this.unionSet) {
            if (!SelectionBase.shouldSnapComponent(comp)) continue;
            this.shouldSnap = true;
            return;
        }
    }

    private static boolean shouldSnapComponent(Component comp) {
        Boolean shouldSnapValue = (Boolean)comp.getFactory().getFeature(ComponentFactory.SHOULD_SNAP, comp.getAttributeSet());
        return shouldSnapValue == null ? true : shouldSnapValue;
    }

    private boolean hasConflictTranslated(Collection components, int dx, int dy, boolean selfConflicts) {
        Circuit circuit = this.proj.getCurrentCircuit();
        if (circuit == null) {
            return false;
        }
        for (Object obj : components) {
            if (obj instanceof Wire) continue;
            Component comp = (Component)obj;
            for (EndData endData : comp.getEnds()) {
                Component conflict;
                Location endLoc;
                if (endData == null || !endData.isExclusive() || (conflict = circuit.getExclusive(endLoc = endData.getLocation().translate(dx, dy))) == null || !selfConflicts && components.contains(conflict)) continue;
                return true;
            }
        }
        return false;
    }

    private static Bounds computeBounds(Collection components) {
        if (components.isEmpty()) {
            return Bounds.EMPTY_BOUNDS;
        }
        Iterator it = components.iterator();
        Bounds ret = ((Component)it.next()).getBounds();
        while (it.hasNext()) {
            Component comp = (Component)it.next();
            Bounds bds = comp.getBounds();
            ret = ret.add(bds);
        }
        return ret;
    }

    private HashMap copyComponents(Collection components) {
        Bounds bds = SelectionBase.computeBounds(components);
        int index = 0;
        do {
            int dx;
            int dy;
            if (index == 0) {
                dx = 0;
                dy = 0;
            } else {
                int side = 1;
                while (side * side <= index) {
                    side += 2;
                }
                int offs = index - (side - 2) * (side - 2);
                dx = side / 2;
                dy = side / 2;
                if (offs < side - 1) {
                    dx -= offs;
                } else if (offs < 2 * (side - 1)) {
                    dx = - dx;
                    dy -= (offs -= side - 1);
                } else if (offs < 3 * (side - 1)) {
                    dx = - dx + (offs -= 2 * (side - 1));
                    dy = - dy;
                } else {
                    dy = - dy + (offs -= 3 * (side - 1));
                }
                dx *= 10;
                dy *= 10;
            }
            if (bds.getX() + dx >= 0 && bds.getY() + dy >= 0 && !this.hasConflictTranslated(components, dx, dy, true)) {
                return this.copyComponents(components, dx, dy);
            }
            ++index;
        } while (true);
    }

    private HashMap copyComponents(Collection components, int dx, int dy) {
        HashMap<Component, Component> ret = new HashMap<Component, Component>();
        for (Component comp : components) {
            Component copy = comp.getFactory().createComponent(comp.getLocation().translate(dx, dy), (AttributeSet)comp.getAttributeSet().clone());
            ret.put(comp, copy);
        }
        return ret;
    }

    public void print() {
        System.err.println(" shouldSnap: " + this.shouldSnap());
        boolean hasPrinted = false;
        for (Component comp2 : this.selected) {
            System.err.println((hasPrinted ? "         " : " select: ") + comp2 + "  [" + comp2.hashCode() + "]");
            hasPrinted = true;
        }
        hasPrinted = false;
        for (Component comp2 : this.lifted) {
            System.err.println((hasPrinted ? "         " : " lifted: ") + comp2 + "  [" + comp2.hashCode() + "]");
            hasPrinted = true;
        }
    }

}

