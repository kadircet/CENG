/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.tools;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.EndData;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.main.Selection;
import com.cburch.logisim.gui.main.SelectionActions;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.LogisimPreferences;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.Strings;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import com.cburch.logisim.util.StringGetter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Icon;

public class SelectTool
extends Tool {
    private static final Cursor selectCursor = Cursor.getPredefinedCursor(0);
    private static final Cursor rectSelectCursor = Cursor.getPredefinedCursor(1);
    private static final Cursor moveCursor = Cursor.getPredefinedCursor(13);
    private static final int IDLE = 0;
    private static final int MOVING = 1;
    private static final int RECT_SELECT = 2;
    private static final Icon toolIcon = Icons.getIcon("select.gif");
    private Location start = null;
    private int state = 0;
    private int cur_dx;
    private int cur_dy;
    private Set connectPoints;

    public boolean equals(Object other) {
        return other instanceof SelectTool;
    }

    public int hashCode() {
        return SelectTool.class.hashCode();
    }

    @Override
    public String getName() {
        return "Select Tool";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("selectTool");
    }

    @Override
    public String getDescription() {
        return Strings.get("selectToolDesc");
    }

    @Override
    public void draw(Canvas canvas, ComponentDrawContext context) {
        Project proj = canvas.getProject();
        if (this.state == 1) {
            if (this.connectPoints != null && (this.cur_dx != 0 && this.cur_dy == 0 || this.cur_dx == 0 && this.cur_dy != 0)) {
                Graphics g = context.getGraphics();
                g.setColor(Color.gray);
                GraphicsUtil.switchToWidth(g, 3);
                for (Location loc : this.connectPoints) {
                    int x = loc.getX();
                    int y = loc.getY();
                    g.drawLine(x, y, x + this.cur_dx, y + this.cur_dy);
                }
                GraphicsUtil.switchToWidth(g, 1);
            }
            proj.getSelection().drawGhostsShifted(context, this.cur_dx, this.cur_dy);
        } else if (this.state == 2) {
            int right;
            int top;
            int bot;
            int left = this.start.getX();
            if (left > (right = left + this.cur_dx)) {
                int i = left;
                left = right;
                right = i;
            }
            if ((top = this.start.getY()) > (bot = top + this.cur_dy)) {
                int i = top;
                top = bot;
                bot = i;
            }
            Graphics g = context.getGraphics();
            g.setColor(Color.gray);
            g.drawRect(left, top, right - left, bot - top);
        }
    }

    @Override
    public void mousePressed(Canvas canvas, Graphics g, MouseEvent e) {
        Collection clicked;
        Project proj = canvas.getProject();
        Selection sel = proj.getSelection();
        Circuit circuit = canvas.getCircuit();
        this.start = Location.create(e.getX(), e.getY());
        this.cur_dx = 0;
        this.cur_dy = 0;
        Collection in_sel = sel.getComponentsContaining(this.start, g);
        if (!in_sel.isEmpty()) {
            if ((e.getModifiers() & 1) == 0) {
                this.setState(proj, 1);
                proj.repaintCanvas();
                return;
            }
            for (com.cburch.logisim.comp.Component comp : in_sel) {
                sel.remove(comp);
            }
        }
        if (!(clicked = circuit.getAllContaining(this.start, g)).isEmpty()) {
            if ((e.getModifiers() & 1) == 0 && sel.getComponentsContaining(this.start).isEmpty()) {
                sel.clear();
            }
            boolean isFirst = true;
            for (com.cburch.logisim.comp.Component comp : clicked) {
                AttributeSet attrs;
                if (!in_sel.contains(comp)) {
                    sel.add(comp);
                }
                if (!isFirst || (attrs = comp.getAttributeSet()) == null || attrs.getAttributes().size() <= 0) continue;
                isFirst = false;
                proj.getFrame().viewComponentAttributes(circuit, comp);
            }
            this.setState(proj, 1);
            proj.repaintCanvas();
            return;
        }
        if ((e.getModifiers() & 1) == 0) {
            sel.clear();
        }
        this.setState(proj, 2);
        proj.repaintCanvas();
    }

    @Override
    public void mouseDragged(Canvas canvas, Graphics g, MouseEvent e) {
        if (this.state == 1) {
            Project proj = canvas.getProject();
            this.computeDxDy(proj, e, g);
            if (this.connectPoints == null && (this.cur_dx != 0 || this.cur_dy != 0)) {
                this.connectPoints = this.computeConnectPoints(canvas);
            }
            proj.repaintCanvas();
        } else if (this.state == 2) {
            Project proj = canvas.getProject();
            this.cur_dx = e.getX() - this.start.getX();
            this.cur_dy = e.getY() - this.start.getY();
            proj.repaintCanvas();
        }
    }

    @Override
    public void mouseReleased(Canvas canvas, Graphics g, MouseEvent e) {
        Project proj = canvas.getProject();
        if (this.state == 1) {
            this.setState(proj, 0);
            this.computeDxDy(proj, e, g);
            if (this.cur_dx != 0 || this.cur_dy != 0) {
                if (!proj.getLogisimFile().contains(canvas.getCircuit())) {
                    canvas.setErrorMessage(Strings.getter("cannotModifyError"));
                } else if (proj.getSelection().hasConflictWhenMoved(this.cur_dx, this.cur_dy)) {
                    canvas.setErrorMessage(Strings.getter("exclusiveError"));
                } else {
                    if (!LogisimPreferences.getStretchWires()) {
                        this.connectPoints = null;
                    }
                    proj.doAction(SelectionActions.move(this.cur_dx, this.cur_dy, this.connectPoints));
                }
            }
            this.connectPoints = null;
            proj.repaintCanvas();
        } else if (this.state == 2) {
            Bounds bds = Bounds.create(this.start).add(this.start.getX() + this.cur_dx, this.start.getY() + this.cur_dy);
            Circuit circuit = canvas.getCircuit();
            Selection sel = proj.getSelection();
            Collection in_sel = sel.getComponentsWithin(bds, g);
            for (com.cburch.logisim.comp.Component comp2 : circuit.getAllWithin(bds, g)) {
                if (in_sel.contains(comp2)) continue;
                sel.add(comp2);
            }
            for (com.cburch.logisim.comp.Component comp2 : in_sel) {
                sel.remove(comp2);
            }
            this.setState(proj, 0);
            proj.repaintCanvas();
        }
    }

    private void computeDxDy(Project proj, MouseEvent e, Graphics g) {
        Bounds bds = proj.getSelection().getBounds(g);
        if (bds == Bounds.EMPTY_BOUNDS) {
            this.cur_dx = e.getX() - this.start.getX();
            this.cur_dy = e.getY() - this.start.getY();
        } else {
            this.cur_dx = Math.max(e.getX() - this.start.getX(), - bds.getX());
            this.cur_dy = Math.max(e.getY() - this.start.getY(), - bds.getY());
        }
        Selection sel = proj.getSelection();
        if (sel.shouldSnap()) {
            this.cur_dx = Canvas.snapXToGrid(this.cur_dx);
            this.cur_dy = Canvas.snapYToGrid(this.cur_dy);
        }
    }

    @Override
    public void paintIcon(ComponentDrawContext c, int x, int y) {
        Graphics g = c.getGraphics();
        if (toolIcon != null) {
            toolIcon.paintIcon(c.getDestination(), g, x + 2, y + 2);
        } else {
            int[] xp = new int[]{x + 5, x + 5, x + 9, x + 12, x + 14, x + 11, x + 16};
            int[] yp = new int[]{y, y + 17, y + 12, y + 18, y + 18, y + 12, y + 12};
            g.setColor(Color.black);
            g.fillPolygon(xp, yp, xp.length);
        }
    }

    @Override
    public Cursor getCursor() {
        return this.state == 0 ? selectCursor : (this.state == 2 ? rectSelectCursor : moveCursor);
    }

    private void setState(Project proj, int new_state) {
        if (this.state == new_state) {
            return;
        }
        this.state = new_state;
        proj.getSelection().setVisible(this.state != 1);
        proj.getFrame().getCanvas().setCursor(this.getCursor());
    }

    private Set computeConnectPoints(Canvas canvas) {
        if (!LogisimPreferences.getStretchWires()) {
            return null;
        }
        Selection sel = canvas.getProject().getSelection();
        if (sel == null) {
            return null;
        }
        Circuit circ = canvas.getCircuit();
        Collection anchored = sel.getAnchoredComponents();
        if (anchored == null || anchored.isEmpty()) {
            return null;
        }
        HashSet<Location> ret = new HashSet<Location>();
        for (com.cburch.logisim.comp.Component comp : anchored) {
            for (EndData end : comp.getEnds()) {
                Location loc = end.getLocation();
                for (com.cburch.logisim.comp.Component comp2 : circ.getComponents(loc)) {
                    if (anchored.contains(comp2)) continue;
                    ret.add(loc);
                }
            }
        }
        return ret.isEmpty() ? null : ret;
    }
}

