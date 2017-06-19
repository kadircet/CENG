/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.memory;

import com.cburch.hex.HexModel;
import com.cburch.hex.HexModelListener;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentEvent;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.gui.hex.HexFrame;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.std.memory.MemContents;
import com.cburch.logisim.std.memory.MemMenu;
import com.cburch.logisim.std.memory.MemState;
import com.cburch.logisim.std.memory.Rom;
import com.cburch.logisim.std.memory.Strings;
import com.cburch.logisim.tools.AbstractCaret;
import com.cburch.logisim.tools.Caret;
import com.cburch.logisim.tools.MenuExtender;
import com.cburch.logisim.tools.Pokable;
import com.cburch.logisim.tools.ToolTipMaker;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.StringUtil;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JPopupMenu;

abstract class Mem
extends ManagedComponent
implements AttributeListener,
Pokable,
MenuExtender,
ToolTipMaker {
    public static final Attribute ADDR_ATTR = Attributes.forBitWidth("addrWidth", Strings.getter("ramAddrWidthAttr"), 2, 24);
    public static final Attribute DATA_ATTR = Attributes.forBitWidth("dataWidth", Strings.getter("ramDataWidthAttr"));
    static final Bounds OFFSET_BOUNDS = Bounds.create(-140, -40, 140, 80);
    static final int DATA = 0;
    static final int ADDR = 1;
    static final int CS = 2;
    static final int DELAY = 10;
    private File currentImageFile = null;

    Mem(Location loc, AttributeSet attrs, int numInputs) {
        super(loc, attrs, numInputs);
        attrs.addAttributeListener(this);
        this.setPins();
    }

    void setPins() {
        Location loc = this.getLocation();
        BitWidth addrBits = (BitWidth)this.getAttributeSet().getValue(ADDR_ATTR);
        BitWidth dataBits = (BitWidth)this.getAttributeSet().getValue(DATA_ATTR);
        this.setEnd(0, loc, dataBits, 3);
        this.setEnd(1, loc.translate(-140, 0), addrBits, 1);
        this.setEnd(2, loc.translate(-90, 40), BitWidth.ONE, 1);
    }

    @Override
    public abstract ComponentFactory getFactory();

    @Override
    public abstract void propagate(CircuitState var1);

    @Override
    public void attributeListChanged(AttributeEvent e) {
    }

    @Override
    public void attributeValueChanged(AttributeEvent e) {
        Attribute attr = e.getAttribute();
        if (attr == ADDR_ATTR || attr == DATA_ATTR) {
            this.setPins();
        }
    }

    @Override
    public void draw(ComponentDrawContext context) {
        Graphics g = context.getGraphics();
        Bounds bds = this.getBounds();
        context.drawBounds(this);
        if (context.getShowState()) {
            MemState state = this.getState(context.getCircuitState());
            state.paint(context.getGraphics(), bds.getX(), bds.getY());
        } else {
            BitWidth addr = (BitWidth)this.getAttributeSet().getValue(ADDR_ATTR);
            int addrBits = addr.getWidth();
            int bytes = 1 << addrBits;
            String label = this instanceof Rom ? (addrBits >= 30 ? StringUtil.format(Strings.get("romGigabyteLabel"), "" + (bytes >>> 30)) : (addrBits >= 20 ? StringUtil.format(Strings.get("romMegabyteLabel"), "" + (bytes >> 20)) : (addrBits >= 10 ? StringUtil.format(Strings.get("romKilobyteLabel"), "" + (bytes >> 10)) : StringUtil.format(Strings.get("romByteLabel"), "" + bytes)))) : (addrBits >= 30 ? StringUtil.format(Strings.get("ramGigabyteLabel"), "" + (bytes >>> 30)) : (addrBits >= 20 ? StringUtil.format(Strings.get("ramMegabyteLabel"), "" + (bytes >> 20)) : (addrBits >= 10 ? StringUtil.format(Strings.get("ramKilobyteLabel"), "" + (bytes >> 10)) : StringUtil.format(Strings.get("ramByteLabel"), "" + bytes))));
            GraphicsUtil.drawCenteredText(g, label, bds.getX() + bds.getWidth() / 2, bds.getY() + bds.getHeight() / 2);
        }
        context.drawPin(this, 0, Strings.get("ramDataLabel"), Direction.WEST);
        context.drawPin(this, 1, Strings.get("ramAddrLabel"), Direction.EAST);
        g.setColor(Color.GRAY);
        context.drawPin(this, 2, Strings.get("ramCSLabel"), Direction.SOUTH);
    }

    @Override
    public Object getFeature(Object key) {
        if (key == Pokable.class) {
            return this;
        }
        if (key == MenuExtender.class) {
            return this;
        }
        if (key == ToolTipMaker.class) {
            return this;
        }
        return super.getFeature(key);
    }

    @Override
    public Caret getPokeCaret(ComponentUserEvent event) {
        Bounds bds = this.getBounds();
        CircuitState circState = event.getCircuitState();
        MemState state = this.getState(circState);
        long addr = state.getAddressAt(event.getX() - bds.getX(), event.getY() - bds.getY());
        if (addr < 0) {
            return new AddrCaret(state, circState);
        }
        state.setCursor(addr);
        return new PokeCaret(state, circState);
    }

    @Override
    public void configureMenu(JPopupMenu menu, Project proj) {
        menu.addSeparator();
        MemMenu compMenu = new MemMenu(proj, this);
        compMenu.appendTo(menu);
    }

    @Override
    public abstract String getToolTip(ComponentUserEvent var1);

    File getCurrentImage() {
        return this.currentImageFile;
    }

    void setCurrentImage(File value) {
        this.currentImageFile = value;
    }

    abstract MemState getState(CircuitState var1);

    abstract HexFrame getHexFrame(Project var1, CircuitState var2);

    class MemListener
    implements HexModelListener {
        MemListener() {
        }

        @Override
        public void metainfoChanged(HexModel source) {
        }

        @Override
        public void bytesChanged(HexModel source, long start, long numBytes, int[] values) {
            Mem.this.fireComponentInvalidated(new ComponentEvent(Mem.this));
        }
    }

    private class AddrCaret
    extends AbstractCaret {
        CircuitState circState;
        MemState state;

        AddrCaret(MemState state, CircuitState circState) {
            this.state = state;
            this.circState = circState;
            this.setBounds(state.getBounds(-1, Mem.this.getBounds()));
        }

        @Override
        public void draw(Graphics g) {
            Bounds bds = this.getBounds(g);
            g.setColor(Color.RED);
            g.drawRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
            g.setColor(Color.BLACK);
        }

        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            int val = Character.digit(e.getKeyChar(), 16);
            if (val >= 0) {
                long newScroll = this.state.getScroll() * 16 + (long)val & this.state.getLastAddress();
                this.state.setScroll(newScroll);
            } else if (c == ' ') {
                this.state.setScroll(this.state.getScroll() + (long)((this.state.getRows() - 1) * this.state.getColumns()));
            } else if (c == '\r' || c == '\n') {
                this.state.setScroll(this.state.getScroll() + (long)this.state.getColumns());
            } else if (c == '\b' || c == '') {
                this.state.setScroll(this.state.getScroll() - (long)this.state.getColumns());
            }
        }
    }

    private class PokeCaret
    extends AbstractCaret {
        CircuitState circState;
        MemState state;
        int initValue;
        int curValue;

        PokeCaret(MemState state, CircuitState circState) {
            this.state = state;
            this.circState = circState;
            this.computeBounds();
            this.curValue = this.initValue = state.getContents().get(state.getCursor());
        }

        private void computeBounds() {
            this.setBounds(this.state.getBounds(this.state.getCursor(), Mem.this.getBounds()));
        }

        @Override
        public void draw(Graphics g) {
            Bounds bds = this.getBounds(g);
            g.setColor(Color.RED);
            g.drawRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
            g.setColor(Color.BLACK);
        }

        @Override
        public void stopEditing() {
            this.state.setCursor(-1);
        }

        @Override
        public void cancelEditing() {
            this.state.getContents().set(this.state.getCursor(), this.initValue);
            this.state.setCursor(-1);
        }

        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            int val = Character.digit(e.getKeyChar(), 16);
            if (val >= 0) {
                this.curValue = this.curValue * 16 + val;
                this.state.getContents().set(this.state.getCursor(), this.curValue);
                Mem.this.propagate(this.circState);
            } else if (c == ' ' || c == '\t') {
                this.moveTo(this.state.getCursor() + 1);
            } else if (c == '\r' || c == '\n') {
                this.moveTo(this.state.getCursor() + (long)this.state.getColumns());
            } else if (c == '\b' || c == '') {
                this.moveTo(this.state.getCursor() - 1);
            }
        }

        private void moveTo(long addr) {
            if (this.state.isValidAddr(addr)) {
                this.state.setCursor(addr);
                this.state.scrollToShow(addr);
                this.curValue = this.initValue = this.state.getContents().get(addr);
                this.computeBounds();
            }
        }
    }

}

