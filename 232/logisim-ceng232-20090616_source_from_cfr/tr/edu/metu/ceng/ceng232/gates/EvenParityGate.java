/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.gates;

import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.Expressions;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.util.Icons;
import com.cburch.logisim.util.StringGetter;
import javax.swing.Icon;
import tr.edu.metu.ceng.ceng232.gates.AbstractGate;
import tr.edu.metu.ceng.ceng232.gates.AbstractGateFactory;
import tr.edu.metu.ceng.ceng232.gates.Strings;

class EvenParityGate
extends AbstractGateFactory {
    public static EvenParityGate instance = new EvenParityGate();
    private static final String LABEL = "2k";

    private EvenParityGate() {
        super("Even Parity", Strings.getter("evenParityComponent"));
        this.setRectangularLabel("2k");
    }

    @Override
    public Icon getIconShaped() {
        return this.getIconRectangular();
    }

    @Override
    public Icon getIconRectangular() {
        return Icons.getIcon("parityEvenGate.gif");
    }

    @Override
    public Icon getIconDin40700() {
        return this.getIconRectangular();
    }

    @Override
    public void paintIconShaped(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        this.paintIconRectangular(context, x, y, attrs);
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
        Value[] ret = inputs[0].not().getAll();
        for (i = 0; i < ret.length; ++i) {
            if (ret[i] != Value.UNKNOWN) continue;
            ret[i] = Value.TRUE;
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
        return Expressions.not(ret);
    }
}

