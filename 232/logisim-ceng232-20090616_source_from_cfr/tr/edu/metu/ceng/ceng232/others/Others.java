/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.others;

import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import java.util.Arrays;
import java.util.List;
import tr.edu.metu.ceng.ceng232.others.ic74112;
import tr.edu.metu.ceng.ceng232.others.ic74138;
import tr.edu.metu.ceng.ceng232.others.ic74153;
import tr.edu.metu.ceng.ceng232.others.ic74155;
import tr.edu.metu.ceng.ceng232.others.ic74195;
import tr.edu.metu.ceng.ceng232.others.ic7474;
import tr.edu.metu.ceng.ceng232.others.ic7475;
import tr.edu.metu.ceng.ceng232.others.ic7483;
import tr.edu.metu.ceng.ceng232.others.ic7495;

public class Others
extends Library {
    private List tools = Arrays.asList(new AddTool(ic74138.factory), new AddTool(ic74153.factory), new AddTool(ic74155.factory), new AddTool(ic7474.factory), new AddTool(ic7475.factory), new AddTool(ic7483.factory), new AddTool(ic7495.factory), new AddTool(ic74112.factory), new AddTool(ic74195.factory));

    @Override
    public String getName() {
        return "CENG232 ICs";
    }

    @Override
    public String getDisplayName() {
        return "CENG232 ICs";
    }

    @Override
    public List getTools() {
        return this.tools;
    }
}

