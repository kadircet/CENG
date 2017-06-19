/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.proj;

import com.cburch.logisim.file.LoadFailedException;
import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.start.SplashScreen;
import com.cburch.logisim.proj.LogisimPreferences;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.Projects;
import com.cburch.logisim.proj.Strings;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.StringUtil;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class ProjectActions {
    private ProjectActions() {
    }

    public static Project doNew(SplashScreen monitor) {
        if (monitor != null) {
            monitor.setProgress(6);
        }
        Loader loader = new Loader(monitor);
        LogisimFile file = LogisimPreferences.getTemplate(loader).cloneLogisimFile(loader);
        return ProjectActions.completeProject(monitor, loader, file);
    }

    private static Project completeProject(SplashScreen monitor, Loader loader, LogisimFile file) {
        if (monitor != null) {
            monitor.setProgress(8);
        }
        Project ret = new Project(file);
        if (monitor != null) {
            monitor.setProgress(9);
        }
        Frame frame = new Frame(ret);
        ret.setFrame(frame);
        frame.setVisible(true);
        frame.toFront();
        frame.getCanvas().grabFocus();
        loader.setParent(frame);
        return ret;
    }

    public static Project doNew(Project baseProject) {
        Loader loader = new Loader(baseProject == null ? null : baseProject.getFrame());
        LogisimFile template = LogisimPreferences.getTemplate(loader);
        Project newProj = new Project(template.cloneLogisimFile(loader));
        Frame frame = new Frame(newProj);
        newProj.setFrame(frame);
        frame.setVisible(true);
        frame.getCanvas().grabFocus();
        newProj.getLogisimFile().getLoader().setParent(frame);
        return newProj;
    }

    public static Project doOpen(SplashScreen monitor, File source) throws LoadFailedException {
        if (monitor != null) {
            monitor.setProgress(7);
        }
        Loader loader = new Loader(monitor);
        LogisimFile file = loader.openLogisimFile(source);
        return ProjectActions.completeProject(monitor, loader, file);
    }

    public static void doOpen(Component parent, Project baseProject) {
        JFileChooser chooser;
        if (baseProject != null) {
            Loader oldLoader = baseProject.getLogisimFile().getLoader();
            chooser = oldLoader.createChooser();
            if (oldLoader.getMainFile() != null) {
                chooser.setSelectedFile(oldLoader.getMainFile());
            }
        } else {
            chooser = new JFileChooser();
        }
        chooser.setFileFilter(Loader.LOGISIM_FILTER);
        int returnVal = chooser.showOpenDialog(parent);
        if (returnVal != 0) {
            return;
        }
        ProjectActions.doOpen(parent, baseProject, chooser.getSelectedFile());
    }

    public static Project doOpen(Component parent, Project baseProject, File f) {
        Project proj = Projects.findProjectFor(f);
        Loader loader = null;
        if (proj != null) {
            proj.getFrame().toFront();
            loader = proj.getLogisimFile().getLoader();
            if (proj.isFileDirty()) {
                String message = StringUtil.format(Strings.get("openAlreadyMessage"), proj.getLogisimFile().getName());
                Object[] options = new String[]{Strings.get("openAlreadyLoseChangesOption"), Strings.get("openAlreadyNewWindowOption"), Strings.get("openAlreadyCancelOption")};
                int result = JOptionPane.showOptionDialog(proj.getFrame(), message, Strings.get("openAlreadyTitle"), 0, 3, null, options, options[2]);
                if (result != 0) {
                    if (result == 1) {
                        proj = null;
                    } else {
                        return proj;
                    }
                }
            }
        }
        if (proj == null && baseProject != null && baseProject.isStartupScreen()) {
            proj = baseProject;
            proj.setStartupScreen(false);
            loader = baseProject.getLogisimFile().getLoader();
        } else {
            loader = new Loader(baseProject == null ? parent : baseProject.getFrame());
        }
        try {
            LogisimFile lib = loader.openLogisimFile(f);
            if (lib == null) {
                return null;
            }
            if (proj == null) {
                proj = new Project(lib);
            } else {
                proj.setLogisimFile(lib);
            }
        }
        catch (LoadFailedException ex) {
            JOptionPane.showMessageDialog(parent, StringUtil.format(Strings.get("fileOpenError"), ex.toString()), Strings.get("fileOpenErrorTitle"), 0);
            return null;
        }
        Frame frame = proj.getFrame();
        if (frame == null) {
            frame = new Frame(proj);
            proj.setFrame(frame);
        }
        frame.setVisible(true);
        frame.toFront();
        frame.getCanvas().grabFocus();
        proj.getLogisimFile().getLoader().setParent(frame);
        return proj;
    }

    public static boolean doSaveAs(Project proj) {
        int confirm;
        int returnVal;
        Loader loader = proj.getLogisimFile().getLoader();
        JFileChooser chooser = loader.createChooser();
        chooser.setFileFilter(Loader.LOGISIM_FILTER);
        if (loader.getMainFile() != null) {
            chooser.setSelectedFile(loader.getMainFile());
        }
        if ((returnVal = chooser.showSaveDialog(proj.getFrame())) != 0) {
            return false;
        }
        File f = chooser.getSelectedFile();
        if (!f.getName().endsWith(".circ")) {
            String oldName = f.getName();
            int extStart = oldName.indexOf(46);
            if (extStart < 0) {
                f = new File(f.getParentFile(), oldName + ".circ");
            } else {
                String extension = oldName.substring(extStart);
                int action = JOptionPane.showConfirmDialog(proj.getFrame(), StringUtil.format(Strings.get("replaceExtensionMessage"), extension), Strings.get("replaceExtensionTitle"), 0, 3);
                if (action == 0) {
                    f = new File(f.getParentFile(), oldName.substring(0, extStart) + ".circ");
                }
            }
        }
        if (f.exists() && (confirm = JOptionPane.showConfirmDialog(proj.getFrame(), Strings.get("confirmOverwriteMessage"), Strings.get("confirmOverwriteTitle"), 0)) != 0) {
            return false;
        }
        return ProjectActions.doSave(proj, f);
    }

    public static boolean doSave(Project proj) {
        Loader loader = proj.getLogisimFile().getLoader();
        File f = loader.getMainFile();
        if (f == null) {
            return ProjectActions.doSaveAs(proj);
        }
        return ProjectActions.doSave(proj, f);
    }

    private static boolean doSave(Project proj, File f) {
        Loader loader = proj.getLogisimFile().getLoader();
        Tool oldTool = proj.getTool();
        proj.setTool(null);
        boolean ret = loader.save(proj.getLogisimFile(), f);
        if (ret) {
            proj.setFileAsClean();
        }
        proj.setTool(oldTool);
        return ret;
    }

    public static void doQuit() {
        ArrayList toClose = new ArrayList(Projects.getOpenProjects());
        for (Project proj : toClose) {
            if (proj.confirmClose(Strings.get("confirmQuitTitle"))) continue;
            return;
        }
        System.exit(0);
    }
}

