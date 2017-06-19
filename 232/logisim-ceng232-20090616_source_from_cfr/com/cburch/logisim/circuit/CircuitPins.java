/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.CircuitAttributes;
import com.cburch.logisim.circuit.CircuitPinListener;
import com.cburch.logisim.circuit.Pin;
import com.cburch.logisim.circuit.Subcircuit;
import com.cburch.logisim.comp.ComponentEvent;
import com.cburch.logisim.comp.ComponentListener;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.util.EventSourceWeakSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class CircuitPins {
    private EventSourceWeakSupport listeners = new EventSourceWeakSupport();
    private MyComponentListener myComponentListener;
    private ArrayList pins;
    private Bounds bounds;

    CircuitPins() {
        this.myComponentListener = new MyComponentListener();
        this.pins = new ArrayList();
        this.bounds = null;
    }

    void addPinListener(CircuitPinListener l) {
        this.listeners.add(l);
    }

    void removePinListener(CircuitPinListener l) {
        this.listeners.remove(l);
    }

    void addPin(Pin added) {
        for (PinData pd : this.pins) {
            if (pd.pin != added) continue;
            return;
        }
        this.pins.add(new PinData(added));
        added.getAttributeSet().addAttributeListener(this.myComponentListener);
        added.addComponentListener(this.myComponentListener);
        Collections.sort(this.pins);
        this.bounds = null;
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            CircuitPinListener l = (CircuitPinListener)it.next();
            l.pinAdded();
        }
    }

    void removePin(Pin removed) {
        boolean success = false;
        Iterator it = this.pins.iterator();
        while (it.hasNext()) {
            PinData pd = (PinData)it.next();
            if (pd.pin != removed) continue;
            it.remove();
            success = true;
            break;
        }
        if (success) {
            removed.removeComponentListener(this.myComponentListener);
            this.bounds = null;
            it = this.listeners.iterator();
            while (it.hasNext()) {
                CircuitPinListener l = (CircuitPinListener)it.next();
                l.pinRemoved();
            }
        } else {
            throw new NoSuchElementException();
        }
        removed.getAttributeSet().addAttributeListener(this.myComponentListener);
    }

    Pin getSubcircuitPin(int which) {
        PinData pd;
        if (this.bounds == null) {
            this.recomputeBounds();
        }
        if ((pd = (PinData)this.pins.get(which)) == null) {
            throw new NoSuchElementException("null element");
        }
        return pd.pin;
    }

    Bounds getOffsetBounds(CircuitAttributes attrs) {
        Direction facing;
        if (this.bounds == null) {
            this.recomputeBounds();
        }
        if ((facing = attrs.getFacing()) == Direction.EAST) {
            return this.bounds;
        }
        if (facing == Direction.WEST) {
            int dx;
            int n = dx = this.bounds.getX() == 0 ? - this.bounds.getWidth() : 0;
            if (this.bounds.getY() == 0 || this.bounds.getY() == - this.bounds.getHeight()) {
                dx = - this.bounds.getX() + this.bounds.getWidth();
            }
            return Bounds.create(dx, - this.bounds.getY() + this.bounds.getHeight(), this.bounds.getWidth(), this.bounds.getHeight());
        }
        if (facing == Direction.SOUTH) {
            return Bounds.create(- this.bounds.getY() + this.bounds.getHeight(), this.bounds.getX(), this.bounds.getHeight(), this.bounds.getWidth());
        }
        if (facing == Direction.NORTH) {
            int dx;
            int n = dx = this.bounds.getX() == 0 ? - this.bounds.getWidth() : 0;
            if (this.bounds.getY() == 0 || this.bounds.getY() == - this.bounds.getHeight()) {
                dx = - this.bounds.getX() + this.bounds.getWidth();
            }
            return Bounds.create(this.bounds.getY(), dx, this.bounds.getHeight(), this.bounds.getWidth());
        }
        return this.bounds;
    }

    List getPins() {
        ArrayList<Pin> ret = new ArrayList<Pin>();
        for (PinData pd : this.pins) {
            ret.add(pd.pin);
        }
        return ret;
    }

    void configureComponent(Subcircuit comp) {
        if (this.bounds == null) {
            this.recomputeBounds();
        }
        CircuitAttributes attrs = (CircuitAttributes)comp.getAttributeSet();
        Direction facing = attrs.getFacing();
        Location loc = comp.getLocation();
        comp.clearManager();
        comp.setBounds(this.getOffsetBounds(attrs).translate(loc.getX(), loc.getY()));
        Location base = loc.translate(facing, this.bounds.getX(), this.bounds.getY());
        int i = 0;
        for (PinData pd : this.pins) {
            int type = pd.pin.getType();
            comp.setEnd(i, base.translate(facing, pd.offset.getX(), pd.offset.getY()), pd.pin.getWidth(), type);
            ++i;
        }
    }

    private void recomputeBounds() {
        int y;
        int x;
        int[] n = new int[]{0, 0, 0, 0};
        int east = Direction.EAST.hashCode();
        int west = Direction.WEST.hashCode();
        int north = Direction.NORTH.hashCode();
        int south = Direction.SOUTH.hashCode();
        for (PinData pd : this.pins) {
            int di = pd.pin.getDirection().hashCode();
            int[] arrn = n;
            int n2 = di;
            arrn[n2] = arrn[n2] + 1;
        }
        int[] start = new int[]{0, 0, 0, 0};
        int ht = this.computeAxis(start, n, east, west);
        int wid = this.computeAxis(start, n, north, south);
        if (n[west] > 0) {
            x = wid;
            y = start[west];
        } else if (n[south] > 0) {
            x = start[south];
            y = 0;
        } else if (n[east] > 0) {
            x = 0;
            y = start[east];
        } else if (n[north] > 0) {
            x = start[north];
            y = ht;
        } else {
            x = 0;
            y = 0;
        }
        this.bounds = Bounds.create(- x, - y, wid, ht);
        Arrays.fill(n, 0);
        for (PinData pd2 : this.pins) {
            int di = pd2.pin.getDirection().hashCode();
            if (di == east) {
                pd2.offset = Location.create(0, start[east] + n[di]);
            } else if (di == west) {
                pd2.offset = Location.create(wid, start[west] + n[di]);
            } else if (di == north) {
                pd2.offset = Location.create(start[north] + n[di], ht);
            } else if (di == south) {
                pd2.offset = Location.create(start[south] + n[di], 0);
            }
            int[] arrn = n;
            int n3 = di;
            arrn[n3] = arrn[n3] + 10;
        }
    }

    private int computeAxis(int[] start, int[] n, int i, int j) {
        int dim;
        int maxOffs;
        int others = n[0] + n[1] + n[2] + n[3] - n[i] - n[j];
        int max = Math.max(n[i], n[j]);
        switch (max) {
            case 0: {
                dim = 30;
                maxOffs = others == 0 ? 15 : 10;
                break;
            }
            case 1: {
                dim = 30;
                maxOffs = others == 0 ? 15 : 10;
                break;
            }
            case 2: {
                dim = 30;
                maxOffs = 10;
                break;
            }
            default: {
                if (others == 0) {
                    dim = 10 * max;
                    maxOffs = 5;
                    break;
                }
                dim = 10 * max + 10;
                maxOffs = 10;
            }
        }
        start[i] = maxOffs + 10 * ((max - n[i]) / 2);
        start[j] = maxOffs + 10 * ((max - n[j]) / 2);
        return dim;
    }

    private class MyComponentListener
    implements ComponentListener,
    AttributeListener {
        private MyComponentListener() {
        }

        @Override
        public void endChanged(ComponentEvent e) {
            Iterator it = CircuitPins.this.listeners.iterator();
            while (it.hasNext()) {
                CircuitPinListener l = (CircuitPinListener)it.next();
                l.pinChanged();
            }
        }

        @Override
        public void componentInvalidated(ComponentEvent e) {
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            Attribute attr = e.getAttribute();
            if (attr == Pin.facing_attr) {
                CircuitPins.this.bounds = null;
                Iterator it = CircuitPins.this.listeners.iterator();
                while (it.hasNext()) {
                    CircuitPinListener l = (CircuitPinListener)it.next();
                    l.pinChanged();
                }
            }
        }
    }

    private static class PinData
    implements Comparable {
        Pin pin;
        Location offset = null;

        PinData(Pin p) {
            this.pin = p;
        }

        public int compareTo(Object other_raw) {
            int py0;
            int px1;
            int py1;
            int px0;
            PinData other = (PinData)other_raw;
            Direction d0 = this.pin.getDirection();
            Location p0 = this.pin.getLocation();
            if (d0 == Direction.EAST || d0 == Direction.WEST) {
                px0 = p0.getX();
                py0 = p0.getY();
            } else {
                py0 = p0.getX();
                px0 = p0.getY();
            }
            Location p1 = other.pin.getLocation();
            Direction d1 = other.pin.getDirection();
            if (d1 == Direction.EAST || d1 == Direction.WEST) {
                px1 = p1.getX();
                py1 = p1.getY();
            } else {
                py1 = p1.getX();
                px1 = p1.getY();
            }
            if (py0 != py1) {
                return py0 - py1;
            }
            return px0 - px1;
        }
    }

}

