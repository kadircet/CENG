/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.file;

import com.cburch.logisim.file.LoadFailedException;
import com.cburch.logisim.file.LoadedLibrary;
import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.LoaderException;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.ProjectsDirty;
import com.cburch.logisim.file.Strings;
import com.cburch.logisim.std.Builtin;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.util.StringUtil;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import javax.swing.filechooser.FileFilter;

class LibraryManager {
    public static final LibraryManager instance = new LibraryManager();
    private static char desc_sep = 35;
    private HashMap fileMap = new HashMap();
    private WeakHashMap invMap = new WeakHashMap();

    private LibraryManager() {
        ProjectsDirty.initialize();
    }

    void setDirty(File file, boolean dirty) {
        LoadedLibrary lib = this.findKnown(file);
        if (lib != null) {
            lib.setDirty(dirty);
        }
    }

    Collection getLogisimLibraries() {
        ArrayList<Library> ret = new ArrayList<Library>();
        for (LoadedLibrary lib : this.invMap.keySet()) {
            if (!(lib.getBase() instanceof LogisimFile)) continue;
            ret.add(lib.getBase());
        }
        return ret;
    }

    public Library loadLibrary(Loader loader, String desc) {
        int sep = desc.indexOf(desc_sep);
        if (sep < 0) {
            loader.showError(StringUtil.format(Strings.get("fileDescriptorError"), desc));
            return null;
        }
        String type = desc.substring(0, sep);
        String name = desc.substring(sep + 1);
        if (type.equals("")) {
            Library ret = loader.getBuiltin().getLibrary(name);
            if (ret == null) {
                loader.showError(StringUtil.format(Strings.get("fileBuiltinMissingError"), name));
                return null;
            }
            return ret;
        }
        if (type.equals("file")) {
            File toRead = loader.getFileFor(name, Loader.LOGISIM_FILTER);
            return this.loadLogisimLibrary(loader, toRead);
        }
        if (type.equals("jar")) {
            int sepLoc = name.lastIndexOf(desc_sep);
            String fileName = name.substring(0, sepLoc);
            String className = name.substring(sepLoc + 1);
            File toRead = loader.getFileFor(fileName, Loader.JAR_FILTER);
            return this.loadJarLibrary(loader, toRead, className);
        }
        loader.showError(StringUtil.format(Strings.get("fileTypeError"), type, desc));
        return null;
    }

    public LoadedLibrary loadLogisimLibrary(Loader loader, File toRead) {
        LoadedLibrary ret = this.findKnown(toRead);
        if (ret != null) {
            return ret;
        }
        try {
            ret = new LoadedLibrary(loader.loadLogisimFile(toRead));
        }
        catch (LoadFailedException e) {
            loader.showError(e.getMessage());
            return null;
        }
        this.fileMap.put(toRead, new WeakReference<LoadedLibrary>(ret));
        this.invMap.put(ret, toRead);
        return ret;
    }

    public LoadedLibrary loadJarLibrary(Loader loader, File toRead, String className) {
        JarDescriptor jarDescriptor = new JarDescriptor(toRead, className);
        LoadedLibrary ret = this.findKnown(jarDescriptor);
        if (ret != null) {
            return ret;
        }
        try {
            ret = new LoadedLibrary(loader.loadJarFile(toRead, className));
        }
        catch (LoadFailedException e) {
            loader.showError(e.getMessage());
            return null;
        }
        this.fileMap.put(jarDescriptor, new WeakReference<LoadedLibrary>(ret));
        this.invMap.put(ret, jarDescriptor);
        return ret;
    }

    public void reload(Loader loader, LoadedLibrary lib) {
        Object descriptor = this.invMap.get(lib);
        if (descriptor == null) {
            loader.showError(StringUtil.format(Strings.get("unknownLibraryFileError"), lib.getDisplayName()));
            return;
        }
        try {
            if (descriptor instanceof JarDescriptor) {
                JarDescriptor jarDesc = (JarDescriptor)descriptor;
                lib.setBase(loader.loadJarFile(jarDesc.file, jarDesc.className));
            } else {
                File file = (File)descriptor;
                lib.setBase(loader.loadLogisimFile(file));
            }
        }
        catch (LoadFailedException e) {
            loader.showError(e.getMessage());
            return;
        }
    }

    public Library findReference(LogisimFile file, File query) {
        for (Library lib : file.getLibraries()) {
            JarDescriptor jarDesc;
            Library ret;
            LoadedLibrary loadedLib;
            Object desc = this.invMap.get(lib);
            if (desc != null && (desc instanceof JarDescriptor ? query.equals((jarDesc = (JarDescriptor)desc).file) : query.equals(desc))) {
                return lib;
            }
            if (!(lib instanceof LoadedLibrary) || !((loadedLib = (LoadedLibrary)lib).getBase() instanceof LogisimFile) || (ret = this.findReference((LogisimFile)loadedLib.getBase(), query)) == null) continue;
            return lib;
        }
        return null;
    }

    public void fileSaved(Loader loader, File dest, File oldFile, LogisimFile file) {
        LoadedLibrary lib;
        LoadedLibrary old = this.findKnown(oldFile);
        if (old != null) {
            old.setDirty(false);
        }
        if ((lib = this.findKnown(dest)) != null) {
            LogisimFile clone = file.cloneLogisimFile(loader);
            clone.setName(file.getName());
            clone.setDirty(false);
            lib.setBase(clone);
        }
    }

    public String getDescriptor(Loader loader, Library lib) {
        if (loader.getBuiltin().getLibraries().contains(lib)) {
            return "" + desc_sep + lib.getName();
        }
        Object desc = this.invMap.get(lib);
        if (desc instanceof JarDescriptor) {
            JarDescriptor jarDesc = (JarDescriptor)desc;
            return "jar#" + this.toRelative(loader, jarDesc.file) + desc_sep + jarDesc.className;
        }
        if (desc instanceof File) {
            File file = (File)desc;
            return "file#" + this.toRelative(loader, file);
        }
        throw new LoaderException(StringUtil.format(Strings.get("fileDescriptorUnknownError"), lib.getDisplayName()));
    }

    private LoadedLibrary findKnown(Object key) {
        WeakReference retLibRef = (WeakReference)this.fileMap.get(key);
        if (retLibRef == null) {
            return null;
        }
        LoadedLibrary retLib = (LoadedLibrary)retLibRef.get();
        if (retLib == null) {
            this.fileMap.remove(key);
            return null;
        }
        return retLib;
    }

    private String toRelative(Loader loader, File file) {
        File currentDirectory = loader.getCurrentDirectory();
        if (currentDirectory == null) {
            try {
                return file.getCanonicalPath();
            }
            catch (IOException e) {
                return file.toString();
            }
        }
        File fileDir = file.getParentFile();
        if (fileDir != null) {
            if (currentDirectory.equals(fileDir)) {
                return file.getName();
            }
            if (currentDirectory.equals(fileDir.getParentFile())) {
                return fileDir.getName() + "/" + file.getName();
            }
            if (fileDir.equals(currentDirectory.getParentFile())) {
                return "../" + file.getName();
            }
        }
        try {
            return file.getCanonicalPath();
        }
        catch (IOException e) {
            return file.toString();
        }
    }

    private static class JarDescriptor {
        private File file;
        private String className;

        JarDescriptor(File file, String className) {
            this.file = file;
            this.className = className;
        }

        public boolean equals(Object other) {
            if (!(other instanceof JarDescriptor)) {
                return false;
            }
            JarDescriptor o = (JarDescriptor)other;
            return this.file.equals(o.file) && this.className.equals(o.className);
        }

        public int hashCode() {
            return this.file.hashCode() * 31 + this.className.hashCode();
        }
    }

}

