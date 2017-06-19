/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.comp;

import com.cburch.logisim.comp.TextField;

public class TextFieldEvent {
    private TextField field;
    private String oldval;
    private String newval;

    public TextFieldEvent(TextField field, String old, String val) {
        this.field = field;
        this.oldval = old;
        this.newval = val;
    }

    public TextField getTextField() {
        return this.field;
    }

    public String getOldText() {
        return this.oldval;
    }

    public String getText() {
        return this.newval;
    }
}

