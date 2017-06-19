/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.util.Hashtable;
import javax.help.GlossaryView;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.InvalidNavigatorViewException;
import javax.help.JHelpNavigator;
import javax.help.NavigatorView;

public class JHelpGlossaryNavigator
extends JHelpNavigator {
    private static final boolean debug = false;

    public JHelpGlossaryNavigator(NavigatorView navigatorView) {
        super(navigatorView);
    }

    public JHelpGlossaryNavigator(NavigatorView navigatorView, HelpModel helpModel) {
        super(navigatorView, helpModel);
    }

    public JHelpGlossaryNavigator(HelpSet helpSet, String string, String string2) throws InvalidNavigatorViewException {
        super(new GlossaryView(helpSet, string, string2, null));
    }

    public String getUIClassID() {
        return "HelpGlossaryNavigatorUI";
    }

    private static void debug(Object object, Object object2, Object object3) {
    }

    private static void debug(Object object) {
        JHelpGlossaryNavigator.debug(object, null, null);
    }

    private static void debug(Object object, Object object2) {
        JHelpGlossaryNavigator.debug(object, object2, null);
    }
}

