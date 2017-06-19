/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.gui.log.LogThread;
import com.cburch.logisim.gui.log.ModelEvent;
import com.cburch.logisim.gui.log.ModelListener;
import com.cburch.logisim.gui.log.Selection;
import com.cburch.logisim.gui.log.SelectionItem;
import com.cburch.logisim.gui.log.ValueLog;
import com.cburch.logisim.util.EventSourceWeakSupport;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JFrame;

class Model {
    private EventSourceWeakSupport listeners = new EventSourceWeakSupport();
    private Selection selection;
    private HashMap log;
    private boolean fileEnabled = false;
    private File file = null;
    private boolean fileHeader = true;
    private boolean selected = false;
    private LogThread logger = null;

    public Model(CircuitState circuitState) {
        this.selection = new Selection(circuitState, this);
        this.log = new HashMap();
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void addModelListener(ModelListener l) {
        this.listeners.add(l);
    }

    public void removeModelListener(ModelListener l) {
        this.listeners.remove(l);
    }

    public CircuitState getCircuitState() {
        return this.selection.getCircuitState();
    }

    public Selection getSelection() {
        return this.selection;
    }

    public ValueLog getValueLog(SelectionItem item) {
        ValueLog ret = (ValueLog)this.log.get(item);
        if (ret == null && this.selection.indexOf(item) >= 0) {
            ret = new ValueLog();
            this.log.put(item, ret);
        }
        return ret;
    }

    public boolean isFileEnabled() {
        return this.fileEnabled;
    }

    public File getFile() {
        return this.file;
    }

    public boolean getFileHeader() {
        return this.fileHeader;
    }

    public void setFileEnabled(boolean value) {
        if (this.fileEnabled == value) {
            return;
        }
        this.fileEnabled = value;
        this.fireFilePropertyChanged(new ModelEvent());
    }

    public void setFile(File value) {
        if (this.file == null ? value == null : this.file.equals(value)) {
            return;
        }
        this.file = value;
        this.fileEnabled = this.file != null;
        this.fireFilePropertyChanged(new ModelEvent());
    }

    public void setFileHeader(boolean value) {
        if (this.fileHeader == value) {
            return;
        }
        this.fileHeader = value;
        this.fireFilePropertyChanged(new ModelEvent());
    }

    public void propagationCompleted() {
        SelectionItem item;
        int i;
        CircuitState circuitState = this.getCircuitState();
        Value[] vals = new Value[this.selection.size()];
        boolean changed = false;
        for (i = this.selection.size() - 1; i >= 0; --i) {
            item = this.selection.get(i);
            vals[i] = item.fetchValue(circuitState);
            if (changed) continue;
            Value v = this.getValueLog(item).getLast();
            changed = v == null ? vals[i] != null : !v.equals(vals[i]);
        }
        if (changed) {
            for (i = this.selection.size() - 1; i >= 0; --i) {
                item = this.selection.get(i);
                this.getValueLog(item).append(vals[i]);
            }
            this.fireEntryAdded(new ModelEvent(), vals);
        }
    }

    public void setSelected(JFrame frame, boolean value) {
        if (this.selected == value) {
            return;
        }
        this.selected = value;
        if (this.selected) {
            this.logger = new LogThread(this);
            this.logger.start();
        } else {
            if (this.logger != null) {
                this.logger.cancel();
            }
            this.logger = null;
            this.fileEnabled = false;
        }
        this.fireFilePropertyChanged(new ModelEvent());
    }

    void fireSelectionChanged(ModelEvent e) {
        Iterator it = this.log.keySet().iterator();
        while (it.hasNext()) {
            SelectionItem i = (SelectionItem)it.next();
            if (this.selection.indexOf(i) >= 0) continue;
            it.remove();
        }
        it = this.listeners.iterator();
        while (it.hasNext()) {
            ((ModelListener)it.next()).selectionChanged(e);
        }
    }

    private void fireEntryAdded(ModelEvent e, Value[] values) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((ModelListener)it.next()).entryAdded(e, values);
        }
    }

    private void fireFilePropertyChanged(ModelEvent e) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((ModelListener)it.next()).filePropertyChanged(e);
        }
    }
}

