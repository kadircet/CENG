/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.gui.Analyzer;
import com.cburch.logisim.analyze.gui.Strings;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.WindowMenuItemManager;
import javax.swing.JFrame;

public class AnalyzerManager
extends WindowMenuItemManager
implements LocaleListener {
    private static Analyzer analysisWindow = null;
    private static AnalyzerManager analysisManager = null;

    public static void initialize() {
        analysisManager = new AnalyzerManager();
    }

    public static Analyzer getAnalyzer() {
        if (analysisWindow == null) {
            analysisWindow = new Analyzer();
            analysisWindow.pack();
            if (analysisManager != null) {
                analysisManager.frameOpened(analysisWindow);
            }
        }
        return analysisWindow;
    }

    private AnalyzerManager() {
        super(Strings.get("analyzerWindowTitle"), true);
        LocaleManager.addLocaleListener(this);
    }

    @Override
    public JFrame getJFrame(boolean create) {
        if (create) {
            return AnalyzerManager.getAnalyzer();
        }
        return analysisWindow;
    }

    @Override
    public void localeChanged() {
        this.setText(Strings.get("analyzerWindowTitle"));
    }
}

