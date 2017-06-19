/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.file;

import com.cburch.logisim.file.LibraryEvent;
import com.cburch.logisim.file.LibraryListener;
import com.cburch.logisim.file.LibraryManager;
import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.Projects;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

class ProjectsDirty {
    private static ProjectListListener projectListListener = new ProjectListListener();
    private static ArrayList listeners = new ArrayList();

    private ProjectsDirty() {
    }

    public static void initialize() {
        Projects.addPropertyChangeListener("projectList", projectListListener);
    }

    private static class ProjectListListener
    implements PropertyChangeListener {
        private ProjectListListener() {
        }

        @Override
        public synchronized void propertyChange(PropertyChangeEvent event) {
            DirtyListener l;
            int n = listeners.size();
            for (int i = 0; i < n; ++i) {
                l = (DirtyListener)listeners.get(i);
                l.proj.removeLibraryListener(l);
            }
            listeners.clear();
            for (Project proj : Projects.getOpenProjects()) {
                l = new DirtyListener(proj);
                proj.addLibraryListener(l);
                listeners.add(l);
                LogisimFile lib = proj.getLogisimFile();
                LibraryManager.instance.setDirty(lib.getLoader().getMainFile(), lib.isDirty());
            }
        }
    }

    private static class DirtyListener
    implements LibraryListener {
        Project proj;

        DirtyListener(Project proj) {
            this.proj = proj;
        }

        @Override
        public void libraryChanged(LibraryEvent event) {
            if (event.getAction() == 6) {
                LogisimFile lib = this.proj.getLogisimFile();
                File file = lib.getLoader().getMainFile();
                LibraryManager.instance.setDirty(file, lib.isDirty());
            }
        }
    }

}

