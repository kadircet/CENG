/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.gui.Strings;
import com.cburch.logisim.analyze.gui.TableTab;
import com.cburch.logisim.analyze.gui.TableTabCaret;
import com.cburch.logisim.analyze.model.Entry;
import com.cburch.logisim.analyze.model.TruthTable;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;

class TableTabClip
implements ClipboardOwner {
    private static final DataFlavor binaryFlavor = new DataFlavor(Data.class, "Binary data");
    private TableTab table;

    TableTabClip(TableTab table) {
        this.table = table;
    }

    public void copy() {
        int t;
        TableTabCaret caret = this.table.getCaret();
        int c0 = caret.getCursorCol();
        int r0 = caret.getCursorRow();
        int c1 = caret.getMarkCol();
        int r1 = caret.getMarkRow();
        if (c1 < c0) {
            t = c0;
            c0 = c1;
            c1 = t;
        }
        if (r1 < r0) {
            t = r0;
            r0 = r1;
            r1 = t;
        }
        TruthTable t2 = this.table.getTruthTable();
        int inputs = t2.getInputColumnCount();
        String[] header = new String[c1 - c0 + 1];
        for (int c = c0; c <= c1; ++c) {
            header[c - c0] = c < inputs ? t2.getInputHeader(c) : t2.getOutputHeader(c - inputs);
        }
        String[][] contents = new String[r1 - r0 + 1][c1 - c0 + 1];
        for (int r = r0; r <= r1; ++r) {
            for (int c2 = c0; c2 <= c1; ++c2) {
                contents[r - r0][c2 - c0] = c2 < inputs ? t2.getInputEntry(r, c2).getDescription() : t2.getOutputEntry(r, c2 - inputs).getDescription();
            }
        }
        Clipboard clip = this.table.getToolkit().getSystemClipboard();
        clip.setContents(new Data(header, contents), this);
    }

    public boolean canPaste() {
        Clipboard clip = this.table.getToolkit().getSystemClipboard();
        Transferable xfer = clip.getContents(this);
        return xfer.isDataFlavorSupported(binaryFlavor);
    }

    public void paste() {
        Transferable xfer;
        Entry[][] entries;
        Clipboard clip = this.table.getToolkit().getSystemClipboard();
        try {
            xfer = clip.getContents(this);
        }
        catch (Throwable t) {
            JOptionPane.showMessageDialog(this.table.getRootPane(), Strings.get("clipPasteSupportedError"), Strings.get("clipPasteErrorTitle"), 0);
            return;
        }
        if (xfer.isDataFlavorSupported(binaryFlavor)) {
            try {
                Data data = (Data)xfer.getTransferData(binaryFlavor);
                entries = new Entry[data.contents.length][];
                for (int i = 0; i < entries.length; ++i) {
                    Entry[] row = new Entry[data.contents[i].length];
                    for (int j = 0; j < row.length; ++j) {
                        row[j] = Entry.parse(data.contents[i][j]);
                    }
                    entries[i] = row;
                }
            }
            catch (UnsupportedFlavorException e) {
                return;
            }
            catch (IOException e) {
                return;
            }
        } else if (xfer.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String buf = (String)xfer.getTransferData(DataFlavor.stringFlavor);
                StringTokenizer lines = new StringTokenizer(buf, "\r\n");
                if (!lines.hasMoreTokens()) {
                    return;
                }
                String first = lines.nextToken();
                StringTokenizer toks = new StringTokenizer(first, "\t,");
                String[] headers = new String[toks.countTokens()];
                Entry[] firstEntries = new Entry[headers.length];
                boolean allParsed = true;
                int i = 0;
                while (toks.hasMoreTokens()) {
                    headers[i] = toks.nextToken();
                    firstEntries[i] = Entry.parse(headers[i]);
                    allParsed = allParsed && firstEntries[i] != null;
                    ++i;
                }
                int rows = lines.countTokens();
                if (allParsed) {
                    ++rows;
                }
                entries = new Entry[rows][];
                int cur = 0;
                if (allParsed) {
                    entries[0] = firstEntries;
                    ++cur;
                }
                while (lines.hasMoreTokens()) {
                    toks = new StringTokenizer(lines.nextToken(), "\t");
                    Entry[] ents = new Entry[toks.countTokens()];
                    int i2 = 0;
                    while (toks.hasMoreTokens()) {
                        ents[i2] = Entry.parse(toks.nextToken());
                        ++i2;
                    }
                    entries[cur] = ents;
                    ++cur;
                }
            }
            catch (UnsupportedFlavorException e) {
                return;
            }
            catch (IOException e) {
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this.table.getRootPane(), Strings.get("clipPasteSupportedError"), Strings.get("clipPasteErrorTitle"), 0);
            return;
        }
        TableTabCaret caret = this.table.getCaret();
        int c0 = caret.getCursorCol();
        int c1 = caret.getMarkCol();
        int r0 = caret.getCursorRow();
        int r1 = caret.getMarkRow();
        if (r0 < 0 || r1 < 0 || c0 < 0 || c1 < 0) {
            return;
        }
        TruthTable model = this.table.getTruthTable();
        int rows = model.getRowCount();
        int inputs = model.getInputColumnCount();
        int outputs = model.getOutputColumnCount();
        if (c0 == c1 && r0 == r1) {
            if (r0 + entries.length > rows || c0 + entries[0].length > inputs + outputs) {
                JOptionPane.showMessageDialog(this.table.getRootPane(), Strings.get("clipPasteEndError"), Strings.get("clipPasteErrorTitle"), 0);
                return;
            }
        } else {
            if (r0 > r1) {
                int t = r0;
                r0 = r1;
                r1 = t;
            }
            if (c0 > c1) {
                int t = c0;
                c0 = c1;
                c1 = t;
            }
            if (r1 - r0 + 1 != entries.length || c1 - c0 + 1 != entries[0].length) {
                JOptionPane.showMessageDialog(this.table.getRootPane(), Strings.get("clipPasteSizeError"), Strings.get("clipPasteErrorTitle"), 0);
                return;
            }
        }
        for (int r = 0; r < entries.length; ++r) {
            for (int c = 0; c < entries[0].length; ++c) {
                if (c0 + c < inputs) continue;
                model.setOutputEntry(r0 + r, c0 + c - inputs, entries[r][c]);
            }
        }
    }

    @Override
    public void lostOwnership(Clipboard clip, Transferable transfer) {
    }

    private static class Data
    implements Transferable,
    Serializable {
        private String[] headers;
        private String[][] contents;

        Data(String[] headers, String[][] contents) {
            this.headers = headers;
            this.contents = contents;
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
                return this;
            }
            if (flavor == DataFlavor.stringFlavor) {
                int i;
                StringBuffer buf = new StringBuffer();
                for (i = 0; i < this.headers.length; ++i) {
                    buf.append(this.headers[i]);
                    buf.append(i == this.headers.length - 1 ? '\n' : '\t');
                }
                for (i = 0; i < this.contents.length; ++i) {
                    for (int j = 0; j < this.contents[i].length; ++j) {
                        buf.append(this.contents[i][j]);
                        buf.append(j == this.contents[i].length - 1 ? '\n' : '\t');
                    }
                }
                return buf.toString();
            }
            throw new UnsupportedFlavorException(flavor);
        }
    }

}

