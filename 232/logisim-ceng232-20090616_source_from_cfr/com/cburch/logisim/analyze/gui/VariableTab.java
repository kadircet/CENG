/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.gui.DefaultRegistry;
import com.cburch.logisim.analyze.gui.Strings;
import com.cburch.logisim.analyze.gui.TabInterface;
import com.cburch.logisim.analyze.model.VariableList;
import com.cburch.logisim.analyze.model.VariableListEvent;
import com.cburch.logisim.analyze.model.VariableListListener;
import com.cburch.logisim.util.StringUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

class VariableTab
extends JPanel
implements TabInterface {
    private VariableList data;
    private MyListener myListener;
    private JList list;
    private JTextField field;
    private JButton remove;
    private JButton moveUp;
    private JButton moveDown;
    private JButton add;
    private JButton rename;
    private JLabel error;

    VariableTab(VariableList data) {
        this.myListener = new MyListener();
        this.list = new JList();
        this.field = new JTextField();
        this.remove = new JButton();
        this.moveUp = new JButton();
        this.moveDown = new JButton();
        this.add = new JButton();
        this.rename = new JButton();
        this.error = new JLabel(" ");
        this.data = data;
        this.list.setModel(new VariableListModel(data));
        this.list.setSelectionMode(0);
        this.list.addListSelectionListener(this.myListener);
        this.remove.addActionListener(this.myListener);
        this.moveUp.addActionListener(this.myListener);
        this.moveDown.addActionListener(this.myListener);
        this.add.addActionListener(this.myListener);
        this.rename.addActionListener(this.myListener);
        this.field.addActionListener(this.myListener);
        this.field.getDocument().addDocumentListener(this.myListener);
        JScrollPane listPane = new JScrollPane(this.list, 22, 31);
        listPane.setPreferredSize(new Dimension(100, 100));
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.add(this.remove);
        topPanel.add(this.moveUp);
        topPanel.add(this.moveDown);
        JPanel fieldPanel = new JPanel();
        fieldPanel.add(this.rename);
        fieldPanel.add(this.add);
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        this.setLayout(gb);
        Insets oldInsets = gc.insets;
        gc.insets = new Insets(10, 10, 0, 0);
        gc.fill = 1;
        gc.weightx = 1.0;
        gb.setConstraints(listPane, gc);
        this.add(listPane);
        gc.fill = 0;
        gc.anchor = 19;
        gc.weightx = 0.0;
        gb.setConstraints(topPanel, gc);
        this.add(topPanel);
        gc.insets = new Insets(10, 10, 0, 10);
        gc.gridwidth = 0;
        gc.gridx = 0;
        gc.gridy = -1;
        gc.fill = 2;
        gb.setConstraints(this.field, gc);
        this.add(this.field);
        gc.insets = oldInsets;
        gc.fill = 0;
        gc.anchor = 22;
        gb.setConstraints(fieldPanel, gc);
        this.add(fieldPanel);
        gc.fill = 2;
        gb.setConstraints(this.error, gc);
        this.add(this.error);
        if (!data.isEmpty()) {
            this.list.setSelectedValue(data.get(0), true);
        }
        this.computeEnabled();
    }

    void localeChanged() {
        this.remove.setText(Strings.get("variableRemoveButton"));
        this.moveUp.setText(Strings.get("variableMoveUpButton"));
        this.moveDown.setText(Strings.get("variableMoveDownButton"));
        this.add.setText(Strings.get("variableAddButton"));
        this.rename.setText(Strings.get("variableRenameButton"));
        this.validateInput();
    }

    void registerDefaultButtons(DefaultRegistry registry) {
        registry.registerDefaultButton(this.field, this.add);
    }

    private void computeEnabled() {
        String name = (String)this.list.getSelectedValue();
        int index = this.list.getSelectedIndex();
        this.remove.setEnabled(name != null);
        this.moveUp.setEnabled(name != null && index > 0);
        this.moveDown.setEnabled(name != null && index < this.data.size() - 1);
        boolean ok = this.validateInput();
        this.add.setEnabled(ok && this.data.size() < this.data.getMaximumSize());
        this.rename.setEnabled(ok && name != null);
    }

    private boolean validateInput() {
        int i;
        String text = this.field.getText().trim();
        boolean ok = true;
        boolean errorShown = true;
        if (text.length() == 0) {
            errorShown = false;
            ok = false;
        } else if (!Character.isJavaIdentifierStart(text.charAt(0))) {
            this.error.setText(Strings.get("variableStartError"));
            ok = false;
        } else {
            for (i = 1; i < text.length() && ok; ++i) {
                char c = text.charAt(i);
                if (Character.isJavaIdentifierPart(c)) continue;
                this.error.setText(StringUtil.format(Strings.get("variablePartError"), "" + c));
                ok = false;
            }
        }
        if (ok) {
            int n = this.data.size();
            for (i = 0; i < n && ok; ++i) {
                String other = this.data.get(i);
                if (!text.equals(other)) continue;
                this.error.setText(Strings.get("variableDuplicateError"));
                ok = false;
            }
        }
        if (ok || !errorShown) {
            if (this.data.size() >= this.data.getMaximumSize()) {
                this.error.setText(StringUtil.format(Strings.get("variableMaximumError"), "" + this.data.getMaximumSize()));
            } else {
                this.error.setText(" ");
            }
        }
        return ok;
    }

    @Override
    public void copy() {
        this.field.requestFocus();
        this.field.copy();
    }

    @Override
    public void paste() {
        this.field.requestFocus();
        this.field.paste();
    }

    @Override
    public void delete() {
        this.field.requestFocus();
        this.field.replaceSelection("");
    }

    @Override
    public void selectAll() {
        this.field.requestFocus();
        this.field.selectAll();
    }

    private class MyListener
    implements ActionListener,
    DocumentListener,
    ListSelectionListener {
        private MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            String name;
            Object src = event.getSource();
            if ((src == VariableTab.this.add || src == VariableTab.this.field) && VariableTab.this.add.isEnabled()) {
                String name2 = VariableTab.this.field.getText().trim();
                if (!name2.equals("")) {
                    VariableTab.this.data.add(name2);
                    VariableTab.this.list.setSelectedValue(name2, true);
                    VariableTab.this.field.setText("");
                    VariableTab.this.field.grabFocus();
                }
            } else if (src == VariableTab.this.rename) {
                String oldName = (String)VariableTab.this.list.getSelectedValue();
                String newName = VariableTab.this.field.getText().trim();
                if (oldName != null && !newName.equals("")) {
                    VariableTab.this.data.replace(oldName, newName);
                    VariableTab.this.field.setText("");
                    VariableTab.this.field.grabFocus();
                }
            } else if (src == VariableTab.this.remove) {
                String name3 = (String)VariableTab.this.list.getSelectedValue();
                if (name3 != null) {
                    VariableTab.this.data.remove(name3);
                }
            } else if (src == VariableTab.this.moveUp) {
                String name4 = (String)VariableTab.this.list.getSelectedValue();
                if (name4 != null) {
                    VariableTab.this.data.move(name4, -1);
                    VariableTab.this.list.setSelectedValue(name4, true);
                }
            } else if (src == VariableTab.this.moveDown && (name = (String)VariableTab.this.list.getSelectedValue()) != null) {
                VariableTab.this.data.move(name, 1);
                VariableTab.this.list.setSelectedValue(name, true);
            }
        }

        @Override
        public void insertUpdate(DocumentEvent event) {
            VariableTab.this.computeEnabled();
        }

        @Override
        public void removeUpdate(DocumentEvent event) {
            this.insertUpdate(event);
        }

        @Override
        public void changedUpdate(DocumentEvent event) {
            this.insertUpdate(event);
        }

        @Override
        public void valueChanged(ListSelectionEvent event) {
            VariableTab.this.computeEnabled();
        }

        public void listChanged(VariableListEvent event) {
            switch (event.getType()) {
                case 0: {
                    VariableTab.this.list.setSelectedValue(null, false);
                    break;
                }
                case 2: {
                    if (!event.getVariable().equals(VariableTab.this.list.getSelectedValue())) break;
                    int index = (Integer)event.getData();
                    if (index >= VariableTab.this.data.size()) {
                        if (VariableTab.this.data.isEmpty()) {
                            VariableTab.this.list.setSelectedValue(null, false);
                        }
                        index = VariableTab.this.data.size() - 1;
                    }
                    VariableTab.this.list.setSelectedValue(VariableTab.this.data.get(index), true);
                    break;
                }
            }
            VariableTab.this.list.validate();
        }
    }

    private static class VariableListModel
    extends AbstractListModel
    implements VariableListListener {
        private VariableList list;

        public VariableListModel(VariableList list) {
            this.list = list;
            list.addVariableListListener(this);
        }

        @Override
        public int getSize() {
            return this.list.size();
        }

        @Override
        public Object getElementAt(int index) {
            return this.list.get(index);
        }

        @Override
        public void listChanged(VariableListEvent event) {
            switch (event.getType()) {
                case 0: {
                    this.fireContentsChanged(this, 0, this.getSize());
                    return;
                }
                case 1: {
                    int index = this.list.indexOf(event.getVariable());
                    this.fireIntervalAdded(this, index, index);
                    return;
                }
                case 2: {
                    int index = (Integer)event.getData();
                    this.fireIntervalRemoved(this, index, index);
                    return;
                }
                case 3: {
                    this.fireContentsChanged(this, 0, this.getSize());
                    return;
                }
                case 4: {
                    int index = (Integer)event.getData();
                    this.fireContentsChanged(this, index, index);
                    return;
                }
            }
        }
    }

}

