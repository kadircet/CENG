/*
 * Decompiled with CFR 0_114.
 */
package javax.help.plaf;

import java.net.URL;
import javax.help.HelpSet;
import javax.help.Map;
import javax.help.NavigatorView;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.plaf.ComponentUI;

public abstract class HelpNavigatorUI
extends ComponentUI {
    private Icon icon;

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public void merge(NavigatorView navigatorView) {
        throw new UnsupportedOperationException("merge is not supported");
    }

    public void remove(NavigatorView navigatorView) {
        throw new UnsupportedOperationException("remove is not supported");
    }

    public ImageIcon getImageIcon(NavigatorView navigatorView) {
        ImageIcon imageIcon = null;
        Map.ID iD = navigatorView.getImageID();
        if (iD != null) {
            try {
                Map map = navigatorView.getHelpSet().getCombinedMap();
                URL uRL = map.getURLFromID(iD);
                imageIcon = new ImageIcon(uRL);
            }
            catch (Exception var4_5) {
                // empty catch block
            }
        }
        return imageIcon;
    }

    public Action getAddAction() {
        throw new UnsupportedOperationException("getAddAction is not supported");
    }
}

