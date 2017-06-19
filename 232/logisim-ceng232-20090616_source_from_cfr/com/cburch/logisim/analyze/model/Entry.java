/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.analyze.model.Strings;
import com.cburch.logisim.util.StringGetter;

public class Entry {
    public static final Entry ZERO = new Entry("0");
    public static final Entry ONE = new Entry("1");
    public static final Entry DONT_CARE = new Entry("x");
    public static final Entry BUS_ERROR = new Entry(Strings.getter("busError"));
    public static final Entry OSCILLATE_ERROR = new Entry(Strings.getter("oscillateError"));
    private String description;
    private StringGetter errorMessage;

    public static Entry parse(String description) {
        if (Entry.ZERO.description.equals(description)) {
            return ZERO;
        }
        if (Entry.ONE.description.equals(description)) {
            return ONE;
        }
        if (Entry.DONT_CARE.description.equals(description)) {
            return DONT_CARE;
        }
        if (Entry.BUS_ERROR.description.equals(description)) {
            return BUS_ERROR;
        }
        return null;
    }

    private Entry(String description) {
        this.description = description;
        this.errorMessage = null;
    }

    private Entry(StringGetter errorMessage) {
        this.description = "!!";
        this.errorMessage = errorMessage;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isError() {
        return this.errorMessage != null;
    }

    public String getErrorMessage() {
        return this.errorMessage == null ? null : this.errorMessage.get();
    }

    public String toString() {
        return "Entry[" + this.description + "]";
    }
}

