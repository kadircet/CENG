/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.PropagationPoints;
import com.cburch.logisim.circuit.Propagator;
import com.cburch.logisim.circuit.SimulatorEvent;
import com.cburch.logisim.circuit.SimulatorListener;
import com.cburch.logisim.comp.ComponentDrawContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Simulator {
    private static final boolean PRINT_TICK_RATE = false;
    private static final int TICK_RATE_QUANTUM = 128;
    private boolean isRunning = true;
    private boolean isTicking = false;
    private boolean exceptionEncountered = false;
    private int tickFrequency = 250;
    private PropagationManager manager;
    private Ticker ticker;
    private ArrayList listeners;

    public Simulator() {
        this.manager = new PropagationManager();
        this.ticker = new Ticker();
        this.listeners = new ArrayList();
        try {
            this.manager.setPriority(this.manager.getPriority() - 1);
            this.ticker.setPriority(this.ticker.getPriority() - 1);
        }
        catch (SecurityException e) {
        }
        catch (IllegalArgumentException e) {
            // empty catch block
        }
        this.manager.start();
        this.ticker.start();
    }

    public void shutDown() {
        this.ticker.shutDown();
        this.manager.shutDown();
    }

    public void setCircuitState(CircuitState state) {
        this.manager.setPropagator(state.getPropagator());
        this.ticker.awake();
    }

    public CircuitState getCircuitState() {
        Propagator prop = this.manager.getPropagator();
        return prop == null ? null : prop.getRootState();
    }

    public void requestReset() {
        this.manager.requestReset();
    }

    public void tick() {
        Ticker ticker = this.ticker;
        synchronized (ticker) {
            this.ticker.ticksPending++;
            this.ticker.notifyAll();
        }
    }

    public void step() {
        PropagationManager propagationManager = this.manager;
        synchronized (propagationManager) {
            this.manager.stepsRequested++;
            this.manager.notifyAll();
        }
    }

    public void drawStepPoints(ComponentDrawContext context) {
        this.manager.stepPoints.draw(context);
    }

    public boolean isExceptionEncountered() {
        return this.exceptionEncountered;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void setIsRunning(boolean value) {
        if (this.isRunning != value) {
            this.isRunning = value;
            this.ticker.awake();
            this.fireSimulatorStateChanged();
        }
    }

    public boolean isTicking() {
        return this.isTicking;
    }

    public void setIsTicking(boolean value) {
        if (this.isTicking != value) {
            this.isTicking = value;
            this.ticker.awake();
            this.fireSimulatorStateChanged();
        }
    }

    public int getTickFrequency() {
        return this.tickFrequency;
    }

    public void setTickFrequency(int millis) {
        if (this.tickFrequency != millis) {
            this.tickFrequency = millis;
            this.ticker.awake();
            this.fireSimulatorStateChanged();
        }
    }

    public void requestPropagate() {
        this.manager.requestPropagate();
    }

    public boolean isOscillating() {
        Propagator prop = this.manager.getPropagator();
        return prop != null && prop.isOscillating();
    }

    public void addSimulatorListener(SimulatorListener l) {
        this.listeners.add(l);
    }

    public void removeSimulatorListener(SimulatorListener l) {
        this.listeners.remove(l);
    }

    void firePropagationCompleted() {
        SimulatorEvent e = new SimulatorEvent(this);
        ArrayList listeners = new ArrayList(this.listeners);
        for (SimulatorListener l : listeners) {
            l.propagationCompleted(e);
        }
    }

    void fireTickCompleted() {
        SimulatorEvent e = new SimulatorEvent(this);
        ArrayList listeners = new ArrayList(this.listeners);
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            ((SimulatorListener)it.next()).tickCompleted(e);
        }
    }

    void fireSimulatorStateChanged() {
        SimulatorEvent e = new SimulatorEvent(this);
        ArrayList listeners = new ArrayList(this.listeners);
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            ((SimulatorListener)it.next()).simulatorStateChanged(e);
        }
    }

    private class Ticker
    extends Thread {
        private boolean shouldTick;
        private int ticksPending;
        private boolean complete;

        private Ticker() {
            this.shouldTick = false;
            this.ticksPending = 0;
            this.complete = false;
        }

        public synchronized void shutDown() {
            this.complete = true;
            this.notifyAll();
        }

        @Override
        public void run() {
            long lastTick = System.currentTimeMillis();
            do {
                int curTickFrequency;
                boolean curShouldTick;
                curShouldTick = this.shouldTick;
                curTickFrequency = Simulator.this.tickFrequency;
                try {
                    Ticker ticker = this;
                    synchronized (ticker) {
                        curShouldTick = this.shouldTick;
                        curTickFrequency = Simulator.this.tickFrequency;
                        while (!curShouldTick && this.ticksPending == 0 && !this.complete) {
                            this.wait();
                            curShouldTick = this.shouldTick;
                            curTickFrequency = Simulator.this.tickFrequency;
                        }
                    }
                }
                catch (InterruptedException e) {
                    // empty catch block
                }
                if (this.complete) break;
                long now = System.currentTimeMillis();
                if (this.ticksPending > 0 || curShouldTick && now >= lastTick + (long)curTickFrequency) {
                    lastTick = now;
                    Simulator.this.manager.requestTick();
                    Ticker ticker = this;
                    synchronized (ticker) {
                        if (this.ticksPending > 0) {
                            --this.ticksPending;
                        }
                    }
                }
                try {
                    long nextTick = lastTick + (long)curTickFrequency;
                    int wait = (int)(nextTick - System.currentTimeMillis());
                    if (wait < 1) {
                        wait = 1;
                    }
                    if (wait > 100) {
                        wait = 100;
                    }
                    Thread.sleep(wait);
                }
                catch (InterruptedException e) {}
            } while (true);
        }

        synchronized void awake() {
            boolean bl = this.shouldTick = Simulator.this.isRunning && Simulator.this.isTicking && Simulator.this.tickFrequency > 0;
            if (this.shouldTick) {
                this.notifyAll();
            }
        }
    }

    private class PropagationManager
    extends Thread {
        private Propagator propagator;
        private PropagationPoints stepPoints;
        private volatile int ticksRequested;
        private volatile int stepsRequested;
        private volatile boolean resetRequested;
        private volatile boolean propagateRequested;
        private volatile boolean complete;
        int tickRateTicks;
        long tickRateStart;

        private PropagationManager() {
            this.propagator = null;
            this.stepPoints = new PropagationPoints();
            this.ticksRequested = 0;
            this.stepsRequested = 0;
            this.resetRequested = false;
            this.propagateRequested = false;
            this.complete = false;
            this.tickRateTicks = 0;
            this.tickRateStart = System.currentTimeMillis();
        }

        public Propagator getPropagator() {
            return this.propagator;
        }

        public void setPropagator(Propagator value) {
            this.propagator = value;
        }

        public synchronized void requestPropagate() {
            if (!this.propagateRequested) {
                this.propagateRequested = true;
                this.notifyAll();
            }
        }

        public synchronized void requestReset() {
            if (!this.resetRequested) {
                this.resetRequested = true;
                this.notifyAll();
            }
        }

        public synchronized void requestTick() {
            ++this.ticksRequested;
            this.notifyAll();
        }

        public synchronized void shutDown() {
            this.complete = true;
            this.notifyAll();
        }

        @Override
        public void run() {
            while (!this.complete) {
                PropagationManager propagationManager = this;
                synchronized (propagationManager) {
                    while (!(this.complete || this.propagateRequested || this.resetRequested || this.ticksRequested != 0 || this.stepsRequested != 0)) {
                        try {
                            this.wait();
                        }
                        catch (InterruptedException e) {}
                    }
                }
                if (this.resetRequested) {
                    this.resetRequested = false;
                    if (this.propagator != null) {
                        this.propagator.reset();
                    }
                    Simulator.this.firePropagationCompleted();
                }
                if (!this.propagateRequested && this.ticksRequested <= 0 && this.stepsRequested <= 0) continue;
                boolean ticked = false;
                this.propagateRequested = false;
                if (Simulator.this.isRunning) {
                    this.stepPoints.clear();
                    this.stepsRequested = 0;
                    if (this.propagator == null) {
                        this.ticksRequested = 0;
                    } else {
                        boolean bl = ticked = this.ticksRequested > 0;
                        if (ticked) {
                            this.doTick();
                        }
                        do {
                            this.propagateRequested = false;
                            try {
                                Simulator.this.exceptionEncountered = false;
                                this.propagator.propagate();
                                continue;
                            }
                            catch (Throwable thr) {
                                thr.printStackTrace();
                                Simulator.this.exceptionEncountered = true;
                                Simulator.this.setIsRunning(false);
                            }
                        } while (this.propagateRequested);
                        if (Simulator.this.isOscillating()) {
                            Simulator.this.setIsRunning(false);
                            this.ticksRequested = 0;
                            this.propagateRequested = false;
                        }
                    }
                } else if (this.stepsRequested > 0) {
                    PropagationManager thr = this;
                    synchronized (thr) {
                        --this.stepsRequested;
                    }
                    Simulator.this.exceptionEncountered = false;
                    try {
                        this.stepPoints.clear();
                        this.propagator.step(this.stepPoints);
                    }
                    catch (Throwable thr) {
                        thr.printStackTrace();
                        Simulator.this.exceptionEncountered = true;
                    }
                }
                if (ticked) {
                    Simulator.this.fireTickCompleted();
                }
                Simulator.this.firePropagationCompleted();
            }
        }

        private void doTick() {
            PropagationManager propagationManager = this;
            synchronized (propagationManager) {
                --this.ticksRequested;
            }
            this.propagator.tick();
        }
    }

}

