/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.io.PrintStream;
import java.text.CollationKey;
import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;
import javax.help.Merge;
import javax.help.MergeHelpUtilities;
import javax.help.NavigatorView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public class SortMerge
extends Merge {
    private static boolean debug = false;

    public SortMerge(NavigatorView navigatorView, NavigatorView navigatorView2) {
        super(navigatorView, navigatorView2);
    }

    public TreeNode processMerge(TreeNode treeNode) {
        SortMerge.debug("processMerge started");
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treeNode;
        if (defaultMutableTreeNode.equals(this.slaveTopNode)) {
            return defaultMutableTreeNode;
        }
        if (this.slaveTopNode.getChildCount() == 0) {
            return defaultMutableTreeNode;
        }
        if (defaultMutableTreeNode.getChildCount() == 0) {
            MergeHelpUtilities.mergeNodeChildren("javax.help.SortMerge", this.slaveTopNode);
            while (this.slaveTopNode.getChildCount() > 0) {
                defaultMutableTreeNode.add((DefaultMutableTreeNode)this.slaveTopNode.getFirstChild());
            }
            return defaultMutableTreeNode;
        }
        SortMerge.mergeNodes(defaultMutableTreeNode, this.slaveTopNode);
        SortMerge.debug("process merge ended");
        return defaultMutableTreeNode;
    }

    public static void mergeNodes(TreeNode treeNode, TreeNode treeNode2) {
        SortMerge.debug("mergeNodes started");
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treeNode;
        DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)treeNode2;
        SortMerge.sortNode(defaultMutableTreeNode2, MergeHelpUtilities.getLocale(defaultMutableTreeNode2));
        int n = defaultMutableTreeNode.getChildCount();
        int n2 = 0;
        DefaultMutableTreeNode defaultMutableTreeNode3 = null;
        if (n > 0) {
            defaultMutableTreeNode3 = (DefaultMutableTreeNode)defaultMutableTreeNode.getChildAt(n2);
        }
        DefaultMutableTreeNode defaultMutableTreeNode4 = null;
        while (defaultMutableTreeNode2.getChildCount() > 0 && defaultMutableTreeNode3 != null) {
            defaultMutableTreeNode4 = (DefaultMutableTreeNode)defaultMutableTreeNode2.getFirstChild();
            int n3 = MergeHelpUtilities.compareNames(defaultMutableTreeNode3, defaultMutableTreeNode4);
            if (n3 < 0) {
                if (++n2 >= n) break;
                defaultMutableTreeNode3 = (DefaultMutableTreeNode)defaultMutableTreeNode.getChildAt(n2);
                continue;
            }
            if (n3 > 0) {
                defaultMutableTreeNode.add(defaultMutableTreeNode4);
                MergeHelpUtilities.mergeNodeChildren("javax.help.SortMerge", defaultMutableTreeNode4);
                continue;
            }
            if (MergeHelpUtilities.haveEqualID(defaultMutableTreeNode3, defaultMutableTreeNode4)) {
                MergeHelpUtilities.mergeNodes("javax.help.SortMerge", defaultMutableTreeNode3, defaultMutableTreeNode4);
                defaultMutableTreeNode4.removeFromParent();
                defaultMutableTreeNode4 = null;
                continue;
            }
            MergeHelpUtilities.markNodes(defaultMutableTreeNode3, defaultMutableTreeNode4);
            defaultMutableTreeNode.add(defaultMutableTreeNode4);
            MergeHelpUtilities.mergeNodeChildren("javax.help.SortMerge", defaultMutableTreeNode4);
        }
        while (defaultMutableTreeNode2.getChildCount() > 0) {
            defaultMutableTreeNode4 = (DefaultMutableTreeNode)defaultMutableTreeNode2.getFirstChild();
            defaultMutableTreeNode.add(defaultMutableTreeNode4);
            MergeHelpUtilities.mergeNodeChildren("javax.help.SortMerge", defaultMutableTreeNode4);
        }
        SortMerge.mergeNodeChildren(defaultMutableTreeNode);
        SortMerge.debug("mergeNode ended");
    }

    public static void mergeNodeChildren(TreeNode treeNode) {
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treeNode;
        SortMerge.debug("mergeNodeChildren master=" + MergeHelpUtilities.getNodeName(defaultMutableTreeNode));
        SortMerge.sortNode(defaultMutableTreeNode, MergeHelpUtilities.getLocale(defaultMutableTreeNode));
        int n = 0;
        while (n < defaultMutableTreeNode.getChildCount()) {
            DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)defaultMutableTreeNode.getChildAt(n);
            if (!defaultMutableTreeNode2.isLeaf()) {
                MergeHelpUtilities.mergeNodeChildren("javax.help.SortMerge", defaultMutableTreeNode2);
            }
            ++n;
        }
    }

    public static void sortNode(DefaultMutableTreeNode defaultMutableTreeNode, Locale locale) {
        SortMerge.debug("sortNode");
        if (locale == null) {
            locale = Locale.getDefault();
        }
        int n = defaultMutableTreeNode.getChildCount();
        DefaultMutableTreeNode defaultMutableTreeNode2 = new DefaultMutableTreeNode();
        Collator collator = Collator.getInstance(locale);
        Object[] arrobject = new CollationKey[n];
        int n2 = 0;
        while (n2 < n) {
            String string = MergeHelpUtilities.getNodeName((DefaultMutableTreeNode)defaultMutableTreeNode.getChildAt(n2));
            SortMerge.debug("String , i:" + string + " , " + n2);
            arrobject[n2] = collator.getCollationKey(string);
            ++n2;
        }
        Arrays.sort(arrobject);
        int n3 = 0;
        while (n3 < n) {
            DefaultMutableTreeNode defaultMutableTreeNode3 = MergeHelpUtilities.getChildWithName(defaultMutableTreeNode, arrobject[n3].getSourceString());
            if (defaultMutableTreeNode3 != null) {
                defaultMutableTreeNode2.add(defaultMutableTreeNode3);
            }
            ++n3;
        }
        while (defaultMutableTreeNode2.getChildCount() > 0) {
            defaultMutableTreeNode.add((DefaultMutableTreeNode)defaultMutableTreeNode2.getFirstChild());
        }
        SortMerge.debug("end sortNode");
    }

    private static void debug(String string) {
        if (debug) {
            System.out.println("SortMerge :" + string);
        }
    }
}

