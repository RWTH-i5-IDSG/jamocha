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

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 *
 * Interface defining working memory
 */
public interface WorkingMemory extends Serializable {

	void assertObject(Fact fact) throws AssertException;

	void retractObject(Fact fact) throws RetractException;

	/**
	 * Return the RuleCompiler for this working memory
	 * @return
	 */
	RuleCompiler getRuleCompiler();

	/**
	 * Clears everything in the working memory
	 *
	 */
	void clear();
}
