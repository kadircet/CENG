/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

public class IllegalNameException
extends IllegalArgumentException {
    private static final String CVS_ID = "@(#) $RCSfile: IllegalNameException.java,v $ $Revision: 1.13 $ $Date: 2004/02/06 09:28:30 $ $Name: jdom_1_0 $";

    public IllegalNameException(String reason) {
        super(reason);
    }

    IllegalNameException(String name, String construct) {
        super("The name \"" + name + "\" is not legal for JDOM/XML " + construct + "s.");
    }

    IllegalNameException(String name, String construct, String reason) {
        super("The name \"" + name + "\" is not legal for JDOM/XML " + construct + "s: " + reason + ".");
    }
}

