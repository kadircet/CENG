/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.tools;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.RadixOption;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.AbstractCaret;
import com.cburch.logisim.tools.Caret;
import com.cburch.logisim.tools.Pokable;
import com.cburch.logisim.tools.Strings;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.Icons;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;

public class PokeTool
extends Tool {
    private static final Icon toolIcon = Icons.getIcon("poke.gif");
    private static final Color caretColor = new Color(255, 255, 150);
    private static Cursor cursor = Cursor.getPredefinedCursor(12);
    private Caret caret;

    public boolean equals(Object other) {
        return other instanceof PokeTool;
    }

    public int hashCode() {
        return PokeTool.class.hashCode();
    }

    @Override
    public String getName() {
        return "Poke Tool";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("pokeTool");
    }

    @Override
    public String getDescription() {
        return Strings.get("pokeToolDesc");
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
        int x = e.getX();
        int y = e.getY();
        Location loc = Location.create(x, y);
        boolean dirty = false;
        if (this.caret != null && !this.caret.getBounds(g).contains(loc)) {
            dirty = true;
            this.caret.stopEditing();
            this.caret = null;
        }
        if (this.caret == null) {
            ComponentUserEvent event = new ComponentUserEvent(canvas, x, y);
            Circuit circ = canvas.getCircuit();
            Iterator it = circ.getAllContaining(loc, g).iterator();
            while (this.caret == null && it.hasNext()) {
                com.cburch.logisim.comp.Component c = (com.cburch.logisim.comp.Component)it.next();
                if (c instanceof Wire) {
                    this.caret = new WireCaret(canvas, (Wire)c, x, y, canvas.getProject().getOptions().getAttributeSet());
                    continue;
                }
                Pokable p = (Pokable)c.getFeature(Pokable.class);
                if (p == null) continue;
                this.caret = p.getPokeCaret(event);
                AttributeSet attrs = c.getAttributeSet();
                if (attrs == null || attrs.getAttributes().size() <= 0) continue;
                Project proj = canvas.getProject();
                proj.getFrame().viewComponentAttributes(circ, c);
            }
        }
        if (this.caret != null) {
            dirty = true;
            this.caret.mousePressed(e);
        }
        if (dirty) {
            canvas.getProject().repaintCanvas();
        }
    }

    @Override
    public void mouseDragged(Canvas canvas, Graphics g, MouseEvent e) {
        if (this.caret != null) {
            this.caret.mouseDragged(e);
            canvas.getProject().repaintCanvas();
        }
    }

    @Override
    public void mouseReleased(Canvas canvas, Graphics g, MouseEvent e) {
        if (this.caret != null) {
            this.caret.mouseReleased(e);
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
    public void paintIcon(ComponentDrawContext c, int x, int y) {
        Graphics g = c.getGraphics();
        if (toolIcon != null) {
            toolIcon.paintIcon(c.getDestination(), g, x + 2, y + 2);
        } else {
            g.setColor(Color.black);
            g.drawLine(x + 4, y + 2, x + 4, y + 17);
            g.drawLine(x + 4, y + 17, x + 1, y + 11);
            g.drawLine(x + 4, y + 17, x + 7, y + 11);
            g.drawLine(x + 15, y + 2, x + 15, y + 17);
            g.drawLine(x + 15, y + 2, x + 12, y + 8);
            g.drawLine(x + 15, y + 2, x + 18, y + 8);
        }
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }

    private static class WireCaret
    extends AbstractCaret {
        AttributeSet opts;
        Canvas canvas;
        Wire wire;
        int x;
        int y;

        WireCaret(Canvas c, Wire w, int x, int y, AttributeSet opts) {
            this.canvas = c;
            this.wire = w;
            this.x = x;
            this.y = y;
            this.opts = opts;
        }

        @Override
        public void draw(Graphics g) {
            Value v = this.canvas.getCircuitState().getValue(this.wire.getEnd0());
            RadixOption radix1 = (RadixOption)this.opts.getValue(Options.ATTR_RADIX_1);
            RadixOption radix2 = (RadixOption)this.opts.getValue(Options.ATTR_RADIX_2);
            if (radix1 == null) {
                radix1 = RadixOption.RADIX_2;
            }
            String vStr = radix1.toString(v);
            if (radix2 != null && v.getWidth() > 1) {
                vStr = vStr + " / " + radix2.toString(v);
            }
            FontMetrics fm = g.getFontMetrics();
            g.setColor(caretColor);
            g.fillRect(this.x + 2, this.y + 2, fm.stringWidth(vStr) + 4, fm.getAscent() + fm.getDescent() + 4);
            g.setColor(Color.BLACK);
            g.drawRect(this.x + 2, this.y + 2, fm.stringWidth(vStr) + 4, fm.getAscent() + fm.getDescent() + 4);
            g.fillOval(this.x - 2, this.y - 2, 5, 5);
            g.drawString(vStr, this.x + 4, this.y + 4 + fm.getAscent());
        }
    }

}

