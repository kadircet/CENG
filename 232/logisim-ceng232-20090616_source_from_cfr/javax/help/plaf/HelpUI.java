/*
 * Decompiled with CFR 0_114.
 */
package javax.help.plaf;

import javax.help.JHelpNavigator;
import javax.swing.plaf.ComponentUI;

public abstract class HelpUI
extends ComponentUI {
    public abstract void addNavigator(JHelpNavigator var1);

    public abstract void removeNavigator(JHelpNavigator var1);

    public abstract void setCurrentNavigator(JHelpNavigator var1);

    public abstract JHelpNavigator getCurrentNavigator();
}

