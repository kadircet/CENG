/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.gui.main.AttributeTable;
import com.cburch.logisim.gui.main.Strings;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.Tool;

public class ToolActions {
    private ToolActions() {
    }

    public static AttributeTable.Listener createTableListener(Project proj, Tool tool) {
        return new AttributeListener(proj, tool);
    }

    public static Action setAttributeValue(Tool tool, Attribute attr, Object value) {
        return new SetAttributeValue(tool, attr, value);
    }

    private static class SetAttributeValue
    extends Action {
        private Tool tool;
        private Attribute attr;
        private Object newval;
        private Object oldval;

        SetAttributeValue(Tool tool, Attribute attr, Object value) {
            this.tool = tool;
            this.attr = attr;
            this.newval = value;
        }

        @Override
        public String getName() {
            return Strings.get("changeToolAttrAction");
        }

        @Override
        public void doIt(Project proj) {
            AttributeSet attrs = this.tool.getAttributeSet();
            this.oldval = attrs.getValue(this.attr);
            attrs.setValue(this.attr, this.newval);
        }

        @Override
        public void undo(Project proj) {
            AttributeSet attrs = this.tool.getAttributeSet();
            attrs.setValue(this.attr, this.oldval);
        }
    }

    private static class AttributeListener
    implements AttributeTable.Listener {
        Project proj;
        Tool tool;

        AttributeListener(Project proj, Tool tool) {
            this.proj = proj;
            this.tool = tool;
        }

        @Override
        public void valueChangeRequested(Attribute attr, Object value) {
            this.proj.doAction(ToolActions.setAttributeValue(this.tool, attr, value));
        }
    }

}

