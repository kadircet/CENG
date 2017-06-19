/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.opts;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.file.ToolbarData;
import com.cburch.logisim.proj.LogisimPreferences;
import com.cburch.logisim.tools.Tool;
import java.awt.Component;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

class ToolbarList
extends JList {
    private ToolbarData base;
    private Model model;

    public ToolbarList(ToolbarData base) {
        this.base = base;
        this.model = new Model();
        this.setModel(this.model);
        this.setCellRenderer(new ListRenderer());
        this.setSelectionMode(0);
        LogisimPreferences.addPropertyChangeListener("gateShape", this.model);
        base.addToolbarListener(this.model);
        base.addToolAttributeListener(this.model);
    }

    public void localeChanged() {
        this.model.toolbarChanged();
    }

    private class Model
    extends AbstractListModel
    implements ToolbarData.ToolbarListener,
    AttributeListener,
    PropertyChangeListener {
        private Model() {
        }

        @Override
        public int getSize() {
            return ToolbarList.this.base.size();
        }

        @Override
        public Object getElementAt(int index) {
            return ToolbarList.this.base.get(index);
        }

        @Override
        public void toolbarChanged() {
            this.fireContentsChanged(this, 0, this.getSize());
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            ToolbarList.this.repaint();
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String prop = event.getPropertyName();
            if (prop.equals("gateShape")) {
                ToolbarList.this.repaint();
            }
        }
    }

    private static class ListRenderer
    extends DefaultListCellRenderer {
        private ListRenderer() {
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            ToolIcon icon;
            Component ret;
            if (value instanceof Tool) {
                Tool t = (Tool)value;
                ret = super.getListCellRendererComponent(list, t.getDisplayName(), index, isSelected, cellHasFocus);
                icon = new ToolIcon(t);
            } else if (value instanceof ToolbarData.Separator) {
                ret = super.getListCellRendererComponent(list, "---", index, isSelected, cellHasFocus);
                icon = null;
            } else {
                ret = super.getListCellRendererComponent(list, value.toString(), index, isSelected, cellHasFocus);
                icon = null;
            }
            if (ret instanceof JLabel) {
                ((JLabel)ret).setIcon(icon);
            }
            return ret;
        }
    }

    private static class ToolIcon
    implements Icon {
        private Tool tool;

        ToolIcon(Tool tool) {
            this.tool = tool;
        }

        @Override
        public void paintIcon(Component comp, Graphics g, int x, int y) {
            Graphics gNew = g.create();
            this.tool.paintIcon(new ComponentDrawContext(comp, null, null, g, gNew), x + 2, y + 2);
            gNew.dispose();
        }

        @Override
        public int getIconWidth() {
            return 20;
        }

        @Override
        public int getIconHeight() {
            return 20;
        }
    }

}

