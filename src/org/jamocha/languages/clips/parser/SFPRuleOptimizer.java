package org.jamocha.languages.clips.parser;

public class SFPRuleOptimizer implements SFPParserVisitor {

	public Object visit(SimpleNode node, Object data) {
		return null;
	}

	public Object visit(SFPStart node, Object data) {
		return node.jjtGetChild(0).jjtAccept(this, data);
	}

	public Object visit(SFPFloat node, Object data) {
		return null;
	}

	public Object visit(SFPInteger node, Object data) {
		return null;
	}

	public Object visit(SFPDateTime node, Object data) {
		return null;
	}

	public Object visit(SFPSymbol node, Object data) {
		return null;
	}

	public Object visit(SFPString node, Object data) {
		return null;
	}

	public Object visit(SFPTrue node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPNil node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPSymbolType node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPStringType node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPDateTimeType node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPLexemeType node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPBooleanType node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPIntegerType node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPLongType node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPShortType node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPFloatType node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPDoubleType node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPNumberType node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPFalse node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPConstant node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPConstructDescription node, Object data) {
		// TODO Auto-generated method stub
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

	public Object visit(SFPVariableType node, Object data) {
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

	public Object visit(SFPModify node, Object data) {
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

	public Object visit(SFPLoopForCountFunc node, Object data) {
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
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPSilent node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPSlotDefinition node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPSingleSlotDefinition node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPMultiSlotDefinition node, Object data) {
		// TODO Auto-generated method stub
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

	public Object visit(SFPDefaultAttributes node, Object data) {
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

	public Object visit(SFPModifyPattern node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPRHSSlot node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPRHSPattern node, Object data) {
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

	public Object visit(SFPNotFunction node, Object data) {
		// TODO Wichtig!
		return node.jjtGetChild(0).jjtAccept(this, data);
	}

	public Object visit(SFPAndFunction node, Object data) {
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return null;
	}

	public Object visit(SFPOrFunction node, Object data) {
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
		}
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

	public Object visit(SFPUnorderedLHSFactBody node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPOrderedLHSFactBody node, Object data) {
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

	public Object visit(SFPAmpersandConnectedConstraint node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPLineConnectedConstraint node, Object data) {
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

	public Object visit(SFPFunctionGroup node, Object data) {
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
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SFPTypeSpecification node, Object data) {
		// TODO Auto-generated method stub
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
