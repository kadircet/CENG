/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.io.PrintStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Vector;
import javax.help.HelpHistoryModel;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.JHelp;
import javax.help.JHelpNavigator;
import javax.help.Map;
import javax.help.TextHelpModel;
import javax.help.event.EventListenerList;
import javax.help.event.HelpHistoryModelEvent;
import javax.help.event.HelpHistoryModelListener;
import javax.help.event.HelpModelEvent;
import javax.help.event.HelpModelListener;

public class DefaultHelpHistoryModel
implements HelpHistoryModel {
    protected Vector history = new Vector();
    protected int historyIndex = -1;
    protected HelpModel helpModel;
    protected EventListenerList listenerList = new EventListenerList();
    protected JHelp help;
    private static boolean debug = false;
    static /* synthetic */ Class class$javax$help$event$HelpHistoryModelListener;

    public DefaultHelpHistoryModel(JHelp jHelp) {
        this.help = jHelp;
        if (jHelp != null) {
            this.setHelpModel(jHelp.getModel());
        }
    }

    public void setHelpModel(HelpModel helpModel) {
        if (helpModel == this.helpModel) {
            return;
        }
        this.discard();
        if (this.helpModel != null) {
            this.helpModel.removeHelpModelListener(this);
        }
        if (helpModel != null) {
            helpModel.addHelpModelListener(this);
        }
        this.helpModel = helpModel;
    }

    public void removeHelpHistoryModelListener(HelpHistoryModelListener helpHistoryModelListener) {
        Class class_ = class$javax$help$event$HelpHistoryModelListener == null ? (DefaultHelpHistoryModel.class$javax$help$event$HelpHistoryModelListener = DefaultHelpHistoryModel.class$("javax.help.event.HelpHistoryModelListener")) : class$javax$help$event$HelpHistoryModelListener;
        this.listenerList.remove(class_, helpHistoryModelListener);
    }

    public void addHelpHistoryModelListener(HelpHistoryModelListener helpHistoryModelListener) {
        Class class_ = class$javax$help$event$HelpHistoryModelListener == null ? (DefaultHelpHistoryModel.class$javax$help$event$HelpHistoryModelListener = DefaultHelpHistoryModel.class$("javax.help.event.HelpHistoryModelListener")) : class$javax$help$event$HelpHistoryModelListener;
        this.listenerList.add(class_, helpHistoryModelListener);
    }

    public void discard() {
        this.history.setSize(0);
        this.historyIndex = -1;
        this.fireHistoryChanged(this, false, false);
    }

    public Vector getHistory() {
        return this.history;
    }

    public void removeLastEntry() {
        int n = this.history.size();
        if (n > 0) {
            this.history.removeElementAt(this.history.size() - 1);
        }
    }

    public int getIndex() {
        return this.historyIndex;
    }

    public void goForward() {
        this.setHistoryEntry(this.historyIndex + 1);
    }

    public void goBack() {
        this.setHistoryEntry(this.historyIndex - 1);
    }

    public Vector getForwardHistory() {
        Vector vector = (Vector)this.history.clone();
        Vector vector2 = new Vector();
        int n = vector.size();
        DefaultHelpHistoryModel.debug(" forward size : " + n);
        int n2 = this.historyIndex + 1;
        while (n2 < n) {
            vector2.addElement(vector.elementAt(n2));
            ++n2;
        }
        return vector2;
    }

    public Vector getBackwardHistory() {
        Vector vector = new Vector();
        Vector vector2 = (Vector)this.history.clone();
        DefaultHelpHistoryModel.debug(" backward size : " + vector2.size());
        DefaultHelpHistoryModel.debug(" backward index : " + this.historyIndex);
        if (vector2 != null) {
            int n = 0;
            while (n < this.historyIndex) {
                vector.addElement(vector2.elementAt(n));
                ++n;
            }
        }
        return vector;
    }

    public void setHistoryEntry(int n) {
        DefaultHelpHistoryModel.debug("setHistoryEntry(" + n + ")");
        if (this.helpModel == null) {
            return;
        }
        if (n < 0 || n >= this.history.size()) {
            this.discard();
            return;
        }
        HelpModelEvent helpModelEvent = (HelpModelEvent)this.history.elementAt(n);
        this.historyIndex = n - 1;
        Map.ID iD = helpModelEvent.getID();
        URL uRL = helpModelEvent.getURL();
        JHelpNavigator jHelpNavigator = helpModelEvent.getNavigator();
        if (iD != null) {
            try {
                DefaultHelpHistoryModel.debug("  setCurrentID" + iD);
                this.helpModel.setCurrentID(iD, helpModelEvent.getHistoryName(), helpModelEvent.getNavigator());
                if (jHelpNavigator != null) {
                    this.help.setCurrentNavigator(jHelpNavigator);
                }
                return;
            }
            catch (Exception var6_6) {
                // empty catch block
            }
        }
        if (uRL != null) {
            try {
                DefaultHelpHistoryModel.debug("  setCurrentURL" + uRL);
                this.helpModel.setCurrentURL(uRL, helpModelEvent.getHistoryName(), helpModelEvent.getNavigator());
                if (jHelpNavigator != null) {
                    this.help.setCurrentNavigator(jHelpNavigator);
                }
                return;
            }
            catch (Exception var6_7) {
                // empty catch block
            }
        }
        this.discard();
    }

    protected void fireHistoryChanged(Object object, boolean bl, boolean bl2) {
        Object[] arrobject = this.listenerList.getListenerList();
        HelpHistoryModelEvent helpHistoryModelEvent = null;
        int n = arrobject.length - 2;
        while (n >= 0) {
            if (arrobject[n] == (class$javax$help$event$HelpHistoryModelListener == null ? DefaultHelpHistoryModel.class$("javax.help.event.HelpHistoryModelListener") : class$javax$help$event$HelpHistoryModelListener)) {
                if (helpHistoryModelEvent == null) {
                    helpHistoryModelEvent = new HelpHistoryModelEvent(object, bl, bl2);
                }
                DefaultHelpHistoryModel.debug("fireHistoryChanged: ");
                DefaultHelpHistoryModel.debug("  " + arrobject[n + 1]);
                DefaultHelpHistoryModel.debug("  previous=" + helpHistoryModelEvent.isPrevious() + " next=" + helpHistoryModelEvent.isNext());
                ((HelpHistoryModelListener)arrobject[n + 1]).historyChanged(helpHistoryModelEvent);
            }
            n -= 2;
        }
    }

    public void idChanged(HelpModelEvent helpModelEvent) {
        DefaultHelpHistoryModel.debug("idChanged(" + helpModelEvent + ")");
        DefaultHelpHistoryModel.debug("  historyIndex==" + this.historyIndex);
        DefaultHelpHistoryModel.debug("  history.size==" + this.history.size());
        if (this.historyIndex == this.history.size() - 1) {
            this.history.addElement(helpModelEvent);
            ++this.historyIndex;
            this.fireHistoryChanged(this, this.historyIndex > 0, this.historyIndex < this.history.size() - 1);
            return;
        }
        if (this.historyIndex >= -1 && this.historyIndex < this.history.size() - 1) {
            ++this.historyIndex;
            HelpModelEvent helpModelEvent2 = (HelpModelEvent)this.history.elementAt(this.historyIndex);
            if (helpModelEvent2 == null) {
                this.discard();
                return;
            }
            if (helpModelEvent2.getID() != null && helpModelEvent.getID() != null && helpModelEvent2.getID().equals(helpModelEvent.getID())) {
                this.fireHistoryChanged(this, this.historyIndex > 0, this.historyIndex < this.history.size() - 1);
                return;
            }
            if (helpModelEvent2.getURL() != null && helpModelEvent.getURL() != null && helpModelEvent2.getURL().sameFile(helpModelEvent.getURL())) {
                this.fireHistoryChanged(this, this.historyIndex > 0, this.historyIndex < this.history.size() - 1);
                return;
            }
            this.history.setSize(this.historyIndex);
            this.history.addElement(helpModelEvent);
            this.fireHistoryChanged(this, this.historyIndex > 0, this.historyIndex < this.history.size() - 1);
        }
    }

    public void removeHelpSet(HelpSet helpSet) {
        int n;
        Enumeration enumeration = this.history.elements();
        DefaultHelpHistoryModel.debug(" size before " + this.history.size());
        if (debug) {
            System.err.println("before : ");
            n = 0;
            while (n < this.history.size()) {
                System.err.println(((HelpModelEvent)this.history.elementAt(n)).getID());
                ++n;
            }
        }
        n = this.history.size();
        Vector vector = new Vector();
        int n2 = this.historyIndex;
        int n3 = 0;
        while (n3 < n) {
            Map.ID iD;
            HelpModelEvent helpModelEvent = (HelpModelEvent)this.history.elementAt(n3);
            Map.ID iD2 = helpModelEvent.getID();
            DefaultHelpHistoryModel.debug(" update id " + iD2);
            URL uRL = helpModelEvent.getURL();
            DefaultHelpHistoryModel.debug(" update url " + uRL);
            if (iD2 != null && iD2.hs != helpSet) {
                DefaultHelpHistoryModel.debug(" remain - " + iD2);
                vector.addElement(this.history.elementAt(n3));
            } else if (uRL != null && (iD = helpSet.getCombinedMap().getIDFromURL(uRL)) == null) {
                DefaultHelpHistoryModel.debug(" remain > " + iD);
                vector.addElement(this.history.elementAt(n3));
            }
            ++n3;
        }
        this.history = vector;
        this.historyIndex = this.history.size() - 1;
        DefaultHelpHistoryModel.debug(" size after " + this.history.size());
        if (debug) {
            System.err.println("after : ");
            int n4 = 0;
            while (n4 < this.history.size()) {
                System.err.println(((HelpModelEvent)this.history.elementAt(n4)).getID());
                ++n4;
            }
        }
        this.setHistoryEntry(this.historyIndex);
    }

    protected HelpModel getModel() {
        return this.helpModel;
    }

    private static void debug(String string) {
        if (debug) {
            System.out.println("BasicHelpUI: " + string);
        }
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException var1_1) {
            throw new NoClassDefFoundError(var1_1.getMessage());
        }
    }
}

