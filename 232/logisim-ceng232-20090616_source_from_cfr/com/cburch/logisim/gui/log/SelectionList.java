/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.data.Value;
import com.cburch.logisim.gui.log.ComponentIcon;
import com.cburch.logisim.gui.log.ModelEvent;
import com.cburch.logisim.gui.log.ModelListener;
import com.cburch.logisim.gui.log.Selection;
import com.cburch.logisim.gui.log.SelectionItem;
import java.awt.Component;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

class SelectionList
extends JList {
    private Selection selection = null;

    public SelectionList() {
        this.setModel(new Model());
        this.setCellRenderer(new MyCellRenderer());
        this.setSelectionMode(0);
    }

    public void setSelection(Selection value) {
        if (this.selection != value) {
            Model model = (Model)this.getModel();
            if (this.selection != null) {
                this.selection.removeModelListener(model);
            }
            this.selection = value;
            if (this.selection != null) {
                this.selection.addModelListener(model);
            }
            model.selectionChanged(null);
        }
    }

    public void localeChanged() {
        this.repaint();
    }

    private class MyCellRenderer
    extends DefaultListCellRenderer {
        private MyCellRenderer() {
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
            Component ret = super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
            if (ret instanceof JLabel && value instanceof SelectionItem) {
                JLabel label = (JLabel)ret;
                SelectionItem item = (SelectionItem)value;
                com.cburch.logisim.comp.Component comp = item.getComponent();
                label.setIcon(new ComponentIcon(comp));
                label.setText(item.toString() + " - " + item.getRadix());
            }
            return ret;
        }
    }

    private class Model
    extends AbstractListModel
    implements ModelListener {
        private Model() {
        }

        @Override
        public int getSize() {
            return SelectionList.this.selection == null ? 0 : SelectionList.this.selection.size();
        }

        @Override
        public Object getElementAt(int index) {
            return SelectionList.this.selection.get(index);
        }

        @Override
        public void selectionChanged(ModelEvent event) {
            this.fireContentsChanged(this, 0, this.getSize());
        }

        @Override
        public void entryAdded(ModelEvent event, Value[] values) {
        }

        @Override
        public void filePropertyChanged(ModelEvent event) {
        }
    }

}

