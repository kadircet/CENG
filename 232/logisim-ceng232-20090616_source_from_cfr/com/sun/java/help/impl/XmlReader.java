/*
 * Decompiled with CFR 0_114.
 */
package com.sun.java.help.impl;

import com.sun.java.help.impl.HeaderParser;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;

public final class XmlReader
extends Reader {
    private boolean closed;
    private InputStreamReader in;
    private InputStream raw;
    private byte[] buffer;
    private boolean isASCII;
    private boolean isLatin1;
    private int offset;
    private int length;
    private char nextChar;
    private int switchover;
    private String encodingAssigned;
    private static boolean debug = false;

    public static Reader createReader(URLConnection uRLConnection) throws IOException {
        String string = XmlReader.getEncodingFromContentType(uRLConnection.getContentType());
        if (string == null) {
            return XmlReader.createReader(uRLConnection.getInputStream());
        }
        return XmlReader.createReader(uRLConnection.getInputStream(), string);
    }

    private static String getEncodingFromContentType(String string) {
        XmlReader.debug("type=" + string);
        if (string == null) {
            return null;
        }
        int n = string.indexOf(";");
        if (n > -1) {
            String string2 = string.substring(n);
            if ((string = string.substring(0, n).trim()).compareTo("text/xml") == 0) {
                return XmlReader.getCharsetFromContentTypeParameters(string2);
            }
        }
        return null;
    }

    private static String getCharsetFromContentTypeParameters(String string) {
        String string2 = null;
        try {
            int n = string.indexOf(59);
            if (n > -1 && n < string.length() - 1) {
                string = string.substring(n + 1);
            }
            if (string.length() > 0) {
                HeaderParser headerParser = new HeaderParser(string);
                string2 = headerParser.findValue("charset");
                return string2;
            }
        }
        catch (IndexOutOfBoundsException var2_3) {
        }
        catch (NullPointerException var3_5) {
        }
        catch (Exception var4_6) {
            System.err.println("Indexer.getCharsetFromContentTypeParameters failed on: " + string);
            var4_6.printStackTrace();
        }
        return string2;
    }

    public static Reader createReader(InputStream inputStream) throws IOException {
        return new XmlReader(inputStream);
    }

    public static Reader createReader(InputStream inputStream, String string) throws IOException {
        if (string == null) {
            return new XmlReader(inputStream);
        }
        if ("UTF-16".equalsIgnoreCase(string) || "ISO-106460-UCS-2".equalsIgnoreCase(string)) {
            string = "Unicode";
        } else {
            if ("UTF-8".equalsIgnoreCase(string)) {
                return new XmlReader(inputStream, "UTF-8");
            }
            if ("EUC-JP".equalsIgnoreCase(string)) {
                string = "EUCJIS";
            } else {
                if (XmlReader.isAsciiName(string)) {
                    return new XmlReader(inputStream, "US-ASCII");
                }
                if (XmlReader.isLatinName(string)) {
                    return new XmlReader(inputStream, "ISO-8859-1");
                }
            }
        }
        return new InputStreamReader(inputStream, string);
    }

    private XmlReader(InputStream inputStream, String string) throws IOException {
        this.buffer = new byte[8192];
        this.length = 0;
        this.raw = inputStream;
        if ("US-ASCII".equals(string)) {
            this.setASCII();
        } else if ("ISO-8859-1".equals(string)) {
            this.setLatin1();
        } else {
            if (!"UTF-8".equals(string)) {
                throw new UnsupportedEncodingException(string);
            }
            this.setUTF8();
        }
    }

    private static boolean isAsciiName(String string) {
        return "US-ASCII".equalsIgnoreCase(string) || "ASCII".equalsIgnoreCase(string);
    }

    private static boolean isLatinName(String string) {
        return "ISO-8859-1".equalsIgnoreCase(string) || "Latin1".equalsIgnoreCase(string) || "8859_1".equalsIgnoreCase(string);
    }

    private void setASCII() {
        this.encodingAssigned = "US-ASCII";
        this.isASCII = true;
        this.isLatin1 = false;
        this.offset = 0;
    }

    private void setLatin1() {
        this.encodingAssigned = "ISO-8859-1";
        this.isASCII = false;
        this.isLatin1 = true;
        this.offset = 0;
    }

    private void setUTF8() {
        this.encodingAssigned = "UTF-8";
        this.isASCII = false;
        this.isLatin1 = false;
        this.offset = 0;
    }

    public String getEncoding() {
        return this.encodingAssigned;
    }

    private XmlReader(InputStream inputStream) throws IOException {
        this.raw = inputStream;
        this.switchover = -1;
        this.buffer = new byte[8192];
        this.length = 0;
        this.offset = 0;
        this.isLatin1 = true;
        int n = this.read();
        block0 : switch (n) {
            case 0: {
                n = this.read();
                if (n == 60 && (n = this.read()) == 0 && (n = this.read()) == 63) {
                    this.setSwitchover("UnicodeBig");
                    return;
                }
                throw new UnsupportedEncodingException("UCS-4 (?)");
            }
            case 60: {
                n = this.read();
                switch (n) {
                    default: {
                        break block0;
                    }
                    case 0: {
                        if (this.read() == 63 && this.read() == 0) {
                            this.setSwitchover("UnicodeLittle");
                            return;
                        }
                        throw new UnsupportedEncodingException("UCS-4");
                    }
                    case 63: 
                }
                if (this.read() != 120 || this.read() != 109 || this.read() != 108 || this.read() != 32) break;
                this.guessEncoding();
                return;
            }
            case 254: {
                n = this.read();
                if (n != 255) break;
                this.setSwitchover("UnicodeBig");
                this.offset = 2;
                return;
            }
            case 255: {
                n = this.read();
                if (n != 254) break;
                this.setSwitchover("UnicodeLittle");
                this.offset = 2;
                return;
            }
            case -1: {
                return;
            }
        }
        this.setUTF8();
    }

    private void setSwitchover(String string) throws IOException {
        this.switchover = this.offset;
        this.encodingAssigned = string;
        this.offset = 0;
    }

    private void doSwitchover() throws IOException {
        if (this.offset != this.switchover) {
            throw new InternalError();
        }
        this.in = new InputStreamReader(this.raw, this.encodingAssigned);
        this.buffer = null;
        this.switchover = -1;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    private void guessEncoding() throws IOException {
        var2_1 = new StringBuffer();
        var3_2 = null;
        var4_3 = null;
        var5_4 = false;
        var6_5 = '\u0000';
        var7_6 = false;
        var8_7 = 0;
        block0 : while (var8_7 < 100) {
            var1_8 = this.read();
            if (var1_8 == -1) {
                this.setASCII();
                return;
            }
            if (Character.isWhitespace((char)var1_8)) ** GOTO lbl54
            if (var1_8 == 63) {
                var7_6 = true;
            } else if (var7_6) {
                if (var1_8 == 62) break;
                var7_6 = false;
            }
            if (var4_3 != null && var5_4) ** GOTO lbl40
            if (var3_2 == null) {
                if (!Character.isWhitespace((char)var1_8)) {
                    var3_2 = var2_1;
                    var2_1.setLength(0);
                    var2_1.append((char)var1_8);
                    var5_4 = false;
                }
            } else if (Character.isWhitespace((char)var1_8)) {
                var4_3 = var3_2.toString();
            } else if (var1_8 == 61) {
                if (var4_3 == null) {
                    var4_3 = var3_2.toString();
                }
                var5_4 = true;
                var3_2 = null;
                var6_5 = '\u0000';
            } else {
                var3_2.append((char)var1_8);
            }
            ** GOTO lbl54
lbl40: // 1 sources:
            if (Character.isWhitespace((char)var1_8)) ** GOTO lbl54
            if (var1_8 != 34 && var1_8 != 39) ** GOTO lbl53
            if (var6_5 != '\u0000') ** GOTO lbl46
            var6_5 = (char)var1_8;
            var2_1.setLength(0);
            ** GOTO lbl54
lbl46: // 1 sources:
            if (var1_8 != var6_5) ** GOTO lbl53
            if (!"encoding".equals(var4_3)) ** GOTO lbl51
            var9_9 = var2_1.toString();
            var8_7 = 0;
            ** GOTO lbl58
lbl51: // 1 sources:
            var4_3 = null;
            ** GOTO lbl54
lbl53: // 2 sources:
            var2_1.append((char)var1_8);
lbl54: // 9 sources:
            ++var8_7;
            continue;
            while ((var1_8 = (int)var9_9.charAt(var8_7)) >= 65 && var1_8 <= 90 || var1_8 >= 97 && var1_8 <= 122 || var8_7 > 0 && (var1_8 == 45 || var1_8 >= 48 && var1_8 <= 57 || var1_8 == 46 || var1_8 == 95)) {
                ++var8_7;
lbl58: // 2 sources:
                if (var8_7 < var9_9.length()) continue;
                if (XmlReader.isAsciiName(var9_9)) {
                    this.setASCII();
                    return;
                }
                if (XmlReader.isLatinName(var9_9)) {
                    this.setLatin1();
                    return;
                }
                if ("UTF-8".equalsIgnoreCase(var9_9) || "UTF8".equalsIgnoreCase(var9_9)) break block0;
                if ("EUC-JP".equalsIgnoreCase(var9_9)) {
                    var9_9 = "EUCJIS";
                }
                this.setSwitchover(var9_9);
                return;
            }
            break block0;
        }
        this.setUTF8();
    }

    private char utf8char() throws IOException {
        int n;
        char c;
        int n2;
        block12 : {
            if (this.nextChar != '\u0000') {
                char c2 = this.nextChar;
                this.nextChar = '\u0000';
                return c2;
            }
            c = this.buffer[this.offset];
            if ((c & 128) == 0) {
                ++this.offset;
                return c;
            }
            if (this.isASCII) {
                throw new CharConversionException("Not US-ASCII:  0x" + Integer.toHexString(c & 255));
            }
            n2 = this.offset;
            try {
                if ((this.buffer[n2] & 224) == 192) {
                    n = (this.buffer[n2++] & 31) << 6;
                    c = (char)(n += this.buffer[n2++] & 63);
                    n = 0;
                    break block12;
                }
                if ((this.buffer[n2] & 240) == 224) {
                    n = (this.buffer[n2++] & 15) << 12;
                    n += (this.buffer[n2++] & 63) << 6;
                    c = (char)(n += this.buffer[n2++] & 63);
                    n = 0;
                    break block12;
                }
                if ((this.buffer[n2] & 248) == 240) {
                    n = (this.buffer[n2++] & 7) << 18;
                    n += (this.buffer[n2++] & 63) << 12;
                    n += (this.buffer[n2++] & 63) << 6;
                    n += this.buffer[n2++] & 63;
                    c = (char)(55296 + ((n -= 65536) >> 10));
                    n = 56320 + (n & 1023);
                    break block12;
                }
                throw new CharConversionException("Illegal XML character 0x" + Integer.toHexString(this.buffer[this.offset] & 255));
            }
            catch (ArrayIndexOutOfBoundsException var4_5) {
                c = '\u0000';
                n = 0;
            }
        }
        if (n2 > this.length) {
            System.arraycopy(this.buffer, this.offset, this.buffer, 0, this.length - this.offset);
            this.length -= this.offset;
            this.offset = 0;
            n2 = this.raw.read(this.buffer, this.length, this.buffer.length - this.length);
            if (n2 < 0) {
                throw new CharConversionException("Partial UTF-8 char");
            }
            this.length += n2;
            return this.utf8char();
        }
        ++this.offset;
        while (this.offset < n2) {
            if ((this.buffer[this.offset] & 192) != 128) {
                throw new CharConversionException("Malformed UTF-8 char");
            }
            ++this.offset;
        }
        this.nextChar = (char)n;
        return c;
    }

    public int read(char[] arrc, int n, int n2) throws IOException {
        int n3;
        if (this.closed) {
            return -1;
        }
        if (this.switchover > 0 && this.offset == this.switchover) {
            this.doSwitchover();
        }
        if (this.in != null) {
            return this.in.read(arrc, n, n2);
        }
        if (this.offset >= this.length) {
            this.offset = 0;
            this.length = this.raw.read(this.buffer, 0, this.buffer.length);
        }
        if (this.length <= 0) {
            return -1;
        }
        if (this.encodingAssigned == null || this.isLatin1) {
            n3 = 0;
            while (n3 < n2 && this.offset < this.length) {
                arrc[n++] = (char)(this.buffer[this.offset++] & 255);
                ++n3;
            }
        } else {
            n3 = 0;
            while (n3 < n2 && this.offset < this.length) {
                arrc[n++] = this.utf8char();
                ++n3;
            }
        }
        return n3;
    }

    public int read() throws IOException {
        if (this.closed) {
            return -1;
        }
        if (this.switchover > 0 && this.offset == this.switchover) {
            this.doSwitchover();
        }
        if (this.in != null) {
            return this.in.read();
        }
        if (this.offset >= this.length) {
            if (this.encodingAssigned == null) {
                if (this.length == this.buffer.length) {
                    throw new InternalError("too much peekahead");
                }
                int n = this.raw.read(this.buffer, this.offset, 1);
                if (n <= 0) {
                    return -1;
                }
                this.length += n;
            } else {
                this.offset = 0;
                this.length = this.raw.read(this.buffer, 0, this.buffer.length);
                if (this.length <= 0) {
                    return -1;
                }
            }
        }
        if (this.isLatin1 || this.encodingAssigned == null) {
            return this.buffer[this.offset++] & 255;
        }
        return this.utf8char();
    }

    public boolean markSupported() {
        return this.in != null && this.in.markSupported();
    }

    public void mark(int n) throws IOException {
        if (this.in != null) {
            this.in.mark(n);
        }
    }

    public void reset() throws IOException {
        if (this.in != null) {
            this.in.reset();
        }
    }

    public long skip(long l) throws IOException {
        if (l < 0) {
            return 0;
        }
        if (this.in != null) {
            return this.in.skip(l);
        }
        long l2 = this.length - this.offset;
        if (l2 >= l) {
            this.offset += (int)l;
            return l;
        }
        this.offset = (int)((long)this.offset + l2);
        return l2 + this.raw.skip(l - l2);
    }

    public boolean ready() throws IOException {
        if (this.in != null) {
            return this.in.ready();
        }
        return this.length > this.offset || this.raw.available() != 0;
    }

    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        if (this.in != null) {
            this.in.close();
        } else {
            this.raw.close();
        }
        this.closed = true;
    }

    private static void debug(String string) {
        if (debug) {
            System.out.println("XmlReader: " + string);
        }
    }
}

