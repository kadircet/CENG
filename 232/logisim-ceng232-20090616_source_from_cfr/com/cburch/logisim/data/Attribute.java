/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.data;

import com.cburch.logisim.util.StringGetter;
import java.awt.Component;
import java.awt.Window;
import javax.swing.JTextField;

public abstract class Attribute {
    private String name;
    private StringGetter disp;

    public Attribute(String name, StringGetter disp) {
        this.name = name;
        this.disp = disp;
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.disp.get();
    }

    public Component getCellEditor(Window source, Object value) {
        return this.getCellEditor(value);
    }

    protected Component getCellEditor(Object value) {
        return new JTextField(this.toDisplayString(value));
    }

    public String toDisplayString(Object value) {
        return value.toString();
    }

    public String toStandardString(Object value) {
        return value.toString();
    }

    public abstract Object parse(String var1);
}

