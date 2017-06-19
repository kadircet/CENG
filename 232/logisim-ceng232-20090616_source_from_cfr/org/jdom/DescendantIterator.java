/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Parent;

class DescendantIterator
implements Iterator {
    private Iterator iterator;
    private Iterator nextIterator;
    private List stack = new ArrayList();
    private static final String CVS_ID = "@(#) $RCSfile: DescendantIterator.java,v $ $Revision: 1.5 $ $Date: 2004/02/27 11:32:57 $ $Name: jdom_1_0 $";

    DescendantIterator(Parent parent) {
        if (parent == null) {
            throw new IllegalArgumentException("parent parameter was null");
        }
        this.iterator = parent.getContent().iterator();
    }

    public boolean hasNext() {
        if (this.iterator != null && this.iterator.hasNext()) {
            return true;
        }
        if (this.nextIterator != null && this.nextIterator.hasNext()) {
            return true;
        }
        if (this.stackHasAnyNext()) {
            return true;
        }
        return false;
    }

    public Object next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        if (this.nextIterator != null) {
            this.push(this.iterator);
            this.iterator = this.nextIterator;
            this.nextIterator = null;
        }
        while (!this.iterator.hasNext()) {
            if (this.stack.size() > 0) {
                this.iterator = this.pop();
                continue;
            }
            throw new NoSuchElementException("Somehow we lost our iterator");
        }
        Content child = (Content)this.iterator.next();
        if (child instanceof Element) {
            this.nextIterator = ((Element)child).getContent().iterator();
        }
        return child;
    }

    private Iterator pop() {
        int stackSize = this.stack.size();
        if (stackSize == 0) {
            throw new NoSuchElementException("empty stack");
        }
        return (Iterator)this.stack.remove(stackSize - 1);
    }

    private void push(Iterator itr) {
        this.stack.add(itr);
    }

    public void remove() {
        this.iterator.remove();
    }

    private boolean stackHasAnyNext() {
        int size = this.stack.size();
        int i = 0;
        while (i < size) {
            Iterator itr = (Iterator)this.stack.get(i);
            if (itr.hasNext()) {
                return true;
            }
            ++i;
        }
        return false;
    }
}

