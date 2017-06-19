/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.proj;

import com.cburch.logisim.proj.Project;

public abstract class Action {
    public boolean isModification() {
        return true;
    }

    public abstract String getName();

    public abstract void doIt(Project var1);

    public abstract void undo(Project var1);

    public boolean shouldAppendTo(Action other) {
        return false;
    }

    public Action append(Action other) {
        return new UnionAction(this, other);
    }

    private static class UnionAction
    extends Action {
        Action first;
        Action second;

        UnionAction(Action first, Action second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean isModification() {
            return this.first.isModification() || this.second.isModification();
        }

        @Override
        public String getName() {
            return this.first.getName();
        }

        @Override
        public void doIt(Project proj) {
            this.first.doIt(proj);
            this.second.doIt(proj);
        }

        @Override
        public void undo(Project proj) {
            this.second.undo(proj);
            this.first.undo(proj);
        }
    }

}

