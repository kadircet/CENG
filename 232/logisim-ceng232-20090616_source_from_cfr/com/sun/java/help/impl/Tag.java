/*
 * Decompiled with CFR 0_114.
 */
package com.sun.java.help.impl;

import com.sun.java.help.impl.TagProperties;

public class Tag {
    public String name;
    public boolean isEnd;
    public boolean isEmpty;
    public TagProperties atts;

    public Tag(String string, TagProperties tagProperties, boolean bl, boolean bl2) {
        this.name = string;
        this.atts = tagProperties;
        this.isEnd = bl;
        this.isEmpty = bl2;
    }
}

