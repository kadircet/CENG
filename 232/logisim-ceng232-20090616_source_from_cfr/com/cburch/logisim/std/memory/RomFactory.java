/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.memory;

import com.cburch.hex.HexModel;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.gui.hex.HexFile;
import com.cburch.logisim.gui.hex.HexFrame;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.std.memory.Mem;
import com.cburch.logisim.std.memory.MemContents;
import com.cburch.logisim.std.memory.Rom;
import com.cburch.logisim.std.memory.RomAttributes;
import com.cburch.logisim.std.memory.Strings;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.StringGetter;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.swing.JLabel;

class RomFactory
extends AbstractComponentFactory {
    public static ComponentFactory INSTANCE = new RomFactory();
    public static Attribute CONTENTS_ATTR = new ContentsAttribute();

    private RomFactory() {
    }

    @Override
    public String getName() {
        return "ROM";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("romComponent");
    }

    @Override
    public AttributeSet createAttributeSet() {
        return new RomAttributes();
    }

    @Override
    public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
        return new Rom(loc, attrs);
    }

    @Override
    public Bounds getOffsetBounds(AttributeSet attrs) {
        return Mem.OFFSET_BOUNDS;
    }

    @Override
    public void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        Font old = g.getFont();
        g.setFont(old.deriveFont(9.0f));
        GraphicsUtil.drawCenteredText(g, "ROM", x + 10, y + 9);
        g.setFont(old);
        g.drawRect(x, y + 4, 19, 12);
        for (int dx = 2; dx < 20; dx += 5) {
            g.drawLine(x + dx, y + 2, x + dx, y + 4);
            g.drawLine(x + dx, y + 16, x + dx, y + 18);
        }
    }

    private static class ContentsCell
    extends JLabel
    implements MouseListener {
        Window source;
        MemContents contents;

        ContentsCell(Window source, MemContents contents) {
            super(Strings.get("romContentsValue"));
            this.source = source;
            this.contents = contents;
            this.addMouseListener(this);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (this.contents == null) {
                return;
            }
            Project proj = this.source instanceof Frame ? ((Frame)this.source).getProject() : null;
            HexFrame frame = RomAttributes.getHexFrame(this.contents, proj);
            frame.setVisible(true);
            frame.toFront();
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    private static class ContentsAttribute
    extends Attribute {
        public ContentsAttribute() {
            super("contents", Strings.getter("romContentsAttr"));
        }

        @Override
        public Component getCellEditor(Window source, Object value) {
            if (source instanceof Frame) {
                Project proj = ((Frame)source).getProject();
                RomAttributes.register((MemContents)value, proj);
            }
            ContentsCell ret = new ContentsCell(source, (MemContents)value);
            ret.mouseClicked(null);
            return ret;
        }

        @Override
        public String toDisplayString(Object value) {
            return Strings.get("romContentsValue");
        }

        @Override
        public String toStandardString(Object value) {
            MemContents state = (MemContents)value;
            int addr = state.getLogLength();
            int data = state.getWidth();
            StringWriter ret = new StringWriter();
            ret.write("addr/data: " + addr + " " + data + "\n");
            try {
                HexFile.save(ret, (HexModel)state);
            }
            catch (IOException e) {
                // empty catch block
            }
            return ret.toString();
        }

        @Override
        public Object parse(String value) {
            int lineBreak = value.indexOf(10);
            String first = lineBreak < 0 ? value : value.substring(0, lineBreak);
            String rest = lineBreak < 0 ? "" : value.substring(lineBreak + 1);
            StringTokenizer toks = new StringTokenizer(first);
            try {
                String header = toks.nextToken();
                if (!header.equals("addr/data:")) {
                    return null;
                }
                int addr = Integer.parseInt(toks.nextToken());
                int data = Integer.parseInt(toks.nextToken());
                MemContents ret = MemContents.create(addr, data);
                HexFile.open((HexModel)ret, new StringReader(rest));
                return ret;
            }
            catch (IOException e) {
                return null;
            }
            catch (NumberFormatException e) {
                return null;
            }
            catch (NoSuchElementException e) {
                return null;
            }
        }
    }

}

