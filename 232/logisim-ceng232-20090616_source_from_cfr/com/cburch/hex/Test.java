/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.hex;

import com.cburch.hex.HexEditor;
import com.cburch.hex.HexModel;
import com.cburch.hex.HexModelListener;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class Test {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Model model = new Model();
        HexEditor editor = new HexEditor(model);
        frame.setDefaultCloseOperation(3);
        frame.getContentPane().add(new JScrollPane(editor));
        frame.pack();
        frame.setVisible(true);
    }

    private static class Model
    implements HexModel {
        private ArrayList listeners = new ArrayList();
        private int[] data = new int[924];

        private Model() {
        }

        @Override
        public void addHexModelListener(HexModelListener l) {
            this.listeners.add(l);
        }

        @Override
        public void removeHexModelListener(HexModelListener l) {
            this.listeners.remove(l);
        }

        @Override
        public long getFirstOffset() {
            return 11111;
        }

        @Override
        public long getLastOffset() {
            return this.data.length + 11110;
        }

        @Override
        public int getValueWidth() {
            return 9;
        }

        @Override
        public int get(long address) {
            return this.data[(int)(address - 11111)];
        }

        @Override
        public void set(long address, int value) {
            int[] oldValues = new int[]{this.data[(int)(address - 11111)]};
            this.data[(int)(address - 11111)] = value & 511;
            for (int i = this.listeners.size() - 1; i >= 0; --i) {
                HexModelListener l = (HexModelListener)this.listeners.get(i);
                l.bytesChanged(this, address, 1, oldValues);
            }
        }

        @Override
        public void set(long start, int[] values) {
            int[] oldValues = new int[values.length];
            System.arraycopy(this.data, (int)(start - 11111), oldValues, 0, values.length);
            System.arraycopy(values, 0, this.data, (int)(start - 11111), values.length);
            for (int i = this.listeners.size() - 1; i >= 0; --i) {
                HexModelListener l = (HexModelListener)this.listeners.get(i);
                l.bytesChanged(this, start, values.length, oldValues);
            }
        }

        @Override
        public void fill(long start, long len, int value) {
            int[] oldValues = new int[(int)len];
            System.arraycopy(this.data, (int)(start - 11111), oldValues, 0, (int)len);
            Arrays.fill(this.data, (int)(start - 11111), (int)len, value);
            for (int i = this.listeners.size() - 1; i >= 0; --i) {
                HexModelListener l = (HexModelListener)this.listeners.get(i);
                l.bytesChanged(this, start, len, oldValues);
            }
        }
    }

}

