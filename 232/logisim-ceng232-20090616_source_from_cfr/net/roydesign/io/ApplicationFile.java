/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import net.roydesign.io.DocumentFile;
import net.roydesign.mac.MRJAdapter;

public class ApplicationFile {
    private static final String osName = System.getProperty("os.name");
    File executable;

    public ApplicationFile(String path) {
        this.executable = new File(path);
    }

    public ApplicationFile(String parent, String child) {
        this.executable = new File(parent, child);
    }

    public ApplicationFile(File parent, String child) {
        this.executable = new File(parent, child);
    }

    public ApplicationFile(File executable) {
        this(executable.getPath());
    }

    public boolean open() throws IOException {
        block13 : {
            if (MRJAdapter.mrjVersion >= 3.0f) {
                try {
                    Process p = Runtime.getRuntime().exec(new String[]{"open", "-a", this.executable.getAbsolutePath()});
                    if (p.waitFor() != 0) {
                        return false;
                    }
                    break block13;
                }
                catch (InterruptedException e) {
                    return false;
                }
            }
            if (MRJAdapter.mrjVersion != -1.0f) {
                Runtime.getRuntime().exec(new String[]{this.executable.getAbsolutePath()});
            } else {
                if (osName.startsWith("Windows")) {
                    try {
                        Process p = Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "\"\"", this.executable.getAbsolutePath()});
                        if (p.waitFor() != 0) {
                            return false;
                        }
                        break block13;
                    }
                    catch (InterruptedException e) {
                        return false;
                    }
                }
                try {
                    Process p = Runtime.getRuntime().exec(new String[]{this.executable.getAbsolutePath()});
                    if (p.waitFor() != 0) {
                        return false;
                    }
                }
                catch (InterruptedException e) {
                    return false;
                }
            }
        }
        return true;
    }

    public Process open(String[] args) throws IOException {
        String[] nargs = new String[args.length + 1];
        nargs[0] = this.executable.getAbsolutePath();
        System.arraycopy(args, 0, nargs, 1, args.length);
        return Runtime.getRuntime().exec(nargs);
    }

    public boolean openDocument(DocumentFile documentFile) throws IOException {
        return this.openDocument(documentFile.file);
    }

    public boolean openDocument(File file) throws IOException {
        return this.openDocuments(new File[]{file});
    }

    public boolean openDocuments(DocumentFile[] documentFiles) throws IOException {
        File[] files = new File[documentFiles.length];
        int i = 0;
        while (i < files.length) {
            files[i] = documentFiles[i].file;
            ++i;
        }
        return this.openDocuments(files);
    }

    public boolean openDocuments(File[] files) throws IOException {
        block17 : {
            if (MRJAdapter.mrjVersion >= 3.0f) {
                try {
                    String[] strs = new String[3 + files.length];
                    strs[0] = "open";
                    strs[1] = "-a";
                    strs[2] = this.executable.getAbsolutePath();
                    int i = 0;
                    while (i < files.length) {
                        strs[3 + i] = files[i].getAbsolutePath();
                        ++i;
                    }
                    Process p = Runtime.getRuntime().exec(strs);
                    if (p.waitFor() != 0) {
                        return false;
                    }
                    break block17;
                }
                catch (InterruptedException e) {
                    return false;
                }
            }
            if (MRJAdapter.mrjVersion != -1.0f) {
                String[] strs = new String[1 + files.length];
                strs[0] = this.executable.getAbsolutePath();
                int i = 0;
                while (i < files.length) {
                    strs[1 + i] = files[i].getAbsolutePath();
                    ++i;
                }
                Runtime.getRuntime().exec(strs);
            } else {
                if (osName.startsWith("Windows")) {
                    try {
                        String[] strs = new String[5 + files.length];
                        strs[0] = "cmd";
                        strs[1] = "/c";
                        strs[2] = "start";
                        strs[3] = "\"\"";
                        strs[4] = this.executable.getAbsolutePath();
                        int i = 0;
                        while (i < files.length) {
                            strs[5 + i] = files[i].getAbsolutePath();
                            ++i;
                        }
                        Process p = Runtime.getRuntime().exec(strs);
                        if (p.waitFor() != 0) {
                            return false;
                        }
                        break block17;
                    }
                    catch (InterruptedException e) {
                        return false;
                    }
                }
                try {
                    String[] strs = new String[1 + files.length];
                    strs[0] = this.executable.getAbsolutePath();
                    int i = 0;
                    while (i < files.length) {
                        strs[1 + i] = files[i].getAbsolutePath();
                        ++i;
                    }
                    Process p = Runtime.getRuntime().exec(strs);
                    if (p.waitFor() != 0) {
                        return false;
                    }
                }
                catch (InterruptedException e) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getPath() {
        return this.executable.getPath();
    }

    public String getAbsolutePath() {
        return this.executable.getAbsolutePath();
    }

    public String getCanonicalPath() throws IOException {
        return this.executable.getCanonicalPath();
    }

    public String getExecutableName() {
        return this.executable.getName();
    }

    public String getDisplayedName() throws IOException {
        if (MRJAdapter.mrjVersion != -1.0f) {
            if (this.executable.isDirectory()) {
                String name;
                File f = new File(this.executable, "Contents/MRJApp.properties");
                if (f.exists() && (name = MRJAdapter.parseMRJAppProperties(f, "com.apple.mrj.application.apple.menu.about.name")) != null) {
                    return name;
                }
                f = new File(this.executable, "Contents/Info.plist");
                if (f.exists()) {
                    name = MRJAdapter.parseInfoPlist(f, "com.apple.mrj.application.apple.menu.about.name");
                    if (name == null && (name = MRJAdapter.parseInfoPlist(f, "CFBundleName")) == null) {
                        name = MRJAdapter.parseInfoPlist(f, "CFBundleExecutable");
                    }
                    return name;
                }
            }
        } else {
            osName.startsWith("Windows");
        }
        return this.getExecutableName();
    }

    public String getMacCreator() throws IOException {
        return MRJAdapter.getFileCreator(this.executable);
    }

    public File getMacBundleResource(String resource) throws FileNotFoundException {
        return MRJAdapter.getBundleResource(resource);
    }

    public File getMacBundleResource(String resource, String subFolder) throws FileNotFoundException {
        return MRJAdapter.getBundleResource(resource, subFolder);
    }
}

