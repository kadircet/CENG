/*
 * Decompiled with CFR 0_114.
 */
package javax.help.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import javax.help.BadIDException;
import javax.help.GlossaryView;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.IndexItem;
import javax.help.InvalidHelpSetContextException;
import javax.help.JHelpContentViewer;
import javax.help.JHelpGlossaryNavigator;
import javax.help.JHelpNavigator;
import javax.help.Map;
import javax.help.Merge;
import javax.help.MergeHelpUtilities;
import javax.help.NavigatorView;
import javax.help.TextHelpModel;
import javax.help.TreeItem;
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
import javax.swing.JSplitPane;
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

public class BasicGlossaryNavigatorUI
extends HelpNavigatorUI
implements HelpModelListener,
TreeSelectionListener,
PropertyChangeListener,
ActionListener,
Serializable {
    protected JHelpGlossaryNavigator glossary;
    protected JScrollPane sp;
    protected DefaultMutableTreeNode topNode;
    protected JTree tree;
    protected JTextField searchField;
    protected RuleBasedCollator rbc;
    protected String oldText;
    protected DefaultMutableTreeNode currentFindNode;
    protected JHelpContentViewer viewer;
    private static boolean debug = false;

    public static ComponentUI createUI(JComponent jComponent) {
        return new BasicGlossaryNavigatorUI((JHelpGlossaryNavigator)jComponent);
    }

    public BasicGlossaryNavigatorUI(JHelpGlossaryNavigator jHelpGlossaryNavigator) {
        ImageIcon imageIcon = this.getImageIcon(jHelpGlossaryNavigator.getNavigatorView());
        if (imageIcon != null) {
            this.setIcon(imageIcon);
        } else {
            this.setIcon(UIManager.getIcon("GlossaryNav.icon"));
        }
    }

    public void installUI(JComponent jComponent) {
        BasicGlossaryNavigatorUI.debug("installUI");
        this.glossary = (JHelpGlossaryNavigator)jComponent;
        HelpModel helpModel = this.glossary.getModel();
        this.glossary.setLayout(new BorderLayout());
        this.glossary.addPropertyChangeListener(this);
        if (helpModel != null) {
            // empty if block
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
        this.glossary.add("North", jPanel);
        this.tree = new JTree(this.topNode);
        TreeSelectionModel treeSelectionModel = this.tree.getSelectionModel();
        treeSelectionModel.addTreeSelectionListener(this);
        this.tree.setShowsRootHandles(true);
        this.tree.setRootVisible(false);
        this.setCellRenderer(this.glossary.getNavigatorView(), this.tree);
        this.sp = new JScrollPane();
        this.sp.getViewport().add(this.tree);
        JSplitPane jSplitPane = new JSplitPane(0, false);
        jSplitPane.setOneTouchExpandable(true);
        jSplitPane.setTopComponent(this.sp);
        this.viewer = new JHelpContentViewer(helpModel.getHelpSet());
        this.viewer.setSynch(false);
        jSplitPane.setBottomComponent(this.viewer);
        this.glossary.add("Center", jSplitPane);
        jSplitPane.setDividerLocation(180);
        this.reloadData();
    }

    protected void setCellRenderer(NavigatorView navigatorView, JTree jTree) {
        jTree.setCellRenderer(new BasicIndexCellRenderer());
    }

    public void uninstallUI(JComponent jComponent) {
        BasicGlossaryNavigatorUI.debug("uninstallUI");
        HelpModel helpModel = this.glossary.getModel();
        this.glossary.removePropertyChangeListener(this);
        TreeSelectionModel treeSelectionModel = this.tree.getSelectionModel();
        treeSelectionModel.removeTreeSelectionListener(this);
        this.glossary.setLayout(null);
        this.glossary.removeAll();
        if (helpModel != null) {
            helpModel.removeHelpModelListener(this);
        }
        this.glossary = null;
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
        BasicGlossaryNavigatorUI.debug("reloadData");
        GlossaryView glossaryView = (GlossaryView)this.glossary.getNavigatorView();
        this.loadData(glossaryView);
    }

    private void loadData(GlossaryView glossaryView) {
        if (glossaryView == null) {
            return;
        }
        this.topNode.removeAllChildren();
        String string = glossaryView.getMergeType();
        Locale locale = glossaryView.getHelpSet().getLocale();
        DefaultMutableTreeNode defaultMutableTreeNode = glossaryView.getDataAsTree();
        MergeHelpUtilities.mergeNodeChildren(string, defaultMutableTreeNode);
        while (defaultMutableTreeNode.getChildCount() > 0) {
            this.topNode.add((DefaultMutableTreeNode)defaultMutableTreeNode.getFirstChild());
        }
        this.addSubHelpSets(glossaryView.getHelpSet());
        ((DefaultTreeModel)this.tree.getModel()).reload();
        this.setVisibility(this.topNode);
    }

    private void reloadData(HelpModel helpModel) {
        BasicGlossaryNavigatorUI.debug("reloadData in using new model");
        GlossaryView glossaryView = null;
        HelpSet helpSet = helpModel.getHelpSet();
        GlossaryView glossaryView2 = (GlossaryView)this.glossary.getNavigatorView();
        String string = glossaryView2.getName();
        NavigatorView[] arrnavigatorView = helpSet.getNavigatorViews();
        int n = 0;
        while (n < arrnavigatorView.length) {
            NavigatorView navigatorView;
            if (arrnavigatorView[n].getName().equals(string) && (navigatorView = arrnavigatorView[n]) instanceof GlossaryView) {
                glossaryView = (GlossaryView)navigatorView;
                break;
            }
            ++n;
        }
        this.loadData(glossaryView);
    }

    protected void addSubHelpSets(HelpSet helpSet) {
        BasicGlossaryNavigatorUI.debug("addSubHelpSets");
        Enumeration enumeration = helpSet.getHelpSets();
        while (enumeration.hasMoreElements()) {
            HelpSet helpSet2 = (HelpSet)enumeration.nextElement();
            NavigatorView[] arrnavigatorView = helpSet2.getNavigatorViews();
            int n = 0;
            while (n < arrnavigatorView.length) {
                if (this.glossary.canMerge(arrnavigatorView[n])) {
                    this.merge(arrnavigatorView[n]);
                }
                ++n;
            }
            this.addSubHelpSets(helpSet2);
        }
    }

    private void expand(String string) {
        BasicGlossaryNavigatorUI.debug("expand called");
        Enumeration enumeration = this.findNodes(string).elements();
        DefaultMutableTreeNode defaultMutableTreeNode = null;
        while (enumeration.hasMoreElements()) {
            Object object;
            TreePath treePath;
            defaultMutableTreeNode = (DefaultMutableTreeNode)enumeration.nextElement();
            BasicGlossaryNavigatorUI.debug("expandPath :" + defaultMutableTreeNode);
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
            BasicGlossaryNavigatorUI.debug(" node :" + defaultMutableTreeNode.toString());
            if (defaultMutableTreeNode == null) continue;
            IndexItem indexItem = (IndexItem)defaultMutableTreeNode.getUserObject();
            if (indexItem == null) {
                BasicGlossaryNavigatorUI.debug("indexItem is null");
                continue;
            }
            Map.ID iD = indexItem.getID();
            if (iD == null) continue;
            BasicGlossaryNavigatorUI.debug("id name :" + iD.id);
            BasicGlossaryNavigatorUI.debug("target :" + string);
            Map.ID iD2 = null;
            try {
                iD2 = Map.ID.create(string, this.glossary.getModel().getHelpSet());
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
        BasicGlossaryNavigatorUI.debug("collapse called");
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
        BasicGlossaryNavigatorUI.debug("merging data");
        Merge merge = Merge.DefaultMergeFactory.getMerge(this.glossary.getNavigatorView(), navigatorView);
        if (merge != null) {
            merge.processMerge(this.topNode);
        }
    }

    public void merge(NavigatorView navigatorView) {
        BasicGlossaryNavigatorUI.debug("merging data");
        this.doMerge(navigatorView);
        ((DefaultTreeModel)this.tree.getModel()).reload();
        this.setVisibility(this.topNode);
    }

    public void remove(NavigatorView navigatorView) {
        BasicGlossaryNavigatorUI.debug("removing " + navigatorView);
        this.remove(this.topNode, navigatorView.getHelpSet());
        ((DefaultTreeModel)this.tree.getModel()).reload();
        this.setVisibility(this.topNode);
    }

    private void remove(DefaultMutableTreeNode defaultMutableTreeNode, HelpSet helpSet) {
        BasicGlossaryNavigatorUI.debug("remove(" + defaultMutableTreeNode + ", " + helpSet + ")");
        Vector<DefaultMutableTreeNode> vector = new Vector<DefaultMutableTreeNode>();
        Enumeration enumeration = defaultMutableTreeNode.children();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)enumeration.nextElement();
            BasicGlossaryNavigatorUI.debug("  considering " + defaultMutableTreeNode2);
            IndexItem indexItem = (IndexItem)defaultMutableTreeNode2.getUserObject();
            HelpSet helpSet2 = indexItem.getHelpSet();
            BasicGlossaryNavigatorUI.debug("chs=" + helpSet2 + " hs.contains(chs)=" + helpSet.contains(helpSet2));
            if (helpSet2 != null && helpSet.contains(helpSet2)) {
                if (defaultMutableTreeNode2.isLeaf()) {
                    BasicGlossaryNavigatorUI.debug("  tagging for removal: " + defaultMutableTreeNode2);
                    vector.addElement(defaultMutableTreeNode2);
                    continue;
                }
                this.remove(defaultMutableTreeNode2, helpSet);
                if (defaultMutableTreeNode2.isLeaf()) {
                    BasicGlossaryNavigatorUI.debug("  tagging for removal: " + defaultMutableTreeNode2);
                    vector.addElement(defaultMutableTreeNode2);
                    continue;
                }
                DefaultMutableTreeNode defaultMutableTreeNode3 = (DefaultMutableTreeNode)defaultMutableTreeNode2.getFirstChild();
                IndexItem indexItem2 = (IndexItem)defaultMutableTreeNode3.getUserObject();
                indexItem.setHelpSet(indexItem2.getHelpSet());
                BasicGlossaryNavigatorUI.debug("  orphaned children - changing hs: " + defaultMutableTreeNode2);
                continue;
            }
            this.remove(defaultMutableTreeNode2, helpSet);
        }
        int n = 0;
        while (n < vector.size()) {
            BasicGlossaryNavigatorUI.debug("  removing " + vector.elementAt(n));
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
        HelpModel helpModel = this.glossary.getModel();
        BasicGlossaryNavigatorUI.debug("idChanged(" + helpModelEvent + ")");
        if (helpModelEvent.getSource() != helpModel) {
            System.err.println("Internal inconsistency!");
            System.err.println("  " + helpModelEvent.getSource() + " != " + helpModel);
            throw new Error("Internal error");
        }
        if (iD2 == null) {
            // empty if block
        }
        if ((treePath = this.tree.getSelectionPath()) != null && (object = treePath.getLastPathComponent()) instanceof DefaultMutableTreeNode && (indexItem = (IndexItem)(defaultMutableTreeNode = (DefaultMutableTreeNode)object).getUserObject()) != null && (iD = indexItem.getID()) != null && iD.equals(iD2)) {
            return;
        }
        object = this.findID(this.topNode, iD2);
        this.selectNode((DefaultMutableTreeNode)object);
    }

    private DefaultMutableTreeNode findID(DefaultMutableTreeNode defaultMutableTreeNode, Map.ID iD) {
        BasicGlossaryNavigatorUI.debug("findID: (" + iD + ")");
        BasicGlossaryNavigatorUI.debug("  node: " + defaultMutableTreeNode);
        if (iD == null) {
            return null;
        }
        IndexItem indexItem = (IndexItem)defaultMutableTreeNode.getUserObject();
        if (indexItem != null) {
            Map.ID iD2 = indexItem.getID();
            BasicGlossaryNavigatorUI.debug("  testID: " + iD2);
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

    protected JHelpContentViewer getContentViewer() {
        return this.viewer;
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
        return this.glossary;
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        IndexItem indexItem;
        JHelpNavigator jHelpNavigator = this.getHelpNavigator();
        HelpModel helpModel = jHelpNavigator.getModel();
        BasicGlossaryNavigatorUI.debug("ValueChanged: " + treeSelectionEvent);
        BasicGlossaryNavigatorUI.debug("  model: " + helpModel);
        TreeItem[] arrtreeItem = null;
        TreePath[] arrtreePath = this.tree.getSelectionPaths();
        if (arrtreePath != null) {
            arrtreeItem = new TreeItem[arrtreePath.length];
            int n = 0;
            while (n < arrtreePath.length) {
                if (arrtreePath[n] != null) {
                    DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)arrtreePath[n].getLastPathComponent();
                    arrtreeItem[n] = (TreeItem)defaultMutableTreeNode.getUserObject();
                }
                ++n;
            }
        }
        jHelpNavigator.setSelectedItems(arrtreeItem);
        if (arrtreeItem != null && arrtreeItem.length == 1 && (indexItem = (IndexItem)arrtreeItem[0]) != null && indexItem.getID() != null) {
            try {
                this.getContentViewer().getModel().setCurrentID(indexItem.getID(), indexItem.getName(), jHelpNavigator);
            }
            catch (InvalidHelpSetContextException var7_9) {
                System.err.println("BadID: " + indexItem.getID());
                return;
            }
        }
    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        BasicGlossaryNavigatorUI.debug("propertyChange: " + propertyChangeEvent.getSource() + " " + propertyChangeEvent.getPropertyName());
        if (propertyChangeEvent.getSource() == this.glossary) {
            String string = propertyChangeEvent.getPropertyName();
            if (string.equals("helpModel")) {
                BasicGlossaryNavigatorUI.debug("model changed");
                this.reloadData((HelpModel)propertyChangeEvent.getNewValue());
            } else if (string.equals("font")) {
                BasicGlossaryNavigatorUI.debug("Font change");
                Font font = (Font)propertyChangeEvent.getNewValue();
                this.tree.setFont(font);
                RepaintManager.currentManager(this.tree).markCompletelyDirty(this.tree);
            } else if (string.equals("expand")) {
                BasicGlossaryNavigatorUI.debug("Expand change");
                this.expand((String)propertyChangeEvent.getNewValue());
            } else if (string.equals("collapse")) {
                BasicGlossaryNavigatorUI.debug("Collapse change");
                this.collapse((String)propertyChangeEvent.getNewValue());
            }
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.searchField) {
            this.rbc = (RuleBasedCollator)Collator.getInstance(this.glossary.getLocale());
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
                this.glossary.getToolkit().beep();
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
            if (indexItem != null && (string2 = indexItem.getName()) != null && (string2 = string2.toLowerCase()).startsWith(string)) {
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
            System.out.println("BasicGlossaryNavigatorUI: " + string);
        }
    }
}

