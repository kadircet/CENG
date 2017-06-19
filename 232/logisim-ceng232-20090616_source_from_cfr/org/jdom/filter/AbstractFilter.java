/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.filter;

import org.jdom.filter.AndFilter;
import org.jdom.filter.Filter;
import org.jdom.filter.NegateFilter;
import org.jdom.filter.OrFilter;

public abstract class AbstractFilter
implements Filter {
    private static final String CVS_ID = "@(#) $RCSfile: AbstractFilter.java,v $ $Revision: 1.5 $ $Date: 2004/02/27 11:32:58 $";

    public Filter and(Filter filter) {
        return new AndFilter(this, filter);
    }

    public abstract boolean matches(Object var1);

    public Filter negate() {
        return new NegateFilter(this);
    }

    public Filter or(Filter filter) {
        return new OrFilter(this, filter);
    }
}

