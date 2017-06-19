/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Locale;
import javax.help.BadIDException;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.InvalidHelpSetContextException;
import javax.help.MainWindow;
import javax.help.Map;
import javax.help.Popup;
import javax.help.Presentation;
import javax.help.SwingHelpUtilities;
import javax.help.UnsupportedOperationException;
import javax.help.WindowPresentation;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class DefaultHelpBroker
implements HelpBroker,
KeyListener {
    private MainWindow mw = null;
    private HelpSet helpKeyHS = null;
    private String helpKeyPresentation = null;
    private String helpKeyPresentationName = null;
    protected ActionListener displayHelpFromFocus;
    protected ActionListener displayHelpFromSource;
    private static final boolean debug = false;
    static /* synthetic */ Class class$javax$help$HelpSet;
    static /* synthetic */ Class class$java$lang$String;

    public DefaultHelpBroker(HelpSet helpSet) {
        this.mw = (MainWindow)MainWindow.getPresentation(helpSet, null);
    }

    public DefaultHelpBroker() {
        this.mw = (MainWindow)MainWindow.getPresentation(null, null);
    }

    public WindowPresentation getWindowPresentation() {
        return this.mw;
    }

    public HelpSet getHelpSet() {
        return this.mw.getHelpSet();
    }

    public void setHelpSet(HelpSet helpSet) {
        DefaultHelpBroker.debug("setHelpSet");
        this.mw.setHelpSet(helpSet);
    }

    public void setHelpSetPresentation(HelpSet.Presentation presentation) {
        DefaultHelpBroker.debug("setHelpSetPresentation");
        this.mw.setHelpSetPresentation(presentation);
    }

    public Locale getLocale() {
        return this.mw.getLocale();
    }

    public void setLocale(Locale locale) {
        this.mw.setLocale(locale);
    }

    public Font getFont() {
        return this.mw.getFont();
    }

    public void setFont(Font font) {
        this.mw.setFont(font);
    }

    public void setCurrentView(String string) {
        this.mw.setCurrentView(string);
    }

    public String getCurrentView() {
        return this.mw.getCurrentView();
    }

    public void initPresentation() {
        this.mw.createHelpWindow();
    }

    public void setDisplayed(boolean bl) {
        DefaultHelpBroker.debug("setDisplayed");
        this.mw.setDisplayed(bl);
    }

    public boolean isDisplayed() {
        return this.mw.isDisplayed();
    }

    public void setLocation(Point point) {
        this.mw.setLocation(point);
    }

    public Point getLocation() {
        return this.mw.getLocation();
    }

    public void setSize(Dimension dimension) {
        this.mw.setSize(dimension);
    }

    public Dimension getSize() throws UnsupportedOperationException {
        return this.mw.getSize();
    }

    public void setScreen(int n) {
        this.mw.setScreen(n);
    }

    public int getScreen() throws UnsupportedOperationException {
        return this.mw.getScreen();
    }

    public void setViewDisplayed(boolean bl) {
        this.mw.setViewDisplayed(bl);
    }

    public boolean isViewDisplayed() {
        return this.mw.isViewDisplayed();
    }

    public void showID(String string, String string2, String string3) throws BadIDException {
        DefaultHelpBroker.debug("showID - string");
        Presentation presentation = this.getPresentation(string2, string3);
        if (presentation != null) {
            presentation.setCurrentID(string);
            presentation.setDisplayed(true);
        }
    }

    public void showID(Map.ID iD, String string, String string2) throws InvalidHelpSetContextException {
        DefaultHelpBroker.debug("showID - ID");
        Presentation presentation = this.getPresentation(string, string2);
        if (presentation != null) {
            presentation.setCurrentID(iD);
            presentation.setDisplayed(true);
        }
    }

    private Presentation getPresentation(String string, String string2) {
        Presentation presentation;
        HelpSet helpSet = this.mw.getHelpSet();
        if (helpSet == null) {
            return null;
        }
        Class[] arrclass = new Class[2];
        Class class_ = class$javax$help$HelpSet == null ? (DefaultHelpBroker.class$javax$help$HelpSet = DefaultHelpBroker.class$("javax.help.HelpSet")) : class$javax$help$HelpSet;
        arrclass[0] = class_;
        Class class_2 = class$java$lang$String == null ? (DefaultHelpBroker.class$java$lang$String = DefaultHelpBroker.class$("java.lang.String")) : class$java$lang$String;
        arrclass[1] = class_2;
        Class[] arrclass2 = arrclass;
        Object[] arrobject = new Object[]{helpSet, string2};
        try {
            ClassLoader classLoader = helpSet.getLoader();
            Class class_3 = classLoader == null ? Class.forName(string) : classLoader.loadClass(string);
            Method method = class_3.getMethod("getPresentation", arrclass2);
            presentation = (Presentation)method.invoke(null, arrobject);
        }
        catch (ClassNotFoundException var9_9) {
            throw new IllegalArgumentException(string + "presentation  invalid");
        }
        catch (Exception var10_11) {
            throw new RuntimeException("error invoking presentation");
        }
        if (presentation == null) {
            return null;
        }
        if (presentation instanceof Popup) {
            return null;
        }
        return presentation;
    }

    public void setCurrentID(String string) throws BadIDException {
        DefaultHelpBroker.debug("setCurrentID - string");
        this.mw.setCurrentID(string);
    }

    public void setCurrentID(Map.ID iD) throws InvalidHelpSetContextException {
        DefaultHelpBroker.debug("setCurrentID - ID");
        this.mw.setCurrentID(iD);
    }

    public Map.ID getCurrentID() {
        return this.mw.getCurrentID();
    }

    public void setCurrentURL(URL uRL) {
        DefaultHelpBroker.debug("setCurrentURL");
        this.mw.setCurrentURL(uRL);
    }

    public URL getCurrentURL() {
        return this.mw.getCurrentURL();
    }

    public void enableHelpKey(Component component, String string, HelpSet helpSet) {
        this.enableHelpKey(component, string, helpSet, null, null);
    }

    public void enableHelpKey(Component component, String string, HelpSet helpSet, String string2, String string3) {
        if (string == null) {
            throw new NullPointerException("id");
        }
        if (string2 != null && helpSet == null) {
            throw new IllegalArgumentException("hs");
        }
        CSH.setHelpIDString(component, string);
        if (helpSet != null) {
            CSH.setHelpSet(component, helpSet);
        }
        if (component instanceof JComponent) {
            JComponent jComponent = (JComponent)component;
            ActionListener actionListener = null;
            actionListener = string2 == null ? this.getDisplayHelpFromFocus() : new CSH.DisplayHelpFromFocus(helpSet, string2, string3);
            jComponent.registerKeyboardAction(actionListener, KeyStroke.getKeyStroke(156, 0), 1);
            jComponent.registerKeyboardAction(actionListener, KeyStroke.getKeyStroke(112, 0), 1);
        } else {
            this.helpKeyHS = helpSet;
            this.helpKeyPresentation = string2;
            this.helpKeyPresentationName = string3;
            component.addKeyListener(this);
        }
    }

    public void keyTyped(KeyEvent keyEvent) {
    }

    public void keyPressed(KeyEvent keyEvent) {
    }

    public void keyReleased(KeyEvent keyEvent) {
        int n = keyEvent.getKeyCode();
        if (n == 112 || n == 156) {
            ActionListener actionListener = null;
            actionListener = this.helpKeyHS != null ? new CSH.DisplayHelpFromFocus(this.helpKeyHS, this.helpKeyPresentation, this.helpKeyPresentationName) : this.getDisplayHelpFromFocus();
            actionListener.actionPerformed(new ActionEvent(keyEvent.getComponent(), 1001, null));
        }
    }

    public void enableHelp(Component component, String string, HelpSet helpSet) {
        if (string == null) {
            throw new NullPointerException("id");
        }
        CSH.setHelpIDString(component, string);
        if (helpSet != null) {
            CSH.setHelpSet(component, helpSet);
        }
    }

    public void enableHelp(MenuItem menuItem, String string, HelpSet helpSet) {
        if (string == null) {
            throw new NullPointerException("id");
        }
        CSH.setHelpIDString(menuItem, string);
        if (helpSet != null) {
            CSH.setHelpSet(menuItem, helpSet);
        }
    }

    public void enableHelpOnButton(Component component, String string, HelpSet helpSet) {
        if (!(component instanceof AbstractButton) && !(component instanceof Button)) {
            throw new IllegalArgumentException("Invalid Component");
        }
        if (string == null) {
            throw new NullPointerException("id");
        }
        CSH.setHelpIDString(component, string);
        if (helpSet != null) {
            CSH.setHelpSet(component, helpSet);
        }
        if (component instanceof AbstractButton) {
            AbstractButton abstractButton = (AbstractButton)component;
            abstractButton.addActionListener(this.getDisplayHelpFromSource());
        } else if (component instanceof Button) {
            Button button = (Button)component;
            button.addActionListener(this.getDisplayHelpFromSource());
        }
    }

    public void enableHelpOnButton(MenuItem menuItem, String string, HelpSet helpSet) {
        if (menuItem == null) {
            throw new IllegalArgumentException("Invalid Component");
        }
        if (string == null) {
            throw new NullPointerException("id");
        }
        CSH.setHelpIDString(menuItem, string);
        if (helpSet != null) {
            CSH.setHelpSet(menuItem, helpSet);
        }
        menuItem.addActionListener(this.getDisplayHelpFromSource());
    }

    public void enableHelpOnButton(Object object, String string, HelpSet helpSet, String string2, String string3) {
        if (!(object instanceof AbstractButton || object instanceof Button || object instanceof MenuItem)) {
            throw new IllegalArgumentException("Invalid Object");
        }
        if (string == null) {
            throw new NullPointerException("id");
        }
        if (object instanceof AbstractButton || object instanceof Button) {
            CSH.setHelpIDString((Component)object, string);
            if (helpSet != null) {
                CSH.setHelpSet((Component)object, helpSet);
            }
        } else {
            CSH.setHelpIDString((MenuItem)object, string);
            if (helpSet != null) {
                CSH.setHelpSet((MenuItem)object, helpSet);
            }
        }
        if (string2 == null) {
            if (object instanceof AbstractButton) {
                AbstractButton abstractButton = (AbstractButton)object;
                abstractButton.addActionListener(this.getDisplayHelpFromSource());
            } else if (object instanceof Button) {
                Button button = (Button)object;
                button.addActionListener(this.getDisplayHelpFromSource());
            } else if (object instanceof MenuItem) {
                MenuItem menuItem = (MenuItem)object;
                menuItem.addActionListener(this.getDisplayHelpFromSource());
            }
        } else if (object instanceof AbstractButton) {
            AbstractButton abstractButton = (AbstractButton)object;
            abstractButton.addActionListener(new CSH.DisplayHelpFromSource(helpSet, string2, string3));
        } else if (object instanceof Button) {
            Button button = (Button)object;
            button.addActionListener(new CSH.DisplayHelpFromSource(helpSet, string2, string3));
        } else if (object instanceof MenuItem) {
            MenuItem menuItem = (MenuItem)object;
            menuItem.addActionListener(new CSH.DisplayHelpFromSource(helpSet, string2, string3));
        }
    }

    protected ActionListener getDisplayHelpFromFocus() {
        if (this.displayHelpFromFocus == null) {
            this.displayHelpFromFocus = new CSH.DisplayHelpFromFocus(this);
        }
        return this.displayHelpFromFocus;
    }

    protected ActionListener getDisplayHelpFromSource() {
        if (this.displayHelpFromSource == null) {
            this.displayHelpFromSource = new CSH.DisplayHelpFromSource(this);
        }
        return this.displayHelpFromSource;
    }

    public void setActivationObject(Object object) {
        this.mw.setActivationObject(object);
    }

    public void setActivationWindow(Window window) {
        this.mw.setActivationWindow(window);
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
        SwingHelpUtilities.installLookAndFeelDefaults();
    }
}

