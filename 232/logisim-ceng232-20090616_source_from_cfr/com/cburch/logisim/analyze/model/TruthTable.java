/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.analyze.model.Entry;
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

public class TruthTable {
    private static final Entry DEFAULT_ENTRY = Entry.DONT_CARE;
    private MyListener myListener;
    private List listeners;
    private AnalyzerModel model;
    private HashMap outputColumns;

    public TruthTable(AnalyzerModel model) {
        this.myListener = new MyListener();
        this.listeners = new ArrayList();
        this.outputColumns = new HashMap();
        this.model = model;
        model.getInputs().addVariableListListener(this.myListener);
        model.getOutputs().addVariableListListener(this.myListener);
    }

    public void addTruthTableListener(TruthTableListener l) {
        this.listeners.add(l);
    }

    public void removeTruthTableListener(TruthTableListener l) {
        this.listeners.remove(l);
    }

    private void fireCellsChanged(int column) {
        TruthTableEvent event = new TruthTableEvent(this, column);
        for (TruthTableListener l : this.listeners) {
            l.cellsChanged(event);
        }
    }

    private void fireStructureChanged(VariableListEvent cause) {
        TruthTableEvent event = new TruthTableEvent(this, cause);
        for (TruthTableListener l : this.listeners) {
            l.structureChanged(event);
        }
    }

    public int getRowCount() {
        int sz = this.model.getInputs().size();
        return 1 << sz;
    }

    public int getInputColumnCount() {
        return this.model.getInputs().size();
    }

    public int getOutputColumnCount() {
        return this.model.getOutputs().size();
    }

    public String getInputHeader(int column) {
        return this.model.getInputs().get(column);
    }

    public String getOutputHeader(int column) {
        return this.model.getOutputs().get(column);
    }

    public int getInputIndex(String input) {
        return this.model.getInputs().indexOf(input);
    }

    public int getOutputIndex(String output) {
        return this.model.getOutputs().indexOf(output);
    }

    public Entry getInputEntry(int row, int column) {
        int rows = this.getRowCount();
        int inputs = this.model.getInputs().size();
        if (row < 0 || row >= rows) {
            throw new IllegalArgumentException("row index: " + row + " size: " + rows);
        }
        if (column < 0 || column >= inputs) {
            throw new IllegalArgumentException("column index: " + column + " size: " + inputs);
        }
        return TruthTable.isInputSet(row, column, inputs) ? Entry.ONE : Entry.ZERO;
    }

    public Entry getOutputEntry(int row, int column) {
        int outputs = this.model.getOutputs().size();
        if (row < 0 || row >= this.getRowCount() || column < 0 || column >= outputs) {
            return Entry.DONT_CARE;
        }
        String outputName = this.model.getOutputs().get(column);
        Entry[] columnData = (Entry[])this.outputColumns.get(outputName);
        if (columnData == null) {
            return DEFAULT_ENTRY;
        }
        return columnData[row];
    }

    public void setOutputEntry(int row, int column, Entry value) {
        int rows = this.getRowCount();
        int outputs = this.model.getOutputs().size();
        if (row < 0 || row >= rows) {
            throw new IllegalArgumentException("row index: " + row + " size: " + rows);
        }
        if (column < 0 || column >= outputs) {
            throw new IllegalArgumentException("column index: " + column + " size: " + outputs);
        }
        String outputName = this.model.getOutputs().get(column);
        Object[] columnData = (Entry[])this.outputColumns.get(outputName);
        if (columnData == null) {
            if (value == DEFAULT_ENTRY) {
                return;
            }
            columnData = new Entry[this.getRowCount()];
            this.outputColumns.put(outputName, columnData);
            Arrays.fill(columnData, DEFAULT_ENTRY);
            columnData[row] = value;
        } else {
            if (columnData[row] == value) {
                return;
            }
            columnData[row] = value;
        }
        this.fireCellsChanged(column);
    }

    public Entry[] getOutputColumn(int column) {
        int outputs = this.model.getOutputs().size();
        if (column < 0 || column >= outputs) {
            throw new IllegalArgumentException("index: " + column + " size: " + outputs);
        }
        String outputName = this.model.getOutputs().get(column);
        Object[] columnData = (Entry[])this.outputColumns.get(outputName);
        if (columnData == null) {
            columnData = new Entry[this.getRowCount()];
            Arrays.fill(columnData, DEFAULT_ENTRY);
            this.outputColumns.put(outputName, columnData);
        }
        return columnData;
    }

    public void setOutputColumn(int column, Entry[] values) {
        if (values != null && values.length != this.getRowCount()) {
            throw new IllegalArgumentException("argument to setOutputColumn is wrong length");
        }
        int outputs = this.model.getOutputs().size();
        if (column < 0 || column >= outputs) {
            throw new IllegalArgumentException("index: " + column + " size: " + outputs);
        }
        String outputName = this.model.getOutputs().get(column);
        Entry[] oldValues = (Entry[])this.outputColumns.get(outputName);
        if (oldValues == values) {
            return;
        }
        if (values == null) {
            this.outputColumns.remove(outputName);
        } else {
            this.outputColumns.put(outputName, values);
        }
        this.fireCellsChanged(column);
    }

    public static boolean isInputSet(int row, int column, int inputs) {
        return (row >> inputs - 1 - column & 1) == 1;
    }

    private class MyListener
    implements VariableListListener {
        private MyListener() {
        }

        @Override
        public void listChanged(VariableListEvent event) {
            if (event.getSource() == TruthTable.this.model.getInputs()) {
                this.inputsChanged(event);
            } else {
                this.outputsChanged(event);
            }
            TruthTable.this.fireStructureChanged(event);
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        private void inputsChanged(VariableListEvent event) {
            int action = event.getType();
            if (action == 1) {
                for (String output : TruthTable.this.outputColumns.keySet()) {
                    Entry[] column = (Entry[])TruthTable.this.outputColumns.get(output);
                    Entry[] newColumn = new Entry[2 * column.length];
                    for (int i = 0; i < column.length; ++i) {
                        newColumn[2 * i] = column[i];
                        newColumn[2 * i + 1] = column[i];
                    }
                    TruthTable.this.outputColumns.put(output, newColumn);
                }
                return;
            } else if (action == 2) {
                int index = (Integer)event.getData();
                for (String output : TruthTable.this.outputColumns.keySet()) {
                    Entry[] column = (Entry[])TruthTable.this.outputColumns.get(output);
                    Entry[] newColumn = this.removeInput(column, index);
                    TruthTable.this.outputColumns.put(output, newColumn);
                }
                return;
            } else {
                if (action != 3) return;
                int delta = (Integer)event.getData();
                int newIndex = TruthTable.this.model.getInputs().indexOf(event.getVariable());
                for (String output : TruthTable.this.outputColumns.keySet()) {
                    Entry[] column = (Entry[])TruthTable.this.outputColumns.get(output);
                    Entry[] newColumn = this.moveInput(column, newIndex - delta, newIndex);
                    TruthTable.this.outputColumns.put(output, newColumn);
                }
            }
        }

        private void outputsChanged(VariableListEvent event) {
            Object column;
            int action = event.getType();
            if (action == 0) {
                TruthTable.this.outputColumns.clear();
            } else if (action == 2) {
                TruthTable.this.outputColumns.remove(event.getVariable());
            } else if (action == 4 && (column = TruthTable.this.outputColumns.remove(event.getVariable())) != null) {
                int index = (Integer)event.getData();
                String newVariable = TruthTable.this.model.getOutputs().get(index);
                if (column != null) {
                    TruthTable.this.outputColumns.put(newVariable, column);
                }
            }
        }

        private Entry[] removeInput(Entry[] old, int index) {
            int oldInputCount = TruthTable.this.model.getInputs().size() + 1;
            Entry[] ret = new Entry[old.length / 2];
            int j = 0;
            int mask = 1 << oldInputCount - 1 - index;
            for (int i = 0; i < old.length; ++i) {
                if ((i & mask) != 0) continue;
                Entry e0 = old[i];
                Entry e1 = old[i | mask];
                ret[j] = e0 == e1 ? e0 : Entry.DONT_CARE;
                ++j;
            }
            return ret;
        }

        private Entry[] moveInput(Entry[] old, int oldIndex, int newIndex) {
            int inputs = TruthTable.this.model.getInputs().size();
            oldIndex = inputs - 1 - oldIndex;
            newIndex = inputs - 1 - newIndex;
            Entry[] ret = new Entry[old.length];
            int sameMask = old.length - 1 ^ (1 << 1 + Math.max(oldIndex, newIndex)) - 1 ^ (1 << Math.min(oldIndex, newIndex)) - 1;
            int moveMask = 1 << oldIndex;
            int moveDist = Math.abs(newIndex - oldIndex);
            boolean moveLeft = newIndex > oldIndex;
            int blockMask = old.length - 1 ^ sameMask ^ moveMask;
            for (int i = 0; i < old.length; ++i) {
                int j = moveLeft ? i & sameMask | (i & moveMask) << moveDist | (i & blockMask) >> 1 : i & sameMask | (i & moveMask) >> moveDist | (i & blockMask) << 1;
                ret[j] = old[i];
            }
            return ret;
        }
    }

}

