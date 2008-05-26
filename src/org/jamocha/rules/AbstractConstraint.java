/*
 * Copyright 2007 Sebastian Reinartz, Alexander Wilden
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

import org.jamocha.engine.AssertException;
import org.jamocha.engine.StopCompileException;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;
import org.jamocha.formatter.Formatter;


public abstract class AbstractConstraint implements Constraint {

	protected Condition parent;
	
	public int getComplexity() {
		return 1;
	}
	
	public void setParentCondition(Condition p) {
		parent = p;
	}
	
	public Condition getParentCondition() {
		return parent;
	}
}
