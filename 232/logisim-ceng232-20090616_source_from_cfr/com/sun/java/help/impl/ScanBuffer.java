/*
 * Decompiled with CFR 0_114.
 */
package com.sun.java.help.impl;

import com.sun.java.help.impl.DocumentParser;

class ScanBuffer {
    char[] buf;
    int buflen;
    int scale = 2;

    ScanBuffer() {
        this.buf = new char[256];
    }

    ScanBuffer(int n, int n2) {
        this.buf = new char[n];
        this.scale = n2;
    }

    protected void clear() {
        this.buflen = 0;
    }

    protected void reset(int n) {
        this.buflen = n;
    }

    protected void flush(DocumentParser documentParser) {
        if (this.buflen > 0) {
            documentParser.callFlush(this.buf, 0, this.buflen);
            this.buflen = 0;
        }
    }

    protected void add(char c) {
        if (this.buflen >= this.buf.length) {
            char[] arrc = new char[this.buf.length * this.scale];
            System.arraycopy(this.buf, 0, arrc, 0, this.buf.length);
            this.buf = arrc;
        }
        this.buf[this.buflen++] = c;
    }

    protected int length() {
        return this.buflen;
    }

    public String toString() {
        return "ScanBuffer, buf = " + this.buf + ", buflen = " + this.buflen;
    }

    protected String extract(int n) {
        return new String(this.buf, n, this.buflen - n);
    }
}

