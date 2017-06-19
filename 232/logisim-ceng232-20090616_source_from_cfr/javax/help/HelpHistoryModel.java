/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.io.Serializable;
import java.util.Vector;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.event.HelpHistoryModelListener;
import javax.help.event.HelpModelListener;

public interface HelpHistoryModel
extends HelpModelListener,
Serializable {
    public void addHelpHistoryModelListener(HelpHistoryModelListener var1);

    public void removeHelpHistoryModelListener(HelpHistoryModelListener var1);

    public void discard();

    public void goForward();

    public void goBack();

    public Vector getBackwardHistory();

    public Vector getForwardHistory();

    public void setHistoryEntry(int var1);

    public void removeHelpSet(HelpSet var1);

    public Vector getHistory();

    public int getIndex();

    public void setHelpModel(HelpModel var1);
}

