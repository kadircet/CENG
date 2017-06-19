/*
 * Decompiled with CFR 0_114.
 */
package com.sun.java.help.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

class MyBufferedReader
extends BufferedReader {
    public MyBufferedReader(Reader reader, int n) {
        super(reader, n);
    }

    public MyBufferedReader(Reader reader) {
        super(reader);
    }

    public int read(char[] arrc, int n, int n2) throws IOException {
        if (this.lock == null) {
            return -1;
        }
        return super.read(arrc, n, n2);
    }
}

