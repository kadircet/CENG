/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Locale;
import javax.help.AbstractHelpAction;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.JHelp;
import javax.help.JHelpFavoritesNavigator;
import javax.help.JHelpNavigator;
import javax.help.TextHelpModel;
import javax.swing.Action;
import javax.swing.UIManager;

public class FavoritesAction
extends AbstractHelpAction
implements ActionListener {
    private static final String NAME = "FavoritesAction";
    private JHelpFavoritesNavigator favorites = null;
    private ActionListener favoritesActionListener = null;

    public FavoritesAction(Object object) {
        super(object, "FavoritesAction");
        if (object instanceof JHelp) {
            void var4_8;
            JHelp jHelp = (JHelp)object;
            Enumeration enumeration = jHelp.getHelpNavigators();
            while (enumeration.hasMoreElements()) {
                JHelpNavigator jHelpNavigator = (JHelpNavigator)enumeration.nextElement();
                if (!(jHelpNavigator instanceof JHelpFavoritesNavigator)) continue;
                this.favorites = (JHelpFavoritesNavigator)jHelpNavigator;
                this.favoritesActionListener = this.favorites.getAddAction();
            }
            this.setEnabled(this.favoritesActionListener != null);
            this.putValue("icon", UIManager.getIcon("FavoritesAction.icon"));
            Object var4_5 = null;
            try {
                Locale locale = jHelp.getModel().getHelpSet().getLocale();
            }
            catch (NullPointerException var5_9) {
                Locale locale = Locale.getDefault();
            }
            this.putValue("tooltip", HelpUtilities.getString((Locale)var4_8, "tooltip.FavoritesAction"));
            this.putValue("access", HelpUtilities.getString((Locale)var4_8, "access.FavoritesAction"));
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (this.favoritesActionListener != null) {
            this.favoritesActionListener.actionPerformed(actionEvent);
        }
    }
}

