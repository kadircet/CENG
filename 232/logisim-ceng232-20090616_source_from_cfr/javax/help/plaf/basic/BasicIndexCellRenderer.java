/*
 * Decompiled with CFR 0_114.
 */
package javax.help.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.util.Locale;
import javax.help.IndexItem;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class BasicIndexCellRenderer
extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree jTree, Object object, boolean bl, boolean bl2, boolean bl3, int n, boolean bl4) {
        Locale locale;
        try {
            this.hasFocus = bl4;
        }
        catch (IllegalAccessError var8_8) {
            // empty catch block
        }
        IndexItem indexItem = (IndexItem)((DefaultMutableTreeNode)object).getUserObject();
        String string = "";
        if (indexItem != null) {
            string = indexItem.getName();
        }
        this.setText(string);
        if (bl) {
            this.setForeground(this.getTextSelectionColor());
        } else {
            this.setForeground(this.getTextNonSelectionColor());
        }
        this.setIcon(null);
        this.selected = bl;
        if (indexItem != null && (locale = indexItem.getLocale()) != null) {
            this.setLocale(locale);
        }
        return this;
    }
}

