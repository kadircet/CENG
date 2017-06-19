/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
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
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.Map;
import javax.help.NavigatorView;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class NavigatorsTag
extends BodyTagSupport {
    private HelpBroker hb;
    private HelpSet hs;
    private String curNav = null;
    private NavigatorView[] views;
    private int i;

    public void setHelpBroker(HelpBroker helpBroker) {
        this.hb = helpBroker;
        this.hs = helpBroker.getHelpSet();
    }

    public void setCurrentNav(String string) {
        this.curNav = string;
    }

    public void initialize() {
        this.checkRequestParams();
        this.initCurNav();
        this.views = this.hs.getNavigatorViews();
    }

    public int doStartTag() {
        this.initialize();
        if (this.views.length > 0) {
            this.i = 0;
            this.setNavigatorAttributes(this.views[this.i++]);
            return 2;
        }
        return 0;
    }

    private void checkRequestParams() {
        ServletRequest servletRequest = this.pageContext.getRequest();
        String string = servletRequest.getParameter("nav");
        if (string != null) {
            this.curNav = string;
        }
    }

    private void initCurNav() {
        if (this.curNav != null) {
            try {
                this.hb.setCurrentView(this.curNav);
            }
            catch (IllegalArgumentException var1_1) {}
        } else {
            this.curNav = this.hb.getCurrentView();
        }
    }

    public int doAfterBody() throws JspException {
        BodyContent bodyContent = this.getBodyContent();
        try {
            bodyContent.writeOut((Writer)this.getPreviousOut());
        }
        catch (IOException var2_2) {
            throw new JspTagException("NavigatorsTag: " + var2_2.getMessage());
        }
        bodyContent.clearBody();
        if (this.i < this.views.length) {
            this.setNavigatorAttributes(this.views[this.i++]);
            return 2;
        }
        return 0;
    }

    private void setNavigatorAttributes(NavigatorView navigatorView) {
        this.pageContext.setAttribute("className", (Object)navigatorView.getClass().getName());
        this.pageContext.setAttribute("name", (Object)navigatorView.getName());
        this.pageContext.setAttribute("tip", (Object)navigatorView.getLabel());
        String string = this.getIconURL(navigatorView);
        this.pageContext.setAttribute("iconURL", (Object)string);
        this.pageContext.setAttribute("isCurrentNav", (Object)new Boolean(this.curNav.compareTo(navigatorView.getName()) == 0));
    }

    private String getIconURL(NavigatorView navigatorView) {
        URL uRL = null;
        Map.ID iD = navigatorView.getImageID();
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
}

