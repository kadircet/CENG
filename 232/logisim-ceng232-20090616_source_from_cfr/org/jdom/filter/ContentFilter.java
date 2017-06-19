/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.filter;

import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.filter.AbstractFilter;

public class ContentFilter
extends AbstractFilter {
    private static final String CVS_ID = "@(#) $RCSfile: ContentFilter.java,v $ $Revision: 1.14 $ $Date: 2004/08/31 04:56:07 $ $Name: jdom_1_0 $";
    public static final int ELEMENT = 1;
    public static final int CDATA = 2;
    public static final int TEXT = 4;
    public static final int COMMENT = 8;
    public static final int PI = 16;
    public static final int ENTITYREF = 32;
    public static final int DOCUMENT = 64;
    public static final int DOCTYPE = 128;
    private int filterMask;

    public ContentFilter() {
        this.setDefaultMask();
    }

    public ContentFilter(int mask) {
        this.setFilterMask(mask);
    }

    public ContentFilter(boolean allVisible) {
        if (allVisible) {
            this.setDefaultMask();
        } else {
            this.filterMask &= ~ this.filterMask;
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ContentFilter)) {
            return false;
        }
        ContentFilter filter = (ContentFilter)obj;
        if (this.filterMask != filter.filterMask) {
            return false;
        }
        return true;
    }

    public int getFilterMask() {
        return this.filterMask;
    }

    public int hashCode() {
        return this.filterMask;
    }

    public boolean matches(Object obj) {
        if (obj instanceof Element) {
            return (this.filterMask & 1) != 0;
        }
        if (obj instanceof CDATA) {
            return (this.filterMask & 2) != 0;
        }
        if (obj instanceof Text) {
            return (this.filterMask & 4) != 0;
        }
        if (obj instanceof Comment) {
            return (this.filterMask & 8) != 0;
        }
        if (obj instanceof ProcessingInstruction) {
            return (this.filterMask & 16) != 0;
        }
        if (obj instanceof EntityRef) {
            return (this.filterMask & 32) != 0;
        }
        if (obj instanceof Document) {
            return (this.filterMask & 64) != 0;
        }
        if (obj instanceof DocType) {
            return (this.filterMask & 128) != 0;
        }
        return false;
    }

    public void setCDATAVisible(boolean visible) {
        this.filterMask = visible ? (this.filterMask |= 2) : (this.filterMask &= -3);
    }

    public void setCommentVisible(boolean visible) {
        this.filterMask = visible ? (this.filterMask |= 8) : (this.filterMask &= -9);
    }

    public void setDefaultMask() {
        this.filterMask = 255;
    }

    public void setDocTypeVisible(boolean visible) {
        this.filterMask = visible ? (this.filterMask |= 128) : (this.filterMask &= -129);
    }

    public void setDocumentContent() {
        this.filterMask = 153;
    }

    public void setElementContent() {
        this.filterMask = 63;
    }

    public void setElementVisible(boolean visible) {
        this.filterMask = visible ? (this.filterMask |= 1) : (this.filterMask &= -2);
    }

    public void setEntityRefVisible(boolean visible) {
        this.filterMask = visible ? (this.filterMask |= 32) : (this.filterMask &= -33);
    }

    public void setFilterMask(int mask) {
        this.setDefaultMask();
        this.filterMask &= mask;
    }

    public void setPIVisible(boolean visible) {
        this.filterMask = visible ? (this.filterMask |= 16) : (this.filterMask &= -17);
    }

    public void setTextVisible(boolean visible) {
        this.filterMask = visible ? (this.filterMask |= 4) : (this.filterMask &= -5);
    }
}

