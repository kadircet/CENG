/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.gates;

import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.Expressions;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.std.gates.AbstractGate;
import com.cburch.logisim.std.gates.AbstractGateFactory;
import com.cburch.logisim.std.gates.DinShape;
import com.cburch.logisim.std.gates.OrGate;
import com.cburch.logisim.std.gates.Strings;
import com.cburch.logisim.std.gates.XorGate;
import com.cburch.logisim.tools.WireRepairData;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import com.cburch.logisim.util.StringGetter;
import java.awt.Graphics;
import javax.swing.Icon;

class XnorGate
extends AbstractGateFactory {
    public static XnorGate instance = new XnorGate();

    private XnorGate() {
        super("XNOR Gate", Strings.getter("xnorGateComponent"));
        this.setHasDongle(true);
        this.setAdditionalWidth(10);
        this.setRectangularLabel(XorGate.instance.getRectangularLabel());
    }

    @Override
    public Icon getIconShaped() {
        return Icons.getIcon("xnorGate.gif");
    }

    @Override
    public Icon getIconRectangular() {
        return Icons.getIcon("xnorGateRect.gif");
    }

    @Override
    public Icon getIconDin40700() {
        return Icons.getIcon("dinXnorGate.gif");
    }

    @Override
    public void paintIconShaped(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        GraphicsUtil.drawCenteredArc(g, x, y - 5, 22, -90, 53);
        GraphicsUtil.drawCenteredArc(g, x, y + 23, 22, 90, -53);
        GraphicsUtil.drawCenteredArc(g, x - 8, y + 9, 16, -30, 60);
        GraphicsUtil.drawCenteredArc(g, x - 10, y + 9, 16, -30, 60);
        g.drawOval(x + 16, y + 8, 4, 4);
    }

    @Override
    protected void drawInputLines(ComponentDrawContext context, AbstractGate comp, int inputs, int x, int yTop, int width, int height) {
        OrGate.instance.drawInputLines(context, comp, inputs, x, yTop, width, height);
    }

    @Override
    protected void drawShape(ComponentDrawContext context, int x, int y, int width, int height) {
        XorGate.instance.drawShape(context, x, y, width, height);
    }

    @Override
    protected void drawDinShape(ComponentDrawContext context, int x, int y, int width, int height, int inputs, AbstractGate gate) {
        DinShape.draw(context, x, y, width, height, false, 3);
    }

    @Override
    protected Value computeOutput(Value[] inputs, int num_inputs) {
        return XorGate.instance.computeOutput(inputs, num_inputs).not();
    }

    @Override
    protected boolean shouldRepairWire(Component comp, WireRepairData data) {
        return !data.getPoint().equals(comp.getLocation());
    }

    @Override
    protected Expression computeExpression(Expression[] inputs, int numInputs) {
        return Expressions.not(XorGate.xorExpression(inputs, numInputs));
    }
}

