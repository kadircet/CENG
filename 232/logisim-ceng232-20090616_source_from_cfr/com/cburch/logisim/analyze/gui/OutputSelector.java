/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.gui.Strings;
import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.analyze.model.VariableList;
import com.cburch.logisim.analyze.model.VariableListEvent;
import com.cburch.logisim.analyze.model.VariableListListener;
import java.awt.Component;
import java.awt.event.ItemListener;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

class OutputSelector
extends JPanel {
    private VariableList source;
    private JLabel label = new JLabel();
    private JComboBox select = new JComboBox();
    private String prototypeValue = null;

    public OutputSelector(AnalyzerModel model) {
        this.source = model.getOutputs();
        Model listModel = new Model();
        this.select.setModel(listModel);
        this.source.addVariableListListener(listModel);
        this.add(this.label);
        this.add(this.select);
    }

    void localeChanged() {
        this.label.setText(Strings.get("outputSelectLabel"));
    }

    public void addItemListener(ItemListener l) {
        this.select.addItemListener(l);
    }

    public void removeItemListener(ItemListener l) {
        this.select.removeItemListener(l);
    }

    public String getSelectedOutput() {
        return (String)this.select.getSelectedItem();
    }

    private void computePrototypeValue() {
        String newValue;
        if (this.source.isEmpty()) {
            newValue = "xx";
        } else {
            newValue = "xx";
            int n = this.source.size();
            for (int i = 0; i < n; ++i) {
                String candidate = this.source.get(i);
                if (candidate.length() <= newValue.length()) continue;
                newValue = candidate;
            }
        }
        if (this.prototypeValue == null || newValue.length() != this.prototypeValue.length()) {
            this.prototypeValue = newValue;
            this.select.setPrototypeDisplayValue(this.prototypeValue + "xx");
            this.revalidate();
        }
    }

    private class Model
    extends AbstractListModel
    implements ComboBoxModel,
    VariableListListener {
        private Object selected;

        private Model() {
        }

        @Override
        public void setSelectedItem(Object value) {
            this.selected = value;
        }

        @Override
        public Object getSelectedItem() {
            return this.selected;
        }

        @Override
        public int getSize() {
            return OutputSelector.this.source.size();
        }

        @Override
        public Object getElementAt(int index) {
            return OutputSelector.this.source.get(index);
        }

        @Override
        public void listChanged(VariableListEvent event) {
            switch (event.getType()) {
                case 0: {
                    OutputSelector.this.computePrototypeValue();
                    this.fireContentsChanged(this, 0, this.getSize());
                    if (OutputSelector.this.source.isEmpty()) {
                        OutputSelector.this.select.setSelectedItem(null);
                        break;
                    }
                    OutputSelector.this.select.setSelectedItem(OutputSelector.this.source.get(0));
                    break;
                }
                case 1: {
                    String variable = event.getVariable();
                    if (OutputSelector.this.prototypeValue == null || variable.length() > OutputSelector.this.prototypeValue.length()) {
                        OutputSelector.this.computePrototypeValue();
                    }
                    int index = OutputSelector.this.source.indexOf(variable);
                    this.fireIntervalAdded(this, index, index);
                    if (OutputSelector.this.select.getSelectedItem() != null) break;
                    OutputSelector.this.select.setSelectedItem(variable);
                    break;
                }
                case 2: {
                    String variable = event.getVariable();
                    if (variable.equals(OutputSelector.this.prototypeValue)) {
                        OutputSelector.this.computePrototypeValue();
                    }
                    int index = (Integer)event.getData();
                    this.fireIntervalRemoved(this, index, index);
                    Object selection = OutputSelector.this.select.getSelectedItem();
                    if (selection == null || !selection.equals(variable)) break;
                    selection = OutputSelector.this.source.isEmpty() ? null : OutputSelector.this.source.get(0);
                    OutputSelector.this.select.setSelectedItem(selection);
                    break;
                }
                case 3: {
                    this.fireContentsChanged(this, 0, this.getSize());
                    break;
                }
                case 4: {
                    String variable = event.getVariable();
                    if (variable.equals(OutputSelector.this.prototypeValue)) {
                        OutputSelector.this.computePrototypeValue();
                    }
                    int index = (Integer)event.getData();
                    this.fireContentsChanged(this, index, index);
                    Object selection = OutputSelector.this.select.getSelectedItem();
                    if (selection == null || !selection.equals(variable)) break;
                    OutputSelector.this.select.setSelectedItem(event.getSource().get(index));
                }
            }
        }
    }

}

