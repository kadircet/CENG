/*
 * Decompiled with CFR 0_114.
 */
package com.sun.java.help.impl;

import com.sun.java.help.impl.Tag;
import java.util.EventObject;

public class ParserEvent
extends EventObject {
    private Tag tag;
    private String text;
    private String target;
    private String data;
    private String root;
    private String publicId;
    private String systemId;

    public ParserEvent(Object object, Tag tag) {
        super(object);
        this.tag = tag;
    }

    public ParserEvent(Object object, String string) {
        super(object);
        this.text = string;
    }

    public ParserEvent(Object object, String string, String string2) {
        super(object);
        this.target = string;
        this.data = string2;
    }

    public ParserEvent(Object object, String string, String string2, String string3) {
        super(object);
        this.root = string;
        this.publicId = string2;
        this.systemId = string3;
    }

    public Tag getTag() {
        return this.tag;
    }

    public String getText() {
        return this.text;
    }

    public String getTarget() {
        return this.target;
    }

    public String getData() {
        return this.data;
    }

    public String getRoot() {
        return this.root;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getSystemId() {
        return this.systemId;
    }
}

