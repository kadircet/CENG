/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.others;

import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.AttributeSets;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.tools.ToolTipMaker;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import tr.edu.metu.ceng.ceng232.others.ICDraw;

public abstract class ic
extends ManagedComponent
implements ToolTipMaker {
    protected static final int DELAY = 3;
    public static final Attribute facing_attr = Attributes.forDirection("Facing");
    private ICDraw.ICDescriptor descriptor;
    private static final Attribute[] ATTRIBUTES = new Attribute[]{facing_attr};
    private static final Object[] VALUES = new Object[]{Direction.EAST};

    public ic(Location loc, AttributeSet attrs, ICDraw.ICDescriptor desc) {
        super(loc, attrs, desc.getE() + desc.getN() + desc.getW() + desc.getS());
        attrs.setReadOnly(facing_attr, true);
        this.descriptor = desc;
    }

    public int getWestPin(int i) {
        return i;
    }

    public int getEastPin(int i) {
        return this.descriptor.getW() + i;
    }

    public int getNorthPin(int i) {
        return this.descriptor.getW() + this.descriptor.getE() + i;
    }

    public int getSouthPin(int i) {
        return this.descriptor.getW() + this.descriptor.getE() + this.descriptor.getN() + i;
    }

    public int getPinW() {
        return this.descriptor.getPinW();
    }

    public int getPinH() {
        return this.descriptor.getPinH();
    }

    public Location getPinLoc(Direction dir, int i) {
        Direction facing = (Direction)this.getAttributeSet().getValue(facing_attr);
        return ICDraw.getPinLoc(this.descriptor, this.getLocation().getX(), this.getLocation().getY(), facing, dir, i);
    }

    public Location getWestPinLoc(int i) {
        return this.getPinLoc(Direction.WEST, i);
    }

    public Location getEastPinLoc(int i) {
        return this.getPinLoc(Direction.EAST, i);
    }

    public Location getNorthPinLoc(int i) {
        return this.getPinLoc(Direction.NORTH, i);
    }

    public Location getSouthPinLoc(int i) {
        return this.getPinLoc(Direction.SOUTH, i);
    }

    @Override
    public Object getFeature(Object key) {
        if (key == ToolTipMaker.class) {
            return this;
        }
        return super.getFeature(key);
    }

    @Override
    public String getToolTip(ComponentUserEvent e) {
        int i;
        for (i = 0; i < this.descriptor.pinsWest.length; ++i) {
            if (this.getEndLocation(this.getWestPin(i)).manhattanDistanceTo(e.getX(), e.getY()) >= 10) continue;
            return this.descriptor.pinsWest[i].name;
        }
        for (i = 0; i < this.descriptor.pinsEast.length; ++i) {
            if (this.getEndLocation(this.getEastPin(i)).manhattanDistanceTo(e.getX(), e.getY()) >= 10) continue;
            return this.descriptor.pinsEast[i].name;
        }
        for (i = 0; i < this.descriptor.pinsNorth.length; ++i) {
            if (this.getEndLocation(this.getNorthPin(i)).manhattanDistanceTo(e.getX(), e.getY()) >= 10) continue;
            return this.descriptor.pinsNorth[i].name;
        }
        for (i = 0; i < this.descriptor.pinsSouth.length; ++i) {
            if (this.getEndLocation(this.getSouthPin(i)).manhattanDistanceTo(e.getX(), e.getY()) >= 10) continue;
            return this.descriptor.pinsSouth[i].name;
        }
        return null;
    }

    public static abstract class ICFactory
    extends AbstractComponentFactory {
        private Icon toolIcon;
        private ICDraw.ICDescriptor desc;

        public ICFactory(ICDraw.ICDescriptor desc, Icon toolicon) {
            this.desc = desc;
            this.toolIcon = toolicon;
        }

        @Override
        public String getDisplayName() {
            return this.getName();
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, VALUES);
        }

        @Override
        public Bounds getOffsetBounds(AttributeSet attrs) {
            Direction facing = (Direction)attrs.getValue(ic.facing_attr);
            return ICDraw.getBounds(this.desc, facing);
        }

        @Override
        public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
            Direction facing = (Direction)attrs.getValue(ic.facing_attr);
            ICDraw.draw(this.desc, context, color, x, y, facing);
        }

        @Override
        public void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
            Graphics g = context.getGraphics();
            if (this.toolIcon != null) {
                this.toolIcon.paintIcon(context.getDestination(), g, x + 2, y + 2);
            }
        }

        @Override
        public Object getFeature(Object key, AttributeSet attrs) {
            if (key == FACING_ATTRIBUTE_KEY) {
                return ic.facing_attr;
            }
            return super.getFeature(key, attrs);
        }
    }

}

