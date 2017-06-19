/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class EventSourceWeakSupport {
    private LinkedList listeners = new LinkedList();

    public void add(Object listener) {
        this.listeners.add(new WeakReference<Object>(listener));
    }

    public void remove(Object listener) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            Object l = ((WeakReference)it.next()).get();
            if (l != null && l != listener) continue;
            it.remove();
        }
    }

    public int size() {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            Object l = ((WeakReference)it.next()).get();
            if (l != null) continue;
            it.remove();
        }
        return this.listeners.size();
    }

    public Iterator iterator() {
        ArrayList ret = new ArrayList(this.listeners.size());
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            Object l = ((WeakReference)it.next()).get();
            if (l == null) {
                it.remove();
                continue;
            }
            ret.add(l);
        }
        return ret.iterator();
    }
}

