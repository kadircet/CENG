/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.memory;

import com.cburch.hex.HexModel;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.gui.hex.HexFile;
import com.cburch.logisim.gui.hex.HexFrame;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.std.memory.Mem;
import com.cburch.logisim.std.memory.MemContents;
import com.cburch.logisim.std.memory.MemState;
import com.cburch.logisim.std.memory.Strings;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

class MemMenu
implements ActionListener {
    private Project proj;
    private Frame frame;
    private Mem mem;
    private CircuitState circState;
    private JMenuItem edit = new JMenuItem(Strings.get("ramEditMenuItem"));
    private JMenuItem clear = new JMenuItem(Strings.get("ramClearMenuItem"));
    private JMenuItem load = new JMenuItem(Strings.get("ramLoadMenuItem"));
    private JMenuItem save = new JMenuItem(Strings.get("ramSaveMenuItem"));

    MemMenu(Project proj, Mem ram) {
        this.proj = proj;
        this.mem = ram;
        this.frame = proj.getFrame();
        this.circState = proj.getCircuitState();
        if (this.circState == null) {
            this.edit.setEnabled(false);
            this.clear.setEnabled(false);
            this.load.setEnabled(false);
            this.save.setEnabled(false);
        }
        this.edit.addActionListener(this);
        this.clear.addActionListener(this);
        this.load.addActionListener(this);
        this.save.addActionListener(this);
    }

    void appendTo(JPopupMenu menu) {
        menu.add(this.edit);
        menu.add(this.clear);
        menu.add(this.load);
        menu.add(this.save);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();
        if (src == this.edit) {
            this.doEdit();
        } else if (src == this.clear) {
            this.doClear();
        } else if (src == this.load) {
            this.doLoad();
        } else if (src == this.save) {
            this.doSave();
        }
    }

    private void doEdit() {
        MemState s = this.mem.getState(this.circState);
        if (s == null) {
            return;
        }
        HexFrame frame = this.mem.getHexFrame(this.proj, this.circState);
        frame.setVisible(true);
        frame.toFront();
    }

    private void doClear() {
        MemState s = this.mem.getState(this.circState);
        boolean isAllZero = s.getContents().isClear();
        if (isAllZero) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this.frame, Strings.get("ramConfirmClearMsg"), Strings.get("ramConfirmClearTitle"), 0);
        if (choice == 0) {
            s.getContents().clear();
        }
    }

    private void doLoad() {
        MemState s = this.mem.getState(this.circState);
        JFileChooser chooser = this.proj.createChooser();
        if (this.mem.getCurrentImage() != null) {
            chooser.setSelectedFile(this.mem.getCurrentImage());
        }
        chooser.setDialogTitle(Strings.get("ramLoadDialogTitle"));
        int choice = chooser.showOpenDialog(this.frame);
        if (choice == 0) {
            File f = chooser.getSelectedFile();
            try {
                HexFile.open((HexModel)s.getContents(), f);
                this.mem.setCurrentImage(f);
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(this.frame, e.getMessage(), Strings.get("ramLoadErrorTitle"), 0);
            }
        }
    }

    private void doSave() {
        MemState s = this.mem.getState(this.circState);
        JFileChooser chooser = this.proj.createChooser();
        if (this.mem.getCurrentImage() != null) {
            chooser.setSelectedFile(this.mem.getCurrentImage());
        }
        chooser.setDialogTitle(Strings.get("ramSaveDialogTitle"));
        int choice = chooser.showSaveDialog(this.frame);
        if (choice == 0) {
            File f = chooser.getSelectedFile();
            try {
                HexFile.save(f, (HexModel)s.getContents());
                this.mem.setCurrentImage(f);
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(this.frame, e.getMessage(), Strings.get("ramSaveErrorTitle"), 0);
            }
        }
    }
}

