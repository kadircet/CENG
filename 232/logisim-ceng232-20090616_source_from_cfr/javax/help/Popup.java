/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Method;
import java.util.Vector;
import javax.help.HelpSet;
import javax.help.JHelpContentViewer;
import javax.help.Presentation;
import javax.help.TextHelpModel;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

public class Popup
extends Presentation
implements ActionListener {
    private JHelpContentViewer jheditor;
    private Component invoker = null;
    private JWindow window = null;
    private Vector grabbed = null;
    private PopupComponentAdapter pca = null;
    private PopupKeyAdapter pka = null;
    private PopupMouseAdapter pma = null;
    private PopupMouseWheelListener pmwl = null;
    private PopupWindowAdapter pwa = null;
    private Rectangle internalBounds = null;
    private static final String SHOW = "show";
    private static final String CANCEL = "cancel";
    private static boolean on1dot4 = false;
    static Popup currentPopup;
    private static final boolean debug = false;
    static /* synthetic */ Class class$javax$swing$JWindow;

    private Popup(HelpSet helpSet) {
        this.setHelpSet(helpSet);
    }

    public static Presentation getPresentation(HelpSet helpSet, String string) {
        Popup popup = new Popup(helpSet);
        if (helpSet != null) {
            HelpSet.Presentation presentation = null;
            if (string != null) {
                presentation = helpSet.getPresentation(string);
            }
            if (presentation == null) {
                presentation = helpSet.getDefaultPresentation();
            }
            popup.setHelpSetPresentation(presentation);
        }
        return popup;
    }

    public Component getInvoker() {
        return this.invoker;
    }

    public void setInvoker(Component component) {
        this.invoker = component;
        if (component instanceof JMenuItem) {
            while (!(this.invoker instanceof JMenu)) {
                this.invoker = this.invoker.getParent();
                if (!(this.invoker instanceof JPopupMenu)) continue;
                this.invoker = ((JPopupMenu)this.invoker).getInvoker();
            }
        }
        if (this.invoker == null) {
            throw new IllegalArgumentException("invoker");
        }
    }

    public Rectangle getInvokerInternalBounds() {
        return this.internalBounds;
    }

    public void setInvokerInternalBounds(Rectangle rectangle) {
        this.internalBounds = rectangle;
    }

    private JWindow getWindow() {
        return this.window;
    }

    public void setDisplayed(boolean bl) {
        Class[] arrclass;
        Container container = this.getTopMostContainer();
        if (this.window == null && !bl) {
            return;
        }
        if (this.window == null) {
            this.window = new JWindow((Window)this.getTopMostContainer());
            this.jheditor = new JHelpContentViewer(this.getHelpModel());
            this.window.getRootPane().setBorder(BorderFactory.createLineBorder(Color.black, 2));
            this.window.getContentPane().add((Component)this.jheditor, "Center");
        }
        if (this.grabbed == null) {
            this.grabbed = new Vector();
            this.pca = new PopupComponentAdapter();
            this.pma = new PopupMouseAdapter();
            this.pwa = new PopupWindowAdapter();
            this.pka = new PopupKeyAdapter();
            if (on1dot4) {
                this.pmwl = new PopupMouseWheelListener();
            }
        } else {
            this.grabbed.clear();
        }
        this.grabContainer(container);
        this.window.addWindowListener(this.pwa);
        if (on1dot4) {
            try {
                arrclass = new Class[]{Class.forName("java.awt.event.WindowFocusListener")};
                Object[] arrobject = new Object[]{this.pwa};
                Method method = this.window.getClass().getMethod("addWindowFocusListener", arrclass);
                method.invoke(this.window, arrobject);
            }
            catch (Exception var3_4) {
                // empty catch block
            }
        }
        if ((arrclass = this.getRootPane()) != null) {
            arrclass.registerKeyboardAction(this, "cancel", KeyStroke.getKeyStroke(27, 0), 1);
        } else if (container != null) {
            container.addKeyListener(this.pka);
        }
        this.window.getRootPane().registerKeyboardAction(this, "cancel", KeyStroke.getKeyStroke(27, 0), 1);
        if (bl) {
            this.showPopup();
        } else {
            this.cancelPopup();
        }
    }

    public boolean isDisplayed() {
        if (this.window != null) {
            return true;
        }
        return false;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String string = actionEvent.getActionCommand();
        if (string == "cancel") {
            this.cancelPopup();
        }
    }

    private void showPopup() {
        if (currentPopup != null) {
            Class class_ = class$javax$swing$JWindow == null ? (Popup.class$javax$swing$JWindow = Popup.class$("javax.swing.JWindow")) : class$javax$swing$JWindow;
            if (SwingUtilities.getAncestorOfClass(class_, this.invoker) == currentPopup.getWindow()) {
                this.setInvoker(currentPopup.getInvoker());
            }
        }
        Rectangle rectangle = this.computePopupBounds(this.getSize());
        this.jheditor.setPreferredSize(new Dimension(rectangle.width, rectangle.height));
        this.window.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        this.window.pack();
        this.window.show();
        currentPopup = this;
    }

    private Rectangle computePopupBounds(Dimension dimension) {
        Point point;
        Rectangle rectangle;
        boolean bl = this.inModalDialog();
        if (bl) {
            Dialog dialog = this.getDialog();
            if (dialog instanceof JDialog) {
                JRootPane jRootPane = ((JDialog)dialog).getRootPane();
                point = jRootPane.getLocationOnScreen();
                rectangle = jRootPane.getBounds();
                rectangle.x = point.x;
                rectangle.y = point.y;
            } else {
                rectangle = dialog.getBounds();
            }
        } else {
            Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
            rectangle = new Rectangle(0, 0, dimension2.width, dimension2.height);
        }
        if (this.internalBounds == null) {
            this.internalBounds = this.invoker.getBounds();
        }
        Popup.debug("\nabsBounds=" + rectangle + "\ninternalBounds=" + this.internalBounds + "\n");
        point = new Point(this.internalBounds.x, this.internalBounds.y + this.internalBounds.height);
        SwingUtilities.convertPointToScreen(point, this.invoker);
        Rectangle rectangle2 = new Rectangle(point.x, point.y, dimension.width, dimension.height);
        Popup.debug("below/right " + rectangle2 + "\n");
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle2)) {
            return rectangle2;
        }
        Rectangle rectangle3 = new Rectangle(rectangle2);
        rectangle3.setLocation(rectangle2.x + this.internalBounds.width - rectangle2.width, rectangle2.y);
        Popup.debug("below/right adjust " + rectangle3 + "\n");
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle3)) {
            return rectangle3;
        }
        Rectangle rectangle4 = new Rectangle(rectangle2.x, rectangle2.y - (rectangle2.height + this.internalBounds.height), rectangle2.width, rectangle2.height);
        Popup.debug("above/right " + rectangle4 + "\n");
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle4)) {
            return rectangle4;
        }
        Rectangle rectangle5 = new Rectangle(rectangle4);
        rectangle5.setLocation(rectangle4.x + this.internalBounds.width - rectangle4.width, rectangle5.y);
        Popup.debug("above/right adjust " + rectangle5 + "\n");
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle5)) {
            return rectangle5;
        }
        point = new Point(this.internalBounds.x + this.internalBounds.width, this.internalBounds.y);
        SwingUtilities.convertPointToScreen(point, this.invoker);
        Rectangle rectangle6 = new Rectangle(point.x, point.y, dimension.width, dimension.height);
        Popup.debug("right/below " + rectangle6 + "\n");
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle6)) {
            return rectangle6;
        }
        Rectangle rectangle7 = new Rectangle(rectangle6);
        rectangle7.setLocation(rectangle7.x, rectangle6.y + this.internalBounds.height - rectangle6.height);
        Popup.debug("right/below adjust " + rectangle7 + "\n");
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle7)) {
            return rectangle7;
        }
        Rectangle rectangle8 = new Rectangle(rectangle6.x - (rectangle6.width + this.internalBounds.width), rectangle6.y, rectangle6.width, rectangle6.height);
        Popup.debug("left/below " + rectangle8 + "\n");
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle8)) {
            return rectangle8;
        }
        Rectangle rectangle9 = new Rectangle(rectangle8);
        rectangle9.setLocation(rectangle9.x, rectangle8.y + this.internalBounds.height - rectangle8.height);
        Popup.debug("left/below adjust " + rectangle9 + "\n");
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle9)) {
            return rectangle9;
        }
        Rectangle rectangle10 = new Rectangle(0, 0, dimension.width, dimension.height);
        Popup.debug("upper left hand corner " + rectangle10 + "\n");
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle10)) {
            return rectangle10;
        }
        SwingUtilities.computeIntersection(rectangle.x, rectangle.y, rectangle.width, rectangle.height, rectangle10);
        return rectangle10;
    }

    private Container getTopMostContainer() {
        if (this.invoker == null) {
            return null;
        }
        Container container = this.invoker instanceof Container ? (Container)this.invoker : this.invoker.getParent();
        while (container != null) {
            Component component;
            if (container instanceof JPopupMenu && (component = ((JPopupMenu)container).getInvoker()) instanceof Container) {
                container = (Container)component;
            }
            if (container instanceof Window || container instanceof Applet) {
                return container;
            }
            container = container.getParent();
        }
        return null;
    }

    private JRootPane getRootPane() {
        Container container = null;
        JRootPane jRootPane = null;
        if (this.invoker == null || !(this.invoker instanceof JComponent)) {
            return null;
        }
        jRootPane = ((JComponent)this.invoker).getRootPane();
        if (jRootPane != null) {
            return jRootPane;
        }
        container = this.invoker instanceof Container ? (Container)this.invoker : this.invoker.getParent();
        while (!(container == null || container instanceof JDialog || container instanceof JWindow || container instanceof JFrame)) {
            Component component;
            if (container instanceof JPopupMenu && (component = ((JPopupMenu)container).getInvoker()) instanceof Container) {
                container = (Container)component;
            }
            container = container.getParent();
        }
        if (container instanceof JFrame) {
            jRootPane = ((JFrame)container).getRootPane();
        } else if (container instanceof JWindow) {
            jRootPane = ((JWindow)container).getRootPane();
        } else if (container instanceof JDialog) {
            jRootPane = ((JDialog)container).getRootPane();
        }
        return jRootPane;
    }

    private Dialog getDialog() {
        if (this.invoker == null) {
            return null;
        }
        Container container = this.invoker instanceof Container ? (Container)this.invoker : this.invoker.getParent();
        while (container != null && !(container instanceof Dialog)) {
            container = container.getParent();
        }
        if (container instanceof Dialog) {
            return (Dialog)container;
        }
        return null;
    }

    private boolean inModalDialog() {
        return this.getDialog() != null;
    }

    private void grabContainer(Container container) {
        if (container instanceof Window) {
            ((Window)container).addWindowListener(this.pwa);
            ((Window)container).addComponentListener(this.pca);
            this.grabbed.addElement(container);
        }
        Object object = container.getTreeLock();
        synchronized (object) {
            int n = container.getComponentCount();
            Component[] arrcomponent = container.getComponents();
            int n2 = 0;
            while (n2 < n) {
                Component component = arrcomponent[n2];
                if (component.isVisible()) {
                    Object object2;
                    component.addMouseListener(this.pma);
                    component.addMouseMotionListener(this.pma);
                    if (on1dot4) {
                        try {
                            object2 = new Class[]{Class.forName("java.awt.event.MouseWheelListener")};
                            Object[] arrobject = new Object[]{this.pmwl};
                            Method method = this.window.getClass().getMethod("addMouseWheelListener", object2);
                            method.invoke(component, arrobject);
                        }
                        catch (Exception var7_8) {
                            // empty catch block
                        }
                    }
                    this.grabbed.addElement(component);
                    if (component instanceof Container) {
                        object2 = (Container)component;
                        this.grabContainer((Container)object2);
                    }
                }
                ++n2;
            }
        }
    }

    void ungrabContainers() {
        int n = 0;
        int n2 = this.grabbed.size();
        while (n < n2) {
            Component component = (Component)this.grabbed.elementAt(n);
            if (component instanceof Window) {
                ((Window)component).removeWindowListener(this.pwa);
                ((Window)component).removeComponentListener(this.pca);
            } else {
                component.removeMouseListener(this.pma);
                component.removeMouseMotionListener(this.pma);
                if (on1dot4) {
                    try {
                        Class[] arrclass = new Class[]{Class.forName("java.awt.event.MouseWheelListener")};
                        Object[] arrobject = new Object[]{this.pmwl};
                        Method method = this.window.getClass().getMethod("removeMouseWheelListener", arrclass);
                        method.invoke(component, arrobject);
                    }
                    catch (Exception var4_5) {
                        // empty catch block
                    }
                }
            }
            ++n;
        }
        this.grabbed = null;
    }

    private void cancelPopup() {
        Container container = this.getTopMostContainer();
        this.ungrabContainers();
        JRootPane jRootPane = this.getRootPane();
        if (jRootPane != null) {
            jRootPane.unregisterKeyboardAction(KeyStroke.getKeyStroke(27, 0));
        } else if (container != null) {
            container.removeKeyListener(this.pka);
        }
        this.window.removeWindowListener(this.pwa);
        if (on1dot4) {
            try {
                Class[] arrclass = new Class[]{Class.forName("java.awt.event.WindowFocusListener")};
                Object[] arrobject = new Object[]{this.pwa};
                Method method = this.window.getClass().getMethod("removeWindowFocusListener", arrclass);
                method.invoke(this.window, arrobject);
            }
            catch (Exception var3_4) {
                // empty catch block
            }
        }
        this.window.getRootPane().unregisterKeyboardAction(KeyStroke.getKeyStroke(27, 0));
        this.window.dispose();
        this.window = null;
        this.jheditor = null;
        currentPopup = null;
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
        try {
            Class class_ = Class.forName("java.awt.event.MouseWheelEvent");
            on1dot4 = class_ == null;
        }
        catch (ClassNotFoundException var0_1) {
            on1dot4 = false;
        }
        currentPopup = null;
    }

    private class PopupKeyAdapter
    extends KeyAdapter {
        private PopupKeyAdapter() {
        }

        public void keyReleased(KeyEvent keyEvent) {
            int n = keyEvent.getKeyCode();
            if (n == 27) {
                Popup.this.cancelPopup();
            }
        }
    }

    private class PopupComponentAdapter
    extends ComponentAdapter {
        private PopupComponentAdapter() {
        }

        public void componentResized(ComponentEvent componentEvent) {
            Popup.this.cancelPopup();
        }

        public void componentMoved(ComponentEvent componentEvent) {
            Popup.this.cancelPopup();
        }

        public void componentShown(ComponentEvent componentEvent) {
            Popup.this.cancelPopup();
        }

        public void componentHidden(ComponentEvent componentEvent) {
            Popup.this.cancelPopup();
        }
    }

    private class PopupMouseWheelListener
    implements MouseWheelListener {
        private PopupMouseWheelListener() {
        }

        public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
            Popup.this.cancelPopup();
        }
    }

    private class PopupMouseAdapter
    extends MouseInputAdapter {
        private PopupMouseAdapter() {
        }

        public void mousePressed(MouseEvent mouseEvent) {
            Popup.this.cancelPopup();
        }
    }

    private class PopupWindowAdapter
    extends WindowAdapter {
        private PopupWindowAdapter() {
        }

        public void windowClosing(WindowEvent windowEvent) {
            Popup.this.cancelPopup();
        }

        public void windowClosed(WindowEvent windowEvent) {
            Popup.this.cancelPopup();
        }

        public void windowIconified(WindowEvent windowEvent) {
            Popup.this.cancelPopup();
        }

        public void windowGainedFocus(WindowEvent windowEvent) {
            Popup.this.window.toFront();
        }
    }

}

