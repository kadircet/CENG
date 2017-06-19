/*
 * Decompiled with CFR 0_114.
 */
package javax.help.event;

import java.util.EventObject;
import javax.help.HelpSet;

public class HelpSetEvent
extends EventObject {
    public static final int HELPSET_ADDED = 0;
    public static final int HELPSET_REMOVED = 1;
    private HelpSet helpset;
    private int action;

    public HelpSetEvent(Object object, HelpSet helpSet, int n) {
        super(object);
        this.helpset = helpSet;
        if (helpSet == null) {
            throw new NullPointerException("helpset");
        }
        this.action = n;
        if (n < 0 || n > 1) {
            throw new IllegalArgumentException("invalid action");
        }
    }

    public HelpSet getHelpSet() {
        return this.helpset;
    }

    public int getAction() {
        return this.action;
    }

    public String toString() {
        if (this.action == 0) {
            return "HelpSetEvent(" + this.source + ", " + this.helpset + "; added";
        }
        return "HelpSetEvent(" + this.source + ", " + this.helpset + "; removed";
    }
}

