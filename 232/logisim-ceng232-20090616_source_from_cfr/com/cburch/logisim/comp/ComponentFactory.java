/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.comp;

import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import java.awt.Color;

public interface ComponentFactory {
    public static final Object SHOULD_SNAP = new Object();
    public static final Object TOOL_TIP = new Object();
    public static final Object FACING_ATTRIBUTE_KEY = new Object();

    public String getName();

    public String getDisplayName();

    public Component createComponent(Location var1, AttributeSet var2);

    public Bounds getOffsetBounds(AttributeSet var1);

    public AttributeSet createAttributeSet();

    public void drawGhost(ComponentDrawContext var1, Color var2, int var3, int var4, AttributeSet var5);

    public void paintIcon(ComponentDrawContext var1, int var2, int var3, AttributeSet var4);

    public Object getFeature(Object var1, AttributeSet var2);
}

