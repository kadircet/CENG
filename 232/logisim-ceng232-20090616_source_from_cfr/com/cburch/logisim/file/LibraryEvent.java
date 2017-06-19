/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.file;

import com.cburch.logisim.tools.Library;

public class LibraryEvent {
    public static final int ADD_TOOL = 0;
    public static final int REMOVE_TOOL = 1;
    public static final int MOVE_TOOL = 6;
    public static final int ADD_LIBRARY = 2;
    public static final int REMOVE_LIBRARY = 3;
    public static final int SET_MAIN = 4;
    public static final int SET_NAME = 5;
    public static final int DIRTY_STATE = 6;
    private Library source;
    private int action;
    private Object data;

    LibraryEvent(Library source, int action, Object data) {
        this.source = source;
        this.action = action;
        this.data = data;
    }

    public Library getSource() {
        return this.source;
    }

    public int getAction() {
        return this.action;
    }

    public Object getData() {
        return this.data;
    }
}

