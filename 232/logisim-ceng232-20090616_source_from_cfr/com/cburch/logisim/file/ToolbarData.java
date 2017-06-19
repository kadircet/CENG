/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.file;

import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.AttributeSets;
import com.cburch.logisim.file.LoadedLibrary;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.EventSourceWeakSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ToolbarData {
    private EventSourceWeakSupport listeners = new EventSourceWeakSupport();
    private EventSourceWeakSupport toolListeners = new EventSourceWeakSupport();
    private ArrayList contents = new ArrayList();

    public void addToolbarListener(ToolbarListener l) {
        this.listeners.add(l);
    }

    public void removeToolbarListener(ToolbarListener l) {
        this.listeners.remove(l);
    }

    public void addToolAttributeListener(AttributeListener l) {
        for (Object o : this.contents) {
            AttributeSet attrs;
            if (!(o instanceof Tool) || (attrs = ((Tool)o).getAttributeSet()) == null) continue;
            attrs.addAttributeListener(l);
        }
        this.toolListeners.add(l);
    }

    public void removeToolAttributeListener(AttributeListener l) {
        for (Object o : this.contents) {
            AttributeSet attrs;
            if (!(o instanceof Tool) || (attrs = ((Tool)o).getAttributeSet()) == null) continue;
            attrs.removeAttributeListener(l);
        }
        this.toolListeners.remove(l);
    }

    private void addAttributeListeners(Tool tool) {
        Iterator it = this.toolListeners.iterator();
        while (it.hasNext()) {
            AttributeListener l = (AttributeListener)it.next();
            AttributeSet attrs = tool.getAttributeSet();
            if (attrs == null) continue;
            attrs.addAttributeListener(l);
        }
    }

    private void removeAttributeListeners(Tool tool) {
        Iterator it = this.toolListeners.iterator();
        while (it.hasNext()) {
            AttributeListener l = (AttributeListener)it.next();
            AttributeSet attrs = tool.getAttributeSet();
            if (attrs == null) continue;
            attrs.removeAttributeListener(l);
        }
    }

    public void fireToolbarChanged() {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((ToolbarListener)it.next()).toolbarChanged();
        }
    }

    public List getContents() {
        return this.contents;
    }

    public Tool getFirstTool() {
        for (Object o : this.contents) {
            if (!(o instanceof Tool)) continue;
            return (Tool)o;
        }
        return null;
    }

    public int size() {
        return this.contents.size();
    }

    public Object get(int index) {
        return this.contents.get(index);
    }

    public void copyFrom(ToolbarData other, LogisimFile file) {
        if (this == other) {
            return;
        }
        for (Object o2 : this.contents) {
            if (!(o2 instanceof Tool)) continue;
            this.removeAttributeListeners((Tool)o2);
        }
        this.contents.clear();
        for (Object o2 : other.contents) {
            Tool toolCopy;
            Tool srcTool;
            if (o2 instanceof Separator) {
                this.addSeparator();
                continue;
            }
            if (!(o2 instanceof Tool) || (toolCopy = file.findTool(srcTool = (Tool)o2)) == null) continue;
            Tool dstTool = toolCopy.cloneTool();
            AttributeSets.copy(srcTool.getAttributeSet(), dstTool.getAttributeSet());
            this.addTool(dstTool);
            this.addAttributeListeners(toolCopy);
        }
        this.fireToolbarChanged();
    }

    public void addSeparator() {
        this.contents.add(new Separator());
        this.fireToolbarChanged();
    }

    public void addTool(Tool tool) {
        this.contents.add(tool);
        this.addAttributeListeners(tool);
        this.fireToolbarChanged();
    }

    public void addTool(int pos, Tool tool) {
        this.contents.add(pos, tool);
        this.addAttributeListeners(tool);
        this.fireToolbarChanged();
    }

    public void addSeparator(int pos) {
        this.contents.add(pos, new Separator());
        this.fireToolbarChanged();
    }

    public Object move(int from, int to) {
        Object moved = this.contents.remove(from);
        this.contents.add(to, moved);
        this.fireToolbarChanged();
        return moved;
    }

    public Object remove(int pos) {
        Object ret = this.contents.remove(pos);
        if (ret instanceof Tool) {
            this.removeAttributeListeners((Tool)ret);
        }
        this.fireToolbarChanged();
        return ret;
    }

    boolean usesToolFromSource(Tool query) {
        for (Object obj : this.contents) {
            Tool tool;
            if (!(obj instanceof Tool) || !(tool = (Tool)obj).sharesSource(query)) continue;
            return true;
        }
        return false;
    }

    void replaceAll(HashMap toolMap) {
        boolean changed = false;
        ListIterator<Tool> it = this.contents.listIterator();
        while (it.hasNext()) {
            Object old = it.next();
            if (old instanceof AddTool) {
                ComponentFactory factory = ((AddTool)old).getFactory();
                if (!toolMap.containsKey(factory)) continue;
                changed = true;
                this.removeAttributeListeners((Tool)old);
                Tool newTool = (Tool)toolMap.get(factory);
                if (newTool == null) {
                    it.remove();
                    continue;
                }
                Tool addedTool = newTool.cloneTool();
                this.addAttributeListeners(addedTool);
                LoadedLibrary.copyAttributes(addedTool.getAttributeSet(), ((Tool)old).getAttributeSet());
                it.set(addedTool);
                continue;
            }
            if (!toolMap.containsKey(old)) continue;
            changed = true;
            this.removeAttributeListeners((Tool)old);
            Tool newTool = (Tool)toolMap.get(old);
            if (newTool == null) {
                it.remove();
                continue;
            }
            Tool addedTool = newTool.cloneTool();
            this.addAttributeListeners(addedTool);
            LoadedLibrary.copyAttributes(addedTool.getAttributeSet(), ((Tool)old).getAttributeSet());
            it.set(addedTool);
        }
        if (changed) {
            this.fireToolbarChanged();
        }
    }

    public static interface ToolbarListener {
        public void toolbarChanged();
    }

    public static class Separator {
    }

}

