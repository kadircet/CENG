/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.analyze.model.Entry;
import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.Expressions;
import com.cburch.logisim.analyze.model.TruthTable;
import com.cburch.logisim.analyze.model.VariableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Implicant
implements Comparable {
    static Implicant MINIMAL_IMPLICANT = new Implicant(0, -1);
    static List MINIMAL_LIST = Arrays.asList(MINIMAL_IMPLICANT);
    private int unknowns;
    private int values;

    private Implicant(int unknowns, int values) {
        this.unknowns = unknowns;
        this.values = values;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Implicant)) {
            return false;
        }
        Implicant o = (Implicant)other;
        return this.unknowns == o.unknowns && this.values == o.values;
    }

    public int compareTo(Object other) {
        Implicant o = (Implicant)other;
        if (this.values < o.values) {
            return -1;
        }
        if (this.values > o.values) {
            return 1;
        }
        if (this.unknowns < o.unknowns) {
            return -1;
        }
        if (this.unknowns > o.unknowns) {
            return 1;
        }
        return 0;
    }

    public int hashCode() {
        return this.unknowns << 16 | this.values;
    }

    public Iterator getTerms() {
        return new TermIterator(this);
    }

    public int getRow() {
        if (this.unknowns != 0) {
            return -1;
        }
        return this.values;
    }

    private Expression toExpression(TruthTable source) {
        Expression term = null;
        int cols = source.getInputColumnCount();
        for (int i = cols - 1; i >= 0; --i) {
            if ((this.unknowns & 1 << i) != 0) continue;
            Expression literal = Expressions.variable(source.getInputHeader(cols - 1 - i));
            if ((this.values & 1 << i) == 0) {
                literal = Expressions.not(literal);
            }
            term = Expressions.and(term, literal);
        }
        return term == null ? Expressions.constant(1) : term;
    }

    static Expression toExpression(AnalyzerModel model, List implicants) {
        if (implicants == null) {
            return null;
        }
        TruthTable table = model.getTruthTable();
        Expression sum = null;
        for (Implicant imp : implicants) {
            sum = Expressions.or(sum, imp.toExpression(table));
        }
        return sum == null ? Expressions.constant(0) : sum;
    }

    static List computeSum(AnalyzerModel model, String variable) {
        TruthTable table = model.getTruthTable();
        int column = model.getOutputs().indexOf(variable);
        if (column < 0) {
            return Collections.EMPTY_LIST;
        }
        ArrayList<Implicant> ret = new ArrayList<Implicant>();
        for (int i = 0; i < table.getRowCount(); ++i) {
            if (table.getOutputEntry(i, column) != Entry.ONE) continue;
            ret.add(new Implicant(0, i));
        }
        return ret;
    }

    static List computeMinimal(AnalyzerModel model, String variable) {
        TruthTable table = model.getTruthTable();
        int column = model.getOutputs().indexOf(variable);
        if (column < 0) {
            return Collections.EMPTY_LIST;
        }
        HashMap<Implicant, Entry> base = new HashMap<Implicant, Entry>();
        HashSet<Implicant> toCover = new HashSet<Implicant>();
        boolean knownFound = false;
        for (int i = 0; i < table.getRowCount(); ++i) {
            Implicant imp;
            Entry entry = table.getOutputEntry(i, column);
            if (entry == Entry.ZERO) {
                knownFound = true;
                continue;
            }
            if (entry == Entry.ONE) {
                knownFound = true;
                imp = new Implicant(0, i);
                base.put(imp, entry);
                toCover.add(imp);
                continue;
            }
            imp = new Implicant(0, i);
            base.put(imp, entry);
        }
        if (!knownFound) {
            return null;
        }
        HashSet<Implicant> primes = new HashSet<Implicant>();
        HashMap<Implicant, Entry> current = base;
        while (current.size() > 1) {
            HashSet<Implicant> toRemove = new HashSet<Implicant>();
            HashMap<Implicant, Entry> next = new HashMap<Implicant, Entry>();
            for (Implicant imp : current.keySet()) {
                Entry detEntry = (Entry)current.get(imp);
                for (int j = 1; j <= imp.values; j *= 2) {
                    Implicant opp;
                    Entry oppEntry;
                    if ((imp.values & j) == 0 || (oppEntry = (Entry)current.get(opp = new Implicant(imp.unknowns, imp.values ^ j))) == null) continue;
                    toRemove.add(imp);
                    toRemove.add(opp);
                    next.put(new Implicant(opp.unknowns | j, opp.values), oppEntry == Entry.DONT_CARE && detEntry == Entry.DONT_CARE ? Entry.DONT_CARE : Entry.ONE);
                }
            }
            for (Implicant det : current.keySet()) {
                if (toRemove.contains(det) || current.get(det) != Entry.ONE) continue;
                primes.add(det);
            }
            current = next;
        }
        for (Implicant imp : current.keySet()) {
            if (current.get(imp) != Entry.ONE) continue;
            primes.add(imp);
        }
        HashSet<Object> retSet = new HashSet<Object>();
        HashSet covered = new HashSet();
        for (Implicant required : toCover) {
            if (covered.contains(required)) continue;
            int row = required.getRow();
            Implicant essential = null;
            for (Implicant imp2 : primes) {
                if ((row & ~ imp2.unknowns) != imp2.values) continue;
                if (essential == null) {
                    essential = imp2;
                    continue;
                }
                essential = null;
                break;
            }
            if (essential == null) continue;
            retSet.add(essential);
            primes.remove(essential);
            Iterator it2 = essential.getTerms();
            while (it2.hasNext()) {
                covered.add(it2.next());
            }
        }
        toCover.removeAll(covered);
        while (!toCover.isEmpty()) {
            Implicant max = null;
            int maxCount = 0;
            Iterator it = primes.iterator();
            while (it.hasNext()) {
                Implicant imp3 = (Implicant)it.next();
                int count = 0;
                Iterator it2 = imp3.getTerms();
                while (it2.hasNext()) {
                    if (!toCover.contains(it2.next())) continue;
                    ++count;
                }
                if (count == 0) {
                    it.remove();
                    continue;
                }
                if (count <= maxCount) continue;
                max = imp3;
                maxCount = count;
            }
            retSet.add(max);
            primes.remove(max);
            it = max.getTerms();
            while (it.hasNext()) {
                toCover.remove(it.next());
            }
        }
        ArrayList<Object> ret = new ArrayList<Object>();
        ret.addAll(retSet);
        Collections.sort(ret);
        return ret;
    }

    private static class TermIterator
    implements Iterator {
        Implicant source;
        int currentMask = 0;

        TermIterator(Implicant source) {
            this.source = source;
        }

        @Override
        public boolean hasNext() {
            return this.currentMask >= 0;
        }

        public Object next() {
            int ret = this.currentMask | this.source.values;
            int diffs = this.currentMask ^ this.source.unknowns;
            int diff = diffs ^ diffs - 1 & diffs;
            this.currentMask = diff == 0 ? -1 : this.currentMask & ~ (diff - 1) | diff;
            return new Implicant(0, ret);
        }

        @Override
        public void remove() {
        }
    }

}

