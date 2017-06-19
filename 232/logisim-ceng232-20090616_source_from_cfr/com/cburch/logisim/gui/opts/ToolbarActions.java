/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.opts;

import com.cburch.logisim.file.ToolbarData;
import com.cburch.logisim.gui.opts.Strings;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.Tool;
import java.util.List;

class ToolbarActions {
    private ToolbarActions() {
    }

    public static Action addTool(ToolbarData toolbar, Tool tool) {
        return new AddTool(toolbar, tool);
    }

    public static Action removeTool(ToolbarData toolbar, int pos) {
        return new RemoveTool(toolbar, pos);
    }

    public static Action moveTool(ToolbarData toolbar, int src, int dest) {
        return new MoveTool(toolbar, src, dest);
    }

    public static Action addSeparator(ToolbarData toolbar, int pos) {
        return new AddSeparator(toolbar, pos);
    }

    public static Action removeSeparator(ToolbarData toolbar, int pos) {
        return new RemoveSeparator(toolbar, pos);
    }

    private static class RemoveSeparator
    extends Action {
        ToolbarData toolbar;
        int pos;

        RemoveSeparator(ToolbarData toolbar, int pos) {
            this.toolbar = toolbar;
            this.pos = pos;
        }

        @Override
        public String getName() {
            return Strings.get("toolbarRemoveSepAction");
        }

        @Override
        public void doIt(Project proj) {
            this.toolbar.remove(this.pos);
        }

        @Override
        public void undo(Project proj) {
            this.toolbar.addSeparator(this.pos);
        }
    }

    private static class AddSeparator
    extends Action {
        ToolbarData toolbar;
        int pos;

        AddSeparator(ToolbarData toolbar, int pos) {
            this.toolbar = toolbar;
            this.pos = pos;
        }

        @Override
        public String getName() {
            return Strings.get("toolbarInsertSepAction");
        }

        @Override
        public void doIt(Project proj) {
            this.toolbar.addSeparator(this.pos);
        }

        @Override
        public void undo(Project proj) {
            this.toolbar.remove(this.pos);
        }
    }

    private static class MoveTool
    extends Action {
        ToolbarData toolbar;
        int oldpos;
        int dest;

        MoveTool(ToolbarData toolbar, int oldpos, int dest) {
            this.toolbar = toolbar;
            this.oldpos = oldpos;
            this.dest = dest;
        }

        @Override
        public String getName() {
            return Strings.get("toolbarMoveAction");
        }

        @Override
        public void doIt(Project proj) {
            this.toolbar.move(this.oldpos, this.dest);
        }

        @Override
        public void undo(Project proj) {
            this.toolbar.move(this.dest, this.oldpos);
        }

        @Override
        public boolean shouldAppendTo(Action other) {
            if (other instanceof MoveTool) {
                MoveTool o = (MoveTool)other;
                return this.toolbar == o.toolbar && o.dest == this.oldpos;
            }
            return false;
        }

        @Override
        public Action append(Action other) {
            if (other instanceof MoveTool) {
                MoveTool o = (MoveTool)other;
                if (this.toolbar == o.toolbar && this.dest == o.oldpos) {
                    return new MoveTool(this.toolbar, this.oldpos, o.dest);
                }
            }
            return super.append(other);
        }
    }

    private static class RemoveTool
    extends Action {
        ToolbarData toolbar;
        Object removed;
        int which;

        RemoveTool(ToolbarData toolbar, int which) {
            this.toolbar = toolbar;
            this.which = which;
        }

        @Override
        public String getName() {
            return Strings.get("toolbarRemoveAction");
        }

        @Override
        public void doIt(Project proj) {
            this.removed = this.toolbar.remove(this.which);
        }

        @Override
        public void undo(Project proj) {
            if (this.removed instanceof Tool) {
                this.toolbar.addTool(this.which, (Tool)this.removed);
            } else if (this.removed instanceof ToolbarData.Separator) {
                this.toolbar.addSeparator(this.which);
            }
        }
    }

    private static class AddTool
    extends Action {
        ToolbarData toolbar;
        Tool tool;
        int pos;

        AddTool(ToolbarData toolbar, Tool tool) {
            this.toolbar = toolbar;
            this.tool = tool;
        }

        @Override
        public String getName() {
            return Strings.get("toolbarAddAction");
        }

        @Override
        public void doIt(Project proj) {
            this.pos = this.toolbar.getContents().size();
            this.toolbar.addTool(this.pos, this.tool);
        }

        @Override
        public void undo(Project proj) {
            this.toolbar.remove(this.pos);
        }
    }

}

