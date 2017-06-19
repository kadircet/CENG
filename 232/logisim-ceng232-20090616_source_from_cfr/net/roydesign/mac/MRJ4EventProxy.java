/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.apple.eawt.Application
 *  com.apple.eawt.ApplicationAdapter
 *  com.apple.eawt.ApplicationEvent
 *  com.apple.eawt.ApplicationListener
 */
package net.roydesign.mac;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;
import java.io.File;
import net.roydesign.mac.MRJEventProxy;

class MRJ4EventProxy
extends MRJEventProxy {
    private static MRJ4EventProxy instance;
    private Application application = new Application();

    public static MRJ4EventProxy getInstance() {
        if (instance == null) {
            instance = new MRJ4EventProxy();
        }
        return instance;
    }

    private MRJ4EventProxy() {
        this.application.addApplicationListener((ApplicationListener)new Handler());
    }

    public boolean isPreferencesEnabled() {
        return this.application.getEnabledPreferencesMenu();
    }

    public void setPreferencesEnabled(boolean enabled) {
        if (enabled != this.application.getEnabledPreferencesMenu()) {
            this.application.setEnabledPreferencesMenu(enabled);
        }
    }

    private class Handler
    extends ApplicationAdapter {
        Handler() {
        }

        public void handleAbout(ApplicationEvent e) {
            MRJ4EventProxy.this.fireMenuEvent(1);
            e.setHandled(true);
        }

        public void handlePreferences(ApplicationEvent e) {
            MRJ4EventProxy.this.fireMenuEvent(2);
            e.setHandled(true);
        }

        public void handleOpenApplication(ApplicationEvent e) {
            MRJ4EventProxy.this.fireApplicationEvent(3);
            e.setHandled(true);
        }

        public void handleReOpenApplication(ApplicationEvent e) {
            MRJ4EventProxy.this.fireApplicationEvent(7);
            e.setHandled(true);
        }

        public void handleQuit(ApplicationEvent e) {
            MRJ4EventProxy.this.fireApplicationEvent(4);
        }

        public void handleOpenFile(ApplicationEvent e) {
            MRJ4EventProxy.this.fireDocumentEvent(5, new File(e.getFilename()));
            e.setHandled(true);
        }

        public void handlePrintFile(ApplicationEvent e) {
            MRJ4EventProxy.this.fireDocumentEvent(6, new File(e.getFilename()));
            e.setHandled(true);
        }
    }

}

