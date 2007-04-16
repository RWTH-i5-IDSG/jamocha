package org.jamocha.parser.sfp;

import java.util.ArrayList;

import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.JamochaValueUtils;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.ExpressionList;
import org.jamocha.rete.ExpressionSequence;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.TemplateSlot;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Defrule;

public class SFPInterpreter implements SFPParserVisitor {

	public Object visit(SimpleNode node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPStart node, Object data) {
		return node.childrenAccept(this, data);
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
		// returns description, stored in subnode Symbol
		return node.jjtGetChild(0).jjtAccept(this, data);
	}

	public Object visit(SFPSingleVariable node, Object data) {
	    BoundParam boundParam = new BoundParam();
	    boundParam.setVariableName(node.name);
	    return boundParam;
	}

	public Object visit(SFPGlobalVariable node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPMultiVariable node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPVariable node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPVariableType node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPFunctionCall node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPAnyFunction node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPExpression node, Object data) {
		// retruns sub value: Constant() | Variable() | FunctionCall()
		return node.jjtGetChild(0).jjtAccept(this, data);
	}

	public Object visit(SFPAssertFunc node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPFindFactByFactFunc node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPRetractFunc node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPIfElseFunc node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPWhileFunc node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPLoopForCntFunc node, Object data) {
		// TODO Auto-generated method stub
		return null;
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
		for (int i = j; i < node.jjtGetNumChildren(); i++) {
			s[i - j] = (TemplateSlot) (node.jjtGetChild(i)
					.jjtAccept(this, data));
		}

		// create the param containing the resulting template
		Deftemplate tpl = new Deftemplate(templName.getStringValue(), null, s);

		if (descr != null) {
			tpl.setDescription(descr.toString());
		}

		FunctionParam2 defTemplate = new FunctionParam2();

		defTemplate
				.setFunctionName(org.jamocha.rete.functions.ruleengine.Deftemplate.NAME);
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

	public Object visit(SFPTemplateAttribute node, Object data) {
		// pass on the Template slot:
		node.jjtGetChild(0).jjtAccept(this, data);

		return null;
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPRHSSlot node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPDefruleConstruct node, Object data) {

		JamochaValue ruleName = null;
		int j = 0;
		
		// get the rule name
		ruleName = (JamochaValue) node.jjtGetChild(j++).jjtAccept(
				this, data);

		// get the rule description
		JamochaValue descr = null;
		
		Node n = node.jjtGetChild(j);

		if (n != null && n instanceof SFPConstructDescription) {
			j++;
			descr = (JamochaValue) n.jjtAccept(this, data);
		}

		// create the rule and set the description
		Defrule rule = new Defrule(ruleName.getStringValue());

		if (descr != null) {
			rule.setDescription(descr.toString());
		}
		
		// set the rule declaration(s)
		n = node.jjtGetChild(j);

		if (n != null && n instanceof SFPDeclaration) {
			j++;
			n.jjtAccept(this, rule);
		}

		// set the rule LHS
		Condition[] conditionList = new Condition[node.jjtGetNumChildren() - j];
		for (int i = j; i < node.jjtGetNumChildren(); i++) {
			conditionList[i - j] = (Condition) (node.jjtGetChild(i)
					.jjtAccept(this, data));
		}

		return null;
	}

	public Object visit(SFPActionList node, Object data) {
		ExpressionSequence actionList = new ExpressionSequence();
		for( int i=0 ; i<node.jjtGetNumChildren() ; i++) {
			actionList.add((Parameter)node.jjtGetChild(i).jjtAccept(this, null));
		}
		return actionList;
	}

	public Object visit(SFPDeclaration node, Object data) {

		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		
		return null;
	}

	public Object visit(SFPSalience node, Object data) {
		JamochaValue jv = (JamochaValue)node.jjtGetChild(0).jjtAccept(this,null);
		((Defrule)data).setSalience(jv.getLongValue());
		return null;
	}

	public Object visit(SFPAutoFocus node, Object data) {
		JamochaValue jv = (JamochaValue)node.jjtGetChild(0).jjtAccept(this,null);
		((Defrule)data).setAutoFocus(jv.getBooleanValue());
		return null;
	}

	public Object visit(SFPRuleVersion node, Object data) {
		JamochaValue jv = (JamochaValue)node.jjtGetChild(0).jjtAccept(this,null);
		((Defrule)data).setVersion(jv.getStringValue());
		return null;
	}

	public Object visit(SFPConditionalElement node, Object data) {
		node.jjtGetChild(0).jjtAccept(this, data);
		return null;
	}

	public Object visit(SFPNotFunction node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPAndFunction node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPOrFunction node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPAssignedPatternCE node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPLogicalCE node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPTestCE node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPExistsCE node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPForallCE node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPTemplatePatternCE node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPAttributeConstraint node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPLHSSlot node, Object data) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPAmpersand node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPLine node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPTerm node, Object data) {
		// TODO Auto-generated method stub
		return null;
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
		JamochaValue functionName = (JamochaValue) node.jjtGetChild(j++).jjtAccept(this, data);

		// get the template description
		JamochaValue descr = JamochaValue.newString("");

		Node n = node.jjtGetChild(j);

		if (n != null && n instanceof SFPConstructDescription) {
			j++;
			descr = (JamochaValue) n.jjtAccept(this, data);
		}

		// get function's variables
		Parameter[] s = new Parameter[node.jjtGetNumChildren() - (j+1)];
		for (int i = j; i < node.jjtGetNumChildren()-1; i++) {
			BoundParam boundParam = (BoundParam) node.jjtGetChild(i).jjtAccept(this, data);
			s[i - j] = boundParam;
		}
		

		// get the function actions
		ExpressionSequence expressions = (ExpressionSequence)node.jjtGetChild( node.jjtGetNumChildren()-1 ).jjtAccept(this, data);
		
		
		FunctionParam2 functionParam = new FunctionParam2();		
		functionParam.setFunctionName(org.jamocha.rete.functions.ruleengine.Deffunction.NAME);
		
		Parameter[] params = new Parameter[4];		
		
		// setup of FunctionParams
		params[0] = functionName;
		params[1] = descr;
		params[2] = JamochaValue.newObject(s);
		params[2] = expressions;
		
		// put the setup vector into the functionParam-Object
		functionParam.setParameters(params);
		
		return functionParam;
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
