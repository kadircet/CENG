/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jdom.filter.Filter;

class FilterIterator
implements Iterator {
    private Iterator iterator;
    private Filter filter;
    private Object nextObject;
    private static final String CVS_ID = "@(#) $RCSfile: FilterIterator.java,v $ $Revision: 1.5 $ $Date: 2004/08/31 19:36:12 $ $Name: jdom_1_0 $";

    public FilterIterator(Iterator iterator, Filter filter) {
        if (iterator == null || filter == null) {
            throw new IllegalArgumentException("null parameter");
        }
        this.iterator = iterator;
        this.filter = filter;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    public boolean hasNext() {
        if (this.nextObject == null) ** GOTO lbl7
        return true;
lbl-1000: // 1 sources:
        {
            obj = this.iterator.next();
            if (!this.filter.matches(obj)) continue;
            this.nextObject = obj;
            return true;
lbl7: // 2 sources:
            ** while (this.iterator.hasNext())
        }
lbl8: // 1 sources:
        return false;
    }

    public Object next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        Object obj = this.nextObject;
        this.nextObject = null;
        return obj;
    }

    public void remove() {
        this.iterator.remove();
    }
}

