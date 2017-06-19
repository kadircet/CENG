/*
 * Decompiled with CFR 0_114.
 */
package javax.help.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import javax.help.AbstractHelpAction;
import javax.help.BackAction;
import javax.help.FavoritesAction;
import javax.help.ForwardAction;
import javax.help.HelpAction;
import javax.help.HelpHistoryModel;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.JHelp;
import javax.help.JHelpContentViewer;
import javax.help.JHelpFavoritesNavigator;
import javax.help.JHelpNavigator;
import javax.help.Map;
import javax.help.PrintAction;
import javax.help.PrintSetupAction;
import javax.help.SeparatorAction;
import javax.help.SwingHelpUtilities;
import javax.help.TextHelpModel;
import javax.help.plaf.HelpUI;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

public class BasicHelpUI
extends HelpUI
implements PropertyChangeListener,
Serializable {
    protected JHelp help;
    protected JToolBar toolbar;
    protected JSplitPane splitPane;
    protected JTabbedPane tabbedPane;
    protected Vector navs = new Vector();
    private static Dimension PREF_SIZE;
    private static Dimension MIN_SIZE;
    static boolean noPageSetup;
    private int dividerLocation = 0;
    private final double dividerLocationRatio = 0.3;
    private JHelpFavoritesNavigator favorites = null;
    private static boolean debug;
    static /* synthetic */ Class class$java$awt$datatransfer$DataFlavor;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$beans$PropertyChangeListener;
    static /* synthetic */ Class class$javax$help$plaf$basic$BasicHelpUI;

    public static ComponentUI createUI(JComponent jComponent) {
        return new BasicHelpUI((JHelp)jComponent);
    }

    public BasicHelpUI(JHelp jHelp) {
        BasicHelpUI.debug("createUI - sort of");
    }

    public void installUI(JComponent jComponent) {
        BasicHelpUI.debug("installUI");
        this.help = (JHelp)jComponent;
        this.help.setLayout(new BorderLayout());
        this.help.addPropertyChangeListener(this);
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.setVisible(false);
        this.splitPane = new JSplitPane(1, false, this.tabbedPane, this.help.getContentViewer());
        this.splitPane.setOneTouchExpandable(true);
        this.help.add("Center", this.splitPane);
        JHelpNavigator jHelpNavigator = null;
        Enumeration enumeration = this.help.getHelpNavigators();
        while (enumeration.hasMoreElements()) {
            JHelpNavigator jHelpNavigator2 = (JHelpNavigator)enumeration.nextElement();
            if (jHelpNavigator2 instanceof JHelpFavoritesNavigator) {
                this.favorites = (JHelpFavoritesNavigator)jHelpNavigator2;
            }
            this.addNavigator(jHelpNavigator2);
            if (jHelpNavigator != null) continue;
            jHelpNavigator = jHelpNavigator2;
        }
        BasicHelpUI.debug("setting the current Navigator");
        if (jHelpNavigator != null) {
            this.setCurrentNavigator(jHelpNavigator);
        }
        this.toolbar = this.createToolBar(HelpUtilities.getLocale(jComponent));
        if (this.toolbar != null) {
            this.toolbar.setFloatable(false);
            this.help.add("North", this.toolbar);
        }
        this.rebuild();
    }

    protected JToolBar createToolBar(Locale locale) {
        this.toolbar = new JToolBar();
        Enumeration enumeration = null;
        HelpSet.Presentation presentation = this.help.getHelpSetPresentation();
        if (presentation != null && presentation.isToolbar()) {
            enumeration = presentation.getHelpActions(this.getModel().getHelpSet(), this.help);
        }
        if (enumeration == null || !enumeration.hasMoreElements()) {
            enumeration = this.createDefaultActions();
        }
        while (enumeration.hasMoreElements()) {
            HelpAction helpAction = (HelpAction)enumeration.nextElement();
            if (helpAction instanceof SeparatorAction) {
                this.toolbar.addSeparator();
                continue;
            }
            this.toolbar.add(new HelpButton(helpAction));
        }
        return this.toolbar;
    }

    private Enumeration createDefaultActions() {
        Vector<AbstractHelpAction> vector = new Vector<AbstractHelpAction>(5);
        vector.add(new BackAction(this.help));
        vector.add(new ForwardAction(this.help));
        vector.add(new SeparatorAction(this.help));
        vector.add(new PrintAction(this.help));
        vector.add(new PrintSetupAction(this.help));
        vector.add(new SeparatorAction(this.help));
        if (this.favorites != null) {
            vector.add(new FavoritesAction(this.help));
        }
        return vector.elements();
    }

    public void uninstallUI(JComponent jComponent) {
        BasicHelpUI.debug("uninstallUI");
        this.help.removePropertyChangeListener(this);
        this.help.setLayout(null);
        this.help.removeAll();
        HelpModel helpModel = this.getModel();
        if (helpModel != null) {
            // empty if block
        }
        this.help = null;
        this.toolbar = null;
    }

    public Dimension getPreferredSize(JComponent jComponent) {
        return PREF_SIZE;
    }

    public Dimension getMinimumSize(JComponent jComponent) {
        return MIN_SIZE;
    }

    public Dimension getMaximumSize(JComponent jComponent) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    private void rebuild() {
        HelpModel helpModel = this.getModel();
        if (helpModel == null) {
            return;
        }
        HelpHistoryModel helpHistoryModel = this.getHistoryModel();
        if (helpHistoryModel != null) {
            helpHistoryModel.discard();
        }
        try {
            Map.ID iD = helpModel.getCurrentID();
            if (iD == null) {
                HelpSet helpSet = helpModel.getHelpSet();
                Map.ID iD2 = helpSet.getHomeID();
                Locale locale = helpSet.getLocale();
                String string = HelpUtilities.getString(locale, "history.homePage");
                helpModel.setCurrentID(iD2, string, null);
            }
        }
        catch (Exception var3_4) {
            return;
        }
    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        Object object = propertyChangeEvent.getSource();
        String string = propertyChangeEvent.getPropertyName();
        BasicHelpUI.debug("propertyChange: " + string);
        if (object == this.help) {
            if (string.equals("helpModel")) {
                this.rebuild();
            } else if (string.equals("font")) {
                BasicHelpUI.debug("Font change");
                Font font = (Font)propertyChangeEvent.getNewValue();
                this.help.getContentViewer().setFont(font);
                this.help.getContentViewer().invalidate();
                Enumeration enumeration = this.help.getHelpNavigators();
                while (enumeration.hasMoreElements()) {
                    JHelpNavigator jHelpNavigator = (JHelpNavigator)enumeration.nextElement();
                    jHelpNavigator.setFont(font);
                }
            } else if (string.equals("navigatorDisplayed")) {
                boolean bl = (Boolean)propertyChangeEvent.getNewValue();
                if (bl) {
                    this.help.add("Center", this.splitPane);
                } else {
                    this.help.add("Center", this.help.getContentViewer());
                }
            } else if (string.equals("toolbarDisplayed")) {
                this.toolbar.setVisible((Boolean)propertyChangeEvent.getNewValue());
            }
        }
    }

    protected HelpModel getModel() {
        if (this.help == null) {
            return null;
        }
        return this.help.getModel();
    }

    protected HelpHistoryModel getHistoryModel() {
        if (this.help == null) {
            return null;
        }
        return this.help.getHistoryModel();
    }

    public void addNavigator(JHelpNavigator jHelpNavigator) {
        BasicHelpUI.debug("addNavigator");
        this.navs.addElement(jHelpNavigator);
        Icon icon = null;
        HelpSet.Presentation presentation = this.help.getHelpSetPresentation();
        if (presentation != null) {
            if (presentation.isViewImagesDisplayed()) {
                icon = jHelpNavigator.getIcon();
            }
        } else {
            icon = jHelpNavigator.getIcon();
        }
        if (icon != null) {
            this.tabbedPane.addTab("", icon, jHelpNavigator, jHelpNavigator.getNavigatorLabel());
        } else {
            String string = jHelpNavigator.getNavigatorLabel();
            if (string == null) {
                string = "<unknown>";
            }
            this.tabbedPane.addTab(string, icon, jHelpNavigator);
        }
        jHelpNavigator.setVisible(false);
        this.tabbedPane.setVisible(this.help.isNavigatorDisplayed());
        this.help.invalidate();
        SwingUtilities.invokeLater(new Runnable(){

            public void run() {
                if ((double)BasicHelpUI.this.dividerLocation == 0.0) {
                    Dimension dimension = BasicHelpUI.this.splitPane.getSize();
                    if (dimension.width != 0) {
                        BasicHelpUI.this.splitPane.setDividerLocation((int)((double)(dimension.width - BasicHelpUI.this.splitPane.getDividerSize()) * 0.3));
                    }
                    BasicHelpUI.this.dividerLocation = BasicHelpUI.this.splitPane.getDividerLocation();
                }
            }
        });
    }

    public void removeNavigator(JHelpNavigator jHelpNavigator) {
        BasicHelpUI.debug("removeNavigator");
        this.navs.removeElement(jHelpNavigator);
        this.tabbedPane.remove(jHelpNavigator);
        this.help.invalidate();
    }

    public Enumeration getHelpNavigators() {
        return this.navs.elements();
    }

    public void setCurrentNavigator(JHelpNavigator jHelpNavigator) {
        try {
            this.tabbedPane.setSelectedComponent(jHelpNavigator);
        }
        catch (IllegalArgumentException var2_2) {
            throw new IllegalArgumentException("JHelpNavigator must be added first");
        }
    }

    public JHelpNavigator getCurrentNavigator() {
        return (JHelpNavigator)this.tabbedPane.getSelectedComponent();
    }

    private ImageIcon getIcon(String string) {
        Class class_ = class$javax$help$plaf$basic$BasicHelpUI == null ? (BasicHelpUI.class$javax$help$plaf$basic$BasicHelpUI = BasicHelpUI.class$("javax.help.plaf.basic.BasicHelpUI")) : class$javax$help$plaf$basic$BasicHelpUI;
        return BasicHelpUI.getIcon(class_, string);
    }

    public static ImageIcon getIcon(Class class_, String string) {
        ImageIcon imageIcon = null;
        try {
            imageIcon = SwingHelpUtilities.getImageIcon(class_, string);
        }
        catch (Exception var3_3) {
            // empty catch block
        }
        if (debug || imageIcon == null) {
            System.err.println("GetIcon");
            System.err.println("  name: " + string);
            System.err.println("  klass: " + class_);
            URL uRL = class_.getResource(string);
            System.err.println("  URL is " + uRL);
            System.err.println("  ImageIcon is " + imageIcon);
        }
        return imageIcon;
    }

    private static void debug(String string) {
        if (debug) {
            System.out.println("BasicHelpUI: " + string);
        }
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
        String[] arrstring;
        PREF_SIZE = new Dimension(600, 600);
        MIN_SIZE = new Dimension(300, 200);
        noPageSetup = false;
        boolean bl = false;
        try {
            Class class_ = class$java$awt$datatransfer$DataFlavor == null ? (BasicHelpUI.class$java$awt$datatransfer$DataFlavor = BasicHelpUI.class$("java.awt.datatransfer.DataFlavor")) : class$java$awt$datatransfer$DataFlavor;
            arrstring = class_.getMethod("getTextPlainUnicodeFlavor", null);
            bl = arrstring == null;
        }
        catch (NoSuchMethodException var1_2) {
            bl = true;
        }
        if (bl) {
            arrstring = new String[]{""};
            arrstring[0] = System.getProperty("os.name");
            if (arrstring[0] != null && (arrstring[0].indexOf("Solaris") != -1 || arrstring[0].indexOf("SunOS") != -1 || arrstring[0].indexOf("Linux") != -1 || arrstring[0].indexOf("HP-UX") != -1)) {
                noPageSetup = true;
            }
        }
        debug = false;
    }

    private class HelpButton
    extends JButton
    implements PropertyChangeListener {
        HelpButton(HelpAction helpAction) {
            this.setEnabled(helpAction.isEnabled());
            String string = (String)helpAction.getValue("name");
            Icon icon = (Icon)helpAction.getValue("icon");
            if (icon == null) {
                icon = UIManager.getIcon("HelpAction.icon");
            }
            this.setIcon(icon);
            Locale locale = null;
            try {
                locale = BasicHelpUI.this.help.getModel().getHelpSet().getLocale();
            }
            catch (NullPointerException var6_6) {
                locale = Locale.getDefault();
            }
            String string2 = (String)helpAction.getValue("tooltip");
            this.setToolTipText(string2);
            String string3 = (String)helpAction.getValue("access");
            this.getAccessibleContext().setAccessibleName(string3);
            if (helpAction instanceof MouseListener) {
                this.addMouseListener((MouseListener)((Object)helpAction));
            }
            if (helpAction instanceof ActionListener) {
                this.addActionListener((ActionListener)((Object)helpAction));
            }
            helpAction.addPropertyChangeListener(this);
        }

        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("enabled")) {
                this.setEnabled((Boolean)propertyChangeEvent.getNewValue());
            }
        }

        private boolean createEnablePropertyChangeSupport(HelpAction helpAction) {
            boolean bl = false;
            try {
                Class[] arrclass = new Class[2];
                Class class_ = BasicHelpUI.class$java$lang$String == null ? (BasicHelpUI.class$java$lang$String = BasicHelpUI.class$("java.lang.String")) : BasicHelpUI.class$java$lang$String;
                arrclass[0] = class_;
                Class class_2 = BasicHelpUI.class$java$beans$PropertyChangeListener == null ? (BasicHelpUI.class$java$beans$PropertyChangeListener = BasicHelpUI.class$("java.beans.PropertyChangeListener")) : BasicHelpUI.class$java$beans$PropertyChangeListener;
                arrclass[1] = class_2;
                Class[] arrclass2 = arrclass;
                Method method = helpAction.getClass().getMethod("addPropertyChangeListener", arrclass2);
                Object[] arrobject = new Object[]{"enabled", this};
                method.invoke(helpAction, arrobject);
                bl = true;
            }
            catch (Exception var3_4) {
                // empty catch block
            }
            return bl;
        }

        private boolean createPropertyChangeSupport(HelpAction helpAction) {
            boolean bl = false;
            try {
                Class[] arrclass = new Class[1];
                Class class_ = BasicHelpUI.class$java$beans$PropertyChangeListener == null ? (BasicHelpUI.class$java$beans$PropertyChangeListener = BasicHelpUI.class$("java.beans.PropertyChangeListener")) : BasicHelpUI.class$java$beans$PropertyChangeListener;
                arrclass[0] = class_;
                Class[] arrclass2 = arrclass;
                Method method = helpAction.getClass().getMethod("addPropertyChangeListener", arrclass2);
                Object[] arrobject = new Object[]{this};
                method.invoke(helpAction, arrobject);
                bl = true;
            }
            catch (Exception var3_4) {
                // empty catch block
            }
            return bl;
        }
    }

}

