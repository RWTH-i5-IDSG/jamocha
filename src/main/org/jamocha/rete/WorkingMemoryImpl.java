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

import org.jamocha.parser.ParserFactory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.nodes.RootNode;

/**
 * @author Peter Lin
 * 
 * WorkingMemoryImpl is a basic implementation of the WorkingMemory interface. A
 * couple of important things about ava - Code Style - Code Templates
 */
public class WorkingMemoryImpl implements WorkingMemory {

	private static final long serialVersionUID = 1L;

	protected Rete engine = null;

	protected RootNode root = null;

	protected RuleCompiler compiler = null;

	/**
	 * 
	 */
	public WorkingMemoryImpl(Rete engine) {
		super();
		this.engine = engine;
		this.root = new RootNode(engine.nextNodeId());
		this.compiler = ParserFactory.getRuleCompiler(engine, this, this.root);
		this.compiler.addListener(engine);
	}

	/**
	 * Return the rootnode of the RETE network
	 * 
	 * @return
	 */
	public RootNode getRootNode() {
		return this.root;
	}

	public synchronized void assertObject(Fact fact) throws AssertException {
		// we assume Rete has already checked to see if the object
		// has been added to the working memory, so we just assert.
		// we need to lookup the defclass and deftemplate to assert
		// the object to the network
		this.root.assertObject(fact, engine);
	}

	/**
	 * Retract an object from the Working memory
	 * 
	 * @param objInstance
	 */
	public synchronized void retractObject(Fact fact) throws RetractException {
		this.root.retractObject(fact, engine);
	}

	/**
	 * The implementation returns the default RuleCompiler
	 */
	public RuleCompiler getRuleCompiler() {
		return compiler;
	}

	public synchronized void clear() {
		this.root.clear();
	}
}
