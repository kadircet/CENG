/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import org.jdom.Content;
import org.jdom.IllegalDataException;
import org.jdom.Verifier;
import org.jdom.output.XMLOutputter;

public class Comment
extends Content {
    private static final String CVS_ID = "@(#) $RCSfile: Comment.java,v $ $Revision: 1.32 $ $Date: 2004/02/11 21:12:43 $ $Name: jdom_1_0 $";
    protected String text;

    protected Comment() {
    }

    public Comment(String text) {
        this.setText(text);
    }

    public String getText() {
        return this.text;
    }

    public String getValue() {
        return this.text;
    }

    public Comment setText(String text) {
        String reason = Verifier.checkCommentData(text);
        if (reason != null) {
            throw new IllegalDataException(text, "comment", reason);
        }
        this.text = text;
        return this;
    }

    public String toString() {
        return "[Comment: " + new XMLOutputter().outputString(this) + "]";
    }
}

