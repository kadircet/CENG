/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.analyze.model.VariableListEvent;
import com.cburch.logisim.analyze.model.VariableListListener;
import com.cburch.logisim.util.IntegerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class VariableList {
    private ArrayList listeners = new ArrayList();
    private int maxSize;
    private ArrayList data;
    private List dataView;

    public VariableList(int maxSize) {
        this.maxSize = maxSize;
        this.data = maxSize > 16 ? new ArrayList() : new ArrayList(maxSize);
        this.dataView = Collections.unmodifiableList(this.data);
    }

    public void addVariableListListener(VariableListListener l) {
        this.listeners.add(l);
    }

    public void removeVariableListListener(VariableListListener l) {
        this.listeners.remove(l);
    }

    private void fireEvent(int type) {
        this.fireEvent(type, null, null);
    }

    private void fireEvent(int type, String variable) {
        this.fireEvent(type, variable, null);
    }

    private void fireEvent(int type, String variable, Object data) {
        int len = this.listeners.size();
        if (len == 0) {
            return;
        }
        VariableListEvent event = new VariableListEvent(this, type, variable, data);
        for (int i = 0; i < len; ++i) {
            VariableListListener l = (VariableListListener)this.listeners.get(i);
            l.listChanged(event);
        }
    }

    public int getMaximumSize() {
        return this.maxSize;
    }

    public List getAll() {
        return this.dataView;
    }

    public int indexOf(String name) {
        return this.data.indexOf(name);
    }

    public int size() {
        return this.data.size();
    }

    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    public boolean isFull() {
        return this.data.size() >= this.maxSize;
    }

    public String get(int index) {
        return (String)this.data.get(index);
    }

    public void setAll(List values) {
        if (values.size() > this.maxSize) {
            throw new IllegalArgumentException("maximum size is " + this.maxSize);
        }
        this.data.clear();
        this.data.addAll(values);
        this.fireEvent(0);
    }

    public void add(String name) {
        if (this.data.size() >= this.maxSize) {
            throw new IllegalArgumentException("maximum size is " + this.maxSize);
        }
        this.data.add(name);
        this.fireEvent(1, name);
    }

    public void remove(String name) {
        int index = this.data.indexOf(name);
        if (index < 0) {
            throw new NoSuchElementException("input " + name);
        }
        this.data.remove(index);
        this.fireEvent(2, name, IntegerFactory.create(index));
    }

    public void move(String name, int delta) {
        int index = this.data.indexOf(name);
        if (index < 0) {
            throw new NoSuchElementException(name);
        }
        int newIndex = index + delta;
        if (newIndex < 0) {
            throw new IllegalArgumentException("cannot move index " + index + " by " + delta);
        }
        if (newIndex > this.data.size() - 1) {
            throw new IllegalArgumentException("cannot move index " + index + " by " + delta + ": size " + this.data.size());
        }
        if (index == newIndex) {
            return;
        }
        this.data.remove(index);
        this.data.add(newIndex, name);
        this.fireEvent(3, name, IntegerFactory.create(newIndex - index));
    }

    public void replace(String oldName, String newName) {
        int index = this.data.indexOf(oldName);
        if (index < 0) {
            throw new NoSuchElementException(oldName);
        }
        if (oldName.equals(newName)) {
            return;
        }
        this.data.set(index, newName);
        this.fireEvent(4, oldName, IntegerFactory.create(index));
    }
}

