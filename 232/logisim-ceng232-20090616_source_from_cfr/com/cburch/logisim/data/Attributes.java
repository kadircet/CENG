/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.data;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeOptionInterface;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Strings;
import com.cburch.logisim.util.FontUtil;
import com.cburch.logisim.util.IntegerFactory;
import com.cburch.logisim.util.JInputComponent;
import com.cburch.logisim.util.StringGetter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class Attributes {
    private Attributes() {
    }

    private static StringGetter getter(String s) {
        return new ConstantGetter(s);
    }

    public static Attribute forString(String name) {
        return Attributes.forString(name, Attributes.getter(name));
    }

    public static Attribute forOption(String name, Object[] vals) {
        return Attributes.forOption(name, Attributes.getter(name), vals);
    }

    public static Attribute forInteger(String name) {
        return Attributes.forInteger(name, Attributes.getter(name));
    }

    public static Attribute forHexInteger(String name) {
        return Attributes.forHexInteger(name, Attributes.getter(name));
    }

    public static Attribute forIntegerRange(String name, int start, int end) {
        return Attributes.forIntegerRange(name, Attributes.getter(name), start, end);
    }

    public static Attribute forDouble(String name) {
        return Attributes.forDouble(name, Attributes.getter(name));
    }

    public static Attribute forBoolean(String name) {
        return Attributes.forBoolean(name, Attributes.getter(name));
    }

    public static Attribute forDirection(String name) {
        return Attributes.forDirection(name, Attributes.getter(name));
    }

    public static Attribute forBitWidth(String name) {
        return Attributes.forBitWidth(name, Attributes.getter(name));
    }

    public static Attribute forBitWidth(String name, int min, int max) {
        return Attributes.forBitWidth(name, Attributes.getter(name), min, max);
    }

    public static Attribute forFont(String name) {
        return Attributes.forFont(name, Attributes.getter(name));
    }

    public static Attribute forLocation(String name) {
        return Attributes.forLocation(name, Attributes.getter(name));
    }

    public static Attribute forColor(String name) {
        return Attributes.forColor(name, Attributes.getter(name));
    }

    public static Attribute forString(String name, StringGetter disp) {
        return new StringAttribute(name, disp);
    }

    public static Attribute forOption(String name, StringGetter disp, Object[] vals) {
        return new OptionAttribute(name, disp, vals);
    }

    public static Attribute forInteger(String name, StringGetter disp) {
        return new IntegerAttribute(name, disp);
    }

    public static Attribute forHexInteger(String name, StringGetter disp) {
        return new HexIntegerAttribute(name, disp);
    }

    public static Attribute forIntegerRange(String name, StringGetter disp, int start, int end) {
        return new IntegerRangeAttribute(name, disp, start, end);
    }

    public static Attribute forDouble(String name, StringGetter disp) {
        return new DoubleAttribute(name, disp);
    }

    public static Attribute forBoolean(String name, StringGetter disp) {
        return new BooleanAttribute(name, disp);
    }

    public static Attribute forDirection(String name, StringGetter disp) {
        return new DirectionAttribute(name, disp);
    }

    public static Attribute forBitWidth(String name, StringGetter disp) {
        return new BitWidth.Attribute(name, disp);
    }

    public static Attribute forBitWidth(String name, StringGetter disp, int min, int max) {
        return new BitWidth.Attribute(name, disp, min, max);
    }

    public static Attribute forFont(String name, StringGetter disp) {
        return new FontAttribute(name, disp);
    }

    public static Attribute forLocation(String name, StringGetter disp) {
        return new LocationAttribute(name, disp);
    }

    public static Attribute forColor(String name, StringGetter disp) {
        return new ColorAttribute(name, disp);
    }

    private static class ColorChooser
    extends JColorChooser
    implements JInputComponent {
        ColorChooser(Color initial) {
            super(initial);
        }

        @Override
        public Object getValue() {
            return this.getColor();
        }

        @Override
        public void setValue(Object value) {
            this.setColor((Color)value);
        }
    }

    private static class ColorAttribute
    extends Attribute {
        public ColorAttribute(String name, StringGetter desc) {
            super(name, desc);
        }

        @Override
        public String toDisplayString(Object value) {
            return this.toStandardString(value);
        }

        @Override
        public String toStandardString(Object value) {
            Color c = (Color)value;
            return "#" + this.hex(c.getRed()) + this.hex(c.getGreen()) + this.hex(c.getBlue());
        }

        private String hex(int value) {
            if (value >= 16) {
                return Integer.toHexString(value);
            }
            return "0" + Integer.toHexString(value);
        }

        @Override
        public Object parse(String value) {
            return Color.decode(value);
        }

        @Override
        public Component getCellEditor(Object value) {
            return new ColorChooser((Color)value);
        }
    }

    private static class LocationAttribute
    extends Attribute {
        public LocationAttribute(String name, StringGetter desc) {
            super(name, desc);
        }

        @Override
        public Object parse(String value) {
            return Location.parse(value);
        }
    }

    private static class FontAttribute
    extends Attribute {
        private FontAttribute(String name, StringGetter disp) {
            super(name, disp);
        }

        @Override
        public String toDisplayString(Object value) {
            Font f = (Font)value;
            return f.getFamily() + " " + FontUtil.toStyleDisplayString(f.getStyle()) + " " + f.getSize();
        }

        @Override
        public String toStandardString(Object value) {
            Font f = (Font)value;
            return f.getFamily() + " " + FontUtil.toStyleStandardString(f.getStyle()) + " " + f.getSize();
        }

        @Override
        public Object parse(String value) {
            return Font.decode(value);
        }

        @Override
        public Component getCellEditor(Object value) {
            return FontUtil.createFontChooser((Font)value);
        }
    }

    private static class DirectionAttribute
    extends OptionAttribute {
        private static Direction[] vals = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        public DirectionAttribute(String name, StringGetter disp) {
            super(name, disp, vals);
        }

        @Override
        public String toDisplayString(Object value) {
            return ((Direction)value).toDisplayString();
        }

        @Override
        public Object parse(String value) {
            return Direction.parse(value);
        }
    }

    private static class IntegerRangeAttribute
    extends Attribute {
        Integer[] options = null;
        int start;
        int end;

        private IntegerRangeAttribute(String name, StringGetter disp, int start, int end) {
            super(name, disp);
            this.start = start;
            this.end = end;
        }

        @Override
        public Object parse(String value) {
            int v = (int)Long.parseLong(value);
            if (v < this.start) {
                throw new NumberFormatException("integer too small");
            }
            if (v > this.end) {
                throw new NumberFormatException("integer too large");
            }
            return IntegerFactory.create(v);
        }

        @Override
        public Component getCellEditor(Object value) {
            if (this.options == null) {
                this.options = new Integer[this.end - this.start + 1];
                for (int i = this.start; i <= this.end; ++i) {
                    this.options[i - this.start] = IntegerFactory.create(i);
                }
            }
            JComboBox<Integer> combo = new JComboBox<Integer>(this.options);
            combo.setSelectedItem(value);
            return combo;
        }
    }

    private static class BooleanAttribute
    extends OptionAttribute {
        private static Object[] vals = new Object[]{Boolean.TRUE, Boolean.FALSE};

        private BooleanAttribute(String name, StringGetter disp) {
            super(name, disp, vals);
        }

        @Override
        public String toDisplayString(Object value) {
            if (value == vals[0]) {
                return Strings.get("booleanTrueOption");
            }
            return Strings.get("booleanFalseOption");
        }

        @Override
        public Object parse(String value) {
            Boolean b = Boolean.valueOf(value);
            return vals[b != false ? 0 : 1];
        }
    }

    private static class DoubleAttribute
    extends Attribute {
        private DoubleAttribute(String name, StringGetter disp) {
            super(name, disp);
        }

        @Override
        public Object parse(String value) {
            return Double.valueOf(value);
        }
    }

    private static class HexIntegerAttribute
    extends Attribute {
        private HexIntegerAttribute(String name, StringGetter disp) {
            super(name, disp);
        }

        @Override
        public String toDisplayString(Object value) {
            int val = (Integer)value;
            return "0x" + Integer.toHexString(val);
        }

        @Override
        public String toStandardString(Object value) {
            return this.toDisplayString(value);
        }

        @Override
        public Object parse(String value) {
            if ((value = value.toLowerCase()).startsWith("0x")) {
                value = value.substring(2);
            }
            return IntegerFactory.create((int)Long.parseLong(value, 16));
        }
    }

    private static class IntegerAttribute
    extends Attribute {
        private IntegerAttribute(String name, StringGetter disp) {
            super(name, disp);
        }

        @Override
        public Object parse(String value) {
            return IntegerFactory.create(value);
        }
    }

    private static class OptionAttribute
    extends Attribute {
        private Object[] vals;

        private OptionAttribute(String name, StringGetter disp, Object[] vals) {
            super(name, disp);
            this.vals = vals;
        }

        @Override
        public String toDisplayString(Object value) {
            if (value instanceof AttributeOptionInterface) {
                return ((AttributeOptionInterface)value).toDisplayString();
            }
            return value.toString();
        }

        @Override
        public Object parse(String value) {
            for (int i = 0; i < this.vals.length; ++i) {
                if (!value.equals(this.vals[i].toString())) continue;
                return this.vals[i];
            }
            throw new NumberFormatException("value not among choices");
        }

        @Override
        public Component getCellEditor(Object value) {
            JComboBox<Object> combo = new JComboBox<Object>(this.vals);
            combo.setRenderer(new OptionComboRenderer(this));
            combo.setSelectedItem(value);
            return combo;
        }
    }

    private static class OptionComboRenderer
    extends BasicComboBoxRenderer {
        Attribute attr;

        OptionComboRenderer(Attribute attr) {
            this.attr = attr;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component ret = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (ret instanceof JLabel) {
                ((JLabel)ret).setText(this.attr.toDisplayString(value));
            }
            return ret;
        }
    }

    private static class StringAttribute
    extends Attribute {
        private StringAttribute(String name, StringGetter disp) {
            super(name, disp);
        }

        @Override
        public Object parse(String value) {
            return value;
        }
    }

    private static class ConstantGetter
    implements StringGetter {
        private String str;

        public ConstantGetter(String str) {
            this.str = str;
        }

        @Override
        public String get() {
            return this.str;
        }

        public String toString() {
            return this.get();
        }
    }

}

