/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.filter;

import org.jdom.filter.AbstractFilter;
import org.jdom.filter.Filter;

final class NegateFilter
extends AbstractFilter {
    private static final String CVS_ID = "@(#) $RCSfile: NegateFilter.java,v $ $Revision: 1.3 $ $Date: 2004/02/06 09:28:31 $";
    private Filter filter;

    public NegateFilter(Filter filter) {
        this.filter = filter;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NegateFilter) {
            return this.filter.equals(((NegateFilter)obj).filter);
        }
        return false;
    }

    public int hashCode() {
        return ~ this.filter.hashCode();
    }

    public boolean matches(Object obj) {
        return this.filter.matches(obj) ^ true;
    }

    public Filter negate() {
        return this.filter;
    }

    public String toString() {
        return new StringBuffer(64).append("[NegateFilter: ").append(this.filter.toString()).append("]").toString();
    }
}

