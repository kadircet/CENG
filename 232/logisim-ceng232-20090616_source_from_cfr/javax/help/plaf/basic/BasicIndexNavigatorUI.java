/*
 * Decompiled with CFR 0_114.
 */
package javax.help.plaf.basic;

import com.sun.java.help.impl.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import javax.help.BadIDException;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.IndexItem;
import javax.help.IndexView;
import javax.help.InvalidHelpSetContextException;
import javax.help.JHelpIndexNavigator;
import javax.help.JHelpNavigator;
import javax.help.Map;
import javax.help.Merge;
import javax.help.MergeHelpUtilities;
import javax.help.NavigatorView;
import javax.help.Popup;
import javax.help.Presentation;
import javax.help.TreeItem;
import javax.help.WindowPresentation;
import javax.help.event.HelpModelEvent;
import javax.help.event.HelpModelListener;
import javax.help.plaf.HelpNavigatorUI;
import javax.help.plaf.basic.BasicIndexCellRenderer;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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

public class BasicIndexNavigatorUI
extends HelpNavigatorUI
implements HelpModelListener,
TreeSelectionListener,
PropertyChangeListener,
ActionListener,
ComponentListener,
Serializable {
    protected JHelpIndexNavigator index;
    protected JScrollPane sp;
    protected DefaultMutableTreeNode topNode;
    protected JTree tree;
    protected JTextField searchField;
    protected RuleBasedCollator rbc;
    protected String oldText;
    protected DefaultMutableTreeNode currentFindNode;
    private SwingWorker worker = null;
    private static boolean debug = false;
    static /* synthetic */ Class class$javax$help$HelpSet;
    static /* synthetic */ Class class$java$lang$String;

    public static ComponentUI createUI(JComponent jComponent) {
        return new BasicIndexNavigatorUI((JHelpIndexNavigator)jComponent);
    }

    public BasicIndexNavigatorUI(JHelpIndexNavigator jHelpIndexNavigator) {
        ImageIcon imageIcon = this.getImageIcon(jHelpIndexNavigator.getNavigatorView());
        if (imageIcon != null) {
            this.setIcon(imageIcon);
        } else {
            this.setIcon(UIManager.getIcon("IndexNav.icon"));
        }
    }

    public void installUI(JComponent jComponent) {
        BasicIndexNavigatorUI.debug("installUI");
        this.index = (JHelpIndexNavigator)jComponent;
        HelpModel helpModel = this.index.getModel();
        this.index.setLayout(new BorderLayout());
        this.index.addPropertyChangeListener(this);
        this.index.addComponentListener(this);
        if (helpModel != null) {
            helpModel.addHelpModelListener(this);
        }
        this.topNode = new DefaultMutableTreeNode();
        JLabel jLabel = new JLabel(HelpUtilities.getString(HelpUtilities.getLocale(jComponent), "index.findLabel"));
        this.searchField = new JTextField();
        jLabel.setLabelFor(this.searchField);
        this.searchField.addActionListener(this);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, 0));
        jPanel.add(jLabel);
        jPanel.add(this.searchField);
        this.index.add("North", jPanel);
        this.tree = new JTree(this.topNode);
        TreeSelectionModel treeSelectionModel = this.tree.getSelectionModel();
        treeSelectionModel.addTreeSelectionListener(this);
        this.tree.setShowsRootHandles(true);
        this.tree.setRootVisible(false);
        this.setCellRenderer(this.index.getNavigatorView(), this.tree);
        this.sp = new JScrollPane();
        this.sp.getViewport().add(this.tree);
        this.index.add("Center", this.sp);
        this.reloadData();
    }

    protected void setCellRenderer(NavigatorView navigatorView, JTree jTree) {
        jTree.setCellRenderer(new BasicIndexCellRenderer());
    }

    public void uninstallUI(JComponent jComponent) {
        BasicIndexNavigatorUI.debug("uninstallUI");
        HelpModel helpModel = this.index.getModel();
        this.index.removeComponentListener(this);
        this.index.removePropertyChangeListener(this);
        TreeSelectionModel treeSelectionModel = this.tree.getSelectionModel();
        treeSelectionModel.removeTreeSelectionListener(this);
        this.index.setLayout(null);
        this.index.removeAll();
        if (helpModel != null) {
            helpModel.removeHelpModelListener(this);
        }
        this.index = null;
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
        BasicIndexNavigatorUI.debug("reloadData");
        IndexView indexView = (IndexView)this.index.getNavigatorView();
        if (this.worker != null) {
            this.worker.interrupt();
        }
        this.worker = new NavSwingWorker(indexView);
        this.worker.start(1);
    }

    private Object loadData(IndexView indexView) {
        if (indexView == null) {
            return Boolean.FALSE;
        }
        this.topNode.removeAllChildren();
        String string = indexView.getMergeType();
        Locale locale = indexView.getHelpSet().getLocale();
        DefaultMutableTreeNode defaultMutableTreeNode = indexView.getDataAsTree();
        MergeHelpUtilities.mergeNodeChildren(string, defaultMutableTreeNode);
        while (defaultMutableTreeNode.getChildCount() > 0) {
            this.topNode.add((DefaultMutableTreeNode)defaultMutableTreeNode.getFirstChild());
        }
        this.addSubHelpSets(indexView.getHelpSet());
        return Boolean.TRUE;
    }

    private void presentData() {
        Map.ID iD;
        ((DefaultTreeModel)this.tree.getModel()).reload();
        this.setVisibility(this.topNode);
        if (this.index.getModel() != null && (iD = this.index.getModel().getCurrentID()) != null) {
            DefaultMutableTreeNode defaultMutableTreeNode = this.findID(this.topNode, iD);
            this.selectNode(defaultMutableTreeNode);
        }
    }

    private void reloadData(HelpModel helpModel) {
        BasicIndexNavigatorUI.debug("reloadData in using new model");
        IndexView indexView = null;
        HelpSet helpSet = helpModel.getHelpSet();
        IndexView indexView2 = (IndexView)this.index.getNavigatorView();
        String string = indexView2.getName();
        NavigatorView[] arrnavigatorView = helpSet.getNavigatorViews();
        int n = 0;
        while (n < arrnavigatorView.length) {
            NavigatorView navigatorView;
            if (arrnavigatorView[n].getName().equals(string) && (navigatorView = arrnavigatorView[n]) instanceof IndexView) {
                indexView = (IndexView)navigatorView;
                break;
            }
            ++n;
        }
        if (this.worker != null) {
            this.worker.interrupt();
        }
        this.worker = new NavSwingWorker(indexView);
        this.worker.start(1);
    }

    protected void addSubHelpSets(HelpSet helpSet) {
        BasicIndexNavigatorUI.debug("addSubHelpSets");
        Enumeration enumeration = helpSet.getHelpSets();
        while (enumeration.hasMoreElements()) {
            HelpSet helpSet2 = (HelpSet)enumeration.nextElement();
            NavigatorView[] arrnavigatorView = helpSet2.getNavigatorViews();
            int n = 0;
            while (n < arrnavigatorView.length) {
                if (this.index.canMerge(arrnavigatorView[n])) {
                    this.doMerge(arrnavigatorView[n]);
                }
                ++n;
            }
            this.addSubHelpSets(helpSet2);
        }
    }

    private void expand(String string) {
        BasicIndexNavigatorUI.debug("expand called");
        Enumeration enumeration = this.findNodes(string).elements();
        DefaultMutableTreeNode defaultMutableTreeNode = null;
        while (enumeration.hasMoreElements()) {
            Object object;
            TreePath treePath;
            defaultMutableTreeNode = (DefaultMutableTreeNode)enumeration.nextElement();
            BasicIndexNavigatorUI.debug("expandPath :" + defaultMutableTreeNode);
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
            BasicIndexNavigatorUI.debug(" node :" + defaultMutableTreeNode.toString());
            if (defaultMutableTreeNode == null) continue;
            IndexItem indexItem = (IndexItem)defaultMutableTreeNode.getUserObject();
            if (indexItem == null) {
                BasicIndexNavigatorUI.debug("indexItem is null");
                continue;
            }
            Map.ID iD = indexItem.getID();
            if (iD == null) continue;
            BasicIndexNavigatorUI.debug("id name :" + iD.id);
            BasicIndexNavigatorUI.debug("target :" + string);
            Map.ID iD2 = null;
            try {
                iD2 = Map.ID.create(string, this.index.getModel().getHelpSet());
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
        BasicIndexNavigatorUI.debug("collapse called");
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
        BasicIndexNavigatorUI.debug("merging data");
        Merge merge = Merge.DefaultMergeFactory.getMerge(this.index.getNavigatorView(), navigatorView);
        if (merge != null) {
            merge.processMerge(this.topNode);
        }
    }

    public void merge(NavigatorView navigatorView) {
        BasicIndexNavigatorUI.debug("merge");
        this.doMerge(navigatorView);
        ((DefaultTreeModel)this.tree.getModel()).reload();
        this.setVisibility(this.topNode);
    }

    public void remove(NavigatorView navigatorView) {
        BasicIndexNavigatorUI.debug("removing " + navigatorView);
        this.remove(this.topNode, navigatorView.getHelpSet());
        ((DefaultTreeModel)this.tree.getModel()).reload();
        this.setVisibility(this.topNode);
    }

    private void remove(DefaultMutableTreeNode defaultMutableTreeNode, HelpSet helpSet) {
        BasicIndexNavigatorUI.debug("remove(" + defaultMutableTreeNode + ", " + helpSet + ")");
        Vector<DefaultMutableTreeNode> vector = new Vector<DefaultMutableTreeNode>();
        Enumeration enumeration = defaultMutableTreeNode.children();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)enumeration.nextElement();
            BasicIndexNavigatorUI.debug("  considering " + defaultMutableTreeNode2);
            IndexItem indexItem = (IndexItem)defaultMutableTreeNode2.getUserObject();
            HelpSet helpSet2 = indexItem.getHelpSet();
            BasicIndexNavigatorUI.debug("chs=" + helpSet2 + " hs.contains(chs)=" + helpSet.contains(helpSet2));
            if (helpSet2 != null && helpSet.contains(helpSet2)) {
                if (defaultMutableTreeNode2.isLeaf()) {
                    BasicIndexNavigatorUI.debug("  tagging for removal: " + defaultMutableTreeNode2);
                    vector.addElement(defaultMutableTreeNode2);
                    continue;
                }
                this.remove(defaultMutableTreeNode2, helpSet);
                if (defaultMutableTreeNode2.isLeaf()) {
                    BasicIndexNavigatorUI.debug("  tagging for removal: " + defaultMutableTreeNode2);
                    vector.addElement(defaultMutableTreeNode2);
                    continue;
                }
                DefaultMutableTreeNode defaultMutableTreeNode3 = (DefaultMutableTreeNode)defaultMutableTreeNode2.getFirstChild();
                IndexItem indexItem2 = (IndexItem)defaultMutableTreeNode3.getUserObject();
                indexItem.setHelpSet(indexItem2.getHelpSet());
                BasicIndexNavigatorUI.debug("  orphaned children - changing hs: " + defaultMutableTreeNode2);
                continue;
            }
            this.remove(defaultMutableTreeNode2, helpSet);
        }
        int n = 0;
        while (n < vector.size()) {
            BasicIndexNavigatorUI.debug("  removing " + vector.elementAt(n));
            defaultMutableTreeNode.remove((DefaultMutableTreeNode)vector.elementAt(n));
            ++n;
        }
    }

    private void setVisibility(DefaultMutableTreeNode defaultMutableTreeNode) {
        IndexItem indexItem = (IndexItem)defaultMutableTreeNode.getUserObject();
        if (defaultMutableTreeNode == this.topNode || indexItem != null && indexItem.getExpansionType() != 0) {
            this.tree.expandPath(new TreePath(defaultMutableTreeNode.getPath()));
            if (!defaultMutableTreeNode.isLeaf()) {
                int n = defaultMutableTreeNode.getChildCount();
                int n2 = 0;
                while (n2 < n) {
                    this.setVisibility((DefaultMutableTreeNode)defaultMutableTreeNode.getChildAt(n2));
                    ++n2;
                }
            }
        }
    }

    public void idChanged(HelpModelEvent helpModelEvent) {
        Map.ID iD;
        TreePath treePath;
        IndexItem indexItem;
        DefaultMutableTreeNode defaultMutableTreeNode;
        Object object;
        Map.ID iD2 = helpModelEvent.getID();
        HelpModel helpModel = this.index.getModel();
        BasicIndexNavigatorUI.debug("idChanged(" + helpModelEvent + ")");
        if (helpModelEvent.getSource() != helpModel) {
            BasicIndexNavigatorUI.debug("Internal inconsistency!");
            BasicIndexNavigatorUI.debug("  " + helpModelEvent.getSource() + " != " + helpModel);
            throw new Error("Internal error");
        }
        if (iD2 == null) {
            iD2 = helpModel.getHelpSet().getCombinedMap().getClosestID(helpModelEvent.getURL());
        }
        if ((treePath = this.tree.getSelectionPath()) != null && (object = treePath.getLastPathComponent()) instanceof DefaultMutableTreeNode && (indexItem = (IndexItem)(defaultMutableTreeNode = (DefaultMutableTreeNode)object).getUserObject()) != null && (iD = indexItem.getID()) != null && iD.equals(iD2)) {
            return;
        }
        object = this.findID(this.topNode, iD2);
        this.selectNode((DefaultMutableTreeNode)object);
    }

    private DefaultMutableTreeNode findID(DefaultMutableTreeNode defaultMutableTreeNode, Map.ID iD) {
        BasicIndexNavigatorUI.debug("findID: (" + iD + ")");
        BasicIndexNavigatorUI.debug("  node: " + defaultMutableTreeNode);
        if (iD == null) {
            return null;
        }
        IndexItem indexItem = (IndexItem)defaultMutableTreeNode.getUserObject();
        if (indexItem != null) {
            Map.ID iD2 = indexItem.getID();
            BasicIndexNavigatorUI.debug("  testID: " + iD2);
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
        return this.index;
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        Object object;
        IndexItem indexItem;
        JHelpNavigator jHelpNavigator = this.getHelpNavigator();
        HelpModel helpModel = jHelpNavigator.getModel();
        BasicIndexNavigatorUI.debug("ValueChanged: " + treeSelectionEvent);
        BasicIndexNavigatorUI.debug("  model: " + helpModel);
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
        if (arrtreeItem != null && arrtreeItem.length == 1 && (indexItem = (IndexItem)arrtreeItem[0]) != null && indexItem.getID() != null) {
            Presentation presentation;
            object = indexItem.getPresentation();
            if (object == null) {
                try {
                    helpModel.setCurrentID(indexItem.getID(), indexItem.getName(), jHelpNavigator);
                }
                catch (InvalidHelpSetContextException var8_9) {
                    System.err.println("BadID: " + indexItem.getID());
                    return;
                }
            }
            HelpSet helpSet = helpModel.getHelpSet();
            Class[] arrclass = new Class[2];
            Class class_ = class$javax$help$HelpSet == null ? (BasicIndexNavigatorUI.class$javax$help$HelpSet = BasicIndexNavigatorUI.class$("javax.help.HelpSet")) : class$javax$help$HelpSet;
            arrclass[0] = class_;
            Class class_2 = class$java$lang$String == null ? (BasicIndexNavigatorUI.class$java$lang$String = BasicIndexNavigatorUI.class$("java.lang.String")) : class$java$lang$String;
            arrclass[1] = class_2;
            Class[] arrclass2 = arrclass;
            Object[] arrobject = new Object[]{helpSet, indexItem.getPresentationName()};
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
                presentation.setCurrentID(indexItem.getID());
            }
            catch (InvalidHelpSetContextException var14_18) {
                System.err.println("BadID: " + indexItem.getID());
                return;
            }
            presentation.setDisplayed(true);
        }
    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        BasicIndexNavigatorUI.debug("propertyChange: " + propertyChangeEvent.getSource() + " " + propertyChangeEvent.getPropertyName());
        if (propertyChangeEvent.getSource() == this.index) {
            String string = propertyChangeEvent.getPropertyName();
            if (string.equals("helpModel")) {
                BasicIndexNavigatorUI.debug("model changed");
                this.reloadData((HelpModel)propertyChangeEvent.getNewValue());
            } else if (string.equals("font")) {
                BasicIndexNavigatorUI.debug("Font change");
                Font font = (Font)propertyChangeEvent.getNewValue();
                this.searchField.setFont(font);
                RepaintManager.currentManager(this.searchField).markCompletelyDirty(this.searchField);
                this.tree.setFont(font);
                RepaintManager.currentManager(this.tree).markCompletelyDirty(this.tree);
            } else if (string.equals("expand")) {
                BasicIndexNavigatorUI.debug("Expand change");
                this.expand((String)propertyChangeEvent.getNewValue());
            } else if (string.equals("collapse")) {
                BasicIndexNavigatorUI.debug("Collapse change");
                this.collapse((String)propertyChangeEvent.getNewValue());
            }
        }
    }

    public void componentResized(ComponentEvent componentEvent) {
    }

    public void componentMoved(ComponentEvent componentEvent) {
    }

    public void componentShown(ComponentEvent componentEvent) {
        this.searchField.selectAll();
        this.searchField.requestFocus();
    }

    public void componentHidden(ComponentEvent componentEvent) {
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.searchField) {
            this.rbc = (RuleBasedCollator)Collator.getInstance(this.index.getLocale());
            String string = this.searchField.getText();
            if (string != null) {
                string = string.toLowerCase();
            }
            if (this.oldText != null && string.compareTo(this.oldText) != 0) {
                this.currentFindNode = null;
            }
            this.oldText = string;
            DefaultMutableTreeNode defaultMutableTreeNode = this.searchName(this.topNode, string);
            if (defaultMutableTreeNode == null) {
                this.currentFindNode = null;
                this.index.getToolkit().beep();
                return;
            }
            this.currentFindNode = defaultMutableTreeNode;
            TreePath treePath = new TreePath(defaultMutableTreeNode.getPath());
            this.tree.scrollPathToVisible(treePath);
            this.tree.expandPath(treePath);
            this.tree.setSelectionPath(treePath);
        }
    }

    private DefaultMutableTreeNode searchName(DefaultMutableTreeNode defaultMutableTreeNode, String string) {
        if (this.currentFindNode == null) {
            String string2;
            IndexItem indexItem = (IndexItem)defaultMutableTreeNode.getUserObject();
            if (indexItem != null && (string2 = indexItem.getName()) != null && HelpUtilities.isStringInString(this.rbc, string, string2 = string2.toLowerCase())) {
                return defaultMutableTreeNode;
            }
        } else if (this.currentFindNode == defaultMutableTreeNode) {
            this.currentFindNode = null;
        }
        int n = defaultMutableTreeNode.getChildCount();
        int n2 = 0;
        while (n2 < n) {
            DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)defaultMutableTreeNode.getChildAt(n2);
            DefaultMutableTreeNode defaultMutableTreeNode3 = this.searchName(defaultMutableTreeNode2, string);
            if (defaultMutableTreeNode3 != null) {
                return defaultMutableTreeNode3;
            }
            ++n2;
        }
        return null;
    }

    private static void debug(String string) {
        if (debug) {
            System.out.println("BasicIndexNavigatorUI: " + string);
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

    private class NavSwingWorker
    extends SwingWorker {
        IndexView view;

        public NavSwingWorker(IndexView indexView) {
            this.view = indexView;
        }

        public Object construct() {
            return BasicIndexNavigatorUI.this.loadData(this.view);
        }

        public void finished() {
            if ((Boolean)this.get() == Boolean.TRUE) {
                BasicIndexNavigatorUI.this.presentData();
            }
        }
    }

}

