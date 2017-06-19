/*
 * Decompiled with CFR 0_114.
 */
package com.sun.java.help.impl;

import com.sun.java.help.impl.ViewAwareComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Vector;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.text.AttributeSet;
import javax.swing.text.ComponentView;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class CustomKit
extends HTMLEditorKit {
    private MouseMotionListener mouseHandler = new MouseHandler();
    private static final boolean debug = false;

    public Object clone() {
        return new CustomKit();
    }

    public void install(JEditorPane jEditorPane) {
        jEditorPane.addMouseMotionListener(this.mouseHandler);
        super.install(jEditorPane);
    }

    public void deinstall(JEditorPane jEditorPane) {
        jEditorPane.removeMouseMotionListener(this.mouseHandler);
        super.deinstall(jEditorPane);
    }

    public Document createDefaultDocument() {
        HTMLDocument hTMLDocument = (HTMLDocument)super.createDefaultDocument();
        CustomDocument customDocument = new CustomDocument(hTMLDocument.getStyleSheet());
        customDocument.putProperty("__PARSER__", this.getParser());
        customDocument.setAsynchronousLoadPriority(4);
        customDocument.setTokenThreshold(100);
        CustomKit.debug("fetch custom document");
        return customDocument;
    }

    public ViewFactory getViewFactory() {
        CustomKit.debug("fetched custom factory");
        return new CustomFactory();
    }

    private static void debug(String string) {
    }

    static class ObjectView1
    extends ComponentView {
        public ObjectView1(Element element) {
            super(element);
        }

        protected Component createComponent() {
            AttributeSet attributeSet = this.getElement().getAttributes();
            CustomKit.debug("attr: " + attributeSet.copyAttributes());
            String string = (String)attributeSet.getAttribute(HTML.Attribute.CLASSID);
            try {
                Object t;
                Class class_;
                String string2;
                int n = string.indexOf(58);
                if (n != -1 && (string2 = string.substring(0, n).toLowerCase()).compareTo("java") == 0 && (t = (class_ = this.getClass(string = string.substring(n + 1))).newInstance()) instanceof Component) {
                    Component component = (Component)t;
                    if (t instanceof ViewAwareComponent) {
                        ((ViewAwareComponent)((Object)component)).setViewData(this);
                    }
                    this.setParameters(component, attributeSet);
                    return component;
                }
            }
            catch (Throwable var3_4) {
                // empty catch block
            }
            return this.getUnloadableRepresentation();
        }

        Component getUnloadableRepresentation() {
            JLabel jLabel = new JLabel("??");
            jLabel.setForeground(Color.red);
            return jLabel;
        }

        private Class getClass(String string) throws ClassNotFoundException {
            Class class_ = null;
            Class class_2 = this.getDocument().getClass();
            ClassLoader classLoader = class_2.getClassLoader();
            if (classLoader != null) {
                class_ = classLoader.loadClass(string);
            }
            if (class_ == null) {
                class_ = Class.forName(string);
            }
            return class_;
        }

        private void setParameters(Component component, AttributeSet attributeSet) {
            BeanInfo beanInfo;
            Class class_ = component.getClass();
            try {
                beanInfo = Introspector.getBeanInfo(class_);
            }
            catch (IntrospectionException var5_5) {
                CustomKit.debug("introspector failed, ex: " + var5_5);
                return;
            }
            PropertyDescriptor[] arrpropertyDescriptor = beanInfo.getPropertyDescriptors();
            int n = 0;
            while (n < arrpropertyDescriptor.length) {
                CustomKit.debug("checking on props[i]: " + arrpropertyDescriptor[n].getName());
                Object object = attributeSet.getAttribute(arrpropertyDescriptor[n].getName());
                if (object instanceof String) {
                    String string = (String)object;
                    Method method = arrpropertyDescriptor[n].getWriteMethod();
                    if (method == null) {
                        return;
                    }
                    Class<?>[] arrclass = method.getParameterTypes();
                    if (arrclass.length != 1) {
                        return;
                    }
                    Object[] arrobject = new String[]{string};
                    try {
                        method.invoke(component, arrobject);
                        CustomKit.debug("Invocation succeeded");
                    }
                    catch (Exception var12_13) {
                        CustomKit.debug("Invocation failed");
                    }
                }
                ++n;
            }
        }
    }

    static class CustomDocument
    extends HTMLDocument {
        CustomDocument(StyleSheet styleSheet) {
            super(styleSheet);
        }

        public HTMLEditorKit.ParserCallback getReader(int n) {
            Object object = this.getProperty("stream");
            if (object instanceof URL) {
                this.setBase((URL)object);
            }
            CustomReader customReader = new CustomReader(n);
            return customReader;
        }

        class CustomReader
        extends HTMLDocument.HTMLReader {
            public CustomReader(int n) {
                super(CustomDocument.this, n);
                this.registerTag(HTML.Tag.PARAM, new ObjectAction1());
            }

            Vector getParseBuffer() {
                return this.parseBuffer;
            }

            class ObjectAction1
            extends HTMLDocument.HTMLReader.SpecialAction {
                ObjectAction1() {
                    super(CustomReader.this);
                }

                public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                    if (tag == HTML.Tag.PARAM) {
                        this.addParameter(mutableAttributeSet);
                    } else {
                        super.start(tag, mutableAttributeSet);
                    }
                }

                public void end(HTML.Tag tag) {
                    if (tag != HTML.Tag.PARAM) {
                        super.end(tag);
                    }
                }

                void addParameter(AttributeSet attributeSet) {
                    CustomKit.debug("addParameter AttributeSet=" + attributeSet);
                    String string = (String)attributeSet.getAttribute(HTML.Attribute.NAME);
                    String string2 = (String)attributeSet.getAttribute(HTML.Attribute.VALUE);
                    if (string != null && string2 != null) {
                        DefaultStyledDocument.ElementSpec elementSpec = (DefaultStyledDocument.ElementSpec)CustomReader.this.getParseBuffer().lastElement();
                        MutableAttributeSet mutableAttributeSet = (MutableAttributeSet)elementSpec.getAttributes();
                        mutableAttributeSet.addAttribute(string, string2);
                    }
                }
            }

        }

    }

    static class CustomFactory
    extends HTMLEditorKit.HTMLFactory {
        CustomFactory() {
        }

        public View create(Element element) {
            HTML.Tag tag;
            Object object = element.getAttributes().getAttribute(StyleConstants.NameAttribute);
            if (object instanceof HTML.Tag && (tag = (HTML.Tag)object) == HTML.Tag.OBJECT) {
                CustomKit.debug("creating ObjectView1 for: " + element);
                return new ObjectView1(element);
            }
            return super.create(element);
        }
    }

    public static class MouseHandler
    implements MouseMotionListener {
        private Element curElem = null;
        private Cursor origCursor;
        private Cursor handCursor = null;

        public void mouseDragged(MouseEvent mouseEvent) {
        }

        public void mouseMoved(MouseEvent mouseEvent) {
            Point point;
            int n;
            Document document;
            JEditorPane jEditorPane = (JEditorPane)mouseEvent.getSource();
            if (!jEditorPane.isEditable() && (n = jEditorPane.viewToModel(point = new Point(mouseEvent.getX(), mouseEvent.getY()))) >= 0 && (document = jEditorPane.getDocument()) instanceof HTMLDocument) {
                String string;
                HTMLDocument hTMLDocument = (HTMLDocument)document;
                Element element = hTMLDocument.getCharacterElement(n);
                AttributeSet attributeSet = element.getAttributes();
                AttributeSet attributeSet2 = (AttributeSet)attributeSet.getAttribute(HTML.Tag.A);
                String string2 = string = attributeSet2 != null ? (String)attributeSet2.getAttribute(HTML.Attribute.HREF) : null;
                if (string != null) {
                    if (this.curElem != element) {
                        this.curElem = element;
                        if (this.origCursor == null) {
                            this.origCursor = jEditorPane.getCursor();
                        }
                        if (this.handCursor == null) {
                            this.handCursor = Cursor.getPredefinedCursor(12);
                        }
                        jEditorPane.setCursor(this.handCursor);
                    }
                } else if (this.curElem != null) {
                    this.curElem = null;
                    jEditorPane.setCursor(this.origCursor);
                }
            }
        }
    }

}

