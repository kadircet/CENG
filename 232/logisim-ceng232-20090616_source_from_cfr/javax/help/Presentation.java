/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;
import java.util.Locale;
import javax.help.BadIDException;
import javax.help.DefaultHelpModel;
import javax.help.HelpSet;
import javax.help.InvalidHelpSetContextException;
import javax.help.Map;
import javax.help.TextHelpModel;

public abstract class Presentation {
    private HelpSet helpset = null;
    private TextHelpModel model = null;
    private Locale locale = null;
    private Font font = null;
    private int width = 645;
    private int height = 495;
    private static final boolean debug = false;

    public static Presentation getPresentation(HelpSet helpSet, String string) {
        return null;
    }

    public void setHelpSetPresentation(HelpSet.Presentation presentation) {
        Presentation.debug("setHelpSetPresentation");
        if (presentation == null) {
            return;
        }
        Dimension dimension = presentation.getSize();
        if (dimension != null) {
            this.setSize(dimension);
        }
    }

    public Map.ID getCurrentID() {
        Presentation.debug("getCurrentID");
        if (this.model != null) {
            return this.model.getCurrentID();
        }
        return null;
    }

    public void setCurrentID(String string) throws BadIDException {
        Presentation.debug("setCurrentID - String");
        try {
            this.setCurrentID(Map.ID.create(string, this.helpset));
        }
        catch (InvalidHelpSetContextException var2_2) {
            new Error("internal error?");
        }
    }

    public void setCurrentID(Map.ID iD) throws InvalidHelpSetContextException {
        Presentation.debug("setCurrentID - ID");
        this.createHelpModel();
        this.model.setCurrentID(iD);
    }

    public URL getCurrentURL() {
        Presentation.debug("getCurrentURL");
        if (this.model != null) {
            return this.model.getCurrentURL();
        }
        return null;
    }

    public void setCurrentURL(URL uRL) {
        Presentation.debug("setCurrentURL");
        this.createHelpModel();
        this.model.setCurrentURL(uRL);
    }

    public HelpSet getHelpSet() {
        Presentation.debug("getHelpSet");
        return this.helpset;
    }

    public void setHelpSet(HelpSet helpSet) {
        Presentation.debug("setHelpSet");
        if (helpSet != null && this.helpset != helpSet) {
            this.model = new DefaultHelpModel(helpSet);
            this.helpset = helpSet;
        }
    }

    public abstract void setDisplayed(boolean var1);

    public abstract boolean isDisplayed();

    public Font getFont() {
        Presentation.debug("getFont");
        return this.font;
    }

    public void setFont(Font font) {
        Presentation.debug("setFont");
        this.font = font;
    }

    public Locale getLocale() {
        Presentation.debug("getLocale");
        if (this.locale == null) {
            return Locale.getDefault();
        }
        return this.locale;
    }

    public void setLocale(Locale locale) {
        Presentation.debug("setLocale");
        this.locale = locale;
    }

    public Dimension getSize() {
        Presentation.debug("getSize");
        return new Dimension(this.width, this.height);
    }

    public void setSize(Dimension dimension) {
        Presentation.debug("setSize");
        this.width = dimension.width;
        this.height = dimension.height;
    }

    private void createHelpModel() {
        if (this.model == null) {
            this.model = new DefaultHelpModel(this.helpset);
        }
    }

    protected TextHelpModel getHelpModel() {
        if (this.model == null) {
            this.createHelpModel();
        }
        return this.model;
    }

    private static void debug(Object object) {
    }
}

