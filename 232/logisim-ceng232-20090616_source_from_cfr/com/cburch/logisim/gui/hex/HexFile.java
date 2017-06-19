/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.hex;

import com.cburch.hex.HexModel;
import com.cburch.logisim.gui.hex.Strings;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.StringTokenizer;

public class HexFile {
    private static final String RAW_IMAGE_HEADER = "v2.0 raw";

    private HexFile() {
    }

    public static void save(Writer out, HexModel src) throws IOException {
        long last;
        long first = src.getFirstOffset();
        for (last = src.getLastOffset(); last > first && src.get(last) == 0; --last) {
        }
        int tokens = 0;
        long cur = 0;
        while (cur <= last) {
            int val = src.get(cur);
            long start = cur++;
            while (cur <= last && src.get(cur) == val) {
                ++cur;
            }
            long len = cur - start;
            if (len < 4) {
                cur = start + 1;
                len = 1;
            }
            try {
                if (tokens > 0) {
                    out.write(tokens % 8 == 0 ? 10 : 32);
                }
                if (cur != start + 1) {
                    out.write("" + (cur - start) + "*");
                }
                out.write(Integer.toHexString(val));
            }
            catch (IOException e) {
                throw new IOException(Strings.get("hexFileWriteError"));
            }
            ++tokens;
        }
        if (tokens > 0) {
            out.write(10);
        }
    }

    public static void open(HexModel dst, Reader in) throws IOException {
        HexReader reader = new HexReader(new BufferedReader(in));
        long offs = dst.getFirstOffset();
        while (reader.hasNext()) {
            int[] values = reader.next();
            if (offs + (long)values.length - 1 > dst.getLastOffset()) {
                throw new IOException(Strings.get("hexFileSizeError"));
            }
            dst.set(offs, values);
            offs += (long)values.length;
        }
        dst.fill(offs, dst.getLastOffset() - offs + 1, 0);
    }

    public static int[] parse(Reader in) throws IOException {
        HexReader reader = new HexReader(new BufferedReader(in));
        int cur = 0;
        int[] data = new int[4096];
        while (reader.hasNext()) {
            int[] values = reader.next();
            if (cur + values.length > data.length) {
                int[] oldData = data;
                data = new int[Math.max(cur + values.length, 3 * data.length / 2)];
                System.arraycopy(oldData, 0, data, 0, cur);
            }
            System.arraycopy(values, 0, data, cur, values.length);
            cur += values.length;
        }
        if (cur != data.length) {
            int[] oldData = data;
            data = new int[cur];
            System.arraycopy(oldData, 0, data, 0, cur);
        }
        return data;
    }

    public static void open(HexModel dst, File src) throws IOException {
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(src));
        }
        catch (IOException e) {
            throw new IOException(Strings.get("hexFileOpenError"));
        }
        try {
            String header = in.readLine();
            if (!header.equals("v2.0 raw")) {
                throw new IOException(Strings.get("hexHeaderFormatError"));
            }
            HexFile.open(dst, in);
            try {
                BufferedReader oldIn = in;
                in = null;
                oldIn.close();
            }
            catch (IOException e) {
                throw new IOException(Strings.get("hexFileReadError"));
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException e) {}
        }
    }

    public static void save(File dst, HexModel src) throws IOException {
        FileWriter out;
        try {
            out = new FileWriter(dst);
        }
        catch (IOException e) {
            throw new IOException(Strings.get("hexFileOpenError"));
        }
        try {
            try {
                out.write("v2.0 raw\n");
            }
            catch (IOException e) {
                throw new IOException(Strings.get("hexFileWriteError"));
            }
            HexFile.save(out, src);
        }
        finally {
            try {
                out.close();
            }
            catch (IOException e) {
                throw new IOException(Strings.get("hexFileWriteError"));
            }
        }
    }

    private static class HexReader {
        private BufferedReader in;
        private int[] data;
        private StringTokenizer curLine;
        private long leftCount;
        private long leftValue;

        public HexReader(BufferedReader in) throws IOException {
            this.in = in;
            this.data = new int[4096];
            this.curLine = this.findNonemptyLine();
        }

        private StringTokenizer findNonemptyLine() throws IOException {
            String line = this.in.readLine();
            while (line != null) {
                StringTokenizer ret = new StringTokenizer(line);
                if (ret.hasMoreTokens()) {
                    return ret;
                }
                line = this.in.readLine();
            }
            return null;
        }

        private String nextToken() throws IOException {
            if (this.curLine != null && this.curLine.hasMoreTokens()) {
                return this.curLine.nextToken();
            }
            this.curLine = this.findNonemptyLine();
            return this.curLine == null ? null : this.curLine.nextToken();
        }

        public boolean hasNext() {
            return this.leftCount > 0 || this.curLine != null && this.curLine.hasMoreTokens();
        }

        public int[] next() throws IOException {
            int pos = 0;
            if (this.leftCount > 0) {
                int n = (int)Math.min((long)(this.data.length - pos), this.leftCount);
                if (n == 1) {
                    this.data[pos] = (int)this.leftValue;
                    ++pos;
                    --this.leftCount;
                } else {
                    Arrays.fill(this.data, pos, pos + n, (int)this.leftValue);
                    pos += n;
                    this.leftCount -= (long)n;
                }
            }
            if (pos >= this.data.length) {
                return this.data;
            }
            String tok = this.nextToken();
            while (tok != null) {
                try {
                    int star = tok.indexOf("*");
                    if (star < 0) {
                        this.leftCount = 1;
                        this.leftValue = Long.parseLong(tok, 16);
                    } else {
                        this.leftCount = Long.parseLong(tok.substring(0, star));
                        this.leftValue = Long.parseLong(tok.substring(star + 1), 16);
                    }
                }
                catch (NumberFormatException e) {
                    throw new IOException(Strings.get("hexNumberFormatError"));
                }
                int n = (int)Math.min((long)(this.data.length - pos), this.leftCount);
                if (n == 1) {
                    this.data[pos] = (int)this.leftValue;
                    ++pos;
                    --this.leftCount;
                } else {
                    Arrays.fill(this.data, pos, pos + n, (int)this.leftValue);
                    pos += n;
                    this.leftCount -= (long)n;
                }
                if (pos >= this.data.length) {
                    return this.data;
                }
                tok = this.nextToken();
            }
            if (pos >= this.data.length) {
                return this.data;
            }
            int[] ret = new int[pos];
            System.arraycopy(this.data, 0, ret, 0, pos);
            return ret;
        }
    }

}

