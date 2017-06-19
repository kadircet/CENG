/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.IteratorUtil;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

public class ListUtil {
    private ListUtil() {
    }

    public static List joinImmutableLists(List a, List b) {
        return new JoinedList(a, b);
    }

    private static class JoinedList
    extends AbstractList {
        List a;
        List b;

        JoinedList(List a, List b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public int size() {
            return this.a.size() + this.b.size();
        }

        @Override
        public Object get(int index) {
            if (index < this.a.size()) {
                return this.a.get(index);
            }
            return this.b.get(index - this.a.size());
        }

        @Override
        public Iterator iterator() {
            return IteratorUtil.createJoinedIterator(this.a.iterator(), this.b.iterator());
        }
    }

}

