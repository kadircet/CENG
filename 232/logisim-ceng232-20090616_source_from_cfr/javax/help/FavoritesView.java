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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Stack;
import java.util.Vector;
import javax.help.FavoritesItem;
import javax.help.FavoritesNode;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.JHelpFavoritesNavigator;
import javax.help.NavigatorView;
import javax.help.TreeItem;
import javax.help.TreeItemFactory;
import javax.swing.tree.DefaultMutableTreeNode;

public class FavoritesView
extends NavigatorView {
    public static final String publicIDString = "-//Sun Microsystems Inc.//DTD JavaHelp Favorites Version 2.0//EN";
    private static boolean warningOfFailures = false;
    private HelpSet hs;
    private boolean enabledSave = true;
    private static final boolean debug = false;

    public FavoritesView(HelpSet helpSet, String string, String string2, Hashtable hashtable) {
        super(helpSet, string, string2, helpSet.getLocale(), hashtable);
    }

    public FavoritesView(HelpSet helpSet, String string, String string2, Locale locale, Hashtable hashtable) {
        super(helpSet, string, string2, locale, hashtable);
    }

    public Component createNavigator(HelpModel helpModel) {
        return new JHelpFavoritesNavigator(this, helpModel);
    }

    public String getMergeType() {
        String string = super.getMergeType();
        if (string == null) {
            return "javax.help.NoMerge";
        }
        return string;
    }

    public FavoritesNode getDataAsTree() {
        HelpSet helpSet = this.getHelpSet();
        FavoritesView.debug("helpSet in " + this + helpSet.toString());
        return this.parse(helpSet, helpSet.getLocale(), new DefaultFavoritesFactory());
    }

    public FavoritesNode parse(HelpSet helpSet, Locale locale, TreeItemFactory treeItemFactory) {
        FavoritesNode favoritesNode = null;
        URL uRL = null;
        try {
            String string = System.getProperty("user.home");
            File file = new File(string + File.separator + ".JavaHelp" + File.separator + "Favorites.xml");
            if (!file.exists()) {
                return new FavoritesNode(new FavoritesItem("Favorites"));
            }
            try {
                uRL = file.toURL();
            }
            catch (MalformedURLException var9_9) {
                System.err.println(var9_9);
            }
            URLConnection uRLConnection = uRL.openConnection();
            Reader reader = XmlReader.createReader(uRLConnection);
            treeItemFactory.parsingStarted(uRL);
            favoritesNode = new FavoritesParser(treeItemFactory).parse(reader, helpSet, locale);
            reader.close();
        }
        catch (Exception var7_7) {
            treeItemFactory.reportMessage("Exception caught while parsing " + uRL + var7_7.toString(), false);
        }
        return (FavoritesNode)treeItemFactory.parsingEnded(favoritesNode);
    }

    public void saveFavorites(FavoritesNode favoritesNode) {
        if (!this.enabledSave) {
            return;
        }
        try {
            String string = System.getProperty("user.home");
            File file = new File(string + File.separator + ".JavaHelp");
            file.mkdirs();
            String string2 = file.getPath() + File.separator + "Favorites.xml";
            FavoritesView.debug("new file:" + string2);
            FileOutputStream fileOutputStream = new FileOutputStream(string2);
            favoritesNode.export(fileOutputStream);
            fileOutputStream.close();
        }
        catch (SecurityException var2_7) {
            this.enabledSave = false;
            var2_7.printStackTrace();
        }
        catch (Exception var3_3) {
            var3_3.printStackTrace();
        }
    }

    private static void debug(String string) {
    }

    private static class FavoritesParser
    implements ParserListener {
        private HelpSet currentParseHS;
        private Stack nodeStack;
        private Stack itemStack;
        private Stack tagStack;
        private Locale defaultLocale;
        private Locale lastLocale;
        private boolean startedfavorites;
        private TreeItemFactory factory;

        FavoritesParser(TreeItemFactory treeItemFactory) {
            this.factory = treeItemFactory;
        }

        synchronized FavoritesNode parse(Reader reader, HelpSet helpSet, Locale locale) throws IOException {
            this.nodeStack = new Stack();
            this.itemStack = new Stack();
            this.tagStack = new Stack();
            this.defaultLocale = locale == null ? Locale.getDefault() : locale;
            this.lastLocale = this.defaultLocale;
            FavoritesNode favoritesNode = new FavoritesNode(new FavoritesItem("Favorites"));
            this.nodeStack.push(favoritesNode);
            this.currentParseHS = helpSet;
            Parser parser = new Parser(reader);
            parser.addParserListener(this);
            parser.parse();
            return favoritesNode;
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
            if (tag.name.equals("favoriteitem")) {
                Object object2;
                if (!this.startedfavorites) {
                    this.factory.reportMessage(HelpUtilities.getText("favorites.invalidFavoritesFormat"), false);
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
                    object = this.factory.createItem("favoriteitem", (Hashtable)var6_8, this.currentParseHS, locale);
                }
                catch (Exception var6_9) {
                    if (warningOfFailures) {
                        object2 = null;
                        if (tagProperties != null) {
                            object2 = tagProperties.getProperty("target");
                        }
                        System.err.println("Failure in FavoritesItem Creation; ");
                        System.err.println("  id: " + (String)object2);
                        System.err.println("  hs: " + this.currentParseHS);
                        var6_9.printStackTrace();
                    }
                    FavoritesView.debug("empty item !");
                    object = this.factory.createItem();
                }
                FavoritesNode favoritesNode = new FavoritesNode((FavoritesItem)object);
                object2 = (FavoritesNode)this.nodeStack.peek();
                object2.add(favoritesNode);
                if (!tag.isEmpty) {
                    this.itemStack.push(object);
                    this.nodeStack.push(favoritesNode);
                    this.addTag(tag, locale);
                }
            } else if (tag.name.equals("favorites")) {
                if (!tag.isEnd) {
                    if (tagProperties != null && (object = tagProperties.getProperty("version")) != null && object.compareTo("2.0") != 0) {
                        this.factory.reportMessage(HelpUtilities.getText("favorites.unknownVersion", (String)object), false);
                    }
                    if (this.startedfavorites) {
                        this.factory.reportMessage(HelpUtilities.getText("favorites.invalidFavoritesFormat"), false);
                    }
                    this.startedfavorites = true;
                    this.addTag(tag, locale);
                } else {
                    if (this.startedfavorites) {
                        this.startedfavorites = false;
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
            if (tag.name.equals("favoriteitem")) {
                FavoritesItem favoritesItem = (FavoritesItem)this.itemStack.peek();
                String string = favoritesItem.getName();
                if (string == null) {
                    favoritesItem.setName(parserEvent.getText().trim());
                } else {
                    favoritesItem.setName(string.concat(parserEvent.getText()).trim());
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

    public static class DefaultFavoritesFactory
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
            if (string2 == null || !string2.equals("-//Sun Microsystems Inc.//DTD JavaHelp Favorites Version 2.0//EN")) {
                this.reportMessage(HelpUtilities.getText("favorites.invalidFavoritesFormat", string2), false);
            }
        }

        public void processPI(HelpSet helpSet, String string, String string2) {
        }

        public TreeItem createItem(String string, Hashtable hashtable, HelpSet helpSet, Locale locale) {
            if (string == null || !string.equals("favoriteitem")) {
                throw new IllegalArgumentException("tagName");
            }
            FavoritesItem favoritesItem = null;
            String string2 = null;
            String string3 = null;
            Object var8_8 = null;
            String string4 = null;
            String string5 = null;
            if (hashtable != null) {
                string2 = (String)hashtable.get("target");
                FavoritesView.debug("target:" + string2);
                string3 = (String)hashtable.get("text");
                string4 = (String)hashtable.get("url");
                string5 = (String)hashtable.get("hstitle");
                favoritesItem = new FavoritesItem(string3, string2, string4, string5, locale);
                if (favoritesItem.getTarget() == null && favoritesItem.getURLSpec() == null) {
                    favoritesItem.setAsFolder();
                }
            } else {
                favoritesItem = new FavoritesItem();
            }
            return favoritesItem;
        }

        public TreeItem createItem() {
            FavoritesView.debug("empty item created");
            return new FavoritesItem();
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

