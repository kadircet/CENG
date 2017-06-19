/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

public class IllegalDataException
extends IllegalArgumentException {
    private static final String CVS_ID = "@(#) $RCSfile: IllegalDataException.java,v $ $Revision: 1.13 $ $Date: 2004/02/06 09:28:30 $ $Name: jdom_1_0 $";

    public IllegalDataException(String reason) {
        super(reason);
    }

    IllegalDataException(String data, String construct) {
        super("The data \"" + data + "\" is not legal for a JDOM " + construct + ".");
    }

    IllegalDataException(String data, String construct, String reason) {
        super("The data \"" + data + "\" is not legal for a JDOM " + construct + ": " + reason + ".");
    }
}

