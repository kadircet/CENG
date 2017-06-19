/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitActions;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.file.LibraryEvent;
import com.cburch.logisim.file.LibraryListener;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.file.ToolbarData;
import com.cburch.logisim.gui.main.AttributeTable;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.gui.main.Explorer;
import com.cburch.logisim.gui.main.ExplorerManip;
import com.cburch.logisim.gui.main.MenuListener;
import com.cburch.logisim.gui.main.Strings;
import com.cburch.logisim.gui.main.ToolActions;
import com.cburch.logisim.gui.main.Toolbar;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.ProjectActions;
import com.cburch.logisim.proj.ProjectEvent;
import com.cburch.logisim.proj.ProjectListener;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.HorizontalSplitPane;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.MacCompatibility;
import com.cburch.logisim.util.StringUtil;
import com.cburch.logisim.util.VerticalSplitPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;

public class Frame
extends JFrame
implements LocaleListener {
    private Project proj;
    private LogisimMenuBar menubar;
    private MenuListener menuListener;
    private Toolbar toolbar;
    private Canvas canvas;
    private JPanel canvasPanel;
    private Explorer explorer;
    private AttributeTable attrTable;
    private MyProjectListener myProjectListener;

    public Frame(Project proj) {
        this.myProjectListener = new MyProjectListener();
        this.proj = proj;
        proj.setFrame(this);
        this.setBackground(Color.white);
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new MyWindowListener());
        proj.addProjectListener(this.myProjectListener);
        proj.addLibraryListener(this.myProjectListener);
        proj.getOptions().getAttributeSet().addAttributeListener(this.myProjectListener);
        this.computeTitle();
        this.menubar = new LogisimMenuBar(this, proj);
        this.setJMenuBar(this.menubar);
        this.menuListener = new MenuListener(this, this.menubar);
        this.menuListener.register();
        this.toolbar = new Toolbar(proj);
        this.explorer = new Explorer(proj);
        this.explorer.setListener(new ExplorerManip(proj, this.explorer));
        this.canvasPanel = new JPanel(new BorderLayout());
        this.canvas = new Canvas(proj);
        this.attrTable = new AttributeTable(this, proj);
        JScrollPane canvasPane = new JScrollPane(this.canvas);
        if (MacCompatibility.mrjVersion >= 0.0) {
            canvasPane.setVerticalScrollBarPolicy(22);
            canvasPane.setHorizontalScrollBarPolicy(32);
        }
        this.canvas.setScrollPane(canvasPane);
        this.canvasPanel.add((java.awt.Component)canvasPane, "Center");
        VerticalSplitPane contents = new VerticalSplitPane(new HorizontalSplitPane(new JScrollPane(this.explorer), new JScrollPane(this.attrTable), 0.5), this.canvasPanel, 0.25);
        this.placeToolbar(proj.getOptions().getAttributeSet().getValue(Options.ATTR_TOOLBAR_LOC));
        this.getContentPane().add((java.awt.Component)contents, "Center");
        this.computeTitle();
        this.setSize(640, 480);
        this.toolbar.registerShortcuts(this.canvas);
        this.toolbar.registerShortcuts(this.toolbar);
        this.toolbar.registerShortcuts(this.explorer);
        this.toolbar.registerShortcuts(this.attrTable);
        if (proj.getTool() == null) {
            proj.setTool(proj.getOptions().getToolbarData().getFirstTool());
        }
        LocaleManager.addLocaleListener(this);
    }

    private void placeToolbar(Object loc) {
        Container contents = this.getContentPane();
        contents.remove(this.toolbar);
        this.canvasPanel.remove(this.toolbar);
        if (loc != Options.TOOLBAR_HIDDEN) {
            if (loc == Options.TOOLBAR_DOWN_MIDDLE) {
                this.toolbar.setOrientation(Toolbar.VERTICAL);
                this.canvasPanel.add((java.awt.Component)this.toolbar, "West");
            } else {
                String value = loc == Direction.EAST ? "East" : (loc == Direction.SOUTH ? "South" : (loc == Direction.WEST ? "West" : "North"));
                contents.add((java.awt.Component)this.toolbar, value);
                boolean vertical = value == "West" || value == "East";
                this.toolbar.setOrientation(vertical ? Toolbar.VERTICAL : Toolbar.HORIZONTAL);
            }
        }
        contents.validate();
    }

    public Project getProject() {
        return this.proj;
    }

    public void viewComponentAttributes(Circuit circ, Component comp) {
        if (comp == null) {
            this.attrTable.setAttributeSet(null);
            this.canvas.setHaloedComponent(null, null);
        } else {
            this.attrTable.setAttributeSet(comp.getAttributeSet(), new ComponentAttributeListener(this.proj, circ, comp));
            this.canvas.setHaloedComponent(circ, comp);
        }
        this.toolbar.setHaloedTool(null);
        this.explorer.setHaloedTool(null);
    }

    boolean getShowHalo() {
        return this.canvas.getShowHalo();
    }

    public AttributeTable getAttributeTable() {
        return this.attrTable;
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    void computeTitle() {
        Circuit circuit = this.proj.getCurrentCircuit();
        String name = this.proj.getLogisimFile().getName();
        String s = circuit != null ? StringUtil.format(Strings.get("titleCircFileKnown"), circuit.getName(), name) : StringUtil.format(Strings.get("titleFileKnown"), name);
        this.setTitle(s);
    }

    void viewAttributes(Tool newTool) {
        this.viewAttributes(null, newTool);
    }

    private void viewAttributes(Tool oldTool, Tool newTool) {
        if (newTool == null) {
            return;
        }
        AttributeSet newAttrs = newTool.getAttributeSet();
        if (newAttrs == null) {
            AttributeSet oldAttrs;
            AttributeSet attributeSet = oldAttrs = oldTool == null ? null : oldTool.getAttributeSet();
            if (this.attrTable.getAttributeSet() != oldAttrs) {
                return;
            }
        }
        this.attrTable.setAttributeSet(newAttrs, ToolActions.createTableListener(this.proj, newTool));
        if (newAttrs != null && newAttrs.getAttributes().size() > 0) {
            this.toolbar.setHaloedTool(newTool);
            this.explorer.setHaloedTool(newTool);
        } else {
            this.toolbar.setHaloedTool(null);
            this.explorer.setHaloedTool(null);
        }
        this.canvas.setHaloedComponent(null, null);
    }

    @Override
    public void localeChanged() {
        this.computeTitle();
    }

    public boolean confirmClose() {
        return this.confirmClose(Strings.get("confirmCloseTitle"));
    }

    public boolean confirmClose(String title) {
        String message = StringUtil.format(Strings.get("confirmDiscardMessage"), this.proj.getLogisimFile().getName());
        if (!this.proj.isFileDirty()) {
            return true;
        }
        this.toFront();
        Object[] options = new String[]{Strings.get("saveOption"), Strings.get("discardOption"), Strings.get("cancelOption")};
        int result = JOptionPane.showOptionDialog(this, message, title, 0, 3, null, options, options[0]);
        boolean ret = result == 0 ? ProjectActions.doSave(this.proj) : result == 1;
        if (ret) {
            this.dispose();
        }
        return ret;
    }

    private static class ComponentAttributeListener
    implements AttributeTable.Listener {
        Project proj;
        Circuit circ;
        Component comp;

        ComponentAttributeListener(Project proj, Circuit circ, Component comp) {
            this.proj = proj;
            this.circ = circ;
            this.comp = comp;
        }

        @Override
        public void valueChangeRequested(Attribute attr, Object value) {
            if (!this.proj.getLogisimFile().contains(this.circ)) {
                JOptionPane.showMessageDialog(this.proj.getFrame(), Strings.get("cannotModifyCircuitError"));
            } else {
                this.proj.doAction(CircuitActions.setAttributeValue(this.circ, this.comp, attr, value));
            }
        }
    }

    class MyWindowListener
    extends WindowAdapter {
        MyWindowListener() {
        }

        @Override
        public void windowClosing(WindowEvent e) {
            if (Frame.this.confirmClose(Strings.get("confirmCloseTitle"))) {
                Frame.this.dispose();
            }
        }

        @Override
        public void windowOpened(WindowEvent e) {
            Frame.this.canvas.computeSize();
        }
    }

    class MyProjectListener
    implements ProjectListener,
    LibraryListener,
    AttributeListener {
        MyProjectListener() {
        }

        @Override
        public void projectChanged(ProjectEvent event) {
            int action = event.getAction();
            if (action == 3 || action == 0) {
                this.enableSave();
            }
            if (action == 0) {
                Frame.this.computeTitle();
                Frame.this.proj.setTool(Frame.this.proj.getOptions().getToolbarData().getFirstTool());
                AttributeSet attrs = Frame.this.proj.getOptions().getAttributeSet();
                attrs.addAttributeListener(this);
                Frame.this.placeToolbar(attrs.getValue(Options.ATTR_TOOLBAR_LOC));
            } else if (action == 1) {
                Frame.this.computeTitle();
            } else if (action == 2) {
                if (Frame.this.attrTable == null) {
                    return;
                }
                Frame.this.viewAttributes((Tool)event.getOldData(), (Tool)event.getData());
            }
        }

        @Override
        public void libraryChanged(LibraryEvent e) {
            if (e.getAction() == 5) {
                Frame.this.computeTitle();
            }
        }

        private void enableSave() {
            Project proj = Frame.this.getProject();
            boolean ok = proj.isFileDirty();
            Frame.this.getRootPane().putClientProperty("windowModified", ok);
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            if (e.getAttribute() == Options.ATTR_TOOLBAR_LOC) {
                Frame.this.placeToolbar(e.getValue());
            }
        }
    }

}

