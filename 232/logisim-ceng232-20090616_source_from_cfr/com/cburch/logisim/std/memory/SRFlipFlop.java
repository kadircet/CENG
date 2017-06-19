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

class SRFlipFlop
extends AbstractFlipFlop {
    public static SRFlipFlop instance = new SRFlipFlop();

    private SRFlipFlop() {
        super("S-R Flip-Flop", Strings.getter("srFlipFlopComponent"));
    }

    @Override
    public void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        g.drawRect(x + 2, y + 2, 16, 16);
        GraphicsUtil.drawCenteredText(g, "SR", x + 10, y + 8);
    }

    @Override
    protected int getNumInputs() {
        return 2;
    }

    @Override
    protected String getInputName(int index) {
        return index == 0 ? "S" : "R";
    }

    @Override
    protected Value computeValue(Value[] inputs, Value curValue) {
        if (inputs[0] == Value.FALSE) {
            if (inputs[1] == Value.FALSE) {
                return curValue;
            }
            if (inputs[1] == Value.TRUE) {
                return Value.FALSE;
            }
        } else if (inputs[0] == Value.TRUE) {
            if (inputs[1] == Value.FALSE) {
                return Value.TRUE;
            }
            if (inputs[1] == Value.TRUE) {
                return Value.ERROR;
            }
        }
        return Value.UNKNOWN;
    }
}

