/*
 *  Copyright (c) 2009, Adrian Moser
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  * Neither the name of the author nor the
 *  names of its contributors may be used to endorse or promote products
 *  derived from this software without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL AUTHOR BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ch.eskaton.regex.parser;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import ch.eskaton.yajpg.api.Lexer;
import ch.eskaton.yajpg.api.Token;

/**
 * Lexer for a regular expression.
 */
@SuppressWarnings("serial")
public class RegexLexer implements Lexer {

    public static final Set<String> ESCAPABLE_CHARS = new HashSet<String>() {
        {
            add("-");
            add("]");
            add("^");
        }
    };

    private static final int EOLIND = -1;

    private int pos = 0;

    private String regex;

    private boolean inCharacterClass = false;

    private Stack<Token> pushBack = new Stack<Token>();

    public RegexLexer(String regex) {
        this.regex = regex;
    }

    public int next(String s) {
        if (pos < s.length()) {
            return s.charAt(pos++);
        } else {
            return EOLIND;
        }
    }

    public void pushBack() {
        if (pos > 0) {
            pos--;
        }
    }

    public void pushBackToken(Token t) {
        pushBack.push(t);
    }

    public Token nextToken() {
        int c;

        if (!pushBack.isEmpty()) {
            return pushBack.pop();
        }

        while ((c = next(regex)) != EOLIND) {
            if (inCharacterClass) {
                if (c == '\\') {
                    int next = next(regex);
                    if (ESCAPABLE_CHARS.contains(String.valueOf((char) next))) {
                        return getToken(RegexToken.CLASSCHAR, next);
                    } else {
                        pushBack();
                    }
                } else if (c == ']') {
                    inCharacterClass = false;
                    return getToken(RegexToken.RBRACKET, c);
                }
                
                if (c == '-') {
                    return getToken(RegexToken.HYPHEN, c);
                }
                
                if (c == '^') {
                    return getToken(RegexToken.NOT, c);
                }
                
                return getToken(RegexToken.CLASSCHAR, c);
            }
            
            switch (c) {
                case '[':
                    inCharacterClass = true;
                    return getToken(RegexToken.LBRACKET, c);
                case '(':
                    return getToken(RegexToken.LBRACE, c);
                case ')':
                    return getToken(RegexToken.RBRACE, c);
                case '|':
                    return getToken(RegexToken.OR, c);
                case '*':
                    return getToken(RegexToken.STAR, c);
                case '+':
                    return getToken(RegexToken.PLUS, c);
                case '?':
                    return getToken(RegexToken.OPTION, c);
                default:
                    return getToken(RegexToken.CHAR, c);
            }
        }

        return getToken(RegexToken.EOL, c);
    }

    private Token getToken(RegexToken type, int c) {
        return new Token(type.ordinal(), String.valueOf((char) c), 0);
    }

}
