/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.memory;

import com.cburch.hex.HexModel;
import com.cburch.hex.HexModelListener;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.gui.hex.HexFrame;
import com.cburch.logisim.gui.log.Loggable;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.std.memory.Mem;
import com.cburch.logisim.std.memory.MemContents;
import com.cburch.logisim.std.memory.MemState;
import com.cburch.logisim.std.memory.RamFactory;
import com.cburch.logisim.std.memory.Strings;
import com.cburch.logisim.util.IntegerFactory;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class Ram
extends Mem {
    private static final int NUM_INPUTS = 6;
    private static final int WE = 3;
    private static final int OE = 4;
    private static final int CLR = 5;
    private static Object[][] logOptions = new Object[9][];

    Ram(Location loc, AttributeSet attrs) {
        super(loc, attrs, 6);
    }

    @Override
    void setPins() {
        super.setPins();
        Location loc = this.getLocation();
        this.setEnd(3, loc.translate(-70, 40), BitWidth.ONE, 1);
        this.setEnd(4, loc.translate(-50, 40), BitWidth.ONE, 1);
        this.setEnd(5, loc.translate(-30, 40), BitWidth.ONE, 1);
    }

    @Override
    public ComponentFactory getFactory() {
        return RamFactory.INSTANCE;
    }

    @Override
    public void propagate(CircuitState state) {
        boolean shouldClear;
        RamState myState = (RamState)this.getState(state);
        BitWidth dataBits = (BitWidth)this.getAttributeSet().getValue(DATA_ATTR);
        Value addrValue = state.getValue(this.getEndLocation(1));
        boolean chipSelect = state.getValue(this.getEndLocation(2)) != Value.FALSE;
        boolean writeEnabled = state.getValue(this.getEndLocation(3)) == Value.TRUE;
        boolean outputEnabled = state.getValue(this.getEndLocation(4)) != Value.FALSE;
        boolean bl = shouldClear = state.getValue(this.getEndLocation(5)) == Value.TRUE;
        if (!chipSelect) {
            myState.setCurrent(-1);
            state.setValue(this.getEndLocation(0), Value.createUnknown(dataBits), this, 10);
            return;
        }
        if (shouldClear) {
            myState.getContents().clear();
        }
        int addr = addrValue.toIntValue();
        if (!addrValue.isFullyDefined() || addr < 0) {
            return;
        }
        if ((long)addr != myState.getCurrent()) {
            myState.setCurrent(addr);
            myState.scrollToShow(addr);
        }
        if (!shouldClear && !outputEnabled && writeEnabled && myState.getLastWriteEnable() == Value.FALSE) {
            Value dataValue = state.getValue(this.getEndLocation(0));
            myState.getContents().set((long)addr, dataValue.toIntValue());
        }
        myState.setLastWriteEnable(writeEnabled ? Value.TRUE : Value.FALSE);
        if (outputEnabled) {
            state.setValue(this.getEndLocation(0), Value.createKnown(dataBits, myState.getContents().get(addr)), this, 10);
        } else {
            state.setValue(this.getEndLocation(0), Value.createUnknown(dataBits), this, 10);
        }
    }

    @Override
    public void draw(ComponentDrawContext context) {
        super.draw(context);
        context.drawClock(this, 3, Direction.NORTH);
        context.drawPin(this, 4, Strings.get("ramOELabel"), Direction.SOUTH);
        context.drawPin(this, 5, Strings.get("ramClrLabel"), Direction.SOUTH);
    }

    @Override
    public Object getFeature(Object key) {
        if (key == Loggable.class) {
            return this;
        }
        return super.getFeature(key);
    }

    @Override
    public String getToolTip(ComponentUserEvent e) {
        int end = -1;
        for (int i = this.getEnds().size() - 1; i >= 0; --i) {
            if (this.getEndLocation(i).manhattanDistanceTo(e.getX(), e.getY()) >= 10) continue;
            end = i;
            break;
        }
        switch (end) {
            case 0: {
                return Strings.get("memDataTip");
            }
            case 1: {
                return Strings.get("memAddrTip");
            }
            case 2: {
                return Strings.get("memCSTip");
            }
            case 3: {
                return Strings.get("ramWETip");
            }
            case 4: {
                return Strings.get("ramOETip");
            }
            case 5: {
                return Strings.get("ramClrTip");
            }
        }
        return null;
    }

    @Override
    MemState getState(CircuitState state) {
        BitWidth addrBits = (BitWidth)this.getAttributeSet().getValue(ADDR_ATTR);
        BitWidth dataBits = (BitWidth)this.getAttributeSet().getValue(DATA_ATTR);
        RamState myState = (RamState)state.getData(this);
        if (myState == null) {
            MemContents contents = MemContents.create(addrBits.getWidth(), dataBits.getWidth());
            myState = new RamState(this, contents, new Mem.MemListener());
            state.setData(this, myState);
        } else {
            myState.setRam(this);
        }
        return myState;
    }

    public Object[] getLogOptions(CircuitState state) {
        int addrBits = ((BitWidth)this.getAttributeSet().getValue(ADDR_ATTR)).getWidth();
        if (addrBits >= logOptions.length) {
            addrBits = logOptions.length - 1;
        }
        Object[][] arrobject = logOptions;
        synchronized (arrobject) {
            Object[] ret = logOptions[addrBits];
            if (ret == null) {
                Ram.logOptions[addrBits] = ret = new Object[1 << addrBits];
                for (int i = 0; i < ret.length; ++i) {
                    ret[i] = IntegerFactory.create(i);
                }
            }
            return ret;
        }
    }

    public String getLogName(Object option) {
        if (option instanceof Integer) {
            return this.getFactory().getDisplayName() + this.getLocation() + "[" + option + "]";
        }
        return null;
    }

    public Value getLogValue(CircuitState state, Object option) {
        if (option instanceof Integer) {
            MemState s = this.getState(state);
            int addr = (Integer)option;
            return Value.createKnown(BitWidth.create(s.getDataBits()), s.getContents().get(addr));
        }
        return Value.NIL;
    }

    @Override
    HexFrame getHexFrame(Project proj, CircuitState circState) {
        RamState state = (RamState)this.getState(circState);
        return state.getHexFrame(proj);
    }

    private static class RamState
    extends MemState
    implements AttributeListener {
        private Ram parent;
        private Mem.MemListener listener;
        private HexFrame hexFrame = null;
        private Value lastWE = Value.FALSE;

        RamState(Ram parent, MemContents contents, Mem.MemListener listener) {
            super(contents);
            this.parent = parent;
            this.listener = listener;
            if (parent != null) {
                parent.getAttributeSet().addAttributeListener(this);
            }
            contents.addHexModelListener(listener);
        }

        void setRam(Ram value) {
            if (this.parent == value) {
                return;
            }
            if (this.parent != null) {
                this.parent.getAttributeSet().removeAttributeListener(this);
            }
            this.parent = value;
            if (value != null) {
                value.getAttributeSet().addAttributeListener(this);
            }
        }

        @Override
        public Object clone() {
            RamState ret = (RamState)super.clone();
            ret.parent = null;
            ret.getContents().addHexModelListener(this.listener);
            return ret;
        }

        public HexFrame getHexFrame(Project proj) {
            if (this.hexFrame == null) {
                this.hexFrame = new HexFrame(proj, this.getContents());
                this.hexFrame.addWindowListener(new WindowAdapter(){

                    @Override
                    public void windowClosed(WindowEvent e) {
                        RamState.this.hexFrame = null;
                    }
                });
            }
            return this.hexFrame;
        }

        public Value getLastWriteEnable() {
            return this.lastWE;
        }

        public void setLastWriteEnable(Value value) {
            this.lastWE = value;
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            AttributeSet attrs = e.getSource();
            BitWidth addrBits = (BitWidth)attrs.getValue(Mem.ADDR_ATTR);
            BitWidth dataBits = (BitWidth)attrs.getValue(Mem.DATA_ATTR);
            this.getContents().setDimensions(addrBits.getWidth(), dataBits.getWidth());
        }

    }

}

