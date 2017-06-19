/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.opts;

import com.cburch.logisim.circuit.RadixOption;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.gui.opts.OptionsActions;
import com.cburch.logisim.gui.opts.OptionsFrame;
import com.cburch.logisim.gui.opts.OptionsPanel;
import com.cburch.logisim.gui.opts.Strings;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.StringGetter;
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

class CanvasOptions
extends OptionsPanel {
    private MyListener myListener;
    private BooleanOption[] checks;
    private JLabel zoomLabel;
    private JComboBox zoom;
    private JLabel radix1Label;
    private JComboBox radix1;
    private JLabel radix2Label;
    private JComboBox radix2;

    public CanvasOptions(OptionsFrame window) {
        super(window);
        this.myListener = new MyListener();
        this.checks = new BooleanOption[]{new BooleanOption(Options.preview_attr, Strings.getter("canvasPrinterView")), new BooleanOption(Options.showgrid_attr, Strings.getter("canvasShowGrid")), new BooleanOption(Options.showhalo_attr, Strings.getter("canvasShowHalo")), new BooleanOption(Options.showghosts_attr, Strings.getter("canvasShowGhosts")), new BooleanOption(Options.showtips_attr, Strings.getter("canvasShowTips"))};
        this.zoomLabel = new JLabel();
        this.zoom = new JComboBox<ZoomOption>(new ZoomOption[]{new ZoomOption("1:1", 1.0), new ZoomOption("1:1.33", 1.33), new ZoomOption("1:1.5", 1.5), new ZoomOption("1:2", 2.0), new ZoomOption("1.33:1", 0.75), new ZoomOption("2:1", 0.5), new ZoomOption("5:1", 0.2)});
        this.radix1Label = new JLabel();
        this.radix2Label = new JLabel();
        AttributeSet attrs = window.getProject().getOptions().getAttributeSet();
        for (int i = 0; i < 2; ++i) {
            RadixOption opt = (RadixOption)attrs.getValue(i == 0 ? Options.ATTR_RADIX_1 : Options.ATTR_RADIX_2);
            RadixOption[] opts = RadixOption.OPTIONS;
            RadixOpt[] items = new RadixOpt[opts.length];
            RadixOpt item = null;
            for (int j = 0; j < RadixOption.OPTIONS.length; ++j) {
                items[j] = new RadixOpt(opts[j]);
                if (opts[j] != opt) continue;
                item = items[j];
            }
            JComboBox<RadixOpt> box = new JComboBox<RadixOpt>(items);
            if (item != null) {
                box.setSelectedItem(item);
            }
            box.addActionListener(this.myListener);
            if (i == 0) {
                this.radix1 = box;
                continue;
            }
            this.radix2 = box;
        }
        JPanel panel = new JPanel(new TableLayout(2));
        panel.add(this.zoomLabel);
        panel.add(this.zoom);
        this.zoom.addActionListener(this.myListener);
        panel.add(this.radix1Label);
        panel.add(this.radix1);
        panel.add(this.radix2Label);
        panel.add(this.radix2);
        this.setLayout(new TableLayout(1));
        for (int i2 = 0; i2 < this.checks.length; ++i2) {
            this.add(this.checks[i2]);
        }
        this.add(panel);
        window.getOptions().getAttributeSet().addAttributeListener(this.myListener);
        this.myListener.loadZoom((Double)this.getOptions().getAttributeSet().getValue(Options.zoom_attr));
    }

    @Override
    public String getTitle() {
        return Strings.get("canvasTitle");
    }

    @Override
    public String getHelpText() {
        return Strings.get("canvasHelp");
    }

    @Override
    public void localeChanged() {
        for (int i = 0; i < this.checks.length; ++i) {
            this.checks[i].localeChanged();
        }
        this.zoomLabel.setText(Strings.get("canvasZoom"));
        this.radix1Label.setText(Strings.get("canvasRadix1"));
        this.radix2Label.setText(Strings.get("canvasRadix2"));
    }

    private class BooleanOption
    extends JCheckBox
    implements ActionListener {
        Attribute attr;
        StringGetter title;

        BooleanOption(Attribute attr, StringGetter title) {
            super(title.get());
            this.attr = attr;
            this.title = title;
            this.addActionListener(this);
            Boolean cur = (Boolean)CanvasOptions.this.getLogisimFile().getOptions().getAttributeSet().getValue(attr);
            if (cur != null) {
                this.setSelected(cur);
            }
        }

        void localeChanged() {
            this.setText(this.title.get());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            AttributeSet attrs = CanvasOptions.this.getLogisimFile().getOptions().getAttributeSet();
            CanvasOptions.this.getProject().doAction(OptionsActions.setAttribute(attrs, this.attr, this.isSelected()));
        }

        public void attributeChanged(AttributeEvent e) {
            if (e.getAttribute() == this.attr) {
                this.setSelected((Boolean)e.getValue());
            }
        }
    }

    private class MyListener
    implements ActionListener,
    AttributeListener {
        private MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            RadixOpt opt;
            Object source = event.getSource();
            if (source == CanvasOptions.this.zoom) {
                ZoomOption opt2 = (ZoomOption)CanvasOptions.this.zoom.getSelectedItem();
                if (opt2 != null) {
                    AttributeSet attrs = CanvasOptions.this.getOptions().getAttributeSet();
                    CanvasOptions.this.getProject().doAction(OptionsActions.setAttribute(attrs, Options.zoom_attr, opt2.ratio));
                }
            } else if (source == CanvasOptions.this.radix1) {
                RadixOpt opt3 = (RadixOpt)CanvasOptions.this.radix1.getSelectedItem();
                if (opt3 != null) {
                    AttributeSet attrs = CanvasOptions.this.getOptions().getAttributeSet();
                    CanvasOptions.this.getProject().doAction(OptionsActions.setAttribute(attrs, Options.ATTR_RADIX_1, opt3.value));
                }
            } else if (source == CanvasOptions.this.radix2 && (opt = (RadixOpt)CanvasOptions.this.radix2.getSelectedItem()) != null) {
                AttributeSet attrs = CanvasOptions.this.getOptions().getAttributeSet();
                CanvasOptions.this.getProject().doAction(OptionsActions.setAttribute(attrs, Options.ATTR_RADIX_2, opt.value));
            }
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            for (int i = 0; i < CanvasOptions.this.checks.length; ++i) {
                CanvasOptions.this.checks[i].attributeChanged(e);
            }
            Attribute attr = e.getAttribute();
            Object val = e.getValue();
            if (attr == Options.zoom_attr) {
                this.loadZoom((Double)val);
            }
        }

        private void loadZoom(Double val) {
            double value = val;
            ComboBoxModel model = CanvasOptions.this.zoom.getModel();
            for (int i = 0; i < model.getSize(); ++i) {
                ZoomOption opt = (ZoomOption)model.getElementAt(i);
                if (Math.abs(opt.ratio - value) >= 0.01) continue;
                CanvasOptions.this.zoom.setSelectedItem(opt);
            }
        }
    }

    private static class RadixOpt {
        private RadixOption value;

        RadixOpt(RadixOption value) {
            this.value = value;
        }

        public String toString() {
            return this.value.toDisplayString();
        }
    }

    private static class ZoomOption {
        private String title;
        private Double ratio;

        ZoomOption(String title, double ratio) {
            this.title = title;
            this.ratio = new Double(ratio);
        }

        public String toString() {
            return this.title;
        }
    }

}

