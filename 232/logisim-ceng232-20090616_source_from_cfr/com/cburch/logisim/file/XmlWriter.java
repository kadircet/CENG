/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.file;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.file.LibraryLoader;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.MouseMappings;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.file.ToolbarData;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.InputEventUtil;
import com.cburch.logisim.util.StringUtil;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

class XmlWriter {
    private XMLOutputter outputter;
    private LibraryLoader loader;

    XmlWriter(LibraryLoader loader) {
        this.loader = loader;
        Format format = Format.getPrettyFormat();
        this.outputter = new XMLOutputter(format);
    }

    Object initialize(LogisimFile file) {
        WriteContext context = new WriteContext(file);
        return new Document(context.fromLogisimFile());
    }

    void output(Object data, Writer writer) throws IOException {
        this.outputter.output((Document)data, writer);
    }

    private class WriteContext {
        LogisimFile file;
        HashMap libs;

        WriteContext(LogisimFile file) {
            this.libs = new HashMap();
            this.file = file;
        }

        Element fromLogisimFile() {
            Element ret = new Element("project");
            ret.addContent("This file is intended to be loaded by Logisim (www.logisim.com).");
            ret.setAttribute("version", "1.0");
            ret.setAttribute("source", "2.1.8");
            for (Library lib : this.file.getLibraries()) {
                Element elt = this.fromLibrary(lib);
                if (elt == null) continue;
                ret.addContent(elt);
            }
            if (this.file.getMainCircuit() != null) {
                Element mainElt = new Element("main");
                mainElt.setAttribute("name", this.file.getMainCircuit().getName());
                ret.addContent(mainElt);
            }
            ret.addContent(this.fromOptions());
            ret.addContent(this.fromMouseMappings());
            ret.addContent(this.fromToolbarData());
            for (AddTool tool : this.file.getTools()) {
                Circuit circ = (Circuit)tool.getFactory();
                ret.addContent(this.fromCircuit(circ));
            }
            return ret;
        }

        Element fromLibrary(Library lib) {
            Element ret = new Element("lib");
            if (this.libs.containsKey(lib)) {
                return null;
            }
            String name = "" + this.libs.size();
            String desc = XmlWriter.this.loader.getDescriptor(lib);
            if (desc == null) {
                XmlWriter.this.loader.showError("library location unknown: " + lib.getName());
                return null;
            }
            this.libs.put(lib, name);
            ret.setAttribute("name", name);
            ret.setAttribute("desc", desc);
            for (Tool t : lib.getTools()) {
                AttributeSet attrs = t.getAttributeSet();
                if (attrs == null) continue;
                Element to_add = new Element("tool");
                to_add.setAttribute("name", t.getName());
                this.addAttributeSetContent(to_add, attrs);
                ret.addContent(to_add);
            }
            return ret;
        }

        Element fromOptions() {
            Element elt = new Element("options");
            this.addAttributeSetContent(elt, this.file.getOptions().getAttributeSet());
            return elt;
        }

        Element fromMouseMappings() {
            Element elt = new Element("mappings");
            MouseMappings map = this.file.getOptions().getMouseMappings();
            for (Integer mods : map.getMappings().keySet()) {
                Tool tool = map.getToolFor(mods);
                Element to_add = this.fromTool(tool);
                to_add.setAttribute("map", InputEventUtil.toString(mods));
                elt.addContent(to_add);
            }
            return elt;
        }

        Element fromToolbarData() {
            Element elt = new Element("toolbar");
            ToolbarData toolbar = this.file.getOptions().getToolbarData();
            for (Object item : toolbar.getContents()) {
                if (item instanceof ToolbarData.Separator) {
                    elt.addContent(new Element("sep"));
                    continue;
                }
                if (!(item instanceof Tool)) continue;
                elt.addContent(this.fromTool((Tool)item));
            }
            return elt;
        }

        Element fromTool(Tool tool) {
            String lib_name;
            Library lib = this.findLibrary(tool);
            if (lib == null) {
                XmlWriter.this.loader.showError(StringUtil.format("tool `%s' not found", tool.getDisplayName()));
                return null;
            }
            if (lib == this.file) {
                lib_name = null;
            } else {
                lib_name = (String)this.libs.get(lib);
                if (lib_name == null) {
                    XmlWriter.this.loader.showError("unknown library within file");
                    return null;
                }
            }
            Element elt = new Element("tool");
            if (lib_name != null) {
                elt.setAttribute("lib", lib_name);
            }
            elt.setAttribute("name", tool.getName());
            this.addAttributeSetContent(elt, tool.getAttributeSet());
            return elt;
        }

        Element fromCircuit(Circuit circuit) {
            Element ret = new Element("circuit");
            ret.setAttribute("name", circuit.getName());
            for (Wire w : circuit.getWires()) {
                ret.addContent(this.fromWire(w));
            }
            for (Component comp : circuit.getNonWires()) {
                Element elt = this.fromComponent(comp);
                if (elt == null) continue;
                ret.addContent(elt);
            }
            return ret;
        }

        Element fromComponent(Component comp) {
            String lib_name;
            ComponentFactory source = comp.getFactory();
            Library lib = this.findLibrary(source);
            if (lib == null) {
                XmlWriter.this.loader.showError(source.getName() + " component not found");
                return null;
            }
            if (lib == this.file) {
                lib_name = null;
            } else {
                lib_name = (String)this.libs.get(lib);
                if (lib_name == null) {
                    XmlWriter.this.loader.showError("unknown library within file");
                    return null;
                }
            }
            Element ret = new Element("comp");
            if (lib_name != null) {
                ret.setAttribute("lib", lib_name);
            }
            ret.setAttribute("name", source.getName());
            ret.setAttribute("loc", comp.getLocation().toString());
            this.addAttributeSetContent(ret, comp.getAttributeSet());
            return ret;
        }

        Element fromWire(Wire w) {
            Element ret = new Element("wire");
            ret.setAttribute("from", w.getEnd0().toString());
            ret.setAttribute("to", w.getEnd1().toString());
            return ret;
        }

        void addAttributeSetContent(Element elt, AttributeSet attrs) {
            if (attrs == null) {
                return;
            }
            for (Attribute attr : attrs.getAttributes()) {
                Object val = attrs.getValue(attr);
                if (val == null) continue;
                Element a = new Element("a");
                a.setAttribute("name", attr.getName());
                String value = attr.toStandardString(val);
                if (value.indexOf("\n") >= 0) {
                    a.addContent(value);
                } else {
                    a.setAttribute("val", attr.toStandardString(val));
                }
                elt.addContent(a);
            }
        }

        Library findLibrary(Tool tool) {
            if (this.libraryContains(this.file, tool)) {
                return this.file;
            }
            for (Library lib : this.file.getLibraries()) {
                if (!this.libraryContains(lib, tool)) continue;
                return lib;
            }
            return null;
        }

        Library findLibrary(ComponentFactory source) {
            if (this.file.contains(source)) {
                return this.file;
            }
            for (Library lib : this.file.getLibraries()) {
                if (!lib.contains(source)) continue;
                return lib;
            }
            return null;
        }

        boolean libraryContains(Library lib, Tool query) {
            for (Tool tool : lib.getTools()) {
                if (!tool.sharesSource(query)) continue;
                return true;
            }
            return false;
        }
    }

}

