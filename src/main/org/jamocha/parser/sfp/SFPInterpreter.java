/*
 * Copyright 2007 Karl-Heinz Krempels, Sebastian Reinartz
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

package org.jamocha.parser.sfp;

import java.util.ArrayList;

import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.JamochaValueUtils;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.ExpressionCollection;
import org.jamocha.rete.ExpressionList;
import org.jamocha.rete.ExpressionSequence;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.TemplateSlot;
import org.jamocha.rete.configurations.AssertConfiguration;
import org.jamocha.rete.configurations.DeclarationConfiguration;
import org.jamocha.rete.configurations.DeffunctionConfiguration;
import org.jamocha.rete.configurations.DefruleConfiguration;
import org.jamocha.rete.configurations.IfElseConfiguration;
import org.jamocha.rete.configurations.LoopForCountConfiguration;
import org.jamocha.rete.configurations.ModifyConfiguration;
import org.jamocha.rete.configurations.Signature;
import org.jamocha.rete.configurations.SlotConfiguration;
import org.jamocha.rete.configurations.WhileDoConfiguration;
import org.jamocha.rule.AndCondition;
import org.jamocha.rule.AndConnectedConstraint;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.ExistCondition;
import org.jamocha.rule.LiteralConstraint;
import org.jamocha.rule.NotCondition;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.OrCondition;
import org.jamocha.rule.OrConnectedConstraint;
import org.jamocha.rule.TestCondition;

public class SFPInterpreter implements SFPParserVisitor {
	
	class ConstraintElementGroup extends ArrayList<Constraint>{
		private static final long serialVersionUID = 1L;

		public String toString() {
			StringBuffer result = new StringBuffer();
			result.append("{ ");
			for (Constraint c : this) {
				result.append(c.toString());
			}
			return result.append("}").toString();
		}
		
		public Constraint getRootConstraint() {
			Constraint root=null;
			AndConnectedConstraint act=null;
			if (this.size() == 1) {
				// we dont need any OR-Constraints
				root = this.get(0);
			} else {
				for (int i=0 ; i < this.size()-1; i++ ) {
					
					// create new AND node
					AndConnectedConstraint newAndNode = new AndConnectedConstraint();

					// mount our constraint on our new AND node
					Constraint actConstr = this.get(i);
					newAndNode.setLeft(actConstr);
					
					// store new AND node as root, we root is null now
					if (root == null) {root = newAndNode;}
					
					// if we have an actual AND node, we mount our new one to the old one
					if (act != null) act.setRight(newAndNode);

					
					act = newAndNode;
				}
				act.setRight(this.get(this.size()-1));

			}
			return root;
		}


	}
	
	class ConstraintElementGroupList extends ArrayList<ConstraintElementGroup>{
		private static final long serialVersionUID = 1L;
		
		ConstraintElementGroup getLastGroup() {
			return this.get( this.size()-1 );
		}
		
		public String toString() {
			StringBuffer result = new StringBuffer();
			result.append("[ \n");
			for (ConstraintElementGroup c : this) {
				result.append("   ").append(c.toString()).append("\n");
			}
			return result.append("\n]").toString();
		}

		
		ConstraintElementGroup createNewGroup() {
			ConstraintElementGroup result;
			this.add(result = new ConstraintElementGroup());
			return result;
		}
		
		Constraint getRootConstraint() {
			Constraint root=null;
			OrConnectedConstraint act=null;
			if (this.size() == 1 ){
				//we dont need any OR-Constraints
				root = this.get(0).getRootConstraint();
			} else {
				for (int i=0 ; i < this.size()-1; i++ ) {
					
					// create new OR node
					OrConnectedConstraint newOrNode = new OrConnectedConstraint();

					// mount our constraint on our new OR node
					Constraint actConstr = this.get(i).getRootConstraint();
					newOrNode.setLeft(actConstr);
					
					// store new OR node as root, we root is null now
					if (root == null) {root = newOrNode;}
					
					// if we have an actual OR node, we mount our new one to the old one
					if (act != null) act.setRight(newOrNode);

					
					act = newOrNode;
				}
				act.setRight(this.getLastGroup().getRootConstraint());
			}
			return root;
		}
		
		
	}
	

	public Object visit(SimpleNode node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPStart node, Object data) {
		return node.jjtGetChild(0).jjtAccept(this, data);
	}

	public Object visit(SFPConstant node, Object data) {
		return node.jjtGetChild(0).jjtAccept(this, data);
	}

	public Object visit(SFPFloat node, Object data) {
		return JamochaValueUtils.convertToDouble(node.getName());
	}

	public Object visit(SFPInteger node, Object data) {
		return JamochaValueUtils.convertToLong(node.getName());
	}

	public Object visit(SFPDateTime node, Object data) {
		return JamochaValueUtils.convertToDateTime(node.getName());
	}

	public Object visit(SFPSymbol node, Object data) {
		return JamochaValue.newIdentifier(node.getName());
	}

	public Object visit(SFPString node, Object data) {
		return JamochaValue.newString(node.getName());
	}

	public Object visit(SFPTrue node, Object data) {
		return JamochaValue.TRUE;
	}

	public Object visit(SFPFalse node, Object data) {
		return JamochaValue.FALSE;
	}

	public Object visit(SFPConstructDescription node, Object data) {
		return node.jjtGetChild(0).jjtAccept(this, data);
	}

	public Object visit(SFPSingleVariable node, Object data) {
		BoundParam bp = new BoundParam();
		bp.setVariableName(node.getName());
		return bp;
	}

	public Object visit(SFPGlobalVariable node, Object data) {
		BoundParam bp = new BoundParam();
		bp.setVariableName(node.getName());
		return bp;
	}

	public Object visit(SFPMultiVariable node, Object data) {
		BoundParam bp = new BoundParam();
		bp.setVariableName(node.getName());
		bp.setIsMultislot(true);
		return bp;
	}

	public Object visit(SFPVariableType node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPAnyFunction node, Object data) {
		// This function is different to the others, because the function name
		// is stored in the first child node and all the other child nodes are
		// considered
		// as function call parameters.

		JamochaValue fctName = (JamochaValue) node.jjtGetChild(0).jjtAccept(
				this, data);

		Parameter params[] = new Parameter[node.jjtGetNumChildren() - 1];
		for (int i = 1; i < node.jjtGetNumChildren(); i++) {
			params[i - 1] = (Parameter) node.jjtGetChild(i).jjtAccept(this,
					data);
		}

		// Create FunctionParam as result:
		Signature funcParam = new Signature();
		funcParam.setSignatureName(fctName.getStringValue());
		funcParam.setParameters(params);
		return funcParam;
	}

	public Object visit(SFPExpression node, Object data) {
		// retruns sub value: Constant() | Variable() | FunctionCall()
		return node.jjtGetChild(0).jjtAccept(this, data);
	}

	public Object visit(SFPAssertFunc node, Object data) {

		// create an AssertConfiguration array an fill it in the subnodes
		AssertConfiguration[] acArray = new AssertConfiguration[node
				.jjtGetNumChildren()];
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			acArray[i] = (AssertConfiguration) node.jjtGetChild(i).jjtAccept(
					this, data);
		}

		// create the resulting signature
		Signature signature = new Signature();
		signature
				.setSignatureName(org.jamocha.rete.functions.ruleengine.Assert.NAME);
		signature.setParameters(acArray);

		return signature;
	}

	public Object visit(SFPModify node, Object data) {

		// create an AssertConfiguration array an fill it in the subnodes
		ModifyConfiguration[] acArray = new ModifyConfiguration[node
				.jjtGetNumChildren()];
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			acArray[i] = (ModifyConfiguration) node.jjtGetChild(i).jjtAccept(
					this, data);
		}

		// create the resulting signature
		Signature signature = new Signature();
		signature
				.setSignatureName(org.jamocha.rete.functions.ruleengine.Modify.NAME);
		signature.setParameters(acArray);

		return signature;
	}

	public Object visit(SFPModifyPattern node, Object data) {
		// get the template name
		BoundParam fact = (BoundParam) node.jjtGetChild(0)
				.jjtAccept(this, data);
		fact.setObjectBinding(true);

		// get the slots from subnodes:
		SlotConfiguration[] slots = new SlotConfiguration[node
				.jjtGetNumChildren() - 1];
		SlotConfiguration slot = null;
		for (int i = 1; i < node.jjtGetNumChildren(); i++) {
			slot = (SlotConfiguration) node.jjtGetChild(i)
					.jjtAccept(this, data);
			slot.setId(i - 1);
			slots[i - 1] = slot;
		}

		ModifyConfiguration mc = new ModifyConfiguration();
		mc.setFactBinding(fact);
		mc.setSlots(slots);

		return mc;
	}

	public Object visit(SFPFindFactByFactFunc node, Object data) {

		// create an AssertConfiguration array an fill it in the subnodes
		AssertConfiguration[] acArray = new AssertConfiguration[node
				.jjtGetNumChildren()];
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			acArray[i] = (AssertConfiguration) node.jjtGetChild(i).jjtAccept(
					this, data);
		}

		// create the resulting signature
		Signature signature = new Signature();
		signature
				.setSignatureName(org.jamocha.rete.functions.ruleengine.FindFactByFact.NAME);
		signature.setParameters(acArray);

		return signature;
	}

	public Object visit(SFPRetractFunc node, Object data) {

		Parameter params[] = new Parameter[node.jjtGetNumChildren()];
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			params[i] = (Parameter) node.jjtGetChild(i).jjtAccept(this, data);
		}

		// Create FunctionParam as result:
		Signature funcParam = new Signature();
		funcParam.setSignatureName("retract");
		funcParam.setParameters(params);
		return funcParam;
	}

	public Object visit(SFPIfElseFunc node, Object data) {
		IfElseConfiguration ifElseConf = new IfElseConfiguration();
		ifElseConf.setCondition((Expression) node.jjtGetChild(0).jjtAccept(
				this, data));
		ifElseConf.setThenActions((ExpressionCollection) node.jjtGetChild(1)
				.jjtAccept(this, data));
		if (node.jjtGetNumChildren() > 2) {
			ifElseConf.setElseActions((ExpressionCollection) node
					.jjtGetChild(2).jjtAccept(this, data));
		}
		Parameter params[] = { ifElseConf };
		Signature funcParam = new Signature();
		funcParam.setSignatureName(org.jamocha.rete.functions.If.NAME);
		funcParam.setParameters(params);
		return funcParam;
	}

	public Object visit(SFPWhileFunc node, Object data) {
		WhileDoConfiguration whileDoConf = new WhileDoConfiguration();
		whileDoConf.setCondition((Expression) node.jjtGetChild(0).jjtAccept(
				this, data));
		whileDoConf.setWhileActions((ExpressionCollection) node.jjtGetChild(1)
				.jjtAccept(this, data));
		Parameter params[] = { whileDoConf };
		Signature funcParam = new Signature();
		funcParam.setSignatureName(org.jamocha.rete.functions.While.NAME);
		funcParam.setParameters(params);
		return funcParam;
	}

	public Object visit(SFPLoopForCountFunc node, Object data) {
		LoopForCountConfiguration lfcConf = new LoopForCountConfiguration();
		lfcConf.setLoopVar((BoundParam) node.jjtGetChild(0).jjtAccept(this,
				data));
		int index = 1;
		if (node.jjtGetNumChildren() > 3) {
			lfcConf.setStartIndex((Expression) node.jjtGetChild(index++)
					.jjtAccept(this, data));
		}
		lfcConf.setEndIndex((Expression) node.jjtGetChild(index++).jjtAccept(
				this, data));
		lfcConf.setActions((ExpressionCollection) node.jjtGetChild(index)
				.jjtAccept(this, data));
		Parameter params[] = { lfcConf };
		Signature funcParam = new Signature();
		funcParam
				.setSignatureName(org.jamocha.rete.functions.LoopForCount.NAME);
		funcParam.setParameters(params);
		return funcParam;
	}

	public Object visit(SFPSwitchCaseFunc node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPCaseStatement node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPSwitchDefaults node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPDeftemplateConstruct node, Object data) {
		// get the template name
		JamochaValue templName = (JamochaValue) node.jjtGetChild(0).jjtAccept(
				this, data);

		// get the template description
		int j = 1;
		JamochaValue descr = null;

		Node n = node.jjtGetChild(1);

		if (n != null && n instanceof SFPConstructDescription) {
			j = 2;
			descr = (JamochaValue) n.jjtAccept(this, data);
		}

		// gather all the slots from the syntax tree and set them up
		TemplateSlot[] s = new TemplateSlot[node.jjtGetNumChildren() - j];
		TemplateSlot slot = null;
		for (int i = j; i < node.jjtGetNumChildren(); i++) {
			slot = (TemplateSlot) (node.jjtGetChild(i).jjtAccept(this, data));
			slot.setId(i - j);
			s[i - j] = slot;
		}

		// create the param containing the resulting template
		Deftemplate tpl = new Deftemplate(templName.getStringValue(), null, s);

		if (descr != null) {
			tpl.setDescription(descr.toString());
		}

		Signature defTemplate = new Signature();

		defTemplate
				.setSignatureName(org.jamocha.rete.functions.ruleengine.Deftemplate.NAME);
		defTemplate
				.setParameters(new Parameter[] { JamochaValue.newObject(tpl) });

		return defTemplate;
	}

	public Object visit(SFPSingleSlotDefinition node, Object data) {
		// slot-name:
		JamochaValue slotName = (JamochaValue) node.jjtGetChild(0).jjtAccept(
				this, data);

		TemplateSlot ts = new TemplateSlot();
		ts.setName(slotName.getStringValue());
		ts.setMultiSlot(false);

		// setting the slot attributes
		for (int i = 1; i < node.jjtGetNumChildren(); i++) {
			node.jjtGetChild(i).jjtAccept(this, ts);
		}

		return ts;
	}

	public Object visit(SFPMultiSlotDefinition node, Object data) {
		// slot-name:
		JamochaValue slotName = (JamochaValue) node.jjtGetChild(0).jjtAccept(
				this, data);

		TemplateSlot ts = new TemplateSlot();
		ts.setName(slotName.getStringValue());
		ts.setMultiSlot(true);
		// set the slot attributes
		for (int i = 1; i < node.jjtGetNumChildren(); i++) {
			node.jjtGetChild(i).jjtAccept(this, ts);
		}

		return ts;
	}

	public Object visit(SFPAttributes node, Object data) {
		// ask all sub expression for their value:
		ExpressionList expressionList = new ExpressionList();
		for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
			expressionList.add((Parameter) node.jjtGetChild(i).jjtAccept(this,
					data));
		}
		return expressionList;
	}

	public Object visit(SFPDefaultAttribute node, Object data) {
		// pass on the Template slot:
		node.jjtGetChild(0).jjtAccept(this, data);
		return null;
	}

	public Object visit(SFPDefaultAttributes node, Object data) {
		// eval subnodes (SFPAttributes) to get dynamic expressions:
		Expression exp = (Expression) node.jjtGetChild(0).jjtAccept(this, null);
		// set this as Default:
		((TemplateSlot) data).setDefaultExpression(exp);
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPDeriveAttribute node, Object data) {
		((TemplateSlot) data).setDefaultDerive();
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPNoneAttribute node, Object data) {
		((TemplateSlot) data).setRequired(true);
		return null;
	}

	public Object visit(SFPDynamicAttribute node, Object data) {
		// eval subnodes (SFPAttributes) to get dynamic expressions:
		Expression exp = (Expression) node.jjtGetChild(0).jjtAccept(this, null);
		// set this as Dynamic Default:
		((TemplateSlot) data).setDynamicDefaultExpression(exp);
		return null;
	}

	public Object visit(SFPTemplateRHSPattern node, Object data) {

		AssertConfiguration ac = new AssertConfiguration();
		// get the Template name
		String templateName = ((JamochaValue) node.jjtGetChild(0).jjtAccept(
				this, data)).toString();

		// get the slots from subnodes:
		SlotConfiguration[] slots = new SlotConfiguration[node
				.jjtGetNumChildren() - 1];
		SlotConfiguration slot = null;
		for (int i = 1; i < node.jjtGetNumChildren(); i++) {
			slot = (SlotConfiguration) node.jjtGetChild(i)
					.jjtAccept(this, data);
			slot.setId(i - 1);
			slots[i - 1] = slot;
		}

		ac.setTemplateName(templateName);
		ac.setSlots(slots);

		return ac;
	}

	public Object visit(SFPRHSSlot node, Object data) {
		SlotConfiguration sc = new SlotConfiguration();

		// get the slot name
		String slotName = ((JamochaValue) node.jjtGetChild(0).jjtAccept(this,
				data)).toString();

		// get the slots values:
		Parameter[] slotValues = new Parameter[node.jjtGetNumChildren() - 1];

		for (int i = 1; i < node.jjtGetNumChildren(); i++) {
			slotValues[i - 1] = (Parameter) node.jjtGetChild(i).jjtAccept(this,
					data);
		}

		sc.setSlotName(slotName);
		sc.setSlotValues(slotValues);

		return sc;
	}

	public Object visit(SFPDefruleConstruct node, Object data) {

		JamochaValue ruleName = null;
		int j = 0;

		// get the rule name
		ruleName = (JamochaValue) node.jjtGetChild(j++).jjtAccept(this, data);

		// get the rule description
		JamochaValue ruleDescription = null;

		Node n = node.jjtGetChild(j);

		if (n != null && n instanceof SFPConstructDescription) {
			j++;
			ruleDescription = (JamochaValue) n.jjtAccept(this, data);
		}

		// get the rule declaration(s)
		DeclarationConfiguration dc = null;

		n = node.jjtGetChild(j);

		if (n != null && n instanceof SFPDeclaration) {
			j++;
			dc = (DeclarationConfiguration) n.jjtAccept(this, data);
		}

		// get the action listCondition
		int k = node.jjtGetNumChildren();
		n = node.jjtGetChild(k - 1);
		ExpressionSequence actions = null;

		if (n != null && n instanceof SFPActionList) {
			k--;
			actions = (ExpressionSequence) n.jjtAccept(this, data);
		}

		// set the rule LHS
		Condition[] conditionList = new Condition[k - j];
		Condition cond;
		int totalComplexity = 0;
		for (int i = j; i < k; i++) {
			cond = (Condition) (node.jjtGetChild(i).jjtAccept(this, data));
			conditionList[i - j] = cond;
			totalComplexity += cond.getTotalComplexity();

		}

		// setup a new DefruleConfiguration
		DefruleConfiguration rc = new DefruleConfiguration();
		rc.setRuleName(ruleName.toString());
		rc.setTotalComplexity(totalComplexity);
		rc.setDeclarationConfiguration(dc);
		rc.seConditions(conditionList);
		rc.setActions(actions);

		if (ruleDescription != null) {
			rc.setRuleDescription(ruleDescription.toString());
		}

		// create the resulting signature
		Signature signature = new Signature();
		signature.setSignatureName(org.jamocha.rete.functions.ruleengine.Defrule.NAME);
		signature.setParameters(new Parameter[] { rc });

		return signature;
	}

	public Object visit(SFPActionList node, Object data) {
		ExpressionSequence actionList = new ExpressionSequence();
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			actionList.add((Parameter) node.jjtGetChild(i)
					.jjtAccept(this, null));
		}
		return actionList;
	}

	public Object visit(SFPDeclaration node, Object data) {

		DeclarationConfiguration dc = new DeclarationConfiguration();

		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			node.jjtGetChild(i).jjtAccept(this, dc);
		}

		return dc;
	}

	public Object visit(SFPSalience node, Object data) {
		// get the node's expression and set it to the DeclarationConfiguration
		Parameter parameter = (Parameter) node.jjtGetChild(0).jjtAccept(this,
				null);
		((DeclarationConfiguration) data).setSalience(parameter);
		return null;
	}

	public Object visit(SFPAutoFocus node, Object data) {
		// get the node's expression and set it to the DeclarationConfiguration
		Parameter parameter = (Parameter) node.jjtGetChild(0).jjtAccept(this,
				null);
		((DeclarationConfiguration) data).setAutoFocus(parameter);

		return null;
	}

	public Object visit(SFPRuleVersion node, Object data) {
		// get the node's expression and set it to the DeclarationConfiguration
		Parameter parameter = (Parameter) node.jjtGetChild(0).jjtAccept(this,
				null);
		((DeclarationConfiguration) data).setVersion(parameter);

		return null;
	}

	public Object visit(SFPNotFunction node, Object data) {
		NotCondition notCond = new NotCondition();

		Condition nested;
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			nested = (Condition) node.jjtGetChild(i).jjtAccept(this, data);
			notCond.addNestedConditionElement(nested);
			notCond.incrementTotalComplexityBy(nested.getTotalComplexity());
		}

		return notCond;
	}

	public Object visit(SFPAndFunction node, Object data) {
		AndCondition andCond = new AndCondition();

		Condition nested;
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			nested = (Condition) node.jjtGetChild(i).jjtAccept(this, data);
			andCond.addNestedConditionElement(nested);
			andCond.incrementTotalComplexityBy(nested.getTotalComplexity());
		}

		return andCond;
	}

	public Object visit(SFPOrFunction node, Object data) {
		OrCondition orCond = new OrCondition();

		Condition nested;
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			nested = (Condition) node.jjtGetChild(i).jjtAccept(this, data);
			orCond.addNestedConditionElement(nested);
			orCond.incrementTotalComplexityBy(nested.getTotalComplexity());
		}

		return orCond;
	}

	public Object visit(SFPAssignedPatternCE node, Object data) {
		// variable:
		BoundParam bp = (BoundParam) node.jjtGetChild(0).jjtAccept(this, data);
		JamochaValue variable = JamochaValue
				.newIdentifier(bp.getVariableName());

		// get the object condition from subnode:
		ObjectCondition objectCond = (ObjectCondition) node.jjtGetChild(1)
				.jjtAccept(this, data);

		// create boundconstraint
		BoundConstraint bc = new BoundConstraint(objectCond.getTemplateName(),
				true);
		bc.setValue(variable);

		// add boundconstraint to object condition
		objectCond.addConstraint(bc, 0);

		return objectCond;
	}

	public Object visit(SFPLogicalCE node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPTestCE node, Object data) {
		TestCondition testCond = new TestCondition();

		Signature signature = (Signature) node.jjtGetChild(0).jjtAccept(this,
				data);
		testCond.setFunction(signature);
		// we don't increment the total Condition here, because we don't know
		// anything about the Condition of the signature
		return testCond;
	}

	public Object visit(SFPExistsCE node, Object data) {
		ExistCondition existCond = new ExistCondition();

		Condition nested;
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			nested = (Condition) node.jjtGetChild(i).jjtAccept(this, data);
			existCond.addNestedConditionElement(nested);
			existCond.incrementTotalComplexityBy(nested.getTotalComplexity());
		}

		return existCond;
	}

	public Object visit(SFPForallCE node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPTemplatePatternCE node, Object data) {
		ObjectCondition objectCond = new ObjectCondition();

		// get Template Name
		JamochaValue templateName = (JamochaValue) node.jjtGetChild(0)
				.jjtAccept(this, data);
		objectCond.setTemplateName(templateName.toString());

		
		// constraints
		Constraint constr;
		for (int i = 1; i < node.jjtGetNumChildren(); i++) {
			constr = (Constraint) node.jjtGetChild(i).jjtAccept(this, data);
			objectCond.addConstraint(constr);
			objectCond.incrementTotalComplexityBy(constr.getTotalComplexity());
		}
		return objectCond;
	}

	public Object visit(SFPLHSSlot node, Object data) {
		// get Slot Name
		JamochaValue slotName = (JamochaValue) node.jjtGetChild(0).jjtAccept(
				this, data);

		// get constraint from subnode
		Constraint constraint = (Constraint) node.jjtGetChild(1).jjtAccept(
				this, data);

		// set name to given constraint
		constraint.setName(slotName.getStringValue());

		return constraint;
	}

	public Object visit(SFPSingleFieldWildcard node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPMultiFieldWildcard node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPConnectedConstraint node, Object data) {
		ConstraintElementGroupList cegl = new ConstraintElementGroupList();
		cegl.createNewGroup().add((Constraint)node.jjtGetChild(0).jjtAccept(this, null));
		for (int i = 1; i < node.jjtGetNumChildren() ; i++) {
			node.jjtGetChild(i).jjtAccept(this,	cegl);
		}
		return cegl.getRootConstraint();
	}
	
	public Object visit(SFPAmpersandConnectedConstraint node, Object data) {
		ConstraintElementGroupList cegl = (ConstraintElementGroupList)data;
		ConstraintElementGroup actGroup = cegl.getLastGroup();
		actGroup.add( (Constraint)node.jjtGetChild(0).jjtAccept(this, null) );
		
		if (node.jjtGetNumChildren() > 1) 
				node.jjtGetChild(1).jjtAccept(this,	data);
		
		return null;
	}
	
	

	public Object visit(SFPLineConnectedConstraint node, Object data) {
		ConstraintElementGroupList cegl = (ConstraintElementGroupList)data;
		cegl.createNewGroup().add( (Constraint)node.jjtGetChild(0).jjtAccept(this, null)   );
		
		if (node.jjtGetNumChildren() > 1)
			node.jjtGetChild(1).jjtAccept(this,	data);
		
		return null;
	}

	public Object visit(SFPTerm node, Object data) {
		int j = 0;
		boolean isNegated = false;
		Node n = node.jjtGetChild(j);
		if (n instanceof SFPNegation) {
			j++;
			isNegated = true;
		}

		Constraint constraint = null;
		n = node.jjtGetChild(j);
		Object obj = n.jjtAccept(this, data);
		if (n instanceof SFPConstant) {
			constraint = new LiteralConstraint();
			constraint.setValue((JamochaValue) obj);
		} else if (n instanceof SFPColon) {
			// TODO: constraint = new PredicateConstraint();
			// predivate can't handle functions containing functioncalls
		} else if (n instanceof SFPEquals) {
			// TODO: constraint = new PredicateConstraint();
			// predivate can't handle functions containing functioncalls
		} else if (n instanceof SFPSingleVariable) {
			constraint = new BoundConstraint();
			JamochaValue jv = JamochaValue.newIdentifier(((BoundParam) obj)
					.getVariableName());
			constraint.setValue(jv);
		} else if (n instanceof SFPMultiVariable) {
			constraint = new BoundConstraint();
			JamochaValue jv = JamochaValue.newIdentifier(((BoundParam) obj)
					.getVariableName());
			constraint.setValue(jv);
			((BoundConstraint) constraint).setIsMultislot(true);
		}

		constraint.setNegated(isNegated);

		return constraint;
	}

	public Object visit(SFPNegation node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPColon node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPEquals node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPDefglobalConstruct node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPGlobalAssignment node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPDeffunctionConstruct node, Object data) {
		int j = 0;
		// get the function name
		JamochaValue functionName = (JamochaValue) node.jjtGetChild(j++)
				.jjtAccept(this, data);

		// get the template description
		JamochaValue functionDescription = JamochaValue.newString("");

		Node n = node.jjtGetChild(j);

		if (n != null && n instanceof SFPConstructDescription) {
			j++;
			functionDescription = (JamochaValue) n.jjtAccept(this, data);
		}
		
		// get the function group
		JamochaValue functionGroup = null;

		n = node.jjtGetChild(j);

		if (n != null && n instanceof SFPFunctionGroup) {
			j++;
			functionGroup = (JamochaValue) n.jjtAccept(this, data);
		}		
		

		// get function's variables
		Parameter[] params = new Parameter[node.jjtGetNumChildren() - (j + 1)];
		for (int i = j; i < node.jjtGetNumChildren() - 1; i++) {
			BoundParam bp = (BoundParam) node.jjtGetChild(i).jjtAccept(this,
					data);
			params[i - j] = bp;
		}

		// get the function's actionlist
		ExpressionSequence expressions = (ExpressionSequence) node.jjtGetChild(
				node.jjtGetNumChildren() - 1).jjtAccept(this, data);

		// set up a new DeffunctionConfiguration
		DeffunctionConfiguration[] dcs = new DeffunctionConfiguration[1];
		DeffunctionConfiguration dc = new DeffunctionConfiguration();

		// set the function's name
		dc.setFunctionName(functionName.toString());
		
		// set the function's group name
		if (functionGroup != null)
			dc.setFunctionGroup(functionGroup.toString());

		// set the function's description
		dc.setFunctionDescription(functionDescription.toString());

		// set the function's params
		dc.setParams(params);

		// set the function's Actionlist
		dc.setActions(expressions);

		Signature functionParam = new Signature();
		functionParam
				.setSignatureName(org.jamocha.rete.functions.ruleengine.Deffunction.NAME);

		dcs[0] = dc;

		functionParam.setParameters(dcs);

		return functionParam;
	}
	
	public Object visit(SFPFunctionGroup node, Object data) {
		return node.jjtGetChild(0).jjtAccept(this, data);
	}

	public Object visit(SFPDefgenericConstruct node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPParameterRestriction node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPWildcardParameterRestriction node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPQuery node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPTypeAttribute node, Object data) {
		// pass on the Template Slot:
		node.jjtGetChild(0).jjtAccept(this, data);

		return null;
	}

	public Object visit(SFPTypeSpecification node, Object data) {
		// collect type from subNode:
		JamochaType type = (JamochaType) node.jjtGetChild(0).jjtAccept(this,
				data);
		// set type to give template slot
		((TemplateSlot) data).setValueType(type);

		return null;
	}

	public Object visit(SFPAllowedConstantAttribute node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPSymbolList node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPStringList node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPLexemeList node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPIntegerList node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPFloatList node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPNumberList node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPValueList node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPRangeAttribute node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPRangeSpecification node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPCardinalityAttribute node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPCardinalitySpecification node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPDefmoduleConstruct node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPSymbolType node, Object data) {
		return JamochaType.IDENTIFIER;
	}

	public Object visit(SFPStringType node, Object data) {
		return JamochaType.STRING;
	}

	public Object visit(SFPDateTimeType node, Object data) {
		return JamochaType.DATETIME;
	}

	public Object visit(SFPLexemeType node, Object data) {
		return JamochaType.STRING;
	}

	public Object visit(SFPBooleanType node, Object data) {
		return JamochaType.BOOLEAN;
	}

	public Object visit(SFPIntegerType node, Object data) {
		return JamochaType.LONG;
	}

	public Object visit(SFPLongType node, Object data) {
		return JamochaType.LONG;
	}

	public Object visit(SFPShortType node, Object data) {
		return JamochaType.LONG;
	}

	public Object visit(SFPFloatType node, Object data) {
		return JamochaType.DOUBLE;
	}

	public Object visit(SFPDoubleType node, Object data) {
		return JamochaType.DOUBLE;
	}

	public Object visit(SFPNumberType node, Object data) {
		// TODO: check is this correct to match number to double?
		return JamochaType.DOUBLE;
	}
}
