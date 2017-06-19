/*
 * Decompiled with CFR 0_114.
 */
package com.sun.java.help.impl;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

public class TagProperties
implements Cloneable {
    protected Hashtable hashtable;
    protected int initialSize;
    static int count1 = 0;
    static int count2 = 0;

    public TagProperties() {
        this(7);
    }

    public TagProperties(int n) {
        this.initialSize = n;
    }

    public String getProperty(String string) {
        return (String)this.get(string);
    }

    public String getProperty(String string, String string2) {
        String string3 = this.getProperty(string);
        return string3 == null ? string2 : string3;
    }

    public Enumeration propertyNames() {
        Hashtable hashtable = new Hashtable(11);
        this.enumerate(hashtable);
        return hashtable.keys();
    }

    public void list(PrintStream printStream) {
        printStream.println("-- listing properties --");
        Hashtable hashtable = new Hashtable(11);
        this.enumerate(hashtable);
        Enumeration enumeration = hashtable.keys();
        while (enumeration.hasMoreElements()) {
            String string = (String)enumeration.nextElement();
            String string2 = (String)hashtable.get(string);
            if (string2.length() > 40) {
                string2 = string2.substring(0, 37) + "...";
            }
            printStream.println(string + "=" + string2);
        }
    }

    public void list(PrintWriter printWriter) {
        printWriter.println("-- listing properties --");
        Hashtable hashtable = new Hashtable(11);
        this.enumerate(hashtable);
        Enumeration enumeration = hashtable.keys();
        while (enumeration.hasMoreElements()) {
            String string = (String)enumeration.nextElement();
            String string2 = (String)hashtable.get(string);
            if (string2.length() > 40) {
                string2 = string2.substring(0, 37) + "...";
            }
            printWriter.println(string + "=" + string2);
        }
    }

    private synchronized void enumerate(Hashtable hashtable) {
        Enumeration enumeration = this.keys();
        while (enumeration.hasMoreElements()) {
            String string = (String)enumeration.nextElement();
            hashtable.put(string, this.get(string));
        }
    }

    public int size() {
        if (this.hashtable != null) {
            return this.hashtable.size();
        }
        return 0;
    }

    public boolean isEmpty() {
        if (this.hashtable != null) {
            return this.hashtable.isEmpty();
        }
        return true;
    }

    public synchronized Enumeration keys() {
        if (this.hashtable != null) {
            return this.hashtable.keys();
        }
        return new EmptyEnumerator();
    }

    public synchronized Enumeration elements() {
        if (this.hashtable != null) {
            return this.hashtable.elements();
        }
        return new EmptyEnumerator();
    }

    public synchronized boolean contains(Object object) {
        if (this.hashtable != null) {
            return this.hashtable.contains(object);
        }
        return false;
    }

    public synchronized boolean containsKey(Object object) {
        if (this.hashtable != null) {
            return this.hashtable.containsKey(object);
        }
        return false;
    }

    public synchronized Object get(Object object) {
        if (this.hashtable != null) {
            return this.hashtable.get(object);
        }
        return null;
    }

    public synchronized Object put(Object object, Object object2) {
        if (this.hashtable == null) {
            this.hashtable = new Hashtable(this.initialSize);
        }
        return this.hashtable.put(object, object2);
    }

    public synchronized Object remove(Object object) {
        if (this.hashtable != null) {
            return this.hashtable.remove(object);
        }
        return null;
    }

    public synchronized void clear() {
        if (this.hashtable != null) {
            this.hashtable.clear();
        }
    }

    protected void setHashtable(Hashtable hashtable) {
        this.hashtable = hashtable;
    }

    public Hashtable getHashtable() {
        return this.hashtable;
    }

    public synchronized Object clone() {
        try {
            TagProperties tagProperties = (TagProperties)super.clone();
            if (this.hashtable != null) {
                tagProperties.setHashtable((Hashtable)this.hashtable.clone());
            }
            return tagProperties;
        }
        catch (CloneNotSupportedException var1_2) {
            throw new InternalError();
        }
    }

    public synchronized String toString() {
        if (this.hashtable != null) {
            return this.hashtable.toString();
        }
        return "{ }";
    }

    class EmptyEnumerator
    implements Enumeration {
        EmptyEnumerator() {
        }

        public boolean hasMoreElements() {
            return false;
        }

        public Object nextElement() {
            throw new NoSuchElementException("EmptyEnumerator");
        }
    }

}

