/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.file;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.file.Strings;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.LogisimPreferences;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;
import java.util.ArrayList;
import java.util.List;

public class LogisimFileActions {
    private LogisimFileActions() {
    }

    public static Action addCircuit(Circuit circuit) {
        return new AddCircuit(circuit);
    }

    public static Action removeCircuit(Circuit circuit) {
        return new RemoveCircuit(circuit);
    }

    public static Action moveCircuit(AddTool tool, int toIndex) {
        return new MoveCircuit(tool, toIndex);
    }

    public static Action loadLibrary(Library lib) {
        return new LoadLibraries(new Library[]{lib});
    }

    public static Action loadLibraries(Library[] libs) {
        return new LoadLibraries(libs);
    }

    public static Action unloadLibrary(Library lib) {
        return new UnloadLibraries(new Library[]{lib});
    }

    public static Action unloadLibraries(Library[] libs) {
        return new UnloadLibraries(libs);
    }

    public static Action setMainCircuit(Circuit circuit) {
        return new SetMainCircuit(circuit);
    }

    public static Action revertDefaults() {
        return new RevertDefaults();
    }

    private static class RevertDefaults
    extends Action {
        private Options oldOpts;
        private ArrayList libraries = null;
        private ArrayList attrValues = new ArrayList();

        RevertDefaults() {
        }

        @Override
        public String getName() {
            return Strings.get("revertDefaultsAction");
        }

        @Override
        public void doIt(Project proj) {
            LogisimFile src = LogisimPreferences.getTemplate(proj.getLogisimFile().getLoader());
            LogisimFile dst = proj.getLogisimFile();
            this.copyToolAttributes(src, dst);
            for (Library srcLib : src.getLibraries()) {
                Library dstLib = dst.getLibrary(srcLib.getName());
                if (dstLib == null) {
                    String desc = src.getLoader().getDescriptor(srcLib);
                    dstLib = dst.getLoader().loadLibrary(desc);
                    proj.getLogisimFile().addLibrary(dstLib);
                    if (this.libraries == null) {
                        this.libraries = new ArrayList();
                    }
                    this.libraries.add(dstLib);
                }
                this.copyToolAttributes(srcLib, dstLib);
            }
            Options newOpts = proj.getOptions();
            this.oldOpts = new Options();
            this.oldOpts.copyFrom(newOpts, dst);
            newOpts.copyFrom(src.getOptions(), dst);
        }

        private void copyToolAttributes(Library srcLib, Library dstLib) {
            for (Tool srcTool : srcLib.getTools()) {
                AttributeSet srcAttrs = srcTool.getAttributeSet();
                Tool dstTool = dstLib.getTool(srcTool.getName());
                if (srcAttrs == null || dstTool == null) continue;
                AttributeSet dstAttrs = dstTool.getAttributeSet();
                for (Attribute attr : srcAttrs.getAttributes()) {
                    Object srcValue = srcAttrs.getValue(attr);
                    Object dstValue = dstAttrs.getValue(attr);
                    if (dstValue.equals(srcValue)) continue;
                    dstAttrs.setValue(attr, srcValue);
                    this.attrValues.add(new RevertAttributeValue(dstAttrs, attr, dstValue));
                }
            }
        }

        @Override
        public void undo(Project proj) {
            proj.getOptions().copyFrom(this.oldOpts, proj.getLogisimFile());
            for (RevertAttributeValue attrValue : this.attrValues) {
                attrValue.attrs.setValue(attrValue.attr, attrValue.value);
            }
            if (this.libraries != null) {
                for (Library lib : this.libraries) {
                    proj.getLogisimFile().removeLibrary(lib);
                }
            }
        }
    }

    private static class RevertAttributeValue {
        private AttributeSet attrs;
        private Attribute attr;
        private Object value;

        RevertAttributeValue(AttributeSet attrs, Attribute attr, Object value) {
            this.attrs = attrs;
            this.attr = attr;
            this.value = value;
        }
    }

    private static class SetMainCircuit
    extends Action {
        private Circuit oldval;
        private Circuit newval;

        SetMainCircuit(Circuit circuit) {
            this.newval = circuit;
        }

        @Override
        public String getName() {
            return Strings.get("setMainCircuitAction");
        }

        @Override
        public void doIt(Project proj) {
            this.oldval = proj.getLogisimFile().getMainCircuit();
            proj.getLogisimFile().setMainCircuit(this.newval);
        }

        @Override
        public void undo(Project proj) {
            proj.getLogisimFile().setMainCircuit(this.oldval);
        }
    }

    private static class UnloadLibraries
    extends Action {
        private Library[] libs;

        UnloadLibraries(Library[] libs) {
            this.libs = libs;
        }

        @Override
        public String getName() {
            if (this.libs.length == 1) {
                return Strings.get("unloadLibraryAction");
            }
            return Strings.get("unloadLibrariesAction");
        }

        @Override
        public void doIt(Project proj) {
            for (int i = this.libs.length - 1; i >= 0; --i) {
                proj.getLogisimFile().removeLibrary(this.libs[i]);
            }
        }

        @Override
        public void undo(Project proj) {
            for (int i = 0; i < this.libs.length; ++i) {
                proj.getLogisimFile().addLibrary(this.libs[i]);
            }
        }
    }

    private static class LoadLibraries
    extends Action {
        private Library[] libs;

        LoadLibraries(Library[] libs) {
            this.libs = libs;
        }

        @Override
        public String getName() {
            if (this.libs.length == 1) {
                return Strings.get("loadLibraryAction");
            }
            return Strings.get("loadLibrariesAction");
        }

        @Override
        public void doIt(Project proj) {
            for (int i = 0; i < this.libs.length; ++i) {
                proj.getLogisimFile().addLibrary(this.libs[i]);
            }
        }

        @Override
        public void undo(Project proj) {
            for (int i = this.libs.length - 1; i >= 0; --i) {
                proj.getLogisimFile().removeLibrary(this.libs[i]);
            }
        }
    }

    private static class MoveCircuit
    extends Action {
        private AddTool tool;
        private int fromIndex;
        private int toIndex;

        MoveCircuit(AddTool tool, int toIndex) {
            this.tool = tool;
            this.toIndex = toIndex;
        }

        @Override
        public String getName() {
            return Strings.get("moveCircuitAction");
        }

        @Override
        public void doIt(Project proj) {
            this.fromIndex = proj.getLogisimFile().getTools().indexOf(this.tool);
            proj.getLogisimFile().moveCircuit(this.tool, this.toIndex);
        }

        @Override
        public void undo(Project proj) {
            proj.getLogisimFile().moveCircuit(this.tool, this.fromIndex);
        }
    }

    private static class RemoveCircuit
    extends Action {
        private Circuit circuit;

        RemoveCircuit(Circuit circuit) {
            this.circuit = circuit;
        }

        @Override
        public String getName() {
            return Strings.get("removeCircuitAction");
        }

        @Override
        public void doIt(Project proj) {
            proj.getLogisimFile().removeCircuit(this.circuit);
        }

        @Override
        public void undo(Project proj) {
            proj.getLogisimFile().addCircuit(this.circuit);
        }
    }

    private static class AddCircuit
    extends Action {
        private Circuit circuit;

        AddCircuit(Circuit circuit) {
            this.circuit = circuit;
        }

        @Override
        public String getName() {
            return Strings.get("addCircuitAction");
        }

        @Override
        public void doIt(Project proj) {
            proj.getLogisimFile().addCircuit(this.circuit);
        }

        @Override
        public void undo(Project proj) {
            proj.getLogisimFile().removeCircuit(this.circuit);
        }
    }

}

