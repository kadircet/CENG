/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.analyze.model.Entry;
import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.Expressions;
import com.cburch.logisim.analyze.model.OutputExpressions;
import com.cburch.logisim.analyze.model.TruthTable;
import com.cburch.logisim.circuit.AnalyzeException;
import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitPins;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.CircuitWires;
import com.cburch.logisim.circuit.ExpressionComputer;
import com.cburch.logisim.circuit.Pin;
import com.cburch.logisim.circuit.Propagator;
import com.cburch.logisim.circuit.Strings;
import com.cburch.logisim.circuit.WireBundle;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.SmallSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class Analyze {
    private static final int MAX_ITERATIONS = 100;

    private Analyze() {
    }

    public static SortedMap getPinLabels(Circuit circuit) {
        TreeMap<Object, String> ret = new TreeMap<Object, String>(new Comparator(){

            public int compare(Object arg0, Object arg1) {
                Location a = ((Pin)arg0).getLocation();
                Location b = ((Pin)arg1).getLocation();
                if (a.getY() < b.getY()) {
                    return -1;
                }
                if (a.getY() > b.getY()) {
                    return 1;
                }
                if (a.getX() < b.getX()) {
                    return -1;
                }
                if (a.getX() > b.getX()) {
                    return 1;
                }
                return arg0.hashCode() - arg1.hashCode();
            }
        });
        for (Object pin : circuit.pins.getPins()) {
            ret.put(pin, null);
        }
        HashSet<String> labelsTaken = new HashSet<String>();
        for (Pin pin22 : ret.keySet()) {
            String label = Analyze.toValidLabel(pin22.getLabel());
            if (label == null) continue;
            if (labelsTaken.contains(label)) {
                int i = 2;
                while (labelsTaken.contains(label + i)) {
                    ++i;
                }
                label = label + i;
            }
            ret.put(pin22, label);
            labelsTaken.add(label);
        }
        for (Pin pin22 : ret.keySet()) {
            String defaultList;
            int i;
            if (ret.get(pin22) != null) continue;
            if (pin22.isInputPin()) {
                defaultList = Strings.get("defaultInputLabels");
                if (defaultList.indexOf(",") < 0) {
                    defaultList = "a,b,c,d,e,f,g,h";
                }
            } else {
                defaultList = Strings.get("defaultOutputLabels");
                if (defaultList.indexOf(",") < 0) {
                    defaultList = "x,y,z,u,v,w,s,t";
                }
            }
            String[] options = defaultList.split(",");
            String label = null;
            for (i = 0; label == null && i < options.length; ++i) {
                if (labelsTaken.contains(options[i])) continue;
                label = options[i];
            }
            if (label == null) {
                i = 1;
                while (labelsTaken.contains(label = "x" + ++i)) {
                }
            }
            labelsTaken.add(label);
            ret.put(pin22, label);
        }
        return ret;
    }

    private static String toValidLabel(String label) {
        if (label == null) {
            return null;
        }
        StringBuffer end = null;
        StringBuffer ret = new StringBuffer();
        boolean afterWhitespace = false;
        for (int i = 0; i < label.length(); ++i) {
            char c = label.charAt(i);
            if (Character.isJavaIdentifierStart(c)) {
                if (afterWhitespace) {
                    c = Character.toTitleCase(c);
                    afterWhitespace = false;
                }
                ret.append(c);
                continue;
            }
            if (Character.isJavaIdentifierPart(c)) {
                if (ret.length() > 0) {
                    ret.append(c);
                } else {
                    if (end == null) {
                        end = new StringBuffer();
                    }
                    end.append(c);
                }
                afterWhitespace = false;
                continue;
            }
            if (!Character.isWhitespace(c)) continue;
            afterWhitespace = true;
        }
        if (end != null && ret.length() > 0) {
            ret.append(end.toString());
        }
        if (ret.length() == 0) {
            return null;
        }
        return ret.toString();
    }

    public static void computeExpression(AnalyzerModel model, Circuit circuit, Map pinNames) throws AnalyzeException {
        ExpressionMap expressionMap = new ExpressionMap(circuit);
        ArrayList<String> inputNames = new ArrayList<String>();
        ArrayList<String> outputNames = new ArrayList<String>();
        ArrayList<Pin> outputPins = new ArrayList<Pin>();
        for (Pin pin2 : pinNames.keySet()) {
            String label = (String)pinNames.get(pin2);
            if (pin2.isInputPin()) {
                expressionMap.currentCause = pin2;
                Expression e = Expressions.variable(label);
                expressionMap.put(pin2.getLocation(), e);
                inputNames.add(label);
                continue;
            }
            outputPins.add(pin2);
            outputNames.add(label);
        }
        Analyze.propagateComponents(expressionMap, circuit.getNonWires());
        int iterations = 0;
        while (!expressionMap.dirtyPoints.isEmpty()) {
            if (iterations > 100) {
                throw new AnalyzeException.Circular();
            }
            Analyze.propagateWires(expressionMap, new HashSet(expressionMap.dirtyPoints));
            HashSet dirtyComponents = Analyze.getDirtyComponents(circuit, expressionMap.dirtyPoints);
            expressionMap.dirtyPoints.clear();
            Analyze.propagateComponents(expressionMap, dirtyComponents);
            Expression expr = Analyze.checkForCircularExpressions(expressionMap);
            if (expr != null) {
                throw new AnalyzeException.Circular();
            }
            ++iterations;
        }
        model.setVariables(inputNames, outputNames);
        for (int i = 0; i < outputPins.size(); ++i) {
            Pin pin2;
            pin2 = (Pin)outputPins.get(i);
            model.getOutputExpressions().setExpression((String)outputNames.get(i), (Expression)expressionMap.get(pin2.getLocation()));
        }
    }

    private static void propagateWires(ExpressionMap expressionMap, HashSet pointsToProcess) throws AnalyzeException {
        expressionMap.currentCause = null;
        for (Location p : pointsToProcess) {
            Expression e = (Expression)expressionMap.get(p);
            expressionMap.currentCause = (Component)expressionMap.causes.get(p);
            WireBundle bundle = ExpressionMap.access$300((ExpressionMap)expressionMap).wires.getWireBundle(p);
            if (e == null || bundle == null || bundle.points == null) continue;
            for (Location p2 : bundle.points) {
                Component eCause;
                Component oldCause;
                if (p2.equals(p)) continue;
                Expression old = (Expression)expressionMap.get(p2);
                if (old != null && (eCause = expressionMap.currentCause) != (oldCause = (Component)expressionMap.causes.get(p2)) && !old.equals(e)) {
                    throw new AnalyzeException.Conflict();
                }
                expressionMap.put(p2, e);
            }
        }
    }

    private static HashSet getDirtyComponents(Circuit circuit, HashSet pointsToProcess) throws AnalyzeException {
        HashSet<Component> dirtyComponents = new HashSet<Component>();
        for (Location point : pointsToProcess) {
            for (Component comp : circuit.getNonWires(point)) {
                dirtyComponents.add(comp);
            }
        }
        return dirtyComponents;
    }

    private static void propagateComponents(ExpressionMap expressionMap, Collection components) throws AnalyzeException {
        for (Component comp : components) {
            ExpressionComputer computer = (ExpressionComputer)comp.getFeature(ExpressionComputer.class);
            if (computer != null) {
                try {
                    expressionMap.currentCause = comp;
                    computer.computeExpression(expressionMap);
                    continue;
                }
                catch (UnsupportedOperationException e) {
                    throw new AnalyzeException.CannotHandle(comp.getFactory().getDisplayName());
                }
            }
            if (comp instanceof Pin) continue;
            throw new AnalyzeException.CannotHandle(comp.getFactory().getDisplayName());
        }
    }

    private static Expression checkForCircularExpressions(ExpressionMap expressionMap) throws AnalyzeException {
        for (Location point : expressionMap.dirtyPoints) {
            Expression expr = (Expression)expressionMap.get(point);
            if (!expr.isCircular()) continue;
            return expr;
        }
        return null;
    }

    public static void computeTable(AnalyzerModel model, Project proj, Circuit circuit, Map pinLabels) {
        int i;
        ArrayList<Pin> inputPins = new ArrayList<Pin>();
        ArrayList inputNames = new ArrayList();
        ArrayList<Pin> outputPins = new ArrayList<Pin>();
        ArrayList outputNames = new ArrayList();
        for (Pin pin : pinLabels.keySet()) {
            if (pin.isInputPin()) {
                inputPins.add(pin);
                inputNames.add(pinLabels.get(pin));
                continue;
            }
            outputPins.add(pin);
            outputNames.add(pinLabels.get(pin));
        }
        int inputCount = inputPins.size();
        int rowCount = 1 << inputCount;
        Entry[][] columns = new Entry[outputPins.size()][rowCount];
        for (i = 0; i < rowCount; ++i) {
            int j;
            CircuitState circuitState = new CircuitState(proj, circuit);
            for (int j2 = 0; j2 < inputCount; ++j2) {
                Pin pin2 = (Pin)inputPins.get(j2);
                boolean value = TruthTable.isInputSet(i, j2, inputCount);
                pin2.setValue(circuitState, value ? Value.TRUE : Value.FALSE);
            }
            Propagator prop = circuitState.getPropagator();
            prop.propagate();
            if (prop.isOscillating()) {
                for (j = 0; j < columns.length; ++j) {
                    columns[j][i] = Entry.OSCILLATE_ERROR;
                }
                continue;
            }
            for (j = 0; j < columns.length; ++j) {
                Pin pin3 = (Pin)outputPins.get(j);
                Value outValue = pin3.getValue(circuitState).get(0);
                Entry out = outValue == Value.TRUE ? Entry.ONE : (outValue == Value.FALSE ? Entry.ZERO : (outValue == Value.ERROR ? Entry.BUS_ERROR : Entry.DONT_CARE));
                columns[j][i] = out;
            }
        }
        model.setVariables(inputNames, outputNames);
        for (i = 0; i < columns.length; ++i) {
            model.getTruthTable().setOutputColumn(i, columns[i]);
        }
    }

    private static class ExpressionMap
    extends HashMap {
        private Circuit circuit;
        private HashSet dirtyPoints = new HashSet();
        private HashMap causes = new HashMap();
        private Component currentCause = null;

        ExpressionMap(Circuit circuit) {
            this.circuit = circuit;
        }

        @Override
        public Object put(Object point, Object expression) {
            Object ret = super.put(point, expression);
            if (this.currentCause != null) {
                this.causes.put(point, this.currentCause);
            }
            if (ret == null ? expression != null : !ret.equals(expression)) {
                this.dirtyPoints.add(point);
            }
            return ret;
        }

        static /* synthetic */ Circuit access$300(ExpressionMap x0) {
            return x0.circuit;
        }
    }

}

