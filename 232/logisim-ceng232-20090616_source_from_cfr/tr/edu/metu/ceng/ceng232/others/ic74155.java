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

class ic74155
extends ic {
    public static final ComponentFactory factory = Factory.access$000();
    private static Icon toolIcon;
    private static ICDraw.ICDescriptor descriptor;

    private ic74155(Location loc, AttributeSet attrs) {
        super(loc, attrs, descriptor);
        this.setPins();
    }

    private void setPins() {
        int i;
        BitWidth data = BitWidth.ONE;
        Direction facing = (Direction)this.getAttributeSet().getValue(ic.facing_attr);
        Location pt = this.getLocation();
        for (i = 0; i < 2; ++i) {
            this.setEnd(this.getWestPin(i), this.getWestPinLoc(i), data, 1);
        }
        for (i = 0; i < 4; ++i) {
            this.setEnd(this.getEastPin(i), this.getEastPinLoc(i), data, 2);
        }
        for (i = 0; i < 4; ++i) {
            this.setEnd(this.getNorthPin(i), this.getNorthPinLoc(i), data, 2);
        }
        for (i = 0; i < 4; ++i) {
            this.setEnd(this.getSouthPin(i), this.getSouthPinLoc(i), data, 1);
        }
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        int i;
        boolean enableda;
        boolean enabledb;
        BitWidth data = BitWidth.ONE;
        Value s0 = state.getValue(this.getWestPinLoc(0));
        Value s1 = state.getValue(this.getWestPinLoc(1));
        Value a_e0 = state.getValue(this.getSouthPinLoc(0));
        Value ae1 = state.getValue(this.getSouthPinLoc(1));
        Value b_e0 = state.getValue(this.getSouthPinLoc(2));
        Value b_e1 = state.getValue(this.getSouthPinLoc(3));
        int out = -1;
        boolean problema = false;
        boolean problemb = false;
        if (a_e0.isFullyDefined() && ae1.isFullyDefined()) {
            enableda = a_e0.not().toIntValue() + ae1.toIntValue() == 2;
        } else {
            enableda = false;
            problema = true;
        }
        if (b_e0.isFullyDefined() && b_e1.isFullyDefined()) {
            enabledb = b_e0.not().toIntValue() + b_e1.not().toIntValue() == 2;
        } else {
            enabledb = false;
            problemb = true;
        }
        if (s0.isFullyDefined() && s1.isFullyDefined()) {
            out = (s1.toIntValue() << 1) + s0.toIntValue();
        } else {
            problema = true;
            problemb = true;
        }
        if (problema) {
            for (i = 0; i < 4; ++i) {
                state.setValue(this.getEastPinLoc(i), Value.ERROR, this, 3);
            }
        } else {
            for (i = 0; i < 4; ++i) {
                state.setValue(this.getEastPinLoc(i), enableda && out == i ? Value.FALSE : Value.TRUE, this, 3);
            }
        }
        if (problemb) {
            for (i = 0; i < 4; ++i) {
                state.setValue(this.getNorthPinLoc(i), Value.ERROR, this, 3);
            }
        } else {
            for (i = 0; i < 4; ++i) {
                state.setValue(this.getNorthPinLoc(i), enabledb && out == i ? Value.FALSE : Value.TRUE, this, 3);
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
            ICDraw.ICPin[] westPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "S0"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "S1")};
            ICDraw.ICPin[] eastPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/1Y0"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/1Y1"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/1Y2"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/1Y3")};
            ICDraw.ICPin[] northPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/2Y0"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/2Y1"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/2Y2"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/2Y3")};
            ICDraw.ICPin[] southPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/1E0"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "1E1"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/2E0"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/2E1")};
            descriptor = new ICDraw.ICDescriptor(westPins, eastPins, northPins, southPins, "74155");
            toolIcon = Icons.getIcon("decoder.gif");
            instance = new Factory();
            return instance;
        }

        private Factory() {
            super(descriptor, toolIcon);
        }

        @Override
        public String getName() {
            return "2-to-4 Decoder (x2) (74155)";
        }

        @Override
        public Component createComponent(Location loc, AttributeSet attrs) {
            return new ic74155(loc, attrs);
        }

        static /* synthetic */ Factory access$000() {
            return Factory.getFactory();
        }
    }

}

