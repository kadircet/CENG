/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.proj;

import com.cburch.logisim.Main;
import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.start.Startup;
import com.cburch.logisim.proj.Strings;
import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.PropertyChangeWeakSupport;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

public class LogisimPreferences {
    public static final int TEMPLATE_UNKNOWN = -1;
    public static final int TEMPLATE_EMPTY = 0;
    public static final int TEMPLATE_PLAIN = 1;
    public static final int TEMPLATE_CUSTOM = 2;
    public static final String TEMPLATE = "template";
    public static final String TEMPLATE_TYPE = "templateType";
    public static final String TEMPLATE_FILE = "templateFile";
    public static final String ACCENTS_REPLACE = "accentsReplace";
    public static final String GATE_SHAPE = "gateShape";
    public static final String GRAPHICS_ACCELERATION = "graphicsAcceleration";
    public static final String STRETCH_WIRES = "stretchWires";
    public static final String SHAPE_SHAPED = "shaped";
    public static final String SHAPE_RECTANGULAR = "rectangular";
    public static final String SHAPE_DIN40700 = "din40700";
    public static final String ACCEL_DEFAULT = "default";
    public static final String ACCEL_NONE = "none";
    public static final String ACCEL_OPENGL = "opengl";
    public static final String ACCEL_D3D = "d3d";
    private static int templateType = 1;
    private static File templateFile = null;
    private static boolean accentsReplace = false;
    private static String gateShape = "shaped";
    private static String graphicsAccel = "default";
    private static boolean stretchWires = false;
    private static Preferences prefs = null;
    private static MyListener myListener = null;
    private static PropertyChangeWeakSupport propertySupport = new PropertyChangeWeakSupport(LogisimPreferences.class);
    private static LogisimFile plainTemplate = null;
    private static LogisimFile emptyTemplate = null;
    private static LogisimFile customTemplate = null;
    private static File customTemplateFile = null;

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static Preferences getPrefs() {
        if (prefs != null) return prefs;
        reference var0 = LogisimPreferences.class;
        synchronized (LogisimPreferences.class) {
            if (prefs != null) return prefs;
            {
                Preferences p = Preferences.userNodeForPackage(Main.class);
                myListener = new MyListener();
                p.addPreferenceChangeListener(myListener);
                prefs = p;
                LogisimPreferences.setGraphicsAcceleration(p.get("graphicsAcceleration", "default"));
                LogisimPreferences.setStretchWires(p.getBoolean("stretchWires", false));
                LogisimPreferences.setAccentsReplace(p.getBoolean("accentsReplace", false));
                LogisimPreferences.setGateShape(p.get("gateShape", "shaped"));
                LogisimPreferences.setTemplateFile(LogisimPreferences.convertFile(p.get("templateFile", null)));
                LogisimPreferences.setTemplateType(p.getInt("templateType", 1));
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return prefs;
        }
    }

    private static File convertFile(String fileName) {
        if (fileName == null || fileName.equals("")) {
            return null;
        }
        File file = new File(fileName);
        return file.canRead() ? file : null;
    }

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public static void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    public static void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    public static void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }

    public static int getTemplateType() {
        LogisimPreferences.getPrefs();
        return templateType;
    }

    public static void setTemplateType(int value) {
        LogisimPreferences.getPrefs();
        if (value != 1 && value != 0 && value != 2) {
            value = -1;
        }
        if (value == 2 && templateFile == null) {
            value = -1;
        }
        if (value != -1 && templateType != value) {
            LogisimPreferences.getPrefs().putInt("templateType", value);
        }
    }

    public static File getTemplateFile() {
        LogisimPreferences.getPrefs();
        return templateFile;
    }

    private static void setTemplateFile(File value) {
        LogisimPreferences.getPrefs();
        LogisimPreferences.setTemplateFile(value, null);
    }

    public static void setTemplateFile(File value, LogisimFile template) {
        LogisimPreferences.getPrefs();
        if (value != null && !value.canRead()) {
            value = null;
        }
        if (value == null ? templateFile != null : !value.equals(templateFile)) {
            try {
                customTemplateFile = template == null ? null : value;
                customTemplate = template;
                LogisimPreferences.getPrefs().put("templateFile", value == null ? "" : value.getCanonicalPath());
            }
            catch (IOException ex) {
                // empty catch block
            }
        }
    }

    public static String getGraphicsAcceleration() {
        LogisimPreferences.getPrefs();
        return graphicsAccel;
    }

    public static void setGraphicsAcceleration(String value) {
        LogisimPreferences.getPrefs();
        if (graphicsAccel != value) {
            LogisimPreferences.getPrefs().put("graphicsAcceleration", value.toLowerCase());
        }
    }

    public static void handleGraphicsAcceleration() {
        String accel = LogisimPreferences.getGraphicsAcceleration();
        try {
            if (accel == "none") {
                System.setProperty("sun.java2d.opengl", "False");
                System.setProperty("sun.java2d.d3d", "False");
            } else if (accel == "opengl") {
                System.setProperty("sun.java2d.opengl", "True");
                System.setProperty("sun.java2d.d3d", "False");
            } else if (accel == "d3d") {
                System.setProperty("sun.java2d.opengl", "False");
                System.setProperty("sun.java2d.d3d", "True");
            }
        }
        catch (Throwable t) {
            // empty catch block
        }
    }

    public static boolean getStretchWires() {
        LogisimPreferences.getPrefs();
        return stretchWires;
    }

    public static void setStretchWires(boolean value) {
        LogisimPreferences.getPrefs();
        if (stretchWires != value) {
            LogisimPreferences.getPrefs().putBoolean("stretchWires", value);
        }
    }

    public static boolean getAccentsReplace() {
        LogisimPreferences.getPrefs();
        return accentsReplace;
    }

    public static void setAccentsReplace(boolean value) {
        LogisimPreferences.getPrefs();
        if (accentsReplace != value) {
            LogisimPreferences.getPrefs().putBoolean("accentsReplace", value);
        }
    }

    public static String getGateShape() {
        LogisimPreferences.getPrefs();
        return gateShape;
    }

    public static void setGateShape(String value) {
        LogisimPreferences.getPrefs();
        if (!gateShape.equals(value)) {
            LogisimPreferences.getPrefs().put("gateShape", value.toLowerCase());
        }
    }

    public static LogisimFile getTemplate(Loader loader) {
        LogisimPreferences.getPrefs();
        switch (templateType) {
            case 1: {
                return LogisimPreferences.getPlainTemplate(loader);
            }
            case 0: {
                return LogisimPreferences.getEmptyTemplate(loader);
            }
            case 2: {
                return LogisimPreferences.getCustomTemplate(loader);
            }
        }
        return LogisimPreferences.getPlainTemplate(loader);
    }

    private static LogisimFile getPlainTemplate(Loader loader) {
        Circuit circ;
        if (plainTemplate == null) {
            ClassLoader ld = Startup.class.getClassLoader();
            InputStream in = ld.getResourceAsStream("com/cburch/logisim/resources/default.templ");
            if (in == null) {
                plainTemplate = LogisimPreferences.getEmptyTemplate(loader);
            } else {
                try {
                    InputStreamReader templReader = new InputStreamReader(in);
                    plainTemplate = loader.openLogisimFile(templReader);
                    templReader.close();
                }
                catch (Throwable e) {
                    plainTemplate = LogisimPreferences.getEmptyTemplate(loader);
                }
            }
        }
        if ((circ = plainTemplate.getCircuit("main")) != null) {
            circ.setName(Strings.get("newCircuitName"));
        }
        return plainTemplate;
    }

    private static LogisimFile getEmptyTemplate(Loader loader) {
        Circuit circ;
        if (emptyTemplate == null) {
            emptyTemplate = LogisimFile.createNew(loader);
        }
        if ((circ = emptyTemplate.getCircuit("main")) != null) {
            circ.setName(Strings.get("newCircuitName"));
        }
        return emptyTemplate;
    }

    private static LogisimFile getCustomTemplate(Loader loader) {
        if (customTemplateFile == null || !customTemplateFile.equals(templateFile)) {
            if (templateFile == null) {
                customTemplate = null;
                customTemplateFile = null;
            } else {
                try {
                    customTemplate = loader.openLogisimFile(templateFile);
                    customTemplateFile = templateFile;
                }
                catch (Throwable t) {
                    LogisimPreferences.setTemplateFile(null);
                    customTemplate = null;
                    customTemplateFile = null;
                }
            }
        }
        return customTemplate == null ? LogisimPreferences.getPlainTemplate(loader) : customTemplate;
    }

    private static class MyListener
    implements PreferenceChangeListener {
        private MyListener() {
        }

        @Override
        public void preferenceChange(PreferenceChangeEvent event) {
            Preferences prefs = event.getNode();
            String prop = event.getKey();
            if (prop.equals("accentsReplace")) {
                boolean oldValue = accentsReplace;
                boolean value = prefs.getBoolean("accentsReplace", false);
                if (value != oldValue) {
                    accentsReplace = value;
                    LocaleManager.setReplaceAccents(accentsReplace);
                    propertySupport.firePropertyChange("accentsReplace", oldValue, value);
                }
            } else if (prop.equals("stretchWires")) {
                boolean oldValue = stretchWires;
                boolean value = prefs.getBoolean("stretchWires", false);
                if (value != oldValue) {
                    stretchWires = value;
                    propertySupport.firePropertyChange("stretchWires", oldValue, value);
                }
            } else if (prop.equals("gateShape")) {
                String oldValue = gateShape;
                String value = prefs.get("gateShape", "shaped").toLowerCase();
                if (!value.equals(oldValue)) {
                    if (value.equals("rectangular")) {
                        gateShape = "rectangular";
                    } else if (value.equals("din40700")) {
                        gateShape = "din40700";
                    } else {
                        gateShape = "shaped";
                    }
                    propertySupport.firePropertyChange("gateShape", oldValue, value);
                }
            } else if (prop.equals("templateType")) {
                int oldValue = templateType;
                int value = prefs.getInt("templateType", -1);
                if (value != oldValue) {
                    templateType = value;
                    propertySupport.firePropertyChange("template", oldValue, value);
                    propertySupport.firePropertyChange("templateType", oldValue, value);
                }
            } else if (prop.equals("templateFile")) {
                File oldValue = templateFile;
                File value = LogisimPreferences.convertFile(prefs.get("templateFile", null));
                if (value == null ? oldValue != null : !value.equals(oldValue)) {
                    templateFile = value;
                    if (templateType == 2) {
                        customTemplate = null;
                        propertySupport.firePropertyChange("template", oldValue, value);
                    }
                    propertySupport.firePropertyChange("templateFile", oldValue, value);
                }
            } else if (prop.equals("graphicsAcceleration")) {
                String oldValue = graphicsAccel;
                String value = prefs.get("graphicsAcceleration", "default").toLowerCase();
                if (!value.equals(oldValue)) {
                    if (value.equals("none")) {
                        graphicsAccel = "none";
                    } else if (value.equals("opengl")) {
                        graphicsAccel = "opengl";
                    } else if (value.equals("d3d")) {
                        graphicsAccel = "d3d";
                    } else {
                        graphicsAccel = "default";
                    }
                    propertySupport.firePropertyChange("graphicsAcceleration", oldValue, value);
                }
            }
        }
    }

}

