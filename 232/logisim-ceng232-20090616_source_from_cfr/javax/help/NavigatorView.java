/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.awt.Component;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Locale;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.InvalidNavigatorViewException;
import javax.help.Map;

public abstract class NavigatorView
implements Serializable {
    private HelpSet hs;
    private String name;
    private String label;
    private Locale locale;
    private Hashtable params;
    private Map.ID imageID;
    private String mergeType;
    static /* synthetic */ Class class$javax$help$HelpSet;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$util$Locale;
    static /* synthetic */ Class class$java$util$Hashtable;

    protected NavigatorView(HelpSet helpSet, String string, String string2, Locale locale, Hashtable hashtable) {
        if (string == null || string2 == null) {
            throw new NullPointerException("Invalid name or label");
        }
        this.imageID = null;
        if (hashtable != null) {
            String string3 = (String)hashtable.get("imageID");
            if (string3 != null) {
                this.imageID = Map.ID.create(string3, helpSet);
            }
            this.mergeType = (String)hashtable.get("mergetype");
        }
        this.hs = helpSet;
        this.name = string;
        this.label = string2;
        this.locale = locale;
        this.params = hashtable;
    }

    public static NavigatorView create(HelpSet helpSet, String string, String string2, Locale locale, String string3, Hashtable hashtable) throws InvalidNavigatorViewException {
        try {
            ClassLoader classLoader = helpSet.getLoader();
            Class[] arrclass = new Class[5];
            Class class_ = class$javax$help$HelpSet == null ? (NavigatorView.class$javax$help$HelpSet = NavigatorView.class$("javax.help.HelpSet")) : class$javax$help$HelpSet;
            arrclass[0] = class_;
            Class class_2 = class$java$lang$String == null ? (NavigatorView.class$java$lang$String = NavigatorView.class$("java.lang.String")) : class$java$lang$String;
            arrclass[1] = class_2;
            arrclass[2] = class$java$lang$String == null ? (NavigatorView.class$java$lang$String = NavigatorView.class$("java.lang.String")) : class$java$lang$String;
            Class class_3 = class$java$util$Locale == null ? (NavigatorView.class$java$util$Locale = NavigatorView.class$("java.util.Locale")) : class$java$util$Locale;
            arrclass[3] = class_3;
            Class class_4 = class$java$util$Hashtable == null ? (NavigatorView.class$java$util$Hashtable = NavigatorView.class$("java.util.Hashtable")) : class$java$util$Hashtable;
            arrclass[4] = class_4;
            Class[] arrclass2 = arrclass;
            Object[] arrobject = new Object[]{helpSet, string, string2, locale, hashtable};
            Class class_5 = classLoader == null ? Class.forName(string3) : classLoader.loadClass(string3);
            Constructor constructor = class_5.getConstructor(arrclass2);
            NavigatorView navigatorView = (NavigatorView)constructor.newInstance(arrobject);
            return navigatorView;
        }
        catch (Exception var9_11) {
            throw new InvalidNavigatorViewException("Could not create", helpSet, string, string2, locale, string3, hashtable);
        }
    }

    public abstract Component createNavigator(HelpModel var1);

    public HelpSet getHelpSet() {
        return this.hs;
    }

    public String getName() {
        return this.name;
    }

    public String getLabel(Locale locale) {
        return this.getLabel();
    }

    public String getLabel() {
        return this.label;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public Hashtable getParameters() {
        return this.params;
    }

    public String getMergeType() {
        return this.mergeType;
    }

    public Map.ID getImageID() {
        return this.imageID;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException var1_1) {
            throw new NoClassDefFoundError(var1_1.getMessage());
        }
    }
}

