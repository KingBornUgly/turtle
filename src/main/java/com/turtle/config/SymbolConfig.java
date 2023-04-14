/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.config;

import java.util.StringJoiner;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 5:49 PM
 */
public final class SymbolConfig {
    public static final String LINE_SEPARATOR_COMPAT;

    private SymbolConfig() {
    }

    static {
        String var10000 = SymbolConfig.Symbol.CARRIAGE_RETURN.toString();
        LINE_SEPARATOR_COMPAT = var10000 + SymbolConfig.Symbol.LINE_SEPARATOR.toString();
    }

    public static enum Symbol {
        OR('|'),
        AND('&'),
        DOT('.'),
        ZERO('0'),
        PLUS('+'),
        MINUS('-'),
        COMMA(','),
        POUND('#'),
        COLON(':'),
        SPACE(' '),
        SLASH('/'),
        BACKSLASH('\\'),
        EQUALS('='),
        PERCENT('%'),
        QUESTION('?'),
        SEMICOLON(';'),
        SINGLE_QUOTE('\''),
        DOUBLE_QUOTE('"'),
        LINE_SEPARATOR('\n'),
        CARRIAGE_RETURN('\r'),
        OPEN_BRACE('{'),
        CLOSE_BRACE('}'),
        OPEN_BRACKET('['),
        CLOSE_BRACKET(']'),
        OPEN_PARENTHESIS('('),
        CLOSE_PARENTHESIS(')');

        private final char charValue;
        private final String stringValue;

        private Symbol(char value) {
            this.charValue = value;
            this.stringValue = Character.toString(value);
        }

        public String join(String... args) {
            if (args == null) {
                return null;
            } else {
                StringJoiner joiner = new StringJoiner(this.stringValue);
                String[] var3 = args;
                int var4 = args.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    String object = var3[var5];
                    joiner.add(object);
                }

                return joiner.toString();
            }
        }

        public String join(Object... args) {
            if (args == null) {
                return null;
            } else {
                StringJoiner joiner = new StringJoiner(this.stringValue);
                Object[] var3 = args;
                int var4 = args.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    Object object = var3[var5];
                    joiner.add(object == null ? null : object.toString());
                }

                return joiner.toString();
            }
        }

        public char toChar() {
            return this.charValue;
        }

        public String toString() {
            return this.stringValue;
        }
    }
}
