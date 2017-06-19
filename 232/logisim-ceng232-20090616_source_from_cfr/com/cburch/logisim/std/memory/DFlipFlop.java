/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.memory;

import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.std.memory.AbstractFlipFlop;
import com.cburch.logisim.std.memory.Strings;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.StringGetter;
import java.awt.Graphics;

class DFlipFlop
extends AbstractFlipFlop {
    public static DFlipFlop instance = new DFlipFlop();

    private DFlipFlop() {
        super("D Flip-Flop", Strings.getter("dFlipFlopComponent"));
    }

    @Override
    public void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        g.drawRect(x + 2, y + 2, 16, 16);
        GraphicsUtil.drawCenteredText(g, "D", x + 10, y + 8);
    }

    @Override
    protected int getNumInputs() {
        return 1;
    }

    @Override
    protected String getInputName(int index) {
        return "D";
    }

    @Override
    protected Value computeValue(Value[] inputs, Value curValue) {
        return inputs[0];
    }
}

