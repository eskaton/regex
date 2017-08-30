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
import java.util.List;
import java.util.Set;
import java.util.Vector;

import ch.eskaton.regex.fsm.RegexCharacterComplementEvent;
import ch.eskaton.regex.fsm.RegexCharacterEvent;
import ch.eskaton.regex.fsm.RegexEvent;
import ch.eskaton.regex.fsm.RegexLambdaEvent;
import ch.eskaton.regex.fsm.RegexState;
import ch.eskaton.regex.fsm.RegexStateMachine;
import ch.eskaton.regex.fsm.RegexTransition;

/**
 * Compiler for regular expressions.
 * <p>
 * The compiler takes a syntax tree and creates automaton from it.
 */
public class RegexCompiler {

    /**
     * Creates the automaton.
     */
    public RegexStateMachine compile(RegexNode node) {
        return compile(node, null);
    }

    /**
     * Creates an automaton. The method takes a second argument to be connected
     * with the states of the automaton. This allows to determine the original 
     * automaton that matched a pattern if multiple automatons are combined.
     */
    public RegexStateMachine compile(RegexNode node, Object object) {
        RegexCharacterClassCollector rccc = new RegexCharacterClassCollector();
        node.visit(rccc);
        Set<RegexCharacterRange> chars = RegexCharacterRange
                .splitIntersectingRanges(rccc.getCharacterRanges());
        RegexCharacterClassSplitter rccs = new RegexCharacterClassSplitter(
                chars);
        node.visit(rccs);
        RegexStateMachineConfig config = traverse(node, object);

        for (RegexState state : config.finalStates) {
            state.setFinalState(true);
        }

        return new RegexStateMachine(config.initialState, config.finalStates);
    }

    /**
     * Traverses the syntax tree and creates the configuration for the
     * automaton.
     * 
     * @param node
     *            A node
     * @param object
     *            Object to be connected with the states of the automaton
     */
    @SuppressWarnings("serial")
    private RegexStateMachineConfig traverse(RegexNode node, Object object) {
        if (node instanceof RegexCharacterClass) {
            final RegexState initialState = new RegexState();
            final RegexState finalState = new RegexState();

            initialState.setObject(object);
            finalState.setObject(object);

            for (RegexCharacterRange r : ((RegexCharacterClass) node)
                    .getCharacterRanges()) {
                RegexTransition trans;
                if (((RegexCharacterClass) node).isInverted()) {
                    trans = new RegexTransition(finalState,
                            new RegexCharacterComplementEvent(
                                    new RegexCharacterClass(r)));
                } else {
                    trans = new RegexTransition(finalState,
                            new RegexCharacterEvent(new RegexCharacterClass(r)));
                }

                initialState.addTransition(trans);
            }

            return new RegexStateMachineConfig(initialState,
                    new HashSet<RegexState>() {
                        {
                            add(finalState);
                        }
                    });
        } else if (node instanceof RegexKleeneStar) {
            return kleeneStar(traverse(node.nodes.firstElement(), object),
                    object);
        } else if (node instanceof RegexOptional) {
            return optional(traverse(node.nodes.firstElement(), object));
        } else if (node instanceof RegexConcatenation) {
            Vector<RegexStateMachineConfig> configs = new Vector<RegexStateMachineConfig>();
            for (RegexNode n : node.nodes) {
                configs.add(traverse(n, object));
            }
            return concatenate(configs);
        } else if (node instanceof RegexSelection) {
            Vector<RegexStateMachineConfig> configs = new Vector<RegexStateMachineConfig>();
            for (RegexNode n : node.nodes) {
                configs.add(traverse(n, object));
            }
            return select(configs);
        } else {
            System.err.println("not implemented");
        }

        return null;
    }

    /**
     * Handles the Kleene star operator.
     */
    @SuppressWarnings("serial")
    private RegexStateMachineConfig kleeneStar(RegexStateMachineConfig config,
            Object object) {
        final RegexState initialState = config.initialState;
        final RegexState finalState = new RegexState();
        Set<RegexState> targetStates = config.finalStates;

        for (RegexState s : targetStates) {
            initialState.addTransition(new RegexTransition(s,
                    new RegexLambdaEvent()));
            s.addTransition(new RegexTransition(finalState,
                    new RegexLambdaEvent()));
        }

        finalState.addTransition(new RegexTransition(initialState,
                new RegexLambdaEvent()));
        finalState.setObject(object);

        return new RegexStateMachineConfig(initialState,
                new HashSet<RegexState>() {
                    {
                        add(finalState);
                    }
                });
    }

    /**
     * Handles the options operator.
     */
    public RegexStateMachineConfig optional(RegexStateMachineConfig config) {
        RegexState initialState = config.initialState;

        for (RegexState s : config.finalStates) {
            initialState.addTransition(new RegexTransition(s,
                    new RegexLambdaEvent()));
        }

        return config;
    }

    /**
     * Handles the selection operator.
     */
    public RegexStateMachineConfig select(
            Vector<RegexStateMachineConfig> configs) {
        RegexState initialState = new RegexState();
        Set<RegexState> finalStates = new HashSet<RegexState>();

        for (RegexStateMachineConfig config : configs) {
            initialState.addTransition(new RegexTransition(config.initialState,
                    new RegexLambdaEvent()));
            finalStates.addAll(config.finalStates);
        }

        return new RegexStateMachineConfig(initialState, finalStates);
    }

    /**
     * Handles the sequence operator. 
     */
    public RegexStateMachineConfig concatenate(
            Vector<RegexStateMachineConfig> configs) {
        RegexState initialState = configs.firstElement().initialState;
        Set<RegexState> finalStates = configs.firstElement().finalStates;

        for (int i = 1; i < configs.size(); i++) {
            RegexStateMachineConfig config = configs.elementAt(i);
            for (RegexState s : finalStates) {
                s.addTransition(new RegexTransition(config.initialState,
                        new RegexLambdaEvent()));
            }
            finalStates = config.finalStates;
        }

        return new RegexStateMachineConfig(initialState, finalStates);
    }

    /**
     * Combines multiple automatons to one which executes them in parallel.
     */
    public RegexStateMachine combine(List<RegexStateMachine> stateMachines) {
        if (stateMachines.size() == 0) {
            return null;
        } else if (stateMachines.size() == 1) {
            return stateMachines.get(0);
        }

        /*
         * Search for all events and split them, so that no character ranges
         * overlap
         */
        Set<RegexCharacterRange> ranges = new HashSet<RegexCharacterRange>();

        for (RegexStateMachine machine : stateMachines) {
            for (RegexEvent event : machine.getEvents()) {
                if (event instanceof RegexCharacterEvent) {
                    RegexCharacterClass chrClass = ((RegexCharacterEvent) event)
                            .getCharClass();
                    ranges.addAll(chrClass.getCharacterRanges());
                }
            }
        }

        Set<RegexCharacterRange> splittedRanges = RegexCharacterRange
                .splitIntersectingRanges(ranges);

        for (RegexStateMachine machine : stateMachines) {
            for (RegexState state : machine.getStates()) {
                Set<RegexTransition> transitionsToAdd = new HashSet<RegexTransition>();
                Set<RegexTransition> transitionsToRemove = new HashSet<RegexTransition>();

                for (RegexTransition trans : state.getTransitions()) {
                    Set<RegexCharacterRange> newRanges = new HashSet<RegexCharacterRange>();
                    RegexEvent event = trans.getEvent();
                    if (event instanceof RegexCharacterEvent) {
                        RegexCharacterClass chrClass = ((RegexCharacterEvent) event)
                                .getCharClass();
                        for (RegexCharacterRange range : chrClass
                                .getCharacterRanges()) {
                            newRanges.addAll(range
                                    .getAllIntersectingRanges(splittedRanges));
                        }

                        if (newRanges.size() > 0
                                && !((RegexCharacterEvent) event)
                                        .getCharClass().getCharacterRanges()
                                        .equals(newRanges)) {
                            transitionsToRemove.add(trans);
                            for (RegexCharacterRange range : newRanges) {
                                transitionsToAdd
                                        .add(new RegexTransition(
                                                trans.getTargetState(),
                                                new RegexCharacterEvent(
                                                        new RegexCharacterClass(
                                                                range))));
                            }
                        }
                    }
                }

                state.removeTransitions(transitionsToRemove);
                state.addTransitions(transitionsToAdd);
            }
        }

        Vector<RegexStateMachineConfig> configs = new Vector<RegexStateMachineConfig>();

        for (RegexStateMachine machine : stateMachines) {
            configs.add(new RegexStateMachineConfig(machine.getInitialState(),
                    machine.getFinalStates()));
        }

        RegexStateMachineConfig newConfig = select(configs);
        return new RegexStateMachine(newConfig.initialState,
                newConfig.finalStates);
    }

    private static class RegexStateMachineConfig {
        RegexState initialState;

        Set<RegexState> finalStates;

        public RegexStateMachineConfig(RegexState initialState,
                Set<RegexState> finalStates) {
            this.initialState = initialState;
            this.finalStates = finalStates;
        }
    }

}
