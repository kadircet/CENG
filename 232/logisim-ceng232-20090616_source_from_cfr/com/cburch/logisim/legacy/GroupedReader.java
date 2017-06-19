/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.legacy;

import com.cburch.logisim.legacy.Strings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

class GroupedReader {
    private int depth = 0;
    private BufferedReader reader;
    private int line_number = 1;
    private String buffer;

    public GroupedReader(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    public GroupedReader(BufferedReader reader) {
        this.reader = reader;
    }

    public void close() throws IOException {
        this.reader.close();
    }

    public String readLine() throws IOException {
        String ret;
        this.getBuffer();
        int pos_lb = this.findFirstUnescaped(this.buffer, '{');
        int pos_rb = this.findFirstUnescaped(this.buffer, '}');
        int pos = pos_lb;
        if (pos_rb >= 0 && (pos == -1 || pos_rb < pos)) {
            pos = pos_rb;
        }
        if (pos < 0) {
            ret = this.buffer;
            this.buffer = null;
        } else {
            ret = this.buffer.substring(0, pos);
            this.buffer = this.buffer.substring(pos);
        }
        ret = this.unprotect(ret);
        return ret;
    }

    public void beginGroup() throws IOException {
        this.getBuffer();
        if (this.buffer.charAt(0) != '{') {
            throw new IOException(Strings.get("notStartError"));
        }
        ++this.depth;
        this.buffer = this.buffer.substring(1);
    }

    public void startGroup() throws IOException {
        this.beginGroup();
    }

    public void endGroup() throws IOException {
        this.getBuffer();
        if (this.buffer.charAt(0) != '}') {
            throw new IOException(Strings.get("notEndError"));
        }
        --this.depth;
        this.buffer = this.buffer.substring(1);
    }

    public boolean atFileEnd() throws IOException {
        this.getBuffer();
        return this.buffer == null;
    }

    public boolean atGroupEnd() throws IOException {
        this.getBuffer();
        return this.buffer.charAt(0) == '}';
    }

    public boolean atGroupStart() throws IOException {
        this.getBuffer();
        return this.buffer != null && this.buffer.charAt(0) == '{';
    }

    public void skipGroup() throws IOException {
        boolean started = this.atGroupStart();
        if (started) {
            this.startGroup();
        }
        do {
            if (this.atGroupStart()) {
                this.skipGroup();
            }
            if (this.atGroupEnd()) break;
            this.readLine();
        } while (true);
        if (started) {
            this.endGroup();
        }
    }

    public String readGroup() throws IOException {
        this.beginGroup();
        StringBuffer ret = new StringBuffer(this.readLine());
        this.getBuffer();
        while (this.buffer.charAt(0) != '}') {
            ret.append('\n');
            ret.append(this.readLine());
            this.getBuffer();
        }
        this.endGroup();
        return ret.toString();
    }

    private void getBuffer() throws IOException {
        int i;
        if (this.buffer != null && this.buffer.length() > 0) {
            return;
        }
        ++this.line_number;
        this.buffer = this.reader.readLine();
        if (this.buffer == null) {
            return;
        }
        for (i = 0; i < this.depth && this.buffer.length() > i && this.buffer.charAt(i) == '\t'; ++i) {
        }
        this.buffer = this.buffer.substring(i);
    }

    private int findFirstUnescaped(String search, char find) {
        int next;
        int pos = 0;
        do {
            if ((next = search.indexOf(find, pos)) < 0) {
                return -1;
            }
            int escape = search.indexOf(92, pos);
            if (escape < 0 || escape >= next || escape + 2 >= search.length()) break;
            pos = escape + 2;
        } while (true);
        return next;
    }

    private String unprotect(String what) {
        int newpos;
        int pos = 0;
        StringBuffer ret = new StringBuffer();
        while ((newpos = what.indexOf(92, pos)) >= 0) {
            ret.append(what.substring(pos, newpos));
            ret.append(what.charAt(newpos + 1));
            pos = newpos + 2;
        }
        ret.append(what.substring(pos));
        return ret.toString();
    }
}

