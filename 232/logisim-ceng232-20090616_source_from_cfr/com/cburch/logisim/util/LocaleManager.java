/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.StringGetter;
import com.cburch.logisim.util.StringUtil;
import com.cburch.logisim.util.Strings;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class LocaleManager {
    private static final String SETTINGS_NAME = "settings";
    private static ArrayList managers = new ArrayList();
    private static ArrayList listeners = new ArrayList();
    private static boolean replaceAccents = false;
    private static HashMap repl = null;
    private String dir_name;
    private String file_start;
    private ResourceBundle settings = null;
    private ResourceBundle locale = null;
    private ResourceBundle dflt_locale = null;

    public static Locale getLocale() {
        return Locale.getDefault();
    }

    public static void setLocale(Locale loc) {
        Locale.setDefault(loc);
        Iterator it = managers.iterator();
        while (it.hasNext()) {
            ((LocaleManager)it.next()).loadDefault();
        }
        repl = replaceAccents ? LocaleManager.fetchReplaceAccents() : null;
        LocaleManager.fireLocaleChanged();
    }

    public static boolean canReplaceAccents() {
        return LocaleManager.fetchReplaceAccents() != null;
    }

    public static void setReplaceAccents(boolean value) {
        HashMap newRepl = value ? LocaleManager.fetchReplaceAccents() : null;
        replaceAccents = value;
        repl = newRepl;
        LocaleManager.fireLocaleChanged();
    }

    private static HashMap fetchReplaceAccents() {
        String val;
        HashMap<Character, String> ret = null;
        try {
            val = Strings.source.locale.getString("accentReplacements");
        }
        catch (MissingResourceException e) {
            return null;
        }
        StringTokenizer toks = new StringTokenizer(val, "/");
        while (toks.hasMoreTokens()) {
            String tok = toks.nextToken().trim();
            char c = '\u0000';
            String s = null;
            if (tok.length() == 1) {
                c = tok.charAt(0);
                s = "";
            } else if (tok.length() >= 2 && tok.charAt(1) == ' ') {
                c = tok.charAt(0);
                s = tok.substring(2).trim();
            }
            if (s == null) continue;
            if (ret == null) {
                ret = new HashMap<Character, String>();
            }
            ret.put(new Character(c), s);
        }
        return ret;
    }

    public static void addLocaleListener(LocaleListener l) {
        listeners.add(l);
    }

    public static void removeLocaleListener(LocaleListener l) {
        listeners.remove(l);
    }

    private static void fireLocaleChanged() {
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            ((LocaleListener)it.next()).localeChanged();
        }
    }

    public LocaleManager(String dir_name, String file_start) {
        this.dir_name = dir_name;
        this.file_start = file_start;
        this.loadDefault();
        managers.add(this);
    }

    private void loadDefault() {
        Locale[] choices;
        if (this.settings == null) {
            try {
                this.settings = ResourceBundle.getBundle(this.dir_name + "/" + "settings");
            }
            catch (MissingResourceException e) {
                // empty catch block
            }
        }
        try {
            this.loadLocale(Locale.getDefault());
            if (this.locale != null) {
                return;
            }
        }
        catch (MissingResourceException e) {
            // empty catch block
        }
        try {
            this.loadLocale(Locale.ENGLISH);
            if (this.locale != null) {
                return;
            }
        }
        catch (MissingResourceException e) {
            // empty catch block
        }
        if ((choices = this.getLocaleOptions()) != null && choices.length > 0) {
            this.loadLocale(choices[0]);
        }
        if (this.locale != null) {
            return;
        }
        throw new RuntimeException("No locale bundles are available");
    }

    private void loadLocale(Locale loc) {
        this.locale = ResourceBundle.getBundle(this.dir_name + "/" + loc + "/" + this.file_start, loc);
        Locale.setDefault(loc);
        if (this.dflt_locale == null) {
            this.dflt_locale = this.locale;
        }
    }

    public String get(String key) {
        String ret;
        try {
            ret = this.locale.getString(key);
        }
        catch (MissingResourceException e) {
            try {
                ret = this.dflt_locale.getString(key);
            }
            catch (MissingResourceException e2) {
                ret = key;
            }
        }
        HashMap repl = LocaleManager.repl;
        if (repl != null) {
            ret = LocaleManager.replaceAccents(ret, repl);
        }
        return ret;
    }

    public StringGetter getter(String key) {
        return new LocaleGetter(this, key);
    }

    public StringGetter getter(String key, String arg) {
        return StringUtil.formatter(this.getter(key), arg);
    }

    public Locale[] getLocaleOptions() {
        String locs = null;
        try {
            if (this.settings != null) {
                locs = this.settings.getString("locales");
            }
        }
        catch (MissingResourceException e) {
            // empty catch block
        }
        if (locs == null) {
            return new Locale[0];
        }
        ArrayList<Locale> retl = new ArrayList<Locale>();
        StringTokenizer toks = new StringTokenizer(locs);
        while (toks.hasMoreTokens()) {
            String country;
            String language;
            String f = toks.nextToken();
            if (f.length() >= 2) {
                language = f.substring(0, 2);
                country = f.length() >= 5 ? f.substring(3, 5) : null;
            } else {
                language = null;
                country = null;
            }
            if (language == null) continue;
            Locale loc = country == null ? new Locale(language) : new Locale(language, country);
            retl.add(loc);
        }
        Locale[] ret = new Locale[retl.size()];
        for (int i = 0; i < retl.size(); ++i) {
            ret[i] = (Locale)retl.get(i);
        }
        return ret;
    }

    public JMenuItem createLocaleMenuItem() {
        Locale[] locales = this.getLocaleOptions();
        if (locales == null || locales.length == 0) {
            return null;
        }
        return new LocaleMenu(locales);
    }

    private static String replaceAccents(String src, HashMap repl) {
        int i;
        char ci;
        int n = src.length();
        for (i = 0; i < n && (ci = src.charAt(i)) >= ' ' && ci < ''; ++i) {
        }
        if (i == n) {
            return src;
        }
        char[] cs = src.toCharArray();
        StringBuffer ret = new StringBuffer(src.substring(0, i));
        for (int j = i; j < cs.length; ++j) {
            char cj = cs[j];
            if (cj < ' ' || cj >= '') {
                String out = (String)repl.get(new Character(cj));
                if (out != null) {
                    ret.append(out);
                    continue;
                }
                ret.append(cj);
                continue;
            }
            ret.append(cj);
        }
        return ret.toString();
    }

    private static class LocaleGetter
    implements StringGetter {
        private LocaleManager source;
        private String key;

        LocaleGetter(LocaleManager source, String key) {
            this.source = source;
            this.key = key;
        }

        @Override
        public String get() {
            return this.source.get(this.key);
        }

        public String toString() {
            return this.get();
        }
    }

    private static class LocaleMenu
    extends JMenu
    implements LocaleListener {
        LocaleItem[] items;

        LocaleMenu(Locale[] locales) {
            ButtonGroup bgroup = new ButtonGroup();
            this.items = new LocaleItem[locales.length];
            for (int i = 0; i < locales.length; ++i) {
                this.items[i] = new LocaleItem(locales[i], bgroup);
                this.add(this.items[i]);
            }
            LocaleManager.addLocaleListener(this);
            this.localeChanged();
        }

        @Override
        public void localeChanged() {
            this.setText(Strings.get("localeMenuItem"));
            Locale current = LocaleManager.getLocale();
            for (int i = 0; i < this.items.length; ++i) {
                LocaleItem it = this.items[i];
                it.setText(it.locale.getDisplayName(current));
                it.setSelected(it.locale.equals(current));
            }
        }
    }

    private static class LocaleItem
    extends JRadioButtonMenuItem
    implements ActionListener {
        private Locale locale;

        LocaleItem(Locale locale, ButtonGroup bgroup) {
            this.locale = locale;
            bgroup.add(this);
            this.addActionListener(this);
            this.setSelected(locale.equals(LocaleManager.getLocale()));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (this.isSelected()) {
                LocaleManager.setLocale(this.locale);
            }
        }
    }

}

