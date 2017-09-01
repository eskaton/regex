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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A character range contains one ore more characters of a character class of
 * a regular expression.
 * <p>
 * E.g. [a-z0] contains two character ranges. One for a-z and one for 0.
 */
public class RegexCharacterRange implements Comparable<RegexCharacterRange> {

    private String chrFrom;

    private String chrTo;

    public RegexCharacterRange(String chr1, String chr2) {
        chrFrom = chr1;
        chrTo = chr2;
    }

    public String getFrom() {
        return chrFrom;
    }

    public String getTo() {
        return chrTo;
    }

    public boolean intersects(RegexCharacterRange range) {
        char f1 = chrFrom.charAt(0);
        char f2 = range.getFrom().charAt(0);
        char t1 = chrTo.charAt(0);
        char t2 = range.getTo().charAt(0);

        if (f2 >= f1 && f2 <= t1) {
            return true;
        } else if (t2 >= f1 && t2 <= t1) {
            return true;
        } else if (f1 >= f2 && f1 <= t2) {
            return true;
        } else if (t1 >= f2 && t1 <= t2) {
            return true;
        }

        return false;
    }

    /**
     * Determines all ranges that overlap with <code>allRanges</code>.
     */
    public Set<RegexCharacterRange> getAllIntersectingRanges(
            Set<RegexCharacterRange> allRanges) {
        Set<RegexCharacterRange> intersecting = new HashSet<RegexCharacterRange>();
        for (RegexCharacterRange range : allRanges) {
            if (range.intersects(this)) {
                intersecting.add(range);
            }
        }
        return intersecting;
    }

    /**
     * Determines whether <code>range</code> overlaps this object's range and 
     * returns a set of non-overlapping ranges or an empty set if they are not 
     * overlapping.
     */
    public Set<RegexCharacterRange> splitIntersectingRanges(
            RegexCharacterRange range) {
        Set<RegexCharacterRange> ranges = new HashSet<RegexCharacterRange>();
        RegexCharacterRange range1, range2;

        if (compareTo(range) == 0) {
            ranges.add(this);
            return ranges;
        } else if (compareTo(range) > 0) {
            range1 = range;
            range2 = this;
        } else {
            range1 = this;
            range2 = range;
        }

        char f1 = range1.chrFrom.charAt(0);
        char f2 = range2.getFrom().charAt(0);
        char t1 = range1.chrTo.charAt(0);
        char t2 = range2.getTo().charAt(0);

        if (f1 < f2 && t1 < f2) {
            return ranges;
        } else if (f1 < f2 && t1 < t2) {
            ranges.add(new RegexCharacterRange(String.valueOf(f1), String
                    .valueOf((char) (f2 - 1))));
            ranges.add(new RegexCharacterRange(String.valueOf(f2), String
                    .valueOf(t1)));
            ranges.add(new RegexCharacterRange(String.valueOf((char) (t1 + 1)),
                    String.valueOf(t2)));
        } else if (f1 > f2 && t1 > t2) {
            ranges.add(new RegexCharacterRange(String.valueOf(f2), String
                    .valueOf((char) (f1 - 1))));
            ranges.add(new RegexCharacterRange(String.valueOf(f1), String
                    .valueOf(t2)));
            ranges.add(new RegexCharacterRange(String.valueOf((char) (t2 + 1)),
                    String.valueOf(t1)));
        } else if (f1 < f2 && t1 > t2) {
            ranges.add(new RegexCharacterRange(String.valueOf(f1), String
                    .valueOf((char) (f2 - 1))));
            ranges.add(new RegexCharacterRange(String.valueOf(f2), String
                    .valueOf(t2)));
            ranges.add(new RegexCharacterRange(String.valueOf((char) (t2 + 1)),
                    String.valueOf(t1)));
        } else if (f1 < f2 && t1 == t2) {
            ranges.add(new RegexCharacterRange(String.valueOf(f1), String
                    .valueOf((char) (f2 - 1))));
            ranges.add(new RegexCharacterRange(String.valueOf(f2), String
                    .valueOf(t2)));
        } else if (f1 == f2 && t1 < t2) {
            ranges.add(new RegexCharacterRange(String.valueOf(f1), String
                    .valueOf(t1)));
            ranges.add(new RegexCharacterRange(String.valueOf((char) (t1 + 1)),
                    String.valueOf(t2)));
        } else {
            throw new IllegalStateException("unhandled case");
        }

        return ranges;
    }

    /**
     * Checks whether the set contains overlapping ranges and returns a new
     * set which contains non-overlapping ranges that conver the same range.
     */
    public static Set<RegexCharacterRange> splitIntersectingRanges(
            Set<RegexCharacterRange> chars) {
        Set<RegexCharacterRange> splitted = new HashSet<RegexCharacterRange>();

        ArrayList<RegexCharacterRange> ranges = new ArrayList<RegexCharacterRange>();
        ranges.addAll(chars);
        Collections.sort(ranges);

        for (int i = 0; i < ranges.size() - 1;) {
            RegexCharacterRange range1 = ranges.get(i);
            RegexCharacterRange range2 = ranges.get(i + 1);

            if (!range1.intersects(range2)) {
                i++;
                continue;
            }

            Set<RegexCharacterRange> newRanges = range1
                    .splitIntersectingRanges(range2);
            ArrayList<RegexCharacterRange> newRangesSorted = new ArrayList<RegexCharacterRange>();
            newRangesSorted.addAll(newRanges);
            Collections.sort(newRangesSorted);
            ranges.remove(i + 1);
            ranges.remove(i);
            ranges.addAll(i, newRangesSorted);
            Collections.sort(ranges);
        }

        splitted.addAll(ranges);

        return splitted;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (chrTo.equals(chrFrom)) {
            if (RegexLexer.ESCAPABLE_CHARS.contains(chrFrom)) {
                sb.append("\\");
            }
            sb.append(chrFrom);
        } else {
            sb.append(chrFrom + "-" + chrTo);
        }
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof RegexCharacterRange)) {
            return false;
        }
        if (((RegexCharacterRange) o).chrFrom.equals(chrFrom)
                && ((RegexCharacterRange) o).chrTo.equals(chrTo)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return chrFrom.hashCode() * 37 + chrTo.hashCode();
    }

    public int compareTo(RegexCharacterRange o) {
        if (chrFrom.charAt(0) == o.chrFrom.charAt(0)
                && chrTo.charAt(0) == o.chrTo.charAt(0)) {
            return 0;
        } else if (chrFrom.charAt(0) == o.chrFrom.charAt(0)) {
            if (chrTo.charAt(0) > o.chrTo.charAt(0)) {
                return 1;
            } else {
                return -1;
            }
        } else if (chrFrom.charAt(0) > o.chrFrom.charAt(0)) {
            return 1;
        } else {
            return -1;
        }
    }

}
