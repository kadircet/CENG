/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.JspTagException
 *  javax.servlet.jsp.JspWriter
 *  javax.servlet.jsp.PageContext
 *  javax.servlet.jsp.tagext.BodyContent
 *  javax.servlet.jsp.tagext.BodyTagSupport
 */
package javax.help.tagext;

import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.NoSuchElementException;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.Map;
import javax.help.Merge;
import javax.help.MergeHelpUtilities;
import javax.help.NavigatorView;
import javax.help.TOCItem;
import javax.help.TOCView;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class TOCItemTag
extends BodyTagSupport {
    private Enumeration treeEnum;
    private DefaultMutableTreeNode topNode;
    private String baseID = "root";
    private HelpBroker hb;
    private TOCView view = null;

    public void setTocView(TOCView tOCView) {
        this.view = tOCView;
    }

    public void setBaseID(String string) {
        this.baseID = string;
    }

    public void setHelpBroker(HelpBroker helpBroker) {
        this.hb = helpBroker;
    }

    private void initialize() {
        if (this.view == null) {
            return;
        }
        this.topNode = this.view.getDataAsTree();
        String string = this.view.getMergeType();
        HelpSet helpSet = this.view.getHelpSet();
        Locale locale = helpSet.getLocale();
        MergeHelpUtilities.mergeNodeChildren(string, this.topNode);
        this.addSubHelpSets(helpSet);
        this.treeEnum = this.topNode.preorderEnumeration();
    }

    private void addSubHelpSets(HelpSet helpSet) {
        Enumeration enumeration = helpSet.getHelpSets();
        while (enumeration.hasMoreElements()) {
            HelpSet helpSet2 = (HelpSet)enumeration.nextElement();
            if (helpSet2 == null) continue;
            NavigatorView[] arrnavigatorView = helpSet2.getNavigatorViews();
            int n = 0;
            while (n < arrnavigatorView.length) {
                Merge merge;
                if (arrnavigatorView[n] instanceof TOCView && (merge = Merge.DefaultMergeFactory.getMerge(this.view, arrnavigatorView[n])) != null) {
                    merge.processMerge(this.topNode);
                }
                ++n;
            }
            this.addSubHelpSets(helpSet2);
        }
    }

    public int doStartTag() {
        this.initialize();
        if (this.treeEnum.hasMoreElements()) {
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)this.treeEnum.nextElement();
            if (defaultMutableTreeNode == this.topNode) {
                try {
                    defaultMutableTreeNode = (DefaultMutableTreeNode)this.treeEnum.nextElement();
                }
                catch (NoSuchElementException var2_2) {
                    return 0;
                }
            }
            this.setNodeAttributes(defaultMutableTreeNode);
            return 2;
        }
        return 0;
    }

    public int doAfterBody() throws JspException {
        BodyContent bodyContent = this.getBodyContent();
        try {
            bodyContent.writeOut((Writer)this.getPreviousOut());
        }
        catch (IOException var2_2) {
            throw new JspTagException("TOCItemTag: " + var2_2.getMessage());
        }
        bodyContent.clearBody();
        if (this.treeEnum.hasMoreElements()) {
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)this.treeEnum.nextElement();
            this.setNodeAttributes(defaultMutableTreeNode);
            return 2;
        }
        return 0;
    }

    private void setNodeAttributes(DefaultMutableTreeNode defaultMutableTreeNode) {
        TOCItem tOCItem = (TOCItem)defaultMutableTreeNode.getUserObject();
        this.pageContext.setAttribute("name", (Object)tOCItem.getName());
        String string = "";
        if (tOCItem.getID() != null) {
            string = tOCItem.getID().id;
        }
        this.pageContext.setAttribute("helpID", (Object)string);
        this.pageContext.setAttribute("parent", (Object)Integer.toHexString(defaultMutableTreeNode.getParent().hashCode()));
        String string2 = this.getID(defaultMutableTreeNode.getParent());
        this.pageContext.setAttribute("parentID", (Object)string2);
        this.pageContext.setAttribute("node", (Object)Integer.toHexString(defaultMutableTreeNode.hashCode()));
        string2 = this.getID(defaultMutableTreeNode);
        this.pageContext.setAttribute("nodeID", (Object)string2);
        String string3 = this.getContentURL(tOCItem);
        this.pageContext.setAttribute("contentURL", (Object)string3);
        String string4 = this.getIconURL(defaultMutableTreeNode, tOCItem, true);
        this.pageContext.setAttribute("iconURL", (Object)string4);
        String string5 = this.getIconURL(defaultMutableTreeNode, tOCItem, false);
        this.pageContext.setAttribute("iconOpenURL", (Object)string5);
        String string6 = Integer.toString(tOCItem.getExpansionType());
        this.pageContext.setAttribute("expansionType", (Object)string6);
    }

    private String getID(TreeNode treeNode) {
        if (treeNode == this.topNode) {
            return this.baseID;
        }
        TreeNode treeNode2 = treeNode.getParent();
        if (treeNode2 == null) {
            return "";
        }
        String string = this.getID(treeNode2);
        string = string.concat("_" + Integer.toString(treeNode2.getIndex(treeNode)));
        return string;
    }

    private String getContentURL(TOCItem tOCItem) {
        URL uRL = null;
        Map.ID iD = tOCItem.getID();
        if (iD != null) {
            HelpSet helpSet = iD.hs;
            Map map = helpSet.getLocalMap();
            try {
                uRL = map.getURLFromID(iD);
            }
            catch (MalformedURLException var6_6) {
                // empty catch block
            }
        }
        if (uRL == null) {
            return "";
        }
        return uRL.toExternalForm();
    }

    private String getIconURL(DefaultMutableTreeNode defaultMutableTreeNode, TOCItem tOCItem, boolean bl) {
        URL uRL = null;
        Map.ID iD = tOCItem.getImageID();
        if (iD == null) {
            iD = defaultMutableTreeNode.isLeaf() ? this.view.getTopicImageID() : (bl ? this.view.getCategoryClosedImageID() : this.view.getCategoryOpenImageID());
        }
        if (iD != null) {
            HelpSet helpSet = iD.hs;
            Map map = helpSet.getLocalMap();
            try {
                uRL = map.getURLFromID(iD);
            }
            catch (MalformedURLException var8_8) {
                // empty catch block
            }
        }
        if (uRL == null) {
            return "";
        }
        return uRL.toExternalForm();
    }
}

