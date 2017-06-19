/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.comp.TextField;
import com.cburch.logisim.comp.TextFieldCaret;
import com.cburch.logisim.comp.TextFieldEvent;
import com.cburch.logisim.comp.TextFieldListener;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeOption;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.std.TextClass;
import com.cburch.logisim.tools.Caret;
import com.cburch.logisim.tools.TextEditable;
import java.awt.Font;
import java.awt.Graphics;

public class Text
extends ManagedComponent
implements TextEditable {
    private TextField field;

    public Text(Location loc, AttributeSet attrs) {
        super(loc, attrs, 0);
        AttributeOption ha = (AttributeOption)attrs.getValue(TextClass.halign_attr);
        AttributeOption va = (AttributeOption)attrs.getValue(TextClass.valign_attr);
        int h = (Integer)ha.getValue();
        int v = (Integer)va.getValue();
        this.field = new TextField(loc.getX(), loc.getY(), h, v, (Font)attrs.getValue(TextClass.font_attr));
        this.field.setText((String)attrs.getValue(TextClass.text_attr));
        MyListener l = new MyListener();
        attrs.addAttributeListener(l);
        this.field.addTextFieldListener(l);
    }

    @Override
    public ComponentFactory getFactory() {
        return TextClass.instance;
    }

    @Override
    public void propagate(CircuitState state) {
    }

    @Override
    public Bounds getBounds(Graphics g) {
        return this.field.getBounds(g);
    }

    @Override
    public void draw(ComponentDrawContext context) {
        this.field.draw(context.getGraphics());
    }

    @Override
    public Object getFeature(Object key) {
        if (key == TextEditable.class) {
            return this;
        }
        return super.getFeature(key);
    }

    @Override
    public Caret getTextCaret(ComponentUserEvent event) {
        int x;
        int y;
        Graphics g = event.getCanvas().getGraphics();
        Bounds bds = this.field.getBounds(g);
        if (bds.getWidth() < 4 || bds.getHeight() < 4) {
            Location loc = this.getLocation();
            bds = bds.add(Bounds.create(loc).expand(2));
        }
        if (bds.contains(x = event.getX(), y = event.getY())) {
            return this.field.getCaret(g, x, y);
        }
        return null;
    }

    private class MyListener
    implements AttributeListener,
    TextFieldListener {
        private MyListener() {
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            Attribute attr = e.getAttribute();
            if (attr == TextClass.text_attr) {
                String text = (String)e.getValue();
                if (!text.equals(Text.this.field.getText())) {
                    Text.this.field.setText((String)e.getValue());
                }
            } else if (attr == TextClass.font_attr) {
                Text.this.field.setFont((Font)e.getValue());
            } else if (attr == TextClass.halign_attr) {
                AttributeOption ha = (AttributeOption)e.getValue();
                int h = (Integer)ha.getValue();
                Text.this.field.setHorzAlign(h);
            } else if (attr == TextClass.valign_attr) {
                AttributeOption va = (AttributeOption)e.getValue();
                int v = (Integer)va.getValue();
                Text.this.field.setVertAlign(v);
            }
        }

        @Override
        public void textChanged(TextFieldEvent e) {
            String next;
            AttributeSet attrs = Text.this.getAttributeSet();
            String prev = (String)attrs.getValue(TextClass.text_attr);
            if (!prev.equals(next = e.getText())) {
                attrs.setValue(TextClass.text_attr, next);
            }
        }
    }

}

