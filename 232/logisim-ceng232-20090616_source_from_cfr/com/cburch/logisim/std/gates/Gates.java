/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.gates;

import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.std.gates.AndGate;
import com.cburch.logisim.std.gates.Buffer;
import com.cburch.logisim.std.gates.Constant;
import com.cburch.logisim.std.gates.ControlledBuffer;
import com.cburch.logisim.std.gates.EvenParityGate;
import com.cburch.logisim.std.gates.NandGate;
import com.cburch.logisim.std.gates.NorGate;
import com.cburch.logisim.std.gates.NotGate;
import com.cburch.logisim.std.gates.OddParityGate;
import com.cburch.logisim.std.gates.OrGate;
import com.cburch.logisim.std.gates.Strings;
import com.cburch.logisim.std.gates.XnorGate;
import com.cburch.logisim.std.gates.XorGate;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import java.util.Arrays;
import java.util.List;

public class Gates
extends Library {
    private List tools = Arrays.asList(new AddTool(Constant.factory), new AddTool(NotGate.factory), new AddTool(Buffer.factory), new AddTool(AndGate.instance), new AddTool(OrGate.instance), new AddTool(NandGate.instance), new AddTool(NorGate.instance), new AddTool(XorGate.instance), new AddTool(XnorGate.instance), new AddTool(OddParityGate.instance), new AddTool(EvenParityGate.instance), new AddTool(ControlledBuffer.bufferFactory), new AddTool(ControlledBuffer.inverterFactory));

    @Override
    public String getName() {
        return "Gates";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("gatesLibrary");
    }

    @Override
    public List getTools() {
        return this.tools;
    }
}

