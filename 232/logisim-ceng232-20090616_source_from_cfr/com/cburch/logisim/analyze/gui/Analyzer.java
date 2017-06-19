/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.gui.BuildCircuitButton;
import com.cburch.logisim.analyze.gui.DefaultRegistry;
import com.cburch.logisim.analyze.gui.ExpressionTab;
import com.cburch.logisim.analyze.gui.MinimizedTab;
import com.cburch.logisim.analyze.gui.Strings;
import com.cburch.logisim.analyze.gui.TabInterface;
import com.cburch.logisim.analyze.gui.TableTab;
import com.cburch.logisim.analyze.gui.TruthTableMouseListener;
import com.cburch.logisim.analyze.gui.VariableTab;
import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.analyze.model.TruthTable;
import com.cburch.logisim.analyze.model.VariableList;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.gui.menu.LogisimMenuItem;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Analyzer
extends JFrame {
    public static final int INPUTS_TAB = 0;
    public static final int OUTPUTS_TAB = 1;
    public static final int TABLE_TAB = 2;
    public static final int EXPRESSION_TAB = 3;
    public static final int MINIMIZED_TAB = 4;
    private MyListener myListener;
    private EditListener editListener;
    private AnalyzerModel model;
    private JTabbedPane tabbedPane;
    private VariableTab inputsPanel;
    private VariableTab outputsPanel;
    private TableTab truthTablePanel;
    private ExpressionTab expressionPanel;
    private MinimizedTab minimizedPanel;
    private BuildCircuitButton buildCircuit;

    Analyzer() {
        this.myListener = new MyListener();
        this.editListener = new EditListener();
        this.model = new AnalyzerModel();
        this.tabbedPane = new JTabbedPane();
        this.inputsPanel = new VariableTab(this.model.getInputs());
        this.outputsPanel = new VariableTab(this.model.getOutputs());
        this.truthTablePanel = new TableTab(this.model.getTruthTable());
        this.expressionPanel = new ExpressionTab(this.model);
        this.minimizedPanel = new MinimizedTab(this.model);
        this.buildCircuit = new BuildCircuitButton(this, this.model);
        this.truthTablePanel.addMouseListener(new TruthTableMouseListener());
        this.tabbedPane = new JTabbedPane();
        this.addTab(0, this.inputsPanel);
        this.addTab(1, this.outputsPanel);
        this.addTab(2, this.truthTablePanel);
        this.addTab(3, this.expressionPanel);
        this.addTab(4, this.minimizedPanel);
        Container contents = this.getContentPane();
        JPanel vertStrut = new JPanel(null);
        vertStrut.setPreferredSize(new Dimension(0, 300));
        JPanel horzStrut = new JPanel(null);
        horzStrut.setPreferredSize(new Dimension(450, 0));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.buildCircuit);
        contents.add((Component)vertStrut, "West");
        contents.add((Component)horzStrut, "North");
        contents.add((Component)this.tabbedPane, "Center");
        contents.add((Component)buttonPanel, "South");
        DefaultRegistry registry = new DefaultRegistry(this.getRootPane());
        this.inputsPanel.registerDefaultButtons(registry);
        this.outputsPanel.registerDefaultButtons(registry);
        this.expressionPanel.registerDefaultButtons(registry);
        LocaleManager.addLocaleListener(this.myListener);
        this.myListener.localeChanged();
        LogisimMenuBar menubar = new LogisimMenuBar(this, null);
        this.setJMenuBar(menubar);
        this.editListener.register(menubar);
    }

    private void addTab(int index, final JComponent comp) {
        final JScrollPane pane = new JScrollPane(comp, 22, 31);
        if (comp instanceof TableTab) {
            pane.setVerticalScrollBar(((TableTab)comp).getVerticalScrollBar());
        }
        pane.addComponentListener(new ComponentListener(){

            @Override
            public void componentResized(ComponentEvent event) {
                int width = pane.getViewport().getWidth();
                comp.setSize(new Dimension(width, comp.getHeight()));
            }

            @Override
            public void componentMoved(ComponentEvent arg0) {
            }

            @Override
            public void componentShown(ComponentEvent arg0) {
            }

            @Override
            public void componentHidden(ComponentEvent arg0) {
            }
        });
        this.tabbedPane.insertTab("Untitled", null, pane, null, index);
    }

    public AnalyzerModel getModel() {
        return this.model;
    }

    public void setSelectedTab(int index) {
        this.tabbedPane.setSelectedIndex(index);
    }

    public static void main(String[] args) {
        Analyzer frame = new Analyzer();
        frame.setDefaultCloseOperation(3);
        frame.pack();
        frame.setVisible(true);
    }

    private class EditListener
    implements ActionListener,
    ChangeListener {
        private EditListener() {
        }

        private void register(LogisimMenuBar menubar) {
            menubar.addActionListener(LogisimMenuBar.CUT, this);
            menubar.addActionListener(LogisimMenuBar.COPY, this);
            menubar.addActionListener(LogisimMenuBar.PASTE, this);
            menubar.addActionListener(LogisimMenuBar.DELETE, this);
            menubar.addActionListener(LogisimMenuBar.SELECT_ALL, this);
            Analyzer.this.tabbedPane.addChangeListener(this);
            this.enableItems(menubar);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            Component c = Analyzer.this.tabbedPane.getSelectedComponent();
            if (c instanceof JScrollPane) {
                c = ((JScrollPane)c).getViewport().getView();
            }
            if (!(c instanceof TabInterface)) {
                return;
            }
            TabInterface tab = (TabInterface)((Object)c);
            if (src == LogisimMenuBar.CUT) {
                tab.copy();
                tab.delete();
            } else if (src == LogisimMenuBar.COPY) {
                tab.copy();
            } else if (src == LogisimMenuBar.PASTE) {
                tab.paste();
            } else if (src == LogisimMenuBar.DELETE) {
                tab.delete();
            } else if (src == LogisimMenuBar.SELECT_ALL) {
                tab.selectAll();
            }
        }

        private void enableItems(LogisimMenuBar menubar) {
            Component c = Analyzer.this.tabbedPane.getSelectedComponent();
            if (c instanceof JScrollPane) {
                c = ((JScrollPane)c).getViewport().getView();
            }
            boolean support = c instanceof TabInterface;
            menubar.setEnabled(LogisimMenuBar.CUT, support);
            menubar.setEnabled(LogisimMenuBar.COPY, support);
            menubar.setEnabled(LogisimMenuBar.PASTE, support);
            menubar.setEnabled(LogisimMenuBar.DELETE, support);
            menubar.setEnabled(LogisimMenuBar.SELECT_ALL, support);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            this.enableItems((LogisimMenuBar)Analyzer.this.getJMenuBar());
        }
    }

    private class MyListener
    implements LocaleListener {
        private MyListener() {
        }

        @Override
        public void localeChanged() {
            Analyzer.this.setTitle(Strings.get("analyzerWindowTitle"));
            Analyzer.this.tabbedPane.setTitleAt(0, Strings.get("inputsTab"));
            Analyzer.this.tabbedPane.setTitleAt(1, Strings.get("outputsTab"));
            Analyzer.this.tabbedPane.setTitleAt(2, Strings.get("tableTab"));
            Analyzer.this.tabbedPane.setTitleAt(3, Strings.get("expressionTab"));
            Analyzer.this.tabbedPane.setTitleAt(4, Strings.get("minimizedTab"));
            Analyzer.this.tabbedPane.setToolTipTextAt(0, Strings.get("inputsTabTip"));
            Analyzer.this.tabbedPane.setToolTipTextAt(1, Strings.get("outputsTabTip"));
            Analyzer.this.tabbedPane.setToolTipTextAt(2, Strings.get("tableTabTip"));
            Analyzer.this.tabbedPane.setToolTipTextAt(3, Strings.get("expressionTabTip"));
            Analyzer.this.tabbedPane.setToolTipTextAt(4, Strings.get("minimizedTabTip"));
            Analyzer.this.buildCircuit.setText(Strings.get("buildCircuitButton"));
            Analyzer.this.inputsPanel.localeChanged();
            Analyzer.this.outputsPanel.localeChanged();
            Analyzer.this.truthTablePanel.localeChanged();
            Analyzer.this.expressionPanel.localeChanged();
            Analyzer.this.minimizedPanel.localeChanged();
            Analyzer.this.buildCircuit.localeChanged();
        }
    }

}

