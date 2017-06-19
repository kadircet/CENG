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
import javax.help.DefaultHelpModel;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.InvalidHelpSetContextException;
import javax.help.Map;
import javax.help.NavigatorView;
import javax.help.UnsupportedOperationException;

public class ServletHelpBroker
implements HelpBroker {
    protected HelpSet helpset = null;
    protected DefaultHelpModel model = null;
    protected NavigatorView curNav = null;
    protected boolean viewDisplayed = true;
    protected Locale locale = null;
    protected Font font;
    private static final boolean debug = false;

    public HelpSet getHelpSet() {
        return this.helpset;
    }

    public void setHelpSet(HelpSet helpSet) {
        if (helpSet != null && this.helpset != helpSet) {
            this.model = new DefaultHelpModel(helpSet);
            this.helpset = helpSet;
        }
    }

    public Locale getLocale() {
        if (this.locale == null) {
            return Locale.getDefault();
        }
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setCurrentView(String string) {
        NavigatorView[] arrnavigatorView = this.helpset.getNavigatorViews();
        int n = 0;
        while (n < arrnavigatorView.length) {
            if (arrnavigatorView[n].getName().equals(string)) {
                this.curNav = arrnavigatorView[n];
                return;
            }
            ++n;
        }
        throw new IllegalArgumentException("Invalid view name");
    }

    public String getCurrentView() {
        if (this.curNav == null) {
            if (this.helpset != null) {
                NavigatorView[] arrnavigatorView = this.helpset.getNavigatorViews();
                this.curNav = arrnavigatorView[0];
            } else {
                return null;
            }
        }
        return this.curNav.getName();
    }

    public NavigatorView getCurrentNavigatorView() {
        if (this.curNav == null) {
            if (this.helpset != null) {
                NavigatorView[] arrnavigatorView = this.helpset.getNavigatorViews();
                this.curNav = arrnavigatorView[0];
            } else {
                return null;
            }
        }
        return this.curNav;
    }

    public void initPresentation() {
    }

    public void setDisplayed(boolean bl) {
    }

    public boolean isDisplayed() {
        return true;
    }

    public void setLocation(Point point) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    public Point getLocation() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    public void setSize(Dimension dimension) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    public Dimension getSize() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    public void setScreen(int n) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    public int getScreen() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    public void setViewDisplayed(boolean bl) {
        this.viewDisplayed = bl;
    }

    public boolean isViewDisplayed() {
        return this.viewDisplayed;
    }

    public void setCurrentID(String string) throws BadIDException {
        try {
            this.setCurrentID(Map.ID.create(string, this.helpset));
        }
        catch (InvalidHelpSetContextException var2_2) {
            new Error("internal error?");
        }
    }

    public void setCurrentID(Map.ID iD) throws InvalidHelpSetContextException {
        ServletHelpBroker.debug("setCurrentID");
        this.model.setCurrentID(iD);
    }

    public Map.ID getCurrentID() {
        return this.model.getCurrentID();
    }

    public void setCurrentURL(URL uRL) {
        this.model.setCurrentURL(uRL);
    }

    public URL getCurrentURL() {
        return this.model.getCurrentURL();
    }

    public void enableHelpKey(Component component, String string, HelpSet helpSet) {
    }

    public void enableHelp(Component component, String string, HelpSet helpSet) {
    }

    public void enableHelp(MenuItem menuItem, String string, HelpSet helpSet) {
    }

    public void enableHelpOnButton(Component component, String string, HelpSet helpSet) {
    }

    public void enableHelpOnButton(MenuItem menuItem, String string, HelpSet helpSet) {
    }

    public void setHelpSetPresentation(HelpSet.Presentation presentation) {
    }

    public void showID(String string, String string2, String string3) throws BadIDException {
    }

    public void showID(Map.ID iD, String string, String string2) throws InvalidHelpSetContextException {
    }

    public void enableHelpKey(Component component, String string, HelpSet helpSet, String string2, String string3) {
    }

    public void enableHelpOnButton(Object object, String string, HelpSet helpSet, String string2, String string3) {
    }

    private static void debug(Object object) {
    }
}

