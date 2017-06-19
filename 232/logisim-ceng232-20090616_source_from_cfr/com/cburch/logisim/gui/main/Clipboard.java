/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.gui.main.Selection;
import com.cburch.logisim.util.PropertyChangeWeakSupport;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;

class Clipboard {
    public static final String contentsProperty = "contents";
    private static Clipboard current = null;
    private static PropertyChangeWeakSupport propertySupport = new PropertyChangeWeakSupport(Clipboard.class);
    private HashSet components = new HashSet();
    private AttributeSet oldAttrs = null;
    private AttributeSet newAttrs = null;

    public static boolean isEmpty() {
        return current == null || Clipboard.current.components.isEmpty();
    }

    public static Clipboard get() {
        return current;
    }

    public static void set(Selection value, AttributeSet oldAttrs) {
        Clipboard.set(new Clipboard(value, oldAttrs));
    }

    public static void set(Clipboard value) {
        Clipboard old = current;
        current = value;
        propertySupport.firePropertyChange("contents", old, current);
    }

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public static void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    public static void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    public static void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }

    private Clipboard(Selection sel, AttributeSet viewAttrs) {
        for (Component base : sel.getComponents()) {
            AttributeSet baseAttrs = base.getAttributeSet();
            AttributeSet copyAttrs = (AttributeSet)baseAttrs.clone();
            Component copy = base.getFactory().createComponent(base.getLocation(), copyAttrs);
            this.components.add(copy);
            if (baseAttrs != viewAttrs) continue;
            this.oldAttrs = baseAttrs;
            this.newAttrs = copyAttrs;
        }
    }

    public Collection getComponents() {
        return this.components;
    }

    public AttributeSet getOldAttributeSet() {
        return this.oldAttrs;
    }

    public AttributeSet getNewAttributeSet() {
        return this.newAttrs;
    }

    void setOldAttributeSet(AttributeSet value) {
        this.oldAttrs = value;
    }
}

