/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  javax.help.JHelpSearchNavigator
 */
package javax.help;

import java.awt.Component;
import java.util.Hashtable;
import java.util.Locale;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.JHelpSearchNavigator;
import javax.help.NavigatorView;

public class SearchView
extends NavigatorView {
    public SearchView(HelpSet helpSet, String string, String string2, Hashtable hashtable) {
        super(helpSet, string, string2, helpSet.getLocale(), hashtable);
    }

    public SearchView(HelpSet helpSet, String string, String string2, Locale locale, Hashtable hashtable) {
        super(helpSet, string, string2, locale, hashtable);
    }

    public Component createNavigator(HelpModel helpModel) {
        return new JHelpSearchNavigator((NavigatorView)this, helpModel);
    }
}

