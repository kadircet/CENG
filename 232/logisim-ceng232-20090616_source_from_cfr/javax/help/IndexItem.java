/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.util.Locale;
import javax.help.HelpSet;
import javax.help.Map;
import javax.help.TreeItem;

public class IndexItem
extends TreeItem {
    public IndexItem(Map.ID iD, HelpSet helpSet, Locale locale) {
        super(iD, helpSet, locale);
    }

    public IndexItem(Map.ID iD, Locale locale) {
        super(iD, locale);
        java.lang.Object var3_3 = null;
        if (iD != null) {
            this.setHelpSet(iD.hs);
        }
    }

    public IndexItem() {
        super(null, null, null);
    }
}

