/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import javax.help.HelpHistoryModel;
import javax.help.HelpSet;
import javax.help.JHelp;
import javax.help.JHelpContentViewer;
import javax.help.JHelpNavigator;
import javax.help.Map;
import javax.help.NavigatorView;
import javax.help.Presentation;
import javax.help.TextHelpModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public abstract class WindowPresentation
extends Presentation {
    private HelpSet.Presentation hsPres = null;
    private JFrame frame = null;
    private JHelp jhelp = null;
    private JDialog dialog = null;
    private Window ownerWindow = null;
    private boolean modallyActivated = false;
    private Point location = null;
    private String title = null;
    private Image image = null;
    private String currentView = null;
    private boolean viewDisplayed = true;
    private boolean toolbarDisplayed = true;
    private boolean destroyOnExit = false;
    private boolean titleFromDocument = false;
    private WindowPropertyChangeListener propertyChangeListener = null;
    private int screen = 0;
    WindowListener dl;
    boolean modalDeactivated = true;
    private static final boolean debug = false;
    static /* synthetic */ Class class$java$awt$Frame;
    static /* synthetic */ Class class$java$awt$Window;

    public WindowPresentation(HelpSet helpSet) {
        this.setHelpSet(helpSet);
    }

    public void setHelpSetPresentation(HelpSet.Presentation presentation) {
        String string;
        Map.ID iD;
        WindowPresentation.debug("setHelpSetPrsentation");
        if (presentation == null) {
            return;
        }
        super.setHelpSetPresentation(presentation);
        Point point = presentation.getLocation();
        if (point != null) {
            this.setLocation(point);
        }
        if ((string = presentation.getTitle()) != null) {
            this.setTitle(string);
        }
        if ((iD = presentation.getImageID()) != null) {
            ImageIcon imageIcon = null;
            try {
                Map map = this.getHelpSet().getCombinedMap();
                URL uRL = map.getURLFromID(iD);
                imageIcon = new ImageIcon(uRL);
                this.image = imageIcon.getImage();
            }
            catch (Exception var6_7) {
                // empty catch block
            }
        }
        if (presentation.isToolbar()) {
            this.setToolbarDisplayed(true);
        }
        if (presentation.isViewDisplayed()) {
            this.setViewDisplayed(true);
        }
        this.hsPres = presentation;
    }

    public HelpSet.Presentation getHelpSetPresentation() {
        return this.hsPres;
    }

    public Window getActivationWindow() {
        WindowPresentation.debug("getActivationWindow");
        return this.ownerWindow;
    }

    public void setActivationWindow(Window window) {
        WindowPresentation.debug("setActivationWindow");
        if (window != null && window instanceof Dialog) {
            Dialog dialog = (Dialog)window;
            if (dialog.isModal()) {
                this.ownerWindow = window;
                this.modallyActivated = true;
            } else {
                this.ownerWindow = null;
                this.modallyActivated = false;
            }
        } else {
            this.ownerWindow = null;
            this.modallyActivated = false;
        }
    }

    public void setActivationObject(Object object) {
        WindowPresentation.debug("setActivationObject");
        while (object instanceof MenuComponent) {
            object = ((MenuComponent)object).getParent();
        }
        Window window = null;
        if (object instanceof Frame) {
            window = (Window)object;
        } else if (object instanceof Component) {
            window = SwingUtilities.windowForComponent((Component)object);
        }
        this.setActivationWindow(window);
    }

    public String getCurrentView() {
        WindowPresentation.debug("getCurrentView");
        if (this.jhelp != null) {
            this.currentView = this.jhelp.getCurrentNavigator().getNavigatorName();
        }
        return this.currentView;
    }

    public void setCurrentView(String string) {
        WindowPresentation.debug("setCurrentView");
        if (this.jhelp != null) {
            JHelpNavigator jHelpNavigator = this.getNavigatorByName(string);
            if (jHelpNavigator == null) {
                throw new IllegalArgumentException("Invalid view name");
            }
            this.jhelp.setCurrentNavigator(jHelpNavigator);
        } else {
            HelpSet helpSet = this.getHelpSet();
            NavigatorView navigatorView = helpSet.getNavigatorView(string);
            if (navigatorView == null) {
                throw new IllegalArgumentException("Invalid view name");
            }
        }
        this.currentView = string;
    }

    private JHelpNavigator getNavigatorByName(String string) {
        JHelpNavigator jHelpNavigator = null;
        if (this.jhelp != null) {
            Enumeration enumeration = this.jhelp.getHelpNavigators();
            while (enumeration.hasMoreElements()) {
                jHelpNavigator = (JHelpNavigator)enumeration.nextElement();
                if (jHelpNavigator.getNavigatorName().equals(string)) break;
                jHelpNavigator = null;
            }
        }
        return jHelpNavigator;
    }

    public boolean isDestroyedOnExit() {
        WindowPresentation.debug("isDestoryedOnExit");
        return this.destroyOnExit;
    }

    public void setDestroyOnExit(boolean bl) {
        WindowPresentation.debug("setDestoryOnExit");
        this.destroyOnExit = bl;
    }

    public void destroy() {
        this.frame = null;
        this.jhelp = null;
        this.dialog = null;
        this.ownerWindow = null;
        this.location = null;
        this.title = null;
        this.currentView = null;
        this.propertyChangeListener = null;
        this.screen = 0;
    }

    public void setHelpSet(HelpSet helpSet) {
        WindowPresentation.debug("setHelpSet");
        HelpSet helpSet2 = super.getHelpSet();
        if (helpSet != null && helpSet2 != helpSet) {
            super.setHelpSet(helpSet);
            if (this.jhelp != null) {
                this.jhelp.setModel(super.getHelpModel());
            }
        }
    }

    public void setDisplayed(boolean bl) {
        WindowPresentation.debug("setDisplayed");
        if (this.jhelp == null && !bl) {
            return;
        }
        this.createHelpWindow();
        if (this.modallyActivated) {
            if (bl) {
                this.dialog.show();
            } else {
                this.dialog.hide();
            }
        } else {
            this.frame.setVisible(bl);
            try {
                Class[] arrclass = new Class[]{Integer.TYPE};
                Class class_ = class$java$awt$Frame == null ? (WindowPresentation.class$java$awt$Frame = WindowPresentation.class$("java.awt.Frame")) : class$java$awt$Frame;
                Method method = class_.getMethod("setState", arrclass);
                if (method != null) {
                    Object[] arrobject = new Object[]{new Integer(0)};
                    method.invoke(this.frame, arrobject);
                }
            }
            catch (NoSuchMethodError var2_3) {
            }
            catch (NoSuchMethodException var3_5) {
            }
            catch (InvocationTargetException var4_7) {
            }
            catch (IllegalAccessException var5_8) {
                // empty catch block
            }
        }
    }

    public boolean isDisplayed() {
        WindowPresentation.debug("isDisplayed");
        if (this.jhelp == null) {
            return false;
        }
        if (this.modallyActivated) {
            if (this.dialog != null) {
                return this.dialog.isShowing();
            }
            return false;
        }
        if (this.frame != null) {
            if (!this.frame.isShowing()) {
                return false;
            }
            try {
                Class class_ = class$java$awt$Frame == null ? (WindowPresentation.class$java$awt$Frame = WindowPresentation.class$("java.awt.Frame")) : class$java$awt$Frame;
                Method method = class_.getMethod("getState", null);
                if (method != null) {
                    int n = (Integer)method.invoke(this.frame, null);
                    if (n == 0) {
                        return true;
                    }
                    return false;
                }
            }
            catch (NoSuchMethodError var1_2) {
            }
            catch (NoSuchMethodException var2_4) {
            }
            catch (InvocationTargetException var3_5) {
            }
            catch (IllegalAccessException var4_6) {
                // empty catch block
            }
            return true;
        }
        return false;
    }

    public void setFont(Font font) {
        WindowPresentation.debug("setFont");
        super.setFont(font);
        if (this.jhelp != null && font != null) {
            this.jhelp.setFont(font);
        }
    }

    public Font getFont() {
        WindowPresentation.debug("getFont");
        Font font = super.getFont();
        if (font == null) {
            if (this.jhelp == null) {
                this.createHelpWindow();
            }
            return this.jhelp.getFont();
        }
        return font;
    }

    public void setLocale(Locale locale) {
        WindowPresentation.debug("setLocale");
        super.setLocale(locale);
        if (this.jhelp != null) {
            this.jhelp.setLocale(locale);
        }
    }

    private boolean isXinerama() {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] arrgraphicsDevice = graphicsEnvironment.getScreenDevices();
        if (arrgraphicsDevice.length == 1) {
            return false;
        }
        int n = 0;
        while (n < arrgraphicsDevice.length) {
            GraphicsConfiguration graphicsConfiguration = arrgraphicsDevice[n].getDefaultConfiguration();
            Rectangle rectangle = graphicsConfiguration.getBounds();
            if (rectangle.x != 0 || rectangle.y != 0) {
                return true;
            }
            ++n;
        }
        return false;
    }

    public Point getLocation() {
        WindowPresentation.debug("getLocation");
        if (this.location != null && this.jhelp == null) {
            return this.location;
        }
        if (this.jhelp == null) {
            this.createHelpWindow();
        }
        if (this.modallyActivated) {
            Point point = this.dialog.getLocation();
            if (this.isXinerama()) {
                GraphicsConfiguration graphicsConfiguration = this.dialog.getGraphicsConfiguration();
                Rectangle rectangle = graphicsConfiguration.getBounds();
                return new Point(point.x - rectangle.x, point.y - rectangle.y);
            }
            return point;
        }
        Point point = this.frame.getLocation();
        if (this.isXinerama()) {
            GraphicsConfiguration graphicsConfiguration = this.frame.getGraphicsConfiguration();
            Rectangle rectangle = graphicsConfiguration.getBounds();
            return new Point(point.x - rectangle.x, point.y - rectangle.y);
        }
        return point;
    }

    public void setLocation(Point point) {
        WindowPresentation.debug("setLocation");
        this.location = point;
        if (this.jhelp != null) {
            if (this.modallyActivated) {
                if (this.dialog != null) {
                    GraphicsConfiguration graphicsConfiguration = this.dialog.getGraphicsConfiguration();
                    Rectangle rectangle = graphicsConfiguration.getBounds();
                    Point point2 = new Point(rectangle.x + point.x, rectangle.y + point.y);
                    this.dialog.setLocation(point2);
                }
            } else if (this.frame != null) {
                GraphicsConfiguration graphicsConfiguration = this.frame.getGraphicsConfiguration();
                Rectangle rectangle = graphicsConfiguration.getBounds();
                Point point3 = new Point(rectangle.x + point.x, rectangle.y + point.y);
                this.frame.setLocation(point3);
            }
        }
    }

    public int getScreen() {
        WindowPresentation.debug("getScreen");
        if (this.jhelp == null) {
            return this.screen;
        }
        GraphicsConfiguration graphicsConfiguration = null;
        if (this.modallyActivated) {
            if (this.dialog != null) {
                graphicsConfiguration = this.dialog.getGraphicsConfiguration();
            }
        } else if (this.frame != null) {
            graphicsConfiguration = this.frame.getGraphicsConfiguration();
        }
        if (graphicsConfiguration != null) {
            GraphicsDevice graphicsDevice = graphicsConfiguration.getDevice();
            GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] arrgraphicsDevice = graphicsEnvironment.getScreenDevices();
            int n = 0;
            while (n < arrgraphicsDevice.length) {
                if (arrgraphicsDevice[n] == graphicsDevice) {
                    this.screen = n;
                    return this.screen;
                }
                ++n;
            }
        }
        return this.screen;
    }

    public void setScreen(int n) {
        WindowPresentation.debug("setScreen");
        if (n == this.screen) {
            return;
        }
        if (n < 0) {
            throw new IllegalArgumentException("Invalid screen");
        }
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] arrgraphicsDevice = graphicsEnvironment.getScreenDevices();
        if (arrgraphicsDevice.length <= n) {
            throw new IllegalArgumentException("Invalid Screen");
        }
        this.screen = n;
        if (this.jhelp != null) {
            boolean bl = this.isXinerama();
            GraphicsDevice graphicsDevice = arrgraphicsDevice[n];
            GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
            Rectangle rectangle = graphicsConfiguration.getBounds();
            if (this.modallyActivated) {
                if (this.dialog != null) {
                    if (bl) {
                        Point point = this.getLocation();
                        Point point2 = new Point(rectangle.x + point.x, rectangle.y + point.y);
                        this.dialog.setLocation(point2);
                    } else {
                        this.location = this.getLocation();
                        this.dialog.hide();
                        this.dialog = null;
                        this.createHelpWindow();
                    }
                }
            } else if (this.frame != null) {
                if (bl) {
                    Point point = this.getLocation();
                    Point point3 = new Point(rectangle.x + point.x, rectangle.y + point.y);
                    this.frame.setLocation(point3);
                } else {
                    this.location = this.getLocation();
                    this.frame.setVisible(false);
                    this.frame = null;
                    this.createHelpWindow();
                }
            }
        }
    }

    public Dimension getSize() {
        WindowPresentation.debug("getSize");
        if (this.jhelp != null) {
            if (this.modallyActivated) {
                if (this.dialog != null) {
                    return this.dialog.getSize();
                }
            } else if (this.frame != null) {
                return this.frame.getSize();
            }
        }
        return super.getSize();
    }

    public void setSize(Dimension dimension) {
        WindowPresentation.debug("setSize");
        super.setSize(dimension);
        if (this.jhelp != null) {
            if (this.modallyActivated) {
                this.dialog.setSize(dimension);
                this.dialog.validate();
            } else {
                this.frame.setSize(dimension);
                this.frame.validate();
            }
        }
    }

    public String getTitle() {
        Object object;
        WindowPresentation.debug("getTitle");
        if (this.titleFromDocument && this.jhelp != null && (object = this.jhelp.getContentViewer().getDocumentTitle()) != null) {
            return object;
        }
        if (this.title != null) {
            return this.title;
        }
        object = this.getHelpSet();
        if (object != null) {
            this.title = object.getTitle();
        }
        return this.title;
    }

    public void setTitle(String string) {
        WindowPresentation.debug("setTitle");
        this.title = string;
        if (this.jhelp != null) {
            if (this.modallyActivated) {
                this.dialog.setTitle(string);
                this.dialog.validate();
            } else {
                this.frame.setTitle(string);
                this.frame.validate();
            }
        }
    }

    public boolean isTitleSetFromDocument() {
        WindowPresentation.debug("isTitleSetFromDocument");
        return this.titleFromDocument;
    }

    public void setTitleFromDocument(boolean bl) {
        WindowPresentation.debug("setTitleFromDocument");
        if (this.titleFromDocument != bl) {
            this.titleFromDocument = bl;
            if (this.titleFromDocument) {
                this.propertyChangeListener = new WindowPropertyChangeListener();
                if (this.jhelp != null) {
                    this.jhelp.getContentViewer().addPropertyChangeListener("page", this.propertyChangeListener);
                }
            } else if (this.jhelp != null) {
                this.jhelp.getContentViewer().removePropertyChangeListener("page", this.propertyChangeListener);
            }
        }
    }

    public boolean isViewDisplayed() {
        WindowPresentation.debug("isViewDisplayed");
        if (this.jhelp != null) {
            return this.jhelp.isNavigatorDisplayed();
        }
        return this.viewDisplayed;
    }

    public void setViewDisplayed(boolean bl) {
        WindowPresentation.debug("setViewDisplayed");
        if (this.jhelp != null) {
            this.jhelp.setNavigatorDisplayed(bl);
        }
        this.viewDisplayed = bl;
    }

    public boolean isToolbarDisplayed() {
        WindowPresentation.debug("isToolbarDisplayed");
        if (this.jhelp != null) {
            return this.jhelp.isToolbarDisplayed();
        }
        return this.toolbarDisplayed;
    }

    public void setToolbarDisplayed(boolean bl) {
        WindowPresentation.debug("setToolbarDisplayed=" + bl);
        if (this.jhelp != null) {
            this.jhelp.setToolbarDisplayed(bl);
        }
        this.toolbarDisplayed = bl;
    }

    private synchronized void createJHelp() {
        WindowPresentation.debug("createJHelp");
        if (this.jhelp == null) {
            Locale locale;
            JHelpNavigator jHelpNavigator;
            this.jhelp = new JHelp(this.getHelpModel(), null, this.getHelpSetPresentation());
            Font font = super.getFont();
            if (font != null) {
                this.jhelp.setFont(font);
            }
            if ((locale = this.getLocale()) != null) {
                this.jhelp.setLocale(locale);
            }
            this.jhelp.setToolbarDisplayed(this.toolbarDisplayed);
            this.jhelp.setNavigatorDisplayed(this.viewDisplayed);
            if (this.currentView != null && (jHelpNavigator = this.getNavigatorByName(this.currentView)) != null) {
                this.jhelp.setCurrentNavigator(jHelpNavigator);
            }
            if (this.titleFromDocument) {
                this.jhelp.getContentViewer().addPropertyChangeListener("page", this.propertyChangeListener);
            }
        }
    }

    public synchronized void createHelpWindow() {
        WindowPresentation.debug("createHelpWindow");
        Point point = null;
        Dimension dimension = this.getSize();
        JDialog jDialog = null;
        this.createJHelp();
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] arrgraphicsDevice = graphicsEnvironment.getScreenDevices();
        GraphicsDevice graphicsDevice = arrgraphicsDevice[this.screen];
        GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
        Rectangle rectangle = graphicsConfiguration.getBounds();
        if (this.modallyActivated) {
            Object object;
            Window window = null;
            try {
                Class class_ = class$java$awt$Window == null ? (WindowPresentation.class$java$awt$Window = WindowPresentation.class$("java.awt.Window")) : class$java$awt$Window;
                object = class_.getMethod("getOwner", null);
                if (object != null && this.dialog != null) {
                    window = (Window)object.invoke(this.dialog, null);
                }
            }
            catch (NoSuchMethodError var10_12) {
            }
            catch (NoSuchMethodException var11_13) {
            }
            catch (InvocationTargetException var12_14) {
            }
            catch (IllegalAccessException var13_15) {
                // empty catch block
            }
            if (this.dialog == null || window != this.ownerWindow || this.modalDeactivated) {
                if (this.frame != null) {
                    point = this.frame.getLocation();
                    dimension = this.frame.getSize();
                    this.frame = null;
                }
                if (this.dialog != null) {
                    point = this.dialog.getLocation();
                    dimension = this.dialog.getSize();
                    jDialog = this.dialog;
                }
                this.dialog = new JDialog((Dialog)this.ownerWindow, this.getTitle(), false, graphicsConfiguration);
                this.dl = new WindowAdapter(){

                    public void windowClosing(WindowEvent windowEvent) {
                        WindowPresentation.debug("modal window closing");
                        if (WindowPresentation.this.destroyOnExit) {
                            WindowPresentation.this.destroy();
                            return;
                        }
                        if (WindowPresentation.this.dialog.isShowing()) {
                            WindowPresentation.this.dialog.hide();
                        }
                        if (WindowPresentation.this.ownerWindow != null) {
                            WindowPresentation.this.ownerWindow.removeWindowListener(WindowPresentation.this.dl);
                        }
                        WindowPresentation.this.ownerWindow = null;
                        WindowPresentation.this.modalDeactivated = true;
                    }

                    public void windowClosed(WindowEvent windowEvent) {
                        WindowPresentation.debug("modal window closing");
                        if (WindowPresentation.this.destroyOnExit) {
                            WindowPresentation.this.destroy();
                            return;
                        }
                    }
                };
                WindowPresentation.debug("adding windowlistener");
                this.ownerWindow.addWindowListener(this.dl);
                this.modalDeactivated = false;
                if (dimension != null) {
                    this.dialog.setSize(dimension);
                } else {
                    this.dialog.setSize(this.getSize());
                }
                if (point != null) {
                    this.dialog.setLocation(point);
                } else {
                    object = null;
                    if (this.location != null) {
                        object = this.isXinerama() ? new Point(rectangle.x + this.location.x, rectangle.y + this.location.y) : this.location;
                        this.dialog.setLocation((Point)object);
                    }
                }
                this.dialog.setTitle(this.getTitle());
                this.dialog.getContentPane().add(this.jhelp);
                if (jDialog != null) {
                    jDialog.hide();
                    jDialog = null;
                }
            }
        } else {
            Object object;
            if (this.frame == null) {
                this.frame = new JFrame(this.getTitle(), graphicsConfiguration);
                object = new WindowAdapter(){

                    public void windowClosing(WindowEvent windowEvent) {
                        if (WindowPresentation.this.destroyOnExit) {
                            WindowPresentation.this.destroy();
                            return;
                        }
                        WindowPresentation.this.frame.setVisible(false);
                    }

                    public void windowClosed(WindowEvent windowEvent) {
                        WindowPresentation.this.frame.setVisible(false);
                        if (WindowPresentation.this.destroyOnExit) {
                            WindowPresentation.this.destroy();
                            return;
                        }
                    }
                };
                this.frame.addWindowListener((WindowListener)object);
                if (this.image != null) {
                    this.frame.setIconImage(this.image);
                }
            }
            if (this.dialog != null) {
                point = this.dialog.getLocation();
                dimension = this.dialog.getSize();
                this.dialog.hide();
                this.dialog = null;
                this.ownerWindow = null;
            }
            if (dimension != null) {
                this.frame.setSize(dimension);
            } else {
                this.frame.setSize(this.getSize());
            }
            if (point != null) {
                this.frame.setLocation(point);
            } else {
                object = null;
                if (this.location != null) {
                    object = this.isXinerama() ? new Point(rectangle.x + this.location.x, rectangle.y + this.location.y) : this.location;
                    this.frame.setLocation((Point)object);
                }
            }
            this.frame.getContentPane().add(this.jhelp);
            this.frame.setTitle(this.getTitle());
        }
    }

    public Window getHelpWindow() {
        if (this.modallyActivated) {
            return this.dialog;
        }
        return this.frame;
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

    private class WindowPropertyChangeListener
    implements PropertyChangeListener {
        private WindowPropertyChangeListener() {
        }

        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            String string = propertyChangeEvent.getPropertyName();
            if (string.equals("page")) {
                String string2 = WindowPresentation.this.getTitle();
                if (WindowPresentation.this.modallyActivated) {
                    WindowPresentation.this.dialog.setTitle(string2);
                } else {
                    WindowPresentation.this.frame.setTitle(string2);
                }
            }
        }
    }

}

