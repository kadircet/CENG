/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.output;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public class JDOMLocator
extends LocatorImpl {
    private static final String CVS_ID = "@(#) $RCSfile: JDOMLocator.java,v $ $Revision: 1.3 $ $Date: 2004/02/06 09:28:32 $ $Name: jdom_1_0 $";
    private Object node;

    JDOMLocator() {
    }

    JDOMLocator(Locator locator) {
        super(locator);
        if (locator instanceof JDOMLocator) {
            this.setNode(((JDOMLocator)locator).getNode());
        }
    }

    public Object getNode() {
        return this.node;
    }

    void setNode(Object node) {
        this.node = node;
    }
}

