package org.jamocha.rete.nodes;

import java.io.Serializable;

import org.jamocha.parser.ParserFactory;
import org.jamocha.parser.RuleException;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.Template;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rule.Rule;

public class ReteNet implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Rete engine = null;

	protected RootNode root = null;

	protected RuleCompiler compiler = null;

	private int lastNodeId = 0;

	/**
	 * 
	 */
	public ReteNet(Rete engine) {
		super();
		this.engine = engine;
		this.root = new RootNode(nextNodeId());
		this.compiler = ParserFactory.getRuleCompiler(engine, this, this.root);
		this.compiler.addListener(engine);
	}

	public synchronized void assertObject(Fact fact) throws AssertException {
		// we assume Rete has already checked to see if the object
		// has been added to the working memory, so we just assert.
		// we need to lookup the defclass and deftemplate to assert
		// the object to the network
		this.root.assertObject(fact, this);
	}

	/**
	 * Retract an object from the Rete-Net
	 * 
	 * @param objInstance
	 */
	public synchronized void retractObject(Fact fact) throws RetractException {
		this.root.retractObject(fact, this);
	}

	public boolean addRule(Rule rule) throws AssertException, RuleException {
		return this.compiler.addRule(rule);
	}

	public void clear() {
		this.lastNodeId = 1;
		this.root.clear();
	}

	public void setValidateRule(boolean val) {
		this.compiler.setValidateRule(val);

	}

	public boolean getValidateRule() {
		return this.compiler.getValidateRule();
	}

	public void addTemplate(Template template) {
		this.compiler.addObjectTypeNode(template);
	}

	/**
	 * return the next rete node id for a new node
	 * 
	 * @return
	 */
	public int nextNodeId() {
		return ++this.lastNodeId;
	}

	public RootNode getRoot() {
		return this.root;
	}

	public Rete getEngine() {
		return this.engine;
	}

	public int shareNodes(Rule rule) {
		// first implementation:
		int result = root.shareNodes(rule, this);
		root.propagateEndMerging();
		return result;
	}

}