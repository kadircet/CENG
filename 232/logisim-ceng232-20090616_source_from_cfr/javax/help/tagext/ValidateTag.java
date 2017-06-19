/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.jsp.PageContext
 *  javax.servlet.jsp.tagext.TagSupport
 */
package javax.help.tagext;

import java.net.MalformedURLException;
import java.net.URL;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.InvalidHelpSetContextException;
import javax.help.Map;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

public class ValidateTag
extends TagSupport {
    private HelpBroker helpBroker;
    private String invalidURLPath = "invalidhelp.html";
    private String hsName = null;
    private String id = null;
    private boolean merge = false;

    public void setHelpBroker(HelpBroker helpBroker) {
        this.helpBroker = helpBroker;
    }

    public void setInvalidURL(String string) {
        this.invalidURLPath = string;
    }

    public void setHelpSetName(String string) {
        this.hsName = string;
    }

    public void setCurrentID(String string) {
        this.id = string;
    }

    public void setMerge(boolean bl) {
        this.merge = bl;
    }

    public int doStartTag() {
        this.checkRequestParams();
        this.validateHelpSet();
        this.validateID();
        return 0;
    }

    private void checkRequestParams() {
        ServletRequest servletRequest = this.pageContext.getRequest();
        if (this.hsName == null) {
            this.hsName = servletRequest.getParameter("helpset");
        }
        if (this.id == null) {
            this.id = servletRequest.getParameter("id");
        }
    }

    private void validateHelpSet() {
        HelpSet helpSet = this.helpBroker.getHelpSet();
        if (helpSet != null && this.hsName == null) {
            return;
        }
        if (helpSet == null && this.hsName == null) {
            try {
                this.pageContext.forward(this.invalidURLPath);
            }
            catch (Exception var2_2) {
                return;
            }
        }
        if (helpSet == null && this.hsName != null) {
            this.helpBroker.setHelpSet(this.createHelpSet());
            return;
        }
        if (helpSet != null && this.hsName != null && this.merge) {
            helpSet.add(this.createHelpSet());
        }
    }

    private HelpSet createHelpSet() {
        HelpSet helpSet = null;
        ServletRequest servletRequest = this.pageContext.getRequest();
        if (!this.hsName.startsWith("/")) {
            this.hsName = "/" + this.hsName;
        }
        URL uRL = null;
        try {
            uRL = this.hsName.startsWith("http") ? new URL(this.hsName) : new URL(servletRequest.getScheme(), servletRequest.getServerName(), servletRequest.getServerPort(), this.hsName);
            helpSet = new HelpSet(null, uRL);
        }
        catch (MalformedURLException var4_4) {
        }
        catch (HelpSetException var5_5) {
            throw new RuntimeException(var5_5.getMessage());
        }
        return helpSet;
    }

    private void validateID() {
        if (this.id != null) {
            this.helpBroker.setCurrentID(this.id);
        } else if (this.helpBroker.getCurrentID() == null && this.helpBroker.getCurrentURL() == null) {
            try {
                this.helpBroker.setCurrentID(this.helpBroker.getHelpSet().getHomeID());
            }
            catch (InvalidHelpSetContextException var1_1) {
                // empty catch block
            }
        }
    }
}

