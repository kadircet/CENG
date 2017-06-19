/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.proj;

import com.cburch.logisim.circuit.Simulator;
import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.ProjectActions;
import com.cburch.logisim.util.MacCompatibility;
import com.cburch.logisim.util.PropertyChangeWeakSupport;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Projects {
    public static final String projectListProperty = "projectList";
    private static final MyListener myListener = new MyListener();
    private static final PropertyChangeWeakSupport propertySupport = new PropertyChangeWeakSupport(Projects.class);
    private static ArrayList openProjects = new ArrayList();

    private static void projectRemoved(Project proj, Frame frame, MyListener listener) {
        frame.removeWindowListener(listener);
        openProjects.remove(proj);
        proj.getSimulator().shutDown();
        propertySupport.firePropertyChange("projectList", null, null);
    }

    private Projects() {
    }

    static void windowCreated(Project proj, Frame oldFrame, Frame frame) {
        if (oldFrame != null) {
            Projects.projectRemoved(proj, oldFrame, myListener);
        }
        if (frame == null) {
            return;
        }
        Point lowest = null;
        for (int i = 0; i < openProjects.size(); ++i) {
            Project p = (Project)openProjects.get(i);
            Frame f = p.getFrame();
            if (f == null) continue;
            Point loc = p.getFrame().getLocation();
            if (lowest != null && loc.y <= lowest.y) continue;
            lowest = loc;
        }
        if (lowest != null) {
            Dimension sz = frame.getToolkit().getScreenSize();
            int x = Math.min(lowest.x + 20, sz.width - 200);
            int y = Math.min(lowest.y + 20, sz.height - 200);
            if (x < 0) {
                x = 0;
            }
            if (y < 0) {
                y = 0;
            }
            frame.setLocation(x, y);
        }
        if (frame.isVisible() && !openProjects.contains(proj)) {
            openProjects.add(proj);
            propertySupport.firePropertyChange("projectList", null, null);
        }
        frame.addWindowListener(myListener);
    }

    public static List getOpenProjects() {
        return Collections.unmodifiableList(openProjects);
    }

    public static boolean windowNamed(String name) {
        for (int i = 0; i < openProjects.size(); ++i) {
            Project proj = (Project)openProjects.get(i);
            if (!proj.getLogisimFile().getName().equals(name)) continue;
            return true;
        }
        return false;
    }

    public static Project findProjectFor(File query) {
        for (int i = Projects.openProjects.size() - 1; i >= 0; --i) {
            File f;
            Project proj = (Project)openProjects.get(i);
            Loader loader = proj.getLogisimFile().getLoader();
            if (loader == null || !query.equals(f = loader.getMainFile())) continue;
            return proj;
        }
        return null;
    }

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public static void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    public static void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    public static void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }

    private static class MyListener
    extends WindowAdapter {
        private MyListener() {
        }

        @Override
        public void windowClosed(WindowEvent event) {
            Project proj;
            Frame frame = (Frame)event.getSource();
            if (frame == (proj = frame.getProject()).getFrame()) {
                Projects.projectRemoved(proj, frame, this);
            }
            if (openProjects.isEmpty() && !MacCompatibility.isSwingUsingScreenMenuBar()) {
                ProjectActions.doQuit();
            }
        }

        @Override
        public void windowOpened(WindowEvent event) {
            Project proj;
            Frame frame = (Frame)event.getSource();
            if (frame == (proj = frame.getProject()).getFrame() && !openProjects.contains(proj)) {
                openProjects.add(proj);
                propertySupport.firePropertyChange("projectList", null, null);
            }
        }
    }

}

