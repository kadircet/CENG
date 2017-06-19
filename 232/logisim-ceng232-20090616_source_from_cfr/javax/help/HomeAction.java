/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.help.AbstractHelpAction;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.JHelp;
import javax.help.JHelpNavigator;
import javax.help.Map;
import javax.help.TextHelpModel;
import javax.swing.UIManager;

public class HomeAction
extends AbstractHelpAction
implements ActionListener {
    private static final String NAME = "HomeAction";

    public HomeAction(Object object) {
        super(object, "HomeAction");
        this.putValue("icon", UIManager.getIcon("HomeAction.icon"));
        if (object instanceof JHelp) {
            JHelp jHelp = (JHelp)object;
            Locale locale = null;
            try {
                locale = jHelp.getModel().getHelpSet().getLocale();
            }
            catch (NullPointerException var4_4) {
                locale = Locale.getDefault();
            }
            this.putValue("tooltip", HelpUtilities.getString(locale, "tooltip.HomeAction"));
            this.putValue("access", HelpUtilities.getString(locale, "access.HomeAction"));
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        try {
            JHelp jHelp = (JHelp)this.getControl();
            HelpSet helpSet = jHelp.getModel().getHelpSet();
            Map.ID iD = helpSet.getHomeID();
            Locale locale = helpSet.getLocale();
            String string = HelpUtilities.getString(locale, "history.homePage");
            jHelp.setCurrentID(iD, string, jHelp.getCurrentNavigator());
        }
        catch (Exception var2_3) {
            // empty catch block
        }
    }
}

