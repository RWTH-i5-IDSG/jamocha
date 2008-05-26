/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

package org.jamocha.engine.configurations;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rules.Condition;
import org.jamocha.engine.ExpressionSequence;
import org.jamocha.engine.Engine;

public class DefruleConfiguration extends AbstractConfiguration {

	private String ruleName = null;

	private String ruleDescription = null;

	private DeclarationConfiguration declarationConfiguration = null;

	private Condition[] conditions = null;

	private ExpressionSequence actions = null;

	private int totalComplexity = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rete.Parameter#isObjectBinding()
	 */
	public boolean isObjectBinding() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.parser.Expression#getExpressionString()
	 */
	public String getExpressionString() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.parser.Expression#getValue(org.jamocha.rete.Rete)
	 */
	public JamochaValue getValue(Engine engine) throws EvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionSequence getActions() {
		return actions;
	}

	public void setActions(ExpressionSequence actions) {
		this.actions = actions;
	}

	public DeclarationConfiguration getDeclarationConfiguration() {
		return declarationConfiguration;
	}

	public void setDeclarationConfiguration(
			DeclarationConfiguration declarationConfiguration) {
		this.declarationConfiguration = declarationConfiguration;
	}

	public Condition[] getConditions() {
		return conditions;
	}

	public void seConditions(Condition[] conditions) {
		this.conditions = conditions;
	}

	public String getRuleDescription() {
		return ruleDescription;
	}

	public void setRuleDescription(String ruleDescription) {
		this.ruleDescription = ruleDescription;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public int getTotalComplexity() {
		return totalComplexity;
	}

	public void setTotalComplexity(int totalComplexity) {
		this.totalComplexity = totalComplexity;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}
}
