/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.hex;

import com.cburch.hex.Caret;
import com.cburch.hex.HexEditor;
import com.cburch.hex.HexModel;
import com.cburch.logisim.gui.hex.HexFile;
import com.cburch.logisim.gui.hex.Strings;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;

class Clip
implements ClipboardOwner {
    private static final DataFlavor binaryFlavor = new DataFlavor(int[].class, "Binary data");
    private HexEditor editor;

    Clip(HexEditor editor) {
        this.editor = editor;
    }

    public void copy() {
        Caret caret = this.editor.getCaret();
        long p0 = caret.getMark();
        long p1 = caret.getDot();
        if (p0 < 0 || p1 < 0) {
            return;
        }
        if (p0 > p1) {
            long t = p0;
            p0 = p1;
            p1 = t;
        }
        int[] data = new int[(int)(++p1 - p0)];
        HexModel model = this.editor.getModel();
        for (long i = p0; i < p1; ++i) {
            data[(int)(i - p0)] = model.get(i);
        }
        Clipboard clip = this.editor.getToolkit().getSystemClipboard();
        clip.setContents(new Data(data), this);
    }

    public boolean canPaste() {
        Clipboard clip = this.editor.getToolkit().getSystemClipboard();
        Transferable xfer = clip.getContents(this);
        return xfer.isDataFlavorSupported(binaryFlavor);
    }

    public void paste() {
        int[] data;
        Clipboard clip = this.editor.getToolkit().getSystemClipboard();
        Transferable xfer = clip.getContents(this);
        if (xfer.isDataFlavorSupported(binaryFlavor)) {
            try {
                data = (int[])xfer.getTransferData(binaryFlavor);
            }
            catch (UnsupportedFlavorException e) {
                return;
            }
            catch (IOException e) {
                return;
            }
        }
        if (xfer.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String buf;
            try {
                buf = (String)xfer.getTransferData(DataFlavor.stringFlavor);
            }
            catch (UnsupportedFlavorException e) {
                return;
            }
            catch (IOException e) {
                return;
            }
            try {
                data = HexFile.parse(new StringReader(buf));
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(this.editor.getRootPane(), e.getMessage(), Strings.get("hexPasteErrorTitle"), 0);
                return;
            }
        }
        JOptionPane.showMessageDialog(this.editor.getRootPane(), Strings.get("hexPasteSupportedError"), Strings.get("hexPasteErrorTitle"), 0);
        return;
        Caret caret = this.editor.getCaret();
        long p0 = caret.getMark();
        long p1 = caret.getDot();
        if (p0 == p1) {
            HexModel model = this.editor.getModel();
            if (p0 + (long)data.length - 1 <= model.getLastOffset()) {
                model.set(p0, data);
            } else {
                JOptionPane.showMessageDialog(this.editor.getRootPane(), Strings.get("hexPasteEndError"), Strings.get("hexPasteErrorTitle"), 0);
            }
        } else {
            if (p0 < 0 || p1 < 0) {
                return;
            }
            if (p0 > p1) {
                long t = p0;
                p0 = p1;
                p1 = t;
            }
            HexModel model = this.editor.getModel();
            if (++p1 - p0 == (long)data.length) {
                model.set(p0, data);
            } else {
                JOptionPane.showMessageDialog(this.editor.getRootPane(), Strings.get("hexPasteSizeError"), Strings.get("hexPasteErrorTitle"), 0);
            }
        }
    }

    @Override
    public void lostOwnership(Clipboard clip, Transferable transfer) {
    }

    private static class Data
    implements Transferable {
        private int[] data;

        Data(int[] data) {
            this.data = data;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{binaryFlavor, DataFlavor.stringFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor == binaryFlavor || flavor == DataFlavor.stringFlavor;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor == binaryFlavor) {
                return this.data;
            }
            if (flavor == DataFlavor.stringFlavor) {
                int bits = 1;
                for (int i = 0; i < this.data.length; ++i) {
                    for (int k = this.data[i] >> bits; k != 0 && bits < 32; ++bits, k >>= 1) {
                    }
                }
                int chars = (bits + 3) / 4;
                StringBuffer buf = new StringBuffer();
                for (int i2 = 0; i2 < this.data.length; ++i2) {
                    if (i2 > 0) {
                        buf.append(i2 % 8 == 0 ? '\n' : ' ');
                    }
                    String s = Integer.toHexString(this.data[i2]);
                    while (s.length() < chars) {
                        s = "0" + s;
                    }
                    buf.append(s);
                }
                return buf.toString();
            }
            throw new UnsupportedFlavorException(flavor);
        }
    }

}

