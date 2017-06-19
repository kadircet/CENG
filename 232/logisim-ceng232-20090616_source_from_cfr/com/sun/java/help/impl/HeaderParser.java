/*
 * Decompiled with CFR 0_114.
 */
package com.sun.java.help.impl;

public class HeaderParser {
    String raw;
    String[][] tab;

    public HeaderParser(String string) {
        this.raw = string;
        this.tab = new String[10][2];
        this.parse();
    }

    private void parse() {
        if (this.raw != null) {
            this.raw = this.raw.trim();
            char[] arrc = this.raw.toCharArray();
            int n = 0;
            int n2 = 0;
            int n3 = 0;
            boolean bl = true;
            boolean bl2 = false;
            int n4 = arrc.length;
            while (n2 < n4) {
                char c = arrc[n2];
                if (c == '=') {
                    this.tab[n3][0] = new String(arrc, n, n2 - n).toLowerCase();
                    bl = false;
                    n = ++n2;
                    continue;
                }
                if (c == '\"') {
                    if (bl2) {
                        this.tab[n3++][1] = new String(arrc, n, n2 - n);
                        bl2 = false;
                        while (++n2 < n4 && (arrc[n2] == ' ' || arrc[n2] == ',')) {
                        }
                        bl = true;
                        n = n2;
                        continue;
                    }
                    bl2 = true;
                    n = ++n2;
                    continue;
                }
                if (c == ' ' || c == ',') {
                    if (bl2) {
                        ++n2;
                        continue;
                    }
                    if (bl) {
                        this.tab[n3++][0] = new String(arrc, n, n2 - n).toLowerCase();
                    } else {
                        this.tab[n3++][1] = new String(arrc, n, n2 - n);
                    }
                    while (n2 < n4 && (arrc[n2] == ' ' || arrc[n2] == ',')) {
                        ++n2;
                    }
                    bl = true;
                    n = n2;
                    continue;
                }
                ++n2;
            }
            if (--n2 > n) {
                if (!bl) {
                    this.tab[n3++][1] = arrc[n2] == '\"' ? new String(arrc, n, n2 - n) : new String(arrc, n, n2 - n + 1);
                } else {
                    this.tab[n3][0] = new String(arrc, n, n2 - n + 1).toLowerCase();
                }
            } else if (n2 == n) {
                if (!bl) {
                    this.tab[n3++][1] = arrc[n2] == '\"' ? String.valueOf(arrc[n2 - 1]) : String.valueOf(arrc[n2]);
                } else {
                    this.tab[n3][0] = String.valueOf(arrc[n2]).toLowerCase();
                }
            }
        }
    }

    public String findKey(int n) {
        if (n < 0 || n > 10) {
            return null;
        }
        return this.tab[n][0];
    }

    public String findValue(int n) {
        if (n < 0 || n > 10) {
            return null;
        }
        return this.tab[n][1];
    }

    public String findValue(String string) {
        return this.findValue(string, null);
    }

    public String findValue(String string, String string2) {
        if (string == null) {
            return string2;
        }
        string.toLowerCase();
        int n = 0;
        while (n < 10) {
            if (this.tab[n][0] == null) {
                return string2;
            }
            if (string.equals(this.tab[n][0])) {
                return this.tab[n][1];
            }
            ++n;
        }
        return string2;
    }

    public int findInt(String string, int n) {
        try {
            return Integer.parseInt(this.findValue(string, String.valueOf(n)));
        }
        catch (Throwable var3_3) {
            return n;
        }
    }
}

