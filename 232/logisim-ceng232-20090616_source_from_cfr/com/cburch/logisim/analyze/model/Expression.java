/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.analyze.model.Assignments;
import com.cburch.logisim.analyze.model.ExpressionVisitor;
import com.cburch.logisim.analyze.model.Expressions;
import java.util.HashSet;

public abstract class Expression {
    public static final int OR_LEVEL = 0;
    public static final int XOR_LEVEL = 1;
    public static final int AND_LEVEL = 2;
    public static final int NOT_LEVEL = 3;

    public abstract int getPrecedence();

    public abstract Object visit(ExpressionVisitor var1);

    abstract void visit(Visitor var1);

    abstract int visit(IntVisitor var1);

    public boolean evaluate(final Assignments assignments) {
        int ret = this.visit(new IntVisitor(){

            @Override
            public int visitAnd(Expression a, Expression b) {
                return a.visit(this) & b.visit(this);
            }

            @Override
            public int visitOr(Expression a, Expression b) {
                return a.visit(this) | b.visit(this);
            }

            @Override
            public int visitXor(Expression a, Expression b) {
                return a.visit(this) ^ b.visit(this);
            }

            @Override
            public int visitNot(Expression a) {
                return ~ a.visit(this);
            }

            @Override
            public int visitVariable(String name) {
                return assignments.get(name) ? 1 : 0;
            }

            @Override
            public int visitConstant(int value) {
                return value;
            }
        });
        return (ret & 1) != 0;
    }

    public String toString() {
        final StringBuffer text = new StringBuffer();
        this.visit(new Visitor(){

            @Override
            public void visitAnd(Expression a, Expression b) {
                this.binary(a, b, 2, " ");
            }

            @Override
            public void visitOr(Expression a, Expression b) {
                this.binary(a, b, 0, " + ");
            }

            @Override
            public void visitXor(Expression a, Expression b) {
                this.binary(a, b, 1, " ^ ");
            }

            private void binary(Expression a, Expression b, int level, String op) {
                if (a.getPrecedence() < level) {
                    text.append("(");
                    a.visit(this);
                    text.append(")");
                } else {
                    a.visit(this);
                }
                text.append(op);
                if (b.getPrecedence() < level) {
                    text.append("(");
                    b.visit(this);
                    text.append(")");
                } else {
                    b.visit(this);
                }
            }

            @Override
            public void visitNot(Expression a) {
                text.append("~");
                if (a.getPrecedence() < 3) {
                    text.append("(");
                    a.visit(this);
                    text.append(")");
                } else {
                    a.visit(this);
                }
            }

            @Override
            public void visitVariable(String name) {
                text.append(name);
            }

            @Override
            public void visitConstant(int value) {
                text.append("" + Integer.toString(value, 16));
            }
        });
        return text.toString();
    }

    public boolean isCircular() {
        final HashSet<Expression> visited = new HashSet<Expression>();
        visited.add(this);
        return 1 == this.visit(new IntVisitor(){

            @Override
            public int visitAnd(Expression a, Expression b) {
                return this.binary(a, b);
            }

            @Override
            public int visitOr(Expression a, Expression b) {
                return this.binary(a, b);
            }

            @Override
            public int visitXor(Expression a, Expression b) {
                return this.binary(a, b);
            }

            @Override
            public int visitNot(Expression a) {
                if (!visited.add(a)) {
                    return 1;
                }
                if (a.visit(this) == 1) {
                    return 1;
                }
                visited.remove(a);
                return 0;
            }

            @Override
            public int visitVariable(String name) {
                return 0;
            }

            @Override
            public int visitConstant(int value) {
                return 0;
            }

            private int binary(Expression a, Expression b) {
                if (!visited.add(a)) {
                    return 1;
                }
                if (a.visit(this) == 1) {
                    return 1;
                }
                visited.remove(a);
                if (!visited.add(b)) {
                    return 1;
                }
                if (b.visit(this) == 1) {
                    return 1;
                }
                visited.remove(b);
                return 0;
            }
        });
    }

    Expression removeVariable(final String input) {
        return (Expression)this.visit(new ExpressionVisitor(){

            @Override
            public Object visitAnd(Expression a, Expression b) {
                Expression l = (Expression)a.visit(this);
                Expression r = (Expression)b.visit(this);
                if (l == null) {
                    return r;
                }
                if (r == null) {
                    return l;
                }
                return Expressions.and(l, r);
            }

            @Override
            public Object visitOr(Expression a, Expression b) {
                Expression l = (Expression)a.visit(this);
                Expression r = (Expression)b.visit(this);
                if (l == null) {
                    return r;
                }
                if (r == null) {
                    return l;
                }
                return Expressions.or(l, r);
            }

            @Override
            public Object visitXor(Expression a, Expression b) {
                Expression l = (Expression)a.visit(this);
                Expression r = (Expression)b.visit(this);
                if (l == null) {
                    return r;
                }
                if (r == null) {
                    return l;
                }
                return Expressions.xor(l, r);
            }

            @Override
            public Object visitNot(Expression a) {
                Expression l = (Expression)a.visit(this);
                if (l == null) {
                    return null;
                }
                return Expressions.not(l);
            }

            @Override
            public Object visitVariable(String name) {
                return name.equals(input) ? null : Expressions.variable(name);
            }

            @Override
            public Object visitConstant(int value) {
                return Expressions.constant(value);
            }
        });
    }

    Expression replaceVariable(final String oldName, final String newName) {
        return (Expression)this.visit(new ExpressionVisitor(){

            @Override
            public Object visitAnd(Expression a, Expression b) {
                Expression l = (Expression)a.visit(this);
                Expression r = (Expression)b.visit(this);
                return Expressions.and(l, r);
            }

            @Override
            public Object visitOr(Expression a, Expression b) {
                Expression l = (Expression)a.visit(this);
                Expression r = (Expression)b.visit(this);
                return Expressions.or(l, r);
            }

            @Override
            public Object visitXor(Expression a, Expression b) {
                Expression l = (Expression)a.visit(this);
                Expression r = (Expression)b.visit(this);
                return Expressions.xor(l, r);
            }

            @Override
            public Object visitNot(Expression a) {
                Expression l = (Expression)a.visit(this);
                return Expressions.not(l);
            }

            @Override
            public Object visitVariable(String name) {
                return Expressions.variable(name.equals(oldName) ? newName : name);
            }

            @Override
            public Object visitConstant(int value) {
                return Expressions.constant(value);
            }
        });
    }

    public boolean containsXor() {
        return 1 == this.visit(new IntVisitor(){

            @Override
            public int visitAnd(Expression a, Expression b) {
                return a.visit(this) == 1 || b.visit(this) == 1 ? 1 : 0;
            }

            @Override
            public int visitOr(Expression a, Expression b) {
                return a.visit(this) == 1 || b.visit(this) == 1 ? 1 : 0;
            }

            @Override
            public int visitXor(Expression a, Expression b) {
                return 1;
            }

            @Override
            public int visitNot(Expression a) {
                return a.visit(this);
            }

            @Override
            public int visitVariable(String name) {
                return 0;
            }

            @Override
            public int visitConstant(int value) {
                return 0;
            }
        });
    }

    public boolean isCnf() {
        return 1 == this.visit(new IntVisitor(){
            int level;

            @Override
            public int visitAnd(Expression a, Expression b) {
                if (this.level > 1) {
                    return 0;
                }
                int oldLevel = this.level;
                this.level = 1;
                int ret = a.visit(this) == 1 && b.visit(this) == 1 ? 1 : 0;
                this.level = oldLevel;
                return ret;
            }

            @Override
            public int visitOr(Expression a, Expression b) {
                if (this.level > 0) {
                    return 0;
                }
                return a.visit(this) == 1 && b.visit(this) == 1 ? 1 : 0;
            }

            @Override
            public int visitXor(Expression a, Expression b) {
                return 0;
            }

            @Override
            public int visitNot(Expression a) {
                if (this.level == 2) {
                    return 0;
                }
                int oldLevel = this.level;
                this.level = 2;
                int ret = a.visit(this);
                this.level = oldLevel;
                return ret;
            }

            @Override
            public int visitVariable(String name) {
                return 1;
            }

            @Override
            public int visitConstant(int value) {
                return 1;
            }
        });
    }

    static interface IntVisitor {
        public int visitAnd(Expression var1, Expression var2);

        public int visitOr(Expression var1, Expression var2);

        public int visitXor(Expression var1, Expression var2);

        public int visitNot(Expression var1);

        public int visitVariable(String var1);

        public int visitConstant(int var1);
    }

    static interface Visitor {
        public void visitAnd(Expression var1, Expression var2);

        public void visitOr(Expression var1, Expression var2);

        public void visitXor(Expression var1, Expression var2);

        public void visitNot(Expression var1);

        public void visitVariable(String var1);

        public void visitConstant(int var1);
    }

}

