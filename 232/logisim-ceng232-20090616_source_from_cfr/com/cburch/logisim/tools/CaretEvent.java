/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.tools;

import com.cburch.logisim.tools.Caret;

public class CaretEvent {
    private Caret caret;
    private String oldtext;
    private String newtext;

    public CaretEvent(Caret caret, String oldtext, String newtext) {
        this.caret = caret;
        this.oldtext = oldtext;
        this.newtext = newtext;
    }

    public Caret getCaret() {
        return this.caret;
    }

    public String getOldText() {
        return this.oldtext;
    }

    public String getText() {
        return this.newtext;
    }
}

