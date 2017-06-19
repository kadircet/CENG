/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Vector;
import javax.help.TreeItem;

public class FavoritesItem
extends TreeItem
implements Transferable,
Serializable {
    private boolean isFolder = false;
    public static final DataFlavor FAVORITES_FLAVOR;
    static DataFlavor[] flavors;
    private FavoritesItem parent = null;
    private Vector children = new Vector();
    private String url = null;
    private String target = null;
    private String title = null;
    private boolean emptyInitState = true;
    private boolean visible = true;
    static /* synthetic */ Class class$javax$help$FavoritesItem;

    public FavoritesItem(String string) {
        this.setName(string);
    }

    public FavoritesItem() {
        this(null);
    }

    public FavoritesItem(String string, String string2, String string3, String string4, Locale locale) {
        this(string);
        this.target = string2;
        this.url = string3;
        this.locale = locale;
        this.title = string4;
    }

    public void setVisible(boolean bl) {
        this.visible = bl;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public String getTarget() {
        return this.target;
    }

    public String getURLSpec() {
        return this.url;
    }

    public URL getURL() {
        try {
            return new URL(this.url);
        }
        catch (MalformedURLException var1_1) {
            return null;
        }
    }

    public String getHelpSetTitle() {
        return this.title;
    }

    public void setAsFolder() {
        this.isFolder = true;
    }

    public boolean allowsChildren() {
        return this.isFolder();
    }

    public boolean isLeaf() {
        return !this.isFolder();
    }

    public boolean isFolder() {
        return this.isFolder;
    }

    public void add(FavoritesItem favoritesItem) {
        favoritesItem.setParent(this);
        this.children.add(favoritesItem);
        this.emptyInitState = false;
        this.isFolder = true;
    }

    public boolean emptyInitState() {
        return this.emptyInitState;
    }

    public void remove(FavoritesItem favoritesItem) {
        favoritesItem.setParent(null);
        this.children.remove(favoritesItem);
    }

    public FavoritesItem getParent() {
        return this.parent;
    }

    public void setParent(FavoritesItem favoritesItem) {
        this.parent = favoritesItem;
    }

    public Vector getChildren() {
        return this.children;
    }

    public Object clone() {
        FavoritesItem favoritesItem = new FavoritesItem(this.getName(), this.target, this.url, this.title, this.locale);
        return favoritesItem;
    }

    public String toString() {
        return this.getName();
    }

    public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
        if (dataFlavor.equals(FAVORITES_FLAVOR)) {
            return this;
        }
        throw new UnsupportedFlavorException(dataFlavor);
    }

    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
        return dataFlavor.equals(FAVORITES_FLAVOR);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException var1_1) {
            throw new NoClassDefFoundError(var1_1.getMessage());
        }
    }

    static {
        Class class_ = class$javax$help$FavoritesItem == null ? (FavoritesItem.class$javax$help$FavoritesItem = FavoritesItem.class$("javax.help.FavoritesItem")) : class$javax$help$FavoritesItem;
        FAVORITES_FLAVOR = new DataFlavor(class_, "Favorites Item");
        flavors = new DataFlavor[]{FAVORITES_FLAVOR};
    }
}

