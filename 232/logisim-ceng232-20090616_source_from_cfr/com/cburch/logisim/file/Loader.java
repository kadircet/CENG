/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.file;

import com.cburch.logisim.file.LibraryLoader;
import com.cburch.logisim.file.LibraryManager;
import com.cburch.logisim.file.LoadFailedException;
import com.cburch.logisim.file.LoadedLibrary;
import com.cburch.logisim.file.LoaderException;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.Strings;
import com.cburch.logisim.std.Builtin;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.util.MacCompatibility;
import com.cburch.logisim.util.StringUtil;
import java.awt.Component;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Stack;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class Loader
implements LibraryLoader {
    public static final String LOGISIM_EXTENSION = ".circ";
    public static final FileFilter LOGISIM_FILTER = new LogisimFileFilter();
    public static final FileFilter JAR_FILTER = new JarFileFilter();
    private Component parent;
    private Builtin builtin = new Builtin();
    private File mainFile = null;
    private Stack filesOpening = new Stack();

    public Loader(Component parent) {
        this.parent = parent;
        this.clear();
    }

    public Builtin getBuiltin() {
        return this.builtin;
    }

    public void setParent(Component value) {
        this.parent = value;
    }

    public File getMainFile() {
        return this.mainFile;
    }

    public JFileChooser createChooser() {
        JFileChooser ret = new JFileChooser();
        ret.setCurrentDirectory(this.getCurrentDirectory());
        return ret;
    }

    File getCurrentDirectory() {
        File ref = !this.filesOpening.empty() ? (File)this.filesOpening.peek() : this.mainFile;
        return ref == null ? null : ref.getParentFile();
    }

    private void setMainFile(File value) {
        this.mainFile = value;
    }

    public void clear() {
        this.filesOpening.clear();
        this.mainFile = null;
    }

    public LogisimFile openLogisimFile(File file) throws LoadFailedException {
        try {
            LogisimFile ret = this.loadLogisimFile(file);
            if (ret != null) {
                this.setMainFile(file);
            }
            this.showMessages(ret);
            return ret;
        }
        catch (LoaderException e) {
            throw new LoadFailedException(e.getMessage());
        }
    }

    public LogisimFile openLogisimFile(Reader reader) throws LoadFailedException, IOException {
        LogisimFile ret = null;
        try {
            ret = LogisimFile.load(reader, this);
        }
        catch (LoaderException e) {
            return null;
        }
        this.showMessages(ret);
        return ret;
    }

    public Library loadLogisimLibrary(File file) {
        LoadedLibrary ret = LibraryManager.instance.loadLogisimLibrary(this, file);
        if (ret != null) {
            LogisimFile retBase = (LogisimFile)ret.getBase();
            this.showMessages(retBase);
        }
        return ret;
    }

    public Library loadJarLibrary(File file, String className) {
        return LibraryManager.instance.loadJarLibrary(this, file, className);
    }

    public void reload(LoadedLibrary lib) {
        LibraryManager.instance.reload(this, lib);
    }

    public boolean save(LogisimFile file, File dest) {
        Library reference = LibraryManager.instance.findReference(file, dest);
        if (reference != null) {
            JOptionPane.showMessageDialog(this.parent, StringUtil.format(Strings.get("fileCircularError"), reference.getDisplayName()), Strings.get("fileSaveErrorTitle"), 0);
            return false;
        }
        OutputStreamWriter fwrite = null;
        try {
            try {
                MacCompatibility.setFileCreatorAndType(dest, "LGSM", "circ");
            }
            catch (IOException e) {
                // empty catch block
            }
            fwrite = new FileWriter(dest);
            file.write(fwrite, this);
            file.setName(this.toProjectName(dest));
            File oldFile = this.getMainFile();
            this.setMainFile(dest);
            LibraryManager.instance.fileSaved(this, dest, oldFile, file);
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(this.parent, StringUtil.format(Strings.get("fileSaveError"), e.toString()), Strings.get("fileSaveErrorTitle"), 0);
            boolean bl = false;
            return bl;
        }
        finally {
            if (fwrite != null) {
                try {
                    fwrite.close();
                }
                catch (IOException e) {
                    JOptionPane.showMessageDialog(this.parent, StringUtil.format(Strings.get("fileSaveCloseError"), e.toString()), Strings.get("fileSaveErrorTitle"), 0);
                    return false;
                }
            }
        }
        return true;
    }

    LogisimFile loadLogisimFile(File file) throws LoadFailedException {
        for (int i = 0; i < this.filesOpening.size(); ++i) {
            if (!this.filesOpening.get(i).equals(file)) continue;
            throw new LoadFailedException(StringUtil.format(Strings.get("logisimCircularError"), this.toProjectName(file)));
        }
        FileReader reader = null;
        LogisimFile ret = null;
        this.filesOpening.push(file);
        try {
            reader = new FileReader(file);
            ret = LogisimFile.load(reader, this);
        }
        catch (IOException e) {
            throw new LoadFailedException(StringUtil.format(Strings.get("logisimLoadError"), this.toProjectName(file), e.toString()));
        }
        finally {
            this.filesOpening.pop();
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    throw new LoadFailedException(StringUtil.format(Strings.get("logisimLoadError"), this.toProjectName(file), e.toString()));
                }
            }
        }
        ret.setName(this.toProjectName(file));
        return ret;
    }

    Library loadJarFile(File file, String className) throws LoadFailedException {
        Class retClass;
        Library ret;
        URL url;
        try {
            url = new URL("file", "localhost", file.getCanonicalPath());
        }
        catch (MalformedURLException e1) {
            throw new LoadFailedException("Internal error: Malformed URL");
        }
        catch (IOException e1) {
            throw new LoadFailedException(Strings.get("jarNotOpenedError"));
        }
        URLClassLoader loader = new URLClassLoader(new URL[]{url});
        try {
            retClass = loader.loadClass(className);
        }
        catch (ClassNotFoundException e) {
            throw new LoadFailedException(StringUtil.format(Strings.get("jarClassNotFoundError"), className));
        }
        if (!Library.class.isAssignableFrom(retClass)) {
            throw new LoadFailedException(StringUtil.format(Strings.get("jarClassNotLibraryError"), className));
        }
        try {
            ret = (Library)retClass.newInstance();
        }
        catch (Exception e) {
            throw new LoadFailedException(StringUtil.format(Strings.get("jarLibraryNotCreatedError"), className));
        }
        return ret;
    }

    @Override
    public Library loadLibrary(String desc) {
        return LibraryManager.instance.loadLibrary(this, desc);
    }

    @Override
    public String getDescriptor(Library lib) {
        return LibraryManager.instance.getDescriptor(this, lib);
    }

    @Override
    public void showError(String description) {
        if (!this.filesOpening.empty()) {
            File top = (File)this.filesOpening.peek();
            description = this.toProjectName(top) + ": " + description;
        }
        JOptionPane.showMessageDialog(this.parent, description, Strings.get("fileErrorTitle"), 0);
        throw new LoaderException(description);
    }

    private void showMessages(LogisimFile source) {
        String message;
        if (source == null) {
            return;
        }
        while ((message = source.getMessage()) != null) {
            JOptionPane.showMessageDialog(this.parent, message, Strings.get("fileMessageTitle"), 1);
        }
    }

    File getFileFor(String name, FileFilter filter) {
        File currentDirectory;
        File file = new File(name);
        if (!file.isAbsolute() && (currentDirectory = this.getCurrentDirectory()) != null) {
            file = new File(currentDirectory, name);
        }
        while (!file.canRead()) {
            JOptionPane.showMessageDialog(this.parent, StringUtil.format(Strings.get("fileLibraryMissingError"), file.getName()));
            JFileChooser chooser = this.createChooser();
            chooser.setFileFilter(filter);
            chooser.setDialogTitle(StringUtil.format(Strings.get("fileLibraryMissingTitle"), file.getName()));
            int action = chooser.showDialog(this.parent, Strings.get("fileLibraryMissingButton"));
            if (action != 0) {
                throw new LoaderException(Strings.get("fileLoadCanceledError"));
            }
            file = chooser.getSelectedFile();
        }
        return file;
    }

    private String toProjectName(File file) {
        String ret = file.getName();
        if (ret.endsWith(".circ")) {
            return ret.substring(0, ret.length() - ".circ".length());
        }
        return ret;
    }

    private static class JarFileFilter
    extends FileFilter {
        private JarFileFilter() {
        }

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".jar");
        }

        @Override
        public String getDescription() {
            return Strings.get("jarFileFilter");
        }
    }

    private static class LogisimFileFilter
    extends FileFilter {
        private LogisimFileFilter() {
        }

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".circ");
        }

        @Override
        public String getDescription() {
            return Strings.get("logisimFileFilter");
        }
    }

}

