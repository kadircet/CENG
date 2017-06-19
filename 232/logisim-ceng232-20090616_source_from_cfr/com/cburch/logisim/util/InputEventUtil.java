/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.Strings;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class InputEventUtil {
    public static String CTRL = "Ctrl";
    public static String SHIFT = "Shift";
    public static String BUTTON1 = "Button1";
    public static String BUTTON2 = "Button2";
    public static String BUTTON3 = "Button3";

    private InputEventUtil() {
    }

    public static int fromString(String str) {
        int ret = 0;
        StringTokenizer toks = new StringTokenizer(str);
        while (toks.hasMoreTokens()) {
            String s = toks.nextToken();
            if (s.equals(CTRL)) {
                ret |= 2;
                continue;
            }
            if (s.equals(SHIFT)) {
                ret |= 1;
                continue;
            }
            if (s.equals(BUTTON1)) {
                ret |= 16;
                continue;
            }
            if (s.equals(BUTTON2)) {
                ret |= 8;
                continue;
            }
            if (s.equals(BUTTON3)) {
                ret |= 4;
                continue;
            }
            throw new NumberFormatException("InputEventUtil");
        }
        return ret;
    }

    public static String toString(int mods) {
        ArrayList<String> arr = new ArrayList<String>();
        if ((mods & 2) != 0) {
            arr.add(CTRL);
        }
        if ((mods & 1) != 0) {
            arr.add(SHIFT);
        }
        if ((mods & 16) != 0) {
            arr.add(BUTTON1);
        }
        if ((mods & 8) != 0) {
            arr.add(BUTTON2);
        }
        if ((mods & 4) != 0) {
            arr.add(BUTTON3);
        }
        if (arr.isEmpty()) {
            return "";
        }
        StringBuffer ret = new StringBuffer();
        Iterator it = arr.iterator();
        ret.append((String)it.next());
        while (it.hasNext()) {
            ret.append(" ");
            ret.append((String)it.next());
        }
        return ret.toString();
    }

    public static int fromDisplayString(String str) {
        int ret = 0;
        StringTokenizer toks = new StringTokenizer(str);
        while (toks.hasMoreTokens()) {
            String s = toks.nextToken();
            if (s.equals(Strings.get("ctrlMod"))) {
                ret |= 2;
                continue;
            }
            if (s.equals(Strings.get("shiftMod"))) {
                ret |= 1;
                continue;
            }
            if (s.equals(Strings.get("button1Mod"))) {
                ret |= 16;
                continue;
            }
            if (s.equals(Strings.get("button2Mod"))) {
                ret |= 8;
                continue;
            }
            if (s.equals(Strings.get("button3Mod"))) {
                ret |= 4;
                continue;
            }
            throw new NumberFormatException("InputEventUtil");
        }
        return ret;
    }

    public static String toDisplayString(int mods) {
        ArrayList<String> arr = new ArrayList<String>();
        if ((mods & 2) != 0) {
            arr.add(Strings.get("ctrlMod"));
        }
        if ((mods & 1) != 0) {
            arr.add(Strings.get("shiftMod"));
        }
        if ((mods & 16) != 0) {
            arr.add(Strings.get("button1Mod"));
        }
        if ((mods & 8) != 0) {
            arr.add(Strings.get("button2Mod"));
        }
        if ((mods & 4) != 0) {
            arr.add(Strings.get("button3Mod"));
        }
        if (arr.isEmpty()) {
            return "";
        }
        StringBuffer ret = new StringBuffer();
        Iterator it = arr.iterator();
        ret.append((String)it.next());
        while (it.hasNext()) {
            ret.append(" ");
            ret.append((String)it.next());
        }
        return ret.toString();
    }

    public static String toKeyDisplayString(int mods) {
        ArrayList<String> arr = new ArrayList<String>();
        if ((mods & 4) != 0) {
            arr.add(Strings.get("metaMod"));
        }
        if ((mods & 8) != 0) {
            arr.add(Strings.get("altMod"));
        }
        if ((mods & 2) != 0) {
            arr.add(Strings.get("ctrlMod"));
        }
        if ((mods & 1) != 0) {
            arr.add(Strings.get("shiftMod"));
        }
        if (arr.isEmpty()) {
            return "";
        }
        StringBuffer ret = new StringBuffer();
        Iterator it = arr.iterator();
        ret.append((String)it.next());
        while (it.hasNext()) {
            ret.append(" ");
            ret.append((String)it.next());
        }
        return ret.toString();
    }
}

