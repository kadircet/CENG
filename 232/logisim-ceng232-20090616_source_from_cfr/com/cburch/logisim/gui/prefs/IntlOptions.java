/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.prefs;

import com.cburch.logisim.gui.prefs.OptionsPanel;
import com.cburch.logisim.gui.prefs.PreferencesFrame;
import com.cburch.logisim.gui.prefs.Strings;
import com.cburch.logisim.proj.LogisimPreferences;
import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.StringGetter;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

class IntlOptions
extends OptionsPanel {
    private MyListener myListener;
    private JLabel localeLabel;
    private JComboBox locale;
    private JCheckBox replaceAccents;
    private JLabel gateShapeLabel;
    private JComboBox gateShape;

    public IntlOptions(PreferencesFrame window) {
        super(window);
        this.myListener = new MyListener();
        this.localeLabel = new JLabel();
        this.locale = new JComboBox();
        this.replaceAccents = new JCheckBox();
        this.gateShapeLabel = new JLabel();
        this.gateShape = new JComboBox();
        JPanel localePanel = new JPanel();
        localePanel.add(this.localeLabel);
        localePanel.add(this.locale);
        JPanel shapePanel = new JPanel();
        shapePanel.add(this.gateShapeLabel);
        shapePanel.add(this.gateShape);
        this.setLayout(new BoxLayout(this, 3));
        this.add(Box.createGlue());
        this.add(shapePanel);
        this.add(localePanel);
        this.add(this.replaceAccents);
        this.add(Box.createGlue());
        Locale[] opts = Strings.getLocaleOptions();
        Locale dfltLocale = LocaleManager.getLocale();
        LocaleOption dfltOpt = null;
        for (int i = 0; i < opts.length; ++i) {
            LocaleOption opt = new LocaleOption(opts[i]);
            if (opts[i].equals(dfltLocale)) {
                dfltOpt = opt;
            }
            this.locale.addItem(opt);
        }
        if (dfltOpt != null) {
            this.locale.setSelectedItem(dfltOpt);
        }
        this.locale.addActionListener(this.myListener);
        this.replaceAccents.addActionListener(this.myListener);
        LogisimPreferences.addPropertyChangeListener("accentsReplace", this.myListener);
        this.replaceAccents.setSelected(LogisimPreferences.getAccentsReplace());
        this.gateShape.addItem(new ShapeOption("shaped", Strings.getter("shapeShaped")));
        this.gateShape.addItem(new ShapeOption("rectangular", Strings.getter("shapeRectangular")));
        this.gateShape.addItem(new ShapeOption("din40700", Strings.getter("shapeDIN40700")));
        this.gateShape.addActionListener(this.myListener);
        LogisimPreferences.addPropertyChangeListener("gateShape", this.myListener);
        this.setGateSelection(LogisimPreferences.getGateShape());
    }

    @Override
    public String getTitle() {
        return Strings.get("intlTitle");
    }

    @Override
    public String getHelpText() {
        return Strings.get("intlHelp");
    }

    @Override
    public void localeChanged() {
        this.localeLabel.setText(Strings.get("intlLocale"));
        this.replaceAccents.setText(Strings.get("intlReplaceAccents"));
        this.replaceAccents.setEnabled(LocaleManager.canReplaceAccents());
        this.gateShapeLabel.setText(Strings.get("intlGateShape"));
        Locale selectedLocale = LocaleManager.getLocale();
        ComboBoxModel model = this.locale.getModel();
        for (int n = model.getSize() - 1; n >= 0; --n) {
            LocaleOption opt = (LocaleOption)model.getElementAt(n);
            if (opt.locale != selectedLocale) continue;
            this.locale.setSelectedItem(opt);
        }
    }

    private void setGateSelection(String value) {
        for (int i = this.gateShape.getItemCount() - 1; i >= 0; --i) {
            ShapeOption opt = (ShapeOption)this.gateShape.getItemAt(i);
            if (!opt.value.equals(value)) continue;
            this.gateShape.setSelectedItem(opt);
            return;
        }
        this.gateShape.setSelectedItem(this.gateShape.getItemAt(0));
    }

    private class MyListener
    implements ActionListener,
    PropertyChangeListener {
        private MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == IntlOptions.this.gateShape) {
                ShapeOption x = (ShapeOption)IntlOptions.this.gateShape.getSelectedItem();
                LogisimPreferences.setGateShape(x.value);
            } else if (src == IntlOptions.this.locale) {
                LocaleOption opt = (LocaleOption)IntlOptions.this.locale.getSelectedItem();
                if (opt != null) {
                    LocaleManager.setLocale(opt.locale);
                }
            } else if (src == IntlOptions.this.replaceAccents) {
                LogisimPreferences.setAccentsReplace(IntlOptions.this.replaceAccents.isSelected());
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String prop = event.getPropertyName();
            if (prop.equals("accentsReplace")) {
                IntlOptions.this.replaceAccents.setSelected(LogisimPreferences.getAccentsReplace());
            } else if (prop.equals("gateShape")) {
                IntlOptions.this.setGateSelection(LogisimPreferences.getGateShape());
            }
        }
    }

    private static class ShapeOption {
        private String value;
        private StringGetter getter;

        ShapeOption(String value, StringGetter getter) {
            this.value = value;
            this.getter = getter;
        }

        public String toString() {
            return this.getter.get();
        }
    }

    private static class LocaleOption {
        private Locale locale;

        LocaleOption(Locale locale) {
            this.locale = locale;
        }

        public String toString() {
            return this.locale.getDisplayName();
        }
    }

}

