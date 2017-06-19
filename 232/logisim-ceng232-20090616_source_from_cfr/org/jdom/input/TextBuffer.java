/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.input;

class TextBuffer {
    private static final String CVS_ID = "@(#) $RCSfile: TextBuffer.java,v $ $Revision: 1.8 $ $Date: 2004/02/06 09:28:31 $ $Name: jdom_1_0 $";
    private String prefixString;
    private char[] array = new char[4096];
    private int arraySize = 0;

    TextBuffer() {
    }

    void append(char[] source, int start, int count) {
        if (this.prefixString == null) {
            this.prefixString = new String(source, start, count);
        } else {
            this.ensureCapacity(this.arraySize + count);
            System.arraycopy(source, start, this.array, this.arraySize, count);
            this.arraySize += count;
        }
    }

    void clear() {
        this.arraySize = 0;
        this.prefixString = null;
    }

    private void ensureCapacity(int csize) {
        int capacity = this.array.length;
        if (csize > capacity) {
            char[] old = this.array;
            int nsize = capacity;
            while (csize > nsize) {
                nsize += capacity / 2;
            }
            this.array = new char[nsize];
            System.arraycopy(old, 0, this.array, 0, this.arraySize);
        }
    }

    int size() {
        if (this.prefixString == null) {
            return 0;
        }
        return this.prefixString.length() + this.arraySize;
    }

    public String toString() {
        if (this.prefixString == null) {
            return "";
        }
        String str = "";
        str = this.arraySize == 0 ? this.prefixString : new StringBuffer(this.prefixString.length() + this.arraySize).append(this.prefixString).append(this.array, 0, this.arraySize).toString();
        return str;
    }
}

