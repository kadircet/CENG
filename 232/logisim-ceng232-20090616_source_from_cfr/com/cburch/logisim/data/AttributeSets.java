/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.data;

import com.cburch.logisim.data.AbstractAttributeSet;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AttributeSets {
    public static final AttributeSet EMPTY = new AttributeSet(){

        @Override
        public Object clone() {
            return this;
        }

        @Override
        public void addAttributeListener(AttributeListener l) {
        }

        @Override
        public void removeAttributeListener(AttributeListener l) {
        }

        @Override
        public List getAttributes() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public boolean containsAttribute(Attribute attr) {
            return false;
        }

        @Override
        public Attribute getAttribute(String name) {
            return null;
        }

        @Override
        public boolean isReadOnly(Attribute attr) {
            return true;
        }

        @Override
        public void setReadOnly(Attribute attr, boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getValue(Attribute attr) {
            return null;
        }

        @Override
        public void setValue(Attribute attr, Object value) {
        }
    };

    private AttributeSets() {
    }

    public static AttributeSet fixedSet(Attribute attr, Object initValue) {
        return new SingletonSet(attr, initValue);
    }

    public static AttributeSet fixedSet(Attribute[] attrs, Object[] initValues) {
        if (attrs.length > 1) {
            return new FixedSet(attrs, initValues);
        }
        if (attrs.length == 1) {
            return new SingletonSet(attrs[0], initValues[0]);
        }
        return EMPTY;
    }

    public static void copy(AttributeSet src, AttributeSet dst) {
        if (src == null || src.getAttributes() == null) {
            return;
        }
        for (Attribute attr : src.getAttributes()) {
            Object value = src.getValue(attr);
            dst.setValue(attr, value);
        }
    }

    private static class FixedSet
    extends AbstractAttributeSet {
        private List attrs;
        private Object[] values;
        private int readOnly = 0;

        FixedSet(Attribute[] attrs, Object[] initValues) {
            if (attrs.length != initValues.length) {
                throw new IllegalArgumentException("attribute and value arrays must have same length");
            }
            if (attrs.length > 32) {
                throw new IllegalArgumentException("cannot handle more than 32 attributes");
            }
            this.attrs = Arrays.asList(attrs);
            this.values = (Object[])initValues.clone();
        }

        @Override
        protected void copyInto(AbstractAttributeSet destSet) {
            FixedSet dest = (FixedSet)destSet;
            dest.attrs = this.attrs;
            dest.values = (Object[])this.values.clone();
            dest.readOnly = this.readOnly;
        }

        @Override
        public List getAttributes() {
            return this.attrs;
        }

        @Override
        public boolean isReadOnly(Attribute attr) {
            int index = this.attrs.indexOf(attr);
            if (index < 0) {
                return true;
            }
            return this.isReadOnly(index);
        }

        @Override
        public void setReadOnly(Attribute attr, boolean value) {
            int index = this.attrs.indexOf(attr);
            if (index < 0) {
                throw new IllegalArgumentException("attribute absent");
            }
            this.readOnly = value ? (this.readOnly |= 1 << index) : (this.readOnly &= ~ (1 << index));
        }

        @Override
        public Object getValue(Attribute attr) {
            int index = this.attrs.indexOf(attr);
            if (index < 0) {
                return null;
            }
            return this.values[index];
        }

        @Override
        public void setValue(Attribute attr, Object value) {
            int index = this.attrs.indexOf(attr);
            if (index < 0) {
                throw new IllegalArgumentException("attribute absent");
            }
            if (this.isReadOnly(index)) {
                throw new IllegalArgumentException("read only");
            }
            this.values[index] = value;
            this.fireAttributeValueChanged(attr, value);
        }

        private boolean isReadOnly(int index) {
            return (this.readOnly >> index & 1) == 1;
        }
    }

    private static class SingletonSet
    extends AbstractAttributeSet {
        private List attrs;
        private Object value;
        private boolean readOnly = false;

        SingletonSet(Attribute attr, Object initValue) {
            this.attrs = Collections.singletonList(attr);
            this.value = initValue;
        }

        @Override
        protected void copyInto(AbstractAttributeSet destSet) {
            SingletonSet dest = (SingletonSet)destSet;
            dest.attrs = this.attrs;
            dest.value = this.value;
            dest.readOnly = this.readOnly;
        }

        @Override
        public List getAttributes() {
            return this.attrs;
        }

        @Override
        public boolean isReadOnly(Attribute attr) {
            return this.readOnly;
        }

        @Override
        public void setReadOnly(Attribute attr, boolean value) {
            int index = this.attrs.indexOf(attr);
            if (index < 0) {
                throw new IllegalArgumentException("attribute absent");
            }
            this.readOnly = value;
        }

        @Override
        public Object getValue(Attribute attr) {
            int index = this.attrs.indexOf(attr);
            return index >= 0 ? this.value : null;
        }

        @Override
        public void setValue(Attribute attr, Object value) {
            int index = this.attrs.indexOf(attr);
            if (index < 0) {
                throw new IllegalArgumentException("attribute absent");
            }
            if (this.readOnly) {
                throw new IllegalArgumentException("read only");
            }
            this.value = value;
            this.fireAttributeValueChanged(attr, value);
        }
    }

}

