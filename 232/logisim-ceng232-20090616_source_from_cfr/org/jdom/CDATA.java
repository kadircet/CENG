/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import org.jdom.IllegalDataException;
import org.jdom.Text;
import org.jdom.Verifier;

public class CDATA
extends Text {
    private static final String CVS_ID = "@(#) $RCSfile: CDATA.java,v $ $Revision: 1.30 $ $Date: 2004/02/27 11:32:57 $ $Name: jdom_1_0 $";

    protected CDATA() {
    }

    public CDATA(String str) {
        this.setText(str);
    }

    public void append(String str) {
        if (str == null) {
            return;
        }
        String reason = Verifier.checkCDATASection(str);
        if (reason != null) {
            throw new IllegalDataException(str, "CDATA section", reason);
        }
        this.value = this.value == "" ? str : String.valueOf(this.value) + str;
    }

    public Text setText(String str) {
        if (str == null) {
            this.value = "";
            return this;
        }
        String reason = Verifier.checkCDATASection(str);
        if (reason != null) {
            throw new IllegalDataException(str, "CDATA section", reason);
        }
        this.value = str;
        return this;
    }

    public String toString() {
        return new StringBuffer(64).append("[CDATA: ").append(this.getText()).append("]").toString();
    }
}

