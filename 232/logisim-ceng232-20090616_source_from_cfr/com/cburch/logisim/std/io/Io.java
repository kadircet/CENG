/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.io;

import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeOption;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.std.io.Button;
import com.cburch.logisim.std.io.Led;
import com.cburch.logisim.std.io.SevenSegment;
import com.cburch.logisim.std.io.Strings;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.util.StringGetter;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;

public class Io
extends Library {
    static final AttributeOption LABEL_CENTER = new AttributeOption("center", "center", Strings.getter("ioLabelCenter"));
    static final Font DEFAULT_LABEL_FONT = new Font("SansSerif", 0, 12);
    static final Attribute ATTR_FACING = Attributes.forDirection("facing", Strings.getter("ioFacingAttr"));
    static final Attribute ATTR_COLOR = Attributes.forColor("color", Strings.getter("ioColorAttr"));
    static final Attribute ATTR_LABEL = Attributes.forString("label", Strings.getter("ioLabelAttr"));
    static final Attribute ATTR_LABEL_LOC = Attributes.forOption("labelloc", Strings.getter("ioLabelLocAttr"), new Object[]{LABEL_CENTER, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST});
    static final Attribute ATTR_LABEL_FONT = Attributes.forFont("labelfont", Strings.getter("ioLabelFontAttr"));
    static final Attribute ATTR_LABEL_COLOR = Attributes.forColor("labelcolor", Strings.getter("ioLabelColorAttr"));
    private List tools = null;

    @Override
    public String getName() {
        return "I/O";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("ioLibrary");
    }

    @Override
    public List getTools() {
        if (this.tools == null) {
            this.tools = Arrays.asList(new AddTool(Button.factory), new AddTool(Led.factory), new AddTool(SevenSegment.factory));
        }
        return this.tools;
    }
}

