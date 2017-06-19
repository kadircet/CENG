/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.ui;

import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.Vector;
import javax.swing.Action;

public class ScreenMenuItem
extends MenuItem {
    private Action action;
    private PropertyChangeListener actionPropertyChangeListener;
    private PropertyChangeSupport propertiesHandler;
    private Vector userFrames;

    public ScreenMenuItem() {
        this.actionPropertyChangeListener = new PropertyChangeListener(){

            public void propertyChange(PropertyChangeEvent e) {
                String prop = e.getPropertyName();
                if (prop.equals("action")) {
                    ScreenMenuItem.this.configurePropertiesFromAction((Action)e.getNewValue());
                } else if (prop.equals("Name")) {
                    ScreenMenuItem.this.setLabel((String)e.getNewValue());
                } else if (prop.equals("enabled")) {
                    ScreenMenuItem.this.setEnabled((Boolean)e.getNewValue());
                } else if (prop.equals("ActionCommandKey")) {
                    ScreenMenuItem.this.setActionCommand((String)e.getNewValue());
                }
            }
        };
        this.propertiesHandler = new PropertyChangeSupport(this);
    }

    public ScreenMenuItem(String text) {
        super(text);
        this.actionPropertyChangeListener = new ;
        this.propertiesHandler = new PropertyChangeSupport(this);
    }

    public ScreenMenuItem(String text, MenuShortcut shortcut) {
        super(text, shortcut);
        this.actionPropertyChangeListener = new ;
        this.propertiesHandler = new PropertyChangeSupport(this);
    }

    public ScreenMenuItem(Action action) {
        this.actionPropertyChangeListener = new ;
        this.propertiesHandler = new PropertyChangeSupport(this);
        this.setAction(action);
    }

    public synchronized void setLabel(String label) {
        String oldLabel = this.getLabel();
        super.setLabel(label);
        if (!label.equals(oldLabel)) {
            this.propertiesHandler.firePropertyChange("label", oldLabel, label);
        }
    }

    public synchronized void setEnabled(boolean enabled) {
        boolean oldEnabled = this.isEnabled();
        super.setEnabled(enabled);
        if (enabled != oldEnabled) {
            this.propertiesHandler.firePropertyChange("enabled", new Boolean(oldEnabled), new Boolean(enabled));
        }
    }

    public void setShortcut(MenuShortcut shortcut) {
        MenuShortcut oldShortcut = this.getShortcut();
        super.setShortcut(shortcut);
        if (shortcut != oldShortcut) {
            this.propertiesHandler.firePropertyChange("shortcut", oldShortcut, shortcut);
        }
    }

    public Action getAction() {
        return this.action;
    }

    public void setAction(Action action) {
        Action oldAction = this.action;
        if (oldAction == null || !oldAction.equals(action)) {
            this.action = action;
            if (oldAction != null) {
                this.removeActionListener(oldAction);
                oldAction.removePropertyChangeListener(this.actionPropertyChangeListener);
            }
            this.configurePropertiesFromAction(this.action);
            if (this.action != null) {
                this.addActionListener(this.action);
                this.action.addPropertyChangeListener(this.actionPropertyChangeListener);
            }
        }
    }

    protected void configurePropertiesFromAction(Action action) {
        if (action != null) {
            this.setLabel((String)action.getValue("Name"));
            this.setEnabled(action.isEnabled());
            Object ks = action.getValue("AcceleratorKey");
            if (ks != null) {
                try {
                    Method met = ks.getClass().getMethod("getModifiers", null);
                    Object obj = met.invoke(ks, null);
                    int mdfrs = ((Number)obj).intValue();
                    if ((mdfrs & 4) != 0) {
                        boolean shft = (mdfrs & 1) != 0;
                        met = ks.getClass().getMethod("getKeyCode", null);
                        obj = met.invoke(ks, null);
                        int code = ((Number)obj).intValue();
                        this.setShortcut(new MenuShortcut(code, shft));
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                this.setShortcut(null);
            }
        } else {
            this.setLabel(null);
            this.setEnabled(true);
            this.setShortcut(null);
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

    public boolean isUsedBy(Frame frame) {
        if (this.userFrames != null && !this.userFrames.contains(frame.getClass())) {
            return false;
        }
        return true;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        this.propertiesHandler.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        this.propertiesHandler.removePropertyChangeListener(l);
    }

}

