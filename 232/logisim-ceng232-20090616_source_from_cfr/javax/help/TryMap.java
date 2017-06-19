/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import javax.help.HelpSet;
import javax.help.Map;

public class TryMap
implements Map,
Serializable {
    private Vector maps = new Vector();
    private static final boolean debug = false;

    public void add(Map map) {
        this.maps.addElement(map);
    }

    public boolean remove(Map map) {
        return this.maps.removeElement(map);
    }

    public Enumeration getMaps() {
        return this.maps.elements();
    }

    public boolean isValidID(String string, HelpSet helpSet) {
        TryMap.debug("isValidID " + string);
        Enumeration enumeration = this.maps.elements();
        while (enumeration.hasMoreElements()) {
            Map map = (Map)enumeration.nextElement();
            if (!map.isValidID(string, helpSet)) continue;
            return true;
        }
        return false;
    }

    public Enumeration getAllIDs() {
        return new TryEnumeration(this.maps.elements(), null);
    }

    public URL getURLFromID(Map.ID iD) throws MalformedURLException {
        TryMap.debug("getURLFromID(" + iD + ")");
        URL uRL = null;
        Enumeration enumeration = this.maps.elements();
        while (enumeration.hasMoreElements()) {
            Map map = (Map)enumeration.nextElement();
            uRL = map.getURLFromID(iD);
            if (uRL == null) continue;
            return uRL;
        }
        return uRL;
    }

    public boolean isID(URL uRL) {
        Enumeration enumeration = this.maps.elements();
        while (enumeration.hasMoreElements()) {
            Map map = (Map)enumeration.nextElement();
            if (!map.isID(uRL)) continue;
            return true;
        }
        return false;
    }

    public Map.ID getIDFromURL(URL uRL) {
        TryMap.debug("getIDFromURL(" + uRL + ")");
        Map.ID iD = null;
        Enumeration enumeration = this.maps.elements();
        while (enumeration.hasMoreElements()) {
            Map map = (Map)enumeration.nextElement();
            iD = map.getIDFromURL(uRL);
            if (iD == null) continue;
            return iD;
        }
        return null;
    }

    public Map.ID getClosestID(URL uRL) {
        Map.ID iD = null;
        iD = this.getIDFromURL(uRL);
        if (iD != null) {
            return iD;
        }
        if (iD == null && uRL == null) {
            return null;
        }
        String string = uRL.getRef();
        if (string != null) {
            String string2 = uRL.toExternalForm();
            string2 = string2.substring(0, string2.lastIndexOf(string) - 1);
            try {
                URL uRL2 = new URL(string2);
                Enumeration enumeration = this.maps.elements();
                while (enumeration.hasMoreElements()) {
                    Map map = (Map)enumeration.nextElement();
                    iD = map.getIDFromURL(uRL2);
                    if (iD == null) continue;
                    return iD;
                }
            }
            catch (MalformedURLException var5_6) {
                // empty catch block
            }
        }
        return null;
    }

    public Enumeration getIDs(URL uRL) {
        return new TryEnumeration(this.maps.elements(), uRL);
    }

    private static void debug(String string) {
    }

    private static class TryEnumeration
    implements Enumeration {
        private Enumeration e;
        private Enumeration k;
        private URL url;

        public TryEnumeration(Enumeration enumeration, URL uRL) {
            this.e = enumeration;
            this.k = null;
            this.url = uRL;
        }

        public boolean hasMoreElements() {
            while (this.k == null || !this.k.hasMoreElements()) {
                if (!this.e.hasMoreElements()) {
                    return false;
                }
                Map map = (Map)this.e.nextElement();
                this.k = this.url == null ? map.getAllIDs() : map.getIDs(this.url);
            }
            return this.k.hasMoreElements();
        }

        public Object nextElement() {
            return this.k.nextElement();
        }
    }

}

