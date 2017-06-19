/*
 * Decompiled with CFR 0_114.
 */
package javax.help.event;

import java.util.EventListener;
import javax.help.event.HelpSetEvent;

public interface HelpSetListener
extends EventListener {
    public void helpSetAdded(HelpSetEvent var1);

    public void helpSetRemoved(HelpSetEvent var1);
}

