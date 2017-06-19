/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Strings;
import com.cburch.logisim.data.AbstractAttributeSet;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.util.IntegerFactory;
import com.cburch.logisim.util.StringGetter;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.JComboBox;

class SplitterAttributes
extends AbstractAttributeSet {
    public static final Attribute facing_attr = Attributes.forDirection("facing", Strings.getter("splitterFacingAttr"));
    public static final Attribute width_attr = Attributes.forBitWidth("incoming", Strings.getter("splitterBitWidthAttr"));
    public static final Attribute fanout_attr = Attributes.forIntegerRange("fanout", Strings.getter("splitterFanOutAttr"), 1, 32);
    private static final List INIT_ATTRIBUTES = Arrays.asList(facing_attr, fanout_attr, width_attr);
    private static final String unchosen_val = "none";
    private ArrayList attrs = new ArrayList(INIT_ATTRIBUTES);
    boolean frozen;
    Direction facing = Direction.EAST;
    byte fanout = 2;
    byte[] bit_end = new byte[2];
    BitOutOption[] options = null;

    SplitterAttributes() {
        this.configureOptions();
        this.configureDefaults();
    }

    @Override
    protected void copyInto(AbstractAttributeSet destObj) {
        SplitterAttributes dest = (SplitterAttributes)destObj;
        dest.attrs = new ArrayList(this.attrs.size());
        dest.attrs.addAll(INIT_ATTRIBUTES);
        int n = this.attrs.size();
        for (int i = SplitterAttributes.INIT_ATTRIBUTES.size(); i < n; ++i) {
            BitOutAttribute attr = (BitOutAttribute)this.attrs.get(i);
            dest.attrs.add(attr.createCopy());
        }
        dest.frozen = this.frozen;
        dest.facing = this.facing;
        dest.fanout = this.fanout;
        dest.bit_end = (byte[])this.bit_end.clone();
        dest.options = this.options;
    }

    @Override
    public List getAttributes() {
        return this.attrs;
    }

    @Override
    public Object getValue(Attribute attr) {
        if (attr == facing_attr) {
            return this.facing;
        }
        if (attr == fanout_attr) {
            return IntegerFactory.create(this.fanout);
        }
        if (attr == width_attr) {
            return BitWidth.create(this.bit_end.length);
        }
        if (attr instanceof BitOutAttribute) {
            BitOutAttribute bitOut = (BitOutAttribute)attr;
            return IntegerFactory.create(this.bit_end[bitOut.which]);
        }
        return null;
    }

    @Override
    public boolean isReadOnly(Attribute attr) {
        if (this.frozen) {
            return attr == facing_attr || attr == fanout_attr;
        }
        return false;
    }

    @Override
    public void setValue(Attribute attr, Object value) {
        if (attr == facing_attr) {
            this.facing = (Direction)value;
            this.configureOptions();
        } else if (attr == fanout_attr) {
            this.fanout = (byte)((Integer)value).intValue();
            this.configureOptions();
            this.configureDefaults();
        } else if (attr == width_attr) {
            BitWidth width = (BitWidth)value;
            this.bit_end = new byte[width.getWidth()];
            this.configureOptions();
            this.configureDefaults();
        } else if (attr instanceof BitOutAttribute) {
            BitOutAttribute bitOutAttr = (BitOutAttribute)attr;
            int val = value instanceof Integer ? (Integer)value : ((BitOutOption)value).value + 1;
            if (val >= 0 && val <= this.fanout) {
                this.bit_end[bitOutAttr.which] = (byte)val;
            }
        } else {
            throw new IllegalArgumentException("unknown attribute " + attr);
        }
        this.fireAttributeValueChanged(attr, value);
    }

    private void configureOptions() {
        this.options = new BitOutOption[this.fanout + 1];
        boolean isVertical = this.facing == Direction.EAST || this.facing == Direction.WEST;
        for (int i = -1; i < this.fanout; ++i) {
            this.options[i + 1] = new BitOutOption(i, isVertical, i == this.fanout - 1);
        }
        int offs = INIT_ATTRIBUTES.size();
        int curNum = this.attrs.size() - offs;
        for (int i2 = 0; i2 < curNum; ++i2) {
            BitOutAttribute attr = (BitOutAttribute)this.attrs.get(offs + i2);
            attr.options = this.options;
        }
    }

    private void configureDefaults() {
        int i;
        boolean changed;
        int offs = INIT_ATTRIBUTES.size();
        int curNum = this.attrs.size() - offs;
        byte[] dflt = new byte[this.bit_end.length];
        if (this.fanout >= this.bit_end.length) {
            for (int i2 = 0; i2 < this.bit_end.length; ++i2) {
                dflt[i2] = (byte)(i2 + 1);
            }
        } else {
            int threads_per_end = dflt.length / this.fanout;
            int ends_with_extra = dflt.length % this.fanout;
            int cur_end = -1;
            int left_in_end = 0;
            for (int i3 = 0; i3 < dflt.length; ++i3) {
                if (left_in_end == 0) {
                    ++cur_end;
                    left_in_end = threads_per_end;
                    if (ends_with_extra > 0) {
                        ++left_in_end;
                        --ends_with_extra;
                    }
                }
                dflt[i3] = (byte)(1 + cur_end);
                --left_in_end;
            }
        }
        boolean bl = changed = curNum != this.bit_end.length;
        while (curNum > this.bit_end.length) {
            this.attrs.remove(offs + --curNum);
        }
        for (i = 0; i < curNum; ++i) {
            if (this.bit_end[i] == dflt[i]) continue;
            BitOutAttribute attr = (BitOutAttribute)this.attrs.get(offs + i);
            this.bit_end[i] = dflt[i];
            this.fireAttributeValueChanged(attr, IntegerFactory.create(this.bit_end[i]));
        }
        for (i = curNum; i < this.bit_end.length; ++i) {
            BitOutAttribute attr = new BitOutAttribute(i, this.options);
            this.bit_end[i] = dflt[i];
            this.attrs.add(attr);
        }
        if (changed) {
            this.fireAttributeListChanged();
        }
    }

    static class BitOutAttribute
    extends Attribute {
        int which;
        BitOutOption[] options;

        private BitOutAttribute(int which, BitOutOption[] options) {
            super("bit" + which, Strings.getter("splitterBitAttr", "" + which));
            this.which = which;
            this.options = options;
        }

        private BitOutAttribute createCopy() {
            return new BitOutAttribute(this.which, this.options);
        }

        @Override
        public Object parse(String value) {
            if (value.equals("none")) {
                return IntegerFactory.ZERO;
            }
            return IntegerFactory.create(1 + Integer.parseInt(value));
        }

        @Override
        public String toDisplayString(Object value) {
            int index = (Integer)value;
            return this.options[index].toString();
        }

        @Override
        public String toStandardString(Object value) {
            int index = (Integer)value;
            if (index == 0) {
                return "none";
            }
            return "" + (index - 1);
        }

        @Override
        public Component getCellEditor(Object value) {
            int index = (Integer)value;
            JComboBox<BitOutOption> combo = new JComboBox<BitOutOption>(this.options);
            combo.setSelectedIndex(index);
            return combo;
        }
    }

    private static class BitOutOption {
        int value;
        boolean isVertical;
        boolean isLast;

        BitOutOption(int value, boolean isVertical, boolean isLast) {
            this.value = value;
            this.isVertical = isVertical;
            this.isLast = isLast;
        }

        public String toString() {
            if (this.value < 0) {
                return Strings.get("splitterBitNone");
            }
            String ret = "" + this.value;
            if (this.value == 0) {
                String note = this.isVertical ? Strings.get("splitterBitNorth") : Strings.get("splitterBitEast");
                ret = ret + " (" + note + ")";
            } else if (this.isLast) {
                String note = this.isVertical ? Strings.get("splitterBitSouth") : Strings.get("splitterBitWest");
                ret = ret + " (" + note + ")";
            }
            return ret;
        }
    }

}

