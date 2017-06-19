/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.gates;

import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.Expressions;
import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.tools.WireRepairData;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import com.cburch.logisim.util.StringGetter;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import javax.swing.Icon;
import tr.edu.metu.ceng.ceng232.gates.AbstractGate;
import tr.edu.metu.ceng.ceng232.gates.AbstractGateFactory;
import tr.edu.metu.ceng.ceng232.gates.DinShape;
import tr.edu.metu.ceng.ceng232.gates.Strings;

class OrGate
extends AbstractGateFactory {
    public static OrGate instance = new OrGate();
    private static final String LABEL = ">0";

    private OrGate() {
        super("OR Gate", Strings.getter("orGateComponent"));
        this.setRectangularLabel(">0");
    }

    @Override
    public Icon getIconShaped() {
        return Icons.getIcon("orGate.gif");
    }

    @Override
    public Icon getIconRectangular() {
        return Icons.getIcon("orGateRect.gif");
    }

    @Override
    public Icon getIconDin40700() {
        return Icons.getIcon("dinOrGate.gif");
    }

    @Override
    public void paintIconShaped(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        GraphicsUtil.drawCenteredArc(g, x, y - 5, 22, -90, 53);
        GraphicsUtil.drawCenteredArc(g, x, y + 23, 22, 90, -53);
        GraphicsUtil.drawCenteredArc(g, x - 12, y + 9, 16, -30, 60);
    }

    @Override
    protected void drawInputLines(ComponentDrawContext context, AbstractGate comp, int inputs, int x, int yTop, int width, int height) {
        int center_break = (inputs - width / 10) / 2;
        int wing_len = (height - width) / 2;
        int dy = 10;
        if (dy * inputs < height) {
            dy = (height - 10) / (inputs - 1);
        }
        CircuitState state = context.getCircuitState();
        Graphics g = context.getGraphics();
        GraphicsUtil.switchToWidth(g, 3);
        int y0 = yTop;
        int i = 0;
        for (int y1 = yTop + (inputs - 1) * dy; y0 <= y1; y0 += dy, y1 -= dy) {
            int r;
            int off_dy;
            if (i < center_break) {
                off_dy = (- wing_len) / 2 + 10 * i + 5;
                r = wing_len;
            } else {
                off_dy = (- width) / 2 + (i - center_break) * 10 + 5;
                r = width;
            }
            double lenf = Math.sqrt(r * r - off_dy * off_dy) - (double)r * Math.sqrt(3.0) / 2.0;
            int len = (int)Math.round(lenf);
            if (len > 1) {
                Location loc0 = comp.getEndLocation(i + 1);
                if (!context.isPrintView() || context.getCircuit().getComponents(loc0).size() > 1) {
                    if (context.getShowState()) {
                        g.setColor(state.getValue(loc0).getColor());
                    }
                    g.drawLine(x, y0, x + len - 1, y0);
                }
                if (y0 != y1) {
                    Location loc1 = comp.getEndLocation(inputs - i);
                    if (!context.isPrintView() || context.getCircuit().getComponents(loc1).size() > 1) {
                        if (context.getShowState()) {
                            g.setColor(state.getValue(loc1).getColor());
                        }
                        g.drawLine(x, y1, x + len - 1, y1);
                    }
                }
            }
            ++i;
        }
        g.setColor(Color.black);
    }

    @Override
    protected void drawShape(ComponentDrawContext context, int x, int y, int width, int height) {
        Graphics g = context.getGraphics();
        GraphicsUtil.switchToWidth(g, 2);
        if (width == 30) {
            GraphicsUtil.drawCenteredArc(g, x - 30, y - 21, 36, -90, 53);
            GraphicsUtil.drawCenteredArc(g, x - 30, y + 21, 36, 90, -53);
        } else {
            GraphicsUtil.drawCenteredArc(g, x - 50, y - 37, 62, -90, 53);
            GraphicsUtil.drawCenteredArc(g, x - 50, y + 37, 62, 90, -53);
        }
        this.drawShield(g, x - width, y, width, height);
    }

    @Override
    protected void drawDinShape(ComponentDrawContext context, int x, int y, int width, int height, int inputs, AbstractGate gate) {
        DinShape.drawOrLines(context, x, y, width, height, inputs, gate, false);
        DinShape.draw(context, x, y, width, height, false, 1);
    }

    protected void drawShield(Graphics g, int x, int y, int width, int height) {
        GraphicsUtil.switchToWidth(g, 2);
        if (width == 30) {
            GraphicsUtil.drawCenteredArc(g, x - 26, y, 30, -30, 60);
        } else {
            GraphicsUtil.drawCenteredArc(g, x - 43, y, 50, -30, 60);
        }
        if (height > width) {
            int extra = (height - width) / 2;
            int dx = (int)Math.round((double)extra * (Math.sqrt(3.0) / 2.0));
            GraphicsUtil.drawCenteredArc(g, x - dx, y - (width + extra) / 2, extra, -30, 60);
            GraphicsUtil.drawCenteredArc(g, x - dx, y + (width + extra) / 2, extra, -30, 60);
        }
    }

    @Override
    protected Value computeOutput(Value[] inputs, int num_inputs) {
        if (num_inputs == 0) {
            return Value.NIL;
        }
        Value ret = inputs[0];
        for (int i = 1; i < num_inputs; ++i) {
            ret = ret.or(inputs[i]);
        }
        return ret;
    }

    @Override
    protected boolean shouldRepairWire(Component comp, WireRepairData data) {
        boolean ret = !data.getPoint().equals(comp.getLocation());
        return ret;
    }

    @Override
    protected Expression computeExpression(Expression[] inputs, int numInputs) {
        Expression ret = inputs[0];
        for (int i = 1; i < numInputs; ++i) {
            ret = Expressions.or(ret, inputs[i]);
        }
        return ret;
    }
}

