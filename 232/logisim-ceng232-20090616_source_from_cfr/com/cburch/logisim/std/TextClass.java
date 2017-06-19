/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std;

import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeOption;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.AttributeSets;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.std.Strings;
import com.cburch.logisim.std.Text;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import com.cburch.logisim.util.IntegerFactory;
import com.cburch.logisim.util.StringGetter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.Icon;

public class TextClass
extends AbstractComponentFactory {
    public static TextClass instance = new TextClass();
    public static Attribute text_attr = Attributes.forString("text", Strings.getter("textTextAttr"));
    public static Attribute font_attr = Attributes.forFont("font", Strings.getter("textFontAttr"));
    public static Attribute halign_attr = Attributes.forOption("halign", Strings.getter("textHorzAlignAttr"), new AttributeOption[]{new AttributeOption(IntegerFactory.create(-1), "left", Strings.getter("textHorzAlignLeftOpt")), new AttributeOption(IntegerFactory.create(1), "right", Strings.getter("textHorzAlignRightOpt")), new AttributeOption(IntegerFactory.create(0), "center", Strings.getter("textHorzAlignCenterOpt"))});
    public static Attribute valign_attr = Attributes.forOption("valign", Strings.getter("textVertAlignAttr"), new AttributeOption[]{new AttributeOption(IntegerFactory.create(-1), "top", Strings.getter("textVertAlignTopOpt")), new AttributeOption(IntegerFactory.create(1), "base", Strings.getter("textVertAlignBaseOpt")), new AttributeOption(IntegerFactory.create(2), "bottom", Strings.getter("textVertAlignBottomOpt")), new AttributeOption(IntegerFactory.create(0), "center", Strings.getter("textVertAlignCenterOpt"))});
    private static final Attribute[] ATTRIBUTES = new Attribute[]{text_attr, font_attr, halign_attr, valign_attr};
    private static final Object[] DEFAULTS = new Object[]{"", new Font("SansSerif", 0, 12), halign_attr.parse("center"), valign_attr.parse("base")};
    private static final Icon toolIcon = Icons.getIcon("text.gif");

    private TextClass() {
    }

    @Override
    public String getName() {
        return "Text";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("textComponent");
    }

    @Override
    public AttributeSet createAttributeSet() {
        return AttributeSets.fixedSet(ATTRIBUTES, DEFAULTS);
    }

    @Override
    public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
        return new Text(loc, attrs);
    }

    @Override
    public Bounds getOffsetBounds(AttributeSet attrs) {
        return Bounds.EMPTY_BOUNDS;
    }

    @Override
    public Object getFeature(Object key, AttributeSet attrs) {
        if (key == SHOULD_SNAP) {
            return Boolean.FALSE;
        }
        return super.getFeature(key, attrs);
    }

    @Override
    public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
        AttributeOption ha = (AttributeOption)attrs.getValue(halign_attr);
        AttributeOption va = (AttributeOption)attrs.getValue(valign_attr);
        int h = (Integer)ha.getValue();
        int v = (Integer)va.getValue();
        Graphics g = context.getGraphics();
        String text = (String)attrs.getValue(text_attr);
        if (text == null || text.equals("")) {
            return;
        }
        g.setColor(color);
        Font old = g.getFont();
        g.setFont((Font)attrs.getValue(font_attr));
        GraphicsUtil.drawText(g, text, x, y, h, v);
        g.setFont(old);
    }

    @Override
    public void paintIcon(ComponentDrawContext c, int x, int y, AttributeSet attrs) {
        Graphics g = c.getGraphics();
        if (toolIcon != null) {
            toolIcon.paintIcon(c.getDestination(), g, x + 2, y + 2);
        } else {
            g.setColor(Color.black);
            GraphicsUtil.switchToWidth(g, 2);
            g.drawLine(x + 5, y + 16, x + 10, y + 2);
            g.drawLine(x + 15, y + 16, x + 10, y + 2);
            g.drawLine(x + 7, y + 11, x + 13, y + 11);
        }
    }
}

