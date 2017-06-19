/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.menu;

import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.gui.menu.Strings;
import com.cburch.logisim.gui.start.About;
import com.cburch.logisim.util.MacCompatibility;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Locale;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

class MenuHelp
extends JMenu
implements ActionListener {
    private static final String HELPSET_LOC = "doc/doc.hs";
    private LogisimMenuBar menubar;
    private JMenuItem tutorial = new JMenuItem();
    private JMenuItem guide = new JMenuItem();
    private JMenuItem library = new JMenuItem();
    private JMenuItem about = new JMenuItem();
    private HelpSet helpSet;
    private HelpBroker helpBroker;

    public MenuHelp(LogisimMenuBar menubar) {
        this.menubar = menubar;
        this.tutorial.addActionListener(this);
        this.guide.addActionListener(this);
        this.library.addActionListener(this);
        this.about.addActionListener(this);
        this.add(this.tutorial);
        this.add(this.guide);
        this.add(this.library);
        if (!MacCompatibility.isAboutAutomaticallyPresent()) {
            this.addSeparator();
            this.add(this.about);
        }
    }

    public void localeChanged() {
        this.setText(Strings.get("helpMenu"));
        this.tutorial.setText(Strings.get("helpTutorialItem"));
        this.guide.setText(Strings.get("helpGuideItem"));
        this.library.setText(Strings.get("helpLibraryItem"));
        this.about.setText(Strings.get("helpAboutItem"));
        if (this.helpBroker != null) {
            this.helpBroker.setLocale(Locale.getDefault());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == this.guide) {
            this.showHelp("guide");
        } else if (src == this.tutorial) {
            this.showHelp("tutorial");
        } else if (src == this.library) {
            this.showHelp("libs");
        } else if (src == this.about) {
            About.showAboutDialog(this.menubar.getParentWindow());
        }
    }

    private void showHelp(String target) {
        if (this.helpSet == null || this.helpBroker == null) {
            ClassLoader cl = MenuHelp.class.getClassLoader();
            try {
                URL hsURL = HelpSet.findHelpSet(cl, "doc/doc.hs");
                if (hsURL == null) {
                    this.disableHelp();
                    JOptionPane.showMessageDialog(this.menubar.getParentWindow(), Strings.get("helpNotFoundError"));
                    return;
                }
                this.helpSet = new HelpSet(null, hsURL);
                this.helpBroker = this.helpSet.createHelpBroker();
            }
            catch (Exception e) {
                this.disableHelp();
                e.printStackTrace();
                JOptionPane.showMessageDialog(this.menubar.getParentWindow(), Strings.get("helpUnavailableError"));
                return;
            }
        }
        try {
            this.helpBroker.setCurrentID(target);
            this.helpBroker.setViewDisplayed(true);
            this.helpBroker.setDisplayed(true);
        }
        catch (Exception e) {
            this.disableHelp();
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.menubar.getParentWindow(), Strings.get("helpDisplayError"));
        }
    }

    private void disableHelp() {
        this.guide.setEnabled(false);
        this.tutorial.setEnabled(false);
        this.library.setEnabled(false);
    }
}

