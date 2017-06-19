/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.prefs;

import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.LoaderException;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.prefs.OptionsPanel;
import com.cburch.logisim.gui.prefs.PreferencesFrame;
import com.cburch.logisim.gui.prefs.Strings;
import com.cburch.logisim.proj.LogisimPreferences;
import com.cburch.logisim.util.StringUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

class TemplateOptions
extends OptionsPanel {
    private MyListener myListener;
    private JRadioButton plain;
    private JRadioButton empty;
    private JRadioButton custom;
    private JTextField templateField;
    private JButton templateButton;

    public TemplateOptions(PreferencesFrame window) {
        super(window);
        this.myListener = new MyListener();
        this.plain = new JRadioButton();
        this.empty = new JRadioButton();
        this.custom = new JRadioButton();
        this.templateField = new JTextField(40);
        this.templateButton = new JButton();
        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(this.plain);
        bgroup.add(this.empty);
        bgroup.add(this.custom);
        this.plain.addActionListener(this.myListener);
        this.empty.addActionListener(this.myListener);
        this.custom.addActionListener(this.myListener);
        this.templateField.setEditable(false);
        this.templateButton.addActionListener(this.myListener);
        this.myListener.computeEnabled();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        this.setLayout(gridbag);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = -1;
        gbc.gridwidth = 3;
        gbc.anchor = 21;
        gridbag.setConstraints(this.plain, gbc);
        this.add(this.plain);
        gridbag.setConstraints(this.empty, gbc);
        this.add(this.empty);
        gridbag.setConstraints(this.custom, gbc);
        this.add(this.custom);
        gbc.fill = 2;
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = -1;
        JPanel strut = new JPanel();
        strut.setMinimumSize(new Dimension(50, 1));
        strut.setPreferredSize(new Dimension(50, 1));
        gbc.weightx = 0.0;
        gridbag.setConstraints(strut, gbc);
        this.add(strut);
        gbc.weightx = 1.0;
        gridbag.setConstraints(this.templateField, gbc);
        this.add(this.templateField);
        gbc.weightx = 0.0;
        gridbag.setConstraints(this.templateButton, gbc);
        this.add(this.templateButton);
        LogisimPreferences.addPropertyChangeListener("templateType", this.myListener);
        LogisimPreferences.addPropertyChangeListener("templateFile", this.myListener);
        switch (LogisimPreferences.getTemplateType()) {
            case 1: {
                this.plain.setSelected(true);
                break;
            }
            case 0: {
                this.empty.setSelected(true);
                break;
            }
            case 2: {
                this.custom.setSelected(true);
            }
        }
        this.myListener.setTemplateField(LogisimPreferences.getTemplateFile());
    }

    @Override
    public String getTitle() {
        return Strings.get("templateTitle");
    }

    @Override
    public String getHelpText() {
        return Strings.get("templateHelp");
    }

    @Override
    public void localeChanged() {
        this.plain.setText(Strings.get("templatePlainOption"));
        this.empty.setText(Strings.get("templateEmptyOption"));
        this.custom.setText(Strings.get("templateCustomOption"));
        this.templateButton.setText(Strings.get("templateSelectButton"));
    }

    static /* synthetic */ JButton access$000(TemplateOptions x0) {
        return x0.templateButton;
    }

    private class MyListener
    implements ActionListener,
    PropertyChangeListener {
        private MyListener() {
        }

        /*
         * Unable to fully structure code
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         * Lifted jumps to return sites
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            block22 : {
                src = event.getSource();
                if (src != TemplateOptions.access$000(TemplateOptions.this)) ** GOTO lbl50
                chooser = new JFileChooser();
                chooser.setDialogTitle(Strings.get("selectDialogTitle"));
                chooser.setApproveButtonText(Strings.get("selectDialogButton"));
                action = chooser.showOpenDialog(TemplateOptions.this.getPreferencesFrame());
                if (action != 0) ** GOTO lbl60
                file = chooser.getSelectedFile();
                reader = null;
                loader = new Loader(TemplateOptions.this.getPreferencesFrame());
                reader = new FileReader(file);
                template = LogisimFile.load(reader, loader);
                LogisimPreferences.setTemplateFile(file, template);
                LogisimPreferences.setTemplateType(2);
                try {
                    if (reader != null) {
                        reader.close();
                    }
                    break block22;
                }
                catch (IOException ex) {}
                ** GOTO lbl60
                catch (LoaderException ex) {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                        break block22;
                    }
                    catch (IOException ex) {}
                    break block22;
                    catch (IOException ex) {
                        try {
                            JOptionPane.showMessageDialog(TemplateOptions.this.getPreferencesFrame(), StringUtil.format(Strings.get("templateErrorMessage"), ex.toString()), Strings.get("templateErrorTitle"), 0);
                        }
                        catch (Throwable var9_15) {
                            try {
                                if (reader == null) throw var9_15;
                                reader.close();
                                throw var9_15;
                            }
                            catch (IOException ex) {
                                // empty catch block
                            }
                            throw var9_15;
                        }
                        try {
                            if (reader != null) {
                                reader.close();
                            }
                            break block22;
                        }
                        catch (IOException ex) {}
                        break block22;
                    }
                }
lbl50: // 1 sources:
                value = -1;
                if (TemplateOptions.access$100(TemplateOptions.this).isSelected()) {
                    value = 1;
                } else if (TemplateOptions.access$200(TemplateOptions.this).isSelected()) {
                    value = 0;
                } else if (TemplateOptions.access$300(TemplateOptions.this).isSelected()) {
                    value = 2;
                }
                LogisimPreferences.setTemplateType(value);
            }
            this.computeEnabled();
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String prop = event.getPropertyName();
            if (prop.equals("templateType")) {
                int value;
                TemplateOptions.this.plain.setSelected((value = LogisimPreferences.getTemplateType()) == 1);
                TemplateOptions.this.empty.setSelected(value == 0);
                TemplateOptions.this.custom.setSelected(value == 2);
            } else if (prop.equals("templateFile")) {
                this.setTemplateField((File)event.getNewValue());
            }
        }

        private void setTemplateField(File f) {
            try {
                TemplateOptions.this.templateField.setText(f == null ? "" : f.getCanonicalPath());
            }
            catch (IOException e) {
                TemplateOptions.this.templateField.setText(f.getName());
            }
            this.computeEnabled();
        }

        private void computeEnabled() {
            TemplateOptions.this.custom.setEnabled(!TemplateOptions.this.templateField.getText().equals(""));
            TemplateOptions.this.templateField.setEnabled(TemplateOptions.this.custom.isSelected());
        }
    }

}

