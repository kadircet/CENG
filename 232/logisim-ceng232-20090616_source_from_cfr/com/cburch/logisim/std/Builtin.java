/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std;

import com.cburch.logisim.legacy.Legacy;
import com.cburch.logisim.std.Base;
import com.cburch.logisim.std.Strings;
import com.cburch.logisim.std.arith.Arithmetic;
import com.cburch.logisim.std.gates.Gates;
import com.cburch.logisim.std.io.Io;
import com.cburch.logisim.std.memory.Memory;
import com.cburch.logisim.std.plexers.Plexers;
import com.cburch.logisim.tools.Library;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import tr.edu.metu.ceng.ceng232.others.Others;
import tr.edu.metu.ceng.ceng232.tools.Tools;

public class Builtin
extends Library {
    private List libraries = Arrays.asList(new Base(), new Gates(), new Memory(), new Plexers(), new Arithmetic(), new Io(), new Legacy(), new tr.edu.metu.ceng.ceng232.gates.Gates(), new Others(), new Tools());

    @Override
    public String getName() {
        return "Builtin";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("builtinLibrary");
    }

    @Override
    public List getTools() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List getLibraries() {
        return this.libraries;
    }
}

