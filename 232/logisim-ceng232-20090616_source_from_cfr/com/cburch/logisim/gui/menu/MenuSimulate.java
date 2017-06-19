/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.menu;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitEvent;
import com.cburch.logisim.circuit.CircuitListener;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Simulator;
import com.cburch.logisim.circuit.SimulatorEvent;
import com.cburch.logisim.circuit.SimulatorListener;
import com.cburch.logisim.gui.log.LogFrame;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.gui.menu.Strings;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.StringUtil;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class MenuSimulate
extends JMenu {
    private LogisimMenuBar menubar;
    private MyListener myListener;
    private CircuitState currentState;
    private CircuitState bottomState;
    private Simulator currentSim;
    private JCheckBoxMenuItem run;
    private JMenuItem reset;
    private JMenuItem step;
    private JCheckBoxMenuItem ticksEnabled;
    private JMenuItem tickOnce;
    private JMenu tickFreq;
    private TickFrequencyChoice[] tickFreqs;
    private JMenu downStateMenu;
    private ArrayList downStateItems;
    private JMenu upStateMenu;
    private ArrayList upStateItems;
    private JMenuItem log;

    public MenuSimulate(LogisimMenuBar menubar) {
        this.myListener = new MyListener();
        this.currentState = null;
        this.bottomState = null;
        this.currentSim = null;
        this.run = new JCheckBoxMenuItem();
        this.reset = new JMenuItem();
        this.step = new JMenuItem();
        this.ticksEnabled = new JCheckBoxMenuItem();
        this.tickOnce = new JMenuItem();
        this.tickFreq = new JMenu();
        this.tickFreqs = new TickFrequencyChoice[]{new TickFrequencyChoice(1024.0), new TickFrequencyChoice(512.0), new TickFrequencyChoice(256.0), new TickFrequencyChoice(64.0), new TickFrequencyChoice(32.0), new TickFrequencyChoice(16.0), new TickFrequencyChoice(8.0), new TickFrequencyChoice(4.0), new TickFrequencyChoice(2.0), new TickFrequencyChoice(1.0), new TickFrequencyChoice(0.5), new TickFrequencyChoice(0.25)};
        this.downStateMenu = new JMenu();
        this.downStateItems = new ArrayList();
        this.upStateMenu = new JMenu();
        this.upStateItems = new ArrayList();
        this.log = new JMenuItem();
        this.menubar = menubar;
        int menuMask = this.getToolkit().getMenuShortcutKeyMask();
        this.run.setAccelerator(KeyStroke.getKeyStroke(69, menuMask));
        this.reset.setAccelerator(KeyStroke.getKeyStroke(82, menuMask));
        this.step.setAccelerator(KeyStroke.getKeyStroke(73, menuMask));
        this.tickOnce.setAccelerator(KeyStroke.getKeyStroke(84, menuMask));
        ButtonGroup bgroup = new ButtonGroup();
        for (int i = 0; i < this.tickFreqs.length; ++i) {
            bgroup.add(this.tickFreqs[i]);
            this.tickFreq.add(this.tickFreqs[i]);
        }
        this.add(this.run);
        this.add(this.reset);
        this.add(this.step);
        this.addSeparator();
        this.add(this.upStateMenu);
        this.add(this.downStateMenu);
        this.addSeparator();
        this.add(this.tickOnce);
        this.add(this.ticksEnabled);
        this.add(this.tickFreq);
        this.addSeparator();
        this.add(this.log);
        this.setEnabled(false);
        this.run.setEnabled(false);
        this.reset.setEnabled(false);
        this.step.setEnabled(false);
        this.upStateMenu.setEnabled(false);
        this.downStateMenu.setEnabled(false);
        this.tickOnce.setEnabled(false);
        this.ticksEnabled.setEnabled(false);
        this.tickFreq.setEnabled(false);
        this.run.addChangeListener(this.myListener);
        this.run.addActionListener(this.myListener);
        this.reset.addActionListener(this.myListener);
        this.step.addActionListener(this.myListener);
        this.tickOnce.addActionListener(this.myListener);
        this.ticksEnabled.addActionListener(this.myListener);
        this.log.addActionListener(this.myListener);
    }

    public void localeChanged() {
        this.setText(Strings.get("simulateMenu"));
        this.run.setText(Strings.get("simulateRunItem"));
        this.reset.setText(Strings.get("simulateResetItem"));
        this.step.setText(Strings.get("simulateStepItem"));
        this.tickOnce.setText(Strings.get("simulateTickOnceItem"));
        this.ticksEnabled.setText(Strings.get("simulateTickItem"));
        this.tickFreq.setText(Strings.get("simulateTickFreqMenu"));
        for (int i = 0; i < this.tickFreqs.length; ++i) {
            this.tickFreqs[i].localeChanged();
        }
        this.downStateMenu.setText(Strings.get("simulateDownStateMenu"));
        this.upStateMenu.setText(Strings.get("simulateUpStateMenu"));
        this.log.setText(Strings.get("simulateLogItem"));
    }

    public void setCurrentState(Simulator sim, CircuitState value) {
        CircuitState cur;
        boolean present;
        if (this.currentState == value) {
            return;
        }
        Simulator oldSim = this.currentSim;
        CircuitState oldState = this.currentState;
        this.currentSim = sim;
        this.currentState = value;
        if (this.bottomState == null) {
            this.bottomState = this.currentState;
        } else if (this.currentState == null) {
            this.bottomState = null;
        } else {
            CircuitState cur2;
            for (cur2 = this.bottomState; cur2 != null && cur2 != this.currentState; cur2 = cur2.getParentState()) {
            }
            if (cur2 == null) {
                this.bottomState = this.currentState;
            }
        }
        boolean oldPresent = oldState != null;
        boolean bl = present = this.currentState != null;
        if (oldPresent != present) {
            this.setEnabled(present);
            this.run.setEnabled(present);
            this.reset.setEnabled(present);
            this.step.setEnabled(present && !this.run.isSelected());
            this.upStateMenu.setEnabled(present);
            this.downStateMenu.setEnabled(present);
            this.tickOnce.setEnabled(present);
            this.ticksEnabled.setEnabled(present);
            this.tickFreq.setEnabled(present);
        }
        if (this.currentSim != oldSim) {
            int wavelength = this.currentSim == null ? -1 : this.currentSim.getTickFrequency();
            for (int i = 0; i < this.tickFreqs.length; ++i) {
                this.tickFreqs[i].setSelected((double)Math.abs(this.tickFreqs[i].wavelength - wavelength) < 0.001);
            }
            if (oldSim != null) {
                oldSim.removeSimulatorListener(this.myListener);
            }
            if (this.currentSim != null) {
                this.currentSim.addSimulatorListener(this.myListener);
            }
            this.myListener.simulatorStateChanged(new SimulatorEvent(sim));
        }
        this.clearItems(this.downStateItems);
        for (cur = this.bottomState; cur != null && cur != this.currentState; cur = cur.getParentState()) {
            this.downStateItems.add(new CircuitStateMenuItem(cur));
        }
        if (cur != null) {
            cur = cur.getParentState();
        }
        this.clearItems(this.upStateItems);
        while (cur != null) {
            this.upStateItems.add(0, new CircuitStateMenuItem(cur));
            cur = cur.getParentState();
        }
        this.recreateStateMenus();
    }

    private void clearItems(ArrayList items) {
        for (int i = 0; i < items.size(); ++i) {
            CircuitStateMenuItem item = (CircuitStateMenuItem)items.get(i);
            item.unregister();
        }
        items.clear();
    }

    private void recreateStateMenus() {
        this.recreateStateMenu(this.downStateMenu, this.downStateItems, 40);
        this.recreateStateMenu(this.upStateMenu, this.upStateItems, 38);
    }

    private void recreateStateMenu(JMenu menu, ArrayList items, int code) {
        menu.removeAll();
        menu.setEnabled(items.size() > 0);
        boolean first = true;
        for (int i = items.size() - 1; i >= 0; --i) {
            JMenuItem item = (JMenuItem)items.get(i);
            menu.add(item);
            if (first) {
                int mask = this.getToolkit().getMenuShortcutKeyMask();
                item.setAccelerator(KeyStroke.getKeyStroke(code, mask));
                first = false;
                continue;
            }
            item.setAccelerator(null);
        }
    }

    private class MyListener
    implements ActionListener,
    SimulatorListener,
    ChangeListener {
        private MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Simulator sim;
            Object src = e.getSource();
            Project proj = MenuSimulate.this.menubar.getProject();
            Simulator simulator = sim = proj == null ? null : proj.getSimulator();
            if (src == MenuSimulate.this.run) {
                boolean value = MenuSimulate.this.run.isSelected();
                if (sim != null) {
                    sim.setIsRunning(value);
                    proj.repaintCanvas();
                }
            } else if (src == MenuSimulate.this.reset) {
                if (sim != null) {
                    sim.requestReset();
                }
            } else if (src == MenuSimulate.this.step) {
                if (sim != null) {
                    sim.step();
                }
            } else if (src == MenuSimulate.this.tickOnce) {
                if (sim != null) {
                    sim.tick();
                }
            } else if (src == MenuSimulate.this.ticksEnabled) {
                boolean value = MenuSimulate.this.ticksEnabled.isSelected();
                if (sim != null) {
                    sim.setIsTicking(value);
                }
            } else if (src == MenuSimulate.this.log) {
                LogFrame frame = MenuSimulate.this.menubar.getProject().getLogFrame(true);
                frame.setVisible(true);
            }
        }

        @Override
        public void propagationCompleted(SimulatorEvent e) {
        }

        @Override
        public void tickCompleted(SimulatorEvent e) {
        }

        @Override
        public void simulatorStateChanged(SimulatorEvent e) {
            Simulator sim = e.getSource();
            if (sim != MenuSimulate.this.currentSim) {
                return;
            }
            MenuSimulate.this.run.setSelected(sim.isRunning());
            MenuSimulate.this.ticksEnabled.setEnabled(sim.isRunning());
            MenuSimulate.this.ticksEnabled.setSelected(sim.isTicking());
            int wavelength = sim.getTickFrequency();
            for (int i = 0; i < MenuSimulate.this.tickFreqs.length; ++i) {
                TickFrequencyChoice item;
                item.setSelected(wavelength == (item = MenuSimulate.this.tickFreqs[i]).wavelength);
            }
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            MenuSimulate.this.step.setEnabled(MenuSimulate.this.run.isEnabled() && !MenuSimulate.this.run.isSelected());
        }
    }

    private class CircuitStateMenuItem
    extends JMenuItem
    implements CircuitListener,
    ActionListener {
        private CircuitState circuitState;

        public CircuitStateMenuItem(CircuitState circuitState) {
            this.circuitState = circuitState;
            Circuit circuit = circuitState.getCircuit();
            circuit.addCircuitListener(this);
            this.setText(circuit.getName());
            this.addActionListener(this);
        }

        void unregister() {
            Circuit circuit = this.circuitState.getCircuit();
            circuit.removeCircuitListener(this);
        }

        @Override
        public void circuitChanged(CircuitEvent event) {
            if (event.getAction() == 0) {
                this.setText(this.circuitState.getCircuit().getName());
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            MenuSimulate.this.menubar.fireStateChanged(MenuSimulate.this.currentSim, this.circuitState);
        }
    }

    private class TickFrequencyChoice
    extends JRadioButtonMenuItem
    implements ActionListener {
        private String hertz;
        private int wavelength;

        public TickFrequencyChoice(double value) {
            this.hertz = Math.abs(value - (double)Math.round(value)) < 1.0E-4 ? "" + (int)Math.round(value) : "" + value;
            this.wavelength = (int)Math.round(1000.0 / value);
            this.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (MenuSimulate.this.currentSim != null) {
                MenuSimulate.this.currentSim.setTickFrequency(this.wavelength);
            }
        }

        public void localeChanged() {
            this.setText(StringUtil.format(Strings.get("simulateTickFreqItem"), this.hertz));
        }
    }

}

