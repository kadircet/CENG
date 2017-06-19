/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.gui.log.ComponentSelector;
import com.cburch.logisim.gui.log.LogFrame;
import com.cburch.logisim.gui.log.LogPanel;
import com.cburch.logisim.gui.log.Model;
import com.cburch.logisim.gui.log.Selection;
import com.cburch.logisim.gui.log.SelectionItem;
import com.cburch.logisim.gui.log.SelectionList;
import com.cburch.logisim.gui.log.Strings;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

class SelectionPanel
extends LogPanel {
    private Listener listener;
    private ComponentSelector selector;
    private JButton addTool;
    private JButton changeBase;
    private JButton moveUp;
    private JButton moveDown;
    private JButton remove;
    private SelectionList list;

    public SelectionPanel(LogFrame window) {
        super(window);
        this.listener = new Listener();
        this.selector = new ComponentSelector(this.getModel());
        this.addTool = new JButton();
        this.changeBase = new JButton();
        this.moveUp = new JButton();
        this.moveDown = new JButton();
        this.remove = new JButton();
        this.list = new SelectionList();
        this.list.setSelection(this.getSelection());
        JPanel buttons = new JPanel(new GridLayout(5, 1));
        buttons.add(this.addTool);
        buttons.add(this.changeBase);
        buttons.add(this.moveUp);
        buttons.add(this.moveDown);
        buttons.add(this.remove);
        this.addTool.addActionListener(this.listener);
        this.changeBase.addActionListener(this.listener);
        this.moveUp.addActionListener(this.listener);
        this.moveDown.addActionListener(this.listener);
        this.remove.addActionListener(this.listener);
        this.selector.addMouseListener(this.listener);
        this.selector.addTreeSelectionListener(this.listener);
        this.list.addListSelectionListener(this.listener);
        this.listener.computeEnabled();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        this.setLayout(gridbag);
        JScrollPane explorerPane = new JScrollPane(this.selector, 22, 30);
        JScrollPane listPane = new JScrollPane(this.list, 22, 30);
        gbc.fill = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gridbag.setConstraints(explorerPane, gbc);
        this.add(explorerPane);
        gbc.fill = 0;
        gbc.anchor = 11;
        gbc.weightx = 0.0;
        gridbag.setConstraints(buttons, gbc);
        this.add(buttons);
        gbc.fill = 1;
        gbc.weightx = 1.0;
        gridbag.setConstraints(listPane, gbc);
        this.add(listPane);
    }

    @Override
    public String getTitle() {
        return Strings.get("selectionTab");
    }

    @Override
    public String getHelpText() {
        return Strings.get("selectionHelp");
    }

    @Override
    public void localeChanged() {
        this.addTool.setText(Strings.get("selectionAdd"));
        this.changeBase.setText(Strings.get("selectionChangeBase"));
        this.moveUp.setText(Strings.get("selectionMoveUp"));
        this.moveDown.setText(Strings.get("selectionMoveDown"));
        this.remove.setText(Strings.get("selectionRemove"));
        this.selector.localeChanged();
        this.list.localeChanged();
    }

    @Override
    public void modelChanged(Model oldModel, Model newModel) {
        if (this.getModel() == null) {
            this.selector.setLogModel(newModel);
            this.list.setSelection(null);
        } else {
            this.selector.setLogModel(newModel);
            this.list.setSelection(this.getSelection());
        }
        this.listener.computeEnabled();
    }

    private class Listener
    extends MouseAdapter
    implements ActionListener,
    TreeSelectionListener,
    ListSelectionListener {
        private Listener() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            TreePath path;
            if (e.getClickCount() == 2 && (path = SelectionPanel.this.selector.getPathForLocation(e.getX(), e.getY())) != null && SelectionPanel.this.listener != null) {
                this.doAdd(SelectionPanel.this.selector.getSelectedItems());
            }
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src == SelectionPanel.this.addTool) {
                this.doAdd(SelectionPanel.this.selector.getSelectedItems());
            } else if (src == SelectionPanel.this.changeBase) {
                SelectionItem sel = (SelectionItem)SelectionPanel.this.list.getSelectedValue();
                if (sel != null) {
                    int radix = sel.getRadix();
                    switch (radix) {
                        case 2: {
                            sel.setRadix(10);
                            break;
                        }
                        case 10: {
                            sel.setRadix(16);
                            break;
                        }
                        default: {
                            sel.setRadix(2);
                            break;
                        }
                    }
                }
            } else if (src == SelectionPanel.this.moveUp) {
                this.doMove(-1);
            } else if (src == SelectionPanel.this.moveDown) {
                this.doMove(1);
            } else if (src == SelectionPanel.this.remove) {
                Selection sel = SelectionPanel.this.getSelection();
                Object[] toRemove = SelectionPanel.this.list.getSelectedValues();
                boolean changed = false;
                for (int i = 0; i < toRemove.length; ++i) {
                    int index = sel.indexOf((SelectionItem)toRemove[i]);
                    if (index < 0) continue;
                    sel.remove(index);
                    changed = true;
                }
                if (changed) {
                    SelectionPanel.this.list.clearSelection();
                }
            }
        }

        @Override
        public void valueChanged(TreeSelectionEvent event) {
            this.computeEnabled();
        }

        @Override
        public void valueChanged(ListSelectionEvent event) {
            this.computeEnabled();
        }

        private void computeEnabled() {
            int index = SelectionPanel.this.list.getSelectedIndex();
            SelectionPanel.this.addTool.setEnabled(SelectionPanel.this.selector.hasSelectedItems());
            SelectionPanel.this.changeBase.setEnabled(index >= 0);
            SelectionPanel.this.moveUp.setEnabled(index > 0);
            SelectionPanel.this.moveDown.setEnabled(index >= 0 && index < SelectionPanel.this.list.getModel().getSize() - 1);
            SelectionPanel.this.remove.setEnabled(index >= 0);
        }

        private void doAdd(List selectedItems) {
            if (selectedItems != null && selectedItems.size() > 0) {
                SelectionItem item2 = null;
                for (SelectionItem item2 : selectedItems) {
                    SelectionPanel.this.getSelection().add(item2);
                }
                SelectionPanel.this.list.setSelectedValue(item2, true);
            }
        }

        private void doMove(int delta) {
            Selection sel = SelectionPanel.this.getSelection();
            int oldIndex = SelectionPanel.this.list.getSelectedIndex();
            int newIndex = oldIndex + delta;
            if (oldIndex >= 0 && newIndex >= 0 && newIndex < sel.size()) {
                sel.move(oldIndex, newIndex);
                SelectionPanel.this.list.setSelectedIndex(newIndex);
            }
        }
    }

}

