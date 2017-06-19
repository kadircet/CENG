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

class TFlipFlop
extends AbstractFlipFlop {
    public static TFlipFlop instance = new TFlipFlop();

    private TFlipFlop() {
        super("T Flip-Flop", Strings.getter("tFlipFlopComponent"));
    }

    @Override
    public void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        g.drawRect(x + 2, y + 2, 16, 16);
        GraphicsUtil.drawCenteredText(g, "T", x + 10, y + 8);
    }

    @Override
    protected int getNumInputs() {
        return 1;
    }

    @Override
    protected String getInputName(int index) {
        return "T";
    }

    @Override
    protected Value computeValue(Value[] inputs, Value curValue) {
        if (curValue == Value.UNKNOWN) {
            curValue = Value.FALSE;
        }
        if (inputs[0] == Value.TRUE) {
            return curValue.not();
        }
        return curValue;
    }
}

