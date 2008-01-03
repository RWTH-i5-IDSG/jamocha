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
import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.implementations.defaultimpl.WorkingMemoryImpl;
import org.jamocha.rule.Rule;
import org.jamocha.settings.JamochaSettings;
import org.jamocha.settings.SettingsChangedListener;
import org.jamocha.settings.SettingsConstants;

public class ReteNet implements SettingsChangedListener, Serializable {

	private static final long serialVersionUID = 1L;

	protected Rete engine = null;

	protected RootNode root = null;

	protected RuleCompiler compiler = null;
	
	protected WorkingMemory workingMemory;

	private int lastNodeId = 0;

	private boolean shareNodes = true;

	private String[] interestedProperties = { SettingsConstants.ENGINE_NET_SETTINGS_SHARE_NODES };

	/**
	 * 
	 */
	public ReteNet(Rete engine) {
		super();
		this.engine = engine;
		
		/* for now, choose the implementation is hard coded, since
		 * there is only this one.
		 */
		this.workingMemory = WorkingMemoryImpl.getWorkingMemory();

		this.root = new RootNode(nextNodeId(), workingMemory, this );
		this.compiler = ParserFactory.getRuleCompiler(engine, this, this.root);
		this.compiler.addListener(engine);
		JamochaSettings.getInstance().addListener(this, interestedProperties);
	}

	public synchronized void assertObject(Fact fact) throws AssertException {
		// we assume Rete has already checked to see if the object
		// has been added to the working memory, so we just assert.
		try {
			this.root.addWME(fact);
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
			this.root.removeWME(fact);
		} catch (NodeException e) {
			throw new RetractException(e);
		}
	}

	public boolean addRule(Rule rule) throws AssertException, RuleException {
		boolean result =compiler.addRule(rule);
		shareNodes(rule);
		return result;
	}

	public void clear() {
		this.lastNodeId = 1;
		try {
			this.root.flush();
		} catch (NodeException e) {
			engine.writeMessage("error while flushing rete network");
		}
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
		//TODO: re-implement it
		// first implementation:
		//if (this.shareNodes) {
		//	int result = root.shareNodes(rule, this);
		//	root.propagateEndMerging();
		//	return result;
		//} else
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

	public WorkingMemory getWorkingMemory() {
		return workingMemory;
	}

}