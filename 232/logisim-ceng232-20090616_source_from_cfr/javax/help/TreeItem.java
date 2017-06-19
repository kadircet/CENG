/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Locale;
import javax.help.HelpSet;
import javax.help.Map;

public class TreeItem
implements Serializable {
    public static final int DEFAULT_EXPANSION = -1;
    public static final int COLLAPSE = 0;
    public static final int EXPAND = 1;
    private String name;
    private Map.ID id;
    protected Locale locale;
    private String mergeType;
    private int expand = -1;
    private String presentation;
    private String presentationName;
    private HelpSet hs;

    public TreeItem(Map.ID iD, HelpSet helpSet, Locale locale) {
        this.id = iD;
        this.hs = helpSet;
        this.locale = locale;
    }

    public TreeItem(Map.ID iD, Locale locale) {
        this(iD, null, locale);
    }

    public TreeItem(String string) {
        this(null, null, null);
        this.setName(string);
    }

    public TreeItem() {
        this(null, null);
    }

    public void setName(String string) {
        this.name = string;
    }

    public String getName() {
        return this.name;
    }

    public void setID(Map.ID iD) {
        this.id = iD;
    }

    public Map.ID getID() {
        return this.id;
    }

    public URL getURL() {
        try {
            return this.id.getURL();
        }
        catch (Exception var1_1) {
            return null;
        }
    }

    public void setHelpSet(HelpSet helpSet) {
        this.hs = helpSet;
    }

    public HelpSet getHelpSet() {
        return this.hs;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setMergeType(String string) {
        this.mergeType = string;
    }

    public String getMergeType() {
        return this.mergeType;
    }

    public void setExpansionType(int n) {
        if (n < -1 || n > 1) {
            throw new IllegalArgumentException("Invalid expansion type");
        }
        this.expand = n;
    }

    public int getExpansionType() {
        return this.expand;
    }

    public void setPresentation(String string) {
        this.presentation = string;
    }

    public String getPresentation() {
        return this.presentation;
    }

    public void setPresentationName(String string) {
        this.presentationName = string;
    }

    public String getPresentationName() {
        return this.presentationName;
    }

    public String toString() {
        return this.id + "(" + this.name + ")";
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
    }
}

