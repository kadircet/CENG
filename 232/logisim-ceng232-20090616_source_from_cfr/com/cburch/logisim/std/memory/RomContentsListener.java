/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.memory;

import com.cburch.hex.HexModel;
import com.cburch.hex.HexModelListener;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.std.memory.MemContents;
import com.cburch.logisim.std.memory.Strings;

class RomContentsListener
implements HexModelListener {
    Project proj;
    boolean enabled = true;

    RomContentsListener(Project proj) {
        this.proj = proj;
    }

    void setEnabled(boolean value) {
        this.enabled = value;
    }

    @Override
    public void metainfoChanged(HexModel source) {
    }

    @Override
    public void bytesChanged(HexModel source, long start, long numBytes, int[] oldValues) {
        if (this.enabled && this.proj != null && oldValues != null) {
            int[] newValues = new int[oldValues.length];
            for (int i = 0; i < newValues.length; ++i) {
                newValues[i] = source.get(start + (long)i);
            }
            this.proj.doAction(new Change(this, (MemContents)source, start, oldValues, newValues));
        }
    }

    private static class Change
    extends Action {
        private RomContentsListener source;
        private MemContents contents;
        private long start;
        private int[] oldValues;
        private int[] newValues;
        private boolean completed = true;

        Change(RomContentsListener source, MemContents contents, long start, int[] oldValues, int[] newValues) {
            this.source = source;
            this.contents = contents;
            this.start = start;
            this.oldValues = oldValues;
            this.newValues = newValues;
        }

        @Override
        public String getName() {
            return Strings.get("romChangeAction");
        }

        @Override
        public void doIt(Project proj) {
            if (!this.completed) {
                this.completed = true;
                try {
                    this.source.setEnabled(false);
                    this.contents.set(this.start, this.newValues);
                }
                finally {
                    this.source.setEnabled(true);
                }
            }
        }

        @Override
        public void undo(Project proj) {
            if (this.completed) {
                this.completed = false;
                try {
                    this.source.setEnabled(false);
                    this.contents.set(this.start, this.oldValues);
                }
                finally {
                    this.source.setEnabled(true);
                }
            }
        }

        @Override
        public boolean shouldAppendTo(Action other) {
            if (other instanceof Change) {
                Change o = (Change)other;
                long oEnd = o.start + (long)o.newValues.length;
                long end = this.start + (long)this.newValues.length;
                if (oEnd >= this.start && end >= o.start) {
                    return true;
                }
            }
            return super.shouldAppendTo(other);
        }

        @Override
        public Action append(Action other) {
            if (other instanceof Change) {
                Change o = (Change)other;
                long oEnd = o.start + (long)o.newValues.length;
                long end = this.start + (long)this.newValues.length;
                if (oEnd >= this.start && end >= o.start) {
                    long nStart = Math.min(this.start, o.start);
                    long nEnd = Math.max(end, oEnd);
                    int[] nOld = new int[(int)(nEnd - nStart)];
                    int[] nNew = new int[(int)(nEnd - nStart)];
                    System.arraycopy(o.oldValues, 0, nOld, (int)(o.start - nStart), o.oldValues.length);
                    System.arraycopy(this.oldValues, 0, nOld, (int)(this.start - nStart), this.oldValues.length);
                    System.arraycopy(this.newValues, 0, nNew, (int)(this.start - nStart), this.newValues.length);
                    System.arraycopy(o.newValues, 0, nNew, (int)(o.start - nStart), o.newValues.length);
                    return new Change(this.source, this.contents, nStart, nOld, nNew);
                }
            }
            return super.append(other);
        }
    }

}

