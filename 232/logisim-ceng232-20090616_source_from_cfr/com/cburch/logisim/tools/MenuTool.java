/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.tools;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitActions;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.main.Selection;
import com.cburch.logisim.gui.main.SelectionActions;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.MenuExtender;
import com.cburch.logisim.tools.Strings;
import com.cburch.logisim.tools.Tool;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class MenuTool
extends Tool {
    public boolean equals(Object other) {
        return other instanceof MenuTool;
    }

    public int hashCode() {
        return MenuTool.class.hashCode();
    }

    @Override
    public String getName() {
        return "Menu Tool";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("menuTool");
    }

    @Override
    public String getDescription() {
        return Strings.get("menuToolDesc");
    }

    @Override
    public void mousePressed(Canvas canvas, Graphics g, MouseEvent e) {
        JPopupMenu menu2;
        JPopupMenu menu2;
        int x = e.getX();
        int y = e.getY();
        Location pt = Location.create(x, y);
        Project proj = canvas.getProject();
        Selection sel = proj.getSelection();
        Collection in_sel = sel.getComponentsContaining(pt, g);
        if (!in_sel.isEmpty()) {
            com.cburch.logisim.comp.Component comp = (com.cburch.logisim.comp.Component)in_sel.iterator().next();
            if (sel.getComponents().size() > 1) {
                menu2 = new MenuSelection(proj);
            } else {
                menu2 = new MenuComponent(proj, canvas.getCircuit(), comp);
                MenuExtender extender = (MenuExtender)comp.getFeature(MenuExtender.class);
                if (extender != null) {
                    extender.configureMenu(menu2, proj);
                }
            }
        } else {
            Collection cl = canvas.getCircuit().getAllContaining(pt, g);
            if (!cl.isEmpty()) {
                com.cburch.logisim.comp.Component comp = (com.cburch.logisim.comp.Component)cl.iterator().next();
                menu2 = new MenuComponent(proj, canvas.getCircuit(), comp);
                MenuExtender extender = (MenuExtender)comp.getFeature(MenuExtender.class);
                if (extender != null) {
                    extender.configureMenu(menu2, proj);
                }
            } else {
                menu2 = null;
            }
        }
        if (menu2 != null) {
            canvas.showPopupMenu(menu2, x, y);
        }
    }

    @Override
    public void paintIcon(ComponentDrawContext c, int x, int y) {
        Graphics g = c.getGraphics();
        g.fillRect(x + 2, y + 1, 9, 2);
        g.drawRect(x + 2, y + 3, 15, 12);
        g.setColor(Color.lightGray);
        g.drawLine(x + 4, y + 2, x + 8, y + 2);
        for (int y_offs = y + 6; y_offs < y + 15; y_offs += 3) {
            g.drawLine(x + 4, y_offs, x + 14, y_offs);
        }
    }

    private class MenuSelection
    extends JPopupMenu
    implements ActionListener {
        Project proj;
        JMenuItem del;
        JMenuItem cut;
        JMenuItem copy;

        MenuSelection(Project proj) {
            this.del = new JMenuItem(Strings.get("selDeleteItem"));
            this.cut = new JMenuItem(Strings.get("selCutItem"));
            this.copy = new JMenuItem(Strings.get("selCopyItem"));
            this.proj = proj;
            boolean canChange = proj.getLogisimFile().contains(proj.getCurrentCircuit());
            this.add(this.del);
            this.del.addActionListener(this);
            this.del.setEnabled(canChange);
            this.add(this.cut);
            this.cut.addActionListener(this);
            this.cut.setEnabled(canChange);
            this.add(this.copy);
            this.copy.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == this.del) {
                this.proj.doAction(SelectionActions.clear());
            } else if (src == this.cut) {
                this.proj.doAction(SelectionActions.cut());
            } else if (src == this.copy) {
                this.proj.doAction(SelectionActions.copy());
            }
        }

        public void show(JComponent parent, int x, int y) {
            super.show(this, x, y);
        }
    }

    private class MenuComponent
    extends JPopupMenu
    implements ActionListener {
        Project proj;
        Circuit circ;
        com.cburch.logisim.comp.Component comp;
        JMenuItem del;
        JMenuItem attrs;

        MenuComponent(Project proj, Circuit circ, com.cburch.logisim.comp.Component comp) {
            this.del = new JMenuItem(Strings.get("compDeleteItem"));
            this.attrs = new JMenuItem(Strings.get("compShowAttrItem"));
            this.proj = proj;
            this.circ = circ;
            this.comp = comp;
            boolean canChange = proj.getLogisimFile().contains(circ);
            this.add(this.del);
            this.del.addActionListener(this);
            this.del.setEnabled(canChange);
            this.add(this.attrs);
            this.attrs.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == this.del) {
                Circuit circ = this.proj.getCurrentCircuit();
                this.proj.doAction(CircuitActions.removeComponent(circ, this.comp));
            } else if (src == this.attrs) {
                this.proj.getFrame().viewComponentAttributes(this.circ, this.comp);
            }
        }
    }

}

