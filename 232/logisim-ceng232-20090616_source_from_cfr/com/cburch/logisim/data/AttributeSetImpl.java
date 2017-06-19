/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.data;

import com.cburch.logisim.data.AbstractAttributeSet;
import com.cburch.logisim.data.Attribute;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

public class AttributeSetImpl
extends AbstractAttributeSet {
    private AttrList list;
    private Node head;
    private Node tail;
    private int count;

    public AttributeSetImpl() {
        this.list = new AttrList();
        this.head = null;
        this.tail = null;
        this.count = 0;
    }

    public AttributeSetImpl(Attribute[] attrs, Object[] values) {
        this.list = new AttrList();
        this.head = null;
        this.tail = null;
        this.count = 0;
        if (attrs.length != values.length) {
            throw new IllegalArgumentException("arrays must have same length");
        }
        for (int i = 0; i < attrs.length; ++i) {
            this.addAttribute(attrs[i], values[i]);
        }
    }

    @Override
    protected void copyInto(AbstractAttributeSet destObj) {
        AttributeSetImpl dest = (AttributeSetImpl)destObj;
        if (this.head != null) {
            Node copy_prev = dest.head = new Node(this.head);
            Node cur = this.head.next;
            while (cur != null) {
                Node copy_cur;
                copy_prev.next = copy_cur = new Node(cur);
                copy_prev = copy_cur;
                cur = cur.next;
            }
            dest.tail = copy_prev;
            dest.count = this.count;
        }
    }

    @Override
    public List getAttributes() {
        return this.list;
    }

    public void addAttribute(Attribute attr, Object value) {
        if (attr == null) {
            throw new IllegalArgumentException("Adding null attribute");
        }
        if (this.findNode(attr) != null) {
            throw new IllegalArgumentException("Attribute " + attr + " already created");
        }
        Node n = new Node(attr, value, false, null);
        if (this.head == null) {
            this.head = n;
        } else {
            this.tail.next = n;
        }
        this.tail = n;
        ++this.count;
        this.fireAttributeListChanged();
    }

    public void removeAttribute(Attribute attr) {
        Node prev = null;
        Node n = this.head;
        while (n != null) {
            if (n.attr.equals(attr)) {
                if (this.tail == n) {
                    this.tail = prev;
                }
                if (prev == null) {
                    this.head = n.next;
                } else {
                    prev.next = n.next;
                }
                --this.count;
                this.fireAttributeListChanged();
                return;
            }
            prev = n;
            n = n.next;
        }
        throw new IllegalArgumentException("Attribute " + attr + " absent");
    }

    @Override
    public boolean isReadOnly(Attribute attr) {
        Node n = this.findNode(attr);
        if (n == null) {
            throw new IllegalArgumentException("Unknown attribute " + attr);
        }
        return n.is_read_only;
    }

    @Override
    public void setReadOnly(Attribute attr, boolean value) {
        Node n = this.findNode(attr);
        if (n == null) {
            throw new IllegalArgumentException("Unknown attribute " + attr);
        }
        n.is_read_only = value;
    }

    @Override
    public Object getValue(Attribute attr) {
        Node n = this.findNode(attr);
        if (n == null) {
            throw new IllegalArgumentException("Unknown attribute " + attr);
        }
        return n.value;
    }

    @Override
    public void setValue(Attribute attr, Object value) {
        Node n;
        if (value instanceof String) {
            value = attr.parse((String)value);
        }
        if ((n = this.findNode(attr)) == null) {
            throw new IllegalArgumentException("Unknown attribute " + attr);
        }
        if (n.is_read_only) {
            throw new IllegalArgumentException("Attribute " + attr + " is read-only");
        }
        if (!value.equals(n.value)) {
            n.value = value;
            this.fireAttributeValueChanged(attr, value);
        }
    }

    private Node findNode(Attribute attr) {
        Node n = this.head;
        while (n != null) {
            if (n.attr.equals(attr)) {
                return n;
            }
            n = n.next;
        }
        return null;
    }

    private class AttrList
    extends AbstractList {
        private AttrList() {
        }

        @Override
        public Iterator iterator() {
            return new AttrIterator(AttributeSetImpl.this.head);
        }

        @Override
        public Object get(int i) {
            int remaining;
            Node n = AttributeSetImpl.this.head;
            for (remaining = i; remaining != 0 && n != null; --remaining) {
                n = n.next;
            }
            if (remaining != 0 || n == null) {
                throw new IndexOutOfBoundsException("" + i + " not in list " + " [" + AttributeSetImpl.this.count + " elements]");
            }
            return n.attr;
        }

        @Override
        public boolean contains(Object o) {
            return this.indexOf(o) != -1;
        }

        @Override
        public int indexOf(Object o) {
            Node n = AttributeSetImpl.this.head;
            int ret = 0;
            while (n != null) {
                if (o.equals(n.attr)) {
                    return ret;
                }
                n = n.next;
                ++ret;
            }
            return -1;
        }

        @Override
        public int size() {
            return AttributeSetImpl.this.count;
        }
    }

    private class AttrIterator
    implements Iterator {
        Node n;

        AttrIterator(Node n) {
            this.n = n;
        }

        @Override
        public boolean hasNext() {
            return this.n != null;
        }

        public Object next() {
            Node ret = this.n;
            this.n = this.n.next;
            return ret.attr;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class Node {
        Attribute attr;
        Object value;
        boolean is_read_only;
        Node next;

        Node(Attribute attr, Object value, boolean is_read_only, Node next) {
            this.attr = attr;
            this.value = value;
            this.is_read_only = is_read_only;
            this.next = next;
        }

        Node(Node other) {
            this.attr = other.attr;
            this.value = other.value;
            this.is_read_only = other.is_read_only;
            this.next = other.next;
        }
    }

}

