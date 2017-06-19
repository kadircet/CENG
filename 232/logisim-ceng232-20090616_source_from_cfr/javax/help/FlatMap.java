/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import com.sun.java.help.impl.Parser;
import com.sun.java.help.impl.ParserEvent;
import com.sun.java.help.impl.ParserListener;
import com.sun.java.help.impl.Tag;
import com.sun.java.help.impl.TagProperties;
import com.sun.java.help.impl.XmlReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.Map;

public class FlatMap
implements Map,
Serializable {
    private URL base;
    private ResourceBundle resource;
    private HelpSet helpset;
    public static final String publicIDString = "-//Sun Microsystems Inc.//DTD JavaHelp Map Version 1.0//EN";
    public static final String publicIDString_V2 = "-//Sun Microsystems Inc.//DTD JavaHelp Map Version 2.0//EN";
    private static final boolean debug = false;

    public FlatMap(URL uRL, HelpSet helpSet) throws IOException {
        FlatMap.debug("Creating FlatMap for: " + uRL);
        Enumeration enumeration = helpSet.getHelpSets();
        if (enumeration.hasMoreElements()) {
            throw new IllegalArgumentException("Cannot create - HelpSet is not flat");
        }
        this.resource = new FlatMapResourceBundle(uRL);
        this.base = uRL;
        this.helpset = helpSet;
    }

    public HelpSet getHelpSet() {
        return this.helpset;
    }

    public boolean isValidID(String string, HelpSet helpSet) {
        FlatMap.debug("isValidID " + string);
        try {
            String string2 = this.resource.getString(string);
        }
        catch (MissingResourceException var3_4) {
            return false;
        }
        return true;
    }

    public Enumeration getAllIDs() {
        return new FlatEnumeration(this.resource.getKeys(), this.helpset);
    }

    public URL getURLFromID(Map.ID iD) throws MalformedURLException {
        FlatMap.debug("getURLFromID(" + iD + ")");
        String string = iD.id;
        HelpSet helpSet = iD.hs;
        if (string == null) {
            return null;
        }
        String string2 = null;
        try {
            string2 = this.resource.getString(string);
            URL uRL = new URL(this.base, string2);
            return uRL;
        }
        catch (MissingResourceException var5_6) {
            return null;
        }
    }

    public boolean isID(URL uRL) {
        Enumeration<String> enumeration = this.resource.getKeys();
        while (enumeration.hasMoreElements()) {
            try {
                String string = enumeration.nextElement();
                URL uRL2 = new URL(this.base, (String)this.resource.getObject(string));
                if (!uRL.sameFile(uRL2)) continue;
                return true;
            }
            catch (Exception var4_5) {
                // empty catch block
            }
        }
        return false;
    }

    public Map.ID getIDFromURL(URL uRL) {
        if (uRL == null) {
            return null;
        }
        String string = uRL.toExternalForm();
        Enumeration<String> enumeration = this.resource.getKeys();
        while (enumeration.hasMoreElements()) {
            String string2 = enumeration.nextElement();
            try {
                String string3;
                String string4 = this.resource.getString(string2);
                URL uRL2 = new URL(this.base, string4);
                if (uRL2 == null || string.compareTo(string3 = uRL2.toExternalForm()) != 0) continue;
                return Map.ID.create(string2, this.helpset);
            }
            catch (Exception var7_8) {
                // empty catch block
            }
        }
        return null;
    }

    public Map.ID getClosestID(URL uRL) {
        return this.getIDFromURL(uRL);
    }

    public Enumeration getIDs(URL uRL) {
        String string = null;
        URL uRL2 = null;
        Vector<String> vector = new Vector<String>();
        Enumeration<String> enumeration = this.resource.getKeys();
        while (enumeration.hasMoreElements()) {
            String string2 = enumeration.nextElement();
            try {
                string = this.resource.getString(string2);
                uRL2 = new URL(this.base, string);
                if (!uRL.sameFile(uRL2)) continue;
                vector.addElement(string2);
                continue;
            }
            catch (Exception var7_7) {
                // empty catch block
            }
        }
        return new FlatEnumeration(vector.elements(), this.helpset);
    }

    private static void debug(String string) {
    }

    protected class FlatMapResourceBundle
    extends ResourceBundle
    implements ParserListener,
    Serializable {
        private Hashtable lookup;
        private boolean startedmap;
        private URL source;
        private Vector messages;
        private boolean validParse;

        public FlatMapResourceBundle(URL uRL) {
            Object object;
            this.lookup = null;
            this.messages = new Vector();
            this.validParse = true;
            this.source = uRL;
            try {
                object = uRL.openConnection();
                Reader reader = XmlReader.createReader((URLConnection)object);
                this.parse(reader);
                reader.close();
            }
            catch (Exception var4_4) {
                this.reportMessage("Exception caught while parsing " + uRL + " " + var4_4.toString(), false);
            }
            this.parsingEnded();
            object = this.lookup.keys();
            while (object.hasMoreElements()) {
                String string = (String)object.nextElement();
                String string2 = (String)this.lookup.get(string);
            }
        }

        public final Object handleGetObject(String string) {
            return this.lookup.get(string);
        }

        public Enumeration getKeys() {
            return this.lookup.keys();
        }

        synchronized void parse(Reader reader) throws IOException {
            this.lookup = new Hashtable(10);
            Parser parser = new Parser(reader);
            parser.addParserListener(this);
            parser.parse();
        }

        public void tagFound(ParserEvent parserEvent) {
            Object var2_2 = null;
            Tag tag = parserEvent.getTag();
            FlatMap.debug("TagFound: " + tag.name);
            TagProperties tagProperties = tag.atts;
            if (tag.name.equals("mapID")) {
                if (!this.startedmap) {
                    this.parsingError("map.invalidMapFormat");
                }
                String string = null;
                String string2 = null;
                if (tagProperties != null) {
                    string = tagProperties.getProperty("target");
                    string2 = tagProperties.getProperty("url");
                }
                if (string == null || string2 == null) {
                    this.reportMessage("Failure in mapID Creation;", true);
                    this.reportMessage("  target: " + string, true);
                    this.reportMessage("  url: " + string2, true);
                    return;
                }
                this.lookup.put(string, string2);
                return;
            }
            if (tag.name.equals("map")) {
                if (!tag.isEnd) {
                    String string;
                    if (tagProperties != null && (string = tagProperties.getProperty("version")) != null && string.compareTo("1.0") != 0 && string.compareTo("2.0") != 0) {
                        this.parsingError("map.unknownVersion", string);
                    }
                    if (this.startedmap) {
                        this.parsingError("map.invalidMapFormat");
                    }
                    this.startedmap = true;
                } else if (this.startedmap) {
                    this.startedmap = false;
                }
                return;
            }
        }

        public void piFound(ParserEvent parserEvent) {
        }

        public void doctypeFound(ParserEvent parserEvent) {
            String string = parserEvent.getPublicId();
            if (string == null || string.compareTo("-//Sun Microsystems Inc.//DTD JavaHelp Map Version 1.0//EN") != 0 && string.compareTo("-//Sun Microsystems Inc.//DTD JavaHelp Map Version 2.0//EN") != 0) {
                this.parsingError("map.wrongPublicID", string);
            }
        }

        public void textFound(ParserEvent parserEvent) {
        }

        public void commentFound(ParserEvent parserEvent) {
        }

        public void errorFound(ParserEvent parserEvent) {
            this.reportMessage(parserEvent.getText(), false);
        }

        public void reportMessage(String string, boolean bl) {
            this.messages.addElement(string);
            this.validParse = this.validParse && bl;
        }

        public Enumeration listMessages() {
            return this.messages.elements();
        }

        private void parsingEnded() {
            if (!this.validParse) {
                if (this.lookup != null) {
                    this.lookup.clear();
                }
                FlatMap.debug("Parsing failed for " + this.source);
                Enumeration enumeration = this.messages.elements();
                while (enumeration.hasMoreElements()) {
                    String string = (String)enumeration.nextElement();
                    FlatMap.debug(string);
                }
            } else {
                this.source = null;
            }
        }

        private void parsingError(String string) {
            String string2 = HelpUtilities.getText(string);
            this.reportMessage(string2, false);
        }

        private void parsingError(String string, String string2) {
            String string3 = HelpUtilities.getText(string, string2);
            this.reportMessage(string3, false);
        }
    }

    private static class FlatEnumeration
    implements Enumeration {
        private Enumeration e;
        private HelpSet hs;

        public FlatEnumeration(Enumeration enumeration, HelpSet helpSet) {
            this.e = enumeration;
            this.hs = helpSet;
        }

        public boolean hasMoreElements() {
            return this.e.hasMoreElements();
        }

        public Object nextElement() {
            Map.ID iD = null;
            try {
                iD = Map.ID.create((String)this.e.nextElement(), this.hs);
            }
            catch (Exception var2_2) {
                // empty catch block
            }
            return iD;
        }
    }

}

