/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.memory;

import com.cburch.hex.HexModelListener;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.gui.hex.HexFrame;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.std.memory.Mem;
import com.cburch.logisim.std.memory.MemContents;
import com.cburch.logisim.std.memory.MemState;
import com.cburch.logisim.std.memory.RomAttributes;
import com.cburch.logisim.std.memory.RomFactory;
import com.cburch.logisim.std.memory.Strings;
import com.cburch.logisim.tools.Caret;
import javax.swing.JPopupMenu;

class Rom
extends Mem {
    private static final int NUM_INPUTS = 3;
    private Mem.MemListener memListener;

    Rom(Location loc, AttributeSet attrs) {
        super(loc, attrs, 3);
        this.memListener = new Mem.MemListener();
        MemContents contents = this.getMemContents();
        contents.addHexModelListener(this.memListener);
    }

    @Override
    public ComponentFactory getFactory() {
        return RomFactory.INSTANCE;
    }

    @Override
    public void propagate(CircuitState state) {
        boolean chipSelect;
        MemState myState = this.getState(state);
        BitWidth dataBits = (BitWidth)this.getAttributeSet().getValue(DATA_ATTR);
        Value addrValue = state.getValue(this.getEndLocation(1));
        boolean bl = chipSelect = state.getValue(this.getEndLocation(2)) != Value.FALSE;
        if (!chipSelect) {
            myState.setCurrent(-1);
            state.setValue(this.getEndLocation(0), Value.createUnknown(dataBits), this, 10);
            return;
        }
        int addr = addrValue.toIntValue();
        if (!addrValue.isFullyDefined() || addr < 0) {
            return;
        }
        if ((long)addr != myState.getCurrent()) {
            myState.setCurrent(addr);
            myState.scrollToShow(addr);
        }
        state.setValue(this.getEndLocation(0), Value.createKnown(dataBits, myState.getContents().get(addr)), this, 10);
    }

    @Override
    public Caret getPokeCaret(ComponentUserEvent event) {
        Canvas canvas = event.getCanvas();
        if (canvas != null) {
            RomAttributes attrs = (RomAttributes)this.getAttributeSet();
            attrs.setProject(canvas.getProject());
        }
        return super.getPokeCaret(event);
    }

    @Override
    public void configureMenu(JPopupMenu menu, Project proj) {
        RomAttributes attrs = (RomAttributes)this.getAttributeSet();
        attrs.setProject(proj);
        super.configureMenu(menu, proj);
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
        }
        return null;
    }

    MemContents getMemContents() {
        return (MemContents)this.getAttributeSet().getValue(RomFactory.CONTENTS_ATTR);
    }

    @Override
    MemState getState(CircuitState state) {
        MemState ret = (MemState)state.getData(this);
        if (ret == null) {
            MemContents contents = this.getMemContents();
            ret = new MemState(contents);
            state.setData(this, ret);
        }
        return ret;
    }

    @Override
    HexFrame getHexFrame(Project proj, CircuitState state) {
        return RomAttributes.getHexFrame(this.getMemContents(), proj);
    }
}

