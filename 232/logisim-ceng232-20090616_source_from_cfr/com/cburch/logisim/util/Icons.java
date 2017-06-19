/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.data.Direction;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icons {
    private static final String path = "com/cburch/logisim/resources/icons";

    private Icons() {
    }

    public static Icon getIcon(String name) {
        URL url = Icons.class.getClassLoader().getResource("com/cburch/logisim/resources/icons/" + name);
        if (url == null) {
            return null;
        }
        return new ImageIcon(url);
    }

    public static void paintRotated(Graphics g, int x, int y, Direction dir, Icon icon, Component dest) {
        if (!(g instanceof Graphics2D) || dir == Direction.EAST) {
            icon.paintIcon(dest, g, x, y);
            return;
        }
        Graphics2D g2 = (Graphics2D)g.create();
        double cx = (double)x + (double)icon.getIconWidth() / 2.0;
        double cy = (double)y + (double)icon.getIconHeight() / 2.0;
        if (dir == Direction.WEST) {
            g2.rotate(3.141592653589793, cx, cy);
        } else if (dir == Direction.NORTH) {
            g2.rotate(-1.5707963267948966, cx, cy);
        } else if (dir == Direction.SOUTH) {
            g2.rotate(1.5707963267948966, cx, cy);
        } else {
            g2.translate(- x, - y);
        }
        icon.paintIcon(dest, g2, x, y);
        g2.dispose();
    }
}

