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

public class ForwardAction
extends AbstractHelpAction
implements MouseListener,
HelpHistoryModelListener {
    private static final String NAME = "ForwardAction";
    private static final int DELAY = 500;
    private Timer timer;
    private HelpHistoryModel historyModel;

    public ForwardAction(Object object) {
        super(object, "ForwardAction");
        if (object instanceof JHelp) {
            JHelp jHelp = (JHelp)object;
            this.historyModel = jHelp.getHistoryModel();
            this.historyModel.addHelpHistoryModelListener(this);
            this.setEnabled(this.historyModel.getIndex() > 0);
            this.putValue("icon", UIManager.getIcon("ForwardAction.icon"));
            Locale locale = null;
            try {
                locale = jHelp.getModel().getHelpSet().getLocale();
            }
            catch (NullPointerException var4_4) {
                locale = Locale.getDefault();
            }
            this.putValue("tooltip", HelpUtilities.getString(locale, "tooltip.ForwardAction"));
            this.putValue("access", HelpUtilities.getString(locale, "access.ForwardAction"));
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
            this.historyModel.goForward();
        }
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    private void showForwardHistory(MouseEvent mouseEvent) {
        JPopupMenu jPopupMenu = new JPopupMenu("Forward History");
        if (this.historyModel == null) {
            return;
        }
        Locale locale = ((JHelp)this.getControl()).getModel().getHelpSet().getLocale();
        Enumeration enumeration = this.historyModel.getForwardHistory().elements();
        JMenuItem jMenuItem = null;
        int n = this.historyModel.getIndex() + 1;
        int n2 = 0;
        while (enumeration.hasMoreElements()) {
            HelpModelEvent helpModelEvent = (HelpModelEvent)enumeration.nextElement();
            if (helpModelEvent != null) {
                String string = helpModelEvent.getHistoryName();
                if (string == null) {
                    string = HelpUtilities.getString(locale, "history.unknownTitle");
                }
                jMenuItem = new JMenuItem(string);
                jMenuItem.addActionListener(new HistoryActionListener(n2 + n));
                jPopupMenu.add(jMenuItem);
            }
            ++n2;
        }
        jPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
    }

    public void historyChanged(HelpHistoryModelEvent helpHistoryModelEvent) {
        this.setEnabled(helpHistoryModelEvent.isNext());
    }

    private class HistoryActionListener
    implements ActionListener {
        private int index;

        public HistoryActionListener(int n) {
            this.index = n;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            if (ForwardAction.this.historyModel != null) {
                ForwardAction.this.historyModel.setHistoryEntry(this.index);
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
            ForwardAction.this.timer.stop();
            if (ForwardAction.this.isEnabled()) {
                ForwardAction.this.showForwardHistory(this.e);
            }
        }
    }

}

