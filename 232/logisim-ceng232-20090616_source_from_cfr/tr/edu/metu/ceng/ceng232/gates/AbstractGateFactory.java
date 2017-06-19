/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.gates;

import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeOption;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.tools.WireRepairData;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.StringGetter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import tr.edu.metu.ceng.ceng232.gates.AbstractGate;
import tr.edu.metu.ceng.ceng232.gates.GateAttributes;

abstract class AbstractGateFactory
extends AbstractComponentFactory {
    private static final int ATTEMPT_SHAPED = 1;
    private static final int ATTEMPT_RECTANGULAR = 2;
    private static final int ATTEMPT_DIN40700 = 4;
    private String name;
    private StringGetter desc;
    private int iconAttempts = 0;
    private Icon iconShaped = null;
    private Icon iconRect = null;
    private Icon iconDin = null;
    int bonus_width = 0;
    boolean has_dongle = false;
    String rect_label = "";

    protected AbstractGateFactory(String name, StringGetter desc) {
        this.name = name;
        this.desc = desc;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return this.desc.get();
    }

    @Override
    public AttributeSet createAttributeSet() {
        return new GateAttributes();
    }

    @Override
    public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
        return new AbstractGate(loc, attrs, this);
    }

    @Override
    public Bounds getOffsetBounds(AttributeSet attrs) {
        return this.computeBounds((GateAttributes)attrs);
    }

    @Override
    public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet baseAttrs) {
        GateAttributes attrs = (GateAttributes)baseAttrs;
        Bounds bounds = this.computeBounds(attrs);
        context.getGraphics().setColor(color);
        AbstractGate.drawBase(context, this, null, attrs, x, y, bounds.getWidth(), bounds.getHeight());
    }

    @Override
    public Object getFeature(Object key, AttributeSet attrs) {
        if (key == FACING_ATTRIBUTE_KEY) {
            return GateAttributes.facing_attr;
        }
        return super.getFeature(key, attrs);
    }

    public abstract Icon getIconShaped();

    public abstract Icon getIconRectangular();

    public abstract Icon getIconDin40700();

    public abstract void paintIconShaped(ComponentDrawContext var1, int var2, int var3, AttributeSet var4);

    public void paintIconRectangular(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        g.drawRect(x + 1, y + 2, 16, 16);
        if (this.has_dongle) {
            g.drawOval(x + 16, y + 8, 4, 4);
        }
        GraphicsUtil.drawCenteredText(g, this.rect_label, x + 9, y + 8);
    }

    @Override
    public final void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        g.setColor(Color.black);
        if (context.getGateShape() == "rectangular") {
            if (this.iconRect == null && (this.iconAttempts & 2) == 0) {
                this.iconRect = this.getIconRectangular();
                this.iconAttempts |= 2;
            }
            if (this.iconRect != null) {
                this.iconRect.paintIcon(context.getDestination(), g, x + 2, y + 2);
            } else {
                this.paintIconRectangular(context, x, y, attrs);
            }
        } else if (context.getGateShape() == "din40700") {
            if (this.iconDin == null && (this.iconAttempts & 4) == 0) {
                this.iconDin = this.getIconDin40700();
                this.iconAttempts |= 4;
            }
            if (this.iconDin != null) {
                this.iconDin.paintIcon(context.getDestination(), g, x + 2, y + 2);
            } else {
                this.paintIconRectangular(context, x, y, attrs);
            }
        } else {
            if (this.iconShaped == null && (this.iconAttempts & 1) == 0) {
                this.iconShaped = this.getIconShaped();
                this.iconAttempts |= 1;
            }
            if (this.iconShaped != null) {
                this.iconShaped.paintIcon(context.getDestination(), g, x + 2, y + 2);
            } else {
                this.paintIconShaped(context, x, y, attrs);
            }
        }
    }

    protected void setAdditionalWidth(int value) {
        this.bonus_width = value;
    }

    protected void setHasDongle(boolean value) {
        this.has_dongle = value;
    }

    protected void setRectangularLabel(String value) {
        this.rect_label = value;
    }

    protected String getRectangularLabel() {
        return this.rect_label;
    }

    protected void drawInputLines(ComponentDrawContext context, AbstractGate comp, int inputs, int x, int yTop, int width, int height) {
    }

    protected abstract void drawShape(ComponentDrawContext var1, int var2, int var3, int var4, int var5);

    protected void drawRectangular(ComponentDrawContext context, int x, int y, int width, int height) {
        int don = this.has_dongle ? 10 : 0;
        context.drawRectangle(x - width, y - height / 2, width - don, height, this.rect_label);
        if (this.has_dongle) {
            context.drawDongle(x - 5, y);
        }
    }

    protected abstract void drawDinShape(ComponentDrawContext var1, int var2, int var3, int var4, int var5, int var6, AbstractGate var7);

    protected abstract Value computeOutput(Value[] var1, int var2);

    protected abstract Expression computeExpression(Expression[] var1, int var2);

    protected boolean shouldRepairWire(com.cburch.logisim.comp.Component comp, WireRepairData data) {
        return false;
    }

    private Bounds computeBounds(GateAttributes attrs) {
        Direction facing = attrs.facing;
        int size = (Integer)attrs.size.getValue();
        int width = size + this.bonus_width + (this.has_dongle ? 10 : 0);
        int height = Math.max(10 * attrs.inputs, size);
        if (facing == Direction.SOUTH) {
            return Bounds.create((- height) / 2, - width, height, width);
        }
        if (facing == Direction.NORTH) {
            return Bounds.create((- height) / 2, 0, height, width);
        }
        if (facing == Direction.WEST) {
            return Bounds.create(0, (- height) / 2, width, height);
        }
        return Bounds.create(- width, (- height) / 2, width, height);
    }
}

