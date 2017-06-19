/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.ui;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;
import net.roydesign.io.ApplicationFile;
import net.roydesign.mac.MRJAdapter;

public class ApplicationDialog
extends FileDialog {
    private boolean modeCheckingEnabled = false;

    public ApplicationDialog(Frame parent) {
        this(parent, "");
    }

    public ApplicationDialog(Frame parent, String title) {
        super(parent, title, 0);
        this.setFilenameFilter(new ApplicationFilter());
        this.modeCheckingEnabled = true;
    }

    public ApplicationFile getApplicationFile() {
        String f = this.getFile();
        return f != null ? new ApplicationFile(this.getDirectory(), f) : null;
    }

    public void setMode(int mode) {
        if (this.modeCheckingEnabled) {
            throw new Error("can't set mode");
        }
        super.setMode(mode);
    }

    public void show() {
        String prop = null;
        if (MRJAdapter.mrjVersion >= 4.0f) {
            prop = "apple.awt.use-file-dialog-packages";
        } else if (MRJAdapter.mrjVersion >= 3.0f) {
            prop = "com.apple.macos.use-file-dialog-packages";
        }
        Properties props = System.getProperties();
        Object oldValue = null;
        if (prop != null) {
            oldValue = props.get(prop);
            props.put(prop, "true");
        }
        super.show();
        if (prop != null) {
            if (oldValue == null) {
                props.remove(prop);
            } else {
                props.put(prop, oldValue);
            }
        }
    }

    private class ApplicationFilter
    implements FilenameFilter {
        ApplicationFilter() {
        }

        public boolean accept(File directory, String name) {
            try {
                if (MRJAdapter.mrjVersion != -1.0f) {
                    return MRJAdapter.getFileType(new File(directory, name)).equals("APPL");
                }
            }
            catch (IOException var3_3) {
                // empty catch block
            }
            return true;
        }
    }

}

