/*
 * Decompiled with CFR 0_114.
 */
package javax.help.plaf.gtk;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import javax.swing.ImageIcon;

public class GTKCursorFactory {
    private static Cursor onItemCursor;
    private static Cursor dndCursor;
    private static GTKCursorFactory theFactory;
    private static final boolean debug = false;

    public static Cursor getOnItemCursor() {
        GTKCursorFactory.debug("getOnItemCursor");
        if (theFactory == null) {
            theFactory = new GTKCursorFactory();
        }
        if (onItemCursor == null) {
            onItemCursor = theFactory.createCursor("OnItemCursor");
        }
        return onItemCursor;
    }

    public static Cursor getDnDCursor() {
        GTKCursorFactory.debug("getDnDCursor");
        if (theFactory == null) {
            theFactory = new GTKCursorFactory();
        }
        if (dndCursor == null) {
            dndCursor = theFactory.createCursor("DnDCursor");
        }
        return dndCursor;
    }

    private Cursor createCursor(String string) {
        byte[] arrby;
        Object object;
        int n;
        String string2 = null;
        String string3 = null;
        GTKCursorFactory.debug("CreateCursor for " + string);
        InputStream inputStream = this.getClass().getResourceAsStream("images/" + string + ".properties");
        if (inputStream == null) {
            GTKCursorFactory.debug(this.getClass().getName() + "/" + "images/" + string + ".properties" + " not found.");
            return null;
        }
        try {
            arrby = new byte[](inputStream);
            string2 = arrby.getString("Cursor.File");
            string3 = arrby.getString("Cursor.HotSpot");
        }
        catch (MissingResourceException var7_6) {
            GTKCursorFactory.debug(this.getClass().getName() + "/" + "images/" + string + ".properties" + " invalid.");
            return null;
        }
        catch (IOException var8_7) {
            GTKCursorFactory.debug(this.getClass().getName() + "/" + "images/" + string + ".properties" + " invalid.");
            return null;
        }
        arrby = null;
        try {
            InputStream inputStream2 = this.getClass().getResourceAsStream(string2);
            if (inputStream2 == null) {
                GTKCursorFactory.debug(this.getClass().getName() + "/" + string2 + " not found.");
                return null;
            }
            object = new BufferedInputStream(inputStream2);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            arrby = new byte[1024];
            while ((n = object.read(arrby)) > 0) {
                byteArrayOutputStream.write(arrby, 0, n);
            }
            object.close();
            byteArrayOutputStream.flush();
            arrby = byteArrayOutputStream.toByteArray();
            if (arrby.length == 0) {
                GTKCursorFactory.debug("warning: " + string2 + " is zero-length");
                return null;
            }
        }
        catch (IOException var8_9) {
            GTKCursorFactory.debug(var8_9.toString());
            return null;
        }
        ImageIcon imageIcon = new ImageIcon(arrby);
        int n2 = string3.indexOf(44);
        Point point = new Point(Integer.parseInt(string3.substring(0, n2)), Integer.parseInt(string3.substring(n2 + 1)));
        GTKCursorFactory.debug("Toolkit fetching cursor");
        try {
            object = imageIcon.getImage();
            int n3 = imageIcon.getIconWidth();
            n = imageIcon.getIconHeight();
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension dimension = toolkit.getBestCursorSize(n3, n);
            if (dimension.width > n3 || dimension.height > n) {
                try {
                    BufferedImage bufferedImage = new BufferedImage(dimension.width, dimension.height, 2);
                    bufferedImage.getGraphics().drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());
                    object = bufferedImage;
                }
                catch (Exception var14_21) {
                    // empty catch block
                }
            }
            return toolkit.createCustomCursor((Image)object, point, string);
        }
        catch (NoSuchMethodError var9_12) {
            return null;
        }
    }

    private static void debug(String string) {
    }
}

