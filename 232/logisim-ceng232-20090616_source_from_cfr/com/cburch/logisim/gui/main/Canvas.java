/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitEvent;
import com.cburch.logisim.circuit.CircuitListener;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Simulator;
import com.cburch.logisim.circuit.SimulatorEvent;
import com.cburch.logisim.circuit.SimulatorListener;
import com.cburch.logisim.circuit.WidthIncompatibilityData;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.file.LibraryEvent;
import com.cburch.logisim.file.LibraryListener;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.MouseMappings;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.gui.main.AttributeTable;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.main.Selection;
import com.cburch.logisim.gui.main.Strings;
import com.cburch.logisim.proj.LogisimPreferences;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.ProjectEvent;
import com.cburch.logisim.proj.ProjectListener;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.tools.ToolTipMaker;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.StringGetter;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.event.MouseInputListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class Canvas
extends JPanel
implements LocaleListener,
Scrollable {
    private static final int BOUNDS_BUFFER = 70;
    static final double SQRT_2 = Math.sqrt(2.0);
    private static final int BUTTONS_MASK = 28;
    private Project proj;
    private Tool drag_tool;
    private MouseMappings mappings;
    private JScrollPane parent = null;
    private MyListener myListener;
    private MyViewport viewport;
    private MyProjectListener myProjectListener;
    private boolean showGrid;
    private boolean printerView;
    private boolean showGhosts;
    private boolean showHalo;
    private boolean showTips;
    private double zoomFactor;
    private boolean paintDirty;
    private boolean inPaint;
    private Object repaintLock;
    private Component haloedComponent;
    private Circuit haloedCircuit;

    public Canvas(Project proj) {
        this.myListener = new MyListener();
        this.viewport = new MyViewport();
        this.myProjectListener = new MyProjectListener();
        this.showGrid = true;
        this.printerView = false;
        this.showGhosts = true;
        this.showHalo = true;
        this.showTips = true;
        this.zoomFactor = 1.0;
        this.paintDirty = false;
        this.inPaint = false;
        this.repaintLock = new Object();
        this.haloedComponent = null;
        this.haloedCircuit = null;
        this.proj = proj;
        this.mappings = proj.getOptions().getMouseMappings();
        this.setBackground(Color.white);
        this.addMouseListener(this.myListener);
        this.addMouseMotionListener(this.myListener);
        this.addKeyListener(this.myListener);
        proj.addProjectListener(this.myProjectListener);
        proj.addLibraryListener(this.myProjectListener);
        proj.addCircuitListener(this.myProjectListener);
        proj.getSelection().addListener(this.myProjectListener);
        LocaleManager.addLocaleListener(this);
        AttributeSet options = proj.getOptions().getAttributeSet();
        options.addAttributeListener(this.myProjectListener);
        LogisimPreferences.addPropertyChangeListener("gateShape", this.myListener);
        this.loadOptions(options);
    }

    private void loadOptions(AttributeSet options) {
        this.printerView = (Boolean)options.getValue(Options.preview_attr);
        this.showGrid = (Boolean)options.getValue(Options.showgrid_attr);
        this.showGhosts = (Boolean)options.getValue(Options.showghosts_attr);
        this.showHalo = (Boolean)options.getValue(Options.showhalo_attr);
        this.showTips = (Boolean)options.getValue(Options.showtips_attr);
        this.zoomFactor = (Double)options.getValue(Options.zoom_attr);
        this.setToolTipText(this.showTips ? "" : null);
        this.proj.getSimulator().removeSimulatorListener(this.myProjectListener);
        this.proj.getSimulator().addSimulatorListener(this.myProjectListener);
    }

    @Override
    public void repaint() {
        if (this.inPaint) {
            this.paintDirty = true;
        } else {
            super.repaint();
        }
    }

    public void setErrorMessage(StringGetter message) {
        this.viewport.setErrorMessage(message);
    }

    public Circuit getCircuit() {
        return this.proj.getCurrentCircuit();
    }

    public CircuitState getCircuitState() {
        return this.proj.getCircuitState();
    }

    public Project getProject() {
        return this.proj;
    }

    public boolean getShowGhosts() {
        return this.showGhosts;
    }

    public boolean getShowHalo() {
        return this.showHalo;
    }

    Component getHaloedComponent() {
        return this.haloedComponent;
    }

    void setHaloedComponent(Circuit circ, Component comp) {
        if (comp == this.haloedComponent) {
            return;
        }
        Graphics g = this.getGraphics();
        this.exposeHaloedComponent(g);
        this.haloedCircuit = circ;
        this.haloedComponent = comp;
        this.exposeHaloedComponent(g);
    }

    private void exposeHaloedComponent(Graphics g) {
        Component c = this.haloedComponent;
        if (c == null) {
            return;
        }
        Bounds bds = c.getBounds(g).expand(7);
        int w = bds.getWidth();
        int h = bds.getHeight();
        double a = SQRT_2 * (double)w;
        double b = SQRT_2 * (double)h;
        this.repaint((int)Math.round((double)bds.getX() + (double)w / 2.0 - a / 2.0), (int)Math.round((double)bds.getY() + (double)h / 2.0 - b / 2.0), (int)Math.round(a), (int)Math.round(b));
    }

    public void showPopupMenu(JPopupMenu menu, int x, int y) {
        if (this.zoomFactor != 1.0) {
            x = (int)Math.round((double)x * this.zoomFactor);
            y = (int)Math.round((double)y * this.zoomFactor);
        }
        this.myListener.menu_on = true;
        menu.addPopupMenuListener(this.myListener);
        menu.show(this, x, y);
    }

    private void completeAction() {
        this.computeSize();
        this.proj.getSimulator().requestPropagate();
    }

    public void setScrollPane(JScrollPane value) {
        if (this.parent != null) {
            this.parent.removeComponentListener(this.myListener);
        }
        this.parent = value;
        if (this.parent != null) {
            this.parent.setViewport(this.viewport);
            this.viewport.setView(this);
            this.setOpaque(false);
            this.parent.addComponentListener(this.myListener);
        }
        this.computeSize();
    }

    public void computeSize() {
        Bounds bounds = this.proj.getCurrentCircuit().getBounds();
        int width = bounds.getX() + bounds.getWidth() + 70;
        int height = bounds.getY() + bounds.getHeight() + 70;
        if (this.zoomFactor != 1.0) {
            width = (int)Math.ceil((double)width * this.zoomFactor);
            height = (int)Math.ceil((double)height * this.zoomFactor);
        }
        if (this.parent != null) {
            Dimension min_size = new Dimension();
            this.parent.getViewport().getSize(min_size);
            if (min_size.width > width) {
                width = min_size.width;
            }
            if (min_size.height > height) {
                height = min_size.height;
            }
        }
        this.setPreferredSize(new Dimension(width, height));
        this.revalidate();
    }

    private void waitForRepaintDone() {
        Object object = this.repaintLock;
        synchronized (object) {
            try {
                while (this.inPaint) {
                    this.repaintLock.wait();
                }
            }
            catch (InterruptedException e) {
                // empty catch block
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        this.inPaint = true;
        try {
            super.paintComponent(g);
            do {
                this.paintContents(g);
            } while (this.paintDirty);
            if (this.parent == null) {
                this.viewport.paintContents(g);
            }
        }
        finally {
            this.inPaint = false;
            Object object = this.repaintLock;
            synchronized (object) {
                this.repaintLock.notifyAll();
            }
        }
    }

    private void paintContents(Graphics g) {
        Rectangle clip = g.getClipBounds();
        Dimension size = this.getSize();
        if (clip == null || this.paintDirty) {
            clip = new Rectangle(0, 0, size.width, size.height);
            this.paintDirty = false;
        }
        g.setColor(Color.white);
        g.fillRect(clip.x, clip.y, clip.width, clip.height);
        if (this.showGrid) {
            g.setColor(Color.gray);
            if (this.zoomFactor == 1.0) {
                int start_x = (clip.x + 9) / 10 * 10;
                int start_y = (clip.y + 9) / 10 * 10;
                for (int x = 0; x < clip.width; x += 10) {
                    for (int y = 0; y < clip.height; y += 10) {
                        g.drawLine(start_x + x, start_y + y, start_x + x, start_y + y);
                    }
                }
            } else {
                double f = this.zoomFactor;
                double x0 = 10.0 * Math.ceil((double)clip.x / f / 10.0);
                double x1 = x0 + (double)clip.width / f;
                double y0 = 10.0 * Math.ceil((double)clip.y / f / 10.0);
                double y1 = y0 + (double)clip.height / f;
                for (double x = x0; x < x1; x += 10.0) {
                    for (double y = y0; y < y1; y += 10.0) {
                        int sx = (int)Math.round(f * x);
                        int sy = (int)Math.round(f * y);
                        g.drawLine(sx, sy, sx, sy);
                    }
                }
            }
        }
        g.setColor(Color.black);
        Graphics gScaled = g.create();
        if (this.zoomFactor != 1.0 && gScaled instanceof Graphics2D) {
            ((Graphics2D)gScaled).scale(this.zoomFactor, this.zoomFactor);
        }
        this.drawWithUserState(g, gScaled, this.printerView);
        this.drawWidthIncompatibilityData(g, gScaled);
        Circuit circ = this.proj.getCurrentCircuit();
        CircuitState circState = this.proj.getCircuitState();
        ComponentDrawContext ptContext = new ComponentDrawContext(this, circ, circState, g, gScaled);
        gScaled.setColor(Color.RED);
        circState.drawOscillatingPoints(ptContext);
        gScaled.setColor(Color.BLUE);
        this.proj.getSimulator().drawStepPoints(ptContext);
        gScaled.dispose();
    }

    private void drawWithUserState(Graphics base, Graphics g, boolean printView) {
        Tool tool;
        Circuit circ = this.proj.getCurrentCircuit();
        Selection sel = this.proj.getSelection();
        Collection hidden = sel.getHiddenComponents();
        if (this.showHalo && this.haloedComponent != null && this.haloedCircuit == circ && !hidden.contains(this.haloedComponent)) {
            GraphicsUtil.switchToWidth(g, 3);
            g.setColor(AttributeTable.HALO_COLOR);
            Bounds bds = this.haloedComponent.getBounds(g).expand(5);
            int w = bds.getWidth();
            int h = bds.getHeight();
            double a = SQRT_2 * (double)w;
            double b = SQRT_2 * (double)h;
            g.drawOval((int)Math.round((double)bds.getX() + (double)w / 2.0 - a / 2.0), (int)Math.round((double)bds.getY() + (double)h / 2.0 - b / 2.0), (int)Math.round(a), (int)Math.round(b));
            GraphicsUtil.switchToWidth(g, 1);
            g.setColor(Color.BLACK);
        }
        CircuitState circState = this.proj.getCircuitState();
        ComponentDrawContext context = new ComponentDrawContext(this, circ, circState, base, g, printView);
        circ.draw(context, hidden);
        sel.draw(context);
        Tool tool2 = tool = this.drag_tool != null ? this.drag_tool : this.proj.getTool();
        if (tool != null && !this.myListener.menu_on) {
            Graphics gCopy = g.create();
            context.setGraphics(gCopy);
            tool.draw(this, context);
            gCopy.dispose();
        }
    }

    private void drawWidthIncompatibilityData(Graphics base, Graphics g) {
        Set exceptions = this.proj.getCurrentCircuit().getWidthIncompatibilityData();
        if (exceptions == null || exceptions.size() == 0) {
            return;
        }
        g.setColor(Value.WIDTH_ERROR_COLOR);
        GraphicsUtil.switchToWidth(g, 2);
        FontMetrics fm = base.getFontMetrics(g.getFont());
        for (WidthIncompatibilityData ex : exceptions) {
            for (int i = 0; i < ex.size(); ++i) {
                Location p = ex.getPoint(i);
                BitWidth w = ex.getBitWidth(i);
                boolean drawn = false;
                for (int j = 0; j < i; ++j) {
                    if (!ex.getPoint(j).equals(p)) continue;
                    drawn = true;
                    break;
                }
                if (drawn) continue;
                String caption = "" + w.getWidth();
                for (int j2 = i + 1; j2 < ex.size(); ++j2) {
                    if (!ex.getPoint(j2).equals(p)) continue;
                    caption = caption + "/" + ex.getBitWidth(j2);
                    break;
                }
                g.drawOval(p.getX() - 4, p.getY() - 4, 8, 8);
                g.drawString(caption, p.getX() + 5, p.getY() + 2 + fm.getAscent());
            }
        }
        g.setColor(Color.BLACK);
        GraphicsUtil.switchToWidth(g, 1);
    }

    private void computeViewportContents() {
        Rectangle viewableBase;
        Set exceptions = this.proj.getCurrentCircuit().getWidthIncompatibilityData();
        if (exceptions == null || exceptions.size() == 0) {
            this.viewport.setWidthMessage(null);
            return;
        }
        if (this.parent != null) {
            viewableBase = this.parent.getViewport().getViewRect();
        } else {
            Bounds bds = this.proj.getCurrentCircuit().getBounds();
            viewableBase = new Rectangle(0, 0, bds.getWidth(), bds.getHeight());
        }
        Rectangle viewable = this.zoomFactor == 1.0 ? viewableBase : new Rectangle((int)((double)viewableBase.x / this.zoomFactor), (int)((double)viewableBase.y / this.zoomFactor), (int)((double)viewableBase.width / this.zoomFactor), (int)((double)viewableBase.height / this.zoomFactor));
        this.viewport.setWidthMessage(Strings.get("canvasWidthError") + (exceptions.size() == 1 ? "" : new StringBuilder().append(" (").append(exceptions.size()).append(")").toString()));
        for (WidthIncompatibilityData ex : exceptions) {
            boolean isSouth;
            boolean isWithin = false;
            for (int i = 0; i < ex.size(); ++i) {
                Location p = ex.getPoint(i);
                int x = p.getX();
                int y = p.getY();
                if (x < viewable.x || x >= viewable.x + viewable.width || y < viewable.y || y >= viewable.y + viewable.height) continue;
                isWithin = true;
                break;
            }
            if (isWithin) continue;
            Location p = ex.getPoint(0);
            int x = p.getX();
            int y = p.getY();
            boolean isWest = x < viewable.x;
            boolean isEast = x >= viewable.x + viewable.width;
            boolean isNorth = y < viewable.y;
            boolean bl = isSouth = y >= viewable.y + viewable.height;
            if (isNorth) {
                if (isEast) {
                    this.viewport.setNortheast(true);
                    continue;
                }
                if (isWest) {
                    this.viewport.setNorthwest(true);
                    continue;
                }
                this.viewport.setNorth(true);
                continue;
            }
            if (isSouth) {
                if (isEast) {
                    this.viewport.setSoutheast(true);
                    continue;
                }
                if (isWest) {
                    this.viewport.setSouthwest(true);
                    continue;
                }
                this.viewport.setSouth(true);
                continue;
            }
            if (isEast) {
                this.viewport.setEast(true);
                continue;
            }
            if (!isWest) continue;
            this.viewport.setWest(true);
        }
    }

    @Override
    public void repaint(Rectangle r) {
        if (this.zoomFactor == 1.0) {
            super.repaint(r);
        } else {
            this.repaint(r.x, r.y, r.width, r.height);
        }
    }

    @Override
    public void repaint(int x, int y, int width, int height) {
        if (this.zoomFactor != 1.0) {
            x = (int)Math.round((double)x * this.zoomFactor);
            y = (int)Math.round((double)y * this.zoomFactor);
            width = (int)Math.round((double)width * this.zoomFactor);
            height = (int)Math.round((double)height * this.zoomFactor);
        }
        super.repaint(x, y, width, height);
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        if (this.showTips) {
            Canvas.snapToGrid(event);
            Location loc = Location.create(event.getX(), event.getY());
            ComponentUserEvent e = null;
            for (Component comp : this.getCircuit().getAllContaining(loc)) {
                String ret;
                Object makerObj = comp.getFeature(ToolTipMaker.class);
                if (makerObj == null || !(makerObj instanceof ToolTipMaker)) continue;
                ToolTipMaker maker = (ToolTipMaker)makerObj;
                if (e == null) {
                    e = new ComponentUserEvent(this, loc.getX(), loc.getY());
                }
                if ((ret = maker.getToolTip(e)) == null) continue;
                return ret;
            }
        }
        return null;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return this.getPreferredSize();
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        int unit = this.getScrollableUnitIncrement(visibleRect, orientation, direction);
        if (direction == 1) {
            return visibleRect.height / unit * unit;
        }
        return visibleRect.width / unit * unit;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return (int)Math.round(10.0 * this.zoomFactor);
    }

    public static int snapXToGrid(int x) {
        if (x < 0) {
            return (- (- x + 5) / 10) * 10;
        }
        return (x + 5) / 10 * 10;
    }

    public static int snapYToGrid(int y) {
        if (y < 0) {
            return (- (- y + 5) / 10) * 10;
        }
        return (y + 5) / 10 * 10;
    }

    public static void snapToGrid(MouseEvent e) {
        int old_x = e.getX();
        int old_y = e.getY();
        int new_x = Canvas.snapXToGrid(old_x);
        int new_y = Canvas.snapYToGrid(old_y);
        e.translatePoint(new_x - old_x, new_y - old_y);
    }

    @Override
    public void localeChanged() {
        this.repaint();
    }

    private class MyViewport
    extends JViewport {
        StringGetter errorMessage;
        String widthMessage;
        boolean isNorth;
        boolean isSouth;
        boolean isWest;
        boolean isEast;
        boolean isNortheast;
        boolean isNorthwest;
        boolean isSoutheast;
        boolean isSouthwest;

        MyViewport() {
            this.errorMessage = null;
            this.widthMessage = null;
            this.isNorth = false;
            this.isSouth = false;
            this.isWest = false;
            this.isEast = false;
            this.isNortheast = false;
            this.isNorthwest = false;
            this.isSoutheast = false;
            this.isSouthwest = false;
        }

        void setErrorMessage(StringGetter msg) {
            if (this.errorMessage != msg) {
                this.errorMessage = msg;
                this.repaint();
            }
        }

        void setWidthMessage(String msg) {
            this.widthMessage = msg;
            this.isNorth = false;
            this.isSouth = false;
            this.isWest = false;
            this.isEast = false;
            this.isNortheast = false;
            this.isNorthwest = false;
            this.isSoutheast = false;
            this.isSouthwest = false;
        }

        void setNorth(boolean value) {
            this.isNorth = value;
        }

        void setSouth(boolean value) {
            this.isSouth = value;
        }

        void setEast(boolean value) {
            this.isEast = value;
        }

        void setWest(boolean value) {
            this.isWest = value;
        }

        void setNortheast(boolean value) {
            this.isNortheast = value;
        }

        void setNorthwest(boolean value) {
            this.isNorthwest = value;
        }

        void setSoutheast(boolean value) {
            this.isSoutheast = value;
        }

        void setSouthwest(boolean value) {
            this.isSouthwest = value;
        }

        @Override
        public void paintChildren(Graphics g) {
            super.paintChildren(g);
            this.paintContents(g);
        }

        @Override
        public Color getBackground() {
            return this.getView() == null ? super.getBackground() : this.getView().getBackground();
        }

        void paintContents(Graphics g) {
            if (this.errorMessage != null) {
                g.setColor(Color.RED);
                this.paintString(g, this.errorMessage.get());
                return;
            }
            if (Canvas.this.proj.getSimulator().isExceptionEncountered()) {
                g.setColor(Color.RED);
                this.paintString(g, Strings.get("canvasExceptionError"));
                return;
            }
            Canvas.this.computeViewportContents();
            Dimension sz = this.getSize();
            g.setColor(Value.WIDTH_ERROR_COLOR);
            if (this.widthMessage != null) {
                this.paintString(g, this.widthMessage);
            }
            GraphicsUtil.switchToWidth(g, 3);
            if (this.isNorth) {
                GraphicsUtil.drawArrow(g, sz.width / 2, 20, sz.width / 2, 2, 10, 30);
            }
            if (this.isSouth) {
                GraphicsUtil.drawArrow(g, sz.width / 2, sz.height - 20, sz.width / 2, sz.height - 2, 10, 30);
            }
            if (this.isEast) {
                GraphicsUtil.drawArrow(g, sz.width - 20, sz.height / 2, sz.width - 2, sz.height / 2, 10, 30);
            }
            if (this.isWest) {
                GraphicsUtil.drawArrow(g, 20, sz.height / 2, 2, sz.height / 2, 10, 30);
            }
            if (this.isNortheast) {
                GraphicsUtil.drawArrow(g, sz.width - 14, 14, sz.width - 2, 2, 10, 30);
            }
            if (this.isNorthwest) {
                GraphicsUtil.drawArrow(g, 14, 14, 2, 2, 10, 30);
            }
            if (this.isSoutheast) {
                GraphicsUtil.drawArrow(g, sz.width - 14, sz.height - 14, sz.width - 2, sz.height - 2, 10, 30);
            }
            if (this.isSouthwest) {
                GraphicsUtil.drawArrow(g, 14, sz.height - 14, 2, sz.height - 2, 10, 30);
            }
            GraphicsUtil.switchToWidth(g, 1);
            g.setColor(Color.BLACK);
        }

        private void paintString(Graphics g, String msg) {
            Font old = g.getFont();
            g.setFont(old.deriveFont(1).deriveFont(18.0f));
            FontMetrics fm = g.getFontMetrics();
            int x = (this.getWidth() - fm.stringWidth(msg)) / 2;
            if (x < 0) {
                x = 0;
            }
            g.drawString(msg, x, this.getHeight() - 23);
            g.setFont(old);
        }
    }

    private class MyProjectListener
    implements ProjectListener,
    LibraryListener,
    CircuitListener,
    AttributeListener,
    SimulatorListener,
    Selection.Listener {
        private MyProjectListener() {
        }

        @Override
        public void projectChanged(ProjectEvent event) {
            int act = event.getAction();
            if (act == 1) {
                Canvas.this.viewport.setErrorMessage(null);
                if (Canvas.this.haloedComponent != null) {
                    Canvas.this.proj.getFrame().viewComponentAttributes(null, null);
                }
            } else if (act == 0) {
                LogisimFile file;
                LogisimFile old = (LogisimFile)event.getOldData();
                if (old != null) {
                    old.getOptions().getAttributeSet().removeAttributeListener(this);
                }
                if ((file = (LogisimFile)event.getData()) != null) {
                    AttributeSet attrs = file.getOptions().getAttributeSet();
                    attrs.addAttributeListener(this);
                    Canvas.this.loadOptions(attrs);
                    Canvas.this.mappings = file.getOptions().getMouseMappings();
                }
            } else if (act == 2) {
                Canvas.this.viewport.setErrorMessage(null);
                Tool t = event.getTool();
                if (t == null) {
                    Canvas.this.setCursor(Cursor.getDefaultCursor());
                } else {
                    Canvas.this.setCursor(t.getCursor());
                }
            }
            if (act != 4) {
                Canvas.this.completeAction();
            }
        }

        @Override
        public void libraryChanged(LibraryEvent event) {
            if (event.getAction() == 1) {
                Object t = event.getData();
                if (t instanceof AddTool) {
                    t = ((AddTool)t).getFactory();
                }
                if (t == Canvas.this.proj.getCurrentCircuit() && t != null) {
                    Canvas.this.proj.setCurrentCircuit(Canvas.this.proj.getLogisimFile().getMainCircuit());
                }
            }
        }

        @Override
        public void circuitChanged(CircuitEvent event) {
            int act = event.getAction();
            if (act == 2) {
                Component c = (Component)event.getData();
                if (c == Canvas.this.haloedComponent) {
                    Canvas.this.proj.getFrame().viewComponentAttributes(null, null);
                }
            } else if (act == 5) {
                if (Canvas.this.haloedComponent != null) {
                    Canvas.this.proj.getFrame().viewComponentAttributes(null, null);
                }
            } else if (act == 4) {
                Canvas.this.completeAction();
            }
        }

        @Override
        public void propagationCompleted(SimulatorEvent e) {
            Canvas.this.repaint();
        }

        @Override
        public void tickCompleted(SimulatorEvent e) {
            Canvas.this.waitForRepaintDone();
        }

        @Override
        public void simulatorStateChanged(SimulatorEvent e) {
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            Attribute attr = e.getAttribute();
            Object val = e.getValue();
            if (attr == Options.zoom_attr) {
                Canvas.this.zoomFactor = (Double)val;
                Canvas.this.repaint();
            } else if (attr == Options.showgrid_attr) {
                Canvas.this.showGrid = (Boolean)val;
                Canvas.this.repaint();
            } else if (attr == Options.preview_attr) {
                Canvas.this.printerView = (Boolean)val;
                Canvas.this.repaint();
            } else if (attr == Options.showghosts_attr) {
                Canvas.this.showGhosts = (Boolean)val;
                Canvas.this.repaint();
            } else if (attr == Options.showhalo_attr) {
                Canvas.this.showHalo = (Boolean)val;
                Canvas.this.repaint();
            } else if (attr == Options.showtips_attr) {
                Canvas.this.showTips = (Boolean)val;
                Canvas.this.setToolTipText(Canvas.this.showTips ? "" : null);
            }
        }

        @Override
        public void selectionChanged(Selection.Event event) {
            Canvas.this.repaint();
        }
    }

    private class MyListener
    implements MouseInputListener,
    KeyListener,
    PopupMenuListener,
    ComponentListener,
    PropertyChangeListener {
        boolean menu_on;

        private MyListener() {
            this.menu_on = false;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if ((e.getModifiers() & 28) != 0) {
                this.mouseDragged(e);
                return;
            }
            Tool tool = this.getToolFor(e);
            if (tool != null) {
                this.repairMouseEvent(e);
                tool.mouseMoved(Canvas.this, Canvas.this.getGraphics(), e);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (Canvas.this.drag_tool != null) {
                this.repairMouseEvent(e);
                Canvas.this.drag_tool.mouseDragged(Canvas.this, Canvas.this.getGraphics(), e);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (Canvas.this.drag_tool != null) {
                this.repairMouseEvent(e);
                Canvas.this.drag_tool.mouseEntered(Canvas.this, Canvas.this.getGraphics(), e);
            } else {
                Tool tool = this.getToolFor(e);
                if (tool != null) {
                    this.repairMouseEvent(e);
                    tool.mouseEntered(Canvas.this, Canvas.this.getGraphics(), e);
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (Canvas.this.drag_tool != null) {
                this.repairMouseEvent(e);
                Canvas.this.drag_tool.mouseExited(Canvas.this, Canvas.this.getGraphics(), e);
            } else {
                Tool tool = this.getToolFor(e);
                if (tool != null) {
                    this.repairMouseEvent(e);
                    tool.mouseExited(Canvas.this, Canvas.this.getGraphics(), e);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Canvas.this.viewport.setErrorMessage(null);
            Canvas.this.proj.setStartupScreen(false);
            Canvas.this.grabFocus();
            Canvas.this.drag_tool = this.getToolFor(e);
            if (Canvas.this.drag_tool != null) {
                this.repairMouseEvent(e);
                Canvas.this.drag_tool.mousePressed(Canvas.this, Canvas.this.getGraphics(), e);
            }
            Canvas.this.completeAction();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            Tool tool;
            if (Canvas.this.drag_tool != null) {
                this.repairMouseEvent(e);
                Canvas.this.drag_tool.mouseReleased(Canvas.this, Canvas.this.getGraphics(), e);
                Canvas.this.drag_tool = null;
            }
            if ((tool = Canvas.this.proj.getTool()) != null) {
                tool.mouseMoved(Canvas.this, Canvas.this.getGraphics(), e);
            }
            Canvas.this.completeAction();
        }

        private Tool getToolFor(MouseEvent e) {
            if (this.menu_on) {
                return null;
            }
            Tool ret = Canvas.this.mappings.getToolFor(e);
            if (ret == null) {
                return Canvas.this.proj.getTool();
            }
            return ret;
        }

        private void repairMouseEvent(MouseEvent e) {
            if (Canvas.this.zoomFactor != 1.0) {
                int oldx = e.getX();
                int oldy = e.getY();
                int newx = (int)Math.round((double)e.getX() / Canvas.this.zoomFactor);
                int newy = (int)Math.round((double)e.getY() / Canvas.this.zoomFactor);
                e.translatePoint(newx - oldx, newy - oldy);
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            Tool tool = Canvas.this.proj.getTool();
            if (tool != null) {
                tool.keyPressed(Canvas.this, e);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            Tool tool = Canvas.this.proj.getTool();
            if (tool != null) {
                tool.keyReleased(Canvas.this, e);
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
            Tool tool = Canvas.this.proj.getTool();
            if (tool != null) {
                tool.keyTyped(Canvas.this, e);
            }
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            this.menu_on = false;
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            this.menu_on = false;
        }

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        }

        @Override
        public void componentResized(ComponentEvent arg0) {
            Canvas.this.computeSize();
        }

        @Override
        public void componentMoved(ComponentEvent arg0) {
        }

        @Override
        public void componentShown(ComponentEvent arg0) {
        }

        @Override
        public void componentHidden(ComponentEvent arg0) {
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String prop = event.getPropertyName();
            if (prop.equals("gateShape")) {
                Canvas.this.repaint();
            }
        }
    }

}

