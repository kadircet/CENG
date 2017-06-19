/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.URL;
import java.util.EventListener;
import java.util.Vector;
import javax.help.HelpSet;
import javax.help.InvalidHelpSetContextException;
import javax.help.JHelpNavigator;
import javax.help.Map;
import javax.help.TextHelpModel;
import javax.help.event.EventListenerList;
import javax.help.event.HelpModelEvent;
import javax.help.event.HelpModelListener;
import javax.help.event.TextHelpModelEvent;
import javax.help.event.TextHelpModelListener;

public class DefaultHelpModel
implements TextHelpModel,
Serializable {
    private HelpSet helpset;
    private Map.ID currentID;
    private URL currentURL;
    private String navID;
    private Vector highlights = new Vector();
    private String title;
    protected EventListenerList listenerList = new EventListenerList();
    protected EventListenerList textListenerList = new EventListenerList();
    protected PropertyChangeSupport changes;
    private static boolean debug = false;
    static /* synthetic */ Class class$javax$help$event$HelpModelListener;
    static /* synthetic */ Class class$javax$help$event$TextHelpModelListener;

    public DefaultHelpModel(HelpSet helpSet) {
        this.changes = new PropertyChangeSupport(this);
        this.helpset = helpSet;
    }

    public void setHelpSet(HelpSet helpSet) {
        HelpSet helpSet2 = this.helpset;
        this.helpset = helpSet;
        this.changes.firePropertyChange("helpSet", helpSet2, helpSet);
    }

    public HelpSet getHelpSet() {
        return this.helpset;
    }

    public void setCurrentID(Map.ID iD) throws InvalidHelpSetContextException {
        this.setCurrentID(iD, null, null);
    }

    public void setCurrentID(Map.ID iD, String string, JHelpNavigator jHelpNavigator) throws InvalidHelpSetContextException {
        if (iD == null) {
            iD = this.helpset.getHomeID();
        }
        if (iD == null || iD.equals(this.currentID)) {
            return;
        }
        String string2 = iD.id;
        HelpSet helpSet = iD.hs;
        if (!this.helpset.contains(helpSet)) {
            throw new InvalidHelpSetContextException("Wrong context", this.helpset, helpSet);
        }
        Map map = this.helpset.getCombinedMap();
        this.currentID = iD;
        try {
            Object object;
            URL uRL;
            Map.ID iD2 = iD;
            if (helpSet == this.helpset) {
                uRL = map.getURLFromID(iD);
            } else {
                object = helpSet.getLocalMap();
                uRL = object.getURLFromID(iD);
            }
            if (this.currentURL != null && this.currentURL.equals(uRL)) {
                object = this.currentURL.getRef();
                String string3 = uRL.getRef();
                if (object == null && string3 == null) {
                    return;
                }
                if (object != null && string3 != null && object.compareTo(string3) == 0) {
                    return;
                }
            }
            this.currentURL = uRL;
        }
        catch (Exception var7_8) {
            this.currentURL = null;
        }
        this.highlights.setSize(0);
        this.fireIDChanged(this, this.currentID, this.currentURL, string, jHelpNavigator);
    }

    public Map.ID getCurrentID() {
        return this.currentID;
    }

    public void setCurrentURL(URL uRL) {
        this.setCurrentURL(uRL, null, null);
    }

    public void setCurrentURL(URL uRL, String string, JHelpNavigator jHelpNavigator) {
        boolean bl = false;
        if (this.currentURL == null) {
            if (this.currentURL != uRL) {
                this.currentURL = uRL;
                bl = true;
            }
        } else if (!this.currentURL.equals(uRL)) {
            this.currentURL = uRL;
            bl = true;
        }
        if (this.currentURL == null) {
            if (this.currentID != null) {
                this.currentID = null;
                bl = true;
            }
        } else {
            Map.ID iD = this.helpset.getCombinedMap().getIDFromURL(this.currentURL);
            if (this.currentID == null) {
                if (this.currentID != iD) {
                    this.currentID = iD;
                    bl = true;
                }
            } else if (!this.currentID.equals(iD)) {
                this.currentID = iD;
                bl = true;
            }
        }
        if (bl) {
            this.highlights.setSize(0);
            this.fireIDChanged(this, this.currentID, this.currentURL, string, jHelpNavigator);
        }
    }

    public URL getCurrentURL() {
        return this.currentURL;
    }

    public void addHighlight(int n, int n2) {
        DefaultHelpModel.debug("addHighlight(" + n + ", " + n2 + ")");
        this.highlights.addElement(new DefaultHighlight(n, n2));
        this.fireHighlightsChanged(this);
    }

    public void removeAllHighlights() {
        DefaultHelpModel.debug("removeAllHighlights");
        this.highlights.setSize(0);
        this.fireHighlightsChanged(this);
    }

    public void setHighlights(TextHelpModel.Highlight[] arrhighlight) {
        this.highlights.setSize(0);
        if (arrhighlight == null) {
            return;
        }
        int n = 0;
        while (n < arrhighlight.length) {
            this.highlights.addElement(new DefaultHighlight(arrhighlight[n].getStartOffset(), arrhighlight[n].getEndOffset()));
            ++n;
        }
        if (this.highlights.size() > 0) {
            this.fireHighlightsChanged(this);
        }
    }

    public TextHelpModel.Highlight[] getHighlights() {
        Object[] arrobject = new DefaultHighlight[this.highlights.size()];
        this.highlights.copyInto(arrobject);
        return arrobject;
    }

    public void addHelpModelListener(HelpModelListener helpModelListener) {
        DefaultHelpModel.debug("addHelpModelListener: ");
        DefaultHelpModel.debug("  l:" + helpModelListener);
        if (debug) {
            try {
                throw new Exception("");
            }
            catch (Exception var2_2) {
                var2_2.printStackTrace();
            }
        }
        Class class_ = class$javax$help$event$HelpModelListener == null ? (DefaultHelpModel.class$javax$help$event$HelpModelListener = DefaultHelpModel.class$("javax.help.event.HelpModelListener")) : class$javax$help$event$HelpModelListener;
        this.listenerList.add(class_, helpModelListener);
    }

    public void removeHelpModelListener(HelpModelListener helpModelListener) {
        Class class_ = class$javax$help$event$HelpModelListener == null ? (DefaultHelpModel.class$javax$help$event$HelpModelListener = DefaultHelpModel.class$("javax.help.event.HelpModelListener")) : class$javax$help$event$HelpModelListener;
        this.listenerList.remove(class_, helpModelListener);
    }

    public void addTextHelpModelListener(TextHelpModelListener textHelpModelListener) {
        DefaultHelpModel.debug("addTextHelpModelListener: ");
        DefaultHelpModel.debug("  l:" + textHelpModelListener);
        if (debug) {
            try {
                throw new Exception("");
            }
            catch (Exception var2_2) {
                var2_2.printStackTrace();
            }
        }
        Class class_ = class$javax$help$event$TextHelpModelListener == null ? (DefaultHelpModel.class$javax$help$event$TextHelpModelListener = DefaultHelpModel.class$("javax.help.event.TextHelpModelListener")) : class$javax$help$event$TextHelpModelListener;
        this.textListenerList.add(class_, textHelpModelListener);
    }

    public void removeTextHelpModelListener(TextHelpModelListener textHelpModelListener) {
        Class class_ = class$javax$help$event$TextHelpModelListener == null ? (DefaultHelpModel.class$javax$help$event$TextHelpModelListener = DefaultHelpModel.class$("javax.help.event.TextHelpModelListener")) : class$javax$help$event$TextHelpModelListener;
        this.textListenerList.remove(class_, textHelpModelListener);
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.changes.addPropertyChangeListener(propertyChangeListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.changes.removePropertyChangeListener(propertyChangeListener);
    }

    public void setDocumentTitle(String string) {
        String string2 = this.title;
        this.title = string;
        this.changes.firePropertyChange("documentTitle", string2, string);
    }

    public String getDocumentTitle() {
        return this.title;
    }

    protected void fireIDChanged(Object object, Map.ID iD, URL uRL, String string, JHelpNavigator jHelpNavigator) {
        Object[] arrobject = this.listenerList.getListenerList();
        HelpModelEvent helpModelEvent = null;
        int n = arrobject.length - 2;
        while (n >= 0) {
            if (arrobject[n] == (class$javax$help$event$HelpModelListener == null ? DefaultHelpModel.class$("javax.help.event.HelpModelListener") : class$javax$help$event$HelpModelListener)) {
                if (helpModelEvent == null) {
                    helpModelEvent = new HelpModelEvent(object, iD, uRL, string, jHelpNavigator);
                }
                DefaultHelpModel.debug("fireIDChanged: ");
                DefaultHelpModel.debug("  " + arrobject[n + 1]);
                DefaultHelpModel.debug("  id=" + helpModelEvent.getID() + " url=" + helpModelEvent.getURL());
                ((HelpModelListener)arrobject[n + 1]).idChanged(helpModelEvent);
            }
            n -= 2;
        }
    }

    protected void fireIDChanged(Object object, Map.ID iD, URL uRL) {
        this.fireIDChanged(object, iD, uRL, null, null);
    }

    protected void fireHighlightsChanged(Object object) {
        Object[] arrobject = this.textListenerList.getListenerList();
        TextHelpModelEvent textHelpModelEvent = null;
        int n = arrobject.length - 2;
        while (n >= 0) {
            if (arrobject[n] == (class$javax$help$event$TextHelpModelListener == null ? DefaultHelpModel.class$("javax.help.event.TextHelpModelListener") : class$javax$help$event$TextHelpModelListener)) {
                if (textHelpModelEvent == null) {
                    textHelpModelEvent = new TextHelpModelEvent(object);
                }
                DefaultHelpModel.debug("fireHighlightsChanged: ");
                DefaultHelpModel.debug("  " + arrobject[n + 1]);
                DefaultHelpModel.debug("  " + textHelpModelEvent);
                ((TextHelpModelListener)arrobject[n + 1]).highlightsChanged(textHelpModelEvent);
            }
            n -= 2;
        }
    }

    private static void debug(String string) {
        if (debug) {
            System.err.println("DefaultHelpModel: " + string);
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

    public static class DefaultHighlight
    implements TextHelpModel.Highlight {
        public int start;
        public int end;

        public DefaultHighlight(int n, int n2) {
            if (n < 0) {
                throw new IllegalArgumentException("start");
            }
            if (n2 < 0) {
                throw new IllegalArgumentException("end");
            }
            this.start = n;
            this.end = n2;
        }

        public int getStartOffset() {
            return this.start;
        }

        public int getEndOffset() {
            return this.end;
        }
    }

}

