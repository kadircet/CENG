/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.legacy;

import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.legacy.DFlipFlop;
import com.cburch.logisim.legacy.JKFlipFlop;
import com.cburch.logisim.legacy.Register;
import com.cburch.logisim.legacy.Strings;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import java.util.Arrays;
import java.util.List;

public class Legacy
extends Library {
    private List tools = null;

    @Override
    public String getName() {
        return "Legacy";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("legacyLibrary");
    }

    @Override
    public List getTools() {
        if (this.tools == null) {
            this.tools = Arrays.asList(new AddTool(DFlipFlop.instance), new AddTool(JKFlipFlop.instance), new AddTool(Register.instance));
        }
        return this.tools;
    }
}

