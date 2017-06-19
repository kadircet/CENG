/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitEvent;
import com.cburch.logisim.circuit.CircuitListener;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Subcircuit;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.gui.log.ComponentIcon;
import com.cburch.logisim.gui.log.Loggable;
import com.cburch.logisim.gui.log.Model;
import com.cburch.logisim.gui.log.SelectionItem;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

class ComponentSelector
extends JTree {
    private Model logModel;

    public ComponentSelector(Model logModel) {
        DefaultTreeModel model = new DefaultTreeModel(null);
        model.setAsksAllowsChildren(false);
        this.setModel(model);
        this.setRootVisible(false);
        this.setLogModel(logModel);
        this.setCellRenderer(new MyCellRenderer());
    }

    public void setLogModel(Model value) {
        CircuitState state;
        this.logModel = value;
        DefaultTreeModel model = (DefaultTreeModel)this.getModel();
        CircuitNode curRoot = (CircuitNode)model.getRoot();
        CircuitState circuitState = state = this.logModel == null ? null : this.logModel.getCircuitState();
        if (state == null) {
            if (curRoot != null) {
                model.setRoot(null);
            }
            return;
        }
        if (curRoot == null || curRoot.circuitState != state) {
            curRoot = new CircuitNode(null, state, null);
            model.setRoot(curRoot);
        }
    }

    public List getSelectedItems() {
        TreePath[] sel = this.getSelectionPaths();
        if (sel == null || sel.length == 0) {
            return Collections.EMPTY_LIST;
        }
        ArrayList<SelectionItem> ret = new ArrayList<SelectionItem>();
        for (int i = 0; i < sel.length; ++i) {
            TreePath path = sel[i];
            Object last = path.getLastPathComponent();
            ComponentNode n = null;
            Object opt = null;
            if (last instanceof OptionNode) {
                OptionNode o = (OptionNode)last;
                n = o.parent;
                opt = o.option;
            } else if (last instanceof ComponentNode && (n = (ComponentNode)last).opts != null) {
                n = null;
            }
            if (n == null) continue;
            int count = 0;
            CircuitNode cur = n.parent;
            while (cur != null) {
                ++count;
                cur = cur.parent;
            }
            Subcircuit[] nPath = new Subcircuit[count - 1];
            CircuitNode cur2 = n.parent;
            for (int j = nPath.length - 1; j >= 0; --j) {
                nPath[j] = cur2.subcircuit;
            }
            ret.add(new SelectionItem(this.logModel, nPath, n.comp, opt));
        }
        return ret.size() == 0 ? null : ret;
    }

    public boolean hasSelectedItems() {
        TreePath[] sel = this.getSelectionPaths();
        if (sel == null || sel.length == 0) {
            return false;
        }
        for (int i = 0; i < sel.length; ++i) {
            Object last = sel[i].getLastPathComponent();
            if (last instanceof OptionNode) {
                return true;
            }
            if (!(last instanceof ComponentNode) || ((ComponentNode)last).opts != null) continue;
            return true;
        }
        return false;
    }

    public void localeChanged() {
        this.repaint();
    }

    private class MyCellRenderer
    extends DefaultTreeCellRenderer {
        private MyCellRenderer() {
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component ret = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if (ret instanceof JLabel && value instanceof ComponentNode) {
                ComponentNode node = (ComponentNode)value;
                ComponentIcon icon = new ComponentIcon(node.comp);
                if (node.getChildCount() > 0) {
                    icon.setTriangleState(expanded ? 2 : 1);
                }
                ((JLabel)ret).setIcon(icon);
            }
            return ret;
        }
    }

    private class OptionNode
    implements TreeNode {
        private ComponentNode parent;
        private Object option;

        public OptionNode(ComponentNode parent, Object option) {
            this.parent = parent;
            this.option = option;
        }

        public String toString() {
            return this.option.toString();
        }

        @Override
        public TreeNode getChildAt(int arg0) {
            return null;
        }

        @Override
        public int getChildCount() {
            return 0;
        }

        @Override
        public TreeNode getParent() {
            return this.parent;
        }

        @Override
        public int getIndex(TreeNode n) {
            return -1;
        }

        @Override
        public boolean getAllowsChildren() {
            return false;
        }

        @Override
        public boolean isLeaf() {
            return true;
        }

        @Override
        public Enumeration children() {
            return Collections.enumeration(Collections.EMPTY_SET);
        }
    }

    private class ComponentNode
    implements TreeNode {
        private CircuitNode parent;
        private com.cburch.logisim.comp.Component comp;
        private OptionNode[] opts;

        public ComponentNode(CircuitNode parent, com.cburch.logisim.comp.Component comp) {
            Object[] opts;
            this.parent = parent;
            this.comp = comp;
            this.opts = null;
            Loggable log = (Loggable)comp.getFeature(Loggable.class);
            if (log != null && (opts = log.getLogOptions(parent.circuitState)) != null && opts.length > 0) {
                this.opts = new OptionNode[opts.length];
                for (int i = 0; i < opts.length; ++i) {
                    this.opts[i] = new OptionNode(this, opts[i]);
                }
            }
        }

        public String toString() {
            String ret;
            Loggable log = (Loggable)this.comp.getFeature(Loggable.class);
            if (log != null && (ret = log.getLogName(null)) != null && !ret.equals("")) {
                return ret;
            }
            return this.comp.getFactory().getDisplayName() + " " + this.comp.getLocation();
        }

        @Override
        public TreeNode getChildAt(int index) {
            return this.opts[index];
        }

        @Override
        public int getChildCount() {
            return this.opts == null ? 0 : this.opts.length;
        }

        @Override
        public TreeNode getParent() {
            return this.parent;
        }

        @Override
        public int getIndex(TreeNode n) {
            for (int i = 0; i < this.opts.length; ++i) {
                if (this.opts[i] != n) continue;
                return i;
            }
            return -1;
        }

        @Override
        public boolean getAllowsChildren() {
            return false;
        }

        @Override
        public boolean isLeaf() {
            return this.opts == null || this.opts.length == 0;
        }

        @Override
        public Enumeration children() {
            return Collections.enumeration(Arrays.asList(this.opts));
        }
    }

    private class CircuitNode
    implements TreeNode,
    CircuitListener,
    Comparator {
        private CircuitNode parent;
        private CircuitState circuitState;
        private Subcircuit subcircuit;
        private ArrayList children;

        public CircuitNode(CircuitNode parent, CircuitState circuitState, Subcircuit subcircuit) {
            this.parent = parent;
            this.circuitState = circuitState;
            this.subcircuit = subcircuit;
            this.children = new ArrayList();
            circuitState.getCircuit().addCircuitListener(this);
            this.computeChildren();
        }

        public String toString() {
            String ret = this.circuitState.getCircuit().getDisplayName();
            if (this.subcircuit != null) {
                ret = ret + this.subcircuit.getLocation();
            }
            return ret;
        }

        @Override
        public TreeNode getChildAt(int index) {
            return (TreeNode)this.children.get(index);
        }

        @Override
        public int getChildCount() {
            return this.children.size();
        }

        @Override
        public TreeNode getParent() {
            return this.parent;
        }

        @Override
        public int getIndex(TreeNode node) {
            return this.children.indexOf(node);
        }

        @Override
        public boolean getAllowsChildren() {
            return true;
        }

        @Override
        public boolean isLeaf() {
            return false;
        }

        @Override
        public Enumeration children() {
            return Collections.enumeration(this.children);
        }

        @Override
        public void circuitChanged(CircuitEvent event) {
            int action = event.getAction();
            DefaultTreeModel model = (DefaultTreeModel)ComponentSelector.this.getModel();
            if (action == 0) {
                model.nodeChanged(this);
            } else if (this.computeChildren()) {
                model.nodeStructureChanged(this);
            } else if (action == 4) {
                Object o = event.getData();
                for (int i = this.children.size() - 1; i >= 0; --i) {
                    ComponentNode n;
                    Object o2 = this.children.get(i);
                    if (!(o2 instanceof ComponentNode) || (n = (ComponentNode)o2).comp != o) continue;
                    int[] changed = new int[]{i};
                    this.children.remove(i);
                    model.nodesWereRemoved(this, changed, new Object[]{n});
                    this.children.add(i, new ComponentNode(this, n.comp));
                    model.nodesWereInserted(this, changed);
                }
            }
        }

        private boolean computeChildren() {
            TreeNode n;
            ArrayList<void> newChildren = new ArrayList<void>();
            ArrayList<com.cburch.logisim.comp.Component> subcircs = new ArrayList<com.cburch.logisim.comp.Component>();
            for (com.cburch.logisim.comp.Component comp2 : this.circuitState.getCircuit().getNonWires()) {
                void toAdd222;
                void toAdd222;
                if (comp2 instanceof Subcircuit) {
                    subcircs.add(comp2);
                    continue;
                }
                Object o = comp2.getFeature(Loggable.class);
                if (o == null) continue;
                Object toAdd222 = null;
                for (Object o2 : this.children) {
                    if (!(o2 instanceof ComponentNode) || (n = (ComponentNode)o2).comp != comp2) continue;
                    ComponentNode toAdd222 = n;
                    break;
                }
                if (toAdd222 == null) {
                    ComponentNode toAdd222 = new ComponentNode(this, comp2);
                }
                newChildren.add(toAdd222);
            }
            Collections.sort(newChildren, this);
            Collections.sort(subcircs, this);
            for (Subcircuit comp : subcircs) {
                void toAdd322;
                void toAdd322;
                CircuitState state = comp.getSubstate(this.circuitState);
                Object toAdd322 = null;
                for (Object o : this.children) {
                    if (!(o instanceof CircuitNode)) continue;
                    n = (CircuitNode)o;
                    if (n.circuitState != state) continue;
                    TreeNode toAdd322 = n;
                    break;
                }
                if (toAdd322 == null) {
                    CircuitNode toAdd322 = new CircuitNode(this, state, comp);
                }
                newChildren.add(toAdd322);
            }
            if (!this.children.equals(newChildren)) {
                this.children = newChildren;
                return true;
            }
            return false;
        }

        public int compare(Object a, Object b) {
            if (a instanceof Subcircuit) {
                Subcircuit x = (Subcircuit)a;
                Subcircuit y = (Subcircuit)b;
                int ret = x == y ? 0 : x.getFactory().getDisplayName().compareToIgnoreCase(y.getFactory().getDisplayName());
                return ret != 0 ? ret : x.getLocation().toString().compareTo(y.getLocation().toString());
            }
            return a.toString().compareToIgnoreCase(b.toString());
        }
    }

}

