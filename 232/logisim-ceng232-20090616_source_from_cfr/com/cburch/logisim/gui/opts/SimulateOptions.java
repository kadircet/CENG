/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.opts;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.gui.opts.OptionsActions;
import com.cburch.logisim.gui.opts.OptionsFrame;
import com.cburch.logisim.gui.opts.OptionsPanel;
import com.cburch.logisim.gui.opts.Strings;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.IntegerFactory;
import com.cburch.logisim.util.TableLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

class SimulateOptions
extends OptionsPanel {
    private MyListener myListener;
    private JLabel simLimitLabel;
    private JComboBox simLimit;
    private JCheckBox simRandomness;

    public SimulateOptions(OptionsFrame window) {
        super(window);
        this.myListener = new MyListener();
        this.simLimitLabel = new JLabel();
        this.simLimit = new JComboBox<Integer>(new Integer[]{IntegerFactory.create(200), IntegerFactory.create(500), IntegerFactory.create(1000), IntegerFactory.create(2000), IntegerFactory.create(5000), IntegerFactory.create(10000), IntegerFactory.create(20000), IntegerFactory.create(50000)});
        this.simRandomness = new JCheckBox();
        JPanel simLimitPanel = new JPanel();
        simLimitPanel.add(this.simLimitLabel);
        simLimitPanel.add(this.simLimit);
        this.simLimit.addActionListener(this.myListener);
        this.simRandomness.addActionListener(this.myListener);
        this.setLayout(new TableLayout(1));
        this.add(this.simRandomness);
        this.add(simLimitPanel);
        window.getOptions().getAttributeSet().addAttributeListener(this.myListener);
        AttributeSet attrs = this.getOptions().getAttributeSet();
        this.myListener.loadSimLimit((Integer)attrs.getValue(Options.sim_limit_attr));
        this.myListener.loadSimRandomness((Integer)attrs.getValue(Options.sim_rand_attr));
    }

    @Override
    public String getTitle() {
        return Strings.get("simulateTitle");
    }

    @Override
    public String getHelpText() {
        return Strings.get("simulateHelp");
    }

    @Override
    public void localeChanged() {
        this.simRandomness.setText(Strings.get("simulateRandomness"));
        this.simLimitLabel.setText(Strings.get("simulateLimit"));
    }

    private class MyListener
    implements ActionListener,
    AttributeListener {
        private MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Object source = event.getSource();
            if (source == SimulateOptions.this.simLimit) {
                Integer opt = (Integer)SimulateOptions.this.simLimit.getSelectedItem();
                if (opt != null) {
                    AttributeSet attrs = SimulateOptions.this.getOptions().getAttributeSet();
                    SimulateOptions.this.getProject().doAction(OptionsActions.setAttribute(attrs, Options.sim_limit_attr, opt));
                }
            } else if (source == SimulateOptions.this.simRandomness) {
                AttributeSet attrs = SimulateOptions.this.getOptions().getAttributeSet();
                SimulateOptions.this.getProject().doAction(OptionsActions.setAttribute(attrs, Options.sim_rand_attr, SimulateOptions.this.simRandomness.isSelected() ? Options.sim_rand_dflt : IntegerFactory.ZERO));
            }
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            Attribute attr = e.getAttribute();
            Object val = e.getValue();
            if (attr == Options.sim_limit_attr) {
                this.loadSimLimit((Integer)val);
            } else if (attr == Options.sim_rand_attr) {
                this.loadSimRandomness((Integer)val);
            }
        }

        private void loadSimLimit(Integer val) {
            int value = val;
            ComboBoxModel model = SimulateOptions.this.simLimit.getModel();
            for (int i = 0; i < model.getSize(); ++i) {
                Integer opt = (Integer)model.getElementAt(i);
                if (opt != value) continue;
                SimulateOptions.this.simLimit.setSelectedItem(opt);
            }
        }

        private void loadSimRandomness(Integer val) {
            SimulateOptions.this.simRandomness.setSelected(val > 0);
        }
    }

}

