/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.analyze.model.OutputExpressions;
import com.cburch.logisim.analyze.model.TruthTable;
import com.cburch.logisim.analyze.model.VariableList;
import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.proj.Project;
import java.util.List;

public class AnalyzerModel {
    public static final int MAX_INPUTS = 12;
    public static final int MAX_OUTPUTS = 12;
    private VariableList inputs = new VariableList(12);
    private VariableList outputs = new VariableList(12);
    private TruthTable table;
    private OutputExpressions outputExpressions;
    private Project currentProject = null;
    private Circuit currentCircuit = null;

    public AnalyzerModel() {
        this.table = new TruthTable(this);
        this.outputExpressions = new OutputExpressions(this);
    }

    public Project getCurrentProject() {
        return this.currentProject;
    }

    public Circuit getCurrentCircuit() {
        return this.currentCircuit;
    }

    public VariableList getInputs() {
        return this.inputs;
    }

    public VariableList getOutputs() {
        return this.outputs;
    }

    public TruthTable getTruthTable() {
        return this.table;
    }

    public OutputExpressions getOutputExpressions() {
        return this.outputExpressions;
    }

    public void setCurrentProject(Project value) {
        this.currentProject = value;
        this.currentCircuit = value.getCurrentCircuit();
    }

    public void setVariables(List inputs, List outputs) {
        this.inputs.setAll(inputs);
        this.outputs.setAll(outputs);
    }
}

