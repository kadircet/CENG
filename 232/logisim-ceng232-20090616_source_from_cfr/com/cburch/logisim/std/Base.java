/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std;

import com.cburch.logisim.circuit.ClockFactory;
import com.cburch.logisim.circuit.PinFactory;
import com.cburch.logisim.circuit.ProbeFactory;
import com.cburch.logisim.circuit.SplitterFactory;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.std.Strings;
import com.cburch.logisim.std.TextClass;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.MenuTool;
import com.cburch.logisim.tools.PokeTool;
import com.cburch.logisim.tools.SelectTool;
import com.cburch.logisim.tools.TextTool;
import com.cburch.logisim.tools.WiringTool;
import java.util.Arrays;
import java.util.List;

public class Base
extends Library {
    private List tools = Arrays.asList(new PokeTool(), new SelectTool(), new WiringTool(), new TextTool(), new MenuTool(), new AddTool(SplitterFactory.instance), new AddTool(PinFactory.instance), new AddTool(ProbeFactory.instance), new AddTool(ClockFactory.instance), new AddTool(TextClass.instance));

    Base() {
    }

    @Override
    public String getName() {
        return "Base";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("baseLibrary");
    }

    @Override
    public List getTools() {
        return this.tools;
    }
}

