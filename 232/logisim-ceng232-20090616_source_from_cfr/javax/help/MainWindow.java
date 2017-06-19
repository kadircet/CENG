/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import javax.help.HelpSet;
import javax.help.Presentation;
import javax.help.WindowPresentation;

public class MainWindow
extends WindowPresentation {
    private static final boolean debug = false;

    private MainWindow(HelpSet helpSet) {
        super(helpSet);
    }

    public static Presentation getPresentation(HelpSet helpSet, String string) {
        MainWindow mainWindow = new MainWindow(helpSet);
        if (helpSet != null) {
            HelpSet.Presentation presentation = null;
            if (string != null) {
                presentation = helpSet.getPresentation(string);
            }
            if (presentation == null) {
                presentation = helpSet.getDefaultPresentation();
            }
            mainWindow.setHelpSetPresentation(presentation);
        }
        return mainWindow;
    }

    private static void debug(Object object) {
    }
}

