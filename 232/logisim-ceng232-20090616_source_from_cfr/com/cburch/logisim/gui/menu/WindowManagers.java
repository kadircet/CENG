/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.menu;

import com.cburch.logisim.analyze.gui.AnalyzerManager;
import com.cburch.logisim.file.LibraryEvent;
import com.cburch.logisim.file.LibraryListener;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.ProjectEvent;
import com.cburch.logisim.proj.ProjectListener;
import com.cburch.logisim.proj.Projects;
import com.cburch.logisim.util.WindowMenuItemManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;

public class WindowManagers {
    private static boolean initialized = false;
    private static MyListener myListener = new MyListener();
    private static HashMap projectMap = new LinkedHashMap();

    private WindowManagers() {
    }

    public static void initialize() {
        if (!initialized) {
            initialized = true;
            AnalyzerManager.initialize();
            Projects.addPropertyChangeListener("projectList", myListener);
            WindowManagers.computeListeners();
        }
    }

    private static void computeListeners() {
        List nowOpen = Projects.getOpenProjects();
        HashSet closed = new HashSet(projectMap.keySet());
        closed.removeAll(nowOpen);
        for (Project proj : closed) {
            ProjectManager manager = (ProjectManager)projectMap.get(proj);
            manager.frameClosed(manager.getJFrame(false));
            projectMap.remove(proj);
        }
        LinkedHashSet opened = new LinkedHashSet(nowOpen);
        opened.removeAll(projectMap.keySet());
        for (Project proj2 : opened) {
            ProjectManager manager = new ProjectManager(proj2);
            projectMap.put(proj2, manager);
        }
    }

    private static class ProjectManager
    extends WindowMenuItemManager
    implements ProjectListener,
    LibraryListener {
        private Project proj;

        ProjectManager(Project proj) {
            super(proj.getLogisimFile().getName(), false);
            this.proj = proj;
            proj.addProjectListener(this);
            proj.addLibraryListener(this);
            this.frameOpened(proj.getFrame());
        }

        @Override
        public JFrame getJFrame(boolean create) {
            return this.proj.getFrame();
        }

        @Override
        public void projectChanged(ProjectEvent event) {
            if (event.getAction() == 0) {
                this.setText(this.proj.getLogisimFile().getName());
            }
        }

        @Override
        public void libraryChanged(LibraryEvent event) {
            if (event.getAction() == 5) {
                this.setText((String)event.getData());
            }
        }
    }

    private static class MyListener
    implements PropertyChangeListener {
        private MyListener() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            WindowManagers.computeListeners();
        }
    }

}

