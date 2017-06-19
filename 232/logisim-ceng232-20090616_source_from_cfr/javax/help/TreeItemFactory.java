/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import javax.help.HelpSet;
import javax.help.TreeItem;
import javax.swing.tree.DefaultMutableTreeNode;

public interface TreeItemFactory {
    public void parsingStarted(URL var1);

    public void processDOCTYPE(String var1, String var2, String var3);

    public void processPI(HelpSet var1, String var2, String var3);

    public TreeItem createItem(String var1, Hashtable var2, HelpSet var3, Locale var4);

    public TreeItem createItem();

    public void reportMessage(String var1, boolean var2);

    public Enumeration listMessages();

    public DefaultMutableTreeNode parsingEnded(DefaultMutableTreeNode var1);
}

