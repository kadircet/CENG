/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.util.Hashtable;
import java.util.Locale;
import javax.help.HelpSet;

public class InvalidNavigatorViewException
extends Exception {
    private HelpSet hs;
    private String name;
    private String label;
    private Locale locale;
    private String className;
    private Hashtable params;

    public InvalidNavigatorViewException(String string, HelpSet helpSet, String string2, String string3, Locale locale, String string4, Hashtable hashtable) {
        super(string);
        this.hs = helpSet;
        this.name = string2;
        this.label = string3;
        this.locale = locale;
        this.className = string4;
        this.params = hashtable;
    }

    public HelpSet getHelpSet() {
        return this.hs;
    }

    public String getName() {
        return this.name;
    }

    public String getLabel() {
        return this.label;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public String getClassName() {
        return this.className;
    }

    public Hashtable getParams() {
        return this.params;
    }
}

