/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.io;

import java.io.File;
import java.io.IOException;
import net.roydesign.io.ApplicationFile;
import net.roydesign.mac.MRJAdapter;

public class DocumentFile {
    private static final String osName = System.getProperty("os.name");
    File file;
    private String macCreator = "";
    private String macType = "";

    public DocumentFile(String path) {
        this.file = new File(path);
    }

    public DocumentFile(String parent, String child) {
        this.file = new File(parent, child);
    }

    public DocumentFile(File parent, String child) {
        this.file = new File(parent, child);
    }

    public DocumentFile(File file) {
        this(file.getPath());
    }

    public boolean open() throws IOException {
        block11 : {
            if (MRJAdapter.mrjVersion >= 3.0f) {
                try {
                    Process p = Runtime.getRuntime().exec(new String[]{"open", this.file.getAbsolutePath()});
                    if (p.waitFor() != 0) {
                        return false;
                    }
                    break block11;
                }
                catch (InterruptedException e) {
                    return false;
                }
            }
            if (MRJAdapter.mrjVersion != -1.0f) {
                File app = MRJAdapter.findApplication(MRJAdapter.getFileCreator(this.file));
                File folder = new File(app.getParent());
                String n = folder.getName();
                if ((n.equals("MacOS") || n.equals("MacOSClassic")) && (folder = new File(folder.getParent())).getName().equals("Contents") && new File(folder, "Info.plist").exists() && (folder = new File(folder.getParent())).getName().endsWith(".app")) {
                    app = folder;
                }
                Runtime.getRuntime().exec(new String[]{app.getAbsolutePath(), this.file.getAbsolutePath()});
            } else {
                if (osName.startsWith("Windows")) {
                    try {
                        Process p = Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "\"\"", this.file.getAbsolutePath()});
                        if (p.waitFor() != 0) {
                            return false;
                        }
                        break block11;
                    }
                    catch (InterruptedException e) {
                        return false;
                    }
                }
                return false;
            }
        }
        return true;
    }

    public boolean openWith(ApplicationFile application) throws IOException {
        return application.openDocument(this);
    }

    public boolean openWith(File application) throws IOException {
        return this.openWith(new ApplicationFile(application));
    }

    public void setMacType(String type) throws IOException {
        this.macType = type;
        MRJAdapter.setFileType(this.file, type);
    }

    public String getMacType() throws IOException {
        String t = MRJAdapter.getFileType(this.file);
        if (t.length() == 0 && this.macType != null) {
            return this.macType;
        }
        return t;
    }

    public void setMacCreator(String creator) throws IOException {
        this.macCreator = creator;
        MRJAdapter.setFileCreator(this.file, creator);
    }

    public String getMacCreator() throws IOException {
        String c = MRJAdapter.getFileCreator(this.file);
        if (c.length() == 0 && this.macCreator != null) {
            return this.macCreator;
        }
        return c;
    }

    public void setMacCreatorAndType(String creator, String type) throws IOException {
        this.macCreator = creator;
        this.macType = type;
        MRJAdapter.setFileCreatorAndType(this.file, creator, type);
    }

    public void setExtension(String extension) throws IOException {
        File f;
        StringBuffer b = new StringBuffer();
        b.append(this.getTitle());
        if (extension != null && extension.length() > 0) {
            b.append('.');
            b.append(extension);
        }
        if (!this.file.renameTo(f = new File(this.file.getParent(), b.toString()))) {
            throw new IOException("failed to rename file");
        }
        this.file = f;
    }

    public String getExtension() throws IOException {
        String n = this.file.getName();
        int pos = n.lastIndexOf(46);
        if (pos != -1 && pos + 1 != n.length()) {
            return n.substring(pos + 1);
        }
        return "";
    }

    public void setTitle(String title) throws IOException {
        File f;
        if (title == null || title.length() == 0) {
            throw new IllegalArgumentException("title can't be null or zero length");
        }
        StringBuffer b = new StringBuffer();
        b.append(title);
        String ext = this.getExtension();
        if (ext != null && ext.length() > 0) {
            b.append('.');
            b.append(ext);
        }
        if (!this.file.renameTo(f = new File(this.file.getParent(), b.toString()))) {
            throw new IOException("failed to rename file");
        }
        this.file = f;
    }

    public String getTitle() throws IOException {
        String n = this.file.getName();
        int pos = n.lastIndexOf(46);
        if (pos != -1 && pos != 0 && pos + 1 != n.length()) {
            return n.substring(0, pos);
        }
        return n;
    }

    public void setTitleAndExtension(String title, String extension) throws IOException {
        File f;
        if (title == null || title.length() == 0) {
            throw new IllegalArgumentException("title can't be null or zero length");
        }
        StringBuffer b = new StringBuffer();
        b.append(title);
        if (extension != null && extension.length() > 0) {
            b.append('.');
            b.append(extension);
        }
        if (!this.file.renameTo(f = new File(this.file.getParent(), b.toString()))) {
            throw new IOException("failed to rename file");
        }
        this.file = f;
    }

    public File getFile() {
        return this.file;
    }

    public String getPath() {
        return this.file.getPath();
    }

    public String getAbsolutePath() {
        return this.file.getAbsolutePath();
    }

    public String getCanonicalPath() throws IOException {
        return this.file.getCanonicalPath();
    }
}

