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

/**
 * 
 */
package org.jamocha.engine.rules.rulecompiler.beffy;

import java.util.List;

import org.jamocha.engine.rules.rulecompiler.CompileRuleException;
import org.jamocha.rules.Condition;

/**
 * The rule optimizer converts any valid condition tree
 * into a optimized form. Which can be understood by the
 * rule compiler. You find a documentation of the
 * requirements this tree has to meet in the documentation
 * of BeffyRuleCompiler.
 * 
 * The rule optimizer consists of three passes:
 * 
 * @see BeffyRuleOptimizerPassOne
 * @see BeffyRuleOptimizerPassTwo
 * TODO Pass three still missing
 * 
 * @author Christoph Terwelp
 * @author Janno von Stuelpnagel
 * 
 * @see BeffyRuleCompiler
 */
public class BeffyRuleOptimizer {
	
	BeffyRuleOptimizerPassZero passzero;
	BeffyRuleOptimizerPassOne passone;
	BeffyRuleOptimizerPassTwo passtwo;
	BeffyRuleOptimizerPassThree passthree;
	
	public BeffyRuleOptimizer() {
		passzero = new BeffyRuleOptimizerPassZero();
		passone = new BeffyRuleOptimizerPassOne();
		passtwo = new BeffyRuleOptimizerPassTwo();
		passthree = new BeffyRuleOptimizerPassThree();
	}
	
	public Condition optimize(List<Condition> cons) throws OptimizeRuleException {
		Condition con;
		cons = passzero.optimize(cons);
		con = passone.optimize(cons);
		con = passtwo.optimize(con);
		con = passthree.optimize(con);
		return con;
	}

}
