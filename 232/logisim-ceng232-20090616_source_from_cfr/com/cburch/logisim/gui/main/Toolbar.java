/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.file.ToolbarData;
import com.cburch.logisim.gui.main.AttributeTable;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.main.Strings;
import com.cburch.logisim.proj.LogisimPreferences;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.ProjectEvent;
import com.cburch.logisim.proj.ProjectListener;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.InputEventUtil;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

class Toolbar
extends JPanel
implements LocaleListener {
    static final Object VERTICAL = new Object();
    static final Object HORIZONTAL = new Object();
    private Project proj;
    private ToolbarData data;
    private MyListener listener;
    private ArrayList contents;
    private Item cur_down;
    private Tool haloedTool;
    private Object orientation;
    private Border inset;
    private int inset_left;
    private int inset_top;
    private int inset_width;
    private int inset_height;

    public Toolbar(Project proj) {
        this.listener = new MyListener();
        this.contents = new ArrayList();
        this.cur_down = null;
        this.haloedTool = null;
        this.orientation = HORIZONTAL;
        this.inset = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        this.proj = proj;
        this.data = proj.getOptions().getToolbarData();
        Insets ins = this.inset.getBorderInsets(this);
        this.inset_left = ins.left;
        this.inset_top = ins.top;
        this.inset_width = ins.left + 24 + ins.right;
        this.inset_height = ins.top + 24 + ins.bottom;
        this.setPreferredSize(new Dimension(this.inset_width, this.inset_height));
        ToolbarData data = proj.getOptions().getToolbarData();
        data.addToolbarListener(this.listener);
        data.addToolAttributeListener(this.listener);
        LogisimPreferences.addPropertyChangeListener("gateShape", this.listener);
        proj.addProjectListener(this.listener);
        this.addMouseListener(this.listener);
        this.setToolTipText(Strings.get("toolbarDefaultToolTip"));
        this.remakeToolbar();
        LocaleManager.addLocaleListener(this);
    }

    public void registerShortcuts(JComponent component) {
        int imask = this.getToolkit().getMenuShortcutKeyMask();
        component.addKeyListener(new SelectAction(imask));
    }

    public void setOrientation(Object value) {
        if (value != VERTICAL && value != HORIZONTAL) {
            throw new IllegalArgumentException();
        }
        this.orientation = value;
    }

    public void setHaloedTool(Tool t) {
        if (this.haloedTool == t) {
            return;
        }
        this.haloedTool = t;
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x = 0;
        for (Item i : this.contents) {
            if (this.orientation == VERTICAL) {
                i.paint(g, 0, x);
            } else {
                i.paint(g, x, 0);
            }
            x += i.getWidth();
        }
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        Item it = this.findItem(e);
        return it == null ? Strings.get("toolbarDefaultToolTip") : it.getToolTipText();
    }

    private Item findItem(MouseEvent e) {
        int ex = e.getX();
        int ey = e.getY();
        if (this.orientation == VERTICAL) {
            int t = ex;
            ex = ey;
            ey = t;
        }
        if (ey < 0 && ey >= this.inset_height) {
            return null;
        }
        int x = 0;
        for (Item i : this.contents) {
            int wid = i.getWidth();
            if (x <= ex && ex < x + wid) {
                return i;
            }
            x += wid;
        }
        return null;
    }

    private void remakeToolbar() {
        ArrayList old_contents = this.contents;
        ArrayList<Item> new_contents = new ArrayList<Item>();
        int pos = -1;
        for (Object o : this.data.getContents()) {
            ++pos;
            if (o instanceof ToolbarData.Separator) {
                ToolbarData.Separator sep = (ToolbarData.Separator)o;
                new_contents.add(new Separator(sep, pos));
                continue;
            }
            if (!(o instanceof Tool)) continue;
            Tool t = (Tool)o;
            ToolButton i = this.findButton(t);
            if (i == null) {
                i = new ToolButton(t, pos);
            } else {
                i.pos = pos;
                old_contents.remove(i);
            }
            new_contents.add(i);
        }
        this.contents = new_contents;
        Tool cur = this.proj.getTool();
        for (Item i : old_contents) {
            if (!(i instanceof ToolButton) || ((ToolButton)i).tool != cur) continue;
            Tool t = this.data.getFirstTool();
            this.proj.setTool(t);
            this.proj.getFrame().viewAttributes(t);
        }
        this.repaint();
    }

    private ToolButton findButton(Tool t) {
        for (Item i : this.contents) {
            if (!(i instanceof ToolButton)) continue;
            ToolButton b = (ToolButton)i;
            if (b.tool != t) continue;
            return b;
        }
        return null;
    }

    @Override
    public void localeChanged() {
        this.repaint();
    }

    private class SelectAction
    implements KeyListener {
        int mask;

        SelectAction(int mask) {
            this.mask = mask;
        }

        @Override
        public void keyTyped(KeyEvent event) {
        }

        @Override
        public void keyPressed(KeyEvent event) {
            if ((event.getModifiers() & this.mask) != 0) {
                int selection = -1;
                switch (event.getKeyCode()) {
                    case 49: {
                        selection = 0;
                        break;
                    }
                    case 50: {
                        selection = 1;
                        break;
                    }
                    case 51: {
                        selection = 2;
                        break;
                    }
                    case 52: {
                        selection = 3;
                        break;
                    }
                    case 53: {
                        selection = 4;
                        break;
                    }
                    case 54: {
                        selection = 5;
                        break;
                    }
                    case 55: {
                        selection = 6;
                        break;
                    }
                    case 56: {
                        selection = 7;
                        break;
                    }
                    case 57: {
                        selection = 8;
                        break;
                    }
                    case 48: {
                        selection = 9;
                    }
                }
                if (selection >= 0) {
                    int current = 0;
                    for (Item item : Toolbar.this.contents) {
                        if (!(item instanceof ToolButton)) continue;
                        if (current == selection) {
                            ToolButton b = (ToolButton)item;
                            b.mouseClicked();
                        }
                        ++current;
                    }
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent arg0) {
        }
    }

    private class MyListener
    implements MouseListener,
    ProjectListener,
    ToolbarData.ToolbarListener,
    AttributeListener,
    PropertyChangeListener {
        private MyListener() {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Item it = Toolbar.this.findItem(e);
            if (it == null) {
                return;
            }
            Toolbar.this.cur_down = it;
            Toolbar.this.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (Toolbar.this.cur_down != null) {
                Item it = Toolbar.this.findItem(e);
                if (it == Toolbar.this.cur_down) {
                    Toolbar.this.cur_down.mouseClicked();
                }
                Toolbar.this.cur_down = null;
                Toolbar.this.repaint();
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void projectChanged(ProjectEvent e) {
            int act = e.getAction();
            if (act == 2) {
                Toolbar.this.repaint();
            } else if (act == 0) {
                LogisimFile file;
                LogisimFile old = (LogisimFile)e.getOldData();
                if (old != null) {
                    ToolbarData data = old.getOptions().getToolbarData();
                    data.removeToolbarListener(this);
                    data.removeToolAttributeListener(this);
                }
                if ((file = (LogisimFile)e.getData()) != null) {
                    Toolbar.this.data = file.getOptions().getToolbarData();
                    Toolbar.this.data.addToolbarListener(this);
                    Toolbar.this.data.addToolAttributeListener(this);
                }
                Toolbar.this.contents.clear();
                Toolbar.this.remakeToolbar();
            }
        }

        @Override
        public void toolbarChanged() {
            Toolbar.this.remakeToolbar();
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            Toolbar.this.repaint();
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String prop = event.getPropertyName();
            if (prop.equals("gateShape")) {
                Toolbar.this.repaint();
            }
        }
    }

    private class ToolButton
    extends Item {
        Tool tool;

        ToolButton(Tool tool, int pos) {
            super(pos);
            this.tool = tool;
        }

        Tool getTool() {
            return this.tool;
        }

        @Override
        String getToolTipText() {
            String ret = this.tool.getDescription();
            int index = 1;
            for (Object item : Toolbar.this.contents) {
                if (item == this) break;
                if (!(item instanceof ToolButton)) continue;
                ++index;
            }
            if (index <= 10) {
                if (index == 10) {
                    index = 0;
                }
                ret = ret + " (" + InputEventUtil.toKeyDisplayString(Toolbar.this.getToolkit().getMenuShortcutKeyMask()) + "-" + index + ")";
            }
            return ret;
        }

        @Override
        int getWidth() {
            return Toolbar.this.inset_width;
        }

        @Override
        void paint(Graphics g, int x, int y) {
            Toolbar.this.inset.paintBorder(Toolbar.this, g, x, y, Toolbar.this.inset_width, Toolbar.this.inset_height);
            x += Toolbar.this.inset_left;
            y += Toolbar.this.inset_top;
            if (Toolbar.this.cur_down == this) {
                g.setColor(Color.GRAY);
                g.fillRect(x, y, 24, 24);
            } else {
                g.setColor(Toolbar.this.getBackground());
                g.fillRect(x, y, 24, 24);
            }
            if (this.tool == Toolbar.this.haloedTool && Toolbar.this.proj.getFrame().getShowHalo()) {
                g.setColor(AttributeTable.HALO_COLOR);
                g.fillRect(x + 1, y + 1, 22, 22);
            }
            if (this.tool == Toolbar.this.proj.getTool()) {
                GraphicsUtil.switchToWidth(g, 2);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, 24, 24);
                GraphicsUtil.switchToWidth(g, 1);
            }
            g.setColor(Color.BLACK);
            Graphics g_copy = g.create();
            ComponentDrawContext c = new ComponentDrawContext(Toolbar.this, null, null, g, g_copy);
            this.tool.paintIcon(c, x + 2, y + 2);
            g_copy.dispose();
        }

        @Override
        void mouseClicked() {
            Toolbar.this.proj.setTool(this.tool);
            Toolbar.this.proj.getFrame().viewAttributes(this.tool);
        }
    }

    private class Separator
    extends Item {
        ToolbarData.Separator sep;

        Separator(ToolbarData.Separator sep, int pos) {
            super(pos);
            this.sep = sep;
        }

        @Override
        int getWidth() {
            return 4;
        }

        @Override
        void paint(Graphics g, int x, int y) {
            g.setColor(Color.gray);
            int width = 2;
            int height = 2;
            if (Toolbar.this.orientation == Toolbar.VERTICAL) {
                x += 4;
                ++y;
                width = Toolbar.this.getWidth() - 8;
            } else {
                ++x;
                y += 4;
                height = Toolbar.this.getHeight() - 8;
            }
            g.fillRect(x, y, width, height);
        }

        @Override
        void mouseClicked() {
        }

        @Override
        String getToolTipText() {
            return null;
        }
    }

    private static abstract class Item {
        int pos;

        Item(int pos) {
            this.pos = pos;
        }

        abstract int getWidth();

        abstract void paint(Graphics var1, int var2, int var3);

        abstract void mouseClicked();

        abstract String getToolTipText();
    }

}

