/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import javax.help.BadIDException;
import javax.help.HelpSet;

public interface Map {
    public boolean isValidID(String var1, HelpSet var2);

    public Enumeration getAllIDs();

    public URL getURLFromID(ID var1) throws MalformedURLException;

    public boolean isID(URL var1);

    public ID getIDFromURL(URL var1);

    public ID getClosestID(URL var1);

    public Enumeration getIDs(URL var1);

    public static final class ID
    implements Serializable {
        public String id;
        public HelpSet hs;

        public static ID create(String string, HelpSet helpSet) throws BadIDException {
            if (helpSet == null || string == null) {
                return null;
            }
            Map map = helpSet.getCombinedMap();
            if (!map.isValidID(string, helpSet)) {
                throw new BadIDException("Not valid ID: " + string, map, string, helpSet);
            }
            return new ID(string, helpSet);
        }

        private ID(String string, HelpSet helpSet) throws BadIDException {
            this.id = string;
            this.hs = helpSet;
        }

        public HelpSet getHelpSet() {
            return this.hs;
        }

        public String getIDString() {
            return this.id;
        }

        public URL getURL() throws MalformedURLException {
            if (this.hs == null || this.id == null) {
                return null;
            }
            return this.hs.getCombinedMap().getURLFromID(this);
        }

        public boolean equals(Object object) {
            if (object instanceof ID) {
                ID iD = (ID)object;
                return iD.id.equals(this.id) && iD.hs.equals(this.hs);
            }
            return false;
        }

        public String toString() {
            return "ID: " + this.id + ", " + this.hs;
        }
    }

}

