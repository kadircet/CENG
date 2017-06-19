/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArraySet
extends AbstractSet {
    private static final Object[] EMPTY_ARRAY = new Object[0];
    private int version = 0;
    private Object[] values = EMPTY_ARRAY;

    @Override
    public Object[] toArray() {
        return this.values;
    }

    public Object clone() {
        ArraySet ret = new ArraySet();
        ret.values = this.values == EMPTY_ARRAY ? EMPTY_ARRAY : (Object[])this.values.clone();
        return ret;
    }

    @Override
    public void clear() {
        this.values = EMPTY_ARRAY;
        ++this.version;
    }

    @Override
    public boolean isEmpty() {
        return this.values.length == 0;
    }

    @Override
    public int size() {
        return this.values.length;
    }

    @Override
    public boolean add(Object value) {
        int n = this.values.length;
        for (int i = 0; i < n; ++i) {
            if (!this.values[i].equals(value)) continue;
            return false;
        }
        Object[] newValues = new Object[n + 1];
        System.arraycopy(this.values, 0, newValues, 0, n);
        newValues[n] = value;
        this.values = newValues;
        ++this.version;
        return true;
    }

    @Override
    public boolean contains(Object value) {
        int n = this.values.length;
        for (int i = 0; i < n; ++i) {
            if (!this.values[i].equals(value)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Iterator iterator() {
        return new ArrayIterator();
    }

    public static void main(String[] args) throws IOException {
        ArraySet set = new ArraySet();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        do {
            System.out.print("" + set.size() + ":");
            Iterator it = set.iterator();
            while (it.hasNext()) {
                System.out.print(" " + it.next());
            }
            System.out.println();
            System.out.print("> ");
            String cmd = in.readLine();
            if (cmd == null) break;
            if ((cmd = cmd.trim()).equals("")) continue;
            if (cmd.startsWith("+")) {
                set.add(cmd.substring(1));
                continue;
            }
            if (cmd.startsWith("-")) {
                set.remove(cmd.substring(1));
                continue;
            }
            if (cmd.startsWith("?")) {
                boolean ret = set.contains(cmd.substring(1));
                System.out.println("  " + ret);
                continue;
            }
            System.out.println("unrecognized command");
        } while (true);
    }

    private class ArrayIterator
    implements Iterator {
        int itVersion;
        int pos;
        boolean hasNext;
        boolean removeOk;

        private ArrayIterator() {
            this.itVersion = ArraySet.this.version;
            this.pos = 0;
            this.hasNext = ArraySet.this.values.length > 0;
            this.removeOk = false;
        }

        @Override
        public boolean hasNext() {
            return this.hasNext;
        }

        public Object next() {
            if (this.itVersion != ArraySet.this.version) {
                throw new ConcurrentModificationException();
            }
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            Object ret = ArraySet.this.values[this.pos];
            ++this.pos;
            this.hasNext = this.pos < ArraySet.this.values.length;
            this.removeOk = true;
            return ret;
        }

        @Override
        public void remove() {
            if (this.itVersion != ArraySet.this.version) {
                throw new ConcurrentModificationException();
            }
            if (!this.removeOk) {
                throw new IllegalStateException();
            }
            if (ArraySet.this.values.length == 1) {
                ArraySet.this.values = EMPTY_ARRAY;
                ++ArraySet.this.version;
                this.itVersion = ArraySet.this.version;
                this.removeOk = false;
            } else {
                Object[] newValues = new Object[ArraySet.this.values.length - 1];
                if (this.pos > 1) {
                    System.arraycopy(ArraySet.this.values, 0, newValues, 0, this.pos - 1);
                }
                if (this.pos < ArraySet.this.values.length) {
                    System.arraycopy(ArraySet.this.values, this.pos, newValues, this.pos - 1, ArraySet.this.values.length - this.pos);
                }
                ArraySet.this.values = newValues;
                --this.pos;
                ++ArraySet.this.version;
                this.itVersion = ArraySet.this.version;
                this.removeOk = false;
            }
        }
    }

}

