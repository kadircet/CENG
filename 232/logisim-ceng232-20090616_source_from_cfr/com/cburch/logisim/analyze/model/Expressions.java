/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.ExpressionVisitor;

public class Expressions {
    private Expressions() {
    }

    public static Expression and(Expression a, Expression b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return new And(a, b);
    }

    public static Expression or(Expression a, Expression b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return new Or(a, b);
    }

    public static Expression xor(Expression a, Expression b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return new Xor(a, b);
    }

    public static Expression not(Expression a) {
        if (a == null) {
            return null;
        }
        return new Not(a);
    }

    public static Expression variable(String name) {
        return new Variable(name);
    }

    public static Expression constant(int value) {
        return new Constant(value);
    }

    private static class Constant
    extends Expression {
        private int value;

        Constant(int value) {
            this.value = value;
        }

        @Override
        public Object visit(ExpressionVisitor visitor) {
            return visitor.visitConstant(this.value);
        }

        @Override
        void visit(Expression.Visitor visitor) {
            visitor.visitConstant(this.value);
        }

        @Override
        int visit(Expression.IntVisitor visitor) {
            return visitor.visitConstant(this.value);
        }

        @Override
        public int getPrecedence() {
            return Integer.MAX_VALUE;
        }

        public boolean equals(Object other) {
            if (!(other instanceof Constant)) {
                return false;
            }
            Constant o = (Constant)other;
            return this.value == o.value;
        }

        public int hashCode() {
            return this.value;
        }
    }

    private static class Variable
    extends Expression {
        private String name;

        Variable(String name) {
            this.name = name;
        }

        @Override
        public Object visit(ExpressionVisitor visitor) {
            return visitor.visitVariable(this.name);
        }

        @Override
        void visit(Expression.Visitor visitor) {
            visitor.visitVariable(this.name);
        }

        @Override
        int visit(Expression.IntVisitor visitor) {
            return visitor.visitVariable(this.name);
        }

        @Override
        public int getPrecedence() {
            return Integer.MAX_VALUE;
        }

        public boolean equals(Object other) {
            if (!(other instanceof Variable)) {
                return false;
            }
            Variable o = (Variable)other;
            return this.name.equals(o.name);
        }

        public int hashCode() {
            return this.name.hashCode();
        }
    }

    private static class Not
    extends Expression {
        private Expression a;

        Not(Expression a) {
            this.a = a;
        }

        @Override
        public Object visit(ExpressionVisitor visitor) {
            return visitor.visitNot(this.a);
        }

        @Override
        void visit(Expression.Visitor visitor) {
            visitor.visitNot(this.a);
        }

        @Override
        int visit(Expression.IntVisitor visitor) {
            return visitor.visitNot(this.a);
        }

        @Override
        public int getPrecedence() {
            return 3;
        }

        public boolean equals(Object other) {
            if (!(other instanceof Not)) {
                return false;
            }
            Not o = (Not)other;
            return this.a.equals(o.a);
        }

        public int hashCode() {
            return 31 * this.a.hashCode();
        }
    }

    private static class Xor
    extends Binary {
        Xor(Expression a, Expression b) {
            super(a, b);
        }

        @Override
        public Object visit(ExpressionVisitor visitor) {
            return visitor.visitXor(this.a, this.b);
        }

        @Override
        void visit(Expression.Visitor visitor) {
            visitor.visitXor(this.a, this.b);
        }

        @Override
        int visit(Expression.IntVisitor visitor) {
            return visitor.visitXor(this.a, this.b);
        }

        @Override
        public int getPrecedence() {
            return 1;
        }
    }

    private static class Or
    extends Binary {
        Or(Expression a, Expression b) {
            super(a, b);
        }

        @Override
        public Object visit(ExpressionVisitor visitor) {
            return visitor.visitOr(this.a, this.b);
        }

        @Override
        void visit(Expression.Visitor visitor) {
            visitor.visitOr(this.a, this.b);
        }

        @Override
        int visit(Expression.IntVisitor visitor) {
            return visitor.visitOr(this.a, this.b);
        }

        @Override
        public int getPrecedence() {
            return 0;
        }
    }

    private static class And
    extends Binary {
        And(Expression a, Expression b) {
            super(a, b);
        }

        @Override
        public Object visit(ExpressionVisitor visitor) {
            return visitor.visitAnd(this.a, this.b);
        }

        @Override
        void visit(Expression.Visitor visitor) {
            visitor.visitAnd(this.a, this.b);
        }

        @Override
        int visit(Expression.IntVisitor visitor) {
            return visitor.visitAnd(this.a, this.b);
        }

        @Override
        public int getPrecedence() {
            return 2;
        }
    }

    private static abstract class Binary
    extends Expression {
        protected final Expression a;
        protected final Expression b;

        Binary(Expression a, Expression b) {
            this.a = a;
            this.b = b;
        }

        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (this.getClass() != other.getClass()) {
                return false;
            }
            Binary o = (Binary)other;
            return this.a.equals(o.a) && this.b.equals(o.b);
        }

        public int hashCode() {
            return 31 * (31 * this.getClass().hashCode() + this.a.hashCode()) + this.b.hashCode();
        }
    }

}

