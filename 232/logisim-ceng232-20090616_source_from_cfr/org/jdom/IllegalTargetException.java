/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

public class IllegalTargetException
extends IllegalArgumentException {
    private static final String CVS_ID = "@(#) $RCSfile: IllegalTargetException.java,v $ $Revision: 1.14 $ $Date: 2004/02/06 09:28:30 $ $Name: jdom_1_0 $";

    public IllegalTargetException(String reason) {
        super(reason);
    }

    IllegalTargetException(String target, String reason) {
        super("The target \"" + target + "\" is not legal for JDOM/XML Processing Instructions: " + reason + ".");
    }
}

