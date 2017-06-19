/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.file;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.file.LibraryEvent;
import com.cburch.logisim.file.LibraryEventSource;
import com.cburch.logisim.file.LibraryListener;
import com.cburch.logisim.file.LibraryManager;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.MouseMappings;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.file.ToolbarData;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.Projects;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.EventSourceWeakSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LoadedLibrary
extends Library
implements LibraryEventSource {
    private Library base;
    private boolean dirty = false;
    private MyListener myListener;
    private EventSourceWeakSupport listeners;

    LoadedLibrary(Library base) {
        this.myListener = new MyListener();
        this.listeners = new EventSourceWeakSupport();
        while (base instanceof LoadedLibrary) {
            base = ((LoadedLibrary)base).base;
        }
        this.base = base;
        if (base instanceof LibraryEventSource) {
            ((LibraryEventSource)((Object)base)).addLibraryListener(this.myListener);
        }
    }

    @Override
    public void addLibraryListener(LibraryListener l) {
        this.listeners.add(l);
    }

    @Override
    public void removeLibraryListener(LibraryListener l) {
        this.listeners.remove(l);
    }

    @Override
    public String getName() {
        return this.base.getName();
    }

    @Override
    public String getDisplayName() {
        return this.base.getDisplayName();
    }

    @Override
    public boolean isDirty() {
        return this.dirty || this.base.isDirty();
    }

    @Override
    public List getTools() {
        return this.base.getTools();
    }

    @Override
    public List getLibraries() {
        return this.base.getLibraries();
    }

    void setDirty(boolean value) {
        if (this.dirty != value) {
            this.dirty = value;
            this.fireLibraryEvent(6, this.isDirty() ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    Library getBase() {
        return this.base;
    }

    void setBase(Library value) {
        if (this.base instanceof LibraryEventSource) {
            ((LibraryEventSource)((Object)this.base)).removeLibraryListener(this.myListener);
        }
        Library old = this.base;
        this.base = value;
        this.resolveChanges(old);
        if (this.base instanceof LibraryEventSource) {
            ((LibraryEventSource)((Object)this.base)).addLibraryListener(this.myListener);
        }
    }

    private void fireLibraryEvent(int action, Object data) {
        this.fireLibraryEvent(new LibraryEvent(this, action, data));
    }

    private void fireLibraryEvent(LibraryEvent event) {
        if (event.getSource() != this) {
            event = new LibraryEvent(this, event.getAction(), event.getData());
        }
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            LibraryListener l = (LibraryListener)it.next();
            l.libraryChanged(event);
        }
    }

    private void resolveChanges(Library old) {
        if (this.listeners.size() == 0) {
            return;
        }
        if (!this.base.getDisplayName().equals(old.getDisplayName())) {
            this.fireLibraryEvent(5, this.base.getDisplayName());
        }
        HashSet changes = new HashSet(old.getLibraries());
        changes.removeAll(this.base.getLibraries());
        Iterator it = changes.iterator();
        while (it.hasNext()) {
            this.fireLibraryEvent(3, it.next());
        }
        changes.clear();
        changes.addAll(this.base.getLibraries());
        changes.removeAll(old.getLibraries());
        it = changes.iterator();
        while (it.hasNext()) {
            this.fireLibraryEvent(2, it.next());
        }
        HashMap<ComponentFactory, ComponentFactory> componentMap = new HashMap<ComponentFactory, ComponentFactory>();
        HashMap<Object, Tool> toolMap = new HashMap<Object, Tool>();
        for (Tool oldTool : old.getTools()) {
            Tool newTool = this.base.getTool(oldTool.getName());
            toolMap.put(oldTool, newTool);
            if (!(oldTool instanceof AddTool)) continue;
            ComponentFactory oldFactory = ((AddTool)oldTool).getFactory();
            toolMap.put(oldFactory, newTool);
            if (newTool != null && newTool instanceof AddTool) {
                ComponentFactory newFactory = ((AddTool)newTool).getFactory();
                componentMap.put(oldFactory, newFactory);
                continue;
            }
            componentMap.put(oldFactory, null);
        }
        LoadedLibrary.replaceAll(componentMap, toolMap);
        changes.clear();
        changes.addAll(old.getTools());
        changes.removeAll(toolMap.keySet());
        Iterator it2 = changes.iterator();
        while (it2.hasNext()) {
            this.fireLibraryEvent(1, it2.next());
        }
        changes.clear();
        changes.addAll(this.base.getTools());
        changes.removeAll(toolMap.values());
        it2 = changes.iterator();
        while (it2.hasNext()) {
            this.fireLibraryEvent(0, it2.next());
        }
    }

    private static void replaceAll(HashMap componentMap, HashMap toolMap) {
        for (Project proj : Projects.getOpenProjects()) {
            Tool oldTool = proj.getTool();
            Circuit oldCircuit = proj.getCurrentCircuit();
            if (toolMap.containsKey(oldTool)) {
                proj.setTool((Tool)toolMap.get(oldTool));
            }
            if (componentMap.containsKey(oldCircuit)) {
                proj.setCurrentCircuit((Circuit)componentMap.get(oldCircuit));
            }
            LoadedLibrary.replaceAll(proj.getLogisimFile(), componentMap, toolMap);
        }
        for (LogisimFile file : LibraryManager.instance.getLogisimLibraries()) {
            LoadedLibrary.replaceAll(file, componentMap, toolMap);
        }
    }

    private static void replaceAll(LogisimFile file, HashMap componentMap, HashMap toolMap) {
        file.getOptions().getToolbarData().replaceAll(toolMap);
        file.getOptions().getMouseMappings().replaceAll(toolMap);
        for (Object tool : file.getTools()) {
            ComponentFactory circuit;
            if (!(tool instanceof AddTool) || !((circuit = ((AddTool)tool).getFactory()) instanceof Circuit)) continue;
            LoadedLibrary.replaceAll((Circuit)circuit, componentMap);
        }
    }

    private static void replaceAll(Circuit circuit, HashMap componentMap) {
        ArrayList<Component> toReplace = null;
        for (Component comp : circuit.getNonWires()) {
            if (!componentMap.containsKey(comp.getFactory())) continue;
            if (toReplace == null) {
                toReplace = new ArrayList<Component>();
            }
            toReplace.add(comp);
        }
        if (toReplace == null) {
            return;
        }
        int n = toReplace.size();
        for (int i = 0; i < n; ++i) {
            Component comp2 = (Component)toReplace.get(i);
            circuit.remove(comp2);
            ComponentFactory factory = (ComponentFactory)componentMap.get(comp2.getFactory());
            if (factory == null) continue;
            AttributeSet newAttrs = LoadedLibrary.createAttributes(factory, comp2.getAttributeSet());
            circuit.add(factory.createComponent(comp2.getLocation(), newAttrs));
        }
    }

    private static AttributeSet createAttributes(ComponentFactory factory, AttributeSet src) {
        AttributeSet dest = factory.createAttributeSet();
        LoadedLibrary.copyAttributes(dest, src);
        return dest;
    }

    static void copyAttributes(AttributeSet dest, AttributeSet src) {
        for (Attribute destAttr : dest.getAttributes()) {
            Attribute srcAttr = src.getAttribute(destAttr.getName());
            if (srcAttr == null) continue;
            dest.setValue(destAttr, src.getValue(srcAttr));
        }
    }

    private class MyListener
    implements LibraryListener {
        private MyListener() {
        }

        @Override
        public void libraryChanged(LibraryEvent event) {
            LoadedLibrary.this.fireLibraryEvent(event);
        }
    }

}

