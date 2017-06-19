/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.util.Hashtable;
import javax.help.HelpSet;
import javax.help.Presentation;
import javax.help.WindowPresentation;

public class SecondaryWindow
extends WindowPresentation {
    private static Hashtable windows = new Hashtable();
    private String name;
    private static final boolean debug = false;

    private SecondaryWindow(HelpSet helpSet, String string) {
        super(helpSet);
        this.name = string;
    }

    public static Presentation getPresentation(HelpSet helpSet, String string) {
        SecondaryWindow secondaryWindow;
        SecondaryWindow.debug("getPresentation");
        String string2 = string;
        if (string == null) {
            string2 = "";
        }
        if ((secondaryWindow = (SecondaryWindow)windows.get(string2)) != null) {
            if (secondaryWindow.getHelpSet() != helpSet) {
                secondaryWindow.setHelpSet(helpSet);
            }
            return secondaryWindow;
        }
        SecondaryWindow.debug("no Presentation - start again");
        secondaryWindow = new SecondaryWindow(helpSet, string2);
        secondaryWindow.setViewDisplayed(false);
        secondaryWindow.setToolbarDisplayed(false);
        secondaryWindow.setDestroyOnExit(true);
        secondaryWindow.setTitleFromDocument(true);
        if (helpSet != null) {
            HelpSet.Presentation presentation = null;
            if (string != null) {
                presentation = helpSet.getPresentation(string);
            }
            if (presentation == null) {
                presentation = helpSet.getDefaultPresentation();
            }
            secondaryWindow.setHelpSetPresentation(presentation);
            windows.put(string2, secondaryWindow);
        }
        return secondaryWindow;
    }

    public static SecondaryWindow getPresentation(String string) {
        SecondaryWindow.debug("getPresenation(name)");
        return (SecondaryWindow)windows.get(string);
    }

    public void destroy() {
        super.destroy();
        windows.remove(this.name);
    }

    private static void debug(Object object) {
    }
}

