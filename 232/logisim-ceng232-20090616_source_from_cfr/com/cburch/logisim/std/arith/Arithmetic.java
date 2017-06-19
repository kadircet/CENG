/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.arith;

import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.std.arith.Adder;
import com.cburch.logisim.std.arith.Comparator;
import com.cburch.logisim.std.arith.Divider;
import com.cburch.logisim.std.arith.Multiplier;
import com.cburch.logisim.std.arith.Negator;
import com.cburch.logisim.std.arith.Strings;
import com.cburch.logisim.std.arith.Subtractor;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import java.util.Arrays;
import java.util.List;

public class Arithmetic
extends Library {
    public static final Attribute data_attr = Attributes.forBitWidth("width", Strings.getter("arithmeticDataWidthAttr"));
    public static final Object data_dflt = BitWidth.create(1);
    private List tools = null;

    @Override
    public String getName() {
        return "Arithmetic";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("arithmeticLibrary");
    }

    @Override
    public List getTools() {
        if (this.tools == null) {
            this.tools = Arrays.asList(new AddTool(Adder.factory), new AddTool(Subtractor.factory), new AddTool(Multiplier.factory), new AddTool(Divider.factory), new AddTool(Negator.factory), new AddTool(Comparator.factory));
        }
        return this.tools;
    }
}

