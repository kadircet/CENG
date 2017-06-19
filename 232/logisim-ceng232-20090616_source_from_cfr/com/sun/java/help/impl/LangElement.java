/*
 * Decompiled with CFR 0_114.
 */
package com.sun.java.help.impl;

import com.sun.java.help.impl.Tag;
import java.util.Locale;

public class LangElement {
    Tag tag;
    Locale locale;

    public LangElement(Tag tag, Locale locale) {
        this.tag = tag;
        this.locale = locale;
    }

    public Tag getTag() {
        return this.tag;
    }

    public Locale getLocale() {
        return this.locale;
    }
}

