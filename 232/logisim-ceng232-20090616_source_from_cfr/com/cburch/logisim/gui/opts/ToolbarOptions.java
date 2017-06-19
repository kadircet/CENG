/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.opts;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.file.ToolbarData;
import com.cburch.logisim.gui.main.Explorer;
import com.cburch.logisim.gui.opts.OptionsFrame;
import com.cburch.logisim.gui.opts.OptionsPanel;
import com.cburch.logisim.gui.opts.Strings;
import com.cburch.logisim.gui.opts.ToolbarActions;
import com.cburch.logisim.gui.opts.ToolbarList;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.TableLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class ToolbarOptions
extends OptionsPanel {
    private Listener listener;
    private Explorer explorer;
    private JButton addTool;
    private JButton addSeparator;
    private JButton moveUp;
    private JButton moveDown;
    private JButton remove;
    private JLabel locLabel;
    private JComboBox locChoice;
    private ToolbarList list;

    public ToolbarOptions(OptionsFrame window) {
        super(window);
        this.listener = new Listener();
        this.explorer = new Explorer(this.getProject());
        this.addTool = new JButton();
        this.addSeparator = new JButton();
        this.moveUp = new JButton();
        this.moveDown = new JButton();
        this.remove = new JButton();
        this.locLabel = new JLabel();
        Component locChoiceOpt = Options.ATTR_TOOLBAR_LOC.getCellEditor(window, this.getOptions().getAttributeSet().getValue(Options.ATTR_TOOLBAR_LOC));
        if (locChoiceOpt instanceof JComboBox) {
            this.locChoice = (JComboBox)locChoiceOpt;
        } else {
            this.locChoice = new JComboBox();
            this.locChoice.setSelectedItem(this.getOptions().getAttributeSet().getValue(Options.ATTR_TOOLBAR_LOC));
        }
        this.list = new ToolbarList(this.getOptions().getToolbarData());
        TableLayout middleLayout = new TableLayout(1);
        JPanel middle = new JPanel(middleLayout);
        middle.add(this.addTool);
        middle.add(this.addSeparator);
        middle.add(this.moveUp);
        middle.add(this.moveDown);
        middle.add(this.remove);
        middleLayout.setRowWeight(4, 1.0);
        middle.add(this.locLabel);
        middle.add(this.locChoice);
        this.explorer.setListener(this.listener);
        this.addTool.addActionListener(this.listener);
        this.addSeparator.addActionListener(this.listener);
        this.moveUp.addActionListener(this.listener);
        this.moveDown.addActionListener(this.listener);
        this.remove.addActionListener(this.listener);
        this.locChoice.addItemListener(this.listener);
        this.list.addListSelectionListener(this.listener);
        this.listener.computeEnabled();
        this.getOptions().getAttributeSet().addAttributeListener(this.listener);
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        this.setLayout(gridbag);
        JScrollPane explorerPane = new JScrollPane(this.explorer, 22, 30);
        JScrollPane listPane = new JScrollPane(this.list, 22, 30);
        gbc.fill = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gridbag.setConstraints(explorerPane, gbc);
        this.add(explorerPane);
        gbc.fill = 3;
        gbc.anchor = 11;
        gbc.weightx = 0.0;
        gridbag.setConstraints(middle, gbc);
        this.add(middle);
        gbc.fill = 1;
        gbc.weightx = 1.0;
        gridbag.setConstraints(listPane, gbc);
        this.add(listPane);
    }

    @Override
    public String getTitle() {
        return Strings.get("toolbarTitle");
    }

    @Override
    public String getHelpText() {
        return Strings.get("toolbarHelp");
    }

    @Override
    public void localeChanged() {
        this.addTool.setText(Strings.get("toolbarAddTool"));
        this.addSeparator.setText(Strings.get("toolbarAddSeparator"));
        this.moveUp.setText(Strings.get("toolbarMoveUp"));
        this.moveDown.setText(Strings.get("toolbarMoveDown"));
        this.remove.setText(Strings.get("toolbarRemove"));
        this.locLabel.setText(Strings.get("toolbarLocation"));
        this.list.localeChanged();
    }

    private class Listener
    implements Explorer.Listener,
    ActionListener,
    ListSelectionListener,
    ItemListener,
    AttributeListener {
        private Listener() {
        }

        @Override
        public void selectionChanged(Explorer.Event event) {
            this.computeEnabled();
        }

        @Override
        public void doubleClicked(Explorer.Event event) {
            Object target = event.getTarget();
            if (target instanceof Tool) {
                this.doAddTool((Tool)target);
            }
        }

        @Override
        public void moveRequested(Explorer.Event event, AddTool dragged, AddTool target) {
        }

        @Override
        public void deleteRequested(Explorer.Event event) {
        }

        @Override
        public JPopupMenu menuRequested(Explorer.Event event) {
            return null;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            int index;
            Object src = event.getSource();
            if (src == ToolbarOptions.this.addTool) {
                this.doAddTool(ToolbarOptions.this.explorer.getSelectedTool().cloneTool());
            } else if (src == ToolbarOptions.this.addSeparator) {
                ToolbarOptions.this.getOptions().getToolbarData().addSeparator();
            } else if (src == ToolbarOptions.this.moveUp) {
                this.doMove(-1);
            } else if (src == ToolbarOptions.this.moveDown) {
                this.doMove(1);
            } else if (src == ToolbarOptions.this.remove && (index = ToolbarOptions.this.list.getSelectedIndex()) >= 0) {
                ToolbarOptions.this.getProject().doAction(ToolbarActions.removeTool(ToolbarOptions.this.getOptions().getToolbarData(), index));
                ToolbarOptions.this.list.clearSelection();
            }
        }

        @Override
        public void valueChanged(ListSelectionEvent event) {
            this.computeEnabled();
        }

        private void computeEnabled() {
            int index = ToolbarOptions.this.list.getSelectedIndex();
            ToolbarOptions.this.addTool.setEnabled(ToolbarOptions.this.explorer.getSelectedTool() instanceof Tool);
            ToolbarOptions.this.moveUp.setEnabled(index > 0);
            ToolbarOptions.this.moveDown.setEnabled(index >= 0 && index < ToolbarOptions.this.list.getModel().getSize() - 1);
            ToolbarOptions.this.remove.setEnabled(index >= 0);
        }

        private void doAddTool(Tool tool) {
            if (tool != null) {
                ToolbarOptions.this.getProject().doAction(ToolbarActions.addTool(ToolbarOptions.this.getOptions().getToolbarData(), tool));
            }
        }

        private void doMove(int delta) {
            int oldIndex = ToolbarOptions.this.list.getSelectedIndex();
            int newIndex = oldIndex + delta;
            ToolbarData data = ToolbarOptions.this.getOptions().getToolbarData();
            if (oldIndex >= 0 && newIndex >= 0 && newIndex < data.size()) {
                ToolbarOptions.this.getProject().doAction(ToolbarActions.moveTool(data, oldIndex, newIndex));
                ToolbarOptions.this.list.setSelectedIndex(newIndex);
            }
        }

        @Override
        public void itemStateChanged(ItemEvent event) {
            Object src = event.getSource();
            if (src == ToolbarOptions.this.locChoice) {
                AttributeSet attrs = ToolbarOptions.this.getOptions().getAttributeSet();
                attrs.setValue(Options.ATTR_TOOLBAR_LOC, ToolbarOptions.this.locChoice.getSelectedItem());
            }
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            if (e.getAttribute() == Options.ATTR_TOOLBAR_LOC) {
                Object value = e.getValue();
                if (ToolbarOptions.this.locChoice.getSelectedItem() != value) {
                    ToolbarOptions.this.locChoice.setSelectedItem(value);
                }
            }
        }
    }

}

