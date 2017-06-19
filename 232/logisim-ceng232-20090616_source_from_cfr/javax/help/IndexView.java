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
import javax.help.IndexItem;
import javax.help.JHelpIndexNavigator;
import javax.help.Map;
import javax.help.NavigatorView;
import javax.help.TreeItem;
import javax.help.TreeItemFactory;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public class IndexView
extends NavigatorView {
    public static final String publicIDString = "-//Sun Microsystems Inc.//DTD JavaHelp Index Version 1.0//EN";
    public static final String publicIDString_V2 = "-//Sun Microsystems Inc.//DTD JavaHelp Index Version 2.0//EN";
    private static boolean warningOfFailures = false;
    private static final boolean debug = false;

    public IndexView(HelpSet helpSet, String string, String string2, Hashtable hashtable) {
        super(helpSet, string, string2, helpSet.getLocale(), hashtable);
    }

    public IndexView(HelpSet helpSet, String string, String string2, Locale locale, Hashtable hashtable) {
        super(helpSet, string, string2, locale, hashtable);
    }

    public Component createNavigator(HelpModel helpModel) {
        return new JHelpIndexNavigator(this, helpModel);
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
        IndexView.debug("helpSet in " + this + helpSet.toString());
        Hashtable hashtable = this.getParameters();
        if (hashtable == null || hashtable != null && !hashtable.containsKey("data")) {
            DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode();
            return defaultMutableTreeNode;
        }
        try {
            uRL = new URL(helpSet.getHelpSetURL(), (String)hashtable.get("data"));
        }
        catch (Exception var4_4) {
            throw new Error("Trouble getting URL to Index data; " + var4_4);
        }
        IndexView.debug("url,hs: " + uRL.toString() + ";" + helpSet.toString());
        return IndexView.parse(uRL, helpSet, helpSet.getLocale(), new DefaultIndexFactory());
    }

    public static DefaultMutableTreeNode parse(URL uRL, HelpSet helpSet, Locale locale, TreeItemFactory treeItemFactory) {
        DefaultMutableTreeNode defaultMutableTreeNode = null;
        try {
            URLConnection uRLConnection = uRL.openConnection();
            Reader reader = XmlReader.createReader(uRLConnection);
            treeItemFactory.parsingStarted(uRL);
            defaultMutableTreeNode = new IndexParser(treeItemFactory).parse(reader, helpSet, locale);
            reader.close();
        }
        catch (Exception var6_6) {
            treeItemFactory.reportMessage("Exception caught while parsing " + uRL + var6_6.toString(), false);
        }
        return treeItemFactory.parsingEnded(defaultMutableTreeNode);
    }

    private static void debug(String string) {
    }

    private static class IndexParser
    implements ParserListener {
        private HelpSet currentParseHS;
        private Stack nodeStack;
        private Stack itemStack;
        private Stack tagStack;
        private Locale defaultLocale;
        private Locale lastLocale;
        private boolean startedindex;
        private TreeItemFactory factory;

        IndexParser(TreeItemFactory treeItemFactory) {
            this.factory = treeItemFactory;
        }

        synchronized DefaultMutableTreeNode parse(Reader reader, HelpSet helpSet, Locale locale) throws IOException {
            this.nodeStack = new Stack();
            this.itemStack = new Stack();
            this.tagStack = new Stack();
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
            TagProperties tagProperties = tag.atts;
            if (tagProperties != null) {
                object = tagProperties.getProperty("xml:lang");
                locale = HelpUtilities.localeFromLang((String)object);
            }
            if (locale == null) {
                locale = this.lastLocale;
            }
            if (tag.name.equals("indexitem")) {
                Object object2;
                if (!this.startedindex) {
                    this.factory.reportMessage(HelpUtilities.getText("index.invalidIndexFormat"), false);
                }
                if (tag.isEnd && !tag.isEmpty) {
                    this.nodeStack.pop();
                    this.itemStack.pop();
                    this.removeTag(tag);
                    return;
                }
                object = null;
                try {
                    void var6_8;
                    Object hashtable = null;
                    if (tagProperties != null) {
                        Hashtable hashtable2 = tagProperties.getHashtable();
                    }
                    object = this.factory.createItem("indexitem", (Hashtable)var6_8, this.currentParseHS, locale);
                }
                catch (Exception var6_9) {
                    if (warningOfFailures) {
                        object2 = null;
                        if (tagProperties != null) {
                            object2 = tagProperties.getProperty("target");
                        }
                        System.err.println("Failure in IndexItem Creation; ");
                        System.err.println("  id: " + (String)object2);
                        System.err.println("  hs: " + this.currentParseHS);
                    }
                    object = this.factory.createItem();
                }
                if (!this.itemStack.empty()) {
                    IndexItem indexItem = (IndexItem)this.itemStack.peek();
                    if (object.getExpansionType() == -1 && indexItem != null && indexItem.getExpansionType() != -1) {
                        object.setExpansionType(indexItem.getExpansionType());
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
            } else if (tag.name.equals("index")) {
                if (!tag.isEnd) {
                    if (tagProperties != null && (object = tagProperties.getProperty("version")) != null && object.compareTo("1.0") != 0 && object.compareTo("2.0") != 0) {
                        this.factory.reportMessage(HelpUtilities.getText("index.unknownVersion", (String)object), false);
                    }
                    if (this.startedindex) {
                        this.factory.reportMessage(HelpUtilities.getText("index.invalidIndexFormat"), false);
                    }
                    this.startedindex = true;
                    this.addTag(tag, locale);
                } else {
                    if (this.startedindex) {
                        this.startedindex = false;
                    }
                    this.removeTag(tag);
                }
                return;
            }
        }

        public void piFound(ParserEvent parserEvent) {
        }

        public void doctypeFound(ParserEvent parserEvent) {
            this.factory.processDOCTYPE(parserEvent.getRoot(), parserEvent.getPublicId(), parserEvent.getSystemId());
        }

        public void textFound(ParserEvent parserEvent) {
            if (this.tagStack.empty()) {
                return;
            }
            LangElement langElement = (LangElement)this.tagStack.peek();
            Tag tag = langElement.getTag();
            if (tag.name.equals("indexitem")) {
                IndexItem indexItem = (IndexItem)this.itemStack.peek();
                String string = indexItem.getName();
                if (string == null) {
                    indexItem.setName(parserEvent.getText().trim());
                } else {
                    indexItem.setName(string.concat(parserEvent.getText()).trim());
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
                if (langElement.getTag().name.equals(string)) continue;
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

    public static class DefaultIndexFactory
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
            if (string2 == null || string2.compareTo("-//Sun Microsystems Inc.//DTD JavaHelp Index Version 1.0//EN") != 0 && string2.compareTo("-//Sun Microsystems Inc.//DTD JavaHelp Index Version 2.0//EN") != 0) {
                this.reportMessage(HelpUtilities.getText("index.invalidIndexFormat", string2), false);
            }
        }

        public void processPI(HelpSet helpSet, String string, String string2) {
        }

        public TreeItem createItem(String string, Hashtable hashtable, HelpSet helpSet, Locale locale) {
            if (string == null || !string.equals("indexitem")) {
                throw new IllegalArgumentException("tagName");
            }
            IndexItem indexItem = null;
            String string2 = null;
            String string3 = null;
            String string4 = null;
            String string5 = null;
            String string6 = null;
            String string7 = null;
            if (hashtable != null) {
                string2 = (String)hashtable.get("target");
                string3 = (String)hashtable.get("text");
                string4 = (String)hashtable.get("mergetype");
                string5 = (String)hashtable.get("expand");
                string6 = (String)hashtable.get("presentationtype");
                string7 = (String)hashtable.get("presentationname");
            }
            try {
                indexItem = new IndexItem(Map.ID.create(string2, helpSet), helpSet, locale);
            }
            catch (BadIDException var12_12) {
                indexItem = new IndexItem();
            }
            if (string3 != null) {
                indexItem.setName(string3);
            }
            if (string4 != null) {
                indexItem.setMergeType(string4);
            }
            if (string5 != null) {
                if (string5.equals("true")) {
                    indexItem.setExpansionType(1);
                } else if (string5.equals("false")) {
                    indexItem.setExpansionType(0);
                }
            }
            if (string6 != null) {
                indexItem.setPresentation(string6);
            }
            if (string7 != null) {
                indexItem.setPresentationName(string7);
            }
            return indexItem;
        }

        public TreeItem createItem() {
            return new IndexItem();
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

