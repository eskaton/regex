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
package ch.eskaton.regex.fsm;

import java.util.HashSet;
import java.util.Set;

/**
 * State for a regex automaton.
 */
public class RegexState {

    private static int stateId = 1;

    private int id;

    private Set<RegexTransition> transitions;

    private Object object;

    private boolean finalState = false;

    public RegexState() {
        id = stateId++;
        transitions = new HashSet<RegexTransition>();
    }

    public int getId() {
        return id;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void addTransition(RegexTransition transition) {
        transitions.add(transition);
    };

    public void removeTransition(RegexTransition transition) {
        transitions.remove(transition);
    }

    public Set<RegexTransition> getTransitions() {
        return transitions;
    }

    public void addTransitions(Set<RegexTransition> transitionsToAdd) {
        transitions.addAll(transitionsToAdd);
    }

    public void removeTransitions(Set<RegexTransition> transitionsToRemove) {
        transitions.removeAll(transitionsToRemove);
    }

    public boolean isFinalState() {
        return finalState;
    }

    public void setFinalState(boolean finalState) {
        this.finalState = finalState;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Object obj = getObject();
        sb.append("[State: ").append(id).append(" object=").append(
                obj == null ? "null" : obj).append(" isFinal=").append(
                isFinalState()).append("\n");

        for (RegexTransition t : transitions) {
            sb.append("\t").append(t).append("\n");
        }

        sb.append("]");
        return sb.toString();
    }

}
