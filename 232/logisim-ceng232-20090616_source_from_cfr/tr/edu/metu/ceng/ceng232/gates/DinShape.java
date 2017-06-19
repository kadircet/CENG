/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.gates;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.IntegerFactory;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import java.util.HashMap;
import tr.edu.metu.ceng.ceng232.gates.AbstractGate;

class DinShape {
    static final int AND = 0;
    static final int OR = 1;
    static final int XOR = 2;
    static final int XNOR = 3;
    private static HashMap orLenArrays = new HashMap();

    private DinShape() {
    }

    static void draw(ComponentDrawContext context, int x, int y, int width, int height, boolean drawBubble, int dinType) {
        CircuitState state;
        Graphics g = context.getGraphics();
        int xMid = x - width;
        int xBase = x;
        int y0 = y - height / 2;
        if (drawBubble) {
            x -= 4;
            width -= 8;
        }
        int diam = Math.min(height, 2 * width);
        if (dinType != 0 && dinType != 1) {
            if (dinType == 2 || dinType == 3) {
                int elen = Math.min(diam / 2 - 10, 20);
                int ex0 = xMid + (diam / 2 - elen) / 2;
                int ex1 = ex0 + elen;
                g.drawLine(ex0, y - 5, ex1, y - 5);
                g.drawLine(ex0, y, ex1, y);
                g.drawLine(ex0, y + 5, ex1, y + 5);
                if (dinType == 2) {
                    int exMid = ex0 + elen / 2;
                    g.drawLine(exMid, y - 8, exMid, y + 8);
                }
            } else {
                throw new IllegalArgumentException("unrecognized shape");
            }
        }
        GraphicsUtil.switchToWidth(g, 2);
        int x0 = xMid - diam / 2;
        Color oldColor = g.getColor();
        if (context.getShowState() && (state = context.getCircuitState()) != null) {
            Location loc = Location.create(xBase, y);
            Value val = state.getValue(loc);
            g.setColor(val.getColor());
        }
        g.drawLine(x0 + diam, y, x, y);
        g.setColor(oldColor);
        if (height <= diam) {
            g.drawArc(x0, y0, diam, diam, -90, 180);
        } else {
            int x1 = x0 + diam;
            int yy0 = y - (height - diam) / 2;
            int yy1 = y + (height - diam) / 2;
            g.drawArc(x0, y0, diam, diam, 0, 90);
            g.drawLine(x1, yy0, x1, yy1);
            g.drawArc(x0, y0 + height - diam, diam, diam, -90, 90);
        }
        g.drawLine(xMid, y0, xMid, y0 + height);
        if (drawBubble) {
            g.fillOval(x0 + diam - 4, y - 4, 8, 8);
        }
    }

    static void drawOrLines(ComponentDrawContext context, int rx, int cy, int width, int height, int inputs, AbstractGate gate, boolean hasBubble) {
        int i;
        int x0 = rx - width;
        if (hasBubble) {
            rx -= 4;
            width -= 8;
        }
        Graphics g = context.getGraphics();
        int dy = (height - 10) / (inputs - 1);
        int r = Math.min(height / 2, width);
        Integer hash = IntegerFactory.create(r << 4 | inputs);
        int[] lens = (int[])orLenArrays.get(hash);
        if (lens == null) {
            int i2;
            lens = new int[inputs];
            orLenArrays.put(hash, lens);
            int y = cy - height / 2 + 5;
            if (height <= 2 * r) {
                for (i2 = 0; i2 < inputs; ++i2) {
                    int a = y - cy;
                    lens[i2] = (int)(Math.sqrt(r * r - a * a) + 0.5);
                    y += dy;
                }
            } else {
                for (i2 = 0; i2 < inputs; ++i2) {
                    lens[i2] = r;
                }
                int yy0 = cy - height / 2 + r;
                i = 0;
                while (y < yy0) {
                    int a = y - yy0;
                    lens[i] = (int)(Math.sqrt(r * r - a * a) + 0.5);
                    lens[lens.length - 1 - i] = lens[i];
                    ++i;
                    y += dy;
                }
            }
        }
        boolean printView = context.isPrintView() && gate != null;
        GraphicsUtil.switchToWidth(g, 2);
        int y = cy - height / 2 + 5;
        i = 0;
        while (i < inputs) {
            block10 : {
                if (printView) {
                    Location loc = gate.getEndLocation(i + 1);
                    if (context.getCircuit().getComponents(loc).size() <= 1) break block10;
                }
                g.drawLine(x0, y, x0 + lens[i], y);
            }
            ++i;
            y += dy;
        }
    }
}

