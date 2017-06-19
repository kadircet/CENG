/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.ui;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import net.roydesign.mac.MRJAdapter;

public class JScreenMenuItem
extends JMenuItem {
    private Action actionBefore13;
    private PropertyChangeListener actionPropertyChangeListener;
    private Vector userFrames;

    public JScreenMenuItem() {
        this.actionPropertyChangeListener = new PropertyChangeListener(){

            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("action")) {
                    JScreenMenuItem.this.configurePropertiesFromAction((Action)e.getNewValue());
                }
            }
        };
    }

    public JScreenMenuItem(Icon icon) {
        super(icon);
        this.actionPropertyChangeListener = new ;
    }

    public JScreenMenuItem(String text) {
        super(text);
        this.actionPropertyChangeListener = new ;
    }

    public JScreenMenuItem(Action action) {
        this.actionPropertyChangeListener = new ;
        this.setAction(action);
    }

    public JScreenMenuItem(String text, Icon icon) {
        super(text, icon);
        this.actionPropertyChangeListener = new ;
    }

    public JScreenMenuItem(String text, int mnemonic) {
        super(text, mnemonic);
        this.actionPropertyChangeListener = new ;
    }

    public Action getAction() {
        if (MRJAdapter.javaVersion < 1.3f) {
            return this.actionBefore13;
        }
        return super.getAction();
    }

    public void setAction(Action action) {
        if (MRJAdapter.javaVersion < 1.3f) {
            this.setActionBefore13(action);
        } else {
            super.setAction(action);
        }
    }

    private void setActionBefore13(Action action) {
        Action oldAction = this.actionBefore13;
        if (oldAction == null || !oldAction.equals(action)) {
            this.actionBefore13 = action;
            if (oldAction != null) {
                this.removeActionListener(oldAction);
                oldAction.removePropertyChangeListener(this.actionPropertyChangeListener);
            }
            this.configurePropertiesFromAction(this.actionBefore13);
            if (this.actionBefore13 != null) {
                this.addActionListener(this.actionBefore13);
                this.actionBefore13.addPropertyChangeListener(this.actionPropertyChangeListener);
            }
            this.firePropertyChange("action", oldAction, this.actionBefore13);
            this.revalidate();
            this.repaint();
        }
    }

    protected void configurePropertiesFromAction(Action action) {
        if (MRJAdapter.javaVersion >= 1.3f) {
            super.configurePropertiesFromAction(action);
            if (MRJAdapter.javaVersion == 1.3f) {
                this.setAccelerator(action != null ? (KeyStroke)action.getValue("AcceleratorKey") : null);
            }
        } else {
            Integer i;
            this.setText(action != null ? (String)action.getValue("Name") : null);
            this.setIcon(action != null ? (Icon)action.getValue("SmallIcon") : null);
            this.setAccelerator(action != null ? (KeyStroke)action.getValue("AcceleratorKey") : null);
            this.setEnabled(action != null ? action.isEnabled() : true);
            this.setToolTipText(action != null ? (String)action.getValue("ShortDescription") : null);
            if (action != null && (i = (Integer)action.getValue("MnemonicKey")) != null) {
                this.setMnemonic(i);
            }
        }
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

}

