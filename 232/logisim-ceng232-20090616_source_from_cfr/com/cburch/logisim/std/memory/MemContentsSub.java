/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.memory;

import java.util.Arrays;

class MemContentsSub {
    private MemContentsSub() {
    }

    static ContentsInterface createContents(int size, int bits) {
        if (bits <= 8) {
            return new ByteContents(size);
        }
        if (bits <= 16) {
            return new ShortContents(size);
        }
        return new IntContents(size);
    }

    private static class IntContents
    extends ContentsInterface {
        private int[] data;

        public IntContents(int size) {
            this.data = new int[size];
        }

        @Override
        public Object clone() {
            IntContents ret = (IntContents)super.clone();
            ret.data = new int[this.data.length];
            System.arraycopy(this.data, 0, ret.data, 0, this.data.length);
            return ret;
        }

        @Override
        int getLength() {
            return this.data.length;
        }

        @Override
        int get(int addr) {
            return addr >= 0 && addr < this.data.length ? this.data[addr] : 0;
        }

        @Override
        void set(int addr, int value) {
            int oldValue;
            if (addr >= 0 && addr < this.data.length && value != (oldValue = this.data[addr])) {
                this.data[addr] = value;
            }
        }

        @Override
        void clear() {
            Arrays.fill(this.data, 0);
        }

        @Override
        void load(int start, int[] values, int mask) {
            int n = Math.min(values.length, this.data.length - start);
            for (int i = 0; i < n; ++i) {
                this.data[i] = values[i] & mask;
            }
        }
    }

    private static class ShortContents
    extends ContentsInterface {
        private short[] data;

        public ShortContents(int size) {
            this.data = new short[size];
        }

        @Override
        public Object clone() {
            ShortContents ret = (ShortContents)super.clone();
            ret.data = new short[this.data.length];
            System.arraycopy(this.data, 0, ret.data, 0, this.data.length);
            return ret;
        }

        @Override
        int getLength() {
            return this.data.length;
        }

        @Override
        int get(int addr) {
            return addr >= 0 && addr < this.data.length ? this.data[addr] : 0;
        }

        @Override
        void set(int addr, int value) {
            short oldValue;
            if (addr >= 0 && addr < this.data.length && value != (oldValue = this.data[addr])) {
                this.data[addr] = (short)value;
            }
        }

        @Override
        void clear() {
            Arrays.fill(this.data, 0);
        }

        @Override
        void load(int start, int[] values, int mask) {
            int n = Math.min(values.length, this.data.length - start);
            for (int i = 0; i < n; ++i) {
                this.data[start + i] = (short)(values[i] & mask);
            }
        }
    }

    private static class ByteContents
    extends ContentsInterface {
        private byte[] data;

        public ByteContents(int size) {
            this.data = new byte[size];
        }

        @Override
        public Object clone() {
            ByteContents ret = (ByteContents)super.clone();
            ret.data = new byte[this.data.length];
            System.arraycopy(this.data, 0, ret.data, 0, this.data.length);
            return ret;
        }

        @Override
        int getLength() {
            return this.data.length;
        }

        @Override
        int get(int addr) {
            return addr >= 0 && addr < this.data.length ? this.data[addr] : 0;
        }

        @Override
        void set(int addr, int value) {
            byte oldValue;
            if (addr >= 0 && addr < this.data.length && value != (oldValue = this.data[addr])) {
                this.data[addr] = (byte)value;
            }
        }

        @Override
        void clear() {
            Arrays.fill(this.data, 0);
        }

        @Override
        void load(int start, int[] values, int mask) {
            int n = Math.min(values.length, this.data.length - start);
            for (int i = 0; i < n; ++i) {
                this.data[start + i] = (byte)(values[i] & mask);
            }
        }
    }

    static abstract class ContentsInterface
    implements Cloneable {
        ContentsInterface() {
        }

        public Object clone() {
            try {
                return super.clone();
            }
            catch (CloneNotSupportedException e) {
                return this;
            }
        }

        abstract int getLength();

        abstract int get(int var1);

        abstract void set(int var1, int var2);

        abstract void clear();

        abstract void load(int var1, int[] var2, int var3);

        boolean matches(int[] values, int start, int mask) {
            for (int i = 0; i < values.length; ++i) {
                if (this.get(start + i) == (values[i] & mask)) continue;
                return false;
            }
            return true;
        }

        int[] get(int start, int len) {
            int[] ret = new int[len];
            for (int i = 0; i < ret.length; ++i) {
                ret[i] = this.get(start + i);
            }
            return ret;
        }

        boolean isClear() {
            int n = this.getLength();
            for (int i = 0; i < n; ++i) {
                if (this.get(i) == 0) continue;
                return false;
            }
            return true;
        }
    }

}

