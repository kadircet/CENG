/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.util.Locale;
import javax.help.HelpSet;
import javax.help.Map;
import javax.help.TreeItem;

public class TOCItem
extends TreeItem {
    private Map.ID imageID;

    public TOCItem(Map.ID iD, Map.ID iD2, HelpSet helpSet, Locale locale) {
        super(iD, helpSet, locale);
        this.imageID = iD2;
    }

    public TOCItem(Map.ID iD, Map.ID iD2, Locale locale) {
        super(iD, locale);
        java.lang.Object var4_4 = null;
        if (iD != null) {
            this.setHelpSet(iD.hs);
        }
        this.imageID = iD2;
    }

    public TOCItem() {
        super(null, null, null);
        this.imageID = null;
    }

    public Map.ID getImageID() {
        return this.imageID;
    }
}

