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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import ch.eskaton.regex.parser.RegexCharacterClass;

public class RegexStateMachine {

    private RegexState initialState;

    private Set<RegexState> finalStates;

    private int currentState;

    private int[][] stateTable;

    /** Maps each state to an index in the state table */
    private Map<RegexState, Integer> stateMap;

    /** Maps each event to an index in the state table */
    private Map<RegexEvent, Integer> eventMap;

    private Set<RegexState> allStates;

    private Set<RegexEvent> allEvents;

    private Map<Integer, RegexState> finalStatesMap;

    private boolean debugMode = false;

    public RegexStateMachine(RegexState initialState,
            Set<RegexState> finalStates) {
        super();
        this.initialState = initialState;
        this.finalStates = finalStates;
        initialise();
    }

    private void initialise() {
        eliminateLambdaTransitions();
        makeDeterministic();
        // here the automaton should be minimised
        buildTables();
        reset();
    }

    public RegexState getInitialState() {
        return initialState;
    }

    public Set<RegexState> getFinalStates() {
        return finalStates;
    }

    public Set<RegexState> getStates() {
        return allStates;
    }

    public Set<RegexEvent> getEvents() {
        return allEvents;
    }

    public int[][] getStateTable() {
        return stateTable.clone();
    }

    public int getStateNumber(RegexState state) {
        return stateMap.get(state);
    }

    public int getEventNumber(RegexEvent event) {
        return eventMap.get(event);
    }

    private void buildTables() {
        allStates = new HashSet<RegexState>();
        allEvents = new HashSet<RegexEvent>();
        finalStatesMap = new HashMap<Integer, RegexState>();
        collectStatesAndEvents(initialState, allStates, allEvents);
        Set<RegexCharacterClass> chars = new HashSet<RegexCharacterClass>();

        for (RegexEvent event : allEvents) {
            if (event instanceof RegexCharacterEvent) {
                chars.add(((RegexCharacterEvent) event).getCharClass());
            }
        }

        stateMap = new HashMap<RegexState, Integer>();
        eventMap = new HashMap<RegexEvent, Integer>();

        int s = 0, e = 0;
        Set<RegexEvent> duplicateEvents = new HashSet<RegexEvent>();

        for (RegexEvent event : allEvents) {
            if (event instanceof RegexCharacterComplementEvent
                    && chars.contains(((RegexCharacterComplementEvent) event)
                            .getCharClass())) {
                duplicateEvents.add(event);
            } else {
                eventMap.put(event, e++);
            }
        }

        eventMap.put(new RegexOtherEvent(), e++);
        allEvents.removeAll(duplicateEvents);

        for (RegexState state : allStates) {
            stateMap.put(state, s);
            if (finalStates.contains(state)) {
                finalStatesMap.put(s, state);
            }
            s++;
        }

        int rows = allStates.size();
        int cols = allEvents.size() + 1; // including "other-event"

        stateTable = new int[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                stateTable[r][c] = -1;
            }
        }

        for (RegexState state : allStates) {
            int r = stateMap.get(state);
            boolean complement = false;
            for (RegexTransition trans : state.getTransitions()) {
                RegexEvent evt = trans.getEvent();
                if (evt instanceof RegexCharacterComplementEvent) {
                    if (!complement) {
                        complement = true;
                        int targetState = stateMap.get(trans.getTargetState());

                        for (int i = 0; i < eventMap.size(); i++) {
                            if (stateTable[r][i] == -1) {
                                stateTable[r][i] = targetState;
                            }
                        }
                    }

                } else if (evt instanceof RegexCharacterEvent) {
                    int c = eventMap.get(evt);
                    int targetState = stateMap.get(trans.getTargetState());
                    stateTable[r][c] = targetState;
                }
            }
        }

    }

    private void collectStatesAndEvents(RegexState currentState,
            Set<RegexState> allStates, Set<RegexEvent> allEvents) {

        if (allStates.contains(currentState)) {
            return;
        }

        allStates.add(currentState);

        for (RegexTransition t : currentState.getTransitions()) {
            allEvents.add(t.getEvent());
            collectStatesAndEvents(t.getTargetState(), allStates, allEvents);
        }
    }

    private void makeDeterministic() {
        Map<String, RegexCompoundState> newStates = new HashMap<String, RegexCompoundState>();
        Stack<RegexCompoundState> stack = new Stack<RegexCompoundState>();

        RegexCompoundState newInitialState = new RegexCompoundState(
                initialState);

        stack.add(newInitialState);
        newStates.put(newInitialState.getComboundId(), newInitialState);

        while (!stack.isEmpty()) {
            RegexCompoundState state = stack.pop();
            Set<RegexEvent> events = state.getAcceptedEvents();
            for (RegexEvent evt : events) {
                Set<RegexState> cmpStates = state.getAcceptingStates(evt);

                if (cmpStates.size() > 0) {
                    RegexCompoundState cmpState = new RegexCompoundState(
                            cmpStates);
                    if (newStates.containsKey(cmpState.getComboundId())) {
                        cmpState = newStates.get(cmpState.getComboundId());
                        RegexTransition t = new RegexTransition(cmpState, evt);
                        state.addTransition(t);
                    } else {
                        RegexTransition t = new RegexTransition(cmpState, evt);
                        state.addTransition(t);
                        stack.push(cmpState);
                        newStates.put(cmpState.getComboundId(), cmpState);
                    }
                }
            }
        }

        Set<RegexState> newFinalStates = new HashSet<RegexState>();

        for (Map.Entry<String, RegexCompoundState> entry : newStates.entrySet()) {
            RegexCompoundState cs = entry.getValue();
            for (RegexState s : cs.getStates()) {
                if (finalStates.contains(s)) {
                    RegexState finalState = cs;
                    finalState.setFinalState(true);
                    newFinalStates.add(finalState);
                    break;
                }
            }
        }

        initialState = newInitialState;
        finalStates = newFinalStates;
    }

    private void eliminateLambdaTransitions() {
        Stack<RegexState> states = new Stack<RegexState>();
        Set<RegexState> handledStates = new HashSet<RegexState>();
        Set<RegexState> nonLambdaStates = new HashSet<RegexState>();

        final RegexState finalState = new RegexState();
        finalState.setFinalState(true);

        for (RegexState f : finalStates) {
            f.addTransition(new RegexTransition(finalState,
                    new RegexLambdaEvent()));
        }

        finalStates.clear();

        /*
         * For each state search possible direct transitions and add them to the state.
         * Determine the target states.
         */
        states.push(initialState);
        nonLambdaStates.add(initialState);

        while (!states.isEmpty()) {
            Stack<RegexState> path = new Stack<RegexState>();
            Set<RegexTransition> directTransitions = new HashSet<RegexTransition>();
            RegexState currentState = states.pop();
            handledStates.add(currentState);
            searchDirectTransitions(currentState, path, directTransitions);
            currentState.addTransitions(directTransitions);

            if (nonLambdaStates.contains(currentState)) {
                if (canReachFinalState(currentState, path, finalState)) {
                    currentState.setFinalState(true);
                    finalStates.add(currentState);
                }
            }

            for (RegexTransition t : directTransitions) {
                if (!handledStates.contains(t.getTargetState())) {
                    if (!(t.getEvent() instanceof RegexLambdaEvent)) {
                        nonLambdaStates.add(t.getTargetState());
                    }
                    states.push(t.getTargetState());
                }
            }
        }

        /*
         * Delete all lambda transitions
         */
        states.push(initialState);
        handledStates.clear();

        while (!states.isEmpty()) {
            Set<RegexTransition> lambdaTransitions = new HashSet<RegexTransition>();
            RegexState currentState = states.pop();
            handledStates.add(currentState);

            for (RegexTransition t : currentState.getTransitions()) {
                if (t.getEvent() instanceof RegexLambdaEvent) {
                    lambdaTransitions.add(t);
                } else if (!handledStates.contains(t.getTargetState())) {
                    states.push(t.getTargetState());
                }
            }

            currentState.removeTransitions(lambdaTransitions);
        }

    }

    private boolean canReachFinalState(RegexState currentState,
            Stack<RegexState> path, RegexState finalState) {
        if (currentState.equals(finalState)) {
            return true;
        }

        if (path.contains(currentState)) {
            /* recognised a cycle */
            return false;
        }

        path.push(currentState);

        for (RegexTransition t : currentState.getTransitions()) {
            if (t.getEvent() instanceof RegexLambdaEvent) {
                if (canReachFinalState(t.getTargetState(), path, finalState)) {
                    path.pop();
                    return true;
                }
            }
        }

        path.pop();

        return false;
    }

    private void searchDirectTransitions(RegexState startState,
            Stack<RegexState> path, Set<RegexTransition> directTransitions) {

        if (path.contains(startState)) {
            /* recognised a cycle */
            return;
        }

        path.push(startState);

        for (RegexTransition t : startState.getTransitions()) {
            if (t.getEvent() instanceof RegexLambdaEvent) {
                searchDirectTransitions(t.getTargetState(), path,
                        directTransitions);
            } else {
                directTransitions.add(t);
            }
        }

        path.pop();
    }

    public void reset() {
        currentState = stateMap.get(initialState);
    }

    public void processEvent(char c) {
        RegexEvent event = null;

        if (currentState == -1) {
            /* in reject state */
            return;
        }

        for (RegexEvent e : allEvents) {
            if (e instanceof RegexCharacterComplementEvent) {
                ; // ignore
            } else if (e instanceof RegexCharacterEvent) {
                if (((RegexCharacterEvent) e).getCharClass().contains(c)) {
                    event = e;
                    break;
                }
            } else {
                throw new IllegalStateException(
                        "invalid event found in event table");
            }
        }

        if (event == null) {
            // try the complement
            currentState = stateTable[currentState][eventMap.size() - 1];
            if (debugMode) {
                System.out.println("Event: other");
            }
        } else {
            currentState = stateTable[currentState][eventMap.get(event)];
            if (debugMode) {
                System.out.println("Event: " + event);
            }
        }

        if (debugMode) {
            System.out.println("New state: " + currentState);
        }

    }

    public boolean rejects() {
        return currentState == -1;
    }

    public boolean accepts() {
        if (currentState == -1) {
            return false;
        }

        if (finalStatesMap.containsKey(currentState)) {
            return true;
        }

        return false;
    }

    public Object getAcceptObject() {
        if (currentState == -1) {
            return false;
        }

        return finalStatesMap.get(currentState).getObject();
    }

    public void setDebugging(boolean debug) {
        debugMode = debug;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[RegexStateMachine\n");

        for (RegexState s : allStates) {
            sb.append(s).append("\n");
        }

        sb.append("]\n");

        return sb.toString();
    }

}
