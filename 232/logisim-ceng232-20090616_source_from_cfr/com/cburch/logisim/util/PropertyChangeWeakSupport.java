/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.IntegerFactory;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

public class PropertyChangeWeakSupport {
    private static final String ALL_PROPERTIES = "ALL PROPERTIES";
    private Object source;
    private LinkedList listeners = new LinkedList();

    public PropertyChangeWeakSupport(Object source) {
        this.source = source;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.addPropertyChangeListener("ALL PROPERTIES", listener);
    }

    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        this.listeners.add(new ListenerData(property, listener));
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.removePropertyChangeListener("ALL PROPERTIES", listener);
    }

    public void removePropertyChangeListener(String property, PropertyChangeListener listener) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ListenerData data = (ListenerData)it.next();
            PropertyChangeListener l = (PropertyChangeListener)data.listener.get();
            if (l == null) {
                it.remove();
                continue;
            }
            if (!data.property.equals(property) || l != listener) continue;
            it.remove();
        }
    }

    public void firePropertyChange(String property, Object oldValue, Object newValue) {
        PropertyChangeEvent e = null;
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ListenerData data = (ListenerData)it.next();
            PropertyChangeListener l = (PropertyChangeListener)data.listener.get();
            if (l == null) {
                it.remove();
                continue;
            }
            if (data.property != "ALL PROPERTIES" && !data.property.equals(property)) continue;
            if (e == null) {
                e = new PropertyChangeEvent(this.source, property, oldValue, newValue);
            }
            l.propertyChange(e);
        }
    }

    public void firePropertyChange(String property, int oldValue, int newValue) {
        PropertyChangeEvent e = null;
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ListenerData data = (ListenerData)it.next();
            PropertyChangeListener l = (PropertyChangeListener)data.listener.get();
            if (l == null) {
                it.remove();
                continue;
            }
            if (data.property != "ALL PROPERTIES" && !data.property.equals(property)) continue;
            if (e == null) {
                e = new PropertyChangeEvent(this.source, property, IntegerFactory.create(oldValue), IntegerFactory.create(newValue));
            }
            l.propertyChange(e);
        }
    }

    public void firePropertyChange(String property, boolean oldValue, boolean newValue) {
        PropertyChangeEvent e = null;
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ListenerData data = (ListenerData)it.next();
            PropertyChangeListener l = (PropertyChangeListener)data.listener.get();
            if (l == null) {
                it.remove();
                continue;
            }
            if (data.property != "ALL PROPERTIES" && !data.property.equals(property)) continue;
            if (e == null) {
                e = new PropertyChangeEvent(this.source, property, oldValue, newValue);
            }
            l.propertyChange(e);
        }
    }

    private static class ListenerData {
        String property;
        WeakReference listener;

        ListenerData(String property, PropertyChangeListener listener) {
            this.property = property;
            this.listener = new WeakReference<PropertyChangeListener>(listener);
        }
    }

}

