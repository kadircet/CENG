/*
 * Decompiled with CFR 0_114.
 */
package javax.help.plaf.basic;

import com.sun.java.help.impl.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import javax.help.BadIDException;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.InvalidHelpSetContextException;
import javax.help.JHelpNavigator;
import javax.help.JHelpTOCNavigator;
import javax.help.Map;
import javax.help.Merge;
import javax.help.MergeHelpUtilities;
import javax.help.NavigatorView;
import javax.help.Popup;
import javax.help.Presentation;
import javax.help.TOCItem;
import javax.help.TOCView;
import javax.help.TreeItem;
import javax.help.WindowPresentation;
import javax.help.event.HelpModelEvent;
import javax.help.event.HelpModelListener;
import javax.help.plaf.HelpNavigatorUI;
import javax.help.plaf.basic.BasicTOCCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.RepaintManager;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class BasicTOCNavigatorUI
extends HelpNavigatorUI
implements HelpModelListener,
TreeSelectionListener,
PropertyChangeListener,
ComponentListener,
Serializable {
    protected JHelpTOCNavigator toc;
    protected JScrollPane sp;
    protected DefaultMutableTreeNode topNode;
    protected JTree tree;
    private boolean inInstallUI = false;
    private SwingWorker worker = null;
    protected static final boolean debug = false;
    static /* synthetic */ Class class$javax$help$HelpSet;
    static /* synthetic */ Class class$java$lang$String;

    public static ComponentUI createUI(JComponent jComponent) {
        return new BasicTOCNavigatorUI((JHelpTOCNavigator)jComponent);
    }

    public BasicTOCNavigatorUI(JHelpTOCNavigator jHelpTOCNavigator) {
        BasicTOCNavigatorUI.debug(this + " " + "CreateUI - sort of");
        ImageIcon imageIcon = this.getImageIcon(jHelpTOCNavigator.getNavigatorView());
        if (imageIcon != null) {
            this.setIcon(imageIcon);
        } else {
            this.setIcon(UIManager.getIcon("TOCNav.icon"));
        }
    }

    public void installUI(JComponent jComponent) {
        BasicTOCNavigatorUI.debug(this + " " + "installUI");
        this.inInstallUI = true;
        this.toc = (JHelpTOCNavigator)jComponent;
        HelpModel helpModel = this.toc.getModel();
        this.toc.setLayout(new BorderLayout());
        this.toc.addPropertyChangeListener(this);
        this.toc.addComponentListener(this);
        if (helpModel != null) {
            helpModel.addHelpModelListener(this);
        }
        this.topNode = new DefaultMutableTreeNode();
        this.tree = new JTree(this.topNode);
        TreeSelectionModel treeSelectionModel = this.tree.getSelectionModel();
        treeSelectionModel.addTreeSelectionListener(this);
        this.tree.setShowsRootHandles(false);
        this.tree.setRootVisible(false);
        this.setCellRenderer(this.toc.getNavigatorView(), this.tree);
        this.sp = new JScrollPane();
        this.sp.getViewport().add(this.tree);
        this.toc.add("Center", this.sp);
        this.reloadData();
        this.inInstallUI = false;
    }

    protected void setCellRenderer(NavigatorView navigatorView, JTree jTree) {
        Map map = navigatorView.getHelpSet().getCombinedMap();
        jTree.setCellRenderer(new BasicTOCCellRenderer(map, (TOCView)navigatorView));
    }

    public void uninstallUI(JComponent jComponent) {
        BasicTOCNavigatorUI.debug(this + " " + "unistallUI");
        HelpModel helpModel = this.toc.getModel();
        this.toc.removeComponentListener(this);
        this.toc.removePropertyChangeListener(this);
        TreeSelectionModel treeSelectionModel = this.tree.getSelectionModel();
        treeSelectionModel.removeTreeSelectionListener(this);
        this.toc.setLayout(null);
        this.toc.removeAll();
        if (helpModel != null) {
            helpModel.removeHelpModelListener(this);
        }
        this.toc = null;
    }

    public Dimension getPreferredSize(JComponent jComponent) {
        return new Dimension(200, 100);
    }

    public Dimension getMinimumSize(JComponent jComponent) {
        return new Dimension(100, 100);
    }

    public Dimension getMaximumSize(JComponent jComponent) {
        return new Dimension(32767, 32767);
    }

    private void reloadData() {
        BasicTOCNavigatorUI.debug("reloadData");
        TOCView tOCView = (TOCView)this.toc.getNavigatorView();
        if (this.worker != null) {
            this.worker.interrupt();
        }
        this.worker = new NavSwingWorker(tOCView);
        this.worker.start(1);
    }

    private Object loadData(TOCView tOCView) {
        if (tOCView == null) {
            return Boolean.FALSE;
        }
        this.topNode.removeAllChildren();
        String string = tOCView.getMergeType();
        Locale locale = tOCView.getHelpSet().getLocale();
        DefaultMutableTreeNode defaultMutableTreeNode = tOCView.getDataAsTree();
        MergeHelpUtilities.mergeNodeChildren(string, defaultMutableTreeNode);
        while (defaultMutableTreeNode.getChildCount() > 0) {
            this.topNode.add((DefaultMutableTreeNode)defaultMutableTreeNode.getFirstChild());
        }
        this.addSubHelpSets(tOCView.getHelpSet());
        return Boolean.TRUE;
    }

    private void presentData() {
        Map.ID iD;
        ((DefaultTreeModel)this.tree.getModel()).reload();
        this.setVisibility(this.topNode);
        if (this.toc.getModel() != null && (iD = this.toc.getModel().getCurrentID()) != null) {
            DefaultMutableTreeNode defaultMutableTreeNode = this.findID(this.topNode, iD);
            this.selectNode(defaultMutableTreeNode);
        }
    }

    private void reloadData(HelpModel helpModel) {
        BasicTOCNavigatorUI.debug("reloadData using new model");
        TOCView tOCView = null;
        HelpSet helpSet = helpModel.getHelpSet();
        TOCView tOCView2 = (TOCView)this.toc.getNavigatorView();
        String string = tOCView2.getName();
        NavigatorView[] arrnavigatorView = helpSet.getNavigatorViews();
        int n = 0;
        while (n < arrnavigatorView.length) {
            NavigatorView navigatorView;
            if (arrnavigatorView[n].getName().equals(string) && (navigatorView = arrnavigatorView[n]) instanceof TOCView) {
                tOCView = (TOCView)navigatorView;
                break;
            }
            ++n;
        }
        if (this.worker != null) {
            this.worker.interrupt();
        }
        this.worker = new NavSwingWorker(tOCView);
        this.worker.start(1);
    }

    protected void addSubHelpSets(HelpSet helpSet) {
        BasicTOCNavigatorUI.debug("addSubHelpSets");
        Enumeration enumeration = helpSet.getHelpSets();
        while (enumeration.hasMoreElements()) {
            HelpSet helpSet2 = (HelpSet)enumeration.nextElement();
            NavigatorView[] arrnavigatorView = helpSet2.getNavigatorViews();
            int n = 0;
            while (n < arrnavigatorView.length) {
                if (this.toc.canMerge(arrnavigatorView[n])) {
                    this.doMerge(arrnavigatorView[n]);
                }
                ++n;
            }
            this.addSubHelpSets(helpSet2);
        }
    }

    private void expand(String string) {
        BasicTOCNavigatorUI.debug("expand called");
        Enumeration enumeration = this.findNodes(string).elements();
        DefaultMutableTreeNode defaultMutableTreeNode = null;
        while (enumeration.hasMoreElements()) {
            Object object;
            TreePath treePath;
            defaultMutableTreeNode = (DefaultMutableTreeNode)enumeration.nextElement();
            BasicTOCNavigatorUI.debug("expandPath :" + defaultMutableTreeNode);
            if (defaultMutableTreeNode.getChildCount() > 0) {
                object = (DefaultMutableTreeNode)defaultMutableTreeNode.getFirstChild();
                treePath = new TreePath(object.getPath());
                this.tree.makeVisible(treePath);
                continue;
            }
            object = defaultMutableTreeNode.getPath();
            treePath = new TreePath((Object[])object);
            this.tree.makeVisible(treePath);
        }
    }

    private Vector findNodes(String string) {
        Enumeration enumeration = this.topNode.preorderEnumeration();
        DefaultMutableTreeNode defaultMutableTreeNode = null;
        Vector<DefaultMutableTreeNode> vector = new Vector<DefaultMutableTreeNode>();
        while (enumeration.hasMoreElements()) {
            defaultMutableTreeNode = (DefaultMutableTreeNode)enumeration.nextElement();
            BasicTOCNavigatorUI.debug(" node :" + defaultMutableTreeNode.toString());
            if (defaultMutableTreeNode == null) continue;
            TOCItem tOCItem = (TOCItem)defaultMutableTreeNode.getUserObject();
            if (tOCItem == null) {
                BasicTOCNavigatorUI.debug("tocItem is null");
                continue;
            }
            Map.ID iD = tOCItem.getID();
            if (iD == null) continue;
            BasicTOCNavigatorUI.debug("id name :" + iD.id);
            BasicTOCNavigatorUI.debug("target :" + string);
            Map.ID iD2 = null;
            try {
                iD2 = Map.ID.create(string, this.toc.getModel().getHelpSet());
            }
            catch (BadIDException var8_8) {
                System.err.println("Not valid ID :" + string);
                break;
            }
            if (!iD.equals(iD2)) continue;
            vector.addElement(defaultMutableTreeNode);
        }
        return vector;
    }

    private void collapse(String string) {
        Enumeration enumeration = this.findNodes(string).elements();
        DefaultMutableTreeNode defaultMutableTreeNode = null;
        BasicTOCNavigatorUI.debug("collapse called");
        while (enumeration.hasMoreElements()) {
            Object object;
            TreePath treePath;
            defaultMutableTreeNode = (DefaultMutableTreeNode)enumeration.nextElement();
            if (defaultMutableTreeNode.getChildCount() > 0) {
                object = defaultMutableTreeNode.getPath();
                treePath = new TreePath((Object[])object);
                this.tree.collapsePath(treePath);
                this.tree.collapseRow(this.tree.getRowForPath(treePath));
                continue;
            }
            object = (DefaultMutableTreeNode)defaultMutableTreeNode.getParent();
            treePath = new TreePath(object.getPath());
            this.tree.collapseRow(this.tree.getRowForPath(treePath));
        }
    }

    public void doMerge(NavigatorView navigatorView) {
        BasicTOCNavigatorUI.debug("merging data");
        Merge merge = Merge.DefaultMergeFactory.getMerge(this.toc.getNavigatorView(), navigatorView);
        if (merge != null) {
            merge.processMerge(this.topNode);
        }
    }

    public void merge(NavigatorView navigatorView) {
        BasicTOCNavigatorUI.debug("merging " + navigatorView);
        this.doMerge(navigatorView);
        ((DefaultTreeModel)this.tree.getModel()).reload();
        this.setVisibility(this.topNode);
    }

    public void remove(NavigatorView navigatorView) {
        BasicTOCNavigatorUI.debug("removing " + navigatorView);
        this.remove(this.topNode, navigatorView.getHelpSet());
        ((DefaultTreeModel)this.tree.getModel()).reload();
        this.setVisibility(this.topNode);
    }

    private void remove(DefaultMutableTreeNode defaultMutableTreeNode, HelpSet helpSet) {
        BasicTOCNavigatorUI.debug("remove(" + defaultMutableTreeNode + ", " + helpSet + ")");
        Vector<DefaultMutableTreeNode> vector = new Vector<DefaultMutableTreeNode>();
        Enumeration enumeration = defaultMutableTreeNode.children();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)enumeration.nextElement();
            BasicTOCNavigatorUI.debug("  considering " + defaultMutableTreeNode2);
            TOCItem tOCItem = (TOCItem)defaultMutableTreeNode2.getUserObject();
            HelpSet helpSet2 = tOCItem.getHelpSet();
            BasicTOCNavigatorUI.debug("chs=" + helpSet2 + " hs.contains(chs)=" + helpSet.contains(helpSet2));
            if (helpSet2 != null && helpSet.contains(helpSet2)) {
                if (defaultMutableTreeNode2.isLeaf()) {
                    BasicTOCNavigatorUI.debug("  tagging for removal: " + defaultMutableTreeNode2);
                    vector.addElement(defaultMutableTreeNode2);
                    continue;
                }
                this.remove(defaultMutableTreeNode2, helpSet);
                if (defaultMutableTreeNode2.isLeaf()) {
                    BasicTOCNavigatorUI.debug("  tagging for removal: " + defaultMutableTreeNode2);
                    vector.addElement(defaultMutableTreeNode2);
                    continue;
                }
                DefaultMutableTreeNode defaultMutableTreeNode3 = (DefaultMutableTreeNode)defaultMutableTreeNode2.getFirstChild();
                TOCItem tOCItem2 = (TOCItem)defaultMutableTreeNode3.getUserObject();
                tOCItem.setHelpSet(tOCItem2.getHelpSet());
                BasicTOCNavigatorUI.debug("  orphaned children - changing hs: " + defaultMutableTreeNode2);
                continue;
            }
            this.remove(defaultMutableTreeNode2, helpSet);
        }
        int n = 0;
        while (n < vector.size()) {
            BasicTOCNavigatorUI.debug("  removing " + vector.elementAt(n));
            defaultMutableTreeNode.remove((DefaultMutableTreeNode)vector.elementAt(n));
            ++n;
        }
    }

    private void setVisibility(DefaultMutableTreeNode defaultMutableTreeNode) {
        TOCItem tOCItem = (TOCItem)defaultMutableTreeNode.getUserObject();
        int n = -1;
        if (tOCItem != null) {
            n = tOCItem.getExpansionType();
        }
        TreePath treePath = new TreePath(defaultMutableTreeNode.getPath());
        int n2 = treePath.getPathCount();
        if (defaultMutableTreeNode == this.topNode || n2 <= 2 && n == -1 || n == 1) {
            this.tree.expandPath(new TreePath(defaultMutableTreeNode.getPath()));
            if (!defaultMutableTreeNode.isLeaf()) {
                int n3 = defaultMutableTreeNode.getChildCount();
                int n4 = 0;
                while (n4 < n3) {
                    this.setVisibility((DefaultMutableTreeNode)defaultMutableTreeNode.getChildAt(n4));
                    ++n4;
                }
            }
        }
    }

    public void idChanged(HelpModelEvent helpModelEvent) {
        Map.ID iD;
        TreePath treePath;
        TOCItem tOCItem;
        DefaultMutableTreeNode defaultMutableTreeNode;
        Object object;
        Map.ID iD2 = helpModelEvent.getID();
        HelpModel helpModel = this.toc.getModel();
        BasicTOCNavigatorUI.debug("idChanged(" + helpModelEvent + ")");
        if (helpModelEvent.getSource() != helpModel) {
            BasicTOCNavigatorUI.debug("Internal inconsistency!");
            BasicTOCNavigatorUI.debug("  " + helpModelEvent.getSource() + " != " + helpModel);
            throw new Error("Internal error");
        }
        if (iD2 == null) {
            iD2 = helpModel.getHelpSet().getCombinedMap().getClosestID(helpModelEvent.getURL());
        }
        if ((treePath = this.tree.getSelectionPath()) != null && (object = treePath.getLastPathComponent()) instanceof DefaultMutableTreeNode && (tOCItem = (TOCItem)(defaultMutableTreeNode = (DefaultMutableTreeNode)object).getUserObject()) != null && (iD = tOCItem.getID()) != null && iD.equals(iD2)) {
            return;
        }
        object = this.findID(this.topNode, iD2);
        this.selectNode((DefaultMutableTreeNode)object);
    }

    private DefaultMutableTreeNode findID(DefaultMutableTreeNode defaultMutableTreeNode, Map.ID iD) {
        BasicTOCNavigatorUI.debug("findID: (" + iD + ")");
        BasicTOCNavigatorUI.debug("  node: " + defaultMutableTreeNode);
        if (iD == null) {
            return null;
        }
        TOCItem tOCItem = (TOCItem)defaultMutableTreeNode.getUserObject();
        if (tOCItem != null) {
            Map.ID iD2 = tOCItem.getID();
            BasicTOCNavigatorUI.debug("  testID: " + iD2);
            if (iD2 != null && iD2.equals(iD)) {
                return defaultMutableTreeNode;
            }
        }
        int n = defaultMutableTreeNode.getChildCount();
        int n2 = 0;
        while (n2 < n) {
            DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)defaultMutableTreeNode.getChildAt(n2);
            DefaultMutableTreeNode defaultMutableTreeNode3 = this.findID(defaultMutableTreeNode2, iD);
            if (defaultMutableTreeNode3 != null) {
                return defaultMutableTreeNode3;
            }
            ++n2;
        }
        return null;
    }

    private void selectNode(DefaultMutableTreeNode defaultMutableTreeNode) {
        if (defaultMutableTreeNode == null) {
            this.tree.clearSelection();
            return;
        }
        TreePath treePath = new TreePath(defaultMutableTreeNode.getPath());
        this.tree.expandPath(treePath);
        this.tree.setSelectionPath(treePath);
        this.tree.scrollPathToVisible(treePath);
    }

    protected JHelpNavigator getHelpNavigator() {
        return this.toc;
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        TOCItem tOCItem;
        Object object;
        JHelpNavigator jHelpNavigator = this.getHelpNavigator();
        HelpModel helpModel = jHelpNavigator.getModel();
        BasicTOCNavigatorUI.debug("ValueChanged: " + treeSelectionEvent);
        BasicTOCNavigatorUI.debug("  model: " + helpModel);
        TreeItem[] arrtreeItem = null;
        TreePath[] arrtreePath = this.tree.getSelectionPaths();
        if (arrtreePath != null) {
            arrtreeItem = new TreeItem[arrtreePath.length];
            int n = 0;
            while (n < arrtreePath.length) {
                if (arrtreePath[n] != null) {
                    object = (DefaultMutableTreeNode)arrtreePath[n].getLastPathComponent();
                    arrtreeItem[n] = (TreeItem)object.getUserObject();
                }
                ++n;
            }
        }
        jHelpNavigator.setSelectedItems(arrtreeItem);
        if (arrtreeItem != null && arrtreeItem.length == 1 && (tOCItem = (TOCItem)arrtreeItem[0]) != null && tOCItem.getID() != null) {
            Presentation presentation;
            object = tOCItem.getPresentation();
            if (object == null) {
                try {
                    helpModel.setCurrentID(tOCItem.getID(), tOCItem.getName(), jHelpNavigator);
                }
                catch (InvalidHelpSetContextException var8_9) {
                    System.err.println("BadID: " + tOCItem.getID());
                    return;
                }
            }
            if (this.inInstallUI) {
                this.tree.clearSelection();
                return;
            }
            HelpSet helpSet = helpModel.getHelpSet();
            Class[] arrclass = new Class[2];
            Class class_ = class$javax$help$HelpSet == null ? (BasicTOCNavigatorUI.class$javax$help$HelpSet = BasicTOCNavigatorUI.class$("javax.help.HelpSet")) : class$javax$help$HelpSet;
            arrclass[0] = class_;
            Class class_2 = class$java$lang$String == null ? (BasicTOCNavigatorUI.class$java$lang$String = BasicTOCNavigatorUI.class$("java.lang.String")) : class$java$lang$String;
            arrclass[1] = class_2;
            Class[] arrclass2 = arrclass;
            Object[] arrobject = new Object[]{helpSet, tOCItem.getPresentationName()};
            try {
                ClassLoader classLoader = helpSet.getLoader();
                Class class_3 = classLoader == null ? Class.forName((String)object) : classLoader.loadClass((String)object);
                Method method = class_3.getMethod("getPresentation", arrclass2);
                presentation = (Presentation)method.invoke(null, arrobject);
            }
            catch (Exception var14_17) {
                throw new RuntimeException("error invoking presentation");
            }
            if (presentation == null) {
                return;
            }
            if (presentation instanceof WindowPresentation) {
                ((WindowPresentation)presentation).setActivationObject(this.tree);
            }
            if (presentation instanceof Popup) {
                ((Popup)presentation).setInvokerInternalBounds(this.tree.getPathBounds(arrtreePath[0]));
                ((Popup)presentation).setInvoker(this.tree);
            }
            try {
                presentation.setCurrentID(tOCItem.getID());
            }
            catch (InvalidHelpSetContextException var14_18) {
                System.err.println("BadID: " + tOCItem.getID());
                return;
            }
            presentation.setDisplayed(true);
        }
    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        BasicTOCNavigatorUI.debug(this + " " + "propertyChange: " + propertyChangeEvent.getSource() + " " + propertyChangeEvent.getPropertyName());
        if (propertyChangeEvent.getSource() == this.toc) {
            String string = propertyChangeEvent.getPropertyName();
            if (string.equals("helpModel")) {
                this.reloadData((HelpModel)propertyChangeEvent.getNewValue());
            } else if (string.equals("font")) {
                BasicTOCNavigatorUI.debug("Font change");
                Font font = (Font)propertyChangeEvent.getNewValue();
                this.tree.setFont(font);
                RepaintManager.currentManager(this.tree).markCompletelyDirty(this.tree);
            } else if (string.equals("expand")) {
                BasicTOCNavigatorUI.debug("Expand change");
                this.expand((String)propertyChangeEvent.getNewValue());
            } else if (string.equals("collapse")) {
                BasicTOCNavigatorUI.debug("Collapse change");
                this.collapse((String)propertyChangeEvent.getNewValue());
            }
        }
    }

    public void componentResized(ComponentEvent componentEvent) {
    }

    public void componentMoved(ComponentEvent componentEvent) {
    }

    public void componentShown(ComponentEvent componentEvent) {
        this.tree.requestFocus();
    }

    public void componentHidden(ComponentEvent componentEvent) {
    }

    protected static void debug(String string) {
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException var1_1) {
            throw new NoClassDefFoundError(var1_1.getMessage());
        }
    }

    private class NavSwingWorker
    extends SwingWorker {
        TOCView view;

        public NavSwingWorker(TOCView tOCView) {
            this.view = tOCView;
        }

        public Object construct() {
            return BasicTOCNavigatorUI.this.loadData(this.view);
        }

        public void finished() {
            if ((Boolean)this.get() == Boolean.TRUE) {
                BasicTOCNavigatorUI.this.presentData();
            }
        }
    }

}

