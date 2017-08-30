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

/**
 * A node which represents a choice in a syntax tree.
 */
public class RegexSelection extends RegexNode {

    public RegexSelection(RegexNode node) {
        nodes.add(node);
    }

    public void add(RegexNode node) {
        nodes.add(node);
    }

    @Override
    public void doPrint() {
        boolean first = true;
        System.out.print("(");
        for (RegexNode node : nodes) {
            if (first) {
                first = false;
            } else {
                System.out.print("|");
            }
            node.doPrint();
        }
        System.out.print(")");
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        boolean first = true;
        for (RegexNode node : nodes) {
            if (first) {
                first = false;
            } else {
                sb.append("|");
            }
            sb.append(node.toString());
        }
        sb.append(")");
        return sb.toString();
    }

}
