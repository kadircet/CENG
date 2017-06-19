/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.Expressions;
import com.cburch.logisim.analyze.model.ParserException;
import com.cburch.logisim.analyze.model.Strings;
import com.cburch.logisim.analyze.model.VariableList;
import com.cburch.logisim.util.StringGetter;
import java.util.ArrayList;

public class Parser {
    private static final int TOKEN_AND = 0;
    private static final int TOKEN_OR = 1;
    private static final int TOKEN_XOR = 2;
    private static final int TOKEN_NOT = 3;
    private static final int TOKEN_LPAREN = 4;
    private static final int TOKEN_RPAREN = 5;
    private static final int TOKEN_IDENT = 6;
    private static final int TOKEN_CONST = 7;
    private static final int TOKEN_WHITE = 8;
    private static final int TOKEN_ERROR = 9;

    private Parser() {
    }

    public static Expression parse(String in, AnalyzerModel model) throws ParserException {
        ArrayList tokens = Parser.toTokens(in, false);
        if (tokens.size() == 0) {
            return null;
        }
        int n = tokens.size();
        for (int i = 0; i < n; ++i) {
            int index;
            Token token = (Token)tokens.get(i);
            if (token.type == 9) {
                throw token.error(Strings.getter("invalidCharacterError", token.text));
            }
            if (token.type != 6 || (index = model.getInputs().indexOf(token.text)) >= 0) continue;
            String opText = token.text.toUpperCase();
            if (opText.equals("NOT")) {
                token.type = 3;
                continue;
            }
            if (opText.equals("AND")) {
                token.type = 0;
                continue;
            }
            if (opText.equals("XOR")) {
                token.type = 2;
                continue;
            }
            if (opText.equals("OR")) {
                token.type = 1;
                continue;
            }
            throw token.error(Strings.getter("badVariableName", token.text));
        }
        return Parser.parse(tokens);
    }

    static String replaceVariable(String in, String oldName, String newName) {
        StringBuffer ret = new StringBuffer();
        ArrayList tokens = Parser.toTokens(in, true);
        int n = tokens.size();
        for (int i = 0; i < n; ++i) {
            Token token = (Token)tokens.get(i);
            if (token.type == 6 && token.text.equals(oldName)) {
                ret.append(newName);
                continue;
            }
            ret.append(token.text);
        }
        return ret.toString();
    }

    private static ArrayList toTokens(String in, boolean includeWhite) {
        ArrayList<Token> tokens = new ArrayList<Token>();
        in = in + " ";
        int pos = 0;
        block11 : do {
            int whiteStart = pos;
            while (pos < in.length() && Character.isWhitespace(in.charAt(pos))) {
                ++pos;
            }
            if (includeWhite && pos != whiteStart) {
                tokens.add(new Token(8, whiteStart, in.substring(whiteStart, pos)));
            }
            if (pos == in.length()) {
                return tokens;
            }
            int start = pos;
            char startChar = in.charAt(pos);
            ++pos;
            if (Character.isJavaIdentifierStart(startChar)) {
                while (Character.isJavaIdentifierPart(in.charAt(pos))) {
                    ++pos;
                }
                tokens.add(new Token(6, start, in.substring(start, pos)));
                continue;
            }
            switch (startChar) {
                case '(': {
                    tokens.add(new Token(4, start, "("));
                    continue block11;
                }
                case ')': {
                    tokens.add(new Token(5, start, ")"));
                    continue block11;
                }
                case '0': 
                case '1': {
                    tokens.add(new Token(7, start, "" + startChar));
                    continue block11;
                }
                case '~': {
                    tokens.add(new Token(3, start, "~"));
                    continue block11;
                }
                case '^': {
                    tokens.add(new Token(2, start, "^"));
                    continue block11;
                }
                case '+': {
                    tokens.add(new Token(1, start, "+"));
                    continue block11;
                }
                case '!': {
                    tokens.add(new Token(3, start, "!"));
                    continue block11;
                }
                case '&': {
                    if (in.charAt(pos) == '&') {
                        ++pos;
                    }
                    tokens.add(new Token(0, start, in.substring(start, pos)));
                    continue block11;
                }
                case '|': {
                    if (in.charAt(pos) == '|') {
                        ++pos;
                    }
                    tokens.add(new Token(1, start, in.substring(start, pos)));
                    continue block11;
                }
            }
            while (!Parser.okCharacter(in.charAt(pos))) {
                ++pos;
            }
            String errorText = in.substring(start, pos);
            tokens.add(new Token(9, start, errorText));
        } while (true);
    }

    private static boolean okCharacter(char c) {
        return Character.isWhitespace(c) || Character.isJavaIdentifierStart(c) || "()01~^+!&|".indexOf(c) >= 0;
    }

    private static Expression parse(ArrayList tokens) throws ParserException {
        ArrayList stack = new ArrayList();
        Expression current = null;
        for (int i = 0; i < tokens.size(); ++i) {
            Token t = (Token)tokens.get(i);
            if (t.type == 6 || t.type == 7) {
                Expression here = t.type == 6 ? Expressions.variable(t.text) : Expressions.constant(Integer.parseInt(t.text, 16));
                while (Parser.peekLevel(stack) == 3) {
                    here = Expressions.not(here);
                    Parser.pop(stack);
                }
                current = Expressions.and(current, here);
                if (Parser.peekLevel(stack) != 2) continue;
                Context top = Parser.pop(stack);
                current = Expressions.and(top.current, current);
                continue;
            }
            if (t.type == 3) {
                if (current != null) {
                    Parser.push(stack, current, 2, new Token(0, t.offset, Strings.get("implicitAndOperator")));
                }
                Parser.push(stack, null, 3, t);
                current = null;
                continue;
            }
            if (t.type == 4) {
                if (current != null) {
                    Parser.push(stack, current, 2, new Token(0, t.offset, 0, Strings.get("implicitAndOperator")));
                }
                Parser.push(stack, null, -2, t);
                current = null;
                continue;
            }
            if (t.type == 5) {
                current = Parser.popTo(stack, -1, current);
                if (stack.isEmpty()) {
                    throw t.error(Strings.getter("lparenMissingError"));
                }
                Parser.pop(stack);
                current = Parser.popTo(stack, 2, current);
                continue;
            }
            if (current == null) {
                throw t.error(Strings.getter("missingLeftOperandError", t.text));
            }
            int level = 0;
            switch (t.type) {
                case 0: {
                    level = 2;
                    break;
                }
                case 1: {
                    level = 0;
                    break;
                }
                case 2: {
                    level = 1;
                }
            }
            Parser.push(stack, Parser.popTo(stack, level, current), level, t);
            current = null;
        }
        current = Parser.popTo(stack, -1, current);
        if (!stack.isEmpty()) {
            Context top = Parser.pop(stack);
            throw top.cause.error(Strings.getter("rparenMissingError"));
        }
        return current;
    }

    private static void push(ArrayList stack, Expression expr, int level, Token cause) {
        stack.add(new Context(expr, level, cause));
    }

    private static int peekLevel(ArrayList stack) {
        if (stack.isEmpty()) {
            return -3;
        }
        Context context = (Context)stack.get(stack.size() - 1);
        return context.level;
    }

    private static Context pop(ArrayList stack) {
        return (Context)stack.remove(stack.size() - 1);
    }

    private static Expression popTo(ArrayList stack, int level, Expression current) throws ParserException {
        while (!stack.isEmpty() && Parser.peekLevel(stack) >= level) {
            Context top = Parser.pop(stack);
            if (current == null) {
                throw top.cause.error(Strings.getter("missingRightOperandError", top.cause.text));
            }
            switch (top.level) {
                case 2: {
                    current = Expressions.and(top.current, current);
                    break;
                }
                case 0: {
                    current = Expressions.or(top.current, current);
                    break;
                }
                case 1: {
                    current = Expressions.xor(top.current, current);
                    break;
                }
                case 3: {
                    current = Expressions.not(current);
                }
            }
        }
        return current;
    }

    private static class Context {
        int level;
        Expression current;
        Token cause;

        Context(Expression current, int level, Token cause) {
            this.level = level;
            this.current = current;
            this.cause = cause;
        }
    }

    private static class Token {
        int type;
        int offset;
        int length;
        String text;

        Token(int type, int offset, String text) {
            this(type, offset, text.length(), text);
        }

        Token(int type, int offset, int length, String text) {
            this.type = type;
            this.offset = offset;
            this.length = length;
            this.text = text;
        }

        ParserException error(StringGetter message) {
            return new ParserException(message, this.offset, this.length);
        }
    }

}

