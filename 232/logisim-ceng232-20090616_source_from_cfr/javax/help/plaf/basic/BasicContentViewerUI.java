/*
 * Decompiled with CFR 0_114.
 */
package javax.help.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Locale;
import javax.accessibility.AccessibleContext;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.InvalidHelpSetContextException;
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
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Highlighter;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.parser.ParserDelegator;

public class BasicContentViewerUI
extends HelpContentViewerUI
implements HelpModelListener,
TextHelpModelListener,
HyperlinkListener,
PropertyChangeListener,
Serializable {
    protected JHelpContentViewer theViewer;
    private static Dimension PREF_SIZE = new Dimension(200, 300);
    private static Dimension MIN_SIZE = new Dimension(80, 80);
    private JEditorPane html;
    private JViewport vp;
    private Hashtable registry;
    private boolean loadingURL;
    private TextHelpModelEvent pendingHighlightsEvent;
    private static final boolean debug = false;
    private static final boolean debug1 = false;

    public static ComponentUI createUI(JComponent jComponent) {
        BasicContentViewerUI.debug("createUI");
        return new BasicContentViewerUI((JHelpContentViewer)jComponent);
    }

    public BasicContentViewerUI(JHelpContentViewer jHelpContentViewer) {
        BasicContentViewerUI.debug("createUI - sort of");
    }

    public void setEditorKit(String string, EditorKit editorKit) {
        BasicContentViewerUI.debug("setEditorKit(" + string + ", " + editorKit + ")");
        if (this.registry == null) {
            this.registry = new Hashtable(3);
        }
        this.registry.put(string, editorKit);
        if (this.html != null) {
            BasicContentViewerUI.debug("  type: " + string);
            BasicContentViewerUI.debug("  kit: " + editorKit);
            this.html.setEditorKitForContentType(string, editorKit);
        }
    }

    public void installUI(JComponent jComponent) {
        Serializable serializable;
        BasicContentViewerUI.debug("installUI");
        this.theViewer = (JHelpContentViewer)jComponent;
        this.theViewer.setLayout(new BorderLayout());
        this.theViewer.addPropertyChangeListener(this);
        TextHelpModel textHelpModel = this.theViewer.getModel();
        if (textHelpModel != null) {
            textHelpModel.addHelpModelListener(this);
            textHelpModel.addTextHelpModelListener(this);
        }
        this.html = new JHEditorPane();
        this.html.addPropertyChangeListener(this);
        this.html.getAccessibleContext().setAccessibleName(HelpUtilities.getString(HelpUtilities.getLocale(this.html), "access.contentViewer"));
        this.html.setEditable(false);
        this.html.addHyperlinkListener(this);
        if (textHelpModel != null && (serializable = textHelpModel.getCurrentURL()) != null) {
            try {
                this.html.setPage((URL)serializable);
            }
            catch (IOException var4_4) {
                // empty catch block
            }
        }
        serializable = new JScrollPane();
        serializable.setBorder(new BevelBorder(1, Color.white, Color.gray));
        this.vp = serializable.getViewport();
        this.vp.add(this.html);
        this.vp.setBackingStoreEnabled(true);
        this.theViewer.add("Center", (Component)serializable);
        this.loadingURL = false;
        this.pendingHighlightsEvent = null;
    }

    public void uninstallUI(JComponent jComponent) {
        BasicContentViewerUI.debug("uninstallUI");
        JHelpContentViewer jHelpContentViewer = (JHelpContentViewer)jComponent;
        jHelpContentViewer.removePropertyChangeListener(this);
        this.html.removePropertyChangeListener(this);
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
        BasicContentViewerUI.debug("idChanged(" + helpModelEvent + ")");
        BasicContentViewerUI.debug("  = " + iD + " " + uRL);
        BasicContentViewerUI.debug("  my helpModel: " + textHelpModel);
        textHelpModel.setDocumentTitle(null);
        try {
            Highlighter highlighter = this.html.getHighlighter();
            BasicContentViewerUI.debug("removeAllHighlights");
            highlighter.removeAllHighlights();
            try {
                this.loadingURL = true;
                this.html.setPage(uRL);
            }
            catch (Exception var6_7) {
                this.loadingURL = false;
            }
            BasicContentViewerUI.debug("html current EditorKit is: " + this.html.getEditorKit());
            BasicContentViewerUI.debug("html current ContentType is: " + this.html.getContentType());
        }
        catch (Exception var5_6) {
            BasicContentViewerUI.debug("Exception geneartated");
        }
        BasicContentViewerUI.debug("done with idChanged");
    }

    private void rebuild() {
        BasicContentViewerUI.debug("rebuild");
        TextHelpModel textHelpModel = this.theViewer.getModel();
        if (textHelpModel == null) {
            BasicContentViewerUI.debug("rebuild-end: model is null");
            return;
        }
        Highlighter highlighter = this.html.getHighlighter();
        BasicContentViewerUI.debug("removeAllHighlights");
        highlighter.removeAllHighlights();
        HelpSet helpSet = textHelpModel.getHelpSet();
        if (this.theViewer.getSynch()) {
            try {
                Map.ID iD = helpSet.getHomeID();
                Locale locale = helpSet.getLocale();
                String string = HelpUtilities.getString(locale, "history.homePage");
                textHelpModel.setCurrentID(iD, string, null);
                this.html.setPage(textHelpModel.getCurrentURL());
            }
            catch (Exception var4_5) {
                // empty catch block
            }
        }
        BasicContentViewerUI.debug("rebuild-end");
    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String string;
        BasicContentViewerUI.debug("propertyChange: " + propertyChangeEvent.getPropertyName() + "\n\toldValue:" + propertyChangeEvent.getOldValue() + "\n\tnewValue:" + propertyChangeEvent.getNewValue());
        if (propertyChangeEvent.getSource() == this.theViewer) {
            URL uRL;
            String string2 = propertyChangeEvent.getPropertyName();
            if (string2.equals("helpModel")) {
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
            } else if (string2.equals("font")) {
                BasicContentViewerUI.debug("font changed");
                Font font = (Font)propertyChangeEvent.getNewValue();
                EditorKit editorKit = this.html.getEditorKit();
                if (editorKit instanceof HTMLEditorKit) {
                    StringBuffer stringBuffer = new StringBuffer(60);
                    stringBuffer.append("body { font: ");
                    stringBuffer.append(font.getSize()).append("pt ");
                    if (font.isBold()) {
                        stringBuffer.append("bold ");
                    }
                    if (font.isItalic()) {
                        stringBuffer.append("italic ");
                    }
                    stringBuffer.append('\"').append(font.getFamily()).append('\"');
                    stringBuffer.append(" }");
                    String string3 = stringBuffer.toString();
                    StyleSheet styleSheet = ((HTMLEditorKit)editorKit).getStyleSheet();
                    styleSheet.addRule(string3);
                    styleSheet = ((HTMLDocument)this.html.getDocument()).getStyleSheet();
                    styleSheet.addRule(string3);
                }
            } else if (string2.equals("clear")) {
                this.html.setText("");
            } else if (string2.equals("reload") && (uRL = this.html.getPage()) != null) {
                try {
                    this.html.setPage(uRL);
                }
                catch (IOException var4_10) {}
            }
        } else if (propertyChangeEvent.getSource() == this.html && (string = propertyChangeEvent.getPropertyName()).equals("page")) {
            BasicContentViewerUI.debug("page finished loading");
            this.loadingURL = false;
            if (this.pendingHighlightsEvent != null) {
                BasicContentViewerUI.debug("Loading the highlights now");
                this.highlightsChanged(this.pendingHighlightsEvent);
                this.pendingHighlightsEvent = null;
            }
            Document document = this.html.getDocument();
            String string4 = (String)document.getProperty("title");
            TextHelpModel textHelpModel = this.theViewer.getModel();
            textHelpModel.setDocumentTitle(string4);
            this.theViewer.firePropertyChange(propertyChangeEvent.getPropertyName(), false, true);
        }
    }

    public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
        if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            if (hyperlinkEvent instanceof HTMLFrameHyperlinkEvent) {
                ((HTMLDocument)this.html.getDocument()).processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent)hyperlinkEvent);
            } else {
                this.linkActivated(hyperlinkEvent.getURL());
            }
        }
    }

    protected void linkActivated(URL uRL) {
        String string;
        BasicContentViewerUI.debug("linkActivated - URL=" + uRL);
        Cursor cursor = this.html.getCursor();
        Cursor cursor2 = Cursor.getPredefinedCursor(3);
        this.html.setCursor(cursor2);
        String string2 = uRL.getRef();
        if (string2 != null && ((string = uRL.getFile()).endsWith("/") || string.endsWith("\\"))) {
            uRL = this.html.getPage();
            BasicContentViewerUI.debug("current u=" + uRL);
            string = uRL.getFile();
            BasicContentViewerUI.debug("file=" + string);
            try {
                uRL = new URL(uRL.getProtocol(), uRL.getHost(), uRL.getPort(), string + "#" + string2);
            }
            catch (MalformedURLException var6_6) {
                return;
            }
            BasicContentViewerUI.debug("new u=" + uRL);
        }
        SwingUtilities.invokeLater(new PageLoader(uRL, cursor));
    }

    public void highlightsChanged(TextHelpModelEvent textHelpModelEvent) {
        BasicContentViewerUI.debug("highlightsChanged " + textHelpModelEvent);
        if (this.loadingURL) {
            BasicContentViewerUI.debug("Humm. loadingURL wait a little");
            this.pendingHighlightsEvent = textHelpModelEvent;
            return;
        }
        Highlighter highlighter = this.html.getHighlighter();
        BasicContentViewerUI.debug1("removeAllHighlights");
        highlighter.removeAllHighlights();
        TextHelpModel textHelpModel = (TextHelpModel)textHelpModelEvent.getSource();
        TextHelpModel.Highlight[] arrhighlight = textHelpModel.getHighlights();
        DefaultHighlighter.DefaultHighlightPainter defaultHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(this.html.getSelectionColor());
        int n = 0;
        while (n < arrhighlight.length) {
            int n2 = arrhighlight[n].getStartOffset();
            int n3 = arrhighlight[n].getEndOffset();
            BasicContentViewerUI.debug("  highlight: " + n2 + ", " + n3);
            try {
                highlighter.addHighlight(n2, n3, defaultHighlightPainter);
                if (n == 0) {
                    ScrollToPosition scrollToPosition = new ScrollToPosition(this.html, n3);
                    SwingUtilities.invokeLater(scrollToPosition);
                }
            }
            catch (BadLocationException var9_10) {
                BasicContentViewerUI.debug("badLocationExcetpion thrown - " + var9_10);
            }
            ++n;
        }
        RepaintManager.currentManager(this.html).markCompletelyDirty(this.html);
    }

    private static void debug(String string) {
    }

    private static void debug1(String string) {
    }

    private class ScrollToPosition
    implements Runnable {
        private int pos;
        private JEditorPane html;

        public ScrollToPosition(JEditorPane jEditorPane, int n) {
            this.html = jEditorPane;
            this.pos = n;
        }

        public void run() {
            try {
                Rectangle rectangle = this.html.modelToView(this.pos);
                if (rectangle != null) {
                    this.html.scrollRectToVisible(rectangle);
                }
            }
            catch (BadLocationException var1_2) {
                // empty catch block
            }
        }
    }

    class PageLoader
    implements Runnable {
        String title;
        URL url;
        Cursor cursor;

        PageLoader(URL uRL, Cursor cursor) {
            this.title = null;
            this.url = uRL;
            this.cursor = cursor;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public void run() {
            if (this.url == null) {
                BasicContentViewerUI.this.html.setCursor(this.cursor);
                RepaintManager.currentManager(BasicContentViewerUI.this.html).markCompletelyDirty(BasicContentViewerUI.this.html);
                return;
            }
            Document document = BasicContentViewerUI.this.html.getDocument();
            try {
                block10 : {
                    try {
                        BasicContentViewerUI.this.html.setPage(this.url);
                        BasicContentViewerUI.this.loadingURL = true;
                        document = BasicContentViewerUI.this.html.getDocument();
                        this.title = (String)document.getProperty("title");
                        String string = this.url.getRef();
                        if (this.title == null) {
                            this.title = this.findTitle(this.url);
                        }
                        if (string != null) {
                            this.title = this.title + "-" + string;
                        }
                        TextHelpModel textHelpModel = BasicContentViewerUI.this.theViewer.getModel();
                        textHelpModel.setDocumentTitle(this.title);
                        Map.ID iD = textHelpModel.getHelpSet().getCombinedMap().getIDFromURL(this.url);
                        if (iD != null) {
                            try {
                                textHelpModel.setCurrentID(iD, this.title, null);
                            }
                            catch (InvalidHelpSetContextException var5_6) {
                                textHelpModel.setCurrentURL(this.url, this.title, null);
                            }
                            break block10;
                        }
                        textHelpModel.setCurrentURL(this.url, this.title, null);
                    }
                    catch (IOException iOException) {
                        BasicContentViewerUI.this.loadingURL = false;
                        BasicContentViewerUI.this.html.setDocument(document);
                        BasicContentViewerUI.this.html.getToolkit().beep();
                        Object var7_8 = null;
                        this.url = null;
                        SwingUtilities.invokeLater(this);
                        return;
                    }
                }
                Object var7_7 = null;
                this.url = null;
                SwingUtilities.invokeLater(this);
                return;
            }
            catch (Throwable var6_10) {
                Object var7_9 = null;
                this.url = null;
                SwingUtilities.invokeLater(this);
                throw var6_10;
            }
        }

        private String findTitle(URL uRL) {
            try {
                URLConnection uRLConnection = uRL.openConnection();
                InputStreamReader inputStreamReader = new InputStreamReader(uRLConnection.getInputStream());
                ParserDelegator parserDelegator = new ParserDelegator();
                Callback callback = new Callback();
                parserDelegator.parse(inputStreamReader, callback, true);
            }
            catch (Exception var3_3) {
                System.err.println(var3_3);
            }
            return this.title;
        }

        class Callback
        extends HTMLEditorKit.ParserCallback {
            boolean wasTitle;

            Callback() {
                this.wasTitle = false;
            }

            public void handleStartTag(HTML.Tag tag, MutableAttributeSet mutableAttributeSet, int n) {
                if (tag.equals(HTML.Tag.TITLE)) {
                    this.wasTitle = true;
                }
            }

            public void handleText(char[] arrc, int n) {
                if (this.wasTitle) {
                    PageLoader.this.title = new String(arrc);
                    this.wasTitle = false;
                }
            }
        }

    }

    class JHEditorPane
    extends JEditorPane {
        private Hashtable typeHandlers;

        JHEditorPane() {
        }

        public EditorKit getEditorKitForContentType(String string) {
            EditorKit editorKit;
            if (this.typeHandlers == null) {
                this.typeHandlers = new Hashtable(3);
            }
            if ((editorKit = (EditorKit)this.typeHandlers.get(string)) == null && (editorKit = BasicContentViewerUI.this.theViewer.createEditorKitForContentType(string)) != null) {
                this.setEditorKitForContentType(string, editorKit);
                this.typeHandlers.put(string, editorKit);
            }
            if (editorKit == null && (editorKit = super.getEditorKitForContentType(string)) != null) {
                this.typeHandlers.put(string, editorKit);
            }
            return editorKit;
        }
    }

}

