/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.Point;
import java.net.URL;
import java.util.Locale;
import javax.help.BadIDException;
import javax.help.HelpSet;
import javax.help.InvalidHelpSetContextException;
import javax.help.Map;
import javax.help.UnsupportedOperationException;

public interface HelpBroker {
    public void setHelpSet(HelpSet var1);

    public HelpSet getHelpSet();

    public void setHelpSetPresentation(HelpSet.Presentation var1);

    public Locale getLocale();

    public void setLocale(Locale var1);

    public Font getFont();

    public void setFont(Font var1);

    public void setCurrentView(String var1);

    public String getCurrentView();

    public void initPresentation();

    public void setDisplayed(boolean var1) throws UnsupportedOperationException;

    public boolean isDisplayed();

    public void setLocation(Point var1) throws UnsupportedOperationException;

    public Point getLocation() throws UnsupportedOperationException;

    public void setSize(Dimension var1) throws UnsupportedOperationException;

    public Dimension getSize() throws UnsupportedOperationException;

    public void setScreen(int var1) throws UnsupportedOperationException;

    public int getScreen() throws UnsupportedOperationException;

    public void setViewDisplayed(boolean var1);

    public boolean isViewDisplayed();

    public void showID(Map.ID var1, String var2, String var3) throws InvalidHelpSetContextException;

    public void showID(String var1, String var2, String var3) throws BadIDException;

    public void setCurrentID(Map.ID var1) throws InvalidHelpSetContextException;

    public void setCurrentID(String var1) throws BadIDException;

    public Map.ID getCurrentID();

    public void setCurrentURL(URL var1);

    public URL getCurrentURL();

    public void enableHelpKey(Component var1, String var2, HelpSet var3);

    public void enableHelpKey(Component var1, String var2, HelpSet var3, String var4, String var5);

    public void enableHelp(Component var1, String var2, HelpSet var3);

    public void enableHelp(MenuItem var1, String var2, HelpSet var3);

    public void enableHelpOnButton(Component var1, String var2, HelpSet var3) throws IllegalArgumentException;

    public void enableHelpOnButton(MenuItem var1, String var2, HelpSet var3);

    public void enableHelpOnButton(Object var1, String var2, HelpSet var3, String var4, String var5) throws IllegalArgumentException;
}

