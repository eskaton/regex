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

public class RegexCompoundState extends RegexState {

    private Set<RegexState> states;

    private String compoundId;

    public RegexCompoundState() {
        states = new HashSet<RegexState>();
    }

    public RegexCompoundState(RegexState state) {
        states = new HashSet<RegexState>();
        states.add(state);
        updateCompoundId();
    }

    public RegexCompoundState(Set<RegexState> states) {
        this.states = states;
        updateCompoundId();
    }

    public void addState(RegexState state) {
        states.add(state);
        updateCompoundId();
    }

    public boolean containsState(RegexState state) {
        return states.contains(state);
    }

    public Set<RegexState> getStates() {
        return states;
    }

    public Set<RegexState> getAcceptingStates(RegexEvent evt) {
        Set<RegexState> acceptingStates = new HashSet<RegexState>();

        for (RegexState s : states) {
            for (RegexTransition t : s.getTransitions()) {
                if (t.acceptsEvent(evt)) {
                    acceptingStates.add(t.getTargetState());
                }
            }
        }

        return acceptingStates;
    }

    private void updateCompoundId() {
        StringBuilder sb = new StringBuilder();

        for (RegexState s : states) {
            if (sb.length() > 0) {
                sb.append("-");
            }
            sb.append(s.getId());
        }

        compoundId = sb.toString();
    }

    public String getComboundId() {
        return compoundId;
    }

    public Set<RegexEvent> getAcceptedEvents() {
        Set<RegexEvent> events = new HashSet<RegexEvent>();

        for (RegexState s : states) {
            for (RegexTransition t : s.getTransitions()) {
                events.add(t.getEvent());
            }
        }

        return events;
    }

    public boolean isFinalState() {
        for (RegexState s : states) {
            if (s.isFinalState()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the object of this state. If multiple objects are available,
     * the object of the state with no more transitions is returned. This
     * is necessary to recognise an exact match.
     */
    @Override
    public Object getObject() {
        Object object = null;

        for (RegexState s : states) {
            if (!s.isFinalState()) {
                continue;
            }
            if (object == null) {
                object = s.getObject();
            } else if (s.getTransitions().size() == 0) {
                object = s.getObject();
            }
        }

        return object;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result
                + ((compoundId == null) ? 0 : compoundId.hashCode());
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
        final RegexCompoundState other = (RegexCompoundState) obj;
        if (compoundId == null) {
            if (other.compoundId != null)
                return false;
        } else if (!compoundId.equals(other.compoundId))
            return false;
        return true;
    }

}
