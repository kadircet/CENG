/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.memory;

import com.cburch.hex.HexModel;
import com.cburch.hex.HexModelListener;
import com.cburch.logisim.data.AbstractAttributeSet;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.gui.hex.HexFrame;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.std.memory.Mem;
import com.cburch.logisim.std.memory.MemContents;
import com.cburch.logisim.std.memory.RomContentsListener;
import com.cburch.logisim.std.memory.RomFactory;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;

class RomAttributes
extends AbstractAttributeSet {
    private static List ATTRIBUTES = Arrays.asList(Mem.ADDR_ATTR, Mem.DATA_ATTR, RomFactory.CONTENTS_ATTR);
    private static WeakHashMap listenerRegistry = new WeakHashMap();
    private static WeakHashMap windowRegistry = new WeakHashMap();
    private BitWidth addrBits = BitWidth.create(8);
    private BitWidth dataBits = BitWidth.create(8);
    private MemContents contents = MemContents.create(this.addrBits.getWidth(), this.dataBits.getWidth());

    static void register(MemContents value, Project proj) {
        if (proj == null || listenerRegistry.containsKey(value)) {
            return;
        }
        RomContentsListener l = new RomContentsListener(proj);
        value.addHexModelListener(l);
        listenerRegistry.put(value, l);
    }

    static HexFrame getHexFrame(MemContents value, Project proj) {
        WeakHashMap weakHashMap = windowRegistry;
        synchronized (weakHashMap) {
            HexFrame ret = (HexFrame)windowRegistry.get(value);
            if (ret == null) {
                ret = new HexFrame(proj, value);
                windowRegistry.put(value, ret);
            }
            return ret;
        }
    }

    RomAttributes() {
    }

    void setProject(Project proj) {
        RomAttributes.register(this.contents, proj);
    }

    @Override
    protected void copyInto(AbstractAttributeSet dest) {
        RomAttributes d = (RomAttributes)dest;
        d.addrBits = this.addrBits;
        d.dataBits = this.dataBits;
        d.contents = (MemContents)this.contents.clone();
    }

    @Override
    public List getAttributes() {
        return ATTRIBUTES;
    }

    @Override
    public Object getValue(Attribute attr) {
        if (attr == Mem.ADDR_ATTR) {
            return this.addrBits;
        }
        if (attr == Mem.DATA_ATTR) {
            return this.dataBits;
        }
        if (attr == RomFactory.CONTENTS_ATTR) {
            return this.contents;
        }
        return null;
    }

    @Override
    public void setValue(Attribute attr, Object value) {
        if (attr == Mem.ADDR_ATTR) {
            this.addrBits = (BitWidth)value;
            this.contents.setDimensions(this.addrBits.getWidth(), this.dataBits.getWidth());
        } else if (attr == Mem.DATA_ATTR) {
            this.dataBits = (BitWidth)value;
            this.contents.setDimensions(this.addrBits.getWidth(), this.dataBits.getWidth());
        } else if (attr == RomFactory.CONTENTS_ATTR) {
            this.contents = (MemContents)value;
        }
        this.fireAttributeValueChanged(attr, value);
    }
}

