/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.net.URL;
import java.util.Hashtable;
import javax.help.FavoritesView;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.InvalidNavigatorViewException;
import javax.help.JHelpNavigator;
import javax.help.NavigatorView;
import javax.help.plaf.HelpNavigatorUI;
import javax.swing.Action;

public class JHelpFavoritesNavigator
extends JHelpNavigator {
    private static final boolean debug = false;

    public JHelpFavoritesNavigator(NavigatorView navigatorView) {
        super(navigatorView, null);
    }

    public JHelpFavoritesNavigator(NavigatorView navigatorView, HelpModel helpModel) {
        super(navigatorView, helpModel);
    }

    public JHelpFavoritesNavigator(HelpSet helpSet, String string, String string2, URL uRL) throws InvalidNavigatorViewException {
        super(new FavoritesView(helpSet, string, string2, JHelpNavigator.createParams(uRL)));
    }

    public String getUIClassID() {
        return "HelpFavoritesNavigatorUI";
    }

    public boolean canMerge(NavigatorView navigatorView) {
        return false;
    }

    public void expandID(String string) {
        this.firePropertyChange("expand", " ", string);
    }

    public void collapseID(String string) {
        this.firePropertyChange("collapse", " ", string);
    }

    public Action getAddAction() {
        return this.getUI().getAddAction();
    }

    private static void debug(String string) {
    }
}

