/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.others;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.util.Icons;
import java.awt.Color;
import javax.swing.Icon;
import tr.edu.metu.ceng.ceng232.others.ICDraw;
import tr.edu.metu.ceng.ceng232.others.ic;

class ic74138
extends ic {
    public static final ComponentFactory factory = Factory.access$000();
    private static Icon toolIcon;
    private static ICDraw.ICDescriptor descriptor;

    private ic74138(Location loc, AttributeSet attrs) {
        super(loc, attrs, descriptor);
        this.setPins();
    }

    private void setPins() {
        int i;
        BitWidth data = BitWidth.ONE;
        Direction facing = (Direction)this.getAttributeSet().getValue(ic.facing_attr);
        Location pt = this.getLocation();
        for (i = 0; i < 3; ++i) {
            this.setEnd(this.getWestPin(i), this.getWestPinLoc(i), data, 1);
        }
        for (i = 0; i < 8; ++i) {
            this.setEnd(this.getEastPin(i), this.getEastPinLoc(i), data, 2);
        }
        for (i = 0; i < 3; ++i) {
            this.setEnd(this.getSouthPin(i), this.getSouthPinLoc(i), data, 1);
        }
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        boolean enabled;
        BitWidth data = BitWidth.ONE;
        Value s0 = state.getValue(this.getWestPinLoc(0));
        Value s1 = state.getValue(this.getWestPinLoc(1));
        Value s2 = state.getValue(this.getWestPinLoc(2));
        Value e1 = state.getValue(this.getSouthPinLoc(0));
        Value _e2 = state.getValue(this.getSouthPinLoc(1));
        Value _e3 = state.getValue(this.getSouthPinLoc(2));
        int out = -1;
        boolean problem = false;
        if (e1.isFullyDefined() && _e2.isFullyDefined() && _e3.isFullyDefined()) {
            enabled = e1.toIntValue() + _e2.not().toIntValue() + _e3.not().toIntValue() == 3;
        } else {
            enabled = false;
            problem = true;
        }
        if (s0.isFullyDefined() && s1.isFullyDefined() && s2.isFullyDefined()) {
            out = (s2.toIntValue() << 2) + (s1.toIntValue() << 1) + s0.toIntValue();
        } else {
            problem = true;
        }
        if (problem) {
            for (int i = 0; i < 8; ++i) {
                state.setValue(this.getEastPinLoc(i), Value.ERROR, this, 3);
            }
        } else {
            for (int i = 0; i < 8; ++i) {
                state.setValue(this.getEastPinLoc(i), enabled && out == i ? Value.FALSE : Value.TRUE, this, 3);
            }
        }
    }

    @Override
    public void draw(ComponentDrawContext context) {
        factory.drawGhost(context, Color.BLACK, this.getLocation().getX(), this.getLocation().getY(), this.getAttributeSet());
        context.drawPins(this);
    }

    private static class Factory
    extends ic.ICFactory {
        private static Factory instance = null;

        private static Factory getFactory() {
            if (instance != null) {
                return instance;
            }
            ICDraw.ICPin[] westPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "S0"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "S1"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "S2")};
            ICDraw.ICPin[] eastPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/Y0"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/Y1"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/Y2"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/Y3"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/Y4"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/Y5"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/Y6"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/Y7")};
            ICDraw.ICPin[] northPins = new ICDraw.ICPin[]{};
            ICDraw.ICPin[] southPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "E0"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "/E1"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "/E2")};
            descriptor = new ICDraw.ICDescriptor(westPins, eastPins, northPins, southPins, "74138");
            toolIcon = Icons.getIcon("decoder.gif");
            instance = new Factory();
            return instance;
        }

        private Factory() {
            super(descriptor, toolIcon);
        }

        @Override
        public String getName() {
            return "3-to-8 decoder (74138)";
        }

        @Override
        public Component createComponent(Location loc, AttributeSet attrs) {
            return new ic74138(loc, attrs);
        }

        static /* synthetic */ Factory access$000() {
            return Factory.getFactory();
        }
    }

}

