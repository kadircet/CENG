/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.mac;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.AbstractButton;
import net.roydesign.event.ApplicationEvent;

abstract class MRJEventProxy {
    private final String ABOUT_KEY = "about";
    private final String PREFERENCES_KEY = "preferences";
    private final String OPEN_APPLICATION_KEY = "open application";
    private final String QUIT_APPLICATION_KEY = "quit application";
    private final String OPEN_DOCUMENT_KEY = "open document";
    private final String PRINT_DOCUMENT_KEY = "print document";
    private final String REOPEN_APPLICATION_KEY = "reopen application";
    private Hashtable listenerLists = new Hashtable();
    private ArrayList documentsOpened = new ArrayList();

    MRJEventProxy() {
    }

    public void addAboutListener(ActionListener l, Object source) {
        this.addListener(l, source, "about");
    }

    public void removeAboutListener(ActionListener l) {
        this.removeListener(l, "about");
    }

    public void addPreferencesListener(ActionListener l, Object source) {
        if (this.listenerLists.get("preferences") == null) {
            this.setPreferencesEnabled(true);
        }
        this.addListener(l, source, "preferences");
    }

    public void removePreferencesListener(ActionListener l) {
        this.removeListener(l, "preferences");
        if (this.listenerLists.get("preferences") == null) {
            this.setPreferencesEnabled(false);
        }
    }

    public void addOpenApplicationListener(ActionListener l, Object source) {
        this.addListener(l, source, "open application");
    }

    public void removeOpenApplicationListener(ActionListener l) {
        this.removeListener(l, "open application");
    }

    public void addReopenApplicationListener(ActionListener l, Object source) {
        this.addListener(l, source, "reopen application");
    }

    public void removeReopenApplicationListener(ActionListener l) {
        this.removeListener(l, "reopen application");
    }

    public void addQuitApplicationListener(ActionListener l, Object source) {
        this.addListener(l, source, "quit application");
    }

    public void removeQuitApplicationListener(ActionListener l) {
        this.removeListener(l, "quit application");
    }

    public void addOpenDocumentListener(ActionListener l, Object source) {
        this.addListener(l, source, "open document");
    }

    public void removeOpenDocumentListener(ActionListener l) {
        this.removeListener(l, "open document");
    }

    public void addPrintDocumentListener(ActionListener l, Object source) {
        this.addListener(l, source, "print document");
    }

    public void removePrintDocumentListener(ActionListener l) {
        this.removeListener(l, "print document");
    }

    private void addListener(ActionListener l, Object source, String key) {
        String name;
        Hashtable<String, ListenerInfo> ht = (Hashtable<String, ListenerInfo>)this.listenerLists.get(key);
        if (ht == null) {
            ht = new Hashtable<String, ListenerInfo>(1);
            this.listenerLists.put(key, ht);
        }
        if (ht.containsKey(name = l.getClass().getName())) {
            return;
        }
        ListenerInfo li = (MRJEventProxy)this.new ListenerInfo();
        li.actionListener = l;
        li.source = source != null ? source : this;
        ht.put(name, li);
    }

    private void removeListener(ActionListener l, String key) {
        Hashtable ht = (Hashtable)this.listenerLists.get(key);
        String name = l.getClass().getName();
        if (ht != null && ht.remove(name) != null && ht.isEmpty()) {
            this.listenerLists.remove(key);
        }
    }

    public abstract boolean isPreferencesEnabled();

    public abstract void setPreferencesEnabled(boolean var1);

    protected void fireMenuEvent(int type) {
        Hashtable ht = null;
        switch (type) {
            case 1: {
                ht = (Hashtable)this.listenerLists.get("about");
                break;
            }
            case 2: {
                ht = (Hashtable)this.listenerLists.get("preferences");
                break;
            }
            default: {
                throw new Error("unknown event type");
            }
        }
        if (ht == null) {
            return;
        }
        Enumeration<V> enumeration = ht.elements();
        while (enumeration.hasMoreElements()) {
            ListenerInfo li = (ListenerInfo)enumeration.nextElement();
            String cmd = null;
            if (li.source instanceof MenuItem) {
                cmd = ((MenuItem)li.source).getActionCommand();
            } else if (li.source instanceof AbstractButton) {
                cmd = ((AbstractButton)li.source).getActionCommand();
            }
            ApplicationEvent e = new ApplicationEvent(li.source, type, cmd);
            li.actionListener.actionPerformed(e);
        }
    }

    protected void fireDocumentEvent(int type, File file) {
        Hashtable ht = null;
        switch (type) {
            case 5: {
                ht = (Hashtable)this.listenerLists.get("open document");
                if (ht != null && !ht.isEmpty()) break;
                this.documentsOpened.add(file);
                break;
            }
            case 6: {
                ht = (Hashtable)this.listenerLists.get("print document");
                break;
            }
            default: {
                throw new Error("unknown event type");
            }
        }
        if (ht == null) {
            return;
        }
        Enumeration<V> enumeration = ht.elements();
        while (enumeration.hasMoreElements()) {
            ListenerInfo li = (ListenerInfo)enumeration.nextElement();
            ApplicationEvent e = new ApplicationEvent(li.source, type, file);
            li.actionListener.actionPerformed(e);
        }
    }

    protected void fireApplicationEvent(int type) {
        Hashtable ht = null;
        switch (type) {
            case 3: {
                ht = (Hashtable)this.listenerLists.get("open application");
                break;
            }
            case 7: {
                ht = (Hashtable)this.listenerLists.get("reopen application");
                break;
            }
            case 4: {
                ht = (Hashtable)this.listenerLists.get("quit application");
                if (ht != null) break;
                System.exit(0);
                return;
            }
            default: {
                throw new Error("unknown event type");
            }
        }
        if (ht == null) {
            return;
        }
        Enumeration<V> enumeration = ht.elements();
        while (enumeration.hasMoreElements()) {
            ListenerInfo li = (ListenerInfo)enumeration.nextElement();
            ApplicationEvent e = new ApplicationEvent(li.source, type);
            li.actionListener.actionPerformed(e);
        }
    }

    public File[] getDocumentsOpened() {
        File[] ret = new File[this.documentsOpened.size()];
        int i = 0;
        while (i < ret.length) {
            ret[i] = (File)this.documentsOpened.get(i);
            ++i;
        }
        this.documentsOpened.clear();
        return ret;
    }

    private class ListenerInfo {
        ActionListener actionListener;
        Object source;

        ListenerInfo() {
        }
    }

}

