/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.analyze.model.AnalyzerModel;

public class OutputExpressionsEvent {
    public static final int ALL_VARIABLES_REPLACED = 0;
    public static final int OUTPUT_EXPRESSION = 1;
    public static final int OUTPUT_MINIMAL = 2;
    private AnalyzerModel model;
    private int type;
    private String variable;
    private Object data;

    public OutputExpressionsEvent(AnalyzerModel model, int type, String variable, Object data) {
        this.model = model;
        this.type = type;
        this.variable = variable;
        this.data = data;
    }

    public AnalyzerModel getModel() {
        return this.model;
    }

    public int getType() {
        return this.type;
    }

    public String getVariable() {
        return this.variable;
    }

    public Object getData() {
        return this.data;
    }
}

