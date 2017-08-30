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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import ch.eskaton.regex.parser.RegexCharacterRange;

public class RegexCharacterRangeTest {

    @SuppressWarnings("serial")
    @Test
    public void test1() {
        RegexCharacterRange r1 = new RegexCharacterRange("0", "0");
        RegexCharacterRange r2 = new RegexCharacterRange("0", "0");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("0", "0"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(0, r1.compareTo(r2));
        assertEquals(0, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test2() {
        RegexCharacterRange r1 = new RegexCharacterRange("0", "9");
        RegexCharacterRange r2 = new RegexCharacterRange("0", "9");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("0", "9"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(0, r1.compareTo(r2));
        assertEquals(0, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test3() {
        RegexCharacterRange r1 = new RegexCharacterRange("0", "9");
        RegexCharacterRange r2 = new RegexCharacterRange("1", "9");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("0", "0"));
                add(new RegexCharacterRange("1", "9"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(-1, r1.compareTo(r2));
        assertEquals(1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test4() {
        RegexCharacterRange r1 = new RegexCharacterRange("0", "9");
        RegexCharacterRange r2 = new RegexCharacterRange("1", "8");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("0", "0"));
                add(new RegexCharacterRange("1", "8"));
                add(new RegexCharacterRange("9", "9"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(-1, r1.compareTo(r2));
        assertEquals(1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test5() {
        RegexCharacterRange r1 = new RegexCharacterRange("1", "9");
        RegexCharacterRange r2 = new RegexCharacterRange("1", "8");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("1", "8"));
                add(new RegexCharacterRange("9", "9"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test6() {
        RegexCharacterRange r1 = new RegexCharacterRange("2", "9");
        RegexCharacterRange r2 = new RegexCharacterRange("1", "8");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("1", "1"));
                add(new RegexCharacterRange("2", "8"));
                add(new RegexCharacterRange("9", "9"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test7() {
        RegexCharacterRange r1 = new RegexCharacterRange("2", "8");
        RegexCharacterRange r2 = new RegexCharacterRange("1", "8");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("1", "1"));
                add(new RegexCharacterRange("2", "8"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test8() {
        RegexCharacterRange r1 = new RegexCharacterRange("2", "7");
        RegexCharacterRange r2 = new RegexCharacterRange("1", "8");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("1", "1"));
                add(new RegexCharacterRange("2", "7"));
                add(new RegexCharacterRange("8", "8"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test9() {
        RegexCharacterRange r1 = new RegexCharacterRange("1", "9");
        RegexCharacterRange r2 = new RegexCharacterRange("0", "9");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("0", "0"));
                add(new RegexCharacterRange("1", "9"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test10() {
        RegexCharacterRange r1 = new RegexCharacterRange("1", "8");
        RegexCharacterRange r2 = new RegexCharacterRange("0", "9");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("0", "0"));
                add(new RegexCharacterRange("1", "8"));
                add(new RegexCharacterRange("9", "9"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test11() {

        RegexCharacterRange r1 = new RegexCharacterRange("1", "8");
        RegexCharacterRange r2 = new RegexCharacterRange("1", "9");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("1", "8"));
                add(new RegexCharacterRange("9", "9"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(-1, r1.compareTo(r2));
        assertEquals(1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test12() {
        RegexCharacterRange r1 = new RegexCharacterRange("1", "8");
        RegexCharacterRange r2 = new RegexCharacterRange("2", "9");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("1", "1"));
                add(new RegexCharacterRange("2", "8"));
                add(new RegexCharacterRange("9", "9"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(-1, r1.compareTo(r2));
        assertEquals(1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test13() {
        RegexCharacterRange r1 = new RegexCharacterRange("1", "8");
        RegexCharacterRange r2 = new RegexCharacterRange("2", "8");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("1", "1"));
                add(new RegexCharacterRange("2", "8"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(-1, r1.compareTo(r2));
        assertEquals(1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test14() {
        RegexCharacterRange r1 = new RegexCharacterRange("1", "8");
        RegexCharacterRange r2 = new RegexCharacterRange("2", "7");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("1", "1"));
                add(new RegexCharacterRange("2", "7"));
                add(new RegexCharacterRange("8", "8"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(-1, r1.compareTo(r2));
        assertEquals(1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test15() {
        RegexCharacterRange r1 = new RegexCharacterRange("4", "5");
        RegexCharacterRange r2 = new RegexCharacterRange("2", "7");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("2", "3"));
                add(new RegexCharacterRange("4", "5"));
                add(new RegexCharacterRange("6", "7"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test16() {
        RegexCharacterRange r1 = new RegexCharacterRange("2", "7");
        RegexCharacterRange r2 = new RegexCharacterRange("4", "5");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("2", "3"));
                add(new RegexCharacterRange("4", "5"));
                add(new RegexCharacterRange("6", "7"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(-1, r1.compareTo(r2));
        assertEquals(1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test17() {
        RegexCharacterRange r1 = new RegexCharacterRange("1", "5");
        RegexCharacterRange r2 = new RegexCharacterRange("5", "9");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("1", "4"));
                add(new RegexCharacterRange("5", "5"));
                add(new RegexCharacterRange("6", "9"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(-1, r1.compareTo(r2));
        assertEquals(1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test18() {
        RegexCharacterRange r1 = new RegexCharacterRange("5", "9");
        RegexCharacterRange r2 = new RegexCharacterRange("1", "5");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("1", "4"));
                add(new RegexCharacterRange("5", "5"));
                add(new RegexCharacterRange("6", "9"));
            }
        };

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @Test
    public void test19() {
        RegexCharacterRange r1 = new RegexCharacterRange("1", "5");
        RegexCharacterRange r2 = new RegexCharacterRange("6", "9");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>();

        assertFalse(r1.intersects(r2));
        assertFalse(r2.intersects(r1));
        assertEquals(-1, r1.compareTo(r2));
        assertEquals(1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @Test
    public void test20() {
        RegexCharacterRange r1 = new RegexCharacterRange("6", "9");
        RegexCharacterRange r2 = new RegexCharacterRange("1", "5");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>();

        assertFalse(r1.intersects(r2));
        assertFalse(r2.intersects(r1));
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));
        assertEquals(expect, r1.splitIntersectingRanges(r2));
    }

    @SuppressWarnings("serial")
    @Test
    public void test21() {
        final RegexCharacterRange r1 = new RegexCharacterRange("0", "3");
        final RegexCharacterRange r2 = new RegexCharacterRange("2", "6");
        final RegexCharacterRange r3 = new RegexCharacterRange("5", "8");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("0", "1"));
                add(new RegexCharacterRange("2", "3"));
                add(new RegexCharacterRange("4", "4"));
                add(new RegexCharacterRange("5", "6"));
                add(new RegexCharacterRange("7", "8"));
            }
        };

        assertEquals(expect, RegexCharacterRange
                .splitIntersectingRanges(new HashSet<RegexCharacterRange>() {
                    {
                        add(r1);
                        add(r2);
                        add(r3);
                    }
                }));
    }

    @SuppressWarnings("serial")
    @Test
    public void test22() {
        final RegexCharacterRange r1 = new RegexCharacterRange("0", "5");
        final RegexCharacterRange r2 = new RegexCharacterRange("5", "6");
        final RegexCharacterRange r3 = new RegexCharacterRange("4", "8");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("0", "3"));
                add(new RegexCharacterRange("4", "4"));
                add(new RegexCharacterRange("5", "5"));
                add(new RegexCharacterRange("6", "6"));
                add(new RegexCharacterRange("7", "8"));
            }
        };

        assertEquals(expect, RegexCharacterRange
                .splitIntersectingRanges(new HashSet<RegexCharacterRange>() {
                    {
                        add(r1);
                        add(r2);
                        add(r3);
                    }
                }));
    }

    @SuppressWarnings("serial")
    @Test
    public void test23() {
        final RegexCharacterRange r1 = new RegexCharacterRange("a", "z");
        final RegexCharacterRange r2 = new RegexCharacterRange("a", "f");
        final RegexCharacterRange r3 = new RegexCharacterRange("a", "b");
        final RegexCharacterRange r4 = new RegexCharacterRange("e", "f");
        final RegexCharacterRange r5 = new RegexCharacterRange("h", "m");
        final RegexCharacterRange r6 = new RegexCharacterRange("o", "z");
        final RegexCharacterRange r7 = new RegexCharacterRange("i", "x");
        Set<RegexCharacterRange> expect = new HashSet<RegexCharacterRange>() {
            {
                add(new RegexCharacterRange("a", "b"));
                add(new RegexCharacterRange("c", "d"));
                add(new RegexCharacterRange("e", "f"));
                add(new RegexCharacterRange("g", "g"));
                add(new RegexCharacterRange("h", "h"));
                add(new RegexCharacterRange("i", "m"));
                add(new RegexCharacterRange("n", "n"));
                add(new RegexCharacterRange("o", "x"));
                add(new RegexCharacterRange("y", "z"));
            }
        };

        assertEquals(expect, RegexCharacterRange
                .splitIntersectingRanges(new HashSet<RegexCharacterRange>() {
                    {
                        add(r1);
                        add(r2);
                        add(r3);
                        add(r4);
                        add(r5);
                        add(r6);
                        add(r7);
                    }
                }));
    }

}
