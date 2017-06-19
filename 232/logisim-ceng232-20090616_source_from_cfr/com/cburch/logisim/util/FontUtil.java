/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.IntegerFactory;
import com.cburch.logisim.util.JInputComponent;
import com.cburch.logisim.util.Strings;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.border.Border;

public class FontUtil {
    public static JPanel createFontChooser(Font dflt) {
        return new FontChooser(dflt);
    }

    public static String toStyleStandardString(int style) {
        switch (style) {
            case 0: {
                return "plain";
            }
            case 2: {
                return "italic";
            }
            case 1: {
                return "bold";
            }
            case 3: {
                return "bolditalic";
            }
        }
        return "??";
    }

    public static String toStyleDisplayString(int style) {
        switch (style) {
            case 0: {
                return Strings.get("fontPlainStyle");
            }
            case 2: {
                return Strings.get("fontItalicStyle");
            }
            case 1: {
                return Strings.get("fontBoldStyle");
            }
            case 3: {
                return Strings.get("fontBoldItalicStyle");
            }
        }
        return "??";
    }

    private static class StyleItem {
        int style;

        StyleItem(int style) {
            this.style = style;
        }

        public String toString() {
            return FontUtil.toStyleDisplayString(this.style);
        }
    }

    private static class FontChooser
    extends JPanel
    implements JInputComponent {
        JList font = new JList<String>(new String[]{"Monospaced", "Serif", "SansSerif"});
        JComboBox size = new JComboBox<Integer>(new Integer[]{IntegerFactory.create(10), IntegerFactory.create(12), IntegerFactory.create(14), IntegerFactory.create(16), IntegerFactory.create(18), IntegerFactory.create(24)});
        JList style = new JList<StyleItem>(new StyleItem[]{new StyleItem(0), new StyleItem(2), new StyleItem(1), new StyleItem(3)});

        public FontChooser(Font dflt) {
            Border border = BorderFactory.createEtchedBorder();
            this.font.setSelectionMode(0);
            this.font.setBorder(border);
            this.size.setEditable(true);
            this.size.setEditor(new IntegerComboBoxEditor(this.size.getEditor()));
            this.style.setSelectionMode(0);
            this.style.setBorder(border);
            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            this.setLayout(gridbag);
            c.anchor = 18;
            c.fill = 0;
            c.ipadx = 10;
            c.ipady = 5;
            c.weighty = 1.0;
            c.gridx = 0;
            JLabel lab = new JLabel(Strings.get("fontDlogFontLabel"));
            c.gridx = 0;
            gridbag.setConstraints(lab, c);
            this.add(lab);
            lab = new JLabel(Strings.get("fontDlogStyleLabel"));
            c.gridx = 1;
            gridbag.setConstraints(lab, c);
            this.add(lab);
            lab = new JLabel(Strings.get("fontDlogSizeLabel"));
            c.gridx = 2;
            gridbag.setConstraints(lab, c);
            this.add(lab);
            c.gridy = 1;
            c.gridx = 0;
            gridbag.setConstraints(this.font, c);
            this.add(this.font);
            c.gridx = 1;
            gridbag.setConstraints(this.style, c);
            this.add(this.style);
            c.gridx = 2;
            gridbag.setConstraints(this.size, c);
            this.add(this.size);
            this.setValue(dflt);
        }

        @Override
        public void setValue(Object raw_val) {
            Font val;
            if (raw_val instanceof String) {
                val = Font.decode((String)raw_val);
            } else if (raw_val instanceof Font) {
                val = (Font)raw_val;
            } else {
                return;
            }
            this.font.setSelectedValue(val.getName(), true);
            this.size.setSelectedItem(IntegerFactory.create(val.getSize()));
            ListModel model = this.style.getModel();
            for (int i = 0; i < model.getSize(); ++i) {
                StyleItem s = (StyleItem)model.getElementAt(i);
                if (s.style != val.getStyle()) continue;
                this.style.setSelectedValue(s, true);
            }
        }

        @Override
        public Object getValue() {
            Object name_val = this.font.getSelectedValue();
            String fname = name_val == null ? null : name_val.toString();
            int fsize = (Integer)this.size.getSelectedItem();
            int fstyle = ((StyleItem)this.style.getSelectedValue()).style;
            Font ret = new Font(fname, fstyle, fsize);
            return ret;
        }
    }

    private static class IntegerComboBoxEditor
    implements ComboBoxEditor {
        private ComboBoxEditor parent;
        private Object oldval;

        private IntegerComboBoxEditor(ComboBoxEditor parent) {
            this.parent = parent;
            this.oldval = parent.getItem();
        }

        @Override
        public void addActionListener(ActionListener l) {
            this.parent.addActionListener(l);
        }

        @Override
        public void removeActionListener(ActionListener l) {
            this.parent.removeActionListener(l);
        }

        @Override
        public Component getEditorComponent() {
            return this.parent.getEditorComponent();
        }

        @Override
        public void selectAll() {
            this.parent.selectAll();
        }

        @Override
        public Object getItem() {
            Object ret = this.parent.getItem();
            if (ret instanceof Integer) {
                return ret;
            }
            String str = ret.toString();
            try {
                return IntegerFactory.create(str);
            }
            catch (NumberFormatException e) {
                return this.oldval;
            }
        }

        @Override
        public void setItem(Object anObject) {
            this.parent.setItem(anObject);
            this.oldval = anObject;
        }
    }

}

