/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import com.sun.java.help.impl.JHelpPrintHandler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Locale;
import javax.help.AbstractHelpAction;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.JHelp;
import javax.help.TextHelpModel;
import javax.help.TreeItem;
import javax.swing.UIManager;

public class PrintAction
extends AbstractHelpAction
implements PropertyChangeListener,
ActionListener {
    private static final String NAME = "PrintAction";
    private JHelpPrintHandler handler = null;

    public PrintAction(Object object) {
        super(object, "PrintAction");
        if (object instanceof JHelp) {
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
            this.putValue("tooltip", HelpUtilities.getString(locale, "tooltip.PrintAction"));
            this.putValue("access", HelpUtilities.getString(locale, "access.PrintAction"));
        }
        this.putValue("icon", UIManager.getIcon("PrintAction.icon"));
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (this.handler != null) {
            JHelp jHelp = (JHelp)this.getControl();
            URL[] arruRL = null;
            TreeItem[] arrtreeItem = jHelp.getSelectedItems();
            if (arrtreeItem != null) {
                arruRL = new URL[arrtreeItem.length];
                int n = 0;
                while (n < arrtreeItem.length) {
                    arruRL[n] = arrtreeItem[n].getURL();
                    ++n;
                }
            }
            if (arruRL != null && arruRL.length > 0) {
                this.handler.print(arruRL);
            } else {
                this.handler.print(jHelp.getModel().getCurrentURL());
            }
        }
    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("enabled")) {
            this.setEnabled((Boolean)propertyChangeEvent.getNewValue());
        }
    }
}

