/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import javax.help.HelpSet;

public class InvalidHelpSetContextException
extends Exception {
    private HelpSet context;
    private HelpSet hs;

    public InvalidHelpSetContextException(String string, HelpSet helpSet, HelpSet helpSet2) {
        super(string);
        this.context = helpSet;
        this.hs = helpSet2;
    }

    public HelpSet getContext() {
        return this.context;
    }

    public HelpSet getHelpSet() {
        return this.hs;
    }
}

