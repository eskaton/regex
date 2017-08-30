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

import ch.eskaton.yajpg.api.ParseException;
import ch.eskaton.yajpg.api.Token;

/**
 * A character class is a node in a regex syntax tree which contains one
 * or more character ranges.
 * <p>
 * All charachters, whether it's a single charachter or a class like [a-z]
 * are converted to a charachter class.
 */
public class RegexCharacterClass extends RegexNode {

    private boolean inverted = false;

    public Set<RegexCharacterRange> chars = new HashSet<RegexCharacterRange>();

    public RegexCharacterClass(Token t) {
        chars.add(new RegexCharacterRange(t.getBuffer(), t.getBuffer()));
    }

    public RegexCharacterClass(Token t1, Token t2) {
        chars.add(new RegexCharacterRange(t1.getBuffer(), t2.getBuffer()));
    }

    public RegexCharacterClass(RegexCharacterRange range) {
        chars.add(range);
    }

    @Override
    public void add(RegexNode node) throws ParseException {
        if (!(node instanceof RegexCharacterClass)) {
            throw new ParseException("character-class-node expected");
        }
        chars.addAll(((RegexCharacterClass) node).chars);
    }

    public Set<RegexCharacterRange> getCharacterRanges() {
        return chars;
    }

    public void setCharacterRanges(Set<RegexCharacterRange> chars) {
        this.chars = chars;
    }

    public boolean contains(char c) {
        RegexCharacterRange cr = new RegexCharacterRange(String.valueOf(c),
                String.valueOf(c));
        for (RegexCharacterRange r : chars) {
            if (r.intersects(cr)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void doPrint() {
        System.out.print(toString());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (RegexCharacterRange chr : chars) {
            sb.append(chr);
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((chars == null) ? 0 : chars.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final RegexCharacterClass other = (RegexCharacterClass) obj;
        if (chars == null) {
            if (other.chars != null)
                return false;
        } else if (!chars.equals(other.chars))
            return false;
        return true;
    }

    public void invert() {
        inverted = true;
    }

    public boolean isInverted() {
        return inverted;
    }

}
