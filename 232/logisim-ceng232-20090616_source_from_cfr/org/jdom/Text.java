/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import org.jdom.Content;
import org.jdom.IllegalDataException;
import org.jdom.Verifier;

public class Text
extends Content {
    private static final String CVS_ID = "@(#) $RCSfile: Text.java,v $ $Revision: 1.24 $ $Date: 2004/02/27 11:32:57 $ $Name: jdom_1_0 $";
    static final String EMPTY_STRING = "";
    protected String value;

    protected Text() {
    }

    public Text(String str) {
        this.setText(str);
    }

    public void append(String str) {
        if (str == null) {
            return;
        }
        String reason = Verifier.checkCharacterData(str);
        if (reason != null) {
            throw new IllegalDataException(str, "character content", reason);
        }
        this.value = str == "" ? str : String.valueOf(this.value) + str;
    }

    public void append(Text text) {
        if (text == null) {
            return;
        }
        this.value = String.valueOf(this.value) + text.getText();
    }

    public Object clone() {
        Text text = (Text)super.clone();
        text.value = this.value;
        return text;
    }

    public String getText() {
        return this.value;
    }

    public String getTextNormalize() {
        return Text.normalizeString(this.getText());
    }

    public String getTextTrim() {
        return this.getText().trim();
    }

    public String getValue() {
        return this.value;
    }

    public static String normalizeString(String str) {
        if (str == null) {
            return "";
        }
        char[] c = str.toCharArray();
        char[] n = new char[c.length];
        boolean white = true;
        int pos = 0;
        int i = 0;
        while (i < c.length) {
            if (" \t\n\r".indexOf(c[i]) != -1) {
                if (!white) {
                    n[pos++] = 32;
                    white = true;
                }
            } else {
                n[pos++] = c[i];
                white = false;
            }
            ++i;
        }
        if (white && pos > 0) {
            --pos;
        }
        return new String(n, 0, pos);
    }

    public Text setText(String str) {
        if (str == null) {
            this.value = "";
            return this;
        }
        String reason = Verifier.checkCharacterData(str);
        if (reason != null) {
            throw new IllegalDataException(str, "character content", reason);
        }
        this.value = str;
        return this;
    }

    public String toString() {
        return new StringBuffer(64).append("[Text: ").append(this.getText()).append("]").toString();
    }
}

