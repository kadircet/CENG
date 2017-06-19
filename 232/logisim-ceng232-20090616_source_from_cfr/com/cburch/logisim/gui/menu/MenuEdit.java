/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.menu;

import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.gui.menu.LogisimMenuItem;
import com.cburch.logisim.gui.menu.Menu;
import com.cburch.logisim.gui.menu.MenuItem;
import com.cburch.logisim.gui.menu.Strings;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.ProjectEvent;
import com.cburch.logisim.proj.ProjectListener;
import com.cburch.logisim.util.StringUtil;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

class MenuEdit
extends Menu {
    private LogisimMenuBar menubar;
    private JMenuItem undo = new JMenuItem();
    private MenuItem cut;
    private MenuItem copy;
    private MenuItem paste;
    private MenuItem clear;
    private MenuItem selall;
    private MyListener myListener;

    public MenuEdit(LogisimMenuBar menubar) {
        this.cut = new MenuItem(this, LogisimMenuBar.CUT);
        this.copy = new MenuItem(this, LogisimMenuBar.COPY);
        this.paste = new MenuItem(this, LogisimMenuBar.PASTE);
        this.clear = new MenuItem(this, LogisimMenuBar.DELETE);
        this.selall = new MenuItem(this, LogisimMenuBar.SELECT_ALL);
        this.myListener = new MyListener();
        this.menubar = menubar;
        int menuMask = this.getToolkit().getMenuShortcutKeyMask();
        this.undo.setAccelerator(KeyStroke.getKeyStroke(90, menuMask));
        this.cut.setAccelerator(KeyStroke.getKeyStroke(88, menuMask));
        this.copy.setAccelerator(KeyStroke.getKeyStroke(67, menuMask));
        this.paste.setAccelerator(KeyStroke.getKeyStroke(86, menuMask));
        this.clear.setAccelerator(KeyStroke.getKeyStroke(127, 0));
        this.selall.setAccelerator(KeyStroke.getKeyStroke(65, menuMask));
        this.add(this.undo);
        this.addSeparator();
        this.add(this.cut);
        this.add(this.copy);
        this.add(this.paste);
        this.add(this.clear);
        this.add(this.selall);
        Project proj = menubar.getProject();
        if (proj != null) {
            proj.addProjectListener(this.myListener);
            this.undo.addActionListener(this.myListener);
        }
        this.undo.setEnabled(false);
        menubar.registerItem(LogisimMenuBar.CUT, this.cut);
        menubar.registerItem(LogisimMenuBar.COPY, this.copy);
        menubar.registerItem(LogisimMenuBar.PASTE, this.paste);
        menubar.registerItem(LogisimMenuBar.DELETE, this.clear);
        menubar.registerItem(LogisimMenuBar.SELECT_ALL, this.selall);
        this.computeEnabled();
    }

    public void localeChanged() {
        this.setText(Strings.get("editMenu"));
        this.myListener.projectChanged(null);
        this.cut.setText(Strings.get("editCutItem"));
        this.copy.setText(Strings.get("editCopyItem"));
        this.paste.setText(Strings.get("editPasteItem"));
        this.clear.setText(Strings.get("editClearItem"));
        this.selall.setText(Strings.get("editSelectAllItem"));
    }

    @Override
    void computeEnabled() {
        this.setEnabled(this.menubar.getProject() != null || this.cut.hasListeners() || this.copy.hasListeners() || this.paste.hasListeners() || this.clear.hasListeners() || this.selall.hasListeners());
    }

    private class MyListener
    implements ProjectListener,
    ActionListener {
        private MyListener() {
        }

        @Override
        public void projectChanged(ProjectEvent e) {
            Action last;
            Project proj = MenuEdit.this.menubar.getProject();
            Action action = last = proj == null ? null : proj.getLastAction();
            if (last == null) {
                MenuEdit.this.undo.setText(Strings.get("editCantUndoItem"));
                MenuEdit.this.undo.setEnabled(false);
            } else {
                MenuEdit.this.undo.setText(StringUtil.format(Strings.get("editUndoItem"), last.getName()));
                MenuEdit.this.undo.setEnabled(true);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            Project proj = MenuEdit.this.menubar.getProject();
            if (src == MenuEdit.this.undo && proj != null) {
                proj.undoAction();
            }
        }
    }

}

