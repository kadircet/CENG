/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.Strings;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.Border;

public abstract class JDialogOk
extends JDialog {
    private JPanel contents = new JPanel(new BorderLayout());
    protected JButton ok = new JButton(Strings.get("dlogOkButton"));
    protected JButton cancel = new JButton(Strings.get("dlogCancelButton"));

    public JDialogOk(Dialog parent, String title, boolean model) {
        super(parent, title, true);
        this.configure();
    }

    public JDialogOk(Frame parent, String title, boolean model) {
        super(parent, title, true);
        this.configure();
    }

    private void configure() {
        MyListener listener = new MyListener();
        this.addWindowListener(listener);
        this.ok.addActionListener(listener);
        this.cancel.addActionListener(listener);
        Box buttons = Box.createHorizontalBox();
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttons.add(Box.createHorizontalGlue());
        buttons.add(this.ok);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(this.cancel);
        buttons.add(Box.createHorizontalGlue());
        Container pane = super.getContentPane();
        pane.add((Component)this.contents, "Center");
        pane.add((Component)buttons, "South");
    }

    @Override
    public Container getContentPane() {
        return this.contents;
    }

    public abstract void okClicked();

    public void cancelClicked() {
    }

    private class MyListener
    extends WindowAdapter
    implements ActionListener {
        private MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == JDialogOk.this.ok) {
                JDialogOk.this.okClicked();
                JDialogOk.this.dispose();
            } else if (src == JDialogOk.this.cancel) {
                JDialogOk.this.cancelClicked();
                JDialogOk.this.dispose();
            }
        }

        @Override
        public void windowClosing(WindowEvent e) {
            JDialogOk.this.removeWindowListener(this);
            JDialogOk.this.cancelClicked();
            JDialogOk.this.dispose();
        }
    }

}

