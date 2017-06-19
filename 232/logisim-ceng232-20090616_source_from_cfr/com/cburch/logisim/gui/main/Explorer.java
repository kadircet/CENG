/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitEvent;
import com.cburch.logisim.circuit.CircuitListener;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.file.LibraryEvent;
import com.cburch.logisim.file.LibraryEventSource;
import com.cburch.logisim.file.LibraryListener;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.main.AttributeTable;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.proj.LogisimPreferences;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.ProjectEvent;
import com.cburch.logisim.proj.ProjectListener;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.JTreeDragController;
import com.cburch.logisim.util.JTreeUtil;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class Explorer
extends JTree
implements LocaleListener {
    private static final String DIRTY_MARKER = "*";
    private Project proj;
    private MyListener myListener;
    private SubListener subListener;
    private MyModel model;
    private MyCellRenderer renderer;
    private DeleteAction deleteAction;
    private Listener listener;
    private Tool haloedTool;

    public Explorer(Project proj) {
        this.myListener = new MyListener();
        this.subListener = new SubListener();
        this.model = new MyModel();
        this.renderer = new MyCellRenderer();
        this.deleteAction = new DeleteAction();
        this.listener = null;
        this.haloedTool = null;
        this.proj = proj;
        this.setModel(this.model);
        this.setRootVisible(true);
        this.addMouseListener(this.myListener);
        ToolTipManager.sharedInstance().registerComponent(this);
        MySelectionModel selector = new MySelectionModel();
        selector.setSelectionMode(1);
        this.setSelectionModel(selector);
        this.setCellRenderer(this.renderer);
        JTreeUtil.configureDragAndDrop(this, new DragController());
        this.addTreeSelectionListener(this.myListener);
        InputMap imap = this.getInputMap(1);
        imap.put(KeyStroke.getKeyStroke(8, 0), this.deleteAction);
        ActionMap amap = this.getActionMap();
        amap.put(this.deleteAction, this.deleteAction);
        proj.addProjectListener(this.myListener);
        proj.addLibraryListener(this.myListener);
        LogisimPreferences.addPropertyChangeListener("gateShape", this.myListener);
        this.myListener.setFile(proj.getLogisimFile());
        LocaleManager.addLocaleListener(this);
    }

    public Tool getSelectedTool() {
        TreePath path = this.getSelectionPath();
        if (path == null) {
            return null;
        }
        Object last = path.getLastPathComponent();
        return last instanceof Tool ? (Tool)last : null;
    }

    public void setListener(Listener value) {
        this.listener = value;
    }

    public void setHaloedTool(Tool t) {
        if (this.haloedTool == t) {
            return;
        }
        this.haloedTool = t;
        this.repaint();
    }

    @Override
    public void localeChanged() {
        this.model.fireTreeStructureChanged();
    }

    private class SubListener
    implements LibraryListener {
        private SubListener() {
        }

        @Override
        public void libraryChanged(LibraryEvent event) {
            Explorer.this.model.fireTreeStructureChanged();
        }
    }

    private class MyListener
    implements MouseListener,
    TreeSelectionListener,
    ProjectListener,
    LibraryListener,
    CircuitListener,
    PropertyChangeListener {
        private MyListener() {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Explorer.this.grabFocus();
            this.checkForPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            this.checkForPopup(e);
        }

        private void checkForPopup(MouseEvent e) {
            JPopupMenu menu;
            TreePath path;
            if (e.isPopupTrigger() && (path = Explorer.this.getPathForLocation(e.getX(), e.getY())) != null && Explorer.this.listener != null && (menu = Explorer.this.listener.menuRequested(new Event(path))) != null) {
                menu.show(Explorer.this, e.getX(), e.getY());
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            TreePath path;
            if (e.getClickCount() == 2 && (path = Explorer.this.getPathForLocation(e.getX(), e.getY())) != null && Explorer.this.listener != null) {
                Explorer.this.listener.doubleClicked(new Event(path));
            }
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getNewLeadSelectionPath();
            if (Explorer.this.listener != null) {
                Explorer.this.listener.selectionChanged(new Event(path));
            }
        }

        @Override
        public void projectChanged(ProjectEvent event) {
            int act = event.getAction();
            if (act == 2) {
                TreePath path = Explorer.this.getSelectionPath();
                if (path != null && path.getLastPathComponent() != event.getTool()) {
                    Explorer.this.clearSelection();
                }
            } else if (act == 0) {
                this.setFile(event.getLogisimFile());
            } else if (act == 1) {
                Explorer.this.repaint();
            }
        }

        @Override
        public void libraryChanged(LibraryEvent event) {
            int act = event.getAction();
            if (act == 0) {
                AddTool tool;
                if (event.getData() instanceof AddTool && (tool = (AddTool)event.getData()).getFactory() instanceof Circuit) {
                    Circuit circ = (Circuit)tool.getFactory();
                    circ.addCircuitListener(this);
                }
            } else if (act == 1) {
                AddTool tool;
                if (event.getData() instanceof AddTool && (tool = (AddTool)event.getData()).getFactory() instanceof Circuit) {
                    Circuit circ = (Circuit)tool.getFactory();
                    circ.removeCircuitListener(this);
                }
            } else if (act == 2) {
                if (event.getData() instanceof LibraryEventSource) {
                    ((LibraryEventSource)event.getData()).addLibraryListener(Explorer.this.subListener);
                }
            } else if (act == 3 && event.getData() instanceof LibraryEventSource) {
                ((LibraryEventSource)event.getData()).removeLibraryListener(Explorer.this.subListener);
            }
            Explorer.this.model.fireTreeStructureChanged();
        }

        @Override
        public void circuitChanged(CircuitEvent event) {
            int act = event.getAction();
            if (act == 0) {
                Explorer.this.model.fireTreeStructureChanged();
            }
        }

        private void setFile(LogisimFile lib) {
            Explorer.this.model.fireTreeStructureChanged();
            Explorer.this.expandRow(0);
            for (Object o2 : lib.getTools()) {
                AddTool tool;
                ComponentFactory source;
                if (!(o2 instanceof AddTool) || !((source = (tool = (AddTool)o2).getFactory()) instanceof Circuit)) continue;
                ((Circuit)source).addCircuitListener(this);
            }
            Explorer.this.subListener = new SubListener();
            for (Object o2 : lib.getLibraries()) {
                if (!(o2 instanceof LibraryEventSource)) continue;
                ((LibraryEventSource)o2).addLibraryListener(Explorer.this.subListener);
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals("gateShape")) {
                Explorer.this.repaint();
            }
        }
    }

    private class DeleteAction
    extends AbstractAction {
        private DeleteAction() {
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            TreePath path = Explorer.this.getSelectionPath();
            if (Explorer.this.listener != null && path != null && path.getPathCount() == 2) {
                Explorer.this.listener.deleteRequested(new Event(path));
            }
            Explorer.this.grabFocus();
        }
    }

    private class DragController
    implements JTreeDragController {
        private DragController() {
        }

        @Override
        public boolean canPerformAction(JTree targetTree, Object draggedNode, int action, Point location) {
            TreePath pathTarget = targetTree.getPathForLocation(location.x, location.y);
            if (pathTarget == null) {
                targetTree.setSelectionPath(null);
                return false;
            }
            targetTree.setSelectionPath(pathTarget);
            if (action == 1) {
                return false;
            }
            if (action == 2) {
                Object targetNode = pathTarget.getLastPathComponent();
                return this.canMove(draggedNode, targetNode);
            }
            return false;
        }

        @Override
        public boolean executeDrop(JTree targetTree, Object draggedNode, Object targetNode, int action) {
            if (action == 1) {
                return false;
            }
            if (action == 2) {
                if (this.canMove(draggedNode, targetNode)) {
                    if (draggedNode == targetNode) {
                        return true;
                    }
                    Explorer.this.listener.moveRequested(new Event(null), (AddTool)draggedNode, (AddTool)targetNode);
                    return true;
                }
                return false;
            }
            return false;
        }

        private boolean canMove(Object draggedNode, Object targetNode) {
            if (Explorer.this.listener == null) {
                return false;
            }
            if (!(draggedNode instanceof AddTool) || !(targetNode instanceof AddTool)) {
                return false;
            }
            LogisimFile file = Explorer.this.proj.getLogisimFile();
            AddTool dragged = (AddTool)draggedNode;
            AddTool target = (AddTool)targetNode;
            int draggedIndex = file.getTools().indexOf(dragged);
            int targetIndex = file.getTools().indexOf(target);
            if (targetIndex < 0 || draggedIndex < 0) {
                return false;
            }
            return true;
        }
    }

    private class MySelectionModel
    extends DefaultTreeSelectionModel {
        private MySelectionModel() {
        }

        @Override
        public void addSelectionPath(TreePath path) {
            if (this.isPathValid(path)) {
                super.addSelectionPath(path);
            }
        }

        @Override
        public void setSelectionPath(TreePath path) {
            if (this.isPathValid(path)) {
                super.setSelectionPath(path);
            }
        }

        @Override
        public void addSelectionPaths(TreePath[] paths) {
            if ((paths = this.getValidPaths(paths)) != null) {
                super.addSelectionPaths(paths);
            }
        }

        @Override
        public void setSelectionPaths(TreePath[] paths) {
            if ((paths = this.getValidPaths(paths)) != null) {
                super.setSelectionPaths(paths);
            }
        }

        private TreePath[] getValidPaths(TreePath[] paths) {
            int count = 0;
            for (int i = 0; i < paths.length; ++i) {
                if (!this.isPathValid(paths[i])) continue;
                ++count;
            }
            if (count == 0) {
                return null;
            }
            if (count == paths.length) {
                return paths;
            }
            TreePath[] ret = new TreePath[count];
            int j = 0;
            for (int i2 = 0; i2 < paths.length; ++i2) {
                if (!this.isPathValid(paths[i2])) continue;
                ret[j++] = paths[i2];
            }
            return ret;
        }

        private boolean isPathValid(TreePath path) {
            if (path == null || path.getPathCount() > 3) {
                return false;
            }
            Object last = path.getLastPathComponent();
            return last instanceof Tool;
        }
    }

    private class MyCellRenderer
    extends DefaultTreeCellRenderer {
        private MyCellRenderer() {
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Tool tool;
            Component ret = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if (ret instanceof JComponent) {
                JComponent comp = (JComponent)ret;
                comp.setToolTipText(null);
            }
            if (value instanceof AddTool) {
                tool = (AddTool)value;
                if (ret instanceof JLabel) {
                    ((JLabel)ret).setText(((Tool)value).getDisplayName());
                    ((JLabel)ret).setIcon(new ToolIcon(tool));
                    ((JLabel)ret).setToolTipText(tool.getDescription());
                }
            } else if (value instanceof Tool) {
                tool = (Tool)value;
                if (ret instanceof JLabel) {
                    ((JLabel)ret).setText(tool.getDisplayName());
                    ((JLabel)ret).setIcon(new ToolIcon(tool));
                    ((JLabel)ret).setToolTipText(tool.getDescription());
                }
            } else if (value instanceof Library && ret instanceof JLabel) {
                Library lib = (Library)value;
                String text = lib.getDisplayName();
                if (lib.isDirty()) {
                    text = text + "*";
                }
                ((JLabel)ret).setText(text);
            }
            return ret;
        }
    }

    private class ToolIcon
    implements Icon {
        Tool tool;
        ComponentFactory circ;

        ToolIcon(Tool tool) {
            this.circ = null;
            this.tool = tool;
            if (tool instanceof AddTool) {
                this.circ = ((AddTool)tool).getFactory();
            }
        }

        @Override
        public int getIconHeight() {
            return 20;
        }

        @Override
        public int getIconWidth() {
            return 20;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (this.tool == Explorer.this.haloedTool && Explorer.this.proj.getFrame().getShowHalo()) {
                g.setColor(AttributeTable.HALO_COLOR);
                g.fillRoundRect(x, y, 20, 20, 10, 10);
                g.setColor(Color.BLACK);
            }
            Graphics gIcon = g.create();
            ComponentDrawContext context = new ComponentDrawContext(Explorer.this, null, null, g, gIcon);
            this.tool.paintIcon(context, x, y);
            gIcon.dispose();
            if (this.circ == Explorer.this.proj.getCurrentCircuit()) {
                int tx = x + 13;
                int ty = y + 13;
                int[] xp = new int[]{tx - 1, x + 18, x + 20, tx + 1};
                int[] yp = new int[]{ty + 1, y + 20, y + 18, ty - 1};
                g.setColor(Color.black);
                g.drawOval(x + 5, y + 5, 10, 10);
                g.fillPolygon(xp, yp, xp.length);
            }
        }
    }

    private class MyModel
    implements TreeModel {
        ArrayList listeners;

        private MyModel() {
            this.listeners = new ArrayList();
        }

        @Override
        public void addTreeModelListener(TreeModelListener l) {
            this.listeners.add(l);
        }

        @Override
        public void removeTreeModelListener(TreeModelListener l) {
            this.listeners.remove(l);
        }

        @Override
        public Object getRoot() {
            return Explorer.this.proj.getLogisimFile();
        }

        private List getChildren(Object parent) {
            if (parent == Explorer.this.proj.getLogisimFile()) {
                return ((Library)parent).getElements();
            }
            if (parent instanceof Library) {
                return ((Library)parent).getTools();
            }
            return Collections.EMPTY_LIST;
        }

        @Override
        public Object getChild(Object parent, int index) {
            return this.getChildren(parent).get(index);
        }

        @Override
        public int getChildCount(Object parent) {
            return this.getChildren(parent).size();
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            if (parent == null || child == null) {
                return -1;
            }
            Iterator it = this.getChildren(parent).iterator();
            int count = 0;
            while (it.hasNext()) {
                if (it.next() == child) {
                    return count;
                }
                ++count;
            }
            return -1;
        }

        @Override
        public boolean isLeaf(Object node) {
            return node != Explorer.this.proj && !(node instanceof Library);
        }

        @Override
        public void valueForPathChanged(TreePath path, Object newValue) {
            throw new UnsupportedOperationException();
        }

        void fireTreeStructureChanged() {
            TreeModelEvent e = new TreeModelEvent((Object)Explorer.this, new Object[]{Explorer.this.model.getRoot()});
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((TreeModelListener)it.next()).treeStructureChanged(e);
            }
            Explorer.this.repaint();
        }
    }

    public static interface Listener {
        public void selectionChanged(Event var1);

        public void doubleClicked(Event var1);

        public void moveRequested(Event var1, AddTool var2, AddTool var3);

        public void deleteRequested(Event var1);

        public JPopupMenu menuRequested(Event var1);
    }

    public static class Event {
        private TreePath path;

        private Event(TreePath path) {
            this.path = path;
        }

        public TreePath getTreePath() {
            return this.path;
        }

        public Object getTarget() {
            return this.path == null ? null : this.path.getLastPathComponent();
        }
    }

}

