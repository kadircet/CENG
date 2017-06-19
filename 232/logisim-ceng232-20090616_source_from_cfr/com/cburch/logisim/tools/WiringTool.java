/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.tools;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitActions;
import com.cburch.logisim.circuit.ComponentAction;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.Strings;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.tools.WireRepair;
import com.cburch.logisim.tools.WireRepairData;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import com.cburch.logisim.util.StringGetter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import javax.swing.Icon;

public class WiringTool
extends Tool {
    private static Cursor cursor = Cursor.getPredefinedCursor(1);
    private static final Icon toolIcon = Icons.getIcon("wiring.gif");
    private boolean exists = false;
    private boolean inCanvas = false;
    private Location start = Location.create(0, 0);
    private Location cur = Location.create(0, 0);
    private boolean startShortening = false;
    private boolean shortening = false;
    private Action lastAction = null;

    public boolean equals(Object other) {
        return other instanceof WiringTool;
    }

    public int hashCode() {
        return WiringTool.class.hashCode();
    }

    @Override
    public String getName() {
        return "Wiring Tool";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("wiringTool");
    }

    @Override
    public String getDescription() {
        return Strings.get("wiringToolDesc");
    }

    private int findX(int new_x, int new_y) {
        int dist_x = Math.abs(new_x - this.start.getX());
        int dist_y = Math.abs(new_y - this.start.getY());
        if (dist_y > dist_x) {
            return this.start.getX();
        }
        return new_x;
    }

    private int findY(int new_x, int new_y) {
        int dist_x = Math.abs(new_x - this.start.getX());
        int dist_y = Math.abs(new_y - this.start.getY());
        if (dist_y > dist_x) {
            return new_y;
        }
        return this.start.getY();
    }

    @Override
    public void draw(Canvas canvas, ComponentDrawContext context) {
        Graphics g = context.getGraphics();
        if (this.exists) {
            g.setColor(this.shortening ? Color.WHITE : Color.BLACK);
            GraphicsUtil.switchToWidth(g, 3);
            g.drawLine(this.start.getX(), this.start.getY(), this.cur.getX(), this.cur.getY());
        } else if (canvas.getShowGhosts() && this.inCanvas) {
            g.setColor(Color.GRAY);
            g.fillOval(this.cur.getX() - 2, this.cur.getY() - 2, 5, 5);
        }
    }

    @Override
    public void mouseEntered(Canvas canvas, Graphics g, MouseEvent e) {
        this.inCanvas = true;
        canvas.getProject().repaintCanvas();
    }

    @Override
    public void mouseExited(Canvas canvas, Graphics g, MouseEvent e) {
        this.inCanvas = false;
        canvas.getProject().repaintCanvas();
    }

    @Override
    public void mouseMoved(Canvas canvas, Graphics g, MouseEvent e) {
        Canvas.snapToGrid(e);
        this.inCanvas = true;
        int curX = e.getX();
        int curY = e.getY();
        if (this.cur.getX() != curX || this.cur.getY() != curY) {
            this.cur = Location.create(curX, curY);
        }
        canvas.getProject().repaintCanvas();
    }

    @Override
    public void mousePressed(Canvas canvas, Graphics g, MouseEvent e) {
        if (!canvas.getProject().getLogisimFile().contains(canvas.getCircuit())) {
            this.exists = false;
            canvas.setErrorMessage(Strings.getter("cannotModifyError"));
            return;
        }
        Canvas.snapToGrid(e);
        this.cur = this.start = Location.create(e.getX(), e.getY());
        this.exists = true;
        this.shortening = this.startShortening = !canvas.getCircuit().getWires(this.start).isEmpty();
        super.mousePressed(canvas, g, e);
        canvas.getProject().repaintCanvas();
    }

    @Override
    public void mouseDragged(Canvas canvas, Graphics g, MouseEvent e) {
        if (this.exists) {
            Canvas.snapToGrid(e);
            int x = e.getX();
            int y = e.getY();
            int curX = this.findX(x, y);
            int curY = this.findY(x, y);
            if (this.cur.getX() == curX && this.cur.getY() == curY) {
                return;
            }
            Rectangle rect = new Rectangle();
            rect.add(this.start.getX(), this.start.getY());
            rect.add(this.cur.getX(), this.cur.getY());
            rect.add(curX, curY);
            rect.grow(3, 3);
            this.cur = Location.create(curX, curY);
            super.mouseDragged(canvas, g, e);
            this.shortening = false;
            if (this.startShortening) {
                for (Wire w : canvas.getCircuit().getWires(this.start)) {
                    if (!w.contains(this.cur)) continue;
                    this.shortening = true;
                    break;
                }
            }
            if (!this.shortening) {
                for (Wire w : canvas.getCircuit().getWires(this.cur)) {
                    if (!w.contains(this.start)) continue;
                    this.shortening = true;
                    break;
                }
            }
            canvas.repaint(rect);
        }
    }

    @Override
    public void mouseReleased(Canvas canvas, Graphics g, MouseEvent e) {
        if (!this.exists) {
            return;
        }
        Canvas.snapToGrid(e);
        int x = e.getX();
        int y = e.getY();
        int curX = this.findX(x, y);
        int curY = this.findY(x, y);
        if (this.cur.getX() != curX || this.cur.getY() != curY) {
            this.cur = Location.create(curX, curY);
        }
        this.exists = false;
        super.mouseReleased(canvas, g, e);
        Wire w = Wire.create(this.cur, this.start);
        if (w.getEnd0().equals(w.getEnd1())) {
            return;
        }
        block0 : for (int i = 1; i >= 0 && w.getLength() > 10; --i) {
            Location end = w.getEndLocation(i);
            if (!canvas.getCircuit().getNonWires(end).isEmpty()) continue;
            int delta = i == 0 ? 10 : -10;
            Location cand = w.isVertical() ? Location.create(end.getX(), end.getY() + delta) : Location.create(end.getX() + delta, end.getY());
            for (com.cburch.logisim.comp.Component comp : canvas.getCircuit().getNonWires(cand)) {
                WireRepair repair;
                if (!comp.getBounds().contains(end) || (repair = (WireRepair)comp.getFeature(WireRepair.class)) == null || !repair.shouldRepairWire(new WireRepairData(w, cand))) continue;
                w = Wire.create(w.getOtherEnd(end), cand);
                canvas.repaint(end.getX() - 13, end.getY() - 13, 26, 26);
                continue block0;
            }
        }
        if (w.getEnd0().equals(w.getEnd1())) {
            return;
        }
        ComponentAction act = CircuitActions.addComponent(canvas.getCircuit(), w, true);
        canvas.getProject().doAction(act);
        this.lastAction = act;
    }

    @Override
    public void keyPressed(Canvas canvas, KeyEvent event) {
        switch (event.getKeyCode()) {
            case 8: {
                if (this.lastAction == null || canvas.getProject().getLastAction() != this.lastAction) break;
                canvas.getProject().undoAction();
                this.lastAction = null;
            }
        }
    }

    @Override
    public void paintIcon(ComponentDrawContext c, int x, int y) {
        Graphics g = c.getGraphics();
        if (toolIcon != null) {
            toolIcon.paintIcon(c.getDestination(), g, x + 2, y + 2);
        } else {
            g.setColor(Color.black);
            g.drawLine(x + 3, y + 13, x + 17, y + 7);
            g.fillOval(x + 1, y + 11, 5, 5);
            g.fillOval(x + 15, y + 5, 5, 5);
        }
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }
}

