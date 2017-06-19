/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.apple.eawt.Application
 *  com.apple.eawt.ApplicationAdapter
 *  com.apple.eawt.ApplicationEvent
 *  com.apple.eawt.ApplicationListener
 */
package com.cburch.logisim.gui.start;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationListener;
import com.cburch.logisim.gui.prefs.PreferencesFrame;
import com.cburch.logisim.gui.start.About;
import com.cburch.logisim.gui.start.Startup;
import com.cburch.logisim.proj.ProjectActions;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import net.roydesign.event.ApplicationEvent;
import net.roydesign.mac.MRJAdapter;

class MacOsAdapter
extends ApplicationAdapter {
    MacOsAdapter() {
    }

    static void addListeners(boolean added) {
        MyListener myListener = new MyListener();
        if (!added) {
            MRJAdapter.addOpenDocumentListener(myListener);
        }
        if (!added) {
            MRJAdapter.addPrintDocumentListener(myListener);
        }
        MRJAdapter.addPreferencesListener(myListener);
        MRJAdapter.addQuitApplicationListener(myListener);
        MRJAdapter.addAboutListener(myListener);
    }

    public void handleOpenFile(com.apple.eawt.ApplicationEvent event) {
        Startup.doOpen(new File(event.getFilename()));
    }

    public void handlePrintFile(com.apple.eawt.ApplicationEvent event) {
        Startup.doPrint(new File(event.getFilename()));
    }

    public void handlePreferences(com.apple.eawt.ApplicationEvent event) {
        PreferencesFrame.showPreferences();
    }

    public static void register() {
        Application.getApplication().addApplicationListener((ApplicationListener)new MacOsAdapter());
    }

    private static class MyListener
    implements ActionListener {
        private MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            ApplicationEvent event2 = (ApplicationEvent)event;
            int type = event2.getType();
            switch (type) {
                case 1: {
                    About.showAboutDialog(null);
                    break;
                }
                case 4: {
                    ProjectActions.doQuit();
                    break;
                }
                case 5: {
                    Startup.doOpen(event2.getFile());
                    break;
                }
                case 6: {
                    Startup.doPrint(event2.getFile());
                    break;
                }
                case 2: {
                    PreferencesFrame.showPreferences();
                }
            }
        }
    }

}

