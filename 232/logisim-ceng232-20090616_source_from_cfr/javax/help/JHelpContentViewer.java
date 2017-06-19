/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.io.PrintStream;
import java.net.URL;
import java.util.Hashtable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.help.BadIDException;
import javax.help.DefaultHelpModel;
import javax.help.HelpSet;
import javax.help.InvalidHelpSetContextException;
import javax.help.Map;
import javax.help.SwingHelpUtilities;
import javax.help.TextHelpModel;
import javax.help.event.HelpModelListener;
import javax.help.event.TextHelpModelListener;
import javax.help.plaf.HelpContentViewerUI;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.EditorKit;

public class JHelpContentViewer
extends JComponent
implements Accessible {
    protected TextHelpModel model;
    private boolean synch = true;
    private Hashtable kitRegistry;
    private boolean debug = false;

    public JHelpContentViewer(HelpSet helpSet) {
        this.setModel(new DefaultHelpModel(helpSet));
        this.updateUI();
    }

    public JHelpContentViewer() {
        this.setModel(new DefaultHelpModel(null));
        this.updateUI();
    }

    public JHelpContentViewer(TextHelpModel textHelpModel) {
        this.setModel(textHelpModel);
        this.updateUI();
    }

    public void setModel(TextHelpModel textHelpModel) {
        this.debug("setModel: " + textHelpModel);
        TextHelpModel textHelpModel2 = this.model;
        if (textHelpModel != textHelpModel2) {
            this.model = textHelpModel;
            this.firePropertyChange("helpModel", textHelpModel2, this.model);
            this.invalidate();
        }
    }

    public TextHelpModel getModel() {
        return this.model;
    }

    public void setUI(HelpContentViewerUI helpContentViewerUI) {
        this.debug("setUI");
        if ((HelpContentViewerUI)this.ui != helpContentViewerUI) {
            super.setUI(helpContentViewerUI);
            this.repaint();
        }
    }

    public HelpContentViewerUI getUI() {
        return (HelpContentViewerUI)this.ui;
    }

    public void updateUI() {
        SwingHelpUtilities.installUIDefaults();
        this.setUI((HelpContentViewerUI)UIManager.getUI(this));
        this.invalidate();
    }

    public String getUIClassID() {
        return "HelpContentViewerUI";
    }

    public void setCurrentID(Map.ID iD) throws InvalidHelpSetContextException {
        this.model.setCurrentID(iD);
    }

    public void setCurrentID(String string) throws BadIDException {
        try {
            this.model.setCurrentID(Map.ID.create(string, this.getModel().getHelpSet()));
        }
        catch (InvalidHelpSetContextException var2_2) {
            // empty catch block
        }
    }

    public void setCurrentURL(URL uRL) {
        this.model.setCurrentURL(uRL);
    }

    public URL getCurrentURL() {
        return this.model.getCurrentURL();
    }

    public String getDocumentTitle() {
        return this.model.getDocumentTitle();
    }

    public void addHighlight(int n, int n2) {
        this.model.addHighlight(n, n2);
    }

    public void removeAllHighlights() {
        this.model.removeAllHighlights();
    }

    public void setSynch(boolean bl) {
        this.synch = bl;
    }

    public boolean getSynch() {
        return this.synch;
    }

    public EditorKit createEditorKitForContentType(String string) {
        EditorKit editorKit = null;
        if (this.kitRegistry == null) {
            this.kitRegistry = new Hashtable();
        } else {
            editorKit = (EditorKit)this.kitRegistry.get(string);
        }
        if (editorKit == null) {
            HelpSet helpSet = this.model.getHelpSet();
            String string2 = (String)helpSet.getKeyData(HelpSet.kitTypeRegistry, string);
            if (string2 == null) {
                return null;
            }
            ClassLoader classLoader = (ClassLoader)helpSet.getKeyData(HelpSet.kitLoaderRegistry, string);
            if (classLoader == null) {
                classLoader = helpSet.getLoader();
            }
            try {
                Class class_ = classLoader != null ? classLoader.loadClass(string2) : Class.forName(string2);
                editorKit = (EditorKit)class_.newInstance();
                this.kitRegistry.put(string, editorKit);
            }
            catch (Throwable var6_7) {
                var6_7.printStackTrace();
                editorKit = null;
            }
        }
        if (editorKit != null) {
            return (EditorKit)editorKit.clone();
        }
        editorKit = JEditorPane.createEditorKitForContentType(string);
        return editorKit;
    }

    public void addTextHelpModelListener(TextHelpModelListener textHelpModelListener) {
        this.getModel().addTextHelpModelListener(textHelpModelListener);
    }

    public void removeHelpModelListener(TextHelpModelListener textHelpModelListener) {
        this.getModel().removeTextHelpModelListener(textHelpModelListener);
    }

    public void addHelpModelListener(HelpModelListener helpModelListener) {
        this.getModel().addHelpModelListener(helpModelListener);
    }

    public void removeHelpModelListener(HelpModelListener helpModelListener) {
        this.getModel().removeHelpModelListener(helpModelListener);
    }

    public void clear() {
        this.firePropertyChange("clear", " ", "xyz");
    }

    public void reload() {
        this.firePropertyChange("reload", " ", "xyz");
    }

    private void debug(String string) {
        if (this.debug) {
            System.err.println("JHelpContentViewer: " + string);
        }
    }

    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJHelpContentViewer();
        }
        return this.accessibleContext;
    }

    static {
        SwingHelpUtilities.installLookAndFeelDefaults();
    }

    protected class AccessibleJHelpContentViewer
    extends JComponent.AccessibleJComponent {
        protected AccessibleJHelpContentViewer() {
            super(JHelpContentViewer.this);
        }

        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
    }

}

