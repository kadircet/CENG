/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.PropagationPoints;
import com.cburch.logisim.circuit.Splitter;
import com.cburch.logisim.circuit.Subcircuit;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.EndData;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.PQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

class Propagator {
    private CircuitState root;
    private int simLimit = 1000;
    private volatile int simRandomShift;
    private PQueue toProcess = new PQueue();
    private int clock = 0;
    private boolean isOscillating = false;
    private boolean oscAdding = false;
    private PropagationPoints oscPoints = new PropagationPoints();
    private int ticks = 0;
    private Random noiseSource = new Random();
    private int noiseCount = 0;
    private int setDataSerialNumber = 0;
    static int lastId = 0;
    int id = lastId++;

    public Propagator(CircuitState root) {
        this.root = root;
        Listener l = new Listener(this);
        root.getProject().getOptions().getAttributeSet().addAttributeListener(l);
        this.updateRandomness();
    }

    private void updateRandomness() {
        Options opts = this.root.getProject().getOptions();
        Object rand = opts.getAttributeSet().getValue(Options.sim_rand_attr);
        int val = (Integer)rand;
        int logVal = 0;
        while (1 << logVal < val) {
            ++logVal;
        }
        this.simRandomShift = logVal;
    }

    boolean isOscillating() {
        return this.isOscillating;
    }

    public String toString() {
        return "Prop" + this.id;
    }

    public void drawOscillatingPoints(ComponentDrawContext context) {
        if (this.isOscillating) {
            this.oscPoints.draw(context);
        }
    }

    CircuitState getRootState() {
        return this.root;
    }

    void reset() {
        this.toProcess.clear();
        this.root.reset();
        this.isOscillating = false;
    }

    void propagate() {
        this.oscPoints.clear();
        this.clearDirtyPoints();
        this.clearDirtyComponents();
        int oscThreshold = this.simLimit;
        int logThreshold = 3 * oscThreshold / 4;
        int iters = 0;
        while (!this.toProcess.isEmpty()) {
            if (++iters < logThreshold) {
                this.stepInternal(null);
                continue;
            }
            if (iters < oscThreshold) {
                this.oscAdding = true;
                this.stepInternal(this.oscPoints);
                continue;
            }
            this.isOscillating = true;
            this.oscAdding = false;
            return;
        }
        this.isOscillating = false;
        this.oscAdding = false;
        this.oscPoints.clear();
    }

    void step(PropagationPoints changedPoints) {
        this.oscPoints.clear();
        this.clearDirtyPoints();
        this.clearDirtyComponents();
        PropagationPoints oldOsc = this.oscPoints;
        this.oscAdding = changedPoints != null;
        this.oscPoints = changedPoints;
        this.stepInternal(changedPoints);
        this.oscAdding = false;
        this.oscPoints = oldOsc;
    }

    private void stepInternal(PropagationPoints changedPoints) {
        SetData data;
        if (this.toProcess.isEmpty()) {
            return;
        }
        this.clock = ((SetData)this.toProcess.peek()).time;
        HashMap<CircuitState, HashSet<ComponentPoint>> visited = new HashMap<CircuitState, HashSet<ComponentPoint>>();
        while ((data = (SetData)this.toProcess.peek()) != null && data.time == this.clock) {
            this.toProcess.remove();
            CircuitState state = data.state;
            HashSet<ComponentPoint> handled = (HashSet<ComponentPoint>)visited.get(state);
            if (handled != null) {
                if (!handled.add(new ComponentPoint(data.cause, data.loc))) {
                    continue;
                }
            } else {
                handled = new HashSet<ComponentPoint>();
                visited.put(state, handled);
                handled.add(new ComponentPoint(data.cause, data.loc));
            }
            if (changedPoints != null) {
                changedPoints.add(state, data.loc);
            }
            SetData oldHead = (SetData)state.causes.get(data.loc);
            Value oldVal = Propagator.computeValue(oldHead);
            SetData newHead = this.addCause(state, oldHead, data);
            Value newVal = Propagator.computeValue(newHead);
            if (newVal.equals(oldVal)) continue;
            state.markPointAsDirty(data.loc);
        }
        this.clearDirtyPoints();
        this.clearDirtyComponents();
    }

    boolean isPending() {
        return !this.toProcess.isEmpty();
    }

    void locationTouched(CircuitState state, Location loc) {
        if (this.oscAdding) {
            this.oscPoints.add(state, loc);
        }
    }

    void setValue(CircuitState state, Location pt, Value val, Component cause, int delay) {
        int randomShift;
        if (cause instanceof Wire || cause instanceof Splitter) {
            return;
        }
        if (delay <= 0) {
            delay = 1;
        }
        if ((randomShift = this.simRandomShift) > 0) {
            delay <<= randomShift;
            if (!(cause instanceof Subcircuit)) {
                if (this.noiseCount > 0) {
                    --this.noiseCount;
                } else {
                    ++delay;
                    this.noiseCount = this.noiseSource.nextInt(1 << randomShift);
                }
            }
        }
        this.toProcess.add(new SetData(this.clock + delay, this.setDataSerialNumber, state, pt, cause, val));
        ++this.setDataSerialNumber;
    }

    boolean tick() {
        ++this.ticks;
        return this.root.tick(this.ticks);
    }

    void checkComponentEnds(CircuitState state, Component comp) {
        for (EndData end : comp.getEnds()) {
            Location loc = end.getLocation();
            SetData oldHead = (SetData)state.causes.get(loc);
            Value oldVal = Propagator.computeValue(oldHead);
            SetData newHead = this.removeCause(state, oldHead, loc, comp);
            Value newVal = Propagator.computeValue(newHead);
            Value wireVal = state.getValueByWire(loc);
            if (!newVal.equals(oldVal) || wireVal != null) {
                state.markPointAsDirty(loc);
            }
            if (wireVal == null) continue;
            state.setValueByWire(loc, Value.NIL);
        }
    }

    private void clearDirtyPoints() {
        this.root.processDirtyPoints();
    }

    private void clearDirtyComponents() {
        this.root.processDirtyComponents();
    }

    private SetData addCause(CircuitState state, SetData head, SetData data) {
        if (data.val == null || data.val.isUnknown()) {
            return this.removeCause(state, head, data.loc, data.cause);
        }
        HashMap causes = state.causes;
        boolean replaced = false;
        SetData n = head;
        while (n != null) {
            if (n.cause == data.cause) {
                n.val = data.val;
                replaced = true;
                break;
            }
            n = n.next;
        }
        if (!replaced) {
            if (head == null) {
                causes.put(data.loc, data);
                head = data;
            } else {
                data.next = head.next;
                head.next = data;
            }
        }
        return head;
    }

    private SetData removeCause(CircuitState state, SetData head, Location loc, Component cause) {
        HashMap causes = state.causes;
        if (head != null) {
            if (head.cause == cause) {
                head = head.next;
                if (head == null) {
                    causes.remove(loc);
                } else {
                    causes.put(loc, head);
                }
            } else {
                SetData prev = head;
                SetData cur = head.next;
                while (cur != null) {
                    if (cur.cause == cause) {
                        prev.next = cur.next;
                        break;
                    }
                    prev = cur;
                    cur = cur.next;
                }
            }
        }
        return head;
    }

    static Value computeValue(SetData causes) {
        if (causes == null) {
            return Value.NIL;
        }
        Value ret = causes.val;
        SetData n = causes.next;
        while (n != null) {
            ret = ret.combine(n.val);
            n = n.next;
        }
        return ret;
    }

    private static class Listener
    implements AttributeListener {
        WeakReference prop;

        public Listener(Propagator propagator) {
            this.prop = new WeakReference<Propagator>(propagator);
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            Propagator p = (Propagator)this.prop.get();
            if (p == null) {
                e.getSource().removeAttributeListener(this);
            } else if (e.getAttribute().equals(Options.sim_rand_attr)) {
                p.updateRandomness();
            }
        }
    }

    private static class ComponentPoint {
        Component cause;
        Location loc;

        public ComponentPoint(Component cause, Location loc) {
            this.cause = cause;
            this.loc = loc;
        }

        public int hashCode() {
            return 31 * this.cause.hashCode() + this.loc.hashCode();
        }

        public boolean equals(Object other) {
            if (!(other instanceof ComponentPoint)) {
                return false;
            }
            ComponentPoint o = (ComponentPoint)other;
            return this.cause.equals(o.cause) && this.loc.equals(o.loc);
        }
    }

    static class SetData
    implements Comparable {
        int time;
        int serialNumber;
        CircuitState state;
        Component cause;
        Location loc;
        Value val;
        SetData next = null;

        private SetData(int time, int serialNumber, CircuitState state, Location loc, Component cause, Value val) {
            this.time = time;
            this.serialNumber = serialNumber;
            this.state = state;
            this.cause = cause;
            this.loc = loc;
            this.val = val;
        }

        public int compareTo(Object other) {
            SetData o = (SetData)other;
            int ret = o.time - this.time;
            if (ret != 0) {
                return ret;
            }
            return o.serialNumber - this.serialNumber;
        }

        public SetData cloneFor(CircuitState newState) {
            Propagator newProp = newState.getPropagator();
            int dtime = newProp.clock - this.state.getPropagator().clock;
            SetData ret = new SetData(this.time + dtime, newProp.setDataSerialNumber, newState, this.loc, this.cause, this.val);
            newProp.setDataSerialNumber++;
            if (this.next != null) {
                ret.next = this.next.cloneFor(newState);
            }
            return ret;
        }

        public String toString() {
            return this.loc + ":" + this.val + "(" + this.cause + ")";
        }
    }

}

