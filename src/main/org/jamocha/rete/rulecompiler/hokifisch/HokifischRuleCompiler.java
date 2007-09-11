package org.jamocha.rete.rulecompiler.hokifisch;

import java.util.HashSet;
import java.util.Set;

import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.RuleException;
import org.jamocha.rete.CompileEvent;
import org.jamocha.rete.CompilerListener;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Rete;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.Slot;
import org.jamocha.rete.Template;
import org.jamocha.rete.TemplateSlot;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.nodes.AbstractAlpha;
import org.jamocha.rete.nodes.AlphaNode;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.nodes.BetaFilterNode;
import org.jamocha.rete.nodes.ObjectTypeNode;
import org.jamocha.rete.nodes.ReteNet;
import org.jamocha.rete.nodes.RootNode;
import org.jamocha.rule.Condition;
import org.jamocha.rule.ConditionWithNested;
import org.jamocha.rule.Constraint;
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
	 */
	public void addObjectTypeNode(Template template) {
		rootNode.addObjectTypeNode(template, engine);
	}

	/**
	 * adds a rule in the rete network
	 */
	public boolean addRule(Rule rule) throws AssertException, RuleException {
		CompileCallInformation information = new CompileCallInformation(rule);
		try {
			preCompile(information);
			compileConditions(information);
			compileActions(information);
		} catch (RuleCompilingException e) {
			throw new RuleException("error while compiling rule "+rule.getName()+": "+e.getMessage());
		}
		for (CompilerListener l : listeners) l.ruleAdded(new CompileEvent(rule,CompileEvent.ADD_RULE_EVENT));
		return true;
	}

	private void preCompileCondition(CompileCallInformation information, Condition c) {
		
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
	
	private void preCompile(CompileCallInformation information) {
		// feed CompileCallInformation.condition2template
		Rule rule = information.rule;
		for (Condition c : rule.getConditions()){
			preCompileCondition(information,c);
		}
	}

	private void compileActions(CompileCallInformation information) throws RuleCompilingException {
		// TODO Auto-generated method stub
		
	}

	private void compileConditions(CompileCallInformation information) throws RuleCompilingException {
		for (Condition condition : information.rule.getConditions()) {
			compileCondition(information,condition);
		}
		compileJoins(information);
	}

	
	/** 
	 * it joins all the conditions given to one (using only simple join nodes)
	 * and returns the bottom join node
	 * @return
	 * @throws RuleCompilingException 
	 */
	private BaseNode joinConditions(CompileCallInformation information, Condition[] conditions, AbstractAlpha fromAbove) throws RuleCompilingException{
		BaseNode foo = fromAbove;
		for (Condition condition : information.rule.getConditions()) {
			BaseNode newJoin = new BetaFilterNode(network.nextNodeId());
			try {
				fromAbove.addNode(newJoin, network);
				information.conditionSubnets.get(condition).getLast().addNode(newJoin, network);
				foo = newJoin;
			} catch (AssertException e) {
				throw new RuleCompilingException(e);
			}
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
		try {
			rootNode.activateObjectTypeNode(initFact, network);
		} catch (AssertException e) {
			throw new RuleCompilingException(e);
		}
		AbstractAlpha initialFact = getObjectTypeNode(initFact);
		joinConditions(information, information.rule.getConditions(), initialFact);
		
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
	private void appendToSubnet(BaseNode first, BaseNode last, ReteSubnet subnet) throws RuleCompilingException{
		try {
			subnet.getLast().addNode(first, network);
			subnet.setLast(last);
		} catch (AssertException e) {
			throw new RuleCompilingException(e);
		}
	}
	
	/**
	 * appends some nodes to a given subnet.
	 */
	private void appendToSubnet(BaseNode node, ReteSubnet subnet) throws RuleCompilingException {
		appendToSubnet(node, node, subnet);
	}
	
	/**
	 * compiles an object condition
	 */
	private void compileObjectCondition(CompileCallInformation information, ObjectCondition condition) throws RuleCompilingException {
		//determine the root of our subnet
		BaseNode relativeRoot = getObjectTypeNode(condition.getTemplate());
		ReteSubnet subnet = new ReteSubnet(relativeRoot, relativeRoot);
		// compile each constraint and append nodes to the subnet
		for (Constraint constraint : condition.getConstraints()) {
			compileConstraint(information,constraint);
			ReteSubnet newConstraintSubnet = information.constraintSubnets.get(constraint);
			if (newConstraintSubnet != null) {
				subnet = new ReteSubnet(subnet,newConstraintSubnet);
			}
		}
		// set the subnet into our information object
		information.conditionSubnets.put(condition, subnet);
	}
	
	private void compileConstraint(CompileCallInformation information, Constraint constraint) throws RuleCompilingException {
		// set slot
		Condition cond = information.constraint2condition.get(constraint);
		Template template = information.condition2template.get(cond);
		TemplateSlot slot = template.getSlot(constraint.getName());
		information.constraint2templateSlot.put(constraint,slot);
		if (constraint instanceof LiteralConstraint) {
			BaseNode literalComparisonNode = compileLiteralConstraint(information,(LiteralConstraint) constraint);
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
	private BaseNode compileLiteralConstraint(CompileCallInformation information,LiteralConstraint constraint) throws RuleCompilingException{
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
		AlphaNode node = new AlphaNode(network.nextNodeId());
		node.setSlot(sl);
		// set the operator
		node.setOperator(
			constraint.getNegated() ? Constants.NOTEQUAL : Constants.EQUAL
		);
		// return our new literal constraint compare node
		return node;
	}

	/**
	 *  gets the object type node for given template
	 */
	public ObjectTypeNode getObjectTypeNode(Template template) {
		return rootNode.getObjectTypeNodes().get(template);
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
		rootNode.removeObjectTypeNode(node);
		node.clear();
		node.destroy(network);
	}

	/**
	 * sets whether the rule compiler must validate new rules
	 */
	public void setValidateRule(boolean validate) {
		this.validate = validate;
	}

}
