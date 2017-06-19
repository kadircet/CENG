/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public abstract class AbstractScreenAction
extends AbstractAction {
    public static final String LARGE_ICON = "LargeIcon";
    private Vector userFrames;

    public AbstractScreenAction() {
    }

    public AbstractScreenAction(String name) {
        super(name);
    }

    public AbstractScreenAction(String name, Icon icon) {
        super(name, icon);
    }

    public void addUserFrame(Class frameClass) {
        if (this.userFrames == null) {
            this.userFrames = new Vector();
        }
        this.userFrames.addElement(frameClass);
    }

    public void removeUserFrame(Class frameClass) {
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

    public JFrame getSourceJFrame(ActionEvent e) {
        return (JFrame)this.getSourceFrame(e);
    }

    public Frame getSourceFrame(ActionEvent e) {
        Object obj = e.getSource();
        if (obj instanceof JMenuItem) {
            Container comp = ((JMenuItem)obj).getParent();
            while (comp instanceof JPopupMenu) {
                JPopupMenu pm = (JPopupMenu)comp;
                JMenu m = (JMenu)pm.getInvoker();
                comp = m.getParent();
            }
            while (!(comp instanceof Frame)) {
                comp = comp.getParent();
            }
            return (Frame)comp;
        }
        if (obj instanceof MenuComponent) {
            MenuContainer cont = ((MenuComponent)obj).getParent();
            while (cont instanceof MenuComponent) {
                cont = ((MenuComponent)((Object)cont)).getParent();
            }
            return (Frame)cont;
        }
        if (obj instanceof Component) {
            Container cont = ((Component)obj).getParent();
            while (!(cont instanceof Frame)) {
                cont = cont.getParent();
            }
            return (Frame)cont;
        }
        return null;
    }
}

