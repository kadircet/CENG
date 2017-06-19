/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.gates;

import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.ExpressionComputer;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.std.gates.AbstractGateFactory;
import com.cburch.logisim.std.gates.GateAttributes;
import com.cburch.logisim.tools.WireRepair;
import com.cburch.logisim.tools.WireRepairData;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Map;

class AbstractGate
extends ManagedComponent
implements WireRepair,
ExpressionComputer {
    private AbstractGateFactory src;

    public AbstractGate(Location loc, AttributeSet attrs, AbstractGateFactory src) {
        super(loc, attrs, 4);
        this.src = src;
        GateAttributes gateAttrs = (GateAttributes)attrs;
        gateAttrs.gate = this;
        this.setEnds();
    }

    @Override
    public ComponentFactory getFactory() {
        return this.src;
    }

    void setEnds() {
        int ht;
        int wid;
        GateAttributes attrs = (GateAttributes)this.getAttributeSet();
        Bounds bounds = this.getBounds();
        Location pt = this.getLocation();
        BitWidth w = attrs.width;
        this.setEnd(0, pt, w, 2);
        if (attrs.facing == Direction.NORTH || attrs.facing == Direction.SOUTH) {
            wid = bounds.getHeight();
            ht = bounds.getWidth();
        } else {
            wid = bounds.getWidth();
            ht = bounds.getHeight();
        }
        int dx = wid;
        int dy = - ht / 2 - 5;
        int ddy = (ht - 10) / (attrs.inputs - 1);
        if (attrs.facing == Direction.NORTH) {
            for (int i = 1; i <= attrs.inputs; ++i) {
                this.setEnd(i, pt.translate(dy, dx), w, 1);
                dy += ddy;
            }
        } else if (attrs.facing == Direction.SOUTH) {
            for (int i = 1; i <= attrs.inputs; ++i) {
                this.setEnd(i, pt.translate(dy, - dx), w, 1);
                dy += ddy;
            }
        } else if (attrs.facing == Direction.WEST) {
            for (int i = 1; i <= attrs.inputs; ++i) {
                this.setEnd(i, pt.translate(dx, dy), w, 1);
                dy += ddy;
            }
        } else {
            for (int i = 1; i <= attrs.inputs; ++i) {
                this.setEnd(i, pt.translate(- dx, dy), w, 1);
                dy += ddy;
            }
        }
    }

    @Override
    public void propagate(CircuitState state) {
        GateAttributes attrs = (GateAttributes)this.getAttributeSet();
        Value[] inputs = new Value[attrs.inputs];
        int num_inputs = 0;
        for (int i = 1; i <= attrs.inputs; ++i) {
            Value v = state.getValue(this.getEndLocation(i));
            if (v == Value.NIL) continue;
            inputs[num_inputs] = v;
            ++num_inputs;
        }
        Value out = this.src.computeOutput(inputs, num_inputs);
        state.setValue(this.getEndLocation(0), out, this, 1);
    }

    @Override
    public void draw(ComponentDrawContext context) {
        GateAttributes attrs = (GateAttributes)this.getAttributeSet();
        Location loc = this.getLocation();
        Bounds bds = this.getBounds();
        context.getGraphics().setColor(Color.BLACK);
        AbstractGate.drawBase(context, this.src, this, attrs, loc.getX(), loc.getY(), bds.getWidth(), bds.getHeight());
        if (!context.isPrintView() || context.getGateShape() == "rectangular") {
            context.drawPins(this);
        }
    }

    static void drawBase(ComponentDrawContext context, AbstractGateFactory src, AbstractGate comp, GateAttributes attrs, int x, int y, int width, int height) {
        Direction facing = attrs.facing;
        Graphics oldG = context.getGraphics();
        if (facing != Direction.EAST && oldG instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D)oldG.create();
            g2.rotate(- facing.toRadians(), x, y);
            context.setGraphics(g2);
            if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                int t = width;
                width = height;
                height = t;
            }
        }
        if (context.getGateShape() == "rectangular") {
            src.drawRectangular(context, x, y, width, height);
        } else if (context.getGateShape() == "din40700") {
            Integer inputs = (Integer)attrs.getValue(GateAttributes.inputs_attr);
            src.drawDinShape(context, x, y, width, height, inputs, comp);
        } else {
            int don;
            int n = don = src.has_dongle ? 10 : 0;
            if (comp != null) {
                src.drawInputLines(context, comp, attrs.inputs, x - width, y - (height - 10) / 2, width - don, height);
            }
            src.drawShape(context, x - don, y, width - don, height);
            if (src.has_dongle) {
                context.drawDongle(x - 5, y);
            }
        }
        context.setGraphics(oldG);
    }

    @Override
    public Object getFeature(Object key) {
        if (key == WireRepair.class) {
            return this;
        }
        if (key == ExpressionComputer.class) {
            return this;
        }
        return super.getFeature(key);
    }

    @Override
    public boolean shouldRepairWire(WireRepairData data) {
        return this.src.shouldRepairWire(this, data);
    }

    @Override
    public void computeExpression(Map expressionMap) {
        GateAttributes attrs = (GateAttributes)this.getAttributeSet();
        Expression[] inputs = new Expression[attrs.inputs];
        int numInputs = 0;
        for (int i = 1; i <= attrs.inputs; ++i) {
            Expression e = (Expression)expressionMap.get(this.getEndLocation(i));
            if (e == null) continue;
            inputs[numInputs] = e;
            ++numInputs;
        }
        if (numInputs > 0) {
            Expression out = this.src.computeExpression(inputs, numInputs);
            expressionMap.put(this.getEndLocation(0), out);
        }
    }

    void attributeValueChanged(Attribute attr, Object value) {
        if (attr == GateAttributes.width_attr) {
            this.setEnds();
        }
    }
}

