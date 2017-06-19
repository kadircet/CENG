/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.gui.Strings;
import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.OutputExpressions;
import com.cburch.logisim.analyze.model.VariableList;
import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.LogisimFileActions;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.Projects;
import com.cburch.logisim.std.gates.CircuitBuilder;
import com.cburch.logisim.util.StringUtil;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

class BuildCircuitButton
extends JButton {
    private MyListener myListener;
    private JFrame parent;
    private AnalyzerModel model;

    BuildCircuitButton(JFrame parent, AnalyzerModel model) {
        this.myListener = new MyListener();
        this.parent = parent;
        this.model = model;
        this.addActionListener(this.myListener);
    }

    void localeChanged() {
        this.setText(Strings.get("buildCircuitButton"));
    }

    private void performAction(Project dest, String name, boolean replace, final boolean twoInputs, final boolean useNands) {
        if (replace) {
            final Circuit circuit = dest.getLogisimFile().getCircuit(name);
            if (circuit == null) {
                JOptionPane.showMessageDialog(this.parent, "Internal error prevents replacing circuit.", "Internal Error", 0);
                return;
            }
            final ArrayList comps = new ArrayList();
            comps.addAll(circuit.getWires());
            comps.addAll(circuit.getNonWires());
            dest.doAction(new Action(){

                @Override
                public String getName() {
                    return Strings.get("replaceCircuitAction");
                }

                @Override
                public void doIt(Project proj) {
                    circuit.clear();
                    CircuitBuilder.build(circuit, BuildCircuitButton.this.model, twoInputs, useNands);
                }

                @Override
                public void undo(Project proj) {
                    circuit.clear();
                    Iterator it = comps.iterator();
                    while (it.hasNext()) {
                        circuit.add((com.cburch.logisim.comp.Component)it.next());
                    }
                }
            });
        } else {
            Circuit circuit = new Circuit(name);
            CircuitBuilder.build(circuit, this.model, twoInputs, useNands);
            dest.doAction(LogisimFileActions.addCircuit(circuit));
            dest.setCurrentCircuit(circuit);
        }
    }

    private class MyListener
    implements ActionListener {
        private MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Project dest = null;
            String name = null;
            boolean twoInputs = false;
            boolean useNands = false;
            boolean replace = false;
            boolean ok = false;
            while (!ok) {
                DialogPanel dlog = new DialogPanel();
                int action = JOptionPane.showConfirmDialog(BuildCircuitButton.this.parent, dlog, Strings.get("buildDialogTitle"), 2, 3);
                if (action != 0) {
                    return;
                }
                ProjectItem projectItem = (ProjectItem)dlog.project.getSelectedItem();
                if (projectItem == null) {
                    JOptionPane.showMessageDialog(BuildCircuitButton.this.parent, Strings.get("buildNeedProjectError"), Strings.get("buildDialogErrorTitle"), 0);
                    continue;
                }
                dest = projectItem.project;
                name = dlog.name.getText().trim();
                if (name.equals("")) {
                    JOptionPane.showMessageDialog(BuildCircuitButton.this.parent, Strings.get("buildNeedCircuitError"), Strings.get("buildDialogErrorTitle"), 0);
                    continue;
                }
                if (dest.getLogisimFile().getCircuit(name) != null) {
                    int choice = JOptionPane.showConfirmDialog(BuildCircuitButton.this.parent, StringUtil.format(Strings.get("buildConfirmReplaceMessage"), name), Strings.get("buildConfirmReplaceTitle"), 0);
                    if (choice != 0) continue;
                    replace = true;
                }
                twoInputs = dlog.twoInputs.isSelected();
                useNands = dlog.nands.isSelected();
                ok = true;
            }
            BuildCircuitButton.this.performAction(dest, name, replace, twoInputs, useNands);
        }
    }

    private class DialogPanel
    extends JPanel {
        private JLabel projectLabel;
        private JComboBox project;
        private JLabel nameLabel;
        private JTextField name;
        private JCheckBox twoInputs;
        private JCheckBox nands;

        DialogPanel() {
            this.projectLabel = new JLabel();
            this.nameLabel = new JLabel();
            this.name = new JTextField(10);
            this.twoInputs = new JCheckBox();
            this.nands = new JCheckBox();
            List projects = Projects.getOpenProjects();
            Object[] options = new Object[projects.size()];
            Object initialSelection = null;
            for (int i = 0; i < options.length; ++i) {
                Project proj = (Project)projects.get(i);
                options[i] = new ProjectItem(proj);
                if (proj != BuildCircuitButton.this.model.getCurrentProject()) continue;
                initialSelection = options[i];
            }
            this.project = new JComboBox<Object>(options);
            if (options.length == 1) {
                this.project.setSelectedItem(options[0]);
                this.project.setEnabled(false);
            } else if (initialSelection != null) {
                this.project.setSelectedItem(initialSelection);
            }
            Circuit defaultCircuit = BuildCircuitButton.this.model.getCurrentCircuit();
            if (defaultCircuit != null) {
                this.name.setText(defaultCircuit.getName());
                this.name.selectAll();
            }
            VariableList outputs = BuildCircuitButton.this.model.getOutputs();
            boolean enableNands = true;
            for (int i2 = 0; i2 < outputs.size(); ++i2) {
                String output = outputs.get(i2);
                Expression expr = BuildCircuitButton.this.model.getOutputExpressions().getExpression(output);
                if (expr == null || !expr.containsXor()) continue;
                enableNands = false;
                break;
            }
            this.nands.setEnabled(enableNands);
            GridBagLayout gb = new GridBagLayout();
            GridBagConstraints gc = new GridBagConstraints();
            this.setLayout(gb);
            gc.anchor = 21;
            gc.fill = 0;
            gc.gridx = 0;
            gc.gridy = 0;
            gb.setConstraints(this.projectLabel, gc);
            this.add(this.projectLabel);
            gc.gridx = 1;
            gb.setConstraints(this.project, gc);
            this.add(this.project);
            ++gc.gridy;
            gc.gridx = 0;
            gb.setConstraints(this.nameLabel, gc);
            this.add(this.nameLabel);
            gc.gridx = 1;
            gb.setConstraints(this.name, gc);
            this.add(this.name);
            ++gc.gridy;
            gb.setConstraints(this.twoInputs, gc);
            this.add(this.twoInputs);
            ++gc.gridy;
            gb.setConstraints(this.nands, gc);
            this.add(this.nands);
            this.projectLabel.setText(Strings.get("buildProjectLabel"));
            this.nameLabel.setText(Strings.get("buildNameLabel"));
            this.twoInputs.setText(Strings.get("buildTwoInputsLabel"));
            this.nands.setText(Strings.get("buildNandsLabel"));
        }
    }

    private static class ProjectItem {
        Project project;

        ProjectItem(Project project) {
            this.project = project;
        }

        public String toString() {
            return this.project.getLogisimFile().getDisplayName();
        }
    }

}

