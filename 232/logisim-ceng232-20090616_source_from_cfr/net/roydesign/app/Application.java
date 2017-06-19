/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.app;

import java.awt.MenuBar;
import java.awt.event.ActionListener;
import javax.swing.JMenuBar;
import net.roydesign.app.AboutJMenuItem;
import net.roydesign.app.AboutMenuItem;
import net.roydesign.app.PreferencesJMenuItem;
import net.roydesign.app.PreferencesMenuItem;
import net.roydesign.app.QuitJMenuItem;
import net.roydesign.app.QuitMenuItem;
import net.roydesign.mac.MRJAdapter;

public class Application {
    private static Application instance;
    private String name;
    private AboutJMenuItem macAboutJMenuItem;
    private AboutMenuItem macAboutMenuItem;
    private PreferencesJMenuItem macPreferencesJMenuItem;
    private PreferencesMenuItem macPreferencesMenuItem;
    private QuitJMenuItem macQuitJMenuItem;
    private QuitMenuItem macQuitMenuItem;

    protected Application() {
        if (instance != null) {
            throw new IllegalStateException();
        }
        instance = this;
    }

    public static synchronized Application getInstance() {
        if (instance == null) {
            new net.roydesign.app.Application();
        }
        return instance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        if (this.name == null) {
            this.name = System.getProperty("com.apple.mrj.application.apple.menu.about.name");
        }
        return this.name;
    }

    public void setFramelessMenuBar(MenuBar menuBar) {
        MRJAdapter.setFramelessMenuBar(menuBar);
    }

    public MenuBar getFramelessMenuBar() {
        return MRJAdapter.getFramelessMenuBar();
    }

    public void setFramelessJMenuBar(JMenuBar menuBar) {
        MRJAdapter.setFramelessJMenuBar(menuBar);
    }

    public JMenuBar getFramelessJMenuBar() {
        return MRJAdapter.getFramelessJMenuBar();
    }

    public AboutJMenuItem getAboutJMenuItem() {
        if (MRJAdapter.mrjVersion != -1.0f) {
            if (this.macAboutJMenuItem == null) {
                this.macAboutJMenuItem = new AboutJMenuItem(this);
            }
            return this.macAboutJMenuItem;
        }
        return new AboutJMenuItem(this);
    }

    public AboutMenuItem getAboutMenuItem() {
        if (MRJAdapter.mrjVersion != -1.0f) {
            if (this.macAboutMenuItem == null) {
                this.macAboutMenuItem = new AboutMenuItem(this);
            }
            return this.macAboutMenuItem;
        }
        return new AboutMenuItem(this);
    }

    public PreferencesJMenuItem getPreferencesJMenuItem() {
        if ((double)MRJAdapter.mrjVersion >= 3.0) {
            if (this.macPreferencesJMenuItem == null) {
                this.macPreferencesJMenuItem = new PreferencesJMenuItem();
            }
            return this.macPreferencesJMenuItem;
        }
        return new PreferencesJMenuItem();
    }

    public PreferencesMenuItem getPreferencesMenuItem() {
        if ((double)MRJAdapter.mrjVersion >= 3.0) {
            if (this.macPreferencesMenuItem == null) {
                this.macPreferencesMenuItem = new PreferencesMenuItem();
            }
            return this.macPreferencesMenuItem;
        }
        return new PreferencesMenuItem();
    }

    public QuitJMenuItem getQuitJMenuItem() {
        if ((double)MRJAdapter.mrjVersion >= 3.0) {
            if (this.macQuitJMenuItem == null) {
                this.macQuitJMenuItem = new QuitJMenuItem(this);
            }
            return this.macQuitJMenuItem;
        }
        return new QuitJMenuItem(this);
    }

    public QuitMenuItem getQuitMenuItem() {
        if ((double)MRJAdapter.mrjVersion >= 3.0) {
            if (this.macQuitMenuItem == null) {
                this.macQuitMenuItem = new QuitMenuItem(this);
            }
            return this.macQuitMenuItem;
        }
        return new QuitMenuItem(this);
    }

    public void addOpenApplicationListener(ActionListener l) {
        MRJAdapter.addOpenApplicationListener(l, this);
    }

    public void removeOpenApplicationListener(ActionListener l) {
        MRJAdapter.removeOpenApplicationListener(l);
    }

    public void addReopenApplicationListener(ActionListener l) {
        MRJAdapter.addReopenApplicationListener(l, this);
    }

    public void removeReopenApplicationListener(ActionListener l) {
        MRJAdapter.removeReopenApplicationListener(l);
    }

    public void addOpenDocumentListener(ActionListener l) {
        MRJAdapter.addOpenDocumentListener(l, this);
    }

    public void removeOpenDocumentListener(ActionListener l) {
        MRJAdapter.removeOpenDocumentListener(l);
    }

    public void addPrintDocumentListener(ActionListener l) {
        MRJAdapter.addPrintDocumentListener(l, this);
    }

    public void removePrintDocumentListener(ActionListener l) {
        MRJAdapter.removePrintDocumentListener(l);
    }
}

