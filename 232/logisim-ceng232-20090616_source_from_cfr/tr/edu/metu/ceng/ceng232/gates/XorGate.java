/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.gates;

import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.Expressions;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.tools.WireRepairData;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import com.cburch.logisim.util.StringGetter;
import java.awt.Graphics;
import javax.swing.Icon;
import tr.edu.metu.ceng.ceng232.gates.AbstractGate;
import tr.edu.metu.ceng.ceng232.gates.AbstractGateFactory;
import tr.edu.metu.ceng.ceng232.gates.DinShape;
import tr.edu.metu.ceng.ceng232.gates.OrGate;
import tr.edu.metu.ceng.ceng232.gates.Strings;

class XorGate
extends AbstractGateFactory {
    public static XorGate instance = new XorGate();
    private static final String LABEL = "1";

    private XorGate() {
        super("XOR Gate", Strings.getter("xorGateComponent"));
        this.setRectangularLabel("1");
        this.setAdditionalWidth(10);
    }

    @Override
    public Icon getIconShaped() {
        return Icons.getIcon("xorGate.gif");
    }

    @Override
    public Icon getIconRectangular() {
        return Icons.getIcon("xorGateRect.gif");
    }

    @Override
    public Icon getIconDin40700() {
        return Icons.getIcon("dinXorGate.gif");
    }

    @Override
    public void paintIconShaped(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        GraphicsUtil.drawCenteredArc(g, x + 2, y - 5, 22, -90, 53);
        GraphicsUtil.drawCenteredArc(g, x + 2, y + 23, 22, 90, -53);
        GraphicsUtil.drawCenteredArc(g, x - 10, y + 9, 16, -30, 60);
        GraphicsUtil.drawCenteredArc(g, x - 12, y + 9, 16, -30, 60);
    }

    @Override
    protected void drawInputLines(ComponentDrawContext context, AbstractGate comp, int inputs, int x, int yTop, int width, int height) {
        OrGate.instance.drawInputLines(context, comp, inputs, x, yTop, width - 10, height);
    }

    @Override
    protected void drawShape(ComponentDrawContext context, int x, int y, int width, int height) {
        Graphics g = context.getGraphics();
        OrGate.instance.drawShape(context, x, y, width - 10, width - 10);
        OrGate.instance.drawShield(g, x - width, y, width - 10, height);
    }

    @Override
    protected void drawDinShape(ComponentDrawContext context, int x, int y, int width, int height, int inputs, AbstractGate gate) {
        DinShape.draw(context, x, y, width, height, false, 2);
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
        for (int i3 = 0; i3 < ret.length; ++i3) {
            if (ret[i3] != Value.UNKNOWN) continue;
            ret[i3] = Value.FALSE;
        }
        boolean[] found = new boolean[ret.length];
        for (i = 0; i < ret.length; ++i) {
            found[i] = ret[i] == Value.TRUE;
        }
        for (i = 1; i < num_inputs; ++i) {
            Value[] other = inputs[i].getAll();
            for (int j = 0; j < other.length; ++j) {
                if (other[j] == Value.TRUE) {
                    if (ret[j] == Value.TRUE) {
                        ret[j] = Value.FALSE;
                        continue;
                    }
                    if (ret[j] != Value.FALSE || found[j]) continue;
                    found[j] = true;
                    ret[j] = Value.TRUE;
                    continue;
                }
                if (other[j] == Value.FALSE || other[j] == Value.UNKNOWN || ret[j] == Value.ERROR) continue;
                ret[j] = other[j];
            }
        }
        return Value.create(ret);
    }

    @Override
    protected boolean shouldRepairWire(Component comp, WireRepairData data) {
        return !data.getPoint().equals(comp.getLocation());
    }

    @Override
    protected Expression computeExpression(Expression[] inputs, int numInputs) {
        return XorGate.xorExpression(inputs, numInputs);
    }

    protected static Expression xorExpression(Expression[] inputs, int numInputs) {
        if (numInputs > 2) {
            throw new UnsupportedOperationException("XorGate");
        }
        Expression ret = inputs[0];
        for (int i = 1; i < numInputs; ++i) {
            ret = Expressions.xor(ret, inputs[i]);
        }
        return ret;
    }
}

