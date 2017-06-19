/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteratorUtil {
    public static Iterator EMPTY_ITERATOR = new EmptyIterator();

    public static Iterator createEmptyIterator() {
        return EMPTY_ITERATOR;
    }

    public static Iterator createUnitIterator(Object data) {
        return new UnitIterator(data);
    }

    public static Iterator createArrayIterator(Object[] data) {
        return new ArrayIterator(data);
    }

    public static Iterator createJoinedIterator(Iterator i0, Iterator i1) {
        if (!i0.hasNext()) {
            return i1;
        }
        if (!i1.hasNext()) {
            return i0;
        }
        return new IteratorUnion(i0, i1);
    }

    private static class IteratorUnion
    implements Iterator {
        Iterator cur;
        Iterator next;

        private IteratorUnion(Iterator cur, Iterator next) {
            this.cur = cur;
            this.next = next;
        }

        public Object next() {
            if (!this.cur.hasNext()) {
                if (this.next == null) {
                    throw new NoSuchElementException();
                }
                this.cur = this.next;
                if (!this.cur.hasNext()) {
                    throw new NoSuchElementException();
                }
            }
            return this.cur.next();
        }

        @Override
        public boolean hasNext() {
            return this.cur.hasNext() || this.next != null && this.next.hasNext();
        }

        @Override
        public void remove() {
            this.cur.remove();
        }
    }

    private static class ArrayIterator
    implements Iterator {
        private Object[] data;
        private int i = -1;

        private ArrayIterator(Object[] data) {
            this.data = data;
        }

        public Object next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            ++this.i;
            return this.data[this.i];
        }

        @Override
        public boolean hasNext() {
            return this.i + 1 < this.data.length;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("ArrayIterator.remove");
        }
    }

    private static class UnitIterator
    implements Iterator {
        private Object data;
        private boolean taken = false;

        private UnitIterator(Object data) {
            this.data = data;
        }

        public Object next() {
            if (this.taken) {
                throw new NoSuchElementException();
            }
            this.taken = true;
            return this.data;
        }

        @Override
        public boolean hasNext() {
            return !this.taken;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("UnitIterator.remove");
        }
    }

    private static class EmptyIterator
    implements Iterator {
        private EmptyIterator() {
        }

        public Object next() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("EmptyIterator.remove");
        }
    }

}

