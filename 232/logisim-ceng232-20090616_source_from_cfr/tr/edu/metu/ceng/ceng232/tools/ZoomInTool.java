/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.tools;

import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.Tool;
import java.awt.Graphics;
import tr.edu.metu.ceng.ceng232.tools.ZoomActions;

public class ZoomInTool
extends Tool {
    public boolean equals(Object other) {
        return other instanceof ZoomInTool;
    }

    public int hashCode() {
        return ZoomInTool.class.hashCode();
    }

    @Override
    public String getName() {
        return "Zoom-in Tool";
    }

    @Override
    public String getDisplayName() {
        return this.getName();
    }

    @Override
    public String getDescription() {
        return this.getName();
    }

    @Override
    public void select(Canvas canvas) {
        double change = 1.41421356;
        AttributeSet attrs = canvas.getProject().getOptions().getAttributeSet();
        canvas.getProject().doAction(ZoomActions.setZoom(attrs, Options.zoom_attr, (Double)attrs.getValue(Options.zoom_attr) * change));
        canvas.getProject().setTool(null);
    }

    @Override
    public void paintIcon(ComponentDrawContext c, int x, int y) {
        Graphics g = c.getGraphics();
        g.drawOval(x + 1, y + 1, 10, 10);
        g.drawLine(x + 10, y + 10, x + 14, y + 14);
        g.drawLine(x + 10, y + 11, x + 14, y + 15);
        g.drawLine(x + 4, y + 6, x + 8, y + 6);
        g.drawLine(x + 6, y + 4, x + 6, y + 8);
    }
}

