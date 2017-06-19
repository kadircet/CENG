/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  org.jdesktop.jdic.browser.WebBrowser
 */
package javax.help.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.net.URL;
import java.util.Locale;
import javax.accessibility.AccessibleContext;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.JHelpContentViewer;
import javax.help.JHelpNavigator;
import javax.help.Map;
import javax.help.TextHelpModel;
import javax.help.event.HelpModelEvent;
import javax.help.event.HelpModelListener;
import javax.help.event.TextHelpModelEvent;
import javax.help.event.TextHelpModelListener;
import javax.help.plaf.HelpContentViewerUI;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import org.jdesktop.jdic.browser.WebBrowser;

public class BasicNativeContentViewerUI
extends HelpContentViewerUI
implements HelpModelListener,
TextHelpModelListener,
PropertyChangeListener,
Serializable {
    protected JHelpContentViewer theViewer;
    private static Dimension PREF_SIZE = new Dimension(200, 300);
    private static Dimension MIN_SIZE = new Dimension(80, 80);
    private WebBrowser html;
    private JViewport vp;
    private static final boolean debug = false;

    public static ComponentUI createUI(JComponent jComponent) {
        BasicNativeContentViewerUI.debug("createUI");
        return new BasicNativeContentViewerUI((JHelpContentViewer)jComponent);
    }

    public BasicNativeContentViewerUI(JHelpContentViewer jHelpContentViewer) {
        BasicNativeContentViewerUI.debug("createUI - sort of");
    }

    public void installUI(JComponent jComponent) {
        Serializable serializable;
        BasicNativeContentViewerUI.debug("installUI");
        this.theViewer = (JHelpContentViewer)jComponent;
        this.theViewer.setLayout(new BorderLayout());
        this.theViewer.addPropertyChangeListener(this);
        TextHelpModel textHelpModel = this.theViewer.getModel();
        if (textHelpModel != null) {
            textHelpModel.addHelpModelListener(this);
            textHelpModel.addTextHelpModelListener(this);
        }
        this.html = new WebBrowser();
        this.html.getAccessibleContext().setAccessibleName(HelpUtilities.getString(HelpUtilities.getLocale((Component)this.html), "access.contentViewer"));
        if (textHelpModel != null && (serializable = textHelpModel.getCurrentURL()) != null) {
            this.html.setURL(serializable);
        }
        serializable = new JScrollPane();
        serializable.setBorder(new BevelBorder(1, Color.white, Color.gray));
        this.vp = serializable.getViewport();
        this.vp.add((Component)this.html);
        this.vp.setBackingStoreEnabled(true);
        this.theViewer.add("Center", (Component)serializable);
    }

    public void uninstallUI(JComponent jComponent) {
        BasicNativeContentViewerUI.debug("uninstallUI");
        JHelpContentViewer jHelpContentViewer = (JHelpContentViewer)jComponent;
        jHelpContentViewer.removePropertyChangeListener(this);
        TextHelpModel textHelpModel = jHelpContentViewer.getModel();
        if (textHelpModel != null) {
            textHelpModel.removeHelpModelListener(this);
            textHelpModel.removeTextHelpModelListener(this);
        }
        jHelpContentViewer.setLayout(null);
        jHelpContentViewer.removeAll();
    }

    public Dimension getPreferredSize(JComponent jComponent) {
        return PREF_SIZE;
    }

    public Dimension getMinimumSize(JComponent jComponent) {
        return MIN_SIZE;
    }

    public Dimension getMaximumSize(JComponent jComponent) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public void idChanged(HelpModelEvent helpModelEvent) {
        Map.ID iD = helpModelEvent.getID();
        URL uRL = helpModelEvent.getURL();
        TextHelpModel textHelpModel = this.theViewer.getModel();
        BasicNativeContentViewerUI.debug("idChanged(" + helpModelEvent + ")");
        BasicNativeContentViewerUI.debug("  = " + iD + " " + uRL);
        BasicNativeContentViewerUI.debug("  my helpModel: " + textHelpModel);
        textHelpModel.setDocumentTitle(null);
        this.html.setURL(uRL);
        BasicNativeContentViewerUI.debug("done with idChanged");
    }

    private void rebuild() {
        BasicNativeContentViewerUI.debug("rebuild");
        TextHelpModel textHelpModel = this.theViewer.getModel();
        if (textHelpModel == null) {
            BasicNativeContentViewerUI.debug("rebuild-end: model is null");
            return;
        }
        HelpSet helpSet = textHelpModel.getHelpSet();
        if (this.theViewer.getSynch()) {
            try {
                Map.ID iD = helpSet.getHomeID();
                Locale locale = helpSet.getLocale();
                String string = HelpUtilities.getString(locale, "history.homePage");
                textHelpModel.setCurrentID(iD, string, null);
                this.html.setURL(textHelpModel.getCurrentURL());
            }
            catch (Exception var3_4) {
                // empty catch block
            }
        }
        BasicNativeContentViewerUI.debug("rebuild-end");
    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        BasicNativeContentViewerUI.debug("propertyChange: " + propertyChangeEvent.getPropertyName() + "\n\toldValue:" + propertyChangeEvent.getOldValue() + "\n\tnewValue:" + propertyChangeEvent.getNewValue());
        if (propertyChangeEvent.getSource() == this.theViewer) {
            String string = propertyChangeEvent.getPropertyName();
            if (string.equals("helpModel")) {
                TextHelpModel textHelpModel = (TextHelpModel)propertyChangeEvent.getOldValue();
                TextHelpModel textHelpModel2 = (TextHelpModel)propertyChangeEvent.getNewValue();
                if (textHelpModel != null) {
                    textHelpModel.removeHelpModelListener(this);
                    textHelpModel.removeTextHelpModelListener(this);
                }
                if (textHelpModel2 != null) {
                    textHelpModel2.addHelpModelListener(this);
                    textHelpModel2.addTextHelpModelListener(this);
                }
                this.rebuild();
            } else if (string.equals("font")) {
                BasicNativeContentViewerUI.debug("font changed");
                Font font = (Font)propertyChangeEvent.getNewValue();
            } else if (!string.equals("clear") && string.equals("reload")) {
                this.html.refresh();
            }
        }
    }

    public void highlightsChanged(TextHelpModelEvent textHelpModelEvent) {
        BasicNativeContentViewerUI.debug("highlightsChanged " + textHelpModelEvent);
    }

    private static void debug(String string) {
    }
}

