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
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Stack;
import java.util.Vector;
import javax.help.BadIDException;
import javax.help.FlatMap;
import javax.help.HelpAction;
import javax.help.HelpBroker;
import javax.help.HelpSetException;
import javax.help.HelpSetFactory;
import javax.help.HelpUtilities;
import javax.help.Map;
import javax.help.NavigatorView;
import javax.help.TryMap;
import javax.help.event.EventListenerList;
import javax.help.event.HelpSetEvent;
import javax.help.event.HelpSetListener;
import javax.swing.ImageIcon;

public class HelpSet
implements Serializable {
    private static String errorMsg = null;
    protected EventListenerList listenerList = new EventListenerList();
    public static final String publicIDString = "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN";
    public static final String publicIDString_V2 = "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN";
    public static final Object implRegistry = new StringBuffer("HelpSet.implRegistry");
    public static final String helpBrokerClass = "helpBroker/class";
    public static final String helpBrokerLoader = "helpBroker/loader";
    public static final Object kitTypeRegistry = new StringBuffer("JHelpViewer.kitTypeRegistry");
    public static final Object kitLoaderRegistry = new StringBuffer("JHelpViewer.kitLoaderRegistry");
    private String title;
    private Map map;
    private TryMap combinedMap;
    private URL helpset;
    private String homeID;
    private Locale locale = Locale.getDefault();
    private transient ClassLoader loader;
    private Vector views = new Vector();
    private Vector presentations = new Vector();
    private Presentation defaultPresentation = null;
    private Vector helpsets;
    private static HelpBroker defaultHelpBroker;
    private Vector subHelpSets = new Vector();
    private static Hashtable defaultKeys;
    private Hashtable localKeys = new Hashtable();
    private PropertyChangeSupport changes;
    private static final boolean debug = false;
    static /* synthetic */ Class class$javax$help$event$HelpSetListener;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$javax$help$HelpSet;

    public HelpSet(ClassLoader classLoader) {
        this.changes = new PropertyChangeSupport(this);
        this.helpsets = new Vector();
        this.loader = classLoader;
    }

    public HelpSet() {
        this.changes = new PropertyChangeSupport(this);
        this.helpsets = new Vector();
        this.loader = null;
    }

    public HelpSet(ClassLoader classLoader, URL uRL) throws HelpSetException {
        this(classLoader);
        this.helpset = uRL;
        DefaultHelpSetFactory defaultHelpSetFactory = new DefaultHelpSetFactory();
        this.parseInto(uRL, defaultHelpSetFactory);
        HelpSet helpSet = defaultHelpSetFactory.parsingEnded(this);
        if (helpSet == null) {
            throw new HelpSetException("Could not parse\n" + errorMsg);
        }
    }

    public static URL findHelpSet(ClassLoader classLoader, String string, String string2, Locale locale) {
        return HelpUtilities.getLocalizedResource(classLoader, string, string2, locale, true);
    }

    public static URL findHelpSet(ClassLoader classLoader, String string, Locale locale) {
        String string2;
        String string3;
        if (string.endsWith(".hs")) {
            string2 = string.substring(0, string.length() - 3);
            string3 = ".hs";
        } else {
            string2 = string;
            string3 = ".hs";
        }
        return HelpSet.findHelpSet(classLoader, string2, string3, locale);
    }

    public static URL findHelpSet(ClassLoader classLoader, String string) {
        return HelpSet.findHelpSet(classLoader, string, Locale.getDefault());
    }

    public HelpBroker createHelpBroker() {
        return this.createHelpBroker(null);
    }

    public HelpBroker createHelpBroker(String string) {
        Object object;
        HelpBroker helpBroker = null;
        String string2 = (String)this.getKeyData(implRegistry, "helpBroker/class");
        ClassLoader classLoader = (ClassLoader)this.getKeyData(implRegistry, "helpBroker/loader");
        if (classLoader == null) {
            classLoader = this.getLoader();
        }
        try {
            object = classLoader != null ? classLoader.loadClass(string2) : Class.forName(string2);
            helpBroker = (HelpBroker)object.newInstance();
        }
        catch (Throwable var5_6) {
            helpBroker = null;
        }
        if (helpBroker != null) {
            helpBroker.setHelpSet(this);
            object = null;
            object = string != null ? this.getPresentation(string) : this.getDefaultPresentation();
            if (object != null) {
                helpBroker.setHelpSetPresentation((Presentation)object);
            }
        }
        return helpBroker;
    }

    public void add(HelpSet helpSet) {
        HelpSet.debug("add(" + helpSet + ")");
        this.helpsets.addElement(helpSet);
        this.fireHelpSetAdded(this, helpSet);
        this.combinedMap = null;
    }

    public boolean remove(HelpSet helpSet) {
        if (this.helpsets.removeElement(helpSet)) {
            this.fireHelpSetRemoved(this, helpSet);
            this.combinedMap = null;
            return true;
        }
        return false;
    }

    public Enumeration getHelpSets() {
        return this.helpsets.elements();
    }

    public boolean contains(HelpSet helpSet) {
        if (helpSet == this) {
            return true;
        }
        Enumeration enumeration = this.helpsets.elements();
        while (enumeration.hasMoreElements()) {
            HelpSet helpSet2 = (HelpSet)enumeration.nextElement();
            if (!helpSet2.contains(helpSet)) continue;
            return true;
        }
        return false;
    }

    public void addHelpSetListener(HelpSetListener helpSetListener) {
        HelpSet.debug("addHelpSetListener(" + helpSetListener + ")");
        Class class_ = class$javax$help$event$HelpSetListener == null ? (HelpSet.class$javax$help$event$HelpSetListener = HelpSet.class$("javax.help.event.HelpSetListener")) : class$javax$help$event$HelpSetListener;
        this.listenerList.add(class_, helpSetListener);
    }

    public void removeHelpSetListener(HelpSetListener helpSetListener) {
        Class class_ = class$javax$help$event$HelpSetListener == null ? (HelpSet.class$javax$help$event$HelpSetListener = HelpSet.class$("javax.help.event.HelpSetListener")) : class$javax$help$event$HelpSetListener;
        this.listenerList.remove(class_, helpSetListener);
    }

    protected void fireHelpSetAdded(Object object, HelpSet helpSet) {
        Object[] arrobject = this.listenerList.getListenerList();
        HelpSetEvent helpSetEvent = null;
        int n = arrobject.length - 2;
        while (n >= 0) {
            if (arrobject[n] == (class$javax$help$event$HelpSetListener == null ? HelpSet.class$("javax.help.event.HelpSetListener") : class$javax$help$event$HelpSetListener)) {
                if (helpSetEvent == null) {
                    helpSetEvent = new HelpSetEvent(this, helpSet, 0);
                }
                ((HelpSetListener)arrobject[n + 1]).helpSetAdded(helpSetEvent);
            }
            n -= 2;
        }
    }

    protected void fireHelpSetRemoved(Object object, HelpSet helpSet) {
        Object[] arrobject = this.listenerList.getListenerList();
        HelpSetEvent helpSetEvent = null;
        int n = arrobject.length - 2;
        while (n >= 0) {
            if (arrobject[n] == (class$javax$help$event$HelpSetListener == null ? HelpSet.class$("javax.help.event.HelpSetListener") : class$javax$help$event$HelpSetListener)) {
                if (helpSetEvent == null) {
                    helpSetEvent = new HelpSetEvent(this, helpSet, 1);
                }
                ((HelpSetListener)arrobject[n + 1]).helpSetRemoved(helpSetEvent);
            }
            n -= 2;
        }
    }

    public String getTitle() {
        if (this.title == null) {
            return "";
        }
        return this.title;
    }

    public void setTitle(String string) {
        String string2 = this.title;
        this.title = string;
        this.changes.firePropertyChange("title", string2, string);
    }

    public Locale getLocale() {
        return this.locale;
    }

    private void setLocale(Locale locale) {
        Locale locale2 = this.locale;
        this.locale = locale;
        this.changes.firePropertyChange("locale", locale2, this.locale);
    }

    public Map.ID getHomeID() {
        if (this.homeID == null) {
            return null;
        }
        try {
            return Map.ID.create(this.homeID, this);
        }
        catch (Exception var1_1) {
            return null;
        }
    }

    public void setHomeID(String string) {
        String string2 = string;
        this.homeID = string;
        this.changes.firePropertyChange("homeID", string2, string);
    }

    public Map getCombinedMap() {
        if (this.combinedMap == null) {
            this.combinedMap = new TryMap();
            if (this.map != null) {
                this.combinedMap.add(this.map);
            }
            Enumeration enumeration = this.helpsets.elements();
            while (enumeration.hasMoreElements()) {
                HelpSet helpSet = (HelpSet)enumeration.nextElement();
                this.combinedMap.add(helpSet.getCombinedMap());
            }
        }
        return this.combinedMap;
    }

    public Map getLocalMap() {
        return this.map;
    }

    public void setLocalMap(Map map) {
        this.map = map;
    }

    public URL getHelpSetURL() {
        return this.helpset;
    }

    public ClassLoader getLoader() {
        return this.loader;
    }

    public NavigatorView[] getNavigatorViews() {
        Object[] arrobject = new NavigatorView[this.views.size()];
        this.views.copyInto(arrobject);
        return arrobject;
    }

    public NavigatorView getNavigatorView(String string) {
        HelpSet.debug("getNavigatorView(" + string + ")");
        int n = 0;
        while (n < this.views.size()) {
            NavigatorView navigatorView = (NavigatorView)this.views.elementAt(n);
            if (navigatorView.getName().equals(string)) {
                HelpSet.debug("  = " + navigatorView);
                return navigatorView;
            }
            ++n;
        }
        HelpSet.debug("  = null");
        return null;
    }

    public Presentation[] getPresentations() {
        Object[] arrobject = new Presentation[this.presentations.size()];
        this.presentations.copyInto(arrobject);
        return arrobject;
    }

    public Presentation getPresentation(String string) {
        HelpSet.debug("getPresentation(" + string + ")");
        int n = 0;
        while (n < this.presentations.size()) {
            Presentation presentation = (Presentation)this.presentations.elementAt(n);
            if (presentation.getName().equals(string)) {
                HelpSet.debug("  = " + presentation);
                return presentation;
            }
            ++n;
        }
        HelpSet.debug("  = null");
        return null;
    }

    public Presentation getDefaultPresentation() {
        return this.defaultPresentation;
    }

    public String toString() {
        return this.getTitle();
    }

    public static HelpSet parse(URL uRL, ClassLoader classLoader, HelpSetFactory helpSetFactory) {
        HelpSet helpSet = new HelpSet(classLoader);
        helpSet.helpset = uRL;
        helpSet.parseInto(uRL, helpSetFactory);
        return helpSetFactory.parsingEnded(helpSet);
    }

    public void parseInto(URL uRL, HelpSetFactory helpSetFactory) {
        try {
            URLConnection uRLConnection = uRL.openConnection();
            Reader reader = XmlReader.createReader(uRLConnection);
            helpSetFactory.parsingStarted(uRL);
            new HelpSetParser(helpSetFactory).parseInto(reader, this);
            reader.close();
        }
        catch (Exception var4_4) {
            helpSetFactory.reportMessage("Got an IOException (" + var4_4.getMessage() + ")", false);
        }
        int n = 0;
        while (n < this.subHelpSets.size()) {
            HelpSet helpSet = (HelpSet)this.subHelpSets.elementAt(n);
            this.add(helpSet);
            ++n;
        }
    }

    protected void addView(NavigatorView navigatorView) {
        this.views.addElement(navigatorView);
    }

    protected void addSubHelpSet(HelpSet helpSet) {
        this.subHelpSets.addElement(helpSet);
    }

    protected void addPresentation(Presentation presentation, boolean bl) {
        this.presentations.addElement(presentation);
        if (bl) {
            this.defaultPresentation = presentation;
        }
    }

    public Object getKeyData(Object object, String string) {
        Object var3_3 = null;
        Hashtable hashtable = (Hashtable)this.localKeys.get(object);
        if (hashtable != null) {
            var3_3 = hashtable.get(string);
        }
        if (var3_3 == null && (hashtable = (Hashtable)defaultKeys.get(object)) != null) {
            var3_3 = hashtable.get(string);
        }
        return var3_3;
    }

    public void setKeyData(Object object, String string, Object object2) {
        Hashtable<String, Object> hashtable = (Hashtable<String, Object>)this.localKeys.get(object);
        if (hashtable == null) {
            hashtable = new Hashtable<String, Object>();
            this.localKeys.put(object, hashtable);
        }
        hashtable.put(string, object2);
    }

    private static void setDefaultKeyData(Object object, String string, Object object2) {
        Hashtable<String, Object> hashtable;
        if (defaultKeys == null) {
            defaultKeys = new Hashtable();
        }
        if ((hashtable = (Hashtable<String, Object>)defaultKeys.get(object)) == null) {
            hashtable = new Hashtable<String, Object>();
            defaultKeys.put(object, hashtable);
        }
        hashtable.put(string, object2);
    }

    private static void debug(String string) {
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException var1_1) {
            throw new NoClassDefFoundError(var1_1.getMessage());
        }
    }

    static {
        HelpSet.setDefaultKeyData(implRegistry, "helpBroker/class", "javax.help.DefaultHelpBroker");
        HelpSet.setDefaultKeyData(kitTypeRegistry, "text/html", "com.sun.java.help.impl.CustomKit");
        Class class_ = class$javax$help$HelpSet == null ? (HelpSet.class$javax$help$HelpSet = HelpSet.class$("javax.help.HelpSet")) : class$javax$help$HelpSet;
        ClassLoader classLoader = class_.getClassLoader();
        if (classLoader != null) {
            HelpSet.setDefaultKeyData(implRegistry, "helpBroker/loader", classLoader);
            HelpSet.setDefaultKeyData(kitLoaderRegistry, "text/html", classLoader);
        }
        defaultHelpBroker = null;
    }

    private static class HelpSetParser
    implements ParserListener {
        private Stack tagStack;
        private Locale defaultLocale;
        private Locale lastLocale;
        private HelpSet myHS;
        private Locale myHSLocale;
        private HelpSetFactory factory;
        private String tagName;
        private String viewLabel;
        private String viewType;
        private String viewEngine;
        private String tagImage;
        private String helpActionImage;
        private String viewData;
        private String viewMergeType;
        private Hashtable htData;
        private boolean defaultPresentation = false;
        private boolean displayViews = true;
        private boolean displayViewImages = true;
        private Dimension size;
        private Point location;
        private String presentationTitle;
        private boolean toolbar;
        private Vector helpActions;
        private String helpAction;

        HelpSetParser(HelpSetFactory helpSetFactory) {
            this.factory = helpSetFactory;
        }

        synchronized void parseInto(Reader reader, HelpSet helpSet) throws IOException {
            this.tagStack = new Stack();
            this.lastLocale = this.defaultLocale = helpSet.getLocale();
            this.myHS = helpSet;
            this.myHSLocale = helpSet.getLocale();
            Parser parser = new Parser(reader);
            parser.addParserListener(this);
            parser.parse();
        }

        public void tagFound(ParserEvent parserEvent) {
            Object object;
            String string;
            Hashtable hashtable;
            HelpSet.debug("tagFound " + parserEvent.getTag().name);
            Locale locale = null;
            Tag tag = parserEvent.getTag();
            String string2 = tag.name;
            int n = 0;
            int n2 = 0;
            int n3 = 0;
            int n4 = 0;
            TagProperties tagProperties = tag.atts;
            Hashtable hashtable2 = hashtable = tagProperties == null ? null : tagProperties.getHashtable();
            if (tagProperties != null) {
                string = tagProperties.getProperty("xml:lang");
                locale = HelpUtilities.localeFromLang(string);
                this.viewMergeType = tagProperties.getProperty("mergetype");
                this.helpActionImage = tagProperties.getProperty("image");
                object = null;
                object = tagProperties.getProperty("width");
                if (object != null) {
                    n3 = Integer.parseInt((String)object);
                }
                object = null;
                object = tagProperties.getProperty("height");
                if (object != null) {
                    n4 = Integer.parseInt((String)object);
                }
                object = null;
                object = tagProperties.getProperty("x");
                if (object != null) {
                    n = Integer.parseInt((String)object);
                }
                object = null;
                object = tagProperties.getProperty("y");
                if (object != null) {
                    n2 = Integer.parseInt((String)object);
                }
                object = null;
                object = tagProperties.getProperty("default");
                if (object != null && object.equals("true")) {
                    this.defaultPresentation = true;
                }
                object = null;
                object = tagProperties.getProperty("displayviews");
                if (object != null && object.equals("false")) {
                    this.displayViews = false;
                }
                object = null;
                object = tagProperties.getProperty("displayviewimages");
                if (object != null && object.equals("false")) {
                    this.displayViewImages = false;
                }
            }
            if (locale == null) {
                locale = this.lastLocale;
            }
            if (string2.equals("helpset")) {
                if (tag.isEnd) {
                    this.removeTag(tag);
                } else {
                    if (!locale.equals(this.defaultLocale) && !locale.equals(this.myHSLocale) && locale != null) {
                        this.myHS.setLocale(locale);
                        this.defaultLocale = locale;
                    }
                    if (tagProperties != null && (string = tagProperties.getProperty("version")) != null && string.compareTo("1.0") != 0 && string.compareTo("2.0") != 0) {
                        this.parsingError("helpset.unknownVersion", string);
                    }
                    this.addTag(tag, locale);
                }
                return;
            }
            if (this.tagStack.empty()) {
                this.parsingError("helpset.wrongTopLevel", string2);
            }
            LangElement langElement = (LangElement)this.tagStack.peek();
            string = langElement.getTag().name;
            if (string2.equals("title")) {
                if (tag.isEnd) {
                    this.removeTag(tag);
                } else {
                    if (!string.equals("helpset") && !string.equals("presentation")) {
                        this.wrongParent(string2, string);
                    }
                    if (!locale.equals(this.defaultLocale) && !locale.equals(this.myHSLocale)) {
                        this.wrongLocale(locale, this.defaultLocale, this.myHSLocale);
                    }
                    this.addTag(tag, locale);
                }
            } else if (string2.equals("homeID")) {
                if (tag.isEnd) {
                    this.removeTag(tag);
                } else {
                    if (!string.equals("maps")) {
                        this.wrongParent(string2, string);
                    }
                    this.addTag(tag, locale);
                }
            } else if (string2.equals("mapref")) {
                if (tag.isEnd && !tag.isEmpty) {
                    this.removeTag(tag);
                } else {
                    if (!string.equals("maps")) {
                        this.wrongParent(string2, string);
                    }
                    if (!tag.isEmpty) {
                        this.addTag(tag, locale);
                    }
                    this.factory.processMapRef(this.myHS, hashtable);
                }
            } else if (string2.equals("data")) {
                if (tag.isEnd) {
                    this.removeTag(tag);
                } else {
                    if (!string.equals("view")) {
                        this.wrongParent(string2, string);
                    } else {
                        this.addTag(tag, locale);
                    }
                    this.htData = hashtable;
                }
            } else if (string2.equals("name") || string2.equals("type") || string2.equals("image")) {
                if (tag.isEnd) {
                    this.removeTag(tag);
                } else if (!string.equals("view") && !string.equals("presentation")) {
                    this.wrongParent(string2, string);
                } else {
                    this.addTag(tag, locale);
                }
            } else if (string2.equals("label")) {
                if (tag.isEnd) {
                    this.removeTag(tag);
                } else if (!string.equals("view")) {
                    this.wrongParent(string2, string);
                } else {
                    if (!locale.equals(this.defaultLocale) && !locale.equals(this.myHSLocale)) {
                        this.wrongLocale(locale, this.defaultLocale, this.myHSLocale);
                    }
                    this.addTag(tag, locale);
                }
            } else if (string2.equals("view")) {
                if (tag.isEnd) {
                    this.removeTag(tag);
                    if (this.tagImage != null) {
                        if (this.htData == null) {
                            this.htData = new Hashtable();
                        }
                        this.htData.put("imageID", this.tagImage);
                    }
                    if (this.viewMergeType != null) {
                        if (this.htData == null) {
                            this.htData = new Hashtable();
                        }
                        this.htData.put("mergetype", this.viewMergeType);
                    }
                    this.factory.processView(this.myHS, this.tagName, this.viewLabel, this.viewType, hashtable, this.viewData, this.htData, locale);
                    this.tagName = null;
                    this.viewLabel = null;
                    this.viewType = null;
                    this.tagImage = null;
                    this.viewData = null;
                    this.htData = null;
                    this.viewMergeType = null;
                } else if (!string.equals("helpset")) {
                    this.wrongParent(string2, string);
                } else {
                    this.addTag(tag, locale);
                }
            } else if (string2.equals("presentation")) {
                if (tag.isEnd) {
                    this.removeTag(tag);
                    this.factory.processPresentation(this.myHS, this.tagName, this.defaultPresentation, this.displayViews, this.displayViewImages, this.size, this.location, this.presentationTitle, this.tagImage, this.toolbar, this.helpActions);
                    this.tagName = null;
                    this.defaultPresentation = false;
                    this.displayViews = true;
                    this.displayViewImages = true;
                    this.size = null;
                    this.location = null;
                    this.presentationTitle = null;
                    this.tagImage = null;
                    this.toolbar = false;
                    this.helpActions = null;
                } else if (!string.equals("helpset")) {
                    this.wrongParent(string2, string);
                } else {
                    this.addTag(tag, locale);
                }
            } else if (string2.equals("size")) {
                if (tag.isEnd) {
                    if (this.size == null) {
                        this.size = new Dimension(n3, n4);
                    } else {
                        this.size.setSize(n3, n4);
                    }
                    n3 = 0;
                    n4 = 0;
                    if (!tag.isEmpty) {
                        this.removeTag(tag);
                    }
                } else if (!string.equals("presentation")) {
                    this.wrongParent(string2, string);
                } else {
                    this.addTag(tag, locale);
                    this.size = new Dimension();
                }
            } else if (string2.equals("location")) {
                if (tag.isEnd) {
                    if (this.location == null) {
                        this.location = new Point(n, n2);
                    } else {
                        this.location.setLocation(n, n2);
                    }
                    n = 0;
                    n2 = 0;
                    if (!tag.isEmpty) {
                        this.removeTag(tag);
                    }
                } else if (!string.equals("presentation")) {
                    this.wrongParent(string2, string);
                } else {
                    this.addTag(tag, locale);
                    this.location = new Point();
                }
            } else if (string2.equals("toolbar")) {
                if (tag.isEnd) {
                    this.removeTag(tag);
                } else if (!string.equals("presentation")) {
                    this.wrongParent(string2, string);
                } else {
                    this.addTag(tag, locale);
                    this.helpActions = new Vector();
                    this.toolbar = true;
                }
            } else if (string2.equals("helpaction")) {
                if (tag.isEnd) {
                    this.removeTag(tag);
                    if (this.helpAction != null) {
                        object = new Hashtable();
                        this.helpActions.add(new HelpSetFactory.HelpAction(this.helpAction, (Hashtable)object));
                        if (this.helpActionImage != null) {
                            object.put("image", this.helpActionImage);
                            this.helpActionImage = null;
                        }
                        this.helpAction = null;
                    }
                } else if (!string.equals("toolbar")) {
                    this.wrongParent(string2, string);
                } else {
                    this.addTag(tag, locale);
                }
            } else if (string2.equals("maps")) {
                if (tag.isEnd) {
                    this.removeTag(tag);
                } else if (!string.equals("helpset")) {
                    this.wrongParent(string2, string);
                } else {
                    this.addTag(tag, locale);
                }
            } else if (string2.equals("subhelpset")) {
                if (tag.isEnd && !tag.isEmpty) {
                    this.removeTag(tag);
                } else {
                    if (!tag.isEmpty) {
                        this.addTag(tag, locale);
                    }
                    this.factory.processSubHelpSet(this.myHS, hashtable);
                }
            } else if (string2.equals("impl")) {
                if (tag.isEnd) {
                    this.removeTag(tag);
                } else if (!string.equals("helpset")) {
                    this.wrongParent(string2, string);
                } else {
                    this.addTag(tag, locale);
                }
            } else if (string2.equals("helpsetregistry")) {
                if (tag.isEnd && !tag.isEmpty) {
                    this.removeTag(tag);
                } else if (!string.equals("impl")) {
                    this.wrongParent(string2, string);
                } else {
                    if (!tag.isEnd) {
                        this.addTag(tag, locale);
                    }
                    if (tagProperties != null && (object = tagProperties.getProperty("helpbrokerclass")) != null) {
                        this.myHS.setKeyData(HelpSet.implRegistry, "helpBroker/class", object);
                    }
                }
            } else if (string2.equals("viewerregistry")) {
                if (tag.isEnd && !tag.isEmpty) {
                    this.removeTag(tag);
                } else if (!string.equals("impl")) {
                    this.wrongParent(string2, string);
                } else {
                    if (!tag.isEnd) {
                        this.addTag(tag, locale);
                    }
                    if (tagProperties != null) {
                        object = tagProperties.getProperty("viewertype");
                        String string3 = tagProperties.getProperty("viewerclass");
                        if (object != null && string3 != null) {
                            Class class_ = HelpSet.class$javax$help$HelpSet == null ? (HelpSet.class$javax$help$HelpSet = HelpSet.class$("javax.help.HelpSet")) : HelpSet.class$javax$help$HelpSet;
                            ClassLoader classLoader = class_.getClassLoader();
                            this.myHS.setKeyData(HelpSet.kitTypeRegistry, (String)object, string3);
                            this.myHS.setKeyData(HelpSet.kitLoaderRegistry, (String)object, classLoader);
                        }
                    }
                }
            }
        }

        public void piFound(ParserEvent parserEvent) {
            this.factory.processPI(this.myHS, parserEvent.getTarget(), parserEvent.getData());
        }

        public void doctypeFound(ParserEvent parserEvent) {
            this.factory.processDOCTYPE(parserEvent.getRoot(), parserEvent.getPublicId(), parserEvent.getSystemId());
        }

        private void checkNull(String string, String string2) {
            if (!string2.equals("")) {
                this.parsingError("helpset.wrongText", string, string2);
            }
        }

        public void textFound(ParserEvent parserEvent) {
            HelpSet.debug("textFound: ");
            HelpSet.debug("  text: " + parserEvent.getText());
            if (this.tagStack.empty()) {
                return;
            }
            LangElement langElement = (LangElement)this.tagStack.peek();
            Tag tag = langElement.getTag();
            TagProperties tagProperties = tag.atts;
            Hashtable hashtable = tagProperties == null ? null : tagProperties.getHashtable();
            String string = parserEvent.getText().trim();
            String string2 = tag.name;
            if (string2.equals("helpset")) {
                this.checkNull("helpset", string);
                return;
            }
            int n = this.tagStack.size();
            String string3 = "";
            if (n >= 2) {
                langElement = (LangElement)this.tagStack.elementAt(n - 2);
                string3 = langElement.getTag().name;
            }
            if (string2.equals("title")) {
                if (string3.equals("helpset")) {
                    this.factory.processTitle(this.myHS, string);
                } else {
                    this.presentationTitle = string.trim();
                }
            } else if (string2.equals("homeID")) {
                this.factory.processHomeID(this.myHS, string);
            } else if (string2.equals("mapref")) {
                this.checkNull("mapref", string);
            } else if (string2.equals("subhelpset")) {
                this.checkNull("subhelpset", string);
            } else if (string2.equals("data")) {
                this.viewData = string.trim();
            } else if (string2.equals("label")) {
                this.viewLabel = string.trim();
            } else if (string2.equals("name")) {
                this.tagName = string.trim();
            } else if (string2.equals("helpaction")) {
                this.helpAction = string.trim();
            } else if (string2.equals("type")) {
                this.viewType = string.trim();
            } else if (string2.equals("image")) {
                this.tagImage = string.trim();
            } else if (string2.equals("view")) {
                this.checkNull("view", string);
            } else if (string2.equals("maps")) {
                this.checkNull("maps", string);
            } else if (string2.equals("mergetype")) {
                this.checkNull("mergetype", string);
            }
        }

        public void errorFound(ParserEvent parserEvent) {
        }

        public void commentFound(ParserEvent parserEvent) {
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
            LangElement langElement;
            String string = tag.name;
            Locale locale = null;
            do {
                if (this.tagStack.empty()) {
                    this.unbalanced(string);
                }
                langElement = (LangElement)this.tagStack.pop();
            } while (!langElement.getTag().name.equals(string));
            if (this.tagStack.empty()) {
                locale = this.defaultLocale;
            } else {
                langElement = (LangElement)this.tagStack.peek();
                locale = langElement.getLocale();
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

        private void parsingError(String string) {
            String string2 = HelpUtilities.getText(string);
            this.factory.reportMessage(string2, false);
        }

        private void parsingError(String string, String string2) {
            String string3 = HelpUtilities.getText(string, string2);
            this.factory.reportMessage(string3, false);
        }

        private void parsingError(String string, String string2, String string3) {
            String string4 = HelpUtilities.getText(string, string2, string3);
            this.factory.reportMessage(string4, false);
        }

        private void wrongParent(String string, String string2) {
            this.parsingError("helpset.wrongParent", string, string2);
        }

        private void unbalanced(String string) {
            this.parsingError("helpset.unbalanced", string);
        }

        private void wrongLocale(Locale locale, Locale locale2, Locale locale3) {
            String string = HelpUtilities.getText("helpset.wrongLocale", locale.toString(), locale2.toString(), locale3.toString());
            this.factory.reportMessage(string, true);
        }
    }

    public static class Presentation {
        private String name;
        private boolean displayViews;
        private boolean displayViewImages;
        private Dimension size;
        private Point location;
        private String title;
        private boolean toolbar;
        private Vector helpActions;
        private Map.ID imageID;

        public Presentation(String string, boolean bl, boolean bl2, Dimension dimension, Point point, String string2, Map.ID iD, boolean bl3, Vector vector) {
            this.name = string;
            this.displayViews = bl;
            this.displayViewImages = bl2;
            this.size = dimension;
            this.location = point;
            this.title = string2;
            this.imageID = iD;
            this.toolbar = bl3;
            this.helpActions = vector;
        }

        public String getName() {
            return this.name;
        }

        public String getTitle() {
            return this.title;
        }

        public Map.ID getImageID() {
            return this.imageID;
        }

        public boolean isViewDisplayed() {
            return this.displayViews;
        }

        public boolean isViewImagesDisplayed() {
            return this.displayViewImages;
        }

        public Dimension getSize() {
            return this.size;
        }

        public Point getLocation() {
            return this.location;
        }

        public boolean isToolbar() {
            return this.toolbar;
        }

        public Enumeration getHelpActions(HelpSet helpSet, Object object) {
            Vector<HelpAction> vector = new Vector<HelpAction>();
            ClassLoader classLoader = helpSet.getLoader();
            if (this.helpActions == null) {
                return vector.elements();
            }
            Enumeration enumeration = this.helpActions.elements();
            while (enumeration.hasMoreElements()) {
                HelpSetFactory.HelpAction helpAction = (HelpSetFactory.HelpAction)enumeration.nextElement();
                try {
                    Class[] arrclass = new Class[1];
                    arrclass[0] = HelpSet.class$java$lang$Object == null ? HelpSet.class$("java.lang.Object") : HelpSet.class$java$lang$Object;
                    Class[] arrclass2 = arrclass;
                    Object[] arrobject = new Object[]{object};
                    Class class_ = classLoader == null ? Class.forName(helpAction.className) : classLoader.loadClass(helpAction.className);
                    Constructor constructor = class_.getConstructor(arrclass2);
                    HelpAction helpAction2 = (HelpAction)constructor.newInstance(arrobject);
                    if (helpAction.attr.containsKey("image")) {
                        String string = (String)helpAction.attr.get("image");
                        try {
                            Map.ID iD = Map.ID.create(string, helpSet);
                            ImageIcon imageIcon = null;
                            Map map = helpSet.getCombinedMap();
                            URL uRL = map.getURLFromID(iD);
                            imageIcon = new ImageIcon(uRL);
                            helpAction2.putValue("icon", imageIcon);
                        }
                        catch (Exception var13_15) {
                            // empty catch block
                        }
                    }
                    vector.add(helpAction2);
                    continue;
                }
                catch (Exception var10_11) {
                    throw new RuntimeException("Could not create HelpAction " + helpAction.className);
                }
            }
            return vector.elements();
        }
    }

    public static class DefaultHelpSetFactory
    implements HelpSetFactory {
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
            if (string2 == null || string2.compareTo("-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN") != 0 && string2.compareTo("-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN") != 0) {
                this.parsingError("helpset.wrongPublicID", string2);
            }
        }

        public void processPI(HelpSet helpSet, String string, String string2) {
        }

        public void processTitle(HelpSet helpSet, String string) {
            String string2 = helpSet.getTitle();
            if (string2 != null && !string2.equals("")) {
                this.parsingWarning("helpset.wrongTitle", string, string2);
            }
            helpSet.setTitle(string);
        }

        public void processHomeID(HelpSet helpSet, String string) {
            Map.ID iD = helpSet.getHomeID();
            if (iD == null || iD.equals("")) {
                helpSet.setHomeID(string);
            } else {
                this.parsingError("helpset.wrongHomeID", string, iD.id);
            }
        }

        public void processMapRef(HelpSet helpSet, Hashtable hashtable) {
            String string = (String)hashtable.get("location");
            URL uRL = helpSet.getHelpSetURL();
            try {
                FlatMap flatMap = new FlatMap(new URL(uRL, string), helpSet);
                Map map = helpSet.getLocalMap();
                if (map == null) {
                    HelpSet.debug("map is null");
                    helpSet.setLocalMap(flatMap);
                } else if (map instanceof TryMap) {
                    HelpSet.debug("map is TryMap");
                    ((TryMap)map).add(flatMap);
                    helpSet.setLocalMap(map);
                } else {
                    HelpSet.debug("map is not TryMap");
                    TryMap tryMap = new TryMap();
                    tryMap.add(map);
                    tryMap.add(flatMap);
                    helpSet.setLocalMap(tryMap);
                }
            }
            catch (MalformedURLException var5_6) {
                this.parsingError("helpset.malformedURL", string);
            }
            catch (IOException var6_8) {
                this.parsingError("helpset.incorrectURL", string);
            }
            catch (Exception var7_10) {
                // empty catch block
            }
        }

        public void processView(HelpSet helpSet, String string, String string2, String string3, Hashtable hashtable, String string4, Hashtable hashtable2, Locale locale) {
            try {
                NavigatorView navigatorView;
                if (string4 != null) {
                    if (hashtable2 == null) {
                        hashtable2 = new Hashtable<String, String>();
                    }
                    hashtable2.put("data", string4);
                }
                if ((navigatorView = NavigatorView.create(helpSet, string, string2, locale, string3, hashtable2)) != null) {
                    helpSet.addView(navigatorView);
                }
            }
            catch (Exception var9_10) {
                // empty catch block
            }
        }

        public void processPresentation(HelpSet helpSet, String string, boolean bl, boolean bl2, boolean bl3, Dimension dimension, Point point, String string2, String string3, boolean bl4, Vector vector) {
            Map.ID iD = null;
            try {
                iD = Map.ID.create(string3, helpSet);
            }
            catch (BadIDException var13_13) {
                // empty catch block
            }
            try {
                Presentation presentation = new Presentation(string, bl2, bl3, dimension, point, string2, iD, bl4, vector);
                if (presentation != null) {
                    helpSet.addPresentation(presentation, bl);
                }
            }
            catch (Exception var13_15) {
                // empty catch block
            }
        }

        public void processSubHelpSet(HelpSet helpSet, Hashtable hashtable) {
            HelpSet.debug("createSubHelpSet");
            String string = (String)hashtable.get("location");
            URL uRL = helpSet.getHelpSetURL();
            HelpSet.debug("  location: " + string);
            HelpSet.debug("  base helpset: " + uRL);
            URL uRL2 = null;
            HelpSet helpSet2 = null;
            try {
                uRL2 = new URL(uRL, string);
                InputStream inputStream = uRL2.openStream();
                if (inputStream != null && (helpSet2 = new HelpSet(helpSet.getLoader(), uRL2)) != null) {
                    helpSet.addSubHelpSet(helpSet2);
                }
            }
            catch (MalformedURLException var7_8) {
            }
            catch (IOException var8_9) {
            }
            catch (HelpSetException var9_10) {
                this.parsingError("helpset.subHelpSetTrouble", string);
            }
        }

        public void reportMessage(String string, boolean bl) {
            this.messages.addElement(string);
            this.validParse = this.validParse && bl;
        }

        public Enumeration listMessages() {
            return this.messages.elements();
        }

        public HelpSet parsingEnded(HelpSet helpSet) {
            HelpSet helpSet2 = helpSet;
            if (!this.validParse) {
                helpSet2 = null;
                String string = "Parsing failed for " + this.source;
                this.messages.addElement(string);
                Enumeration enumeration = this.messages.elements();
                while (enumeration.hasMoreElements()) {
                    String string2 = (String)enumeration.nextElement();
                    if (errorMsg == null) {
                        errorMsg = string2;
                        continue;
                    }
                    errorMsg = errorMsg + "\n";
                    errorMsg = errorMsg + string2;
                }
            }
            return helpSet2;
        }

        private void parsingError(String string) {
            String string2 = HelpUtilities.getText(string);
            this.reportMessage(string2, false);
        }

        private void parsingError(String string, String string2) {
            String string3 = HelpUtilities.getText(string, string2);
            this.reportMessage(string3, false);
        }

        private void parsingError(String string, String string2, String string3) {
            String string4 = HelpUtilities.getText(string, string2, string3);
            this.reportMessage(string4, false);
        }

        private void parsingWarning(String string, String string2, String string3) {
            String string4 = HelpUtilities.getText(string, string2, string3);
            this.reportMessage(string4, true);
        }
    }

}

