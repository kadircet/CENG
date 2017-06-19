/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.start;

import com.cburch.logisim.file.LoadFailedException;
import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.main.Print;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.gui.menu.WindowManagers;
import com.cburch.logisim.gui.start.MacOsAdapter;
import com.cburch.logisim.gui.start.SplashScreen;
import com.cburch.logisim.gui.start.Strings;
import com.cburch.logisim.proj.LogisimPreferences;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.ProjectActions;
import com.cburch.logisim.std.Builtin;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.MacCompatibility;
import com.cburch.logisim.util.StringUtil;
import java.awt.Component;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.UIManager;
import tr.edu.metu.ceng.ceng232.grader.Settings;

public class Startup {
    private static Startup startupTemp = null;
    private File templFile = null;
    private boolean templEmpty = false;
    private boolean templPlain = false;
    private ArrayList filesToOpen = new ArrayList();
    private boolean showSplash = true;
    private boolean initialized = false;
    private SplashScreen monitor = null;
    private ArrayList filesToPrint = new ArrayList();

    static void doOpen(File file) {
        if (startupTemp != null) {
            startupTemp.doOpenFile(file);
        }
    }

    static void doPrint(File file) {
        if (startupTemp != null) {
            startupTemp.doPrintFile(file);
        }
    }

    private void doOpenFile(File file) {
        if (this.initialized) {
            ProjectActions.doOpen(null, null, file);
        } else {
            this.filesToOpen.add(file);
        }
    }

    private void doPrintFile(File file) {
        if (this.initialized) {
            Project toPrint = ProjectActions.doOpen(null, null, file);
            Print.doPrint(toPrint);
            toPrint.getFrame().dispose();
        } else {
            this.filesToPrint.add(file);
        }
    }

    private static void registerHandler() {
        try {
            Class needed1 = Class.forName("com.apple.eawt.Application");
            if (needed1 == null) {
                return;
            }
            Class needed2 = Class.forName("com.apple.eawt.ApplicationAdapter");
            if (needed2 == null) {
                return;
            }
            MacOsAdapter.register();
            MacOsAdapter.addListeners(true);
        }
        catch (ClassNotFoundException e) {
            return;
        }
        catch (Throwable t) {
            try {
                MacOsAdapter.addListeners(false);
            }
            catch (Throwable t2) {
                // empty catch block
            }
        }
    }

    private Startup() {
    }

    public void run() {
        Loader templLoader;
        int count;
        Project proj;
        int i;
        if (this.showSplash) {
            try {
                this.monitor = new SplashScreen();
                this.monitor.setVisible(true);
            }
            catch (Throwable t) {
                this.monitor = null;
                this.showSplash = false;
            }
        }
        if (this.showSplash) {
            this.monitor.setProgress(0);
        }
        if ((count = (templLoader = new Loader(this.monitor)).getBuiltin().getLibrary("Base").getTools().size() + templLoader.getBuiltin().getLibrary("Gates").getTools().size()) < 0) {
            System.err.println("FATAL ERROR - no components");
            System.exit(-1);
        }
        this.loadTemplate(templLoader, this.templFile, this.templEmpty);
        if (this.showSplash) {
            this.monitor.setProgress(5);
        }
        WindowManagers.initialize();
        if (MacCompatibility.isSwingUsingScreenMenuBar()) {
            MacCompatibility.setFramelessJMenuBar(new LogisimMenuBar(null, null));
        } else {
            new LogisimMenuBar(null, null);
        }
        this.initialized = true;
        if (this.filesToOpen.isEmpty()) {
            proj = ProjectActions.doNew(this.monitor);
            proj.setStartupScreen(true);
            if (this.showSplash) {
                this.monitor.close();
            }
        } else {
            for (i = 0; i < this.filesToOpen.size(); ++i) {
                File fileToOpen = (File)this.filesToOpen.get(i);
                try {
                    proj = ProjectActions.doOpen(this.monitor, fileToOpen);
                }
                catch (LoadFailedException ex) {
                    System.err.println(fileToOpen.getName() + ": " + ex.getMessage());
                    System.exit(-1);
                }
                if (i != 0) continue;
                if (this.showSplash) {
                    this.monitor.close();
                }
                this.monitor = null;
            }
        }
        for (i = 0; i < this.filesToPrint.size(); ++i) {
            File fileToPrint = (File)this.filesToPrint.get(i);
            this.doPrintFile(fileToPrint);
        }
    }

    private static void setLocale(String lang) {
        int i;
        Locale[] opts = Strings.getLocaleOptions();
        for (i = 0; i < opts.length; ++i) {
            if (!lang.equals(opts[i].toString())) continue;
            LocaleManager.setLocale(opts[i]);
            return;
        }
        System.err.println(Strings.get("invalidLocaleError"));
        System.err.println(Strings.get("invalidLocaleOptionsHeader"));
        for (i = 0; i < opts.length; ++i) {
            System.err.println("   " + opts[i].toString());
        }
        System.exit(-1);
    }

    private void loadTemplate(Loader loader, File templFile, boolean templEmpty) {
        if (this.showSplash) {
            this.monitor.setProgress(2);
        }
        if (templFile != null) {
            try {
                LogisimFile templ = loader.openLogisimFile(templFile);
                LogisimPreferences.setTemplateFile(templFile, templ);
                LogisimPreferences.setTemplateType(2);
            }
            catch (LoadFailedException e) {
                if (this.showSplash) {
                    this.monitor.close();
                }
                System.exit(-1);
            }
        } else if (templEmpty) {
            LogisimPreferences.setTemplateType(0);
        } else if (this.templPlain) {
            LogisimPreferences.setTemplateType(1);
        }
        LogisimPreferences.getTemplate(loader);
    }

    public static Startup parseArgs(String[] args) {
        Startup ret;
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Logisim");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        LocaleManager.setReplaceAccents(false);
        LogisimPreferences.handleGraphicsAcceleration();
        startupTemp = ret = new Startup();
        Startup.registerHandler();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
            // empty catch block
        }
        for (int i = 0; i < args.length; ++i) {
            String a;
            String arg = args[i];
            if (arg.equals("-empty")) {
                if (ret.templFile != null || ret.templEmpty || ret.templPlain) {
                    System.err.println(Strings.get("argOneTemplateError"));
                    return null;
                }
                ret.templEmpty = true;
                continue;
            }
            if (arg.equals("-plain")) {
                if (ret.templFile != null || ret.templEmpty || ret.templPlain) {
                    System.err.println(Strings.get("argOneTemplateError"));
                    return null;
                }
                ret.templPlain = true;
                continue;
            }
            if (arg.equals("-version")) {
                System.out.println("2.1.8");
                return null;
            }
            if (arg.equals("-gates")) {
                if (++i >= args.length) {
                    Startup.printUsage();
                }
                if ((a = args[i]).equals("shaped")) {
                    LogisimPreferences.setGateShape("shaped");
                    continue;
                }
                if (a.equals("rectangular")) {
                    LogisimPreferences.setGateShape("rectangular");
                    continue;
                }
                System.err.println(Strings.get("argGatesOptionError"));
                System.exit(-1);
                continue;
            }
            if (arg.equals("-locale")) {
                if (++i >= args.length) {
                    Startup.printUsage();
                }
                Startup.setLocale(args[i]);
                continue;
            }
            if (arg.equals("-accents")) {
                if (++i >= args.length) {
                    Startup.printUsage();
                }
                if ((a = args[i]).equals("yes")) {
                    LogisimPreferences.setAccentsReplace(false);
                    continue;
                }
                if (a.equals("no")) {
                    LogisimPreferences.setAccentsReplace(true);
                    continue;
                }
                System.err.println(Strings.get("argAccentsOptionError"));
                System.exit(-1);
                continue;
            }
            if (arg.equals("-template")) {
                if (ret.templFile != null || ret.templEmpty || ret.templPlain) {
                    System.err.println(Strings.get("argOneTemplateError"));
                    return null;
                }
                if (++i >= args.length) {
                    Startup.printUsage();
                }
                ret.templFile = new File(args[i]);
                if (!ret.templFile.exists()) {
                    System.err.println(StringUtil.format(Strings.get("templateMissingError"), args[i]));
                    continue;
                }
                if (ret.templFile.canRead()) continue;
                System.err.println(StringUtil.format(Strings.get("templateCannotReadError"), args[i]));
                continue;
            }
            if (arg.equals("-nosplash")) {
                ret.showSplash = false;
                continue;
            }
            if (arg.equals("-grader")) {
                if (++i >= args.length) {
                    Startup.printUsage();
                }
                Settings.loadGradingFile(new File(args[i]));
                continue;
            }
            if (arg.charAt(0) == '-') {
                Startup.printUsage();
                return null;
            }
            ret.filesToOpen.add(new File(arg));
        }
        return ret;
    }

    private static void printUsage() {
        System.err.println(StringUtil.format(Strings.get("argUsage"), Startup.class.getName()));
        System.err.println();
        System.err.println(Strings.get("argOptionHeader"));
        System.err.println("   " + Strings.get("argEmptyOption"));
        System.err.println("   " + Strings.get("argPlainOption"));
        System.err.println("   " + Strings.get("argTemplateOption"));
        System.err.println("   " + Strings.get("argGatesOption"));
        System.err.println("   " + Strings.get("argLocaleOption"));
        System.err.println("   " + Strings.get("argAccentsOption"));
        System.err.println("   " + Strings.get("argNoSplashOption"));
        System.err.println("   " + Strings.get("argVersionOption"));
        System.err.println("   " + Strings.get("argHelpOption"));
        System.exit(-1);
    }
}

