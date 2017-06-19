/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.jdom.CDATA;
import org.jdom.Content;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.IllegalAddException;
import org.jdom.Parent;
import org.jdom.Text;
import org.jdom.filter.Filter;

final class ContentList
extends AbstractList
implements Serializable {
    private static final String CVS_ID = "@(#) $RCSfile: ContentList.java,v $ $Revision: 1.39 $ $Date: 2004/02/28 03:30:27 $ $Name: jdom_1_0 $";
    private static final int INITIAL_ARRAY_SIZE = 5;
    private static final int CREATE = 0;
    private static final int HASPREV = 1;
    private static final int HASNEXT = 2;
    private static final int PREV = 3;
    private static final int NEXT = 4;
    private static final int ADD = 5;
    private static final int REMOVE = 6;
    private Content[] elementData;
    private int size;
    private Parent parent;

    ContentList(Parent parent) {
        this.parent = parent;
    }

    public void add(int index, Object obj) {
        if (obj == null) {
            throw new IllegalAddException("Cannot add null object");
        }
        if (!(obj instanceof Content)) {
            throw new IllegalAddException("Class " + obj.getClass().getName() + " is of unrecognized type and cannot be added");
        }
        this.add(index, (Content)obj);
    }

    void add(int index, Content child) {
        if (child == null) {
            throw new IllegalAddException("Cannot add null object");
        }
        if (this.parent instanceof Document) {
            this.documentCanContain(index, child);
        } else {
            ContentList.elementCanContain(index, child);
        }
        if (child.getParent() != null) {
            Parent p = child.getParent();
            if (p instanceof Document) {
                throw new IllegalAddException((Element)child, "The Content already has an existing parent document");
            }
            throw new IllegalAddException("The Content already has an existing parent \"" + ((Element)p).getQualifiedName() + "\"");
        }
        if (child == this.parent) {
            throw new IllegalAddException("The Element cannot be added to itself");
        }
        if (this.parent instanceof Element && child instanceof Element && ((Element)child).isAncestor((Element)this.parent)) {
            throw new IllegalAddException("The Element cannot be added as a descendent of itself");
        }
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        child.setParent(this.parent);
        this.ensureCapacity(this.size + 1);
        if (index == this.size) {
            this.elementData[this.size++] = child;
        } else {
            System.arraycopy(this.elementData, index, this.elementData, index + 1, this.size - index);
            this.elementData[index] = child;
            ++this.size;
        }
        ++this.modCount;
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
                Content obj = this.elementData[i];
                ContentList.removeParent(obj);
                ++i;
            }
            this.elementData = null;
            this.size = 0;
        }
        ++this.modCount;
    }

    void clearAndSet(Collection collection) {
        Content[] old = this.elementData;
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
                ContentList.removeParent(old[i]);
                ++i;
            }
        }
        ++this.modCount;
    }

    private void documentCanContain(int index, Content child) throws IllegalAddException {
        if (child instanceof Element) {
            if (this.indexOfFirstElement() >= 0) {
                throw new IllegalAddException("Cannot add a second root element, only one is allowed");
            }
            if (this.indexOfDocType() > index) {
                throw new IllegalAddException("A root element cannot be added before the DocType");
            }
        }
        if (child instanceof DocType) {
            if (this.indexOfDocType() >= 0) {
                throw new IllegalAddException("Cannot add a second doctype, only one is allowed");
            }
            int firstElt = this.indexOfFirstElement();
            if (firstElt != -1 && firstElt < index) {
                throw new IllegalAddException("A DocType cannot be added after the root element");
            }
        }
        if (child instanceof CDATA) {
            throw new IllegalAddException("A CDATA is not allowed at the document root");
        }
        if (child instanceof Text) {
            throw new IllegalAddException("A Text is not allowed at the document root");
        }
        if (child instanceof EntityRef) {
            throw new IllegalAddException("An EntityRef is not allowed at the document root");
        }
    }

    private static void elementCanContain(int index, Content child) throws IllegalAddException {
        if (child instanceof DocType) {
            throw new IllegalAddException("A DocType is not allowed except at the document level");
        }
    }

    void ensureCapacity(int minCapacity) {
        if (this.elementData == null) {
            this.elementData = new Content[java.lang.Math.max(minCapacity, 5)];
        } else {
            int oldCapacity = this.elementData.length;
            if (minCapacity > oldCapacity) {
                Content[] oldData = this.elementData;
                int newCapacity = oldCapacity * 3 / 2 + 1;
                if (newCapacity < minCapacity) {
                    newCapacity = minCapacity;
                }
                this.elementData = new Content[newCapacity];
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

    private int getModCount() {
        return this.modCount;
    }

    List getView(Filter filter) {
        return new FilterList(filter);
    }

    int indexOfDocType() {
        if (this.elementData != null) {
            int i = 0;
            while (i < this.size) {
                if (this.elementData[i] instanceof DocType) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }

    int indexOfFirstElement() {
        if (this.elementData != null) {
            int i = 0;
            while (i < this.size) {
                if (this.elementData[i] instanceof Element) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }

    public Object remove(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        Content old = this.elementData[index];
        ContentList.removeParent(old);
        int numMoved = this.size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(this.elementData, index + 1, this.elementData, index, numMoved);
        }
        this.elementData[--this.size] = null;
        ++this.modCount;
        return old;
    }

    private static void removeParent(Content c) {
        c.setParent(null);
    }

    public Object set(int index, Object obj) {
        int root;
        int docTypeIndex;
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        if (obj instanceof Element && this.parent instanceof Document && (root = this.indexOfFirstElement()) >= 0 && root != index) {
            throw new IllegalAddException("Cannot add a second root element, only one is allowed");
        }
        if (obj instanceof DocType && this.parent instanceof Document && (docTypeIndex = this.indexOfDocType()) >= 0 && docTypeIndex != index) {
            throw new IllegalAddException("Cannot add a second doctype, only one is allowed");
        }
        Object old = this.remove(index);
        try {
            this.add(index, obj);
        }
        catch (RuntimeException exception) {
            this.add(index, old);
            throw exception;
        }
        return old;
    }

    public int size() {
        return this.size;
    }

    public String toString() {
        return super.toString();
    }

    final void uncheckedAddContent(Content c) {
        c.parent = this.parent;
        this.ensureCapacity(this.size + 1);
        this.elementData[this.size++] = c;
        ++this.modCount;
    }

    class FilterList
    extends AbstractList
    implements Serializable {
        Filter filter;
        int count;
        int expected;

        FilterList(Filter filter) {
            this.count = 0;
            this.expected = -1;
            this.filter = filter;
        }

        public void add(int index, Object obj) {
            if (this.filter.matches(obj)) {
                int adjusted = this.getAdjustedIndex(index);
                ContentList.this.add(adjusted, obj);
                ++this.expected;
                ++this.count;
            } else {
                throw new IllegalAddException("Filter won't allow the " + obj.getClass().getName() + " '" + obj + "' to be added to the list");
            }
        }

        public Object get(int index) {
            int adjusted = this.getAdjustedIndex(index);
            return ContentList.this.get(adjusted);
        }

        private final int getAdjustedIndex(int index) {
            int adjusted = 0;
            int i = 0;
            while (i < ContentList.this.size) {
                Content obj = ContentList.this.elementData[i];
                if (this.filter.matches(obj)) {
                    if (index == adjusted) {
                        return i;
                    }
                    ++adjusted;
                }
                ++i;
            }
            if (index == adjusted) {
                return ContentList.this.size;
            }
            return ContentList.this.size + 1;
        }

        public Iterator iterator() {
            return new FilterListIterator(this.filter, 0);
        }

        public ListIterator listIterator() {
            return new FilterListIterator(this.filter, 0);
        }

        public ListIterator listIterator(int index) {
            return new FilterListIterator(this.filter, index);
        }

        public Object remove(int index) {
            int adjusted = this.getAdjustedIndex(index);
            Object old = ContentList.this.get(adjusted);
            if (this.filter.matches(old)) {
                old = ContentList.this.remove(adjusted);
                ++this.expected;
                --this.count;
            } else {
                throw new IllegalAddException("Filter won't allow the " + old.getClass().getName() + " '" + old + "' (index " + index + ") to be removed");
            }
            return old;
        }

        public Object set(int index, Object obj) {
            Object old = null;
            if (this.filter.matches(obj)) {
                int adjusted = this.getAdjustedIndex(index);
                old = ContentList.this.get(adjusted);
                if (!this.filter.matches(old)) {
                    throw new IllegalAddException("Filter won't allow the " + old.getClass().getName() + " '" + old + "' (index " + index + ") to be removed");
                }
                old = ContentList.this.set(adjusted, obj);
                this.expected += 2;
            } else {
                throw new IllegalAddException("Filter won't allow index " + index + " to be set to " + obj.getClass().getName());
            }
            return old;
        }

        public int size() {
            if (this.expected == ContentList.this.getModCount()) {
                return this.count;
            }
            this.count = 0;
            int i = 0;
            while (i < ContentList.this.size()) {
                Content obj = ContentList.this.elementData[i];
                if (this.filter.matches(obj)) {
                    ++this.count;
                }
                ++i;
            }
            this.expected = ContentList.this.getModCount();
            return this.count;
        }
    }

    class FilterListIterator
    implements ListIterator {
        Filter filter;
        int lastOperation;
        int initialCursor;
        int cursor;
        int last;
        int expected;

        FilterListIterator(Filter filter, int start) {
            this.filter = filter;
            this.initialCursor = this.initializeCursor(start);
            this.last = -1;
            this.expected = ContentList.this.getModCount();
            this.lastOperation = 0;
        }

        public void add(Object obj) {
            this.checkConcurrentModification();
            if (!this.filter.matches(obj)) {
                throw new IllegalAddException("Filter won't allow add of " + obj.getClass().getName());
            }
            this.last = this.cursor + 1;
            ContentList.this.add(this.last, obj);
            this.expected = ContentList.this.getModCount();
            this.lastOperation = 5;
        }

        private void checkConcurrentModification() {
            if (this.expected != ContentList.this.getModCount()) {
                throw new ConcurrentModificationException();
            }
        }

        public boolean hasNext() {
            this.checkConcurrentModification();
            switch (this.lastOperation) {
                case 0: {
                    this.cursor = this.initialCursor;
                    break;
                }
                case 3: {
                    this.cursor = this.last;
                    break;
                }
                case 4: 
                case 5: {
                    this.cursor = this.moveForward(this.last + 1);
                    break;
                }
                case 6: {
                    this.cursor = this.moveForward(this.last);
                    break;
                }
                case 1: {
                    this.cursor = this.moveForward(this.cursor + 1);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unknown operation");
                }
                case 2: 
            }
            if (this.lastOperation != 0) {
                this.lastOperation = 2;
            }
            return this.cursor < ContentList.this.size();
        }

        public boolean hasPrevious() {
            this.checkConcurrentModification();
            switch (this.lastOperation) {
                case 0: {
                    this.cursor = this.initialCursor;
                    int size = ContentList.this.size();
                    if (this.cursor < size) break;
                    this.cursor = this.moveBackward(size - 1);
                    break;
                }
                case 3: 
                case 6: {
                    this.cursor = this.moveBackward(this.last - 1);
                    break;
                }
                case 2: {
                    this.cursor = this.moveBackward(this.cursor - 1);
                    break;
                }
                case 4: 
                case 5: {
                    this.cursor = this.last;
                    break;
                }
                default: {
                    throw new IllegalStateException("Unknown operation");
                }
                case 1: 
            }
            if (this.lastOperation != 0) {
                this.lastOperation = 1;
            }
            return this.cursor >= 0;
        }

        private int initializeCursor(int start) {
            if (start < 0) {
                throw new IndexOutOfBoundsException("Index: " + start);
            }
            int count = 0;
            int i = 0;
            while (i < ContentList.this.size()) {
                Object obj = ContentList.this.get(i);
                if (this.filter.matches(obj)) {
                    if (start == count) {
                        return i;
                    }
                    ++count;
                }
                ++i;
            }
            if (start > count) {
                throw new IndexOutOfBoundsException("Index: " + start + " Size: " + count);
            }
            return ContentList.this.size();
        }

        private int moveBackward(int start) {
            if (start >= ContentList.this.size()) {
                start = ContentList.this.size() - 1;
            }
            int i = start;
            while (i >= 0) {
                Object obj = ContentList.this.get(i);
                if (this.filter.matches(obj)) {
                    return i;
                }
                --i;
            }
            return -1;
        }

        private int moveForward(int start) {
            if (start < 0) {
                start = 0;
            }
            int i = start;
            while (i < ContentList.this.size()) {
                Object obj = ContentList.this.get(i);
                if (this.filter.matches(obj)) {
                    return i;
                }
                ++i;
            }
            return ContentList.this.size();
        }

        public Object next() {
            this.checkConcurrentModification();
            if (!this.hasNext()) {
                this.last = ContentList.this.size();
                throw new NoSuchElementException();
            }
            this.last = this.cursor;
            this.lastOperation = 4;
            return ContentList.this.get(this.last);
        }

        public int nextIndex() {
            this.checkConcurrentModification();
            this.hasNext();
            int count = 0;
            int i = 0;
            while (i < ContentList.this.size()) {
                if (this.filter.matches(ContentList.this.get(i))) {
                    if (i == this.cursor) {
                        return count;
                    }
                    ++count;
                }
                ++i;
            }
            this.expected = ContentList.this.getModCount();
            return count;
        }

        public Object previous() {
            this.checkConcurrentModification();
            if (!this.hasPrevious()) {
                this.last = -1;
                throw new NoSuchElementException();
            }
            this.last = this.cursor;
            this.lastOperation = 3;
            return ContentList.this.get(this.last);
        }

        public int previousIndex() {
            this.checkConcurrentModification();
            if (this.hasPrevious()) {
                int count = 0;
                int i = 0;
                while (i < ContentList.this.size()) {
                    if (this.filter.matches(ContentList.this.get(i))) {
                        if (i == this.cursor) {
                            return count;
                        }
                        ++count;
                    }
                    ++i;
                }
            }
            return -1;
        }

        public void remove() {
            this.checkConcurrentModification();
            if (this.last < 0 || this.lastOperation == 6) {
                throw new IllegalStateException("no preceeding call to prev() or next()");
            }
            if (this.lastOperation == 5) {
                throw new IllegalStateException("cannot call remove() after add()");
            }
            Object old = ContentList.this.get(this.last);
            if (!this.filter.matches(old)) {
                throw new IllegalAddException("Filter won't allow " + old.getClass().getName() + " (index " + this.last + ") to be removed");
            }
            ContentList.this.remove(this.last);
            this.expected = ContentList.this.getModCount();
            this.lastOperation = 6;
        }

        public void set(Object obj) {
            this.checkConcurrentModification();
            if (this.lastOperation == 5 || this.lastOperation == 6) {
                throw new IllegalStateException("cannot call set() after add() or remove()");
            }
            if (this.last < 0) {
                throw new IllegalStateException("no preceeding call to prev() or next()");
            }
            if (this.filter.matches(obj)) {
                Object old = ContentList.this.get(this.last);
                if (!this.filter.matches(old)) {
                    throw new IllegalAddException("Filter won't allow " + old.getClass().getName() + " (index " + this.last + ") to be removed");
                }
            } else {
                throw new IllegalAddException("Filter won't allow index " + this.last + " to be set to " + obj.getClass().getName());
            }
            ContentList.this.set(this.last, obj);
            this.expected = ContentList.this.getModCount();
        }
    }

}

