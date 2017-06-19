/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.gates;

import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.ExpressionVisitor;
import com.cburch.logisim.comp.ComponentFactory;
import java.util.ArrayList;
import java.util.Collection;
import tr.edu.metu.ceng.ceng232.gates.AndGate;
import tr.edu.metu.ceng.ceng232.gates.EvenParityGate;
import tr.edu.metu.ceng.ceng232.gates.NandGate;
import tr.edu.metu.ceng.ceng232.gates.NorGate;
import tr.edu.metu.ceng.ceng232.gates.NotGate;
import tr.edu.metu.ceng.ceng232.gates.OddParityGate;
import tr.edu.metu.ceng.ceng232.gates.OrGate;
import tr.edu.metu.ceng.ceng232.gates.XnorGate;
import tr.edu.metu.ceng.ceng232.gates.XorGate;

abstract class CircuitDetermination {
    CircuitDetermination() {
    }

    void convertToTwoInputs() {
    }

    void convertToNands() {
    }

    void repair() {
    }

    boolean isNandNot() {
        return false;
    }

    static CircuitDetermination create(Expression expr) {
        if (expr == null) {
            return null;
        }
        return (CircuitDetermination)expr.visit(new Determine());
    }

    private static class Determine
    implements ExpressionVisitor {
        private Determine() {
        }

        @Override
        public Object visitAnd(Expression a, Expression b) {
            return this.binary(a.visit(this), b.visit(this), AndGate.instance);
        }

        @Override
        public Object visitOr(Expression a, Expression b) {
            return this.binary(a.visit(this), b.visit(this), OrGate.instance);
        }

        @Override
        public Object visitXor(Expression a, Expression b) {
            return this.binary(a.visit(this), b.visit(this), XorGate.instance);
        }

        private Gate binary(Object aret, Object bret, ComponentFactory factory) {
            Gate a;
            Gate b;
            if (aret instanceof Gate && (a = (Gate)aret).factory == factory) {
                Gate b2;
                if (bret instanceof Gate && (b2 = (Gate)bret).factory == factory) {
                    a.inputs.addAll(b2.inputs);
                    return a;
                }
                a.inputs.add(bret);
                return a;
            }
            if (bret instanceof Gate && (b = (Gate)bret).factory == factory) {
                b.inputs.add(aret);
                return b;
            }
            Gate ret = new Gate(factory);
            ret.inputs.add(aret);
            ret.inputs.add(bret);
            return ret;
        }

        @Override
        public Object visitNot(Expression aBase) {
            Object aret = aBase.visit(this);
            if (aret instanceof Gate) {
                Gate a = (Gate)aret;
                if (a.factory == AndGate.instance) {
                    a.factory = NandGate.instance;
                    return a;
                }
                if (a.factory == OrGate.instance) {
                    a.factory = NorGate.instance;
                    return a;
                }
                if (a.factory == XorGate.instance) {
                    a.factory = XnorGate.instance;
                    return a;
                }
            }
            Gate ret = new Gate(NotGate.factory);
            ret.inputs.add(aret);
            return ret;
        }

        @Override
        public Object visitVariable(String name) {
            return new Input(name);
        }

        @Override
        public Object visitConstant(int value) {
            return new Value(value);
        }
    }

    static class Value
    extends CircuitDetermination {
        private int value;

        private Value(int value) {
            this.value = value;
        }

        int getValue() {
            return this.value;
        }
    }

    static class Input
    extends CircuitDetermination {
        private String name;

        private Input(String name) {
            this.name = name;
        }

        String getName() {
            return this.name;
        }
    }

    static class Gate
    extends CircuitDetermination {
        private ComponentFactory factory;
        private ArrayList inputs = new ArrayList();

        private Gate(ComponentFactory factory) {
            this.factory = factory;
        }

        ComponentFactory getFactory() {
            return this.factory;
        }

        ArrayList getInputs() {
            return this.inputs;
        }

        @Override
        void convertToTwoInputs() {
            if (this.inputs.size() <= 2) {
                for (int i = 0; i < this.inputs.size(); ++i) {
                    CircuitDetermination a = (CircuitDetermination)this.inputs.get(i);
                    a.convertToTwoInputs();
                }
            } else {
                ComponentFactory subFactory = this.factory == NorGate.instance ? OrGate.instance : (this.factory == NandGate.instance ? AndGate.instance : this.factory);
                int split = (this.inputs.size() + 1) / 2;
                CircuitDetermination a = this.convertToTwoInputsSub(0, split, subFactory);
                CircuitDetermination b = this.convertToTwoInputsSub(split, this.inputs.size(), subFactory);
                this.inputs.clear();
                this.inputs.add(a);
                this.inputs.add(b);
            }
        }

        private CircuitDetermination convertToTwoInputsSub(int start, int stop, ComponentFactory subFactory) {
            if (stop - start == 1) {
                CircuitDetermination a = (CircuitDetermination)this.inputs.get(start);
                a.convertToTwoInputs();
                return a;
            }
            int split = (start + stop + 1) / 2;
            CircuitDetermination a = this.convertToTwoInputsSub(start, split, subFactory);
            CircuitDetermination b = this.convertToTwoInputsSub(split, stop, subFactory);
            Gate ret = new Gate(subFactory);
            ret.inputs.add(a);
            ret.inputs.add(b);
            return ret;
        }

        @Override
        void convertToNands() {
            int num = this.inputs.size();
            for (int i = 0; i < num; ++i) {
                CircuitDetermination sub = (CircuitDetermination)this.inputs.get(i);
                sub.convertToNands();
            }
            if (this.factory == NotGate.factory) {
                this.inputs.add(this.inputs.get(0));
            } else if (this.factory == AndGate.instance) {
                this.notOutput();
            } else if (this.factory == OrGate.instance) {
                this.notAllInputs();
            } else if (this.factory == NorGate.instance) {
                this.notAllInputs();
                this.notOutput();
            } else if (this.factory != NandGate.instance) {
                throw new IllegalArgumentException("Cannot handle " + this.factory.getDisplayName());
            }
            this.factory = NandGate.instance;
        }

        private void notOutput() {
            Gate sub = new Gate(NandGate.instance);
            sub.inputs = this.inputs;
            this.inputs = new ArrayList();
            this.inputs.add(sub);
            this.inputs.add(sub);
        }

        private void notAllInputs() {
            for (int i = 0; i < this.inputs.size(); ++i) {
                CircuitDetermination old = (CircuitDetermination)this.inputs.get(i);
                if (old.isNandNot()) {
                    this.inputs.set(i, ((Gate)old).inputs.get(0));
                    continue;
                }
                Gate now = new Gate(NandGate.instance);
                now.inputs.add(old);
                now.inputs.add(old);
                this.inputs.set(i, now);
            }
        }

        @Override
        boolean isNandNot() {
            return this.factory == NandGate.instance && this.inputs.size() == 2 && this.inputs.get(0) == this.inputs.get(1);
        }

        @Override
        void repair() {
            int num = this.inputs.size();
            if (num > 9) {
                int newNum = (num + 9 - 1) / 9;
                ArrayList oldInputs = this.inputs;
                this.inputs = new ArrayList();
                ComponentFactory subFactory = this.factory;
                if (subFactory == NandGate.instance) {
                    subFactory = AndGate.instance;
                }
                if (subFactory == NorGate.instance) {
                    subFactory = OrGate.instance;
                }
                int per = num / newNum;
                int numExtra = num - per * newNum;
                int k = 0;
                for (int i = 0; i < newNum; ++i) {
                    Gate sub = new Gate(subFactory);
                    int subCount = per + (i < numExtra ? 1 : 0);
                    for (int j = 0; j < subCount; ++j) {
                        sub.inputs.add(oldInputs.get(k));
                        ++k;
                    }
                    this.inputs.add(sub);
                }
            }
            if (this.inputs.size() > 2) {
                if (this.factory == XorGate.instance) {
                    this.factory = OddParityGate.instance;
                } else if (this.factory == XnorGate.instance) {
                    this.factory = EvenParityGate.instance;
                }
            }
            for (int i = 0; i < this.inputs.size(); ++i) {
                CircuitDetermination sub = (CircuitDetermination)this.inputs.get(i);
                sub.repair();
            }
        }
    }

}

