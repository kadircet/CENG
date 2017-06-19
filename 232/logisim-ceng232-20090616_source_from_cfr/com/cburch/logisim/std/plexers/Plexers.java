/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.plexers;

import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.std.plexers.BitSelector;
import com.cburch.logisim.std.plexers.Decoder;
import com.cburch.logisim.std.plexers.Demultiplexer;
import com.cburch.logisim.std.plexers.Multiplexer;
import com.cburch.logisim.std.plexers.Strings;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.List;

public class Plexers
extends Library {
    public static final Attribute facing_attr = Attributes.forDirection("facing", Strings.getter("plexerFacingAttr"));
    public static final Attribute data_attr = Attributes.forBitWidth("width", Strings.getter("plexerDataWidthAttr"));
    public static final Object data_dflt = BitWidth.create(1);
    public static final Attribute select_attr = Attributes.forBitWidth("select", Strings.getter("plexerSelectBitsAttr"), 1, 4);
    public static final Object select_dflt = BitWidth.create(1);
    public static final Attribute threeState_attr = Attributes.forBoolean("tristate", Strings.getter("plexerThreeStateAttr"));
    public static final Object threeState_dflt = Boolean.FALSE;
    protected static final int DELAY = 3;
    private List tools = null;

    @Override
    public String getName() {
        return "Plexers";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("plexerLibrary");
    }

    @Override
    public List getTools() {
        if (this.tools == null) {
            this.tools = Arrays.asList(new AddTool(Multiplexer.factory), new AddTool(Demultiplexer.factory), new AddTool(Decoder.factory), new AddTool(BitSelector.factory));
        }
        return this.tools;
    }

    static void drawTrapezoid(Graphics g, Bounds bds, Direction facing, int facingLean) {
        int wid = bds.getWidth();
        int ht = bds.getHeight();
        int x0 = bds.getX();
        int x1 = x0 + wid;
        int y0 = bds.getY();
        int y1 = y0 + ht;
        int[] xp = new int[]{x0, x1, x1, x0};
        int[] yp = new int[]{y0, y0, y1, y1};
        if (facing == Direction.WEST) {
            int[] arrn = yp;
            arrn[0] = arrn[0] + facingLean;
            int[] arrn2 = yp;
            arrn2[3] = arrn2[3] - facingLean;
        } else if (facing == Direction.NORTH) {
            int[] arrn = xp;
            arrn[0] = arrn[0] + facingLean;
            int[] arrn3 = xp;
            arrn3[1] = arrn3[1] - facingLean;
        } else if (facing == Direction.SOUTH) {
            int[] arrn = xp;
            arrn[2] = arrn[2] - facingLean;
            int[] arrn4 = xp;
            arrn4[3] = arrn4[3] + facingLean;
        } else {
            int[] arrn = yp;
            arrn[1] = arrn[1] + facingLean;
            int[] arrn5 = yp;
            arrn5[2] = arrn5[2] - facingLean;
        }
        GraphicsUtil.switchToWidth(g, 2);
        g.drawPolygon(xp, yp, 4);
    }
}

