package org.jamocha.rete.rulecompiler.hokifisch;

import jade.core.BaseNode;

import java.util.HashSet;
import java.util.Set;

import org.jamocha.Constants;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.RuleException;
import org.jamocha.rete.CompileEvent;
import org.jamocha.rete.CompilerListener;
import org.jamocha.rete.Rete;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.Slot;
import org.jamocha.rete.Template;
import org.jamocha.rete.TemplateSlot;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.nodes.Node;
import org.jamocha.rete.nodes.NodeException;
import org.jamocha.rete.nodes.ObjectTypeNode;
import org.jamocha.rete.nodes.OneInputNode;
import org.jamocha.rete.nodes.ReteNet;
import org.jamocha.rete.nodes.RootNode;
import org.jamocha.rete.nodes.SimpleBetaFilterNode;
import org.jamocha.rete.nodes.SlotFilterNode;
import org.jamocha.rete.nodes.TerminalNode;
import org.jamocha.rete.nodes.TwoInputNode;
import org.jamocha.rule.Action;
import org.jamocha.rule.Condition;
import org.jamocha.rule.ConditionWithNested;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.FunctionAction;
import org.jamocha.rule.LiteralConstraint;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.Rule;

/**
 * This is the first rule compiler, we write from scratch. Wish us luck ;)
 * @author Josef Hahn
 *
 */
public class HokifischRuleCompiler implements RuleCompiler {

	private static final long serialVersionUID = 1L;
	private Rete engine;
	private RootNode rootNode;
	private ReteNet network;
	private boolean validate; // true, iff our rule compiler must validate given rules
	private Set<CompilerListener> listeners; // listeners
	
	public HokifischRuleCompiler(Rete engine, ReteNet net) {
		this(engine,net.getRoot(),net);
	}
	
	@Deprecated
	public HokifischRuleCompiler(Rete engine, RootNode root, ReteNet net) {
		this.engine = engine;
		this.rootNode = root;
		this.network = net;
		listeners = new HashSet<CompilerListener>();
	}
	
	/**
	 * adds a listener
	 */
	public void addListener(CompilerListener listener) {
		listeners.add(listener);
	}

	/**
	 * adds an object type node for a given template
	 * @throws  
	 */
	public void addObjectTypeNode(Template template) {
		int newid = network.nextNodeId();
		ObjectTypeNode newOtn = new ObjectTypeNode(newid,engine.getWorkingMemory(),network,template);
		try {
			rootNode.addChild(newOtn);
		} catch (NodeException e) {
			engine.writeMessage(e.getMessage());
		}
	}

	
	/**
	 * adds a rule in the rete network
	 */
	public boolean addRule(Rule rule) throws AssertException, RuleException {
		CompileCallInformation information = new CompileCallInformation(rule);
		determineModule(information);
		try {
			preCompile(information);
			compileConditions(information);
			compileActions(information);
		} catch (RuleCompilingException e) {
			throw new RuleException("error while compiling rule "+rule.getName()+": "+e.getMessage());
		}
		rule.getModule().addRule(rule);
		for (CompilerListener l : listeners) l.ruleAdded(new CompileEvent(rule,CompileEvent.ADD_RULE_EVENT));
		return true;
	}

	private void determineModule(CompileCallInformation information) {
		Rule rule = information.rule;
		// we check the name of the rule to see if it is for a specific
		// module. if it is, we have to add it to that module
		if (rule.getName().indexOf("::") > 0) { //$NON-NLS-1$
			String text = rule.getName();
			String[] sp = text.split("::"); //$NON-NLS-1$
			rule.setName(sp[1]);
			String modName = sp[0].toUpperCase();
			rule.setModule(engine.getModules().getModule(modName, false));
		} else {
			rule.setModule(engine.getCurrentFocus());
		}
	}
	
	private void preCompileCondition(CompileCallInformation information, Condition c) throws RuleCompilingException {
		
		if (c.getConstraints() != null) {
			for (Constraint constr : c.getConstraints() ){
				information.constraint2condition.put(constr, c);
			}
		}
		
		if (c instanceof ObjectCondition) {
			ObjectCondition oc = (ObjectCondition) c;
			Template t = engine.findTemplate( oc.getTemplateName() );
			information.condition2template.put(c, t);
		} else if (c instanceof ConditionWithNested) {
			ConditionWithNested cwn = (ConditionWithNested) c;
			for (Condition sub : cwn.getNestedConditionalElement()) {
				preCompileCondition(information, sub);
			}
		}
	}
	
	private void preCompile(CompileCallInformation information) throws RuleCompilingException {
		// feed CompileCallInformation.condition2template
		Rule rule = information.rule;
		for (Condition c : rule.getConditions()){
			preCompileCondition(information,c);
		}
	}

	private void compileActions(CompileCallInformation information) throws RuleCompilingException {
		Rule rule = information.rule;
		for (Action action : rule.getActions()) {
			if (action instanceof FunctionAction) {
				FunctionAction fa = (FunctionAction) action;
				try {
					fa.configure(this.engine, rule);
				} catch (EvaluationException e) {
					throw new RuleCompilingException(e);
				}
			} else {
				throw new RuleCompilingException("unknown action type "+action.getClass().getSimpleName());
			}
		}
	}

	private void compileConditions(CompileCallInformation information) throws RuleCompilingException {
		for (Condition condition : information.rule.getConditions()) {
			compileCondition(information,condition);
		}
		compileJoins(information);
		addTerminalNode(information);
	}

	
	private void addTerminalNode(CompileCallInformation information) throws RuleCompilingException {
		TerminalNode tnode = new TerminalNode(network.nextNodeId() , engine.getWorkingMemory(), information.rule, engine.getNet());
		try {
			information.lastJoin.addChild(tnode);
		} catch (NodeException e) {
			engine.writeMessage(e.getMessage());
		}
	}

	/** 
	 * it joins all the conditions given to one (using only simple join nodes)
	 * and returns the bottom join node
	 * @return
	 * @throws RuleCompilingException 
	 */
	private Node joinConditions(CompileCallInformation information, Condition[] conditions, Node fromAbove) throws RuleCompilingException{
		Node foo = fromAbove;
		try{
			for (Condition condition : information.rule.getConditions()) {
				TwoInputNode newJoin = new SimpleBetaFilterNode(network.nextNodeId(), engine.getWorkingMemory(), network);
				fromAbove.addChild(newJoin);
				information.conditionSubnets.get(condition).getLast().addChild(newJoin);
				information.condition2join.put(condition,newJoin);
				foo = newJoin;
			}
		} catch (NodeException e) {
			engine.writeMessage(e.getMessage());
		}
		return foo;
	}

	/**
	 * compiles the seperated condition nodes to one network by using
	 * joins
	 * @throws RuleCompilingException 
	 */
	private void compileJoins(CompileCallInformation information) throws RuleCompilingException {
		// at first, we take an initialfact for having beta input
		// from all of our condition's last nodes (like also done
		// in old rule compiler)
		Template initFact = engine.getInitialTemplate();
		Node initialFact = getObjectTypeNode(initFact);
		information.lastJoin=joinConditions(information, information.rule.getConditions(), initialFact);
	}

	private void compileCondition(CompileCallInformation information,Condition condition) throws RuleCompilingException{
		/* here, i decided against visitor pattern, since the
		 * rule compiler must be independent from the condition-/
		 * constraint-classes. furthermore, it is more ugly than
		 * those instanceof's imho
		 */
		if (condition instanceof ObjectCondition) {
			compileObjectCondition(information, (ObjectCondition) condition);
		} else {
			throw new ConditionTypeNotImplementedException(condition.getClass().getSimpleName());
		}
	}

	/**
	 *
	 */ 
	private void appendToSubnet(Node first, Node last, ReteSubnet subnet) throws RuleCompilingException{
		try {
			subnet.getLast().addChild(first);
		} catch (NodeException e) {
			throw new RuleCompilingException(e);
		}
		subnet.setLast(last);
	}
	
	/**
	 * appends some nodes to a given subnet.
	 */
	@SuppressWarnings("unused")
	private void appendToSubnet(Node node, ReteSubnet subnet) throws RuleCompilingException {
		appendToSubnet(node, node, subnet);
	}
	
	/**
	 * compiles an object condition
	 */
	private void compileObjectCondition(CompileCallInformation information, ObjectCondition condition) throws RuleCompilingException {
		//determine the root of our subnet
		Node relativeRoot = getObjectTypeNode(information.getTemplate(condition));
		ReteSubnet subnet = new ReteSubnet(relativeRoot, relativeRoot);
		Node last = relativeRoot;
		// compile each constraint and append nodes to the subnet
		for (Constraint constraint : condition.getConstraints()) {
			compileConstraint(information,constraint);
			ReteSubnet newConstraintSubnet = information.constraintSubnets.get(constraint);
			try{
				last.addChild(newConstraintSubnet.getRoot());
			} catch (NodeException e) {
				engine.writeMessage(e.getMessage());
			}
			if (newConstraintSubnet != null) {
				subnet = new ReteSubnet(subnet,newConstraintSubnet);
			}
		}
		// set the subnet into our information object
		information.conditionSubnets.put(condition, subnet);
	}
	
	private void compileConstraint(CompileCallInformation information, Constraint constraint) throws RuleCompilingException {
		// set slot
		Template template = information.getTemplate(constraint);
		TemplateSlot slot = template.getSlot(constraint.getName());
		information.constraint2templateSlot.put(constraint,slot);
		if (constraint instanceof LiteralConstraint) {
			Node literalComparisonNode = compileLiteralConstraint(information,(LiteralConstraint) constraint);
			ReteSubnet net = new ReteSubnet(literalComparisonNode,literalComparisonNode);
			information.constraintSubnets.put(constraint,net);
		} else {
			throw new ConstraintTypeNotImplementedException(constraint.getClass().getSimpleName());
		}
	}

	/**
	 * 
	 * @param constraint
	 * @return
	 * @throws RuleCompilingException
	 */
	private Node compileLiteralConstraint(CompileCallInformation information,LiteralConstraint constraint) throws RuleCompilingException{
		// get an empty slot and put in the reference value
		TemplateSlot templateSlot = information.constraint2templateSlot.get(constraint);
		Slot sl = (Slot) templateSlot.clone();
		JamochaValue sval;
		try {
			sval = constraint.getValue().implicitCast(sl.getValueType());
			sl.setValue(sval);
		} catch (Exception e) {
			throw new RuleCompilingException(e);
		}
		// generate a new node and put in the slot
		int op = constraint.getNegated() ? Constants.NOTEQUAL : Constants.EQUAL;
		Node node = new SlotFilterNode(network.nextNodeId(), engine.getWorkingMemory(), op, sl, network);
		return node;
	}

	/**
	 *  gets the object type node for given template
	 */
	public ObjectTypeNode getObjectTypeNode(Template template) {
		for (Node n: rootNode.getParentNodes()) {
			if (n instanceof ObjectTypeNode) {
				ObjectTypeNode otn = (ObjectTypeNode)n;
				if ( otn.getTemplate().equals(template) ) {
					return otn;
				}
			}
		}
		return null;
	}

	/**
	 * asks whether the rule compiler must do validation of rules
	 */
	public boolean getValidateRule() {
		return validate;
	}

	/**
	 * removes a listener
	 */
	public void removeListener(CompilerListener listener) {
		listeners.remove(listener);
	}

	/**
	 * removes an object type node
	 */
	public void removeObjectTypeNode(ObjectTypeNode node) throws RetractException {
		//TODO implement me
	}

	/**
	 * sets whether the rule compiler must validate new rules
	 */
	public void setValidateRule(boolean validate) {
		this.validate = validate;
	}

}
