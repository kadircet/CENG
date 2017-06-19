/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.gui.Strings;
import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.ExpressionVisitor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import javax.swing.JPanel;

class ExpressionView
extends JPanel {
    private static final int BADNESS_IDENT_BREAK = 10000;
    private static final int BADNESS_BEFORE_SPACE = 500;
    private static final int BADNESS_BEFORE_AND = 50;
    private static final int BADNESS_BEFORE_XOR = 30;
    private static final int BADNESS_BEFORE_OR = 0;
    private static final int BADNESS_NOT_BREAK = 100;
    private static final int BADNESS_PER_NOT_BREAK = 30;
    private static final int BADNESS_PER_PIXEL = 1;
    private static final int NOT_SEP = 3;
    private static final int EXTRA_LEADING = 4;
    private static final int MINIMUM_HEIGHT = 25;
    private MyListener myListener;
    private RenderData renderData;

    public ExpressionView() {
        this.myListener = new MyListener();
        this.addComponentListener(this.myListener);
        this.setExpression(null);
    }

    public void setExpression(Expression expr) {
        ExpressionData exprData = new ExpressionData(expr);
        Graphics g = this.getGraphics();
        FontMetrics fm = g == null ? null : g.getFontMetrics();
        this.renderData = new RenderData(exprData, this.getWidth(), fm);
        this.setPreferredSize(this.renderData.getPreferredSize());
        this.revalidate();
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.renderData != null) {
            int x = Math.max(0, (this.getWidth() - this.renderData.prefWidth) / 2);
            int y = Math.max(0, (this.getHeight() - this.renderData.height) / 2);
            this.renderData.paint(g, x, y);
        }
    }

    void localeChanged() {
        this.repaint();
    }

    private static class RenderData {
        ExpressionData exprData;
        int prefWidth;
        int width;
        int height;
        String[] lineText;
        ArrayList[] lineNots;
        int[] lineY;

        RenderData(ExpressionData exprData, int width, FontMetrics fm) {
            this.exprData = exprData;
            this.width = width;
            this.height = 25;
            if (fm == null) {
                this.lineText = new String[]{exprData.text};
                this.lineNots = new ArrayList[]{exprData.nots};
                this.computeNotDepths();
                this.lineY = new int[]{25};
            } else {
                if (exprData.text.length() == 0) {
                    this.lineText = new String[]{Strings.get("expressionEmpty")};
                    this.lineNots = new ArrayList[]{new ArrayList()};
                } else {
                    this.computeLineText(fm);
                    this.computeLineNots();
                    this.computeNotDepths();
                }
                this.computeLineY(fm);
                this.prefWidth = this.lineText.length > 1 ? width : fm.stringWidth(this.lineText[0]);
            }
        }

        private void computeLineText(FontMetrics fm) {
            String text = this.exprData.text;
            int[] badness = this.exprData.badness;
            if (fm.stringWidth(text) <= this.width) {
                this.lineText = new String[]{text};
                return;
            }
            int startPos = 0;
            ArrayList<String> lines = new ArrayList<String>();
            while (startPos < text.length()) {
                String line;
                int stopPos = startPos + 1;
                String bestLine = text.substring(startPos, stopPos);
                if (stopPos >= text.length()) {
                    lines.add(bestLine);
                    break;
                }
                int bestStopPos = stopPos;
                int lineWidth = fm.stringWidth(bestLine);
                int bestBadness = badness[stopPos] + (this.width - lineWidth) * 1;
                while (stopPos < text.length() && (lineWidth = fm.stringWidth(line = text.substring(startPos, ++stopPos))) <= this.width) {
                    int lineBadness = badness[stopPos] + (this.width - lineWidth) * 1;
                    if (lineBadness >= bestBadness) continue;
                    bestBadness = lineBadness;
                    bestStopPos = stopPos;
                    bestLine = line;
                }
                lines.add(bestLine);
                startPos = bestStopPos;
            }
            this.lineText = new String[lines.size()];
            for (int i = 0; i < this.lineText.length; ++i) {
                this.lineText[i] = (String)lines.get(i);
            }
        }

        private void computeLineNots() {
            int i;
            ArrayList allNots = this.exprData.nots;
            this.lineNots = new ArrayList[this.lineText.length];
            for (i = 0; i < this.lineText.length; ++i) {
                this.lineNots[i] = new ArrayList();
            }
            for (i = 0; i < allNots.size(); ++i) {
                NotData nd = (NotData)allNots.get(i);
                int pos = 0;
                for (int j = 0; j < this.lineNots.length && pos < nd.stopIndex; ++j) {
                    String line = this.lineText[j];
                    int nextPos = pos + line.length();
                    if (nextPos > nd.startIndex) {
                        NotData toAdd = new NotData();
                        toAdd.startIndex = Math.max(pos, nd.startIndex) - pos;
                        toAdd.stopIndex = Math.min(nextPos, nd.stopIndex) - pos;
                        this.lineNots[j].add(toAdd);
                    }
                    pos = nextPos;
                }
            }
        }

        private void computeNotDepths() {
            for (int k = 0; k < this.lineNots.length; ++k) {
                ArrayList nots = this.lineNots[k];
                int n = nots.size();
                int[] stack = new int[n];
                for (int i = 0; i < nots.size(); ++i) {
                    NotData nd = (NotData)nots.get(i);
                    int depth = 0;
                    int top = 0;
                    stack[0] = nd.stopIndex;
                    for (int j = i + 1; j < nots.size(); ++j) {
                        NotData nd2 = (NotData)nots.get(j);
                        if (nd2.startIndex >= nd.stopIndex) break;
                        while (nd2.startIndex >= stack[top]) {
                            --top;
                        }
                        stack[++top] = nd2.stopIndex;
                        if (top <= depth) continue;
                        depth = top;
                    }
                    nd.depth = depth;
                }
            }
        }

        private void computeLineY(FontMetrics fm) {
            this.lineY = new int[this.lineNots.length];
            int curY = 0;
            for (int i = 0; i < this.lineY.length; ++i) {
                int maxDepth = -1;
                ArrayList nots = this.lineNots[i];
                for (int j = 0; j < nots.size(); ++j) {
                    NotData nd = (NotData)nots.get(j);
                    if (nd.depth <= maxDepth) continue;
                    maxDepth = nd.depth;
                }
                this.lineY[i] = curY + maxDepth * 3;
                curY = this.lineY[i] + fm.getHeight() + 4;
            }
            this.height = Math.max(25, curY - fm.getLeading() - 4);
        }

        public Dimension getPreferredSize() {
            return new Dimension(10, this.height);
        }

        public void paint(Graphics g, int x, int y) {
            FontMetrics fm = g.getFontMetrics();
            for (int i = 0; i < this.lineText.length; ++i) {
                String line = this.lineText[i];
                g.drawString(line, x, y + this.lineY[i] + fm.getAscent());
                ArrayList nots = this.lineNots[i];
                int n = nots.size();
                for (int j = 0; j < n; ++j) {
                    NotData nd = (NotData)nots.get(j);
                    int notY = y + this.lineY[i] - nd.depth * 3;
                    int startX = x + fm.stringWidth(line.substring(0, nd.startIndex));
                    int stopX = x + fm.stringWidth(line.substring(0, nd.stopIndex));
                    g.drawLine(startX, notY, stopX, notY);
                }
            }
        }
    }

    private static class ExpressionData {
        String text;
        final ArrayList nots = new ArrayList();
        int[] badness;

        ExpressionData(Expression expr) {
            if (expr == null) {
                this.text = "";
                this.badness = new int[0];
            } else {
                this.computeText(expr);
                this.computeBadnesses();
            }
        }

        private void computeText(Expression expr) {
            final StringBuffer text = new StringBuffer();
            expr.visit(new ExpressionVisitor(){

                @Override
                public Object visitAnd(Expression a, Expression b) {
                    return this.binary(a, b, 2, " ");
                }

                @Override
                public Object visitOr(Expression a, Expression b) {
                    return this.binary(a, b, 0, " + ");
                }

                @Override
                public Object visitXor(Expression a, Expression b) {
                    return this.binary(a, b, 1, " ^ ");
                }

                private Object binary(Expression a, Expression b, int level, String op) {
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
                    return null;
                }

                @Override
                public Object visitNot(Expression a) {
                    NotData notData = new NotData();
                    notData.startIndex = text.length();
                    ExpressionData.this.nots.add(notData);
                    a.visit(this);
                    notData.stopIndex = text.length();
                    return null;
                }

                @Override
                public Object visitVariable(String name) {
                    text.append(name);
                    return null;
                }

                @Override
                public Object visitConstant(int value) {
                    text.append("" + Integer.toString(value, 16));
                    return null;
                }
            });
            this.text = text.toString();
        }

        private void computeBadnesses() {
            this.badness = new int[this.text.length() + 1];
            this.badness[this.text.length()] = 0;
            if (this.text.length() == 0) {
                return;
            }
            this.badness[0] = Integer.MAX_VALUE;
            NotData curNot = this.nots.isEmpty() ? null : (NotData)this.nots.get(0);
            int curNotIndex = 0;
            char prev = this.text.charAt(0);
            for (int i = 1; i < this.text.length(); ++i) {
                char cur = this.text.charAt(i);
                this.badness[i] = cur == ' ' ? 500 : (Character.isJavaIdentifierPart(cur) ? (Character.isJavaIdentifierPart(prev) ? 10000 : 50) : (cur == '+' ? 0 : (cur == '^' ? 30 : (cur == ')' ? 500 : 50))));
                while (curNot != null && curNot.stopIndex <= i) {
                    curNot = ++curNotIndex >= this.nots.size() ? null : (NotData)this.nots.get(curNotIndex);
                }
                if (curNot != null && this.badness[i] < 10000) {
                    int depth = 0;
                    NotData nd = curNot;
                    int ndi = curNotIndex;
                    while (nd != null && nd.startIndex < i) {
                        if (nd.stopIndex > i) {
                            ++depth;
                        }
                        nd = ++ndi < this.nots.size() ? (NotData)this.nots.get(ndi) : null;
                    }
                    if (depth > 0) {
                        int[] arrn = this.badness;
                        int n = i;
                        arrn[n] = arrn[n] + (100 + (depth - 1) * 30);
                    }
                }
                prev = cur;
            }
        }

    }

    private static class NotData {
        int startIndex;
        int stopIndex;
        int depth;

        private NotData() {
        }
    }

    private class MyListener
    implements ComponentListener {
        private MyListener() {
        }

        @Override
        public void componentResized(ComponentEvent arg0) {
            int width = ExpressionView.this.getWidth();
            if (ExpressionView.this.renderData != null && Math.abs(ExpressionView.access$000((ExpressionView)ExpressionView.this).width - width) > 2) {
                Graphics g = ExpressionView.this.getGraphics();
                FontMetrics fm = g == null ? null : g.getFontMetrics();
                ExpressionView.this.renderData = new RenderData(ExpressionView.access$000((ExpressionView)ExpressionView.this).exprData, width, fm);
                ExpressionView.this.setPreferredSize(ExpressionView.this.renderData.getPreferredSize());
                ExpressionView.this.revalidate();
                ExpressionView.this.repaint();
            }
        }

        @Override
        public void componentMoved(ComponentEvent arg0) {
        }

        @Override
        public void componentShown(ComponentEvent arg0) {
        }

        @Override
        public void componentHidden(ComponentEvent arg0) {
        }
    }

}

