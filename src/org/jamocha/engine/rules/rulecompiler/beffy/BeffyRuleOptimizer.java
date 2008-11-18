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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.engine.Engine;
import org.jamocha.engine.configurations.DefruleConfiguration;
import org.jamocha.engine.configurations.Signature;
import org.jamocha.engine.rules.rulecompiler.CompileRuleException;
import org.jamocha.languages.clips.parser.SFPInterpreter;
import org.jamocha.languages.clips.parser.SFPParser;
import org.jamocha.languages.clips.parser.SFPStart;
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
		con = passzero.optimize(cons);
		con = passone.optimize(con);
		con = passtwo.optimize(con);
		con = passthree.optimize(con);
		return con;
	}
	
	public static void main(String[] args) {
		Engine engine = new Engine();
        SFPParser p = new SFPParser(System.in);
        try
        {
        while (true)
                {
                        SFPStart n = p.Start();
                        if (n==null) System.exit(0);
                        n.dump(" ");
                        Signature val = null;
                        val = (Signature)n.jjtAccept(new SFPInterpreter(), null);
                        BeffyRuleOptimizer optimizer = new BeffyRuleOptimizer();
                        Condition con = optimizer.optimize(new ArrayList(Arrays.asList(((DefruleConfiguration)(val.getParameters()[0])).getConditions())));
                        con.dump();
                }
        }
        catch (Exception e)
        {
                System.err.println("ERROR: " + e.getMessage());
                e.printStackTrace();
        }
	}

}
