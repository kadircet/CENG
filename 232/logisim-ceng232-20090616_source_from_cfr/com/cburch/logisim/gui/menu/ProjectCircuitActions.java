/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.menu;

import com.cburch.logisim.analyze.gui.Analyzer;
import com.cburch.logisim.analyze.gui.AnalyzerManager;
import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.circuit.Analyze;
import com.cburch.logisim.circuit.AnalyzeException;
import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitActions;
import com.cburch.logisim.circuit.Pin;
import com.cburch.logisim.comp.EndData;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.LogisimFileActions;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.menu.Strings;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Dependencies;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.StringUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ProjectCircuitActions {
    private ProjectCircuitActions() {
    }

    public static void doAddCircuit(Project proj) {
        String name = ProjectCircuitActions.promptForCircuitName(proj.getFrame(), proj.getLogisimFile(), "");
        if (name != null) {
            Circuit circuit = new Circuit(name);
            proj.doAction(LogisimFileActions.addCircuit(circuit));
            proj.setCurrentCircuit(circuit);
        }
    }

    private static String promptForCircuitName(JFrame frame, Library lib, String initialValue) {
        JLabel label = new JLabel(Strings.get("circuitNamePrompt"));
        final JTextField field = new JTextField(15);
        field.setText(initialValue);
        JLabel error = new JLabel(" ");
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        JPanel strut = new JPanel(null);
        strut.setPreferredSize(new Dimension(3 * field.getPreferredSize().width / 2, 0));
        JPanel panel = new JPanel(gb);
        gc.gridx = 0;
        gc.gridy = -1;
        gc.weightx = 1.0;
        gc.fill = 0;
        gc.anchor = 21;
        gb.setConstraints(label, gc);
        panel.add(label);
        gb.setConstraints(field, gc);
        panel.add(field);
        gb.setConstraints(error, gc);
        panel.add(error);
        gb.setConstraints(strut, gc);
        panel.add(strut);
        JOptionPane pane = new JOptionPane(panel, 3, 2);
        pane.setInitialValue(field);
        JDialog dlog = pane.createDialog(frame, Strings.get("circuitNameDialogTitle"));
        dlog.addWindowFocusListener(new WindowFocusListener(){

            @Override
            public void windowGainedFocus(WindowEvent arg0) {
                field.grabFocus();
            }

            @Override
            public void windowLostFocus(WindowEvent arg0) {
            }
        });
        do {
            field.selectAll();
            field.grabFocus();
            dlog.pack();
            dlog.setVisible(true);
            Object action = pane.getValue();
            if (action == null || !(action instanceof Integer) || (Integer)action != 0) {
                return null;
            }
            String name = field.getText().trim();
            if (name.equals("")) {
                error.setText(Strings.get("circuitNameMissingError"));
                continue;
            }
            if (lib.getTool(name) == null) {
                return name;
            }
            error.setText(Strings.get("circuitNameDuplicateError"));
        } while (true);
    }

    public static void doRenameCircuit(Project proj, Circuit circuit) {
        String name = ProjectCircuitActions.promptForCircuitName(proj.getFrame(), proj.getLogisimFile(), circuit.getName());
        if (name != null) {
            proj.doAction(CircuitActions.setCircuitName(circuit, name));
        }
    }

    public static void doSetAsMainCircuit(Project proj, Circuit circuit) {
        proj.doAction(LogisimFileActions.setMainCircuit(circuit));
    }

    public static void doRemoveCircuit(Project proj, Circuit circuit) {
        if (proj.getLogisimFile().getTools().size() == 1) {
            JOptionPane.showMessageDialog(proj.getFrame(), Strings.get("circuitRemoveLastError"), Strings.get("circuitRemoveErrorTitle"), 0);
        } else if (!proj.getDependencies().canRemove(circuit)) {
            JOptionPane.showMessageDialog(proj.getFrame(), Strings.get("circuitRemoveUsedError"), Strings.get("circuitRemoveErrorTitle"), 0);
        } else {
            proj.doAction(LogisimFileActions.removeCircuit(circuit));
        }
    }

    public static void doAnalyze(Project proj, Circuit circuit) {
        SortedMap pinNames = Analyze.getPinLabels(circuit);
        ArrayList inputNames = new ArrayList();
        ArrayList outputNames = new ArrayList();
        for (Pin pin : pinNames.keySet()) {
            boolean isOutput;
            EndData pinEnd = pin.getEnd(0);
            boolean bl = isOutput = pinEnd.getType() != 2;
            if (pin.isInputPin()) {
                inputNames.add(pinNames.get(pin));
            } else {
                outputNames.add(pinNames.get(pin));
            }
            if (pin.getEnd(0).getWidth().getWidth() <= 1) continue;
            if (isOutput) {
                ProjectCircuitActions.analyzeError(proj, Strings.get("analyzeMultibitOutputError"));
            } else {
                ProjectCircuitActions.analyzeError(proj, Strings.get("analyzeMultibitInputError"));
            }
            return;
        }
        if (inputNames.size() > 12) {
            ProjectCircuitActions.analyzeError(proj, StringUtil.format(Strings.get("analyzeTooManyInputsError"), "12"));
            return;
        }
        if (outputNames.size() > 12) {
            ProjectCircuitActions.analyzeError(proj, StringUtil.format(Strings.get("analyzeTooManyOutputsError"), "12"));
            return;
        }
        Analyzer analyzer = AnalyzerManager.getAnalyzer();
        analyzer.getModel().setCurrentProject(proj);
        ProjectCircuitActions.configureAnalyzer(proj, circuit, analyzer, pinNames, inputNames, outputNames);
        analyzer.setVisible(true);
        analyzer.toFront();
    }

    private static void configureAnalyzer(Project proj, Circuit circuit, Analyzer analyzer, Map pinNames, ArrayList inputNames, ArrayList outputNames) {
        analyzer.getModel().setVariables(inputNames, outputNames);
        if (inputNames.size() == 0) {
            analyzer.setSelectedTab(0);
            return;
        }
        if (outputNames.size() == 0) {
            analyzer.setSelectedTab(1);
            return;
        }
        try {
            Analyze.computeExpression(analyzer.getModel(), circuit, pinNames);
            analyzer.setSelectedTab(3);
            return;
        }
        catch (AnalyzeException ex) {
            JOptionPane.showMessageDialog(proj.getFrame(), ex.getMessage(), Strings.get("analyzeNoExpressionTitle"), 1);
            Analyze.computeTable(analyzer.getModel(), proj, circuit, pinNames);
            analyzer.setSelectedTab(2);
            return;
        }
    }

    private static void analyzeError(Project proj, String message) {
        JOptionPane.showMessageDialog(proj.getFrame(), message, Strings.get("analyzeErrorTitle"), 0);
    }

}

