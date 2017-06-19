/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.gui.ExpressionView;
import com.cburch.logisim.analyze.gui.KarnaughMapPanel;
import com.cburch.logisim.analyze.gui.OutputSelector;
import com.cburch.logisim.analyze.gui.Strings;
import com.cburch.logisim.analyze.gui.TruthTableMouseListener;
import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.OutputExpressions;
import com.cburch.logisim.analyze.model.OutputExpressionsEvent;
import com.cburch.logisim.analyze.model.OutputExpressionsListener;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JPanel;

class MinimizedTab
extends JPanel {
    private OutputSelector selector;
    private KarnaughMapPanel karnaughMap;
    private ExpressionView minimizedExpr = new ExpressionView();
    private JButton setAsExpr = new JButton();
    private MyListener myListener;
    private OutputExpressions outputExprs;

    public MinimizedTab(AnalyzerModel model) {
        this.myListener = new MyListener();
        this.outputExprs = model.getOutputExpressions();
        this.outputExprs.addOutputExpressionsListener(this.myListener);
        this.selector = new OutputSelector(model);
        this.selector.addItemListener(this.myListener);
        this.karnaughMap = new KarnaughMapPanel(model);
        this.karnaughMap.addMouseListener(new TruthTableMouseListener());
        this.setAsExpr.addActionListener(this.myListener);
        JPanel buttons = new JPanel(new GridLayout(1, 1));
        buttons.add(this.setAsExpr);
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        this.setLayout(gb);
        gc.weightx = 1.0;
        gc.gridx = 0;
        gc.gridy = -1;
        gc.fill = 1;
        gb.setConstraints(this.selector, gc);
        this.add(this.selector);
        gb.setConstraints(this.karnaughMap, gc);
        this.add(this.karnaughMap);
        Insets oldInsets = gc.insets;
        gc.insets = new Insets(20, 0, 0, 0);
        gb.setConstraints(this.minimizedExpr, gc);
        this.add(this.minimizedExpr);
        gc.insets = oldInsets;
        gc.fill = 0;
        gb.setConstraints(buttons, gc);
        this.add(buttons);
        String selected = this.selector.getSelectedOutput();
        this.setAsExpr.setEnabled(selected != null && !this.outputExprs.isExpressionMinimal(selected));
    }

    void localeChanged() {
        this.selector.localeChanged();
        this.karnaughMap.localeChanged();
        this.minimizedExpr.localeChanged();
        this.setAsExpr.setText(Strings.get("minimizedSetButton"));
    }

    private String getCurrentVariable() {
        return this.selector.getSelectedOutput();
    }

    private class MyListener
    implements OutputExpressionsListener,
    ActionListener,
    ItemListener {
        private MyListener() {
        }

        @Override
        public void expressionChanged(OutputExpressionsEvent event) {
            String output = MinimizedTab.this.getCurrentVariable();
            if (event.getType() == 2 && event.getVariable().equals(output)) {
                MinimizedTab.this.minimizedExpr.setExpression(MinimizedTab.this.outputExprs.getMinimalExpression(output));
                MinimizedTab.this.validate();
            }
            MinimizedTab.this.setAsExpr.setEnabled(output != null && !MinimizedTab.this.outputExprs.isExpressionMinimal(output));
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            String output = MinimizedTab.this.getCurrentVariable();
            MinimizedTab.this.outputExprs.setExpression(output, MinimizedTab.this.outputExprs.getMinimalExpression(output));
        }

        @Override
        public void itemStateChanged(ItemEvent event) {
            String output = MinimizedTab.this.getCurrentVariable();
            MinimizedTab.this.karnaughMap.setOutput(output);
            MinimizedTab.this.minimizedExpr.setExpression(MinimizedTab.this.outputExprs.getMinimalExpression(output));
            MinimizedTab.this.setAsExpr.setEnabled(output != null && !MinimizedTab.this.outputExprs.isExpressionMinimal(output));
        }
    }

}

