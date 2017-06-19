/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.beans.PropertyChangeListener;

public interface HelpAction {
    public boolean isEnabled();

    public void setEnabled(boolean var1);

    public Object getControl();

    public Object getValue(String var1);

    public void putValue(String var1, Object var2);

    public void addPropertyChangeListener(PropertyChangeListener var1);

    public void removePropertyChangeListener(PropertyChangeListener var1);
}

