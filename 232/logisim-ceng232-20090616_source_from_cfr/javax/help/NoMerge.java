/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.io.PrintStream;
import javax.help.Merge;
import javax.help.MergeHelpUtilities;
import javax.help.NavigatorView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class NoMerge
extends Merge {
    private static boolean debug = false;

    public NoMerge(NavigatorView navigatorView, NavigatorView navigatorView2) {
        super(navigatorView, navigatorView2);
    }

    public TreeNode processMerge(TreeNode treeNode) {
        NoMerge.debug("start merge");
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treeNode;
        MergeHelpUtilities.mergeNodeChildren("javax.help.NoMerge", defaultMutableTreeNode);
        return defaultMutableTreeNode;
    }

    public static void mergeNodes(TreeNode treeNode, TreeNode treeNode2) {
    }

    public static void mergeNodeChildren(TreeNode treeNode) {
    }

    private static void debug(String string) {
        if (debug) {
            System.out.println("NoMerge :" + string);
        }
    }
}

