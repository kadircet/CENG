/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import javax.help.HelpSet;
import javax.help.Map;

public class BadIDException
extends IllegalArgumentException {
    private Map map;
    private String id;
    private HelpSet hs;

    public BadIDException(String string, Map map, String string2, HelpSet helpSet) {
        super(string);
        this.map = map;
        this.id = string2;
        this.hs = helpSet;
    }

    public Map getMap() {
        return this.map;
    }

    public String getID() {
        return this.id;
    }

    public HelpSet getHelpSet() {
        return this.hs;
    }
}

