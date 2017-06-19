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

class ic74112
extends ic {
    public static final ComponentFactory factory = Factory.access$000();
    private static Icon toolIcon;
    private static ICDraw.ICDescriptor descriptor;

    private ic74112(Location loc, AttributeSet attrs) {
        super(loc, attrs, descriptor);
        this.setPins();
    }

    private void setPins() {
        int i;
        BitWidth data = BitWidth.ONE;
        Location pt = this.getLocation();
        for (i = 0; i < 5; ++i) {
            this.setEnd(this.getWestPin(i), this.getWestPinLoc(i), data, 1);
        }
        for (i = 5; i < 7; ++i) {
            this.setEnd(this.getWestPin(i), this.getWestPinLoc(i), data, 2);
        }
        for (i = 0; i < 5; ++i) {
            this.setEnd(this.getEastPin(i), this.getEastPinLoc(i), data, 1);
        }
        for (i = 5; i < 7; ++i) {
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
        Value s1 = state.getValue(this.getWestPinLoc(0));
        Value j1 = state.getValue(this.getWestPinLoc(1));
        Value clk1 = state.getValue(this.getWestPinLoc(2));
        Value k1 = state.getValue(this.getWestPinLoc(3));
        Value r1 = state.getValue(this.getWestPinLoc(4));
        Value s2 = state.getValue(this.getEastPinLoc(0));
        Value j2 = state.getValue(this.getEastPinLoc(1));
        Value clk2 = state.getValue(this.getEastPinLoc(2));
        Value k2 = state.getValue(this.getEastPinLoc(3));
        Value r2 = state.getValue(this.getEastPinLoc(4));
        Value q1 = is.Q1;
        if (s1.isFullyDefined() && j1.isFullyDefined() && clk1.isFullyDefined() && k1.isFullyDefined() && r1.isFullyDefined()) {
            if (s1.toIntValue() == 0 && r1.toIntValue() == 1) {
                q1 = Value.FALSE;
            } else if (s1.toIntValue() == 1 && r1.toIntValue() == 0) {
                q1 = Value.TRUE;
            } else if (s1.toIntValue() == 0 && r1.toIntValue() == 0) {
                q1 = Value.ERROR;
            } else if (is.CLK1.toIntValue() == 1 && clk1.toIntValue() == 0) {
                if (j1.toIntValue() == 1 && k1.toIntValue() == 0) {
                    q1 = Value.TRUE;
                } else if (j1.toIntValue() == 0 && k1.toIntValue() == 1) {
                    q1 = Value.FALSE;
                } else if (j1.toIntValue() == 1 && k1.toIntValue() == 1) {
                    q1 = q1.not();
                }
            }
        }
        if (q1.isErrorValue()) {
            state.setValue(this.getWestPinLoc(5), Value.TRUE, this, 3);
            state.setValue(this.getWestPinLoc(6), Value.TRUE, this, 3);
        } else {
            state.setValue(this.getWestPinLoc(5), q1, this, 3);
            state.setValue(this.getWestPinLoc(6), q1.not(), this, 3);
        }
        Value q2 = is.Q2;
        if (s2.isFullyDefined() && j2.isFullyDefined() && clk2.isFullyDefined() && k2.isFullyDefined() && r2.isFullyDefined()) {
            if (s2.toIntValue() == 0 && r2.toIntValue() == 1) {
                q2 = Value.FALSE;
            } else if (s2.toIntValue() == 1 && r2.toIntValue() == 0) {
                q2 = Value.TRUE;
            } else if (s2.toIntValue() == 0 && r2.toIntValue() == 0) {
                q2 = Value.ERROR;
            } else if (is.CLK2.toIntValue() == 1 && clk2.toIntValue() == 0) {
                if (j2.toIntValue() == 1 && k2.toIntValue() == 0) {
                    q2 = Value.TRUE;
                } else if (j2.toIntValue() == 0 && k2.toIntValue() == 1) {
                    q2 = Value.FALSE;
                } else if (j2.toIntValue() == 1 && k2.toIntValue() == 1) {
                    q2 = q2.not();
                }
            }
        }
        if (q2.isErrorValue()) {
            state.setValue(this.getEastPinLoc(5), Value.TRUE, this, 3);
            state.setValue(this.getEastPinLoc(6), Value.TRUE, this, 3);
        } else {
            state.setValue(this.getEastPinLoc(5), q2, this, 3);
            state.setValue(this.getEastPinLoc(6), q2.not(), this, 3);
        }
        state.setData(this, new InternalState(q1, clk1, q2, clk2));
    }

    @Override
    public void draw(ComponentDrawContext context) {
        factory.drawGhost(context, Color.BLACK, this.getLocation().getX(), this.getLocation().getY(), this.getAttributeSet());
        context.drawPins(this);
    }

    protected InternalState getInternalState(CircuitState circuitState) {
        InternalState state = (InternalState)circuitState.getData(this);
        if (state == null) {
            state = new InternalState(Value.FALSE, Value.UNKNOWN, Value.FALSE, Value.UNKNOWN);
            circuitState.setData(this, state);
        }
        return state;
    }

    public class InternalState
    implements ComponentState,
    Cloneable {
        public Value Q1;
        public Value CLK1;
        public Value Q2;
        public Value CLK2;

        public InternalState(Value q1, Value clk1, Value q2, Value clk2) {
            this.Q1 = q1;
            this.CLK1 = clk1;
            this.Q2 = q2;
            this.CLK2 = clk2;
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
            ICDraw.ICPin[] westPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "/1R"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "1J"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/1CLK"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "1K"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "/1S"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "1Q"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/1Q")};
            ICDraw.ICPin[] eastPins = new ICDraw.ICPin[]{new ICDraw.ICPin(ICDraw.ICPinType.PIN, "/2R"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "2J"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/2CLK"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "2K"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "/2S"), new ICDraw.ICPin(ICDraw.ICPinType.PIN, "2Q"), new ICDraw.ICPin(ICDraw.ICPinType.INVERSEPIN, "/2Q")};
            ICDraw.ICPin[] northPins = new ICDraw.ICPin[]{};
            ICDraw.ICPin[] southPins = new ICDraw.ICPin[]{};
            descriptor = new ICDraw.ICDescriptor(westPins, eastPins, northPins, southPins, "74112");
            toolIcon = Icons.getIcon("decoder.gif");
            instance = new Factory();
            return instance;
        }

        private Factory() {
            super(descriptor, toolIcon);
        }

        @Override
        public String getName() {
            return "Dual J-K Flip Flop (74112)";
        }

        @Override
        public Component createComponent(Location loc, AttributeSet attrs) {
            return new ic74112(loc, attrs);
        }

        static /* synthetic */ Factory access$000() {
            return Factory.getFactory();
        }
    }

}

