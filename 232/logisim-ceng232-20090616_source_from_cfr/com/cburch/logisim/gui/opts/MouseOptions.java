/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.opts;

import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.file.MouseMappings;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.gui.main.AttributeTable;
import com.cburch.logisim.gui.main.Explorer;
import com.cburch.logisim.gui.main.ToolActions;
import com.cburch.logisim.gui.opts.OptionsActions;
import com.cburch.logisim.gui.opts.OptionsFrame;
import com.cburch.logisim.gui.opts.OptionsPanel;
import com.cburch.logisim.gui.opts.Strings;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.InputEventUtil;
import com.cburch.logisim.util.IntegerFactory;
import com.cburch.logisim.util.StringUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

class MouseOptions
extends OptionsPanel {
    private MyListener listener;
    private Tool curTool;
    private MappingsModel model;
    private Explorer explorer;
    private JPanel addArea;
    private JTable mappings;
    private AttributeTable attr_table;
    private JButton remove;

    public MouseOptions(OptionsFrame window) {
        super(window, new GridLayout(1, 3));
        this.listener = new MyListener();
        this.curTool = null;
        this.addArea = new AddArea();
        this.mappings = new JTable();
        this.remove = new JButton();
        this.explorer = new Explorer(this.getProject());
        this.explorer.setListener(this.listener);
        this.addArea.addMouseListener(this.listener);
        this.model = new MappingsModel();
        this.mappings.setTableHeader(null);
        this.mappings.setModel(this.model);
        this.mappings.setSelectionMode(0);
        this.mappings.getSelectionModel().addListSelectionListener(this.listener);
        this.mappings.clearSelection();
        JScrollPane mapping_pane = new JScrollPane(this.mappings);
        JPanel remove_area = new JPanel();
        this.remove.addActionListener(this.listener);
        this.remove.setEnabled(false);
        remove_area.add(this.remove);
        this.attr_table = new AttributeTable(this.getOptionsFrame(), this.getProject());
        JScrollPane attr_pane = new JScrollPane(this.attr_table);
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        this.setLayout(gridbag);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = 4;
        gbc.fill = 1;
        JScrollPane explorerPane = new JScrollPane(this.explorer, 22, 30);
        gridbag.setConstraints(explorerPane, gbc);
        this.add(explorerPane);
        gbc.weightx = 0.0;
        JPanel gap = new JPanel();
        gap.setPreferredSize(new Dimension(10, 10));
        gridbag.setConstraints(gap, gbc);
        this.add(gap);
        gbc.weightx = 1.0;
        gbc.gridheight = 1;
        gbc.gridx = 2;
        gbc.gridy = -1;
        gbc.weighty = 0.0;
        gridbag.setConstraints(this.addArea, gbc);
        this.add(this.addArea);
        gbc.weighty = 1.0;
        gridbag.setConstraints(mapping_pane, gbc);
        this.add(mapping_pane);
        gbc.weighty = 0.0;
        gridbag.setConstraints(remove_area, gbc);
        this.add(remove_area);
        gbc.weighty = 1.0;
        gridbag.setConstraints(attr_pane, gbc);
        this.add(attr_pane);
        this.getOptions().getMouseMappings().addMouseMappingsListener(this.listener);
        this.setCurrentTool(null);
    }

    @Override
    public String getTitle() {
        return Strings.get("mouseTitle");
    }

    @Override
    public String getHelpText() {
        return Strings.get("mouseHelp");
    }

    @Override
    public void localeChanged() {
        this.remove.setText(Strings.get("mouseRemoveButton"));
        this.addArea.repaint();
    }

    private void setCurrentTool(Tool t) {
        this.curTool = t;
        this.localeChanged();
    }

    private void setSelectedRow(int row) {
        if (row < 0) {
            row = 0;
        }
        if (row >= this.model.getRowCount()) {
            row = this.model.getRowCount() - 1;
        }
        if (row >= 0) {
            this.mappings.getSelectionModel().setSelectionInterval(row, row);
        }
    }

    private class MappingsModel
    extends AbstractTableModel {
        ArrayList cur_keys;

        MappingsModel() {
            this.fireTableStructureChanged();
        }

        @Override
        public void fireTableStructureChanged() {
            this.cur_keys = new ArrayList(MouseOptions.this.getOptions().getMouseMappings().getMappedModifiers());
            Collections.sort(this.cur_keys);
            super.fireTableStructureChanged();
        }

        @Override
        public int getRowCount() {
            return this.cur_keys.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int row, int column) {
            Integer key = (Integer)this.cur_keys.get(row);
            if (column == 0) {
                return InputEventUtil.toDisplayString(key);
            }
            Tool tool = MouseOptions.this.getOptions().getMouseMappings().getToolFor(key);
            return tool.getDisplayName();
        }

        Integer getKey(int row) {
            return (Integer)this.cur_keys.get(row);
        }

        Tool getTool(int row) {
            if (row < 0 || row >= this.cur_keys.size()) {
                return null;
            }
            Integer key = (Integer)this.cur_keys.get(row);
            return MouseOptions.this.getOptions().getMouseMappings().getToolFor((int)key);
        }

        int getRow(Integer mods) {
            int row = Collections.binarySearch(this.cur_keys, mods);
            if (row < 0) {
                row = - row + 1;
            }
            return row;
        }
    }

    private class MyListener
    implements ActionListener,
    MouseListener,
    ListSelectionListener,
    MouseMappings.MouseMappingsListener,
    Explorer.Listener {
        private MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == MouseOptions.this.remove) {
                int row = MouseOptions.this.mappings.getSelectedRow();
                MouseOptions.this.getProject().doAction(OptionsActions.removeMapping(MouseOptions.this.getOptions().getMouseMappings(), MouseOptions.this.model.getKey(row)));
                row = Math.min(row, MouseOptions.this.model.getRowCount() - 1);
                if (row >= 0) {
                    MouseOptions.this.setSelectedRow(row);
                }
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getSource() == MouseOptions.this.addArea && MouseOptions.this.curTool != null) {
                Tool t = MouseOptions.this.curTool.cloneTool();
                Integer mods = IntegerFactory.create(e.getModifiers());
                MouseOptions.this.getProject().doAction(OptionsActions.setMapping(MouseOptions.this.getOptions().getMouseMappings(), mods, t));
                MouseOptions.this.setSelectedRow(MouseOptions.this.model.getRow(mods));
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            int row = MouseOptions.this.mappings.getSelectedRow();
            if (row < 0) {
                MouseOptions.this.remove.setEnabled(false);
                MouseOptions.this.attr_table.setAttributeSet(null, null);
            } else {
                MouseOptions.this.remove.setEnabled(true);
                Tool tool = MouseOptions.this.model.getTool(row);
                MouseOptions.this.attr_table.setAttributeSet(tool.getAttributeSet(), ToolActions.createTableListener(MouseOptions.this.getProject(), tool));
            }
        }

        @Override
        public void mouseMappingsChanged() {
            MouseOptions.this.model.fireTableStructureChanged();
        }

        @Override
        public void selectionChanged(Explorer.Event event) {
            Object target = event.getTarget();
            if (target instanceof Tool) {
                MouseOptions.this.setCurrentTool((Tool)event.getTarget());
            } else {
                MouseOptions.this.setCurrentTool(null);
            }
        }

        @Override
        public void doubleClicked(Explorer.Event event) {
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
    }

    private class AddArea
    extends JPanel {
        public AddArea() {
            this.setPreferredSize(new Dimension(75, 60));
            this.setMinimumSize(new Dimension(75, 60));
            this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createEtchedBorder()));
        }

        @Override
        public void paintComponent(Graphics g) {
            String label2;
            String label1;
            super.paintComponent(g);
            Dimension sz = this.getSize();
            g.setFont(MouseOptions.this.remove.getFont());
            if (MouseOptions.this.curTool == null) {
                g.setColor(Color.GRAY);
                label1 = Strings.get("mouseMapNone");
                label2 = null;
            } else {
                g.setColor(Color.BLACK);
                label1 = Strings.get("mouseMapText");
                label2 = StringUtil.format(Strings.get("mouseMapText2"), MouseOptions.this.curTool.getDisplayName());
            }
            FontMetrics fm = g.getFontMetrics();
            int x1 = (sz.width - fm.stringWidth(label1)) / 2;
            if (label2 == null) {
                int y = Math.max(0, (sz.height - fm.getHeight()) / 2 + fm.getAscent() - 2);
                g.drawString(label1, x1, y);
            } else {
                int x2 = (sz.width - fm.stringWidth(label2)) / 2;
                int y = Math.max(0, (sz.height - 2 * fm.getHeight()) / 2 + fm.getAscent() - 2);
                g.drawString(label1, x1, y);
                g.drawString(label2, x2, y += fm.getHeight());
            }
        }
    }

}

