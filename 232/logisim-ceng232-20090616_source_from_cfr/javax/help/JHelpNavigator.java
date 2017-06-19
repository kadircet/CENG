/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.net.URL;
import java.util.Hashtable;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.help.DefaultHelpModel;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.NavigatorView;
import javax.help.SwingHelpUtilities;
import javax.help.TreeItem;
import javax.help.UnsupportedOperationException;
import javax.help.event.HelpModelListener;
import javax.help.plaf.HelpNavigatorUI;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

public class JHelpNavigator
extends JComponent
implements Accessible {
    protected HelpModel helpModel;
    protected String type;
    private static String jhPackageName;
    private NavigatorView view;
    private TreeItem[] selectedItems;
    private static final boolean debug = false;

    public JHelpNavigator(NavigatorView navigatorView) {
        this.view = navigatorView;
        if (navigatorView != null) {
            this.setModel(new DefaultHelpModel(navigatorView.getHelpSet()));
        } else {
            this.setModel(null);
        }
        this.updateUI();
    }

    public JHelpNavigator(NavigatorView navigatorView, HelpModel helpModel) {
        this.view = navigatorView;
        this.setModel(helpModel);
        this.updateUI();
    }

    public TreeItem[] getSelectedItems() {
        if (this.selectedItems == null) {
            return new TreeItem[0];
        }
        return (TreeItem[])this.selectedItems.clone();
    }

    public void setSelectedItems(TreeItem[] arrtreeItem) {
        TreeItem[] arrtreeItem2 = this.selectedItems;
        this.selectedItems = arrtreeItem;
        this.firePropertyChange("SelectedItemsChangedProperty", arrtreeItem2, this.selectedItems);
    }

    public String getUIClassID() {
        return "HelpNavigatorUI";
    }

    public boolean canMerge(NavigatorView navigatorView) {
        return false;
    }

    public void merge(NavigatorView navigatorView) {
        throw new UnsupportedOperationException();
    }

    public void remove(NavigatorView navigatorView) {
        throw new UnsupportedOperationException();
    }

    public String getNavigatorName() {
        return this.view.getName();
    }

    public NavigatorView getNavigatorView() {
        return this.view;
    }

    public String getNavigatorLabel() {
        return this.view.getLabel();
    }

    public String getNavigatorLabel(Locale locale) {
        return this.view.getLabel(locale);
    }

    public Icon getIcon() {
        return this.getUI().getIcon();
    }

    public void setModel(HelpModel helpModel) {
        HelpModel helpModel2 = this.helpModel;
        if (helpModel != helpModel2) {
            this.helpModel = helpModel;
            this.firePropertyChange("helpModel", helpModel2, this.helpModel);
            this.invalidate();
        }
    }

    public HelpModel getModel() {
        return this.helpModel;
    }

    public void setUI(HelpNavigatorUI helpNavigatorUI) {
        if ((HelpNavigatorUI)this.ui != helpNavigatorUI) {
            super.setUI(helpNavigatorUI);
        }
    }

    public HelpNavigatorUI getUI() {
        return (HelpNavigatorUI)this.ui;
    }

    public void updateUI() {
        SwingHelpUtilities.installUIDefaults();
        this.setUI((HelpNavigatorUI)UIManager.getUI(this));
        this.invalidate();
    }

    public void addHelpModelListener(HelpModelListener helpModelListener) {
        this.getModel().addHelpModelListener(helpModelListener);
    }

    public void removeHelpModelListener(HelpModelListener helpModelListener) {
        this.getModel().removeHelpModelListener(helpModelListener);
    }

    protected static Hashtable createParams(URL uRL) {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put("data", uRL.toString());
        return hashtable;
    }

    private static void debug(String string) {
    }

    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJHelpNavigator();
        }
        return this.accessibleContext;
    }

    static {
        SwingHelpUtilities.installLookAndFeelDefaults();
    }

    protected class AccessibleJHelpNavigator
    extends JComponent.AccessibleJComponent {
        protected AccessibleJHelpNavigator() {
            super(JHelpNavigator.this);
        }

        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
    }

}

