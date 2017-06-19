/*
 * Decompiled with CFR 0_114.
 */
package javax.help.event;

import java.util.EventObject;

public class HelpHistoryModelEvent
extends EventObject {
    private boolean next;
    private boolean previous;

    public HelpHistoryModelEvent(Object object, boolean bl, boolean bl2) {
        super(object);
        this.next = bl2;
        this.previous = bl;
    }

    public boolean isPrevious() {
        return this.previous;
    }

    public boolean isNext() {
        return this.next;
    }
}

