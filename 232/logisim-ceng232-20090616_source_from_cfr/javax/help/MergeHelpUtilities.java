/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.Locale;
import javax.help.HelpSet;
import javax.help.Map;
import javax.help.TreeItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class MergeHelpUtilities {
    private static boolean debug = false;
    static /* synthetic */ Class class$javax$swing$tree$TreeNode;

    public static void mergeNodes(String string, DefaultMutableTreeNode defaultMutableTreeNode, DefaultMutableTreeNode defaultMutableTreeNode2) {
        if (defaultMutableTreeNode2.isLeaf()) {
            return;
        }
        String string2 = MergeHelpUtilities.getMergeType(defaultMutableTreeNode);
        if (string2 == null) {
            string2 = string;
        }
        Class[] arrclass = new Class[2];
        Class class_ = class$javax$swing$tree$TreeNode == null ? (MergeHelpUtilities.class$javax$swing$tree$TreeNode = MergeHelpUtilities.class$("javax.swing.tree.TreeNode")) : class$javax$swing$tree$TreeNode;
        arrclass[0] = class_;
        arrclass[1] = class$javax$swing$tree$TreeNode == null ? (MergeHelpUtilities.class$javax$swing$tree$TreeNode = MergeHelpUtilities.class$("javax.swing.tree.TreeNode")) : class$javax$swing$tree$TreeNode;
        Class[] arrclass2 = arrclass;
        Object[] arrobject = new Object[]{defaultMutableTreeNode, defaultMutableTreeNode2};
        try {
            Class class_2 = Class.forName(string2);
            Method method = class_2.getDeclaredMethod("mergeNodes", arrclass2);
            method.invoke(null, arrobject);
        }
        catch (Exception var8_8) {
            var8_8.printStackTrace();
            throw new RuntimeException("Could not find or execute mergeNodes for " + string2);
        }
    }

    public static void mergeNodeChildren(String string, DefaultMutableTreeNode defaultMutableTreeNode) {
        if (defaultMutableTreeNode.isLeaf()) {
            return;
        }
        String string2 = MergeHelpUtilities.getMergeType(defaultMutableTreeNode);
        if (string2 == null) {
            string2 = string;
        }
        Class[] arrclass = new Class[1];
        Class class_ = class$javax$swing$tree$TreeNode == null ? (MergeHelpUtilities.class$javax$swing$tree$TreeNode = MergeHelpUtilities.class$("javax.swing.tree.TreeNode")) : class$javax$swing$tree$TreeNode;
        arrclass[0] = class_;
        Class[] arrclass2 = arrclass;
        Object[] arrobject = new Object[]{defaultMutableTreeNode};
        try {
            Class class_2 = Class.forName(string2);
            Method method = class_2.getDeclaredMethod("mergeNodeChildren", arrclass2);
            method.invoke(null, arrobject);
        }
        catch (Exception var7_7) {
            var7_7.printStackTrace();
            throw new RuntimeException("Could not find or execute mergeNodeChildren for " + string2);
        }
    }

    private static String getMergeType(DefaultMutableTreeNode defaultMutableTreeNode) {
        TreeItem treeItem = (TreeItem)defaultMutableTreeNode.getUserObject();
        if (treeItem == null) {
            return null;
        }
        return treeItem.getMergeType();
    }

    public static String getNodeName(DefaultMutableTreeNode defaultMutableTreeNode) {
        if (defaultMutableTreeNode == null) {
            return null;
        }
        TreeItem treeItem = (TreeItem)defaultMutableTreeNode.getUserObject();
        if (treeItem == null) {
            return null;
        }
        return treeItem.getName();
    }

    public static DefaultMutableTreeNode getChildWithName(DefaultMutableTreeNode defaultMutableTreeNode, String string) {
        DefaultMutableTreeNode defaultMutableTreeNode2 = null;
        DefaultMutableTreeNode defaultMutableTreeNode3 = null;
        int n = 0;
        while (n < defaultMutableTreeNode.getChildCount()) {
            defaultMutableTreeNode2 = (DefaultMutableTreeNode)defaultMutableTreeNode.getChildAt(n);
            if (MergeHelpUtilities.getNodeName(defaultMutableTreeNode2).equals(string)) {
                defaultMutableTreeNode3 = defaultMutableTreeNode2;
                break;
            }
            ++n;
        }
        return defaultMutableTreeNode3;
    }

    public static Locale getLocale(DefaultMutableTreeNode defaultMutableTreeNode) {
        Locale locale = null;
        TreeItem treeItem = (TreeItem)defaultMutableTreeNode.getUserObject();
        if (treeItem != null) {
            locale = treeItem.getLocale();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }

    public static int compareNames(DefaultMutableTreeNode defaultMutableTreeNode, DefaultMutableTreeNode defaultMutableTreeNode2) {
        String string;
        TreeItem treeItem = (TreeItem)defaultMutableTreeNode.getUserObject();
        MergeHelpUtilities.debug("haveEqualName - master:" + treeItem);
        TreeItem treeItem2 = (TreeItem)defaultMutableTreeNode2.getUserObject();
        MergeHelpUtilities.debug("haveEqualName - slave:" + treeItem2);
        String string2 = treeItem.getName();
        if (string2 == null) {
            string2 = " ";
        }
        if ((string = treeItem2.getName()) == null) {
            string = " ";
        }
        Collator collator = Collator.getInstance(MergeHelpUtilities.getLocale(defaultMutableTreeNode));
        return collator.compare(string2, string);
    }

    public static boolean haveEqualID(DefaultMutableTreeNode defaultMutableTreeNode, DefaultMutableTreeNode defaultMutableTreeNode2) {
        TreeItem treeItem = (TreeItem)defaultMutableTreeNode.getUserObject();
        TreeItem treeItem2 = (TreeItem)defaultMutableTreeNode2.getUserObject();
        if (treeItem.getID() == null) {
            if (treeItem2.getID() == null) {
                return true;
            }
            return false;
        }
        if (treeItem2.getID() == null) {
            return false;
        }
        Map.ID iD = treeItem.getID();
        Map.ID iD2 = treeItem2.getID();
        if (iD.id == null) {
            if (iD2.id == null) {
                return true;
            }
            return false;
        }
        if (iD2.id == null) {
            return false;
        }
        return iD.id.equals(iD2.id);
    }

    public static void markNodes(DefaultMutableTreeNode defaultMutableTreeNode, DefaultMutableTreeNode defaultMutableTreeNode2) {
        MergeHelpUtilities.debug("MarkNodes");
        TreeItem treeItem = (TreeItem)defaultMutableTreeNode.getUserObject();
        TreeItem treeItem2 = (TreeItem)defaultMutableTreeNode2.getUserObject();
        HelpSet helpSet = treeItem.getHelpSet();
        HelpSet helpSet2 = treeItem2.getHelpSet();
        if (treeItem.getName() != null) {
            treeItem.setName(treeItem.getName() + "(" + helpSet.getTitle() + ")");
        } else {
            treeItem.setName(treeItem.getName() + "(" + helpSet.getTitle() + ")");
        }
        if (treeItem2.getName() != null) {
            treeItem2.setName(treeItem2.getName() + "(" + helpSet2.getTitle() + ")");
        } else {
            treeItem2.setName(treeItem2.getName() + "(" + helpSet2.getTitle() + ")");
        }
    }

    private static void debug(String string) {
        if (debug) {
            System.out.println("MergeHelpUtilities :" + string);
        }
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException var1_1) {
            throw new NoClassDefFoundError(var1_1.getMessage());
        }
    }
}

