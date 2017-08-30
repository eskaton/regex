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
package ch.eskaton.regex;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import ch.eskaton.yajpg.api.ParseException;
import ch.eskaton.yajpg.api.ParserFactory;
import ch.eskaton.yajpg.api.ParserInstantiationException;
import ch.eskaton.regex.Regex;
import ch.eskaton.regex.RegexException;
import ch.eskaton.regex.fsm.RegexStateMachine;
import ch.eskaton.regex.parser.RegexCompiler;
import ch.eskaton.regex.parser.RegexLexer;
import ch.eskaton.regex.parser.RegexNode;

public class RegexTest {

    @Test
    public void testNondeterministic() throws RegexException {
        String regex = "(a|b)*aba";
        assertTrue(Regex.match(regex, "aba"));
        assertTrue(Regex.match(regex, "ababa"));
        assertTrue(Regex.match(regex, "aaaaaaba"));
        assertTrue(Regex.match(regex, "bbbbaba"));
        assertTrue(Regex.match(regex, "ababababa"));
        assertFalse(Regex.match(regex, "ababbba"));
        assertFalse(Regex.match(regex, "a"));
        assertFalse(Regex.match(regex, "b"));
    }

    @Test
    public void testDecimals() throws RegexException {
        String regex = "([1-9][0-9]*(.[0-9]*)?)|(0*?.[0-9]+)";
        assertTrue(Regex.match(regex, "1"));
        assertTrue(Regex.match(regex, "125"));
        assertTrue(Regex.match(regex, "50."));
        assertTrue(Regex.match(regex, ".0123"));
        assertTrue(Regex.match(regex, "0.123"));
    }

    @Test
    public void testHex() throws RegexException, ParserInstantiationException,
            ParseException, IOException {
        String number = "0x1F2A";
        String regexDec = "([0-9]+(.[0-9]*)?)|(.[0-9]+)";
        String regexOct = "0[0-7]*";
        String regexHex = "0x[0-9a-fA-F]+";

        RegexStateMachine decSm = new RegexCompiler()
                .compile((RegexNode) ParserFactory.create(
                        "ch.eskaton.regex.parser.RegexParser",
                        new RegexLexer(regexDec)).parse());

        RegexStateMachine octSm = new RegexCompiler()
                .compile((RegexNode) ParserFactory.create(
                        "ch.eskaton.regex.parser.RegexParser",
                        new RegexLexer(regexOct)).parse());

        RegexStateMachine hexSm = new RegexCompiler()
                .compile((RegexNode) ParserFactory.create(
                        "ch.eskaton.regex.parser.RegexParser",
                        new RegexLexer(regexHex)).parse());

        for (int i = 0; i < number.length(); i++) {
            char c = number.charAt(i);
            decSm.processEvent(c);
            octSm.processEvent(c);
            hexSm.processEvent(c);
        }

        assertFalse(decSm.accepts());
        assertFalse(octSm.accepts());
        assertTrue(hexSm.accepts());
    }

    @Test
    public void testOctal() throws RegexException,
            ParserInstantiationException, ParseException, IOException {
        String number = "0127";
        String regexDec = "([1-9][0-9]*(.[0-9]*)?)|(.[0-9]+)";
        String regexOct = "0[0-7]*";
        String regexHex = "0x[0-9a-fA-F]+";

        RegexStateMachine decSm = new RegexCompiler()
                .compile((RegexNode) ParserFactory.create(
                        "ch.eskaton.regex.parser.RegexParser",
                        new RegexLexer(regexDec)).parse());

        RegexStateMachine octSm = new RegexCompiler()
                .compile((RegexNode) ParserFactory.create(
                         "ch.eskaton.regex.parser.RegexParser",
                        new RegexLexer(regexOct)).parse());

        RegexStateMachine hexSm = new RegexCompiler()
                .compile((RegexNode) ParserFactory.create(
                        "ch.eskaton.regex.parser.RegexParser",
                        new RegexLexer(regexHex)).parse());

        for (int i = 0; i < number.length(); i++) {
            char c = number.charAt(i);
            decSm.processEvent(c);
            octSm.processEvent(c);
            hexSm.processEvent(c);
        }

        assertFalse(decSm.accepts());
        assertTrue(octSm.accepts());
        assertFalse(hexSm.accepts());
    }

    @SuppressWarnings("serial")
    @Test
    public void testCombinedHex() throws ParseException, IOException,
            ParserInstantiationException {
        String number = "0x1F2A";
        String regexDec = "([1-9][0-9]*(.[0-9]*)?)|(0*?.[0-9]+)";
        String regexOct = "0[0-7]*";
        String regexHex = "0x[0-9a-fA-F]+";

        final RegexStateMachine decSm = new RegexCompiler()
                .compile((RegexNode) ParserFactory.create(
                        "ch.eskaton.regex.parser.RegexParser",
                        new RegexLexer(regexDec)).parse());

        final RegexStateMachine octSm = new RegexCompiler()
                .compile((RegexNode) ParserFactory.create(
                        "ch.eskaton.regex.parser.RegexParser",
                        new RegexLexer(regexOct)).parse());

        final RegexStateMachine hexSm = new RegexCompiler()
                .compile((RegexNode) ParserFactory.create(
                        "ch.eskaton.regex.parser.RegexParser",
                        new RegexLexer(regexHex)).parse());

        RegexStateMachine combSm = new RegexCompiler()
                .combine(new ArrayList<RegexStateMachine>() {
                    {
                        add(decSm);
                        add(octSm);
                        add(hexSm);
                    }
                });

        for (int i = 0; i < number.length(); i++) {
            char c = number.charAt(i);
            combSm.processEvent(c);
            decSm.processEvent(c);
            octSm.processEvent(c);
            hexSm.processEvent(c);
        }

        assertTrue(combSm.accepts());
        assertFalse(decSm.accepts());
        assertFalse(octSm.accepts());
        assertTrue(hexSm.accepts());
    }

    @SuppressWarnings("serial")
    @Test
    public void testCombinedDec() throws ParseException, IOException,
            ParserInstantiationException {
        String number = "00.127";
        String regexDec = "([1-9][0-9]*(.[0-9]*)?)|(0*?.[0-9]+)";
        String regexOct = "0[0-7]*";
        String regexHex = "0x[0-9a-fA-F]+";

        final RegexStateMachine decSm = new RegexCompiler()
                .compile((RegexNode) ParserFactory.create(
                        "ch.eskaton.regex.parser.RegexParser",
                        new RegexLexer(regexDec)).parse());

        final RegexStateMachine octSm = new RegexCompiler()
                .compile((RegexNode) ParserFactory.create(
                        "ch.eskaton.regex.parser.RegexParser",
                        new RegexLexer(regexOct)).parse());

        final RegexStateMachine hexSm = new RegexCompiler()
                .compile((RegexNode) ParserFactory.create(
                        "ch.eskaton.regex.parser.RegexParser",
                        new RegexLexer(regexHex)).parse());

        RegexStateMachine combSm = new RegexCompiler()
                .combine(new ArrayList<RegexStateMachine>() {
                    {
                        add(decSm);
                        add(octSm);
                        add(hexSm);
                    }
                });

        for (int i = 0; i < number.length(); i++) {
            char c = number.charAt(i);
            combSm.processEvent(c);
            decSm.processEvent(c);
            octSm.processEvent(c);
            hexSm.processEvent(c);
        }

        assertTrue(combSm.accepts());
        assertTrue(decSm.accepts());
        assertFalse(octSm.accepts());
        assertFalse(hexSm.accepts());
    }

    @SuppressWarnings("serial")
    @Test
    public void testCombinedString() throws ParseException, IOException,
            ParserInstantiationException {
        String regex1 = "string";
        String regex2 = "[a-z]+";

        final RegexStateMachine sm1 = new RegexCompiler().compile(
                (RegexNode) ParserFactory.create(
                        "ch.eskaton.regex.parser.RegexParser",
                        new RegexLexer(regex1)).parse(), new String("sm1"));

        final RegexStateMachine sm2 = new RegexCompiler().compile(
                (RegexNode) ParserFactory.create(
                        "ch.eskaton.regex.parser.RegexParser",
                        new RegexLexer(regex2)).parse(), new String("sm2"));

        RegexStateMachine combSm = new RegexCompiler()
                .combine(new ArrayList<RegexStateMachine>() {
                    {
                        add(sm1);
                        add(sm2);
                    }
                });

        assertFalse(combSm.accepts());
        combSm.processEvent('s');
        assertTrue(combSm.accepts());
        assertTrue("sm2".equals(combSm.getAcceptObject()));
        combSm.processEvent('t');
        assertTrue(combSm.accepts());
        assertTrue("sm2".equals(combSm.getAcceptObject()));
        combSm.processEvent('r');
        assertTrue(combSm.accepts());
        assertTrue("sm2".equals(combSm.getAcceptObject()));
        combSm.processEvent('i');
        assertTrue(combSm.accepts());
        assertTrue("sm2".equals(combSm.getAcceptObject()));
        combSm.processEvent('n');
        assertTrue(combSm.accepts());
        assertTrue("sm2".equals(combSm.getAcceptObject()));
        combSm.processEvent('g');
        assertTrue(combSm.accepts());
        assertTrue("sm1".equals(combSm.getAcceptObject()));
        combSm.processEvent('x');
        assertTrue(combSm.accepts());
        assertTrue("sm2".equals(combSm.getAcceptObject()));
    }

    @Test
    public void testDate() throws RegexException {
        String regex = "([0-9][0-9][0-9][0-9]).((0[1-9])|(1[0-2])).((0[1-9])|([1-2][0-9])|(3[0-1]))";
        assertTrue(Regex.match(regex, "0000.01.01"));
        assertTrue(Regex.match(regex, "0001.01.01"));
        assertTrue(Regex.match(regex, "2000.01.01"));
        assertTrue(Regex.match(regex, "2000.12.31"));
        assertFalse(Regex.match(regex, "2000.01.00"));
        assertFalse(Regex.match(regex, "2000.00.01"));
        assertFalse(Regex.match(regex, "2000.13.01"));
        assertFalse(Regex.match(regex, "2000.12.32"));
    }

    @Test
    public void testUnicodeChars() throws RegexException {
        String regex = "[\u0030-\u0039]+";
        assertTrue(Regex.match(regex, "0123456789"));
    }

    @Test
    public void testComplement() throws RegexException {
        String regex = "\"[^\"]+\"";
        assertTrue(Regex.match(regex, "\"abcdef\""));
    }

}
