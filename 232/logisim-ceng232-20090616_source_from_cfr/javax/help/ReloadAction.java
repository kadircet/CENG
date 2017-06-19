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
import javax.help.JHelpContentViewer;
import javax.help.TextHelpModel;
import javax.swing.UIManager;

public class ReloadAction
extends AbstractHelpAction
implements ActionListener {
    private static final String NAME = "ReloadAction";

    public ReloadAction(Object object) {
        super(object, "ReloadAction");
        this.putValue("icon", UIManager.getIcon("ReloadAction.icon"));
        if (object instanceof JHelp) {
            JHelp jHelp = (JHelp)object;
            Locale locale = null;
            try {
                locale = jHelp.getModel().getHelpSet().getLocale();
            }
            catch (NullPointerException var4_4) {
                locale = Locale.getDefault();
            }
            this.putValue("tooltip", HelpUtilities.getString(locale, "tooltip.ReloadAction"));
            this.putValue("access", HelpUtilities.getString(locale, "access.ReloadAction"));
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        JHelp jHelp = (JHelp)this.getControl();
        JHelpContentViewer jHelpContentViewer = jHelp.getContentViewer();
        jHelpContentViewer.reload();
    }
}

