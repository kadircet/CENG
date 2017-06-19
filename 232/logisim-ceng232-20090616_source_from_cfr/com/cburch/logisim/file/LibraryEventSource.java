/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.file;

import com.cburch.logisim.file.LibraryListener;

public interface LibraryEventSource {
    public void addLibraryListener(LibraryListener var1);

    public void removeLibraryListener(LibraryListener var1);
}

