/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.IteratorUtil;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CollectionUtil {
    private CollectionUtil() {
    }

    public static Set createUnmodifiableSetUnion(Set a, Set b) {
        return new UnionSet(a, b);
    }

    public static List createUnmodifiableListUnion(List a, List b) {
        return new UnionList(a, b);
    }

    private static class UnionList
    extends AbstractList {
        private List a;
        private List b;

        UnionList(List a, List b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public int size() {
            return this.a.size() + this.b.size();
        }

        @Override
        public Object get(int index) {
            return index < this.a.size() ? this.a.get(index) : this.a.get(index - this.a.size());
        }
    }

    private static class UnionSet
    extends AbstractSet {
        private Set a;
        private Set b;

        UnionSet(Set a, Set b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public int size() {
            return this.a.size() + this.b.size();
        }

        @Override
        public Iterator iterator() {
            return IteratorUtil.createJoinedIterator(this.a.iterator(), this.b.iterator());
        }
    }

}

