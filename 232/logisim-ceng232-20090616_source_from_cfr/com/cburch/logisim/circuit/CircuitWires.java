/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.CircuitPoints;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Splitter;
import com.cburch.logisim.circuit.SplitterAttributes;
import com.cburch.logisim.circuit.WidthIncompatibilityData;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.circuit.WireBundle;
import com.cburch.logisim.circuit.WireThread;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.EndData;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.IteratorUtil;
import com.cburch.logisim.util.SmallSet;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class CircuitWires {
    private HashSet wires = new HashSet();
    private HashSet splitters = new HashSet();
    final CircuitPoints points = new CircuitPoints();
    private Bounds bounds = Bounds.EMPTY_BOUNDS;
    private BundleMap bundleMap = null;

    CircuitWires() {
    }

    Set getWidthIncompatibilityData() {
        return this.getBundleMap().getWidthIncompatibilityData();
    }

    void ensureComputed() {
        this.getBundleMap();
    }

    BitWidth getWidth(Location q) {
        BitWidth det = this.points.getWidth(q);
        if (det != BitWidth.UNKNOWN) {
            return det;
        }
        BundleMap bmap = this.getBundleMap();
        if (!bmap.isValid()) {
            return BitWidth.UNKNOWN;
        }
        WireBundle qb = bmap.getBundleAt(q);
        if (qb != null && qb.isValid()) {
            return qb.getWidth();
        }
        return BitWidth.UNKNOWN;
    }

    Location getWidthDeterminant(Location q) {
        BitWidth det = this.points.getWidth(q);
        if (det != BitWidth.UNKNOWN) {
            return q;
        }
        WireBundle qb = this.getBundleMap().getBundleAt(q);
        if (qb != null && qb.isValid()) {
            return qb.getWidthDeterminant();
        }
        return q;
    }

    Iterator getComponents() {
        return IteratorUtil.createJoinedIterator(this.splitters.iterator(), this.wires.iterator());
    }

    Set getWires() {
        return this.wires;
    }

    Bounds getWireBounds() {
        if (this.bounds == Bounds.EMPTY_BOUNDS) {
            this.recomputeBounds();
        }
        return this.bounds;
    }

    WireBundle getWireBundle(Location query) {
        BundleMap bmap = this.getBundleMap();
        return bmap.getBundleAt(query);
    }

    void add(Component comp) {
        if (comp instanceof Wire) {
            this.addWire((Wire)comp);
        } else if (comp instanceof Splitter) {
            this.splitters.add(comp);
        }
        this.points.add(comp);
        this.voidBundleMap();
    }

    void remove(Component comp) {
        if (comp instanceof Wire) {
            this.removeWire((Wire)comp);
        } else if (comp instanceof Splitter) {
            this.splitters.remove(comp);
        }
        this.points.remove(comp);
        this.voidBundleMap();
    }

    void add(Component comp, EndData end) {
        this.points.add(comp, end);
        this.voidBundleMap();
    }

    void remove(Component comp, EndData end) {
        this.points.remove(comp, end);
        this.voidBundleMap();
    }

    private void addWire(Wire w) {
        this.wires.add(w);
        if (this.bounds != Bounds.EMPTY_BOUNDS) {
            this.bounds = this.bounds.add(w.e0).add(w.e1);
        }
    }

    private void removeWire(Wire w) {
        Bounds smaller;
        boolean removed = this.wires.remove(w);
        if (!removed) {
            return;
        }
        if (!(this.bounds == Bounds.EMPTY_BOUNDS || (smaller = this.bounds.expand(-2)).contains(w.e0) && smaller.contains(w.e1))) {
            this.bounds = Bounds.EMPTY_BOUNDS;
        }
    }

    void propagate(CircuitState circState, Set points) {
        BundleMap map = this.getBundleMap();
        SmallSet dirtyThreads = new SmallSet();
        State s = circState.getWireData();
        if (s == null || s.bundleMap != map) {
            s = new State(map);
            for (WireBundle b : map.getBundles()) {
                WireThread[] th = b.threads;
                if (!b.isValid() || th == null) continue;
                for (int i = 0; i < th.length; ++i) {
                    dirtyThreads.add(th[i]);
                }
            }
            circState.setWireData(s);
        }
        for (Location p : points) {
            WireBundle pb = map.getBundleAt(p);
            if (pb == null) {
                circState.setValueByWire(p, circState.getComponentOutputAt(p));
                continue;
            }
            WireThread[] th = pb.threads;
            if (!pb.isValid() || th == null) {
                SmallSet pbPoints = pb.points;
                if (pbPoints == null) {
                    circState.setValueByWire(p, Value.NIL);
                    continue;
                }
                Iterator it2 = pbPoints.iterator();
                while (it2.hasNext()) {
                    circState.setValueByWire((Location)it2.next(), Value.NIL);
                }
                continue;
            }
            for (int i = 0; i < th.length; ++i) {
                dirtyThreads.add(th[i]);
            }
        }
        if (dirtyThreads.isEmpty()) {
            return;
        }
        HashSet bundles = new HashSet();
        for (WireThread t : dirtyThreads) {
            Value v = this.getThreadValue(circState, t);
            s.thr_values.put(t, v);
            bundles.addAll(t.getBundles());
        }
        for (ThreadBundle tb : bundles) {
            WireBundle b = tb.b;
            Value bv = null;
            if (b.isValid() && b.threads != null) {
                if (b.threads.length == 1) {
                    bv = (Value)s.thr_values.get(b.threads[0]);
                } else {
                    Value[] tvs = new Value[b.threads.length];
                    boolean tvs_valid = true;
                    for (int i = 0; i < tvs.length; ++i) {
                        Value tv = (Value)s.thr_values.get(b.threads[i]);
                        if (tv == null) {
                            tvs_valid = false;
                            break;
                        }
                        tvs[i] = tv;
                    }
                    if (tvs_valid) {
                        bv = Value.create(tvs);
                    }
                }
            }
            if (bv == null) continue;
            for (Location p2 : b.points) {
                circState.setValueByWire(p2, bv);
            }
        }
    }

    void draw(ComponentDrawContext context, Collection hidden) {
        boolean showState = context.getShowState();
        CircuitState state = context.getCircuitState();
        Graphics g = context.getGraphics();
        g.setColor(Color.BLACK);
        GraphicsUtil.switchToWidth(g, 3);
        BundleMap bmap = this.getBundleMap();
        boolean isValid = bmap.isValid();
        if (hidden == null || hidden.size() == 0) {
            for (Wire w : this.wires) {
                Location s = w.e0;
                Location t = w.e1;
                WireBundle wb = bmap.getBundleAt(s);
                if (!wb.isValid()) {
                    g.setColor(Value.WIDTH_ERROR_COLOR);
                } else if (showState) {
                    if (!isValid) {
                        g.setColor(Value.NIL_COLOR);
                    } else {
                        g.setColor(state.getValue(s).getColor());
                    }
                } else {
                    g.setColor(Color.BLACK);
                }
                g.drawLine(s.getX(), s.getY(), t.getX(), t.getY());
            }
            for (Location loc : this.points.getSplitLocations()) {
                WireBundle wb;
                if (this.points.getComponentCount(loc) <= 2 || (wb = bmap.getBundleAt(loc)) == null) continue;
                if (!wb.isValid()) {
                    g.setColor(Value.WIDTH_ERROR_COLOR);
                } else if (showState) {
                    if (!isValid) {
                        g.setColor(Value.NIL_COLOR);
                    } else {
                        g.setColor(state.getValue(loc).getColor());
                    }
                } else {
                    g.setColor(Color.BLACK);
                }
                g.fillOval(loc.getX() - 4, loc.getY() - 4, 8, 8);
            }
        } else {
            for (Wire w : this.wires) {
                if (hidden.contains(w)) continue;
                Location s = w.e0;
                Location t = w.e1;
                WireBundle wb = bmap.getBundleAt(s);
                if (!wb.isValid()) {
                    g.setColor(Value.WIDTH_ERROR_COLOR);
                } else if (showState) {
                    if (!isValid) {
                        g.setColor(Value.NIL_COLOR);
                    } else {
                        g.setColor(state.getValue(s).getColor());
                    }
                } else {
                    g.setColor(Color.BLACK);
                }
                g.drawLine(s.getX(), s.getY(), t.getX(), t.getY());
            }
            for (Location loc : this.points.getSplitLocations()) {
                WireBundle wb;
                if (this.points.getComponentCount(loc) <= 2) continue;
                int icount = 0;
                Iterator it2 = this.points.getComponents(loc).iterator();
                while (it2.hasNext()) {
                    if (hidden.contains(it2.next())) continue;
                    ++icount;
                }
                if (icount <= 2 || (wb = bmap.getBundleAt(loc)) == null) continue;
                if (!wb.isValid()) {
                    g.setColor(Value.WIDTH_ERROR_COLOR);
                } else if (showState) {
                    if (!isValid) {
                        g.setColor(Value.NIL_COLOR);
                    } else {
                        g.setColor(state.getValue(loc).getColor());
                    }
                } else {
                    g.setColor(Color.BLACK);
                }
                g.fillOval(loc.getX() - 4, loc.getY() - 4, 8, 8);
            }
        }
    }

    private void voidBundleMap() {
        this.bundleMap = null;
    }

    private BundleMap getBundleMap() {
        BundleMap ret = this.bundleMap;
        if (ret != null) {
            ret.waitUntilComputed();
            return ret;
        }
        try {
            this.bundleMap = ret = new BundleMap();
            this.computeBundleMap(ret);
        }
        catch (RuntimeException ex) {
            ret.invalidate();
            throw ex;
        }
        finally {
            ret.markComputed();
        }
        return ret;
    }

    private void computeBundleMap(BundleMap ret) {
        WireBundle pb;
        Location p;
        EndData end;
        Object ends;
        int index;
        for (Wire w : this.wires) {
            WireBundle b1;
            WireBundle b0 = ret.getBundleAt(w.e0);
            if (b0 == null) {
                b1 = ret.createBundleAt(w.e1);
                b1.points.add(w.e0);
                ret.setBundleAt(w.e0, b1);
                continue;
            }
            b1 = ret.getBundleAt(w.e1);
            if (b1 == null) {
                b0.points.add(w.e1);
                ret.setBundleAt(w.e1, b0);
                continue;
            }
            b1.unite(b0);
        }
        Iterator it = ret.getBundles().iterator();
        while (it.hasNext()) {
            WireBundle b = (WireBundle)it.next();
            WireBundle bpar = b.find();
            if (bpar == b) continue;
            for (Location pt : b.points) {
                ret.setBundleAt(pt, bpar);
                bpar.points.add(pt);
            }
            it.remove();
        }
        for (Splitter spl22 : this.splitters) {
            ends = spl22.getEnds();
            int num_ends = ends.size();
            for (index = 0; index < num_ends; ++index) {
                end = (EndData)ends.get(index);
                p = end.getLocation();
                pb = ret.createBundleAt(p);
                pb.setWidth(end.getWidth(), p);
            }
        }
        for (Location p2 : ret.getBundlePoints()) {
            WireBundle pb2 = ret.getBundleAt(p2);
            BitWidth width = this.points.getWidth(p2);
            if (width == BitWidth.UNKNOWN) continue;
            pb2.setWidth(width, p2);
        }
        for (Splitter spl22 : this.splitters) {
            ends = spl22.getEnds();
            int num_ends = ends.size();
            for (index = 0; index < num_ends; ++index) {
                end = (EndData)ends.get(index);
                p = end.getLocation();
                pb = ret.getBundleAt(p);
                pb.setWidth(end.getWidth(), p);
                spl22.wire_data.end_bundle[index] = pb;
            }
        }
        it = this.splitters.iterator();
        while (it.hasNext()) {
            Splitter spl22;
            ends = spl22 = (Splitter)it.next();
            synchronized (ends) {
                SplitterAttributes spl_attrs = (SplitterAttributes)spl22.getAttributeSet();
                byte[] bit_end = spl_attrs.bit_end;
                SplitterData spl_data = spl22.wire_data;
                WireBundle from_bundle = spl_data.end_bundle[0];
                if (from_bundle == null || !from_bundle.isValid()) {
                    continue;
                }
                for (int i = 0; i < bit_end.length; ++i) {
                    byte j = bit_end[i];
                    if (j <= 0) continue;
                    byte thr = spl22.bit_thread[i];
                    WireBundle to_bundle = spl_data.end_bundle[j];
                    if (!to_bundle.isValid()) continue;
                    if (i >= from_bundle.threads.length) {
                        throw new ArrayIndexOutOfBoundsException("from " + i + " of " + from_bundle.threads.length);
                    }
                    if (thr >= to_bundle.threads.length) {
                        throw new ArrayIndexOutOfBoundsException("to " + thr + " of " + to_bundle.threads.length);
                    }
                    from_bundle.threads[i].unite(to_bundle.threads[thr]);
                }
                continue;
            }
        }
        for (WireBundle b : ret.getBundles()) {
            if (!b.isValid() || b.threads == null) continue;
            for (int i = 0; i < b.threads.length; ++i) {
                WireThread thr;
                b.threads[i] = thr = b.threads[i].find();
                thr.getBundles().add(new ThreadBundle(i, b));
            }
        }
        Collection exceptions = this.points.getWidthIncompatibilityData();
        if (exceptions != null && exceptions.size() > 0) {
            Iterator it2 = exceptions.iterator();
            while (it2.hasNext()) {
                ret.addWidthIncompatibilityData((WidthIncompatibilityData)it2.next());
            }
        }
        for (WireBundle b2 : ret.getBundles()) {
            WidthIncompatibilityData e = b2.getWidthIncompatibilityData();
            if (e == null) continue;
            ret.addWidthIncompatibilityData(e);
        }
    }

    private Value getThreadValue(CircuitState state, WireThread t) {
        Value ret = Value.UNKNOWN;
        for (ThreadBundle tb : t.getBundles()) {
            for (Location p : tb.b.points) {
                Value val = state.getComponentOutputAt(p);
                if (val == null || val == Value.NIL) continue;
                ret = ret.combine(val.get(tb.loc));
            }
        }
        return ret;
    }

    private void recomputeBounds() {
        Iterator it = this.wires.iterator();
        if (!it.hasNext()) {
            this.bounds = Bounds.EMPTY_BOUNDS;
            return;
        }
        Wire w = (Wire)it.next();
        int xmin = w.e0.getX();
        int ymin = w.e0.getY();
        int xmax = w.e1.getX();
        int ymax = w.e1.getY();
        while (it.hasNext()) {
            int y1;
            int y0;
            int x1;
            w = (Wire)it.next();
            int x0 = w.e0.getX();
            if (x0 < xmin) {
                xmin = x0;
            }
            if ((x1 = w.e1.getX()) > xmax) {
                xmax = x1;
            }
            if ((y0 = w.e0.getY()) < ymin) {
                ymin = y0;
            }
            if ((y1 = w.e1.getY()) <= ymax) continue;
            ymax = y1;
        }
        this.bounds = Bounds.create(xmin, ymin, xmax - xmin + 1, ymax - ymin + 1);
    }

    static class BundleMap {
        boolean computed = false;
        HashMap pointBundles = new HashMap();
        HashSet bundles = new HashSet();
        boolean isValid = true;
        HashSet incompatibilityData = null;

        BundleMap() {
        }

        HashSet getWidthIncompatibilityData() {
            return this.incompatibilityData;
        }

        void addWidthIncompatibilityData(WidthIncompatibilityData e) {
            if (this.incompatibilityData == null) {
                this.incompatibilityData = new HashSet();
            }
            this.incompatibilityData.add(e);
        }

        WireBundle getBundleAt(Location p) {
            return (WireBundle)this.pointBundles.get(p);
        }

        WireBundle createBundleAt(Location p) {
            WireBundle ret = (WireBundle)this.pointBundles.get(p);
            if (ret == null) {
                ret = new WireBundle();
                this.pointBundles.put(p, ret);
                ret.points.add(p);
                this.bundles.add(ret);
            }
            return ret;
        }

        boolean isValid() {
            return this.isValid;
        }

        void invalidate() {
            this.isValid = false;
        }

        void setBundleAt(Location p, WireBundle b) {
            this.pointBundles.put(p, b);
        }

        Set getBundlePoints() {
            return this.pointBundles.keySet();
        }

        Set getBundles() {
            return this.bundles;
        }

        synchronized void markComputed() {
            this.computed = true;
            this.notifyAll();
        }

        synchronized void waitUntilComputed() {
            while (!this.computed) {
                try {
                    this.wait();
                }
                catch (InterruptedException e) {}
            }
        }
    }

    static class State {
        BundleMap bundleMap;
        HashMap thr_values = new HashMap();

        State(BundleMap bundleMap) {
            this.bundleMap = bundleMap;
        }

        public Object clone() {
            State ret = new State(this.bundleMap);
            ret.thr_values.putAll(this.thr_values);
            return ret;
        }
    }

    private static class ThreadBundle {
        int loc;
        WireBundle b;

        ThreadBundle(int loc, WireBundle b) {
            this.loc = loc;
            this.b = b;
        }
    }

    static class SplitterData {
        WireBundle[] end_bundle;

        SplitterData(int fan_out) {
            this.end_bundle = new WireBundle[fan_out + 1];
        }
    }

}

