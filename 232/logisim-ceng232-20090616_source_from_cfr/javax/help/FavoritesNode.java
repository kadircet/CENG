/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Vector;
import javax.help.FavoritesItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public class FavoritesNode
extends DefaultMutableTreeNode {
    public static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE favorites\n PUBLIC \"-//Sun Microsystems Inc.//DTD JavaHelp Favorites Version 2.0//EN\"\n        \"http://java.sun.com/products/javahelp/favorites_2_0.dtd\">\n\n<favorites version=\"2.0\">\n";
    public static final String ELEMENT = "favoriteitem";
    public static final String FOOTER = "</favorites>";
    private FavoritesItem item;
    private static final boolean debug = false;

    public FavoritesNode(FavoritesItem favoritesItem) {
        super(favoritesItem);
        this.item = favoritesItem;
    }

    public boolean getAllowsChildren() {
        return ((FavoritesItem)this.getUserObject()).isFolder();
    }

    public void add(DefaultMutableTreeNode defaultMutableTreeNode) {
        super.add(defaultMutableTreeNode);
        FavoritesItem favoritesItem = (FavoritesItem)defaultMutableTreeNode.getUserObject();
        FavoritesItem favoritesItem2 = favoritesItem.getParent();
        FavoritesItem favoritesItem3 = (FavoritesItem)this.getUserObject();
        favoritesItem3.add(favoritesItem);
    }

    public void remove(DefaultMutableTreeNode defaultMutableTreeNode) {
        super.remove(defaultMutableTreeNode);
        FavoritesItem favoritesItem = (FavoritesItem)((FavoritesNode)defaultMutableTreeNode).getUserObject();
        FavoritesItem favoritesItem2 = (FavoritesItem)this.getUserObject();
        if (this.parent != null) {
            favoritesItem2.remove(favoritesItem);
        }
    }

    public int getVisibleChildCount() {
        int n = 0;
        if (this.item == null) {
            return 0;
        }
        Enumeration enumeration = this.item.getChildren().elements();
        while (enumeration.hasMoreElements()) {
            FavoritesItem favoritesItem = (FavoritesItem)enumeration.nextElement();
            if (!favoritesItem.isVisible()) continue;
            ++n;
        }
        return n;
    }

    public String getOffset() {
        String string = null;
        String string2 = null;
        FavoritesNode favoritesNode = (FavoritesNode)this.getParent();
        if (favoritesNode != null) {
            string = favoritesNode.getOffset();
            string2 = string + "  ";
        } else {
            string2 = "  ";
        }
        return string2;
    }

    public void export(OutputStream outputStream) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        outputStreamWriter = this.exportHeader(outputStream);
        Enumeration enumeration = this.children();
        if (!enumeration.equals(DefaultMutableTreeNode.EMPTY_ENUMERATION)) {
            while (enumeration.hasMoreElements()) {
                FavoritesNode favoritesNode = (FavoritesNode)enumeration.nextElement();
                favoritesNode.exportNode(outputStreamWriter);
            }
        }
        outputStreamWriter.write("</favorites>");
        outputStreamWriter.close();
    }

    public void exportNode(OutputStreamWriter outputStreamWriter) throws IOException {
        String string;
        Enumeration enumeration;
        String string2;
        TreeNode treeNode = this.getParent();
        FavoritesItem favoritesItem = (FavoritesItem)this.getUserObject();
        outputStreamWriter.write(this.getOffset() + "<" + this.getXMLElement() + " text=\"" + favoritesItem.getName() + "\" ");
        String string3 = favoritesItem.getTarget();
        if (string3 != null) {
            outputStreamWriter.write("target=\"" + string3 + "\" ");
        }
        if ((string2 = favoritesItem.getURLSpec()) != null) {
            outputStreamWriter.write("url=\"" + string2 + "\"");
        }
        if ((string = favoritesItem.getHelpSetTitle()) != null) {
            outputStreamWriter.write(" hstitle=\"" + string + "\"");
        }
        if ((enumeration = this.children()).equals(DefaultMutableTreeNode.EMPTY_ENUMERATION)) {
            outputStreamWriter.write("/>\n");
        } else {
            outputStreamWriter.write(">\n");
            Enumeration enumeration2 = this.children.elements();
            while (enumeration2.hasMoreElements()) {
                FavoritesNode favoritesNode = (FavoritesNode)enumeration2.nextElement();
                FavoritesNode.debug("offspring: " + favoritesNode);
                favoritesNode.exportNode(outputStreamWriter);
            }
            outputStreamWriter.write(this.getOffset() + "</" + "favoriteitem" + ">\n");
        }
    }

    public OutputStreamWriter exportHeader(OutputStream outputStream) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
        outputStreamWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE favorites\n PUBLIC \"-//Sun Microsystems Inc.//DTD JavaHelp Favorites Version 2.0//EN\"\n        \"http://java.sun.com/products/javahelp/favorites_2_0.dtd\">\n\n<favorites version=\"2.0\">\n");
        return outputStreamWriter;
    }

    public String getXMLHeader() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE favorites\n PUBLIC \"-//Sun Microsystems Inc.//DTD JavaHelp Favorites Version 2.0//EN\"\n        \"http://java.sun.com/products/javahelp/favorites_2_0.dtd\">\n\n<favorites version=\"2.0\">\n";
    }

    public String getXMLElement() {
        return "favoriteitem";
    }

    public FavoritesNode getDeepCopy() {
        return new FavoritesNode((FavoritesItem)this.item.clone());
    }

    public boolean isVisible() {
        return this.item.isVisible();
    }

    public void setVisible(boolean bl) {
        this.item.setVisible(bl);
    }

    private static void debug(String string) {
    }
}

