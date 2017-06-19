/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Stack;
import java.util.Vector;
import javax.help.AbstractHelpAction;
import javax.help.HelpHistoryModel;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.JHelp;
import javax.help.TextHelpModel;
import javax.help.event.HelpHistoryModelEvent;
import javax.help.event.HelpHistoryModelListener;
import javax.help.event.HelpModelEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.UIManager;

public class BackAction
extends AbstractHelpAction
implements MouseListener,
HelpHistoryModelListener {
    private static final String NAME = "BackAction";
    private static final int DELAY = 500;
    private Timer timer;
    private HelpHistoryModel historyModel;

    public BackAction(Object object) {
        super(object, "BackAction");
        if (object instanceof JHelp) {
            JHelp jHelp = (JHelp)object;
            this.historyModel = jHelp.getHistoryModel();
            this.historyModel.addHelpHistoryModelListener(this);
            this.setEnabled(this.historyModel.getIndex() > 0);
            this.putValue("icon", UIManager.getIcon("BackAction.icon"));
            Locale locale = null;
            try {
                locale = jHelp.getModel().getHelpSet().getLocale();
            }
            catch (NullPointerException var4_4) {
                locale = Locale.getDefault();
            }
            this.putValue("tooltip", HelpUtilities.getString(locale, "tooltip.BackAction"));
            this.putValue("access", HelpUtilities.getString(locale, "access.BackAction"));
        }
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        if (this.timer != null) {
            this.timer.stop();
        }
    }

    public void mousePressed(MouseEvent mouseEvent) {
        this.timer = new Timer(500, new TimeListener(mouseEvent));
        this.timer.start();
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if (this.historyModel != null && this.isEnabled()) {
            this.historyModel.goBack();
        }
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    private void showBackwardHistory(MouseEvent mouseEvent) {
        JPopupMenu jPopupMenu = new JPopupMenu("Backward History");
        if (this.historyModel == null) {
            return;
        }
        Stack<JMenuItem> stack = new Stack<JMenuItem>();
        Locale locale = ((JHelp)this.getControl()).getModel().getHelpSet().getLocale();
        Enumeration enumeration = this.historyModel.getBackwardHistory().elements();
        JMenuItem jMenuItem = null;
        int n = 0;
        while (enumeration.hasMoreElements()) {
            HelpModelEvent helpModelEvent = (HelpModelEvent)enumeration.nextElement();
            if (helpModelEvent != null) {
                String string = helpModelEvent.getHistoryName();
                if (string == null) {
                    string = HelpUtilities.getString(locale, "history.unknownTitle");
                }
                jMenuItem = new JMenuItem(string);
                jMenuItem.addActionListener(new HistoryActionListener(n));
                stack.push(jMenuItem);
            }
            ++n;
        }
        int n2 = stack.size();
        int n3 = 0;
        while (n3 < n2) {
            jPopupMenu.add((JMenuItem)stack.pop());
            ++n3;
        }
        jPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
    }

    public void historyChanged(HelpHistoryModelEvent helpHistoryModelEvent) {
        this.setEnabled(helpHistoryModelEvent.isPrevious());
    }

    private class HistoryActionListener
    implements ActionListener {
        private int index;

        public HistoryActionListener(int n) {
            this.index = n;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            if (BackAction.this.historyModel != null) {
                BackAction.this.historyModel.setHistoryEntry(this.index);
            }
        }
    }

    private class TimeListener
    implements ActionListener {
        private MouseEvent e;

        public TimeListener(MouseEvent mouseEvent) {
            this.e = mouseEvent;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            BackAction.this.timer.stop();
            if (BackAction.this.isEnabled()) {
                BackAction.this.showBackwardHistory(this.e);
            }
        }
    }

}

