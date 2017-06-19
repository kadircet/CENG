/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.net.URL;
import java.util.Hashtable;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.IndexView;
import javax.help.InvalidNavigatorViewException;
import javax.help.JHelpNavigator;
import javax.help.NavigatorView;
import javax.help.plaf.HelpNavigatorUI;

public class JHelpIndexNavigator
extends JHelpNavigator {
    private static final boolean debug = false;

    public JHelpIndexNavigator(NavigatorView navigatorView) {
        super(navigatorView, null);
    }

    public JHelpIndexNavigator(NavigatorView navigatorView, HelpModel helpModel) {
        super(navigatorView, helpModel);
    }

    public JHelpIndexNavigator(HelpSet helpSet, String string, String string2, URL uRL) throws InvalidNavigatorViewException {
        super(new IndexView(helpSet, string, string2, JHelpNavigator.createParams(uRL)));
    }

    public String getUIClassID() {
        return "HelpIndexNavigatorUI";
    }

    public boolean canMerge(NavigatorView navigatorView) {
        if (navigatorView instanceof IndexView && this.getNavigatorName().equals(navigatorView.getName())) {
            return true;
        }
        return false;
    }

    public void merge(NavigatorView navigatorView) {
        JHelpIndexNavigator.debug("merge of: " + navigatorView);
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

