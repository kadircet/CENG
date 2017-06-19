/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.file;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.file.LibraryEvent;
import com.cburch.logisim.file.LibraryEventSource;
import com.cburch.logisim.file.LibraryListener;
import com.cburch.logisim.file.LibraryLoader;
import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.MouseMappings;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.file.Strings;
import com.cburch.logisim.file.ToolbarData;
import com.cburch.logisim.file.XmlReader;
import com.cburch.logisim.file.XmlWriter;
import com.cburch.logisim.legacy.Version1Support;
import com.cburch.logisim.proj.Projects;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.EventSourceWeakSupport;
import com.cburch.logisim.util.ListUtil;
import com.cburch.logisim.util.StringUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class LogisimFile
extends Library
implements LibraryEventSource {
    private EventSourceWeakSupport listeners = new EventSourceWeakSupport();
    private Loader loader;
    private LinkedList messages = new LinkedList();
    private Options options = new Options();
    private LinkedList tools = new LinkedList();
    private LinkedList libraries = new LinkedList();
    private Circuit main = null;
    private String name;
    private boolean dirty = false;

    LogisimFile(Loader loader) {
        this.loader = loader;
        this.name = Strings.get("defaultProjectName");
        if (Projects.windowNamed(this.name)) {
            int i = 2;
            do {
                if (!Projects.windowNamed(this.name + " " + i)) {
                    this.name = this.name + " " + i;
                    break;
                }
                ++i;
            } while (true);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    public String getMessage() {
        if (this.messages.size() == 0) {
            return null;
        }
        return (String)this.messages.removeFirst();
    }

    public Loader getLoader() {
        return this.loader;
    }

    public Options getOptions() {
        return this.options;
    }

    @Override
    public List getTools() {
        return this.tools;
    }

    @Override
    public List getLibraries() {
        return this.libraries;
    }

    @Override
    public List getElements() {
        return ListUtil.joinImmutableLists(this.tools, this.libraries);
    }

    public Circuit getCircuit(String name) {
        if (name == null) {
            return null;
        }
        for (AddTool tool : this.tools) {
            Circuit circ = (Circuit)tool.getFactory();
            if (!name.equals(circ.getName())) continue;
            return circ;
        }
        return null;
    }

    public Circuit getMainCircuit() {
        return this.main;
    }

    public int getCircuitCount() {
        return this.tools.size();
    }

    @Override
    public void addLibraryListener(LibraryListener what) {
        this.listeners.add(what);
    }

    @Override
    public void removeLibraryListener(LibraryListener what) {
        this.listeners.remove(what);
    }

    private void fireEvent(int action, Object data) {
        LibraryEvent e = new LibraryEvent(this, action, data);
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            LibraryListener what = (LibraryListener)it.next();
            what.libraryChanged(e);
        }
    }

    public void addMessage(String msg) {
        this.messages.addLast(msg);
    }

    public void setDirty(boolean value) {
        if (this.dirty != value) {
            this.dirty = value;
            this.fireEvent(6, value ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    public void setName(String name) {
        this.name = name;
        this.fireEvent(5, name);
    }

    public void addCircuit(Circuit circuit) {
        AddTool tool = new AddTool(circuit);
        this.tools.add(tool);
        if (this.tools.size() == 1) {
            this.setMainCircuit(circuit);
        }
        this.fireEvent(0, tool);
    }

    public void removeCircuit(Circuit circuit) {
        if (this.tools.size() <= 1) {
            throw new RuntimeException("Cannot remove last circuit");
        }
        AddTool circuitTool = null;
        Iterator it = this.tools.iterator();
        while (it.hasNext()) {
            AddTool tool = (AddTool)it.next();
            if (tool.getFactory() != circuit) continue;
            it.remove();
            circuitTool = tool;
            break;
        }
        if (circuitTool != null) {
            if (this.main == circuit) {
                AddTool dflt_tool = (AddTool)this.tools.get(0);
                Circuit dflt_circ = (Circuit)dflt_tool.getFactory();
                this.setMainCircuit(dflt_circ);
            }
            this.fireEvent(1, circuitTool);
        }
    }

    public void moveCircuit(AddTool tool, int index) {
        int oldIndex = this.tools.indexOf(tool);
        if (oldIndex < 0) {
            this.tools.add(index, tool);
            this.fireEvent(0, tool);
        } else {
            Object value = this.tools.remove(oldIndex);
            if (index > oldIndex) {
                --index;
            }
            this.tools.add(index, value);
            this.fireEvent(6, tool);
        }
    }

    public void addLibrary(Library lib) {
        this.libraries.add(lib);
        this.fireEvent(2, lib);
    }

    public void removeLibrary(Library lib) {
        this.libraries.remove(lib);
        this.fireEvent(3, lib);
    }

    public String getUnloadLibraryMessage(Library lib) {
        HashSet<ComponentFactory> factories = new HashSet<ComponentFactory>();
        for (Object tool2 : lib.getTools()) {
            if (!(tool2 instanceof AddTool)) continue;
            factories.add(((AddTool)tool2).getFactory());
        }
        for (Object tool2 : this.tools) {
            Circuit circuit = (Circuit)tool2.getFactory();
            for (Component comp : circuit.getNonWires()) {
                if (!factories.contains(comp.getFactory())) continue;
                return StringUtil.format(Strings.get("unloadUsedError"), circuit.getName());
            }
        }
        ToolbarData tb = this.options.getToolbarData();
        MouseMappings mm = this.options.getMouseMappings();
        for (Tool t : lib.getTools()) {
            if (tb.usesToolFromSource(t)) {
                return Strings.get("unloadToolbarError");
            }
            if (!mm.usesToolFromSource(t)) continue;
            return Strings.get("unloadMappingError");
        }
        return null;
    }

    public void setMainCircuit(Circuit circuit) {
        if (circuit == null) {
            return;
        }
        this.main = circuit;
        this.fireEvent(4, circuit);
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    void write(Writer writer, LibraryLoader loader) throws IOException {
        XmlWriter out = new XmlWriter(loader);
        Object data = out.initialize(this);
        out.output(data, writer);
    }

    public LogisimFile cloneLogisimFile(Loader newloader) {
        PipedReader reader = new PipedReader();
        PipedWriter writer = new PipedWriter();
        try {
            reader.connect(writer);
        }
        catch (IOException e) {
            newloader.showError(StringUtil.format(Strings.get("fileDuplicateError"), e.toString()));
            return null;
        }
        new WritingThread(writer, this).start();
        try {
            return LogisimFile.load(reader, newloader);
        }
        catch (IOException e) {
            newloader.showError(StringUtil.format(Strings.get("fileDuplicateError"), e.toString()));
            return null;
        }
    }

    Tool findTool(Tool query) {
        for (Library lib : this.getLibraries()) {
            Tool ret = this.findTool(lib, query);
            if (ret == null) continue;
            return ret;
        }
        return null;
    }

    private Tool findTool(Library lib, Tool query) {
        for (Tool tool : lib.getTools()) {
            if (!tool.equals(query)) continue;
            return tool;
        }
        return null;
    }

    public static LogisimFile createNew(Loader loader) {
        LogisimFile ret = new LogisimFile(loader);
        ret.main = new Circuit("main");
        ret.tools.add(new AddTool(ret.main));
        return ret;
    }

    public static LogisimFile load(Reader reader, Loader loader) throws IOException {
        BufferedReader buf = new BufferedReader(reader);
        buf.mark(128);
        String firstLine = buf.readLine();
        buf.reset();
        if (firstLine == null) {
            throw new IOException("File is empty");
        }
        if (firstLine.equals("Logisim v1.0")) {
            StringWriter out = new StringWriter();
            Version1Support.translate(buf, out);
            reader = new StringReader(out.toString());
        } else {
            reader = buf;
        }
        XmlReader in = new XmlReader(loader);
        LogisimFile ret = in.readLibrary(reader);
        ret.loader = loader;
        return ret;
    }

    private static class WritingThread
    extends Thread {
        Writer writer;
        LogisimFile file;

        WritingThread(Writer writer, LogisimFile file) {
            this.writer = writer;
            this.file = file;
        }

        @Override
        public void run() {
            try {
                this.file.write(this.writer, this.file.loader);
            }
            catch (IOException e) {
                this.file.loader.showError(StringUtil.format(Strings.get("fileDuplicateError"), e.toString()));
            }
            try {
                this.writer.close();
            }
            catch (IOException e) {
                this.file.loader.showError(StringUtil.format(Strings.get("fileDuplicateError"), e.toString()));
            }
        }
    }

}

