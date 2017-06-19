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

public class AppendMerge
extends Merge {
    private static boolean debug = false;

    public AppendMerge(NavigatorView navigatorView, NavigatorView navigatorView2) {
        super(navigatorView, navigatorView2);
    }

    public TreeNode processMerge(TreeNode treeNode) {
        AppendMerge.debug("start merge");
        AppendMerge.mergeNodes(treeNode, this.slaveTopNode);
        return treeNode;
    }

    public static void mergeNodes(TreeNode treeNode, TreeNode treeNode2) {
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treeNode;
        DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)treeNode2;
        AppendMerge.debug("mergeNodes master=" + MergeHelpUtilities.getNodeName(defaultMutableTreeNode) + " slave=" + MergeHelpUtilities.getNodeName(defaultMutableTreeNode2));
        while (defaultMutableTreeNode2.getChildCount() > 0) {
            DefaultMutableTreeNode defaultMutableTreeNode3 = (DefaultMutableTreeNode)defaultMutableTreeNode2.getFirstChild();
            defaultMutableTreeNode.add(defaultMutableTreeNode3);
            MergeHelpUtilities.mergeNodeChildren("javax.help.AppendMerge", defaultMutableTreeNode3);
        }
    }

    public static void mergeNodeChildren(TreeNode treeNode) {
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treeNode;
        AppendMerge.debug("mergeNodes master=" + MergeHelpUtilities.getNodeName(defaultMutableTreeNode));
        int n = 0;
        while (n < defaultMutableTreeNode.getChildCount()) {
            DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)defaultMutableTreeNode.getChildAt(n);
            if (!defaultMutableTreeNode2.isLeaf()) {
                MergeHelpUtilities.mergeNodeChildren("javax.help.AppendMerge", defaultMutableTreeNode2);
            }
            ++n;
        }
    }

    private static void debug(String string) {
        if (debug) {
            System.out.println("AppendMerge :" + string);
        }
    }
}

