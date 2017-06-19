/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.tools;

import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.ListUtil;
import java.util.Collections;
import java.util.List;

public abstract class Library {
    public abstract String getName();

    public abstract List getTools();

    public String toString() {
        return this.getName();
    }

    public String getDisplayName() {
        return this.getName();
    }

    public boolean isDirty() {
        return false;
    }

    public List getLibraries() {
        return Collections.EMPTY_LIST;
    }

    public List getElements() {
        return ListUtil.joinImmutableLists(this.getTools(), this.getLibraries());
    }

    public Tool getTool(String name) {
        for (E o : this.getTools()) {
            if (!(o instanceof Tool) || !((Tool)o).getName().equals(name)) continue;
            return (Tool)o;
        }
        return null;
    }

    public boolean containsFromSource(Tool query) {
        for (E obj : this.getTools()) {
            Tool tool;
            if (!(obj instanceof Tool) || !(tool = (Tool)obj).sharesSource(query)) continue;
            return true;
        }
        return false;
    }

    public int indexOf(ComponentFactory query) {
        int index = 0;
        for (E obj : this.getTools()) {
            AddTool tool;
            if (obj instanceof AddTool && (tool = (AddTool)obj).getFactory() == query) {
                return index;
            }
            ++index;
        }
        return -1;
    }

    public boolean contains(ComponentFactory query) {
        return this.indexOf(query) >= 0;
    }

    public Library getLibrary(String name) {
        for (E o : this.getLibraries()) {
            if (!(o instanceof Library) || !((Library)o).getName().equals(name)) continue;
            return (Library)o;
        }
        return null;
    }
}

