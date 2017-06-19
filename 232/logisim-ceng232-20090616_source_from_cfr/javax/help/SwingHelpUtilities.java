/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import javax.swing.ImageIcon;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

public class SwingHelpUtilities
implements PropertyChangeListener {
    private static UIDefaults uiDefaults = null;
    private static SwingHelpUtilities myLAFListener = new SwingHelpUtilities();
    private static String contentViewerUI = null;
    static Object basicOnItemCursor;
    static Object basicDnDCursor;
    static Object gtkOnItemCursor;
    static Object gtkDnDCursor;
    private static final boolean debug = false;
    static /* synthetic */ Class class$javax$help$HelpUtilities;
    static /* synthetic */ Class class$javax$help$plaf$basic$BasicHelpUI;
    static /* synthetic */ Class class$javax$help$plaf$gtk$GTKCursorFactory;
    static /* synthetic */ Class class$java$beans$PropertyChangeListener;

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String string = propertyChangeEvent.getPropertyName();
        if (string.equals("lookAndFeel")) {
            SwingHelpUtilities.installLookAndFeelDefaults();
        }
    }

    static void installUIDefaults() {
        UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
        if (uiDefaults != uIDefaults) {
            uiDefaults = uIDefaults;
            UIManager.removePropertyChangeListener(myLAFListener);
            SwingHelpUtilities.installLookAndFeelDefaults();
            UIManager.addPropertyChangeListener(myLAFListener);
        }
    }

    public static void setContentViewerUI(String string) {
        if (string != null) {
            try {
                Class class_ = Class.forName(string);
                contentViewerUI = string;
            }
            catch (Throwable var1_2) {
                System.out.println("ContentViewerClass " + string + " doesn't exist");
            }
        }
    }

    static void installLookAndFeelDefaults() {
        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
        SwingHelpUtilities.debug("installLookAndFeelDefaults - " + lookAndFeel);
        if (lookAndFeel != null && uIDefaults != null) {
            if (lookAndFeel.getID().equals("Motif")) {
                SwingHelpUtilities.installMotifDefaults(uIDefaults);
            } else if (lookAndFeel.getID().equals("Windows")) {
                SwingHelpUtilities.installWindowsDefaults(uIDefaults);
            } else if (lookAndFeel.getID().equals("GTK")) {
                SwingHelpUtilities.installGTKDefaults(uIDefaults);
            } else {
                SwingHelpUtilities.installMetalDefaults(uIDefaults);
            }
        }
        SwingHelpUtilities.debug("verifing UIDefaults; HelpUI=" + uIDefaults.getString("HelpUI"));
    }

    private static Object createIcon(String string, String string2) {
        Class class_ = class$javax$help$HelpUtilities == null ? (SwingHelpUtilities.class$javax$help$HelpUtilities = SwingHelpUtilities.class$("javax.help.HelpUtilities")) : class$javax$help$HelpUtilities;
        ClassLoader classLoader = class_.getClassLoader();
        try {
            Class[] arrclass = new Class[]{};
            Object[] arrobject = new Object[]{};
            Class class_2 = classLoader == null ? Class.forName(string) : classLoader.loadClass(string);
            Method method = class_2.getMethod(string2, arrclass);
            Object object = method.invoke(null, arrobject);
            return object;
        }
        catch (Exception var3_4) {
            return null;
        }
    }

    private static Object makeBasicIcon(final String string) {
        return new UIDefaults.LazyValue(){

            public Object createValue(UIDefaults uIDefaults) {
                Class class_ = SwingHelpUtilities.class$javax$help$plaf$basic$BasicHelpUI == null ? (SwingHelpUtilities.class$javax$help$plaf$basic$BasicHelpUI = SwingHelpUtilities.class$("javax.help.plaf.basic.BasicHelpUI")) : SwingHelpUtilities.class$javax$help$plaf$basic$BasicHelpUI;
                return SwingHelpUtilities.getImageIcon(class_, string);
            }
        };
    }

    private static void installBasicDefaults(UIDefaults uIDefaults) {
        String string = "javax.help.plaf.basic.";
        String string2 = string + "BasicContentViewerUI";
        if (contentViewerUI != null) {
            string2 = contentViewerUI;
        }
        Object[] arrobject = new Object[]{"HelpUI", string + "BasicHelpUI", "HelpTOCNavigatorUI", string + "BasicTOCNavigatorUI", "HelpIndexNavigatorUI", string + "BasicIndexNavigatorUI", "HelpSearchNavigatorUI", string + "BasicSearchNavigatorUI", "HelpGlossaryNavigatorUI", string + "BasicGlossaryNavigatorUI", "HelpFavoritesNavigatorUI", string + "BasicFavoritesNavigatorUI", "HelpContentViewerUI", string2, "HelpDnDCursor", basicDnDCursor, "HelpOnItemCursor", basicOnItemCursor, "BackAction.icon", SwingHelpUtilities.makeBasicIcon("images/Back.gif"), "ForwardAction.icon", SwingHelpUtilities.makeBasicIcon("images/Forward.gif"), "PrintAction.icon", SwingHelpUtilities.makeBasicIcon("images/Print.gif"), "PrintSetupAction.icon", SwingHelpUtilities.makeBasicIcon("images/PrintSetup.gif"), "ReloadAction.icon", SwingHelpUtilities.makeBasicIcon("images/Reload.gif"), "FavoritesAction.icon", SwingHelpUtilities.makeBasicIcon("images/Favorites.gif"), "HomeAction.icon", SwingHelpUtilities.makeBasicIcon("images/Home.gif"), "FavoritesNav.icon", SwingHelpUtilities.makeBasicIcon("images/FavoritesNav.gif"), "IndexNav.icon", SwingHelpUtilities.makeBasicIcon("images/IndexNav.gif"), "TOCNav.icon", SwingHelpUtilities.makeBasicIcon("images/TOCNav.gif"), "SearchNav.icon", SwingHelpUtilities.makeBasicIcon("images/SearchNav.gif"), "GlossaryNav.icon", SwingHelpUtilities.makeBasicIcon("images/GlossaryNav.gif"), "HistoryNav.icon", SwingHelpUtilities.makeBasicIcon("images/HistoryNav.gif"), "SearchLow.icon", SwingHelpUtilities.makeBasicIcon("images/SearchLow.gif"), "SearchMedLow.icon", SwingHelpUtilities.makeBasicIcon("images/SearchMedLow.gif"), "SearchMed.icon", SwingHelpUtilities.makeBasicIcon("images/SearchMed.gif"), "SearchMedHigh.icon", SwingHelpUtilities.makeBasicIcon("images/SearchMedHigh.gif"), "SearchHigh.icon", SwingHelpUtilities.makeBasicIcon("images/SearchHigh.gif")};
        uIDefaults.putDefaults(arrobject);
    }

    private static void installMetalDefaults(UIDefaults uIDefaults) {
        SwingHelpUtilities.installBasicDefaults(uIDefaults);
    }

    private static void installWindowsDefaults(UIDefaults uIDefaults) {
        SwingHelpUtilities.installBasicDefaults(uIDefaults);
    }

    private static void installMotifDefaults(UIDefaults uIDefaults) {
        SwingHelpUtilities.installBasicDefaults(uIDefaults);
    }

    private static Object makeGTKIcon(final String string) {
        return new UIDefaults.LazyValue(){

            public Object createValue(UIDefaults uIDefaults) {
                Class class_ = SwingHelpUtilities.class$javax$help$plaf$gtk$GTKCursorFactory == null ? (SwingHelpUtilities.class$javax$help$plaf$gtk$GTKCursorFactory = SwingHelpUtilities.class$("javax.help.plaf.gtk.GTKCursorFactory")) : SwingHelpUtilities.class$javax$help$plaf$gtk$GTKCursorFactory;
                return SwingHelpUtilities.getImageIcon(class_, string);
            }
        };
    }

    private static void installGTKDefaults(UIDefaults uIDefaults) {
        String string = "javax.help.plaf.basic.";
        String string2 = "javax.help.plaf.gtk.";
        String string3 = string + "BasicContentViewerUI";
        if (contentViewerUI != null) {
            string3 = contentViewerUI;
        }
        Object[] arrobject = new Object[]{"HelpUI", string + "BasicHelpUI", "HelpTOCNavigatorUI", string + "BasicTOCNavigatorUI", "HelpIndexNavigatorUI", string + "BasicIndexNavigatorUI", "HelpSearchNavigatorUI", string + "BasicSearchNavigatorUI", "HelpGlossaryNavigatorUI", string + "BasicGlossaryNavigatorUI", "HelpFavoritesNavigatorUI", string + "BasicFavoritesNavigatorUI", "HelpContentViewerUI", string3, "HelpDnDCursor", gtkDnDCursor, "HelpOnItemCursor", gtkOnItemCursor, "BackAction.icon", SwingHelpUtilities.makeGTKIcon("images/Back.png"), "ForwardAction.icon", SwingHelpUtilities.makeGTKIcon("images/Forward.png"), "PrintAction.icon", SwingHelpUtilities.makeGTKIcon("images/Print.png"), "PrintSetupAction.icon", SwingHelpUtilities.makeGTKIcon("images/PrintSetup.png"), "ReloadAction.icon", SwingHelpUtilities.makeGTKIcon("images/Reload.png"), "FavoritesAction.icon", SwingHelpUtilities.makeGTKIcon("images/Favorites.png"), "HomeAction.icon", SwingHelpUtilities.makeGTKIcon("images/Home.png"), "FavoritesNav.icon", SwingHelpUtilities.makeGTKIcon("images/FavoritesNav.png"), "IndexNav.icon", SwingHelpUtilities.makeGTKIcon("images/IndexNav.gif"), "TOCNav.icon", SwingHelpUtilities.makeGTKIcon("images/TOCNav.gif"), "SearchNav.icon", SwingHelpUtilities.makeGTKIcon("images/SearchNav.gif"), "GlossaryNav.icon", SwingHelpUtilities.makeGTKIcon("images/GlossaryNav.gif"), "HistoryNav.icon", SwingHelpUtilities.makeGTKIcon("images/HistoryNav.gif"), "SearchLow.icon", SwingHelpUtilities.makeGTKIcon("images/SearchLow.gif"), "SearchMedLow.icon", SwingHelpUtilities.makeGTKIcon("images/SearchMedLow.gif"), "SearchMed.icon", SwingHelpUtilities.makeGTKIcon("images/SearchMed.gif"), "SearchMedHigh.icon", SwingHelpUtilities.makeGTKIcon("images/SearchMedHigh.gif"), "SearchHigh.icon", SwingHelpUtilities.makeGTKIcon("images/SearchHigh.gif")};
        uIDefaults.putDefaults(arrobject);
    }

    public static ImageIcon getImageIcon(Class class_, String string) {
        if (string == null) {
            return null;
        }
        byte[][] arrarrby = new byte[1][];
        try {
            int n;
            InputStream inputStream = class_.getResourceAsStream(string);
            if (inputStream == null) {
                return null;
            }
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            arrarrby[0] = new byte[1024];
            while ((n = bufferedInputStream.read(arrarrby[0])) > 0) {
                byteArrayOutputStream.write(arrarrby[0], 0, n);
            }
            bufferedInputStream.close();
            byteArrayOutputStream.flush();
            arrarrby[0] = byteArrayOutputStream.toByteArray();
        }
        catch (IOException var3_4) {
            System.err.println(var3_4.toString());
            return null;
        }
        if (arrarrby[0] == null) {
            System.err.println(class_.getName() + "/" + string + " not found.");
            return null;
        }
        if (arrarrby[0].length == 0) {
            System.err.println("warning: " + string + " is zero-length");
            return null;
        }
        return new ImageIcon(arrarrby[0]);
    }

    static void addPropertyChangeListener(Object object, PropertyChangeListener propertyChangeListener) {
        try {
            Class[] arrclass = new Class[1];
            Class class_ = class$java$beans$PropertyChangeListener == null ? (SwingHelpUtilities.class$java$beans$PropertyChangeListener = SwingHelpUtilities.class$("java.beans.PropertyChangeListener")) : class$java$beans$PropertyChangeListener;
            arrclass[0] = class_;
            Class[] arrclass2 = arrclass;
            Object[] arrobject = new Object[]{propertyChangeListener};
            object.getClass().getMethod("addPropertyChangeListener", arrclass2).invoke(object, arrobject);
        }
        catch (Exception var2_3) {
            // empty catch block
        }
    }

    private static void debug(Object object) {
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
        SwingHelpUtilities.installUIDefaults();
        basicOnItemCursor = new UIDefaults.LazyValue(){

            public Object createValue(UIDefaults uIDefaults) {
                return SwingHelpUtilities.createIcon("javax.help.plaf.basic.BasicCursorFactory", "getOnItemCursor");
            }
        };
        basicDnDCursor = new UIDefaults.LazyValue(){

            public Object createValue(UIDefaults uIDefaults) {
                return SwingHelpUtilities.createIcon("javax.help.plaf.basic.BasicCursorFactory", "getDnDCursor");
            }
        };
        gtkOnItemCursor = new UIDefaults.LazyValue(){

            public Object createValue(UIDefaults uIDefaults) {
                return SwingHelpUtilities.createIcon("javax.help.plaf.gtk.GTKCursorFactory", "getOnItemCursor");
            }
        };
        gtkDnDCursor = new UIDefaults.LazyValue(){

            public Object createValue(UIDefaults uIDefaults) {
                return SwingHelpUtilities.createIcon("javax.help.plaf.gtk.GTKCursorFactory", "getDnDCursor");
            }
        };
    }

}

