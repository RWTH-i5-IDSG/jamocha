package org.jamocha.parser.sfp;

import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.JamochaValueUtils;
import org.jamocha.rete.Rete;
import org.jamocha.rete.TemplateSlot;

public class SFPInterpreter implements SFPParserVisitor {
	
	private Rete engine;

	public SFPInterpreter(Rete engine) {
		super();
		this.engine = engine;
	}

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
		return  JamochaValue.newString(node.getName());
	}

	public Object visit(SFPTrue node, Object data) {
		return JamochaValue.TRUE;
	}

	public Object visit(SFPFalse node, Object data) {
		return JamochaValue.FALSE;
	}

	public Object visit(SFPConstructDescription node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(SFPSingleVariable node, Object data) {
		// TODO Auto-generated method stub
		return null;
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

	public Object visit(SFPType node, Object data) {
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
		// TODO Auto-generated method stub
		return null;
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
		return null;
	}

	public Object visit(SFPSingleSlotDefinition node, Object data) {
		
		TemplateSlot ts = new TemplateSlot();
		
		// setting the slot name
		ts.setName((String)node.jjtGetChild(0).jjtAccept(this, data));
	
		// setting the slot attributes
		for (int i=1; i<node.jjtGetNumChildren(); i++) {
			node.jjtGetChild(i).jjtAccept(this, ts);
		}
		
		return ts;

	}

	public Object visit(SFPMultiSlotDefinition node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPTemplateAttribute node, Object data) {

		node.jjtGetChild(0).jjtAccept(this, data);
		
		return null;
	}

	public Object visit(SFPAttributes node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPDefaultAttribute node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPDeriveAttribute node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPNoneAttribute node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPDynamicAttribute node, Object data) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPActionList node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPDeclaration node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPRuleProperty node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPSalience node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPAutoFocus node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPRuleVersion node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPBooleanSymbol node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPConditionalElement node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPBooleanFunction node, Object data) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
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

		node.jjtGetChild(0).jjtAccept(this, data);

		return null;
	}

	public Object visit(SFPTypeSpecification node, Object data) {

		node.jjtGetChild(0).jjtAccept(this, data);

		return null;
	}

	public Object visit(SFPAllowedType node, Object data) {

		
//		JamochaType jt = new JamochaType();
	
		((TemplateSlot)data).setValueType(JamochaType.LONG);
		
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

}
