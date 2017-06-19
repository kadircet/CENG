/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.grader;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Clock;
import com.cburch.logisim.circuit.Pin;
import com.cburch.logisim.circuit.Simulator;
import com.cburch.logisim.circuit.SimulatorEvent;
import com.cburch.logisim.circuit.SimulatorListener;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.proj.Project;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tr.edu.metu.ceng.ceng232.grader.Run;
import tr.edu.metu.ceng.ceng232.grader.Settings;
import tr.edu.metu.ceng.ceng232.grader.State;
import tr.edu.metu.ceng.ceng232.others.ic74195;
import tr.edu.metu.ceng.ceng232.others.ic7495;

public class Grader {
    private Project project;
    private MyListener myListener;
    private Simulator simulator;
    private boolean initialPropagationCompleted;
    private Object[] inputPins;
    private Object[] outputPins;
    private boolean waitingNewRun;
    private int curRun;
    private int curState;
    private int curPhase;
    private Map<Integer, Integer> ifCount;

    public Grader(Project proj) {
        this.myListener = new MyListener();
        this.initialPropagationCompleted = false;
        this.waitingNewRun = false;
        this.curRun = 0;
        this.curPhase = 0;
        if (!Settings.isEnabled()) {
            return;
        }
        this.project = proj;
        this.simulator = this.project.getSimulator();
        this.simulator.setIsTicking(false);
        this.simulator.addSimulatorListener(this.myListener);
    }

    private Object fetchInputPin(String name) {
        Circuit c = this.project.getCurrentCircuit();
        Set nonWires = c.getNonWires();
        for (Object o : nonWires) {
            ManagedComponent p;
            if (o instanceof Pin) {
                p = (Pin)o;
                if (!p.isInputPin() || !name.equals(p.getAttributeSet().getValue(Pin.label_attr))) continue;
                if (1 != p.getLogValue(this.project.getCircuitState(), null).getBitWidth().getWidth()) {
                    System.err.println("Pin " + name + " has a wider bit-width than 1");
                    System.exit(1);
                }
                return p;
            }
            if (!(o instanceof Clock) || !name.equals((p = (Clock)o).getAttributeSet().getValue(Pin.label_attr))) continue;
            if (1 != p.getLogValue(this.project.getCircuitState(), null).getBitWidth().getWidth()) {
                System.err.println("Pin " + name + " has a wider bit-width than 1");
                System.exit(1);
            }
            return p;
        }
        return null;
    }

    private Object fetchOutputPin(String name) {
        Circuit c = this.project.getCurrentCircuit();
        Set nonWires = c.getNonWires();
        for (Object o : nonWires) {
            Pin p;
            if (!(o instanceof Pin) || (p = (Pin)o).isInputPin() || !name.equals(p.getAttributeSet().getValue(Pin.label_attr))) continue;
            return p;
        }
        return null;
    }

    public void fetchGradingPins() {
        int i;
        this.inputPins = new Object[Settings.inputs.length];
        this.outputPins = new Object[Settings.outputs.length];
        for (i = 0; i < Settings.inputs.length; ++i) {
            this.inputPins[i] = this.fetchInputPin(Settings.inputs[i]);
            if (this.inputPins[i] != null) continue;
            System.err.println("Cannot find input pin " + Settings.inputs[i]);
            System.exit(1);
        }
        for (i = 0; i < Settings.outputs.length; ++i) {
            this.outputPins[i] = this.fetchOutputPin(Settings.outputs[i]);
            if (this.outputPins[i] != null) continue;
            System.err.println("Cannot find output pin " + Settings.outputs[i]);
            System.exit(1);
        }
    }

    public void printCircuitComponents() {
        Circuit c = this.project.getCurrentCircuit();
        Set nonWires = c.getNonWires();
        for (Object o : nonWires) {
            if (!(o instanceof Pin)) continue;
            Pin p = (Pin)o;
            System.out.println(p.getAttributeSet().getValue(Pin.label_attr));
            if (!p.isInputPin()) continue;
            this.setPinValue(p, Value.TRUE);
        }
        this.project.repaintCanvas();
    }

    public void setInputs(char[] values) {
        for (int i = 0; i < values.length; ++i) {
            if (this.inputPins[i] instanceof Pin) {
                this.setPinValue((Pin)this.inputPins[i], values[i] == '0' ? Value.FALSE : Value.TRUE);
                continue;
            }
            this.setClockValue((Clock)this.inputPins[i], values[i] == '0' ? Value.FALSE : Value.TRUE);
        }
        this.project.repaintCanvas();
    }

    public boolean checkOutputs(char[] values) {
        for (int i = 0; i < values.length; ++i) {
            char sendingValue;
            if (values[i] == 'X' || (sendingValue = this.outputPins[i] instanceof Pin ? this.getPinValue((Pin)this.outputPins[i]) : this.getClockValue((Clock)this.outputPins[i])) == values[i]) continue;
            return false;
        }
        return true;
    }

    private void setClockValue(Clock clock, Value value) {
        if (value.getBitWidth().getWidth() != 1) {
            System.err.println("value.getBitWidth().getWidth() != 1");
            return;
        }
        if (!value.equals(Value.FALSE) && !value.equals(Value.TRUE)) {
            System.err.println("!value.equals(Value.FALSE) && !value.equals(Value.TRUE)");
            return;
        }
        clock.changeValue(this.project.getCircuitState(), value);
    }

    public void setPinValue(Pin pin, Value value) {
        if (!pin.isInputPin()) {
            return;
        }
        if (value.getBitWidth().getWidth() != 1) {
            System.err.println("value.getBitWidth().getWidth() != 1");
            return;
        }
        if (!value.equals(Value.FALSE) && !value.equals(Value.TRUE)) {
            System.err.println("!value.equals(Value.FALSE) && !value.equals(Value.TRUE)");
            return;
        }
        Value sendingValue = pin.getLogValue(this.project.getCircuitState(), null);
        if (sendingValue.getBitWidth().getWidth() != 1) {
            System.err.println("sendingValue.getBitWidth().getWidth() != 1");
            return;
        }
        pin.changeValue(this.project.getCircuitState(), value);
    }

    public char getClockValue(Clock clock) {
        Value sendingValue = clock.getLogValue(this.project.getCircuitState(), null);
        if (sendingValue.equals(Value.ERROR)) {
            return 'E';
        }
        if (sendingValue.equals(Value.FALSE)) {
            return '0';
        }
        if (sendingValue.equals(Value.TRUE)) {
            return '1';
        }
        return 'X';
    }

    public char getPinValue(Pin pin) {
        Value sendingValue = pin.getLogValue(this.project.getCircuitState(), null);
        if (sendingValue.getBitWidth().getWidth() != 1) {
            System.err.println("sendingValue.getBitWidth().getWidth() != 1");
            return 'X';
        }
        if (sendingValue.equals(Value.ERROR)) {
            return 'E';
        }
        if (sendingValue.equals(Value.FALSE)) {
            return '0';
        }
        if (sendingValue.equals(Value.TRUE)) {
            return '1';
        }
        return 'X';
    }

    private void checkChips() {
        HashSet<String> allowedChips = new HashSet<String>(Arrays.asList(Settings.allowedChips));
        for (String chip : Settings.components.keySet()) {
            if (allowedChips.contains(chip)) continue;
            System.out.println("CHIPS FAIL");
            return;
        }
        System.out.println("CHIPS PASS");
    }

    void applyRegisterModify(State s) {
        if (s.type != State.TYPE.REGISTER_MODIFY) {
            return;
        }
        Value[] arrvalue = new Value[4];
        arrvalue[0] = s.stateFrom[0] == '1' ? Value.TRUE : Value.FALSE;
        arrvalue[1] = s.stateFrom[1] == '1' ? Value.TRUE : Value.FALSE;
        arrvalue[2] = s.stateFrom[2] == '1' ? Value.TRUE : Value.FALSE;
        arrvalue[3] = s.stateFrom[3] == '1' ? Value.TRUE : Value.FALSE;
        Value[] a = arrvalue;
        Value[] arrvalue2 = new Value[4];
        arrvalue2[0] = s.stateTo[0] == '1' ? Value.TRUE : Value.FALSE;
        arrvalue2[1] = s.stateTo[1] == '1' ? Value.TRUE : Value.FALSE;
        arrvalue2[2] = s.stateTo[2] == '1' ? Value.TRUE : Value.FALSE;
        arrvalue2[3] = s.stateTo[3] == '1' ? Value.TRUE : Value.FALSE;
        Value[] b = arrvalue2;
        Value fromValue = Value.create(a);
        Value toValue = Value.create(b);
        Circuit c = this.project.getCurrentCircuit();
        Set nonWires = c.getNonWires();
        for (Object o : nonWires) {
            ic7495 ic2;
            Value v;
            if (o instanceof ic7495) {
                ic2 = (ic7495)o;
                v = ic2.fetchValue(this.project.getCircuitState());
                if (!v.equals(fromValue)) continue;
                ic2.modifyValue(this.project.getCircuitState(), toValue);
                continue;
            }
            if (!(o instanceof ic74195) || !(v = (ic2 = (ic74195)o).fetchValue(this.project.getCircuitState())).equals(fromValue)) continue;
            ic2.modifyValue(this.project.getCircuitState(), toValue);
        }
    }

    private class MyListener
    implements SimulatorListener {
        private MyListener() {
        }

        @Override
        public void propagationCompleted(SimulatorEvent e) {
            if (!Grader.this.initialPropagationCompleted) {
                Grader.this.initialPropagationCompleted = true;
                Grader.this.fetchGradingPins();
                Grader.this.curRun = 0;
                Grader.this.waitingNewRun = true;
                Grader.this.simulator.requestReset();
                Grader.this.checkChips();
                return;
            }
            if (Grader.this.waitingNewRun) {
                Grader.this.waitingNewRun = false;
                Grader.this.curState = 1;
                Grader.this.curPhase = 0;
                Grader.this.ifCount = new HashMap();
            }
            if (Grader.this.curRun >= Settings.runs.size()) {
                return;
            }
            Run r = Settings.runs.get(Grader.this.curRun);
            boolean runFailed = false;
            while (Grader.this.curState < r.states.size()) {
                State s = r.states.get(Grader.this.curState);
                if (s.type == State.TYPE.REGISTER_MODIFY) {
                    Grader.this.applyRegisterModify(s);
                    Grader.this.curState++;
                    continue;
                }
                if (s.type == State.TYPE.TRUTH_TABLE) {
                    if (Grader.this.curPhase == 0) {
                        Grader.this.setInputs(s.inputs);
                        Grader.this.curPhase = 1;
                        return;
                    }
                    if (!Grader.this.checkOutputs(s.outputs)) {
                        runFailed = true;
                        Grader.this.curState = r.states.size();
                        continue;
                    }
                    Grader.this.curState++;
                    Grader.this.curPhase = 0;
                    continue;
                }
                if (s.type != State.TYPE.CONDITION) continue;
                if (!Grader.this.checkOutputs(s.outputs)) {
                    Grader.this.curState++;
                    Grader.this.curPhase = 0;
                    continue;
                }
                Integer a = (Integer)Grader.this.ifCount.get(new Integer(Grader.this.curState));
                if (a == null) {
                    a = new Integer(0);
                }
                a = a + 1;
                Grader.this.ifCount.put(Grader.this.curState, a);
                if (a > s.gotoLimit) {
                    runFailed = true;
                    Grader.this.curState = r.states.size();
                    continue;
                }
                Grader.this.curState = s.gotoState;
                Grader.this.curPhase = 0;
            }
            if (runFailed) {
                System.out.println("FAIL");
            } else {
                System.out.println("PASS");
            }
            Grader.this.curRun++;
            if (Grader.this.curRun < Settings.runs.size()) {
                Grader.this.waitingNewRun = true;
                Grader.this.simulator.requestReset();
            } else {
                System.exit(0);
            }
        }

        @Override
        public void tickCompleted(SimulatorEvent e) {
        }

        @Override
        public void simulatorStateChanged(SimulatorEvent e) {
        }
    }

}

