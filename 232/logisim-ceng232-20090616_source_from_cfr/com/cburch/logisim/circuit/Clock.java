/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.ClockFactory;
import com.cburch.logisim.circuit.Pin;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentState;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.comp.TextField;
import com.cburch.logisim.comp.TextFieldCaret;
import com.cburch.logisim.comp.TextFieldEvent;
import com.cburch.logisim.comp.TextFieldListener;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.gui.log.Loggable;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.tools.Caret;
import com.cburch.logisim.tools.CaretListener;
import com.cburch.logisim.tools.Pokable;
import com.cburch.logisim.tools.TextEditable;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Clock
extends ManagedComponent
implements Pokable,
TextEditable,
Loggable {
    private MyListener myListener;
    private Direction dir;
    private int durationHigh;
    private int durationLow;
    private TextField field;

    public Clock(Location loc, AttributeSet attrs) {
        super(loc, attrs, 1);
        this.myListener = new MyListener();
        this.durationHigh = 1;
        this.durationLow = 1;
        attrs.addAttributeListener(this.myListener);
        this.dir = (Direction)attrs.getValue(Pin.facing_attr);
        this.durationHigh = (Integer)attrs.getValue(ClockFactory.high_attr);
        this.durationLow = (Integer)attrs.getValue(ClockFactory.low_attr);
        this.setEnd(0, loc, BitWidth.ONE, 2);
        String text = (String)attrs.getValue(Pin.label_attr);
        if (text != null && !text.equals("")) {
            this.createTextField();
        }
    }

    @Override
    public ComponentFactory getFactory() {
        return ClockFactory.instance;
    }

    @Override
    public void propagate(CircuitState state) {
        Location pt = this.getEndLocation(0);
        Value val = state.getValue(pt);
        State q = this.getState(state);
        if (!val.equals(q.sending)) {
            state.setValue(pt, q.sending, this, 1);
        }
    }

    @Override
    public Bounds getBounds(Graphics g) {
        Bounds ret = super.getBounds();
        if (this.field != null) {
            ret = ret.add(this.field.getBounds(g));
        }
        return ret;
    }

    @Override
    public boolean contains(Location pt, Graphics g) {
        return super.contains(pt) || this.field != null && this.field.getBounds(g).contains(pt);
    }

    boolean tick(CircuitState circState, int ticks) {
        Value desired;
        boolean curValue;
        State state = this.getState(circState);
        boolean bl = curValue = ticks % (this.durationHigh + this.durationLow) < this.durationLow;
        if (state.clicks % 2 == 1) {
            curValue = !curValue;
        }
        Value value = desired = curValue ? Value.FALSE : Value.TRUE;
        if (!state.sending.equals(desired)) {
            state.sending = desired;
            circState.setValue(this.getLocation(), desired, this, 1);
            return true;
        }
        return false;
    }

    private State getState(CircuitState state) {
        State ret = (State)state.getData(this);
        if (ret == null) {
            ret = new State();
            state.setData(this, ret);
        }
        return ret;
    }

    private void createTextField() {
        int x;
        int valign;
        int halign;
        int y;
        AttributeSet attrs = this.getAttributeSet();
        Direction labelloc = (Direction)attrs.getValue(Pin.labelloc_attr);
        Bounds bds = this.getBounds();
        if (labelloc == Direction.NORTH) {
            halign = 0;
            valign = 2;
            x = bds.getX() + bds.getWidth() / 2;
            y = bds.getY() - 2;
            if (this.dir == labelloc) {
                halign = -1;
                x += 2;
            }
        } else if (labelloc == Direction.SOUTH) {
            halign = 0;
            valign = -1;
            x = bds.getX() + bds.getWidth() / 2;
            y = bds.getY() + bds.getHeight() + 2;
            if (this.dir == labelloc) {
                halign = -1;
                x += 2;
            }
        } else if (labelloc == Direction.EAST) {
            halign = -1;
            valign = 0;
            x = bds.getX() + bds.getWidth() + 2;
            y = bds.getY() + bds.getHeight() / 2;
            if (this.dir == labelloc) {
                valign = 2;
                y -= 2;
            }
        } else {
            halign = 1;
            valign = 0;
            x = bds.getX() - 2;
            y = bds.getY() + bds.getHeight() / 2;
            if (this.dir == labelloc) {
                valign = 2;
                y -= 2;
            }
        }
        if (this.field == null) {
            this.field = new TextField(x, y, halign, valign, (Font)attrs.getValue(Pin.labelfont_attr));
            this.field.addTextFieldListener(this.myListener);
        } else {
            this.field.setLocation(x, y, halign, valign);
            this.field.setFont((Font)attrs.getValue(Pin.labelfont_attr));
        }
        String text = (String)attrs.getValue(Pin.label_attr);
        this.field.setText(text == null ? "" : text);
    }

    @Override
    public void draw(ComponentDrawContext context) {
        boolean drawUp;
        Graphics g = context.getGraphics();
        Bounds bds = this.getBounds();
        int x = bds.getX();
        int y = bds.getY();
        GraphicsUtil.switchToWidth(g, 2);
        g.setColor(Color.black);
        g.drawRect(x, y, bds.getWidth(), bds.getHeight());
        if (this.field != null) {
            this.field.draw(g);
        }
        if (context.getShowState()) {
            CircuitState circState = context.getCircuitState();
            State state = this.getState(circState);
            g.setColor(state.sending.getColor());
            drawUp = state.sending == Value.TRUE;
        } else {
            g.setColor(Color.BLACK);
            drawUp = true;
        }
        int[] xs = new int[]{(x += 10) - 6, x - 6, x, x, x + 6, x + 6};
        int[] ys = drawUp ? new int[]{y, y - 4, y - 4, y + 4, y + 4, y} : new int[]{y, y + 4, y + 4, y - 4, y - 4, y += 10};
        g.drawPolyline(xs, ys, xs.length);
        context.drawPins(this);
    }

    @Override
    public Object getFeature(Object key) {
        if (key == Pokable.class) {
            return this;
        }
        if (key == Loggable.class) {
            return this;
        }
        if (key == TextEditable.class) {
            return this;
        }
        return super.getFeature(key);
    }

    @Override
    public Caret getPokeCaret(ComponentUserEvent event) {
        if (this.getBounds().contains(event.getX(), event.getY())) {
            return new PokeCaret(this, event.getCircuitState());
        }
        return null;
    }

    @Override
    public Caret getTextCaret(ComponentUserEvent event) {
        int x;
        int y;
        Graphics g = event.getCanvas().getGraphics();
        if (this.field == null) {
            this.createTextField();
            return this.field.getCaret(g, 0);
        }
        Bounds bds = this.field.getBounds(g);
        if (bds.getWidth() < 4 || bds.getHeight() < 4) {
            Location loc = this.getLocation();
            bds = bds.add(Bounds.create(loc).expand(2));
        }
        if (bds.contains(x = event.getX(), y = event.getY())) {
            return this.field.getCaret(g, x, y);
        }
        return null;
    }

    @Override
    public Object[] getLogOptions(CircuitState state) {
        return null;
    }

    @Override
    public String getLogName(Object option) {
        return (String)this.getAttributeSet().getValue(Pin.label_attr);
    }

    @Override
    public Value getLogValue(CircuitState state, Object option) {
        State s = this.getState(state);
        return s.sending;
    }

    public void changeValue(CircuitState state, Value value) {
        State myState = this.getState(state);
        myState.sending = value;
        ++myState.clicks;
        state.setValue(this.getLocation(), myState.sending, this, 1);
    }

    private class MyListener
    implements AttributeListener,
    TextFieldListener {
        private MyListener() {
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            Attribute attr = e.getAttribute();
            if (attr == ClockFactory.high_attr) {
                Clock.this.durationHigh = (Integer)e.getValue();
            } else if (attr == ClockFactory.low_attr) {
                Clock.this.durationLow = (Integer)e.getValue();
            } else if (attr == Pin.label_attr) {
                String val = (String)e.getValue();
                if (val == null || val.equals("")) {
                    Clock.this.field = null;
                } else if (Clock.this.field == null) {
                    Clock.this.createTextField();
                } else {
                    Clock.this.field.setText(val);
                }
            } else if (attr == Pin.labelloc_attr) {
                if (Clock.this.field != null) {
                    Clock.this.createTextField();
                }
            } else if (attr == Pin.labelfont_attr) {
                if (Clock.this.field != null) {
                    Clock.this.createTextField();
                }
            } else if (attr == Pin.facing_attr) {
                Location loc = Clock.this.getLocation();
                Clock.this.dir = (Direction)e.getValue();
                Clock.this.setBounds(ClockFactory.instance.getOffsetBounds(Clock.this.getAttributeSet()).translate(loc.getX(), loc.getY()));
                if (Clock.this.field != null) {
                    Clock.this.createTextField();
                }
            }
        }

        @Override
        public void textChanged(TextFieldEvent e) {
            String next;
            AttributeSet attrs = Clock.this.getAttributeSet();
            String prev = (String)attrs.getValue(Pin.label_attr);
            if (!prev.equals(next = e.getText())) {
                attrs.setValue(Pin.label_attr, next);
            }
        }
    }

    private static class PokeCaret
    implements Caret {
        Clock clock;
        CircuitState state;
        boolean isPressed = true;

        PokeCaret(Clock clock, CircuitState state) {
            this.clock = clock;
            this.state = state;
        }

        @Override
        public void addCaretListener(CaretListener e) {
        }

        @Override
        public void removeCaretListener(CaretListener e) {
        }

        @Override
        public String getText() {
            return "";
        }

        @Override
        public Bounds getBounds(Graphics g) {
            return this.clock.getBounds();
        }

        @Override
        public void draw(Graphics g) {
        }

        @Override
        public void commitText(String text) {
        }

        @Override
        public void cancelEditing() {
        }

        @Override
        public void stopEditing() {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            this.isPressed = this.isInside(e.getX(), e.getY());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            boolean isIn = this.isInside(e.getX(), e.getY());
            if (this.isPressed && isIn) {
                State myState = this.clock.getState(this.state);
                myState.sending = myState.sending.not();
                ++myState.clicks;
                this.state.setValue(this.clock.getLocation(), myState.sending, this.clock, 1);
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        private boolean isInside(int x, int y) {
            return this.clock.getBounds().contains(x, y);
        }
    }

    private static class State
    implements ComponentState,
    Cloneable {
        Value sending = Value.FALSE;
        int clicks = 0;

        private State() {
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            }
            catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }

}

