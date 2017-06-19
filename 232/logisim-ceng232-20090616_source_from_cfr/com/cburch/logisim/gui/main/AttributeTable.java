/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.main.Strings;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.JDialogOk;
import com.cburch.logisim.util.JInputComponent;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

public class AttributeTable
extends JTable
implements LocaleListener {
    static final Color HALO_COLOR = new Color(192, 255, 255);
    private Window parent;
    private Project proj;
    private Model model;
    private CellEditor editor;
    private AttributeSet attrs;
    private Listener listener;
    private MyListener attrsListener;
    private AttributeData[] history;

    public AttributeTable(Window parent, Project proj) {
        this.model = new Model();
        this.editor = new CellEditor();
        this.attrs = null;
        this.listener = new DefaultListener();
        this.attrsListener = new MyListener();
        this.history = new AttributeData[5];
        this.parent = parent;
        this.proj = proj;
        this.setModel(this.model);
        this.setDefaultEditor(Object.class, this.editor);
        this.setTableHeader(null);
        this.setRowHeight(20);
        LocaleManager.addLocaleListener(this);
    }

    public AttributeSet getAttributeSet() {
        return this.attrs;
    }

    public void setAttributeSet(AttributeSet attrs) {
        this.setAttributeSet(attrs, null);
    }

    public void setAttributeSet(AttributeSet attrs, Listener l) {
        if (attrs != this.attrs) {
            this.removeEditor();
            if (this.attrs != null) {
                this.attrs.removeAttributeListener(this.attrsListener);
            }
            this.attrs = attrs;
            if (this.attrs != null) {
                this.attrs.addAttributeListener(this.attrsListener);
            }
            this.listener = l == null ? new DefaultListener() : l;
            this.model.fireTableChanged();
        }
    }

    @Override
    public void localeChanged() {
        this.model.fireTableChanged();
    }

    private class CellEditor
    implements TableCellEditor,
    FocusListener,
    ActionListener {
        LinkedList listeners;

        private CellEditor() {
            this.listeners = new LinkedList();
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            this.listeners.add(l);
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            this.listeners.remove(l);
        }

        public void fireEditingCanceled() {
            ChangeEvent e = new ChangeEvent(AttributeTable.this);
            for (CellEditorListener l : (List)this.listeners.clone()) {
                l.editingCanceled(e);
            }
        }

        public void fireEditingStopped() {
            ChangeEvent e = new ChangeEvent(AttributeTable.this);
            for (CellEditorListener l : (LinkedList)this.listeners.clone()) {
                l.editingStopped(e);
            }
        }

        @Override
        public void cancelCellEditing() {
            this.fireEditingCanceled();
        }

        @Override
        public boolean stopCellEditing() {
            this.fireEditingStopped();
            return true;
        }

        @Override
        public Object getCellEditorValue() {
            Component comp = AttributeTable.this.editorComp;
            if (comp instanceof JTextField) {
                return ((JTextField)comp).getText();
            }
            if (comp instanceof JComboBox) {
                Object val = ((JComboBox)comp).getSelectedItem();
                return val;
            }
            return null;
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            Attribute attr;
            Component ret;
            if (column == 0 || AttributeTable.this.attrs == null) {
                return new JLabel(value.toString());
            }
            if (AttributeTable.this.editorComp != null) {
                AttributeTable.this.editorComp.transferFocus();
            }
            if ((ret = (attr = (Attribute)AttributeTable.this.attrs.getAttributes().get(row)).getCellEditor(AttributeTable.this.parent, AttributeTable.this.attrs.getValue(attr))) instanceof JComboBox) {
                ((JComboBox)ret).addActionListener(this);
            } else {
                if (ret instanceof JInputComponent) {
                    JInputComponent input = (JInputComponent)((Object)ret);
                    Window parent = AttributeTable.this.parent;
                    MyDialog dlog = parent instanceof java.awt.Frame ? new MyDialog((java.awt.Frame)parent, input) : new MyDialog((Dialog)parent, input);
                    dlog.setVisible(true);
                    Object retval = dlog.getValue();
                    AttributeTable.this.listener.valueChangeRequested(attr, retval);
                    return new JLabel(attr.toDisplayString(retval));
                }
                ret.addFocusListener(this);
            }
            AttributeData n = AttributeTable.this.history[AttributeTable.this.history.length - 1];
            if (n == null) {
                n = new AttributeData();
            }
            for (int i = AttributeTable.access$400((AttributeTable)AttributeTable.this).length - 1; i > 0; --i) {
                AttributeTable.access$400((AttributeTable)AttributeTable.this)[i] = AttributeTable.this.history[i - 1];
            }
            n.attrs = AttributeTable.this.attrs;
            n.attr = attr;
            n.comp = ret;
            return ret;
        }

        @Override
        public void focusLost(FocusEvent e) {
            Component dst = e.getOppositeComponent();
            if (dst instanceof Component) {
                for (Component p = dst; p != null && !(p instanceof Window); p = p.getParent()) {
                    if (p != AttributeTable.this) continue;
                    return;
                }
                AttributeTable.this.editor.stopCellEditing();
            }
        }

        @Override
        public void focusGained(FocusEvent e) {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.stopCellEditing();
        }
    }

    private class Model
    implements TableModel {
        LinkedList listeners;
        private Object lastValue;
        private long lastUpdate;

        private Model() {
            this.listeners = new LinkedList();
            this.lastValue = null;
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            this.listeners.add(l);
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            this.listeners.remove(l);
        }

        void fireTableChanged() {
            TableModelEvent e = new TableModelEvent(this);
            for (TableModelListener l : (List)this.listeners.clone()) {
                l.tableChanged(e);
            }
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) {
                return "Attribute";
            }
            return "Value";
        }

        public Class getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return String.class;
            }
            return Object.class;
        }

        @Override
        public int getRowCount() {
            if (AttributeTable.this.attrs == null) {
                return 0;
            }
            return AttributeTable.this.attrs.getAttributes().size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (AttributeTable.this.attrs == null) {
                return null;
            }
            Attribute attr = (Attribute)AttributeTable.this.attrs.getAttributes().get(rowIndex);
            if (attr == null) {
                return null;
            }
            if (columnIndex == 0) {
                return attr.getDisplayName();
            }
            return attr.toDisplayString(AttributeTable.this.attrs.getValue(attr));
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return false;
            }
            if (AttributeTable.this.attrs == null) {
                return false;
            }
            Attribute attr = (Attribute)AttributeTable.this.attrs.getAttributes().get(rowIndex);
            return !AttributeTable.this.attrs.isReadOnly(attr);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Attribute attr = this.findCurrentAttribute();
            if (attr == null) {
                if (AttributeTable.this.attrs == null) {
                    return;
                }
                List attrList = AttributeTable.this.attrs.getAttributes();
                if (rowIndex >= attrList.size()) {
                    return;
                }
                attr = (Attribute)attrList.get(rowIndex);
            }
            if (attr == null || aValue == null) {
                return;
            }
            try {
                if (aValue instanceof String) {
                    aValue = attr.parse((String)aValue);
                }
                AttributeTable.this.listener.valueChangeRequested(attr, aValue);
            }
            catch (ClassCastException e) {
                String msg = Strings.get("attributeChangeInvalidError") + ": " + e;
                JOptionPane.showMessageDialog(AttributeTable.this.proj.getFrame(), msg, Strings.get("attributeChangeInvalidTitle"), 2);
            }
            catch (NumberFormatException e) {
                long now = System.currentTimeMillis();
                if (aValue.equals(this.lastValue) && now < this.lastUpdate + 500) {
                    return;
                }
                this.lastValue = aValue;
                this.lastUpdate = System.currentTimeMillis();
                String msg = Strings.get("attributeChangeInvalidError");
                String emsg = e.getMessage();
                if (emsg != null && emsg.length() > 0) {
                    msg = msg + ": " + emsg;
                }
                msg = msg + ".";
                JOptionPane.showMessageDialog(AttributeTable.this.proj.getFrame(), msg, Strings.get("attributeChangeInvalidTitle"), 2);
            }
        }

        private Attribute findCurrentAttribute() {
            for (int i = 0; i < AttributeTable.this.history.length; ++i) {
                if (AttributeTable.this.history[i] == null || AttributeTable.access$400((AttributeTable)AttributeTable.this)[i].comp != AttributeTable.this.editorComp) continue;
                return AttributeTable.access$400((AttributeTable)AttributeTable.this)[i].attr;
            }
            return null;
        }
    }

    private static class MyDialog
    extends JDialogOk {
        JInputComponent input;
        Object value;

        public MyDialog(Dialog parent, JInputComponent input) {
            super(parent, Strings.get("attributeDialogTitle"), true);
            this.configure(input);
        }

        public MyDialog(java.awt.Frame parent, JInputComponent input) {
            super(parent, Strings.get("attributeDialogTitle"), true);
            this.configure(input);
        }

        private void configure(JInputComponent input) {
            this.input = input;
            this.value = input.getValue();
            JPanel p = new JPanel(new BorderLayout());
            p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            p.add((Component)((JComponent)((Object)input)), "Center");
            this.getContentPane().add((Component)p, "Center");
            this.pack();
        }

        @Override
        public void okClicked() {
            this.value = this.input.getValue();
        }

        public Object getValue() {
            return this.value;
        }
    }

    private static class AttributeData {
        AttributeSet attrs;
        Attribute attr;
        Component comp;

        AttributeData() {
        }
    }

    private class MyListener
    implements AttributeListener {
        private MyListener() {
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
            if (e.getSource() != AttributeTable.this.attrs) {
                e.getSource().removeAttributeListener(this);
                return;
            }
            AttributeTable.this.model.fireTableChanged();
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            if (e.getSource() != AttributeTable.this.attrs) {
                e.getSource().removeAttributeListener(this);
                return;
            }
            AttributeTable.this.model.fireTableChanged();
        }
    }

    private static class ChangeAttributeAction
    extends Action {
        AttributeSet attrs;
        Attribute attr;
        Object oldValue;
        Object newValue;

        ChangeAttributeAction(AttributeSet as, Attribute a, Object o, Object n) {
            this.attrs = as;
            this.attr = a;
            this.oldValue = o;
            this.newValue = n;
        }

        @Override
        public String getName() {
            return Strings.get("changeAttributeAction");
        }

        @Override
        public void doIt(Project proj) {
            this.attrs.setValue(this.attr, this.newValue);
        }

        @Override
        public void undo(Project proj) {
            this.attrs.setValue(this.attr, this.oldValue);
        }
    }

    private class DefaultListener
    implements Listener {
        private DefaultListener() {
        }

        @Override
        public void valueChangeRequested(Attribute attr, Object value) {
            if (AttributeTable.this.attrs == null) {
                return;
            }
            if (!AttributeTable.this.attrs.containsAttribute(attr)) {
                return;
            }
            Object oldValue = AttributeTable.this.attrs.getValue(attr);
            AttributeTable.this.proj.doAction(new ChangeAttributeAction(AttributeTable.this.attrs, attr, oldValue, value));
        }
    }

    public static interface Listener {
        public void valueChangeRequested(Attribute var1, Object var2);
    }

}

