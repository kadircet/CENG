/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.data.Value;
import com.cburch.logisim.gui.log.LogFrame;
import com.cburch.logisim.gui.log.LogPanel;
import com.cburch.logisim.gui.log.Model;
import com.cburch.logisim.gui.log.ModelEvent;
import com.cburch.logisim.gui.log.ModelListener;
import com.cburch.logisim.gui.log.Strings;
import com.cburch.logisim.util.StringUtil;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

class FilePanel
extends LogPanel {
    private Listener listener;
    private JLabel enableLabel;
    private JButton enableButton;
    private JLabel fileLabel;
    private JTextField fileField;
    private JButton selectButton;
    private JCheckBox headerCheckBox;
    private JFileChooser chooser;

    public FilePanel(LogFrame frame) {
        super(frame);
        this.listener = new Listener();
        this.enableLabel = new JLabel();
        this.enableButton = new JButton();
        this.fileLabel = new JLabel();
        this.fileField = new JTextField();
        this.selectButton = new JButton();
        this.headerCheckBox = new JCheckBox();
        this.chooser = new JFileChooser();
        JPanel filePanel = new JPanel(new GridBagLayout());
        GridBagLayout gb = (GridBagLayout)filePanel.getLayout();
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = 2;
        gb.setConstraints(this.fileLabel, gc);
        filePanel.add(this.fileLabel);
        gc.weightx = 1.0;
        gb.setConstraints(this.fileField, gc);
        filePanel.add(this.fileField);
        gc.weightx = 0.0;
        gb.setConstraints(this.selectButton, gc);
        filePanel.add(this.selectButton);
        this.fileField.setEditable(false);
        this.fileField.setEnabled(false);
        this.setLayout(new GridBagLayout());
        gb = (GridBagLayout)this.getLayout();
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.weightx = 1.0;
        gc.gridy = -1;
        JPanel glue = new JPanel();
        gc.weighty = 1.0;
        gb.setConstraints(glue, gc);
        this.add(glue);
        gc.weighty = 0.0;
        gb.setConstraints(this.enableLabel, gc);
        this.add(this.enableLabel);
        gb.setConstraints(this.enableButton, gc);
        this.add(this.enableButton);
        glue = new JPanel();
        gc.weighty = 1.0;
        gb.setConstraints(glue, gc);
        this.add(glue);
        gc.weighty = 0.0;
        gc.fill = 2;
        gb.setConstraints(filePanel, gc);
        this.add(filePanel);
        gc.fill = 0;
        glue = new JPanel();
        gc.weighty = 1.0;
        gb.setConstraints(glue, gc);
        this.add(glue);
        gc.weighty = 0.0;
        gb.setConstraints(this.headerCheckBox, gc);
        this.add(this.headerCheckBox);
        glue = new JPanel();
        gc.weighty = 1.0;
        gb.setConstraints(glue, gc);
        this.add(glue);
        gc.weighty = 0.0;
        this.enableButton.addActionListener(this.listener);
        this.selectButton.addActionListener(this.listener);
        this.headerCheckBox.addActionListener(this.listener);
        this.modelChanged(null, this.getModel());
        this.localeChanged();
    }

    @Override
    public String getTitle() {
        return Strings.get("fileTab");
    }

    @Override
    public String getHelpText() {
        return Strings.get("fileHelp");
    }

    @Override
    public void localeChanged() {
        this.listener.computeEnableItems(this.getModel());
        this.fileLabel.setText(Strings.get("fileLabel") + " ");
        this.selectButton.setText(Strings.get("fileSelectButton"));
        this.headerCheckBox.setText(Strings.get("fileHeaderCheck"));
    }

    @Override
    public void modelChanged(Model oldModel, Model newModel) {
        if (oldModel != null) {
            oldModel.removeModelListener(this.listener);
        }
        if (newModel != null) {
            newModel.addModelListener(this.listener);
            this.listener.filePropertyChanged(null);
        }
    }

    private class Listener
    implements ActionListener,
    ModelListener {
        private Listener() {
        }

        @Override
        public void selectionChanged(ModelEvent event) {
        }

        @Override
        public void entryAdded(ModelEvent event, Value[] values) {
        }

        @Override
        public void filePropertyChanged(ModelEvent event) {
            Model model = FilePanel.this.getModel();
            this.computeEnableItems(model);
            File file = model.getFile();
            FilePanel.this.fileField.setText(file == null ? "" : file.getPath());
            FilePanel.this.enableButton.setEnabled(file != null);
            FilePanel.this.headerCheckBox.setSelected(model.getFileHeader());
        }

        private void computeEnableItems(Model model) {
            if (model.isFileEnabled()) {
                FilePanel.this.enableLabel.setText(Strings.get("fileEnabled"));
                FilePanel.this.enableButton.setText(Strings.get("fileDisableButton"));
            } else {
                FilePanel.this.enableLabel.setText(Strings.get("fileDisabled"));
                FilePanel.this.enableButton.setText(Strings.get("fileEnableButton"));
            }
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src == FilePanel.this.enableButton) {
                FilePanel.this.getModel().setFileEnabled(!FilePanel.this.getModel().isFileEnabled());
            } else if (src == FilePanel.this.selectButton) {
                int result = FilePanel.this.chooser.showSaveDialog(FilePanel.this.getLogFrame());
                if (result != 0) {
                    return;
                }
                File file = FilePanel.this.chooser.getSelectedFile();
                if (file.exists() && (!file.canWrite() || file.isDirectory())) {
                    JOptionPane.showMessageDialog(FilePanel.this.getLogFrame(), StringUtil.format(Strings.get("fileCannotWriteMessage"), file.getName()), Strings.get("fileCannotWriteTitle"), 0);
                    return;
                }
                if (file.exists() && file.length() > 0) {
                    Object[] options = new String[]{Strings.get("fileOverwriteOption"), Strings.get("fileAppendOption"), Strings.get("fileCancelOption")};
                    int option = JOptionPane.showOptionDialog(FilePanel.this.getLogFrame(), StringUtil.format(Strings.get("fileExistsMessage"), file.getName()), Strings.get("fileExistsTitle"), 0, 3, null, options, options[0]);
                    if (option == 0) {
                        try {
                            FileWriter delete = new FileWriter(file);
                            delete.close();
                        }
                        catch (IOException e) {}
                    } else if (option != 1) {
                        return;
                    }
                }
                FilePanel.this.getModel().setFile(file);
            } else if (src == FilePanel.this.headerCheckBox) {
                FilePanel.this.getModel().setFileHeader(FilePanel.this.headerCheckBox.isSelected());
            }
        }
    }

}

