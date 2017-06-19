/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.IteratorUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SmallSet
extends AbstractSet {
    private static final int HASH_POINT = 4;
    private int size = 0;
    private int version = 0;
    private Object values = null;

    public Object clone() {
        SmallSet ret = new SmallSet();
        ret.size = this.size;
        if (this.size == 1) {
            ret.values = this.values;
        } else if (this.size <= 4) {
            Object[] oldVals = (Object[])this.values;
            Object[] retVals = new Object[this.size];
            for (int i = this.size - 1; i >= 0; --i) {
                retVals[i] = oldVals[i];
            }
        } else {
            HashSet oldVals = (HashSet)this.values;
            this.values = oldVals.clone();
        }
        return ret;
    }

    @Override
    public Object[] toArray() {
        if (this.size == 1) {
            return new Object[]{this.values};
        }
        if (this.size <= 4) {
            Object[] ret = new Object[this.size];
            System.arraycopy((Object[])this.values, 0, ret, 0, this.size);
            return ret;
        }
        HashSet hash = (HashSet)this.values;
        return hash.toArray();
    }

    @Override
    public void clear() {
        this.size = 0;
        this.values = null;
        ++this.version;
    }

    @Override
    public boolean isEmpty() {
        if (this.size <= 4) {
            return this.size == 0;
        }
        return ((HashSet)this.values).isEmpty();
    }

    @Override
    public int size() {
        if (this.size <= 4) {
            return this.size;
        }
        return ((HashSet)this.values).size();
    }

    @Override
    public boolean add(Object value) {
        if (this.size < 2) {
            if (this.size == 0) {
                this.values = value;
                this.size = 1;
                ++this.version;
                return true;
            }
            if (this.values.equals(value)) {
                return false;
            }
            Object[] newValues = new Object[4];
            newValues[0] = this.values;
            newValues[1] = value;
            this.values = newValues;
            this.size = 2;
            ++this.version;
            return true;
        }
        if (this.size <= 4) {
            Object[] vals = (Object[])this.values;
            for (int i = 0; i < this.size; ++i) {
                if (!vals[i].equals(value)) continue;
                return false;
            }
            if (this.size < 4) {
                vals[this.size] = value;
                ++this.size;
                ++this.version;
                return true;
            }
            HashSet<Object> newValues = new HashSet<Object>();
            for (int i2 = 0; i2 < this.size; ++i2) {
                newValues.add(vals[i2]);
            }
            newValues.add(value);
            this.values = newValues;
            ++this.size;
            ++this.version;
            return true;
        }
        HashSet vals = (HashSet)this.values;
        if (vals.add(value)) {
            ++this.version;
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(Object value) {
        if (this.size <= 2) {
            if (this.size == 0) {
                return false;
            }
            return this.values.equals(value);
        }
        if (this.size <= 4) {
            Object[] vals = (Object[])this.values;
            for (int i = 0; i < this.size; ++i) {
                if (!vals[i].equals(value)) continue;
                return true;
            }
            return false;
        }
        HashSet vals = (HashSet)this.values;
        return vals.contains(value);
    }

    @Override
    public Iterator iterator() {
        if (this.size <= 4) {
            if (this.size == 0) {
                return IteratorUtil.EMPTY_ITERATOR;
            }
            return new ArrayIterator();
        }
        return ((HashSet)this.values).iterator();
    }

    public static void main(String[] args) throws IOException {
        SmallSet set = new SmallSet();
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
            this.itVersion = SmallSet.this.version;
            this.pos = 0;
            this.hasNext = true;
            this.removeOk = false;
        }

        @Override
        public boolean hasNext() {
            return this.hasNext;
        }

        public Object next() {
            if (this.itVersion != SmallSet.this.version) {
                throw new ConcurrentModificationException();
            }
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (SmallSet.this.size == 1) {
                this.pos = 1;
                this.hasNext = false;
                this.removeOk = true;
                return SmallSet.this.values;
            }
            Object ret = ((Object[])SmallSet.this.values)[this.pos];
            ++this.pos;
            this.hasNext = this.pos < SmallSet.this.size;
            this.removeOk = true;
            return ret;
        }

        @Override
        public void remove() {
            if (this.itVersion != SmallSet.this.version) {
                throw new ConcurrentModificationException();
            }
            if (!this.removeOk) {
                throw new IllegalStateException();
            }
            if (SmallSet.this.size == 1) {
                SmallSet.this.values = null;
                SmallSet.this.size = 0;
                ++SmallSet.this.version;
                this.itVersion = SmallSet.this.version;
                this.removeOk = false;
            } else {
                Object[] vals = (Object[])SmallSet.this.values;
                if (SmallSet.this.size == 2) {
                    SmallSet.this.values = this.pos == 2 ? vals[0] : vals[1];
                    SmallSet.this.size = 1;
                } else {
                    for (int i = this.pos; i < SmallSet.this.size; ++i) {
                        vals[i - 1] = vals[i];
                    }
                    --this.pos;
                    --SmallSet.this.size;
                    vals[SmallSet.access$100((SmallSet)SmallSet.this)] = null;
                }
                ++SmallSet.this.version;
                this.itVersion = SmallSet.this.version;
                this.removeOk = false;
            }
        }
    }

}

