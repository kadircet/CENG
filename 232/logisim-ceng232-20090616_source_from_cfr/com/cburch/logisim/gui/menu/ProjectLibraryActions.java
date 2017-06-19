/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.menu;

import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.LogisimFileActions;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.menu.Strings;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.std.Builtin;
import com.cburch.logisim.tools.Library;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

public class ProjectLibraryActions {
    private ProjectLibraryActions() {
    }

    public static void doLoadBuiltinLibrary(Project proj) {
        Library[] libs;
        LogisimFile file = proj.getLogisimFile();
        ArrayList builtins = new ArrayList(file.getLoader().getBuiltin().getLibraries());
        builtins.removeAll(file.getLibraries());
        if (builtins.isEmpty()) {
            JOptionPane.showMessageDialog(proj.getFrame(), Strings.get("loadBuiltinNoneError"), Strings.get("loadBuiltinErrorTitle"), 1);
            return;
        }
        LibraryJList list = new LibraryJList(builtins);
        JScrollPane listPane = new JScrollPane(list);
        int action = JOptionPane.showConfirmDialog(proj.getFrame(), listPane, Strings.get("loadBuiltinDialogTitle"), 2, 3);
        if (action == 0 && (libs = list.getSelectedLibraries()) != null) {
            proj.doAction(LogisimFileActions.loadLibraries(libs));
        }
    }

    public static void doLoadLogisimLibrary(Project proj) {
        File f;
        Library lib;
        Loader loader = proj.getLogisimFile().getLoader();
        JFileChooser chooser = loader.createChooser();
        chooser.setDialogTitle(Strings.get("loadLogisimDialogTitle"));
        chooser.setFileFilter(Loader.LOGISIM_FILTER);
        int check = chooser.showOpenDialog(proj.getFrame());
        if (check == 0 && (lib = loader.loadLogisimLibrary(f = chooser.getSelectedFile())) != null) {
            proj.doAction(LogisimFileActions.loadLibrary(lib));
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static void doLoadJarLibrary(Project proj) {
        loader = proj.getLogisimFile().getLoader();
        chooser = loader.createChooser();
        chooser.setDialogTitle(Strings.get("loadJarDialogTitle"));
        chooser.setFileFilter(Loader.JAR_FILTER);
        check = chooser.showOpenDialog(proj.getFrame());
        if (check != 0) return;
        f = chooser.getSelectedFile();
        className = null;
        jarFile = null;
        try {
            jarFile = new JarFile(f);
            manifest = jarFile.getManifest();
            className = manifest.getMainAttributes().getValue("Library-Class");
            ** if (jarFile == null) goto lbl-1000
        }
        catch (IOException e) {
            if (jarFile != null) {
                try {
                    jarFile.close();
                }
                catch (IOException e) {}
            }
            catch (Throwable var8_11) {
                if (jarFile == null) throw var8_11;
                try {
                    jarFile.close();
                    throw var8_11;
                }
                catch (IOException e) {
                    // empty catch block
                }
                throw var8_11;
            }
        }
lbl-1000: // 1 sources:
        {
            try {
                jarFile.close();
            }
            catch (IOException e) {}
        }
lbl-1000: // 2 sources:
        {
        }
        if (className == null && (className = JOptionPane.showInputDialog(proj.getFrame(), Strings.get("jarClassNamePrompt"), Strings.get("jarClassNameTitle"), 3)) == null) {
            return;
        }
        lib = loader.loadJarLibrary(f, className);
        if (lib == null) return;
        proj.doAction(LogisimFileActions.loadLibrary(lib));
    }

    public static void doUnloadLibraries(Project proj) {
        Library[] libs;
        LogisimFile file = proj.getLogisimFile();
        ArrayList<Library> canUnload = new ArrayList<Library>();
        for (Library lib : file.getLibraries()) {
            String message = file.getUnloadLibraryMessage(lib);
            if (message != null) continue;
            canUnload.add(lib);
        }
        if (canUnload.isEmpty()) {
            JOptionPane.showMessageDialog(proj.getFrame(), Strings.get("unloadNoneError"), Strings.get("unloadErrorTitle"), 1);
            return;
        }
        LibraryJList list = new LibraryJList(canUnload);
        JScrollPane listPane = new JScrollPane(list);
        int action = JOptionPane.showConfirmDialog(proj.getFrame(), listPane, Strings.get("unloadLibrariesDialogTitle"), 2, 3);
        if (action == 0 && (libs = list.getSelectedLibraries()) != null) {
            proj.doAction(LogisimFileActions.unloadLibraries(libs));
        }
    }

    public static void doUnloadLibrary(Project proj, Library lib) {
        String message = proj.getLogisimFile().getUnloadLibraryMessage(lib);
        if (message != null) {
            JOptionPane.showMessageDialog(proj.getFrame(), message, Strings.get("unloadErrorTitle"), 0);
        } else {
            proj.doAction(LogisimFileActions.unloadLibrary(lib));
        }
    }

    private static class LibraryJList
    extends JList {
        LibraryJList(List libraries) {
            ArrayList<BuiltinOption> options = new ArrayList<BuiltinOption>();
            for (Library lib : libraries) {
                options.add(new BuiltinOption(lib));
            }
            this.setListData(options.toArray());
        }

        Library[] getSelectedLibraries() {
            Object[] selected = this.getSelectedValues();
            if (selected != null && selected.length > 0) {
                Library[] libs = new Library[selected.length];
                for (int i = 0; i < selected.length; ++i) {
                    libs[i] = ((BuiltinOption)selected[i]).lib;
                }
                return libs;
            }
            return null;
        }
    }

    private static class BuiltinOption {
        Library lib;

        BuiltinOption(Library lib) {
            this.lib = lib;
        }

        public String toString() {
            return this.lib.getDisplayName();
        }
    }

}

