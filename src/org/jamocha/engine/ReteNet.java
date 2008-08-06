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

package org.jamocha.engine;

import java.io.Serializable;
import java.util.List;

import org.jamocha.engine.nodes.NodeException;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.rules.rulecompiler.CompileRuleException;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryImpl;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.ParserFactory;
import org.jamocha.parser.RuleException;
import org.jamocha.rules.Rule;
import org.jamocha.settings.JamochaSettings;
import org.jamocha.settings.SettingsChangedListener;
import org.jamocha.settings.SettingsConstants;

public class ReteNet implements SettingsChangedListener, Serializable {

	private static final long serialVersionUID = 1L;

	protected Engine engine = null;

	protected RootNode root = null;

	protected RuleCompiler compiler = null;

	protected WorkingMemory workingMemory;

	private int lastNodeId = 0;

	private boolean shareNodes = true;

	private final String[] interestedProperties = { SettingsConstants.ENGINE_NET_SETTINGS_SHARE_NODES };

	/**
	 * 
	 */
	public ReteNet(Engine engine) {
		super();
		this.engine = engine;

		/*
		 * for now, choose the implementation is hard coded, since there is only
		 * this one.
		 */
		workingMemory = WorkingMemoryImpl.getWorkingMemory();

		root = new RootNode(nextNodeId(), workingMemory, this);
		compiler = ParserFactory.getRuleCompiler(engine, this, root);
		JamochaSettings.getInstance().addListener(this, interestedProperties);
	}

	public synchronized void assertObject(Fact fact) throws AssertException {
		// we assume Rete has already checked to see if the object
		// has been added to the working memory, so we just assert.
		try {
			root.addWME(null,fact);
		} catch (NodeException e) {
			throw new AssertException(e);
		}
	}

	/**
	 * Retract an object from the Rete-Net
	 * 
	 * @param objInstance
	 */
	public synchronized void retractObject(Fact fact) throws RetractException {
		try {
			root.removeWME(null, fact);
		} catch (NodeException e) {
			throw new RetractException(e);
		}
	}

	public boolean addRule(Rule rule) throws EvaluationException, RuleException, CompileRuleException {
		boolean result = compiler.addRule(rule);
		shareNodes(rule);
		return result;
	}

	public void clear() {
		lastNodeId = 1;
		try {
			root.flush();
		} catch (NodeException e) {
			engine.writeMessage("error while flushing rete network");
		}
	}

	public void addTemplate(Template template) {
		compiler.addObjectTypeNode(template);
	}

	/**
	 * return the next rete node id for a new node
	 * 
	 * @return
	 */
	public int nextNodeId() {
		return ++lastNodeId;
	}

	public RootNode getRoot() {
		return root;
	}

	public Engine getEngine() {
		return engine;
	}

	private int shareNodes(Rule rule) {
		// TODO: re-implement it
		// first implementation:
		// if (this.shareNodes) {
		// int result = root.shareNodes(rule, this);
		// root.propagateEndMerging();
		// return result;
		// } else
		return 0;
	}

	private int shareAllNodes() {
		int result = 0;
		List<Rule> rules = engine.getModules().getAllRules();
		for (Rule rule : rules)
			result += shareNodes(rule);
		return result;
	}

	private void setShareNodes(boolean newValue) {
		boolean oldValue = shareNodes;
		shareNodes = newValue;

		if (!oldValue && newValue)
			shareAllNodes();
	}

	public void settingsChanged(String propertyName) {
		JamochaSettings settings = JamochaSettings.getInstance();
		// share nodes
		if (propertyName
				.equals(SettingsConstants.ENGINE_NET_SETTINGS_SHARE_NODES))
			setShareNodes(settings.getBoolean(propertyName));

	}

	public WorkingMemory getWorkingMemory() {
		return workingMemory;
	}

}