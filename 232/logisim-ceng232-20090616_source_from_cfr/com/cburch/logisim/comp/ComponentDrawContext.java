/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.comp;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.EndData;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.proj.LogisimPreferences;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.List;

public class ComponentDrawContext {
    private static final int PIN_OFFS = 2;
    private static final int PIN_RAD = 4;
    private java.awt.Component dest;
    private Circuit circuit;
    private CircuitState circuitState;
    private Graphics base;
    private Graphics g;
    private boolean printView;

    public ComponentDrawContext(java.awt.Component dest, Circuit circuit, CircuitState circuitState, Graphics base, Graphics g, boolean printView) {
        this.dest = dest;
        this.circuit = circuit;
        this.circuitState = circuitState;
        this.base = base;
        this.g = g;
        this.printView = printView;
    }

    public ComponentDrawContext(java.awt.Component dest, Circuit circuit, CircuitState circuitState, Graphics base, Graphics g) {
        this(dest, circuit, circuitState, base, g, false);
    }

    public boolean getShowState() {
        return !this.printView;
    }

    public boolean isPrintView() {
        return this.printView;
    }

    public boolean shouldDrawColor() {
        return !this.printView;
    }

    public java.awt.Component getDestination() {
        return this.dest;
    }

    public Graphics getGraphics() {
        return this.g;
    }

    public Circuit getCircuit() {
        return this.circuit;
    }

    public CircuitState getCircuitState() {
        return this.circuitState;
    }

    public void setGraphics(Graphics g) {
        this.g = g;
    }

    public Object getGateShape() {
        return LogisimPreferences.getGateShape();
    }

    public void drawBounds(Component comp) {
        GraphicsUtil.switchToWidth(this.g, 2);
        this.g.setColor(Color.BLACK);
        Bounds bds = comp.getBounds(this.g);
        this.g.drawRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
        GraphicsUtil.switchToWidth(this.g, 1);
    }

    public void drawRectangle(Component comp) {
        this.drawRectangle(comp, "");
    }

    public void drawRectangle(Component comp, String label) {
        Bounds bds = comp.getBounds(this.g);
        this.drawRectangle(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight(), label);
    }

    public void drawRectangle(int x, int y, int width, int height, String label) {
        GraphicsUtil.switchToWidth(this.g, 2);
        this.g.drawRect(x, y, width, height);
        if (label != null && !label.equals("")) {
            FontMetrics fm = this.base.getFontMetrics(this.g.getFont());
            int lwid = fm.stringWidth(label);
            if (height > 20) {
                this.g.drawString(label, x + (width - lwid) / 2, y + 2 + fm.getAscent());
            } else {
                this.g.drawString(label, x + (width - lwid) / 2, y + (height + fm.getAscent()) / 2 - 1);
            }
        }
    }

    public void drawRectangle(ComponentFactory source, int x, int y, AttributeSet attrs, String label) {
        Bounds bds = source.getOffsetBounds(attrs);
        this.drawRectangle(source, x + bds.getX(), y + bds.getY(), bds.getWidth(), bds.getHeight(), label);
    }

    public void drawRectangle(ComponentFactory source, int x, int y, int width, int height, String label) {
        GraphicsUtil.switchToWidth(this.g, 2);
        this.g.drawRect(x + 1, y + 1, width - 1, height - 1);
        if (label != null && !label.equals("")) {
            FontMetrics fm = this.base.getFontMetrics(this.g.getFont());
            int lwid = fm.stringWidth(label);
            if (height > 20) {
                this.g.drawString(label, x + (width - lwid) / 2, y + 2 + fm.getAscent());
            } else {
                this.g.drawString(label, x + (width - lwid) / 2, y + (height + fm.getAscent()) / 2 - 1);
            }
        }
    }

    public void drawDongle(int x, int y) {
        GraphicsUtil.switchToWidth(this.g, 2);
        this.g.drawOval(x - 4, y - 4, 9, 9);
    }

    public void drawPin(Component comp, int i, String label, Direction dir) {
        Color curColor = this.g.getColor();
        EndData e = comp.getEnd(i);
        Location pt = e.getLocation();
        int x = pt.getX();
        int y = pt.getY();
        if (this.getShowState()) {
            CircuitState state = this.getCircuitState();
            this.g.setColor(state.getValue(pt).getColor());
        } else {
            this.g.setColor(Color.BLACK);
        }
        this.g.fillOval(x - 2, y - 2, 4, 4);
        this.g.setColor(curColor);
        if (dir == Direction.EAST) {
            GraphicsUtil.drawText(this.g, label, x + 3, y, -1, 0);
        } else if (dir == Direction.WEST) {
            GraphicsUtil.drawText(this.g, label, x - 3, y, 1, 0);
        } else if (dir == Direction.SOUTH) {
            GraphicsUtil.drawText(this.g, label, x, y - 3, 0, 1);
        } else if (dir == Direction.NORTH) {
            GraphicsUtil.drawText(this.g, label, x, y + 3, 0, -1);
        }
    }

    public void drawPin(Component comp, int i) {
        EndData e = comp.getEnd(i);
        Location pt = e.getLocation();
        Color curColor = this.g.getColor();
        if (this.getShowState()) {
            CircuitState state = this.getCircuitState();
            this.g.setColor(state.getValue(pt).getColor());
        } else {
            this.g.setColor(Color.BLACK);
        }
        this.g.fillOval(pt.getX() - 2, pt.getY() - 2, 4, 4);
        this.g.setColor(curColor);
    }

    public void drawPins(Component comp) {
        Color curColor = this.g.getColor();
        for (EndData e : comp.getEnds()) {
            Location pt = e.getLocation();
            if (this.getShowState()) {
                CircuitState state = this.getCircuitState();
                this.g.setColor(state.getValue(pt).getColor());
            } else {
                this.g.setColor(Color.BLACK);
            }
            this.g.fillOval(pt.getX() - 2, pt.getY() - 2, 4, 4);
        }
        this.g.setColor(curColor);
    }

    public void drawClock(Component comp, int i, Direction dir) {
        Color curColor = this.g.getColor();
        this.g.setColor(Color.BLACK);
        GraphicsUtil.switchToWidth(this.g, 2);
        EndData e = comp.getEnd(i);
        Location pt = e.getLocation();
        int x = pt.getX();
        int y = pt.getY();
        int CLK_SZ = 4;
        int CLK_SZD = 3;
        if (dir == Direction.NORTH) {
            this.g.drawLine(x - 3, y - 1, x, y - 4);
            this.g.drawLine(x + 3, y - 1, x, y - 4);
        } else if (dir == Direction.SOUTH) {
            this.g.drawLine(x - 3, y + 1, x, y + 4);
            this.g.drawLine(x + 3, y + 1, x, y + 4);
        } else if (dir == Direction.EAST) {
            this.g.drawLine(x + 1, y - 3, x + 4, y);
            this.g.drawLine(x + 1, y + 3, x + 4, y);
        } else if (dir == Direction.WEST) {
            this.g.drawLine(x - 1, y - 3, x - 4, y);
            this.g.drawLine(x - 1, y + 3, x - 4, y);
        }
        this.g.setColor(curColor);
        GraphicsUtil.switchToWidth(this.g, 1);
    }

    public void drawHandles(Component comp) {
        Bounds b = comp.getBounds(this.g);
        int left = b.getX();
        int right = left + b.getWidth();
        int top = b.getY();
        int bot = top + b.getHeight();
        this.drawHandle(right, top);
        this.drawHandle(left, bot);
        this.drawHandle(right, bot);
        this.drawHandle(left, top);
    }

    public void drawHandle(Location loc) {
        this.drawHandle(loc.getX(), loc.getY());
    }

    public void drawHandle(int x, int y) {
        this.g.setColor(Color.white);
        this.g.fillRect(x - 3, y - 3, 7, 7);
        this.g.setColor(Color.black);
        this.g.drawRect(x - 3, y - 3, 7, 7);
    }
}

