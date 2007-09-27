package org.jamocha.rete.nodes;

import java.io.Serializable;
import java.util.List;

import org.jamocha.parser.ParserFactory;
import org.jamocha.parser.RuleException;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.Template;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rule.Rule;
import org.jamocha.settings.JamochaSettings;
import org.jamocha.settings.SettingsChangedListener;
import org.jamocha.settings.SettingsConstants;

public class ReteNet implements SettingsChangedListener, Serializable {

	private static final long serialVersionUID = 1L;

	protected Rete engine = null;

	protected RootNode root = null;

	protected RuleCompiler compiler = null;

	private int lastNodeId = 0;

	private boolean shareNodes = true;

	private String[] interestedProperties = { SettingsConstants.ENGINE_NET_SETTINGS_SHARE_NODES };

	/**
	 * 
	 */
	public ReteNet(Rete engine) {
		super();
		this.engine = engine;
		this.root = new RootNode(nextNodeId());
		this.compiler = ParserFactory.getRuleCompiler(engine, this, this.root);
		this.compiler.addListener(engine);
		JamochaSettings.getInstance().addListener(this, interestedProperties);
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
		boolean result =compiler.addRule(rule);
		shareNodes(rule);
		return result;
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

	private int shareNodes(Rule rule) {
		// first implementation:
		if (this.shareNodes) {
			int result = root.shareNodes(rule, this);
			root.propagateEndMerging();
			return result;
		} else
			return 0;
	}
	
	private int shareAllNodes(){
		int result = 0;
		List<Rule> rules =this.engine.getModules().getAllRules();
		for (Rule rule: rules){
			result +=shareNodes(rule);
		}
		return result;
	}
	
	private void setShareNodes(boolean newValue){
		boolean oldValue =this.shareNodes;
		this.shareNodes = newValue;
		
		if (!oldValue && newValue){
			this.shareAllNodes();
		}
	}

	public void settingsChanged(String propertyName) {
		JamochaSettings settings = JamochaSettings.getInstance();
		// share nodes
		if (propertyName.equals(SettingsConstants.ENGINE_NET_SETTINGS_SHARE_NODES)) {
			setShareNodes(settings.getBoolean(propertyName));
		}

	}

}