/*
 * Decompiled with CFR 0_114.
 */
package com.sun.java.help.impl;

import com.sun.java.help.impl.MyBufferedReader;
import com.sun.java.help.impl.ScanBuffer;
import com.sun.java.help.impl.TagProperties;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

public abstract class DocumentParser {
    static final char EOF = '\uffff';
    protected Reader source;
    int readOffset;
    ScanBuffer buffer;
    ScanBuffer escapeBuffer;
    ScanBuffer documentSource;
    boolean shouldCacheSource;
    Hashtable entities;
    int defaultCharBufferSize = 8192;
    char[] cb = new char[this.defaultCharBufferSize];
    int mySize = 0;
    int myCount = 0;

    public DocumentParser(InputStream inputStream) {
        this.source = new MyBufferedReader(new InputStreamReader(inputStream));
        this.init();
    }

    public DocumentParser(Reader reader) {
        this.source = reader instanceof MyBufferedReader ? reader : new MyBufferedReader(reader);
        this.init();
    }

    public void setInput(Reader reader) {
        this.source = reader instanceof MyBufferedReader ? reader : new MyBufferedReader(reader);
    }

    public void setInput(InputStream inputStream, String string) throws UnsupportedEncodingException {
        if (inputStream == null) {
            this.source = null;
            return;
        }
        this.source = new MyBufferedReader(new InputStreamReader(inputStream, string));
    }

    public void setShouldCacheSource(boolean bl) {
        this.shouldCacheSource = bl;
    }

    public String getDocumentSource() {
        if (!this.shouldCacheSource) {
            return null;
        }
        int n = 0 == this.documentSource.length() ? 0 : this.documentSource.length() - 1;
        return new String(this.documentSource.buf, 0, n) + "\n";
    }

    public void parse() throws IOException {
        int n = 65535;
        this.buffer.clear();
        if (this.source != null) {
            n = this.readChar();
        }
        while (n != 65535) {
            if (n == 38) {
                n = this.parseEscape();
                continue;
            }
            if (n == 60) {
                this.buffer.flush(this);
                n = this.parseTag();
                continue;
            }
            if (this.buffer.buflen >= this.buffer.buf.length) {
                char[] arrc = new char[this.buffer.buf.length * this.buffer.scale];
                System.arraycopy(this.buffer.buf, 0, arrc, 0, this.buffer.buf.length);
                this.buffer.buf = arrc;
            }
            if (n != 13) {
                this.buffer.buf[this.buffer.buflen++] = n;
            }
            if (this.myCount >= this.mySize) {
                try {
                    this.mySize = this.source.read(this.cb, 0, this.defaultCharBufferSize);
                    if (this.mySize < 0) break;
                    if (this.mySize == 0) {
                        System.err.println(" DocumentParser::parse() !!!ERROR !!! source.read(...) == 0");
                        break;
                    }
                    this.myCount = 0;
                }
                catch (CharConversionException var2_3) {
                    throw var2_3;
                }
                catch (IOException var3_4) {
                    break;
                }
            }
            if (this.shouldCacheSource) {
                this.documentSource.add(this.cb[this.myCount]);
            }
            n = this.cb[this.myCount++];
        }
        this.buffer.flush(this);
    }

    public void parseText() throws IOException {
        this.tag("PRE", null, false, false);
        this.buffer.clear();
        char c = this.readChar();
        while (c != '\uffff') {
            this.buffer.add(c);
            c = this.readChar();
        }
        this.buffer.flush(this);
    }

    protected void callFlush(char[] arrc, int n, int n2) {
        this.flush(arrc, n, n2);
    }

    protected abstract void flush(char[] var1, int var2, int var3);

    protected abstract void comment(String var1);

    protected abstract void tag(String var1, TagProperties var2, boolean var3, boolean var4);

    protected abstract void pi(String var1, String var2);

    protected abstract void doctype(String var1, String var2, String var3);

    protected abstract String documentAttribute(String var1);

    protected abstract void errorString(String var1);

    private void init() {
        this.buffer = new ScanBuffer(8192, 4);
        this.escapeBuffer = new ScanBuffer(8192, 4);
        this.documentSource = new ScanBuffer(8192, 4);
        this.readOffset = 0;
    }

    protected void findCloseAngleForComment(char c) throws IOException {
        this.buffer.add(c);
        while ((c = this.readChar()) != '>') {
            this.buffer.add(c);
        }
        this.buffer.add(c);
        this.comment(this.buffer.extract(0));
        this.buffer.clear();
    }

    protected char handleCommentOrDoctype(char c) throws IOException {
        this.buffer.add(c);
        int n = this.buffer.length();
        c = this.scanIdentifier(c);
        String string = this.buffer.extract(n);
        if (!string.equals("DOCTYPE")) {
            this.findCloseAngleForComment(c);
            return this.readChar();
        }
        c = this.skipWhite(c);
        n = this.buffer.length();
        c = this.scanIdentifier(c);
        String string2 = this.buffer.extract(n);
        if ((c = this.skipWhite(c)) == '>') {
            this.buffer.clear();
            return this.readChar();
        }
        n = this.buffer.length();
        c = this.scanIdentifier(c);
        string = this.buffer.extract(n);
        String string3 = null;
        String string4 = null;
        if (string.equals("SYSTEM")) {
            c = this.skipWhite(c);
            n = this.buffer.length();
            c = this.scanQuotedString(c);
            string4 = this.buffer.extract(n);
            this.doctype(string2, null, string4);
            if (c != '>') {
                this.findCloseAngleForComment(c);
            }
            this.buffer.clear();
            return this.readChar();
        }
        if (string.equals("PUBLIC")) {
            c = this.skipWhite(c);
            n = this.buffer.length();
            c = this.scanQuotedString(c);
            string3 = this.buffer.extract(n);
            c = this.skipWhite(c);
            n = this.buffer.length();
            c = this.scanQuotedString(c);
            string4 = this.buffer.extract(n);
            this.doctype(string2, string3, string4);
            if (c != '>') {
                this.findCloseAngleForComment(c);
            }
            this.buffer.clear();
            return this.readChar();
        }
        if (c != '>') {
            this.findCloseAngleForComment(c);
        }
        this.findCloseAngleForComment(c);
        this.doctype(string2, null, null);
        this.buffer.clear();
        return this.readChar();
    }

    protected void setXmlEntities(TagProperties tagProperties) {
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    protected char parseTag() throws IOException {
        this.buffer.clear();
        var5_1 = false;
        var1_2 = '<';
        this.buffer.add('<');
        var1_2 = this.readChar();
        if (var1_2 != 33) ** GOTO lbl41
        var8_3 = 0;
        this.buffer.add('!');
        var1_2 = this.readChar();
        if (var1_2 != '-') {
            return this.handleCommentOrDoctype(var1_2);
        }
        this.buffer.add(var1_2);
        var1_2 = this.readChar();
        if (var1_2 != '-') {
            this.findCloseAngleForComment(var1_2);
            return this.readChar();
        }
        this.buffer.add(var1_2);
        var8_3 = this.buffer.length();
        var9_5 = 0;
        do {
            if ((var1_2 = this.readChar()) == '\uffff') {
                this.commentEOFError(var8_3);
                break;
            }
            if (var1_2 == 45) ** GOTO lbl30
            this.buffer.add(var1_2);
            continue;
lbl-1000: // 1 sources:
            {
                this.buffer.add(var1_2);
                ++var9_5;
                var1_2 = this.readChar();
lbl30: // 2 sources:
                ** while (var1_2 == 45)
            }
lbl31: // 1 sources:
            if (var1_2 == '\uffff') {
                this.commentEOFError(var8_3);
                break;
            }
            this.buffer.add(var1_2);
            if (var9_5 >= 2 && var1_2 == '>') {
                this.comment(this.buffer.extract(0));
                this.buffer.clear();
                return this.readChar();
            }
            var9_5 = 0;
        } while (true);
lbl41: // 3 sources:
        if (var1_2 != 63) ** GOTO lbl46
        var8_3 = 0;
        var9_6 = new StringBuffer();
        this.buffer.add('?');
        ** GOTO lbl60
lbl46: // 1 sources:
        if ((var1_2 = this.skipWhite(var1_2)) == '\uffff') {
            this.eofError();
            return var1_2;
        }
        if (var1_2 == '/') {
            this.buffer.add(var1_2);
            var1_2 = this.skipWhite(this.readChar());
            var6_16 = true;
            var7_17 = false;
        } else {
            var6_16 = false;
            var7_17 = false;
        }
        ** GOTO lbl151
lbl-1000: // 1 sources:
        {
            this.buffer.add(var1_2);
            var9_6.append(var1_2);
lbl60: // 2 sources:
            ** while ((var1_2 = this.readChar()) != '\"' && var1_2 != ' ' && var1_2 != '\t' && var1_2 != '\n' && var1_2 != 62)
        }
lbl61: // 1 sources:
        if (var9_6.toString().equals("xml")) ** GOTO lbl78
        this.buffer.clear();
        ** GOTO lbl65
lbl-1000: // 1 sources:
        {
            this.buffer.add(var1_2);
lbl65: // 2 sources:
            ** while ((var1_2 = this.readChar()) != '?' && var1_2 != 65535)
        }
lbl66: // 1 sources:
        if (var1_2 == '\uffff') {
            this.eofError();
            return this.readChar();
        }
        var1_2 = this.readChar();
        if (var1_2 != '>' && var1_2 != '\uffff') {
            this.buffer.add('?');
            this.buffer.add(var1_2);
        }
        if (var1_2 == '\uffff') {
            this.eofError();
            return this.readChar();
        }
        this.pi(var9_6.toString(), this.buffer.extract(0));
        return this.readChar();
lbl78: // 1 sources:
        var9_6 = null;
        var1_2 = this.readChar();
        var3_8 = null;
        do {
            block40 : {
                if ((var1_2 = this.skipWhite(var1_2)) == '\uffff') {
                    this.eofError();
                    return var1_2;
                }
                if (var1_2 != 63) ** GOTO lbl89
                var1_2 = this.readChar();
                if (var1_2 != 65535) ** GOTO lbl112
                ** GOTO lbl110
lbl89: // 1 sources:
                var4_10 = this.buffer.length();
                var1_2 = this.scanIdentifier(var1_2);
                if (var4_10 == this.buffer.length()) {
                    this.error("Expecting an attribute");
                    this.skipToCloseAngle(var1_2);
                    return this.readChar();
                }
                var10_12 = this.buffer.extract(var4_10);
                var1_2 = this.skipWhite(var1_2);
                if (var3_8 == null) {
                    var3_8 = new TagProperties();
                }
                if (var1_2 != 61) ** GOTO lbl108
                this.buffer.add(var1_2);
                var1_2 = this.readChar();
                var1_2 = this.skipWhite(var1_2);
                if (var1_2 != '?' && var1_2 != 60) ** GOTO lbl106
                var11_14 = "";
                ** GOTO lbl148
lbl106: // 1 sources:
                if (var1_2 == '\"') ** GOTO lbl121
                ** GOTO lbl136
lbl108: // 1 sources:
                var11_14 = "true";
                ** GOTO lbl148
lbl110: // 1 sources:
                this.eofError();
                return this.readChar();
lbl112: // 1 sources:
                var1_2 = this.skipWhite(var1_2);
                this.buffer.add(var1_2);
                if (var1_2 == '>') {
                    this.setXmlEntities(var3_8);
                    this.buffer.clear();
                    return this.readChar();
                }
                this.error("Expecting ?>");
                this.skipToCloseAngle(var1_2);
                return this.readChar();
lbl121: // 1 sources:
                this.buffer.add(var1_2);
                var12_15 = this.buffer.length();
                do {
                    if ((var1_2 = this.readChar()) == '\uffff') {
                        this.eofError();
                        return var1_2;
                    }
                    if (var1_2 == '\"') {
                        var11_14 = this.buffer.extract(var12_15);
                        this.buffer.add(var1_2);
                        var1_2 = this.readChar();
                        break block40;
                    }
                    if (var1_2 == '&') {
                        var1_2 = this.parseEscape();
                    }
                    this.buffer.add(var1_2);
                } while (true);
lbl136: // 1 sources:
                var12_15 = this.buffer.length();
                this.buffer.add(var1_2);
                do {
                    if ((var1_2 = this.readChar()) == '\uffff') {
                        this.eofError();
                        return var1_2;
                    }
                    if (var1_2 == 34 || var1_2 == 32 || var1_2 == 9 || var1_2 == 10 || var1_2 == '?') break;
                    if (var1_2 == '&') {
                        var1_2 = this.parseEscape();
                    }
                    this.buffer.add(var1_2);
                } while (true);
                var11_14 = this.buffer.extract(var12_15);
            }
            var3_8.put(var10_12, var11_14);
        } while (true);
lbl151: // 2 sources:
        var4_11 = this.buffer.length();
        var1_2 = this.scanIdentifier(var1_2);
        var2_18 = this.buffer.extract(var4_11);
        var3_9 = null;
        do {
            block41 : {
                if ((var1_2 = this.skipWhite(var1_2)) == '\uffff') {
                    this.eofError();
                    return var1_2;
                }
                if (var1_2 == '>') ** GOTO lbl195
                if (var1_2 != 47) ** GOTO lbl170
                this.buffer.add(var1_2);
                var1_2 = this.readChar();
                if (var1_2 != '>') {
                    this.error("Expecting />");
                    this.skipToCloseAngle(var1_2);
                    return this.readChar();
                }
                var6_16 = true;
                var7_17 = true;
                ** GOTO lbl195
lbl170: // 1 sources:
                if (var1_2 == '<') {
                    this.tag(var2_18, var3_9, var6_16, false);
                    this.buffer.clear();
                    return '<';
                }
                var4_11 = this.buffer.length();
                var1_2 = this.scanIdentifier(var1_2);
                if (var4_11 == this.buffer.length()) {
                    this.error("Expecting an attribute (2)");
                    this.skipToCloseAngle(var1_2);
                    return this.readChar();
                }
                var8_4 = this.buffer.extract(var4_11);
                var1_2 = this.skipWhite(var1_2);
                if (var3_9 == null) {
                    var3_9 = new TagProperties();
                }
                if (var1_2 != 61) ** GOTO lbl193
                this.buffer.add(var1_2);
                var1_2 = this.readChar();
                var1_2 = this.skipWhite(var1_2);
                if (var1_2 != '>' && var1_2 != 60) ** GOTO lbl191
                var9_7 = "";
                ** GOTO lbl225
lbl191: // 1 sources:
                if (var1_2 == '\"') ** GOTO lbl198
                ** GOTO lbl213
lbl193: // 1 sources:
                var9_7 = "true";
                ** GOTO lbl225
lbl195: // 2 sources:
                this.tag(var2_18, var3_9, var6_16, var7_17);
                this.buffer.clear();
                return this.readChar();
lbl198: // 1 sources:
                this.buffer.add(var1_2);
                var10_13 = this.buffer.length();
                do {
                    if ((var1_2 = this.readChar()) == '\uffff') {
                        this.eofError();
                        return var1_2;
                    }
                    if (var1_2 == '\"') {
                        var9_7 = this.buffer.extract(var10_13);
                        this.buffer.add(var1_2);
                        var1_2 = this.readChar();
                        break block41;
                    }
                    if (var1_2 == '&') {
                        var1_2 = this.parseEscape();
                    }
                    this.buffer.add(var1_2);
                } while (true);
lbl213: // 1 sources:
                var10_13 = this.buffer.length();
                this.buffer.add(var1_2);
                do {
                    if ((var1_2 = this.readChar()) == '\uffff') {
                        this.eofError();
                        return var1_2;
                    }
                    if (var1_2 == 34 || var1_2 == 32 || var1_2 == 9 || var1_2 == 10 || var1_2 == '>') break;
                    if (var1_2 == '&') {
                        var1_2 = this.parseEscape();
                    }
                    this.buffer.add(var1_2);
                } while (true);
                var9_7 = this.buffer.extract(var10_13);
            }
            var3_9.put(var8_4, var9_7);
        } while (true);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    protected char parseEscape() throws IOException {
        block14 : {
            var2_1 = this.buffer.length();
            this.buffer.add('&');
            var1_2 = this.readChar();
            if (var1_2 == '\uffff') {
                this.generateError(var2_1);
                return var1_2;
            }
            if (var1_2 != '#') ** GOTO lbl27
            var3_3 = 0;
            do {
                if ((var1_2 = this.readChar()) == '\uffff') {
                    this.generateError(var2_1);
                    return var1_2;
                }
                if (var1_2 == ';') {
                    var1_2 = '\u0000';
                    break;
                }
                if (!Character.isDigit(var1_2)) {
                    if (var3_3 > 0) break;
                    this.error("Expecting a digit");
                    this.generateError(var2_1);
                    return var1_2;
                }
                this.buffer.add(var1_2);
                var3_3 = var3_3 * 10 + Character.digit(var1_2, 10);
            } while (true);
            this.buffer.reset(var2_1);
            this.buffer.add((char)var3_3);
            ** GOTO lbl66
lbl27: // 1 sources:
            if (!Character.isLowerCase(var1_2) && !Character.isUpperCase(var1_2)) {
                this.error("Expecting a letter o");
                this.generateError(var2_1);
                return var1_2;
            }
            if (this.entities == null) {
                this.initEntities();
            }
            this.escapeBuffer.clear();
            this.escapeBuffer.add(var1_2);
            do {
                this.buffer.add(var1_2);
                var1_2 = this.readChar();
                if (var1_2 == '\uffff') {
                    this.generateError(var2_1);
                    return var1_2;
                }
                if (!Character.isLowerCase(var1_2) && !Character.isUpperCase(var1_2)) break block14;
                this.escapeBuffer.add(var1_2);
            } while (this.entities.get(this.escapeBuffer.extract(0)) == null);
            var1_2 = this.readChar();
            if (var1_2 == ';') {
                var1_2 = '\u0000';
            }
            ** GOTO lbl54
        }
        if (var1_2 != ';') {
            this.error("Expecting a letter");
            this.generateError(var2_1);
            return var1_2;
        }
        var1_2 = '\u0000';
lbl54: // 2 sources:
        var3_4 = this.escapeBuffer.extract(0);
        this.buffer.reset(var2_1);
        var4_5 = (Character)this.entities.get(var3_4);
        if (var4_5 != null) {
            return var4_5.charValue();
        }
        var5_6 = this.documentAttribute(var3_4);
        if (var5_6 != null) {
            var6_7 = 0;
            while (var6_7 < var5_6.length()) {
                this.buffer.add(var5_6.charAt(var6_7));
                ++var6_7;
            }
        }
lbl66: // 4 sources:
        if (var1_2 == '\u0000') return this.readChar();
        return var1_2;
    }

    protected void initEntities() {
        this.entities = new Hashtable<K, V>();
        this.entities.put("quot", new Character('\"'));
        this.entities.put("amp", new Character('&'));
        this.entities.put("gt", new Character('>'));
        this.entities.put("lt", new Character('<'));
        this.entities.put("nbsp", new Character('\u00a0'));
        this.entities.put("copy", new Character('\u00a9'));
        this.entities.put("Agrave", new Character('\u00c0'));
        this.entities.put("Aacute", new Character('\u00c1'));
        this.entities.put("Acirc", new Character('\u00c2'));
        this.entities.put("Atilde", new Character('\u00c3'));
        this.entities.put("Auml", new Character('\u00c4'));
        this.entities.put("Aring", new Character('\u00c5'));
        this.entities.put("AElig", new Character('\u00c6'));
        this.entities.put("Ccedil", new Character('\u00c7'));
        this.entities.put("Egrave", new Character('\u00c8'));
        this.entities.put("Eacute", new Character('\u00c9'));
        this.entities.put("Ecirc", new Character('\u00ca'));
        this.entities.put("Euml", new Character('\u00cb'));
        this.entities.put("Igrave", new Character('\u00cc'));
        this.entities.put("Iacute", new Character('\u00cd'));
        this.entities.put("Icirc", new Character('\u00ce'));
        this.entities.put("Iuml", new Character('\u00cf'));
        this.entities.put("Ntilde", new Character('\u00d1'));
        this.entities.put("Ograve", new Character('\u00d2'));
        this.entities.put("Oacute", new Character('\u00d3'));
        this.entities.put("Ocirc", new Character('\u00d4'));
        this.entities.put("Otilde", new Character('\u00d5'));
        this.entities.put("Ouml", new Character('\u00d6'));
        this.entities.put("Oslash", new Character('\u00d8'));
        this.entities.put("Ugrave", new Character('\u00d9'));
        this.entities.put("Uacute", new Character('\u00da'));
        this.entities.put("Ucirc", new Character('\u00db'));
        this.entities.put("Uuml", new Character('\u00dc'));
        this.entities.put("Yacute", new Character('\u00dd'));
        this.entities.put("THORN", new Character('\u00de'));
        this.entities.put("szlig", new Character('\u00df'));
        this.entities.put("agrave", new Character('\u00e0'));
        this.entities.put("aacute", new Character('\u00e1'));
        this.entities.put("acirc", new Character('\u00e2'));
        this.entities.put("atilde", new Character('\u00e3'));
        this.entities.put("auml", new Character('\u00e4'));
        this.entities.put("aring", new Character('\u00e5'));
        this.entities.put("aelig", new Character('\u00e6'));
        this.entities.put("ccedil", new Character('\u00e7'));
        this.entities.put("egrave", new Character('\u00e8'));
        this.entities.put("eacute", new Character('\u00e9'));
        this.entities.put("ecirc", new Character('\u00ea'));
        this.entities.put("euml", new Character('\u00eb'));
        this.entities.put("igrave", new Character('\u00ec'));
        this.entities.put("iacute", new Character('\u00ed'));
        this.entities.put("icirc", new Character('\u00ee'));
        this.entities.put("iuml", new Character('\u00ef'));
        this.entities.put("eth", new Character('\u00f0'));
        this.entities.put("ntilde", new Character('\u00f1'));
        this.entities.put("ograve", new Character('\u00f2'));
        this.entities.put("oacute", new Character('\u00f3'));
        this.entities.put("ocirc", new Character('\u00f4'));
        this.entities.put("otilde", new Character('\u00f5'));
        this.entities.put("ouml", new Character('\u00f6'));
        this.entities.put("oslash", new Character('\u00f8'));
        this.entities.put("ugrave", new Character('\u00f9'));
        this.entities.put("uacute", new Character('\u00fa'));
        this.entities.put("ucirc", new Character('\u00fb'));
        this.entities.put("uuml", new Character('\u00fc'));
        this.entities.put("yacute", new Character('\u00fd'));
        this.entities.put("thorn", new Character('\u00fe'));
        this.entities.put("yuml", new Character('\u00ff'));
    }

    protected char scanIdentifier(char c) throws IOException {
        while (c == '_' || c == ':' || c >= '0' && c <= '9' || Character.isLetter(c)) {
            if (this.buffer.buflen >= this.buffer.buf.length) {
                char[] arrc = new char[this.buffer.buf.length * this.buffer.scale];
                System.arraycopy(this.buffer.buf, 0, arrc, 0, this.buffer.buf.length);
                this.buffer.buf = arrc;
            }
            this.buffer.buf[this.buffer.buflen++] = c;
            if (this.myCount >= this.mySize) {
                try {
                    this.mySize = this.source.read(this.cb, 0, this.defaultCharBufferSize);
                    if (this.mySize < 0) break;
                    if (this.mySize == 0) {
                        System.err.println(" DocumentParser::scanIdentifier() !!!ERROR !!! source.read(...) == 0");
                        break;
                    }
                    this.myCount = 0;
                }
                catch (CharConversionException var2_3) {
                    throw var2_3;
                }
                catch (IOException var3_4) {
                    break;
                }
            }
            if (this.shouldCacheSource) {
                this.documentSource.add(this.cb[this.myCount]);
            }
            c = this.cb[this.myCount++];
        }
        return c;
    }

    protected char scanQuotedString(char c) throws IOException {
        if ((c = this.skipWhite(c)) == '\"') {
            while ((c = this.readChar()) != '\"' && c != '>') {
                this.buffer.add(c);
            }
            return this.readChar();
        }
        if (c == '\'') {
            while ((c = this.readChar()) != '\'' && c != '>') {
                this.buffer.add(c);
            }
            return this.readChar();
        }
        return c;
    }

    protected char skipWhite(char c) throws IOException {
        while (c == ' ' || c == '\r' || c == '\t' || c == '\n') {
            if (this.buffer.buflen >= this.buffer.buf.length) {
                char[] arrc = new char[this.buffer.buf.length * this.buffer.scale];
                System.arraycopy(this.buffer.buf, 0, arrc, 0, this.buffer.buf.length);
                this.buffer.buf = arrc;
            }
            this.buffer.buf[this.buffer.buflen++] = c;
            if (this.myCount >= this.mySize) {
                try {
                    this.mySize = this.source.read(this.cb, 0, this.defaultCharBufferSize);
                    if (this.mySize < 0) break;
                    if (this.mySize == 0) {
                        System.err.println(" DocumentParser::parse() !!!ERROR !!! source.read(...) == 0");
                        break;
                    }
                    this.myCount = 0;
                }
                catch (CharConversionException var2_3) {
                    throw var2_3;
                }
                catch (IOException var3_4) {
                    break;
                }
            }
            if (this.shouldCacheSource) {
                this.documentSource.add(this.cb[this.myCount]);
            }
            c = this.cb[this.myCount++];
        }
        return c;
    }

    protected char readChar() throws IOException {
        if (this.myCount >= this.mySize) {
            try {
                this.mySize = this.source.read(this.cb, 0, this.defaultCharBufferSize);
                if (this.mySize < 0) {
                    return '\uffff';
                }
                if (this.mySize == 0) {
                    System.err.println(" DocumentParser::readChar() !!!ERROR !!! source.read(...) == 0");
                    return '\uffff';
                }
                this.myCount = 0;
            }
            catch (CharConversionException var1_1) {
                throw var1_1;
            }
            catch (IOException var2_2) {
                return '\uffff';
            }
        }
        if (this.shouldCacheSource) {
            this.documentSource.add(this.cb[this.myCount]);
        }
        return this.cb[this.myCount++];
    }

    protected void skipToCloseAngle(char c) throws IOException {
        do {
            if (this.buffer.buflen >= this.buffer.buf.length) {
                char[] arrc = new char[this.buffer.buf.length * this.buffer.scale];
                System.arraycopy(this.buffer.buf, 0, arrc, 0, this.buffer.buf.length);
                this.buffer.buf = arrc;
            }
            this.buffer.buf[this.buffer.buflen++] = c;
            if (c == '>') break;
            if (this.myCount >= this.mySize) {
                try {
                    this.mySize = this.source.read(this.cb, 0, this.defaultCharBufferSize);
                    if (this.mySize < 0) break;
                    if (this.mySize == 0) {
                        System.err.println(" DocumentParser::skipToCloseAngle() !!!ERROR !!! source.read(...) == 0");
                        break;
                    }
                    this.myCount = 0;
                }
                catch (CharConversionException var2_3) {
                    throw var2_3;
                }
                catch (IOException var3_4) {
                    break;
                }
            }
            c = this.cb[this.myCount++];
        } while (true);
        this.generateError(0);
    }

    protected void generateError(int n) {
        String string = this.buffer.extract(n);
        this.buffer.reset(n);
        this.buffer.flush(this);
        this.errorString(string);
    }

    protected void commentEOFError(int n) {
        this.eofError();
    }

    protected void eofError() {
        this.error("Unexpected end of file");
        this.generateError(0);
    }

    void error(String string) {
        System.err.println("DocumentParser Error: " + string);
    }
}

