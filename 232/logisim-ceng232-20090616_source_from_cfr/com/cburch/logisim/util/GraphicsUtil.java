/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

public class GraphicsUtil {
    public static final int H_LEFT = -1;
    public static final int H_CENTER = 0;
    public static final int H_RIGHT = 1;
    public static final int V_TOP = -1;
    public static final int V_CENTER = 0;
    public static final int V_BASELINE = 1;
    public static final int V_BOTTOM = 2;

    public static void switchToWidth(Graphics g, int width) {
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setStroke(new BasicStroke(width));
        }
    }

    public static void drawCenteredArc(Graphics g, int x, int y, int r, int start, int dist) {
        g.drawArc(x - r, y - r, 2 * r, 2 * r, start, dist);
    }

    public static Rectangle getTextBounds(Graphics g, Font font, String text, int x, int y, int halign, int valign) {
        if (g == null) {
            return new Rectangle(x, y, 0, 0);
        }
        Font oldfont = g.getFont();
        if (font != null) {
            g.setFont(font);
        }
        Rectangle ret = GraphicsUtil.getTextBounds(g, text, x, y, halign, valign);
        if (font != null) {
            g.setFont(oldfont);
        }
        return ret;
    }

    public static Rectangle getTextBounds(Graphics g, String text, int x, int y, int halign, int valign) {
        if (g == null) {
            return new Rectangle(x, y, 0, 0);
        }
        FontMetrics mets = g.getFontMetrics();
        int width = mets.stringWidth(text);
        int ascent = mets.getAscent();
        int height = ascent + mets.getDescent();
        Rectangle ret = new Rectangle(x, y, width, height);
        switch (halign) {
            case 0: {
                ret.translate(- width / 2, 0);
                break;
            }
            case 1: {
                ret.translate(- width, 0);
                break;
            }
        }
        switch (valign) {
            case -1: {
                break;
            }
            case 0: {
                ret.translate(0, - ascent / 2);
                break;
            }
            case 1: {
                ret.translate(0, - ascent);
                break;
            }
            case 2: {
                ret.translate(0, - height);
                break;
            }
        }
        return ret;
    }

    public static void drawText(Graphics g, Font font, String text, int x, int y, int halign, int valign) {
        Font oldfont = g.getFont();
        if (font != null) {
            g.setFont(font);
        }
        GraphicsUtil.drawText(g, text, x, y, halign, valign);
        if (font != null) {
            g.setFont(oldfont);
        }
    }

    public static void drawText(Graphics g, String text, int x, int y, int halign, int valign) {
        if (text.length() == 0) {
            return;
        }
        Rectangle bd = GraphicsUtil.getTextBounds(g, text, x, y, halign, valign);
        g.drawString(text, bd.x, bd.y + g.getFontMetrics().getAscent());
    }

    public static void drawCenteredText(Graphics g, String text, int x, int y) {
        GraphicsUtil.drawText(g, text, x, y, 0, 0);
    }

    public static void drawArrow(Graphics g, int x0, int y0, int x1, int y1, int headLength, int headAngle) {
        double offs = (double)headAngle * 3.141592653589793 / 180.0;
        double angle = Math.atan2(y0 - y1, x0 - x1);
        int[] xs = new int[]{x1 + (int)((double)headLength * Math.cos(angle + offs)), x1, x1 + (int)((double)headLength * Math.cos(angle - offs))};
        int[] ys = new int[]{y1 + (int)((double)headLength * Math.sin(angle + offs)), y1, y1 + (int)((double)headLength * Math.sin(angle - offs))};
        g.drawLine(x0, y0, x1, y1);
        g.drawPolyline(xs, ys, 3);
    }
}

