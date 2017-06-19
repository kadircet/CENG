/*
 * Decompiled with CFR 0_114.
 */
package javax.help.event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventListener;

public class EventListenerList
implements Serializable {
    private static final Object[] NULL_ARRAY = new Object[0];
    protected transient Object[] listenerList = NULL_ARRAY;

    public Object[] getListenerList() {
        return this.listenerList;
    }

    public int getListenerCount() {
        return this.listenerList.length / 2;
    }

    public int getListenerCount(Class class_) {
        int n = 0;
        Object[] arrobject = this.listenerList;
        int n2 = 0;
        while (n2 < arrobject.length) {
            if (class_ == (Class)arrobject[n2]) {
                ++n;
            }
            n2 += 2;
        }
        return n;
    }

    public synchronized void add(Class class_, EventListener eventListener) {
        if (eventListener == null || class_ == null) {
            throw new IllegalArgumentException("Listener " + eventListener + " is null");
        }
        if (!class_.isInstance(eventListener)) {
            throw new IllegalArgumentException("Listener " + eventListener + " is not of type " + class_);
        }
        if (this.listenerList == NULL_ARRAY) {
            this.listenerList = new Object[]{class_, eventListener};
        } else {
            int n = this.listenerList.length;
            Object[] arrobject = new Object[n + 2];
            System.arraycopy(this.listenerList, 0, arrobject, 0, n);
            arrobject[n] = class_;
            arrobject[n + 1] = eventListener;
            this.listenerList = arrobject;
        }
    }

    public synchronized void remove(Class class_, EventListener eventListener) {
        if (eventListener == null || class_ == null) {
            throw new IllegalArgumentException("Listener " + eventListener + " is null");
        }
        if (!class_.isInstance(eventListener)) {
            throw new IllegalArgumentException("Listener " + eventListener + " is not of type " + class_);
        }
        int n = -1;
        int n2 = this.listenerList.length - 2;
        while (n2 >= 0) {
            if (this.listenerList[n2] == class_ && this.listenerList[n2 + 1] == eventListener) {
                n = n2;
                break;
            }
            n2 -= 2;
        }
        if (n != -1) {
            Object[] arrobject = new Object[this.listenerList.length - 2];
            System.arraycopy(this.listenerList, 0, arrobject, 0, n);
            if (n < arrobject.length) {
                System.arraycopy(this.listenerList, n + 2, arrobject, n, arrobject.length - n);
            }
            this.listenerList = arrobject.length == 0 ? NULL_ARRAY : arrobject;
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        Object[] arrobject = this.listenerList;
        objectOutputStream.defaultWriteObject();
        int n = 0;
        while (n < arrobject.length) {
            Class class_ = (Class)arrobject[n];
            EventListener eventListener = (EventListener)arrobject[n + 1];
            if (eventListener != null && eventListener instanceof Serializable) {
                objectOutputStream.writeObject(class_.getName());
                objectOutputStream.writeObject(eventListener);
            }
            n += 2;
        }
        objectOutputStream.writeObject(null);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        Object object;
        this.listenerList = NULL_ARRAY;
        objectInputStream.defaultReadObject();
        while (null != (object = objectInputStream.readObject())) {
            EventListener eventListener = (EventListener)objectInputStream.readObject();
            this.add(Class.forName((String)object), eventListener);
        }
    }

    public String toString() {
        Object[] arrobject = this.listenerList;
        String string = "EventListenerList: ";
        string = string + arrobject.length / 2 + " listeners: ";
        int n = 0;
        while (n <= arrobject.length - 2) {
            string = string + " type " + ((Class)arrobject[n]).getName();
            string = string + " listener " + arrobject[n + 1];
            n += 2;
        }
        return string;
    }
}

