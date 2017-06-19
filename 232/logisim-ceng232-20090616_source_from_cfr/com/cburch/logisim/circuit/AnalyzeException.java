/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Strings;
import com.cburch.logisim.util.StringUtil;

public class AnalyzeException
extends Exception {
    public AnalyzeException() {
    }

    public AnalyzeException(String message) {
        super(message);
    }

    public static class CannotHandle
    extends AnalyzeException {
        public CannotHandle(String reason) {
            super(StringUtil.format(Strings.get("analyzeCannotHandleError"), reason));
        }
    }

    public static class Conflict
    extends AnalyzeException {
        public Conflict() {
            super(Strings.get("analyzeConflictError"));
        }
    }

    public static class Circular
    extends AnalyzeException {
        public Circular() {
            super(Strings.get("analyzeCircularError"));
        }
    }

}

