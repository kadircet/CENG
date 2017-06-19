/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.gates;

import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.Expressions;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.std.gates.AbstractGate;
import com.cburch.logisim.std.gates.AbstractGateFactory;
import com.cburch.logisim.std.gates.AndGate;
import com.cburch.logisim.std.gates.DinShape;
import com.cburch.logisim.std.gates.Strings;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import com.cburch.logisim.util.StringGetter;
import java.awt.Graphics;
import javax.swing.Icon;

class NandGate
extends AbstractGateFactory {
    public static NandGate instance = new NandGate();

    private NandGate() {
        super("NAND Gate", Strings.getter("nandGateComponent"));
        this.setHasDongle(true);
        this.setRectangularLabel(AndGate.instance.getRectangularLabel());
    }

    @Override
    public Icon getIconShaped() {
        return Icons.getIcon("nandGate.gif");
    }

    @Override
    public Icon getIconRectangular() {
        return Icons.getIcon("nandGateRect.gif");
    }

    @Override
    public Icon getIconDin40700() {
        return Icons.getIcon("dinNandGate.gif");
    }

    @Override
    public void paintIconShaped(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        int[] xp = new int[4];
        int[] yp = new int[4];
        xp[0] = x + 8;
        yp[0] = y + 2;
        xp[1] = x;
        yp[1] = y + 2;
        xp[2] = x;
        yp[2] = y + 18;
        xp[3] = x + 8;
        yp[3] = y + 18;
        g.drawPolyline(xp, yp, 4);
        GraphicsUtil.drawCenteredArc(g, x + 8, y + 10, 8, -90, 180);
        g.drawOval(x + 16, y + 8, 4, 4);
    }

    @Override
    protected void drawShape(ComponentDrawContext context, int x, int y, int width, int height) {
        AndGate.instance.drawShape(context, x, y, width, height);
    }

    @Override
    protected void drawDinShape(ComponentDrawContext context, int x, int y, int width, int height, int inputs, AbstractGate gate) {
        DinShape.draw(context, x, y, width, height, true, 0);
    }

    @Override
    protected Value computeOutput(Value[] inputs, int num_inputs) {
        return AndGate.instance.computeOutput(inputs, num_inputs).not();
    }

    @Override
    protected Expression computeExpression(Expression[] inputs, int numInputs) {
        Expression ret = inputs[0];
        for (int i = 1; i < numInputs; ++i) {
            ret = Expressions.and(ret, inputs[i]);
        }
        return Expressions.not(ret);
    }
}

