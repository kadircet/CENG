/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.ui;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.util.Properties;
import net.roydesign.mac.MRJAdapter;

public class FolderDialog
extends FileDialog {
    private boolean modeCheckingEnabled = false;

    public FolderDialog(Frame parent) {
        this(parent, "");
    }

    public FolderDialog(Frame parent, String title) {
        super(parent, title, FolderDialog.getInitialMode());
        if (MRJAdapter.mrjVersion == -1.0f) {
            this.setFile("-");
        }
        this.modeCheckingEnabled = true;
    }

    public String getFile() {
        return super.getFile() != null ? "" : null;
    }

    public String getDirectory() {
        String path = super.getDirectory();
        if (path == null) {
            return null;
        }
        if (MRJAdapter.mrjVersion >= 3.0f && super.getFile() != null) {
            return new File(path, super.getFile()).getPath();
        }
        return path;
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
            prop = "apple.awt.fileDialogForDirectories";
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

    private static int getInitialMode() {
        if (MRJAdapter.mrjVersion >= 4.0f) {
            return 0;
        }
        if (MRJAdapter.mrjVersion != -1.0f) {
            return 3;
        }
        return 1;
    }
}

