/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Simulator;
import com.cburch.logisim.circuit.SimulatorEvent;
import com.cburch.logisim.circuit.SimulatorListener;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.log.FilePanel;
import com.cburch.logisim.gui.log.LogPanel;
import com.cburch.logisim.gui.log.Model;
import com.cburch.logisim.gui.log.ScrollPanel;
import com.cburch.logisim.gui.log.SelectionPanel;
import com.cburch.logisim.gui.log.Strings;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.ProjectEvent;
import com.cburch.logisim.proj.ProjectListener;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.StringUtil;
import com.cburch.logisim.util.WindowMenuItemManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class LogFrame
extends JFrame {
    private Project project;
    private Simulator curSimulator = null;
    private Model curModel;
    private Map modelMap = new HashMap();
    private MyListener myListener;
    private WindowMenuManager windowManager;
    private LogPanel[] panels;
    private JTabbedPane tabbedPane;
    private JButton close;

    public LogFrame(Project project) {
        this.myListener = new MyListener();
        this.close = new JButton();
        this.project = project;
        this.windowManager = new WindowMenuManager();
        project.addProjectListener(this.myListener);
        this.setDefaultCloseOperation(1);
        this.setJMenuBar(new LogisimMenuBar(this, project));
        this.setSimulator(project.getSimulator(), project.getCircuitState());
        this.panels = new LogPanel[]{new SelectionPanel(this), new ScrollPanel(this), new FilePanel(this)};
        this.tabbedPane = new JTabbedPane();
        for (int index = 0; index < this.panels.length; ++index) {
            LogPanel panel = this.panels[index];
            this.tabbedPane.addTab(panel.getTitle(), null, panel, panel.getToolTipText());
        }
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.close);
        this.close.addActionListener(this.myListener);
        Container contents = this.getContentPane();
        this.tabbedPane.setPreferredSize(new Dimension(450, 300));
        contents.add((Component)this.tabbedPane, "Center");
        contents.add((Component)buttonPanel, "South");
        LocaleManager.addLocaleListener(this.myListener);
        this.myListener.localeChanged();
        this.pack();
    }

    public Project getProject() {
        return this.project;
    }

    Model getModel() {
        return this.curModel;
    }

    private void setSimulator(Simulator value, CircuitState state) {
        if (value == null == (this.curModel == null) && (value == null || value.getCircuitState() == this.curModel.getCircuitState())) {
            return;
        }
        LogisimMenuBar menubar = (LogisimMenuBar)this.getJMenuBar();
        menubar.setCircuitState(value, state);
        if (this.curSimulator != null) {
            this.curSimulator.removeSimulatorListener(this.myListener);
        }
        if (this.curModel != null) {
            this.curModel.setSelected(this, false);
        }
        Model oldModel = this.curModel;
        Model data = null;
        if (value != null && (data = (Model)this.modelMap.get(value.getCircuitState())) == null) {
            data = new Model(value.getCircuitState());
            this.modelMap.put(data.getCircuitState(), data);
        }
        this.curSimulator = value;
        this.curModel = data;
        if (this.curSimulator != null) {
            this.curSimulator.addSimulatorListener(this.myListener);
        }
        if (this.curModel != null) {
            this.curModel.setSelected(this, true);
        }
        this.setTitle(LogFrame.computeTitle(this.curModel, this.project));
        if (this.panels != null) {
            for (int i = 0; i < this.panels.length; ++i) {
                this.panels[i].modelChanged(oldModel, this.curModel);
            }
        }
    }

    @Override
    public void setVisible(boolean value) {
        if (value) {
            this.windowManager.frameOpened(this);
        }
        super.setVisible(value);
    }

    LogPanel[] getPrefPanels() {
        return this.panels;
    }

    private static String computeTitle(Model data, Project proj) {
        String name = data == null ? "???" : data.getCircuitState().getCircuit().getDisplayName();
        return StringUtil.format(Strings.get("logFrameTitle"), name, proj.getLogisimFile().getDisplayName());
    }

    private class MyListener
    implements ActionListener,
    ProjectListener,
    SimulatorListener,
    LocaleListener {
        private MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src == LogFrame.this.close) {
                WindowEvent e = new WindowEvent(LogFrame.this, 201);
                LogFrame.this.processWindowEvent(e);
            }
        }

        @Override
        public void projectChanged(ProjectEvent event) {
            int action = event.getAction();
            if (action == 5) {
                LogFrame.this.setSimulator(event.getProject().getSimulator(), event.getProject().getCircuitState());
            } else if (action == 0) {
                LogFrame.this.setTitle(LogFrame.computeTitle(LogFrame.this.curModel, LogFrame.this.project));
            }
        }

        @Override
        public void localeChanged() {
            LogFrame.this.setTitle(LogFrame.computeTitle(LogFrame.this.curModel, LogFrame.this.project));
            for (int i = 0; i < LogFrame.this.panels.length; ++i) {
                LogFrame.this.tabbedPane.setTitleAt(i, LogFrame.this.panels[i].getTitle());
                LogFrame.this.tabbedPane.setToolTipTextAt(i, LogFrame.this.panels[i].getToolTipText());
                LogFrame.this.panels[i].localeChanged();
            }
            LogFrame.this.close.setText(Strings.get("closeButton"));
            LogFrame.this.windowManager.localeChanged();
        }

        @Override
        public void propagationCompleted(SimulatorEvent e) {
            LogFrame.this.curModel.propagationCompleted();
        }

        @Override
        public void tickCompleted(SimulatorEvent e) {
        }

        @Override
        public void simulatorStateChanged(SimulatorEvent e) {
        }
    }

    private class WindowMenuManager
    extends WindowMenuItemManager
    implements LocaleListener,
    ProjectListener {
        WindowMenuManager() {
            super(Strings.get("logFrameMenuItem"), false);
            LogFrame.this.project.addProjectListener(this);
        }

        @Override
        public JFrame getJFrame(boolean create) {
            return LogFrame.this;
        }

        @Override
        public void localeChanged() {
            String title = LogFrame.this.project.getLogisimFile().getDisplayName();
            this.setText(StringUtil.format(Strings.get("logFrameMenuItem"), title));
        }

        @Override
        public void projectChanged(ProjectEvent event) {
            if (event.getAction() == 0) {
                this.localeChanged();
            }
        }
    }

}

