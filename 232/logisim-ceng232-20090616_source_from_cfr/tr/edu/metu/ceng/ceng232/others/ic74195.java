/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.others;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentState;
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

public class ic74195
extends ic {
    public static final ComponentFactory factory = Factory.access$000();
    private static Icon toolIcon;
    private static ICDraw.ICDescriptor descriptor;

    private ic74195(Location loc, AttributeSet attrs) {
        super(loc, attrs, descriptor);
        this.setPins();
    }

    private void setPins() {
        int i;
        BitWidth data = BitWidth.ONE;
        Direction facing = (Direction)this.getAttributeSet().getValue(ic.facing_attr);
        Location pt = this.getLocation();
        for (i = 0; i < 5; ++i) {
            this.setEnd(this.getWestPin(i), this.getWestPinLoc(i), data, 1);
        }
        for (i = 0; i < 4; ++i) {
            this.setEnd(this.getEastPin(i), this.getEastPinLoc(i), data, 2);
        }
        this.setEnd(this.getEastPin(4), this.getEastPinLoc(4), data, 1);
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
        InternalState is = this.getInternalState(state);
        BitWidth data = BitWidth.ONE;
        Value p3 = state.getValue(this.getWestPinLoc(0));
        Value p2 = state.getValue(this.getWestPinLoc(1));
        Value p1 = state.getValue(this.getWestPinLoc(2));
        Value p0 = state.getValue(this.getWestPinLoc(3));
        Value _pe = state.getValue(this.getWestPinLoc(4));
        Value j = state.getValue(this.getSouthPinLoc(0));
        Value cp = state.getValue(this.getSouthPinLoc(1));
        Value _k = state.getValue(this.getSouthPinLoc(2));
        Value _mr = state.getValue(this.getEastPinLoc(4));
        boolean problem = false;
        if (!(p0.isFullyDefined() && p1.isFullyDefined() && p2.isFullyDefined() && p3.isFullyDefined() && _pe.isFullyDefined() && cp.isFullyDefined() && j.isFullyDefined() && _k.isFullyDefined() && _mr.isFullyDefined())) {
            state.setValue(this.getEastPinLoc(3), is.value.get(0), this, 3);
            state.setValue(this.getEastPinLoc(2), is.value.get(1), this, 3);
            state.setValue(this.getEastPinLoc(1), is.value.get(2), this, 3);
            state.setValue(this.getEastPinLoc(0), is.value.get(3), this, 3);
            return;
        }
        Value outputs = is.value;
        if (_mr.toIntValue() == 0) {
            Value[] newValues = new Value[]{Value.FALSE, Value.FALSE, Value.FALSE, Value.FALSE};
            outputs = Value.create(newValues);
        } else if (cp.toIntValue() == 1 && is.CP.toIntValue() == 0) {
            if (_pe.toIntValue() == 1) {
                Value newFirst = null;
                newFirst = j.toIntValue() == _k.toIntValue() ? j : (j.toIntValue() == 1 ? is.value.get(0).not() : is.value.get(0));
                Value[] newValues = new Value[]{newFirst, is.value.get(0), is.value.get(1), is.value.get(2)};
                outputs = Value.create(newValues);
            } else {
                Value[] newValues = new Value[]{p0, p1, p2, p3};
                outputs = Value.create(newValues);
            }
        } else {
            outputs = is.value;
        }
        state.setValue(this.getEastPinLoc(3), outputs.get(0), this, 3);
        state.setValue(this.getEastPinLoc(2), outputs.get(1), this, 3);
        state.setValue(this.getEastPinLoc(1), outputs.get(2), this, 3);
        state.setValue(this.getEastPinLoc(0), outputs.get(3), this, 3);
        state.setData(this, new InternalState(cp, outputs));
    }

    @Override
    public void draw(ComponentDrawContext context) {
        factory.drawGhost(context, Color.BLACK, this.getLocation().getX(), this.getLocation().getY(), this.getAttributeSet());
        context.drawPins(this);
    }

    protected InternalState getInternalState(CircuitState circuitState) {
        InternalState state = (InternalState)circuitState.getData(this);
        if (state == null) {
            state = new InternalState(Value.createUnknown(BitWidth.ONE), Value.createKnown(BitWidth.create(4), 0));
            circuitState.setData(this, state);
        }
        return state;
    }

    public Value fetchValue(CircuitState state) {
        InternalState is = this.getInternalState(state);
        return is.value;
    }

    public void modifyValue(CircuitState state, Value outputs) {
        InternalState is = this.getInternalState(state);
        state.setData(this, new InternalState(is.CP, outputs));
        state.setValue(this.getEastPinLoc(3), outputs.get(0), this, 3);
        state.setValue(this.getEastPinLoc(2), outputs.get(1), this, 3);
        state.setValue(this.getEastPinLoc(1), outputs.get(2), this, 3);
        state.setValue(this.getEastPinLoc(0), outputs.get(3), this, 3);
    }

    public class InternalState
    implements ComponentState,
    Cloneable {
        public Value CP;
        public Value value;

        public InternalState(Value cp, Value v) {
            this.CP = cp;
            this.value = v;
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
            ICDraw.ICPin[] westPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "P3"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "P2"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "P1"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "P0"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/PE")};
            ICDraw.ICPin[] eastPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "Q3"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "Q2"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "Q1"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "Q0"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/MR")};
            ICDraw.ICPin[] northPins = new ICDraw.ICPin[]{};
            ICDraw.ICPin[] southPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "J"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "CP"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/K")};
            descriptor = new ICDraw.ICDescriptor(westPins, eastPins, northPins, southPins, "74195");
            toolIcon = Icons.getIcon("decoder.gif");
            instance = new Factory();
            return instance;
        }

        private Factory() {
            super(descriptor, toolIcon);
        }

        @Override
        public String getName() {
            return "4-bit shift register (74195)";
        }

        @Override
        public Component createComponent(Location loc, AttributeSet attrs) {
            return new ic74195(loc, attrs);
        }

        static /* synthetic */ Factory access$000() {
            return Factory.getFactory();
        }
    }

}

