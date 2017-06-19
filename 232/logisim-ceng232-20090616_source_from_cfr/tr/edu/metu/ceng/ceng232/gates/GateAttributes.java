/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.gates;

import com.cburch.logisim.data.AbstractAttributeSet;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeOption;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.util.IntegerFactory;
import com.cburch.logisim.util.StringGetter;
import java.util.Arrays;
import java.util.List;
import tr.edu.metu.ceng.ceng232.gates.AbstractGate;
import tr.edu.metu.ceng.ceng232.gates.Strings;

class GateAttributes
extends AbstractAttributeSet {
    static final int MAX_INPUTS = 9;
    static final int DELAY = 1;
    public static final Attribute facing_attr = Attributes.forDirection("facing", Strings.getter("gateFacingAttr"));
    public static final Attribute width_attr = Attributes.forBitWidth("width", 1, 1);
    static final AttributeOption SIZE_NARROW = new AttributeOption(IntegerFactory.create(30), Strings.getter("gateSizeNarrowOpt"));
    static final AttributeOption SIZE_WIDE = new AttributeOption(IntegerFactory.create(50), Strings.getter("gateSizeWideOpt"));
    public static final Attribute size_attr = Attributes.forOption("size", Strings.getter("gateSizeAttr"), new AttributeOption[]{SIZE_NARROW, SIZE_WIDE});
    private static final Integer INPUTS_2 = IntegerFactory.create(2);
    private static final Integer INPUTS_3 = IntegerFactory.create(3);
    private static final Integer INPUTS_5 = IntegerFactory.create(5);
    private static final Integer INPUTS_7 = IntegerFactory.create(7);
    private static final Integer INPUTS_9 = IntegerFactory.create(9);
    public static final Attribute inputs_attr = Attributes.forOption("inputs", Strings.getter("gateInputsAttr"), new Object[]{INPUTS_2});
    private static final List ATTRIBUTES = Arrays.asList(facing_attr, width_attr, size_attr, inputs_attr);
    AbstractGate gate = null;
    Direction facing = Direction.EAST;
    BitWidth width = BitWidth.ONE;
    AttributeOption size = SIZE_NARROW;
    int inputs = 2;

    GateAttributes() {
    }

    @Override
    protected void copyInto(AbstractAttributeSet destObj) {
        GateAttributes dest = (GateAttributes)destObj;
        dest.gate = null;
    }

    @Override
    public List getAttributes() {
        return ATTRIBUTES;
    }

    @Override
    public Object getValue(Attribute attr) {
        if (attr == facing_attr) {
            return this.facing;
        }
        if (attr == width_attr) {
            return this.width;
        }
        if (attr == size_attr) {
            return this.size;
        }
        if (attr == inputs_attr) {
            return IntegerFactory.create(this.inputs);
        }
        return null;
    }

    @Override
    public boolean isReadOnly(Attribute attr) {
        if (this.gate != null) {
            return attr == facing_attr || attr == size_attr || attr == inputs_attr;
        }
        return false;
    }

    @Override
    public void setValue(Attribute attr, Object value) {
        if (attr == width_attr) {
            this.width = (BitWidth)value;
        } else if (attr == facing_attr) {
            this.facing = (Direction)value;
        } else if (attr == size_attr) {
            this.size = (AttributeOption)value;
        } else if (attr == inputs_attr) {
            this.inputs = (Integer)value;
        } else {
            throw new IllegalArgumentException("unrecognized argument");
        }
        if (this.gate != null) {
            this.gate.attributeValueChanged(attr, value);
        }
        this.fireAttributeValueChanged(attr, value);
    }
}

