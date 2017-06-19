/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.awt.Dimension;
import java.awt.Point;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import javax.help.HelpSet;

interface HelpSetFactory {
    public void parsingStarted(URL var1);

    public void processDOCTYPE(String var1, String var2, String var3);

    public void processPI(HelpSet var1, String var2, String var3);

    public void processTitle(HelpSet var1, String var2);

    public void processHomeID(HelpSet var1, String var2);

    public void processMapRef(HelpSet var1, Hashtable var2);

    public void processView(HelpSet var1, String var2, String var3, String var4, Hashtable var5, String var6, Hashtable var7, Locale var8);

    public void processSubHelpSet(HelpSet var1, Hashtable var2);

    public void processPresentation(HelpSet var1, String var2, boolean var3, boolean var4, boolean var5, Dimension var6, Point var7, String var8, String var9, boolean var10, Vector var11);

    public void reportMessage(String var1, boolean var2);

    public Enumeration listMessages();

    public HelpSet parsingEnded(HelpSet var1);

    public static class HelpAction {
        public String className = null;
        public Hashtable attr = null;

        public HelpAction(String string, Hashtable hashtable) {
            this.className = string;
            this.attr = hashtable;
        }
    }

}

