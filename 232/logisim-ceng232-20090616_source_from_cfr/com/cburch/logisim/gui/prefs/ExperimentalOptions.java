/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.prefs;

import com.cburch.logisim.gui.prefs.OptionsPanel;
import com.cburch.logisim.gui.prefs.PreferencesFrame;
import com.cburch.logisim.gui.prefs.Strings;
import com.cburch.logisim.proj.LogisimPreferences;
import com.cburch.logisim.util.StringGetter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

class ExperimentalOptions
extends OptionsPanel {
    private MyListener myListener;
    private JLabel accelLabel;
    private JLabel accelRestart;
    private JComboBox accel;
    private JCheckBox stretchWires;

    public ExperimentalOptions(PreferencesFrame window) {
        super(window);
        this.myListener = new MyListener();
        this.accelLabel = new JLabel();
        this.accelRestart = new JLabel();
        this.accel = new JComboBox();
        this.stretchWires = new JCheckBox();
        JPanel accelPanel = new JPanel(new BorderLayout());
        accelPanel.add((Component)this.accelLabel, "Before");
        accelPanel.add((Component)this.accel, "Center");
        accelPanel.add((Component)this.accelRestart, "Last");
        this.accelRestart.setFont(this.accelRestart.getFont().deriveFont(2));
        JPanel accelPanel2 = new JPanel();
        accelPanel2.add(accelPanel);
        this.setLayout(new BoxLayout(this, 3));
        this.add(Box.createGlue());
        this.add(accelPanel2);
        this.add(Box.createGlue());
        this.add(this.stretchWires);
        this.add(Box.createGlue());
        this.stretchWires.addActionListener(this.myListener);
        LogisimPreferences.addPropertyChangeListener("stretchWires", this.myListener);
        this.stretchWires.setSelected(LogisimPreferences.getStretchWires());
        this.accel.addItem(new AccelOption("default", Strings.getter("accelDefault")));
        this.accel.addItem(new AccelOption("none", Strings.getter("accelNone")));
        this.accel.addItem(new AccelOption("opengl", Strings.getter("accelOpenGL")));
        this.accel.addItem(new AccelOption("d3d", Strings.getter("accelD3D")));
        this.accel.addActionListener(this.myListener);
        LogisimPreferences.addPropertyChangeListener("graphicsAcceleration", this.myListener);
        this.setAccel(LogisimPreferences.getGraphicsAcceleration());
    }

    @Override
    public String getTitle() {
        return Strings.get("experimentTitle");
    }

    @Override
    public String getHelpText() {
        return Strings.get("experimentHelp");
    }

    @Override
    public void localeChanged() {
        this.stretchWires.setText(Strings.get("stretchWires"));
        this.accelLabel.setText(Strings.get("accelLabel"));
        this.accelRestart.setText(Strings.get("accelRestartLabel"));
    }

    private void setAccel(String value) {
        for (int i = this.accel.getItemCount() - 1; i >= 0; --i) {
            AccelOption opt = (AccelOption)this.accel.getItemAt(i);
            if (!opt.value.equals(value)) continue;
            this.accel.setSelectedItem(opt);
            return;
        }
        this.accel.setSelectedItem(this.accel.getItemAt(0));
    }

    private class MyListener
    implements ActionListener,
    PropertyChangeListener {
        private MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == ExperimentalOptions.this.accel) {
                AccelOption x = (AccelOption)ExperimentalOptions.this.accel.getSelectedItem();
                LogisimPreferences.setGraphicsAcceleration(x.value);
            } else if (src == ExperimentalOptions.this.stretchWires) {
                LogisimPreferences.setStretchWires(ExperimentalOptions.this.stretchWires.isSelected());
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String prop = event.getPropertyName();
            if (prop.equals("stretchWires")) {
                ExperimentalOptions.this.stretchWires.setSelected(LogisimPreferences.getStretchWires());
            }
        }
    }

    private static class AccelOption {
        private String value;
        private StringGetter getter;

        AccelOption(String value, StringGetter getter) {
            this.value = value;
            this.getter = getter;
        }

        public String toString() {
            return this.getter.get();
        }
    }

}

