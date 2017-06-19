/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdom.Content;
import org.jdom.IllegalDataException;
import org.jdom.IllegalTargetException;
import org.jdom.Verifier;
import org.jdom.output.XMLOutputter;

public class ProcessingInstruction
extends Content {
    private static final String CVS_ID = "@(#) $RCSfile: ProcessingInstruction.java,v $ $Revision: 1.46 $ $Date: 2004/02/27 11:32:57 $ $Name: jdom_1_0 $";
    protected String target;
    protected String rawData;
    protected Map mapData;

    protected ProcessingInstruction() {
    }

    public ProcessingInstruction(String target, String data) {
        this.setTarget(target);
        this.setData(data);
    }

    public ProcessingInstruction(String target, Map data) {
        this.setTarget(target);
        this.setData(data);
    }

    public Object clone() {
        ProcessingInstruction pi = (ProcessingInstruction)super.clone();
        if (this.mapData != null) {
            pi.mapData = this.parseData(this.rawData);
        }
        return pi;
    }

    private static int[] extractQuotedString(String rawData) {
        boolean inQuotes = false;
        char quoteChar = '\"';
        int start = 0;
        int pos = 0;
        while (pos < rawData.length()) {
            char currentChar = rawData.charAt(pos);
            if (currentChar == '\"' || currentChar == '\'') {
                if (!inQuotes) {
                    quoteChar = currentChar;
                    inQuotes = true;
                    start = pos + 1;
                } else if (quoteChar == currentChar) {
                    inQuotes = false;
                    return new int[]{start, pos};
                }
            }
            ++pos;
        }
        return null;
    }

    public String getData() {
        return this.rawData;
    }

    public List getPseudoAttributeNames() {
        Set mapDataSet = this.mapData.entrySet();
        ArrayList<String> nameList = new ArrayList<String>();
        Iterator i = mapDataSet.iterator();
        while (i.hasNext()) {
            String wholeSet = i.next().toString();
            String attrName = wholeSet.substring(0, wholeSet.indexOf("="));
            nameList.add(attrName);
        }
        return nameList;
    }

    public String getPseudoAttributeValue(String name) {
        return (String)this.mapData.get(name);
    }

    public String getTarget() {
        return this.target;
    }

    public String getValue() {
        return this.rawData;
    }

    private Map parseData(String rawData) {
        HashMap<String, String> data = new HashMap<String, String>();
        String inputData = rawData.trim();
        while (!inputData.trim().equals("")) {
            String name = "";
            String value = "";
            int startName = 0;
            char previousChar = inputData.charAt(startName);
            int pos = 1;
            while (pos < inputData.length()) {
                char currentChar = inputData.charAt(pos);
                if (currentChar == '=') {
                    name = inputData.substring(startName, pos).trim();
                    int[] bounds = ProcessingInstruction.extractQuotedString(inputData.substring(pos + 1));
                    if (bounds == null) {
                        return new HashMap();
                    }
                    value = inputData.substring(bounds[0] + pos + 1, bounds[1] + pos + 1);
                    pos += bounds[1] + 1;
                    break;
                }
                if (Character.isWhitespace(previousChar) && !Character.isWhitespace(currentChar)) {
                    startName = pos;
                }
                previousChar = currentChar;
                ++pos;
            }
            inputData = inputData.substring(pos);
            if (name.length() <= 0 || value == null) continue;
            data.put(name, value);
        }
        return data;
    }

    public boolean removePseudoAttribute(String name) {
        if (this.mapData.remove(name) != null) {
            this.rawData = this.toString(this.mapData);
            return true;
        }
        return false;
    }

    public ProcessingInstruction setData(String data) {
        String reason = Verifier.checkProcessingInstructionData(data);
        if (reason != null) {
            throw new IllegalDataException(data, reason);
        }
        this.rawData = data;
        this.mapData = this.parseData(data);
        return this;
    }

    public ProcessingInstruction setData(Map data) {
        String temp = this.toString(data);
        String reason = Verifier.checkProcessingInstructionData(temp);
        if (reason != null) {
            throw new IllegalDataException(temp, reason);
        }
        this.rawData = temp;
        this.mapData = data;
        return this;
    }

    public ProcessingInstruction setPseudoAttribute(String name, String value) {
        String reason = Verifier.checkProcessingInstructionData(name);
        if (reason != null) {
            throw new IllegalDataException(name, reason);
        }
        reason = Verifier.checkProcessingInstructionData(value);
        if (reason != null) {
            throw new IllegalDataException(value, reason);
        }
        this.mapData.put(name, value);
        this.rawData = this.toString(this.mapData);
        return this;
    }

    public ProcessingInstruction setTarget(String newTarget) {
        String reason = Verifier.checkProcessingInstructionTarget(newTarget);
        if (reason != null) {
            throw new IllegalTargetException(newTarget, reason);
        }
        this.target = newTarget;
        return this;
    }

    public String toString() {
        return "[ProcessingInstruction: " + new XMLOutputter().outputString(this) + "]";
    }

    private String toString(Map mapData) {
        StringBuffer rawData = new StringBuffer();
        Iterator i = mapData.keySet().iterator();
        while (i.hasNext()) {
            String name = (String)i.next();
            String value = (String)mapData.get(name);
            rawData.append(name).append("=\"").append(value).append("\" ");
        }
        if (rawData.length() > 0) {
            rawData.setLength(rawData.length() - 1);
        }
        return rawData.toString();
    }
}

