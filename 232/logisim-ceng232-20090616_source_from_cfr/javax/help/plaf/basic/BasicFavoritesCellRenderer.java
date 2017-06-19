/*
 * Decompiled with CFR 0_114.
 */
package javax.help.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.util.Locale;
import javax.help.FavoritesItem;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class BasicFavoritesCellRenderer
extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree jTree, Object object, boolean bl, boolean bl2, boolean bl3, int n, boolean bl4) {
        Locale locale;
        Object object2 = ((DefaultMutableTreeNode)object).getUserObject();
        String string = "";
        FavoritesItem favoritesItem = (FavoritesItem)object2;
        if (favoritesItem != null) {
            string = favoritesItem.getName();
        }
        if (favoritesItem != null && (locale = favoritesItem.getLocale()) != null) {
            this.setLocale(locale);
        }
        this.setText(string);
        if (bl) {
            this.setForeground(this.getTextSelectionColor());
        } else {
            this.setForeground(this.getTextNonSelectionColor());
        }
        this.selected = bl;
        if (bl3) {
            this.setIcon(this.getDefaultLeafIcon());
        } else if (bl2) {
            this.setIcon(this.getDefaultOpenIcon());
        } else {
            this.setIcon(this.getDefaultClosedIcon());
        }
        return this;
    }
}

