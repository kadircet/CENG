/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import com.sun.java.help.impl.JHelpPrintHandler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.help.AbstractHelpAction;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.JHelp;
import javax.help.TextHelpModel;
import javax.swing.UIManager;

public class PrintSetupAction
extends AbstractHelpAction
implements PropertyChangeListener,
ActionListener {
    private static final String NAME = "PrintSetupAction";
    private JHelpPrintHandler handler = null;

    public PrintSetupAction(Object object) {
        super(object, "PrintSetupAction");
        if (this.getControl() instanceof JHelp) {
            JHelp jHelp = (JHelp)object;
            this.handler = JHelpPrintHandler.getJHelpPrintHandler(jHelp);
            this.handler.addPropertyChangeListener(this);
            Locale locale = null;
            try {
                locale = jHelp.getModel().getHelpSet().getLocale();
            }
            catch (NullPointerException var4_4) {
                locale = Locale.getDefault();
            }
            this.putValue("tooltip", HelpUtilities.getString(locale, "tooltip.PrintSetupAction"));
            this.putValue("access", HelpUtilities.getString(locale, "access.PrintSetupAction"));
        }
        this.putValue("icon", UIManager.getIcon("PrintSetupAction.icon"));
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (this.handler != null) {
            this.handler.printSetup();
        }
    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("enabled")) {
            this.setEnabled((Boolean)propertyChangeEvent.getNewValue());
        }
    }
}

