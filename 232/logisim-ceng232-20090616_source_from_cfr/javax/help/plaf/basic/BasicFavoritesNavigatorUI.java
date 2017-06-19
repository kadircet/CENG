/*
 * Decompiled with CFR 0_114.
 */
package javax.help.plaf.basic;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.RuleBasedCollator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;
import javax.help.BadIDException;
import javax.help.FavoritesItem;
import javax.help.FavoritesNode;
import javax.help.FavoritesView;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.InvalidHelpSetContextException;
import javax.help.JHelpFavoritesNavigator;
import javax.help.JHelpNavigator;
import javax.help.Map;
import javax.help.NavigatorView;
import javax.help.SwingHelpUtilities;
import javax.help.TreeItem;
import javax.help.event.HelpModelEvent;
import javax.help.event.HelpModelListener;
import javax.help.event.HelpSetEvent;
import javax.help.event.HelpSetListener;
import javax.help.plaf.HelpNavigatorUI;
import javax.help.plaf.basic.BasicFavoritesCellRenderer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.RepaintManager;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class BasicFavoritesNavigatorUI
extends HelpNavigatorUI
implements HelpModelListener,
TreeSelectionListener,
HelpSetListener,
PropertyChangeListener,
TreeModelListener,
Serializable {
    protected JHelpFavoritesNavigator favorites;
    protected JScrollPane sp;
    protected FavoritesNode topNode;
    protected JTree tree;
    private String contentTitle;
    protected RuleBasedCollator rbc;
    protected String oldText;
    protected DefaultMutableTreeNode currentFindNode;
    protected Action addAction;
    protected Action removeAction;
    protected Action cutAction;
    protected Action pasteAction;
    protected Action copyAction;
    protected Action folderAction;
    protected JPopupMenu popup;
    private HashMap dataMap = new HashMap();
    private FavoritesNode favNode = null;
    private FavoritesNode rootNode = null;
    private Locale locale = null;
    private FavoritesNode selectedNode = null;
    private FavoritesItem selectedItem = null;
    private TreePath selectedTreePath = null;
    private Map.ID selectedID = null;
    private JMenuItem newFolderMI = null;
    private JMenuItem addMI = null;
    private JSeparator separatorMI = null;
    private JMenuItem cutMI = null;
    private JMenuItem copyMI = null;
    private JMenuItem pasteMI = null;
    private JMenuItem removeMI = null;
    private HashMap hsMap = null;
    private HashMap connections = new HashMap();
    private Vector nodeClipboard = new Vector();
    private static boolean on1dot3 = false;
    private static boolean debug;
    static /* synthetic */ Class class$javax$help$plaf$basic$BasicFavoritesNavigatorUI;

    public static ComponentUI createUI(JComponent jComponent) {
        return new BasicFavoritesNavigatorUI((JHelpFavoritesNavigator)jComponent);
    }

    public BasicFavoritesNavigatorUI(JHelpFavoritesNavigator jHelpFavoritesNavigator) {
        ImageIcon imageIcon = this.getImageIcon(jHelpFavoritesNavigator.getNavigatorView());
        if (imageIcon != null) {
            this.setIcon(imageIcon);
        } else {
            this.setIcon(UIManager.getIcon("FavoritesNav.icon"));
        }
    }

    public void installUI(JComponent jComponent) {
        Object object;
        BasicFavoritesNavigatorUI.debug("installUI");
        this.locale = HelpUtilities.getLocale(jComponent);
        this.addAction = new AddAction(this);
        this.removeAction = new RemoveAction(this);
        this.folderAction = new FolderAction(this);
        this.favorites = (JHelpFavoritesNavigator)jComponent;
        HelpModel helpModel = this.favorites.getModel();
        this.favorites.setLayout(new BorderLayout());
        this.favorites.addPropertyChangeListener(this);
        if (helpModel != null) {
            helpModel.addHelpModelListener(this);
            helpModel.addPropertyChangeListener(this);
            object = helpModel.getHelpSet();
            if (object != null) {
                object.addHelpSetListener(this);
            }
        }
        this.topNode = new FavoritesNode(new FavoritesItem("Favorites"));
        this.tree = on1dot3 ? new FavoritesTree(this.topNode) : new JTree(this.topNode);
        this.tree.setEditable(true);
        this.tree.addMouseListener(new PopupListener());
        this.cutAction = new CutAction();
        this.copyAction = new CopyAction();
        this.pasteAction = new PasteAction();
        this.popup = new JPopupMenu();
        this.newFolderMI = new JMenuItem((String)this.folderAction.getValue("Name"));
        this.newFolderMI.addActionListener(this.folderAction);
        this.popup.add(this.newFolderMI);
        this.addMI = new JMenuItem((String)this.addAction.getValue("Name"));
        this.addMI.addActionListener(this.addAction);
        this.popup.add(this.addMI);
        this.separatorMI = new JSeparator();
        this.popup.add(this.separatorMI);
        this.cutMI = new JMenuItem((String)this.cutAction.getValue("Name"));
        this.cutMI.addActionListener(this.cutAction);
        this.cutMI.setAccelerator(KeyStroke.getKeyStroke(88, 2));
        this.popup.add(this.cutMI);
        this.copyMI = new JMenuItem((String)this.copyAction.getValue("Name"));
        this.copyMI.addActionListener(this.copyAction);
        this.copyMI.setAccelerator(KeyStroke.getKeyStroke(67, 2));
        this.popup.add(this.copyMI);
        this.pasteMI = new JMenuItem((String)this.pasteAction.getValue("Name"));
        this.pasteMI.addActionListener(this.pasteAction);
        this.pasteMI.setEnabled(false);
        this.pasteMI.setAccelerator(KeyStroke.getKeyStroke(86, 2));
        this.popup.add(this.pasteMI);
        this.removeMI = new JMenuItem((String)this.removeAction.getValue("Name"));
        this.removeMI.addActionListener(this.removeAction);
        this.popup.add(this.removeMI);
        this.tree.getModel().addTreeModelListener(this);
        this.tree.addTreeSelectionListener(this);
        object = this.tree.getSelectionModel();
        object.addTreeSelectionListener(this);
        this.tree.setShowsRootHandles(true);
        this.tree.setRootVisible(false);
        this.setCellRenderer(this.favorites.getNavigatorView(), this.tree);
        this.sp = new JScrollPane();
        this.sp.getViewport().add(this.tree);
        this.favorites.add("Center", this.sp);
        this.reloadData();
    }

    protected void setCellRenderer(NavigatorView navigatorView, JTree jTree) {
        jTree.setCellRenderer(new BasicFavoritesCellRenderer());
    }

    public void uninstallUI(JComponent jComponent) {
        BasicFavoritesNavigatorUI.debug("uninstallUI");
        HelpModel helpModel = this.favorites.getModel();
        this.favorites.removePropertyChangeListener(this);
        TreeSelectionModel treeSelectionModel = this.tree.getSelectionModel();
        treeSelectionModel.removeTreeSelectionListener(this);
        this.favorites.setLayout(null);
        this.favorites.removeAll();
        if (helpModel != null) {
            helpModel.removeHelpModelListener(this);
        }
        this.favorites = null;
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
        BasicFavoritesNavigatorUI.debug("reloadData");
        if (this.favorites.getModel() == null) {
            return;
        }
        this.topNode.removeAllChildren();
        FavoritesView favoritesView = (FavoritesView)this.favorites.getNavigatorView();
        if (favoritesView == null) {
            return;
        }
        this.favNode = favoritesView.getDataAsTree();
        this.rootNode = this.favNode.getDeepCopy();
        this.classifyNode(this.favNode);
        while (this.favNode.getChildCount() > 0) {
            this.topNode.add((DefaultMutableTreeNode)this.favNode.getFirstChild());
        }
        ((DefaultTreeModel)this.tree.getModel()).reload();
        this.setVisibility(this.topNode);
    }

    private void classifyNode(FavoritesNode favoritesNode) {
        BasicFavoritesNavigatorUI.debug("classifyNode");
        if (favoritesNode == null) {
            return;
        }
        HelpModel helpModel = this.favorites.getModel();
        if (helpModel == null) {
            favoritesNode.removeAllChildren();
            return;
        }
        HelpSet helpSet = helpModel.getHelpSet();
        if (helpSet == null) {
            favoritesNode.removeAllChildren();
            return;
        }
        this.hsMap = new HashMap();
        this.hsMap.put(helpSet.getTitle(), helpSet);
        this.fillHelpSetTitles(helpSet);
        this.classifyChildren(favoritesNode);
    }

    private void fillHelpSetTitles(HelpSet helpSet) {
        Enumeration enumeration = helpSet.getHelpSets();
        while (enumeration.hasMoreElements()) {
            HelpSet helpSet2 = (HelpSet)enumeration.nextElement();
            if (helpSet2 == null) continue;
            this.hsMap.put(helpSet2.getTitle(), helpSet2);
            BasicFavoritesNavigatorUI.debug(" fill title: " + helpSet2.getTitle());
            this.fillHelpSetTitles(helpSet2);
        }
    }

    private void classifyChildren(FavoritesNode favoritesNode) {
        FavoritesNode favoritesNode2;
        Vector vector;
        Object object;
        BasicFavoritesNavigatorUI.debug("classifyChildren: " + favoritesNode);
        if (favoritesNode == null) {
            return;
        }
        boolean bl = true;
        Enumeration enumeration = favoritesNode.preorderEnumeration();
        while (enumeration.hasMoreElements()) {
            vector = (FavoritesNode)enumeration.nextElement();
            if (bl) {
                bl = false;
                continue;
            }
            object = vector.getDeepCopy();
            this.connections.put(vector, object);
            favoritesNode2 = (FavoritesNode)this.connections.get(vector.getParent());
            if (favoritesNode2 == null) {
                favoritesNode2 = this.rootNode;
            }
            favoritesNode2.add((DefaultMutableTreeNode)object);
            FavoritesItem favoritesItem = (FavoritesItem)vector.getUserObject();
            BasicFavoritesNavigatorUI.debug("classify item: " + favoritesItem);
            if (favoritesItem == null) {
                BasicFavoritesNavigatorUI.debug("item is null : fillDataMap");
                continue;
            }
            String string = favoritesItem.getTarget();
            String string2 = favoritesItem.getHelpSetTitle();
            if (!this.hsMap.containsKey(string2) && vector.getVisibleChildCount() == 0) {
                if (favoritesItem.emptyInitState() && favoritesItem.isFolder()) {
                    BasicFavoritesNavigatorUI.debug("empty init state");
                    continue;
                }
                favoritesItem.setVisible(false);
                continue;
            }
            if (string == null) {
                BasicFavoritesNavigatorUI.debug("target is null:fillDataMap");
                continue;
            }
            Map.ID iD = null;
            try {
                iD = Map.ID.create(string, (HelpSet)this.hsMap.get(string2));
            }
            catch (BadIDException var11_13) {
                BasicFavoritesNavigatorUI.debug(var11_13.getMessage());
                continue;
            }
            BasicFavoritesNavigatorUI.debug("put to the dataMap: " + favoritesItem);
            this.dataMap.put(favoritesItem, iD);
        }
        vector = new Vector();
        object = favoritesNode.breadthFirstEnumeration();
        while (object.hasMoreElements()) {
            favoritesNode2 = (FavoritesNode)object.nextElement();
            if (favoritesNode2.isVisible()) continue;
            BasicFavoritesNavigatorUI.debug("remove node:" + (FavoritesItem)favoritesNode2.getUserObject());
            vector.addElement(favoritesNode2);
        }
        int n = 0;
        while (n < vector.size()) {
            BasicFavoritesNavigatorUI.debug("removing " + vector.elementAt(n));
            try {
                favoritesNode.remove((DefaultMutableTreeNode)vector.elementAt(n));
            }
            catch (IllegalArgumentException var7_9) {
                // empty catch block
            }
            ++n;
        }
    }

    private void expand(String string) {
        BasicFavoritesNavigatorUI.debug("expand called");
        Enumeration enumeration = this.findNodes(string).elements();
        DefaultMutableTreeNode defaultMutableTreeNode = null;
        while (enumeration.hasMoreElements()) {
            Object object;
            TreePath treePath;
            defaultMutableTreeNode = (DefaultMutableTreeNode)enumeration.nextElement();
            BasicFavoritesNavigatorUI.debug("expandPath :" + defaultMutableTreeNode);
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
            BasicFavoritesNavigatorUI.debug(" node :" + defaultMutableTreeNode.toString());
            if (defaultMutableTreeNode == null) continue;
            FavoritesItem favoritesItem = (FavoritesItem)defaultMutableTreeNode.getUserObject();
            if (favoritesItem == null) {
                BasicFavoritesNavigatorUI.debug("favoritesItem is null");
                continue;
            }
            Map.ID iD = (Map.ID)this.dataMap.get(favoritesItem);
            if (iD == null) continue;
            BasicFavoritesNavigatorUI.debug("id name :" + iD.id);
            BasicFavoritesNavigatorUI.debug("target :" + string);
            Map.ID iD2 = null;
            try {
                iD2 = Map.ID.create(string, this.favorites.getModel().getHelpSet());
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
        BasicFavoritesNavigatorUI.debug("collapse called");
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

    private void setVisibility(DefaultMutableTreeNode defaultMutableTreeNode) {
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

    public void idChanged(HelpModelEvent helpModelEvent) {
        Object object;
        BasicFavoritesNavigatorUI.debug("idChanged(" + helpModelEvent + ")");
        Map.ID iD = helpModelEvent.getID();
        this.contentTitle = helpModelEvent.getHistoryName();
        URL uRL = helpModelEvent.getURL();
        String string = null;
        if (uRL != null) {
            string = uRL.toExternalForm();
        }
        Map.ID iD2 = null;
        String string2 = null;
        String string3 = null;
        FavoritesItem favoritesItem = null;
        HelpModel helpModel = this.favorites.getModel();
        if (helpModelEvent.getSource() != helpModel) {
            BasicFavoritesNavigatorUI.debug("Internal inconsistency!");
            BasicFavoritesNavigatorUI.debug("  " + helpModelEvent.getSource() + " != " + helpModel);
            throw new Error("Internal error");
        }
        TreePath treePath = this.tree.getSelectionPath();
        if (treePath != null && (object = treePath.getLastPathComponent()) instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)object;
            favoritesItem = (FavoritesItem)defaultMutableTreeNode.getUserObject();
            if (favoritesItem != null) {
                iD2 = (Map.ID)this.dataMap.get(favoritesItem);
                string2 = favoritesItem.getURLSpec();
                string3 = favoritesItem.getName();
            }
            if (string3 != null && string3.equals(this.contentTitle)) {
                if (iD2 != null && iD2.equals(iD)) {
                    return;
                }
                if (string2 != null && string2.equals(string)) {
                    return;
                }
            }
        }
        object = null;
        object = this.findID(this.topNode, iD);
        if (object == null) {
            object = this.findURL(this.topNode, string);
        }
        if (object == null) {
            this.tree.clearSelection();
            return;
        }
        TreePath treePath2 = new TreePath(object.getPath());
        this.tree.expandPath(treePath2);
        this.tree.setSelectionPath(treePath2);
        this.tree.scrollPathToVisible(treePath2);
    }

    private DefaultMutableTreeNode findID(DefaultMutableTreeNode defaultMutableTreeNode, Map.ID iD) {
        BasicFavoritesNavigatorUI.debug("findID: (" + iD + ")");
        BasicFavoritesNavigatorUI.debug("  node: " + defaultMutableTreeNode);
        if (iD == null) {
            return null;
        }
        FavoritesItem favoritesItem = (FavoritesItem)defaultMutableTreeNode.getUserObject();
        Map.ID iD2 = (Map.ID)this.dataMap.get(favoritesItem);
        if (iD2 != null) {
            Map.ID iD3 = iD2;
            BasicFavoritesNavigatorUI.debug("  testID: " + iD3);
            if (iD3 != null && iD3.equals(iD)) {
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

    private DefaultMutableTreeNode findURL(DefaultMutableTreeNode defaultMutableTreeNode, String string) {
        BasicFavoritesNavigatorUI.debug(" findURL: " + string);
        if (string == null) {
            return null;
        }
        Enumeration enumeration = defaultMutableTreeNode.children();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)enumeration.nextElement();
            FavoritesItem favoritesItem = (FavoritesItem)defaultMutableTreeNode2.getUserObject();
            String string2 = favoritesItem.getName();
            String string3 = favoritesItem.getURLSpec();
            if (string.equals(string3)) {
                return defaultMutableTreeNode2;
            }
            this.findURL(defaultMutableTreeNode2, string);
        }
        return null;
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        FavoritesItem favoritesItem;
        this.selectedTreePath = treeSelectionEvent.getNewLeadSelectionPath();
        if (this.selectedTreePath == null) {
            this.selectedNode = null;
            return;
        }
        this.selectedNode = (FavoritesNode)this.selectedTreePath.getLastPathComponent();
        if (this.selectedNode != null) {
            this.selectedItem = (FavoritesItem)this.selectedNode.getUserObject();
            this.selectedID = (Map.ID)this.dataMap.get(this.selectedItem);
        }
        HelpModel helpModel = this.favorites.getModel();
        HelpSet helpSet = helpModel.getHelpSet();
        BasicFavoritesNavigatorUI.debug("ValueChanged: " + treeSelectionEvent);
        BasicFavoritesNavigatorUI.debug("  model: " + helpModel);
        if (helpModel == null) {
            return;
        }
        TreeItem[] arrtreeItem = null;
        TreePath[] arrtreePath = this.tree.getSelectionPaths();
        if (arrtreePath != null) {
            this.removeAction.setEnabled(true);
            arrtreeItem = new TreeItem[arrtreePath.length];
            int n = 0;
            while (n < arrtreePath.length) {
                if (arrtreePath[n] != null) {
                    DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)arrtreePath[n].getLastPathComponent();
                    FavoritesItem favoritesItem2 = (FavoritesItem)defaultMutableTreeNode.getUserObject();
                    try {
                        String string = favoritesItem2.getTarget();
                        if (string != null) {
                            Map.ID iD = Map.ID.create(favoritesItem2.getTarget(), helpSet);
                            favoritesItem2.setID(iD);
                        }
                    }
                    catch (BadIDException var9_15) {
                        // empty catch block
                    }
                    arrtreeItem[n] = favoritesItem2;
                }
                ++n;
            }
        } else {
            this.removeAction.setEnabled(false);
            this.pasteAction.setEnabled(false);
            this.pasteMI.setEnabled(false);
            return;
        }
        this.favorites.setSelectedItems(arrtreeItem);
        if (arrtreeItem != null && arrtreeItem.length == 1 && (favoritesItem = (FavoritesItem)arrtreeItem[0]) != null) {
            Map.ID iD = (Map.ID)this.dataMap.get(favoritesItem);
            if (iD != null) {
                BasicFavoritesNavigatorUI.debug("itemID: " + iD);
                try {
                    helpModel.setCurrentID(iD, favoritesItem.getName(), this.favorites);
                }
                catch (InvalidHelpSetContextException var8_11) {
                    System.err.println("BadID: " + favoritesItem.getID());
                    return;
                }
            }
            if (favoritesItem.getURLSpec() != null) {
                try {
                    URL uRL = new URL(favoritesItem.getURLSpec());
                    helpModel.setCurrentURL(uRL, favoritesItem.getName(), this.favorites);
                }
                catch (MalformedURLException var8_13) {
                    System.err.println(var8_13);
                }
            }
        }
    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String string;
        BasicFavoritesNavigatorUI.debug("propertyChange: " + propertyChangeEvent.getSource() + " " + propertyChangeEvent.getPropertyName());
        if (propertyChangeEvent.getSource() == this.favorites) {
            String string2 = propertyChangeEvent.getPropertyName();
            if (string2.equals("helpModel")) {
                BasicFavoritesNavigatorUI.debug("model changed");
                this.reloadData();
            } else if (string2.equals("font")) {
                BasicFavoritesNavigatorUI.debug("Font change");
                Font font = (Font)propertyChangeEvent.getNewValue();
                this.tree.setFont(font);
                RepaintManager.currentManager(this.tree).markCompletelyDirty(this.tree);
            } else if (string2.equals("expand")) {
                BasicFavoritesNavigatorUI.debug("Expand change");
                this.expand((String)propertyChangeEvent.getNewValue());
            } else if (string2.equals("collapse")) {
                BasicFavoritesNavigatorUI.debug("Collapse change");
                this.collapse((String)propertyChangeEvent.getNewValue());
            } else if (string2.equals("navigatorChange")) {
                BasicFavoritesNavigatorUI.debug("Navigator change");
                this.tree.clearSelection();
            }
        } else if (this.favorites != null && propertyChangeEvent.getSource() == this.favorites.getModel() && (string = propertyChangeEvent.getPropertyName()).equals("helpSet")) {
            this.reloadData();
        }
    }

    public void helpSetAdded(HelpSetEvent helpSetEvent) {
        BasicFavoritesNavigatorUI.debug("HelpSet added");
        this.reloadData();
    }

    public void helpSetRemoved(HelpSetEvent helpSetEvent) {
        BasicFavoritesNavigatorUI.debug("HelpSet removed");
        this.reloadData();
    }

    public void saveFavorites() {
        FavoritesView favoritesView = (FavoritesView)this.favorites.getNavigatorView();
        favoritesView.saveFavorites(this.rootNode);
    }

    public void treeStructureChanged(TreeModelEvent treeModelEvent) {
        BasicFavoritesNavigatorUI.debug("tree structure changed");
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void treeNodesInserted(TreeModelEvent treeModelEvent) {
        BasicFavoritesNavigatorUI.debug("node inserted");
        int n = -1;
        FavoritesNode favoritesNode = (FavoritesNode)treeModelEvent.getTreePath().getLastPathComponent();
        Object[] arrobject = treeModelEvent.getChildren();
        int[] arrn = treeModelEvent.getChildIndices();
        int n2 = arrn[0];
        BasicFavoritesNavigatorUI.debug("index first " + n2);
        int n3 = arrn.length + n2 - 1;
        FavoritesNode favoritesNode2 = (FavoritesNode)this.connections.get(favoritesNode);
        if (favoritesNode2 == null) {
            favoritesNode2 = this.rootNode;
        }
        BasicFavoritesNavigatorUI.debug("root parent " + favoritesNode2);
        if (n2 == 0) {
            FavoritesNode favoritesNode3;
            if (favoritesNode2.getChildCount() == 0) {
                n = 0;
            } else {
                Enumeration enumeration = favoritesNode2.children();
                while (enumeration.hasMoreElements()) {
                    favoritesNode3 = (FavoritesNode)enumeration.nextElement();
                    if (!favoritesNode3.isVisible()) continue;
                    BasicFavoritesNavigatorUI.debug("is visible : " + favoritesNode3);
                    n = favoritesNode2.getIndex(favoritesNode3);
                    break;
                }
            }
            if (n < 0) return;
            int n4 = arrobject.length - 1;
            while (n4 >= 0) {
                favoritesNode3 = ((FavoritesNode)arrobject[n4]).getDeepCopy();
                favoritesNode2.insert(favoritesNode3, n);
                this.connections.put((FavoritesNode)arrobject[n4], favoritesNode3);
                --n4;
            }
            return;
        } else {
            if (n2 <= 0) return;
            FavoritesNode favoritesNode4 = (FavoritesNode)favoritesNode.getChildAt(n2 - 1);
            FavoritesNode favoritesNode5 = (FavoritesNode)this.connections.get(favoritesNode4);
            n = favoritesNode2.getIndex(favoritesNode5) + 1;
            int n5 = arrobject.length - 1;
            while (n5 >= 0) {
                FavoritesNode favoritesNode6 = ((FavoritesNode)arrobject[n5]).getDeepCopy();
                favoritesNode2.insert(favoritesNode6, n);
                this.connections.put((FavoritesNode)arrobject[n5], favoritesNode6);
                --n5;
            }
        }
    }

    public void treeNodesRemoved(TreeModelEvent treeModelEvent) {
        BasicFavoritesNavigatorUI.debug("nodes removed");
        Object[] arrobject = treeModelEvent.getChildren();
        int n = 0;
        while (n < arrobject.length) {
            FavoritesNode favoritesNode = (FavoritesNode)arrobject[n];
            FavoritesNode favoritesNode2 = (FavoritesNode)this.connections.get(favoritesNode);
            if (favoritesNode2 != null) {
                favoritesNode2.removeFromParent();
            }
            ++n;
        }
    }

    public void treeNodesChanged(TreeModelEvent treeModelEvent) {
        BasicFavoritesNavigatorUI.debug("node changed");
        TreeCellEditor treeCellEditor = this.tree.getCellEditor();
        Object object = treeCellEditor.getCellEditorValue();
        if (object instanceof String && this.selectedItem != null) {
            BasicFavoritesNavigatorUI.debug("new name");
            Map.ID iD = (Map.ID)this.dataMap.get(this.selectedItem);
            this.dataMap.remove(this.selectedItem);
            FavoritesNode favoritesNode = (FavoritesNode)this.connections.get(this.getSelectedNode());
            this.selectedItem.setName((String)object);
            this.selectedNode.setUserObject(this.selectedItem);
            if (favoritesNode != null) {
                FavoritesItem favoritesItem = (FavoritesItem)favoritesNode.getUserObject();
                favoritesItem.setName((String)object);
            }
            this.dataMap.put(this.selectedItem, iD);
            this.saveFavorites();
        }
    }

    public FavoritesNode getSelectedNode() {
        return this.selectedNode;
    }

    public Action getAddAction() {
        return this.addAction;
    }

    public Action getRemoveAction() {
        return this.removeAction;
    }

    public Action getFolderAction() {
        return this.folderAction;
    }

    public Action getCutAction() {
        return this.cutAction;
    }

    public Action getPasteAction() {
        return this.pasteAction;
    }

    public Action getCopyAction() {
        return this.copyAction;
    }

    private static void debug(String string) {
        if (debug) {
            System.out.println("BasicFavoritesNavigatorUI: " + string);
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

    static {
        try {
            Class class_ = Class.forName("javax.swing.InputVerifier");
            on1dot3 = class_ != null;
        }
        catch (ClassNotFoundException var0_1) {
            on1dot3 = false;
        }
        debug = false;
    }

    public class FavoritesTree
    extends JTree
    implements DragGestureListener,
    DropTargetListener,
    DragSourceListener {
        protected Map.ID selectedID;
        private DragSource dragSource;
        private DragSourceContext dragSourceContext;
        private Point cursorLocation;
        private TreePath pathSource;
        private BufferedImage ghostImage;
        private Point offset;
        private Point ptLast;
        private Rectangle2D ghostRect;
        private Map.ID hashCandidate;
        private Cursor dndCursor;

        public FavoritesTree(FavoritesNode favoritesNode) {
            super(favoritesNode);
            this.selectedID = null;
            this.dragSource = null;
            this.dragSourceContext = null;
            this.cursorLocation = null;
            this.offset = new Point();
            this.ptLast = new Point();
            this.ghostRect = new Rectangle2D.Float();
            this.setEditable(true);
            this.dragSource = DragSource.getDefaultDragSource();
            DragGestureRecognizer dragGestureRecognizer = this.dragSource.createDefaultDragGestureRecognizer(this, 3, this);
            dragGestureRecognizer.setSourceActions(dragGestureRecognizer.getSourceActions() & -5);
            DropTarget dropTarget = new DropTarget(this, this);
            Toolkit toolkit = this.getToolkit();
            if (toolkit.getBestCursorSize(16, 16).equals(new Dimension(64, 64))) {
                this.dndCursor = (Cursor)UIManager.get("HelpDnDCursor");
            }
            if (this.dndCursor == null) {
                BasicFavoritesNavigatorUI.debug("cursor is null");
            }
            this.putClientProperty("JTree.lineStyle", "None");
        }

        public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
            FavoritesNode favoritesNode = BasicFavoritesNavigatorUI.this.getSelectedNode();
            if (favoritesNode != null) {
                this.ghostImage = this.createGhostImage(dragGestureEvent);
                Transferable transferable = (Transferable)favoritesNode.getUserObject();
                this.hashCandidate = (Map.ID)BasicFavoritesNavigatorUI.this.dataMap.get((FavoritesItem)transferable);
                Cursor cursor = DragSource.DefaultCopyDrop;
                int n = dragGestureEvent.getDragAction();
                if (n == 2) {
                    BasicFavoritesNavigatorUI.debug("action move");
                    cursor = DragSource.DefaultMoveDrop;
                }
                this.dragSource.startDrag(dragGestureEvent, this.dndCursor, this.ghostImage, new Point(5, 5), transferable, this);
            }
        }

        private BufferedImage createGhostImage(DragGestureEvent dragGestureEvent) {
            BasicFavoritesNavigatorUI.debug("createGhostImage");
            BufferedImage bufferedImage = null;
            Point point = dragGestureEvent.getDragOrigin();
            TreePath treePath = this.getPathForLocation(point.x, point.y);
            if (treePath == null) {
                return bufferedImage;
            }
            Rectangle rectangle = this.getPathBounds(treePath);
            this.offset.setLocation(point.x - rectangle.x, point.y - rectangle.y);
            JLabel jLabel = (JLabel)this.getCellRenderer().getTreeCellRendererComponent(this, treePath.getLastPathComponent(), false, this.isExpanded(treePath), this.getModel().isLeaf(treePath.getLastPathComponent()), 0, false);
            jLabel.setSize((int)rectangle.getWidth(), (int)rectangle.getHeight());
            bufferedImage = new BufferedImage((int)rectangle.getWidth(), (int)rectangle.getHeight(), 3);
            Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.setComposite(AlphaComposite.getInstance(2, 0.5f));
            jLabel.paint(graphics2D);
            Icon icon = jLabel.getIcon();
            int n = icon == null ? 0 : icon.getIconWidth() + jLabel.getIconTextGap();
            graphics2D.setComposite(AlphaComposite.getInstance(4, 0.5f));
            graphics2D.setPaint(new GradientPaint(n, 0.0f, SystemColor.controlShadow, this.getWidth(), 0.0f, new Color(255, 255, 255, 0)));
            graphics2D.fillRect(n, 0, this.getWidth(), bufferedImage.getHeight());
            graphics2D.dispose();
            return bufferedImage;
        }

        public void dragDropEnd(DragSourceDropEvent dragSourceDropEvent) {
            BasicFavoritesNavigatorUI.debug("dragDropEnd");
        }

        public void dragEnter(DragSourceDragEvent dragSourceDragEvent) {
            BasicFavoritesNavigatorUI.debug("dragEnter");
            this.setCursor(dragSourceDragEvent);
        }

        public void dragOver(DragSourceDragEvent dragSourceDragEvent) {
            BasicFavoritesNavigatorUI.debug("drag over");
            this.setCursor(dragSourceDragEvent);
        }

        public void dropActionChanged(DragSourceDragEvent dragSourceDragEvent) {
            BasicFavoritesNavigatorUI.debug("dropActionChanged");
            this.setCursor(dragSourceDragEvent);
        }

        public void dragExit(DragSourceEvent dragSourceEvent) {
            BasicFavoritesNavigatorUI.debug("dragExit");
        }

        private void setCursor(DragSourceDragEvent dragSourceDragEvent) {
            if (this.cursorLocation == null) {
                return;
            }
            TreePath treePath = this.getPathForLocation(this.cursorLocation.x, this.cursorLocation.y);
            DragSourceContext dragSourceContext = dragSourceDragEvent.getDragSourceContext();
            if (this.testDropTarget(treePath, BasicFavoritesNavigatorUI.this.selectedTreePath) == null) {
                dragSourceContext.setCursor(DragSource.DefaultCopyDrop);
            } else {
                dragSourceContext.setCursor(DragSource.DefaultCopyNoDrop);
            }
        }

        public void drop(DropTargetDropEvent dropTargetDropEvent) {
            BasicFavoritesNavigatorUI.debug("drop");
            try {
                Object object;
                Transferable transferable = dropTargetDropEvent.getTransferable();
                if (!transferable.isDataFlavorSupported(FavoritesItem.FAVORITES_FLAVOR)) {
                    BasicFavoritesNavigatorUI.debug("drop rejected not data flavor");
                    dropTargetDropEvent.rejectDrop();
                }
                FavoritesItem favoritesItem = (FavoritesItem)transferable.getTransferData(FavoritesItem.FAVORITES_FLAVOR);
                Point point = dropTargetDropEvent.getLocation();
                TreePath treePath = this.getPathForLocation(point.x, point.y);
                String string = this.testDropTarget(treePath, BasicFavoritesNavigatorUI.this.selectedTreePath);
                if (string != null) {
                    dropTargetDropEvent.rejectDrop();
                    BasicFavoritesNavigatorUI.debug("Error : " + string);
                    return;
                }
                FavoritesNode favoritesNode = (FavoritesNode)treePath.getLastPathComponent();
                BasicFavoritesNavigatorUI.debug("new parent: " + favoritesNode);
                FavoritesNode favoritesNode2 = (FavoritesNode)BasicFavoritesNavigatorUI.this.getSelectedNode().getParent();
                FavoritesNode favoritesNode3 = BasicFavoritesNavigatorUI.this.getSelectedNode();
                FavoritesItem favoritesItem2 = (FavoritesItem)favoritesNode3.getUserObject();
                FavoritesNode favoritesNode4 = favoritesNode3.getDeepCopy();
                int n = dropTargetDropEvent.getDropAction();
                boolean bl = n == 1;
                BasicFavoritesNavigatorUI.debug("copy action: " + bl);
                FavoritesNode favoritesNode5 = new FavoritesNode(favoritesItem);
                BasicFavoritesNavigatorUI.debug("new child: " + favoritesNode5);
                try {
                    if (!bl) {
                        FavoritesNode defaultTreeModel = (FavoritesNode)BasicFavoritesNavigatorUI.this.connections.get(BasicFavoritesNavigatorUI.this.getSelectedNode());
                        if (defaultTreeModel != null) {
                            defaultTreeModel.removeFromParent();
                        }
                        favoritesNode2.remove(BasicFavoritesNavigatorUI.this.getSelectedNode());
                    }
                    DefaultTreeModel defaultTreeModel = (DefaultTreeModel)this.getModel();
                    if (!favoritesNode.getAllowsChildren()) {
                        object = favoritesNode.getParent();
                        if (object != null) {
                            int n2 = object.getIndex(favoritesNode);
                            defaultTreeModel.insertNodeInto(favoritesNode4, (DefaultMutableTreeNode)object, n2 + 1);
                        }
                    } else {
                        defaultTreeModel.insertNodeInto(favoritesNode4, favoritesNode, favoritesNode.getChildCount());
                    }
                    if (bl) {
                        dropTargetDropEvent.acceptDrop(1);
                    } else {
                        dropTargetDropEvent.acceptDrop(2);
                    }
                }
                catch (IllegalStateException var15_19) {
                    BasicFavoritesNavigatorUI.debug("drop ejected");
                    dropTargetDropEvent.rejectDrop();
                }
                dropTargetDropEvent.getDropTargetContext().dropComplete(true);
                FavoritesItem favoritesItem3 = (FavoritesItem)favoritesNode4.getUserObject();
                BasicFavoritesNavigatorUI.this.dataMap.put(favoritesItem3, this.hashCandidate);
                object = (DefaultTreeModel)this.getModel();
                object.reload(favoritesNode2);
                object.reload(favoritesNode);
                TreePath treePath2 = new TreePath(favoritesNode.getPath());
                this.expandPath(treePath2);
                BasicFavoritesNavigatorUI.this.saveFavorites();
            }
            catch (IOException var2_3) {
                dropTargetDropEvent.rejectDrop();
                BasicFavoritesNavigatorUI.debug("drop rejected" + var2_3);
            }
            catch (UnsupportedFlavorException var3_5) {
                dropTargetDropEvent.rejectDrop();
                BasicFavoritesNavigatorUI.debug("drop rejected: " + var3_5);
            }
        }

        public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
        }

        public void dragExit(DropTargetEvent dropTargetEvent) {
            if (!DragSource.isDragImageSupported()) {
                this.repaint(this.ghostRect.getBounds());
            }
        }

        public void dragOver(DropTargetDragEvent dropTargetDragEvent) {
            Point point = dropTargetDragEvent.getLocation();
            if (point.equals(this.ptLast)) {
                return;
            }
            this.ptLast = point;
            Point point2 = dropTargetDragEvent.getLocation();
            TreePath treePath = this.getPathForLocation(point2.x, point2.y);
            Graphics2D graphics2D = (Graphics2D)this.getGraphics();
            if (this.testDropTarget(treePath, BasicFavoritesNavigatorUI.this.selectedTreePath) == null) {
                dropTargetDragEvent.acceptDrag(3);
                if (!DragSource.isDragImageSupported()) {
                    this.paintImmediately(this.ghostRect.getBounds());
                    this.ghostRect.setRect(point.x - this.offset.x, point.y - this.offset.y, this.ghostImage.getWidth(), this.ghostImage.getHeight());
                    graphics2D.drawImage(this.ghostImage, AffineTransform.getTranslateInstance(this.ghostRect.getX(), this.ghostRect.getY()), null);
                }
            } else {
                dropTargetDragEvent.rejectDrag();
            }
        }

        public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {
        }

        private String testDropTarget(TreePath treePath, TreePath treePath2) {
            boolean bl;
            boolean bl2 = bl = treePath == null;
            if (bl) {
                return "Invalid drop location.";
            }
            FavoritesNode favoritesNode = (FavoritesNode)treePath.getLastPathComponent();
            if (treePath.equals(treePath2)) {
                return "Destination cannot be same as source";
            }
            if (treePath2.isDescendant(treePath)) {
                return "Destination node cannot be a descendant.";
            }
            if (treePath2.getParentPath().equals(treePath)) {
                return "Destination node cannot be a parent.";
            }
            return null;
        }
    }

    public class PopupListener
    extends MouseAdapter {
        public void mousePressed(MouseEvent mouseEvent) {
            this.maybeShowPopup(mouseEvent);
        }

        public void mouseReleased(MouseEvent mouseEvent) {
            this.maybeShowPopup(mouseEvent);
        }

        private void maybeShowPopup(MouseEvent mouseEvent) {
            TreePath treePath = BasicFavoritesNavigatorUI.this.tree.getSelectionPath();
            TreePath treePath2 = BasicFavoritesNavigatorUI.this.tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
            if (mouseEvent.isPopupTrigger()) {
                if (treePath != null && treePath.equals(treePath2)) {
                    BasicFavoritesNavigatorUI.this.separatorMI.setVisible(true);
                    BasicFavoritesNavigatorUI.this.cutMI.setVisible(true);
                    BasicFavoritesNavigatorUI.this.copyMI.setVisible(true);
                    BasicFavoritesNavigatorUI.this.pasteMI.setVisible(true);
                    BasicFavoritesNavigatorUI.this.removeMI.setVisible(true);
                } else {
                    BasicFavoritesNavigatorUI.this.separatorMI.setVisible(false);
                    BasicFavoritesNavigatorUI.this.cutMI.setVisible(false);
                    BasicFavoritesNavigatorUI.this.copyMI.setVisible(false);
                    BasicFavoritesNavigatorUI.this.pasteMI.setVisible(false);
                    BasicFavoritesNavigatorUI.this.removeMI.setVisible(false);
                }
                BasicFavoritesNavigatorUI.this.popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }
        }
    }

    public class CopyAction
    extends AbstractAction {
        public CopyAction() {
            super(HelpUtilities.getString(BasicFavoritesNavigatorUI.this.locale, "favorites.copy"));
        }

        public void actionPerformed(ActionEvent actionEvent) {
            BasicFavoritesNavigatorUI.debug("paste");
            DefaultMutableTreeNode defaultMutableTreeNode = null;
            BasicFavoritesNavigatorUI.this.nodeClipboard.removeAllElements();
            DefaultTreeModel defaultTreeModel = (DefaultTreeModel)BasicFavoritesNavigatorUI.this.tree.getModel();
            TreePath[] arrtreePath = BasicFavoritesNavigatorUI.this.tree.getSelectionPaths();
            int n = 0;
            while (n < arrtreePath.length) {
                if (arrtreePath[n] != null && (defaultMutableTreeNode = (DefaultMutableTreeNode)arrtreePath[n].getLastPathComponent()) != null) {
                    FavoritesItem favoritesItem = (FavoritesItem)defaultMutableTreeNode.getUserObject();
                    FavoritesNode favoritesNode = ((FavoritesNode)defaultMutableTreeNode).getDeepCopy();
                    BasicFavoritesNavigatorUI.this.nodeClipboard.add(favoritesNode);
                }
                ++n;
            }
            BasicFavoritesNavigatorUI.this.saveFavorites();
            BasicFavoritesNavigatorUI.this.pasteMI.setEnabled(true);
        }
    }

    public class PasteAction
    extends AbstractAction {
        public PasteAction() {
            super(HelpUtilities.getString(BasicFavoritesNavigatorUI.this.locale, "favorites.paste"));
        }

        public void actionPerformed(ActionEvent actionEvent) {
            BasicFavoritesNavigatorUI.debug("paste");
            DefaultTreeModel defaultTreeModel = (DefaultTreeModel)BasicFavoritesNavigatorUI.this.tree.getModel();
            TreePath treePath = BasicFavoritesNavigatorUI.this.tree.getSelectionPath();
            FavoritesNode favoritesNode = (FavoritesNode)treePath.getLastPathComponent();
            if (favoritesNode != null) {
                if (favoritesNode.getAllowsChildren()) {
                    Enumeration enumeration = BasicFavoritesNavigatorUI.this.nodeClipboard.elements();
                    while (enumeration.hasMoreElements()) {
                        defaultTreeModel.insertNodeInto((DefaultMutableTreeNode)enumeration.nextElement(), favoritesNode, favoritesNode.getChildCount());
                    }
                } else {
                    DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)favoritesNode.getParent();
                    if (defaultMutableTreeNode == null) {
                        return;
                    }
                    int n = defaultMutableTreeNode.getIndex(favoritesNode);
                    Enumeration enumeration = BasicFavoritesNavigatorUI.this.nodeClipboard.elements();
                    while (enumeration.hasMoreElements()) {
                        defaultTreeModel.insertNodeInto((DefaultMutableTreeNode)enumeration.nextElement(), defaultMutableTreeNode, n);
                        ++n;
                    }
                }
                BasicFavoritesNavigatorUI.this.saveFavorites();
            }
        }
    }

    public class CutAction
    extends AbstractAction {
        public CutAction() {
            super(HelpUtilities.getString(BasicFavoritesNavigatorUI.this.locale, "favorites.cut"));
        }

        public void actionPerformed(ActionEvent actionEvent) {
            BasicFavoritesNavigatorUI.debug("cut");
            DefaultMutableTreeNode defaultMutableTreeNode = null;
            BasicFavoritesNavigatorUI.this.nodeClipboard.removeAllElements();
            DefaultTreeModel defaultTreeModel = (DefaultTreeModel)BasicFavoritesNavigatorUI.this.tree.getModel();
            TreePath[] arrtreePath = BasicFavoritesNavigatorUI.this.tree.getSelectionPaths();
            int n = 0;
            while (n < arrtreePath.length) {
                if (arrtreePath[n] != null) {
                    defaultMutableTreeNode = (DefaultMutableTreeNode)arrtreePath[n].getLastPathComponent();
                    if (defaultMutableTreeNode != null) {
                        FavoritesItem favoritesItem = (FavoritesItem)defaultMutableTreeNode.getUserObject();
                        BasicFavoritesNavigatorUI.this.nodeClipboard.add(defaultMutableTreeNode);
                    }
                    defaultTreeModel.removeNodeFromParent(defaultMutableTreeNode);
                }
                ++n;
            }
            BasicFavoritesNavigatorUI.this.saveFavorites();
            BasicFavoritesNavigatorUI.this.pasteMI.setEnabled(true);
        }
    }

    public class FolderAction
    extends AbstractAction {
        private final /* synthetic */ BasicFavoritesNavigatorUI this$0;

        public FolderAction(BasicFavoritesNavigatorUI basicFavoritesNavigatorUI) {
            Class class_ = BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI == null ? (BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI = BasicFavoritesNavigatorUI.class$("javax.help.plaf.basic.BasicFavoritesNavigatorUI")) : BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI;
            super(HelpUtilities.getString(basicFavoritesNavigatorUI.locale, "favorites.folder"), SwingHelpUtilities.getImageIcon(class_, "images/folder.gif"));
            this.this$0 = basicFavoritesNavigatorUI;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            FavoritesItem favoritesItem = new FavoritesItem(HelpUtilities.getString(this.this$0.locale, "favorites.folder"));
            favoritesItem.setAsFolder();
            FavoritesNode favoritesNode2 = new FavoritesNode(favoritesItem);
            TreePath treePath = this.this$0.tree.getSelectionPath();
            TreeNode treeNode = null;
            if (treePath == null) {
                treeNode = this.this$0.topNode;
            } else {
                FavoritesNode favoritesNode = (FavoritesNode)treePath.getLastPathComponent();
                treeNode = favoritesNode.getParent();
            }
            DefaultTreeModel defaultTreeModel = (DefaultTreeModel)this.this$0.tree.getModel();
            defaultTreeModel.insertNodeInto(favoritesNode2, (DefaultMutableTreeNode)treeNode, treeNode.getChildCount());
            TreePath treePath2 = new TreePath(favoritesNode2.getPath());
            this.this$0.tree.expandPath(treePath2);
            this.this$0.tree.setSelectionPath(treePath2);
            this.this$0.tree.scrollPathToVisible(treePath2);
            this.this$0.saveFavorites();
        }
    }

    public class RemoveAction
    extends AbstractAction {
        private final /* synthetic */ BasicFavoritesNavigatorUI this$0;

        public RemoveAction(BasicFavoritesNavigatorUI basicFavoritesNavigatorUI) {
            Class class_ = BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI == null ? (BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI = BasicFavoritesNavigatorUI.class$("javax.help.plaf.basic.BasicFavoritesNavigatorUI")) : BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI;
            super(HelpUtilities.getString(basicFavoritesNavigatorUI.locale, "favorites.remove"), SwingHelpUtilities.getImageIcon(class_, "images/remove.gif"));
            this.this$0 = basicFavoritesNavigatorUI;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            BasicFavoritesNavigatorUI.debug("remove");
            DefaultMutableTreeNode defaultMutableTreeNode = null;
            DefaultTreeModel defaultTreeModel = (DefaultTreeModel)this.this$0.tree.getModel();
            TreePath[] arrtreePath = this.this$0.tree.getSelectionPaths();
            int n = 0;
            while (n < arrtreePath.length) {
                if (arrtreePath[n] != null) {
                    defaultMutableTreeNode = (DefaultMutableTreeNode)arrtreePath[n].getLastPathComponent();
                    defaultTreeModel.removeNodeFromParent(defaultMutableTreeNode);
                    if (defaultMutableTreeNode != null) {
                        FavoritesItem favoritesItem = (FavoritesItem)defaultMutableTreeNode.getUserObject();
                        this.this$0.dataMap.remove(favoritesItem);
                    }
                }
                this.this$0.saveFavorites();
                ++n;
            }
        }
    }

    public class AddAction
    extends AbstractAction {
        private final /* synthetic */ BasicFavoritesNavigatorUI this$0;

        public AddAction(BasicFavoritesNavigatorUI basicFavoritesNavigatorUI) {
            Class class_ = BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI == null ? (BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI = BasicFavoritesNavigatorUI.class$("javax.help.plaf.basic.BasicFavoritesNavigatorUI")) : BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI;
            super(HelpUtilities.getString(basicFavoritesNavigatorUI.locale, "favorites.add"), SwingHelpUtilities.getImageIcon(class_, "images/addToFav.gif"));
            this.this$0 = basicFavoritesNavigatorUI;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            BasicFavoritesNavigatorUI.debug("add");
            String string = null;
            String string2 = null;
            FavoritesItem favoritesItem = null;
            HelpModel helpModel = this.this$0.favorites.getModel();
            HelpSet helpSet = helpModel.getHelpSet();
            Map.ID iD = helpModel.getCurrentID();
            URL uRL = helpModel.getCurrentURL();
            if (iD != null) {
                string = iD.id;
                string2 = iD.hs.getTitle();
            }
            if (string2 == null && (string2 = this.getHelpSetTitle(helpSet, uRL)) == null) {
                string2 = helpSet.getTitle();
            }
            String string3 = null;
            if (string == null) {
                string3 = uRL.toExternalForm();
            }
            favoritesItem = new FavoritesItem(this.this$0.contentTitle, string, string3, string2, Locale.getDefault());
            this.this$0.dataMap.put(favoritesItem, iD);
            FavoritesNode favoritesNode = new FavoritesNode(favoritesItem);
            DefaultTreeModel defaultTreeModel = (DefaultTreeModel)this.this$0.tree.getModel();
            defaultTreeModel.insertNodeInto(favoritesNode, this.this$0.topNode, this.this$0.topNode.getChildCount());
            TreePath treePath = new TreePath(favoritesNode.getPath());
            this.this$0.tree.expandPath(treePath);
            this.this$0.tree.setSelectionPath(treePath);
            this.this$0.tree.scrollPathToVisible(treePath);
            this.this$0.saveFavorites();
        }

        private String getHelpSetTitle(HelpSet helpSet, URL uRL) {
            String string;
            URL uRL2 = helpSet.getHelpSetURL();
            String string2 = uRL.toExternalForm();
            if (string2.startsWith(string = uRL2.toExternalForm())) {
                return helpSet.getTitle();
            }
            Enumeration enumeration = helpSet.getHelpSets();
            String string3 = null;
            while (enumeration.hasMoreElements()) {
                HelpSet helpSet2 = (HelpSet)enumeration.nextElement();
                string3 = this.getHelpSetTitle(helpSet2, uRL);
                if (string3 != null) break;
            }
            return string3;
        }
    }

}

