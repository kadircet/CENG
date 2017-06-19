/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.gui.main.Clipboard;
import com.cburch.logisim.gui.main.SelectionBase;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.CustomHandles;
import java.awt.Color;
import java.awt.Graphics;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Selection
extends SelectionBase {
    private boolean isVisible = true;

    public Selection(Project proj) {
        super(proj);
    }

    public boolean isEmpty() {
        return this.selected.isEmpty() && this.lifted.isEmpty();
    }

    public boolean equals(Object other) {
        if (!(other instanceof Selection)) {
            return false;
        }
        Selection otherSelection = (Selection)other;
        return this.selected.equals(otherSelection.selected) && this.lifted.equals(otherSelection.lifted);
    }

    public Collection getComponents() {
        return this.unionSet;
    }

    public Collection getAnchoredComponents() {
        return this.selected;
    }

    public Collection getHiddenComponents() {
        return this.isVisible ? Collections.EMPTY_SET : this.unionSet;
    }

    public Collection getComponentsContaining(Location query) {
        HashSet<Component> ret = new HashSet<Component>();
        for (Component comp : this.unionSet) {
            if (!comp.contains(query)) continue;
            ret.add(comp);
        }
        return ret;
    }

    public Collection getComponentsContaining(Location query, Graphics g) {
        HashSet<Component> ret = new HashSet<Component>();
        for (Component comp : this.unionSet) {
            if (!comp.contains(query, g)) continue;
            ret.add(comp);
        }
        return ret;
    }

    public Collection getComponentsWithin(Bounds bds) {
        HashSet<Component> ret = new HashSet<Component>();
        for (Component comp : this.unionSet) {
            if (!bds.contains(comp.getBounds())) continue;
            ret.add(comp);
        }
        return ret;
    }

    public Collection getComponentsWithin(Bounds bds, Graphics g) {
        HashSet<Component> ret = new HashSet<Component>();
        for (Component comp : this.unionSet) {
            if (!bds.contains(comp.getBounds(g))) continue;
            ret.add(comp);
        }
        return ret;
    }

    public boolean contains(Component comp) {
        return this.unionSet.contains(comp);
    }

    public void setVisible(boolean value) {
        this.isVisible = value;
    }

    public void draw(ComponentDrawContext context) {
        if (this.isVisible) {
            Graphics g = context.getGraphics();
            for (Component c : this.lifted) {
                Location loc = c.getLocation();
                Graphics g_new = g.create();
                context.setGraphics(g_new);
                c.getFactory().drawGhost(context, Color.GRAY, loc.getX(), loc.getY(), c.getAttributeSet());
                g_new.dispose();
            }
            for (Component comp : this.unionSet) {
                Graphics g_new = g.create();
                context.setGraphics(g_new);
                CustomHandles handler = (CustomHandles)comp.getFeature(CustomHandles.class);
                if (handler == null) {
                    context.drawHandles(comp);
                } else {
                    handler.drawHandles(context);
                }
                g_new.dispose();
            }
            context.setGraphics(g);
        }
    }

    public void drawGhostsShifted(ComponentDrawContext context, int dx, int dy) {
        if (this.shouldSnap()) {
            dx = Canvas.snapXToGrid(dx);
            dy = Canvas.snapYToGrid(dy);
        }
        Graphics g = context.getGraphics();
        for (Component comp : this.unionSet) {
            AttributeSet attrs = comp.getAttributeSet();
            Location loc = comp.getLocation();
            int x = loc.getX() + dx;
            int y = loc.getY() + dy;
            context.setGraphics(g.create());
            comp.getFactory().drawGhost(context, Color.gray, x, y, attrs);
            context.getGraphics().dispose();
        }
        context.setGraphics(g);
    }

    @Override
    public void print() {
        System.err.println(" isVisible: " + this.isVisible);
        super.print();
    }

    public static interface Listener {
        public void selectionChanged(Event var1);
    }

    public static class Event {
        Object source;

        Event(Object source) {
            this.source = source;
        }

        public Object getSource() {
            return this.source;
        }
    }

}

