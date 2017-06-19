/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.gates;

import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.Expressions;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import com.cburch.logisim.util.StringGetter;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.Icon;
import tr.edu.metu.ceng.ceng232.gates.AbstractGate;
import tr.edu.metu.ceng.ceng232.gates.AbstractGateFactory;
import tr.edu.metu.ceng.ceng232.gates.Strings;

class OddParityGate
extends AbstractGateFactory {
    public static OddParityGate instance = new OddParityGate();
    private static final String LABEL = "2k+1";

    private OddParityGate() {
        super("Odd Parity", Strings.getter("oddParityComponent"));
        this.setRectangularLabel("2k+1");
    }

    @Override
    public Icon getIconShaped() {
        return this.getIconRectangular();
    }

    @Override
    public Icon getIconRectangular() {
        return Icons.getIcon("parityOddGate.gif");
    }

    @Override
    public Icon getIconDin40700() {
        return this.getIconRectangular();
    }

    @Override
    public void paintIconShaped(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        this.paintIconRectangular(context, x, y, attrs);
    }

    @Override
    public void paintIconRectangular(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        g.setColor(Color.black);
        g.drawRect(x + 1, y + 2, 16, 16);
        Font old = g.getFont();
        g.setFont(old.deriveFont(9.0f));
        GraphicsUtil.drawCenteredText(g, "2k", x + 9, y + 6);
        GraphicsUtil.drawCenteredText(g, "+1", x + 9, y + 13);
        g.setFont(old);
    }

    protected boolean shouldDrawShaped(ComponentDrawContext c) {
        return false;
    }

    @Override
    protected void drawShape(ComponentDrawContext context, int x, int y, int width, int height) {
        this.drawRectangular(context, x, y, width, height);
    }

    @Override
    protected void drawDinShape(ComponentDrawContext context, int x, int y, int width, int height, int inputs, AbstractGate gate) {
        this.drawRectangular(context, x, y, width, height);
    }

    @Override
    protected Value computeOutput(Value[] inputs, int num_inputs) {
        int i;
        if (num_inputs == 0) {
            return Value.NIL;
        }
        boolean allUnknown = true;
        for (int i2 = 0; i2 < inputs.length; ++i2) {
            if (inputs[i2].isUnknown()) continue;
            allUnknown = false;
            break;
        }
        if (allUnknown) {
            return inputs[0];
        }
        Value[] ret = inputs[0].getAll();
        for (i = 0; i < ret.length; ++i) {
            if (ret[i] != Value.UNKNOWN) continue;
            ret[i] = Value.FALSE;
        }
        for (i = 1; i < num_inputs; ++i) {
            Value[] other = inputs[i].getAll();
            for (int j = 0; j < other.length; ++j) {
                if (other[j] == Value.TRUE) {
                    if (ret[j] == Value.TRUE) {
                        ret[j] = Value.FALSE;
                        continue;
                    }
                    if (ret[j] != Value.FALSE) continue;
                    ret[j] = Value.TRUE;
                    continue;
                }
                if (other[j] == Value.FALSE || other[j] == Value.UNKNOWN || ret[j] == Value.ERROR) continue;
                ret[j] = other[j];
            }
        }
        Value r = Value.create(ret);
        return r;
    }

    @Override
    protected Expression computeExpression(Expression[] inputs, int numInputs) {
        Expression ret = inputs[0];
        for (int i = 1; i < numInputs; ++i) {
            ret = Expressions.xor(ret, inputs[i]);
        }
        return ret;
    }
}

