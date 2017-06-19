/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkListener;

public class StandardMacAboutFrame
extends JFrame {
    private JLabel applicationIconLabel;
    private JTextArea applicationNameField;
    private JTextArea versionField;
    private JEditorPane creditsField;
    private JScrollPane creditsScrollPane;
    private JTextArea copyrightField;
    private String applicationVersion;
    private String buildVersion;
    private HyperlinkListener hyperlinkListener;

    public StandardMacAboutFrame(String applicationName, String applicationVersion) {
        this.setResizable(false);
        this.setDefaultCloseOperation(2);
        JPanel c = (JPanel)this.getContentPane();
        c.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 100.0;
        gbc.insets.top = 5;
        gbc.insets.bottom = 5;
        gbc.gridy = 0;
        this.applicationIconLabel = new JLabel();
        c.add((Component)this.applicationIconLabel, gbc);
        gbc.gridy = 1;
        this.applicationNameField = new JTextArea("java");
        this.applicationNameField.setEditable(false);
        this.applicationNameField.setOpaque(false);
        this.applicationNameField.setFont(new Font("Lucida Grande", 1, 14));
        c.add((Component)this.applicationNameField, gbc);
        gbc.gridy = 2;
        this.versionField = new JTextArea("Version x.x");
        this.versionField.setEditable(false);
        this.versionField.setOpaque(false);
        Font f = new Font("Lucida Grande", 0, 10);
        this.versionField.setFont(f);
        c.add((Component)this.versionField, gbc);
        gbc.gridy = 3;
        gbc.fill = 2;
        this.creditsField = new JEditorPane();
        this.creditsField.setMargin(new Insets(2, 4, 2, 4));
        this.creditsField.setEditable(false);
        this.creditsScrollPane = new JScrollPane(this.creditsField, 22, 31);
        Border bo = this.creditsScrollPane.getBorder();
        Insets i = bo.getBorderInsets(this.creditsScrollPane);
        this.creditsScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, - i.left + 1, 0, - i.right + 1), bo));
        this.creditsScrollPane.setPreferredSize(new Dimension(100, 150));
        c.add((Component)this.creditsScrollPane, gbc);
        gbc.gridy = 4;
        gbc.insets.bottom = 32;
        gbc.fill = 0;
        this.copyrightField = new JTextArea(" ");
        this.copyrightField.setEditable(false);
        this.copyrightField.setOpaque(false);
        this.copyrightField.setFont(f);
        c.add((Component)this.copyrightField, gbc);
        this.applicationIconLabel.setVisible(false);
        this.creditsScrollPane.setVisible(false);
        if (applicationName != null) {
            this.applicationNameField.setText(applicationName);
        }
        this.applicationVersion = applicationVersion;
        if (applicationVersion != null) {
            this.versionField.setText(applicationVersion);
        }
        this.packAndCenter();
    }

    public void setApplicationIcon(Icon applicationIcon) {
        this.applicationIconLabel.setIcon(applicationIcon);
        this.applicationIconLabel.setVisible(applicationIcon != null);
        this.packAndCenter();
    }

    public void setApplicationName(String applicationName) {
        this.applicationNameField.setText(applicationName != null ? applicationName : "java");
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
        this.applyVersion();
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
        this.applyVersion();
    }

    private void applyVersion() {
        StringBuffer b = new StringBuffer();
        if (this.applicationVersion != null) {
            b.append(this.applicationVersion);
        } else {
            b.append("Version x.x");
        }
        if (this.buildVersion != null) {
            b.append(" (v");
            b.append(this.buildVersion);
            b.append(")");
        }
        this.versionField.setText(b.toString());
    }

    public void setCredits(String credits, String contentType) {
        if (credits != null) {
            this.creditsField.setContentType(contentType);
        }
        this.creditsField.setText(credits != null ? credits : "");
        this.creditsField.setCaretPosition(0);
        this.creditsScrollPane.setVisible(credits != null);
        this.packAndCenter();
    }

    public void setCreditsPreferredSize(Dimension preferredSize) {
        this.creditsScrollPane.setPreferredSize(preferredSize);
        this.packAndCenter();
    }

    public void setHyperlinkListener(HyperlinkListener l) {
        if (this.hyperlinkListener != null) {
            this.creditsField.removeHyperlinkListener(this.hyperlinkListener);
        }
        this.hyperlinkListener = l;
        if (l != null) {
            this.creditsField.addHyperlinkListener(l);
        }
    }

    public void setCopyright(String copyright) {
        this.copyrightField.setText(copyright != null ? copyright : " ");
        this.packAndCenter();
    }

    private void packAndCenter() {
        this.pack();
        this.setSize(285, this.getSize().height);
        Dimension ss = this.getToolkit().getScreenSize();
        Dimension fs = this.getSize();
        this.setLocation((ss.width - fs.width) / 2, (ss.height - fs.height) / 4);
    }
}

