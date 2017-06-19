/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.proj;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.Tool;

public class ProjectEvent {
    public static final int ACTION_SET_FILE = 0;
    public static final int ACTION_SET_CURRENT = 1;
    public static final int ACTION_SET_TOOL = 2;
    public static final int ACTION_COMPLETED = 3;
    public static final int ACTION_SELECTION = 4;
    public static final int ACTION_SET_STATE = 5;
    private int action;
    private Project proj;
    private Object old_data;
    private Object data;

    ProjectEvent(int action, Project proj, Object old, Object data) {
        this.action = action;
        this.proj = proj;
        this.old_data = old;
        this.data = data;
    }

    ProjectEvent(int action, Project proj, Object data) {
        this.action = action;
        this.proj = proj;
        this.data = data;
    }

    ProjectEvent(int action, Project proj) {
        this.action = action;
        this.proj = proj;
        this.data = null;
    }

    public int getAction() {
        return this.action;
    }

    public Project getProject() {
        return this.proj;
    }

    public Object getOldData() {
        return this.old_data;
    }

    public Object getData() {
        return this.data;
    }

    public LogisimFile getLogisimFile() {
        return this.proj.getLogisimFile();
    }

    public Circuit getCircuit() {
        return this.proj.getCurrentCircuit();
    }

    public Tool getTool() {
        return this.proj.getTool();
    }
}

