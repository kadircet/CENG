/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.CircuitWires;
import com.cburch.logisim.circuit.SplitterAttributes;
import com.cburch.logisim.circuit.SplitterFactory;
import com.cburch.logisim.circuit.Strings;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.tools.ToolTipMaker;
import com.cburch.logisim.tools.WireRepair;
import com.cburch.logisim.tools.WireRepairData;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.StringUtil;
import java.awt.Color;
import java.awt.Graphics;

class Splitter
extends ManagedComponent
implements WireRepair,
ToolTipMaker {
    private static final int SPINE_WIDTH = 4;
    private static final int SPINE_DOT = 4;
    byte[] bit_thread;
    private MyAttributeListener myAttributeListener;
    CircuitWires.SplitterData wire_data;

    public Splitter(Location loc, AttributeSet attrs) {
        super(loc, attrs, 3);
        this.myAttributeListener = new MyAttributeListener();
        ((SplitterAttributes)attrs).frozen = true;
        this.configureComponent();
        attrs.addAttributeListener(this.myAttributeListener);
    }

    @Override
    public ComponentFactory getFactory() {
        return SplitterFactory.instance;
    }

    @Override
    public void propagate(CircuitState state) {
    }

    private synchronized void configureComponent() {
        int ddy;
        int dy;
        int ddx;
        int dx;
        this.clearManager();
        SplitterAttributes attrs = (SplitterAttributes)this.getAttributeSet();
        Direction facing = attrs.facing;
        int fanout = attrs.fanout;
        byte[] bit_end = attrs.bit_end;
        this.bit_thread = new byte[bit_end.length];
        byte[] end_width = new byte[fanout + 1];
        end_width[0] = (byte)bit_end.length;
        for (int i = 0; i < bit_end.length; ++i) {
            byte thr = bit_end[i];
            if (thr > 0) {
                this.bit_thread[i] = end_width[thr];
                byte[] arrby = end_width;
                byte by = thr;
                arrby[by] = (byte)(arrby[by] + 1);
                continue;
            }
            this.bit_thread[i] = -1;
        }
        int offs = (- fanout / 2) * 10;
        if (facing == Direction.EAST) {
            dx = 20;
            ddx = 0;
            dy = offs;
            ddy = 10;
        } else if (facing == Direction.WEST) {
            dx = -20;
            ddx = 0;
            dy = offs;
            ddy = 10;
        } else if (facing == Direction.NORTH) {
            dx = offs + (fanout - 1) * 10;
            ddx = -10;
            dy = -20;
            ddy = 0;
        } else if (facing == Direction.SOUTH) {
            dx = offs + (fanout - 1) * 10;
            ddx = -10;
            dy = 20;
            ddy = 0;
        } else {
            throw new IllegalArgumentException("unrecognized direction");
        }
        Location loc = this.getLocation();
        this.setEnd(0, loc, BitWidth.create(bit_end.length), 3);
        for (int i2 = 1; i2 <= fanout; ++i2) {
            Location p = loc.translate(dx, dy);
            this.setEnd(i2, p, BitWidth.create(end_width[i2]), 3);
            dx += ddx;
            dy += ddy;
        }
        this.wire_data = new CircuitWires.SplitterData(fanout);
    }

    @Override
    public void draw(ComponentDrawContext context) {
        Graphics g = context.getGraphics();
        CircuitState state = context.getCircuitState();
        SplitterAttributes attrs = (SplitterAttributes)this.getAttributeSet();
        Direction facing = attrs.facing;
        int fanout = attrs.fanout;
        g.setColor(Color.BLACK);
        Location s = this.getEndLocation(0);
        if (facing == Direction.NORTH || facing == Direction.SOUTH) {
            Location t = this.getEndLocation(1);
            int mx = s.getX();
            int my = (s.getY() + t.getY()) / 2;
            GraphicsUtil.switchToWidth(g, 3);
            g.drawLine(mx, s.getY(), mx, my);
            for (int i = 1; i <= fanout; ++i) {
                int tx;
                t = this.getEndLocation(i);
                if (context.getShowState()) {
                    g.setColor(state.getValue(t).getColor());
                }
                g.drawLine(tx, t.getY(), (tx = t.getX()) < mx ? tx + 10 : (tx > mx ? tx - 10 : tx), my);
            }
            if (fanout > 3) {
                GraphicsUtil.switchToWidth(g, 4);
                g.setColor(Color.BLACK);
                t = this.getEndLocation(1);
                Location last = this.getEndLocation(fanout);
                g.drawLine(t.getX() - 10, my, last.getX() + 10, my);
            } else {
                g.setColor(Color.BLACK);
                g.fillOval(mx - 2, my - 2, 4, 4);
            }
        } else {
            Location t = this.getEndLocation(1);
            int mx = (s.getX() + t.getX()) / 2;
            int my = s.getY();
            GraphicsUtil.switchToWidth(g, 3);
            g.drawLine(s.getX(), my, mx, my);
            for (int i = 1; i <= fanout; ++i) {
                int ty;
                t = this.getEndLocation(i);
                if (context.getShowState()) {
                    g.setColor(state.getValue(t).getColor());
                }
                g.drawLine(t.getX(), ty, mx, (ty = t.getY()) < my ? ty + 10 : (ty > my ? ty - 10 : ty));
            }
            if (fanout > 3) {
                GraphicsUtil.switchToWidth(g, 4);
                g.setColor(Color.BLACK);
                t = this.getEndLocation(1);
                Location last = this.getEndLocation(fanout);
                g.drawLine(mx, t.getY() + 10, mx, last.getY() - 10);
            } else {
                g.setColor(Color.BLACK);
                g.fillOval(mx - 2, my - 2, 4, 4);
            }
        }
        GraphicsUtil.switchToWidth(g, 1);
    }

    @Override
    public Object getFeature(Object key) {
        if (key == WireRepair.class) {
            return this;
        }
        if (key == ToolTipMaker.class) {
            return this;
        }
        return super.getFeature(key);
    }

    @Override
    public boolean shouldRepairWire(WireRepairData data) {
        return true;
    }

    @Override
    public String getToolTip(ComponentUserEvent e) {
        byte end = -1;
        for (byte i = this.getEnds().size() - 1; i >= 0; --i) {
            if (this.getEndLocation(i).manhattanDistanceTo(e.getX(), e.getY()) >= 10) continue;
            end = i;
            break;
        }
        if (end == 0) {
            return Strings.get("splitterCombinedTip");
        }
        if (end > 0) {
            String base;
            int bits = 0;
            StringBuffer buf = new StringBuffer();
            SplitterAttributes attrs = (SplitterAttributes)this.getAttributeSet();
            byte[] bit_end = attrs.bit_end;
            boolean inString = false;
            int beginString = 0;
            for (int i2 = 0; i2 < bit_end.length; ++i2) {
                if (bit_end[i2] == end) {
                    ++bits;
                    if (inString) continue;
                    inString = true;
                    beginString = i2;
                    continue;
                }
                if (!inString) continue;
                Splitter.appendBuf(buf, beginString, i2 - 1);
                inString = false;
            }
            if (inString) {
                Splitter.appendBuf(buf, beginString, bit_end.length - 1);
            }
            switch (bits) {
                case 0: {
                    base = Strings.get("splitterSplit0Tip");
                    break;
                }
                case 1: {
                    base = Strings.get("splitterSplit1Tip");
                    break;
                }
                default: {
                    base = Strings.get("splitterSplitManyTip");
                }
            }
            return StringUtil.format(base, buf.toString());
        }
        return null;
    }

    private static void appendBuf(StringBuffer buf, int start, int end) {
        if (buf.length() > 0) {
            buf.append(",");
        }
        if (start == end) {
            buf.append(start);
        } else {
            buf.append("" + start + "-" + end);
        }
    }

    private class MyAttributeListener
    implements AttributeListener {
        private MyAttributeListener() {
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            Splitter.this.configureComponent();
        }
    }

}

