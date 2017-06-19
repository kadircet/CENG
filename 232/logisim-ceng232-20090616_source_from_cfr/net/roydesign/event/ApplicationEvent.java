/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.event;

import java.awt.event.ActionEvent;
import java.io.File;

public class ApplicationEvent
extends ActionEvent {
    public static final int ABOUT = 1;
    public static final int PREFERENCES = 2;
    public static final int OPEN_APPLICATION = 3;
    public static final int QUIT_APPLICATION = 4;
    public static final int OPEN_DOCUMENT = 5;
    public static final int PRINT_DOCUMENT = 6;
    public static final int REOPEN_APPLICATION = 7;
    private int type;
    private File file;

    public ApplicationEvent(Object source, int type) {
        this(source, type, (File)null);
    }

    public ApplicationEvent(Object source, int type, String actionCommand) {
        this(source, type, null, actionCommand);
    }

    public ApplicationEvent(Object source, int type, File file) {
        this(source, type, file, "");
    }

    public ApplicationEvent(Object source, int type, File file, String actionCommand) {
        super(source, 1001, actionCommand, 0);
        switch (type) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 7: {
                if (file == null) break;
                throw new IllegalArgumentException("adapter event ID can't include a file");
            }
            case 5: 
            case 6: {
                if (file != null) break;
                throw new IllegalArgumentException("adapter event ID requires a file");
            }
        }
        this.type = type;
        this.file = file;
    }

    public int getType() {
        return this.type;
    }

    public File getFile() {
        return this.file;
    }
}

