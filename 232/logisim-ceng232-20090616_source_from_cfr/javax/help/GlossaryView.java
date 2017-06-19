/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.awt.Component;
import java.util.Hashtable;
import java.util.Locale;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.IndexView;
import javax.help.JHelpGlossaryNavigator;
import javax.help.NavigatorView;

public class GlossaryView
extends IndexView {
    public GlossaryView(HelpSet helpSet, String string, String string2, Hashtable hashtable) {
        super(helpSet, string, string2, helpSet.getLocale(), hashtable);
    }

    public GlossaryView(HelpSet helpSet, String string, String string2, Locale locale, Hashtable hashtable) {
        super(helpSet, string, string2, locale, hashtable);
    }

    public Component createNavigator(HelpModel helpModel) {
        return new JHelpGlossaryNavigator(this, helpModel);
    }
}

