/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.others;

import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class ICDraw {
    static final int PIN_SPACING = 10;

    public static void draw(ICDescriptor desc, ComponentDrawContext context, Color color, int x, int y, Direction facing) {
        Graphics g = context.getGraphics();
        g.setColor(color);
        Bounds bds = ICDraw.getBounds(desc, facing).translate(x, y);
        g.drawRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
        GraphicsUtil.drawCenteredText(g, desc.name, bds.getX() + bds.getWidth() / 2, bds.getY() + bds.getHeight() / 2);
        ICDraw.drawPins(g, desc, x, y, facing, Direction.EAST);
        ICDraw.drawPins(g, desc, x, y, facing, Direction.WEST);
        ICDraw.drawPins(g, desc, x, y, facing, Direction.NORTH);
        ICDraw.drawPins(g, desc, x, y, facing, Direction.SOUTH);
    }

    public static void drawPins(Graphics g, ICDescriptor desc, int locX, int locY, Direction facing, Direction dir) {
        ICPin[] pins = dir == Direction.EAST ? desc.pinsEast : (dir == Direction.NORTH ? desc.pinsNorth : (dir == Direction.WEST ? desc.pinsWest : desc.pinsSouth));
        Direction pinFacing = ICDraw.getPinFacing(facing, dir);
        Font font = g.getFont();
        font = new Font(font.getName(), 0, font.getSize() / 2);
        for (int i = 0; i < pins.length; ++i) {
            ICPin pin = pins[i];
            Location loc = ICDraw.getPinLoc(desc, locX, locY, facing, dir, i);
            int halign = 0;
            int valign = 0;
            int yOffset = 0;
            int xOffset = 0;
            Graphics2D g2d = g instanceof Graphics2D ? (Graphics2D)g : null;
            AffineTransform originalTransform = null;
            if (g2d != null) {
                originalTransform = g2d.getTransform();
            }
            if (pinFacing == Direction.EAST) {
                halign = 1;
                xOffset = (- g.getFontMetrics().charWidth(' ')) / 2;
            } else if (pinFacing == Direction.WEST) {
                halign = -1;
                xOffset = g.getFontMetrics().charWidth(' ') / 2;
            } else if (pinFacing == Direction.NORTH) {
                valign = -1;
                halign = -1;
                yOffset = g.getFontMetrics().getAscent() / 4;
                if (g2d != null) {
                    AffineTransform northTransform = new AffineTransform();
                    northTransform.rotate(1.5707963267948966, xOffset + loc.getX(), yOffset + loc.getY());
                    g2d.transform(northTransform);
                }
            } else {
                valign = -1;
                halign = -1;
                yOffset = (- g.getFontMetrics().getAscent()) / 4;
                if (g2d != null) {
                    AffineTransform southTransform = new AffineTransform();
                    southTransform.rotate(-1.5707963267948966, xOffset + loc.getX(), yOffset + loc.getY());
                    g2d.transform(southTransform);
                }
            }
            String n = "";
            if (pin.type == ICPinType.CLOCK) {
                n = "CLK";
            } else if (pin.type == ICPinType.INVERSEPIN) {
                n = "/";
            }
            n = pin.name;
            GraphicsUtil.drawText(g, font, n, xOffset + loc.getX(), yOffset + loc.getY(), halign, valign);
            if (originalTransform == null) continue;
            g2d.setTransform(originalTransform);
        }
    }

    public static Bounds getBounds(ICDescriptor desc, Direction facing) {
        int width = (desc.getPinW() + 1) * 10;
        int height = (desc.getPinH() + 1) * 10;
        return Bounds.create(0, 0, width, height).rotate(Direction.EAST, facing, 0, 0);
    }

    public static Location getPinLoc(ICDescriptor desc, int locX, int locY, Direction facing, Direction dir, int i) {
        int x = 0;
        int y = 0;
        if (dir == Direction.WEST) {
            x = 0;
            y = 10 + i * 10;
        } else if (dir == Direction.EAST) {
            x = 10 + desc.getPinW() * 10;
            y = 10 + i * 10;
        } else if (dir == Direction.NORTH) {
            x = 10 + i * 10;
            y = 0;
        } else if (dir == Direction.SOUTH) {
            x = 10 + i * 10;
            y = 10 + desc.getPinH() * 10;
        }
        Bounds b = Bounds.create(x, y, 0, 0).rotate(Direction.EAST, facing, 0, 0);
        return Location.create(locX + b.getX(), locY + b.getY());
    }

    public static Direction getPinFacing(Direction icFacing, Direction pinDir) {
        if (icFacing == Direction.EAST) {
            return pinDir;
        }
        if (icFacing == Direction.NORTH) {
            if (pinDir == Direction.EAST) {
                return Direction.NORTH;
            }
            if (pinDir == Direction.NORTH) {
                return Direction.WEST;
            }
            if (pinDir == Direction.WEST) {
                return Direction.SOUTH;
            }
            return Direction.EAST;
        }
        if (icFacing == Direction.WEST) {
            if (pinDir == Direction.EAST) {
                return Direction.WEST;
            }
            if (pinDir == Direction.NORTH) {
                return Direction.SOUTH;
            }
            if (pinDir == Direction.WEST) {
                return Direction.EAST;
            }
            return Direction.NORTH;
        }
        if (pinDir == Direction.EAST) {
            return Direction.SOUTH;
        }
        if (pinDir == Direction.NORTH) {
            return Direction.WEST;
        }
        if (pinDir == Direction.WEST) {
            return Direction.NORTH;
        }
        return Direction.EAST;
    }

    public static class ICDescriptor {
        public ICPin[] pinsWest;
        public ICPin[] pinsEast;
        public ICPin[] pinsNorth;
        public ICPin[] pinsSouth;
        public String name;

        public ICDescriptor(ICPin[] w, ICPin[] e, ICPin[] n, ICPin[] s, String name) {
            this.pinsWest = w;
            this.pinsEast = e;
            this.pinsNorth = n;
            this.pinsSouth = s;
            this.name = name;
        }

        public int getN() {
            return this.pinsNorth.length;
        }

        public int getS() {
            return this.pinsSouth.length;
        }

        public int getW() {
            return this.pinsWest.length;
        }

        public int getE() {
            return this.pinsEast.length;
        }

        public int getPinW() {
            int a;
            int n = a = this.getN() > this.getS() ? this.getN() : this.getS();
            if (a < 5) {
                return 5;
            }
            return a;
        }

        public int getPinH() {
            int a;
            int n = a = this.getW() > this.getE() ? this.getW() : this.getE();
            if (a < 5) {
                return 5;
            }
            return a;
        }
    }

    public static class ICPin {
        public ICPinType type;
        public String name;

        public ICPin(ICPinType t, String n) {
            this.type = t;
            this.name = n;
        }
    }

    public static enum ICPinType {
        PIN,
        INVERSEPIN,
        CLOCK;
        

        private ICPinType() {
        }
    }

}

