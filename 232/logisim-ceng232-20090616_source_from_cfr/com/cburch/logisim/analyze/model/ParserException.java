/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.util.StringGetter;

public class ParserException
extends Exception {
    private StringGetter message;
    private int start;
    private int length;

    public ParserException(StringGetter message, int start, int length) {
        super(message.get());
        this.message = message;
        this.start = start;
        this.length = length;
    }

    @Override
    public String getMessage() {
        return this.message.get();
    }

    public StringGetter getMessageGetter() {
        return this.message;
    }

    public int getOffset() {
        return this.start;
    }

    public int getEndOffset() {
        return this.start + this.length;
    }
}

