/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.net.URL;
import java.util.Hashtable;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.InvalidNavigatorViewException;
import javax.help.JHelpNavigator;
import javax.help.NavigatorView;
import javax.help.TOCView;
import javax.help.plaf.HelpNavigatorUI;

public class JHelpTOCNavigator
extends JHelpNavigator {
    private static final boolean debug = false;

    public JHelpTOCNavigator(NavigatorView navigatorView) {
        super(navigatorView, null);
    }

    public JHelpTOCNavigator(NavigatorView navigatorView, HelpModel helpModel) {
        super(navigatorView, helpModel);
    }

    public JHelpTOCNavigator(HelpSet helpSet, String string, String string2, URL uRL) throws InvalidNavigatorViewException {
        super(new TOCView(helpSet, string, string2, JHelpNavigator.createParams(uRL)));
    }

    public String getUIClassID() {
        return "HelpTOCNavigatorUI";
    }

    public boolean canMerge(NavigatorView navigatorView) {
        if (navigatorView instanceof TOCView && this.getNavigatorName().equals(navigatorView.getName())) {
            JHelpTOCNavigator.debug("canMerge: true");
            return true;
        }
        JHelpTOCNavigator.debug("canMerge: false");
        return false;
    }

    public void merge(NavigatorView navigatorView) {
        JHelpTOCNavigator.debug("merge: " + navigatorView);
        this.getUI().merge(navigatorView);
    }

    public void remove(NavigatorView navigatorView) {
        this.getUI().remove(navigatorView);
    }

    public void expandID(String string) {
        this.firePropertyChange("expand", " ", string);
    }

    public void collapseID(String string) {
        this.firePropertyChange("collapse", " ", string);
    }

    private static void debug(String string) {
    }
}

