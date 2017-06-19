/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitActions;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.gui.main.AttributeTable;
import com.cburch.logisim.gui.main.Clipboard;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.main.Selection;
import com.cburch.logisim.gui.main.Strings;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import java.util.Collection;
import java.util.HashSet;

public class SelectionActions {
    private SelectionActions() {
    }

    public static Action move(int dx, int dy, Collection connectPoints) {
        return new Move(dx, dy, connectPoints);
    }

    public static Action clear() {
        return new Clear();
    }

    public static Action cut() {
        return new Cut();
    }

    public static Action copy() {
        return new Copy();
    }

    public static Action paste() {
        return new Paste();
    }

    private static class Paste
    extends Action {
        Action pasteAction;

        Paste() {
        }

        @Override
        public String getName() {
            return Strings.get("pasteClipboardAction");
        }

        @Override
        public void doIt(Project proj) {
            Clipboard clip = Clipboard.get();
            this.pasteAction = proj.getSelection().paste(clip);
            this.pasteAction.doIt(proj);
        }

        @Override
        public void undo(Project proj) {
            this.pasteAction.undo(proj);
        }
    }

    private static class Copy
    extends Action {
        Clipboard oldClip;

        Copy() {
        }

        @Override
        public boolean isModification() {
            return false;
        }

        @Override
        public String getName() {
            return Strings.get("copySelectionAction");
        }

        @Override
        public void doIt(Project proj) {
            this.oldClip = Clipboard.get();
            Clipboard.set(proj.getSelection(), proj.getFrame().getAttributeTable().getAttributeSet());
        }

        @Override
        public void undo(Project proj) {
            Clipboard.set(this.oldClip);
        }
    }

    private static class Cut
    extends Action {
        Action first = new Copy();
        Action second = new Clear();

        Cut() {
        }

        @Override
        public String getName() {
            return Strings.get("cutSelectionAction");
        }

        @Override
        public void doIt(Project proj) {
            this.first.doIt(proj);
            this.second.doIt(proj);
        }

        @Override
        public void undo(Project proj) {
            this.second.undo(proj);
            this.first.undo(proj);
        }
    }

    private static class Clear
    extends Action {
        Action clearAction;

        Clear() {
        }

        @Override
        public String getName() {
            return Strings.get("clearSelectionAction");
        }

        @Override
        public void doIt(Project proj) {
            this.clearAction = proj.getSelection().deleteAll();
            this.clearAction.doIt(proj);
        }

        @Override
        public void undo(Project proj) {
            this.clearAction.undo(proj);
        }
    }

    private static class Move
    extends Action {
        Action moveAction;
        Action wiresRemove;
        Action wiresAdd;
        int dx;
        int dy;
        Collection connectPoints;

        Move(int dx, int dy, Collection connectPoints) {
            this.dx = dx;
            this.dy = dy;
            if (dx == 0 && dy != 0 || dx != 0 && dy == 0) {
                this.connectPoints = connectPoints;
            }
        }

        @Override
        public String getName() {
            return Strings.get("moveSelectionAction");
        }

        @Override
        public void doIt(Project proj) {
            this.moveAction = proj.getSelection().translateAll(this.dx, this.dy);
            this.moveAction.doIt(proj);
            if (this.connectPoints != null && !this.connectPoints.isEmpty()) {
                Circuit circ = proj.getCurrentCircuit();
                HashSet<Object> removals = new HashSet<Object>();
                HashSet<Wire> additions = new HashSet<Wire>();
                for (Location loc : this.connectPoints) {
                    Wire w;
                    Wire removal = null;
                    for (Object obj : circ.getComponents(loc)) {
                        Wire w2;
                        if (!(obj instanceof Wire) || (w2 = (Wire)obj).isVertical() != (this.dx == 0)) continue;
                        if (w2.isVertical()) {
                            if (w2.getOtherEnd(loc).getY() < loc.getY() != this.dy < 0) continue;
                            removal = w2;
                            continue;
                        }
                        if (w2.getOtherEnd(loc).getX() < loc.getX() != this.dx < 0) continue;
                        removal = w2;
                    }
                    if (removal == null) {
                        w = Wire.create(loc, loc.translate(this.dx, this.dy));
                        additions.add(w);
                        continue;
                    }
                    removals.add(removal);
                    w = Wire.create(removal.getOtherEnd(loc), loc.translate(this.dx, this.dy));
                    additions.add(w);
                }
                if (!removals.isEmpty()) {
                    this.wiresRemove = CircuitActions.removeComponents(circ, removals);
                    this.wiresRemove.doIt(proj);
                }
                this.wiresAdd = CircuitActions.addComponents(circ, additions);
                this.wiresAdd.doIt(proj);
            }
        }

        @Override
        public void undo(Project proj) {
            if (this.wiresAdd != null) {
                this.wiresAdd.undo(proj);
                this.wiresAdd = null;
            }
            if (this.wiresRemove != null) {
                this.wiresRemove.undo(proj);
                this.wiresRemove = null;
            }
            this.moveAction.undo(proj);
        }
    }

}

