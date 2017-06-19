/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.help.DefaultHelpBroker;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.Map;
import javax.help.Popup;
import javax.help.Presentation;
import javax.help.SwingHelpUtilities;
import javax.help.WindowPresentation;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class CSH {
    private static java.util.Map comps;
    private static java.util.Map parents;
    private static Vector managers;
    private static final boolean debug = false;
    static /* synthetic */ Class class$javax$help$HelpSet;
    static /* synthetic */ Class class$java$lang$String;

    public static void addManager(Manager manager) {
        managers.add(manager);
    }

    public static void addManager(int n, Manager manager) {
        managers.add(n, manager);
    }

    public static boolean removeManager(Manager manager) {
        return managers.remove(manager);
    }

    public static void removeManager(int n) {
        managers.remove(n);
    }

    public static void removeAllManagers() {
        managers.clear();
    }

    public static Manager getManager(int n) {
        return (Manager)managers.get(n);
    }

    public static Manager[] getManagers() {
        return managers.toArray(new Manager[0]);
    }

    public static int getManagerCount() {
        return managers.size();
    }

    private static void _setHelpIDString(Object object, String string) {
        if (object instanceof JComponent) {
            ((JComponent)object).putClientProperty("HelpID", string);
        } else if (object instanceof Component || object instanceof MenuItem) {
            Hashtable<String, String> hashtable;
            if (comps == null) {
                comps = new WeakHashMap(5);
            }
            if ((hashtable = (Hashtable<String, String>)comps.get(object)) != null) {
                if (string != null) {
                    hashtable.put("HelpID", string);
                } else {
                    hashtable.remove("HelpID");
                    if (hashtable.isEmpty()) {
                        comps.remove(object);
                    }
                }
            } else if (string != null) {
                hashtable = new Hashtable<String, String>(2);
                hashtable.put("HelpID", string);
                comps.put(object, hashtable);
            }
        } else {
            throw new IllegalArgumentException("Invalid Component");
        }
    }

    private static String _getHelpIDString(Object object) {
        String string = null;
        if (object != null) {
            if (object instanceof JComponent) {
                string = (String)((JComponent)object).getClientProperty("HelpID");
            } else if (object instanceof Component || object instanceof MenuItem) {
                Hashtable hashtable;
                if (comps != null && (hashtable = (Hashtable)comps.get(object)) != null) {
                    string = (String)hashtable.get("HelpID");
                }
            } else {
                throw new IllegalArgumentException("Invalid Component");
            }
        }
        return string;
    }

    private static String _getHelpIDString(Object object, AWTEvent aWTEvent) {
        String string = null;
        if (object != null) {
            Manager[] arrmanager = CSH.getManagers();
            int n = 0;
            while (n < arrmanager.length) {
                string = arrmanager[n].getHelpIDString(object, aWTEvent);
                if (string != null) {
                    return string;
                }
                ++n;
            }
        }
        return null;
    }

    private static Object getParent(Object object) {
        if (object == null) {
            return null;
        }
        MenuContainer menuContainer = null;
        if (object instanceof MenuComponent) {
            menuContainer = ((MenuComponent)object).getParent();
        } else if (object instanceof JPopupMenu) {
            menuContainer = ((JPopupMenu)object).getInvoker();
        } else if (object instanceof Component) {
            menuContainer = ((Component)object).getParent();
        } else {
            throw new IllegalArgumentException("Invalid Component");
        }
        if (menuContainer == null && parents != null) {
            menuContainer = parents.get(object);
        }
        return menuContainer;
    }

    public static void setHelpIDString(Component component, String string) {
        CSH._setHelpIDString(component, string);
    }

    public static void setHelpIDString(MenuItem menuItem, String string) {
        CSH._setHelpIDString(menuItem, string);
    }

    public static String getHelpIDString(Object object, AWTEvent aWTEvent) {
        if (object == null) {
            return null;
        }
        String string = CSH._getHelpIDString(object, aWTEvent);
        if (string == null) {
            string = CSH._getHelpIDString(object);
        }
        if (string == null) {
            string = CSH.getHelpIDString(CSH.getParent(object), aWTEvent);
        }
        return string;
    }

    public static String getHelpIDString(Component component) {
        return CSH.getHelpIDString(component, null);
    }

    public static String getHelpIDString(MenuItem menuItem) {
        return CSH.getHelpIDString(menuItem, null);
    }

    private static void _setHelpSet(Object object, HelpSet helpSet) {
        if (object instanceof JComponent) {
            ((JComponent)object).putClientProperty("HelpSet", helpSet);
        } else if (object instanceof Component || object instanceof MenuItem) {
            Hashtable<String, HelpSet> hashtable;
            if (comps == null) {
                comps = new WeakHashMap(5);
            }
            if ((hashtable = (Hashtable<String, HelpSet>)comps.get(object)) != null) {
                if (helpSet != null) {
                    hashtable.put("HelpSet", helpSet);
                } else {
                    hashtable.remove("HelpSet");
                    if (hashtable.isEmpty()) {
                        comps.remove(object);
                    }
                }
            } else if (helpSet != null) {
                hashtable = new Hashtable<String, HelpSet>(2);
                hashtable.put("HelpSet", helpSet);
                comps.put(object, hashtable);
            }
        } else {
            throw new IllegalArgumentException("Invalid Component");
        }
    }

    private static HelpSet _getHelpSet(Object object) {
        HelpSet helpSet = null;
        if (object != null) {
            if (object instanceof JComponent) {
                helpSet = (HelpSet)((JComponent)object).getClientProperty("HelpSet");
            } else if (object instanceof Component || object instanceof MenuItem) {
                Hashtable hashtable;
                if (comps != null && (hashtable = (Hashtable)comps.get(object)) != null) {
                    helpSet = (HelpSet)hashtable.get("HelpSet");
                }
            } else {
                throw new IllegalArgumentException("Invalid Component");
            }
        }
        return helpSet;
    }

    private static HelpSet _getHelpSet(Object object, AWTEvent aWTEvent) {
        HelpSet helpSet = null;
        if (object != null) {
            Manager[] arrmanager = CSH.getManagers();
            int n = 0;
            while (n < arrmanager.length) {
                helpSet = arrmanager[n].getHelpSet(object, aWTEvent);
                if (helpSet != null) {
                    return helpSet;
                }
                ++n;
            }
        }
        return helpSet;
    }

    public static void setHelpSet(Component component, HelpSet helpSet) {
        CSH._setHelpSet(component, helpSet);
    }

    public static void setHelpSet(MenuItem menuItem, HelpSet helpSet) {
        CSH._setHelpSet(menuItem, helpSet);
    }

    public static HelpSet getHelpSet(Object object, AWTEvent aWTEvent) {
        if (object == null) {
            return null;
        }
        String string = CSH._getHelpIDString(object, aWTEvent);
        if (string == null) {
            string = CSH._getHelpIDString(object);
        }
        if (string != null) {
            HelpSet helpSet = CSH._getHelpSet(object, aWTEvent);
            if (helpSet == null) {
                helpSet = CSH._getHelpSet(object);
            }
            return helpSet;
        }
        return CSH.getHelpSet(CSH.getParent(object), aWTEvent);
    }

    public static HelpSet getHelpSet(Component component) {
        return CSH.getHelpSet(component, null);
    }

    public static HelpSet getHelpSet(MenuItem menuItem) {
        return CSH.getHelpSet(menuItem, null);
    }

    public static Object trackCSEvents() {
        MouseEvent mouseEvent = CSH.getMouseEvent();
        if (mouseEvent != null) {
            return CSH.getDeepestObjectAt(mouseEvent.getSource(), mouseEvent.getX(), mouseEvent.getY());
        }
        return null;
    }

    private static void displayHelp(HelpBroker helpBroker, HelpSet helpSet, String string, String string2, Object object, Object object2, AWTEvent aWTEvent) {
        Object object4;
        Object object3;
        Presentation presentation = null;
        if (helpBroker != null) {
            if (helpBroker instanceof DefaultHelpBroker) {
                ((DefaultHelpBroker)helpBroker).setActivationObject(object2);
            }
        } else {
            Class[] arrclass = new Class[2];
            Class class_ = class$javax$help$HelpSet == null ? (CSH.class$javax$help$HelpSet = CSH.class$("javax.help.HelpSet")) : class$javax$help$HelpSet;
            arrclass[0] = class_;
            Class class_2 = class$java$lang$String == null ? (CSH.class$java$lang$String = CSH.class$("java.lang.String")) : class$java$lang$String;
            arrclass[1] = class_2;
            object3 = arrclass;
            Object[] arrobject = new Object[]{helpSet, string2};
            try {
                void var9_14;
                object4 = helpSet.getLoader();
                if (object4 == null) {
                    Class helpSet2 = Class.forName(string);
                } else {
                    Class class_3 = object4.loadClass(string);
                }
                Method method = var9_14.getMethod("getPresentation", object3);
                presentation = (Presentation)method.invoke(null, arrobject);
            }
            catch (Exception var12_21) {
                throw new RuntimeException("error invoking presentation");
            }
            if (presentation == null) {
                return;
            }
            if (presentation instanceof WindowPresentation) {
                ((WindowPresentation)presentation).setActivationObject(object2);
            }
            if (presentation instanceof Popup && object instanceof Component) {
                ((Popup)presentation).setInvoker((Component)object);
            }
        }
        object4 = null;
        Object var9_15 = null;
        object4 = CSH.getHelpIDString(object, aWTEvent);
        HelpSet helpSet2 = CSH.getHelpSet(object, aWTEvent);
        if (helpSet2 == null) {
            if (helpBroker != null) {
                HelpSet helpSet3 = helpBroker.getHelpSet();
            } else {
                HelpSet helpSet4 = helpSet;
            }
        }
        try {
            void var9_19;
            object3 = Map.ID.create((String)object4, (HelpSet)var9_19);
            if (object3 == null) {
                object3 = var9_19.getHomeID();
            }
            if (helpBroker != null) {
                helpBroker.setCurrentID((Map.ID)object3);
                helpBroker.setDisplayed(true);
            } else {
                presentation.setCurrentID((Map.ID)object3);
                presentation.setDisplayed(true);
            }
        }
        catch (Exception var10_9) {
            var10_9.printStackTrace();
        }
    }

    private static MouseEvent getMouseEvent() {
        block14 : {
            try {
                if (!EventQueue.isDispatchThread()) break block14;
                EventQueue eventQueue = null;
                try {
                    eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
                }
                catch (Exception var1_2) {
                    CSH.debug(var1_2);
                }
                if (eventQueue == null) {
                    return null;
                }
                int n = -1;
                do {
                    ++n;
                    AWTEvent aWTEvent = eventQueue.getNextEvent();
                    Object object = aWTEvent.getSource();
                    if (aWTEvent instanceof ActiveEvent) {
                        ((ActiveEvent)((Object)aWTEvent)).dispatch();
                        continue;
                    }
                    if (object instanceof Component) {
                        AWTEvent aWTEvent2;
                        if (aWTEvent instanceof KeyEvent) {
                            aWTEvent2 = (KeyEvent)aWTEvent;
                            if (aWTEvent2.getKeyCode() == 3 || aWTEvent2.getKeyCode() == 27) {
                                aWTEvent2.consume();
                                return null;
                            }
                            aWTEvent2.consume();
                            continue;
                        }
                        if (aWTEvent instanceof MouseEvent) {
                            aWTEvent2 = (MouseEvent)aWTEvent;
                            int n2 = aWTEvent2.getID();
                            if ((n2 == 500 || n2 == 501 || n2 == 502) && SwingUtilities.isLeftMouseButton((MouseEvent)aWTEvent2)) {
                                if (n2 == 500 && n == 0) {
                                    CSH.dispatchEvent(aWTEvent);
                                    continue;
                                }
                                aWTEvent2.consume();
                                return aWTEvent2;
                            }
                            aWTEvent2.consume();
                            continue;
                        }
                        CSH.dispatchEvent(aWTEvent);
                        continue;
                    }
                    if (object instanceof MenuComponent) {
                        if (!(aWTEvent instanceof InputEvent)) continue;
                        ((InputEvent)aWTEvent).consume();
                        continue;
                    }
                    System.err.println("unable to dispatch event: " + aWTEvent);
                } while (true);
            }
            catch (InterruptedException var0_1) {
                CSH.debug(var0_1);
            }
        }
        CSH.debug("Fall Through code");
        return null;
    }

    private static void dispatchEvent(AWTEvent aWTEvent) {
        Object object = aWTEvent.getSource();
        if (aWTEvent instanceof ActiveEvent) {
            ((ActiveEvent)((Object)aWTEvent)).dispatch();
        } else if (object instanceof Component) {
            ((Component)object).dispatchEvent(aWTEvent);
        } else if (object instanceof MenuComponent) {
            ((MenuComponent)object).dispatchEvent(aWTEvent);
        } else {
            System.err.println("unable to dispatch event: " + aWTEvent);
        }
    }

    private static Object getDeepestObjectAt(Object object, int n, int n2) {
        Container container;
        Component component;
        if (object instanceof Container && (component = CSH.findComponentAt(container = (Container)object, container.getWidth(), container.getHeight(), n, n2)) != null && component != container) {
            if (component instanceof JRootPane) {
                JLayeredPane jLayeredPane = ((JRootPane)component).getLayeredPane();
                Rectangle rectangle = jLayeredPane.getBounds();
                component = (Component)CSH.getDeepestObjectAt(jLayeredPane, n - rectangle.x, n2 - rectangle.y);
            }
            if (component != null) {
                return component;
            }
        }
        return object;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Component findComponentAt(Container container, int n, int n2, int n3, int n4) {
        Object object = container.getTreeLock();
        synchronized (object) {
            Serializable serializable;
            Component component;
            if (n3 < 0) return null;
            if (n3 >= n) return null;
            if (n4 < 0) return null;
            if (n4 >= n2) return null;
            if (!container.isVisible()) return null;
            if (!container.isEnabled()) {
                return null;
            }
            Component[] arrcomponent = container.getComponents();
            int n5 = container.getComponentCount();
            int n6 = 0;
            while (n6 < n5) {
                Component component2 = arrcomponent[n6];
                component = null;
                if (component2 instanceof CellRendererPane && (serializable = CSH.getComponentAt((CellRendererPane)component2, n3, n4)) != null) {
                    component = CSH.getRectangleAt((CellRendererPane)component2, n3, n4);
                    component2 = serializable;
                }
                if (component2 != null && !component2.isLightweight()) {
                    if (component == null || component.width == 0 || component.height == 0) {
                        component = component2.getBounds();
                    }
                    component2 = component2 instanceof Container ? CSH.findComponentAt((Container)component2, component.width, component.height, n3 - component.x, n4 - component.y) : component2.getComponentAt(n3 - component.x, n4 - component.y);
                    if (component2 != null && component2.isVisible() && component2.isEnabled()) {
                        return component2;
                    }
                }
                ++n6;
            }
            int n7 = 0;
            while (n7 < n5) {
                Component component3;
                component = arrcomponent[n7];
                serializable = null;
                if (component instanceof CellRendererPane && (component3 = CSH.getComponentAt((CellRendererPane)component, n3, n4)) != null) {
                    serializable = CSH.getRectangleAt((CellRendererPane)component, n3, n4);
                    component = component3;
                }
                if (component != null && component.isLightweight()) {
                    if (serializable == null || serializable.width == 0 || serializable.height == 0) {
                        serializable = component.getBounds();
                    }
                    component = component instanceof Container ? CSH.findComponentAt((Container)component, serializable.width, serializable.height, n3 - serializable.x, n4 - serializable.y) : component.getComponentAt(n3 - serializable.x, n4 - serializable.y);
                    if (component != null && component.isVisible() && component.isEnabled()) {
                        return component;
                    }
                }
                ++n7;
            }
            return container;
        }
    }

    private static Rectangle getRectangleAt(CellRendererPane cellRendererPane, int n, int n2) {
        Rectangle rectangle = null;
        Container container = cellRendererPane.getParent();
        if (container instanceof JTable) {
            rectangle = CSH.getRectangleAt((JTable)container, n, n2);
        } else if (container instanceof JTableHeader) {
            rectangle = CSH.getRectangleAt((JTableHeader)container, n, n2);
        } else if (container instanceof JTree) {
            rectangle = CSH.getRectangleAt((JTree)container, n, n2);
        } else if (container instanceof JList) {
            rectangle = CSH.getRectangleAt((JList)container, n, n2);
        }
        return rectangle;
    }

    private static Component getComponentAt(CellRendererPane cellRendererPane, int n, int n2) {
        Component component = null;
        Container container = cellRendererPane.getParent();
        if (container instanceof JTable) {
            component = CSH.getComponentAt((JTable)container, n, n2);
        } else if (container instanceof JTableHeader) {
            component = CSH.getComponentAt((JTableHeader)container, n, n2);
        } else if (container instanceof JTree) {
            component = CSH.getComponentAt((JTree)container, n, n2);
        } else if (container instanceof JList) {
            component = CSH.getComponentAt((JList)container, n, n2);
        }
        if (component != null) {
            if (parents == null) {
                parents = new WeakHashMap(4){

                    public Object put(Object object, Object object2) {
                        return super.put(object, new WeakReference<Object>(object2));
                    }

                    public Object get(Object object) {
                        WeakReference weakReference = (WeakReference)super.get(object);
                        if (weakReference != null) {
                            return weakReference.get();
                        }
                        return null;
                    }
                };
            }
            parents.put(component, cellRendererPane);
        }
        return component;
    }

    private static Rectangle getRectangleAt(JTableHeader jTableHeader, int n, int n2) {
        Rectangle rectangle = null;
        try {
            int n3 = jTableHeader.columnAtPoint(new Point(n, n2));
            rectangle = jTableHeader.getHeaderRect(n3);
        }
        catch (Exception var4_5) {
            // empty catch block
        }
        return rectangle;
    }

    private static Component getComponentAt(JTableHeader jTableHeader, int n, int n2) {
        try {
            int n3;
            if (!(jTableHeader.contains(n, n2) && jTableHeader.isVisible() && jTableHeader.isEnabled())) {
                return null;
            }
            TableColumnModel tableColumnModel = jTableHeader.getColumnModel();
            TableColumn tableColumn = tableColumnModel.getColumn(n3 = tableColumnModel.getColumnIndexAtX(n));
            TableCellRenderer tableCellRenderer = tableColumn.getHeaderRenderer();
            if (tableCellRenderer == null) {
                tableCellRenderer = jTableHeader.getDefaultRenderer();
            }
            return tableCellRenderer.getTableCellRendererComponent(jTableHeader.getTable(), tableColumn.getHeaderValue(), false, false, -1, n3);
        }
        catch (Exception var3_4) {
            return null;
        }
    }

    private static Rectangle getRectangleAt(JTable jTable, int n, int n2) {
        Rectangle rectangle = null;
        try {
            Point point = new Point(n, n2);
            int n3 = jTable.rowAtPoint(point);
            int n4 = jTable.columnAtPoint(point);
            rectangle = jTable.getCellRect(n3, n4, true);
        }
        catch (Exception var4_5) {
            // empty catch block
        }
        return rectangle;
    }

    private static Component getComponentAt(JTable jTable, int n, int n2) {
        try {
            if (!(jTable.contains(n, n2) && jTable.isVisible() && jTable.isEnabled())) {
                return null;
            }
            Point point = new Point(n, n2);
            int n3 = jTable.rowAtPoint(point);
            int n4 = jTable.columnAtPoint(point);
            if (jTable.isEditing() && jTable.getEditingRow() == n3 && jTable.getEditingColumn() == n4) {
                return null;
            }
            TableCellRenderer tableCellRenderer = jTable.getCellRenderer(n3, n4);
            return jTable.prepareRenderer(tableCellRenderer, n3, n4);
        }
        catch (Exception var3_4) {
            return null;
        }
    }

    private static Rectangle getRectangleAt(JTree jTree, int n, int n2) {
        Rectangle rectangle = null;
        try {
            TreePath treePath = jTree.getPathForLocation(n, n2);
            rectangle = jTree.getPathBounds(treePath);
        }
        catch (Exception var4_5) {
            // empty catch block
        }
        return rectangle;
    }

    private static Component getComponentAt(JTree jTree, int n, int n2) {
        try {
            TreePath treePath = jTree.getPathForLocation(n, n2);
            if (jTree.isEditing() && jTree.getSelectionPath() == treePath) {
                return null;
            }
            int n3 = jTree.getRowForPath(treePath);
            Object object = treePath.getLastPathComponent();
            boolean bl = jTree.isRowSelected(n3);
            boolean bl2 = jTree.isExpanded(treePath);
            boolean bl3 = jTree.getModel().isLeaf(object);
            boolean bl4 = jTree.hasFocus() && jTree.getLeadSelectionRow() == n3;
            return jTree.getCellRenderer().getTreeCellRendererComponent(jTree, object, bl, bl2, bl3, n3, bl4);
        }
        catch (Exception var3_4) {
            return null;
        }
    }

    private static Rectangle getRectangleAt(JList jList, int n, int n2) {
        Rectangle rectangle = null;
        try {
            int n3 = jList.locationToIndex(new Point(n, n2));
            rectangle = jList.getCellBounds(n3, n3);
        }
        catch (Exception var4_5) {
            // empty catch block
        }
        return rectangle;
    }

    private static Component getComponentAt(JList jList, int n, int n2) {
        try {
            int n3 = jList.locationToIndex(new Point(n, n2));
            Object e = jList.getModel().getElementAt(n3);
            boolean bl = jList.isSelectedIndex(n3);
            boolean bl2 = jList.hasFocus() && jList.getLeadSelectionIndex() == n3;
            return jList.getCellRenderer().getListCellRendererComponent(jList, e, n3, bl, bl2);
        }
        catch (Exception var3_4) {
            return null;
        }
    }

    private static JPopupMenu getRootPopupMenu(JPopupMenu jPopupMenu) {
        while (jPopupMenu != null && jPopupMenu.getInvoker() instanceof JMenu && jPopupMenu.getInvoker().getParent() instanceof JPopupMenu) {
            jPopupMenu = (JPopupMenu)jPopupMenu.getInvoker().getParent();
        }
        return jPopupMenu;
    }

    private static Component findFocusOwner(JPopupMenu jPopupMenu) {
        if (jPopupMenu == null) {
            return null;
        }
        Object object = jPopupMenu.getTreeLock();
        synchronized (object) {
            block12 : {
                Component component;
                block11 : {
                    if (jPopupMenu.isVisible()) break block11;
                    Component component2 = null;
                    return component2;
                }
                Component component3 = null;
                int n = 0;
                int n2 = jPopupMenu.getComponentCount();
                while (n < n2) {
                    component = jPopupMenu.getComponent(n);
                    if (component.hasFocus()) {
                        component3 = component;
                        break;
                    }
                    if (component instanceof JMenu && ((JMenu)component).isPopupMenuVisible()) {
                        component3 = component;
                    }
                    if (component instanceof JMenuItem && ((JMenuItem)component).isArmed()) {
                        component3 = component;
                    }
                    ++n;
                }
                if (component3 instanceof JMenu) {
                    component3 = CSH.findFocusOwner(((JMenu)component3).getPopupMenu());
                }
                if (component3 == null) break block12;
                component = component3;
                return component;
            }
        }
        return jPopupMenu;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Component findFocusOwner(Component component) {
        Object object = component.getTreeLock();
        synchronized (object) {
            if (component instanceof JPopupMenu) {
                return CSH.findFocusOwner(CSH.getRootPopupMenu((JPopupMenu)component));
            }
            if (component.hasFocus()) {
                return component;
            }
            if (!(component instanceof Container)) return null;
            int n = 0;
            int n2 = ((Container)component).getComponentCount();
            while (n < n2) {
                Component component2 = CSH.findFocusOwner(((Container)component).getComponent(n));
                if (component2 != null) {
                    return component2;
                }
                ++n;
            }
            return null;
        }
    }

    private static void debug(Object object) {
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
        managers = new Vector();
    }

    public static interface Manager {
        public HelpSet getHelpSet(Object var1, AWTEvent var2);

        public String getHelpIDString(Object var1, AWTEvent var2);
    }

    public static class DisplayHelpFromSource
    implements ActionListener {
        private HelpBroker hb;
        private HelpSet hs = null;
        private String presentation = null;
        private String presentationName = null;

        public DisplayHelpFromSource(HelpBroker helpBroker) {
            if (helpBroker == null) {
                throw new NullPointerException("hb");
            }
            this.hb = helpBroker;
        }

        public DisplayHelpFromSource(HelpSet helpSet, String string, String string2) {
            if (helpSet == null) {
                throw new NullPointerException("hs");
            }
            try {
                ClassLoader classLoader = helpSet.getLoader();
                if (classLoader == null) {
                    Class class_ = Class.forName(string);
                } else {
                    Class class_ = classLoader.loadClass(string);
                }
            }
            catch (Exception var6_7) {
                throw new IllegalArgumentException(string + "presentation  invalid");
            }
            this.presentation = string;
            this.presentationName = string2;
            this.hs = helpSet;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            Object object = actionEvent.getSource();
            CSH.displayHelp(this.hb, this.hs, this.presentation, this.presentationName, object, object, actionEvent);
        }
    }

    public static class DisplayHelpAfterTracking
    implements ActionListener {
        private HelpBroker hb = null;
        private HelpSet hs = null;
        private String presentation = null;
        private String presentationName = null;
        private Hashtable cursors;

        public DisplayHelpAfterTracking(HelpBroker helpBroker) {
            if (helpBroker == null) {
                throw new NullPointerException("hb");
            }
            this.hb = helpBroker;
        }

        public DisplayHelpAfterTracking(HelpSet helpSet, String string, String string2) {
            if (helpSet == null) {
                throw new NullPointerException("hs");
            }
            try {
                ClassLoader classLoader = helpSet.getLoader();
                if (classLoader == null) {
                    Class class_ = Class.forName(string);
                } else {
                    Class class_ = classLoader.loadClass(string);
                }
            }
            catch (Exception var6_7) {
                throw new IllegalArgumentException(string + "presentation  invalid");
            }
            this.presentation = string;
            this.presentationName = string2;
            this.hs = helpSet;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            Object object;
            Object object2;
            SwingHelpUtilities.installUIDefaults();
            Cursor cursor = (Cursor)UIManager.get("HelpOnItemCursor");
            if (cursor == null) {
                return;
            }
            Vector vector = null;
            this.cursors = null;
            if (cursor != null) {
                this.cursors = new Hashtable();
                vector = DisplayHelpAfterTracking.getTopContainers(actionEvent.getSource());
                object = vector.elements();
                while (object.hasMoreElements()) {
                    this.setAndStoreCursors((Container)object.nextElement(), cursor);
                }
            }
            object = CSH.getMouseEvent();
            CSH.debug("CSH.getMouseEvent() >>> " + object);
            if (object != null) {
                object2 = CSH.getDeepestObjectAt(object.getSource(), object.getX(), object.getY());
                CSH.debug("CSH.getDeepestObjectAt() >>> " + object2);
                if (object2 != null) {
                    CSH.displayHelp(this.hb, this.hs, this.presentation, this.presentationName, object2, actionEvent.getSource(), (AWTEvent)object);
                }
            }
            if (vector != null) {
                object2 = vector.elements();
                while (object2.hasMoreElements()) {
                    this.resetAndRestoreCursors((Container)object2.nextElement());
                }
            }
            this.cursors = null;
        }

        private static Vector getTopContainers(Object object) {
            Object object2;
            Vector<Object> vector = new Vector<Object>();
            Component component = null;
            component = DisplayHelpAfterTracking.getRoot(object);
            if (component instanceof Applet) {
                try {
                    object2 = ((Applet)component).getAppletContext().getApplets();
                    while (object2.hasMoreElements()) {
                        vector.add(object2.nextElement());
                    }
                }
                catch (NullPointerException var3_4) {
                    vector.add(component);
                }
            }
            object2 = Frame.getFrames();
            int n = 0;
            while (n < object2.length) {
                Window[] arrwindow = object2[n].getOwnedWindows();
                int n2 = 0;
                while (n2 < arrwindow.length) {
                    vector.add(arrwindow[n2]);
                    ++n2;
                }
                if (!vector.contains(object2[n])) {
                    vector.add(object2[n]);
                }
                ++n;
            }
            return vector;
        }

        private static Component getRoot(Object object) {
            Object object2 = object;
            while (object2 != null) {
                object = object2;
                if (object instanceof MenuComponent) {
                    object2 = ((MenuComponent)object).getParent();
                    continue;
                }
                if (!(object instanceof Component) || object instanceof Window || object instanceof Applet) break;
                object2 = ((Component)object).getParent();
            }
            if (object instanceof Component) {
                return (Component)object;
            }
            return null;
        }

        private void setAndStoreCursors(Component component, Cursor cursor) {
            if (component == null) {
                return;
            }
            Cursor cursor2 = component.getCursor();
            if (cursor2 != cursor) {
                this.cursors.put(component, cursor2);
                CSH.debug("set cursor on " + component);
                component.setCursor(cursor);
            }
            if (component instanceof Container) {
                Component[] arrcomponent = ((Container)component).getComponents();
                int n = 0;
                while (n < arrcomponent.length) {
                    this.setAndStoreCursors(arrcomponent[n], cursor);
                    ++n;
                }
            }
        }

        private void resetAndRestoreCursors(Component component) {
            if (component == null) {
                return;
            }
            Cursor cursor = (Cursor)this.cursors.get(component);
            if (cursor != null) {
                CSH.debug("restored cursor " + cursor + " on " + component);
                component.setCursor(cursor);
            }
            if (component instanceof Container) {
                Component[] arrcomponent = ((Container)component).getComponents();
                int n = 0;
                while (n < arrcomponent.length) {
                    this.resetAndRestoreCursors(arrcomponent[n]);
                    ++n;
                }
            }
        }
    }

    public static class DisplayHelpFromFocus
    implements ActionListener {
        private HelpBroker hb = null;
        private HelpSet hs = null;
        private String presentation = null;
        private String presentationName = null;

        public DisplayHelpFromFocus(HelpBroker helpBroker) {
            if (helpBroker == null) {
                throw new NullPointerException("hb");
            }
            this.hb = helpBroker;
        }

        public DisplayHelpFromFocus(HelpSet helpSet, String string, String string2) {
            if (helpSet == null) {
                throw new NullPointerException("hs");
            }
            try {
                ClassLoader classLoader = helpSet.getLoader();
                if (classLoader == null) {
                    Class class_ = Class.forName(string);
                } else {
                    Class class_ = classLoader.loadClass(string);
                }
            }
            catch (Exception var6_7) {
                throw new IllegalArgumentException(string + "presentation  invalid");
            }
            this.presentation = string;
            this.presentationName = string2;
            this.hs = helpSet;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            Component component = (Component)actionEvent.getSource();
            if (this.hb instanceof DefaultHelpBroker) {
                ((DefaultHelpBroker)this.hb).setActivationObject(component);
            }
            Component component2 = CSH.findFocusOwner(component);
            CSH.debug("focusOwner:" + component2);
            if (component2 == null) {
                component2 = component;
            }
            CSH.displayHelp(this.hb, this.hs, this.presentation, this.presentationName, component2, component2, actionEvent);
        }
    }

}

