/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import net.roydesign.mac.MRJAdapter;
import net.roydesign.ui.AbstractScreenAction;
import net.roydesign.ui.JScreenMenuItem;

public class JScreenMenu
extends JMenu
implements PropertyChangeListener {
    private static ComponentAdapter initialStateSetterMRJ3 = new ComponentAdapter(){

        public void componentResized(ComponentEvent e) {
            Component comp = e.getComponent();
            comp.removeComponentListener(this);
            if (!comp.isEnabled()) {
                comp.setEnabled(true);
                comp.setEnabled(false);
            }
        }
    };
    private Vector userFrames;

    public JScreenMenu() {
        super("");
        this.init();
    }

    public JScreenMenu(String text) {
        super(text);
        this.init();
    }

    private void init() {
        if (MRJAdapter.isSwingUsingScreenMenuBar() && MRJAdapter.mrjVersion >= 3.0f && MRJAdapter.mrjVersion < 4.0f) {
            this.addComponentListener(initialStateSetterMRJ3);
        }
    }

    public JMenuItem add(JMenuItem menuItem) {
        menuItem.addPropertyChangeListener(this);
        return super.add(menuItem);
    }

    public Component add(Component comp) {
        comp.addPropertyChangeListener(this);
        return super.add(comp);
    }

    public Component add(Component comp, int index) {
        comp.addPropertyChangeListener(this);
        return super.add(comp, index);
    }

    public void remove(JMenuItem menuItem) {
        menuItem.removePropertyChangeListener(this);
        super.remove(menuItem);
    }

    public void remove(int index) {
        this.getItem(index).removePropertyChangeListener(this);
        super.remove(index);
    }

    public void remove(Component comp) {
        comp.removePropertyChangeListener(this);
        super.remove(comp);
    }

    public void removeAll() {
        int n = this.getMenuComponentCount();
        int i = 0;
        while (i < n) {
            this.getMenuComponent(i).removePropertyChangeListener(this);
            ++i;
        }
        super.removeAll();
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    public void addNotify() {
        f = this.getParentFrame();
        enabled = false;
        hasSeparator = true;
        n = this.getMenuComponentCount();
        i = n - 1;
        while (i >= 0) {
            comp = this.getMenuComponent(i);
            if (!(comp instanceof JSeparator)) ** GOTO lbl14
            if (!hasSeparator) ** GOTO lbl12
            comp.setVisible(false);
            ** GOTO lbl38
lbl12: // 1 sources:
            hasSeparator = true;
            ** GOTO lbl36
lbl14: // 1 sources:
            if (comp instanceof JScreenMenuItem) {
                mi = (JScreenMenuItem)comp;
                a = mi.getAction();
                if (a != null && a instanceof AbstractScreenAction && !((AbstractScreenAction)a).isUsedBy(f) || !mi.isUsedBy(f)) {
                    if (MRJAdapter.isSwingUsingScreenMenuBar()) {
                        mi.setEnabled(false);
                        hasSeparator = false;
                    } else {
                        mi.setVisible(false);
                    }
                } else {
                    hasSeparator = false;
                }
            } else if (comp instanceof JScreenMenu) {
                m = (JScreenMenu)comp;
                if (!m.isUsedBy(f)) {
                    if (MRJAdapter.isSwingUsingScreenMenuBar()) {
                        hasSeparator = false;
                    }
                } else {
                    hasSeparator = false;
                }
                m.addNotify();
            } else {
                hasSeparator = false;
            }
lbl36: // 6 sources:
            if (comp.isVisible() && comp.isEnabled() && !(comp instanceof JSeparator)) {
                enabled = true;
            }
lbl38: // 4 sources:
            --i;
        }
        if (!enabled) {
            this.setEnabled(false);
        }
        super.addNotify();
    }

    public synchronized void addUserFrame(Class frameClass) {
        if (this.userFrames == null) {
            this.userFrames = new Vector();
        }
        this.userFrames.addElement(frameClass);
    }

    public synchronized void removeUserFrame(Class frameClass) {
        if (this.userFrames == null) {
            return;
        }
        this.userFrames.removeElement(frameClass);
        if (this.userFrames.size() == 0) {
            this.userFrames = null;
        }
    }

    public boolean isUsedBy(JFrame frame) {
        if (this.userFrames != null && !this.userFrames.contains(frame.getClass())) {
            return false;
        }
        return true;
    }

    protected JFrame getParentFrame() {
        Container comp = this.getParent();
        while (comp != null && !(comp instanceof JFrame)) {
            comp = comp.getParent();
        }
        return (JFrame)comp;
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("enabled")) {
            if (((Boolean)e.getNewValue()).booleanValue()) {
                this.setEnabled(true);
            } else {
                int n = this.getMenuComponentCount();
                int i = 0;
                while (i < n) {
                    Component comp = this.getMenuComponent(i);
                    if (comp.isVisible() && comp.isEnabled() && !(comp instanceof JSeparator)) {
                        return;
                    }
                    ++i;
                }
                this.setEnabled(false);
            }
        }
    }

}

