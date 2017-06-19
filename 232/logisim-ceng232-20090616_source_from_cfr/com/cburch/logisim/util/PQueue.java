/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class PQueue {
    private static final int INIT_SIZE = 64;
    private Comparable[] data = new Comparable[64];
    private int size = 0;

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void clear() {
        this.size = 0;
        if (this.data.length != 64) {
            this.data = new Comparable[64];
        }
    }

    public void add(Comparable value) {
        Comparable c;
        int parent;
        if (value == null) {
            throw new IllegalArgumentException("Cannot add null");
        }
        if (this.size == this.data.length) {
            Comparable[] newData = new Comparable[2 * this.data.length];
            System.arraycopy(this.data, 0, newData, 0, this.data.length);
            this.data = newData;
        }
        int index = this.size;
        while (index > 0 && (c = this.data[parent = (index - 1) / 2]).compareTo(value) < 0) {
            this.data[index] = c;
            index = parent;
        }
        this.data[index] = value;
        ++this.size;
    }

    public Object peek() {
        return this.size > 0 ? this.data[0] : null;
    }

    public Object remove() {
        int newSize = this.size - 1;
        if (newSize < 0) {
            throw new NoSuchElementException("priority queue is empty");
        }
        Comparable ret = this.data[0];
        Comparable value = this.data[newSize];
        if (value == null) {
            return null;
        }
        this.size = newSize;
        this.data[newSize] = null;
        int index = 0;
        do {
            int childIndex;
            Comparable child;
            if ((childIndex = 2 * index + 1) + 1 < newSize) {
                Comparable other = this.data[childIndex + 1];
                child = this.data[childIndex];
                if (other.compareTo(child) > 0) {
                    child = other;
                    ++childIndex;
                }
            } else {
                if (childIndex >= newSize) break;
                child = this.data[childIndex];
            }
            if (value.compareTo(child) >= 0) break;
            this.data[index] = child;
            index = childIndex;
        } while (true);
        this.data[index] = value;
        return ret;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PQueue pq = new PQueue();
        do {
            System.out.print("data:");
            if (!pq.isEmpty()) {
                for (int i = 0; i < pq.size; ++i) {
                    System.out.print(" " + pq.data[i]);
                }
                System.out.println();
            } else {
                System.out.println(" (empty)");
            }
            System.out.print("? ");
            String line = in.readLine();
            StringTokenizer toks = new StringTokenizer(line);
            if (!toks.hasMoreTokens()) continue;
            String cmd = toks.nextToken();
            if (cmd.equals("+")) {
                String data = toks.hasMoreTokens() ? toks.nextToken() : "";
                pq.add((Comparable)((Object)data));
                continue;
            }
            if (cmd.equals("-")) {
                System.out.println("removed " + pq.remove());
                continue;
            }
            System.out.println("unknown command " + cmd);
        } while (true);
    }
}

