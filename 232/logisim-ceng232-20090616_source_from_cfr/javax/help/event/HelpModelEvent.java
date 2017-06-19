/*
 * Decompiled with CFR 0_114.
 */
package javax.help.event;

import java.net.URL;
import java.util.EventObject;
import javax.help.JHelpNavigator;
import javax.help.Map;

public class HelpModelEvent
extends EventObject {
    private Map.ID id;
    private URL url;
    private String historyName;
    private JHelpNavigator navigator;
    private int pos0;
    private int pos1;

    public HelpModelEvent(Object object, Map.ID iD, URL uRL) {
        this(object, iD, uRL, null, null);
    }

    public HelpModelEvent(Object object, Map.ID iD, URL uRL, String string, JHelpNavigator jHelpNavigator) {
        super(object);
        if (iD == null && uRL == null) {
            throw new IllegalArgumentException("ID or URL must not be null");
        }
        this.id = iD;
        this.url = uRL;
        this.historyName = string;
        this.navigator = jHelpNavigator;
    }

    public HelpModelEvent(Object object, int n, int n2) {
        super(object);
        this.pos0 = n;
        this.pos1 = n2;
    }

    public Map.ID getID() {
        return this.id;
    }

    public URL getURL() {
        return this.url;
    }

    public String getHistoryName() {
        return this.historyName;
    }

    public JHelpNavigator getNavigator() {
        return this.navigator;
    }

    public int getPos0() {
        return this.pos0;
    }

    public int getPos1() {
        return this.pos1;
    }
}

