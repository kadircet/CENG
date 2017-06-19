/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.ui;

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.awt.MenuItem;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import javax.swing.Action;
import net.roydesign.mac.MRJAdapter;
import net.roydesign.ui.AbstractScreenAction;
import net.roydesign.ui.ScreenMenuItem;

public class ScreenMenu
extends Menu
implements PropertyChangeListener {
    private Vector userFrames;

    public ScreenMenu() {
        super("");
    }

    public ScreenMenu(String text) {
        super(text);
    }

    public ScreenMenu(String text, boolean tearOff) {
        super(text, tearOff);
    }

    public MenuItem add(MenuItem menuItem) {
        if (menuItem instanceof ScreenMenuItem) {
            ((ScreenMenuItem)menuItem).addPropertyChangeListener(this);
        }
        return super.add(menuItem);
    }

    public void insert(MenuItem menuItem, int index) {
        if (menuItem instanceof ScreenMenuItem) {
            ((ScreenMenuItem)menuItem).addPropertyChangeListener(this);
        }
        super.insert(menuItem, index);
    }

    public void remove(int index) {
        MenuItem it = this.getItem(index);
        if (it instanceof ScreenMenuItem) {
            ((ScreenMenuItem)it).removePropertyChangeListener(this);
        }
        super.remove(index);
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
        n = this.getItemCount();
        i = n - 1;
        while (i >= 0) {
            it = this.getItem(i);
            if (!it.getLabel().equals("-")) ** GOTO lbl14
            if (!hasSeparator) ** GOTO lbl12
            this.remove(i);
            ** GOTO lbl40
lbl12: // 1 sources:
            hasSeparator = true;
            ** GOTO lbl38
lbl14: // 1 sources:
            if (it instanceof ScreenMenuItem) {
                mi = (ScreenMenuItem)it;
                a = mi.getAction();
                if (a != null && a instanceof AbstractScreenAction && !((AbstractScreenAction)a).isUsedBy(f) || !mi.isUsedBy(f)) {
                    if (MRJAdapter.isAWTUsingScreenMenuBar()) {
                        mi.setEnabled(false);
                        hasSeparator = false;
                    } else {
                        this.remove(i);
                    }
                } else {
                    hasSeparator = false;
                }
            } else if (it instanceof ScreenMenu) {
                m = (ScreenMenu)it;
                if (!m.isUsedBy(f)) {
                    if (MRJAdapter.isAWTUsingScreenMenuBar()) {
                        m.setEnabled(false);
                        hasSeparator = false;
                    } else {
                        this.remove(i);
                    }
                } else {
                    hasSeparator = false;
                }
            } else {
                hasSeparator = false;
            }
lbl38: // 8 sources:
            if (it.getParent() != null && it.isEnabled() && !it.getLabel().equals("-")) {
                enabled = true;
            }
lbl40: // 4 sources:
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

    public boolean isUsedBy(Frame frame) {
        if (this.userFrames != null && !this.userFrames.contains(frame.getClass())) {
            return false;
        }
        return true;
    }

    protected Frame getParentFrame() {
        MenuContainer cont = this.getParent();
        while (cont != null && !(cont instanceof Frame)) {
            cont = ((MenuComponent)((Object)cont)).getParent();
        }
        return (Frame)cont;
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("enabled")) {
            if (((Boolean)e.getNewValue()).booleanValue()) {
                this.setEnabled(true);
            } else {
                int n = this.getItemCount();
                int i = 0;
                while (i < n) {
                    MenuItem it = this.getItem(i);
                    if (it.isEnabled() && !it.getLabel().equals("-")) {
                        return;
                    }
                    ++i;
                }
                this.setEnabled(false);
            }
        }
    }
}

