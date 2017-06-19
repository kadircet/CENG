/*
 * Decompiled with CFR 0_114.
 */
package com.sun.java.help.impl;

import com.sun.java.help.impl.ParserEvent;
import java.util.EventListener;

public interface ParserListener
extends EventListener {
    public void tagFound(ParserEvent var1);

    public void piFound(ParserEvent var1);

    public void doctypeFound(ParserEvent var1);

    public void textFound(ParserEvent var1);

    public void commentFound(ParserEvent var1);

    public void errorFound(ParserEvent var1);
}

