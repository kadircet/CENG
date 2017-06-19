/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.grader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import tr.edu.metu.ceng.ceng232.grader.Run;
import tr.edu.metu.ceng.ceng232.grader.State;

public class Settings {
    private static boolean bEnabled = false;
    protected static List<Run> runs;
    protected static String[] inputs;
    protected static String[] outputs;
    protected static String[] allowedChips;
    private static String[] possibleChips;
    protected static Map<String, Integer> components;

    public static boolean isEnabled() {
        return bEnabled;
    }

    public static void loadGradingFile(File file) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(file));
        }
        catch (Exception e) {
            System.err.println("Cannot load grading file: " + e);
            return;
        }
        try {
            Settings.loadFromProperties(prop);
        }
        catch (Exception e) {
            bEnabled = false;
            System.err.println("Error in grading file: " + e);
            System.exit(1);
            return;
        }
    }

    protected static void loadFromProperties(Properties prop) throws Exception {
        int numRuns;
        bEnabled = true;
        runs = new Vector<Run>();
        String sInputs = prop.getProperty("inputs");
        if (sInputs == null) {
            throw new Exception("key \"inputs\" is null");
        }
        inputs = sInputs.trim().split(",");
        String sOutputs = prop.getProperty("outputs");
        if (sOutputs == null) {
            throw new Exception("key \"outputs\" is null");
        }
        outputs = sOutputs.trim().split(",");
        String sNumRuns = prop.getProperty("number_of_runs");
        if (sNumRuns == null) {
            throw new Exception("key \"number_of_runs\" is null");
        }
        try {
            numRuns = Integer.parseInt(sNumRuns);
        }
        catch (NumberFormatException e) {
            throw new Exception("key \"number_of_runs\" is not an integer");
        }
        for (int i = 1; i <= numRuns; ++i) {
            Settings.loadRun(prop, i);
        }
        String sAllowedChips = prop.getProperty("allowed_chips");
        if (sAllowedChips == null) {
            throw new Exception("key \"allowed_chips\" is null");
        }
        allowedChips = sAllowedChips.trim().split(",");
        for (int i2 = 0; i2 < allowedChips.length; ++i2) {
            int j;
            Settings.allowedChips[i2] = allowedChips[i2].trim();
            for (j = 0; j < possibleChips.length && !allowedChips[i2].equals(possibleChips[j]); ++j) {
            }
            if (j != possibleChips.length) continue;
            throw new Exception("\"" + allowedChips[i2] + "\" is not within list of possible chips: " + new Vector<String>(Arrays.asList(possibleChips)));
        }
    }

    protected static void loadRun(Properties prop, int whichRun) throws Exception {
        String prefix = "run." + whichRun + ".";
        String sLength = prop.getProperty(prefix + "length");
        if (sLength == null) {
            throw new Exception("key \"" + prefix + "length\" is null");
        }
        int length = 0;
        try {
            length = Integer.parseInt(sLength);
        }
        catch (NumberFormatException e) {
            throw new Exception("key \"" + prefix + "length\" is not an integer");
        }
        Run r = new Run();
        for (int i = 1; i <= length; ++i) {
            String sState = prop.getProperty(prefix + "state." + i);
            if (sState == null) {
                throw new Exception("key \"" + prefix + "state." + i + "\" is null");
            }
            State state = new State(sState);
            r.states.add(state);
            if (state.outputs.length != outputs.length) {
                throw new Exception("number of outputs in \"" + prefix + "state." + i + "\" do not match previously defined outputs");
            }
            if (state.type != State.TYPE.TRUTH_TABLE || state.inputs.length == inputs.length) continue;
            throw new Exception("number of inputs in \"" + prefix + "state." + i + "\" do not match previously defined inputs");
        }
        if (prop.getProperty(prefix + "state." + (length + 1)) != null) {
            throw new Exception("\"" + prefix + "length\" is " + length + " but key \"" + prefix + "state." + (length + 1) + "\" exists?");
        }
        runs.add(r);
    }

    public static void useComponent(String name) {
        Integer a = components.get(name);
        if (a == null) {
            a = 0;
        }
        Integer n = a;
        Integer n2 = a = Integer.valueOf(a + 1);
        components.put(name, a);
    }

    static {
        possibleChips = new String[]{"Base:Text", "Base:Splitter", "Base:Clock", "Base:Pin", "Base:Probe", "CENG232 Gates:AND Gate", "CENG232 Gates:OR Gate", "CENG232 Gates:NOT Gate", "CENG232 Gates:NOR Gate", "CENG232 Gates:Constant", "CENG232 Gates:Controlled Buffer", "CENG232 Gates:Buffer", "CENG232 Gates:Controlled Inverter", "CENG232 Gates:XOR Gate", "CENG232 Gates:NAND Gate", "CENG232 Gates:XNOR Gate", "CENG232 ICs:4-bit Latch (7475)", "CENG232 ICs:4 bit full adder (7483)", "CENG232 ICs:Dual J-K Flip Flop (74112)", "CENG232 ICs:Dual D Flip Flop (7474)", "CENG232 ICs:3-to-8 decoder (74138)", "CENG232 ICs:4-to-1 MUX (x2) (74153)", "CENG232 ICs:4-bit shift register (74195)", "CENG232 ICs:2-to-4 Decoder (x2) (74155)", "CENG232 ICs:4-bit shift register (7495)"};
        components = new HashMap<String, Integer>();
    }
}

