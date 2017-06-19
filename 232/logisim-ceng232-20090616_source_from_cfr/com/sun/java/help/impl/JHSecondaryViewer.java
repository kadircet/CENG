/*
 * Decompiled with CFR 0_114.
 */
package com.sun.java.help.impl;

import com.sun.java.help.impl.ViewAwareComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.help.HelpSet;
import javax.help.JHelpContentViewer;
import javax.help.Map;
import javax.help.Popup;
import javax.help.SecondaryWindow;
import javax.help.TextHelpModel;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

public class JHSecondaryViewer
extends JButton
implements ActionListener,
ViewAwareComponent {
    private View myView;
    private HelpSet hs;
    private SimpleAttributeSet textAttribs;
    private URL base;
    private HTMLDocument doc;
    public static String POPUP = "javax.help.Popup";
    public static String SECONDARY_WINDOW = "javax.help.SecondaryWindow";
    public static String LINK_BUTTON = "javax.help.LinkButton";
    public static String LINK_LABEL = "javax.help.LinkLabel";
    private static final String buttonPropertyPrefix = "Button.";
    private static final String editorPropertyPrefix = "EditorPane.";
    private static final Cursor handCursor = Cursor.getPredefinedCursor(12);
    private static Component container;
    private Cursor origCursor;
    private int viewerHeight = 0;
    private int viewerWidth = 0;
    private int viewerX = 0;
    private int viewerY = 0;
    private String viewerName = "";
    private int viewerActivator = 0;
    private int viewerStyle = 0;
    private Icon viewerIcon;
    private String content = "";
    private Map.ID ident;
    private static final boolean debug = false;

    public JHSecondaryViewer() {
        this.setText(">");
        this.setMargin(new Insets(0, 0, 0, 0));
        this.createLinkButton();
        this.addActionListener(this);
        this.origCursor = this.getCursor();
        this.addMouseListener(new MouseListener(){

            public void mouseClicked(MouseEvent mouseEvent) {
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                JHSecondaryViewer.this.setCursor(handCursor);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                JHSecondaryViewer.this.setCursor(JHSecondaryViewer.this.origCursor);
            }

            public void mousePressed(MouseEvent mouseEvent) {
            }

            public void mouseReleased(MouseEvent mouseEvent) {
            }
        });
    }

    public void setViewData(View view) {
        TextHelpModel textHelpModel;
        this.myView = view;
        this.doc = (HTMLDocument)this.myView.getDocument();
        this.base = this.doc.getBase();
        Font font = this.getFont();
        this.textAttribs = new SimpleAttributeSet();
        this.textAttribs.removeAttribute(StyleConstants.FontSize);
        this.textAttribs.removeAttribute(StyleConstants.Bold);
        this.textAttribs.removeAttribute(StyleConstants.Italic);
        this.textAttribs.addAttribute(StyleConstants.FontFamily, font.getName());
        this.textAttribs.addAttribute(StyleConstants.FontSize, new Integer(font.getSize()));
        this.textAttribs.addAttribute(StyleConstants.Bold, new Boolean(font.isBold()));
        this.textAttribs.addAttribute(StyleConstants.Italic, new Boolean(font.isItalic()));
        Component component = JHSecondaryViewer.container = this.myView.getContainer();
        while (component != null) {
            if (component instanceof JHelpContentViewer) break;
            component = component.getParent();
        }
        if (component != null && (textHelpModel = ((JHelpContentViewer)component).getModel()) != null) {
            this.hs = textHelpModel.getHelpSet();
        }
    }

    public void setContent(String string) {
        JHSecondaryViewer.debug("setContent");
        this.content = string;
        this.ident = null;
    }

    public String getContent() {
        if (this.ident != null) {
            Map map = this.hs.getCombinedMap();
            try {
                URL uRL = map.getURLFromID(this.ident);
                return uRL.toExternalForm();
            }
            catch (Exception var2_3) {
                // empty catch block
            }
        }
        return this.content;
    }

    public void setId(String string) {
        JHSecondaryViewer.debug("setID");
        this.ident = Map.ID.create(string, this.hs);
        this.content = "";
    }

    public String getId() {
        return this.ident.id;
    }

    public void setViewerName(String string) {
        JHSecondaryViewer.debug("setViewerName");
        this.viewerName = string;
    }

    public String getViewerName() {
        return this.viewerName;
    }

    public void setViewerActivator(String string) {
        JHSecondaryViewer.debug("setViewerActivator");
        if (string.compareTo(LINK_BUTTON) == 0 && this.viewerActivator != 0) {
            this.viewerActivator = 0;
            this.createLinkButton();
        } else if (string.compareTo(LINK_LABEL) == 0 && this.viewerActivator != 1) {
            this.viewerActivator = 1;
            this.createLinkLabel();
        }
    }

    public String getViewerActivator() {
        switch (this.viewerActivator) {
            case 0: {
                return LINK_BUTTON;
            }
            case 1: {
                return LINK_LABEL;
            }
        }
        return "unknownStyle";
    }

    private void createLinkButton() {
        LookAndFeel.installBorder(this, "Button.border");
        this.setBorderPainted(true);
        this.setFocusPainted(true);
        this.setAlignmentY(0.5f);
        this.setContentAreaFilled(true);
        this.setBackground(UIManager.getColor("Button.background"));
        if (this.textAttribs != null && this.textAttribs.isDefined(StyleConstants.Foreground)) {
            this.setForeground((Color)this.textAttribs.getAttribute(StyleConstants.Foreground));
        } else {
            this.setForeground(UIManager.getColor("Button.foreground"));
        }
        this.invalidate();
    }

    private void createLinkLabel() {
        this.setBorder(new EmptyBorder(1, 1, 1, 1));
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.setAlignmentY(this.getPreferredLabelAlignment());
        this.setContentAreaFilled(false);
        this.setBackground(UIManager.getColor("EditorPane.background"));
        if (this.textAttribs != null && this.textAttribs.isDefined(StyleConstants.Foreground)) {
            this.setForeground((Color)this.textAttribs.getAttribute(StyleConstants.Foreground));
        } else {
            this.setForeground(Color.blue);
        }
        this.invalidate();
    }

    private float getPreferredLabelAlignment() {
        Icon icon = this.getIcon();
        String string = this.getText();
        Font font = this.getFont();
        FontMetrics fontMetrics = this.getToolkit().getFontMetrics(font);
        Rectangle rectangle = new Rectangle();
        Rectangle rectangle2 = new Rectangle();
        Rectangle rectangle3 = new Rectangle(32767, 32767);
        SwingUtilities.layoutCompoundLabel(this, fontMetrics, string, icon, this.getVerticalAlignment(), this.getHorizontalAlignment(), this.getVerticalTextPosition(), this.getHorizontalTextPosition(), rectangle3, rectangle, rectangle2, string == null ? 0 : ((BasicButtonUI)this.ui).getDefaultTextIconGap(this));
        Rectangle rectangle4 = rectangle.union(rectangle2);
        Insets insets = this.getInsets();
        rectangle4.height += insets.top + insets.bottom;
        if (rectangle4.height % 2 == 0) {
            ++rectangle4.height;
        }
        float f = fontMetrics.getMaxAscent() + insets.top;
        return f / (float)rectangle4.height;
    }

    public void setViewerStyle(String string) {
        JHSecondaryViewer.debug("setViewerStyle");
        if (string.compareTo(SECONDARY_WINDOW) == 0) {
            this.viewerStyle = 0;
        } else if (string.compareTo(POPUP) == 0) {
            this.viewerStyle = 1;
        }
    }

    public String getViewerStyle() {
        switch (this.viewerStyle) {
            case 0: {
                return SECONDARY_WINDOW;
            }
            case 1: {
                return POPUP;
            }
        }
        return "unknownStyle";
    }

    public void setViewerLocation(String string) {
        JHSecondaryViewer.debug("setViewerLocation");
        int n = string.indexOf(",");
        if (n != -1) {
            String string2 = string.substring(0, n).trim();
            String string3 = string.substring(n + 1).trim();
            if (string2 != null && string3 != null) {
                this.viewerX = Integer.parseInt(string2);
                this.viewerY = Integer.parseInt(string3);
            }
        }
    }

    public String getViewerLocation() {
        String string = Integer.toString(this.viewerX) + "," + Integer.toString(this.viewerY);
        switch (this.viewerStyle) {
            case 0: {
                Point point;
                SecondaryWindow secondaryWindow = SecondaryWindow.getPresentation(this.viewerName);
                if (secondaryWindow == null || (point = secondaryWindow.getLocation()) == null) break;
                string = Integer.toString(point.x) + "," + Integer.toString(point.y);
                break;
            }
        }
        return string;
    }

    public void setViewerSize(String string) {
        JHSecondaryViewer.debug("setViewerSize");
        int n = string.indexOf(",");
        if (n != -1) {
            String string2 = string.substring(0, n).trim();
            String string3 = string.substring(n + 1).trim();
            if (string2 != null && string3 != null) {
                this.viewerWidth = Integer.parseInt(string2);
                this.viewerHeight = Integer.parseInt(string3);
            }
        }
    }

    public String getViewerSize() {
        String string = "";
        if (this.viewerWidth != 0) {
            string = Integer.toString(this.viewerWidth) + "," + Integer.toString(this.viewerHeight);
        }
        switch (this.viewerStyle) {
            case 0: {
                Dimension dimension;
                SecondaryWindow secondaryWindow = SecondaryWindow.getPresentation(this.viewerName);
                if (secondaryWindow == null || (dimension = secondaryWindow.getSize()) == null) break;
                string = Integer.toString(dimension.width) + "," + Integer.toString(dimension.height);
                break;
            }
        }
        return string;
    }

    public void setIconByName(String string) {
        JHSecondaryViewer.debug("setIconByName");
        ImageIcon imageIcon = null;
        URL uRL = null;
        try {
            uRL = new URL(this.base, string);
        }
        catch (MalformedURLException var4_4) {
            return;
        }
        imageIcon = new ImageIcon(uRL);
        if (imageIcon != null) {
            this.setIcon(imageIcon);
            String string2 = this.getText();
            if (string2.compareTo(">") == 0) {
                this.setText("");
            }
        }
    }

    public void setIconByID(String string) {
        JHSecondaryViewer.debug("setIconByID");
        ImageIcon imageIcon = null;
        URL uRL = null;
        Map map = this.hs.getCombinedMap();
        try {
            uRL = map.getURLFromID(Map.ID.create(string, this.hs));
        }
        catch (MalformedURLException var5_5) {
            return;
        }
        imageIcon = new ImageIcon(uRL);
        if (imageIcon != null) {
            this.setIcon(imageIcon);
            String string2 = this.getText();
            if (string2.compareTo(">") == 0) {
                this.setText("");
            }
        }
    }

    public void setTextFontFamily(String string) {
        this.textAttribs.removeAttribute(StyleConstants.FontFamily);
        this.textAttribs.addAttribute(StyleConstants.FontFamily, string);
        this.setFont(this.getAttributeSetFont(this.textAttribs));
        Font font = this.getFont();
    }

    public String getTextFontFamily() {
        return StyleConstants.getFontFamily(this.textAttribs);
    }

    public void setTextFontSize(String string) {
        int n;
        Object object;
        StyleSheet styleSheet = this.doc.getStyleSheet();
        try {
            if (string.equals("xx-small")) {
                n = (int)styleSheet.getPointSize(0);
            } else if (string.equals("x-small")) {
                n = (int)styleSheet.getPointSize(1);
            } else if (string.equals("small")) {
                n = (int)styleSheet.getPointSize(2);
            } else if (string.equals("medium")) {
                n = (int)styleSheet.getPointSize(3);
            } else if (string.equals("large")) {
                n = (int)styleSheet.getPointSize(4);
            } else if (string.equals("x-large")) {
                n = (int)styleSheet.getPointSize(5);
            } else if (string.equals("xx-large")) {
                n = (int)styleSheet.getPointSize(6);
            } else if (string.equals("bigger")) {
                n = (int)styleSheet.getPointSize("+1");
            } else if (string.equals("smaller")) {
                n = (int)styleSheet.getPointSize("-1");
            } else if (string.endsWith("pt")) {
                object = string.substring(0, string.length() - 2);
                n = Integer.parseInt((String)object);
            } else {
                n = (int)styleSheet.getPointSize(string);
            }
        }
        catch (NumberFormatException var5_5) {
            return;
        }
        if (n == 0) {
            return;
        }
        this.textAttribs.removeAttribute(StyleConstants.FontSize);
        this.textAttribs.addAttribute(StyleConstants.FontSize, new Integer(n));
        this.setFont(this.getAttributeSetFont(this.textAttribs));
        object = this.getFont();
    }

    public String getTextFontSize() {
        return Integer.toString(StyleConstants.getFontSize(this.textAttribs));
    }

    public void setTextFontWeight(String string) {
        boolean bl = false;
        bl = string.compareTo("bold") == 0;
        this.textAttribs.removeAttribute(StyleConstants.Bold);
        this.textAttribs.addAttribute(StyleConstants.Bold, new Boolean(bl));
        this.setFont(this.getAttributeSetFont(this.textAttribs));
        Font font = this.getFont();
    }

    public String getTextFontWeight() {
        if (StyleConstants.isBold(this.textAttribs)) {
            return "bold";
        }
        return "plain";
    }

    public void setTextFontStyle(String string) {
        boolean bl = false;
        bl = string.compareTo("italic") == 0;
        this.textAttribs.removeAttribute(StyleConstants.Italic);
        this.textAttribs.addAttribute(StyleConstants.Italic, new Boolean(bl));
        this.setFont(this.getAttributeSetFont(this.textAttribs));
        Font font = this.getFont();
    }

    public String getTextFontStyle() {
        if (StyleConstants.isItalic(this.textAttribs)) {
            return "italic";
        }
        return "plain";
    }

    public void setTextColor(String string) {
        Color color = null;
        if (string.compareTo("black") == 0) {
            color = Color.black;
        } else if (string.compareTo("blue") == 0) {
            color = Color.blue;
        } else if (string.compareTo("cyan") == 0) {
            color = Color.cyan;
        } else if (string.compareTo("darkGray") == 0) {
            color = Color.darkGray;
        } else if (string.compareTo("gray") == 0) {
            color = Color.gray;
        } else if (string.compareTo("green") == 0) {
            color = Color.green;
        } else if (string.compareTo("lightGray") == 0) {
            color = Color.lightGray;
        } else if (string.compareTo("magenta") == 0) {
            color = Color.magenta;
        } else if (string.compareTo("orange") == 0) {
            color = Color.orange;
        } else if (string.compareTo("pink") == 0) {
            color = Color.pink;
        } else if (string.compareTo("red") == 0) {
            color = Color.red;
        } else if (string.compareTo("white") == 0) {
            color = Color.white;
        } else if (string.compareTo("yellow") == 0) {
            color = Color.yellow;
        }
        if (color == null) {
            return;
        }
        this.textAttribs.removeAttribute(StyleConstants.Foreground);
        this.textAttribs.addAttribute(StyleConstants.Foreground, color);
        this.setForeground(color);
    }

    public String getTextColor() {
        Color color = this.getForeground();
        return color.toString();
    }

    private Font getAttributeSetFont(AttributeSet attributeSet) {
        int n = 0;
        if (StyleConstants.isBold(attributeSet)) {
            n |= true;
        }
        if (StyleConstants.isItalic(attributeSet)) {
            n |= 2;
        }
        String string = StyleConstants.getFontFamily(attributeSet);
        int n2 = StyleConstants.getFontSize(attributeSet);
        if (StyleConstants.isSuperscript(attributeSet) || StyleConstants.isSubscript(attributeSet)) {
            n2 -= 2;
        }
        return this.doc.getStyleSheet().getFont(string, n, n2);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        JHSecondaryViewer.debug("actionPerformed");
        switch (this.viewerStyle) {
            case 0: {
                if (this.hs == null) break;
                SecondaryWindow secondaryWindow = (SecondaryWindow)SecondaryWindow.getPresentation(this.hs, this.viewerName);
                if (this.ident != null) {
                    JHSecondaryViewer.debug("ident=" + this.ident);
                    try {
                        secondaryWindow.setCurrentID(this.ident);
                    }
                    catch (Exception var3_4) {
                        JHSecondaryViewer.debug("error setting CurrentID");
                    }
                } else if (this.content != null) {
                    JHSecondaryViewer.debug("content=" + this.content);
                    try {
                        URL uRL = new URL(this.base, this.content);
                        secondaryWindow.setCurrentURL(uRL);
                    }
                    catch (Exception var3_6) {
                        JHSecondaryViewer.debug("error setting URL");
                    }
                }
                if (this.viewerX != -1) {
                    secondaryWindow.setLocation(new Point(this.viewerX, this.viewerY));
                }
                if (this.viewerWidth != 0) {
                    secondaryWindow.setSize(new Dimension(this.viewerWidth, this.viewerHeight));
                }
                secondaryWindow.setDisplayed(true);
                break;
            }
            case 1: {
                if (this.hs == null) break;
                Popup popup = (Popup)Popup.getPresentation(this.hs, this.viewerName);
                if (this.ident != null) {
                    JHSecondaryViewer.debug("ident=" + this.ident);
                    try {
                        popup.setCurrentID(this.ident);
                    }
                    catch (Exception var3_7) {
                        JHSecondaryViewer.debug("error setting CurrentID");
                    }
                } else if (this.content != null) {
                    JHSecondaryViewer.debug("content=" + this.content);
                    try {
                        URL uRL = new URL(this.base, this.content);
                        popup.setCurrentURL(uRL);
                    }
                    catch (Exception var3_9) {
                        JHSecondaryViewer.debug("error setting URL");
                    }
                }
                if (this.viewerWidth != 0) {
                    popup.setSize(new Dimension(this.viewerWidth, this.viewerHeight));
                }
                popup.setInvoker(this);
                popup.setDisplayed(true);
                break;
            }
            default: {
                System.out.println("Unknown viewerStyle");
            }
        }
    }

    private static void debug(String string) {
    }

}

