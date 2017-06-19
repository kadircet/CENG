/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.beans.PropertyChangeListener;
import java.net.URL;
import javax.help.HelpSet;
import javax.help.InvalidHelpSetContextException;
import javax.help.JHelpNavigator;
import javax.help.Map;
import javax.help.event.HelpModelListener;

public interface HelpModel {
    public void setHelpSet(HelpSet var1);

    public HelpSet getHelpSet();

    public void setCurrentID(Map.ID var1) throws InvalidHelpSetContextException;

    public void setCurrentID(Map.ID var1, String var2, JHelpNavigator var3) throws InvalidHelpSetContextException;

    public Map.ID getCurrentID();

    public void setCurrentURL(URL var1);

    public void setCurrentURL(URL var1, String var2, JHelpNavigator var3);

    public URL getCurrentURL();

    public void addHelpModelListener(HelpModelListener var1);

    public void removeHelpModelListener(HelpModelListener var1);

    public void addPropertyChangeListener(PropertyChangeListener var1);

    public void removePropertyChangeListener(PropertyChangeListener var1);
}

