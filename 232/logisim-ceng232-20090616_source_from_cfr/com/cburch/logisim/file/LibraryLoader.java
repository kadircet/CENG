/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.file;

import com.cburch.logisim.tools.Library;

interface LibraryLoader {
    public Library loadLibrary(String var1);

    public String getDescriptor(Library var1);

    public void showError(String var1);
}

