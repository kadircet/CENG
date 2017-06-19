/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.opts;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.file.MouseMappings;
import com.cburch.logisim.gui.opts.Strings;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.StringUtil;

class OptionsActions {
    private OptionsActions() {
    }

    public static Action setAttribute(AttributeSet attrs, Attribute attr, Object value) {
        Object oldValue = attrs.getValue(attr);
        if (!oldValue.equals(value)) {
            return new SetAction(attrs, attr, value);
        }
        return null;
    }

    public static Action setMapping(MouseMappings mm, Integer mods, Tool tool) {
        return new SetMapping(mm, mods, tool);
    }

    public static Action removeMapping(MouseMappings mm, Integer mods) {
        return new RemoveMapping(mm, mods);
    }

    private static class RemoveMapping
    extends Action {
        MouseMappings mm;
        Integer mods;
        Tool oldtool;

        RemoveMapping(MouseMappings mm, Integer mods) {
            this.mm = mm;
            this.mods = mods;
        }

        @Override
        public String getName() {
            return Strings.get("removeMouseMappingAction");
        }

        @Override
        public void doIt(Project proj) {
            this.oldtool = this.mm.getToolFor(this.mods);
            this.mm.setToolFor(this.mods, (Tool)null);
        }

        @Override
        public void undo(Project proj) {
            this.mm.setToolFor(this.mods, this.oldtool);
        }
    }

    private static class SetMapping
    extends Action {
        MouseMappings mm;
        Integer mods;
        Tool oldtool;
        Tool tool;

        SetMapping(MouseMappings mm, Integer mods, Tool tool) {
            this.mm = mm;
            this.mods = mods;
            this.tool = tool;
        }

        @Override
        public String getName() {
            return Strings.get("addMouseMappingAction");
        }

        @Override
        public void doIt(Project proj) {
            this.oldtool = this.mm.getToolFor(this.mods);
            this.mm.setToolFor(this.mods, this.tool);
        }

        @Override
        public void undo(Project proj) {
            this.mm.setToolFor(this.mods, this.oldtool);
        }
    }

    private static class SetAction
    extends Action {
        private AttributeSet attrs;
        private Attribute attr;
        private Object newval;
        private Object oldval;

        SetAction(AttributeSet attrs, Attribute attr, Object value) {
            this.attrs = attrs;
            this.attr = attr;
            this.newval = value;
        }

        @Override
        public String getName() {
            return StringUtil.format(Strings.get("setOptionAction"), this.attr.getDisplayName());
        }

        @Override
        public void doIt(Project proj) {
            this.oldval = this.attrs.getValue(this.attr);
            this.attrs.setValue(this.attr, this.newval);
        }

        @Override
        public void undo(Project proj) {
            this.attrs.setValue(this.attr, this.oldval);
        }
    }

}

