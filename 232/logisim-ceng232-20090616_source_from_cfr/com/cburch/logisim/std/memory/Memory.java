/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.memory;

import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.std.memory.DFlipFlop;
import com.cburch.logisim.std.memory.JKFlipFlop;
import com.cburch.logisim.std.memory.RamFactory;
import com.cburch.logisim.std.memory.Register;
import com.cburch.logisim.std.memory.RomFactory;
import com.cburch.logisim.std.memory.SRFlipFlop;
import com.cburch.logisim.std.memory.Strings;
import com.cburch.logisim.std.memory.TFlipFlop;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import java.util.Arrays;
import java.util.List;

public class Memory
extends Library {
    protected static final int DELAY = 5;
    private List tools = null;

    @Override
    public String getName() {
        return "Memory";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("memoryLibrary");
    }

    @Override
    public List getTools() {
        if (this.tools == null) {
            this.tools = Arrays.asList(new AddTool(DFlipFlop.instance), new AddTool(TFlipFlop.instance), new AddTool(JKFlipFlop.instance), new AddTool(SRFlipFlop.instance), new AddTool(Register.factory), new AddTool(RamFactory.INSTANCE), new AddTool(RomFactory.INSTANCE));
        }
        return this.tools;
    }
}

