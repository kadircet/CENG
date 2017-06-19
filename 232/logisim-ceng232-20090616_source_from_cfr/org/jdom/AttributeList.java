/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.IllegalAddException;
import org.jdom.Namespace;
import org.jdom.Verifier;

class AttributeList
extends AbstractList
implements List,
Serializable {
    private static final String CVS_ID = "@(#) $RCSfile: AttributeList.java,v $ $Revision: 1.23 $ $Date: 2004/02/28 03:30:27 $ $Name: jdom_1_0 $";
    private static final int INITIAL_ARRAY_SIZE = 5;
    private Attribute[] elementData;
    private int size;
    private Element parent;

    private AttributeList() {
    }

    AttributeList(Element parent) {
        this.parent = parent;
    }

    public void add(int index, Object obj) {
        Attribute attribute;
        if (obj instanceof Attribute) {
            attribute = (Attribute)obj;
            int duplicate = this.indexOfDuplicate(attribute);
            if (duplicate >= 0) {
                throw new IllegalAddException("Cannot add duplicate attribute");
            }
        } else {
            if (obj == null) {
                throw new IllegalAddException("Cannot add null attribute");
            }
            throw new IllegalAddException("Class " + obj.getClass().getName() + " is not an attribute");
        }
        this.add(index, attribute);
        ++this.modCount;
    }

    void add(int index, Attribute attribute) {
        if (attribute.getParent() != null) {
            throw new IllegalAddException("The attribute already has an existing parent \"" + attribute.getParent().getQualifiedName() + "\"");
        }
        String reason = Verifier.checkNamespaceCollision(attribute, this.parent);
        if (reason != null) {
            throw new IllegalAddException(this.parent, attribute, reason);
        }
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        attribute.setParent(this.parent);
        this.ensureCapacity(this.size + 1);
        if (index == this.size) {
            this.elementData[this.size++] = attribute;
        } else {
            System.arraycopy(this.elementData, index, this.elementData, index + 1, this.size - index);
            this.elementData[index] = attribute;
            ++this.size;
        }
        ++this.modCount;
    }

    public boolean add(Object obj) {
        if (obj instanceof Attribute) {
            Attribute attribute = (Attribute)obj;
            int duplicate = this.indexOfDuplicate(attribute);
            if (duplicate < 0) {
                this.add(this.size(), attribute);
            } else {
                this.set(duplicate, attribute);
            }
        } else {
            if (obj == null) {
                throw new IllegalAddException("Cannot add null attribute");
            }
            throw new IllegalAddException("Class " + obj.getClass().getName() + " is not an attribute");
        }
        return true;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public boolean addAll(int index, Collection collection) {
        if (index < 0) throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        if (index > this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        if (collection == null) return false;
        if (collection.size() == 0) {
            return false;
        }
        this.ensureCapacity(this.size() + collection.size());
        count = 0;
        try {
            i = collection.iterator();
            do {
                if (!i.hasNext()) {
                    return true;
                }
                obj = i.next();
                this.add(index + count, obj);
                ++count;
            } while (true);
        }
        catch (RuntimeException exception) {
            i = 0;
            ** while (i < count)
        }
lbl-1000: // 1 sources:
        {
            this.remove(index);
            ++i;
            continue;
        }
lbl24: // 1 sources:
        throw exception;
    }

    public boolean addAll(Collection collection) {
        return this.addAll(this.size(), collection);
    }

    public void clear() {
        if (this.elementData != null) {
            int i = 0;
            while (i < this.size) {
                Attribute attribute = this.elementData[i];
                attribute.setParent(null);
                ++i;
            }
            this.elementData = null;
            this.size = 0;
        }
        ++this.modCount;
    }

    void clearAndSet(Collection collection) {
        Attribute[] old = this.elementData;
        int oldSize = this.size;
        this.elementData = null;
        this.size = 0;
        if (collection != null && collection.size() != 0) {
            this.ensureCapacity(collection.size());
            try {
                this.addAll(0, collection);
            }
            catch (RuntimeException exception) {
                this.elementData = old;
                this.size = oldSize;
                throw exception;
            }
        }
        if (old != null) {
            int i = 0;
            while (i < oldSize) {
                Attribute attribute = old[i];
                attribute.setParent(null);
                ++i;
            }
        }
        ++this.modCount;
    }

    private void ensureCapacity(int minCapacity) {
        if (this.elementData == null) {
            this.elementData = new Attribute[java.lang.Math.max(minCapacity, 5)];
        } else {
            int oldCapacity = this.elementData.length;
            if (minCapacity > oldCapacity) {
                Attribute[] oldData = this.elementData;
                int newCapacity = oldCapacity * 3 / 2 + 1;
                if (newCapacity < minCapacity) {
                    newCapacity = minCapacity;
                }
                this.elementData = new Attribute[newCapacity];
                System.arraycopy(oldData, 0, this.elementData, 0, this.size);
            }
        }
    }

    public Object get(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        return this.elementData[index];
    }

    Object get(String name, Namespace namespace) {
        int index = this.indexOf(name, namespace);
        if (index < 0) {
            return null;
        }
        return this.elementData[index];
    }

    int indexOf(String name, Namespace namespace) {
        String uri = namespace.getURI();
        if (this.elementData != null) {
            int i = 0;
            while (i < this.size) {
                Attribute old = this.elementData[i];
                String oldURI = old.getNamespaceURI();
                String oldName = old.getName();
                if (oldURI.equals(uri) && oldName.equals(name)) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }

    private int indexOfDuplicate(Attribute attribute) {
        int duplicate = -1;
        String name = attribute.getName();
        Namespace namespace = attribute.getNamespace();
        duplicate = this.indexOf(name, namespace);
        return duplicate;
    }

    public Object remove(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        Attribute old = this.elementData[index];
        old.setParent(null);
        int numMoved = this.size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(this.elementData, index + 1, this.elementData, index, numMoved);
        }
        this.elementData[--this.size] = null;
        ++this.modCount;
        return old;
    }

    boolean remove(String name, Namespace namespace) {
        int index = this.indexOf(name, namespace);
        if (index < 0) {
            return false;
        }
        this.remove(index);
        return true;
    }

    public Object set(int index, Object obj) {
        if (obj instanceof Attribute) {
            Attribute attribute = (Attribute)obj;
            int duplicate = this.indexOfDuplicate(attribute);
            if (duplicate >= 0 && duplicate != index) {
                throw new IllegalAddException("Cannot set duplicate attribute");
            }
            return this.set(index, attribute);
        }
        if (obj == null) {
            throw new IllegalAddException("Cannot add null attribute");
        }
        throw new IllegalAddException("Class " + obj.getClass().getName() + " is not an attribute");
    }

    Object set(int index, Attribute attribute) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        if (attribute.getParent() != null) {
            throw new IllegalAddException("The attribute already has an existing parent \"" + attribute.getParent().getQualifiedName() + "\"");
        }
        String reason = Verifier.checkNamespaceCollision(attribute, this.parent);
        if (reason != null) {
            throw new IllegalAddException(this.parent, attribute, reason);
        }
        Attribute old = this.elementData[index];
        old.setParent(null);
        this.elementData[index] = attribute;
        attribute.setParent(this.parent);
        return old;
    }

    public int size() {
        return this.size;
    }

    public String toString() {
        return super.toString();
    }

    final void uncheckedAddAttribute(Attribute a) {
        a.parent = this.parent;
        this.ensureCapacity(this.size + 1);
        this.elementData[this.size++] = a;
        ++this.modCount;
    }
}

