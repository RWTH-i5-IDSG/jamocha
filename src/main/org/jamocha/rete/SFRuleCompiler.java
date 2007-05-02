/*
 * Copyright 2007 Karl-Heinz Krempels
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
package org.jamocha.rete;

import org.jamocha.rule.Rule;

/**
 * @author charlie
 *
 */
public class SFRuleCompiler implements RuleCompiler {
	
	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

	/* (non-Javadoc)
	 * @see org.jamocha.rete.RuleCompiler#addListener(org.jamocha.rete.CompilerListener)
	 */
	public void addListener(CompilerListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.jamocha.rete.RuleCompiler#addObjectTypeNode(org.jamocha.rete.ObjectTypeNode)
	 */
	public void addObjectTypeNode(ObjectTypeNode node) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.jamocha.rete.RuleCompiler#addRule(org.jamocha.rule.Rule)
	 */
	public boolean addRule(Rule rule) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.jamocha.rete.RuleCompiler#getObjectTypeNode(org.jamocha.rete.Template)
	 */
	public ObjectTypeNode getObjectTypeNode(Template template) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jamocha.rete.RuleCompiler#getValidateRule()
	 */
	public boolean getValidateRule() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.jamocha.rete.RuleCompiler#removeListener(org.jamocha.rete.CompilerListener)
	 */
	public void removeListener(CompilerListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.jamocha.rete.RuleCompiler#removeObjectTypeNode(org.jamocha.rete.ObjectTypeNode)
	 */
	public void removeObjectTypeNode(ObjectTypeNode node) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.jamocha.rete.RuleCompiler#setValidateRule(boolean)
	 */
	public void setValidateRule(boolean validate) {
		// TODO Auto-generated method stub

	}

}
