/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.tools;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitActions;
import com.cburch.logisim.circuit.CircuitException;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Dependencies;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.Strings;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.StringGetter;
import com.cburch.logisim.util.StringUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;

public class AddTool
extends Tool {
    private static int SHOW_NONE = 0;
    private static int SHOW_GHOST = 1;
    private static int SHOW_ADD = 2;
    private static int SHOW_ADD_NO = 3;
    private static Cursor cursor = Cursor.getPredefinedCursor(1);
    private ComponentFactory source;
    private AttributeSet attrs;
    private Bounds bounds;
    private boolean should_snap;
    private int last_x = Integer.MIN_VALUE;
    private int last_y = Integer.MIN_VALUE;
    private int state = SHOW_GHOST;
    private Action lastAddition;

    public AddTool(ComponentFactory source) {
        this(source, source.createAttributeSet());
    }

    private AddTool(AddTool base) {
        this(base.source, (AttributeSet)base.attrs.clone());
    }

    private AddTool(ComponentFactory source, AttributeSet attrs) {
        this.source = source;
        this.attrs = attrs;
        this.bounds = source.getOffsetBounds(attrs).expand(5);
        attrs.addAttributeListener(new MyAttributeListener());
        Boolean value = (Boolean)source.getFeature(ComponentFactory.SHOULD_SNAP, attrs);
        this.should_snap = value == null ? true : value;
    }

    public boolean equals(Object other) {
        return other instanceof AddTool && this.source.equals(((AddTool)other).source);
    }

    public int hashCode() {
        return this.source.hashCode();
    }

    @Override
    public boolean sharesSource(Tool other) {
        if (!(other instanceof AddTool)) {
            return false;
        }
        AddTool o = (AddTool)other;
        return this.source.equals(o.source);
    }

    public ComponentFactory getFactory() {
        return this.source;
    }

    @Override
    public String getName() {
        return this.source.getName();
    }

    @Override
    public String getDisplayName() {
        return this.source.getDisplayName();
    }

    @Override
    public String getDescription() {
        String ret = (String)this.source.getFeature(ComponentFactory.TOOL_TIP, this.attrs);
        if (ret == null) {
            ret = StringUtil.format(Strings.get("addToolText"), this.source.getDisplayName());
        }
        return ret;
    }

    @Override
    public Tool cloneTool() {
        return new AddTool(this);
    }

    @Override
    public AttributeSet getAttributeSet() {
        return this.attrs;
    }

    @Override
    public void draw(Canvas canvas, ComponentDrawContext context) {
        if (this.state == SHOW_GHOST) {
            this.source.drawGhost(context, Color.GRAY, this.last_x, this.last_y, this.attrs);
        } else if (this.state == SHOW_ADD) {
            this.source.drawGhost(context, Color.BLACK, this.last_x, this.last_y, this.attrs);
        }
    }

    public void cancelOp() {
    }

    @Override
    public void select(Canvas canvas) {
        this.setState(canvas, SHOW_GHOST);
        this.recomputeBounds();
    }

    @Override
    public void deselect(Canvas canvas) {
        this.setState(canvas, SHOW_GHOST);
        this.moveTo(canvas, canvas.getGraphics(), Integer.MAX_VALUE, Integer.MAX_VALUE);
        this.recomputeBounds();
        this.lastAddition = null;
    }

    private synchronized void moveTo(Canvas canvas, Graphics g, int x, int y) {
        if (this.state != SHOW_NONE) {
            this.expose(canvas, this.last_x, this.last_y);
        }
        this.last_x = x;
        this.last_y = y;
        if (this.state != SHOW_NONE) {
            this.expose(canvas, this.last_x, this.last_y);
        }
    }

    @Override
    public void mouseEntered(Canvas canvas, Graphics g, MouseEvent e) {
        if (this.state == SHOW_GHOST || this.state == SHOW_NONE) {
            this.setState(canvas, SHOW_GHOST);
            canvas.grabFocus();
        } else if (this.state == SHOW_ADD_NO) {
            this.setState(canvas, SHOW_ADD);
            canvas.grabFocus();
        }
    }

    @Override
    public void mouseExited(Canvas canvas, Graphics g, MouseEvent e) {
        if (this.state == SHOW_GHOST) {
            this.moveTo(canvas, canvas.getGraphics(), Integer.MAX_VALUE, Integer.MAX_VALUE);
            this.setState(canvas, SHOW_NONE);
        } else if (this.state == SHOW_ADD) {
            this.moveTo(canvas, canvas.getGraphics(), Integer.MAX_VALUE, Integer.MAX_VALUE);
            this.setState(canvas, SHOW_ADD_NO);
        }
    }

    @Override
    public void mouseMoved(Canvas canvas, Graphics g, MouseEvent e) {
        if (this.state != SHOW_NONE) {
            if (this.should_snap) {
                Canvas.snapToGrid(e);
            }
            this.moveTo(canvas, g, e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(Canvas canvas, Graphics g, MouseEvent e) {
        Circuit circ = canvas.getCircuit();
        if (!canvas.getProject().getLogisimFile().contains(circ)) {
            canvas.setErrorMessage(Strings.getter("cannotModifyError"));
            return;
        }
        Dependencies depends = canvas.getProject().getDependencies();
        if (this.source instanceof Circuit && !depends.canAdd(circ, (Circuit)this.source)) {
            canvas.setErrorMessage(Strings.getter("circularError"));
            return;
        }
        if (this.should_snap) {
            Canvas.snapToGrid(e);
        }
        this.moveTo(canvas, g, e.getX(), e.getY());
        this.setState(canvas, SHOW_ADD);
    }

    @Override
    public void mouseDragged(Canvas canvas, Graphics g, MouseEvent e) {
        if (this.state != SHOW_NONE) {
            if (this.should_snap) {
                Canvas.snapToGrid(e);
            }
            this.moveTo(canvas, g, e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(Canvas canvas, Graphics g, MouseEvent e) {
        if (this.state == SHOW_ADD) {
            Circuit circ = canvas.getCircuit();
            if (!canvas.getProject().getLogisimFile().contains(circ)) {
                return;
            }
            if (this.should_snap) {
                Canvas.snapToGrid(e);
            }
            this.moveTo(canvas, g, e.getX(), e.getY());
            Location loc = Location.create(e.getX(), e.getY());
            AttributeSet attrs_copy = (AttributeSet)this.attrs.clone();
            com.cburch.logisim.comp.Component c = this.source.createComponent(loc, attrs_copy);
            if (circ.hasConflict(c)) {
                canvas.setErrorMessage(Strings.getter("exclusiveError"));
                return;
            }
            Bounds bds = c.getBounds(g);
            if (bds.getX() < 0 || bds.getY() < 0) {
                canvas.setErrorMessage(Strings.getter("negativeCoordError"));
                return;
            }
            try {
                this.lastAddition = CircuitActions.addComponent(circ, c, false);
                canvas.getProject().doAction(this.lastAddition);
            }
            catch (CircuitException ex) {
                JOptionPane.showMessageDialog(canvas.getProject().getFrame(), ex.getMessage());
            }
            this.setState(canvas, SHOW_GHOST);
        } else if (this.state == SHOW_ADD_NO) {
            this.setState(canvas, SHOW_NONE);
        }
    }

    @Override
    public void keyPressed(Canvas canvas, KeyEvent event) {
        Attribute attr;
        Direction facing = null;
        switch (event.getKeyCode()) {
            case 38: {
                facing = Direction.NORTH;
                break;
            }
            case 40: {
                facing = Direction.SOUTH;
                break;
            }
            case 37: {
                facing = Direction.WEST;
                break;
            }
            case 39: {
                facing = Direction.EAST;
                break;
            }
            case 8: {
                if (this.lastAddition == null || canvas.getProject().getLastAction() != this.lastAddition) break;
                canvas.getProject().undoAction();
                this.lastAddition = null;
            }
        }
        if (facing != null && (attr = (Attribute)this.source.getFeature(ComponentFactory.FACING_ATTRIBUTE_KEY, this.attrs)) != null) {
            this.attrs.setValue(attr, facing);
            canvas.repaint();
        }
    }

    @Override
    public void paintIcon(ComponentDrawContext c, int x, int y) {
        this.source.paintIcon(c, x, y, this.attrs);
    }

    private void expose(Component c, int x, int y) {
        Bounds bds = this.bounds;
        c.repaint(x + bds.getX(), y + bds.getY(), bds.getWidth(), bds.getHeight());
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }

    private void setState(Canvas canvas, int value) {
        this.state = value == SHOW_GHOST ? (canvas.getShowGhosts() && canvas.getProject().getLogisimFile().contains(canvas.getCircuit()) ? SHOW_GHOST : SHOW_NONE) : value;
    }

    private void recomputeBounds() {
        this.bounds = this.source.getOffsetBounds(this.attrs).expand(5);
    }

    private class MyAttributeListener
    implements AttributeListener {
        private MyAttributeListener() {
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
            AddTool.this.recomputeBounds();
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            AddTool.this.recomputeBounds();
        }
    }

}

