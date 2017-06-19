/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.apple.eio.FileManager
 *  com.apple.mrj.MRJFileUtils
 *  com.apple.mrj.MRJOSType
 */
package net.roydesign.mac;

import com.apple.eio.FileManager;
import com.apple.mrj.MRJFileUtils;
import com.apple.mrj.MRJOSType;
import java.awt.Frame;
import java.awt.MenuBar;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import net.roydesign.mac.MRJ23EventProxy;
import net.roydesign.mac.MRJ4EventProxy;
import net.roydesign.mac.MRJFolderConstants;

public final class MRJAdapter
implements MRJFolderConstants {
    public static final String VERSION = "1.0.9";
    public static float javaVersion;
    public static float mrjVersion;
    public static boolean useMacBinaryToolkit;
    public static boolean useBrowserLauncher;
    private static ClassLoader cocoaClassLoader;
    private static String startupDisk;
    private static String applicationPath;
    private static Frame invisibleFrame;
    private static Method getResourceMethod;
    private static Method getResourceSubMethod;
    static /* synthetic */ Class class$0;
    static /* synthetic */ Class class$1;

    static {
        mrjVersion = -1.0f;
        useMacBinaryToolkit = true;
        useBrowserLauncher = true;
        String prop = System.getProperty("java.version");
        javaVersion = new Float(prop.substring(0, 3)).floatValue();
        prop = System.getProperty("mrj.version");
        if (prop != null) {
            int len = prop.length();
            int pos = prop.indexOf(46);
            if (pos != -1 && pos != len - 1) {
                pos = prop.indexOf(46, pos + 1);
            }
            if (pos == -1) {
                pos = len;
            }
            mrjVersion = new Float(prop.substring(0, pos)).floatValue();
        }
        if (mrjVersion >= 3.0f) {
            try {
                cocoaClassLoader = new URLClassLoader(new URL[]{new URL("file://127.0.0.1/System/Library/Java/")});
            }
            catch (MalformedURLException len) {
                // empty catch block
            }
        }
    }

    private MRJAdapter() {
    }

    public static void setFileType(File file, String type) throws IOException {
        if (mrjVersion >= 4.0f) {
            FileManager.setFileType((String)file.getAbsolutePath(), (int)MRJAdapter.fourCharCodeToInt(type));
        } else if (mrjVersion >= 1.5f) {
            MRJFileUtils.setFileType((File)file, (MRJOSType)new MRJOSType(MRJAdapter.fourCharCodeToInt(type)));
        }
    }

    public static String getFileType(File file) throws IOException {
        if (mrjVersion >= 4.0f) {
            long t = FileManager.getFileType((String)file.getAbsolutePath());
            return MRJAdapter.intToFourCharCode((int)t);
        }
        if (file.isDirectory()) {
            File infoPlist = new File(file, "Contents/Info.plist");
            if (infoPlist.exists()) {
                File pkgInfo = new File(file, "Contents/PkgInfo");
                if (pkgInfo.exists()) {
                    String t = MRJAdapter.parsePkgInfo(pkgInfo, "type");
                    return t == null ? "" : t;
                }
                String t = MRJAdapter.parseInfoPlist(infoPlist, "CFBundlePackageType");
                return t == null ? "" : t;
            }
        } else if (mrjVersion >= 1.5f) {
            MRJOSType t = MRJFileUtils.getFileType((File)file);
            return t.toInt() == 0 ? "" : t.toString();
        }
        return "";
    }

    public static void setFileCreator(File file, String creator) throws IOException {
        if (mrjVersion >= 4.0f) {
            FileManager.setFileCreator((String)file.getAbsolutePath(), (int)MRJAdapter.fourCharCodeToInt(creator));
        } else if (mrjVersion >= 1.5f) {
            MRJFileUtils.setFileCreator((File)file, (MRJOSType)new MRJOSType(MRJAdapter.fourCharCodeToInt(creator)));
        }
    }

    public static String getFileCreator(File file) throws IOException {
        if (mrjVersion >= 4.0f) {
            long c = FileManager.getFileCreator((String)file.getAbsolutePath());
            return MRJAdapter.intToFourCharCode((int)c);
        }
        if (file.isDirectory()) {
            File infoPlist = new File(file, "Contents/Info.plist");
            if (infoPlist.exists()) {
                File pkgInfo = new File(file, "Contents/PkgInfo");
                if (pkgInfo.exists()) {
                    String t = MRJAdapter.parsePkgInfo(pkgInfo, "creator");
                    return t == null ? "" : t;
                }
                String t = MRJAdapter.parseInfoPlist(infoPlist, "CFBundleSignature");
                return t == null ? "" : t;
            }
        } else if (mrjVersion >= 1.5f) {
            MRJOSType t = MRJFileUtils.getFileCreator((File)file);
            return t.toInt() == 0 ? "" : t.toString();
        }
        return "";
    }

    public static void setFileCreatorAndType(File file, String creator, String type) throws IOException {
        if (mrjVersion >= 4.0f) {
            FileManager.setFileTypeAndCreator((String)file.getAbsolutePath(), (int)MRJAdapter.fourCharCodeToInt(type), (int)MRJAdapter.fourCharCodeToInt(creator));
        } else if (mrjVersion >= 1.5f) {
            MRJFileUtils.setFileTypeAndCreator((File)file, (MRJOSType)new MRJOSType(MRJAdapter.fourCharCodeToInt(type)), (MRJOSType)new MRJOSType(MRJAdapter.fourCharCodeToInt(creator)));
        }
    }

    public static boolean setFileLastModified(File file, long time) {
        if (javaVersion >= 1.2f) {
            return file.setLastModified(time);
        }
        if (mrjVersion >= 1.5f) {
            return MRJFileUtils.setFileLastModified((File)file, (long)time);
        }
        return false;
    }

    public static File findFolder(short domain, int type, boolean create) throws FileNotFoundException {
        if (mrjVersion >= 4.0f) {
            return new File(FileManager.findFolder((short)domain, (int)type, (boolean)create));
        }
        if (mrjVersion >= 3.2f) {
            return MRJFileUtils.findFolder((short)domain, (MRJOSType)new MRJOSType(type), (boolean)create);
        }
        if (mrjVersion >= 3.0f) {
            return MRJFileUtils.findFolder((short)domain, (MRJOSType)new MRJOSType(type));
        }
        if (mrjVersion >= 1.5f) {
            return MRJFileUtils.findFolder((MRJOSType)new MRJOSType(type));
        }
        throw new FileNotFoundException();
    }

    public static File findFolder(short domain, String type, boolean create) throws FileNotFoundException {
        return MRJAdapter.findFolder(domain, MRJAdapter.fourCharCodeToInt(type), create);
    }

    public static File findApplication(String creator) throws FileNotFoundException {
        if (mrjVersion >= 3.0f) {
            try {
                StringBuffer script = new StringBuffer();
                script.append("tell application \"Finder\" to get POSIX path of (application file id \"");
                script.append(creator);
                script.append("\" as alias)");
                return new File(MRJAdapter.runAppleScript(script.toString()));
            }
            catch (IOException script) {}
        } else if (mrjVersion >= 1.5f) {
            return MRJFileUtils.findApplication((MRJOSType)new MRJOSType(MRJAdapter.fourCharCodeToInt(creator)));
        }
        throw new FileNotFoundException();
    }

    public static File getBundleResource(String resource) throws FileNotFoundException {
        if (mrjVersion >= 4.0f) {
            return new File(FileManager.getResource((String)resource));
        }
        if (mrjVersion >= 3.0f) {
            try {
                if (getResourceMethod == null) {
                    Class class_;
                    Class[] arrclass;
                    Class cls;
                    cls = Class.forName("com.apple.mrj.MRJFileUtils");
                    arrclass = new Class[1];
                    class_ = class$0;
                    if (class_ == null) {
                        try {
                            class_ = MRJAdapter.class$0 = Class.forName("java.lang.String");
                        }
                        catch (ClassNotFoundException v2) {
                            throw new NoClassDefFoundError(v2.getMessage());
                        }
                    }
                    arrclass[0] = class_;
                    getResourceMethod = cls.getMethod("getResource", arrclass);
                }
                return (File)getResourceMethod.invoke(null, resource);
            }
            catch (Exception cls) {
                // empty catch block
            }
        }
        throw new FileNotFoundException();
    }

    public static File getBundleResource(String resource, String subFolder) throws FileNotFoundException {
        if (mrjVersion >= 4.0f) {
            return new File(FileManager.getResource((String)resource, (String)subFolder));
        }
        if (mrjVersion >= 3.0f) {
            try {
                if (getResourceSubMethod == null) {
                    Class[] arrclass;
                    Class class_;
                    Class cls;
                    Class class_2;
                    cls = Class.forName("com.apple.mrj.MRJFileUtils");
                    arrclass = new Class[2];
                    class_2 = class$0;
                    if (class_2 == null) {
                        try {
                            class_2 = MRJAdapter.class$0 = Class.forName("java.lang.String");
                        }
                        catch (ClassNotFoundException v2) {
                            throw new NoClassDefFoundError(v2.getMessage());
                        }
                    }
                    arrclass[0] = class_2;
                    class_ = class$0;
                    if (class_ == null) {
                        try {
                            class_ = MRJAdapter.class$0 = Class.forName("java.lang.String");
                        }
                        catch (ClassNotFoundException v4) {
                            throw new NoClassDefFoundError(v4.getMessage());
                        }
                    }
                    arrclass[1] = class_;
                    getResourceSubMethod = cls.getMethod("getResource", arrclass);
                }
                return (File)getResourceSubMethod.invoke(null, resource, subFolder);
            }
            catch (Exception cls) {
                // empty catch block
            }
        }
        throw new FileNotFoundException();
    }

    public static InputStream openFileResourceFork(File file) throws FileNotFoundException {
        File rf;
        if (useMacBinaryToolkit) {
            try {
                Class pathnameClass;
                Object ff;
                Class fileForkerClass;
                Class[] arrclass;
                Class class_;
                Method setFactoryMethod;
                Class macPlatformClass;
                Class[] arrclass2;
                Class class_2;
                Class class_3;
                Class[] arrclass3;
                fileForkerClass = Class.forName("glguerin.io.FileForker");
                arrclass3 = new Class[1];
                class_3 = class$0;
                if (class_3 == null) {
                    try {
                        class_3 = MRJAdapter.class$0 = Class.forName("java.lang.String");
                    }
                    catch (ClassNotFoundException v2) {
                        throw new NoClassDefFoundError(v2.getMessage());
                    }
                }
                arrclass3[0] = class_3;
                setFactoryMethod = fileForkerClass.getMethod("SetFactory", arrclass3);
                macPlatformClass = Class.forName("glguerin.util.MacPlatform");
                arrclass2 = new Class[1];
                class_2 = class$0;
                if (class_2 == null) {
                    try {
                        class_2 = MRJAdapter.class$0 = Class.forName("java.lang.String");
                    }
                    catch (ClassNotFoundException v5) {
                        throw new NoClassDefFoundError(v5.getMessage());
                    }
                }
                arrclass2[0] = class_2;
                Method selectFactoryNameMethod = macPlatformClass.getMethod("selectFactoryName", arrclass2);
                String fctry = (String)selectFactoryNameMethod.invoke(null, new Object[1]);
                setFactoryMethod.invoke(null, fctry);
                Method makeOneMethod = fileForkerClass.getMethod("MakeOne", null);
                ff = makeOneMethod.invoke(null, null);
                pathnameClass = Class.forName("glguerin.io.Pathname");
                arrclass = new Class[1];
                class_ = class$1;
                if (class_ == null) {
                    try {
                        class_ = MRJAdapter.class$1 = Class.forName("java.io.File");
                    }
                    catch (ClassNotFoundException v8) {
                        throw new NoClassDefFoundError(v8.getMessage());
                    }
                }
                arrclass[0] = class_;
                Constructor pathnameConstructor = pathnameClass.getConstructor(arrclass);
                Object path = pathnameConstructor.newInstance(file);
                Method setTargetMethod = fileForkerClass.getMethod("setTarget", pathnameClass);
                setTargetMethod.invoke(ff, path);
                Method makeForkInputStreamMethod = fileForkerClass.getMethod("makeForkInputStream", Boolean.TYPE);
                return (InputStream)makeForkInputStreamMethod.invoke(ff, Boolean.TRUE);
            }
            catch (Exception ex) {
                useMacBinaryToolkit = false;
            }
        }
        if (mrjVersion >= 3.0f && (rf = new File(file, "/..namedfork/rsrc")).length() > 0) {
            return new FileInputStream(rf);
        }
        File fo = new File(file.getParent(), ".HSResource");
        File rf2 = new File(fo, file.getName());
        if (rf2.exists()) {
            return new FileInputStream(rf2);
        }
        throw new FileNotFoundException();
    }

    public static void openURL(String url) throws IOException {
        if (useBrowserLauncher && mrjVersion < 4.0f) {
            try {
                Class class_;
                Class[] arrclass;
                Class browserLauncherClass;
                browserLauncherClass = Class.forName("edu.stanford.ejalbert.BrowserLauncher");
                arrclass = new Class[1];
                class_ = class$0;
                if (class_ == null) {
                    try {
                        class_ = MRJAdapter.class$0 = Class.forName("java.lang.String");
                    }
                    catch (ClassNotFoundException v2) {
                        throw new NoClassDefFoundError(v2.getMessage());
                    }
                }
                arrclass[0] = class_;
                Method openURLMethod = browserLauncherClass.getMethod("openURL", arrclass);
                openURLMethod.invoke(null, url);
                return;
            }
            catch (Exception ex) {
                useBrowserLauncher = false;
            }
        }
        if (mrjVersion >= 4.0f) {
            Runtime.getRuntime().exec(new String[]{"open", url});
        } else if (mrjVersion >= 2.2f) {
            MRJFileUtils.openURL((String)url);
        } else if (mrjVersion >= 1.5f) {
            File finder = MRJFileUtils.findApplication((MRJOSType)new MRJOSType("MACS"));
            Runtime.getRuntime().exec(new String[]{finder.getPath(), url});
        } else {
            throw new IOException("openURL not supported on this platform");
        }
    }

    public static boolean isAboutAutomaticallyPresent() {
        if (mrjVersion != -1.0f) {
            return true;
        }
        return false;
    }

    public static void addAboutListener(ActionListener l) {
        MRJAdapter.addAboutListener(l, null);
    }

    public static void addAboutListener(ActionListener l, Object source) {
        if (mrjVersion >= 4.0f) {
            MRJ4EventProxy.getInstance().addAboutListener(l, source);
        } else if (mrjVersion >= 1.5f) {
            MRJ23EventProxy.getInstance().addAboutListener(l, source);
        }
    }

    public static void removeAboutListener(ActionListener l) {
        if (mrjVersion >= 4.0f) {
            MRJ4EventProxy.getInstance().removeAboutListener(l);
        } else if (mrjVersion >= 1.5f) {
            MRJ23EventProxy.getInstance().removeAboutListener(l);
        }
    }

    public static boolean isPreferencesAutomaticallyPresent() {
        if (mrjVersion >= 3.0f) {
            return true;
        }
        return false;
    }

    public static void addPreferencesListener(ActionListener l) {
        MRJAdapter.addPreferencesListener(l, null);
    }

    public static void addPreferencesListener(ActionListener l, Object source) {
        if (mrjVersion >= 4.0f) {
            MRJ4EventProxy.getInstance().addPreferencesListener(l, source);
        } else if (mrjVersion >= 3.0f) {
            MRJ23EventProxy.getInstance().addPreferencesListener(l, source);
        }
    }

    public static void removePreferencesListener(ActionListener l) {
        if (mrjVersion >= 4.0f) {
            MRJ4EventProxy.getInstance().removePreferencesListener(l);
        } else if (mrjVersion >= 3.0f) {
            MRJ23EventProxy.getInstance().removePreferencesListener(l);
        }
    }

    public static boolean isPreferencesEnabled() {
        if (mrjVersion >= 4.0f) {
            return MRJ4EventProxy.getInstance().isPreferencesEnabled();
        }
        if (mrjVersion >= 3.0f) {
            return MRJ23EventProxy.getInstance().isPreferencesEnabled();
        }
        return false;
    }

    public static void setPreferencesEnabled(boolean enabled) {
        if (mrjVersion >= 4.0f) {
            MRJ4EventProxy.getInstance().setPreferencesEnabled(enabled);
        } else if (mrjVersion >= 3.0f) {
            MRJ23EventProxy.getInstance().setPreferencesEnabled(enabled);
        }
    }

    public static void addOpenApplicationListener(ActionListener l) {
        MRJAdapter.addOpenApplicationListener(l, null);
    }

    public static void addOpenApplicationListener(ActionListener l, Object source) {
        if (mrjVersion >= 4.0f) {
            MRJ4EventProxy.getInstance().addOpenApplicationListener(l, source);
        } else if (mrjVersion >= 2.2f) {
            MRJ23EventProxy.getInstance().addOpenApplicationListener(l, source);
        }
    }

    public static void removeOpenApplicationListener(ActionListener l) {
        if (mrjVersion >= 4.0f) {
            MRJ4EventProxy.getInstance().removeOpenApplicationListener(l);
        } else if (mrjVersion >= 2.2f) {
            MRJ23EventProxy.getInstance().removeOpenApplicationListener(l);
        }
    }

    public static void addReopenApplicationListener(ActionListener l) {
        MRJAdapter.addReopenApplicationListener(l, null);
    }

    public static void addReopenApplicationListener(ActionListener l, Object source) {
        if (mrjVersion >= 4.0f) {
            MRJ4EventProxy.getInstance().addReopenApplicationListener(l, source);
        } else if (mrjVersion >= 2.2f) {
            MRJ23EventProxy.getInstance().addReopenApplicationListener(l, source);
        }
    }

    public static void removeReopenApplicationListener(ActionListener l) {
        if (mrjVersion >= 4.0f) {
            MRJ4EventProxy.getInstance().removeReopenApplicationListener(l);
        } else if (mrjVersion >= 2.2f) {
            MRJ23EventProxy.getInstance().removeReopenApplicationListener(l);
        }
    }

    public static boolean isQuitAutomaticallyPresent() {
        if (mrjVersion >= 3.0f) {
            return true;
        }
        return false;
    }

    public static void addQuitApplicationListener(ActionListener l) {
        MRJAdapter.addQuitApplicationListener(l, null);
    }

    public static void addQuitApplicationListener(ActionListener l, Object source) {
        if (mrjVersion >= 4.0f) {
            MRJ4EventProxy.getInstance().addQuitApplicationListener(l, source);
        } else if (mrjVersion >= 1.5f) {
            MRJ23EventProxy.getInstance().addQuitApplicationListener(l, source);
        }
    }

    public static void removeQuitApplicationListener(ActionListener l) {
        if (mrjVersion >= 4.0f) {
            MRJ4EventProxy.getInstance().removeQuitApplicationListener(l);
        } else if (mrjVersion >= 1.5f) {
            MRJ23EventProxy.getInstance().removeQuitApplicationListener(l);
        }
    }

    public static void addOpenDocumentListener(ActionListener l) {
        MRJAdapter.addOpenDocumentListener(l, null);
    }

    public static void addOpenDocumentListener(ActionListener l, Object source) {
        if (mrjVersion >= 4.0f) {
            MRJ4EventProxy.getInstance().addOpenDocumentListener(l, source);
        } else if (mrjVersion >= 1.5f) {
            MRJ23EventProxy.getInstance().addOpenDocumentListener(l, source);
        }
    }

    public static void removeOpenDocumentListener(ActionListener l) {
        if (mrjVersion >= 4.0f) {
            MRJ4EventProxy.getInstance().removeOpenDocumentListener(l);
        } else if (mrjVersion >= 1.5f) {
            MRJ23EventProxy.getInstance().removeOpenDocumentListener(l);
        }
    }

    public static void addPrintDocumentListener(ActionListener l) {
        MRJAdapter.addPrintDocumentListener(l, null);
    }

    public static void addPrintDocumentListener(ActionListener l, Object source) {
        if (mrjVersion >= 4.0f) {
            MRJ4EventProxy.getInstance().addPrintDocumentListener(l, source);
        } else if (mrjVersion >= 1.5f) {
            MRJ23EventProxy.getInstance().addPrintDocumentListener(l, source);
        }
    }

    public static void removePrintDocumentListener(ActionListener l) {
        if (mrjVersion >= 4.0f) {
            MRJ4EventProxy.getInstance().removePrintDocumentListener(l);
        } else if (mrjVersion >= 1.5f) {
            MRJ23EventProxy.getInstance().removePrintDocumentListener(l);
        }
    }

    public static boolean isAppleJDirectAvailable() {
        if (MRJAdapter.getAppleJDirectVersion() != -1) {
            return true;
        }
        return false;
    }

    public static int getAppleJDirectVersion() {
        if (mrjVersion >= 3.0f && mrjVersion < 4.0f) {
            return 3;
        }
        if (mrjVersion >= 2.1f && mrjVersion < 3.0f) {
            return 2;
        }
        if (mrjVersion >= 1.5f && mrjVersion < 2.1f) {
            return 1;
        }
        return -1;
    }

    public static boolean isAWTUsingScreenMenuBar() {
        if (mrjVersion != -1.0f) {
            return true;
        }
        return false;
    }

    public static boolean isSwingUsingScreenMenuBar() {
        boolean result = false;
        LookAndFeel laf = UIManager.getLookAndFeel();
        String id = laf.getID();
        String name = laf.getClass().getName();
        if (id.equals("Mac") || id.equals("Aqua")) {
            result = true;
        } else if (mrjVersion >= 4.0f) {
            String prop = System.getProperty("apple.laf.useScreenMenuBar");
            if (prop == null) {
                prop = System.getProperty("com.apple.macos.useScreenMenuBar");
            }
            result = prop != null && prop.equalsIgnoreCase("true") && (name.equals("apple.laf.AquaLookAndFeel") || name.startsWith("ch.randelshofer.quaqua"));
        } else if (mrjVersion >= 3.0f) {
            String prop = System.getProperty("com.apple.macos.useScreenMenuBar");
            result = prop != null && prop.equalsIgnoreCase("true") && (name.equals("com.apple.mrj.swing.MacLookAndFeel") || name.startsWith("ch.randelshofer.quaqua"));
        } else if (mrjVersion != -1.0f) {
            result = name.equals("it.unitn.ing.swing.plaf.macos.MacOSLookAndFeel");
        }
        return result;
    }

    public static void setFramelessMenuBar(MenuBar menuBar) {
        if (invisibleFrame == null) {
            try {
                Class.forName("javax.swing.JFrame");
                invisibleFrame = new InvisibleJFrame();
            }
            catch (Exception ex) {
                invisibleFrame = new Frame();
            }
            if (mrjVersion >= 4.0f) {
                try {
                    Method mthd = invisibleFrame.getClass().getMethod("setUndecorated", Boolean.TYPE);
                    mthd.invoke(invisibleFrame, Boolean.TRUE);
                }
                catch (Exception mthd) {
                    // empty catch block
                }
                invisibleFrame.setLocation(0, 10000);
                invisibleFrame.setSize(0, 0);
                invisibleFrame.pack();
            } else if (mrjVersion != -1.0f) {
                invisibleFrame.setLocation(0, 10000);
                invisibleFrame.pack();
            }
        }
        if (mrjVersion >= 4.0f) {
            if (!invisibleFrame.isVisible()) {
                invisibleFrame.setVisible(true);
            }
        } else if (mrjVersion != -1.0f && !invisibleFrame.isVisible()) {
            invisibleFrame.setVisible(true);
        }
        invisibleFrame.setMenuBar(menuBar);
    }

    public static MenuBar getFramelessMenuBar() {
        if (invisibleFrame != null) {
            return invisibleFrame.getMenuBar();
        }
        return null;
    }

    public static void setFramelessJMenuBar(JMenuBar menuBar) {
        if (invisibleFrame != null && !(invisibleFrame instanceof JFrame)) {
            invisibleFrame.dispose();
            invisibleFrame = null;
        }
        if (MRJAdapter.isSwingUsingScreenMenuBar()) {
            if (mrjVersion >= 4.0f) {
                if (invisibleFrame == null) {
                    invisibleFrame = new InvisibleJFrame();
                    try {
                        Method mthd = invisibleFrame.getClass().getMethod("setUndecorated", Boolean.TYPE);
                        mthd.invoke(invisibleFrame, Boolean.TRUE);
                    }
                    catch (Exception mthd) {
                        // empty catch block
                    }
                    invisibleFrame.setSize(0, 0);
                    invisibleFrame.pack();
                }
                if (!invisibleFrame.isVisible()) {
                    invisibleFrame.setVisible(true);
                }
            } else if (mrjVersion != -1.0f) {
                if (invisibleFrame == null) {
                    invisibleFrame = new InvisibleJFrame();
                    invisibleFrame.setLocation(0, 10000);
                    invisibleFrame.pack();
                }
                if (!invisibleFrame.isVisible()) {
                    invisibleFrame.setVisible(true);
                }
            }
        } else if (invisibleFrame == null) {
            invisibleFrame = new InvisibleJFrame();
        }
        ((JFrame)invisibleFrame).setJMenuBar(menuBar);
        invisibleFrame.pack();
    }

    public static JMenuBar getFramelessJMenuBar() {
        if (invisibleFrame instanceof JFrame) {
            return ((JFrame)invisibleFrame).getJMenuBar();
        }
        return null;
    }

    public static int fourCharCodeToInt(String code) {
        byte[] bytes = new byte[4];
        int len = code.length();
        if (len > 0) {
            if (len > 4) {
                len = 4;
            }
            byte[] bs = code.getBytes();
            System.arraycopy(bs, 0, bytes, 0, Math.min(4, bs.length));
        }
        int val = 0;
        int i = 0;
        while (i < bytes.length) {
            if (i > 0) {
                val <<= 8;
            }
            val |= bytes[i] & 255;
            ++i;
        }
        return val;
    }

    public static String intToFourCharCode(int code) {
        if (code == 0) {
            return "";
        }
        byte[] bytes = new byte[]{(byte)(code >> 24), (byte)(code >> 16), (byte)(code >> 8), (byte)code};
        return new String(bytes);
    }

    public static String parsePkgInfo(File file, String key) throws IOException {
        String val = null;
        LineNumberReader r = new LineNumberReader(new FileReader(file));
        String line = r.readLine();
        if (line != null) {
            if (key.equals("type")) {
                if (line.length() >= 4) {
                    val = line.substring(0, 4);
                }
            } else if (key.equals("creator") && line.length() >= 8) {
                val = line.substring(4, 8);
            }
        }
        r.close();
        return val;
    }

    public static String parseInfoPlist(File file, String key) throws IOException {
        String line;
        String val = null;
        LineNumberReader r = new LineNumberReader(new FileReader(file));
        while ((line = r.readLine()) != null) {
            if (line.indexOf(key) == -1) continue;
            line = r.readLine();
            if (line == null) break;
            line = line.trim();
            val = line.substring(line.indexOf(62) + 1, line.lastIndexOf(60));
            break;
        }
        r.close();
        return val;
    }

    public static String parseMRJAppProperties(File file, String key) throws IOException {
        FileInputStream in = new FileInputStream(file);
        Properties props = new Properties();
        props.load(in);
        in.close();
        return props.getProperty(key);
    }

    public static String getStartupDisk() throws IOException {
        if (startupDisk == null) {
            if (mrjVersion >= 3.0f) {
                startupDisk = MRJAdapter.runAppleScript("tell application \"Finder\" to get name of startup disk");
            } else if (mrjVersion != -1.0f) {
                String path = MRJFileUtils.findFolder((MRJOSType)new MRJOSType("macs")).getPath();
                startupDisk = path.substring(1, path.indexOf(47, 1));
            } else {
                throw new IOException();
            }
        }
        return startupDisk;
    }

    private static String getApplicationPath() throws IOException {
        if (applicationPath == null) {
            if (mrjVersion >= 3.0f) {
                try {
                    Class nsBundleClass = Class.forName("com.apple.cocoa.foundation.NSBundle", true, cocoaClassLoader);
                    Method mainBundleMethod = nsBundleClass.getMethod("mainBundle", null);
                    Object bndl = mainBundleMethod.invoke(null, null);
                    Method bundlePathMethod = nsBundleClass.getMethod("bundlePath", null);
                    applicationPath = (String)bundlePathMethod.invoke(bndl, null);
                }
                catch (Exception ex) {
                    throw new IOException(ex.getMessage());
                }
            } else {
                if (mrjVersion != -1.0f) {
                    throw new IOException();
                }
                throw new IOException();
            }
        }
        return applicationPath;
    }

    private static String runAppleScript(String script) throws IOException {
        int n;
        Process p = Runtime.getRuntime().exec(new String[]{"osascript", "-e", script});
        InputStreamReader r = new InputStreamReader(p.getInputStream());
        StringBuffer b = new StringBuffer();
        char[] buf = new char[128];
        while ((n = r.read(buf)) != -1) {
            b.append(buf, 0, n);
        }
        r.close();
        return b.toString().trim();
    }

    public static File[] getDocumentsOpened() {
        if (mrjVersion >= 4.0f) {
            return MRJ4EventProxy.getInstance().getDocumentsOpened();
        }
        if (mrjVersion >= 1.5f) {
            return MRJ23EventProxy.getInstance().getDocumentsOpened();
        }
        return new File[0];
    }

    private static class InvisibleJFrame
    extends JFrame {
        InvisibleJFrame() {
            this.setDefaultCloseOperation(0);
        }
    }

}

