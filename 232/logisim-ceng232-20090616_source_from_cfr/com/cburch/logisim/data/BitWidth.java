/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.data;

import com.cburch.logisim.util.StringGetter;
import java.awt.Component;
import javax.swing.JComboBox;

public class BitWidth
implements Comparable {
    public static final BitWidth UNKNOWN = new BitWidth(0);
    public static final BitWidth ONE = new BitWidth(1);
    private static BitWidth[] prefab = null;
    final int width;

    private BitWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return this.width;
    }

    public int getMask() {
        if (this.width == 0) {
            return 0;
        }
        if (this.width == 32) {
            return -1;
        }
        return (1 << this.width) - 1;
    }

    public boolean equals(Object other_obj) {
        if (!(other_obj instanceof BitWidth)) {
            return false;
        }
        BitWidth other = (BitWidth)other_obj;
        return this.width == other.width;
    }

    public int compareTo(Object other_obj) {
        BitWidth other = (BitWidth)other_obj;
        return this.width - other.width;
    }

    public int hashCode() {
        return this.width;
    }

    public String toString() {
        return "" + this.width;
    }

    public static BitWidth create(int width) {
        BitWidth.ensurePrefab();
        if (width <= 0) {
            if (width == 0) {
                return UNKNOWN;
            }
            throw new IllegalArgumentException("width " + width + " must be positive");
        }
        if (width - 1 < prefab.length) {
            return prefab[width - 1];
        }
        return new BitWidth(width);
    }

    public static BitWidth parse(String str) {
        if (str == null || str.length() == 0) {
            throw new NumberFormatException("Width string cannot be null");
        }
        if (str.charAt(0) == '/') {
            str = str.substring(1);
        }
        return BitWidth.create(Integer.parseInt(str));
    }

    private static void ensurePrefab() {
        if (prefab == null) {
            prefab = new BitWidth[java.lang.Math.min(32, 32)];
            BitWidth.prefab[0] = ONE;
            for (int i = 1; i < prefab.length; ++i) {
                BitWidth.prefab[i] = new BitWidth(i + 1);
            }
        }
    }

    static class Attribute
    extends com.cburch.logisim.data.Attribute {
        private BitWidth[] choices;

        public Attribute(String name, StringGetter disp) {
            super(name, disp);
            BitWidth.ensurePrefab();
            this.choices = prefab;
        }

        public Attribute(String name, StringGetter disp, int min, int max) {
            super(name, disp);
            this.choices = new BitWidth[max - min + 1];
            for (int i = 0; i < this.choices.length; ++i) {
                this.choices[i] = BitWidth.create(min + i);
            }
        }

        @Override
        public Object parse(String value) {
            return BitWidth.parse(value);
        }

        @Override
        public Component getCellEditor(Object value) {
            JComboBox<BitWidth> combo = new JComboBox<BitWidth>(this.choices);
            int wid = ((BitWidth)value).getWidth();
            if (wid <= 0 || wid > prefab.length) {
                combo.addItem((BitWidth)value);
            }
            combo.setSelectedItem(value);
            return combo;
        }
    }

}

