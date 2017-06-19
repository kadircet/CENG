/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.tools;

import com.cburch.logisim.tools.Library;
import java.util.Arrays;
import java.util.List;
import tr.edu.metu.ceng.ceng232.tools.ZoomInTool;
import tr.edu.metu.ceng.ceng232.tools.ZoomOutTool;

public class Tools
extends Library {
    private List tools = Arrays.asList(new ZoomInTool(), new ZoomOutTool());

    @Override
    public String getName() {
        return "CENG232 Tools";
    }

    @Override
    public String getDisplayName() {
        return this.getName();
    }

    @Override
    public List getTools() {
        return this.tools;
    }
}

