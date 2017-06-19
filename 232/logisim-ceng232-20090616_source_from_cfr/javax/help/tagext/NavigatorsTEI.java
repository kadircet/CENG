/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.TagData
 *  javax.servlet.jsp.tagext.TagExtraInfo
 *  javax.servlet.jsp.tagext.VariableInfo
 */
package javax.help.tagext;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class NavigatorsTEI
extends TagExtraInfo {
    public VariableInfo[] getVariableInfo(TagData tagData) {
        return new VariableInfo[]{new VariableInfo("className", "java.lang.String", true, 0), new VariableInfo("name", "java.lang.String", true, 0), new VariableInfo("tip", "java.lang.String", true, 0), new VariableInfo("iconURL", "java.lang.String", true, 0), new VariableInfo("isCurrentNav", "java.lang.Boolean", true, 0)};
    }
}

