/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.transform;

import org.jdom.JDOMException;

public class XSLTransformException
extends JDOMException {
    private static final String CVS_ID = "@(#) $RCSfile: XSLTransformException.java,v $ $Revision: 1.3 $ $Date: 2004/02/06 09:28:32 $ $Name: jdom_1_0 $";

    public XSLTransformException() {
    }

    public XSLTransformException(String message) {
        super(message);
    }

    public XSLTransformException(String message, Exception cause) {
        super(message, cause);
    }
}

