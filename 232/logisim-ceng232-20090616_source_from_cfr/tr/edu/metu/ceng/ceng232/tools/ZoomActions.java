/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.tools;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.StringUtil;

class ZoomActions {
    private ZoomActions() {
    }

    public static Action setZoom(AttributeSet attrs, Attribute attr, Object value) {
        Object oldValue = attrs.getValue(attr);
        if (!oldValue.equals(value)) {
            return new ZoomAction(attrs, attr, value);
        }
        return null;
    }

    private static class ZoomAction
    extends Action {
        private AttributeSet attrs;
        private Attribute attr;
        private Object newval;
        private Object oldval;

        ZoomAction(AttributeSet attrs, Attribute attr, Object value) {
            this.attrs = attrs;
            this.attr = attr;
            this.newval = value;
        }

        @Override
        public String getName() {
            return StringUtil.format("Zoom Action", this.attr.getDisplayName());
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

