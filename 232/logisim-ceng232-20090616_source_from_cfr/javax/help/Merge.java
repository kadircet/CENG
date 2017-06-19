/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.util.Locale;
import javax.help.AppendMerge;
import javax.help.HelpSet;
import javax.help.IndexView;
import javax.help.NavigatorView;
import javax.help.TOCView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public abstract class Merge {
    protected DefaultMutableTreeNode slaveTopNode;
    protected Locale locale;
    static /* synthetic */ Class class$javax$help$NavigatorView;

    protected Merge(NavigatorView navigatorView, NavigatorView navigatorView2) {
        try {
            Class class_ = Class.forName("javax.help.TOCView");
            if (class_.isInstance(navigatorView2)) {
                this.slaveTopNode = ((TOCView)navigatorView2).getDataAsTree();
            }
            if ((class_ = Class.forName("javax.help.IndexView")).isInstance(navigatorView2)) {
                this.slaveTopNode = ((IndexView)navigatorView2).getDataAsTree();
            }
        }
        catch (ClassNotFoundException var3_4) {
            System.err.println(var3_4);
        }
        this.locale = navigatorView.getHelpSet().getLocale();
        if (this.locale == null) {
            this.locale = Locale.getDefault();
        }
    }

    public abstract TreeNode processMerge(TreeNode var1);

    public static void mergeNodes(TreeNode treeNode, TreeNode treeNode2) {
    }

    public static void mergeNodeChildren(TreeNode treeNode) {
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException var1_1) {
            throw new NoClassDefFoundError(var1_1.getMessage());
        }
    }

    public static class DefaultMergeFactory {
        public static Merge getMerge(NavigatorView navigatorView, NavigatorView navigatorView2) {
            Merge merge = null;
            if (navigatorView == null || navigatorView2 == null) {
                throw new NullPointerException("masterView and/or slaveView are null");
            }
            String string = navigatorView.getMergeType();
            HelpSet helpSet = navigatorView.getHelpSet();
            Locale locale = helpSet.getLocale();
            ClassLoader classLoader = helpSet.getLoader();
            if (string != null) {
                try {
                    Class[] arrclass = new Class[2];
                    Class class_ = Merge.class$javax$help$NavigatorView == null ? (Merge.class$javax$help$NavigatorView = Merge.class$("javax.help.NavigatorView")) : Merge.class$javax$help$NavigatorView;
                    arrclass[0] = class_;
                    arrclass[1] = Merge.class$javax$help$NavigatorView == null ? (Merge.class$javax$help$NavigatorView = Merge.class$("javax.help.NavigatorView")) : Merge.class$javax$help$NavigatorView;
                    Class[] arrclass2 = arrclass;
                    Object[] arrobject = new Object[]{navigatorView, navigatorView2};
                    Class class_2 = classLoader == null ? Class.forName(string) : classLoader.loadClass(string);
                    Constructor constructor = class_2.getConstructor(arrclass2);
                    merge = (Merge)constructor.newInstance(arrobject);
                }
                catch (Exception var9_8) {
                    var9_8.printStackTrace();
                    throw new RuntimeException("Could not create Merge type " + string);
                }
            } else {
                merge = new AppendMerge(navigatorView, navigatorView2);
            }
            return merge;
        }
    }

}

