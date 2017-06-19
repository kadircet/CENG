/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.awt.Component;
import java.awt.IllegalComponentStateException;
import java.beans.BeanDescriptor;
import java.beans.Introspector;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.CollationElementIterator;
import java.text.MessageFormat;
import java.text.RuleBasedCollator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

public class HelpUtilities {
    private static Hashtable tailsPerLocales = new Hashtable();
    private static Hashtable bundles;
    private static ResourceBundle lastBundle;
    private static Locale lastLocale;
    private static final boolean debug = false;

    public static String getHelpSetNameFromBean(Class class_) {
        Object object;
        String string;
        try {
            object = Introspector.getBeanInfo(class_);
            string = (String)object.getBeanDescriptor().getValue("helpSetName");
        }
        catch (Exception var2_2) {
            string = null;
        }
        if (string == null) {
            object = class_.getName();
            int n = object.lastIndexOf(".");
            if (n == -1) {
                string = (String)object + "Help.hs";
            } else {
                String string2 = object.substring(0, n);
                string = string2.replace('.', '/') + "/Help.hs";
            }
        }
        return string;
    }

    public static String getIDStringFromBean(Class class_) {
        Object object;
        String string;
        try {
            object = Introspector.getBeanInfo(class_);
            string = (String)object.getBeanDescriptor().getValue("helpID");
        }
        catch (Exception var2_2) {
            string = null;
        }
        if (string == null) {
            object = class_.getName();
            string = (String)object + ".topID";
        }
        return string;
    }

    public static String getDefaultQueryEngine() {
        return "com.sun.java.help.search.DefaultSearchEngine";
    }

    public static URL getLocalizedResource(ClassLoader classLoader, String string, String string2, Locale locale) {
        return HelpUtilities.getLocalizedResource(classLoader, string, string2, locale, false);
    }

    public static URL getLocalizedResource(ClassLoader classLoader, String string, String string2, Locale locale, boolean bl) {
        Enumeration enumeration = HelpUtilities.getCandidates(locale);
        while (enumeration.hasMoreElements()) {
            String string3 = (String)enumeration.nextElement();
            String string4 = string + string3 + string2;
            URL uRL = classLoader == null ? ClassLoader.getSystemResource(string4) : classLoader.getResource(string4);
            if (uRL == null) continue;
            if (bl) {
                try {
                    InputStream inputStream = uRL.openConnection().getInputStream();
                    if (inputStream == null) continue;
                    int n = inputStream.read();
                    inputStream.close();
                    if (n == -1) continue;
                    return uRL;
                }
                catch (Throwable var9_10) {
                    continue;
                }
            }
            return uRL;
        }
        return null;
    }

    public static synchronized Enumeration getCandidates(Locale locale) {
        String string;
        LocalePair localePair = new LocalePair(locale, Locale.getDefault());
        Vector<String> vector = (Vector<String>)tailsPerLocales.get(localePair);
        if (vector != null) {
            HelpUtilities.debug("getCandidates - cached copy");
            return vector.elements();
        }
        String string2 = locale.toString();
        StringBuffer stringBuffer = new StringBuffer("_").append(string2);
        if (string2 == null) {
            stringBuffer.setLength(0);
        }
        vector = new Vector<String>();
        while (stringBuffer.length() != 0) {
            HelpUtilities.debug("  adding ", stringBuffer);
            string = stringBuffer.toString();
            vector.addElement(string);
            int n = string.lastIndexOf(95);
            if (n == -1) continue;
            stringBuffer.setLength(n);
        }
        HelpUtilities.debug("  addign -- null -- ");
        vector.addElement("");
        if (locale != Locale.getDefault()) {
            string = Locale.getDefault().toString();
            StringBuffer stringBuffer2 = new StringBuffer("_").append(string);
            if (string == null) {
                stringBuffer2.setLength(0);
            }
            while (stringBuffer2.length() != 0) {
                HelpUtilities.debug("  adding ", stringBuffer2);
                String string3 = stringBuffer2.toString();
                vector.addElement(string3);
                int n = string3.lastIndexOf(95);
                if (n == -1) continue;
                stringBuffer2.setLength(n);
            }
        }
        tailsPerLocales.put(localePair, vector);
        HelpUtilities.debug("tails is == ", vector);
        return vector.elements();
    }

    public static Locale getLocale(Component component) {
        if (component == null) {
            return Locale.getDefault();
        }
        try {
            return component.getLocale();
        }
        catch (IllegalComponentStateException var1_1) {
            return Locale.getDefault();
        }
    }

    private static synchronized ResourceBundle getBundle(Locale locale) {
        ResourceBundle resourceBundle;
        if (lastLocale == locale) {
            return lastBundle;
        }
        if (bundles == null) {
            bundles = new Hashtable();
        }
        if ((resourceBundle = (ResourceBundle)bundles.get(locale)) == null) {
            try {
                resourceBundle = ResourceBundle.getBundle("javax.help.resources.Constants", locale);
            }
            catch (MissingResourceException var2_2) {
                throw new Error("Fatal: Resource for javahelp is missing");
            }
            bundles.put(locale, resourceBundle);
        }
        lastBundle = resourceBundle;
        lastLocale = locale;
        return resourceBundle;
    }

    public static String getString(String string) {
        return HelpUtilities.getString(Locale.getDefault(), string);
    }

    public static String getText(String string) {
        return HelpUtilities.getText(Locale.getDefault(), string, null, null);
    }

    public static String getText(String string, String string2) {
        return HelpUtilities.getText(Locale.getDefault(), string, string2, null);
    }

    public static String getText(String string, String string2, String string3) {
        return HelpUtilities.getText(Locale.getDefault(), string, string2, string3);
    }

    public static String getText(String string, String string2, String string3, String string4) {
        return HelpUtilities.getText(Locale.getDefault(), string, string2, string3, string4);
    }

    public static String getString(Locale locale, String string) {
        ResourceBundle resourceBundle = HelpUtilities.getBundle(locale);
        try {
            return resourceBundle.getString(string);
        }
        catch (MissingResourceException var3_3) {
            throw new Error("Fatal: Localization data for JavaHelp is broken.  Missing " + string + " key.");
        }
    }

    public static String[] getStringArray(Locale locale, String string) {
        ResourceBundle resourceBundle = HelpUtilities.getBundle(locale);
        try {
            return resourceBundle.getStringArray(string);
        }
        catch (MissingResourceException var3_3) {
            throw new Error("Fatal: Localization data for JavaHelp is broken.  Missing " + string + " key.");
        }
    }

    public static String getText(Locale locale, String string) {
        return HelpUtilities.getText(locale, string, null, null, null);
    }

    public static String getText(Locale locale, String string, String string2) {
        return HelpUtilities.getText(locale, string, string2, null, null);
    }

    public static String getText(Locale locale, String string, String string2, String string3) {
        return HelpUtilities.getText(locale, string, string2, string3, null);
    }

    public static String getText(Locale locale, String string, String string2, String string3, String string4) {
        ResourceBundle resourceBundle = HelpUtilities.getBundle(locale);
        if (string2 == null) {
            string2 = "null";
        }
        if (string3 == null) {
            string3 = "null";
        }
        if (string4 == null) {
            string4 = "null";
        }
        try {
            String string5 = resourceBundle.getString(string);
            String[] arrstring = new String[]{string2, string3, string4};
            MessageFormat messageFormat = new MessageFormat(string5);
            try {
                messageFormat.setLocale(locale);
            }
            catch (NullPointerException var9_10) {
                // empty catch block
            }
            return messageFormat.format(arrstring);
        }
        catch (MissingResourceException var6_7) {
            throw new Error("Fatal: Localization data for JavaHelp is broken.  Missing " + string + " key.");
        }
    }

    public static Locale localeFromLang(String string) {
        String string2 = null;
        Locale locale = null;
        if (string == null) {
            return locale;
        }
        int n = string.indexOf("_");
        int n2 = string.indexOf("-");
        if (n == -1 && n2 == -1) {
            String string3 = string;
            String string4 = "";
            locale = new Locale(string3, string4);
        } else {
            if (n == -1 && n2 != -1) {
                n = n2;
            }
            String string5 = string.substring(0, n);
            int n3 = string.indexOf("_", n + 1);
            int n4 = string.indexOf("-", n + 1);
            if (n3 == -1 && n4 == -1) {
                String string6 = string.substring(n + 1);
                locale = new Locale(string5, string6);
            } else {
                if (n3 == -1 && n4 != -1) {
                    n3 = n4;
                }
                String string7 = string.substring(n + 1, n3);
                string2 = string.substring(n3 + 1);
                locale = new Locale(string5, string7, string2);
            }
        }
        return locale;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static boolean isStringInString(RuleBasedCollator var0, String var1_1, String var2_2) {
        if (var1_1 == null) return false;
        if (var2_2 == null) {
            return false;
        }
        if (var1_1.length() == 0 && var2_2.length() == 0) {
            return true;
        }
        var3_3 = 3;
        var6_4 = false;
        var0.setDecomposition(2);
        var7_5 = var0.getRules();
        if (var7_5.startsWith("@")) {
            var6_4 = true;
        }
        var4_6 = var0.getCollationElementIterator(var1_1);
        var5_7 = var0.getCollationElementIterator(var2_2);
        var8_8 = 0;
        var9_9 = 0;
        var16_10 = true;
        var17_11 = true;
        var18_12 = 0;
        block9 : do {
            block28 : {
                try {
                    var4_6.setOffset(0);
                }
                catch (NoSuchMethodError var22_23) {
                    // empty catch block
                }
                var8_8 = var4_6.next();
                try {
                    var5_7.setOffset(var18_12);
                }
                catch (NoSuchMethodError var22_24) {
                }
                catch (Exception var23_27) {
                    return false;
                }
                var9_9 = var5_7.next();
                if (var9_9 != -1) ** GOTO lbl50
                return false;
lbl-1000: // 1 sources:
                {
                    if (var8_8 == var9_9) {
                        try {
                            var18_12 = var5_7.getOffset();
                        }
                        catch (NoSuchMethodError var22_25) {}
                        break;
                    }
                    var10_13 = CollationElementIterator.primaryOrder(var8_8);
                    if (var10_13 == (var11_14 = CollationElementIterator.primaryOrder(var9_9))) {
                        try {
                            var18_12 = var5_7.getOffset();
                        }
                        catch (NoSuchMethodError var22_26) {}
                        break;
                    }
                    var9_9 = var5_7.next();
lbl50: // 2 sources:
                    ** while (var9_9 != -1)
                }
lbl51: // 5 sources:
                if (var9_9 == -1) {
                    return false;
                }
                var16_10 = false;
                var17_11 = false;
                var20_20 = var19_19 = var0.getStrength() >= 1;
                v0 = var21_21 = var0.getStrength() >= 2;
                do {
                    if (var16_10) {
                        var8_8 = var4_6.next();
                    } else {
                        var16_10 = true;
                    }
                    if (var17_11) {
                        var9_9 = var5_7.next();
                    } else {
                        var17_11 = true;
                    }
                    if (var8_8 == -1 || var9_9 == -1) break block28;
                    var10_13 = CollationElementIterator.primaryOrder(var8_8);
                    var11_14 = CollationElementIterator.primaryOrder(var9_9);
                    if (var8_8 == var9_9) {
                        if (!var6_4 || var10_13 == 0 || var20_20) continue;
                        var20_20 = var19_19;
                        var21_21 = false;
                        continue;
                    }
                    if (var10_13 != var11_14) {
                        if (var8_8 == 0) {
                            var17_11 = false;
                            continue;
                        }
                        if (var9_9 == 0) {
                            var16_10 = false;
                            continue;
                        }
                        if (var10_13 == 0) {
                            if (var20_20) continue block9;
                            var17_11 = false;
                            continue;
                        }
                        if (var11_14 != 0 || var20_20) continue block9;
                        var16_10 = false;
                        continue;
                    }
                    if (var20_20 && ((var12_15 = CollationElementIterator.secondaryOrder(var8_8)) != (var13_16 = CollationElementIterator.secondaryOrder(var9_9)) || var21_21 && (var14_17 = CollationElementIterator.tertiaryOrder(var8_8)) != (var15_18 = CollationElementIterator.tertiaryOrder(var9_9)))) break;
                } while (true);
                continue;
            }
            if (var8_8 == -1) return true;
            do {
                if (!(CollationElementIterator.primaryOrder(var8_8) != 0 || CollationElementIterator.secondaryOrder(var8_8) != 0 && var20_20)) ** break;
                continue block9;
            } while ((var8_8 = var4_6.next()) != -1);
            break;
        } while (true);
        return true;
    }

    private static void debug(Object object, Object object2, Object object3) {
    }

    private static void debug(Object object) {
        HelpUtilities.debug(object, "", "");
    }

    private static void debug(Object object, Object object2) {
        HelpUtilities.debug(object, object2, "");
    }

    static {
        lastBundle = null;
        lastLocale = null;
    }

    static class LocalePair {
        Locale locale1;
        Locale locale2;

        LocalePair(Locale locale, Locale locale2) {
            this.locale1 = locale;
            this.locale2 = locale2;
        }

        public int hashCode() {
            return this.locale1.hashCode() + this.locale2.hashCode();
        }

        public boolean equals(Object object) {
            if (object == null || !(object instanceof LocalePair)) {
                return false;
            }
            LocalePair localePair = (LocalePair)object;
            return this.locale1.equals(localePair.locale1) && this.locale2.equals(localePair.locale2);
        }
    }

}

