/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.data;

import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Strings;
import com.cburch.logisim.util.Cache;
import java.awt.Color;

public class Value {
    public static final Value FALSE = new Value(1, 0, 0, 0);
    public static final Value TRUE = new Value(1, 0, 0, 1);
    public static final Value UNKNOWN = new Value(1, 0, 1, 0);
    public static final Value ERROR = new Value(1, 1, 0, 0);
    public static final Value NIL = new Value(0, 0, 0, 0);
    public static final int MAX_WIDTH = 32;
    public static final Color NIL_COLOR = Color.GRAY;
    public static final Color FALSE_COLOR = new Color(0, 80, 0);
    public static final Color TRUE_COLOR = new Color(0, 210, 0);
    public static final Color UNKNOWN_COLOR = new Color(40, 40, 255);
    public static final Color ERROR_COLOR = Color.RED;
    public static final Color WIDTH_ERROR_COLOR = new Color(255, 123, 0);
    public static final Color MULTI_COLOR = Color.BLACK;
    private static final Cache cache = new Cache();
    private final int width;
    private final int error;
    private final int unknown;
    private final int value;

    public static Value create(Value[] values) {
        if (values.length == 0) {
            return NIL;
        }
        if (values.length == 1) {
            return values[0];
        }
        if (values.length > 32) {
            throw new RuntimeException("Cannot have more than 32 bits in a value");
        }
        int width = values.length;
        int value = 0;
        int unknown = 0;
        int error = 0;
        for (int i = 0; i < values.length; ++i) {
            int mask = 1 << i;
            if (values[i] == TRUE) {
                value |= mask;
                continue;
            }
            if (values[i] == FALSE) continue;
            if (values[i] == UNKNOWN) {
                unknown |= mask;
                continue;
            }
            if (values[i] == ERROR) {
                error |= mask;
                continue;
            }
            throw new RuntimeException("unrecognized value " + values[i]);
        }
        return Value.create(width, error, unknown, value);
    }

    public static Value createKnown(BitWidth bits, int value) {
        return Value.create(bits.getWidth(), 0, 0, value);
    }

    public static Value createUnknown(BitWidth bits) {
        return Value.create(bits.getWidth(), 0, -1, 0);
    }

    public static Value createError(BitWidth bits) {
        return Value.create(bits.getWidth(), -1, 0, 0);
    }

    private static Value create(int width, int error, int unknown, int value) {
        if (width == 0) {
            return NIL;
        }
        if (width == 1) {
            if ((error & 1) != 0) {
                return ERROR;
            }
            if ((unknown & 1) != 0) {
                return UNKNOWN;
            }
            if ((value & 1) != 0) {
                return TRUE;
            }
            return FALSE;
        }
        int mask = width == 32 ? -1 : ~ (-1 << width);
        int hashCode = 31 * (31 * (31 * width + error) + (unknown = unknown & mask & ~ (error &= mask))) + (value = value & mask & ~ unknown & ~ error);
        Object cached = cache.get(hashCode);
        if (cached != null) {
            Value val = (Value)cached;
            if (val.value == value && val.width == width && val.error == error && val.unknown == unknown) {
                return val;
            }
        }
        Value ret = new Value(width, error, unknown, value);
        cache.put(hashCode, ret);
        return ret;
    }

    private Value(int width, int error, int unknown, int value) {
        this.width = width;
        this.error = error;
        this.unknown = unknown;
        this.value = value;
    }

    public boolean isErrorValue() {
        return this.error != 0;
    }

    public Value extendWidth(int newWidth, Value others) {
        int maskInverse;
        if (this.width == newWidth) {
            return this;
        }
        int n = maskInverse = this.width == 32 ? 0 : -1 << this.width;
        if (others == ERROR) {
            return Value.create(newWidth, this.error | maskInverse, this.unknown, this.value);
        }
        if (others == FALSE) {
            return Value.create(newWidth, this.error, this.unknown, this.value);
        }
        if (others == TRUE) {
            return Value.create(newWidth, this.error, this.unknown, this.value | maskInverse);
        }
        return Value.create(newWidth, this.error, this.unknown | maskInverse, this.value);
    }

    public boolean isUnknown() {
        if (this.width == 32) {
            return this.error == 0 && this.unknown == -1;
        }
        return this.error == 0 && this.unknown == (1 << this.width) - 1;
    }

    public boolean isFullyDefined() {
        return this.width > 0 && this.error == 0 && this.unknown == 0;
    }

    public Value set(int which, Value val) {
        if (val.width != 1) {
            throw new RuntimeException("Cannot set multiple values");
        }
        if (which < 0 || which >= this.width) {
            throw new RuntimeException("Attempt to set outside value's width");
        }
        if (this.width == 1) {
            return val;
        }
        int mask = ~ (1 << which);
        return Value.create(this.width, this.error & mask | val.error << which, this.unknown & mask | val.unknown << which, this.value & mask | val.value << which);
    }

    public Value[] getAll() {
        Value[] ret = new Value[this.width];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = this.get(i);
        }
        return ret;
    }

    public Value get(int which) {
        if (which < 0 || which >= this.width) {
            return ERROR;
        }
        int mask = 1 << which;
        if ((this.error & mask) != 0) {
            return ERROR;
        }
        if ((this.unknown & mask) != 0) {
            return UNKNOWN;
        }
        if ((this.value & mask) != 0) {
            return TRUE;
        }
        return FALSE;
    }

    public BitWidth getBitWidth() {
        return BitWidth.create(this.width);
    }

    public int getWidth() {
        return this.width;
    }

    public boolean equals(Object other_obj) {
        if (!(other_obj instanceof Value)) {
            return false;
        }
        Value other = (Value)other_obj;
        boolean ret = this.width == other.width && this.error == other.error && this.unknown == other.unknown && this.value == other.value;
        return ret;
    }

    public int hashCode() {
        int ret = this.width;
        ret = 31 * ret + this.error;
        ret = 31 * ret + this.unknown;
        ret = 31 * ret + this.value;
        return ret;
    }

    public int toIntValue() {
        if (this.error != 0) {
            return -1;
        }
        if (this.unknown != 0) {
            return -1;
        }
        return this.value;
    }

    public String toString() {
        switch (this.width) {
            case 0: {
                return "-";
            }
            case 1: {
                if (this.error != 0) {
                    return "E";
                }
                if (this.unknown != 0) {
                    return "x";
                }
                if (this.value != 0) {
                    return "1";
                }
                return "0";
            }
        }
        StringBuffer ret = new StringBuffer();
        for (int i = this.width - 1; i >= 0; --i) {
            ret.append(this.get(i).toString());
            if (i % 4 != 0 || i == 0) continue;
            ret.append(" ");
        }
        return ret.toString();
    }

    public String toOctalString() {
        if (this.width <= 1) {
            return this.toString();
        }
        Value[] vals = this.getAll();
        char[] c = new char[(vals.length + 2) / 3];
        for (int i = 0; i < c.length; ++i) {
            int k = c.length - 1 - i;
            int frst = 3 * k;
            int last = Math.min(vals.length, 3 * (k + 1));
            int v = 0;
            c[i] = 63;
            for (int j = last - 1; j >= frst; --j) {
                if (vals[j] == ERROR) {
                    c[i] = 69;
                    break;
                }
                if (vals[j] == UNKNOWN) {
                    c[i] = 120;
                    break;
                }
                v = 2 * v;
                if (vals[j] != TRUE) continue;
                ++v;
            }
            if (c[i] != '?') continue;
            c[i] = Character.forDigit(v, 8);
        }
        return new String(c);
    }

    public String toHexString() {
        if (this.width <= 1) {
            return this.toString();
        }
        Value[] vals = this.getAll();
        char[] c = new char[(vals.length + 3) / 4];
        for (int i = 0; i < c.length; ++i) {
            int k = c.length - 1 - i;
            int frst = 4 * k;
            int last = Math.min(vals.length, 4 * (k + 1));
            int v = 0;
            c[i] = 63;
            for (int j = last - 1; j >= frst; --j) {
                if (vals[j] == ERROR) {
                    c[i] = 69;
                    break;
                }
                if (vals[j] == UNKNOWN) {
                    c[i] = 120;
                    break;
                }
                v = 2 * v;
                if (vals[j] != TRUE) continue;
                ++v;
            }
            if (c[i] != '?') continue;
            c[i] = Character.forDigit(v, 16);
        }
        return new String(c);
    }

    public String toDecimalString(boolean signed) {
        if (this.width == 0) {
            return "-";
        }
        if (this.isErrorValue()) {
            return Strings.get("valueError");
        }
        if (!this.isFullyDefined()) {
            return Strings.get("valueUnknown");
        }
        int value = this.toIntValue();
        if (signed) {
            if (this.width < 32 && value >> this.width - 1 != 0) {
                value |= -1 << this.width;
            }
            return "" + value;
        }
        return "" + ((long)value & 0xFFFFFFFFL);
    }

    public String toDisplayString(int radix) {
        switch (radix) {
            case 2: {
                return this.toDisplayString();
            }
            case 8: {
                return this.toOctalString();
            }
            case 16: {
                return this.toHexString();
            }
        }
        if (this.width == 0) {
            return "-";
        }
        if (this.isErrorValue()) {
            return Strings.get("valueError");
        }
        if (!this.isFullyDefined()) {
            return Strings.get("valueUnknown");
        }
        return Integer.toString(this.toIntValue(), radix);
    }

    public String toDisplayString() {
        switch (this.width) {
            case 0: {
                return "-";
            }
            case 1: {
                if (this.error != 0) {
                    return Strings.get("valueErrorSymbol");
                }
                if (this.unknown != 0) {
                    return Strings.get("valueUnknownSymbol");
                }
                if (this.value != 0) {
                    return "1";
                }
                return "0";
            }
        }
        StringBuffer ret = new StringBuffer();
        for (int i = this.width - 1; i >= 0; --i) {
            ret.append(this.get(i).toString());
            if (i % 4 != 0 || i == 0) continue;
            ret.append(" ");
        }
        return ret.toString();
    }

    public Value combine(Value other) {
        if (other == null) {
            return this;
        }
        if (this == NIL) {
            return other;
        }
        if (other == NIL) {
            return this;
        }
        if (this.width == 1 && other.width == 1) {
            if (this == other) {
                return this;
            }
            if (this == UNKNOWN) {
                return other;
            }
            if (other == UNKNOWN) {
                return this;
            }
            return ERROR;
        }
        int disagree = (this.value ^ other.value) & ~ (this.unknown | other.unknown);
        return Value.create(Math.max(this.width, other.width), this.error | other.error | disagree, this.unknown & other.unknown, this.value & ~ this.unknown | other.value & ~ other.unknown);
    }

    public Value and(Value other) {
        if (other == null) {
            return this;
        }
        if (this.width == 1 && other.width == 1) {
            if (this == ERROR || other == ERROR) {
                return ERROR;
            }
            if (this == FALSE || other == FALSE) {
                return FALSE;
            }
            if (this == TRUE || other == TRUE) {
                return TRUE;
            }
            return UNKNOWN;
        }
        return Value.create(Math.max(this.width, other.width), this.error | other.error, this.unknown & other.unknown, (this.value | this.unknown) & (other.value | other.unknown));
    }

    public Value or(Value other) {
        if (other == null) {
            return this;
        }
        if (this.width == 1 && other.width == 1) {
            if (this == ERROR || other == ERROR) {
                return ERROR;
            }
            if (this == TRUE || other == TRUE) {
                return TRUE;
            }
            if (this == FALSE || other == FALSE) {
                return FALSE;
            }
            return UNKNOWN;
        }
        return Value.create(Math.max(this.width, other.width), this.error | other.error, this.unknown & other.unknown, this.value | other.value);
    }

    public Value xor(Value other) {
        if (other == null) {
            return this;
        }
        if (this.width <= 1 && other.width <= 1) {
            if (this == NIL && other == NIL) {
                return NIL;
            }
            if (this == ERROR || other == ERROR) {
                return ERROR;
            }
            if (this == UNKNOWN || other == UNKNOWN) {
                return UNKNOWN;
            }
            if (this == TRUE == (other == TRUE)) {
                return FALSE;
            }
            return TRUE;
        }
        return Value.create(Math.max(this.width, other.width), this.error | other.error, this.unknown | other.unknown, this.value ^ other.value);
    }

    public Value not() {
        if (this.width <= 1) {
            if (this == TRUE) {
                return FALSE;
            }
            if (this == FALSE) {
                return TRUE;
            }
            return this;
        }
        return Value.create(this.width, this.error, this.unknown, ~ this.value);
    }

    public Color getColor() {
        if (this.error != 0) {
            return ERROR_COLOR;
        }
        if (this.width == 0) {
            return NIL_COLOR;
        }
        if (this.width == 1) {
            if (this == UNKNOWN) {
                return UNKNOWN_COLOR;
            }
            if (this == TRUE) {
                return TRUE_COLOR;
            }
            return FALSE_COLOR;
        }
        return MULTI_COLOR;
    }
}

