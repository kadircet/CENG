/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import com.sun.java.help.impl.LangElement;
import com.sun.java.help.impl.Parser;
import com.sun.java.help.impl.ParserEvent;
import com.sun.java.help.impl.ParserListener;
import com.sun.java.help.impl.Tag;
import com.sun.java.help.impl.TagProperties;
import com.sun.java.help.impl.XmlReader;
import java.awt.Component;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Stack;
import java.util.Vector;
import javax.help.BadIDException;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.JHelpTOCNavigator;
import javax.help.Map;
import javax.help.NavigatorView;
import javax.help.TOCItem;
import javax.help.TreeItem;
import javax.help.TreeItemFactory;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public class TOCView
extends NavigatorView {
    public static final String publicIDString = "-//Sun Microsystems Inc.//DTD JavaHelp TOC Version 1.0//EN";
    public static final String publicIDString_V2 = "-//Sun Microsystems Inc.//DTD JavaHelp TOC Version 2.0//EN";
    private Map.ID categoryOpenImageID = null;
    private Map.ID categoryClosedImageID = null;
    private Map.ID topicImageID = null;
    private static boolean warningOfFailures = false;
    private static final boolean debug = false;

    public TOCView(HelpSet helpSet, String string, String string2, Hashtable hashtable) {
        this(helpSet, string, string2, helpSet.getLocale(), hashtable);
    }

    public TOCView(HelpSet helpSet, String string, String string2, Locale locale, Hashtable hashtable) {
        super(helpSet, string, string2, locale, hashtable);
    }

    public Component createNavigator(HelpModel helpModel) {
        return new JHelpTOCNavigator(this, helpModel);
    }

    public String getMergeType() {
        String string = super.getMergeType();
        if (string == null) {
            return "javax.help.AppendMerge";
        }
        return string;
    }

    public DefaultMutableTreeNode getDataAsTree() {
        URL uRL;
        HelpSet helpSet = this.getHelpSet();
        Hashtable hashtable = this.getParameters();
        if (hashtable == null || hashtable != null && !hashtable.containsKey("data")) {
            DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode();
            return defaultMutableTreeNode;
        }
        try {
            uRL = new URL(helpSet.getHelpSetURL(), (String)hashtable.get("data"));
        }
        catch (Exception var4_4) {
            throw new Error("Trouble getting URL to TOC data; " + var4_4);
        }
        return TOCView.parse(uRL, helpSet, helpSet.getLocale(), new DefaultTOCFactory(), this);
    }

    public static DefaultMutableTreeNode parse(URL uRL, HelpSet helpSet, Locale locale, TreeItemFactory treeItemFactory) {
        return TOCView.parse(uRL, helpSet, locale, treeItemFactory, null);
    }

    public static DefaultMutableTreeNode parse(URL uRL, HelpSet helpSet, Locale locale, TreeItemFactory treeItemFactory, TOCView tOCView) {
        DefaultMutableTreeNode defaultMutableTreeNode = null;
        try {
            URLConnection uRLConnection = uRL.openConnection();
            Reader reader = XmlReader.createReader(uRLConnection);
            treeItemFactory.parsingStarted(uRL);
            TOCParser tOCParser = new TOCParser(treeItemFactory, tOCView);
            defaultMutableTreeNode = tOCParser.parse(reader, helpSet, locale);
            reader.close();
        }
        catch (Exception var7_7) {
            treeItemFactory.reportMessage("Exception caught while parsing " + uRL + var7_7.toString(), false);
        }
        return treeItemFactory.parsingEnded(defaultMutableTreeNode);
    }

    public void setCategoryOpenImageID(String string) {
        if (string == null) {
            return;
        }
        try {
            this.categoryOpenImageID = Map.ID.create(string, this.getHelpSet());
        }
        catch (BadIDException var2_2) {
            // empty catch block
        }
    }

    public Map.ID getCategoryOpenImageID() {
        if (this.categoryOpenImageID == null) {
            return this.categoryClosedImageID;
        }
        return this.categoryOpenImageID;
    }

    public void setCategoryClosedImageID(String string) {
        if (string == null) {
            return;
        }
        try {
            this.categoryClosedImageID = Map.ID.create(string, this.getHelpSet());
        }
        catch (BadIDException var2_2) {
            // empty catch block
        }
    }

    public Map.ID getCategoryClosedImageID() {
        return this.categoryClosedImageID;
    }

    public void setTopicImageID(String string) {
        if (string == null) {
            this.topicImageID = null;
            return;
        }
        try {
            this.topicImageID = Map.ID.create(string, this.getHelpSet());
        }
        catch (BadIDException var2_2) {
            // empty catch block
        }
    }

    public Map.ID getTopicImageID() {
        return this.topicImageID;
    }

    private static void debug(String string) {
    }

    private static class TOCParser
    implements ParserListener {
        private HelpSet currentParseHS;
        private Stack nodeStack;
        private Stack itemStack;
        private boolean startedtoc;
        private Stack tagStack;
        private Locale defaultLocale;
        private Locale lastLocale;
        private TreeItemFactory factory;
        private TOCView tocView;

        TOCParser(TreeItemFactory treeItemFactory, TOCView tOCView) {
            this.factory = treeItemFactory;
            this.tocView = tOCView;
        }

        synchronized DefaultMutableTreeNode parse(Reader reader, HelpSet helpSet, Locale locale) throws IOException {
            this.nodeStack = new Stack();
            this.tagStack = new Stack();
            this.itemStack = new Stack();
            this.defaultLocale = locale == null ? Locale.getDefault() : locale;
            this.lastLocale = this.defaultLocale;
            DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode();
            this.nodeStack.push(defaultMutableTreeNode);
            this.currentParseHS = helpSet;
            Parser parser = new Parser(reader);
            parser.addParserListener(this);
            parser.parse();
            return defaultMutableTreeNode;
        }

        public void tagFound(ParserEvent parserEvent) {
            Object object;
            Locale locale = null;
            Tag tag = parserEvent.getTag();
            TOCView.debug("TagFound: " + tag.name);
            TagProperties tagProperties = tag.atts;
            if (tagProperties != null) {
                object = tagProperties.getProperty("xml:lang");
                locale = HelpUtilities.localeFromLang((String)object);
            }
            if (locale == null) {
                locale = this.lastLocale;
            }
            if (tag.name.equals("tocitem")) {
                Object object2;
                if (!this.startedtoc) {
                    this.factory.reportMessage(HelpUtilities.getText("toc.invalidTOCFormat"), false);
                }
                if (tag.isEnd && !tag.isEmpty) {
                    this.nodeStack.pop();
                    this.itemStack.pop();
                    this.removeTag(tag);
                    return;
                }
                try {
                    void var6_8;
                    Object hashtable = null;
                    if (tagProperties != null) {
                        Hashtable hashtable2 = tagProperties.getHashtable();
                    }
                    object = (TOCItem)this.factory.createItem("tocitem", (Hashtable)var6_8, this.currentParseHS, locale);
                }
                catch (Exception var6_9) {
                    if (warningOfFailures) {
                        object2 = null;
                        String string = null;
                        if (tagProperties != null) {
                            object2 = tagProperties.getProperty("target");
                            string = tagProperties.getProperty("image");
                        }
                        System.err.println("Failure in IndexItem Creation; ");
                        System.err.println("  id: " + (String)object2);
                        System.err.println("  hs: " + this.currentParseHS);
                    }
                    object = (TOCItem)this.factory.createItem();
                }
                if (!this.itemStack.empty()) {
                    TOCItem tOCItem = (TOCItem)this.itemStack.peek();
                    if (object.getExpansionType() == -1 && tOCItem != null && tOCItem.getExpansionType() != -1) {
                        object.setExpansionType(tOCItem.getExpansionType());
                    }
                }
                DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(object);
                object2 = (DefaultMutableTreeNode)this.nodeStack.peek();
                object2.add(defaultMutableTreeNode);
                if (!tag.isEmpty) {
                    this.itemStack.push(object);
                    this.nodeStack.push(defaultMutableTreeNode);
                    this.addTag(tag, locale);
                }
            } else if (tag.name.equals("toc")) {
                TOCView.debug("attr: " + tagProperties);
                if (!tag.isEnd) {
                    if (tagProperties != null) {
                        object = tagProperties.getProperty("version");
                        if (object != null && object.compareTo("1.0") != 0 && object.compareTo("2.0") != 0) {
                            this.factory.reportMessage(HelpUtilities.getText("toc.unknownVersion", (String)object), false);
                        }
                        if (this.tocView != null) {
                            this.tocView.setCategoryOpenImageID(tagProperties.getProperty("categoryopenimage"));
                            this.tocView.setCategoryClosedImageID(tagProperties.getProperty("categoryclosedimage"));
                            this.tocView.setTopicImageID(tagProperties.getProperty("topicimage"));
                        }
                    }
                    if (this.startedtoc) {
                        this.factory.reportMessage(HelpUtilities.getText("toc.invalidTOCFormat"), false);
                    }
                    this.startedtoc = true;
                    this.addTag(tag, locale);
                } else {
                    if (this.startedtoc) {
                        this.startedtoc = false;
                    }
                    this.removeTag(tag);
                }
                return;
            }
        }

        public void piFound(ParserEvent parserEvent) {
        }

        public void doctypeFound(ParserEvent parserEvent) {
        }

        public void textFound(ParserEvent parserEvent) {
            TOCView.debug("TextFound: " + parserEvent.getText().trim());
            if (this.tagStack.empty()) {
                return;
            }
            LangElement langElement = (LangElement)this.tagStack.peek();
            Tag tag = langElement.getTag();
            if (tag.name.equals("tocitem")) {
                TOCItem tOCItem = (TOCItem)this.itemStack.peek();
                String string = tOCItem.getName();
                if (string == null) {
                    tOCItem.setName(parserEvent.getText().trim());
                } else {
                    tOCItem.setName(string.concat(parserEvent.getText()).trim());
                }
            }
        }

        public void commentFound(ParserEvent parserEvent) {
        }

        public void errorFound(ParserEvent parserEvent) {
            this.factory.reportMessage(parserEvent.getText(), false);
        }

        protected void addTag(Tag tag, Locale locale) {
            LangElement langElement = new LangElement(tag, locale);
            this.tagStack.push(langElement);
            if (this.lastLocale == null) {
                this.lastLocale = locale;
                return;
            }
            if (locale == null) {
                this.lastLocale = locale;
                return;
            }
            if (!this.lastLocale.equals(locale)) {
                this.lastLocale = locale;
            }
        }

        protected void removeTag(Tag tag) {
            String string = tag.name;
            Locale locale = null;
            while (!this.tagStack.empty()) {
                LangElement langElement = (LangElement)this.tagStack.pop();
                if (!langElement.getTag().name.equals(string)) continue;
                if (this.tagStack.empty()) {
                    locale = this.defaultLocale;
                    break;
                }
                langElement = (LangElement)this.tagStack.peek();
                locale = langElement.getLocale();
                break;
            }
            if (this.lastLocale == null) {
                this.lastLocale = locale;
                return;
            }
            if (locale == null) {
                this.lastLocale = locale;
                return;
            }
            if (!this.lastLocale.equals(locale)) {
                this.lastLocale = locale;
            }
        }
    }

    public static class DefaultTOCFactory
    implements TreeItemFactory {
        private Vector messages = new Vector();
        private URL source;
        private boolean validParse = true;

        public void parsingStarted(URL uRL) {
            if (uRL == null) {
                throw new NullPointerException("source");
            }
            this.source = uRL;
        }

        public void processDOCTYPE(String string, String string2, String string3) {
            if (string2 == null || string2.compareTo("-//Sun Microsystems Inc.//DTD JavaHelp TOC Version 1.0//EN") != 0 && string2.compareTo("-//Sun Microsystems Inc.//DTD JavaHelp TOC Version 2.0//EN") != 0) {
                this.reportMessage(HelpUtilities.getText("toc.wrongPublicID", string2), false);
            }
        }

        public void processPI(HelpSet helpSet, String string, String string2) {
        }

        public TreeItem createItem(String string, Hashtable hashtable, HelpSet helpSet, Locale locale) {
            if (string == null || !string.equals("tocitem")) {
                throw new IllegalArgumentException("tagName");
            }
            TOCItem tOCItem = null;
            String string2 = null;
            String string3 = null;
            String string4 = null;
            String string5 = null;
            String string6 = null;
            String string7 = null;
            String string8 = null;
            if (hashtable != null) {
                string2 = (String)hashtable.get("target");
                string3 = (String)hashtable.get("image");
                string4 = (String)hashtable.get("text");
                string5 = (String)hashtable.get("mergetype");
                string6 = (String)hashtable.get("expand");
                string7 = (String)hashtable.get("presentationtype");
                string8 = (String)hashtable.get("presentationname");
            }
            Map.ID iD = null;
            Map.ID iD2 = null;
            try {
                iD = Map.ID.create(string2, helpSet);
            }
            catch (BadIDException var15_15) {
                // empty catch block
            }
            try {
                iD2 = Map.ID.create(string3, helpSet);
            }
            catch (BadIDException var15_16) {
                // empty catch block
            }
            tOCItem = new TOCItem(iD, iD2, helpSet, locale);
            if (string4 != null) {
                tOCItem.setName(string4);
            }
            if (string5 != null) {
                tOCItem.setMergeType(string5);
            }
            if (string6 != null) {
                if (string6.equals("true")) {
                    tOCItem.setExpansionType(1);
                } else if (string6.equals("false")) {
                    tOCItem.setExpansionType(0);
                }
            }
            if (string7 != null) {
                tOCItem.setPresentation(string7);
            }
            if (string8 != null) {
                tOCItem.setPresentationName(string8);
            }
            return tOCItem;
        }

        public TreeItem createItem() {
            return new TOCItem();
        }

        public void reportMessage(String string, boolean bl) {
            this.messages.addElement(string);
            this.validParse = this.validParse && bl;
        }

        public Enumeration listMessages() {
            return this.messages.elements();
        }

        public DefaultMutableTreeNode parsingEnded(DefaultMutableTreeNode defaultMutableTreeNode) {
            DefaultMutableTreeNode defaultMutableTreeNode2 = defaultMutableTreeNode;
            if (!this.validParse) {
                defaultMutableTreeNode2 = null;
                System.err.println("Parsing failed for " + this.source);
                Enumeration enumeration = this.messages.elements();
                while (enumeration.hasMoreElements()) {
                    String string = (String)enumeration.nextElement();
                    System.err.println(string);
                }
            }
            return defaultMutableTreeNode2;
        }
    }

}

