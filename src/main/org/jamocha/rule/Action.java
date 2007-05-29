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
package org.jamocha.rule;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.ExecuteException;


/**
 * @author Peter Lin
 *
 * Actions constitute the Right-hand side of a rule. In RULEML terms,
 * the Right-hand side is the head. The Action interface is meant to
 * define the methods necessary to execute rules which match fully.
 * An action will typically contain several functions. It's the job
 * of the action to contain the functions and make it easier to
 * execute.
 * Part of the responsibility of the action is to know how to get
 * the a fact or binding.
 */
public interface Action extends Serializable {
    /**
     * The purpose of configure is to setup the action when the rule
     * is loaded to the rule engine. When the parser parses the rule,
     * it may not lookup the functions. That will be the common case,
     * which means when the rule is added to the rule engine, the
     * lookup needs to occur.
     */
    void configure(Rete engine, Rule util) throws EvaluationException;
    /**
     * When an action is executed, we pass the facts and the Rete
     * instance.
     * @param engine
     * @param facts
     * @throws ExecuteException
     */
    JamochaValue executeAction(Rete engine, Fact[] facts) throws ExecuteException;
}
