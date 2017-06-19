/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.data.Value;
import com.cburch.logisim.gui.log.Model;
import com.cburch.logisim.gui.log.ModelEvent;
import com.cburch.logisim.gui.log.ModelListener;
import com.cburch.logisim.gui.log.Selection;
import com.cburch.logisim.gui.log.SelectionItem;
import com.cburch.logisim.gui.log.ValueLog;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

class LogThread
extends Thread
implements ModelListener {
    private static final int FLUSH_FREQUENCY = 500;
    private static final int IDLE_UNTIL_CLOSE = 10000;
    private Model model;
    private boolean canceled = false;
    private Object lock = new Object();
    private PrintWriter writer = null;
    private boolean headerDirty = true;
    private long lastWrite = 0;

    public LogThread(Model model) {
        this.model = model;
        model.addModelListener(this);
    }

    @Override
    public void run() {
        while (!this.canceled) {
            Object object = this.lock;
            synchronized (object) {
                if (this.writer != null) {
                    if (System.currentTimeMillis() - this.lastWrite > 10000) {
                        this.writer.close();
                        this.writer = null;
                    } else {
                        this.writer.flush();
                    }
                }
            }
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {}
        }
        Object e = this.lock;
        synchronized (e) {
            if (this.writer != null) {
                this.writer.close();
                this.writer = null;
            }
        }
    }

    public void cancel() {
        Object object = this.lock;
        synchronized (object) {
            this.canceled = true;
            if (this.writer != null) {
                this.writer.close();
                this.writer = null;
            }
        }
    }

    @Override
    public void selectionChanged(ModelEvent event) {
        this.headerDirty = true;
    }

    @Override
    public void entryAdded(ModelEvent event, Value[] values) {
        Object object = this.lock;
        synchronized (object) {
            if (this.isFileEnabled()) {
                this.addEntry(values);
            }
        }
    }

    @Override
    public void filePropertyChanged(ModelEvent event) {
        Object object = this.lock;
        synchronized (object) {
            if (this.isFileEnabled()) {
                if (this.writer == null) {
                    Selection sel = this.model.getSelection();
                    Value[] values = new Value[sel.size()];
                    boolean found = false;
                    for (int i = 0; i < values.length; ++i) {
                        values[i] = this.model.getValueLog(sel.get(i)).getLast();
                        if (values[i] == null) continue;
                        found = true;
                    }
                    if (found) {
                        this.addEntry(values);
                    }
                }
            } else if (this.writer != null) {
                this.writer.close();
                this.writer = null;
            }
        }
    }

    private boolean isFileEnabled() {
        return !this.canceled && this.model.isSelected() && this.model.isFileEnabled() && this.model.getFile() != null;
    }

    private void addEntry(Value[] values) {
        StringBuffer buf;
        int i;
        if (this.writer == null) {
            try {
                this.writer = new PrintWriter(new FileWriter(this.model.getFile(), true));
            }
            catch (IOException e) {
                this.model.setFile(null);
                return;
            }
        }
        Selection sel = this.model.getSelection();
        if (this.headerDirty) {
            if (this.model.getFileHeader()) {
                buf = new StringBuffer();
                for (i = 0; i < sel.size(); ++i) {
                    if (i > 0) {
                        buf.append("\t");
                    }
                    buf.append(sel.get(i).toString());
                }
                this.writer.println(buf.toString());
            }
            this.headerDirty = false;
        }
        buf = new StringBuffer();
        for (i = 0; i < values.length; ++i) {
            if (i > 0) {
                buf.append("\t");
            }
            if (values[i] == null) continue;
            int radix = sel.get(i).getRadix();
            buf.append(values[i].toDisplayString(radix));
        }
        this.writer.println(buf.toString());
        this.lastWrite = System.currentTimeMillis();
    }
}

