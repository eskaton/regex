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

public class RegexTransition {

    private static int transitionId = 1;

    private int id;

    private RegexState targetState;

    private RegexEvent event;

    public RegexTransition(RegexState targetState, RegexEvent event) {
        super();
        id = transitionId++;
        this.targetState = targetState;
        this.event = event;
    }

    public RegexEvent getEvent() {
        return event;
    }

    public void setEvent(RegexEvent event) {
        this.event = event;
    }

    public RegexState getTargetState() {
        return targetState;
    }

    public void setTargetState(RegexState targetState) {
        this.targetState = targetState;
    }

    public boolean isApplicable(char c) {
        if (event instanceof RegexCharacterEvent) {
            return ((RegexCharacterEvent) event).getCharClass().contains(c);
        }
        return false;
    }

    public boolean acceptsEvent(RegexEvent evt) {
        return event.equals(evt);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((event == null) ? 0 : event.hashCode());
        result = prime * result
                + ((targetState == null) ? 0 : targetState.hashCode());
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
        RegexTransition other = (RegexTransition) obj;
        if (event == null) {
            if (other.event != null)
                return false;
        } else if (!event.equals(other.event))
            return false;
        if (targetState == null) {
            if (other.targetState != null)
                return false;
        } else if (!targetState.equals(other.targetState))
            return false;
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Transition: id=").append(id).append(" event=")
                .append(event).append(" target=").append(targetState.getId())
                .append("]");
        return sb.toString();
    }

}
