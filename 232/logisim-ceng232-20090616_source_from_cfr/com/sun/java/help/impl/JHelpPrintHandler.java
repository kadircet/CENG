/*
 * Decompiled with CFR 0_114.
 */
package com.sun.java.help.impl;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.net.URL;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.JHelp;
import javax.help.TextHelpModel;
import javax.help.TreeItem;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.text.BoxView;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.View;

public class JHelpPrintHandler
implements ActionListener {
    public static final String PRINT_BUTTON_NAME = "PrintButton";
    public static final String PAGE_SETUP_BUTTON_NAME = "PageSetupButton";
    public static final String PRINT_LATER_BUTTON_NAME = "PrintLaterButton";
    private boolean enabled = true;
    private PrinterJob printerJob;
    private PageFormat pageFormat;
    private JHelp help;
    private HelpModel helpModel;
    private URL documentURL;
    private String documentTitle;
    private static WeakHashMap handlers;
    protected SwingPropertyChangeSupport changeSupport;
    private static final boolean debug = false;

    public JHelpPrintHandler(JHelp jHelp) {
        this.help = jHelp;
    }

    public static JHelpPrintHandler getJHelpPrintHandler(JHelp jHelp) {
        JHelpPrintHandler jHelpPrintHandler = null;
        if (handlers == null) {
            handlers = new WeakHashMap();
        } else {
            jHelpPrintHandler = (JHelpPrintHandler)handlers.get(jHelp);
        }
        if (jHelpPrintHandler == null) {
            jHelpPrintHandler = new JHelpPrintHandler(jHelp);
            handlers.put(jHelp, jHelpPrintHandler);
        }
        return jHelpPrintHandler;
    }

    protected void firePropertyChange(String string, Object object, Object object2) {
        if (this.changeSupport == null) {
            return;
        }
        this.changeSupport.firePropertyChange(string, object, object2);
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (this.changeSupport == null) {
            this.changeSupport = new SwingPropertyChangeSupport(this);
        }
        this.changeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (this.changeSupport == null) {
            return;
        }
        this.changeSupport.removePropertyChangeListener(propertyChangeListener);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean bl) {
        boolean bl2 = this.enabled;
        this.enabled = bl;
        this.firePropertyChange("enabled", new Boolean(bl2), new Boolean(bl));
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String string = null;
        Object object = actionEvent.getSource();
        if (object instanceof MenuComponent) {
            string = ((MenuComponent)object).getName();
        }
        if (object instanceof Component) {
            string = ((Component)object).getName();
        }
        if (this.isEnabled()) {
            if (string != null && string.equals("PageSetupButton")) {
                this.printSetup();
            } else if (this.getHelp() != null) {
                this.print(this.getURLs());
            } else if (this.getHelpModel() != null) {
                this.print(this.getHelpModel().getCurrentURL());
            }
        }
    }

    public void printSetup() {
        if (this.isEnabled()) {
            new PageSetupThread().start();
        }
    }

    public void print(URL uRL) {
        if (this.isEnabled()) {
            new PrintThread(uRL).start();
        }
    }

    public void print(URL[] arruRL) {
        if (this.isEnabled()) {
            new PrintThread(arruRL).start();
        }
    }

    private JHelp getHelp() {
        return this.help;
    }

    private URL[] getURLs() {
        TreeItem[] arrtreeItem;
        URL[] arruRL = null;
        if (this.getHelp() != null && (arrtreeItem = this.getHelp().getSelectedItems()) != null) {
            JHelpPrintHandler.debug("pages to print: " + arrtreeItem.length);
            arruRL = new URL[arrtreeItem.length];
            int n = 0;
            while (n < arrtreeItem.length) {
                JHelpPrintHandler.debug("   " + arrtreeItem[n].getName() + ": " + arrtreeItem[n].getURL());
                arruRL[n] = arrtreeItem[n].getURL();
                ++n;
            }
        }
        return arruRL;
    }

    public PageFormat getPageFormat() {
        if (this.pageFormat == null && this.getPrinterJob() != null) {
            this.pageFormat = this.getPrinterJob().defaultPage();
        }
        return this.pageFormat;
    }

    public PageFormat getPF() {
        return this.getPageFormat();
    }

    public void setPageFormat(PageFormat pageFormat) {
        if (this.pageFormat == pageFormat) {
            return;
        }
        PageFormat pageFormat2 = this.pageFormat;
        this.pageFormat = pageFormat;
        this.firePropertyChange("pageFormat", pageFormat2, pageFormat);
    }

    public void setPF(PageFormat pageFormat) {
        this.setPageFormat(pageFormat);
    }

    public PrinterJob getPrinterJob() {
        if (this.printerJob == null) {
            try {
                this.printerJob = PrinterJob.getPrinterJob();
            }
            catch (SecurityException var1_1) {
                this.setEnabled(false);
                JHelpPrintHandler.processException(var1_1);
            }
        }
        return this.printerJob;
    }

    public void handlePageSetup(Component component) {
        component.setName("PageSetupButton");
    }

    public void setHelpModel(HelpModel helpModel) {
        HelpModel helpModel2 = this.helpModel;
        this.helpModel = helpModel;
        this.firePropertyChange("helpModel", helpModel2, helpModel);
    }

    public HelpModel getHelpModel() {
        return this.helpModel;
    }

    static Window getWindowForObject(Object object) {
        if (object == null || object instanceof Frame || object instanceof Dialog) {
            return (Window)object;
        }
        MenuContainer menuContainer = null;
        if (object instanceof MenuComponent) {
            menuContainer = ((MenuComponent)object).getParent();
        } else if (object instanceof Component) {
            menuContainer = ((Component)object).getParent();
        }
        return JHelpPrintHandler.getWindowForObject(menuContainer);
    }

    static Insets getInsetsForContainer(Container container) {
        Insets insets = container.getInsets();
        container = container.getParent();
        while (container != null) {
            Insets insets2 = container.getInsets();
            insets.bottom += insets2.bottom;
            insets.left += insets2.left;
            insets.right += insets2.right;
            insets.top += insets2.top;
            if (container instanceof Window) break;
            container = container.getParent();
        }
        return insets;
    }

    EditorKit createEditorKitForContentType(String string) {
        HelpModel helpModel = null;
        JHelp jHelp = this.getHelp();
        helpModel = jHelp != null ? jHelp.getModel() : this.getHelpModel();
        if (helpModel == null) {
            return null;
        }
        HelpSet helpSet = helpModel.getHelpSet();
        if (helpSet == null) {
            return null;
        }
        String string2 = (String)helpSet.getKeyData(HelpSet.kitTypeRegistry, string);
        if (string2 == null) {
            return null;
        }
        ClassLoader classLoader = (ClassLoader)helpSet.getKeyData(HelpSet.kitLoaderRegistry, string);
        if (classLoader == null) {
            classLoader = helpSet.getLoader();
        }
        EditorKit editorKit = null;
        try {
            Class class_ = classLoader != null ? classLoader.loadClass(string2) : Class.forName(string2);
            editorKit = (EditorKit)class_.newInstance();
        }
        catch (Throwable var8_9) {
            System.err.println(var8_9);
            editorKit = null;
        }
        if (editorKit != null) {
            return (EditorKit)editorKit.clone();
        }
        return editorKit;
    }

    protected static Rectangle getViewRec(View view, float f, float f2) {
        Rectangle rectangle = new Rectangle();
        view.setSize(f, f2);
        rectangle.width = (int)Math.max((long)Math.ceil(view.getMinimumSpan(0)), (long)f);
        rectangle.height = (int)Math.min((long)Math.ceil(view.getPreferredSpan(1)), Integer.MAX_VALUE);
        view.setSize(rectangle.width, rectangle.height);
        if (view.getView(0) instanceof BoxView) {
            BoxView boxView = (BoxView)view.getView(0);
            rectangle.width = boxView.getWidth();
            rectangle.height = boxView.getHeight();
        } else {
            rectangle.height = (int)Math.min((long)Math.ceil(view.getPreferredSpan(1)), Integer.MAX_VALUE);
        }
        return rectangle;
    }

    protected static void processException(Exception exception) {
        System.err.println(exception);
    }

    private static void debug(String string) {
    }

    class JHEditorPane
    extends JEditorPane {
        public JHEditorPane() {
            this.setDoubleBuffered(false);
            this.setEditable(false);
            this.setDropTarget(null);
        }

        public EditorKit getEditorKitForContentType(String string) {
            EditorKit editorKit = JHelpPrintHandler.this.createEditorKitForContentType(string);
            if (editorKit == null) {
                editorKit = super.getEditorKitForContentType(string);
            }
            return editorKit;
        }

        public void addMouseListener(MouseListener mouseListener) {
        }

        public void removeMouseListener(MouseListener mouseListener) {
        }

        public void addMouseMotionListener(MouseMotionListener mouseMotionListener) {
        }

        public void removeMouseMotionListener(MouseMotionListener mouseMotionListener) {
        }

        public void addFocusListener(FocusListener focusListener) {
        }

        public void removeFocusListener(FocusListener focusListener) {
        }

        public void addKeyListener(KeyListener keyListener) {
        }

        public void removeKeyListener(KeyListener keyListener) {
        }
    }

    class JHFrame
    extends JFrame {
        JHFrame() {
        }

        public void addNotify() {
            this.getRootPane().addNotify();
        }

        public void validate() {
            this.validateTree();
        }

        public Graphics getGraphics() {
            return JHelpPrintHandler.this.getHelp().getGraphics();
        }
    }

    public class JHPageable
    implements Pageable {
        private JEditorPane editor;
        private URL[] urls;
        private PageFormat pf;
        private int lastIndex;
        private int lastPage;
        private Printable printable;
        private int numPages;

        public JHPageable(JEditorPane jEditorPane, URL[] arruRL, PageFormat pageFormat) {
            this.lastIndex = -1;
            this.lastPage = 0;
            this.printable = new EmptyPrintable();
            this.numPages = 0;
            this.editor = jEditorPane;
            this.urls = arruRL;
            this.pf = pageFormat;
        }

        public PageFormat getPageFormat(int n) throws IndexOutOfBoundsException {
            return this.pf;
        }

        public int getNumberOfPages() {
            if (this.numPages != 0) {
                return this.numPages;
            }
            if (this.urls != null) {
                int n = 0;
                while (n < this.urls.length) {
                    JHPrintable jHPrintable = new JHPrintable(this.editor, this.urls[n], this.pf, 0, true);
                    this.numPages += jHPrintable.getNumberOfPages();
                    ++n;
                }
            }
            return this.numPages;
        }

        public Printable getPrintable(int n) throws IndexOutOfBoundsException {
            JHelpPrintHandler.debug("JHPageable.getPrintable(" + n + "): lastIndex=" + this.lastIndex + ", lastPage" + this.lastPage);
            if (n < 0) {
                throw new IndexOutOfBoundsException("" + n + " < 0");
            }
            if (this.urls != null) {
                while (n >= this.lastPage && this.lastIndex + 1 < this.urls.length) {
                    if (this.urls[++this.lastIndex] != null) {
                        this.printable = new JHPrintable(this.editor, this.urls[this.lastIndex], this.pf, this.lastPage, true);
                        this.lastPage += ((JHPrintable)this.printable).getNumberOfPages();
                        continue;
                    }
                    this.printable = new EmptyPrintable();
                }
            }
            if (n > this.lastPage) {
                throw new IndexOutOfBoundsException("" + n + " > " + this.lastPage);
            }
            return this.printable;
        }
    }

    class EmptyPrintable
    implements Printable {
        EmptyPrintable() {
        }

        public int print(Graphics graphics, PageFormat pageFormat, int n) throws PrinterException {
            return 1;
        }
    }

    class JHPrintable
    implements Printable,
    PropertyChangeListener {
        JEditorPane editor;
        URL url;
        PageFormat pf;
        int firstPage;
        boolean scaleToFit;
        Vector transforms;

        public JHPrintable(JEditorPane jEditorPane, URL uRL, PageFormat pageFormat, int n, boolean bl) {
            this.editor = jEditorPane;
            this.url = uRL;
            this.pf = pageFormat;
            this.firstPage = n;
            this.scaleToFit = bl;
        }

        private synchronized void loadPage() {
            JHelpPrintHandler.debug("JHPrintable.loadPage(): " + this.url);
            URL uRL = this.editor.getPage();
            if (uRL != null && uRL.equals(this.url)) {
                return;
            }
            this.editor.addPropertyChangeListener("page", this);
            try {
                this.editor.setPage(this.url);
                this.wait();
            }
            catch (Exception var2_2) {
                JHelpPrintHandler.processException(var2_2);
            }
            this.editor.removePropertyChangeListener("page", this);
        }

        public int getNumberOfPages() {
            if (this.transforms == null) {
                this.loadPage();
                this.transforms = this.createTransforms();
            }
            return this.transforms.size();
        }

        public synchronized void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            this.notifyAll();
        }

        /*
         * Exception decompiling
         */
        public Vector createTransforms() {
            // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
            // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[DOLOOP]], but top level block is 0[TRYBLOCK]
            // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:394)
            // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:446)
            // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:2859)
            // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:805)
            // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:220)
            // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:165)
            // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:91)
            // org.benf.cfr.reader.entities.Method.analyse(Method.java:354)
            // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:751)
            // org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:664)
            // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:747)
            // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:683)
            // org.benf.cfr.reader.Main.doJar(Main.java:129)
            // org.benf.cfr.reader.Main.main(Main.java:181)
            throw new IllegalStateException("Decompilation failed");
        }

        private void printHeader(Graphics2D graphics2D, int n) {
            graphics2D.setClip(new Rectangle2D.Double(0.0, 0.0, this.pf.getWidth(), this.pf.getHeight()));
            graphics2D.setFont(new Font("Serif", 2, 10));
            String string = (String)this.editor.getDocument().getProperty("title") + " " + (n + 1) + "/" + this.transforms.size();
            Rectangle2D rectangle2D = graphics2D.getFontMetrics().getStringBounds(string, graphics2D);
            graphics2D.drawString(string, (float)(this.pf.getWidth() - rectangle2D.getX() - rectangle2D.getWidth() - 36.0), 36.0f);
            graphics2D.setClip(new Rectangle2D.Double(this.pf.getImageableX(), this.pf.getImageableY(), this.pf.getImageableWidth(), this.pf.getImageableHeight()));
        }

        public int print(Graphics graphics, PageFormat pageFormat, int n) {
            JHelpPrintHandler.debug("Printing document page=" + n);
            JHelpPrintHandler.debug("Printing page=" + (n -= this.firstPage));
            if (n >= this.getNumberOfPages()) {
                return 1;
            }
            Graphics2D graphics2D = (Graphics2D)graphics;
            this.printHeader(graphics2D, n);
            graphics2D.transform((AffineTransform)this.transforms.get(n));
            JHelpPrintHandler.debug("Graphics tansform=" + graphics2D.getTransform());
            JHelpPrintHandler.debug("Graphics clip=" + graphics2D.getClip());
            Rectangle2D rectangle2D = graphics2D.getClip().getBounds2D();
            double d = ((PageTransform)this.transforms.get(n)).getHeight();
            double d2 = rectangle2D.getY() + rectangle2D.getHeight() - 1.0 - d;
            if (d2 > 0.0) {
                JHelpPrintHandler.debug("Graphics adjusted height=" + d2);
                graphics2D.clip(new Rectangle2D.Double(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight() - d2));
                rectangle2D = graphics2D.getClip().getBounds2D();
                JHelpPrintHandler.debug("Graphics tansform=" + graphics2D.getTransform());
                JHelpPrintHandler.debug("Graphics clip=" + graphics2D.getClip());
            }
            if (rectangle2D.getY() >= d) {
                return 1;
            }
            this.editor.paint(graphics2D);
            return 0;
        }
    }

    class PageTransform
    extends AffineTransform {
        private double height;

        PageTransform() {
        }

        public double getHeight() {
            return this.height;
        }

        public void setHeight(double d) {
            this.height = d;
        }
    }

    class PrintThread
    extends Thread {
        private URL[] urls;
        private int index;

        PrintThread(URL uRL) {
            this(new URL[]{uRL});
        }

        PrintThread(URL[] arruRL) {
            this.index = 0;
            this.urls = arruRL;
            this.setDaemon(true);
        }

        public void run() {
            PrinterJob printerJob = JHelpPrintHandler.this.getPrinterJob();
            if (printerJob != null) {
                PrinterJob printerJob2 = printerJob;
                synchronized (printerJob2) {
                    JHEditorPane jHEditorPane = new JHEditorPane();
                    JHFrame jHFrame = new JHFrame();
                    jHFrame.setContentPane(jHEditorPane);
                    try {
                        printerJob.setPageable(new JHPageable(jHEditorPane, this.urls, (PageFormat)JHelpPrintHandler.this.getPageFormat().clone()));
                    }
                    catch (Exception var5_5) {
                        JHelpPrintHandler.processException(var5_5);
                    }
                    if (printerJob.printDialog()) {
                        try {
                            printerJob.print();
                        }
                        catch (Exception var5_6) {
                            JHelpPrintHandler.processException(var5_6);
                        }
                    }
                    jHFrame.dispose();
                }
            }
        }
    }

    class PageSetupThread
    extends Thread {
        PrinterJob job;

        PageSetupThread() {
            this.setDaemon(true);
        }

        public void run() {
            PrinterJob printerJob = JHelpPrintHandler.this.getPrinterJob();
            if (printerJob != null) {
                PrinterJob printerJob2 = printerJob;
                synchronized (printerJob2) {
                    try {
                        JHelpPrintHandler.this.setPageFormat(printerJob.pageDialog(JHelpPrintHandler.this.getPageFormat()));
                    }
                    catch (Exception var3_3) {
                        JHelpPrintHandler.processException(var3_3);
                    }
                }
            }
        }
    }

}

