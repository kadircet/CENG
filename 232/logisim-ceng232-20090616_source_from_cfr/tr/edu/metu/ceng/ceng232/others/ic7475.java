/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.others;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentState;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.util.Icons;
import java.awt.Color;
import javax.swing.Icon;
import tr.edu.metu.ceng.ceng232.others.ICDraw;
import tr.edu.metu.ceng.ceng232.others.ic;

class ic7475
extends ic {
    public static final ComponentFactory factory = Factory.access$000();
    private static Icon toolIcon;
    private static ICDraw.ICDescriptor descriptor;

    private ic7475(Location loc, AttributeSet attrs) {
        super(loc, attrs, descriptor);
        this.setPins();
    }

    private void setPins() {
        int i;
        BitWidth data = BitWidth.ONE;
        Location pt = this.getLocation();
        for (i = 0; i < 6; ++i) {
            this.setEnd(this.getWestPin(i), this.getWestPinLoc(i), data, 1);
        }
        for (i = 0; i < 8; ++i) {
            this.setEnd(this.getEastPin(i), this.getEastPinLoc(i), data, 2);
        }
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        InternalState is = this.getInternalState(state);
        BitWidth data = BitWidth.ONE;
        Value d1 = state.getValue(this.getWestPinLoc(0));
        Value c1 = state.getValue(this.getWestPinLoc(1));
        Value d2 = state.getValue(this.getWestPinLoc(2));
        Value c2 = c1;
        Value d3 = state.getValue(this.getWestPinLoc(3));
        Value c3 = state.getValue(this.getWestPinLoc(4));
        Value d4 = state.getValue(this.getWestPinLoc(5));
        Value c4 = c3;
        Value q1 = is.Q1;
        Value q2 = is.Q2;
        Value q3 = is.Q3;
        Value q4 = is.Q4;
        if (d1.isFullyDefined() && c1.isFullyDefined() && c1.toIntValue() == 1) {
            q1 = d1;
        }
        state.setValue(this.getEastPinLoc(0), q1, this, 3);
        state.setValue(this.getEastPinLoc(1), q1.not(), this, 3);
        if (d2.isFullyDefined() && c2.isFullyDefined() && c2.toIntValue() == 1) {
            q2 = d2;
        }
        state.setValue(this.getEastPinLoc(2), q2, this, 3);
        state.setValue(this.getEastPinLoc(3), q2.not(), this, 3);
        if (d3.isFullyDefined() && c3.isFullyDefined() && c3.toIntValue() == 1) {
            q3 = d3;
        }
        state.setValue(this.getEastPinLoc(4), q3, this, 3);
        state.setValue(this.getEastPinLoc(5), q3.not(), this, 3);
        if (d4.isFullyDefined() && c4.isFullyDefined() && c4.toIntValue() == 1) {
            q4 = d4;
        }
        state.setValue(this.getEastPinLoc(6), q4, this, 3);
        state.setValue(this.getEastPinLoc(7), q4.not(), this, 3);
        state.setData(this, new InternalState(q1, q2, q3, q4));
    }

    @Override
    public void draw(ComponentDrawContext context) {
        factory.drawGhost(context, Color.BLACK, this.getLocation().getX(), this.getLocation().getY(), this.getAttributeSet());
        context.drawPins(this);
    }

    protected InternalState getInternalState(CircuitState circuitState) {
        InternalState state = (InternalState)circuitState.getData(this);
        if (state == null) {
            state = new InternalState(Value.FALSE, Value.FALSE, Value.FALSE, Value.FALSE);
            circuitState.setData(this, state);
        }
        return state;
    }

    public class InternalState
    implements ComponentState,
    Cloneable {
        public Value Q1;
        public Value Q2;
        public Value Q3;
        public Value Q4;

        public InternalState(Value q1, Value q2, Value q3, Value q4) {
            this.Q1 = q1;
            this.Q2 = q2;
            this.Q3 = q3;
            this.Q4 = q4;
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            }
            catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }

    private static class Factory
    extends ic.ICFactory {
        private static Factory instance = null;

        private static Factory getFactory() {
            if (instance != null) {
                return instance;
            }
            ICDraw.ICPin[] westPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "1D"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "1C,2C"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "2D"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "3D"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "3C,4C"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "4D")};
            ICDraw.ICPin[] eastPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "1Q"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/1Q"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "2Q"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/2Q"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "3Q"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/3Q"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "4Q"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/4Q")};
            ICDraw.ICPin[] northPins = new ICDraw.ICPin[]{};
            ICDraw.ICPin[] southPins = new ICDraw.ICPin[]{};
            descriptor = new ICDraw.ICDescriptor(westPins, eastPins, northPins, southPins, "7475");
            toolIcon = Icons.getIcon("decoder.gif");
            instance = new Factory();
            return instance;
        }

        private Factory() {
            super(descriptor, toolIcon);
        }

        @Override
        public String getName() {
            return "4-bit Latch (7475)";
        }

        @Override
        public Component createComponent(Location loc, AttributeSet attrs) {
            return new ic7475(loc, attrs);
        }

        static /* synthetic */ Factory access$000() {
            return Factory.getFactory();
        }
    }

}

