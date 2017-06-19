/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.gates;

import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import java.util.Arrays;
import java.util.List;
import tr.edu.metu.ceng.ceng232.gates.AndGate;
import tr.edu.metu.ceng.ceng232.gates.Buffer;
import tr.edu.metu.ceng.ceng232.gates.Constant;
import tr.edu.metu.ceng.ceng232.gates.ControlledBuffer;
import tr.edu.metu.ceng.ceng232.gates.NandGate;
import tr.edu.metu.ceng.ceng232.gates.NorGate;
import tr.edu.metu.ceng.ceng232.gates.NotGate;
import tr.edu.metu.ceng.ceng232.gates.OrGate;
import tr.edu.metu.ceng.ceng232.gates.XnorGate;
import tr.edu.metu.ceng.ceng232.gates.XorGate;

public class Gates
extends Library {
    private List tools = Arrays.asList(new AddTool(Constant.factory), new AddTool(NotGate.factory), new AddTool(Buffer.factory), new AddTool(AndGate.instance), new AddTool(OrGate.instance), new AddTool(NandGate.instance), new AddTool(NorGate.instance), new AddTool(XorGate.instance), new AddTool(XnorGate.instance), new AddTool(ControlledBuffer.bufferFactory), new AddTool(ControlledBuffer.inverterFactory));

    @Override
    public String getName() {
        return "CENG232 Gates";
    }

    @Override
    public String getDisplayName() {
        return this.getName();
    }

    @Override
    public List getTools() {
        return this.tools;
    }
}

