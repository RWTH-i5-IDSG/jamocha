/*
 * Copyright 2002-2008 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jamocha.rules;

import org.jamocha.engine.ExecuteException;
import org.jamocha.engine.nodes.FactTuple;
import org.jamocha.formatter.Formattable;
import org.jamocha.parser.JamochaValue;


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
public interface Action extends Formattable {
    
    /**
     * When an action is executed, we pass the facts and the Rete
     * instance.
     * @param engine
     * @param facts
     * @throws ExecuteException
     */
    JamochaValue executeAction(FactTuple facts) throws ExecuteException;
    
}
