/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import java.io.Serializable;

import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rule.Rule;


/**
 * @author Peter Lin
 *
 * Basic interface for activation. There may be more than one implementation
 * of Activation. The important thing about the activation is it knows which
 * facts trigger a single rule.
 */
public interface Activation extends Serializable {
    /**
     * The aggregate time is the sum of the Fact timestamps
     * @return
     */
    long getAggregateTime();
    /**
     * Get the Facts that triggered the rule
     * @return
     */
    Fact[] getFacts();
    /**
     * Get the Index for the Facts
     * @return
     */
    Index getIndex();
    /**
     * Get the rule that should fire
     * @return
     */
    Rule getRule();
    /**
     * The nanosecond timestamp for the activation
     * @return
     */
    long getTimeStamp();
    /**
     * If the activation passed in the parameter has the same rule
     * and facts, the method should return true
     * @param act
     * @return
     */
    boolean compare(Activation act);
    /**
     * Execute the right-hand side (aka actions) of the rule.
     * @param engine
     * @throws ExecuteException
     */
    void executeActivation(Rete engine) throws ExecuteException;
    /**
     * When watch activation is turned on, we use the method to print out
     * the activation.
     * @return
     */
    String toPPString();
    /**
     * after the activation is executed, clear has to be called.
     */
    void clear();
}
