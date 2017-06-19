/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Tool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import javax.swing.JList;

class CircuitJList
extends JList {
    public CircuitJList(Project proj, boolean includeEmpty) {
        LogisimFile file = proj.getLogisimFile();
        Circuit current = proj.getCurrentCircuit();
        Vector<Circuit> options = new Vector<Circuit>();
        boolean currentFound = false;
        for (Tool t : file.getTools()) {
            ComponentFactory c;
            if (!(t instanceof AddTool) || !((c = ((AddTool)t).getFactory()) instanceof Circuit)) continue;
            Circuit circ = (Circuit)c;
            if (includeEmpty && circ.getBounds() == Bounds.EMPTY_BOUNDS) continue;
            if (circ == current) {
                currentFound = true;
            }
            options.add(circ);
        }
        this.setListData(options);
        if (currentFound) {
            this.setSelectedValue(current, true);
        }
        this.setVisibleRowCount(Math.min(6, options.size()));
    }

    public List getSelectedCircuits() {
        Object[] selected = this.getSelectedValues();
        if (selected != null && selected.length > 0) {
            ArrayList<Object> ret = new ArrayList<Object>(selected.length);
            for (int i = 0; i < selected.length; ++i) {
                ret.add(selected[i]);
            }
            return ret;
        }
        return Collections.EMPTY_LIST;
    }
}

