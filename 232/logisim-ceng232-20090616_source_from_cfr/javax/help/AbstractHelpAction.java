/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Hashtable;
import javax.help.HelpAction;

public abstract class AbstractHelpAction
implements HelpAction {
    private boolean enabled = true;
    private Object control;
    private Hashtable table;
    private PropertyChangeSupport propertyChangeSupport;

    AbstractHelpAction(Object object, String string) {
        this.control = object;
        this.putValue("name", string);
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (this.propertyChangeSupport == null) {
            this.propertyChangeSupport = new PropertyChangeSupport(this);
        }
        this.propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (this.propertyChangeSupport == null) {
            this.propertyChangeSupport = new PropertyChangeSupport(this);
        }
        this.propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
    }

    protected void firePropertyChange(String string, Object object, Object object2) {
        if (this.propertyChangeSupport == null) {
            return;
        }
        this.propertyChangeSupport.firePropertyChange(string, object, object2);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean bl) {
        boolean bl2 = this.enabled;
        this.enabled = bl;
        this.firePropertyChange("enabled", new Boolean(bl2), new Boolean(bl));
    }

    public Object getControl() {
        return this.control;
    }

    public Object getValue(String string) {
        if (this.table == null) {
            return null;
        }
        return this.table.get(string);
    }

    public void putValue(String string, Object object) {
        if (this.table == null) {
            this.table = new Hashtable();
        }
        Object v = object == null ? this.table.remove(string) : this.table.put(string, object);
        this.firePropertyChange(string, v, object);
    }
}

