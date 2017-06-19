/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.grader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class State {
    protected char[] inputs;
    protected char[] outputs;
    protected char[] stateFrom;
    protected char[] stateTo;
    protected int gotoState;
    protected int gotoLimit;
    protected TYPE type;

    protected State(String loadFrom) throws Exception {
        String str = loadFrom.trim();
        Pattern p1 = Pattern.compile("^(([01],)*[01]);(([01X],)*[01X])$");
        Pattern p2 = Pattern.compile("^if +(([01X],)*[01X]) +then +goto +([0-9]+) +at +most +([0-9]+) +times$");
        Pattern p3 = Pattern.compile("^modify +74x95 +state +from +([01],[01],[01],[01]) +to +([01],[01],[01],[01])$");
        Matcher m1 = p1.matcher(str);
        Matcher m2 = p2.matcher(str);
        Matcher m3 = p3.matcher(str);
        if (m1.matches()) {
            int i;
            String[] sInputs = m1.group(1).split(",");
            String[] sOutputs = m1.group(3).split(",");
            this.inputs = new char[sInputs.length];
            for (i = 0; i < sInputs.length; ++i) {
                this.inputs[i] = sInputs[i].charAt(0);
            }
            this.outputs = new char[sOutputs.length];
            for (i = 0; i < sOutputs.length; ++i) {
                this.outputs[i] = sOutputs[i].charAt(0);
            }
            this.type = TYPE.TRUTH_TABLE;
        } else if (m2.matches()) {
            String[] sOutputs = m2.group(1).split(",");
            String sNewState = m2.group(3);
            String sAtMost = m2.group(4);
            this.outputs = new char[sOutputs.length];
            for (int i = 0; i < sOutputs.length; ++i) {
                this.outputs[i] = sOutputs[i].charAt(0);
            }
            try {
                this.gotoState = Integer.parseInt(sNewState);
                this.gotoLimit = Integer.parseInt(sAtMost);
            }
            catch (NumberFormatException e) {
                // empty catch block
            }
            this.type = TYPE.CONDITION;
        } else if (m3.matches()) {
            int i;
            String[] sFrom = m3.group(1).split(",");
            String[] sTo = m3.group(2).split(",");
            this.stateFrom = new char[sFrom.length];
            for (i = 0; i < sFrom.length; ++i) {
                this.stateFrom[i] = sFrom[i].charAt(0);
            }
            this.stateTo = new char[sTo.length];
            for (i = 0; i < sTo.length; ++i) {
                this.stateTo[i] = sTo[i].charAt(0);
            }
            this.type = TYPE.REGISTER_MODIFY;
        } else {
            throw new Exception("Invalid state string: " + str);
        }
    }

    public static enum TYPE {
        TRUTH_TABLE,
        CONDITION,
        REGISTER_MODIFY;
        

        private TYPE() {
        }
    }

}

