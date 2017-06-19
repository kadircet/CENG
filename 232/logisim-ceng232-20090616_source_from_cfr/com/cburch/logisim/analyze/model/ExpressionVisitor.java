/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.analyze.model.Expression;

public interface ExpressionVisitor {
    public Object visitAnd(Expression var1, Expression var2);

    public Object visitOr(Expression var1, Expression var2);

    public Object visitXor(Expression var1, Expression var2);

    public Object visitNot(Expression var1);

    public Object visitVariable(String var1);

    public Object visitConstant(int var1);
}

