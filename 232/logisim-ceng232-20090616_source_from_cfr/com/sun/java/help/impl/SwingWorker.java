/*
 * Decompiled with CFR 0_114.
 */
package com.sun.java.help.impl;

import javax.swing.SwingUtilities;

public abstract class SwingWorker {
    private Object value;
    private ThreadVar threadVar;

    protected synchronized Object getValue() {
        return this.value;
    }

    private synchronized void setValue(Object object) {
        this.value = object;
    }

    public abstract Object construct();

    public void finished() {
    }

    public void interrupt() {
        Thread thread = this.threadVar.get();
        if (thread != null) {
            thread.interrupt();
        }
        this.threadVar.clear();
    }

    /*
     * Exception decompiling
     */
    public Object get() {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[UNCONDITIONALDOLOOP]], but top level block is 1[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:394)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:446)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:2859)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:805)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:220)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:165)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:91)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:354)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:751)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:683)
        // org.benf.cfr.reader.Main.doJar(Main.java:129)
        // org.benf.cfr.reader.Main.main(Main.java:181)
        throw new IllegalStateException("Decompilation failed");
    }

    public SwingWorker() {
        final Runnable runnable = new Runnable(){

            public void run() {
                SwingWorker.this.finished();
            }
        };
        Runnable runnable2 = new Runnable(){

            public void run() {
                try {
                    SwingWorker.this.setValue(SwingWorker.this.construct());
                    Object var2_1 = null;
                    SwingWorker.this.threadVar.clear();
                }
                catch (Throwable var1_3) {
                    Object var2_2 = null;
                    SwingWorker.this.threadVar.clear();
                    throw var1_3;
                }
                SwingUtilities.invokeLater(runnable);
            }
        };
        Thread thread = new Thread(runnable2);
        this.threadVar = new ThreadVar(thread);
    }

    public void start() {
        this.start(5);
    }

    public void start(int n) {
        Thread thread = this.threadVar.get();
        if (thread != null) {
            if (n < 10 && n > 1) {
                thread.setPriority(n);
            }
            thread.start();
        }
    }

    private static class ThreadVar {
        private Thread thread;

        ThreadVar(Thread thread) {
            this.thread = thread;
        }

        synchronized Thread get() {
            return this.thread;
        }

        synchronized void clear() {
            this.thread = null;
        }
    }

}

