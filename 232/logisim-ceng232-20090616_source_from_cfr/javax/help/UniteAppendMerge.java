/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.io.PrintStream;
import javax.help.Merge;
import javax.help.MergeHelpUtilities;
import javax.help.NavigatorView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public class UniteAppendMerge
extends Merge {
    private static boolean debug = false;

    public UniteAppendMerge(NavigatorView navigatorView, NavigatorView navigatorView2) {
        super(navigatorView, navigatorView2);
    }

    public TreeNode processMerge(TreeNode treeNode) {
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treeNode;
        if (defaultMutableTreeNode.equals(this.slaveTopNode)) {
            return defaultMutableTreeNode;
        }
        if (this.slaveTopNode.getChildCount() == 0) {
            return defaultMutableTreeNode;
        }
        UniteAppendMerge.mergeNodes(defaultMutableTreeNode, this.slaveTopNode);
        return defaultMutableTreeNode;
    }

    public static void mergeNodes(TreeNode treeNode, TreeNode treeNode2) {
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treeNode;
        DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)treeNode2;
        UniteAppendMerge.debug("mergeNodes master=" + MergeHelpUtilities.getNodeName(defaultMutableTreeNode) + " slave=" + MergeHelpUtilities.getNodeName(defaultMutableTreeNode2));
        int n = defaultMutableTreeNode.getChildCount();
        while (defaultMutableTreeNode2.getChildCount() > 0) {
            DefaultMutableTreeNode defaultMutableTreeNode3 = (DefaultMutableTreeNode)defaultMutableTreeNode2.getFirstChild();
            int n2 = 0;
            while (n2 < n) {
                DefaultMutableTreeNode defaultMutableTreeNode4 = (DefaultMutableTreeNode)defaultMutableTreeNode.getChildAt(n2);
                if (MergeHelpUtilities.compareNames(defaultMutableTreeNode4, defaultMutableTreeNode3) == 0) {
                    if (MergeHelpUtilities.haveEqualID(defaultMutableTreeNode4, defaultMutableTreeNode3)) {
                        MergeHelpUtilities.mergeNodes("javax.help.UniteAppendMerge", defaultMutableTreeNode4, defaultMutableTreeNode3);
                        defaultMutableTreeNode3.removeFromParent();
                        defaultMutableTreeNode3 = null;
                        break;
                    }
                    MergeHelpUtilities.markNodes(defaultMutableTreeNode4, defaultMutableTreeNode3);
                    defaultMutableTreeNode.add(defaultMutableTreeNode3);
                    MergeHelpUtilities.mergeNodeChildren("javax.help.UniteAppendMerge", defaultMutableTreeNode3);
                    defaultMutableTreeNode3 = null;
                    break;
                }
                ++n2;
            }
            if (defaultMutableTreeNode3 == null) continue;
            defaultMutableTreeNode.add(defaultMutableTreeNode3);
            MergeHelpUtilities.mergeNodeChildren("javax.help.UniteAppendMerge", defaultMutableTreeNode3);
        }
        defaultMutableTreeNode2.removeFromParent();
        defaultMutableTreeNode2 = null;
    }

    public static void mergeNodeChildren(TreeNode treeNode) {
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treeNode;
        UniteAppendMerge.debug("mergeNodes master=" + MergeHelpUtilities.getNodeName(defaultMutableTreeNode));
        int n = 0;
        while (n < defaultMutableTreeNode.getChildCount()) {
            DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)defaultMutableTreeNode.getChildAt(n);
            if (!defaultMutableTreeNode2.isLeaf()) {
                MergeHelpUtilities.mergeNodeChildren("javax.help.UniteAppendMerge", defaultMutableTreeNode2);
            }
            ++n;
        }
    }

    private static void debug(String string) {
        if (debug) {
            System.out.println("UniteAppendMerge :" + string);
        }
    }
}

