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

class ic7483
extends ic {
    public static final ComponentFactory factory = Factory.access$000();
    private static Icon toolIcon;
    private static ICDraw.ICDescriptor descriptor;

    private ic7483(Location loc, AttributeSet attrs) {
        super(loc, attrs, descriptor);
        this.setPins();
    }

    private void setPins() {
        int i;
        BitWidth data = BitWidth.ONE;
        for (i = 0; i < 4; ++i) {
            this.setEnd(this.getWestPin(i), this.getWestPinLoc(i), data, 1);
        }
        for (i = 0; i < 5; ++i) {
            this.setEnd(this.getEastPin(i), this.getEastPinLoc(i), data, 2);
        }
        for (i = 0; i < 4; ++i) {
            this.setEnd(this.getNorthPin(i), this.getNorthPinLoc(i), data, 1);
        }
        for (i = 0; i < 1; ++i) {
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
        Value[] b = new Value[]{state.getValue(this.getNorthPinLoc(0)), state.getValue(this.getNorthPinLoc(1)), state.getValue(this.getNorthPinLoc(2)), state.getValue(this.getNorthPinLoc(3))};
        Value ci = state.getValue(this.getSouthPinLoc(0));
        Value[] s = new Value[]{state.getValue(this.getEastPinLoc(0)), state.getValue(this.getEastPinLoc(1)), state.getValue(this.getEastPinLoc(2)), state.getValue(this.getEastPinLoc(3)), state.getValue(this.getEastPinLoc(4))};
        int outVal = 0;
        boolean problem = false;
        if (a[0].isFullyDefined() && a[1].isFullyDefined() && a[2].isFullyDefined() && a[3].isFullyDefined() && b[0].isFullyDefined() && b[1].isFullyDefined() && b[2].isFullyDefined() && b[3].isFullyDefined() && ci.isFullyDefined()) {
            outVal = (a[3].toIntValue() << 3) + (a[2].toIntValue() << 2) + (a[1].toIntValue() << 1) + (a[0].toIntValue() << 0) + (b[3].toIntValue() << 3) + (b[2].toIntValue() << 2) + (b[1].toIntValue() << 1) + (b[0].toIntValue() << 0) + ci.toIntValue();
        } else {
            problem = true;
        }
        for (int i = 0; i < 5; ++i) {
            if (problem) {
                state.setValue(this.getEastPinLoc(i), Value.ERROR, this, 3);
                continue;
            }
            state.setValue(this.getEastPinLoc(i), (outVal & 1 << i) != 0 ? Value.TRUE : Value.FALSE, this, 3);
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
            ICDraw.ICPin[] westPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "A1"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "A2"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "A3"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "A4")};
            ICDraw.ICPin[] eastPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "S1"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "S2"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "S3"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "S4"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "CO")};
            ICDraw.ICPin[] northPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "B1"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "B2"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "B3"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "B4")};
            ICDraw.ICPin[] southPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "CI")};
            descriptor = new ICDraw.ICDescriptor(westPins, eastPins, northPins, southPins, "7483");
            toolIcon = Icons.getIcon("decoder.gif");
            instance = new Factory();
            return instance;
        }

        private Factory() {
            super(descriptor, toolIcon);
        }

        @Override
        public String getName() {
            return "4 bit full adder (7483)";
        }

        @Override
        public Component createComponent(Location loc, AttributeSet attrs) {
            return new ic7483(loc, attrs);
        }

        static /* synthetic */ Factory access$000() {
            return Factory.getFactory();
        }
    }

}

