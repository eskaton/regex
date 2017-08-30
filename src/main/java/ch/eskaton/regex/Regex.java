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

import ch.eskaton.yajpg.api.Parser;
import ch.eskaton.yajpg.api.ParserFactory;
import ch.eskaton.regex.fsm.RegexStateMachine;
import ch.eskaton.regex.parser.RegexCompiler;
import ch.eskaton.regex.parser.RegexLexer;
import ch.eskaton.regex.parser.RegexNode;

/**
 * A class to evaluate regular expressions.
 * <p>
 * 
 * <h4>Supported meta characters</h4>
 * <ul>
 * <li>? - 0..1 repetition</li>
 * <li>&#042; - 0..n repetition</li>
 * <li>+ - 1..n repetition</li>
 * <li>() - group</li>
 * <li>| - selection</li>
 * </ul>
 * 
 * <p>
 * 
 * <h4>Character classes</h4> 
 * Charachters can be combined to classes in square brackets. The meta characters
 * "?*+()|" lose their meaning, i.e. they're part of the pattern. Only "-" and "]"
 * must be escaped with a backslash.
 * 
 * <h4>Examples</h4>
 * <ul>
 * <li>[\u0030-\u0039]+ accepts all numeric strings like 0123456789</li>
 * <li>0x[0-9a-fA-F]+ accepts hexadecimal numbers like 0xFF</li>
 * <li>[*]* accepts 0 to n asterisks</li>
 * <li>([a-zA-Z]+|[0-9]+) accepts 1 to n letters or digits</li>
 * </ul>
 * 
 * <h4>Usage</h4>
 * 
 * There are two possibilities to use the class. If a pattern is to be used 
 * multiple times, the class can be instantiated, to reuse the compiled pattern:
 * 
 * <pre>
 * Regex regex = new Regex(&quot;0x[0-9a-fA-F]+&quot;);
 * if( regex.match(&quot;0x7A4E&quot;) ) {
 *     ...
 * } else if(regex.match(&quot;0x9DEF&quot;)) {
 *     ...
 * }
 * </pre>
 * 
 * For one time use there is a class method:
 * 
 * <pre>
 * if( Regex.match(&quot;0x[0-9a-fA-F]+&quot;, &quot;0xAB12&quot; ) ) {
 *     ...
 * }
 * </pre>
 */
public class Regex {

	private static final String PARSER_CLASS = "ch.eskaton.regex.parser.RegexParser";

	private RegexStateMachine rsm;

	public Regex(String regex) throws RegexException {
		try {
			Parser p = ParserFactory
					.create(PARSER_CLASS, new RegexLexer(regex));
			RegexNode s = (RegexNode) p.parse();
			rsm = new RegexCompiler().compile(s);
		} catch (Exception ex) {
			throw new RegexException(ex);
		}
	}

	public boolean match(String str) {
		rsm.reset();

		for (int i = 0; i < str.length(); i++) {
			rsm.processEvent(str.charAt(i));
		}

		return rsm.accepts();
	}

	public static boolean match(String regex, String str) throws RegexException {
		try {
			Parser p = ParserFactory
					.create(PARSER_CLASS, new RegexLexer(regex));
			RegexNode s = (RegexNode) p.parse();
			RegexStateMachine rsm = new RegexCompiler().compile(s);

			for (int i = 0; i < str.length(); i++) {
				rsm.processEvent(str.charAt(i));
			}

			return rsm.accepts();
		} catch (Exception ex) {
			throw new RegexException(ex);
		}
	}

}
