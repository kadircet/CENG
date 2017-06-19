/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Strings;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeOption;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.util.StringGetter;

public abstract class RadixOption
extends AttributeOption {
    public static final RadixOption RADIX_2 = new Radix2();
    public static final RadixOption RADIX_8 = new Radix8();
    public static final RadixOption RADIX_10_UNSIGNED = new Radix10Unsigned();
    public static final RadixOption RADIX_10_SIGNED = new Radix10Signed();
    public static final RadixOption RADIX_16 = new Radix16();
    public static final RadixOption[] OPTIONS = new RadixOption[]{RADIX_2, RADIX_8, RADIX_10_SIGNED, RADIX_10_UNSIGNED, RADIX_16};
    public static final Attribute ATTRIBUTE = Attributes.forOption("radix", Strings.getter("radixAttr"), OPTIONS);

    private RadixOption(String name, StringGetter display) {
        super(name, display);
    }

    public abstract String toString(Value var1);

    public abstract int getMaxLength(BitWidth var1);

    public int getMaxLength(Value value) {
        return this.getMaxLength(value.getBitWidth());
    }

    private static class Radix16
    extends RadixOption {
        private Radix16() {
            super("16", Strings.getter("radix16"));
        }

        @Override
        public String toString(Value value) {
            return value.toDisplayString(16);
        }

        @Override
        public int getMaxLength(BitWidth width) {
            return Math.max(1, (width.getWidth() + 3) / 4);
        }
    }

    private static class Radix8
    extends RadixOption {
        private Radix8() {
            super("8", Strings.getter("radix8"));
        }

        @Override
        public String toString(Value value) {
            return value.toDisplayString(8);
        }

        @Override
        public int getMaxLength(Value value) {
            return value.toDisplayString(8).length();
        }

        @Override
        public int getMaxLength(BitWidth width) {
            return Math.max(1, (width.getWidth() + 2) / 3);
        }
    }

    private static class Radix10Unsigned
    extends RadixOption {
        private Radix10Unsigned() {
            super("10unsigned", Strings.getter("radix10Unsigned"));
        }

        @Override
        public String toString(Value value) {
            return value.toDecimalString(false);
        }

        @Override
        public int getMaxLength(BitWidth width) {
            switch (width.getWidth()) {
                case 4: 
                case 5: 
                case 6: {
                    return 2;
                }
                case 7: 
                case 8: 
                case 9: {
                    return 3;
                }
                case 10: 
                case 11: 
                case 12: 
                case 13: {
                    return 4;
                }
                case 14: 
                case 15: 
                case 16: {
                    return 5;
                }
                case 17: 
                case 18: 
                case 19: {
                    return 6;
                }
                case 20: 
                case 21: 
                case 22: 
                case 23: {
                    return 7;
                }
                case 24: 
                case 25: 
                case 26: {
                    return 8;
                }
                case 27: 
                case 28: 
                case 29: {
                    return 9;
                }
                case 30: 
                case 31: 
                case 32: {
                    return 10;
                }
            }
            return 1;
        }
    }

    private static class Radix10Signed
    extends RadixOption {
        private Radix10Signed() {
            super("10signed", Strings.getter("radix10Signed"));
        }

        @Override
        public String toString(Value value) {
            return value.toDecimalString(true);
        }

        @Override
        public int getMaxLength(BitWidth width) {
            switch (width.getWidth()) {
                case 2: 
                case 3: 
                case 4: {
                    return 2;
                }
                case 5: 
                case 6: 
                case 7: {
                    return 3;
                }
                case 8: 
                case 9: 
                case 10: {
                    return 4;
                }
                case 11: 
                case 12: 
                case 13: 
                case 14: {
                    return 5;
                }
                case 15: 
                case 16: 
                case 17: {
                    return 6;
                }
                case 18: 
                case 19: 
                case 20: {
                    return 7;
                }
                case 21: 
                case 22: 
                case 23: 
                case 24: {
                    return 8;
                }
                case 25: 
                case 26: 
                case 27: {
                    return 9;
                }
                case 28: 
                case 29: 
                case 30: {
                    return 10;
                }
                case 31: 
                case 32: {
                    return 11;
                }
            }
            return 1;
        }
    }

    private static class Radix2
    extends RadixOption {
        private Radix2() {
            super("2", Strings.getter("radix2"));
        }

        @Override
        public String toString(Value value) {
            return value.toDisplayString(2);
        }

        @Override
        public int getMaxLength(Value value) {
            return value.toDisplayString(2).length();
        }

        @Override
        public int getMaxLength(BitWidth width) {
            int bits = width.getWidth();
            if (bits <= 1) {
                return 1;
            }
            return bits + (bits - 1) / 4;
        }
    }

}

