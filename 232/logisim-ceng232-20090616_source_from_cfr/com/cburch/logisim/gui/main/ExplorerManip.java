/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.file.LibraryEvent;
import com.cburch.logisim.file.LibraryEventSource;
import com.cburch.logisim.file.LibraryListener;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.LogisimFileActions;
import com.cburch.logisim.gui.main.Explorer;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.menu.Popups;
import com.cburch.logisim.gui.menu.ProjectCircuitActions;
import com.cburch.logisim.gui.menu.ProjectLibraryActions;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.ProjectEvent;
import com.cburch.logisim.proj.ProjectListener;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

class ExplorerManip
implements Explorer.Listener {
    private Project proj;
    private Explorer explorer;
    private MyListener myListener;
    private Tool lastSelected;

    ExplorerManip(Project proj, Explorer explorer) {
        this.myListener = new MyListener();
        this.lastSelected = null;
        this.proj = proj;
        this.explorer = explorer;
        proj.addProjectListener(this.myListener);
        this.myListener.setFile(null, proj.getLogisimFile());
    }

    @Override
    public void selectionChanged(Explorer.Event event) {
        Object selected = event.getTarget();
        if (selected instanceof Tool) {
            this.lastSelected = this.proj.getTool();
            Tool tool = (Tool)selected;
            this.proj.setTool(tool);
            this.proj.getFrame().viewAttributes(tool);
        }
    }

    @Override
    public void doubleClicked(Explorer.Event event) {
        AddTool tool;
        ComponentFactory source;
        Object clicked = event.getTarget();
        if (clicked instanceof AddTool && (source = (tool = (AddTool)clicked).getFactory()) instanceof Circuit) {
            Circuit circ = (Circuit)source;
            this.proj.setCurrentCircuit(circ);
            if (this.lastSelected != null) {
                this.proj.setTool(this.lastSelected);
            }
        }
    }

    @Override
    public void moveRequested(Explorer.Event event, AddTool dragged, AddTool target) {
        LogisimFile file = this.proj.getLogisimFile();
        int draggedIndex = file.getTools().indexOf(dragged);
        int targetIndex = file.getTools().indexOf(target);
        if (targetIndex > draggedIndex) {
            ++targetIndex;
        }
        this.proj.doAction(LogisimFileActions.moveCircuit(dragged, targetIndex));
    }

    @Override
    public void deleteRequested(Explorer.Event event) {
        ComponentFactory factory;
        Object request = event.getTarget();
        if (request instanceof Library) {
            ProjectLibraryActions.doUnloadLibrary(this.proj, (Library)request);
        } else if (request instanceof AddTool && (factory = ((AddTool)request).getFactory()) instanceof Circuit) {
            ProjectCircuitActions.doRemoveCircuit(this.proj, (Circuit)factory);
        }
    }

    @Override
    public JPopupMenu menuRequested(Explorer.Event event) {
        Object clicked = event.getTarget();
        if (clicked instanceof AddTool) {
            AddTool tool = (AddTool)clicked;
            ComponentFactory source = tool.getFactory();
            if (source instanceof Circuit) {
                Circuit circ = (Circuit)source;
                return Popups.forCircuit(this.proj, tool, circ);
            }
            return null;
        }
        if (clicked instanceof Tool) {
            return null;
        }
        if (clicked == this.proj.getLogisimFile()) {
            return Popups.forProject(this.proj);
        }
        if (clicked instanceof Library) {
            boolean is_top = event.getTreePath().getPathCount() <= 2;
            return Popups.forLibrary(this.proj, (Library)clicked, is_top);
        }
        return null;
    }

    private class MyListener
    implements ProjectListener,
    LibraryListener,
    AttributeListener {
        private LogisimFile curFile;

        private MyListener() {
            this.curFile = null;
        }

        @Override
        public void projectChanged(ProjectEvent event) {
            int action = event.getAction();
            if (action == 0) {
                this.setFile((LogisimFile)event.getOldData(), (LogisimFile)event.getData());
                ExplorerManip.this.explorer.repaint();
            }
        }

        private void setFile(LogisimFile oldFile, LogisimFile newFile) {
            Iterator it;
            if (oldFile != null) {
                this.removeLibrary(oldFile);
                it = oldFile.getLibraries().iterator();
                while (it.hasNext()) {
                    this.removeLibrary((Library)it.next());
                }
            }
            this.curFile = newFile;
            if (newFile != null) {
                this.addLibrary(newFile);
                it = newFile.getLibraries().iterator();
                while (it.hasNext()) {
                    this.addLibrary((Library)it.next());
                }
            }
        }

        @Override
        public void libraryChanged(LibraryEvent event) {
            Tool tool;
            AttributeSet attrs;
            int action = event.getAction();
            if (action == 2) {
                if (event.getSource() == this.curFile) {
                    this.addLibrary((Library)event.getData());
                }
            } else if (action == 3) {
                if (event.getSource() == this.curFile) {
                    this.removeLibrary((Library)event.getData());
                }
            } else if (action == 0) {
                Tool tool2 = (Tool)event.getData();
                AttributeSet attrs2 = tool2.getAttributeSet();
                if (attrs2 != null) {
                    attrs2.addAttributeListener(this);
                }
            } else if (action == 1 && (attrs = (tool = (Tool)event.getData()).getAttributeSet()) != null) {
                attrs.removeAttributeListener(this);
            }
            ExplorerManip.this.explorer.repaint();
        }

        private void addLibrary(Library lib) {
            if (lib instanceof LibraryEventSource) {
                ((LibraryEventSource)((Object)lib)).addLibraryListener(this);
            }
            for (Tool tool : lib.getTools()) {
                AttributeSet attrs = tool.getAttributeSet();
                if (attrs == null) continue;
                attrs.addAttributeListener(this);
            }
        }

        private void removeLibrary(Library lib) {
            if (lib instanceof LibraryEventSource) {
                ((LibraryEventSource)((Object)lib)).removeLibraryListener(this);
            }
            for (Tool tool : lib.getTools()) {
                AttributeSet attrs = tool.getAttributeSet();
                if (attrs == null) continue;
                attrs.removeAttributeListener(this);
            }
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            ExplorerManip.this.explorer.repaint();
        }
    }

}

