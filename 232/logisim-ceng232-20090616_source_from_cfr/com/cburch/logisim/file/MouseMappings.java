/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.file;

import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.AttributeSets;
import com.cburch.logisim.file.LoadedLibrary;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.SelectTool;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.IntegerFactory;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MouseMappings {
    private ArrayList listeners = new ArrayList();
    private HashMap map = new HashMap();
    private int cache_mods;
    private Tool cache_tool;

    public void addMouseMappingsListener(MouseMappingsListener l) {
        this.listeners.add(l);
    }

    public void removeMouseMappingsListener(MouseMappingsListener l) {
        this.listeners.add(l);
    }

    private void fireMouseMappingsChanged() {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((MouseMappingsListener)it.next()).mouseMappingsChanged();
        }
    }

    public Map getMappings() {
        return this.map;
    }

    public Set getMappedModifiers() {
        return this.map.keySet();
    }

    public Tool getToolFor(MouseEvent e) {
        return this.getToolFor(e.getModifiers());
    }

    public Tool getToolFor(int mods) {
        if (mods == this.cache_mods) {
            return this.cache_tool;
        }
        Tool ret = (Tool)this.map.get(IntegerFactory.create(mods));
        this.cache_mods = mods;
        this.cache_tool = ret;
        return ret;
    }

    public Tool getToolFor(Integer mods) {
        if (mods == this.cache_mods) {
            return this.cache_tool;
        }
        Tool ret = (Tool)this.map.get(mods);
        this.cache_mods = mods;
        this.cache_tool = ret;
        return ret;
    }

    public boolean usesToolFromSource(Tool query) {
        for (Tool tool : this.map.values()) {
            if (!tool.sharesSource(query)) continue;
            return true;
        }
        return false;
    }

    public boolean containsSelectTool() {
        for (Object tool : this.map.values()) {
            if (!(tool instanceof SelectTool)) continue;
            return true;
        }
        return false;
    }

    public void copyFrom(MouseMappings other, LogisimFile file) {
        if (this == other) {
            return;
        }
        this.cache_mods = -1;
        this.map.clear();
        for (Integer mods : other.map.keySet()) {
            Tool srcTool = (Tool)other.map.get(mods);
            Tool dstTool = file.findTool(srcTool);
            if (dstTool == null) continue;
            dstTool = dstTool.cloneTool();
            AttributeSets.copy(srcTool.getAttributeSet(), dstTool.getAttributeSet());
            this.map.put(mods, dstTool);
        }
        this.fireMouseMappingsChanged();
    }

    public void setToolFor(MouseEvent e, Tool tool) {
        this.setToolFor(e.getModifiers(), tool);
    }

    public void setToolFor(int mods, Tool tool) {
        if (mods == this.cache_mods) {
            this.cache_mods = -1;
        }
        if (tool == null) {
            Object old = this.map.remove(IntegerFactory.create(mods));
            if (old != null) {
                this.fireMouseMappingsChanged();
            }
        } else {
            Tool old = this.map.put(IntegerFactory.create(mods), tool);
            if (old != tool) {
                this.fireMouseMappingsChanged();
            }
        }
    }

    public void setToolFor(Integer mods, Tool tool) {
        if (mods == this.cache_mods) {
            this.cache_mods = -1;
        }
        if (tool == null) {
            Object old = this.map.remove(mods);
            if (old != null) {
                this.fireMouseMappingsChanged();
            }
        } else {
            Tool old = this.map.put(mods, tool);
            if (old != tool) {
                this.fireMouseMappingsChanged();
            }
        }
    }

    void replaceAll(HashMap toolMap) {
        boolean changed = false;
        for (Object key : this.map.keySet()) {
            Object tool = this.map.get(key);
            if (tool instanceof AddTool) {
                ComponentFactory factory = ((AddTool)tool).getFactory();
                if (!toolMap.containsKey(factory)) continue;
                changed = true;
                Tool newTool = (Tool)toolMap.get(factory);
                if (newTool == null) {
                    this.map.remove(key);
                    continue;
                }
                Tool clone = newTool.cloneTool();
                LoadedLibrary.copyAttributes(clone.getAttributeSet(), ((Tool)tool).getAttributeSet());
                this.map.put(key, clone);
                continue;
            }
            if (!toolMap.containsKey(tool)) continue;
            changed = true;
            Tool newTool = (Tool)toolMap.get(tool);
            if (newTool == null) {
                this.map.remove(key);
                continue;
            }
            Tool clone = newTool.cloneTool();
            LoadedLibrary.copyAttributes(clone.getAttributeSet(), ((Tool)tool).getAttributeSet());
            this.map.put(key, clone);
        }
        if (changed) {
            this.fireMouseMappingsChanged();
        }
    }

    public static interface MouseMappingsListener {
        public void mouseMappingsChanged();
    }

}

