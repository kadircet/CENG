/*
 * Decompiled with CFR 0_114.
 */
package javax.help.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;
import java.net.URL;
import java.util.Locale;
import javax.help.Map;
import javax.help.TOCItem;
import javax.help.TOCView;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class BasicTOCCellRenderer
extends DefaultTreeCellRenderer {
    protected Map map;
    protected TOCView view;

    public BasicTOCCellRenderer(Map map) {
        this(map, null);
    }

    public BasicTOCCellRenderer(Map map, TOCView tOCView) {
        this.map = map;
        this.view = tOCView;
    }

    public Component getTreeCellRendererComponent(JTree jTree, Object object, boolean bl, boolean bl2, boolean bl3, int n, boolean bl4) {
        Serializable serializable;
        String string = "";
        try {
            this.hasFocus = bl4;
        }
        catch (IllegalAccessError var9_9) {
            // empty catch block
        }
        TOCItem tOCItem = (TOCItem)((DefaultMutableTreeNode)object).getUserObject();
        if (tOCItem != null) {
            string = tOCItem.getName();
        }
        this.setText(string);
        if (bl) {
            this.setForeground(this.getTextSelectionColor());
        } else {
            this.setForeground(this.getTextNonSelectionColor());
        }
        ImageIcon imageIcon = null;
        if (tOCItem != null && (serializable = tOCItem.getImageID()) != null) {
            try {
                URL uRL = this.map.getURLFromID((Map.ID)serializable);
                imageIcon = new ImageIcon(uRL);
            }
            catch (Exception var12_14) {
                // empty catch block
            }
        }
        if (tOCItem != null && (serializable = tOCItem.getLocale()) != null) {
            this.setLocale((Locale)serializable);
        }
        if (imageIcon != null) {
            this.setIcon(imageIcon);
        } else if (bl3) {
            this.setIcon(this.getLeafIcon());
        } else if (bl2) {
            this.setIcon(this.getOpenIcon());
        } else {
            this.setIcon(this.getClosedIcon());
        }
        this.selected = bl;
        return this;
    }

    public Icon getLeafIcon() {
        Map.ID iD;
        ImageIcon imageIcon = null;
        if (this.view != null && (iD = this.view.getTopicImageID()) != null) {
            try {
                URL uRL = this.map.getURLFromID(iD);
                imageIcon = new ImageIcon(uRL);
                return imageIcon;
            }
            catch (Exception var3_4) {
                // empty catch block
            }
        }
        return super.getLeafIcon();
    }

    public Icon getOpenIcon() {
        Map.ID iD;
        ImageIcon imageIcon = null;
        if (this.view != null && (iD = this.view.getCategoryOpenImageID()) != null) {
            try {
                URL uRL = this.map.getURLFromID(iD);
                imageIcon = new ImageIcon(uRL);
                return imageIcon;
            }
            catch (Exception var3_4) {
                // empty catch block
            }
        }
        return super.getOpenIcon();
    }

    public Icon getClosedIcon() {
        Map.ID iD;
        ImageIcon imageIcon = null;
        if (this.view != null && (iD = this.view.getCategoryClosedImageID()) != null) {
            try {
                URL uRL = this.map.getURLFromID(iD);
                imageIcon = new ImageIcon(uRL);
                return imageIcon;
            }
            catch (Exception var3_4) {
                // empty catch block
            }
        }
        return super.getClosedIcon();
    }
}

