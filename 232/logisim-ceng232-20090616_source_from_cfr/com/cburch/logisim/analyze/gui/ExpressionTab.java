/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.gui.DefaultRegistry;
import com.cburch.logisim.analyze.gui.ExpressionView;
import com.cburch.logisim.analyze.gui.OutputSelector;
import com.cburch.logisim.analyze.gui.Strings;
import com.cburch.logisim.analyze.gui.TabInterface;
import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.OutputExpressions;
import com.cburch.logisim.analyze.model.OutputExpressionsEvent;
import com.cburch.logisim.analyze.model.OutputExpressionsListener;
import com.cburch.logisim.analyze.model.Parser;
import com.cburch.logisim.analyze.model.ParserException;
import com.cburch.logisim.util.StringGetter;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

class ExpressionTab
extends JPanel
implements TabInterface {
    private OutputSelector selector;
    private ExpressionView prettyView = new ExpressionView();
    private JTextArea field = new JTextArea(4, 25);
    private JButton clear = new JButton();
    private JButton revert = new JButton();
    private JButton enter = new JButton();
    private JLabel error = new JLabel();
    private MyListener myListener;
    private AnalyzerModel model;
    private int curExprStringLength;
    private StringGetter errorMessage;

    public ExpressionTab(AnalyzerModel model) {
        this.myListener = new MyListener();
        this.curExprStringLength = 0;
        this.model = model;
        this.selector = new OutputSelector(model);
        model.getOutputExpressions().addOutputExpressionsListener(this.myListener);
        this.selector.addItemListener(this.myListener);
        this.clear.addActionListener(this.myListener);
        this.revert.addActionListener(this.myListener);
        this.enter.addActionListener(this.myListener);
        this.field.setLineWrap(true);
        this.field.setWrapStyleWord(true);
        this.field.getInputMap().put(KeyStroke.getKeyStroke(10, 0), this.myListener);
        this.field.getDocument().addDocumentListener(this.myListener);
        JPanel buttons = new JPanel();
        buttons.add(this.clear);
        buttons.add(this.revert);
        buttons.add(this.enter);
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        this.setLayout(gb);
        gc.weightx = 1.0;
        gc.gridx = 0;
        gc.gridy = -1;
        gc.fill = 1;
        gb.setConstraints(this.selector, gc);
        this.add(this.selector);
        gb.setConstraints(this.prettyView, gc);
        this.add(this.prettyView);
        Insets oldInsets = gc.insets;
        gc.insets = new Insets(10, 10, 0, 10);
        JScrollPane fieldPane = new JScrollPane(this.field, 22, 31);
        gb.setConstraints(fieldPane, gc);
        this.add(fieldPane);
        gc.insets = oldInsets;
        gc.fill = 0;
        gc.anchor = 22;
        gb.setConstraints(buttons, gc);
        this.add(buttons);
        gc.fill = 1;
        gb.setConstraints(this.error, gc);
        this.add(this.error);
        this.myListener.insertUpdate(null);
        this.setError(null);
    }

    void localeChanged() {
        this.selector.localeChanged();
        this.prettyView.localeChanged();
        this.clear.setText(Strings.get("exprClearButton"));
        this.revert.setText(Strings.get("exprRevertButton"));
        this.enter.setText(Strings.get("exprEnterButton"));
        if (this.errorMessage != null) {
            this.error.setText(this.errorMessage.get());
        }
    }

    void registerDefaultButtons(DefaultRegistry registry) {
        registry.registerDefaultButton(this.field, this.enter);
    }

    String getCurrentVariable() {
        return this.selector.getSelectedOutput();
    }

    private void setError(StringGetter msg) {
        if (msg == null) {
            this.errorMessage = null;
            this.error.setText(" ");
        } else {
            this.errorMessage = msg;
            this.error.setText(msg.get());
        }
    }

    @Override
    public void copy() {
        this.field.requestFocus();
        this.field.copy();
    }

    @Override
    public void paste() {
        this.field.requestFocus();
        this.field.paste();
    }

    @Override
    public void delete() {
        this.field.requestFocus();
        this.field.replaceSelection("");
    }

    @Override
    public void selectAll() {
        this.field.requestFocus();
        this.field.selectAll();
    }

    private class MyListener
    extends AbstractAction
    implements DocumentListener,
    OutputExpressionsListener,
    ItemListener {
        boolean edited;

        private MyListener() {
            this.edited = false;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src == ExpressionTab.this.clear) {
                ExpressionTab.this.setError(null);
                ExpressionTab.this.field.setText("");
                ExpressionTab.this.field.grabFocus();
            } else if (src == ExpressionTab.this.revert) {
                ExpressionTab.this.setError(null);
                ExpressionTab.this.field.setText(this.getCurrentString());
                ExpressionTab.this.field.grabFocus();
            } else if ((src == ExpressionTab.this.field || src == ExpressionTab.this.enter) && ExpressionTab.this.enter.isEnabled()) {
                try {
                    String exprString = ExpressionTab.this.field.getText();
                    Expression expr = Parser.parse(ExpressionTab.this.field.getText(), ExpressionTab.this.model);
                    ExpressionTab.this.setError(null);
                    ExpressionTab.this.model.getOutputExpressions().setExpression(ExpressionTab.this.getCurrentVariable(), expr, exprString);
                    this.insertUpdate(null);
                }
                catch (ParserException ex) {
                    ExpressionTab.this.setError(ex.getMessageGetter());
                    ExpressionTab.this.field.setCaretPosition(ex.getOffset());
                    ExpressionTab.this.field.moveCaretPosition(ex.getEndOffset());
                }
                ExpressionTab.this.field.grabFocus();
            }
        }

        @Override
        public void insertUpdate(DocumentEvent event) {
            String curText = ExpressionTab.this.field.getText();
            this.edited = curText.length() != ExpressionTab.this.curExprStringLength || !curText.equals(this.getCurrentString());
            boolean enable = this.edited && ExpressionTab.this.getCurrentVariable() != null;
            ExpressionTab.this.clear.setEnabled(curText.length() > 0);
            ExpressionTab.this.revert.setEnabled(enable);
            ExpressionTab.this.enter.setEnabled(enable);
        }

        @Override
        public void removeUpdate(DocumentEvent event) {
            this.insertUpdate(event);
        }

        @Override
        public void changedUpdate(DocumentEvent event) {
            this.insertUpdate(event);
        }

        @Override
        public void expressionChanged(OutputExpressionsEvent event) {
            String output;
            if (event.getType() == 1 && (output = event.getVariable()).equals(ExpressionTab.this.getCurrentVariable())) {
                ExpressionTab.this.prettyView.setExpression(ExpressionTab.this.model.getOutputExpressions().getExpression(output));
                this.currentStringChanged();
            }
        }

        @Override
        public void itemStateChanged(ItemEvent event) {
            String output = ExpressionTab.this.getCurrentVariable();
            ExpressionTab.this.prettyView.setExpression(ExpressionTab.this.model.getOutputExpressions().getExpression(output));
            this.currentStringChanged();
        }

        private String getCurrentString() {
            String output = ExpressionTab.this.getCurrentVariable();
            return output == null ? "" : ExpressionTab.this.model.getOutputExpressions().getExpressionString(output);
        }

        private void currentStringChanged() {
            String output = ExpressionTab.this.getCurrentVariable();
            String exprString = ExpressionTab.this.model.getOutputExpressions().getExpressionString(output);
            ExpressionTab.this.curExprStringLength = exprString.length();
            if (!this.edited) {
                ExpressionTab.this.setError(null);
                ExpressionTab.this.field.setText(this.getCurrentString());
            } else {
                this.insertUpdate(null);
            }
        }
    }

}

