/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.awt.Component;
import java.io.PrintStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.help.BadIDException;
import javax.help.DefaultHelpHistoryModel;
import javax.help.DefaultHelpModel;
import javax.help.HelpHistoryModel;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.InvalidHelpSetContextException;
import javax.help.JHelpContentViewer;
import javax.help.JHelpNavigator;
import javax.help.Map;
import javax.help.NavigatorView;
import javax.help.SwingHelpUtilities;
import javax.help.TextHelpModel;
import javax.help.TreeItem;
import javax.help.event.HelpSetEvent;
import javax.help.event.HelpSetListener;
import javax.help.plaf.HelpUI;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

public class JHelp
extends JComponent
implements HelpSetListener,
Accessible {
    protected TextHelpModel helpModel;
    protected HelpHistoryModel historyModel;
    protected Vector navigators;
    protected boolean navDisplayed = true;
    protected boolean toolbarDisplayed = true;
    protected HelpSet.Presentation hsPres;
    protected JHelpContentViewer contentViewer;
    private boolean debug = false;

    public JHelp(HelpSet helpSet) {
        this(new DefaultHelpModel(helpSet), null, helpSet == null ? null : helpSet.getDefaultPresentation());
    }

    public JHelp() {
        this((TextHelpModel)null);
    }

    public JHelp(TextHelpModel textHelpModel) {
        this(textHelpModel, null, null);
    }

    public JHelp(TextHelpModel textHelpModel, HelpHistoryModel helpHistoryModel, HelpSet.Presentation presentation) {
        this.historyModel = helpHistoryModel == null ? new DefaultHelpHistoryModel(this) : helpHistoryModel;
        this.hsPres = presentation;
        this.navigators = new Vector();
        this.navDisplayed = true;
        this.contentViewer = new JHelpContentViewer(textHelpModel);
        this.setModel(textHelpModel);
        if (textHelpModel != null) {
            this.setupNavigators();
        }
        this.updateUI();
    }

    protected void setupNavigators() {
        HelpSet helpSet = this.helpModel.getHelpSet();
        if (helpSet == null) {
            return;
        }
        NavigatorView[] arrnavigatorView = helpSet.getNavigatorViews();
        this.debug("views: " + arrnavigatorView);
        int n = 0;
        while (n < arrnavigatorView.length) {
            this.debug("  processing info: " + arrnavigatorView[n]);
            JHelpNavigator jHelpNavigator = (JHelpNavigator)arrnavigatorView[n].createNavigator(this.helpModel);
            if (jHelpNavigator == null) {
                this.debug("no JHelpNavigator for given info");
            } else {
                this.debug("  adding the navigator");
                this.navigators.addElement(jHelpNavigator);
            }
            ++n;
        }
    }

    public void setModel(TextHelpModel textHelpModel) {
        TextHelpModel textHelpModel2 = this.helpModel;
        if (textHelpModel != textHelpModel2) {
            Object object;
            if (textHelpModel2 != null) {
                textHelpModel2.getHelpSet().removeHelpSetListener(this);
            }
            this.helpModel = textHelpModel;
            if (textHelpModel != null && (object = textHelpModel.getHelpSet()) != null) {
                object.addHelpSetListener(this);
            }
            this.firePropertyChange("helpModel", textHelpModel2, this.helpModel);
            this.contentViewer.setModel(textHelpModel);
            this.getHistoryModel().setHelpModel(textHelpModel);
            object = this.getUI();
            if (object == null) {
                return;
            }
            Enumeration enumeration = this.getHelpNavigators();
            while (enumeration.hasMoreElements()) {
                JHelpNavigator jHelpNavigator = (JHelpNavigator)enumeration.nextElement();
                object.removeNavigator(jHelpNavigator);
            }
            this.navigators.removeAllElements();
            this.setupNavigators();
            this.updateUI();
        }
    }

    public TextHelpModel getModel() {
        return this.helpModel;
    }

    public HelpHistoryModel getHistoryModel() {
        return this.historyModel;
    }

    public void setHelpSetPresentation(HelpSet.Presentation presentation) {
        this.hsPres = presentation;
    }

    public HelpSet.Presentation getHelpSetPresentation() {
        return this.hsPres;
    }

    public void setHelpSetSpec(String string) {
        HelpSet helpSet;
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            URL uRL = new URL(string);
            helpSet = new HelpSet(classLoader, uRL);
        }
        catch (Exception var5_5) {
            System.err.println("Trouble setting HelpSetSpec to spec |" + string + "|");
            System.err.println("  ex: " + var5_5);
            helpSet = null;
        }
        this.contentViewer.setModel(new DefaultHelpModel(helpSet));
        this.setModel(this.contentViewer.getModel());
        this.updateUI();
    }

    public URL getHelpSetURL() {
        HelpSet helpSet = this.contentViewer.getModel().getHelpSet();
        if (helpSet == null) {
            return null;
        }
        return helpSet.getHelpSetURL();
    }

    public void setUI(HelpUI helpUI) {
        if ((HelpUI)this.ui != helpUI) {
            super.setUI(helpUI);
        }
    }

    public HelpUI getUI() {
        return (HelpUI)this.ui;
    }

    public void updateUI() {
        SwingHelpUtilities.installUIDefaults();
        this.setUI((HelpUI)UIManager.getUI(this));
        this.invalidate();
    }

    public String getUIClassID() {
        return "HelpUI";
    }

    private JHelpNavigator findNavigator(String string) {
        this.debug("findNavigator(" + string + ")");
        Enumeration enumeration = this.getHelpNavigators();
        while (enumeration.hasMoreElements()) {
            JHelpNavigator jHelpNavigator = (JHelpNavigator)enumeration.nextElement();
            this.debug("  nav: " + jHelpNavigator);
            this.debug("  nav.getName: " + jHelpNavigator.getNavigatorName());
            if (!jHelpNavigator.getNavigatorName().equals(string)) continue;
            return jHelpNavigator;
        }
        return null;
    }

    public void helpSetAdded(HelpSetEvent helpSetEvent) {
        this.debug("helpSetAdded(" + helpSetEvent + ")");
        HelpSet helpSet = helpSetEvent.getHelpSet();
        this.addHelpSet(helpSet);
    }

    private void addHelpSet(HelpSet helpSet) {
        this.debug("helpset :" + helpSet);
        NavigatorView[] arrnavigatorView = helpSet.getNavigatorViews();
        int n = 0;
        Enumeration enumeration = this.getHelpNavigators();
        while (enumeration.hasMoreElements()) {
            ++n;
            enumeration.nextElement();
        }
        if (n == 0) {
            this.debug("master helpset without navigators");
            DefaultHelpModel defaultHelpModel = new DefaultHelpModel(helpSet);
            this.setModel(defaultHelpModel);
            this.setupNavigators();
            return;
        }
        int n2 = 0;
        while (n2 < arrnavigatorView.length) {
            String string = arrnavigatorView[n2].getName();
            this.debug("addHelpSet: looking for navigator for " + string);
            JHelpNavigator jHelpNavigator = this.findNavigator(string);
            if (jHelpNavigator != null) {
                this.debug("   found");
                if (jHelpNavigator.canMerge(arrnavigatorView[n2])) {
                    this.debug("  canMerge: true; merging...");
                    jHelpNavigator.merge(arrnavigatorView[n2]);
                } else {
                    this.debug("  canMerge: false");
                }
            } else {
                this.debug("   not found");
            }
            ++n2;
        }
    }

    public void helpSetRemoved(HelpSetEvent helpSetEvent) {
        this.debug("helpSetRemoved(" + helpSetEvent + ")");
        HelpSet helpSet = helpSetEvent.getHelpSet();
        this.removeHelpSet(helpSet);
    }

    private void removeHelpSet(HelpSet helpSet) {
        NavigatorView[] arrnavigatorView = helpSet.getNavigatorViews();
        int n = 0;
        while (n < arrnavigatorView.length) {
            String string = arrnavigatorView[n].getName();
            this.debug("removeHelpSet: looking for navigator for " + string);
            JHelpNavigator jHelpNavigator = this.findNavigator(string);
            if (jHelpNavigator != null) {
                this.debug("   found");
                if (jHelpNavigator.canMerge(arrnavigatorView[n])) {
                    this.debug("  canMerge: true; removing...");
                    jHelpNavigator.remove(arrnavigatorView[n]);
                } else {
                    this.debug("  canMerge: false");
                }
            } else {
                this.debug("   not found");
            }
            ++n;
        }
        this.getHistoryModel().removeHelpSet(helpSet);
    }

    public void setCurrentID(Map.ID iD) throws InvalidHelpSetContextException {
        if (this.helpModel != null) {
            this.helpModel.setCurrentID(iD);
        }
    }

    public void setCurrentID(Map.ID iD, String string, JHelpNavigator jHelpNavigator) throws InvalidHelpSetContextException {
        if (this.helpModel != null) {
            this.helpModel.setCurrentID(iD, string, jHelpNavigator);
        }
    }

    public void setCurrentID(String string) throws BadIDException {
        try {
            this.helpModel.setCurrentID(Map.ID.create(string, this.getModel().getHelpSet()));
        }
        catch (InvalidHelpSetContextException var2_2) {
            // empty catch block
        }
    }

    public void setCurrentURL(URL uRL) {
        this.helpModel.setCurrentURL(uRL);
    }

    public void setCurrentURL(URL uRL, String string, JHelpNavigator jHelpNavigator) {
        this.helpModel.setCurrentURL(uRL, string, jHelpNavigator);
    }

    public TreeItem[] getSelectedItems() {
        return this.getCurrentNavigator().getSelectedItems();
    }

    public void addHelpNavigator(JHelpNavigator jHelpNavigator) {
        this.debug("addHelpNavigator(" + jHelpNavigator + ")");
        this.navigators.addElement(jHelpNavigator);
        HelpUI helpUI = this.getUI();
        helpUI.addNavigator(jHelpNavigator);
        jHelpNavigator.setModel(this.getModel());
    }

    public void removeHelpNavigator(JHelpNavigator jHelpNavigator) {
        this.debug("removeHelpNavigator(" + jHelpNavigator + ")");
        if (jHelpNavigator == null) {
            throw new NullPointerException("navigator");
        }
        this.navigators.removeElement(jHelpNavigator);
        HelpUI helpUI = this.getUI();
        helpUI.removeNavigator(jHelpNavigator);
    }

    public Enumeration getHelpNavigators() {
        return this.navigators.elements();
    }

    public void setCurrentNavigator(JHelpNavigator jHelpNavigator) {
        HelpUI helpUI = this.getUI();
        helpUI.setCurrentNavigator(jHelpNavigator);
    }

    public JHelpNavigator getCurrentNavigator() {
        HelpUI helpUI = this.getUI();
        return helpUI.getCurrentNavigator();
    }

    public void setNavigatorDisplayed(boolean bl) {
        if (this.navDisplayed != bl) {
            this.navDisplayed = bl;
            this.firePropertyChange("navigatorDisplayed", !bl, bl);
        }
    }

    public boolean isNavigatorDisplayed() {
        return this.navDisplayed;
    }

    public void setToolbarDisplayed(boolean bl) {
        if (this.toolbarDisplayed != bl) {
            this.toolbarDisplayed = bl;
            this.firePropertyChange("toolbarDisplayed", !bl, bl);
        }
    }

    public boolean isToolbarDisplayed() {
        return this.toolbarDisplayed;
    }

    public JHelpContentViewer getContentViewer() {
        return this.contentViewer;
    }

    private void debug(String string) {
        if (this.debug) {
            System.err.println("JHelp: " + string);
        }
    }

    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJHelp();
        }
        return this.accessibleContext;
    }

    static {
        SwingHelpUtilities.installLookAndFeelDefaults();
    }

    protected class AccessibleJHelp
    extends JComponent.AccessibleJComponent {
        protected AccessibleJHelp() {
            super(JHelp.this);
        }

        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
    }

}

