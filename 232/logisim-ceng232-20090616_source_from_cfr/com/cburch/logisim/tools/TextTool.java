/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.tools;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitActions;
import com.cburch.logisim.circuit.CircuitEvent;
import com.cburch.logisim.circuit.CircuitListener;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.main.Selection;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.std.Text;
import com.cburch.logisim.std.TextClass;
import com.cburch.logisim.tools.Caret;
import com.cburch.logisim.tools.CaretEvent;
import com.cburch.logisim.tools.CaretListener;
import com.cburch.logisim.tools.Strings;
import com.cburch.logisim.tools.TextEditable;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.StringGetter;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;

public class TextTool
extends Tool {
    private static Cursor cursor = Cursor.getPredefinedCursor(2);
    private MyListener listener;
    private AttributeSet attrs;
    private Canvas caret_canvas;
    private Circuit caret_circ;
    private Component caret_comp;
    private Caret caret;
    private boolean caret_comp_created;

    public TextTool() {
        this.listener = new MyListener();
        this.caret_canvas = null;
        this.caret_circ = null;
        this.caret_comp = null;
        this.caret = null;
        this.attrs = TextClass.instance.createAttributeSet();
    }

    public boolean equals(Object other) {
        return other instanceof TextTool;
    }

    public int hashCode() {
        return TextTool.class.hashCode();
    }

    @Override
    public String getName() {
        return "Text Tool";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("textTool");
    }

    @Override
    public String getDescription() {
        return Strings.get("textToolDesc");
    }

    @Override
    public AttributeSet getAttributeSet() {
        return this.attrs;
    }

    @Override
    public void paintIcon(ComponentDrawContext c, int x, int y) {
        TextClass.instance.paintIcon(c, x, y, null);
    }

    @Override
    public void draw(Canvas canvas, ComponentDrawContext context) {
        if (this.caret != null) {
            this.caret.draw(context.getGraphics());
        }
    }

    @Override
    public void deselect(Canvas canvas) {
        if (this.caret != null) {
            this.caret.stopEditing();
            this.caret = null;
        }
    }

    @Override
    public void mousePressed(Canvas canvas, Graphics g, MouseEvent e) {
        TextEditable editable;
        Project proj = canvas.getProject();
        Circuit circ = canvas.getCircuit();
        if (!proj.getLogisimFile().contains(circ)) {
            if (this.caret != null) {
                this.caret.cancelEditing();
            }
            canvas.setErrorMessage(Strings.getter("cannotModifyError"));
            return;
        }
        if (this.caret != null) {
            if (this.caret.getBounds(g).contains(e.getX(), e.getY())) {
                this.caret.mousePressed(e);
                proj.repaintCanvas();
                return;
            }
            this.caret.stopEditing();
        }
        int x = e.getX();
        int y = e.getY();
        Location loc = Location.create(x, y);
        ComponentUserEvent event = new ComponentUserEvent(canvas, x, y);
        for (Component comp2 : proj.getSelection().getComponentsContaining(loc, g)) {
            editable = (TextEditable)comp2.getFeature(TextEditable.class);
            if (editable == null) continue;
            this.caret = editable.getTextCaret(event);
            if (this.caret == null) continue;
            proj.getFrame().viewComponentAttributes(circ, comp2);
            this.caret_comp = comp2;
            this.caret_comp_created = false;
            break;
        }
        if (this.caret == null) {
            for (Component comp2 : circ.getAllContaining(loc, g)) {
                editable = (TextEditable)comp2.getFeature(TextEditable.class);
                if (editable == null) continue;
                this.caret = editable.getTextCaret(event);
                if (this.caret == null) continue;
                proj.getFrame().viewComponentAttributes(circ, comp2);
                this.caret_comp = comp2;
                this.caret_comp_created = false;
                break;
            }
        }
        if (this.caret == null) {
            if (loc.getX() < 0 || loc.getY() < 0) {
                return;
            }
            AttributeSet copy = (AttributeSet)this.attrs.clone();
            this.caret_comp = TextClass.instance.createComponent(loc, copy);
            this.caret_comp_created = true;
            editable = (TextEditable)this.caret_comp.getFeature(TextEditable.class);
            if (editable != null) {
                this.caret = editable.getTextCaret(event);
                proj.getFrame().viewComponentAttributes(circ, this.caret_comp);
            }
        }
        if (this.caret != null) {
            this.caret_canvas = canvas;
            this.caret_circ = canvas.getCircuit();
            this.caret.addCaretListener(this.listener);
            this.caret_circ.addCircuitListener(this.listener);
        }
        proj.repaintCanvas();
    }

    @Override
    public void mouseDragged(Canvas canvas, Graphics g, MouseEvent e) {
    }

    @Override
    public void mouseReleased(Canvas canvas, Graphics g, MouseEvent e) {
    }

    @Override
    public void keyPressed(Canvas canvas, KeyEvent e) {
        if (this.caret != null) {
            this.caret.keyPressed(e);
            canvas.getProject().repaintCanvas();
        }
    }

    @Override
    public void keyReleased(Canvas canvas, KeyEvent e) {
        if (this.caret != null) {
            this.caret.keyReleased(e);
            canvas.getProject().repaintCanvas();
        }
    }

    @Override
    public void keyTyped(Canvas canvas, KeyEvent e) {
        if (this.caret != null) {
            this.caret.keyTyped(e);
            canvas.getProject().repaintCanvas();
        }
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }

    private class MyListener
    implements CaretListener,
    CircuitListener {
        private MyListener() {
        }

        @Override
        public void editingCanceled(CaretEvent e) {
            if (e.getCaret() != TextTool.this.caret) {
                e.getCaret().removeCaretListener(this);
                return;
            }
            TextTool.this.caret.removeCaretListener(this);
            TextTool.this.caret_circ.removeCircuitListener(this);
            TextTool.this.caret_circ = null;
            TextTool.this.caret_comp = null;
            TextTool.this.caret_comp_created = false;
            TextTool.this.caret = null;
        }

        @Override
        public void editingStopped(CaretEvent e) {
            boolean is_null;
            if (e.getCaret() != TextTool.this.caret) {
                e.getCaret().removeCaretListener(this);
                return;
            }
            TextTool.this.caret.removeCaretListener(this);
            TextTool.this.caret_circ.removeCircuitListener(this);
            String val = TextTool.this.caret.getText();
            boolean bl = is_null = val == null || val.equals("");
            Action a = TextTool.this.caret_comp_created ? (!is_null ? CircuitActions.addComponent(TextTool.this.caret_circ, TextTool.this.caret_comp, false) : null) : (is_null && TextTool.this.caret_comp instanceof Text ? CircuitActions.removeComponent(TextTool.this.caret_circ, TextTool.this.caret_comp) : new TextChangedAction(TextTool.this.caret_circ, TextTool.this.caret_comp, TextTool.this.caret, e.getOldText(), e.getText()));
            TextTool.this.caret_circ = null;
            TextTool.this.caret_comp = null;
            TextTool.this.caret_comp_created = false;
            TextTool.this.caret = null;
            if (a != null) {
                TextTool.this.caret_canvas.getProject().doAction(a);
            }
        }

        @Override
        public void circuitChanged(CircuitEvent event) {
            if (event.getCircuit() != TextTool.this.caret_circ) {
                event.getCircuit().removeCircuitListener(this);
                return;
            }
            int action = event.getAction();
            if (action == 2) {
                if (event.getData() == TextTool.this.caret_comp) {
                    TextTool.this.caret.cancelEditing();
                }
            } else if (action == 5 && TextTool.this.caret_comp != null) {
                TextTool.this.caret.cancelEditing();
            }
        }
    }

    private static class TextChangedAction
    extends Action {
        private Circuit circ;
        private Component comp;
        private Caret caret;
        private String oldstr;
        private String newstr;

        TextChangedAction(Circuit circ, Component comp, Caret caret, String oldstr, String newstr) {
            this.circ = circ;
            this.caret = caret;
            this.comp = comp;
            this.oldstr = oldstr;
            this.newstr = newstr;
        }

        @Override
        public String getName() {
            return Strings.get("changeTextAction");
        }

        @Override
        public void doIt(Project proj) {
            this.caret.commitText(this.newstr);
            this.circ.componentChanged(this.comp);
        }

        @Override
        public void undo(Project proj) {
            this.caret.commitText(this.oldstr);
            this.circ.componentChanged(this.comp);
        }
    }

}

