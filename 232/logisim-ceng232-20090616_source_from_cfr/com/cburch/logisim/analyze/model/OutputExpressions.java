/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.analyze.model.Assignments;
import com.cburch.logisim.analyze.model.Entry;
import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.Expressions;
import com.cburch.logisim.analyze.model.Implicant;
import com.cburch.logisim.analyze.model.OutputExpressionsEvent;
import com.cburch.logisim.analyze.model.OutputExpressionsListener;
import com.cburch.logisim.analyze.model.Parser;
import com.cburch.logisim.analyze.model.TruthTable;
import com.cburch.logisim.analyze.model.TruthTableEvent;
import com.cburch.logisim.analyze.model.TruthTableListener;
import com.cburch.logisim.analyze.model.VariableList;
import com.cburch.logisim.analyze.model.VariableListEvent;
import com.cburch.logisim.analyze.model.VariableListListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class OutputExpressions {
    private MyListener myListener;
    private AnalyzerModel model;
    private HashMap outputData;
    private ArrayList listeners;
    private boolean updatingTable;

    public OutputExpressions(AnalyzerModel model) {
        this.myListener = new MyListener();
        this.outputData = new HashMap();
        this.listeners = new ArrayList();
        this.updatingTable = false;
        this.model = model;
        model.getInputs().addVariableListListener(this.myListener);
        model.getOutputs().addVariableListListener(this.myListener);
        model.getTruthTable().addTruthTableListener(this.myListener);
    }

    public void addOutputExpressionsListener(OutputExpressionsListener l) {
        this.listeners.add(l);
    }

    public void removeOutputExpressionsListener(OutputExpressionsListener l) {
        this.listeners.remove(l);
    }

    private void fireModelChanged(int type) {
        this.fireModelChanged(type, null, null);
    }

    private void fireModelChanged(int type, String variable) {
        this.fireModelChanged(type, variable, null);
    }

    private void fireModelChanged(int type, String variable, Object data) {
        OutputExpressionsEvent event = new OutputExpressionsEvent(this.model, type, variable, data);
        for (OutputExpressionsListener l : this.listeners) {
            l.expressionChanged(event);
        }
    }

    public Expression getExpression(String output) {
        if (output == null) {
            return null;
        }
        return this.getOutputData(output, true).getExpression();
    }

    public String getExpressionString(String output) {
        if (output == null) {
            return "";
        }
        return this.getOutputData(output, true).getExpressionString();
    }

    public boolean isExpressionMinimal(String output) {
        OutputData data = this.getOutputData(output, false);
        return data == null ? true : data.isExpressionMinimal();
    }

    public Expression getMinimalExpression(String output) {
        if (output == null) {
            return Expressions.constant(0);
        }
        return this.getOutputData(output, true).getMinimalExpression();
    }

    public List getMinimalImplicants(String output) {
        if (output == null) {
            return Implicant.MINIMAL_LIST;
        }
        return this.getOutputData(output, true).getMinimalImplicants();
    }

    public void setExpression(String output, Expression expr) {
        this.setExpression(output, expr, null);
    }

    public void setExpression(String output, Expression expr, String exprString) {
        if (output == null) {
            return;
        }
        this.getOutputData(output, true).setExpression(expr, exprString);
    }

    private void invalidate(String output) {
        OutputData data = this.getOutputData(output, false);
        if (data != null) {
            data.invalidate(false);
        }
    }

    private OutputData getOutputData(String output, boolean create) {
        if (output == null) {
            throw new IllegalArgumentException("null output name");
        }
        OutputData ret = (OutputData)this.outputData.get(output);
        if (ret == null && create) {
            if (this.model.getOutputs().indexOf(output) < 0) {
                throw new IllegalArgumentException("unrecognized output " + output);
            }
            ret = new OutputData(output);
            this.outputData.put(output, ret);
        }
        return ret;
    }

    private static Entry[] computeColumn(TruthTable table, Expression expr) {
        int rows = table.getRowCount();
        int cols = table.getInputColumnCount();
        Object[] values = new Entry[rows];
        if (expr == null) {
            Arrays.fill(values, Entry.DONT_CARE);
        } else {
            Assignments assn = new Assignments();
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < cols; ++j) {
                    assn.put(table.getInputHeader(j), TruthTable.isInputSet(i, j, cols));
                }
                values[i] = expr.evaluate(assn) ? Entry.ONE : Entry.ZERO;
            }
        }
        return values;
    }

    private static boolean columnsMatch(Entry[] a, Entry[] b) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; ++i) {
            boolean bothDefined;
            if (a[i] == b[i]) continue;
            boolean bl = bothDefined = !(a[i] != Entry.ZERO && a[i] != Entry.ONE || b[i] != Entry.ZERO && b[i] != Entry.ONE);
            if (!bothDefined) continue;
            return false;
        }
        return true;
    }

    private static boolean isAllUndefined(Entry[] a) {
        for (int i = 0; i < a.length; ++i) {
            if (a[i] != Entry.ZERO && a[i] != Entry.ONE) continue;
            return false;
        }
        return true;
    }

    private class MyListener
    implements VariableListListener,
    TruthTableListener {
        private MyListener() {
        }

        @Override
        public void listChanged(VariableListEvent event) {
            if (event.getSource() == OutputExpressions.this.model.getInputs()) {
                this.inputsChanged(event);
            } else {
                this.outputsChanged(event);
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        private void inputsChanged(VariableListEvent event) {
            int type = event.getType();
            if (type == 0 && !OutputExpressions.this.outputData.isEmpty()) {
                OutputExpressions.this.outputData.clear();
                OutputExpressions.this.fireModelChanged(0);
                return;
            } else if (type == 2) {
                String input = event.getVariable();
                for (String output : OutputExpressions.this.outputData.keySet()) {
                    OutputData data = OutputExpressions.this.getOutputData(output, false);
                    if (data == null) continue;
                    data.removeInput(input);
                }
                return;
            } else if (type == 4) {
                String input = event.getVariable();
                int inputIndex = (Integer)event.getData();
                String newName = event.getSource().get(inputIndex);
                for (String output : OutputExpressions.this.outputData.keySet()) {
                    OutputData data = OutputExpressions.this.getOutputData(output, false);
                    if (data == null) continue;
                    data.replaceInput(input, newName);
                }
                return;
            } else {
                if (type != 3 && type != 1) return;
                for (String output : OutputExpressions.this.outputData.keySet()) {
                    OutputData data = OutputExpressions.this.getOutputData(output, false);
                    if (data == null) continue;
                    data.invalidate(false);
                }
            }
        }

        private void outputsChanged(VariableListEvent event) {
            int type = event.getType();
            if (type == 0 && !OutputExpressions.this.outputData.isEmpty()) {
                OutputExpressions.this.outputData.clear();
                OutputExpressions.this.fireModelChanged(0);
            } else if (type == 2) {
                OutputExpressions.this.outputData.remove(event.getVariable());
            } else if (type == 4) {
                String oldName = event.getVariable();
                if (OutputExpressions.this.outputData.containsKey(oldName)) {
                    String newName;
                    OutputData toMove = (OutputData)OutputExpressions.this.outputData.remove(oldName);
                    int inputIndex = (Integer)event.getData();
                    toMove.output = newName = event.getSource().get(inputIndex);
                    OutputExpressions.this.outputData.put(newName, toMove);
                }
            }
        }

        @Override
        public void cellsChanged(TruthTableEvent event) {
            String output = OutputExpressions.this.model.getOutputs().get(event.getColumn());
            OutputExpressions.this.invalidate(output);
        }

        @Override
        public void structureChanged(TruthTableEvent event) {
        }
    }

    private class OutputData {
        String output;
        Expression expr;
        String exprString;
        List minimalImplicants;
        Expression minimalExpr;

        OutputData(String output) {
            this.expr = null;
            this.exprString = null;
            this.minimalImplicants = null;
            this.minimalExpr = null;
            this.output = output;
            this.invalidate(true);
        }

        boolean isExpressionMinimal() {
            return this.expr == this.minimalExpr;
        }

        Expression getExpression() {
            return this.expr;
        }

        String getExpressionString() {
            if (this.exprString == null) {
                if (this.expr == null) {
                    this.invalidate(false);
                }
                this.exprString = this.expr == null ? "" : this.expr.toString();
            }
            return this.exprString;
        }

        Expression getMinimalExpression() {
            if (this.minimalExpr == null) {
                this.invalidate(false);
            }
            return this.minimalExpr;
        }

        List getMinimalImplicants() {
            return this.minimalImplicants;
        }

        void setExpression(Expression newExpr, String newExprString) {
            boolean changed = newExpr == null ? this.expr != null : newExpr != this.expr && !newExpr.equals(this.expr);
            this.expr = newExpr;
            this.exprString = newExprString;
            if (changed) {
                if (this.expr != this.minimalExpr) {
                    Entry[] values = OutputExpressions.computeColumn(OutputExpressions.this.model.getTruthTable(), this.expr);
                    int outputColumn = OutputExpressions.this.model.getOutputs().indexOf(this.output);
                    OutputExpressions.this.updatingTable = true;
                    try {
                        OutputExpressions.this.model.getTruthTable().setOutputColumn(outputColumn, values);
                    }
                    finally {
                        OutputExpressions.this.updatingTable = false;
                    }
                }
                OutputExpressions.this.fireModelChanged(1, this.output, this.getExpression());
            }
        }

        private void removeInput(String input) {
            Expression oldMinExpr = this.minimalExpr;
            this.minimalImplicants = null;
            this.minimalExpr = null;
            if (this.exprString != null) {
                this.exprString = null;
            }
            if (this.expr != null) {
                Expression newExpr;
                Expression oldExpr = this.expr;
                if (oldExpr == oldMinExpr) {
                    this.expr = newExpr = this.getMinimalExpression();
                } else {
                    newExpr = this.expr.removeVariable(input);
                }
                if (newExpr == null || !newExpr.equals(oldExpr)) {
                    this.expr = newExpr;
                    OutputExpressions.this.fireModelChanged(1, this.output, this.expr);
                }
            }
            OutputExpressions.this.fireModelChanged(2, this.output, this.minimalExpr);
        }

        private void replaceInput(String input, String newName) {
            this.minimalExpr = null;
            if (this.exprString != null) {
                this.exprString = Parser.replaceVariable(this.exprString, input, newName);
            }
            if (this.expr != null) {
                Expression newExpr = this.expr.replaceVariable(input, newName);
                if (!newExpr.equals(this.expr)) {
                    this.expr = newExpr;
                    OutputExpressions.this.fireModelChanged(1, this.output);
                }
            } else {
                OutputExpressions.this.fireModelChanged(1, this.output);
            }
            OutputExpressions.this.fireModelChanged(2, this.output);
        }

        private void invalidate(boolean initializing) {
            boolean minChanged;
            Expression oldMinExpr = this.minimalExpr;
            this.minimalImplicants = Implicant.computeMinimal(OutputExpressions.this.model, this.output);
            this.minimalExpr = Implicant.toExpression(OutputExpressions.this.model, this.minimalImplicants);
            boolean bl = oldMinExpr == null ? this.minimalExpr != null : (minChanged = !oldMinExpr.equals(this.minimalExpr));
            if (!OutputExpressions.this.updatingTable) {
                TruthTable table = OutputExpressions.this.model.getTruthTable();
                Entry[] outputColumn = OutputExpressions.computeColumn(OutputExpressions.this.model.getTruthTable(), this.expr);
                int outputIndex = OutputExpressions.this.model.getOutputs().indexOf(this.output);
                Entry[] currentColumn = table.getOutputColumn(outputIndex);
                if (!OutputExpressions.columnsMatch(currentColumn, outputColumn) || OutputExpressions.isAllUndefined(outputColumn)) {
                    boolean exprChanged = this.expr != oldMinExpr || minChanged;
                    this.expr = this.minimalExpr;
                    if (exprChanged) {
                        this.exprString = null;
                        if (!initializing) {
                            OutputExpressions.this.fireModelChanged(1, this.output);
                        }
                    }
                }
            }
            if (!initializing && minChanged) {
                OutputExpressions.this.fireModelChanged(2, this.output);
            }
        }
    }

}

