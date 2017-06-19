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
import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.MouseMappings;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.file.Strings;
import com.cburch.logisim.file.ToolbarData;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.InputEventUtil;
import com.cburch.logisim.util.StringUtil;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import tr.edu.metu.ceng.ceng232.grader.Settings;

class XmlReader {
    private SAXBuilder builder = new SAXBuilder();
    private LibraryLoader loader;

    XmlReader(Loader loader) {
        this.loader = loader;
    }

    LogisimFile readLibrary(Reader reader) throws IOException {
        try {
            Document doc = this.builder.build(reader);
            Element elt = doc.getRootElement();
            LogisimFile file = new LogisimFile((Loader)this.loader);
            ReadContext context = new ReadContext(file);
            context.toLogisimFile(elt);
            return file;
        }
        catch (JDOMException e) {
            this.loader.showError(StringUtil.format(Strings.get("xmlFormatError"), e.toString()));
            return null;
        }
    }

    private class ReadContext {
        LogisimFile file;
        HashMap libs;
        ArrayList unknowns;

        ReadContext(LogisimFile file) {
            this.libs = new HashMap();
            this.unknowns = new ArrayList();
            this.file = file;
        }

        void toLogisimFile(Element elt) {
            String main = null;
            for (Element o : elt.getChildren("lib")) {
                Library lib = this.toLibrary(o);
                if (lib == null) continue;
                this.file.addLibrary(lib);
            }
            for (Element sub_elt : elt.getChildren()) {
                String name = sub_elt.getName();
                if (name.equals("circuit")) {
                    Circuit to_add = this.toCircuit(sub_elt);
                    this.file.addCircuit(to_add);
                    continue;
                }
                if (name.equals("lib")) continue;
                if (name.equals("options")) {
                    this.initOptions(sub_elt);
                    continue;
                }
                if (name.equals("mappings")) {
                    this.initMouseMappings(sub_elt);
                    continue;
                }
                if (name.equals("toolbar")) {
                    this.initToolbarData(sub_elt);
                    continue;
                }
                if (name.equals("main")) {
                    main = sub_elt.getAttributeValue("name");
                    continue;
                }
                if (!name.equals("message")) continue;
                this.file.addMessage(sub_elt.getAttributeValue("value"));
            }
            this.file.setMainCircuit(this.file.getCircuit(main));
            for (UnknownComponent unk : this.unknowns) {
                AddTool tool = (AddTool)this.file.getTool(unk.comp_name);
                if (tool == null) {
                    XmlReader.this.loader.showError(StringUtil.format(Strings.get("compUnknownError"), unk.comp_name));
                    continue;
                }
                this.addComponent(unk.circuit, tool.getFactory(), unk.elt);
            }
        }

        Library toLibrary(Element elt) {
            String name = elt.getAttributeValue("name");
            if (name == null) {
                XmlReader.this.loader.showError(Strings.get("libNameMissingError"));
                return null;
            }
            String desc = elt.getAttributeValue("desc");
            if (desc == null) {
                XmlReader.this.loader.showError(Strings.get("libDescMissingError"));
                return null;
            }
            Library ret = XmlReader.this.loader.loadLibrary(desc);
            if (ret == null) {
                return null;
            }
            this.libs.put(name, ret);
            for (Element sub_elt : elt.getChildren()) {
                if (!sub_elt.getName().equals("tool")) continue;
                String tool_str = sub_elt.getAttributeValue("name");
                if (tool_str == null) {
                    XmlReader.this.loader.showError(Strings.get("toolNameMissingError"));
                    continue;
                }
                Tool tool = ret.getTool(tool_str);
                if (tool == null) continue;
                this.initAttributeSet(sub_elt, tool.getAttributeSet());
            }
            return ret;
        }

        void initOptions(Element elt) {
            this.initAttributeSet(elt, this.file.getOptions().getAttributeSet());
        }

        void initMouseMappings(Element elt) {
            MouseMappings map = this.file.getOptions().getMouseMappings();
            for (Element sub_elt : elt.getChildren()) {
                Tool tool;
                int mods;
                if (!sub_elt.getName().equals("tool") || (tool = this.toTool(sub_elt)) == null) continue;
                String mods_str = sub_elt.getAttributeValue("map");
                if (mods_str == null) {
                    XmlReader.this.loader.showError(Strings.get("mappingMissingError"));
                    continue;
                }
                try {
                    mods = InputEventUtil.fromString(mods_str);
                }
                catch (NumberFormatException e) {
                    XmlReader.this.loader.showError(StringUtil.format(Strings.get("mappingBadError"), mods_str));
                    continue;
                }
                tool = tool.cloneTool();
                this.initAttributeSet(sub_elt, tool.getAttributeSet());
                map.setToolFor(mods, tool);
            }
        }

        void initToolbarData(Element elt) {
            ToolbarData toolbar = this.file.getOptions().getToolbarData();
            for (Element sub_elt : elt.getChildren()) {
                Tool tool;
                if (sub_elt.getName().equals("sep")) {
                    toolbar.addSeparator();
                    continue;
                }
                if (!sub_elt.getName().equals("tool") || (tool = this.toTool(sub_elt)) == null) continue;
                tool = tool.cloneTool();
                this.initAttributeSet(sub_elt, tool.getAttributeSet());
                toolbar.addTool(tool);
            }
        }

        Tool toTool(Element elt) {
            Library lib = this.findLibrary(elt.getAttributeValue("lib"));
            if (lib == null) {
                return null;
            }
            String tool_name = elt.getAttributeValue("name");
            if (tool_name == null) {
                return null;
            }
            return lib.getTool(tool_name);
        }

        Circuit toCircuit(Element circuit_elt) {
            String circuit_name = circuit_elt.getAttributeValue("name");
            if (circuit_name == null) {
                XmlReader.this.loader.showError(Strings.get("circNameMissingError"));
            }
            Circuit ret = new Circuit(circuit_name);
            for (Element sub_elt : circuit_elt.getChildren()) {
                if (sub_elt.getName().equals("comp")) {
                    this.addComponent(ret, sub_elt);
                    continue;
                }
                if (!sub_elt.getName().equals("wire")) continue;
                this.addWire(ret, sub_elt);
            }
            return ret;
        }

        void addComponent(Circuit circuit, Element elt) {
            String comp_name = elt.getAttributeValue("name");
            if (comp_name == null) {
                XmlReader.this.loader.showError(Strings.get("compNameMissingError"));
                return;
            }
            Library lib = this.findLibrary(elt.getAttributeValue("lib"));
            Settings.useComponent(lib.getName() + ":" + comp_name);
            if (lib == null) {
                return;
            }
            Tool tool = lib.getTool(comp_name);
            if (tool == null || !(tool instanceof AddTool)) {
                if (lib == this.file) {
                    this.unknowns.add(new UnknownComponent(circuit, comp_name, elt));
                } else {
                    XmlReader.this.loader.showError(StringUtil.format(Strings.get("compAbsentError"), comp_name, lib.getName()));
                }
            } else {
                ComponentFactory source = ((AddTool)tool).getFactory();
                this.addComponent(circuit, source, elt);
            }
        }

        void addComponent(Circuit circuit, ComponentFactory source, Element elt) {
            String loc_str = elt.getAttributeValue("loc");
            AttributeSet attrs = source.createAttributeSet();
            this.initAttributeSet(elt, attrs);
            if (loc_str == null) {
                XmlReader.this.loader.showError(StringUtil.format(Strings.get("compLocMissingError"), source.getName()));
            } else {
                try {
                    Location loc = Location.parse(loc_str);
                    circuit.add(source.createComponent(loc, attrs));
                }
                catch (NumberFormatException e) {
                    XmlReader.this.loader.showError(StringUtil.format(Strings.get("compLocInvalidError"), source.getName(), loc_str));
                }
            }
        }

        void addWire(Circuit circuit, Element elt) {
            Location pt0;
            Location pt1;
            try {
                String str = elt.getAttributeValue("from");
                if (str == null) {
                    XmlReader.this.loader.showError(Strings.get("wireStartMissingError"));
                }
                pt0 = Location.parse(str);
            }
            catch (NumberFormatException e) {
                XmlReader.this.loader.showError(Strings.get("wireStartInvalidError"));
                return;
            }
            try {
                String str = elt.getAttributeValue("to");
                if (str == null) {
                    XmlReader.this.loader.showError(Strings.get("wireEndMissingError"));
                }
                pt1 = Location.parse(str);
            }
            catch (NumberFormatException e) {
                XmlReader.this.loader.showError(Strings.get("wireEndInvalidError"));
                return;
            }
            circuit.add(Wire.create(pt0, pt1));
        }

        void initAttributeSet(Element elt, AttributeSet attrs) {
            for (Element attr_elt : elt.getChildren()) {
                if (!attr_elt.getName().equals("a")) continue;
                String attr_name = attr_elt.getAttributeValue("name");
                if (attr_name == null) {
                    XmlReader.this.loader.showError(Strings.get("attrNameMissingError"));
                    continue;
                }
                Attribute attr = attrs.getAttribute(attr_name);
                if (attr == null) continue;
                String attr_val = attr_elt.getAttributeValue("val");
                if (attr_val == null) {
                    attr_val = attr_elt.getText();
                }
                if (attr_val == null) continue;
                Object val = null;
                try {
                    val = attr.parse(attr_val);
                }
                catch (NumberFormatException e) {
                    XmlReader.this.loader.showError(StringUtil.format(Strings.get("attrValueInvalidError"), attr_val, attr_name));
                    continue;
                }
                attrs.setValue(attr, val);
            }
        }

        Library findLibrary(String lib_name) {
            if (lib_name == null) {
                return this.file;
            }
            Library ret = (Library)this.libs.get(lib_name);
            if (ret == null) {
                XmlReader.this.loader.showError(StringUtil.format(Strings.get("libMissingError"), lib_name));
                return null;
            }
            return ret;
        }
    }

    private static class UnknownComponent {
        Circuit circuit;
        String comp_name;
        Element elt;

        UnknownComponent(Circuit circuit, String comp_name, Element elt) {
            this.circuit = circuit;
            this.comp_name = comp_name;
            this.elt = elt;
        }
    }

}

