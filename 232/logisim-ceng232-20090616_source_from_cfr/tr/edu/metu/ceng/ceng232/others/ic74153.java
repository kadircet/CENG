/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.others;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.util.Icons;
import java.awt.Color;
import javax.swing.Icon;
import tr.edu.metu.ceng.ceng232.others.ICDraw;
import tr.edu.metu.ceng.ceng232.others.ic;

class ic74153
extends ic {
    public static final ComponentFactory factory = Factory.access$000();
    private static Icon toolIcon;
    private static ICDraw.ICDescriptor descriptor;

    private ic74153(Location loc, AttributeSet attrs) {
        super(loc, attrs, descriptor);
        this.setPins();
    }

    private void setPins() {
        int i;
        BitWidth data = BitWidth.ONE;
        for (i = 0; i < 8; ++i) {
            this.setEnd(this.getWestPin(i), this.getWestPinLoc(i), data, 1);
        }
        for (i = 0; i < 2; ++i) {
            this.setEnd(this.getEastPin(i), this.getEastPinLoc(i), data, 2);
        }
        for (i = 0; i < 2; ++i) {
            this.setEnd(this.getNorthPin(i), this.getNorthPinLoc(i), data, 1);
        }
        for (i = 0; i < 2; ++i) {
            this.setEnd(this.getSouthPin(i), this.getSouthPinLoc(i), data, 1);
        }
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        Value[] a = new Value[]{state.getValue(this.getWestPinLoc(0)), state.getValue(this.getWestPinLoc(1)), state.getValue(this.getWestPinLoc(2)), state.getValue(this.getWestPinLoc(3))};
        Value[] b = new Value[]{state.getValue(this.getWestPinLoc(4)), state.getValue(this.getWestPinLoc(5)), state.getValue(this.getWestPinLoc(6)), state.getValue(this.getWestPinLoc(7))};
        Value s0 = state.getValue(this.getSouthPinLoc(0));
        Value s1 = state.getValue(this.getSouthPinLoc(1));
        Value _ea = state.getValue(this.getNorthPinLoc(0));
        Value _eb = state.getValue(this.getNorthPinLoc(1));
        int out = -1;
        boolean enableda = false;
        boolean enabledb = false;
        boolean problema = false;
        boolean problemb = false;
        if (s0.isFullyDefined() && s1.isFullyDefined()) {
            out = (s1.toIntValue() << 1) + s0.toIntValue();
        } else {
            problema = true;
            problemb = true;
        }
        if (_ea.isFullyDefined()) {
            enableda = _ea.toIntValue() == 0;
        } else {
            enableda = false;
            problema = true;
        }
        if (_eb.isFullyDefined()) {
            enabledb = _eb.toIntValue() == 0;
        } else {
            enabledb = false;
            problemb = true;
        }
        if (problema) {
            state.setValue(this.getEastPinLoc(0), Value.ERROR, this, 3);
        } else if (enableda) {
            state.setValue(this.getEastPinLoc(0), a[out], this, 3);
        } else {
            state.setValue(this.getEastPinLoc(0), Value.FALSE, this, 3);
        }
        if (problemb) {
            state.setValue(this.getEastPinLoc(1), Value.ERROR, this, 3);
        } else if (enabledb) {
            state.setValue(this.getEastPinLoc(1), b[out], this, 3);
        } else {
            state.setValue(this.getEastPinLoc(1), Value.FALSE, this, 3);
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
            ICDraw.ICPin[] westPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "1A0"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "1A1"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "1A2"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "1A3"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "2A0"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "2A1"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "2A2"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "2A3")};
            ICDraw.ICPin[] eastPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "1Y"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "2Y")};
            ICDraw.ICPin[] northPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/1E"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/2E")};
            ICDraw.ICPin[] southPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "S0"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "S1")};
            descriptor = new ICDraw.ICDescriptor(westPins, eastPins, northPins, southPins, "74153");
            toolIcon = Icons.getIcon("decoder.gif");
            instance = new Factory();
            return instance;
        }

        private Factory() {
            super(descriptor, toolIcon);
        }

        @Override
        public String getName() {
            return "4-to-1 MUX (x2) (74153)";
        }

        @Override
        public Component createComponent(Location loc, AttributeSet attrs) {
            return new ic74153(loc, attrs);
        }

        static /* synthetic */ Factory access$000() {
            return Factory.getFactory();
        }
    }

}

