/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.apple.mrj.MRJAboutHandler
 *  com.apple.mrj.MRJApplicationUtils
 *  com.apple.mrj.MRJOpenApplicationHandler
 *  com.apple.mrj.MRJOpenDocumentHandler
 *  com.apple.mrj.MRJPrefsHandler
 *  com.apple.mrj.MRJPrintDocumentHandler
 *  com.apple.mrj.MRJQuitHandler
 */
package net.roydesign.mac;

import com.apple.mrj.MRJAboutHandler;
import com.apple.mrj.MRJApplicationUtils;
import com.apple.mrj.MRJOpenApplicationHandler;
import com.apple.mrj.MRJOpenDocumentHandler;
import com.apple.mrj.MRJPrefsHandler;
import com.apple.mrj.MRJPrintDocumentHandler;
import com.apple.mrj.MRJQuitHandler;
import java.io.File;
import net.roydesign.mac.JD3CarbonFunctions;
import net.roydesign.mac.MRJAdapter;
import net.roydesign.mac.MRJEventProxy;

class MRJ23EventProxy
extends MRJEventProxy {
    private static final int kCoreEventClass = 1634039412;
    private static final int kPreferencesItem = 1886545254;
    private static final int kReopenApplicationEvent = 1918988400;
    private static MRJ23EventProxy instance;
    private Object preferencesHandler;
    private Object reopenApplicationHandler;
    private boolean preferencesEnabled;

    public static MRJ23EventProxy getInstance() {
        if (instance == null) {
            instance = new MRJ23EventProxy();
        }
        return instance;
    }

    private MRJ23EventProxy() {
        throw new Error("Unresolved compilation problems: \n\tThe method getProc() is undefined for the type JD3AppleEventHandlerThunk\n\tThe method getProc() is undefined for the type JD3AppleEventHandlerThunk\n\tThe method getProc() is undefined for the type JD2AppleEventHandlerThunk\n");
    }

    public boolean isPreferencesEnabled() {
        return this.preferencesEnabled;
    }

    public void setPreferencesEnabled(boolean enabled) {
        if (enabled != this.preferencesEnabled) {
            if (MRJAdapter.mrjVersion >= 3.2f) {
                if (enabled) {
                    MRJApplicationUtils.registerPrefsHandler((MRJPrefsHandler)((MRJPrefsHandler)this.preferencesHandler));
                } else {
                    MRJApplicationUtils.registerPrefsHandler((MRJPrefsHandler)null);
                }
            } else if (MRJAdapter.mrjVersion >= 3.0f) {
                if (enabled) {
                    JD3CarbonFunctions.EnableMenuCommand(0, 1886545254);
                } else {
                    JD3CarbonFunctions.DisableMenuCommand(0, 1886545254);
                }
            }
            this.preferencesEnabled = enabled;
        }
    }

    private class Handler
    implements MRJAboutHandler,
    MRJOpenApplicationHandler,
    MRJOpenDocumentHandler,
    MRJPrintDocumentHandler,
    MRJQuitHandler {
        Handler() {
        }

        public void handleAbout() {
            MRJ23EventProxy.this.fireMenuEvent(1);
        }

        public void handleOpenApplication() {
            MRJ23EventProxy.this.fireApplicationEvent(3);
        }

        public void handleQuit() {
            MRJ23EventProxy.this.fireApplicationEvent(4);
            if (MRJAdapter.mrjVersion >= 3.0f) {
                throw new IllegalStateException();
            }
        }

        public void handleOpenFile(File file) {
            MRJ23EventProxy.this.fireDocumentEvent(5, file);
        }

        public void handlePrintFile(File file) {
            MRJ23EventProxy.this.fireDocumentEvent(6, file);
        }
    }

}

