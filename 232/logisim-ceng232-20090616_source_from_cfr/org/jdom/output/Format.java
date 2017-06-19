/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.output;

import java.lang.reflect.Method;
import org.jdom.output.EscapeStrategy;

public class Format
implements Cloneable {
    private static final String CVS_ID = "@(#) $RCSfile: Format.java,v $ $Revision: 1.10 $ $Date: 2004/09/07 06:37:20 $ $Name: jdom_1_0 $";
    private static final String STANDARD_INDENT = "  ";
    private static final String STANDARD_LINE_SEPARATOR = "\r\n";
    private static final String STANDARD_ENCODING = "UTF-8";
    String indent = null;
    String lineSeparator = "\r\n";
    String encoding = "UTF-8";
    boolean omitDeclaration = false;
    boolean omitEncoding = false;
    boolean expandEmptyElements = false;
    boolean ignoreTrAXEscapingPIs = false;
    TextMode mode = TextMode.PRESERVE;
    EscapeStrategy escapeStrategy;
    static /* synthetic */ Class class$java$lang$String;

    private Format() {
        this.escapeStrategy = new DefaultEscapeStrategy(this.encoding);
    }

    static /* synthetic */ Class class$(String class$) {
        try {
            return Class.forName(class$);
        }
        catch (ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }

    protected Object clone() {
        Format format;
        format = null;
        try {
            format = (Format)super.clone();
        }
        catch (CloneNotSupportedException v0) {}
        return format;
    }

    public static Format getCompactFormat() {
        Format f = new Format();
        f.setTextMode(TextMode.NORMALIZE);
        return f;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public EscapeStrategy getEscapeStrategy() {
        return this.escapeStrategy;
    }

    public boolean getExpandEmptyElements() {
        return this.expandEmptyElements;
    }

    public boolean getIgnoreTrAXEscapingPIs() {
        return this.ignoreTrAXEscapingPIs;
    }

    public String getIndent() {
        return this.indent;
    }

    public String getLineSeparator() {
        return this.lineSeparator;
    }

    public boolean getOmitDeclaration() {
        return this.omitDeclaration;
    }

    public boolean getOmitEncoding() {
        return this.omitEncoding;
    }

    public static Format getPrettyFormat() {
        Format f = new Format();
        f.setIndent("  ");
        f.setTextMode(TextMode.TRIM);
        return f;
    }

    public static Format getRawFormat() {
        return new Format();
    }

    public TextMode getTextMode() {
        return this.mode;
    }

    public Format setEncoding(String encoding) {
        this.encoding = encoding;
        this.escapeStrategy = new DefaultEscapeStrategy(encoding);
        return this;
    }

    public Format setEscapeStrategy(EscapeStrategy strategy) {
        this.escapeStrategy = strategy;
        return this;
    }

    public Format setExpandEmptyElements(boolean expandEmptyElements) {
        this.expandEmptyElements = expandEmptyElements;
        return this;
    }

    public void setIgnoreTrAXEscapingPIs(boolean ignoreTrAXEscapingPIs) {
        this.ignoreTrAXEscapingPIs = ignoreTrAXEscapingPIs;
    }

    public Format setIndent(String indent) {
        if ("".equals(indent)) {
            indent = null;
        }
        this.indent = indent;
        return this;
    }

    public Format setLineSeparator(String separator) {
        this.lineSeparator = separator;
        return this;
    }

    public Format setOmitDeclaration(boolean omitDeclaration) {
        this.omitDeclaration = omitDeclaration;
        return this;
    }

    public Format setOmitEncoding(boolean omitEncoding) {
        this.omitEncoding = omitEncoding;
        return this;
    }

    public Format setTextMode(TextMode mode) {
        this.mode = mode;
        return this;
    }

    class DefaultEscapeStrategy
    implements EscapeStrategy {
        private int bits;
        Object encoder;
        Method canEncode;

        public DefaultEscapeStrategy(String encoding) {
            if ("UTF-8".equalsIgnoreCase(encoding) || "UTF-16".equalsIgnoreCase(encoding)) {
                this.bits = 16;
            } else if ("ISO-8859-1".equalsIgnoreCase(encoding) || "Latin1".equalsIgnoreCase(encoding)) {
                this.bits = 8;
            } else if ("US-ASCII".equalsIgnoreCase(encoding) || "ASCII".equalsIgnoreCase(encoding)) {
                this.bits = 7;
            } else {
                this.bits = 0;
                try {
                    Class charsetClass = Class.forName("java.nio.charset.Charset");
                    Class encoderClass = Class.forName("java.nio.charset.CharsetEncoder");
                    Class[] arrclass = new Class[1];
                    Class class_ = Format.class$java$lang$String != null ? Format.class$java$lang$String : (Format.class$java$lang$String = Format.class$("java.lang.String"));
                    arrclass[0] = class_;
                    Method forName = charsetClass.getMethod("forName", arrclass);
                    Object charsetObj = forName.invoke(null, encoding);
                    Method newEncoder = charsetClass.getMethod("newEncoder", null);
                    this.encoder = newEncoder.invoke(charsetObj, null);
                    this.canEncode = encoderClass.getMethod("canEncode", Character.TYPE);
                }
                catch (Exception v2) {}
            }
        }

        public boolean shouldEscape(char ch) {
            if (this.bits == 16) {
                return false;
            }
            if (this.bits == 8) {
                if (ch > '\u00ff') {
                    return true;
                }
                return false;
            }
            if (this.bits == 7) {
                if (ch > '') {
                    return true;
                }
                return false;
            }
            if (this.canEncode != null && this.encoder != null) {
                try {
                    Boolean val = (Boolean)this.canEncode.invoke(this.encoder, new Character(ch));
                    return val ^ true;
                }
                catch (Exception v0) {}
            }
            return false;
        }
    }

    public static class TextMode {
        public static final TextMode PRESERVE = new TextMode("PRESERVE");
        public static final TextMode TRIM = new TextMode("TRIM");
        public static final TextMode NORMALIZE = new TextMode("NORMALIZE");
        public static final TextMode TRIM_FULL_WHITE = new TextMode("TRIM_FULL_WHITE");
        private final String name;

        private TextMode(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

}

