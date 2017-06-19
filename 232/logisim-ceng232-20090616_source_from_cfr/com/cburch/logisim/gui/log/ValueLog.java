/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.data.Value;

class ValueLog {
    private static final int LOG_SIZE = 400;
    private Value[] log = new Value[400];
    private short curSize = 0;
    private short firstIndex = 0;

    public int size() {
        return this.curSize;
    }

    public Value get(int index) {
        int i = this.firstIndex + index;
        if (i >= 400) {
            i -= 400;
        }
        return this.log[i];
    }

    public Value getLast() {
        return this.curSize < 400 ? (this.curSize == 0 ? null : this.log[this.curSize - 1]) : (this.firstIndex == 0 ? this.log[this.curSize - 1] : this.log[this.firstIndex - 1]);
    }

    public void append(Value val) {
        if (this.curSize < 400) {
            this.log[this.curSize] = val;
            this.curSize = (short)(this.curSize + 1);
        } else {
            this.log[this.firstIndex] = val;
            this.firstIndex = (short)(this.firstIndex + 1);
            if (this.firstIndex >= 400) {
                this.firstIndex = 0;
            }
        }
    }
}

